package phase05.d17_capstone;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.sql.DataSource;
import org.junit.jupiter.api.*;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import phase05.d17_capstone.CapstoneFailures.Conflict;
import phase05.d17_capstone.CapstoneFailures.InvalidOrder;
import phase05.d17_capstone.CapstoneFailures.InvalidPage;
import phase05.d17_capstone.CapstoneFailures.NotFound;
import phase05.support.PostgresFixture;

/** B3/B4/B7: committed PostgreSQL state, real proxy, actual executed SQL. */
class OrderServicePostgresTest {
    static Harness h;
    @BeforeAll static void start() { h = new Harness(); }
    @AfterAll static void close() { if (h != null) h.close(); }
    @BeforeEach void reset() throws Exception { h.reset(); }

    @Test void serverPricesCommitAndRemainImmutableSnapshots() {
        var created = h.orders.placeOrder("alice", request(11, 2));
        assertTrue(created.id() > 0);
        assertEquals("NEW", created.status());
        assertEquals("USD", created.currency());
        assertEquals(new BigDecimal("8.50"), created.total());
        assertEquals(List.of(new OrderItemResponse(11, 2, new BigDecimal("4.25"))), created.items());
        assertNotNull(created.createdAt());
        assertEquals(18, h.stock(11));
        h.jdbc.update("update products set price=9.99 where id=11");
        assertEquals(created, h.orders.getOrder("alice", created.id()));
        assertThrows(UnsupportedOperationException.class, () -> created.items().clear());
    }

    @Test void duplicatesAndNullRequestsWriteNothingAfterValidControl() {
        h.orders.placeOrder("alice", request(11, 1));
        h.clearOrders();
        int stock = h.stock(11);
        assertThrows(InvalidOrder.class, () -> h.orders.placeOrder("alice", null));
        assertThrows(InvalidOrder.class, () -> h.orders.placeOrder("alice",
                new CreateOrderRequest(List.of(new LineRequest(11, 1), new LineRequest(11, 2)))));
        assertEquals(stock, h.stock(11));
        h.assertCounts(0, 0);
    }

    @Test void missingCustomerAndForeignOrdersAreNotFound() {
        var created = h.orders.placeOrder("alice", request(11, 1));
        assertEquals(created, h.orders.getOrder("alice", created.id()));
        assertThrows(NotFound.class, () -> h.orders.placeOrder("nobody", request(11, 1)));
        assertThrows(NotFound.class, () -> h.orders.getOrder("bob", created.id()));
        assertThrows(NotFound.class, () -> h.orders.cancelOrder("bob", created.id()));
        assertThrows(NotFound.class, () -> h.orders.getOrder("alice", Long.MAX_VALUE));
        assertEquals(19, h.stock(11));
        h.assertCounts(1, 1);
    }

    @Test void laterMissingOrInsufficientLineRollsBackFirstStockAndItem() {
        h.orders.placeOrder("alice", request(11, 1));
        h.clearOrders();
        int before = h.stock(11);
        for (long laterId : List.of(12L, 999L)) {
            h.sql.clear();
            var request = new CreateOrderRequest(List.of(new LineRequest(laterId, 1), new LineRequest(11, 2)));
            Class<? extends RuntimeException> expected = laterId == 12 ? Conflict.class : NotFound.class;
            assertThrows(expected, () -> h.orders.placeOrder("alice", request));
            assertTrue(h.sql.stream().anyMatch(sql -> sql.toLowerCase().startsWith("insert into order_items")));
            assertTrue(h.sql.stream().anyMatch(sql -> sql.toLowerCase().startsWith("update products set stock=stock-")));
            assertEquals(before, h.stock(11));
            h.assertCounts(0, 0);
        }
    }

    @Test void injectedFailureAfterExecutedFirstItemRollsBackOnIndependentConnection() {
        h.orders.placeOrder("alice", request(11, 1));
        h.clearOrders();
        int before = h.stock(11);
        h.sql.clear();
        h.failAfterItem.set(true);
        assertThrows(org.springframework.dao.DataAccessException.class,
                () -> h.orders.placeOrder("alice", request(11, 2)));
        assertFalse(h.failAfterItem.get()); // fault fired only after actual INSERT completed
        assertTrue(h.sql.stream().anyMatch(sql -> sql.toLowerCase().startsWith("insert into order_items")));
        assertEquals(before, h.stock(11));
        h.assertCounts(0, 0);
    }

    @Test void cancellationRestocksOriginalQuantityExactlyOnce() {
        var created = h.orders.placeOrder("alice", request(11, 3));
        h.jdbc.update("update products set price=7.99 where id=11");
        var cancelled = h.orders.cancelOrder("alice", created.id());
        assertEquals("CANCELLED", cancelled.status());
        assertEquals(created.items(), cancelled.items());
        assertEquals(created.total(), cancelled.total());
        assertEquals(20, h.stock(11));
        assertThrows(Conflict.class, () -> h.orders.cancelOrder("alice", created.id()));
        assertEquals(20, h.stock(11));
        h.assertCounts(1, 1);
    }

    @Test void productPagingUsesDatabaseBoundsAllowlistTiesAndLongOffset() throws Exception {
        h.seeds.seedProduct(13, "Tea", new BigDecimal("4.25"), 8);
        assertEquals(List.of(11L, 12L, 13L), ids(h.orders.productPage(0, 100, "id")));
        assertEquals(List.of(12L, 11L), ids(h.orders.productPage(0, 2, "name")));
        assertEquals(List.of(11L, 13L), ids(h.orders.productPage(0, 2, "price")));
        h.sql.clear();
        var page = h.orders.productPage(1, 2, "price");
        assertEquals(List.of(12L), ids(page));
        assertEquals(3, page.totalItems());
        assertEquals(2, page.totalPages());
        assertEquals(1, page.page());
        assertEquals(2, page.size());
        assertEquals(2, h.selects().size());
        assertTrue(h.selects().stream().anyMatch(sql -> sql.toLowerCase().contains("limit ? offset ?")));
        assertTrue(h.orders.productPage(Integer.MAX_VALUE, 100, "id").items().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> page.items().clear());
        for (int size : new int[] {0, -1, 101, Integer.MAX_VALUE})
            assertThrows(InvalidPage.class, () -> h.orders.productPage(0, size, "id"));
        assertThrows(InvalidPage.class, () -> h.orders.productPage(-1, 2, "id"));
        assertThrows(InvalidPage.class, () -> h.orders.productPage(0, 2, "price; drop table products"));
        assertThrows(InvalidPage.class, () -> h.orders.productPage(0, 2, null));
    }

    @Test void ownerPagingIncludesActualItemsWithConstantSqlForThreeAndTwelveFullPages() {
        h.jdbc.update("update products set stock=40 where id=11");
        List<OrderResponse> expected = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            h.orders.placeOrder("bob", request(11, 1));
            expected.add(h.orders.placeOrder("alice", request(11, 1)));
        }
        // Equal timestamps force id tie-breaker; newer id with older time proves created_at comes first.
        h.jdbc.update("update orders set created_at=?", java.sql.Timestamp.from(Instant.parse("2026-01-01T00:00:00Z")));
        h.jdbc.update("update orders set created_at=? where id=?",
                java.sql.Timestamp.from(Instant.parse("2025-12-31T00:00:00Z")), expected.getLast().id());
        expected = expected.stream().map(order -> h.orders.getOrder("alice", order.id()))
                .sorted(java.util.Comparator.comparing(OrderResponse::createdAt).thenComparingLong(OrderResponse::id)).toList();
        h.sql.clear();
        var three = h.orders.orderPage("alice", 0, 3);
        var threeSql = h.selects();
        h.sql.clear();
        var twelve = h.orders.orderPage("alice", 0, 12);
        var twelveSql = h.selects();
        assertEquals(expected.subList(0, 3), three.items());
        assertEquals(expected, twelve.items());
        assertEquals(12, three.totalItems());
        assertEquals(4, three.totalPages());
        assertEquals(3, threeSql.size());
        assertEquals(3, twelveSql.size());
        assertTrue(threeSql.stream().anyMatch(sql -> sql.toLowerCase().contains("limit ? offset ?")
                && sql.contains("subject") && sql.contains("created_at")));
        assertEquals(2, threeSql.stream().filter(sql -> sql.contains("subject")).count());
        assertEquals(expected.subList(9, 12), h.orders.orderPage("alice", 3, 3).items());
        assertTrue(h.orders.orderPage("alice", 4, 3).items().isEmpty());
        assertEquals(expected, h.orders.orderPage("alice", 0, 100).items());
        assertTrue(h.orders.orderPage("alice", Integer.MAX_VALUE, 100).items().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> twelve.items().clear());
        assertThrows(NotFound.class, () -> h.orders.orderPage("nobody", 0, 3));
        assertThrows(InvalidPage.class, () -> h.orders.orderPage("alice", -1, 3));
        for (int size : new int[] {0, -1, 101, Integer.MAX_VALUE})
            assertThrows(InvalidPage.class, () -> h.orders.orderPage("alice", 0, size));
    }

    @Test void realMainDefaultsDisableOsivAndValidateFlywaySchema() {
        // Native Boot main augmentation captures real started servlet context; no source-string assertion.
        try (var context = org.springframework.boot.SpringApplication.from(OrderApiApplication::main)
                .with(Harness.BootstrapDataSource.class).run("--server.port=0", "--spring.main.banner-mode=off",
                        "--logging.level.root=WARN", "--spring.flyway.locations=",
                        "--spring.autoconfigure.exclude=org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration")
                .getApplicationContext()) {
            assertEquals("false", context.getEnvironment().getProperty("spring.jpa.open-in-view"));
            assertEquals("validate", context.getEnvironment().getProperty("spring.jpa.hibernate.ddl-auto"));
            assertEquals(0, context.getBeansOfType(org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor.class).size());
            assertEquals("validate", context.getBean(jakarta.persistence.EntityManagerFactory.class)
                    .getProperties().get("hibernate.hbm2ddl.auto"));
            assertEquals(4, context.getBean(jakarta.persistence.EntityManagerFactory.class).getMetamodel().getEntities().size());
            var service = context.getBean(OrderService.class);
            assertTrue(org.springframework.aop.support.AopUtils.isAopProxy(service));
            var order = service.placeOrder("alice", request(11, 1));
            assertEquals(order, service.getOrder("alice", order.id()));
        }
    }

    private static List<Long> ids(PageResponse<ProductResponse> page) {
        return page.items().stream().map(ProductResponse::id).toList();
    }
    static CreateOrderRequest request(long product, int quantity) {
        return new CreateOrderRequest(List.of(new LineRequest(product, quantity)));
    }

    // Provided test-only instrumentation. Observes executed JDBC statements, never repository calls.
    static final class Harness implements AutoCloseable {
        final PostgresFixture db = PostgresFixture.start();
        final List<String> sql = new CopyOnWriteArrayList<>();
        final AtomicBoolean failAfterItem = new AtomicBoolean();
        final ThreadLocal<Consumer<Connection>> connectionGate = new ThreadLocal<>();
        final DataSource traced = (DataSource) Proxy.newProxyInstance(DataSource.class.getClassLoader(),
                new Class<?>[] {DataSource.class}, (proxy, method, args) -> {
                    try {
                        Object value = method.invoke(db.dataSource(), args);
                        if (value instanceof Connection connection) {
                            try {
                                try (var statement = connection.createStatement()) { statement.execute("set statement_timeout='8s'"); }
                                var gate = connectionGate.get();
                                if (gate != null) gate.accept(connection);
                                return wrapConnection(connection);
                            } catch (Throwable failure) { connection.close(); throw failure; }
                        }
                        return value;
                    } catch (InvocationTargetException failure) { throw failure.getCause(); }
                });
        ConfigurableApplicationContext context;
        final JdbcTemplate jdbc = new JdbcTemplate(db.dataSource());
        final CapstoneSeeds seeds = new CapstoneSeeds(db.dataSource());
        OrderService orders;

        void initialize() {
            try {
                context = new SpringApplicationBuilder(OrderApiApplication.class, TestDataSource.class)
                        .web(WebApplicationType.NONE).initializers(ctx -> ctx.getBeanFactory().registerSingleton("testSource", traced))
                        .properties("spring.jpa.open-in-view=false", "spring.jpa.hibernate.ddl-auto=validate",
                                "spring.flyway.locations=", "spring.main.banner-mode=off", "logging.level.root=WARN").run();
                orders = context.getBean(OrderService.class);
                assertTrue(org.springframework.aop.support.AopUtils.isAopProxy(orders));
                assertEquals(4, context.getBean(jakarta.persistence.EntityManagerFactory.class).getMetamodel().getEntities().size());
            } catch (RuntimeException | Error failure) { db.close(); throw failure; }
        }
        @TestConfiguration(proxyBeanMethods = false)
        static class TestDataSource {
            @Bean @Primary DataSource dataSource(org.springframework.beans.factory.BeanFactory factory) {
                return (DataSource) factory.getBean("testSource");
            }
        }
        @TestConfiguration(proxyBeanMethods = false)
        static class BootstrapDataSource {
            @Bean @Primary DataSource dataSource() { return h.db.dataSource(); }
        }
        private Connection wrapConnection(Connection connection) {
            return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class<?>[] {Connection.class},
                    (proxy, method, args) -> {
                        try {
                            Object value = method.invoke(connection, args);
                            if (value instanceof Statement statement) {
                                String prepared = args != null && args.length > 0 && args[0] instanceof String text ? text : null;
                                Class<?> api = value instanceof java.sql.PreparedStatement ? java.sql.PreparedStatement.class : Statement.class;
                                return Proxy.newProxyInstance(api.getClassLoader(), new Class<?>[] {api}, (p, m, a) -> {
                                    try {
                                        boolean execute = m.getName().startsWith("execute");
                                        String text = prepared != null ? prepared : a != null && a.length > 0 && a[0] instanceof String s ? s : "";
                                        if (execute) sql.add(text);
                                        Object result = m.invoke(statement, a);
                                        if (execute && text.toLowerCase().startsWith("insert into order_items") && failAfterItem.compareAndSet(true, false))
                                            throw new SQLException("private SQL/token fault sentinel");
                                        return result;
                                    } catch (InvocationTargetException failure) { throw failure.getCause(); }
                                });
                            }
                            return value;
                        } catch (InvocationTargetException failure) { throw failure.getCause(); }
                    });
        }
        void reset() throws SQLException {
            // Provided prerequisite probe: full stripping removes B1. Fail each method directly there,
            // not @BeforeAll bean creation (which collapses the class to one Surefire testcase).
            CapstoneSchema.sql();
            if (context == null) initialize();
            clearOrders();
            jdbc.update("delete from products");
            jdbc.update("delete from customers");
            seeds.seedCustomer(7, "alice");
            seeds.seedCustomer(8, "bob");
            seeds.seedProduct(11, "Tea", new BigDecimal("4.25"), 20);
            seeds.seedProduct(12, "Coffee", new BigDecimal("6.00"), 0);
            failAfterItem.set(false);
            sql.clear();
        }
        void clearOrders() { jdbc.update("delete from order_items"); jdbc.update("delete from orders"); }
        int stock(long id) { return jdbc.queryForObject("select stock from products where id=?", Integer.class, id); }
        void assertCounts(int orders, int items) {
            assertEquals(orders, jdbc.queryForObject("select count(*) from orders", Integer.class));
            assertEquals(items, jdbc.queryForObject("select count(*) from order_items", Integer.class));
        }
        List<String> selects() { return sql.stream().filter(text -> text.toLowerCase().startsWith("select")).toList(); }
        @Override public void close() { try { if (context != null) context.close(); } finally { db.close(); } }
    }
}
