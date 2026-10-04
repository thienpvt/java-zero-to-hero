package phase01.d17_capstone;

import java.io.IOException;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.List;

import phase01.d17_capstone.CsvOrderParser.LineError;
import phase01.d17_capstone.CsvOrderParser.ParseResult;
import phase01.d17_capstone.CustomerReportService.CustomerSummary;

/**
 * Bài tích hợp — ReportApp (điểm vào dòng lệnh, in báo cáo ra console)
 *
 * Nguồn: 01-java-core-advanced.md, mục "Bài thực hành tích hợp", bước 5 ("Đạt khi: chạy
 * được từ dòng lệnh").
 * Cần làm trước: CsvOrderParser (B2, B3), CustomerReportService (B3, B4).
 * Cách làm: cài {@code render(...)} rồi chạy ReportAppTest bằng nút ▶ (Ctrl+Shift+F10); sau
 * khi test xanh, chạy thử từ Terminal (Alt+F12) bằng lệnh dòng lệnh ở cuối Javadoc này.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * B5 [CODE] Cài {@code render} để các test (file rỗng, dòng sai, ID trùng, múi giờ khác và
 *     kết quả không cho sửa ngoài ý muốn) xanh; "Đạt khi: chạy được từ dòng lệnh, test xanh".
 *   Bắt đầu   : mở ReportAppTest, đọc kỳ vọng của {@code render(...)} với 1 dòng dữ liệu và
 *               1 lỗi trước khi cài code.
 *   Kiểm chứng: đặt breakpoint đầu {@code render(...)}, Debug test, dùng Evaluate Expression
 *               (Alt+F8) để xem chuỗi báo cáo bạn đang ghép tăng dần qua từng dòng.
 *   Code      : cài {@code render(List<CustomerSummary> rows, List<LineError> errors)}: dòng
 *               tiêu đề {@code "customerId | orders | totalPaid | firstOrderDate"}; mỗi dòng
 *               dữ liệu {@code customerId | orderCount | totalPaid.toPlainString() |
 *               firstOrderDate}; các dòng phân tách bằng {@code "\n"}; nếu có lỗi, thêm một
 *               dòng trống rồi mỗi lỗi một dòng {@code "Dòng <n>: <message>"}.
 *   Hoàn thành khi: mọi test trong ReportAppTest xanh và chạy được lệnh dòng lệnh dưới đây
 *               trên file mẫu, ra báo cáo hợp lý (đối chiếu với test đọc file mẫu).
 *
 * &lt;&lt;&lt;TAG0&gt;&gt;&gt;Chạy từ dòng lệnh (sau khi test xanh). Console Windows mặc định không phải UTF-8,
 * nên tiếng Việt bị lỗi font nếu thiếu {@code chcp 65001} và hai cờ encoding:
 * &lt;pre&gt;{@code
 * chcp 65001
 * .\mvnw.cmd -q -pl phase-01-core-advanced compile
 * java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp phase-01-core-advanced/target/classes phase01.d17_capstone.ReportApp phase-01-core-advanced/src/test/resources/phase01/d17_capstone/orders-sample.csv Asia/Ho_Chi_Minh
 * }&lt;/pre&gt;
 */
public final class ReportApp {

    private ReportApp() {
    }

    /**
     * Ghép {@code rows} (đã sắp theo customerId) và {@code errors} (theo số dòng gốc) thành
     * một báo cáo văn bản nhiều dòng, phân tách bằng {@code "\n"}.
     */
    static String render(List<CustomerSummary> rows, List<LineError> errors) {
        // SOLUTION-BEGIN throw B5
        StringBuilder sb = new StringBuilder();
        sb.append("customerId | orders | totalPaid | firstOrderDate");
        for (CustomerSummary row : rows) {
            sb.append('\n')
                    .append(row.customerId()).append(" | ")
                    .append(row.orderCount()).append(" | ")
                    .append(row.totalPaid().toPlainString()).append(" | ")
                    .append(row.firstOrderDate());
        }
        if (!errors.isEmpty()) {
            sb.append('\n');
            for (LineError error : errors) {
                sb.append('\n').append("Dòng ").append(error.lineNumber()).append(": ").append(error.message());
            }
        }
        return sb.toString();
        // SOLUTION-END
    }

    /**
     * Đọc file CSV ở {@code args[0]}, tổng hợp báo cáo theo múi giờ {@code args[1]}, in ra
     * {@code System.out}. Thiếu tham số, hoặc {@code args[1]} không phải một zone id hợp lệ
     * (ví dụ gõ sai như {@code "Asia/Ho-Chi-Minh"} thay vì {@code "Asia/Ho_Chi_Minh"}) →
     * hướng dẫn/lỗi ra {@code stderr}, exit code 2. Lỗi đọc file (I/O) → thông báo ra
     * {@code stderr}, exit code 1.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Cách dùng: java ... phase01.d17_capstone.ReportApp <file.csv> <zoneId>");
            System.exit(2);
            return;
        }
        Path file = Path.of(args[0]);
        ZoneId zone;
        try {
            zone = ZoneId.of(args[1]);
        } catch (DateTimeException e) {
            System.err.println("Múi giờ không hợp lệ: '" + args[1] + "'. Ví dụ hợp lệ: Asia/Ho_Chi_Minh, UTC.");
            System.exit(2);
            return;
        }
        try {
            CsvOrderParser parser = new CsvOrderParser();
            ParseResult parseResult = parser.parse(file);
            CustomerReportService service = new CustomerReportService(zone);
            List<CustomerSummary> summaries = service.summarizeWithStream(parseResult.orders());
            System.out.println(render(summaries, parseResult.errors()));
        } catch (IOException e) {
            System.err.println("Lỗi đọc file '" + file + "': " + e.getMessage());
            System.exit(1);
        }
    }
}
