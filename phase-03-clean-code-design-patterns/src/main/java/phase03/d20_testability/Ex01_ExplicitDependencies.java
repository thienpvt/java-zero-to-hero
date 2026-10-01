package phase03.d20_testability;

/**
 * Testability — Bài 1: dependency tường minh
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 20 (Testability as a Design Signal), câu 1, 2, 5, 6.
 * Cần làm trước: d19_clean_code.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_ExplicitDependenciesTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN + CODE] Vì sao constructor injection tăng testability?
 *   Bắt đầu   : điền Q1_CTOR_INJECTION_TESTABILITY, rồi cài {@code CheckoutService.checkout}
 *               gọi repository rồi gateway, trả reference.
 *   Kiểm chứng: chạy q01_prediction và q01_fakesReplaceRealities.
 *   Hoàn thành khi: hai test xanh và ANSWER Q1 nêu dependency thay được khi tạo object.
 * <p>
 * Q2 [DỰ ĐOÁN] Static utility có luôn xấu không?
 *   Bắt đầu   : điền Q2_STATIC_ALWAYS_BAD.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu static thuần không trạng thái thì test dễ.
 * <p>
 * Q5 [DỰ ĐOÁN] Unit test khó viết có thể phản ánh vấn đề thiết kế nào?
 *   Bắt đầu   : điền Q5_HARD_TO_TEST_SIGNAL bằng một giá trị của {@code Signal}.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 gắn độ khó test với coupling và trạng thái ẩn.
 * <p>
 * Q6 [TỰ TRẢ LỜI] Khi mock gần như mọi class, test có còn chứng minh behavior quan trọng không?
 *   Bắt đầu   : viết khối ANSWER Q6.
 *   Hoàn thành khi: ANSWER Q6 nêu test chỉ còn kiểm tra thứ tự lời gọi đã giả định.
 */
public class Ex01_ExplicitDependencies {

    /** Lý do constructor injection tăng testability. */
    public enum Reason {
        FEWER_LINES,
        DEPENDENCIES_REPLACEABLE_AT_CREATION,
        FASTER_COMPILATION
    }

    /** Vấn đề thiết kế khi test khó viết. */
    public enum Signal {
        HIGH_COUPLING_AND_HIDDEN_STATE,
        WEAK_TYPING,
        MISSING_COMMENTS
    }

    public interface PaymentGateway {

        String charge(long amountCents);
    }

    public interface OrderRepository {

        void save(String orderId, long totalCents);
    }

    // Q1 — vì sao constructor injection tăng testability.
    static final Reason Q1_CTOR_INJECTION_TESTABILITY = Reason.DEPENDENCIES_REPLACEABLE_AT_CREATION; // SOLUTION-VALUE
    // Q2 — static utility có luôn xấu.
    static final Boolean Q2_STATIC_ALWAYS_BAD = false; // SOLUTION-VALUE

    // Q5 — test khó viết phản ánh vấn đề gì.
    static final Signal Q5_HARD_TO_TEST_SIGNAL = Signal.HIGH_COUPLING_AND_HIDDEN_STATE; // SOLUTION-VALUE

    /** Dependency tường minh, thay được trong test. */
    public static final class CheckoutService {

        private final PaymentGateway paymentGateway;
        private final OrderRepository orderRepository;

        public CheckoutService(PaymentGateway paymentGateway, OrderRepository orderRepository) {
            this.paymentGateway = paymentGateway;
            this.orderRepository = orderRepository;
        }

        public String checkout(String orderId, long totalCents) {
            // SOLUTION-BEGIN throw Q1
            orderRepository.save(orderId, totalCents);
            return paymentGateway.charge(totalCents);
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Dependency nhận qua constructor nên test tạo object với fake thay cho hệ thật.
 * Không cần mạng, không cần database, không cần trạng thái toàn cục.
 * Mỗi đường lỗi dựng được tất định bằng cách cho fake trả hoặc ném theo ý muốn.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Không. Static utility thuần, không trạng thái và không phụ thuộc bên ngoài, test trực tiếp rất dễ.
 * Vấn đề chỉ xuất hiện khi static giữ trạng thái hoặc gọi hệ ngoài.
 * Khi đó không có đường thay thế trong test, và thứ tự chạy test trở thành ràng buộc.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Test khó viết là tín hiệu coupling cao, dependency ẩn, hoặc trạng thái toàn cục.
 * Object tự gọi new hoặc đọc static làm không có chỗ đặt fake.
 * Cách xử lý là sửa thiết kế, không phải thêm hạ tầng test.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Khi mọi collaborator đều là mock, test chỉ còn kiểm tra thứ tự và số lần gọi đã giả định sẵn.
 * Nó không chứng minh kết quả nghiệp vụ vì kết quả do chính mock tạo ra.
 * Test như vậy sẽ xanh dù logic tính toán bên trong sai hoàn toàn.
 * SOLUTION-END
 */
