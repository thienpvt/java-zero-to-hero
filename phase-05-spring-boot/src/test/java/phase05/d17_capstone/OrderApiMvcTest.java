package phase05.d17_capstone;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/** B6/B7: standalone MVC with trusted mock JWT, not decoder/filter/scope acceptance (Task7). */
class OrderApiMvcTest {
    static OrderServicePostgresTest.Harness h;
    MockMvc mvc;
    final JwtAuthenticationToken alice = principal("alice");
    final JwtAuthenticationToken bob = principal("bob");
    @BeforeAll static void start() { h = new OrderServicePostgresTest.Harness(); }
    @AfterAll static void close() { if (h != null) h.close(); }
    @BeforeEach void reset() throws Exception {
        h.reset();
        mvc = mvc(h.orders);
    }
    static MockMvc mvc(OrderService service) {
        return MockMvcBuilders.standaloneSetup(new OrderController(service), new ProductController(service))
                .setControllerAdvice(new CapstoneErrors()).build();
    }
    static JwtAuthenticationToken principal(String subject) {
        return new JwtAuthenticationToken(Jwt.withTokenValue("mock-only-not-verified").header("alg", "RS256")
                .subject(subject).claim("scope", "orders.read orders.write products.read").build());
    }
    static String validJson() { return "{\"items\":[{\"productId\":11,\"quantity\":2}]}"; }
    long create() throws Exception {
        var result = mvc.perform(post("/api/orders").principal(alice).contentType(MediaType.APPLICATION_JSON).content(validJson()))
                .andExpect(status().isCreated()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total").value(8.5)).andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.items[0].unitPrice").value(4.25)).andReturn();
        long id = h.jdbc.queryForObject("select id from orders", Long.class);
        assertEquals("/api/orders/" + id, result.getResponse().getHeader("Location"));
        return id;
    }

    @Test void createGetCancelAndOwnerListUseFrozenPathsAndDtoSnapshots() throws Exception {
        long id = create();
        mvc.perform(get("/api/orders/{id}", id).principal(alice)).andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(2)).andExpect(jsonPath("$.status").value("NEW"));
        mvc.perform(get("/api/orders").principal(alice)).andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(id)).andExpect(jsonPath("$.items[0].items[0].unitPrice").value(4.25))
                .andExpect(jsonPath("$.page").value(0)).andExpect(jsonPath("$.size").value(20));
        mvc.perform(get("/api/orders").principal(bob)).andExpect(status().isOk()).andExpect(jsonPath("$.totalItems").value(0));
        mvc.perform(post("/api/orders/{id}/cancel", id).principal(alice)).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
        assertEquals(20, h.stock(11));
    }

    @Test void clientOwnerPriceAndCurrencyFieldsAreIgnoredNeverTrusted() throws Exception {
        create();
        h.clearOrders();
        mvc.perform(post("/api/orders").principal(alice).contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":8,\"subject\":\"bob\",\"currency\":\"EUR\",\"price\":0.01,"
                        + "\"items\":[{\"productId\":11,\"quantity\":1,\"unitPrice\":0.01}]}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.total").value(4.25))
                .andExpect(jsonPath("$.currency").value("USD"));
        assertEquals(7, h.jdbc.queryForObject("select customer_id from orders", Integer.class));
    }

    @Test void invalidBodiesMalformedJsonAndUnsupportedMediaNeverWrite() throws Exception {
        create();
        h.clearOrders();
        int before = h.stock(11);
        for (String json : List.of("{", "{}", "{\"items\":[]}", "{\"items\":[null]}",
                "{\"items\":[{\"productId\":11,\"quantity\":0}]}",
                "{\"items\":[{\"productId\":11,\"quantity\":1001}]}",
                "{\"items\":[{\"productId\":0,\"quantity\":1}]}",
                "{\"items\":[{\"productId\":11,\"quantity\":1},{\"productId\":11,\"quantity\":2}]}")) {
            var response = mvc.perform(post("/api/orders").principal(alice).contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isBadRequest()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(400)).andReturn().getResponse();
            assertSafe(response.getContentAsString());
        }
        mvc.perform(post("/api/orders").principal(alice).contentType(MediaType.TEXT_PLAIN).content(validJson()))
                .andExpect(status().isUnsupportedMediaType()).andExpect(jsonPath("$.status").value(415));
        assertEquals(before, h.stock(11));
        h.assertCounts(0, 0);
    }

    @Test void foreignMissingAndConflictsAreSafeProblemDetails() throws Exception {
        long id = create();
        for (String path : List.of("/api/orders/" + id, "/api/orders/999999"))
            mvc.perform(get(path).principal(bob)).andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404)).andExpect(jsonPath("$.detail").value("Resource not found."));
        mvc.perform(post("/api/orders/{id}/cancel", id).principal(bob)).andExpect(status().isNotFound());
        mvc.perform(post("/api/orders").principal(principal("unknown")).contentType(MediaType.APPLICATION_JSON).content(validJson()))
                .andExpect(status().isNotFound());
        mvc.perform(post("/api/orders").principal(alice).contentType(MediaType.APPLICATION_JSON)
                .content("{\"items\":[{\"productId\":12,\"quantity\":1}]}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
        mvc.perform(post("/api/orders/{id}/cancel", id).principal(alice)).andExpect(status().isOk());
        var response = mvc.perform(post("/api/orders/{id}/cancel", id).principal(alice)).andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)).andReturn().getResponse();
        assertSafe(response.getContentAsString());
        assertEquals(20, h.stock(11));
    }

    @Test void mvcPagesCapSizeRejectHostileSortAndKeepActualItems() throws Exception {
        create();
        mvc.perform(get("/api/products").param("size", "100").param("sort", "price"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.size").value(100)).andExpect(jsonPath("$.items[0].id").value(11));
        mvc.perform(get("/api/orders").principal(alice).param("size", "100"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items[0].items[0].quantity").value(2));
        for (String path : List.of("/api/products", "/api/orders")) {
            for (String size : List.of("0", "101", "2147483647", "2147483648", "bad"))
                mvc.perform(get(path).principal(alice).param("size", size)).andExpect(status().isBadRequest());
            mvc.perform(get(path).principal(alice).param("page", "-1")).andExpect(status().isBadRequest());
            mvc.perform(get(path).principal(alice).param("page", "2147483647").param("size", "100"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.items").isEmpty());
        }
        mvc.perform(get("/api/products").param("sort", "price;drop table products")).andExpect(status().isBadRequest());
    }

    @Test void bindingFailureDoesNotInvokeServiceAndUnexpectedDatabaseFailureStays500() throws Exception {
        create(); // real target first; stripped TODO must fail directly
        var calls = new java.util.concurrent.atomic.AtomicInteger();
        AtomicReference<RuntimeException> failure = new AtomicReference<>();
        OrderService service = (OrderService) java.lang.reflect.Proxy.newProxyInstance(OrderService.class.getClassLoader(),
                new Class<?>[] {OrderService.class}, (proxy, method, args) -> {
                    calls.incrementAndGet();
                    if (failure.get() != null) throw failure.get();
                    try { return method.invoke(h.orders, args); }
                    catch (java.lang.reflect.InvocationTargetException exception) { throw exception.getCause(); }
                });
        var isolated = mvc(service);
        isolated.perform(post("/api/orders").principal(alice).contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
        isolated.perform(post("/api/orders").principal(alice).contentType(MediaType.TEXT_PLAIN).content(validJson()))
                .andExpect(status().isUnsupportedMediaType());
        assertEquals(0, calls.get());
        failure.set(new org.springframework.dao.DataAccessResourceFailureException("SQL token password private sentinel"));
        var response = isolated.perform(get("/api/products")).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500)).andReturn().getResponse();
        assertEquals(1, calls.get());
        assertSafe(response.getContentAsString());
        // Unrelated JDK exceptions are not domain 400/404/409: advice leaves them to Boot's generic 500 error page.
        for (RuntimeException unexpected : List.of(new IllegalStateException("private sentinel"),
                new IllegalArgumentException("private sentinel"), new java.util.NoSuchElementException("private sentinel"))) {
            failure.set(unexpected);
            var thrown = assertThrows(jakarta.servlet.ServletException.class, () -> isolated.perform(get("/api/products")));
            assertSame(unexpected, thrown.getCause());
        }
    }
    private static void assertSafe(String body) {
        for (String secret : List.of("SQL", "token", "password", "sentinel", "Exception", "order_items", "stackTrace"))
            assertFalse(body.contains(secret), body);
    }
}
