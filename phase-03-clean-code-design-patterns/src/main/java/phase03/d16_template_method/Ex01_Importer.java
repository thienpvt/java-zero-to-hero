package phase03.d16_template_method;

import java.util.ArrayList;
import java.util.List;

/**
 * Template Method — Bài 1: Importer
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 16 (Template Method), câu 1, 2, 3.
 * Cần làm trước: d15_observer.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_ImporterTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Template Method giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q1_TEMPLATE_PROBLEM bằng một giá trị của {@code Problem}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu lịch chạy cố định, bước cho subclass.
 * <p>
 * Q2 [DỰ ĐOÁN] Template Method khác Strategy thế nào?
 *   Bắt đầu   : điền Q2_TEMPLATE_VS_STRATEGY bằng một giá trị của {@code Difference}.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu inheritance so với composition.
 * <p>
 * Q3 [DỰ ĐOÁN] Pattern này dựa trên inheritance hay composition?
 *   Bắt đầu   : điền Q3_USES_INHERITANCE.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh.
 * <p>
 * Q4 [CODE] Khi có quá nhiều override points thì vấn đề gì xảy ra?
 *   Bắt đầu   : cài {@code Importer.importData} gọi ba bước theo đúng thứ tự read, validate, persist,
 *               và cài ba bước của {@code CsvImporter} và {@code JsonImporter}.
 *   Kiểm chứng: chạy q04_importDataRunsSteps, q04_csvAndJsonShareSkeleton. Ctrl+F12 trên
 *               {@code CsvImporter} xem chỉ có ba override.
 *   Hoàn thành khi: hai test xanh; ANSWER Q4 nêu subclass phải hiểu thứ tự và trạng thái của các bước khác.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Khi nào Strategy linh hoạt hơn Template Method?
 *   Bắt đầu   : viết khối ANSWER Q5.
 *   Hoàn thành khi: ANSWER Q5 nêu đổi lịch chạy hoặc chọn bước lúc runtime.
 */
public class Ex01_Importer {

    /** Vấn đề Template Method giải quyết. */
    public enum Problem {
        FASTER_IO,
        FIXED_ALGORITHM_WITH_VARIABLE_STEPS,
        FEWER_CLASSES
    }

    /** Template Method khác Strategy ở đâu. */
    public enum Difference {
        SAME_THING,
        INHERITANCE_VS_COMPOSITION,
        TEMPLATE_IS_FASTER
    }

    // Q1 — Template Method giải quyết vấn đề gì.
    static final Problem Q1_TEMPLATE_PROBLEM = Problem.FIXED_ALGORITHM_WITH_VARIABLE_STEPS; // SOLUTION-VALUE

    // Q2 — Template Method khác Strategy ở đâu.
    static final Difference Q2_TEMPLATE_VS_STRATEGY = Difference.INHERITANCE_VS_COMPOSITION; // SOLUTION-VALUE

    // Q3 — Template Method dựa trên inheritance.
    static final Boolean Q3_USES_INHERITANCE = true; // SOLUTION-VALUE

    /** Class cha giữ lịch chạy; subclass chỉ cài ba bước. */
    public abstract static class Importer {

        /** Lịch cố định; subclass không override. */
        public final List<String> importData(String raw) {
            // SOLUTION-BEGIN throw Q4
            List<String> rows = read(raw);
            List<String> valid = new ArrayList<>();
            for (String row : rows) {
                valid.add(validate(row));
            }
            List<String> persisted = new ArrayList<>();
            for (String row : valid) {
                persisted.add(persist(row));
            }
            return persisted;
            // SOLUTION-END
        }

        protected abstract List<String> read(String raw);

        protected abstract String validate(String row);

        protected abstract String persist(String valid);
    }

    public static final class CsvImporter extends Importer {

        @Override
        protected List<String> read(String raw) {
            // SOLUTION-BEGIN throw Q4
            return List.of(raw.split(","));
            // SOLUTION-END
        }

        @Override
        protected String validate(String row) {
            // SOLUTION-BEGIN throw Q4
            return row.strip();
            // SOLUTION-END
        }

        @Override
        protected String persist(String valid) {
            // SOLUTION-BEGIN throw Q4
            return "csv:" + valid;
            // SOLUTION-END
        }
    }

    public static final class JsonImporter extends Importer {

        @Override
        protected List<String> read(String raw) {
            // SOLUTION-BEGIN throw Q4
            return List.of(raw.replace("[", "").replace("]", "").split(","));
            // SOLUTION-END
        }

        @Override
        protected String validate(String row) {
            // SOLUTION-BEGIN throw Q4
            return row.strip();
            // SOLUTION-END
        }

        @Override
        protected String persist(String valid) {
            // SOLUTION-BEGIN throw Q4
            return "json:" + valid;
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Template Method cố định lịch chạy trong class cha và cho subclass cài từng bước.
 * Mọi biến thể đi qua cùng một bộ khung, nên thứ tự không bị lệch giữa các subclass.
 * Phần chung nằm một chỗ, không lặp lại ở mỗi implementation.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Template Method dùng inheritance: subclass override các bước, class cha giữ lịch.
 * Strategy dùng composition: caller giữ một object bước và tự gọi theo lịch của mình.
 * Template Method cố định lịch, Strategy cho đổi lịch và đổi bộ bước lúc runtime.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Dựa trên inheritance. Class cha định nghĩa bộ khung, subclass thay từng bước.
 * Đó cũng là lý do pattern này khó đổi lịch chạy mà không sửa class cha.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Nhiều override point làm subclass phải hiểu thứ tự và trạng thái chung giữa các bước.
 * Class cha không còn giữ được bất biến vì bất kỳ bước nào cũng có thể thay đổi.
 * Đọc một subclass không đủ biết hành vi vì phần lớn nằm ở class cha.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Strategy linh hoạt hơn khi cần đổi lịch chạy hoặc chọn bộ bước lúc runtime.
 * Nó cũng cho phép một class dùng nhiều bộ bước khác nhau theo cấu hình.
 * Template Method phù hợp khi lịch chạy thật sự cố định cho mọi biến thể.
 * SOLUTION-END
 */
