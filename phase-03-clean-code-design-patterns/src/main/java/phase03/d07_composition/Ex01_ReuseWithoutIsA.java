package phase03.d07_composition;

/**
 * Composition — Bài 1: dùng lại hành vi không cần is-a
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 7 (Composition vs Inheritance), câu 1, 2, 3, 4.
 * Cần làm trước: d06_di.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_ReuseWithoutIsATest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Khi nào inheritance phù hợp?
 *   Bắt đầu   : điền Q1_INHERITANCE_FIT bằng một giá trị của {@code Fit}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu is-a thật với cùng kiểu.
 * <p>
 * Q2 [DỰ ĐOÁN] Khi nào composition tốt hơn?
 *   Bắt đầu   : điền Q2_COMPOSITION_FIT bằng một giá trị của {@code Fit}.
 *   Kiểm chứng: chạy q02_prediction và q02_orderServiceDelegates.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu has-a.
 * <p>
 * Q3 [TỰ TRẢ LỜI] "is-a" và "has-a" khác nhau thế nào?
 *   Bắt đầu   : viết khối ANSWER Q3.
 *   Hoàn thành khi: ANSWER Q3 nêu được ví dụ thay thế được của subtype cho mỗi quan hệ.
 * <p>
 * Q4 [DỰ ĐOÁN] Vì sao deep inheritance hierarchy khó bảo trì?
 *   Bắt đầu   : điền Q4_DEEP_HIERARCHY_PAIN bằng một giá trị của {@code Pain}.
 *   Kiểm chứng: chạy q04_prediction. Ctrl+B trên {@code log} xem định nghĩa ở đâu.
 *   Hoàn thành khi: q04_prediction xanh và ANSWER Q4 nêu phải đọc cả chuỗi để biết một method chạy gì.
 * <p>
 * Q5 [CODE] Template Method dùng inheritance, Strategy dùng composition. Trade-off?
 *   Bắt đầu   : cài {@code AbstractImporter.run} gọi ba bước theo thứ tự read, validate, persist.
 *               Không override {@code run} ở subclass.
 *   Kiểm chứng: chạy q05_runCallsStepsInOrder.
 *   Hoàn thành khi: q05_runCallsStepsInOrder xanh; ANSWER Q5 nêu inheritance gọi ngược lên subclass
 *               còn strategy gọi xuống qua field.
 */
public class Ex01_ReuseWithoutIsA {

    /** Khi inheritance phù hợp. */
    public enum Fit {
        TRUELY_IS_A,
        REUSE_CODE,
        SHORTER_CLASS
    }

    /** Vì sao hierarchy sâu khó bảo trì. */
    public enum Pain {
        SLOWER_RUNTIME,
        BEHAVIOR_SPREAD_ACROSS_LEVELS,
        MORE_FILES
    }

    public interface Logger {

        String log(String message);
    }

    public static final class ConsoleLogger implements Logger {

        @Override
        public String log(String message) {
            return "log:" + message;
        }
    }

    // Q1 — inheritance phù hợp khi nào.
    static final Fit Q1_INHERITANCE_FIT = null;

    // Q2 — composition tốt hơn khi nào.
    static final Fit Q2_COMPOSITION_FIT = null;

    // Q4 — hierarchy sâu khó bảo trì vì sao.
    static final Pain Q4_DEEP_HIERARCHY_PAIN = null;

    /** Dùng composition: giữ một {@code Logger}, không extends. */
    public static final class OrderService {

        private final Logger logger;

        public OrderService(Logger logger) {
            this.logger = logger;
        }

        public String place(String order) {
            throw new UnsupportedOperationException("TODO Q5");
        }
    }

    /** Cho sẵn: Template Method cho ba bước của một lần nhập dữ liệu. */
    public abstract static class AbstractImporter {

        public final String run() {
            String read = read();
            String validated = validate(read);
            return persist(validated);
        }

        protected abstract String read();

        protected abstract String validate(String raw);

        protected abstract String persist(String valid);
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
