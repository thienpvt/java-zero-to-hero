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
class Ex01_Complexity {
    static long countPairsQuadratic(int[] values, int target) {
        throw new UnsupportedOperationException("TODO B1");
    }

    static long countPairsLinear(int[] values, int target) {
        throw new UnsupportedOperationException("TODO B1");
    }

    static String experimentReport(int n) {
        throw new UnsupportedOperationException("TODO B1");
    }
}
