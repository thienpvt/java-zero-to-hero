package phase03.d21_capstone;

import java.util.List;

/**
 * Bài kiến trúc — refactor đơn hàng bằng các boundary rõ ràng.
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục Architecture Exercise.
 * Cần làm trước: đọc CoupledOrderService và chạy CoupledOrderServiceTest.
 * Cách làm: làm lần lượt B2 đến B6; chạy CheckoutTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * B1 [THÍ NGHIỆM] Ghi lại hành vi bản coupled trước khi refactor.
 *   Bắt đầu   : chạy CoupledOrderServiceTest và so số tiền, đơn lưu, charge và event.
 *   Kiểm chứng: b01_standardOrder và b01_vipOrder xanh trước khi sửa service mới.
 *   Hoàn thành khi: biết hành vi nào cần giữ, gồm cả đường từ chối.
 * <p>
 * B2 [CODE] Tách pricing, payment, repository và publisher thành boundary.
 *   Bắt đầu   : cài checkout dùng các port ở constructor; không gọi hệ ngoài thật.
 *   Kiểm chứng: chạy b02_standardOrderPreservesTotalAndEffects và b02_vipOrderPreservesDiscount.
 *   Hoàn thành khi: tổng tiền và side effect giữ nguyên hành vi trước refactor.
 * <p>
 * B3 [CODE] Thanh toán bị từ chối không lưu đơn, không phát event.
 *   Bắt đầu   : trả PAYMENT_DECLINED khi payment trả approved=false.
 *   Kiểm chứng: chạy b03_declinedPaymentDoesNotPersistOrPublish.
 *   Hoàn thành khi: trạng thái đúng và repository còn rỗng.
 * <p>
 * B4 [CODE] Charge thành công nhưng lưu đơn thất bại không được giả định rollback charge.
 *   Bắt đầu   : trả PAYMENT_CHARGED_ORDER_NOT_SAVED kèm paymentReference.
 *   Kiểm chứng: chạy b04_chargeThenSaveFailureKeepsExternalChargeVisible.
 *   Hoàn thành khi: charge còn trong fake, đơn chưa được lưu.
 * <p>
 * B5 [CODE] Publish thất bại sau khi lưu không được làm mất đơn.
 *   Bắt đầu   : trả PUBLISH_FAILED, giữ đơn trong repository.
 *   Kiểm chứng: chạy b05_publishFailureKeepsCommittedOrder.
 *   Hoàn thành khi: đơn đã lưu còn nguyên dù event không phát.
 * <p>
 * B6 [TỰ TRẢ LỜI] Điểm commit, idempotent retry và outbox nằm ở đâu?
 *   Bắt đầu   : viết ANSWER B6 sau khi các test xanh.
 *   Hoàn thành khi: giải thích được charge hệ ngoài không bị local rollback và outbox cần transaction chung với đơn.
 */
public final class OrderApplicationService {

    public record LineItem(String sku, long unitCents, int qty) {
        public LineItem {
            if (sku == null || sku.isBlank() || unitCents < 0 || qty <= 0) {
                throw new IllegalArgumentException("Mặt hàng không hợp lệ.");
            }
        }

        public long totalCents() {
            return Math.multiplyExact(unitCents, qty);
        }
    }

    public record OrderRequest(String customer, List<LineItem> items, boolean vip) {
        public OrderRequest {
            if (customer == null || customer.isBlank() || items == null || items.isEmpty()) {
                throw new IllegalArgumentException("Thiếu khách hàng hoặc mặt hàng.");
            }
            items = List.copyOf(items);
        }
    }

    public enum OrderStatus {
        SAVED, PAYMENT_DECLINED, PAYMENT_CHARGED_ORDER_NOT_SAVED, PUBLISH_FAILED
    }

    public record Order(String id, String customer, long totalCents) { }

    public record PaymentResult(boolean approved, String reference) { }

    public record CheckoutResult(OrderStatus status, String paymentReference, long totalCents) { }

    public interface PricingPolicy {
        long totalCents(OrderRequest request);
    }

    public interface PaymentGateway {
        PaymentResult charge(long totalCents);
    }

    public interface OrderRepository {
        void save(Order order);
    }

    public interface EventPublisher {
        void publish(Order order);
    }

    private final PricingPolicy pricing;
    private final PaymentGateway payment;
    private final OrderRepository orders;
    private final EventPublisher events;
    private int nextOrderNumber = 1;

    public OrderApplicationService(PricingPolicy pricing, PaymentGateway payment,
            OrderRepository orders, EventPublisher events) {
        this.pricing = java.util.Objects.requireNonNull(pricing);
        this.payment = java.util.Objects.requireNonNull(payment);
        this.orders = java.util.Objects.requireNonNull(orders);
        this.events = java.util.Objects.requireNonNull(events);
    }

    public CheckoutResult checkout(OrderRequest request) {
        // SOLUTION-BEGIN throw B2
        long total = pricing.totalCents(request);
        String id = "o-" + nextOrderNumber++;
        PaymentResult charged = payment.charge(total);
        if (!charged.approved()) {
            return new CheckoutResult(OrderStatus.PAYMENT_DECLINED, charged.reference(), total);
        }
        Order order = new Order(id, request.customer(), total);
        try {
            orders.save(order);
        } catch (RuntimeException failure) {
            return new CheckoutResult(OrderStatus.PAYMENT_CHARGED_ORDER_NOT_SAVED, charged.reference(), total);
        }
        try {
            events.publish(order);
        } catch (RuntimeException failure) {
            return new CheckoutResult(OrderStatus.PUBLISH_FAILED, charged.reference(), total);
        }
        return new CheckoutResult(OrderStatus.SAVED, charged.reference(), total);
        // SOLUTION-END
    }
}

/* ANSWER B6:
 * SOLUTION-BEGIN
 * Điểm commit cục bộ là lần lưu đơn thành công ở OrderRepository. Charge trước đó chạy ở hệ ngoài;
 * transaction cục bộ không thể hoàn tác tiền đã thu khi lưu hoặc publish thất bại.
 * Retry charge cần cùng một idempotency key để không thu hai lần. Outbox cần ghi event và đơn trong
 * cùng transaction cục bộ, rồi worker gửi event và retry an toàn ở giai đoạn sau.
 * Port chỉ xuất hiện tại ranh giới payment, persistence, publish; không tạo interface cho mỗi phép tính.
 * SOLUTION-END
 */
