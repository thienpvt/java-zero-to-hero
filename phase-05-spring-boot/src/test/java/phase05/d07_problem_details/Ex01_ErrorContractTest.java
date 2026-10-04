package phase05.d07_problem_details;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class Ex01_ErrorContractTest {
    private static final String PRIVATE = "select * from customers; class private.Owner; token-secret private-user";
    private final FakeProducts products = new FakeProducts();
    private final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        var controller = Ex01_ErrorContract.controller(products);
        var advice = Ex01_ErrorContract.advice();
        validator.afterPropertiesSet();
        mvc = standaloneSetup(controller).setControllerAdvice(advice).setValidator(validator).build();
    }

    @AfterEach
    void closeValidator() { validator.close(); }

    @Test
    void validationUsesRfc9457WithoutEchoingInputOrCallingService() throws Exception {
        validControl();
        problem(mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content("{}")),
                400, "validation", "/api/products");
        assertEquals(0, products.calls);
    }

    @Test
    void malformedJsonAndUnsupportedMediaHaveSafeDistinct4xx() throws Exception {
        validControl();
        problem(mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content("{")),
                400, "malformed-json", "/api/products");
        problem(mvc.perform(post("/api/products").contentType(MediaType.TEXT_PLAIN).content(PRIVATE)),
                415, "unsupported-media-type", "/api/products");
        assertEquals(0, products.calls);
    }

    @Test
    void notFoundAndConflictHaveStableTypesAndNoPersistenceDetails() throws Exception {
        validControl();
        products.failure = new Ex01_ErrorContract.NotFound(PRIVATE);
        problem(mvc.perform(get("/api/products/99")), 404, "not-found", "/api/products/99");
        products.failure = new Ex01_ErrorContract.Conflict(PRIVATE);
        problem(mvc.perform(get("/api/products/99")), 409, "conflict", "/api/products/99");
    }

    @Test
    void unexpectedDatabaseFailureIsSafe500NotCatchAll400() throws Exception {
        validControl();
        products.failure = new DataAccessResourceFailureException(PRIVATE);
        problem(mvc.perform(get("/api/products/99")), 500, "internal", "/api/products/99");
    }

    @Test
    void filterAuthenticationEntryPointReturns401WithBasicChallenge() throws Exception {
        // Invoke real handler contract, not MVC advice and not JWT verification.
        var entryPoint = Ex01_ErrorContract.authenticationEntryPoint(new JacksonJsonHttpMessageConverter());
        var request = new MockHttpServletRequest("GET", "/api/products");
        request.addHeader("Authorization", "Basic token-secret");
        var response = new MockHttpServletResponse();
        entryPoint.commence(request, response, new BadCredentialsException(PRIVATE));
        assertEquals(401, response.getStatus());
        assertEquals("Basic realm=\"phase05-lab\"", response.getHeader("WWW-Authenticate"));
        assertFilterProblem(response, 401, "authentication");
    }

    @Test
    void filterDeniedHandlerReturns403WithoutNewBasicChallenge() throws Exception {
        var denied = Ex01_ErrorContract.accessDeniedHandler(new JacksonJsonHttpMessageConverter());
        var request = new MockHttpServletRequest("GET", "/api/products");
        var response = new MockHttpServletResponse();
        denied.handle(request, response, new AccessDeniedException(PRIVATE));
        assertEquals(403, response.getStatus());
        assertNull(response.getHeader("WWW-Authenticate"));
        assertFilterProblem(response, 403, "forbidden");
    }

    private void validControl() throws Exception {
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"control\"}"))
                .andExpect(status().isCreated());
        products.calls = 0;
    }

    private static void problem(ResultActions result, int status, String type, String instance) throws Exception {
        result.andExpect(status().is(status))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(status))
                .andExpect(jsonPath("$.type").value("urn:phase05:problem:" + type))
                .andExpect(jsonPath("$.title").isNotEmpty())
                .andExpect(jsonPath("$.detail").isNotEmpty())
                .andExpect(jsonPath("$.instance").value(instance))
                .andExpect(jsonPath("$.trace").doesNotExist())
                .andExpect(jsonPath("$.exception").doesNotExist())
                .andExpect(content().string(not(containsString(PRIVATE))));
    }

    private static void assertFilterProblem(MockHttpServletResponse response, int status, String type) throws Exception {
        assertTrue(MediaType.APPLICATION_PROBLEM_JSON.isCompatibleWith(MediaType.parseMediaType(response.getContentType())));
        var body = new JacksonJsonHttpMessageConverter().getMapper().readTree(response.getContentAsString());
        assertEquals(status, body.get("status").asInt());
        assertEquals("urn:phase05:problem:" + type, body.get("type").asString());
        assertEquals("/api/products", body.get("instance").asString());
        assertFalse(body.get("title").asString().isBlank());
        assertFalse(body.get("detail").asString().isBlank());
        assertFalse(response.getContentAsString().contains(PRIVATE));
        assertFalse(response.getContentAsString().contains("token-secret"));
        assertFalse(response.getContentAsString().contains("private-user"));
        assertFalse(body.has("trace"));
        assertFalse(body.has("exception"));
    }

    private static class FakeProducts implements Ex01_ErrorContract.Products {
        int calls;
        RuntimeException failure;

        public Ex01_ErrorContract.ProductResponse create(Ex01_ErrorContract.CreateProductRequest request) {
            calls++;
            if (failure != null) throw failure;
            return new Ex01_ErrorContract.ProductResponse(1, request.name());
        }

        public Ex01_ErrorContract.ProductResponse find(long id) {
            calls++;
            if (failure != null) throw failure;
            return new Ex01_ErrorContract.ProductResponse(id, "Tea");
        }
    }
}
