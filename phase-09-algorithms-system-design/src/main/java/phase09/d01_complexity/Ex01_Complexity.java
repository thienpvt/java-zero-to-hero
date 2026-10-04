package phase09.d01_complexity;

/**
 * Độ phức tạp và cách kiểm chứng.
 * Nguồn: 09-algorithms-system-design.md, mục 1, câu 1–5.
 * Cần làm trước: ôn vòng lặp, mảng và phép cộng số nguyên trong Java.
 * Cách làm: đọc từng câu, ghi câu trả lời ngắn vào ANSWER tương ứng; chạy test B1.
 *
 * Q1 [TỰ TRẢ LỜI] Vì sao hai vòng lặp lồng nhau không phải lúc nào cũng O(n²)?
 *   Bắt đầu: phân tích số lần chạy vòng trong theo input.
 *   Tra cứu: xem lại mục 1 của tài liệu nguồn và đếm thao tác trên vài kích thước.
 *   Hoàn thành khi: phân biệt được trường hợp vòng trong chạy cố định, tuyến tính hoặc phụ thuộc dữ liệu.
 * Q2 [TỰ TRẢ LỜI] Amortized complexity khác expected complexity thế nào?
 *   Bắt đầu: viết điều kiện lấy trung bình cho mỗi khái niệm.
 *   Tra cứu: mục 1 tài liệu nguồn; đối chiếu chuỗi thao tác với phân phối xác suất.
 *   Hoàn thành khi: nêu được amortized không cần giả định xác suất còn expected cần mô hình xác suất.
 * Q3 [TỰ TRẢ LỜI] HashMap có bảo đảm lookup worst-case O(1) không?
 *   Bắt đầu: phân biệt expected với worst-case và xét collision.
 *   Tra cứu: Java 21 HashMap API cùng mục 1 tài liệu nguồn.
 *   Hoàn thành khi: giải thích được lookup trung bình/expected không phải cam kết worst-case O(1).
 * Q4 [TỰ TRẢ LỜI] O(n log n) tăng thế nào so với O(n²) khi n lớn?
 *   Bắt đầu: lập bảng giá trị n, n log₂ n và n².
 *   Tra cứu: tính một vài giá trị lớn bằng máy tính, không dùng kết luận từ thời gian chạy.
 *   Hoàn thành khi: chỉ ra tỷ lệ tăng và giải thích n² cuối cùng vượt n log n.
 * Q5 [TỰ TRẢ LỜI] Khi nào benchmark không đủ để chứng minh thuật toán phù hợp?
 *   Bắt đầu: liệt kê giới hạn của input, môi trường và phép đo.
 *   Tra cứu: mục 1 tài liệu nguồn; so sánh bằng chứng benchmark với phân tích độ phức tạp.
 *   Hoàn thành khi: nêu được benchmark không chứng minh tính đúng hay worst-case cho mọi input.
 * B1 [CODE]: so sánh countPairsQuadratic và countPairsLinear qua test; experimentReport chỉ đếm thao tác, không kết luận thời gian.
 */

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Hai vòng lặp lồng nhau không tự động tạo n lần lặp ở mỗi tầng.
 * Nếu vòng trong chạy số lần cố định, tổng vẫn O(n); nếu độ dài phụ thuộc n, có thể là O(n²).
 * Phải đếm tổng số lần thực thi theo kích thước input.
 * SOLUTION-END
 */
/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Amortized phân bố tổng chi phí chuỗi thao tác lên mỗi thao tác trong chuỗi.
 * Expected complexity là kỳ vọng theo phân phối xác suất đầu vào hoặc ngẫu nhiên hóa.
 * Amortized không đòi giả định xác suất; expected thì cần mô hình xác suất rõ.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Không. HashMap thường có lookup expected O(1), nhưng collision có thể làm worst-case lớn hơn.
 * Không xem O(1) trung bình là bảo đảm cho từng input.
 * SOLUTION-END
 */
/* ANSWER Q4:
 * SOLUTION-BEGIN
 * n log n tăng chậm hơn n² vì tỷ lệ n²/(n log n) tăng theo n/log n.
 * Với n đủ lớn, thuật toán O(n log n) xử lý input lớn hiệu quả hơn O(n²), dù hằng số thực tế vẫn quan trọng.
 * SOLUTION-END
 */
/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Benchmark đo một tập input trên một môi trường, có thể bị ảnh hưởng bởi JIT, warmup và tải hệ thống.
 * Nó không chứng minh tính đúng, worst-case hay giới hạn tài nguyên cho mọi input.
 * Dùng phân tích và test bổ sung; benchmark là bằng chứng thực nghiệm có phạm vi.
 * SOLUTION-END
 */
class Ex01_Complexity {
    static long countPairsQuadratic(int[] values, int target) {
        // SOLUTION-BEGIN throw B1
        java.util.Objects.requireNonNull(values, "values");
        long count = 0;
        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                if ((long) values[i] + values[j] == target) count++;
            }
        }
        return count;
        // SOLUTION-END
    }

    static long countPairsLinear(int[] values, int target) {
        // SOLUTION-BEGIN throw B1
        java.util.Objects.requireNonNull(values, "values");
        java.util.Map<Integer, Long> seen = new java.util.HashMap<>();
        long pairs = 0;
        for (int value : values) {
            long complement = (long) target - value;
            if (complement >= Integer.MIN_VALUE && complement <= Integer.MAX_VALUE) {
                pairs += seen.getOrDefault((int) complement, 0L);
            }
            seen.merge(value, 1L, Long::sum);
        }
        return pairs;
        // SOLUTION-END
    }

    static String experimentReport(int n) {
        // SOLUTION-BEGIN throw B1
        if (n < 0) throw new IllegalArgumentException("n must be nonnegative");
        long quadratic = (long) n * (n - 1) / 2;
        return "n=" + n + "; quadratic=" + quadratic + "; linear=" + n;
        // SOLUTION-END
    }
}
