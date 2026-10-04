package phase09.d12_dynamic_programming;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Dynamic Programming.
 * <p>
 * Nguồn: 09-algorithms-system-design.md, mục 12 (Dynamic Programming), câu 1–5.
 * Cần làm trước: mục 1 (Độ phức tạp và cách kiểm chứng), mục 5 (Binary Search).
 * Cách làm: trả lời từng câu, chạy test trong Ex01_DynamicProgrammingTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] State biểu diễn điều gì và làm sao biết state đủ thông tin?
 *   Bắt đầu   : viết ANSWER Q1; đề xuất ý nghĩa của mỗi state.
 *   Tra cứu   : recurrence và điều kiện chuyển tiếp.
 *   Hoàn thành khi: ANSWER Q1 nêu state phải đủ xác định transition và kết quả tối ưu.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Base case sai thường tạo lỗi kiểu nào?
 *   Bắt đầu   : viết ANSWER Q2; lần theo input nhỏ nhất.
 *   Tra cứu   : khởi tạo recurrence và sentinel.
 *   Hoàn thành khi: ANSWER Q2 liên hệ base case với sai lệch hoặc truy cập ngoài biên.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Khi memoization tốt hơn tabulation, hoặc ngược lại?
 *   Bắt đầu   : viết ANSWER Q3; xét số state thực sự được yêu cầu.
 *   Tra cứu   : call stack và thứ tự tính state.
 *   Hoàn thành khi: ANSWER Q3 nêu trade-off về state thưa, overhead và stack.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Điều kiện nào cho phép giảm bộ nhớ từ O(n²) xuống O(n)?
 *   Bắt đầu   : viết ANSWER Q4; xác định state của các bước trước còn được dùng không.
 *   Tra cứu   : dependency của recurrence và iteration order.
 *   Hoàn thành khi: ANSWER Q4 nêu điều kiện lưu đủ rolling state mà không ghi đè cần thiết.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Làm sao tránh overflow khi cộng chi phí hoặc điểm số?
 *   Bắt đầu   : viết ANSWER Q5; xét kiểu số, sentinel và giới hạn input.
 *   Tra cứu   : checked arithmetic và unreachable state.
 *   Hoàn thành khi: ANSWER Q5 nêu kiểm tra sentinel/range trước khi cộng.
 * <p>
 * B1: Tìm số coin ít nhất bằng tabulation; khi hòa, chọn danh sách coin tăng dần nhỏ nhất theo từ điển.
 */
class Ex01_DynamicProgramming {
    static Optional<List<Integer>> minCoins(int[] denominations, int amount) {
        throw new UnsupportedOperationException("TODO B1");
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
