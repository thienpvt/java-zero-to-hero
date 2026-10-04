package phase05.d17_capstone;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.*;

/** B7: signed identity, independent committed SQL reads, no test-managed transaction. */
@Timeout(180)
class OrderOwnershipTest {
    static CapstoneHttpFixture h;
    @BeforeEach void start() throws Exception {
        CapstoneHttpFixture.probe();
        if (h == null) h = new CapstoneHttpFixture();
        h.start(false);
        h.reset();
    }
    @AfterAll static void close() { if (h != null) h.close(); }

    @Test void ownerReadCancelAndRepeatConflictKeepForeignAndUnknownIdentities404() throws Exception {
        var alice = CapstoneHttpFixture.token("alice", "orders.read orders.write");
        var bob = CapstoneHttpFixture.token("bob", "orders.read orders.write");
        var unknown = CapstoneHttpFixture.token("not-a-customer", "orders.read orders.write");
        long id = h.create(alice, CapstoneHttpFixture.orderJson());
        CapstoneHttpFixture.status(200, h.get("/api/orders/" + id, alice));
        for (String token : List.of(bob, unknown)) {
            CapstoneHttpFixture.status(404, h.get("/api/orders/" + id, token));
            CapstoneHttpFixture.status(404, h.send("POST", "/api/orders/" + id + "/cancel", "Bearer " + token, null));
        }
        CapstoneHttpFixture.status(404, h.get("/api/orders/999999", alice));
        CapstoneHttpFixture.status(404, h.get("/api/orders", unknown));
        CapstoneHttpFixture.status(404, h.send("POST", "/api/orders", "Bearer " + unknown, CapstoneHttpFixture.orderJson()));
        assertEquals(18, h.jdbc.queryForObject("select stock from products where id=11", Integer.class));
        assertEquals("NEW", h.jdbc.queryForObject("select status from orders where id=?", String.class, id));
        var cancelled = h.send("POST", "/api/orders/" + id + "/cancel", "Bearer " + alice, null);
        CapstoneHttpFixture.status(200, cancelled);
        assertEquals("CANCELLED", h.body(cancelled).get("status").asText());
        CapstoneHttpFixture.status(409, h.send("POST", "/api/orders/" + id + "/cancel", "Bearer " + alice, null));
        assertEquals(20, h.jdbc.queryForObject("select stock from products where id=11", Integer.class));
        assertEquals("CANCELLED", h.jdbc.queryForObject("select status from orders where id=?", String.class, id));
        assertEquals(1, h.jdbc.queryForObject("select count(*) from orders", Integer.class));
    }

    @Test void requestOwnerAndPriceCannotOverrideSignedSubjectAndListsStayOwnerScoped() throws Exception {
        var alice = CapstoneHttpFixture.token("alice", "orders.read orders.write");
        var bob = CapstoneHttpFixture.token("bob", "orders.read orders.write");
        long id = h.create(alice, "{\"customerId\":8,\"subject\":\"bob\",\"currency\":\"EUR\",\"price\":0.01,"
                + "\"items\":[{\"productId\":11,\"quantity\":2,\"unitPrice\":0.01}]}");
        h.create(bob, CapstoneHttpFixture.orderJson());
        assertEquals(7L, h.jdbc.queryForObject("select customer_id from orders where id=?", Long.class, id));
        var response = h.get("/api/orders/" + id, alice);
        CapstoneHttpFixture.status(200, response);
        assertEquals("USD", h.body(response).get("currency").asText());
        assertEquals(new java.math.BigDecimal("8.50"), h.body(response).get("total").decimalValue().setScale(2));
        for (String token : List.of(alice, bob)) {
            var page = h.get("/api/orders?page=0&size=1", token);
            CapstoneHttpFixture.status(200, page);
            assertEquals(1, h.body(page).get("totalItems").asInt());
            assertEquals(1, h.body(page).get("items").size());
            assertEquals(token.equals(alice), h.body(page).get("items").get(0).get("id").asLong() == id);
        }
    }
}
