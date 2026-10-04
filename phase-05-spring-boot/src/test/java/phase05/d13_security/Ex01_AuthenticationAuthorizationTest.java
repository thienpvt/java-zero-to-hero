package phase05.d13_security;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static phase05.d13_security.Ex01_AuthenticationAuthorization.*;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.springframework.context.annotation.*;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.server.ResponseStatusException;
import phase05.support.TestJwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

class Ex01_AuthenticationAuthorizationTest {
    AnnotationConfigWebApplicationContext context;
    MockMvc mvc;
    Orders orders;
    @BeforeEach void setup() {
        // Direct factory before Spring startup; missing learner body cannot be hidden in context failure.
        orders = Ex01_AuthenticationAuthorization.orders(Map.of(
                1L, new Order(1, "alice", 2, false), 2L, new Order(2, "bob", 3, false)));
        assertEquals("alice", orders.read("alice", 1).owner());
        context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(Config.class);
        context.addBeanFactoryPostProcessor(factory -> {
            factory.registerSingleton("orders", orders);
            factory.registerSingleton("decoder", TestJwt.decoder());
        });
        context.refresh();
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }
    @AfterEach void close() { if (context != null) context.close(); }

    @Configuration(proxyBeanMethods = false) @EnableWebMvc @EnableWebSecurity
    static class Config {
        @Bean Controller controller(Orders orders) { return new Controller(orders); }
        @Bean SecurityFilterChain chain(HttpSecurity http, JwtDecoder decoder) throws Exception {
            return Ex01_AuthenticationAuthorization.security(http, decoder);
        }
    }

    @Test void bearerAuthenticationAndScopeAuthorizationHaveDifferentChallenges() throws Exception {
        mvc.perform(get("/api/orders/1")).andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", org.hamcrest.Matchers.startsWith("Bearer")));
        String noScope = TestJwt.signed(Map.of("scope", "products.read"));
        assertEquals("alice", TestJwt.decoder().decode(noScope).getSubject());
        mvc.perform(get("/api/orders/1").header("Authorization", "Bearer " + noScope))
                .andExpect(status().isForbidden()).andExpect(header().string("WWW-Authenticate",
                        org.hamcrest.Matchers.containsString("insufficient_scope")));
        mvc.perform(get("/api/orders/1").header("Authorization", "Bearer " + TestJwt.signed(Map.of())))
                .andExpect(status().isOk()).andExpect(jsonPath("$.owner").value("alice"));
    }

    @Test void ownerCannotReadChangeOrCancelForeignOrderAtServiceBoundary() {
        for (Runnable action : java.util.List.<Runnable>of(
                () -> orders.read("alice", 2), () -> orders.changeQuantity("alice", 2, 9),
                () -> orders.cancel("alice", 2), () -> orders.read("alice", 999))) {
            assertEquals(404, assertThrows(ResponseStatusException.class, action::run).getStatusCode().value());
        }
        assertEquals(new Order(2, "bob", 3, false), orders.read("bob", 2));
        assertEquals(7, orders.changeQuantity("alice", 1, 7).quantity());
        assertTrue(orders.cancel("alice", 1).cancelled());
    }

    @Test void guessedIdAndClientOwnerCannotOverrideAuthenticatedSubject() throws Exception {
        String token = TestJwt.signed(Map.of());
        mvc.perform(get("/api/orders/2").param("customerId", "bob").header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
        mvc.perform(patch("/api/orders/2").header("Authorization", "Bearer " + token)
                .contentType("application/json").content("{\"quantity\":9,\"customerId\":\"bob\"}"))
                .andExpect(status().isNotFound());
        mvc.perform(post("/api/orders/2/cancel").header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
        mvc.perform(patch("/api/orders/1").header("Authorization", "Bearer " + token)
                .contentType("application/json").content("{\"quantity\":4,\"customerId\":\"bob\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.owner").value("alice"));
    }

    @Test void productsReadAndOrdersWriteRequireTheirOwnScopesAndUnlistedIsDenied() throws Exception {
        String read = TestJwt.signed(Map.of("scope", "orders.read"));
        mvc.perform(get("/api/products").header("Authorization", "Bearer " + read)).andExpect(status().isForbidden());
        mvc.perform(post("/api/orders/1/cancel").header("Authorization", "Bearer " + read)).andExpect(status().isForbidden());
        mvc.perform(patch("/api/orders/1").header("Authorization", "Bearer " + read)
                .contentType("application/json").content("{\"quantity\":4}")).andExpect(status().isForbidden());
        mvc.perform(get("/api/products").header("Authorization", "Bearer " + TestJwt.signed(Map.of("scope", "products.read"))))
                .andExpect(status().isOk());
        mvc.perform(get("/unlisted").header("Authorization", "Bearer " + TestJwt.signed(Map.of())))
                .andExpect(status().isForbidden());
    }

    @Test void invalidSignedTokenGets401AndNoSessionOrCookieAuthentication() throws Exception {
        var now = java.time.Instant.parse("2026-10-04T12:00:00Z");
        for (String invalid : java.util.List.of(TestJwt.signedWithWrongKey(Map.of()),
                TestJwt.signedWithAlgorithm("RS512", Map.of()),
                TestJwt.signedWithAlgorithm("HS256", Map.of()),
                TestJwt.signed(Map.of("iss", "urn:wrong")),
                TestJwt.signed(Map.of("aud", java.util.List.of("other-api"))),
                TestJwt.signed(Map.of("iat", now.minusSeconds(600), "nbf", now.minusSeconds(600), "exp", now.minusSeconds(120))),
                TestJwt.signed(Map.of("nbf", now.plusSeconds(120))))) {
            mvc.perform(get("/api/orders/1").header("Authorization", "Bearer " + invalid))
                    .andExpect(status().isUnauthorized()).andExpect(header().string("WWW-Authenticate",
                            org.hamcrest.Matchers.containsString("invalid_token")));
        }
        String token = TestJwt.signed(Map.of());
        mvc.perform(get("/api/orders/1").cookie(new jakarta.servlet.http.Cookie("access_token", token)))
                .andExpect(status().isUnauthorized());
        var result = mvc.perform(get("/api/orders/1").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andReturn();
        assertNull(result.getRequest().getSession(false));
        assertNull(result.getResponse().getHeader("Set-Cookie"));
    }

    @Test void q04_statusBoundary_experimentRuns() throws Exception {
        mvc.perform(get("/api/products")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/products").header("Authorization", "Bearer " + TestJwt.signed(Map.of("scope", "orders.read"))))
                .andExpect(status().isForbidden());
    }
}
