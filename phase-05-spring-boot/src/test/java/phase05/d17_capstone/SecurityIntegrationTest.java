package phase05.d17_capstone;

import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import phase05.support.TestJwt;

/** B7: genuine signed tokens through real loopback HTTP, never mock JWT acceptance. */
@Timeout(180)
class SecurityIntegrationTest {
    static CapstoneHttpFixture h;
    @BeforeEach void start() throws Exception {
        CapstoneHttpFixture.probe();
        if (h == null) h = new CapstoneHttpFixture();
        h.start(false);
        h.reset();
    }
    @AfterAll static void close() { if (h != null) h.close(); }

    @Test void everyFrozenRouteRequiresItsExactScopeBeforeService() throws Exception {
        var all = CapstoneHttpFixture.token("alice", "orders.read orders.write products.read metrics.read");
        CapstoneHttpFixture.status(200, h.get("/api/products", all));
        long id = h.create(all, CapstoneHttpFixture.orderJson());
        var noScopes = CapstoneHttpFixture.token("alice", "");
        for (String path : List.of("/api/products", "/api/orders", "/api/orders/" + id)) {
            CapstoneHttpFixture.status(401, h.get(path, null));
            CapstoneHttpFixture.status(403, h.get(path, noScopes));
            CapstoneHttpFixture.status(200, h.get(path, all));
        }
        for (String path : List.of("/api/orders", "/api/orders/" + id + "/cancel")) {
            CapstoneHttpFixture.status(401, h.send("POST", path, null, CapstoneHttpFixture.orderJson()));
            CapstoneHttpFixture.status(403, h.send("POST", path, "Bearer " + noScopes, CapstoneHttpFixture.orderJson()));
            CapstoneHttpFixture.status(403, h.send("POST", path, "Bearer " + CapstoneHttpFixture.token("alice", "orders.read"), CapstoneHttpFixture.orderJson()));
        }
        CapstoneHttpFixture.status(403, h.get("/api/products", CapstoneHttpFixture.token("alice", "orders.read")));
        CapstoneHttpFixture.status(403, h.get("/api/orders", CapstoneHttpFixture.token("alice", "orders.write")));
        assertEquals("NEW", h.jdbc.queryForObject("select status from orders where id=?", String.class, id));
        assertEquals(18, h.jdbc.queryForObject("select stock from products where id=11", Integer.class));
    }

    @Test void genuineDecoderRejectsWrongSignatureAlgorithmAndClaimsAfterValidControl() throws Exception {
        CapstoneHttpFixture.status(200, h.get("/api/products", CapstoneHttpFixture.token("alice", "products.read")));
        var now = Instant.now();
        var missingExpiry = new HashMap<String, Object>();
        missingExpiry.put("exp", null);
        var cases = List.of(
                TestJwt.signedWithWrongKey(CapstoneHttpFixture.claims(Map.of())),
                TestJwt.signedWithAlgorithm("RS384", CapstoneHttpFixture.claims(Map.of())),
                TestJwt.signedWithAlgorithm("RS512", CapstoneHttpFixture.claims(Map.of())),
                TestJwt.signedWithAlgorithm("HS256", CapstoneHttpFixture.claims(Map.of())),
                TestJwt.signed(CapstoneHttpFixture.claims(Map.of("iss", "urn:wrong"))),
                TestJwt.signed(CapstoneHttpFixture.claims(Map.of("aud", List.of("other-api")))),
                TestJwt.signed(CapstoneHttpFixture.claims(Map.of("exp", now.minusSeconds(120), "iat", now.minusSeconds(300), "nbf", now.minusSeconds(300)))),
                TestJwt.signed(CapstoneHttpFixture.claims(Map.of("nbf", now.plusSeconds(120)))),
                TestJwt.signed(CapstoneHttpFixture.claims(missingExpiry)),
                TestJwt.signed(CapstoneHttpFixture.claims(java.util.Collections.singletonMap("nbf", null))),
                TestJwt.signed(CapstoneHttpFixture.claims(Map.of("sub", ""))),
                TestJwt.signed(CapstoneHttpFixture.claims(Map.of("token_use", "id"))));
        for (String token : cases) {
            var response = h.get("/api/products", token);
            CapstoneHttpFixture.status(401, response);
            assertTrue(response.headers().firstValue("WWW-Authenticate").orElseThrow().contains("error=\"invalid_token\""));
        }
        CapstoneHttpFixture.status(200, h.get("/api/products", TestJwt.signed(CapstoneHttpFixture.claims(Map.of(
                "aud", List.of("another-audience", "phase05-api"), "token_use", "access")))));
    }

    @Test void runtimeDecoderFailsMissingSettingsAndMalformedPublicKeyWithoutSecretEcho() {
        var security = new CapstoneSecurity();
        var complete = new org.springframework.mock.env.MockEnvironment()
                .withProperty("capstone.jwt.issuer", "urn:phase05:test")
                .withProperty("capstone.jwt.audience", "phase05-api")
                .withProperty("capstone.jwt.public-key", CapstoneTestRuntime.publicKey());
        var decoder = security.jwtDecoder(complete);
        assertNotNull(decoder.decode(CapstoneHttpFixture.token("alice", "products.read")));
        for (String missing : List.of("capstone.jwt.issuer", "capstone.jwt.audience", "capstone.jwt.public-key")) {
            var environment = new org.springframework.mock.env.MockEnvironment();
            for (String key : List.of("capstone.jwt.issuer", "capstone.jwt.audience", "capstone.jwt.public-key"))
                if (!missing.equals(key)) environment.setProperty(key, complete.getProperty(key));
            assertThrows(IllegalStateException.class, () -> security.jwtDecoder(environment));
        }
        complete.setProperty("capstone.jwt.public-key", "private-key-misconfig-sentinel");
        var failure = assertThrows(IllegalStateException.class, () -> security.jwtDecoder(complete));
        assertFalse(failure.getMessage().contains("private-key-misconfig-sentinel"));
        assertNull(failure.getCause(), "Public key parse failure must not echo configured key");
    }

    @Test void denyByDefaultIncludesWrongMethodsAndCookieCredentials() throws Exception {
        var token = CapstoneHttpFixture.token("alice", "orders.read orders.write products.read metrics.read");
        CapstoneHttpFixture.status(200, h.get("/api/products", token));
        for (String method : List.of("POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS"))
            CapstoneHttpFixture.status(403, h.send(method, "/api/products", "Bearer " + token, null));
        CapstoneHttpFixture.status(403, h.get("/api/unknown", token));
        CapstoneHttpFixture.status(403, h.send("POST", "/actuator/health", "Bearer " + token, null));
        var request = java.net.http.HttpRequest.newBuilder(h.base.resolve("/api/products"))
                .timeout(java.time.Duration.ofSeconds(10)).header("Cookie", "access_token=" + token + "; JSESSIONID=ignored").GET().build();
        CapstoneHttpFixture.status(401, h.client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString()));
    }
}
