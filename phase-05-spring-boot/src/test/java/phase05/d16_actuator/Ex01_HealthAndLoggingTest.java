package phase05.d16_actuator;

import static org.junit.jupiter.api.Assertions.*;
import com.zaxxer.hikari.*;
import com.zaxxer.hikari.metrics.micrometer.MicrometerMetricsTrackerFactory;
import io.micrometer.core.instrument.MeterRegistry;
import java.net.URI;
import java.net.http.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.availability.*;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import phase05.support.PostgresFixture;
import phase05.support.TestJwt;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(OutputCaptureExtension.class)
class Ex01_HealthAndLoggingTest {
    static final String METRIC = "/actuator/metrics/hikaricp.connections.acquire?tag=pool:task4-health";
    final HttpClient client = HttpClient.newHttpClient();
    final JsonMapper json = JsonMapper.builder().build();

    @Test void defaultHealthOnlyDoesNotExposeMetricsEvenToAuthorizedCaller() throws Exception {
        Map<String,Object> properties = Ex01_HealthAndLogging.properties(false); // Direct TODO before context.
        try (var db = PostgresFixture.start(); var app = start(db, properties, false)) {
            assertEquals(200, get(app, "/actuator/health", null).statusCode());
            assertEquals(404, get(app, METRIC, token("metrics.read")).statusCode());
            assertEquals(404, get(app, "/actuator/env", token("metrics.read")).statusCode());
            var health = json.readTree(get(app, "/actuator/health", null).body());
            assertEquals("UP", health.get("status").asString());
            assertNull(health.get("components"));
        }
    }

    @Test void localLabMetricsUseRealSamePoolAcquisitionCountAndSeconds() throws Exception {
        Map<String,Object> properties = Ex01_HealthAndLogging.properties(true);
        try (var db = PostgresFixture.start(); var app = start(db, properties, true)) {
            var pool = app.getBean(HikariDataSource.class);
            try (var connection = pool.getConnection()) { assertTrue(connection.isValid(2)); }
            var registry = app.getBean(MeterRegistry.class);
            var timer = registry.get("hikaricp.connections.acquire").tag("pool", "task4-health").timer();
            assertEquals("seconds", timer.getId().getBaseUnit());
            var start = get(app, METRIC, token("metrics.read")); assertEquals(200, start.statusCode());
            var first = json.readTree(start.body());
            assertEquals("hikaricp.connections.acquire", first.get("name").asString());
            assertEquals("seconds", first.get("baseUnit").asString());
            var stats = measurements(first);
            assertEquals((double)timer.count(), stats.get("COUNT"));
            assertEquals(timer.totalTime(TimeUnit.SECONDS), stats.get("TOTAL_TIME"), 0.000001);
            long before = timer.count(); double beforeSeconds = timer.totalTime(TimeUnit.SECONDS);
            try (var connection = pool.getConnection()) { assertTrue(connection.isValid(2)); }
            var second = measurements(json.readTree(get(app, METRIC, token("metrics.read")).body()));
            assertTrue(second.get("COUNT") > stats.get("COUNT"));
            assertEquals(1L, timer.count() - before);
            assertTrue(second.get("TOTAL_TIME") >= beforeSeconds);
            assertTrue(second.get("TOTAL_TIME") > 0);
            assertEquals((double)timer.count(), second.get("COUNT"));
            assertEquals(timer.totalTime(TimeUnit.SECONDS), second.get("TOTAL_TIME"), 0.000001);
            assertTrue(timer.getId().getTags().stream().noneMatch(tag -> tag.getKey().equals("user")));
            System.out.printf("HIKARI_ACQUIRE baseUnit=seconds count=%.0f deltaCount=%.0f totalTimeSeconds=%.9f%n",
                    second.get("COUNT"), second.get("COUNT")-stats.get("COUNT"), second.get("TOTAL_TIME"));
        }
    }

    @Test void localLabMetricsAndProductRoutesHaveSeparateBearerScopes() throws Exception {
        Map<String,Object> properties = Ex01_HealthAndLogging.properties(true);
        try (var db = PostgresFixture.start(); var app = start(db, properties, true)) {
            assertEquals(401, get(app, METRIC, null).statusCode());
            var denied = get(app, METRIC, token("products.read"));
            assertEquals(403, denied.statusCode());
            assertTrue(denied.headers().firstValue("WWW-Authenticate").orElseThrow().contains("insufficient_scope"));
            assertEquals(403, get(app, "/api/products", token("metrics.read")).statusCode());
            assertEquals(200, get(app, "/api/products", token("products.read")).statusCode());
            assertEquals(404, get(app, "/actuator/env", token("metrics.read")).statusCode());
            assertEquals("127.0.0.1", app.getEnvironment().getProperty("server.address"));
        }
    }

    @Test void safeAuthProblemAndLogsNeverEchoSignedTokenSecretOrException(CapturedOutput output) throws Exception {
        Map<String,Object> properties = Ex01_HealthAndLogging.properties(true);
        String sentinel = "TASK4_SECRET_SENTINEL_DO_NOT_ECHO";
        String invalid = TestJwt.signed(Map.of("iss", "urn:wrong:"+sentinel, "secret", sentinel));
        try (var db = PostgresFixture.start(); var app = start(db, properties, true)) {
            var missing = get(app, "/api/products", null);
            assertEquals(401, missing.statusCode());
            assertEquals("Bearer", missing.headers().firstValue("WWW-Authenticate").orElseThrow());
            var rejected = get(app, "/api/products", invalid);
            assertEquals(401, rejected.statusCode());
            assertTrue(rejected.headers().firstValue("WWW-Authenticate").orElseThrow().contains("invalid_token"));
            assertTrue(rejected.headers().firstValue("Content-Type").orElseThrow().startsWith("application/problem+json"));
            assertEquals(401, json.readTree(rejected.body()).get("status").asInt());
            var denied = get(app, METRIC, TestJwt.signed(Map.of("scope", "products.read", "secret", sentinel)));
            assertEquals(403, denied.statusCode());
            assertEquals(403, json.readTree(denied.body()).get("status").asInt());
            for (String text : List.of(rejected.body(), denied.body(), rejected.headers().toString(), denied.headers().toString())) {
                assertFalse(text.contains(sentinel)); assertFalse(text.contains(invalid));
                assertFalse(text.contains("JwtValidationException"));
            }
        }
        assertTrue(output.getAll().contains("security.authentication_failed"));
        assertTrue(output.getAll().contains("security.access_denied"));
        assertFalse(output.getAll().contains(sentinel)); assertFalse(output.getAll().contains(invalid));
    }

    @Test void q04_readinessAndPoolTags_experimentRuns() throws Exception {
        Map<String,Object> properties = Ex01_HealthAndLogging.properties(true);
        try (var db = PostgresFixture.start(); var app = start(db, properties, true)) {
            assertEquals(200, get(app, "/actuator/health/liveness", null).statusCode());
            AvailabilityChangeEvent.publish(app, ReadinessState.REFUSING_TRAFFIC);
            assertEquals(503, get(app, "/actuator/health/readiness", null).statusCode());
            assertEquals(200, get(app, "/actuator/health/liveness", null).statusCode());
            AvailabilityChangeEvent.publish(app, ReadinessState.ACCEPTING_TRAFFIC);
            assertEquals(200, get(app, "/actuator/health/readiness", null).statusCode());
            var pool = app.getBean(HikariDataSource.class);
            try (var connection = pool.getConnection()) { assertTrue(connection.isValid(2)); }
            var timer = app.getBean(MeterRegistry.class).get("hikaricp.connections.acquire")
                    .tag("pool", "task4-health").timer();
            long count = timer.count();
            // Different callers use same bounded pool series, not one user-ID series per principal.
            for (String subject : List.of("alice", "bob")) {
                assertEquals(200, get(app, METRIC, TestJwt.signed(Map.of("sub", subject, "scope", "metrics.read"))).statusCode());
            }
            assertEquals(count, timer.count()); // Reading metrics is not a DB acquisition.
            assertEquals(List.of("pool"), timer.getId().getTags().stream().map(tag -> tag.getKey()).toList());
        }
    }

    private ConfigurableApplicationContext start(PostgresFixture db, Map<String,Object> learner, boolean localLab) {
        var properties = new HashMap<>(learner);
        properties.put("server.port", 0); properties.put("server.address", "127.0.0.1");
        properties.put("spring.datasource.url", db.jdbcUrl());
        properties.put("spring.datasource.username", db.username()); properties.put("spring.datasource.password", db.password());
        properties.put("spring.main.banner-mode", "off");
        var application = new SpringApplication(Config.class);
        application.setWebApplicationType(WebApplicationType.SERVLET); application.setDefaultProperties(properties);
        if (localLab) application.setAdditionalProfiles("local-lab");
        return application.run();
    }
    private HttpResponse<String> get(ConfigurableApplicationContext app, String path, String token) throws Exception {
        String port = app.getEnvironment().getProperty("local.server.port");
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+path)).GET();
        if (token != null) request.header("Authorization", "Bearer "+token);
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
    private String token(String scope) { return TestJwt.signed(Map.of("scope", scope)); }
    private Map<String,Double> measurements(tools.jackson.databind.JsonNode node) {
        var result = new HashMap<String,Double>();
        for (var measurement : node.get("measurements")) result.put(measurement.get("statistic").asString(), measurement.get("value").asDouble());
        return result;
    }

    // Explicit full Boot HTTP boundary, programmatic startup lets TODO factory fail before context creation.
    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration(exclude = {
            org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration.class,
            org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration.class,
            org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration.class})
    @Import(Ex01_HealthAndLogging.Products.class)
    static class Config {
        @Bean JwtDecoder decoder() { return TestJwt.decoder(); }
        @Bean SecurityFilterChain security(HttpSecurity http, JwtDecoder decoder) throws Exception {
            return Ex01_HealthAndLogging.security(http, decoder);
        }
        @Bean(destroyMethod = "close") HikariDataSource pool(Environment env, MeterRegistry registry) {
            var config = new HikariConfig(); config.setJdbcUrl(env.getRequiredProperty("spring.datasource.url"));
            config.setUsername(env.getRequiredProperty("spring.datasource.username"));
            config.setPassword(env.getRequiredProperty("spring.datasource.password"));
            config.setPoolName("task4-health"); config.setMaximumPoolSize(2);
            config.setMetricsTrackerFactory(new MicrometerMetricsTrackerFactory(registry));
            return new HikariDataSource(config);
        }
    }
}
