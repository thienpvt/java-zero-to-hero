package phase05.d10_transactions;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.aop.support.AopUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.UnexpectedRollbackException;
import phase05.support.PostgresFixture;

class Ex01_ProxyAndRollbackTest {
    static PostgresFixture db;
    JdbcTemplate jdbc;
    @BeforeAll static void start() { db = PostgresFixture.start(); }
    @AfterAll static void stop() { if (db != null) db.close(); }
    @BeforeEach void reset() throws Exception {
        db.reset("""
                create table customers(id bigint primary key, subject varchar(100) unique not null);
                create table products(id bigint primary key, price numeric(19,2) not null, stock integer not null check(stock>=0));
                create table orders(id bigint generated always as identity primary key, customer_id bigint references customers,
                    status varchar(20) not null, currency varchar(3) not null, total numeric(19,2) not null,
                    created_at timestamp with time zone not null default current_timestamp);
                create table order_items(order_id bigint references orders, product_id bigint references products,
                    quantity integer not null check(quantity>0), unit_price numeric(19,2) not null);
                create table events(label varchar(100) not null);
                """, "insert into customers values(1,'alice'); insert into products values(1,12.34,5);");
        jdbc = new JdbcTemplate(db.dataSource());
    }

    @Test void q01_externalProxyStartsTransactionSelfInvocationDoesNot() {
        assertNotNull(Ex01_ProxyAndRollback.SELF_HAS_TRANSACTION, "thay null: dự đoán Q1");
        try (var context = Ex01_ProxyAndRollback.context(db.dataSource(), () -> {})) {
            var service = context.getBean(Ex01_ProxyAndRollback.OrderService.class);
            assertTrue(AopUtils.isAopProxy(service));
            assertTrue(service.transactionActive());
            assertEquals(Ex01_ProxyAndRollback.SELF_HAS_TRANSACTION, service.selfInvocation());
            assertFalse(service.selfInvocation());
        }
    }

    @Test void b10_failureAfterStockUpdateRollsBackOrderItemAndStock() {
        // Factory and successful learner call outside assertThrows; injected failure cannot hide TODO.
        try (var good = Ex01_ProxyAndRollback.context(db.dataSource(), () -> {})) {
            var order = good.getBean(Ex01_ProxyAndRollback.OrderService.class).placeOrder("alice", request(1));
            assertEquals("NEW", order.status());
            assertEquals(1, count("orders"));
            assertEquals(1, count("order_items"));
            assertEquals(4, stock());
        }
        jdbc.update("delete from order_items"); jdbc.update("delete from orders");
        jdbc.update("update products set stock=5");
        try (var failing = Ex01_ProxyAndRollback.context(db.dataSource(), () -> { throw new InjectedFailure(); })) {
            var service = failing.getBean(Ex01_ProxyAndRollback.OrderService.class);
            assertThrows(InjectedFailure.class, () -> service.placeOrder("alice", request(2)));
            assertEquals(5, stock());
            assertEquals(0, count("orders"));
            assertEquals(0, count("order_items"));
        }
    }

    @Test void q02_checkedDefaultCommitsExplicitRollbackForRollsBack() {
        try (var context = Ex01_ProxyAndRollback.context(db.dataSource(), () -> {})) {
            var service = context.getBean(Ex01_ProxyAndRollback.OrderService.class);
            assertTrue(service.transactionActive());
            assertThrows(Ex01_ProxyAndRollback.CheckedFailure.class, service::checkedDefault);
            assertEquals(1, count("events"));
            assertThrows(Ex01_ProxyAndRollback.CheckedFailure.class, service::checkedRollback);
            assertEquals(1, count("events"));
        }
    }

    @Test void q05_runtimeAndErrorRollbackWithoutRemoteCall() {
        try (var context = Ex01_ProxyAndRollback.context(db.dataSource(), () -> {})) {
            var service = context.getBean(Ex01_ProxyAndRollback.OrderService.class);
            service.placeOrder("alice", request(1));
            assertThrows(IllegalStateException.class, service::runtimeFailure);
            assertThrows(AssertionError.class, service::errorFailure);
            assertEquals(0, count("events"));
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("alice", request(0)));
            assertThrows(IllegalArgumentException.class, () -> service.placeOrder("alice", request(1001)));
            assertEquals(4, stock());
            assertEquals(1, count("orders"));
        }
    }

    @Test void b10_caughtRequiredFailureStillMakesOuterRollbackOnly() {
        try (var context = Ex01_ProxyAndRollback.context(db.dataSource(), () -> {})) {
            var outer = context.getBean(Ex01_ProxyAndRollback.OuterService.class);
            assertEquals(1, outer.successfulWrite());
            assertEquals(1, count("events"));
            assertThrows(UnexpectedRollbackException.class, outer::catchRequiredFailure);
            assertEquals(1, count("events"));
        }
    }

    @Test void q04_experimentRuns() {
        try (var context = Ex01_ProxyAndRollback.context(db.dataSource(), () -> {})) {
            System.out.println("B10 proxy: " + AopUtils.isAopProxy(context.getBean(Ex01_ProxyAndRollback.OrderService.class)));
        }
    }
    static class InjectedFailure extends RuntimeException {}
    static Ex01_ProxyAndRollback.CreateOrderRequest request(int quantity) {
        return new Ex01_ProxyAndRollback.CreateOrderRequest(List.of(new Ex01_ProxyAndRollback.LineRequest(1, quantity)));
    }
    int stock() { return jdbc.queryForObject("select stock from products where id=1", Integer.class); }
    int count(String table) { return jdbc.queryForObject("select count(*) from " + table, Integer.class); }
}
