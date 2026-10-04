package phase05.d15_testing;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static phase05.d15_testing.Ex01_TestBoundaries.*;

import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.*;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import phase05.d13_security.Ex01_AuthenticationAuthorization;
import phase05.support.PostgresFixture;
import phase05.support.TestJwt;

class Ex01_TestBoundariesTest {
    static final class MemoryStore implements Store {
        final List<Created> rows = new ArrayList<>();
        public long insert(String owner, String name, int quantity) {
            long id = rows.size() + 1L; rows.add(new Created(id, owner, name, quantity)); return id;
        }
    }

    @Test void serviceUnitNeedsNoSpringAndUsesTrustedOwner() {
        var store = new MemoryStore();
        var service = Ex01_TestBoundaries.service(store);
        assertEquals(new Created(1, "alice", "Book", 2), service.create("alice", new Request("Book", 2)));
        assertEquals(List.of(new Created(1, "alice", "Book", 2)), store.rows);
        assertThrows(IllegalArgumentException.class, () -> service.create("alice", new Request(" ", 2)));
        assertThrows(IllegalArgumentException.class, () -> service.create("alice", new Request("Book", 0)));
        assertThrows(IllegalArgumentException.class, () -> service.create("", new Request("Book", 2)));
        assertEquals(1, store.rows.size());
    }

    @Test void mvcBindingAndValidationRejectBeforePersistence() throws Exception {
        var store = new MemoryStore();
        var controller = Ex01_TestBoundaries.controller(Ex01_TestBoundaries.service(store));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver()).build();
        mvc.perform(post("/api/orders").contentType("application/json").content("{\"name\":\"\",\"quantity\":0}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/orders").contentType("application/json").content("{\"name\":\"Book\",\"quantity\":\"bad\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/orders").contentType("application/json").content("{"))
                .andExpect(status().isBadRequest());
        assertTrue(store.rows.isEmpty());
    }

    @Test void postgresMigrationConstraintAndCommittedRowsAreNotMockEvidence() throws Exception {
        String migration = Ex01_TestBoundaries.schemaSql(); // Outside negative assertion and before DB startup.
        try (var db = PostgresFixture.start()) {
            db.reset(migration, "");
            try (var connection = db.dataSource().getConnection(); var statement = connection.createStatement()) {
                var rs = statement.executeQuery("show server_version_num"); rs.next();
                assertTrue(rs.getInt(1) >= 180000 && rs.getInt(1) < 190000);
                statement.executeUpdate("insert into boundary_orders(owner,name,quantity) values ('alice','Book',2)");
                var failure = assertThrows(java.sql.SQLException.class, () -> statement.executeUpdate(
                        "insert into boundary_orders(owner,name,quantity) values ('alice','Book',0)"));
                assertEquals("23514", failure.getSQLState());
            }
            try (var second = db.dataSource().getConnection(); var statement = second.createStatement();
                 var rs = statement.executeQuery("select owner,quantity from boundary_orders")) {
                assertTrue(rs.next()); assertEquals("alice", rs.getString(1)); assertEquals(2, rs.getInt(2));
                assertFalse(rs.next());
            }
        }
    }

    @Test void mockJwtPrincipalBypassesDecoderOnlyForControllerMapping() throws Exception {
        var store = new MemoryStore();
        try (var context = context(store)) {
            var mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
            mvc.perform(get("/api/orders/current").with(jwt().jwt(jwt -> jwt.subject("mock-owner")
                    .claim("iss", "deliberately-invalid-mock-issuer"))
                    .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("SCOPE_orders.read"))))
                    .andExpect(status().isOk()).andExpect(content().string("mock-owner"));
            mvc.perform(post("/api/orders").with(jwt().jwt(jwt -> jwt.subject("mock-owner"))
                    .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("SCOPE_orders.write")))
                    .contentType("application/json").content("{\"name\":\"Book\",\"quantity\":2,\"customerId\":\"bob\"}"))
                    .andExpect(status().isCreated()).andExpect(header().string("Location", "/api/orders/1"))
                    .andExpect(jsonPath("$.owner").value("mock-owner"));
            assertEquals("mock-owner", store.rows.getFirst().owner());
        }
    }

    @Test void mockUserRoleDoesNotCarryJwtPrincipalOrOAuthScope() throws Exception {
        var store = new MemoryStore();
        var controller = Ex01_TestBoundaries.controller(Ex01_TestBoundaries.service(store));
        var user = org.springframework.security.core.userdetails.User.withUsername("alice")
                .password("unused-test-value").roles("ADMIN").build();
        assertFalse(Jwt.class.isInstance(user));
        assertTrue(user.getAuthorities().stream().noneMatch(a -> a.getAuthority().startsWith("SCOPE_")));
        try (var context = context(store)) {
            MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build()
                    .perform(get("/api/orders/current").with(user("alice").roles("ADMIN")))
                    .andExpect(status().isForbidden());
        }
        assertNotNull(controller);
    }

    @Test void realSignedFilterRequestMapsOwnerAndRejectsInvalidAudience() throws Exception {
        var store = new MemoryStore();
        try (var context = context(store)) {
            var mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
            mvc.perform(post("/api/orders").header("Authorization", "Bearer " + TestJwt.signed(Map.of("sub", "bob")))
                    .contentType("application/json").content("{\"name\":\"Book\",\"quantity\":2,\"customerId\":\"alice\"}"))
                    .andExpect(status().isCreated()).andExpect(jsonPath("$.owner").value("bob"));
            mvc.perform(get("/api/orders/current").header("Authorization", "Bearer " + TestJwt.signed(Map.of("aud", List.of("other")))))
                    .andExpect(status().isUnauthorized()).andExpect(header().string("WWW-Authenticate",
                            org.hamcrest.Matchers.containsString("invalid_token")));
            assertEquals(List.of(new Created(1, "bob", "Book", 2)), store.rows);
        }
    }

    @Test void q04_narrowContexts_experimentRuns() throws Exception {
        var store = new MemoryStore();
        var controller = Ex01_TestBoundaries.controller(Ex01_TestBoundaries.service(store));
        MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver()).build().perform(post("/api/orders")
                .contentType("application/json").content("{\"name\":\"\",\"quantity\":0}")).andExpect(status().isBadRequest());
    }

    private AnnotationConfigWebApplicationContext context(MemoryStore store) {
        var controller = Ex01_TestBoundaries.controller(Ex01_TestBoundaries.service(store)); // Learner control before context.
        var context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext()); context.register(Config.class);
        context.addBeanFactoryPostProcessor(factory -> {
            factory.registerSingleton("controller", controller); factory.registerSingleton("decoder", TestJwt.decoder());
        });
        try { context.refresh(); return context; }
        catch (RuntimeException | Error failure) { context.close(); throw failure; }
    }

    @Configuration(proxyBeanMethods = false) @EnableWebMvc @EnableWebSecurity
    static class Config {
        @Bean SecurityFilterChain security(HttpSecurity http, JwtDecoder decoder) throws Exception {
            return Ex01_AuthenticationAuthorization.security(http, decoder);
        }
    }
}
