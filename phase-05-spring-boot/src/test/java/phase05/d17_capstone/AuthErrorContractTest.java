package phase05.d17_capstone;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import phase05.support.TestJwt;

/** B7: real filter failures stay RFC9457; no unsafe OAuth2 description or credential logging. */
@ExtendWith(OutputCaptureExtension.class)
@Timeout(180)
class AuthErrorContractTest {
    static CapstoneHttpFixture h;
    @BeforeEach void start() throws Exception {
        CapstoneHttpFixture.probe();
        if (h == null) h = new CapstoneHttpFixture();
        h.start(false);
        h.reset();
    }
    @AfterAll static void close() { if (h != null) h.close(); }

    @Test void missingAndBasicArePlainBearerInvalidBearerKeepsActualOAuth2Code() throws Exception {
        CapstoneHttpFixture.status(200, h.get("/api/products", CapstoneHttpFixture.token("alice", "products.read")));
        for (String auth : java.util.Arrays.asList(null, "Basic c2VudGluZWw6cHJpdmF0ZQ==")) {
            var response = h.send("GET", "/api/products", auth, null);
            problem(401, response);
            assertEquals("Bearer", response.headers().firstValue("WWW-Authenticate").orElseThrow());
        }
        for (String auth : List.of("Bearer not-a-jwt-private-sentinel", "Bearer bad token-private-sentinel")) {
            var response = h.send("GET", "/api/products", auth, null);
            problem(401, response);
            var challenge = response.headers().firstValue("WWW-Authenticate").orElseThrow();
            assertTrue(challenge.contains("error=\"invalid_token\""));
            assertFalse(challenge.contains("error_description"));
            assertFalse(challenge.contains("private-sentinel"));
        }
        var forbidden = h.get("/api/products", CapstoneHttpFixture.token("alice", "orders.read"));
        problem(403, forbidden);
        assertTrue(forbidden.headers().firstValue("WWW-Authenticate").orElseThrow().contains("error=\"insufficient_scope\""));
    }

    @Test void delegatePreservesActualOAuth2StatusCodeScopeAndUriWhileDroppingOnlyDescription() throws Exception {
        // Separate handler check, not signed JWT acceptance proof.
        var request = new org.springframework.mock.web.MockHttpServletRequest("GET", "/api/products");
        var response = new org.springframework.mock.web.MockHttpServletResponse();
        var error = new org.springframework.security.oauth2.server.resource.BearerTokenError("invalid_request",
                org.springframework.http.HttpStatus.BAD_REQUEST, "credential-description-private-sentinel",
                "https://example.invalid/oauth-error", "products.read");
        CapstoneSecurity.unauthorized(request, response, new org.springframework.security.oauth2.core.OAuth2AuthenticationException(error));
        assertEquals(400, response.getStatus());
        var challenge = response.getHeader("WWW-Authenticate");
        assertNotNull(challenge);
        assertTrue(challenge.contains("error=\"invalid_request\""));
        assertTrue(challenge.contains("scope=\"products.read\""));
        assertTrue(challenge.contains("error_uri=\"https://example.invalid/oauth-error\""));
        assertFalse(challenge.contains("error_description"));
        assertFalse((challenge + response.getContentAsString()).contains("credential-description-private-sentinel"));
        assertEquals(400, h.json.readTree(response.getContentAsString()).get("status").asInt());
    }

    @Test void secretBearingValidAndInvalidJwtNeverAppearInHeadersBodyOrLogs(CapturedOutput output) throws Exception {
        String sentinel = "private-jwt-sentinel-" + java.util.UUID.randomUUID();
        String valid = TestJwt.signed(CapstoneHttpFixture.claims(Map.of("scope", "products.read", "credential", sentinel)));
        String invalid = TestJwt.signedWithWrongKey(CapstoneHttpFixture.claims(Map.of("credential", sentinel)));
        var good = h.get("/api/products", valid);
        CapstoneHttpFixture.status(200, good);
        var bad = h.get("/api/products", invalid);
        problem(401, bad);
        var forbidden = h.get("/api/products", TestJwt.signed(CapstoneHttpFixture.claims(Map.of("scope", "", "credential", sentinel))));
        problem(403, forbidden);
        for (var response : List.of(good, bad, forbidden)) {
            String exposed = response.body() + response.headers().map();
            for (String secret : List.of(sentinel, valid, invalid)) assertFalse(exposed.contains(secret), "Response leaked credential; content withheld");
        }
        for (String secret : List.of(sentinel, valid, invalid)) assertFalse(output.getAll().contains(secret), "Logs leaked credential; content withheld");
    }

    @Test void mvc400404409AndSafeDatabase500StayOutsideBearerChallengeContract() throws Exception {
        var token = CapstoneHttpFixture.token("alice", "orders.read orders.write products.read");
        long id = h.create(token, CapstoneHttpFixture.orderJson());
        var invalid = h.send("POST", "/api/orders", "Bearer " + token, "{");
        var missing = h.get("/api/orders/999999", token);
        CapstoneHttpFixture.status(200, h.send("POST", "/api/orders/" + id + "/cancel", "Bearer " + token, null));
        var conflict = h.send("POST", "/api/orders/" + id + "/cancel", "Bearer " + token, null);
        problem(400, invalid); problem(404, missing); problem(409, conflict);
        try {
            h.jdbc.execute("alter table products rename to private_sql_sentinel");
            var failure = h.get("/api/products", token);
            problem(500, failure);
            assertFalse(failure.body().contains("private_sql_sentinel"), "Database detail leaked");
            assertTrue(failure.headers().allValues("WWW-Authenticate").isEmpty());
        } finally { h.jdbc.execute("alter table private_sql_sentinel rename to products"); }
        for (var response : List.of(invalid, missing, conflict)) assertTrue(response.headers().allValues("WWW-Authenticate").isEmpty());
    }

    private static void problem(int status, java.net.http.HttpResponse<String> response) throws Exception {
        CapstoneHttpFixture.status(status, response);
        assertTrue(response.headers().firstValue("Content-Type").orElseThrow().startsWith("application/problem+json"));
        var body = new tools.jackson.databind.ObjectMapper().readTree(response.body());
        assertEquals(status, body.get("status").asInt());
        assertNotNull(body.get("type")); assertNotNull(body.get("title")); assertNotNull(body.get("detail"));
        for (String leak : List.of("Exception", "stackTrace", "private-sentinel", "password", "SQL"))
            assertFalse(response.body().contains(leak), "Unsafe Problem Details; body withheld");
    }
}
