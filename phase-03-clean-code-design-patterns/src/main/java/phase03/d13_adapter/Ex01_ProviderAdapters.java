package phase03.d13_adapter;

/**
 * Adapter — Bài 1: bọc SDK nhà cung cấp
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 13 (Adapter Pattern), câu 1, 2, 3, 4, 5.
 * Cần làm trước: d12_builder.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_ProviderAdaptersTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Adapter giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q1_ADAPTER_PROBLEM bằng một giá trị của {@code ProblemType}.
 *   Kiểm chứng: chạy q01_prediction. Ctrl+B trên {@code ProviderGateway} xem application gọi kiểu gì.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu chuyển interface hệ ngoài về interface mong muốn.
 * <p>
 * Q2 [DỰ ĐOÁN] Adapter khác Decorator thế nào?
 *   Bắt đầu   : điền Q2_ADAPTER_VS_DECORATOR bằng một giá trị của {@code Difference}.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu adapter đổi interface, decorator giữ interface.
 * <p>
 * Q3 [DỰ ĐOÁN] Vì sao wrap third-party SDK bằng adapter có lợi?
 *   Bắt đầu   : điền Q3_WRAP_SDK_BENEFIT.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nêu chỗ duy nhất phải sửa khi SDK đổi.
 * <p>
 * Q4 [DỰ ĐOÁN + CODE] Adapter giúp testing thế nào?
 *   Bắt đầu   : điền Q4_ADAPTER_HELPS_TESTING, rồi cài {@code StripeAdapter.charge}.
 *   Kiểm chứng: chạy q04_prediction và q04_adapterTranslatesCall.
 *   Hoàn thành khi: hai test xanh và ANSWER Q4 nêu test dùng fake theo interface của application.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Khi Stripe SDK đổi API, adapter giúp giới hạn blast radius ra sao?
 *   Bắt đầu   : viết khối ANSWER Q5.
 *   Hoàn thành khi: ANSWER Q5 nêu chỉ adapter và test của nó phải đổi.
 */
public class Ex01_ProviderAdapters {

    /** Vấn đề Adapter giải quyết. */
    public enum ProblemType {
        SLOWER_CALLS,
        EXTERNAL_INTERFACE_DIFFERS_FROM_APP_INTERFACE,
        MORE_CLASSES
    }

    /** Adapter khác Decorator ở đâu. */
    public enum Difference {
        SAME_THING,
        ADAPTER_CHANGES_INTERFACE_DECORATOR_KEEPS_IT,
        ADAPTER_IS_FASTER
    }

    public interface ProviderGateway {

        String charge(long amountCents);
    }

    /** Cho sẵn, không sửa. SDK giả có kiểu và cách gọi khác application. */
    public static final class LegacyStripeSdk {

        private int lastCharge;
        private boolean ok = true;

        public int makeCharge(int amountInCents, String currency) {
            this.lastCharge = amountInCents;
            return ok ? amountInCents : -1;
        }

        public boolean isOk() {
            return ok;
        }

        public void setOk(boolean ok) {
            this.ok = ok;
        }

        int lastCharge() {
            return lastCharge;
        }
    }

    // Q1 — Adapter giải quyết vấn đề gì.
    static final ProblemType Q1_ADAPTER_PROBLEM = null;
    // Q2 — Adapter khác Decorator ở đâu.
    static final Difference Q2_ADAPTER_VS_DECORATOR = null;
    // Q3 — bọc SDK có lợi gì.
    static final Boolean Q3_WRAP_SDK_BENEFIT = null;

    // Q4 — adapter giúp testing.
    static final Boolean Q4_ADAPTER_HELPS_TESTING = null;

    /** Chuyển cách gọi của SDK sang contract của application. */
    public static final class StripeAdapter implements ProviderGateway {

        private final LegacyStripeSdk sdk;

        public StripeAdapter(LegacyStripeSdk sdk) {
            this.sdk = sdk;
        }

        @Override
        public String charge(long amountCents) {
            throw new UnsupportedOperationException("TODO Q4");
        }
    }
}

/* ANSWER Q1:
 *
 */

/* ANSWER Q2:
 *
 */

/* ANSWER Q3:
 *
 */

/* ANSWER Q4:
 *
 */

/* ANSWER Q5:
 *
 */
