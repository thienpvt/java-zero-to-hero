package phase03.d07_composition;

/**
 * Composition — Bài 2: Template Method và Strategy
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 7, câu 5.
 * Cần làm trước: Ex01_ReuseWithoutIsA.
 * Cách làm: chạy test trong Ex02_TemplateVsStrategyTradeoffTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Ví dụ Q5 [DỰ ĐOÁN + CODE] Trade-off giữa hai cách?
 *   Bắt đầu   : điền Q5_HYBRID_TRADEOFF, rồi cài ba subclass bước và {@code StrategyImporter.importData}
 *               gọi ba bước của strategy theo cùng thứ tự.
 *   Kiểm chứng: chạy q05_sameResultBothWays và q05_strategySwappableAtRuntime.
 *   Hoàn thành khi: hai test xanh và cả hai đường cho cùng chuỗi kết quả.
 */
public class Ex02_TemplateVsStrategyTradeoff {

    /** Trade-off chính giữa hai cách. */
    public enum Tradeoff {
        NONE,
        FIXED_SKELETON_VS_SWAPPABLE_STEPS,
        TEMPLATE_IS_ALWAYS_FASTER
    }

    // Q5 — trade-off giữa Template Method và Strategy.
    static final Tradeoff Q5_HYBRID_TRADEOFF = Tradeoff.FIXED_SKELETON_VS_SWAPPABLE_STEPS; // SOLUTION-VALUE

    /** Ba bước, tách khỏi lịch chạy. */
    public interface ImportSteps {

        String read();

        String validate(String raw);

        String persist(String valid);
    }

    /** Template Method: lịch cố định trong class cha. */
    public static final class CsvImporter extends Ex01_ReuseWithoutIsA.AbstractImporter {

        @Override
        protected String read() {
            // SOLUTION-BEGIN throw Q5
            return "csv-row";
            // SOLUTION-END
        }

        @Override
        protected String validate(String raw) {
            // SOLUTION-BEGIN throw Q5
            return "valid:" + raw;
            // SOLUTION-END
        }

        @Override
        protected String persist(String valid) {
            // SOLUTION-BEGIN throw Q5
            return "saved:" + valid;
            // SOLUTION-END
        }
    }

    /** Strategy: cùng lịch, nhưng các bước là field đổi được. */
    public static final class StrategyImporter {

        private final ImportSteps steps;

        public StrategyImporter(ImportSteps steps) {
            this.steps = steps;
        }

        public String importData() {
            // SOLUTION-BEGIN throw Q5
            String read = steps.read();
            String validated = steps.validate(read);
            return steps.persist(validated);
            // SOLUTION-END
        }
    }
}

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Template Method cố định lịch chạy ở class cha và chỉ cho override từng bước.
 * Strategy để lịch chạy ở caller, đổi được toàn bộ bộ bước lúc runtime.
 * Cả hai cho cùng kết quả; khác nhau ở chỗ đổi lịch có cần sửa class cha hay không.
 * SOLUTION-END
 */
