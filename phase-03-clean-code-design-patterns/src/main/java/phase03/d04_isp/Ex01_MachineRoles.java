package phase03.d04_isp;

/**
 * ISP — Bài 1: vai trò của máy
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 4 (Interface Segregation Principle), câu 1, 2, 3, 4, 5.
 * Cần làm trước: d03_lsp.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_MachineRolesTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] ISP giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q1_ISP_PURPOSE bằng một giá trị của {@code Purpose}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nói client không phụ thuộc method nó không dùng.
 * <p>
 * Q2 [DỰ ĐOÁN] Interface có càng nhỏ càng tốt không?
 *   Bắt đầu   : điền Q2_SMALLER_ALWAYS_BETTER.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu fragmentation khi tách quá mức.
 * <p>
 * Q3 [DỰ ĐOÁN] Một implementation có nhiều method throw UnsupportedOperationException nói lên gì?
 *   Bắt đầu   : điền Q3_UNSUPPORTED_IS_SMELL.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 trỏ về interface đã gộp nhiều vai trò.
 * <p>
 * Q4 [DỰ ĐOÁN] ISP liên quan đến cohesion như thế nào?
 *   Bắt đầu   : điền Q4_ISP_IS_COHESION.
 *   Kiểm chứng: chạy q04_prediction.
 *   Hoàn thành khi: q04_prediction xanh.
 * <p>
 * Q5 [CODE] Khi nào tách interface gây fragmentation quá mức?
 *   Bắt đầu   : cài {@code SimplePrinter.print}. Chỉ cài {@code Printer}; {@code SimplePrinter}
 *               không được khai báo {@code Scanner} hay {@code Fax}.
 *   Kiểm chứng: chạy q05_simplePrinterOnlyPrints và q05_allInOneDoesAll.
 *               Ctrl+F12 trên {@code SimplePrinter} xem danh sách method.
 *   Hoàn thành khi: hai test xanh; ANSWER Q5 nêu chi phí khi mỗi method một interface.
 */
public class Ex01_MachineRoles {

    /** Vấn đề ISP giải quyết. */
    public enum Purpose {
        FASTER_DISPATCH,
        CLIENT_NOT_DEPEND_ON_UNUSED_METHODS,
        FEWER_CLASSES
    }

    public interface Printer {

        String print(String document);
    }

    public interface Scanner {

        String scan();
    }

    public interface Fax {

        String fax(String number);
    }

    // Q1 — ISP nhắm vấn đề nào.
    static final Purpose Q1_ISP_PURPOSE = null;

    // Q2 — interface càng nhỏ càng tốt.
    static final Boolean Q2_SMALLER_ALWAYS_BETTER = null;

    // Q3 — nhiều UnsupportedOperationException là dấu hiệu xấu.
    static final Boolean Q3_UNSUPPORTED_IS_SMELL = null;

    // Q4 — ISP là một cách nhìn cohesion.
    static final Boolean Q4_ISP_IS_COHESION = null;

    /** Cho sẵn: máy chỉ in. Không khai báo Scanner hay Fax. */
    public static final class SimplePrinter implements Printer {

        @Override
        public String print(String document) {
            throw new UnsupportedOperationException("TODO Q5");
        }
    }

    /** Máy đa năng giữ cả ba vai trò. */
    public static final class AllInOne implements Printer, Scanner, Fax {

        @Override
        public String print(String document) {
            return "print:" + document;
        }

        @Override
        public String scan() {
            return "scan:page-1";
        }

        @Override
        public String fax(String number) {
            return "fax:" + number;
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
