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
    static final Reason Q1_CTOR_INJECTION_TESTABILITY = null;
    // Q2 — static utility có luôn xấu.
    static final Boolean Q2_STATIC_ALWAYS_BAD = null;

    // Q5 — test khó viết phản ánh vấn đề gì.
    static final Signal Q5_HARD_TO_TEST_SIGNAL = null;

    /** Dependency tường minh, thay được trong test. */
    public static final class CheckoutService {

        private final PaymentGateway paymentGateway;
        private final OrderRepository orderRepository;

        public CheckoutService(PaymentGateway paymentGateway, OrderRepository orderRepository) {
            this.paymentGateway = paymentGateway;
            this.orderRepository = orderRepository;
        }

        public String checkout(String orderId, long totalCents) {
            throw new UnsupportedOperationException("TODO Q1");
        }
    }
}

/* ANSWER Q1:
 *
 */

/* ANSWER Q2:
 *
 */

/* ANSWER Q5:
 *
 */

/* ANSWER Q6:
 *
 */
