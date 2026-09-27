package phase00.d11_io_time;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * File và thời gian — Bài 2: Đọc tồn kho và ghi báo cáo
 *
 * Nguồn: 00-java-basics-review.md, mục 11 (File I/O và thời gian cơ bản), câu 1, 2.
 * Cần làm trước: Ex01_LocalDateAndInstant.
 * Cách làm: cài {@code summarize}, chạy Ex02_StockReportTest.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q1 [CODE + TỰ TRẢ LỜI] Vì sao nên chỉ rõ charset khi đọc/ghi file văn bản?
 *   Bắt đầu   : cài {@code summarize} với {@code StandardCharsets.UTF_8}, rồi viết ANSWER Q1.
 *   Kiểm chứng: chạy q01_sumsVietnameseCodes. Mở file báo cáo trong test bằng
 *               {@code Files.readString(report, StandardCharsets.UTF_8)}. Ctrl+Q trên
 *               {@code Files.newBufferedReader}.
 *   Code      : đọc {@code productCode,quantity}, bỏ dòng trống, cộng số lượng (mã trùng thì cộng dồn).
 *               File không tồn tại → {@code NoSuchFileException}. Dòng không đúng hai field, mã trống,
 *               số lượng không phải int hoặc âm → {@code IllegalArgumentException} có chữ {@code dòng &lt;n&gt;}
 *               (số dòng vật lý, kể cả dòng trống đã bỏ qua). Ghi báo cáo chỉ khi đọc hết:
 *               mỗi mã một dòng {@code code=qty} theo lần gặp, rồi {@code total=&lt;n&gt;}, ngăn bởi {@code \n}.
 *   Hoàn thành khi: q01_* xanh; ANSWER Q1 nói charset mặc định của máy có thể khác UTF-8.
 * <p>
 * Q2 [TỰ TRẢ LỜI] File rất lớn có nên luôn dùng {@code Files.readAllLines()} không?
 *   Bắt đầu   : viết ANSWER Q2. Phần code của Q1 đọc từng dòng, không gọi {@code readAllLines}.
 *   Tra cứu   : Ctrl+Q trên {@code Files.readAllLines} và trên {@code Files.newBufferedReader}.
 *   Hoàn thành khi: ANSWER Q2 nói bộ nhớ và cách đọc thay thế.
 */
public class Ex02_StockReport {

    /**
     * @return tổng số lượng
     * @throws IOException              khi không đọc hoặc không ghi được
     * @throws NoSuchFileException      khi {@code input} không tồn tại
     * @throws IllegalArgumentException khi một dòng dữ liệu sai
     */
    static int summarize(Path input, Path report) throws IOException {
        // SOLUTION-BEGIN throw Q1
        Objects.requireNonNull(input, "input");
        Objects.requireNonNull(report, "report");
        if (!Files.exists(input)) {
            throw new NoSuchFileException(input.toString());
        }
        Map<String, Integer> totals = new LinkedHashMap<>();
        int lineNo = 0;
        try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length != 2 || parts[0].trim().isEmpty()) {
                    throw new IllegalArgumentException("dòng " + lineNo + ": cần productCode,quantity");
                }
                int quantity;
                try {
                    quantity = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("dòng " + lineNo + ": số lượng không hợp lệ", ex);
                }
                if (quantity < 0) {
                    throw new IllegalArgumentException("dòng " + lineNo + ": số lượng âm");
                }
                totals.merge(parts[0].trim(), quantity, Integer::sum);
            }
        }
        int total = 0;
        StringBuilder text = new StringBuilder();
        for (Map.Entry<String, Integer> entry : totals.entrySet()) {
            text.append(entry.getKey()).append('=').append(entry.getValue()).append('\n');
            total += entry.getValue();
        }
        text.append("total=").append(total).append('\n');
        Files.writeString(report, text.toString(), StandardCharsets.UTF_8);
        return total;
        // SOLUTION-END
    }

    /* ANSWER Q1:
     * SOLUTION-BEGIN
     * Không chỉ charset thì JDK dùng charset mặc định của máy. Windows và Linux có thể khác nhau,
     * nên chữ "Đồng-hồ" đọc ra thành ký tự sai dù file đã lưu UTF-8. Ghi và đọc cùng StandardCharsets.UTF_8
     * thì nội dung không phụ thuộc máy đang chạy.
     * SOLUTION-END
     */

    /* ANSWER Q2:
     * SOLUTION-BEGIN
     * Files.readAllLines kéo mọi dòng vào một List trong bộ nhớ. File rất lớn có thể làm đầy heap
     * trước khi xử lý xong dòng đầu. File tồn kho lớn nên đọc từng dòng bằng BufferedReader
     * (hoặc Files.lines) và đóng reader bằng try-with-resources. File nhỏ thì readAllLines vẫn chấp nhận được.
     * SOLUTION-END
     */
}
