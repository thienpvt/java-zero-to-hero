package phase03.d18_singleton;

/**
 * Singleton — Bài 2: scope của container và mutable field
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 18, câu 5, 6.
 * Cần làm trước: Ex01_EnumSingletonAndState.
 * Cách làm: chạy test trong Ex02_ScopeVsPatternTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q5 [DỰ ĐOÁN] Spring singleton bean có giống GoF Singleton hoàn toàn không?
 *   Bắt đầu   : điền Q5_SPRING_SINGLETON_SAME_AS_GOF bằng một giá trị của {@code Verdict}.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 nêu scope quản lý bởi container, không phải static toàn cục.
 * <p>
 * Q6 [DỰ ĐOÁN + CODE] Một Spring singleton service chứa mutable field có vấn đề gì?
 *   Bắt đầu   : điền Q6_MUTABLE_FIELD_IN_SINGLETON_BEAN, rồi cài {@code MutableCounterService.increment}
 *               và {@code count} để lộ trạng thái dùng chung giữa các lời gọi.
 *   Kiểm chứng: chạy q06_prediction và q06_sharedFieldAcrossCallers.
 *   Hoàn thành khi: hai test xanh và ANSWER Q6 nêu nhiều request cùng đụng một field.
 */
public class Ex02_ScopeVsPattern {

    /** Spring singleton bean có giống GoF Singleton. */
    public enum Verdict {
        YES_IDENTICAL,
        SAME_SCOPE_DIFFERENT_OWNERSHIP,
        NO_BEAN_IS_ALWAYS_NEW
    }

    // Q5 — Spring singleton bean có giống GoF Singleton hoàn toàn.
    static final Verdict Q5_SPRING_SINGLETON_SAME_AS_GOF = null;
    // Q6 — mutable field trong singleton bean có vấn đề.
    static final Boolean Q6_MUTABLE_FIELD_IN_SINGLETON_BEAN = null;

    /** Cho sẵn cấu trúc; field mutable là điểm sai cần thấy. */
    public static final class MutableCounterService {

        private int requests;

        public int increment() {
            throw new UnsupportedOperationException("TODO Q6");
        }

        public int count() {
            throw new UnsupportedOperationException("TODO Q6");
        }
    }
}

/* ANSWER Q5:
 *
 */

/* ANSWER Q6:
 *
 */
