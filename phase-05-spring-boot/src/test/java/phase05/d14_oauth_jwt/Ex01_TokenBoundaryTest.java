package phase05.d14_oauth_jwt;

import static org.junit.jupiter.api.Assertions.*;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import phase05.support.TestJwt;

class Ex01_TokenBoundaryTest {
    static final Instant NOW = Instant.parse("2026-10-04T12:00:00Z");
    JwtDecoder decoder;
    @BeforeEach void validControlBeforeEveryRejection() throws Exception {
        var set = JWKSet.parse(TestJwt.publicJwkSet());
        assertEquals(1, set.getKeys().size());
        var key = (RSAKey) set.getKeys().getFirst();
        assertFalse(key.isPrivate());
        decoder = Ex01_TokenBoundary.decoder(key.toRSAPublicKey(), Clock.fixed(NOW, ZoneOffset.UTC),
                "urn:phase05:test", "phase05-api");
        assertEquals("alice", decoder.decode(TestJwt.signed(Map.of())).getSubject());
    }

    @Test void acceptedSignedAccessTokenAndPublicJwkHaveMatchingIdentity() throws Exception {
        var jwt = decoder.decode(TestJwt.signed(Map.of("sub", "bob")));
        var key = JWKSet.parse(TestJwt.publicJwkSet()).getKeys().getFirst();
        assertEquals(key.getKeyID(), jwt.getHeaders().get("kid"));
        assertEquals("RS256", jwt.getHeaders().get("alg"));
        assertEquals("bob", jwt.getSubject());
        assertEquals(List.of("phase05-api"), jwt.getAudience());
        assertEquals(NOW.plusSeconds(300), jwt.getExpiresAt());
    }

    @Test void wrongRealSignatureIsRejected() {
        assertThrows(JwtException.class, () -> decoder.decode(TestJwt.signedWithWrongKey(Map.of())));
    }

    @ParameterizedTest @ValueSource(strings = {"RS384", "RS512", "HS256"})
    void onlyRs256AllowedEvenWithSameKeyOrPublicKeyMac(String algorithm) {
        String signed = TestJwt.signedWithAlgorithm(algorithm, Map.of());
        assertThrows(JwtException.class, () -> decoder.decode(signed));
    }

    @Test void issuerAndAudienceAreIndependentTrustBoundaries() {
        for (var claims : List.of(Map.<String,Object>of("iss", "urn:other"),
                Map.<String,Object>of("aud", List.of("other-api")), Map.<String,Object>of("aud", List.of()))) {
            String token = TestJwt.signed(claims);
            assertThrows(JwtException.class, () -> decoder.decode(token));
        }
        assertEquals(List.of("other-api", "phase05-api"), decoder.decode(TestJwt.signed(
                Map.of("aud", List.of("other-api", "phase05-api")))).getAudience());
    }

    @Test void expiryAndNotBeforeRespectExplicitSixtySecondSkew() {
        assertNotNull(decoder.decode(TestJwt.signed(Map.of("iat", NOW.minusSeconds(300), "nbf", NOW.minusSeconds(300), "exp", NOW.minusSeconds(30)))));
        assertNotNull(decoder.decode(TestJwt.signed(Map.of("nbf", NOW.plusSeconds(30)))));
        String expired = TestJwt.signed(Map.of("iat", NOW.minusSeconds(600), "nbf", NOW.minusSeconds(600),
                "exp", NOW.minusSeconds(120)));
        String future = TestJwt.signed(Map.of("nbf", NOW.plusSeconds(120)));
        assertThrows(JwtException.class, () -> decoder.decode(expired));
        assertThrows(JwtException.class, () -> decoder.decode(future));
    }

    @Test void applicationRequiresExpiryButScopeRemainsAuthorizationNotDecoder() {
        var claims = new HashMap<String,Object>(); claims.put("exp", null);
        String missingExpiry = TestJwt.signed(claims);
        assertThrows(JwtException.class, () -> decoder.decode(missingExpiry));
        var noScope = decoder.decode(TestJwt.signed(Map.of("scope", "")));
        assertEquals("alice", noScope.getSubject());
        assertTrue(new JwtGrantedAuthoritiesConverter().convert(noScope).isEmpty());
        var scoped = decoder.decode(TestJwt.signed(Map.of("scope", "orders.read", "roles", List.of("ADMIN"))));
        assertEquals(List.of("SCOPE_orders.read"), new JwtGrantedAuthoritiesConverter().convert(scoped)
                .stream().map(a -> a.getAuthority()).toList());
    }

    @Test void q04_bearerOnlyCsrfBoundary_experimentRuns() throws Exception {
        // Real standard filter: no cookie authentication; valid Bearer POST needs no CSRF token.
        try (var context = new org.springframework.web.context.support.AnnotationConfigWebApplicationContext()) {
            context.setServletContext(new org.springframework.mock.web.MockServletContext());
            context.register(FilterConfig.class);
            context.addBeanFactoryPostProcessor(factory -> factory.registerSingleton("decoder", decoder));
            context.refresh();
            var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup(context)
                    .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity()).build();
            String token = TestJwt.signed(Map.of());
            mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/orders")
                    .header("Authorization", "Bearer " + token)).andExpect(
                    org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
            mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/orders")
                    .cookie(new jakarta.servlet.http.Cookie("access_token", token))).andExpect(
                    org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isUnauthorized());
            mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/products")
                    .header("Origin", "https://example.test")).andExpect(
                    org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isUnauthorized());
        }
    }

    @org.springframework.context.annotation.Configuration(proxyBeanMethods = false)
    @org.springframework.web.servlet.config.annotation.EnableWebMvc
    @org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
    static class FilterConfig {
        @org.springframework.context.annotation.Bean
        org.springframework.security.web.SecurityFilterChain chain(
                org.springframework.security.config.annotation.web.builders.HttpSecurity http, JwtDecoder decoder) throws Exception {
            return phase05.d13_security.Ex01_AuthenticationAuthorization.security(http, decoder);
        }
        @org.springframework.context.annotation.Bean Probe probe() { return new Probe(); }
    }
    @org.springframework.web.bind.annotation.RestController
    static class Probe {
        @org.springframework.web.bind.annotation.PostMapping("/api/orders")
        public String create() { return "accepted"; }
        @org.springframework.web.bind.annotation.GetMapping("/api/products")
        public String products() { return "accepted"; }
    }

    @Test void idTokenAudienceDoesNotBecomeApiAccessToken() {
        String idToken = TestJwt.signed(Map.of("aud", List.of("browser-client"), "nonce", "test-nonce"));
        assertThrows(JwtException.class, () -> decoder.decode(idToken));
    }
}
