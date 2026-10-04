package phase05.d17_capstone;

import static org.junit.jupiter.api.Assertions.*;
import static phase05.d17_capstone.OrderServicePostgresTest.request;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;
import org.junit.jupiter.api.*;

/** B7: workers have independent backend connections; DB, not JVM lock, arbitrates writes. */
class StockConcurrencyTest {
    static OrderServicePostgresTest.Harness h;
    @BeforeAll static void start() { h = new OrderServicePostgresTest.Harness(); }
    @AfterAll static void close() { if (h != null) h.close(); }
    @BeforeEach void reset() throws Exception { h.reset(); }

    @Test void lastStockHasOneCommittedOrderAndNoOversell() throws Exception {
        h.orders.placeOrder("alice", request(11, 1)); // TODO fails before latches or broad rejection assertions
        h.clearOrders();
        h.jdbc.update("update products set stock=1 where id=11");
        var results = race(() -> h.orders.placeOrder("alice", request(11, 1)),
                () -> h.orders.placeOrder("bob", request(11, 1)));
        assertEquals(1, results.stream().filter(Objects::nonNull).count());
        assertEquals(0, h.stock(11));
        h.assertCounts(1, 1);
        assertEquals(1, h.jdbc.queryForObject("select sum(quantity) from order_items", Integer.class));
    }

    @Test void simultaneousCancelsHaveOneWinnerAndOneConflictWithExactRestock() throws Exception {
        var control = h.orders.placeOrder("alice", request(11, 1));
        assertEquals("CANCELLED", h.orders.cancelOrder("alice", control.id()).status()); // direct TODO before latches
        h.clearOrders();
        var order = h.orders.placeOrder("alice", request(11, 3));
        assertEquals(order, h.orders.getOrder("alice", order.id()));
        var results = race(() -> h.orders.cancelOrder("alice", order.id()),
                () -> h.orders.cancelOrder("alice", order.id()));
        assertEquals(1, results.stream().filter(Objects::nonNull).count());
        assertEquals("CANCELLED", results.stream().filter(Objects::nonNull).findFirst().orElseThrow().status());
        assertEquals(20, h.stock(11));
        h.assertCounts(1, 1);
        assertThrows(CapstoneFailures.Conflict.class, () -> h.orders.cancelOrder("alice", order.id()));
        assertEquals(20, h.stock(11));
    }

    @Test void reversedMultiItemRequestsUseConsistentLocksAndWholeLoserRollback() throws Exception {
        h.orders.placeOrder("alice", request(11, 1));
        h.clearOrders();
        h.jdbc.update("update products set stock=1");
        var forward = new CreateOrderRequest(List.of(new LineRequest(11, 1), new LineRequest(12, 1)));
        var reverse = new CreateOrderRequest(List.of(new LineRequest(12, 1), new LineRequest(11, 1)));
        var results = race(() -> h.orders.placeOrder("alice", forward), () -> h.orders.placeOrder("bob", reverse));
        assertEquals(1, results.stream().filter(Objects::nonNull).count());
        assertEquals(0, h.stock(11));
        assertEquals(0, h.stock(12));
        h.assertCounts(1, 2);
        assertEquals(List.of(11L, 12L), results.stream().filter(Objects::nonNull).findFirst().orElseThrow()
                .items().stream().map(OrderItemResponse::productId).toList());
    }

    private List<OrderResponse> race(Supplier<OrderResponse> first, Supplier<OrderResponse> second) throws Exception {
        var ready = new CountDownLatch(2);
        var start = new CountDownLatch(1);
        Set<Integer> backends = ConcurrentHashMap.newKeySet();
        var executor = Executors.newFixedThreadPool(2);
        List<Future<OrderResponse>> futures = new ArrayList<>();
        try {
            for (var operation : List.of(first, second)) futures.add(executor.submit(() -> {
                h.connectionGate.set(connection -> {
                    try {
                        try (var statement = connection.createStatement(); var rows = statement.executeQuery("select pg_backend_pid()")) {
                            assertTrue(rows.next());
                            backends.add(rows.getInt(1));
                        }
                        ready.countDown();
                        if (!start.await(8, TimeUnit.SECONDS)) throw new AssertionError("worker start gate timed out");
                    } catch (Exception failure) { throw new AssertionError(failure); }
                });
                try {
                    try { return operation.get(); }
                    catch (CapstoneFailures.Conflict conflict) { return null; }
                } finally { h.connectionGate.remove(); }
            }));
            assertTrue(ready.await(8, TimeUnit.SECONDS), "both independent connections ready before contention");
            assertEquals(2, backends.size());
            start.countDown();
            List<OrderResponse> results = new ArrayList<>();
            for (var future : futures) results.add(future.get(12, TimeUnit.SECONDS));
            return results;
        } finally {
            start.countDown();
            for (var future : futures) future.cancel(true);
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(12, TimeUnit.SECONDS), "workers must stop before DB cleanup");
        }
    }
}
