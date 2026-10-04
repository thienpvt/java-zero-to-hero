package phase05.d11_concurrency;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.OptimisticLockException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import phase05.support.PostgresFixture;

class Ex01_StockRaceTest {
    static PostgresFixture db;
    JdbcTemplate jdbc;
    @BeforeAll static void start() { db = PostgresFixture.start(); }
    @AfterAll static void stop() { if (db != null) db.close(); }
    @BeforeEach void reset() throws Exception {
        db.reset("""
                create table products(id bigint primary key, stock integer not null check(stock>=0), version bigint not null);
                create table orders(id bigint generated always as identity primary key, quantity integer not null check(quantity>0));
                """, "insert into products values(1,1,0);");
        jdbc = new JdbcTemplate(db.dataSource());
    }

    @Test void q01_checkThenUpdateLosesOneDecrement() throws Exception {
        assertNotNull(Ex01_StockRace.NAIVE_SUCCESSFUL_ORDERS, "thay null: dự đoán Q1");
        var gate = new Gate();
        var results = compete(() -> {
            try (var connection = db.dataSource().getConnection()) {
                connection.setAutoCommit(false);
                try {
                    boolean bought = Ex01_StockRace.unsafeBuy(connection, 1, gate::await);
                    connection.commit();
                    return bought;
                } catch (Exception | Error failure) { connection.rollback(); throw failure; }
            }
        });
        assertEquals(2, results.stream().filter(Boolean.TRUE::equals).count());
        assertEquals(Ex01_StockRace.NAIVE_SUCCESSFUL_ORDERS.intValue(), orders());
        assertEquals(2, orders());
        assertEquals(0, stock()); // two sales for one available unit, despite nonnegative stock
    }

    @Test void q02_versionDetectsStaleWriteAndRollsBackLosingOrder() throws Exception {
        try (var sessions = mapping().buildSessionFactory()) {
            // Valid learner control before broad concurrency error handling.
            try (var session = sessions.openSession()) {
                var tx = session.beginTransaction();
                assertTrue(Ex01_StockRace.optimisticBuy(session, 1, () -> {}));
                assertThrows(IllegalArgumentException.class, () -> Ex01_StockRace.optimisticBuy(session, 0, () -> {}));
                assertThrows(IllegalArgumentException.class, () -> Ex01_StockRace.optimisticBuy(session, -1, () -> {}));
                tx.commit();
            }
            assertEquals(1, orders());
            jdbc.update("delete from orders"); jdbc.update("update products set stock=1, version=0");
            var gate = new Gate();
            var pids = ConcurrentHashMap.<Integer>newKeySet();
            var results = compete(() -> {
                try (var session = sessions.openSession()) {
                    var tx = session.beginTransaction();
                    pids.add(((Number) session.createNativeQuery("select pg_backend_pid()", Integer.class).getSingleResult()).intValue());
                    try {
                        boolean bought = Ex01_StockRace.optimisticBuy(session, 1, gate::await);
                        tx.commit();
                        return bought;
                    } catch (OptimisticLockException conflict) {
                        if (tx.isActive()) tx.rollback();
                        return false;
                    } catch (RuntimeException | Error failure) {
                        if (tx.isActive()) tx.rollback();
                        throw failure;
                    }
                }
            });
            assertEquals(2, pids.size());
            assertEquals(1, results.stream().filter(Boolean.TRUE::equals).count());
            assertConserved();
            assertEquals(1L, jdbc.queryForObject("select version from products where id=1", Long.class));
        }
    }

    @Test void b11_conditionalUpdateHasOneWinnerAcrossIndependentTransactions() throws Exception {
        // First successful invocation reaches TODO outside any assertThrows/future wrapping.
        try (var good = Ex01_StockRace.context(db.dataSource(), () -> {})) {
            assertTrue(good.getBean(Ex01_StockRace.PurchaseService.class).buy(1));
        }
        assertEquals(1, orders());
        jdbc.update("delete from orders"); jdbc.update("update products set stock=1, version=0");
        var gate = new Gate();
        var pids = ConcurrentHashMap.<Integer>newKeySet();
        Runnable beforeWrite = () -> {
            pids.add(jdbc.queryForObject("select pg_backend_pid()", Integer.class));
            gate.await();
        };
        try (var context = Ex01_StockRace.context(db.dataSource(), beforeWrite)) {
            var service = context.getBean(Ex01_StockRace.PurchaseService.class);
            var results = compete(() -> service.buy(1));
            assertEquals(2, pids.size());
            assertEquals(1, results.stream().filter(Boolean.TRUE::equals).count());
            assertConserved();
        }
    }

    @Test void q05_invalidQuantityCannotIncreaseStockAndConflictIs409() {
        try (var context = Ex01_StockRace.context(db.dataSource(), () -> {})) {
            var service = context.getBean(Ex01_StockRace.PurchaseService.class);
            assertTrue(service.buy(1));
            assertEquals(409, Ex01_StockRace.conflictStatus());
            assertThrows(IllegalArgumentException.class, () -> service.buy(0));
            assertThrows(IllegalArgumentException.class, () -> service.buy(-1));
            assertThrows(IllegalArgumentException.class, () -> service.buy(1001));
            assertFalse(service.buy(1));
            assertConserved();
        }
    }

    @Test void q04_experimentRuns() {
        try (var context = Ex01_StockRace.context(db.dataSource(), () -> {})) {
            System.out.println("B11 DB transaction: " + context.getBean(Ex01_StockRace.PurchaseService.class).buy(1));
        }
    }

    void assertConserved() {
        assertEquals(0, stock()); assertEquals(1, orders());
        assertEquals(1, jdbc.queryForObject("select coalesce(sum(quantity),0) from orders", Integer.class) + stock());
    }
    int stock() { return jdbc.queryForObject("select stock from products where id=1", Integer.class); }
    int orders() { return jdbc.queryForObject("select count(*) from orders", Integer.class); }
    Configuration mapping() {
        return new Configuration().addAnnotatedClass(Ex01_StockRace.Product.class)
                .setProperty("hibernate.hbm2ddl.auto", "validate")
                .setProperty("hibernate.connection.url", db.jdbcUrl())
                .setProperty("hibernate.connection.username", db.username())
                .setProperty("hibernate.connection.password", db.password());
    }
    static List<Boolean> compete(Callable<Boolean> action) throws Exception {
        var pool = Executors.newFixedThreadPool(2);
        var futures = new ArrayList<Future<Boolean>>();
        try {
            futures.add(pool.submit(action)); futures.add(pool.submit(action));
            var results = new ArrayList<Boolean>();
            for (var future : futures) results.add(future.get(15, TimeUnit.SECONDS));
            return results;
        } finally {
            for (var future : futures) future.cancel(true);
            pool.shutdownNow();
            assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS), "workers must terminate");
        }
    }
    static class Gate {
        final CountDownLatch ready = new CountDownLatch(2);
        void await() {
            ready.countDown();
            try {
                if (!ready.await(5, TimeUnit.SECONDS)) throw new AssertionError("barrier timeout");
            } catch (InterruptedException failure) {
                Thread.currentThread().interrupt(); throw new AssertionError(failure);
            }
        }
    }
}
