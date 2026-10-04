package phase05.d17_capstone;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.flywaydb.core.Flyway;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import phase05.support.PostgresFixture;

/** B1/B2: real migration/constraints and constructor boundaries, no future service beans. */
class CapstoneSchemaTest {
    private static PostgresFixture db;
    private JdbcTemplate jdbc;
    private CapstoneSeeds seeds;

    @BeforeAll static void startDatabase() { db = PostgresFixture.start(); }
    @AfterAll static void closeDatabase() { if (db != null) db.close(); }

    @BeforeEach void freshDatabase() throws SQLException {
        db.reset("", "");
        jdbc = new JdbcTemplate(db.dataSource());
        seeds = new CapstoneSeeds(db.dataSource());
    }

    private Flyway migrateFresh() {
        assertEquals(0, jdbc.queryForObject(
                "select count(*) from information_schema.tables where table_schema='public'", Integer.class));
        // No resource SQL discovery: only learner-owned Java migration executes DDL.
        var flyway = Flyway.configure().dataSource(db.dataSource()).locations(new String[0])
                .skipDefaultResolvers(true).javaMigrations(new CapstoneSchema()).load();
        assertEquals(1, flyway.migrate().migrationsExecuted);
        return flyway;
    }

    @Test void b1_freshMigrationCreatesFourTablesAndSecondMigrateKeepsHistory() {
        var flyway = migrateFresh();
        assertEquals(Set.of("customers", "products", "orders", "order_items"), Set.copyOf(jdbc.queryForList(
                "select table_name from information_schema.tables where table_schema='public' "
                        + "and table_name <> 'flyway_schema_history'", String.class)));
        assertEquals("1", jdbc.queryForObject(
                "select version from flyway_schema_history where success=true", String.class));
        assertEquals(0, flyway.migrate().migrationsExecuted);
        flyway.validate();
        assertEquals(1, jdbc.queryForObject("select count(*) from flyway_schema_history", Integer.class));
    }

    @Test void b1_hibernateValidatesOnlyCapstoneEntities() {
        migrateFresh();
        var registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.datasource", db.dataSource())
                .applySetting("hibernate.hbm2ddl.auto", "validate")
                .build();
        try (var factory = new MetadataSources(registry)
                .addAnnotatedClass(Customer.class).addAnnotatedClass(Product.class)
                .addAnnotatedClass(PurchaseOrder.class).addAnnotatedClass(OrderItem.class)
                .buildMetadata().buildSessionFactory()) {
            assertTrue(factory.isOpen());
            assertEquals(4, factory.getMetamodel().getEntities().size());
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @Test void b1_schemaUsesBigintUsdMoneyStockAndTimestampTypes() {
        migrateFresh();
        for (String column : List.of("customers.id", "products.id", "orders.id", "orders.customer_id",
                "order_items.order_id", "order_items.product_id")) {
            assertColumnType(column, "bigint");
        }
        assertColumnType("products.stock", "integer");
        assertColumnType("order_items.quantity", "integer");
        assertColumnType("orders.created_at", "timestamp with time zone");
        assertColumnType("orders.currency", "character");
        assertEquals(3, jdbc.queryForObject("select character_maximum_length from information_schema.columns "
                + "where table_schema='public' and table_name='orders' and column_name='currency'", Integer.class));
        for (String tableColumn : List.of("products.price", "order_items.unit_price")) {
            assertColumnType(tableColumn, "numeric");
            var parts = tableColumn.split("\\.");
            assertEquals(19, jdbc.queryForObject("select numeric_precision from information_schema.columns "
                    + "where table_schema='public' and table_name=? and column_name=?", Integer.class, parts[0], parts[1]));
            assertEquals(2, jdbc.queryForObject("select numeric_scale from information_schema.columns "
                    + "where table_schema='public' and table_name=? and column_name=?", Integer.class, parts[0], parts[1]));
        }
    }

    private void assertColumnType(String tableColumn, String expected) {
        var parts = tableColumn.split("\\.");
        assertEquals(expected, jdbc.queryForObject("select data_type from information_schema.columns "
                + "where table_schema='public' and table_name=? and column_name=?", String.class, parts[0], parts[1]));
    }

    private void validControl() throws SQLException {
        seeds.seedCustomer(7, "alice");
        seeds.seedProduct(11, "Tea", new BigDecimal("4.25"), 3);
        execute("insert into orders(id,customer_id,status,currency,created_at) values(21,7,'NEW','USD',current_timestamp)");
        execute("insert into order_items(order_id,product_id,quantity,unit_price) values(21,11,1,4.25)");
        // Different, independent connection observes committed learner DDL and seed/control rows.
        try (var connection = db.dataSource().getConnection(); var statement = connection.createStatement();
                var rows = statement.executeQuery("select quantity,unit_price from order_items where order_id=21")) {
            assertTrue(rows.next());
            assertEquals(1, rows.getInt(1));
            assertEquals(new BigDecimal("4.25"), rows.getBigDecimal(2));
        }
    }

    @Test void b1_notNullConstraintsRejectDirectJdbcWrites() throws SQLException {
        migrateFresh();
        validControl();
        for (String sql : List.of(
                "insert into customers(id,subject) values(8,null)",
                "insert into products(id,name,price,stock) values(12,null,1.00,0)",
                "insert into products(id,name,price,stock) values(12,'Coffee',null,0)",
                "insert into products(id,name,price,stock) values(12,'Coffee',1.00,null)",
                "insert into orders(id,customer_id,status,currency,created_at) values(22,null,'NEW','USD',current_timestamp)",
                "insert into orders(id,customer_id,status,currency,created_at) values(22,7,null,'USD',current_timestamp)",
                "insert into orders(id,customer_id,status,currency,created_at) values(22,7,'NEW',null,current_timestamp)",
                "insert into orders(id,customer_id,status,currency,created_at) values(22,7,'NEW','USD',null)",
                "update order_items set quantity=null where order_id=21",
                "update order_items set unit_price=null where order_id=21",
                "update order_items set order_id=null where order_id=21",
                "update order_items set product_id=null where order_id=21")) {
            assertSqlState("23502", sql);
        }
        assertEquals(1, jdbc.queryForObject("select count(*) from orders", Integer.class));
    }

    @Test void b1_checksRejectInvalidStockQuantityStatusCurrencyAndPrice() throws SQLException {
        migrateFresh();
        validControl();
        // Valid extremes first: zero stock, max quantity, allowed terminal status.
        execute("update products set stock=0 where id=11");
        execute("update order_items set quantity=1000 where order_id=21");
        execute("update orders set status='CANCELLED' where id=21");
        for (String sql : List.of(
                "update products set stock=-1 where id=11",
                "update products set price=0 where id=11",
                "update products set price=-0.01 where id=11",
                "update order_items set quantity=0 where order_id=21",
                "update order_items set quantity=1001 where order_id=21",
                "update order_items set unit_price=0 where order_id=21",
                "update orders set status='PAID' where id=21",
                "update orders set currency='EUR' where id=21")) {
            assertSqlState("23514", sql);
        }
        assertEquals(0, jdbc.queryForObject("select stock from products where id=11", Integer.class));
        assertEquals(1000, jdbc.queryForObject("select quantity from order_items where order_id=21", Integer.class));
    }

    @Test void b1_foreignKeysRejectOrphansAndReferencedParentDeletion() throws SQLException {
        migrateFresh();
        validControl();
        for (String sql : List.of(
                "insert into orders(id,customer_id,status,currency,created_at) values(22,999,'NEW','USD',current_timestamp)",
                "insert into order_items(order_id,product_id,quantity,unit_price) values(999,11,1,4.25)",
                "insert into order_items(order_id,product_id,quantity,unit_price) values(21,999,1,4.25)",
                "delete from customers where id=7",
                "delete from products where id=11",
                "delete from orders where id=21")) {
            assertSqlState("23503", sql);
        }
        assertEquals(1, jdbc.queryForObject("select count(*) from order_items", Integer.class));
    }

    @Test void b1_uniqueSubjectAndCompositeItemKeyRejectDuplicates() throws SQLException {
        migrateFresh();
        validControl();
        seeds.seedCustomer(8, "bob");
        seeds.seedProduct(12, "Coffee", new BigDecimal("6.00"), 0);
        execute("insert into order_items(order_id,product_id,quantity,unit_price) values(21,12,1,6.00)");
        assertSqlState("23505", "insert into customers(id,subject) values(9,'alice')");
        assertSqlState("23505", "insert into order_items(order_id,product_id,quantity,unit_price) values(21,11,2,4.25)");
        assertEquals(2, jdbc.queryForObject("select count(*) from customers", Integer.class));
        assertEquals(2, jdbc.queryForObject("select count(*) from order_items", Integer.class));
    }

    @Test void b1_canonicalSeedsCommitAndItemUsdPriceRemainsSnapshot() throws SQLException {
        migrateFresh();
        validControl();
        assertEquals("alice", jdbc.queryForObject("select subject from customers where id=7", String.class));
        assertEquals("Tea", jdbc.queryForObject("select name from products where id=11", String.class));
        assertEquals(3, jdbc.queryForObject("select stock from products where id=11", Integer.class));
        execute("update products set price=9.99 where id=11");
        assertEquals(new BigDecimal("4.25"), jdbc.queryForObject(
                "select unit_price from order_items where order_id=21 and product_id=11", BigDecimal.class));
        assertEquals(new BigDecimal("9.99"), jdbc.queryForObject("select price from products where id=11", BigDecimal.class));
        assertEquals("USD", jdbc.queryForObject("select currency from orders where id=21", String.class));
    }

    private void execute(String sql) throws SQLException {
        try (var connection = db.dataSource().getConnection(); var statement = connection.createStatement()) {
            assertTrue(connection.getAutoCommit());
            statement.executeUpdate(sql);
        }
    }

    private void assertSqlState(String expected, String sql) {
        var failure = assertThrows(SQLException.class, () -> execute(sql), sql);
        assertEquals(expected, failure.getSQLState(), sql);
    }

    @Test void b2_lineRequestAcceptsValidQuantityBoundaries() {
        assertEquals(1, new LineRequest(11, 1).quantity());
        assertEquals(1000, new LineRequest(11, 1000).quantity());
    }

    @Test void b2_lineRequestRejectsNonpositiveProductId() {
        assertThrows(IllegalArgumentException.class, () -> new LineRequest(0, 1));
        assertThrows(IllegalArgumentException.class, () -> new LineRequest(-1, 1));
    }

    @Test void b2_lineRequestRejectsQuantityOutsideOneToThousand() {
        for (int quantity : new int[] {Integer.MIN_VALUE, -1, 0, 1001, Integer.MAX_VALUE}) {
            assertThrows(IllegalArgumentException.class, () -> new LineRequest(11, quantity));
        }
    }

    @Test void b2_createOrderRejectsNullEmptyAndNullElementItems() {
        assertThrows(IllegalArgumentException.class, () -> new CreateOrderRequest(null));
        assertThrows(IllegalArgumentException.class, () -> new CreateOrderRequest(List.of()));
        var items = new ArrayList<LineRequest>();
        items.add(null);
        assertThrows(IllegalArgumentException.class, () -> new CreateOrderRequest(items));
    }

    @Test void b2_createOrderCopiesCallerListAndKeepsDuplicatePolicyInService() {
        var items = new ArrayList<>(List.of(new LineRequest(11, 1), new LineRequest(11, 2)));
        var request = new CreateOrderRequest(items);
        items.clear();
        assertEquals(2, request.items().size());
        assertEquals(1, request.items().getFirst().quantity());
        assertEquals(2, request.items().getLast().quantity());
        assertThrows(UnsupportedOperationException.class, () -> request.items().clear());
    }
}
