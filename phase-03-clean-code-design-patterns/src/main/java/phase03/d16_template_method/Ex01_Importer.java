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
    static final Problem Q1_TEMPLATE_PROBLEM = null;

    // Q2 — Template Method khác Strategy ở đâu.
    static final Difference Q2_TEMPLATE_VS_STRATEGY = null;

    // Q3 — Template Method dựa trên inheritance.
    static final Boolean Q3_USES_INHERITANCE = null;

    /** Class cha giữ lịch chạy; subclass chỉ cài ba bước. */
    public abstract static class Importer {

        /** Lịch cố định; subclass không override. */
        public final List<String> importData(String raw) {
            throw new UnsupportedOperationException("TODO Q4");
        }

        protected abstract List<String> read(String raw);

        protected abstract String validate(String row);

        protected abstract String persist(String valid);
    }

    public static final class CsvImporter extends Importer {

        @Override
        protected List<String> read(String raw) {
            throw new UnsupportedOperationException("TODO Q4");
        }

        @Override
        protected String validate(String row) {
            throw new UnsupportedOperationException("TODO Q4");
        }

        @Override
        protected String persist(String valid) {
            throw new UnsupportedOperationException("TODO Q4");
        }
    }

    public static final class JsonImporter extends Importer {

        @Override
        protected List<String> read(String raw) {
            throw new UnsupportedOperationException("TODO Q4");
        }

        @Override
        protected String validate(String row) {
            throw new UnsupportedOperationException("TODO Q4");
        }

        @Override
        protected String persist(String valid) {
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
