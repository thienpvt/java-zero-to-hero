package phase04.d16_migrations;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.flywaydb.core.api.FlywayException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import phase04.support.PostgresFixture;
import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
@Timeout(45)
class Ex01_MigrationCompatibilityTest {
    static PostgresFixture fixture;
    @BeforeAll static void start() { fixture = PostgresFixture.start(); }
    @AfterAll static void close() { if (fixture != null) fixture.close(); }
    @BeforeEach void reset() throws SQLException { fixture.reset("", ""); }

    @Test @DisplayName("B16 — DB rỗng lên version 2, constraint trực tiếp, migrate lại không thêm history")
    void b16_emptyDatabaseConstraintsAndRepeatHistory() throws Exception {
        Ex01_MigrationCompatibility.migrate(fixture.dataSource()); // positive learner control
        assertEquals(2, scalar("SELECT count(*) FROM flyway_schema_history WHERE success"));
        assertEquals(4, scalar("SELECT count(*) FROM information_schema.tables WHERE table_schema='public' AND table_name IN ('customers','products','orders','order_items')"));
        seed();
        for (String bad : new String[]{
                "INSERT INTO customers(name) VALUES ('owner')",
                "INSERT INTO customers(name) VALUES (NULL)",
                "INSERT INTO products(name,price,stock) VALUES ('bad-price',-1,0)",
                "INSERT INTO products(name,price,stock) VALUES ('bad-stock',1,-1)",
                "INSERT INTO orders(customer_id,status,created_at) VALUES (999,'NEW',CURRENT_TIMESTAMP)",
                "INSERT INTO orders(customer_id,status,created_at) VALUES (1,'PAID',CURRENT_TIMESTAMP)",
                "INSERT INTO orders(customer_id,status,created_at) VALUES (1,'NEW',NULL)",
                "INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (1,1,0,2.50)",
                "INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (1,1,1001,2.50)",
                "INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (1,1,1,-1)",
                "INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (999,1,1,2.50)",
                "INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (1,999,1,2.50)",
                "INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (1,1,NULL,2.50)"}) {
            SQLException error = assertThrows(SQLException.class, () -> execute(bad));
            assertTrue(error.getSQLState().startsWith("23"), error.getSQLState());
        }
        execute("INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (1,1,1000,2.50)");
        assertEquals("23505", assertThrows(SQLException.class, () -> execute(
                "INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (1,1,1,2.50)")).getSQLState());
        execute("DELETE FROM orders WHERE id=1");
        assertEquals(0, scalar("SELECT count(*) FROM order_items"));
        Ex01_MigrationCompatibility.migrate(fixture.dataSource());
        assertEquals(2, scalar("SELECT count(*) FROM flyway_schema_history"));
        assertEquals(1, scalar("SELECT count(*) FROM customers"));
        System.out.println("B16 PG=" + text("SHOW server_version") + " versions=1,2 history=2 repeat=2 fourTables=4");
    }

    @Test @DisplayName("B16 — Row hợp lệ tạo ở version 1 còn nguyên sau version 2")
    void b16_versionOneValidOldRowSurvivesVersionTwo() throws Exception {
        Ex01_MigrationCompatibility.migrate(fixture.dataSource(), "1");
        assertEquals(1, scalar("SELECT count(*) FROM flyway_schema_history"));
        seed();
        execute("INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (1,1,2,2.50)");
        Ex01_MigrationCompatibility.migrate(fixture.dataSource());
        assertEquals(2, scalar("SELECT quantity FROM order_items WHERE id=1"));
        assertEquals("2.50", text("SELECT unit_price FROM order_items WHERE id=1"));
        assertEquals("23514", assertThrows(SQLException.class, () -> execute(
                "UPDATE order_items SET quantity=1001 WHERE id=1")).getSQLState());
        Ex01_MigrationCompatibility.migrate(fixture.dataSource());
        assertEquals(2, scalar("SELECT count(*) FROM flyway_schema_history"));
    }

    @Test @DisplayName("B16 — Row cũ sai chặn upgrade, không mất dữ liệu; sửa tường minh rồi migrate")
    void b16_incompatibleOldRowBlocksUpgradeWithoutDataLoss() throws Exception {
        Ex01_MigrationCompatibility.migrate(fixture.dataSource(), "1"); // positive outside assertThrows
        seed();
        execute("INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (1,1,0,2.50)");
        assertThrows(FlywayException.class, () -> Ex01_MigrationCompatibility.migrate(fixture.dataSource()));
        assertEquals(0, scalar("SELECT quantity FROM order_items WHERE id=1"));
        assertEquals(1, scalar("SELECT count(*) FROM flyway_schema_history"));
        execute("UPDATE order_items SET quantity=2 WHERE id=1"); // explicit operator repair, not silent repricing
        Ex01_MigrationCompatibility.migrate(fixture.dataSource());
        assertEquals(2, scalar("SELECT quantity FROM order_items WHERE id=1"));
        assertEquals(2, scalar("SELECT count(*) FROM flyway_schema_history"));
    }

    @Test @DisplayName("Q4 — Hai connection PostgreSQL thật tranh stock=1, bảo toàn tồn kho")
    void q04_realPostgresIndependentContendersConserveStock() throws Exception {
        Ex01_MigrationCompatibility.migrate(fixture.dataSource());
        seed();
        execute("UPDATE products SET stock=1 WHERE id=1");
        CountDownLatch ready = new CountDownLatch(2), go = new CountDownLatch(1);
        var pool = Executors.newFixedThreadPool(2);
        boolean terminated;
        try {
            var first = pool.submit(() -> contender(ready, go));
            var second = pool.submit(() -> contender(ready, go));
            assertTrue(ready.await(10, TimeUnit.SECONDS));
            go.countDown();
            int sold = (first.get(20, TimeUnit.SECONDS) ? 1 : 0) + (second.get(20, TimeUnit.SECONDS) ? 1 : 0);
            assertEquals(1, sold);
            assertEquals(1, sold + scalar("SELECT stock FROM products WHERE id=1"));
        } finally {
            go.countDown(); pool.shutdownNow();
            try { terminated = pool.awaitTermination(10, TimeUnit.SECONDS); }
            catch (InterruptedException interrupted) { Thread.currentThread().interrupt(); terminated = false; }
        }
        assertTrue(terminated);
    }

    @Test @DisplayName("Q5 — Reset disposable cho phép seed unique lặp lại, không phụ thuộc dữ liệu cũ")
    void q05_disposableResetAllowsSameUniqueSeedWithoutPriorRows() throws Exception {
        Ex01_MigrationCompatibility.migrate(fixture.dataSource());
        long id = Ex01_MigrationCompatibility.insertCustomer(fixture.dataSource(), "same-seed");
        assertTrue(id > 0);
        assertEquals(1, scalar("SELECT count(*) FROM customers"));
        fixture.reset("", "");
        Ex01_MigrationCompatibility.migrate(fixture.dataSource());
        assertEquals(0, scalar("SELECT count(*) FROM customers"));
        assertTrue(Ex01_MigrationCompatibility.insertCustomer(fixture.dataSource(), "same-seed") > 0);
        assertEquals(1, scalar("SELECT count(*) FROM customers"));
    }

    boolean contender(CountDownLatch ready, CountDownLatch go) throws Exception {
        try (Connection c = fixture.dataSource().getConnection()) {
            c.setAutoCommit(false);
            ready.countDown();
            assertTrue(go.await(10, TimeUnit.SECONDS)); // before contested stock lock
            try {
                boolean accepted = Ex01_MigrationCompatibility.reserve(c, 1, 1);
                c.commit(); return accepted;
            } catch (SQLException | RuntimeException | Error failure) {
                try { c.rollback(); } catch (SQLException rollback) { failure.addSuppressed(rollback); }
                throw failure;
            }
        }
    }

    void seed() throws SQLException {
        execute("INSERT INTO customers(name) VALUES ('owner'); INSERT INTO products(name,price,stock) VALUES ('product',2.50,3); INSERT INTO orders(customer_id,status,created_at) VALUES (1,'NEW','2026-01-01T00:00:00Z')");
    }
    void execute(String sql) throws SQLException {
        try (var c = fixture.dataSource().getConnection(); var s = c.createStatement()) {
            s.setQueryTimeout(10); s.execute(sql);
        }
    }
    long scalar(String sql) throws SQLException { return Long.parseLong(text(sql)); }
    String text(String sql) throws SQLException {
        try (var c = fixture.dataSource().getConnection(); var s = c.createStatement()) {
            s.setQueryTimeout(10);
            try (var rows = s.executeQuery(sql)) { assertTrue(rows.next()); return rows.getString(1); }
        }
    }
}
