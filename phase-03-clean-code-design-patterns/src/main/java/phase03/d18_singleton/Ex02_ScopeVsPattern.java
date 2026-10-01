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
    static final Verdict Q5_SPRING_SINGLETON_SAME_AS_GOF = Verdict.SAME_SCOPE_DIFFERENT_OWNERSHIP; // SOLUTION-VALUE
    // Q6 — mutable field trong singleton bean có vấn đề.
    static final Boolean Q6_MUTABLE_FIELD_IN_SINGLETON_BEAN = true; // SOLUTION-VALUE

    /** Cho sẵn cấu trúc; field mutable là điểm sai cần thấy. */
    public static final class MutableCounterService {

        private int requests;

        public int increment() {
            // SOLUTION-BEGIN throw Q6
            return ++requests;
            // SOLUTION-END
        }

        public int count() {
            // SOLUTION-BEGIN throw Q6
            return requests;
            // SOLUTION-END
        }
    }
}

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Không hoàn toàn giống. Cả hai đều chỉ có một instance trong phạm vi của mình.
 * GoF Singleton tự quản việc tạo instance bằng static và thường truy cập qua static getter.
 * Spring bean do container tạo và giữ; object không biết mình là singleton, nên vẫn inject và test được.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Bean singleton dùng chung một instance cho mọi request.
 * Mutable field trên instance đó là trạng thái chia sẻ giữa các request không liên quan.
 * Hai request đồng thời có thể đọc và ghi cùng field, sinh kết quả sai và khó tái hiện.
 * Service singleton nên không trạng thái, hoặc giữ trạng thái trong biến cục bộ.
 * SOLUTION-END
 */
