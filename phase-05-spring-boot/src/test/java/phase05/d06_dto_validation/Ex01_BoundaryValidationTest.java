package phase05.d06_dto_validation;

import java.math.BigDecimal;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import phase05.support.PostgresFixture;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class Ex01_BoundaryValidationTest {
    private static PostgresFixture postgres;
    private final FakeProducts products = new FakeProducts();
    private final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    private MockMvc mvc;

    @BeforeAll
    static void startPostgres() { postgres = PostgresFixture.start(); }

    @AfterAll
    static void closePostgres() { if (postgres != null) postgres.close(); }

    @BeforeEach
    void setUp() throws SQLException {
        // Factory and student DDL execute outside assertThrows; TODO cannot masquerade as rejection.
        var controller = Ex01_BoundaryValidation.controller(products);
        postgres.reset(Ex01_BoundaryValidation.schemaSql(), "");
        validator.afterPropertiesSet();
        mvc = standaloneSetup(controller).setValidator(validator).build();
    }

    @AfterEach
    void closeValidator() { validator.close(); }

    @Test
    void servletBindingProducesOnlyResponseDtoWithServerTotalAndUsdScale() throws Exception {
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Tea\",\"price\":\"2\",\"quantity\":3,\"total\":0.01,\"currency\":\"EUR\",\"id\":999}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Tea"))
                .andExpect(jsonPath("$.price").value(2.00))
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.total").value(6.00))
                .andExpect(jsonPath("$.related").doesNotExist())
                .andExpect(jsonPath("$.internalNote").doesNotExist())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("private-owner"))))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("2.00")));
        assertEquals(new BigDecimal("2.00"), products.request.price());
        assertEquals(1, products.calls);
    }

    @Test
    void requiredFieldsAndPositivePriceQuantityValidatedBeforeService() throws Exception {
        validControl();
        for (String input : new String[] {
                "{}", "{\"name\":\" \" ,\"price\":2,\"quantity\":1}",
                "{\"name\":\"Tea\",\"price\":0,\"quantity\":1}",
                "{\"name\":\"Tea\",\"price\":-1,\"quantity\":1}",
                "{\"name\":\"Tea\",\"price\":2,\"quantity\":0}",
                "{\"name\":\"Tea\",\"price\":2,\"quantity\":-1}",
                "{\"name\":\"Tea\",\"price\":2,\"quantity\":1001}",
                "{\"name\":\"Tea\",\"price\":1.001,\"quantity\":1}" }) {
            mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(input))
                    .andExpect(status().isBadRequest());
        }
        assertEquals(0, products.calls);
    }

    @Test
    void malformedJsonAndWrongMediaNeverInvokeService() throws Exception {
        validControl();
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/products").contentType(MediaType.TEXT_PLAIN).content("Tea"))
                .andExpect(status().isUnsupportedMediaType());
        assertEquals(0, products.calls);
    }

    @Test
    void businessInvariantIsNotDtoValidation() throws Exception {
        validControl();
        products.stockAvailable = false;
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Tea\",\"price\":2,\"quantity\":1}"))
                .andExpect(status().isConflict());
        assertEquals(1, products.calls);
    }

    @Test
    void postgresRejectsInvalidWritesEvenWithoutMvc() throws SQLException {
        try (var connection = postgres.dataSource().getConnection(); var statement = connection.createStatement()) {
            assertEquals(1, statement.executeUpdate("insert into products(name,price,quantity,currency) values ('Tea',2.00,1,'USD')"));
            for (String sql : new String[] {
                    "insert into products(name,price,quantity,currency) values (null,2.00,1,'USD')",
                    "insert into products(name,price,quantity,currency) values (' ',2.00,1,'USD')",
                    "insert into products(name,price,quantity,currency) values ('Tea',null,1,'USD')",
                    "insert into products(name,price,quantity,currency) values ('Tea',-1,1,'USD')",
                    "insert into products(name,price,quantity,currency) values ('Tea',0,1,'USD')",
                    "insert into products(name,price,quantity,currency) values ('Tea',2.00,null,'USD')",
                    "insert into products(name,price,quantity,currency) values ('Tea',2.00,0,'USD')",
                    "insert into products(name,price,quantity,currency) values ('Tea',2.00,1001,'USD')",
                    "insert into products(name,price,quantity,currency) values ('Tea',2.00,1,'EUR')",
                    "insert into products(name,price,quantity,currency) values ('Tea',2.00,1,null)" }) {
                SQLException failure = assertThrows(SQLException.class, () -> statement.executeUpdate(sql));
                assertTrue(failure.getSQLState().equals("23514") || failure.getSQLState().equals("23502"));
            }
        }
        try (var connection = postgres.dataSource().getConnection(); var statement = connection.createStatement();
             var rows = statement.executeQuery("select count(*), min(price) from products")) {
            rows.next();
            assertEquals(1, rows.getInt(1));
            assertEquals(new BigDecimal("2.00"), rows.getBigDecimal(2));
        }
    }

    private void validControl() throws Exception {
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"control\",\"price\":2,\"quantity\":1}"))
                .andExpect(status().isCreated());
        products.calls = 0;
    }

    private static class FakeProducts implements Ex01_BoundaryValidation.Products {
        int calls;
        boolean stockAvailable = true;
        Ex01_BoundaryValidation.CreateProductRequest request;

        public Ex01_BoundaryValidation.ProductEntity create(Ex01_BoundaryValidation.CreateProductRequest input) {
            calls++;
            request = input;
            if (!stockAvailable) throw new Ex01_BoundaryValidation.StockConflict();
            var entity = new Ex01_BoundaryValidation.ProductEntity();
            entity.id = 42L;
            entity.name = input.name();
            entity.price = input.price();
            entity.quantity = input.quantity();
            entity.internalNote = "private-owner";
            entity.related = entity; // Entity serialization would leak internals or recurse.
            return entity;
        }
    }
}
