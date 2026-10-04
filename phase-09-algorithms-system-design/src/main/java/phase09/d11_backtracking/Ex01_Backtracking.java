package phase09.d11_backtracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Backtracking.
 * <p>
 * Nguồn: 09-algorithms-system-design.md, mục 11 (Backtracking), câu 1–5.
 * Cần làm trước: mục 4 (Two Pointers và Sliding Window), mục 10 (Graph: BFS, DFS và đường đi).
 * Cách làm: trả lời từng câu, chạy test trong Ex01_BacktrackingTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] State của backtracking cần chứa thông tin gì?
 *   Bắt đầu   : viết ANSWER Q1; liệt kê quyết định đã chọn và tiến độ.
 *   Tra cứu   : invariant và điều kiện dừng.
 *   Hoàn thành khi: ANSWER Q1 nêu thông tin cần để tiếp tục và kiểm tra constraint.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Pruning bảo toàn tính đúng ra sao?
 *   Bắt đầu   : viết ANSWER Q2; xét điều kiện loại nhánh.
 *   Tra cứu   : tính đầy đủ của search.
 *   Hoàn thành khi: ANSWER Q2 giải thích nhánh bị loại không thể tạo nghiệm hợp lệ.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Vì sao phải hoàn tác state sau mỗi nhánh?
 *   Bắt đầu   : viết ANSWER Q3; theo dõi ảnh hưởng của lựa chọn trước.
 *   Tra cứu   : mutable path và sibling branch.
 *   Hoàn thành khi: ANSWER Q3 giải thích việc cô lập các nhánh.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Duplicate đầu vào có thể tạo output trùng thế nào?
 *   Bắt đầu   : viết ANSWER Q4; thử chọn các occurrence giống nhau ở vị trí khác.
 *   Tra cứu   : sort và bỏ qua giá trị trùng ở cùng độ sâu.
 *   Hoàn thành khi: ANSWER Q4 nêu cách tránh output trùng mà vẫn giữ tổ hợp hợp lệ.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Liệt kê mọi nghiệm có thể tránh chi phí phụ thuộc output không?
 *   Bắt đầu   : viết ANSWER Q5; xét số lượng nghiệm được trả về.
 *   Tra cứu   : output-sensitive complexity.
 *   Hoàn thành khi: ANSWER Q5 giải thích cận dưới theo kích thước output.
 * <p>
 * B1: Sinh tổ hợp theo thứ tự từ điển, bỏ tổ hợp trùng khi đầu vào có duplicate.
 */
class Ex01_Backtracking {
    static List<List<Integer>> combinations(int n, int k) {
        throw new UnsupportedOperationException("TODO B1");
    }

    static List<List<Integer>> combinations(int[] values, int k) {
        throw new UnsupportedOperationException("TODO B1");
    }

    private static void collectRange(int n, int k, int next, List<Integer> path, List<List<Integer>> result) {
        throw new UnsupportedOperationException("TODO B1");
    }

    private static void collectValues(int[] values, int k, int start,
                                      List<Integer> path, List<List<Integer>> result) {
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
