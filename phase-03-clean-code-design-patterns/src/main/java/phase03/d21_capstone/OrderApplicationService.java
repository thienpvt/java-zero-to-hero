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
 * B4 [CODE] Charge thành công nhưng kết quả lưu đơn không rõ, không được giả định rollback charge.
 *   Bắt đầu   : trả SAVE_OUTCOME_UNKNOWN kèm paymentReference; kiểm tra repository trước khi retry.
 *   Kiểm chứng: chạy b04_chargeThenSaveFailureKeepsExternalChargeVisible và b04_saveResponseLostDoesNotClaimOrderAbsent.
 *   Hoàn thành khi: charge còn trong fake, kể cả khi lưu đã commit rồi response bị mất.
 * <p>
 * B5 [CODE] Publish báo lỗi sau khi lưu không được làm mất đơn.
 *   Bắt đầu   : trả PUBLISH_OUTCOME_UNKNOWN vì event có thể đã tới người nhận; giữ đơn trong repository.
 *   Kiểm chứng: chạy b05_publishFailureKeepsCommittedOrder và b05_publishResponseLostDoesNotClaimEventAbsent.
 *   Hoàn thành khi: đơn đã lưu còn nguyên và không tuyên bố chắc event chưa phát.
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
        SAVED, PAYMENT_DECLINED, SAVE_OUTCOME_UNKNOWN, PUBLISH_OUTCOME_UNKNOWN
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
        throw new UnsupportedOperationException("TODO B2");
    }
}

/* ANSWER B6:
 *
 */
