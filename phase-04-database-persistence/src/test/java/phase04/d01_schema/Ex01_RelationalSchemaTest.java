package phase04.d01_schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import phase04.support.PostgresFixture;

class Ex01_RelationalSchemaTest {
    private static PostgresFixture fixture;

    @BeforeAll static void startDatabase() { fixture = PostgresFixture.start(); }
    @AfterAll static void stopDatabase() { fixture.close(); }

    @Test @DisplayName("B1 — Schema bảo vệ quan hệ và snapshot giá")
    void b01_schemaEnforcesForeignKeyAndSnapshot() throws SQLException {
        fixture.reset(String.join(";", Ex01_RelationalSchema.migrations()), "");
        try (var c = fixture.dataSource().getConnection(); var s = c.createStatement()) {
            s.executeUpdate("INSERT INTO customers(id,name) VALUES (1,'Mai')");
            s.executeUpdate("INSERT INTO products(id,name,price,stock) VALUES (1,'Tea',10.00,4)");
            s.executeUpdate("INSERT INTO orders(id,customer_id,status,created_at) VALUES (1,1,'NEW',TIMESTAMPTZ '2026-01-01 00:00:00+00')");
            s.executeUpdate("INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (1,1,1,10.00)");
            assertThrows(SQLException.class, () -> s.executeUpdate("INSERT INTO orders(customer_id,status,created_at) VALUES (999,'NEW',CURRENT_TIMESTAMP)"));
            assertSqlState("23505", () -> s.executeUpdate("INSERT INTO customers(name) VALUES ('Mai')"));
            assertSqlState("23505", () -> s.executeUpdate("INSERT INTO products(name,price,stock) VALUES ('Tea',9.00,1)"));
            assertSqlState("23502", () -> s.executeUpdate("INSERT INTO products(name,price,stock) VALUES (NULL,9.00,1)"));
            assertSqlState("23514", () -> s.executeUpdate("INSERT INTO products(name,price,stock) VALUES ('Bad price',-1,1)"));
            assertSqlState("23514", () -> s.executeUpdate("INSERT INTO products(name,price,stock) VALUES ('Bad stock',1,-1)"));
            assertSqlState("23502", () -> s.executeUpdate("INSERT INTO orders(customer_id,status,created_at) VALUES (NULL,'NEW',CURRENT_TIMESTAMP)"));
            assertSqlState("23514", () -> s.executeUpdate("INSERT INTO orders(customer_id,status,created_at) VALUES (1,'PENDING',CURRENT_TIMESTAMP)"));
            assertSqlState("23502", () -> s.executeUpdate("INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (1,1,NULL,10.00)"));
            assertSqlState("23514", () -> s.executeUpdate("INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (1,1,0,10.00)"));
            assertSqlState("23514", () -> s.executeUpdate("INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (1,1,1001,10.00)"));
            assertSqlState("23514", () -> s.executeUpdate("INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (1,1,1,-1)"));
            assertSqlState("23505", () -> s.executeUpdate("INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (1,1,2,10.00)"));
            s.executeUpdate("UPDATE products SET price=12.00 WHERE id=1");
            try (var rs = s.executeQuery("SELECT unit_price FROM order_items WHERE order_id=1")) {
                rs.next();
                assertEquals(new java.math.BigDecimal("10.00"), rs.getBigDecimal(1));
            }
        }
    }

    private static void assertSqlState(String expected, SqlAction action) {
        SQLException failure = assertThrows(SQLException.class, action::run);
        assertEquals(expected, failure.getSQLState());
    }

    @FunctionalInterface
    private interface SqlAction {
        void run() throws SQLException;
    }
}
