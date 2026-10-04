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
        // SOLUTION-BEGIN throw B1
        if (n < 0 || k < 0) throw new IllegalArgumentException("n and k must be non-negative");
        if (k > n) throw new IllegalArgumentException("k must not exceed n");
        List<List<Integer>> result = new ArrayList<>();
        collectRange(n, k, 1, new ArrayList<>(), result);
        return result.stream().map(List::copyOf).toList();
        // SOLUTION-END
    }

    static List<List<Integer>> combinations(int[] values, int k) {
        // SOLUTION-BEGIN throw B1
        if (values == null) throw new NullPointerException("values");
        if (k < 0) throw new IllegalArgumentException("k must be non-negative");
        int[] sorted = values.clone();
        Arrays.sort(sorted);
        if (k > sorted.length) throw new IllegalArgumentException("k must not exceed input size");
        List<List<Integer>> result = new ArrayList<>();
        collectValues(sorted, k, 0, new ArrayList<>(), result);
        return result.stream().map(List::copyOf).toList();
        // SOLUTION-END
    }

    private static void collectRange(int n, int k, int next, List<Integer> path, List<List<Integer>> result) {
        if (path.size() == k) {
            result.add(new ArrayList<>(path));
            return;
        }
        int needed = k - path.size();
        for (int value = next; value <= n - needed + 1; value++) {
            path.add(value);
            collectRange(n, k, value + 1, path, result);
            path.remove(path.size() - 1);
        }
    }

    private static void collectValues(int[] values, int k, int start,
                                      List<Integer> path, List<List<Integer>> result) {
        if (path.size() == k) {
            result.add(new ArrayList<>(path));
            return;
        }
        int needed = k - path.size();
        for (int i = start; i <= values.length - needed; i++) {
            if (i > start && values[i] == values[i - 1]) continue;
            path.add(values[i]);
            collectValues(values, k, i + 1, path, result);
            path.remove(path.size() - 1);
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * State gồm các lựa chọn đã đưa vào path và vị trí/lựa chọn kế tiếp có thể xét.
 * Nó cần đủ để kiểm tra constraint, tiến tới điều kiện dừng và khôi phục nhánh cha.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Pruning chỉ loại nhánh khi chứng minh không phần mở rộng nào của nhánh có thể hợp lệ.
 * Mọi nhánh còn khả năng tạo nghiệm vẫn được duyệt, nên không làm mất nghiệm đúng.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Nếu không undo, lựa chọn của nhánh trước rò sang nhánh sibling và làm sai state.
 * Thêm lựa chọn trước khi đệ quy rồi gỡ nó sau khi trở về giữ path đúng với từng nhánh.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Chọn các occurrence giống nhau ở vị trí khác nhau có thể sinh cùng một tổ hợp nhiều lần.
 * Có thể sort rồi bỏ qua giá trị lặp tại cùng độ sâu để chỉ sinh một nhánh cho mỗi giá trị.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Không. Nếu có R nghiệm và output chứa tổng cộng L phần tử, chỉ ghi output đã cần Ω(L) thời gian.
 * Thuật toán có thể giảm phần tìm kiếm thừa, nhưng không thể tránh chi phí tạo và trả mọi nghiệm.
 * SOLUTION-END
 */
