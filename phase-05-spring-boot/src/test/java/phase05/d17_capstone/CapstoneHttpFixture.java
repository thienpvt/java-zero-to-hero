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

    void start(boolean localLab) {
        if (app != null) return;
        db = PostgresFixture.start();
        var settings = new HashMap<String, Object>();
        settings.put("spring.datasource.url", db.jdbcUrl());
        settings.put("spring.datasource.username", db.username());
        settings.put("spring.datasource.password", db.password());
        settings.put("spring.datasource.hikari.connection-timeout", "5000");
        settings.put("spring.datasource.hikari.connection-init-sql", "SET statement_timeout='8s'");
        CapstoneTestRuntime.SETTINGS.set(settings);
        try {
            app = SpringApplication.from(OrderApiApplication::main).run("--server.port=0",
                    "--spring.main.banner-mode=off", "--logging.level.root=WARN", "--spring.flyway.locations=",
                    "--spring.profiles.active=" + (localLab ? "local-lab" : "default")).getApplicationContext();
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
    @Override public void close() {
        try { if (app != null) { app.close(); app = null; } }
        finally { try { client.close(); } finally { if (db != null) { db.close(); db = null; } } }
    }
}
