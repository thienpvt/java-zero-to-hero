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
            // SOLUTION-BEGIN throw Q4
            return "stripe:" + amountCents;
            // SOLUTION-END
        }
    }

    static final class MomoProvider implements PaymentProvider {

        @Override
        public String charge(long amountCents) {
            // SOLUTION-BEGIN throw Q4
            return "momo:" + amountCents;
            // SOLUTION-END
        }
    }

    // Q1 — Factory giải quyết vấn đề gì.
    static final Problem Q1_FACTORY_PROBLEM = Problem.CENTRALIZE_WHICH_CONCRETE_CLASS_TO_CREATE; // SOLUTION-VALUE

    // Q4 — factory chọn implementation theo dữ liệu runtime.
    static final Boolean Q4_RUNTIME_DATA_SELECTION = true; // SOLUTION-VALUE

    // Q5 — switch khổng lồ trong factory còn extensible.
    static final Boolean Q5_GIANT_SWITCH_EXTENSIBLE = false; // SOLUTION-VALUE

    /** Chọn provider theo tham số cấu hình. */
    public static final class ProviderFactory {

        private ProviderFactory() {
        }

        public static PaymentProvider create(String providerName) {
            // SOLUTION-BEGIN throw Q4
            return switch (providerName) {
                case "stripe" -> new StripeProvider();
                case "momo" -> new MomoProvider();
                default -> throw new IllegalArgumentException("Nhà cung cấp không hỗ trợ: " + providerName);
            };
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Factory gom chỗ quyết định lớp cụ thể vào một nơi.
 * Phần còn lại của hệ chỉ thấy interface, không rải new khắp code.
 * Đổi cách chọn hoặc thêm điều kiện chọn chỉ sửa một chỗ.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Factory nhận dữ liệu lúc chạy, ví dụ tên nhà cung cấp từ cấu hình hoặc từ đơn hàng.
 * Từ dữ liệu đó nó trả về một implementation của cùng interface.
 * Caller không biết lớp nào, nên đổi quy tắc chọn không lan ra ngoài factory.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Switch khổng lồ trong factory nghĩa là mỗi provider mới vẫn phải sửa chính factory.
 * Nó gom việc tạo object nhưng không mở rộng được, chỉ giấu vấn đề đi một chỗ.
 * Cách mở rộng là đăng ký nhà cung cấp vào một map, thêm provider là thêm một dòng đăng ký riêng.
 * SOLUTION-END
 */
