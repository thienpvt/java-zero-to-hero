package phase03.d21_capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import phase03.d21_capstone.OrderApplicationService.CheckoutResult;
import phase03.d21_capstone.OrderApplicationService.LineItem;
import phase03.d21_capstone.OrderApplicationService.Order;
import phase03.d21_capstone.OrderApplicationService.OrderRequest;
import phase03.d21_capstone.OrderApplicationService.OrderStatus;
import phase03.d21_capstone.OrderApplicationService.PaymentResult;

class CheckoutTest {

    private static final OrderRequest REQUEST = new OrderRequest("alice", List.of(
            new LineItem("book", 1_000, 3), new LineItem("pen", 500, 2)), false);

    private static final class FakeSystems {
        final Map<String, Order> saved = new HashMap<>();
        final List<Long> charges = new ArrayList<>();
        final List<String> events = new ArrayList<>();
        boolean decline;
        boolean failSave;
        boolean failSaveAfterCommit;
        boolean failPublish;
        boolean failPublishAfterDelivery;

        OrderApplicationService service() {
            return new OrderApplicationService(
                    request -> request.items().stream().mapToLong(LineItem::totalCents).sum()
                            * (request.vip() ? 9 : 10) / 10,
                    cents -> {
                        charges.add(cents);
                        return new PaymentResult(!decline, "ref-" + cents);
                    },
                    order -> {
                        if (failSave) throw new IllegalStateException("save failed");
                        saved.put(order.id(), order);
                        if (failSaveAfterCommit) throw new IllegalStateException("save response lost");
                    },
                    order -> {
                        if (failPublish) throw new IllegalStateException("publish failed");
                        events.add(order.id());
                        if (failPublishAfterDelivery) throw new IllegalStateException("publish response lost");
                    });
        }
    }

    @Test
    void b02_standardOrderPreservesTotalAndEffects() {
        FakeSystems fake = new FakeSystems();
        CheckoutResult result = fake.service().checkout(REQUEST);
        assertEquals(OrderStatus.SAVED, result.status());
        assertEquals(4_000, result.totalCents());
        assertEquals(List.of(4_000L), fake.charges);
        assertEquals(4_000, fake.saved.get("o-1").totalCents());
        assertEquals(List.of("o-1"), fake.events);
    }

    @Test
    void b02_vipOrderPreservesDiscount() {
        FakeSystems fake = new FakeSystems();
        assertEquals(3_600, fake.service().checkout(new OrderRequest("alice", REQUEST.items(), true)).totalCents());
    }

    @Test
    void b02_largeOrderPreservesAutomaticDecline() {
        FakeSystems fake = new FakeSystems();
        CheckoutResult result = fake.service().checkout(new OrderRequest("bob",
                List.of(new LineItem("tv", 100_000, 2)), false));
        assertEquals(OrderStatus.PAYMENT_DECLINED, result.status());
        assertEquals(200_000, result.totalCents());
        assertTrue(fake.charges.isEmpty());
        assertTrue(fake.saved.isEmpty());
        assertTrue(fake.events.isEmpty());
    }

    @Test
    void b03_declinedPaymentDoesNotPersistOrPublish() {
        FakeSystems fake = new FakeSystems();
        fake.decline = true;
        CheckoutResult result = fake.service().checkout(REQUEST);
        assertEquals(OrderStatus.PAYMENT_DECLINED, result.status());
        assertTrue(fake.saved.isEmpty());
        assertTrue(fake.events.isEmpty());
    }

    @Test
    void b04_chargeThenSaveFailureKeepsExternalChargeVisible() {
        FakeSystems fake = new FakeSystems();
        fake.failSave = true;
        CheckoutResult result = fake.service().checkout(REQUEST);
        assertEquals(OrderStatus.SAVE_OUTCOME_UNKNOWN, result.status());
        assertEquals("ref-4000", result.paymentReference());
        assertEquals(List.of(4_000L), fake.charges);
        assertTrue(fake.saved.isEmpty());
    }

    @Test
    void b04_saveResponseLostDoesNotClaimOrderAbsent() {
        FakeSystems fake = new FakeSystems();
        fake.failSaveAfterCommit = true;
        CheckoutResult result = fake.service().checkout(REQUEST);
        assertEquals(OrderStatus.SAVE_OUTCOME_UNKNOWN, result.status());
        assertEquals("ref-4000", result.paymentReference());
        assertEquals(4_000, fake.saved.get("o-1").totalCents());
        assertEquals(List.of(4_000L), fake.charges);
    }

    @Test
    void b05_publishFailureKeepsCommittedOrder() {
        FakeSystems fake = new FakeSystems();
        fake.failPublish = true;
        CheckoutResult result = fake.service().checkout(REQUEST);
        assertEquals(OrderStatus.PUBLISH_OUTCOME_UNKNOWN, result.status());
        assertEquals(4_000, fake.saved.get("o-1").totalCents());
        assertTrue(fake.events.isEmpty());
    }

    @Test
    void b05_publishResponseLostDoesNotClaimEventAbsent() {
        FakeSystems fake = new FakeSystems();
        fake.failPublishAfterDelivery = true;
        CheckoutResult result = fake.service().checkout(REQUEST);
        assertEquals(OrderStatus.PUBLISH_OUTCOME_UNKNOWN, result.status());
        assertEquals(List.of("o-1"), fake.events);
        assertEquals(4_000, fake.saved.get("o-1").totalCents());
    }

    @Test
    void b02_invalidInputRejectedBeforeExternalEffects() {
        FakeSystems fake = new FakeSystems();
        OrderApplicationService service = fake.service();
        assertThrows(IllegalArgumentException.class, () -> service.checkout(new OrderRequest(" ", REQUEST.items(), false)));
        assertThrows(IllegalArgumentException.class, () -> service.checkout(new OrderRequest("alice", List.of(), false)));
        assertTrue(fake.charges.isEmpty());
    }
}
