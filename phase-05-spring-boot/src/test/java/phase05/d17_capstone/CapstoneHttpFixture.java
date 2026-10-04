package phase05.d17_capstone;

import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import phase05.support.PostgresFixture;
import phase05.support.TestJwt;

/** Provided real HTTP/PG harness. Per-method prerequisite probe precedes all infrastructure. */
final class CapstoneHttpFixture implements AutoCloseable {
    PostgresFixture db;
    ConfigurableApplicationContext app;
    final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    final ObjectMapper json = new ObjectMapper();
    JdbcTemplate jdbc;
    URI base;

    static void probe() {
        CapstoneSchema.sql();
        CapstoneSecurity.runtimeSettings(new SpringApplication(OrderApiApplication.class));
    }

    void start(boolean localLab) { start(localLab, Map.of()); }

    void start(boolean localLab, Map<String, Object> overrides) {
        if (app != null) return;
        db = PostgresFixture.start();
        var settings = new HashMap<String, Object>();
        settings.put("spring.datasource.url", db.jdbcUrl());
        settings.put("spring.datasource.username", db.username());
        settings.put("spring.datasource.password", db.password());
        settings.put("spring.datasource.hikari.connection-timeout", "5000");
        settings.put("spring.datasource.hikari.connection-init-sql", "SET statement_timeout='8s'");
        settings.putAll(overrides);
        CapstoneTestRuntime.SETTINGS.set(settings);
        Listeners.PORTS.clear();
        try {
            var runArgs = new java.util.ArrayList<String>(java.util.List.of("--server.port=0",
                    "--spring.main.banner-mode=off", "--logging.level.root=WARN", "--spring.flyway.locations=",
                    "--spring.profiles.active=" + (localLab ? "local-lab" : "default")));
            for (var override : overrides.entrySet())
                if (override.getKey().startsWith("management.server.") || override.getKey().startsWith("server.")) {
                    runArgs.add("--" + override.getKey() + "=" + override.getValue());
                    settings.remove(override.getKey());
                }
            CapstoneTestRuntime.SETTINGS.set(settings);
            app = SpringApplication.from(OrderApiApplication::main).with(Listeners.class)
                    .run(runArgs.toArray(String[]::new)).getApplicationContext();
            int port = ((WebServerApplicationContext) app).getWebServer().getPort();
            base = URI.create("http://127.0.0.1:" + port);
            jdbc = new JdbcTemplate(db.dataSource());
            assertEquals("false", app.getEnvironment().getProperty("spring.jpa.open-in-view"));
            assertEquals("validate", app.getBean(jakarta.persistence.EntityManagerFactory.class)
                    .getProperties().get("hibernate.hbm2ddl.auto"));
            assertEquals(4, app.getBean(jakarta.persistence.EntityManagerFactory.class).getMetamodel().getEntities().size());
            assertEquals(0, app.getBeansOfType(org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor.class).size());
            assertEquals(1, jdbc.queryForObject("select count(*) from flyway_schema_history where success", Integer.class));
        } catch (RuntimeException | Error failure) { close(); throw failure; }
        finally { CapstoneTestRuntime.SETTINGS.remove(); }
    }

    void reset() throws Exception {
        jdbc.update("delete from order_items");
        jdbc.update("delete from orders");
        jdbc.update("delete from products");
        jdbc.update("delete from customers");
        var seeds = new CapstoneSeeds(db.dataSource());
        seeds.seedCustomer(7, "alice");
        seeds.seedCustomer(8, "bob");
        seeds.seedProduct(11, "Tea", new BigDecimal("4.25"), 20);
        seeds.seedProduct(12, "Coffee", new BigDecimal("6.00"), 0);
    }

    static Map<String, Object> claims(Map<String, Object> overrides) {
        var now = Instant.now();
        var claims = new HashMap<String, Object>();
        claims.put("iat", now.minusSeconds(5));
        claims.put("nbf", now.minusSeconds(5));
        claims.put("exp", now.plusSeconds(300));
        claims.putAll(overrides);
        return claims;
    }
    static String token(String subject, String scope) {
        return TestJwt.signed(claims(Map.of("sub", subject, "scope", scope)));
    }
    HttpResponse<String> send(String method, String path, String authorization, String body) throws Exception {
        var request = HttpRequest.newBuilder(base.resolve(path)).timeout(Duration.ofSeconds(10));
        if (authorization != null) request.header("Authorization", authorization);
        if (body != null) request.header("Content-Type", "application/json");
        request.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
    HttpResponse<String> get(String path, String token) throws Exception {
        return send("GET", path, token == null ? null : "Bearer " + token, null);
    }
    JsonNode body(HttpResponse<String> response) throws Exception { return json.readTree(response.body()); }
    static void status(int expected, HttpResponse<String> response) {
        assertEquals(expected, response.statusCode(), "HTTP status mismatch; body deliberately withheld");
        assertTrue(response.headers().allValues("Set-Cookie").isEmpty(), "Stateless API must not set cookies");
    }
    long create(String token, String body) throws Exception {
        var response = send("POST", "/api/orders", "Bearer " + token, body);
        status(201, response);
        long id = body(response).get("id").asLong();
        assertEquals("/api/orders/" + id, response.headers().firstValue("Location").orElseThrow());
        return id;
    }
    static String orderJson() { return "{\"items\":[{\"productId\":11,\"quantity\":2}]}"; }

    /** Provided listener: records every initialized server port, native event only. */
    static final class Listeners implements org.springframework.context.ApplicationListener<org.springframework.boot.web.server.context.WebServerInitializedEvent> {
        static final java.util.List<Integer> PORTS = new java.util.concurrent.CopyOnWriteArrayList<>();
        @Override public void onApplicationEvent(org.springframework.boot.web.server.context.WebServerInitializedEvent event) {
            PORTS.add(event.getWebServer().getPort());
        }
    }
    static int managementPort(ConfigurableApplicationContext main) {
        for (int index = Listeners.PORTS.size() - 1; index >= 0; index--) {
            int port = Listeners.PORTS.get(index);
            if (port != ((WebServerApplicationContext) main).getWebServer().getPort()) return port;
        }
        return -1;
    }
    @Override public void close() {
        try { if (app != null) { app.close(); app = null; } }
        finally { try { client.close(); } finally { if (db != null) { db.close(); db = null; } } }
    }
}
