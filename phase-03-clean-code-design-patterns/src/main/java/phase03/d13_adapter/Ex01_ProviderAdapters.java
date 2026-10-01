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
    static final ProblemType Q1_ADAPTER_PROBLEM = ProblemType.EXTERNAL_INTERFACE_DIFFERS_FROM_APP_INTERFACE; // SOLUTION-VALUE
    // Q2 — Adapter khác Decorator ở đâu.
    static final Difference Q2_ADAPTER_VS_DECORATOR = Difference.ADAPTER_CHANGES_INTERFACE_DECORATOR_KEEPS_IT; // SOLUTION-VALUE
    // Q3 — bọc SDK có lợi gì.
    static final Boolean Q3_WRAP_SDK_BENEFIT = true; // SOLUTION-VALUE

    // Q4 — adapter giúp testing.
    static final Boolean Q4_ADAPTER_HELPS_TESTING = true; // SOLUTION-VALUE

    /** Chuyển cách gọi của SDK sang contract của application. */
    public static final class StripeAdapter implements ProviderGateway {

        private final LegacyStripeSdk sdk;

        public StripeAdapter(LegacyStripeSdk sdk) {
            this.sdk = sdk;
        }

        @Override
        public String charge(long amountCents) {
            // SOLUTION-BEGIN throw Q4
            int raw = sdk.makeCharge((int) amountCents, "vnd");
            if (raw < 0 || !sdk.isOk()) {
                throw new IllegalStateException("Stripe từ chối " + amountCents);
            }
            return "stripe:" + raw;
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Adapter chuyển interface của hệ ngoài thành interface mà application mong muốn.
 * Application chỉ biết ProviderGateway, không biết kiểu tham số hay cách báo lỗi của SDK.
 * Nhờ vậy đổi nhà cung cấp không lan vào nghiệp vụ.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Adapter đổi interface: đầu vào và đầu ra khác của cái bị bọc.
 * Decorator giữ nguyên interface và chỉ thêm hành vi trước hoặc sau lời gọi.
 * Vì vậy decorator ghép chuỗi được, còn adapter thường là điểm cuối của chuỗi.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Adapter là chỗ duy nhất biết SDK thật, nên khi SDK đổi API chỉ sửa ở đây.
 * Nghiệp vụ và test không phải đổi theo.
 * Nó cũng là chỗ để dịch kiểu lỗi của SDK sang lỗi có nghĩa của application.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Test thay adapter bằng một implementation của ProviderGateway, không cần SDK.
 * Đường lỗi như bị từ chối dựng được bằng một adapter giả luôn ném.
 * Không cần mạng, không cần khoá API, và test không phụ thuộc nhà cung cấp.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Blast radius giới hạn ở StripeAdapter và test của nó.
 * Nếu SDK đổi tên method hoặc đổi kiểu tham số, chỉ thân adapter đổi.
 * Nghiệp vụ gọi ProviderGateway nên không biết gì về thay đổi đó.
 * SOLUTION-END
 */
