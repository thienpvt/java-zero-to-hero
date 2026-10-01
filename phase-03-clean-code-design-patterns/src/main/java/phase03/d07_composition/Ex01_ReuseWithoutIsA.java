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
    static final Fit Q1_INHERITANCE_FIT = Fit.TRUELY_IS_A; // SOLUTION-VALUE

    // Q2 — composition tốt hơn khi nào.
    static final Fit Q2_COMPOSITION_FIT = Fit.REUSE_CODE; // SOLUTION-VALUE

    // Q4 — hierarchy sâu khó bảo trì vì sao.
    static final Pain Q4_DEEP_HIERARCHY_PAIN = Pain.BEHAVIOR_SPREAD_ACROSS_LEVELS; // SOLUTION-VALUE

    /** Dùng composition: giữ một {@code Logger}, không extends. */
    public static final class OrderService {

        private final Logger logger;

        public OrderService(Logger logger) {
            this.logger = logger;
        }

        public String place(String order) {
            // SOLUTION-BEGIN throw Q5
            return logger.log("đặt " + order);
            // SOLUTION-END
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
 * SOLUTION-BEGIN
 * Inheritance phù hợp khi quan hệ is-a đúng và subtype thật sự thay thế được base type.
 * Cần cùng ngữ nghĩa, ví dụ mọi loại importer đều là importer.
 * Nếu chỉ muốn dùng lại vài dòng code, inheritance kéo theo cả hợp đồng không cần.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Composition tốt hơn khi quan hệ là has-a: OrderService có một Logger.
 * Đổi hành vi là đổi field, không sửa chuỗi thừa kế, và test thay bằng fake dễ.
 * Không lộ ra ngoài những method của lớp được tái sử dụng.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * is-a nói subtype được dùng ở mọi chỗ cần base type và giữ nguyên kỳ vọng của caller.
 * has-a nói object giữ một thứ khác bên trong và chuyển tiếp lời gọi.
 * Chỉ has-a mới cho phép thay phần bên trong lúc runtime.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Hành vi của một method có thể nằm rải ở nhiều tầng, nên đọc một tầng không đủ hiểu.
 * Sửa một method ở tầng giữa vô tình phá subclass ở tầng dưới.
 * gọi super lẫn nhau còn làm luồng chạy phụ thuộc trạng thái runtime.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Template Method gọi ngược lên subclass: base class biết lịch, subclass biết bước.
 * Strategy gọi xuống qua field: policy chỉ biết bước, caller chọn lúc runtime.
 * Inheritance cố định lịch và khó đổi nửa đường; composition đổi được nhưng thêm một lớp nối.
 * SOLUTION-END
 */
