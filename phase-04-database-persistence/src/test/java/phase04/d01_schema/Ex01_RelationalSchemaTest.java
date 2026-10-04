package phase04.d01_schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test @DisplayName("Q1 — Tự trả lời: primary key và unique constraint")
    void q01_writtenAnswer() { assertTrue(true); }

    @Test @DisplayName("Q2 — Tự trả lời: NULL và giá trị rỗng")
    void q02_writtenAnswer() { assertTrue(true); }

    @Test @DisplayName("Q3 — Thí nghiệm: giá snapshot")
    void q03_snapshotAnswerIsRecordedBySchema() { assertTrue(true); }

    @Test @DisplayName("Q4 — Thí nghiệm: foreign key")
    void q04_foreignKeyAnswerIsRecordedBySchema() { assertTrue(true); }

    @Test @DisplayName("Q5 — Tự trả lời: denormalization")
    void q05_writtenAnswer() { assertTrue(true); }

    @Test @DisplayName("B1 — Schema bảo vệ quan hệ và snapshot giá")
    void b01_schemaEnforcesForeignKeyAndSnapshot() throws SQLException {
        fixture.reset(String.join(";", Ex01_RelationalSchema.migrations()), "");
        try (var c = fixture.dataSource().getConnection(); var s = c.createStatement()) {
            s.executeUpdate("INSERT INTO customers(id,name) VALUES (1,'Mai')");
            s.executeUpdate("INSERT INTO products(id,name,price,stock) VALUES (1,'Tea',10.00,4)");
            s.executeUpdate("INSERT INTO orders(id,customer_id,status,created_at) VALUES (1,1,'NEW',TIMESTAMPTZ '2026-01-01 00:00:00+00')");
            s.executeUpdate("INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (1,1,1,10.00)");
            assertThrows(SQLException.class, () -> s.executeUpdate("INSERT INTO orders(customer_id,status,created_at) VALUES (999,'NEW',CURRENT_TIMESTAMP)"));
            assertThrows(SQLException.class, () -> s.executeUpdate("INSERT INTO customers(name) VALUES ('Mai')"));
            s.executeUpdate("UPDATE products SET price=12.00 WHERE id=1");
            try (var rs = s.executeQuery("SELECT unit_price FROM order_items WHERE order_id=1")) {
                rs.next();
                assertEquals(new java.math.BigDecimal("10.00"), rs.getBigDecimal(1));
            }
        }
    }
}
