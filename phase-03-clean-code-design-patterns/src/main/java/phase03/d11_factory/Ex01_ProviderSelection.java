package phase03.d11_factory;

/**
 * Factory — Bài 1: chọn nhà cung cấp
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 11 (Factory Pattern), câu 1, 4, 5.
 * Cần làm trước: d10_strategy.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_ProviderSelectionTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Factory giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q1_FACTORY_PROBLEM bằng một giá trị của {@code Problem}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu gom chỗ quyết định lớp cụ thể.
 * <p>
 * Q4 [DỰ ĐOÁN] Factory chọn implementation dựa trên runtime data thế nào?
 *   Bắt đầu   : điền Q4_RUNTIME_DATA_SELECTION, rồi cài {@code ProviderFactory.create}.
 *   Kiểm chứng: chạy q04_prediction và q04_createReturnsRightProvider.
 *   Hoàn thành khi: hai test xanh và ANSWER Q4 nêu factory đọc dữ liệu rồi quyết định.
 * <p>
 * Q5 [DỰ ĐOÁN] Factory quá lớn với switch khổng lồ có còn extensible không?
 *   Bắt đầu   : điền Q5_GIANT_SWITCH_EXTENSIBLE.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 nêu mỗi provider mới vẫn phải sửa switch.
 */
public class Ex01_ProviderSelection {

    /** Vấn đề Factory giải quyết. */
    public enum Problem {
        SPEED_UP_CHARGE,
        CENTRALIZE_WHICH_CONCRETE_CLASS_TO_CREATE,
        REMOVE_INTERFACES
    }

    /** Contract cho mọi nhà cung cấp thanh toán. */
    public interface PaymentProvider {

        String charge(long amountCents);
    }

    static final class StripeProvider implements PaymentProvider {

        @Override
        public String charge(long amountCents) {
            throw new UnsupportedOperationException("TODO Q4");
        }
    }

    static final class MomoProvider implements PaymentProvider {

        @Override
        public String charge(long amountCents) {
            throw new UnsupportedOperationException("TODO Q4");
        }
    }

    // Q1 — Factory giải quyết vấn đề gì.
    static final Problem Q1_FACTORY_PROBLEM = null;

    // Q4 — factory chọn implementation theo dữ liệu runtime.
    static final Boolean Q4_RUNTIME_DATA_SELECTION = null;

    // Q5 — switch khổng lồ trong factory còn extensible.
    static final Boolean Q5_GIANT_SWITCH_EXTENSIBLE = null;

    /** Chọn provider theo tham số cấu hình. */
    public static final class ProviderFactory {

        private ProviderFactory() {
        }

        public static PaymentProvider create(String providerName) {
            throw new UnsupportedOperationException("TODO Q4");
        }
    }
}

/* ANSWER Q1:
 *
 */

/* ANSWER Q4:
 *
 */

/* ANSWER Q5:
 *
 */
