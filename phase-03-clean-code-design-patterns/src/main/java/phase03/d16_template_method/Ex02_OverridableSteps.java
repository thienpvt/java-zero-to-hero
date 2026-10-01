package phase03.d16_template_method;

import java.util.ArrayList;
import java.util.List;

/**
 * Template Method — Bài 2: hook và số override point
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 16, câu 4, 5.
 * Cần làm trước: Ex01_Importer.
 * Cách làm: chạy test trong Ex02_OverridableStepsTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Ví dụ Q4 [DỰ ĐOÁN + CODE] Quá nhiều override point gây vấn đề gì?
 *   Bắt đầu   : điền Q4_TOO_MANY_HOOKS, rồi cài {@code HookImporter.importData} gọi read, validate,
 *               persist, rồi {@code afterPersist} — hook mặc định không làm gì.
 *   Kiểm chứng: chạy q04_prediction và q04_hookRunsAfterPersist.
 *   Hoàn thành khi: hai test xanh và ANSWER Q4 nêu mỗi hook thêm một trạng thái subclass phải hiểu.
 * <p>
 * Ví dụ Q5 [DỰ ĐOÁN] Khi nào Strategy linh hoạt hơn Template Method?
 *   Bắt đầu   : điền Q5_STRATEGY_MORE_FLEXIBLE bằng một giá trị của {@code When}.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 nêu đổi lịch chạy lúc runtime.
 */
public class Ex02_OverridableSteps {

    /** Khi Strategy linh hoạt hơn. */
    public enum When {
        NEVER,
        WHEN_ORDER_OR_STEP_SET_CHANGES_AT_RUNTIME,
        WHEN_THERE_ARE_MANY_SUBCLASSES
    }

    // Q4 — quá nhiều hook gây vấn đề gì.
    static final Boolean Q4_TOO_MANY_HOOKS = null;

    // Q5 — Strategy linh hoạt hơn khi nào.
    static final When Q5_STRATEGY_MORE_FLEXIBLE = null;
    /** Có thêm một hook tuỳ chọn sau bước persist. */
    public abstract static class HookImporter {

        public final List<String> importData(String raw) {
            throw new UnsupportedOperationException("TODO Q4");
        }

        /** Hook mặc định không làm gì; subclass có thể override. */
        protected void afterPersist(List<String> persisted) {
        }

        protected abstract List<String> read(String raw);

        protected abstract String validate(String row);

        protected abstract String persist(String valid);
    }
}

/* ANSWER Q4:
 *
 */

/* ANSWER Q5:
 *
 */
