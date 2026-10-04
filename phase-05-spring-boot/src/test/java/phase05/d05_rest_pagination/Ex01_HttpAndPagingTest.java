package phase05.d05_rest_pagination;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class Ex01_HttpAndPagingTest {
    private final FakeProducts products = new FakeProducts();
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        // Learner factory executes before any rejection assertion; no context startup failure.
        mvc = standaloneSetup(Ex01_HttpAndPaging.controller(products)).build();
    }

    @Test
    void createReturns201LocationAndJson() throws Exception {
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Tea\",\"price\":\"2.00\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/products/41"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(41))
                .andExpect(jsonPath("$.name").value("Tea"))
                .andExpect(jsonPath("$.price").value(2.00));
        assertEquals(new Ex01_HttpAndPaging.CreateProductRequest("Tea", new BigDecimal("2.00")), products.created);
    }

    @Test
    void getIsReadOnlyAndPagesWithStableNameIdOrder() throws Exception {
        mvc.perform(get("/api/products").param("page", "0").param("size", "2").param("sort", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(2))
                .andExpect(jsonPath("$.items[1].id").value(3))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalItems").value(4))
                .andExpect(jsonPath("$.totalPages").value(2));
        mvc.perform(get("/api/products").param("page", "1").param("size", "2").param("sort", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(8))
                .andExpect(jsonPath("$.items[1].id").value(9));
        assertEquals(0, products.writes);
    }

    @Test
    void priceSortAlsoBreaksTiesByIdAndIdIsAllowed() throws Exception {
        mvc.perform(get("/api/products").param("sort", "price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(2))
                .andExpect(jsonPath("$.items[1].id").value(8))
                .andExpect(jsonPath("$.items[2].id").value(3))
                .andExpect(jsonPath("$.items[3].id").value(9));
        mvc.perform(get("/api/products").param("sort", "id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(2))
                .andExpect(jsonPath("$.items[1].id").value(3))
                .andExpect(jsonPath("$.items[2].id").value(8))
                .andExpect(jsonPath("$.items[3].id").value(9));
    }

    @Test
    void rejectsUnboundedSizesNegativePagesAndUnknownSortBeforeReading() throws Exception {
        mvc.perform(get("/api/products").param("size", "100")).andExpect(status().isOk());
        int reads = products.reads;
        for (String size : List.of("0", "-1", "101", "2147483647")) {
            mvc.perform(get("/api/products").param("size", size)).andExpect(status().isBadRequest());
        }
        mvc.perform(get("/api/products").param("page", "-1")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/products").param("sort", "privateCost")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/products").param("sort", "name desc; drop table products"))
                .andExpect(status().isBadRequest());
        assertEquals(reads, products.reads);
    }

    @Test
    void largePageDoesNotOverflowOffset() throws Exception {
        mvc.perform(get("/api/products").param("page", "2147483647").param("size", "100"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items").isEmpty());
        assertEquals(0, products.writes);
    }

    @Test
    void extremePriceExponentIs400ProblemBeforeBusinessCall() throws Exception {
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"control\",\"price\":\"2.00\"}"))
                .andExpect(status().isCreated());
        products.writes = 0;
        products.created = null;

        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Tea\",\"price\":\"1e2147483647\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.type").value("urn:phase05:problem:validation"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
        assertEquals(0, products.writes);
        org.junit.jupiter.api.Assertions.assertNull(products.created);
    }

    @Test
    void stockConflictIs409NotSuccessfulCreation() throws Exception {
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"control\",\"price\":\"2.00\"}"))
                .andExpect(status().isCreated());
        products.conflict = true;
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Tea\",\"price\":\"2.00\"}"))
                .andExpect(status().isConflict()).andExpect(header().doesNotExist("Location"));
    }

    // Fixed boundary data, not persistence/pagination implementation or learner answers.
    private static class FakeProducts implements Ex01_HttpAndPaging.Products {
        int reads;
        int writes;
        boolean conflict;
        Ex01_HttpAndPaging.CreateProductRequest created;

        public List<Ex01_HttpAndPaging.ProductResponse> all() {
            reads++;
            return List.of(product(9, "Tea", "4.00"), product(8, "Tea", "2.00"),
                    product(3, "Coffee", "4.00"), product(2, "Coffee", "2.00"));
        }

        public Ex01_HttpAndPaging.ProductResponse create(Ex01_HttpAndPaging.CreateProductRequest request) {
            if (conflict) throw new Ex01_HttpAndPaging.StockConflict();
            writes++;
            created = request;
            return new Ex01_HttpAndPaging.ProductResponse(41, request.name(), request.price());
        }

        private static Ex01_HttpAndPaging.ProductResponse product(long id, String name, String price) {
            return new Ex01_HttpAndPaging.ProductResponse(id, name, new BigDecimal(price));
        }
    }
}
