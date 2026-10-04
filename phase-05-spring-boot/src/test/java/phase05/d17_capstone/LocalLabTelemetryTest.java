package phase05.d17_capstone;

import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;

/** B7: native Hikari acquisition timer, real getConnection/close; no HTTP/lease substitute. */
@Timeout(180)
class LocalLabTelemetryTest {
    static CapstoneHttpFixture h;
    @BeforeEach void start() throws Exception {
        CapstoneHttpFixture.probe();
        if (h == null) h = new CapstoneHttpFixture();
        h.start(true);
        h.reset();
    }
    @AfterAll static void close() { if (h != null) h.close(); }

    @Test void actualCapstonePoolExportsSecondsAndPositiveAcquisitionDeltaOverHttp() throws Exception {
        var token = CapstoneHttpFixture.token("alice", "metrics.read");
        String path = "/actuator/metrics/hikaricp.connections.acquire?tag=pool:capstone";
        var control = h.get(path, token);
        CapstoneHttpFixture.status(200, control);
        CapstoneHttpFixture.status(401, h.get(path, null));
        CapstoneHttpFixture.status(403, h.get(path, CapstoneHttpFixture.token("alice", "products.read")));
        var pool = h.app.getBean(com.zaxxer.hikari.HikariDataSource.class);
        assertEquals("capstone", pool.getPoolName());
        var registry = h.app.getBean(io.micrometer.core.instrument.MeterRegistry.class);
        var timer = registry.find("hikaricp.connections.acquire").tag("pool", "capstone").timer();
        assertNotNull(timer, "BLOCKER: native capstone Hikari acquisition meter missing");
        assertEquals(List.of("pool"), timer.getId().getTags().stream().map(io.micrometer.core.instrument.Tag::getKey).toList());
        Instant start = Instant.now();
        var before = h.get(path, token);
        CapstoneHttpFixture.status(200, before);
        long localCount = timer.count();
        for (int i = 0; i < 4; i++) try (var connection = pool.getConnection()) { assertFalse(connection.isClosed()); }
        var after = h.get(path, token);
        CapstoneHttpFixture.status(200, after);
        Instant end = Instant.now();
        assertFalse(end.isBefore(start));
        for (var response : List.of(before, after)) {
            var body = h.body(response);
            assertEquals("hikaricp.connections.acquire", body.get("name").asText());
            assertEquals("seconds", body.get("baseUnit").asText());
        }
        double countDelta = measurement(after, "COUNT") - measurement(before, "COUNT");
        double secondsDelta = measurement(after, "TOTAL_TIME") - measurement(before, "TOTAL_TIME");
        assertEquals(4, timer.count() - localCount);
        assertEquals(4.0, countDelta);
        assertTrue(secondsDelta > 0, "Native acquisition duration must increase, no fake zero");
        assertEquals(measurement(after, "TOTAL_TIME"), timer.totalTime(java.util.concurrent.TimeUnit.SECONDS), 0.000001);
        long productCount = timer.count();
        CapstoneHttpFixture.status(200, h.get("/api/products?page=0&size=2", CapstoneHttpFixture.token("alice", "products.read")));
        assertTrue(timer.count() > productCount, "Products HTTP must acquire real capstone connections for Phase09 sampling");
        // Consumer only: run,snapshot,capturedAtUtc,metricName,pool,count,totalTimeSeconds.
        // Four baseline/changed x start/end rows; same capstone pool, restart resets each run's counters.
    }

    @Test void minimalHealthProbesAndLoopbackLabNeverExposeSensitiveEndpoints() throws Exception {
        assertEquals("127.0.0.1", h.app.getEnvironment().getProperty("server.address"));
        var token = CapstoneHttpFixture.token("alice", "metrics.read");
        var health = h.get("/actuator/health", null);
        CapstoneHttpFixture.status(200, health);
        assertFalse(h.body(health).has("components"));
        assertFalse(h.body(health).has("details"));
        assertEquals("UP", h.body(health).get("status").asText());
        assertEquals(2, h.body(health).size()); // Native probe group names, never DB/component details.
        assertEquals("liveness", h.body(health).get("groups").get(0).asText());
        assertEquals("readiness", h.body(health).get("groups").get(1).asText());
        for (String path : List.of("/actuator/env", "/actuator/beans", "/actuator/configprops", "/actuator/logfile"))
            CapstoneHttpFixture.status(403, h.get(path, token));
        try {
            AvailabilityChangeEvent.publish(h.app, ReadinessState.REFUSING_TRAFFIC);
            CapstoneHttpFixture.status(503, h.get("/actuator/health/readiness", null));
            CapstoneHttpFixture.status(200, h.get("/actuator/health/liveness", null));
        } finally { AvailabilityChangeEvent.publish(h.app, ReadinessState.ACCEPTING_TRAFFIC); }
    }

    @Test void separateEphemeralManagementListenerStaysLoopbackWhenAddressUnsetOrUnsafe() throws Exception {
        // P1 regression: management.server.port differs, management.server.address absent or 0.0.0.0.
        try (var lab = new CapstoneHttpFixture()) {
            lab.start(true, java.util.Map.of("management.server.port", "0"));
            int managementPort = CapstoneHttpFixture.managementPort(lab.app);
            assertTrue(managementPort > 0);
            var properties = lab.app.getBean(org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties.class);
            var bound = properties.getAddress();
            // Main context bean: a separate listener resolves the bound address only in its child context.
            if (bound != null) assertEquals("127.0.0.1", bound.getHostAddress());
            var token = CapstoneHttpFixture.token("alice", "metrics.read");
            lab.base = java.net.URI.create("http://127.0.0.1:" + managementPort);
            CapstoneHttpFixture.status(200, lab.get("/actuator/metrics/hikaricp.connections.acquire?tag=pool:capstone", token));
            // Native listener contract: the loopback-bound management port refuses non-loopback connections.
            var loopbackOnly = java.net.URI.create("http://localhost:" + managementPort);
            lab.base = loopbackOnly;
            CapstoneHttpFixture.status(200, lab.get("/actuator/metrics/hikaricp.connections.acquire?tag=pool:capstone", token));
            lab.base = java.net.URI.create("http://127.0.0.1:" + managementPort);
            CapstoneHttpFixture.status(401, lab.get("/actuator/metrics", null));
            CapstoneHttpFixture.status(403, lab.get("/actuator/metrics", CapstoneHttpFixture.token("alice", "products.read")));
        }
        try (var unsafe = new CapstoneHttpFixture()) {
            unsafe.start(true, java.util.Map.of("management.server.port", "9091", "management.server.address", "0.0.0.0"));
            fail("Explicit non-loopback management address must fail closed before listener exposure");
        } catch (IllegalStateException expected) { }
        try (var external = new CapstoneHttpFixture()) {
            external.start(true, java.util.Map.of("management.server.port", "9091", "management.server.address", "192.0.2.10"));
            fail("Explicit non-loopback management address must fail closed before listener exposure");
        } catch (IllegalStateException expected) { }
        try (var absent = new CapstoneHttpFixture()) {
            absent.start(true, java.util.Map.of("management.server.port", "9091"));
            assertEquals("127.0.0.1", absent.app.getBean(
                    org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties.class)
                    .getAddress().getHostAddress());
        }
    }

    @Test void defaultProfileHasHealthOnlyAndNoAcquisitionHttpEndpoint() throws Exception {
        try (var defaultApp = new CapstoneHttpFixture()) {
            defaultApp.start(false);
            var token = CapstoneHttpFixture.token("alice", "metrics.read");
            CapstoneHttpFixture.status(200, defaultApp.get("/actuator/health", null));
            CapstoneHttpFixture.status(403, defaultApp.get("/actuator/metrics", token));
            CapstoneHttpFixture.status(403, defaultApp.get("/actuator/metrics/hikaricp.connections.acquire", token));
            assertEquals("health", defaultApp.app.getEnvironment().getProperty("management.endpoints.web.exposure.include"));
            assertEquals("never", defaultApp.app.getEnvironment().getProperty("management.endpoint.health.show-details"));
        }
    }

    private double measurement(java.net.http.HttpResponse<String> response, String statistic) throws Exception {
        for (var item : h.body(response).get("measurements"))
            if (statistic.equals(item.get("statistic").asText())) return item.get("value").asDouble();
        fail("BLOCKER: standard meter lacks " + statistic);
        return Double.NaN;
    }
}
