package phase00.d03_flow;

/**
 * Luồng và phương thức — Bài 2: Phân loại điểm, overload và override
 *
 * Nguồn: 00-java-basics-review.md, mục 3 (Điều khiển luồng và phương thức), câu 1, 4.
 * Cần làm trước: Ex01_BreakAndArguments.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_GradeAndOverloadTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q1 [TỰ TRẢ LỜI + CODE] Khi nào dùng {@code switch} thay cho chuỗi {@code if/else}?
 *   Bắt đầu   : cài {@code gradeIf} và {@code gradeSwitch} cùng một thang điểm, rồi viết ANSWER Q1.
 *   Kiểm chứng: chạy q01_grades. Đặt breakpoint ở nhánh đầu của {@code gradeIf}, Debug với điểm 49 rồi 50.
 *   Code      : cả hai hàm nhận điểm {@code int}. Ngoài khoảng 0..100 thì
 *               {@code IllegalArgumentException}. 0..49 → {@code "Không đạt"}; 50..79 → {@code "Đạt"};
 *               80..100 → {@code "Giỏi"}. Bản switch kiểm tra khoảng trước, rồi {@code switch} trên band.
 *   Hoàn thành khi: q01_grades xanh; ANSWER Q1 nói bản nào dễ đọc hơn với khoảng điểm và khi nào switch thắng.
 *
 * Q4 [DỰ ĐOÁN] Overload khác override ở điểm nào?
 *   Bắt đầu   : đọc Printer và LoudPrinter. Điền hai hằng theo đúng kịch bản ghi trên hằng, chưa chạy test.
 *   Kiểm chứng: Debug q04_prediction. F7 Step Into từ lời gọi {@code format} để thấy method nào được chọn.
 *               Ctrl+Q trên {@code @Override}.
 *   Hoàn thành khi: q04_prediction xanh; nói được overload chọn lúc biên dịch theo kiểu tham chiếu,
 *               override chọn lúc chạy theo object thật.
 */
public class Ex02_GradeAndOverload {

    static String gradeIf(int score) {
        throw new UnsupportedOperationException("TODO Q1");
    }

    static String gradeSwitch(int score) {
        throw new UnsupportedOperationException("TODO Q1");
    }

    static class Printer {
        String format(Object value) {
            return "object";
        }

        String format(String value) {
            return "string";
        }
    }

    static class LoudPrinter extends Printer {
        @Override
        String format(Object value) {
            return "loud-object";
        }
    }

    // Q4 — kịch bản: new Printer().format("hi")
    static final String Q4_FORMAT_STRING_LITERAL = null;

    // Q4 — kịch bản: Printer p = new LoudPrinter(); p.format((Object) "hi")
    static final String Q4_FORMAT_OBJECT_ON_CHILD = null;

    /* ANSWER Q1:
     *
     */
}
