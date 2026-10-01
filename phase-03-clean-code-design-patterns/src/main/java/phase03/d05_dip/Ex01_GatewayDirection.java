package phase03.d05_dip;

/**
 * DIP — Bài 1: hướng phụ thuộc của gateway
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 5 (Dependency Inversion Principle), câu 1, 2, 3, 5, 6.
 * Cần làm trước: d04_isp.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_GatewayDirectionTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Dependency Inversion và Dependency Injection khác nhau thế nào?
 *   Bắt đầu   : điền Q1_DIP_VS_DI bằng một giá trị của {@code Difference}.
 *   Kiểm chứng: chạy q01_prediction. Ctrl+B trên {@code PaymentGateway} xem ai phụ thuộc ai.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 tách "đảo hướng phụ thuộc" khỏi "đưa dependency từ ngoài vào".
 * <p>
 * Q2 [CODE] DIP giải quyết coupling như thế nào?
 *   Bắt đầu   : cài {@code Checkout.checkout}. Method chỉ được gọi qua {@code gateway} của interface,
 *               không được gọi class cụ thể nào.
 *   Kiểm chứng: chạy q02_checkoutThroughInterface.
 *   Hoàn thành khi: q02_checkoutThroughInterface xanh; Answer Q2 nói policy không biết implementation.
 * <p>
 * Q3 [DỰ ĐOÁN] Có cần tạo interface cho mọi class không?
 *   Bắt đầu   : điền Q3_INTERFACE_FOR_EVERY_CLASS.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nêu chi phí của interface không có biến thể.
 * <p>
 * Q5 [DỰ ĐOÁN] Nếu abstraction chỉ có một implementation thì interface có luôn cần thiết không?
 *   Bắt đầu   : điền Q5_ONE_IMPL_NEEDS_INTERFACE.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 phân biệt "chưa cần" với "không bao giờ cần".
 * <p>
 * Q6 [TỰ TRẢ LỜI] DIP giúp testing thế nào?
 *   Bắt đầu   : viết khối ANSWER Q6.
 *   Hoàn thành khi: ANSWER Q6 nêu fake thay cho hệ ngoài mà không cần mạng.
 */
public class Ex01_GatewayDirection {

    /** Khác nhau giữa DIP và DI. */
    public enum Difference {
        SAME_THING,
        DIRECTION_VS_MECHANISM,
        LAYERING_VS_INHERITANCE
    }

    public record PaymentResult(long chargedCents, String reference) {
    }

    /** Contract cho cổng thanh toán, đặt gần policy dùng nó. */
    public interface PaymentGateway {

        PaymentResult charge(long amountCents);
    }

    // Q1 — DIP và DI khác nhau ở đâu.
    static final Difference Q1_DIP_VS_DI = Difference.DIRECTION_VS_MECHANISM; // SOLUTION-VALUE

    // Q3 — có cần interface cho mọi class.
    static final Boolean Q3_INTERFACE_FOR_EVERY_CLASS = false; // SOLUTION-VALUE

    // Q5 — abstraction chỉ một implementation có luôn cần interface.
    static final Boolean Q5_ONE_IMPL_NEEDS_INTERFACE = false; // SOLUTION-VALUE

    /** Cho sẵn, không sửa: policy phụ thuộc trực tiếp implementation, đúng bản DIP vi phạm. */
    static final class StripeCheckout {

        PaymentResult checkout(long amountCents) {
            return new PaymentResult(amountCents, "stripe-direct");
        }
    }

    /** Bản đúng DIP: policy phụ thuộc {@code PaymentGateway}. */
    public static final class Checkout {

        private final PaymentGateway gateway;

        public Checkout(PaymentGateway gateway) {
            this.gateway = gateway;
        }

        public PaymentResult checkout(long amountCents) {
            // SOLUTION-BEGIN throw Q2
            if (amountCents <= 0) {
                throw new IllegalArgumentException("Số tiền phải dương: " + amountCents);
            }
            return gateway.charge(amountCents);
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * DIP nói về hướng phụ thuộc lúc biên dịch: policy không import implementation.
 * DI nói về cơ chế đưa dependency vào lúc runtime: constructor, setter, container.
 * Có thể áp dụng DI mà vẫn để policy phụ thuộc class cụ thể trong chữ ký.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Policy chỉ biết PaymentGateway, không biết Stripe hay class nào khác.
 * Đổi nhà cung cấp là thêm implementation mới, không sửa policy và không build lại policy.
 * Coupling tụt từ "biết class cụ thể" xuống "biết một contract nhỏ".
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Không. Interface chỉ đáng tạo khi có biến thể thật hoặc khi cần cắt một hệ ngoài khỏi test.
 * Với class nội bộ chỉ một cách dùng, interface thêm một lớp phải đọc mà không đổi hướng phụ thuộc.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Chưa cần không có nghĩa là không bao giờ cần.
 * Interface có một implementation vẫn hợp lý khi implementation là hệ ngoài cần thay bằng fake trong test.
 * Nếu chỉ có một implementation nội bộ và không có kế hoạch thay, để class cụ thể rẻ hơn.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Policy nhận gateway từ ngoài, nên test đưa vào một fake trả kết quả định sẵn.
 * Test không cần mạng, không cần tài khoản, không phụ thuộc nhà cung cấp.
 * Đường lỗi như từ chối thanh toán cũng dựng được tất định bằng fake.
 * SOLUTION-END
 */
