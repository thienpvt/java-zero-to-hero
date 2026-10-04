package phase09.d05_binary_search;

import java.util.function.IntPredicate;

/**
 * Binary Search.
 * <p>
 * Nguồn: 09-algorithms-system-design.md, mục 5 (Binary Search), câu 1–5.
 * Cần làm trước: mục 2 (Array và String), mục 4 (Two Pointers và Sliding Window).
 * Cách làm: trả lời từng câu, chạy test trong Ex01_BinarySearchTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] Dữ liệu hoặc predicate cần tính chất gì để binary search đúng?
 *   Bắt đầu   : viết ANSWER Q1; chỉ ra tính có thứ tự hoặc tính đơn điệu.
 *   Tra cứu   : invariant của binary search.
 *   Hoàn thành khi: ANSWER Q1 nêu điều kiện đơn điệu và miền tìm kiếm.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Khác biệt giữa tìm một occurrence và lower bound là gì?
 *   Bắt đầu   : viết ANSWER Q2; xét trường hợp có phần tử trùng.
 *   Tra cứu   : lower bound trên mảng tăng dần.
 *   Hoàn thành khi: ANSWER Q2 phân biệt một vị trí bất kỳ với vị trí đầu tiên không nhỏ hơn target.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Vì sao cập nhật biên sai một đơn vị có thể gây infinite loop?
 *   Bắt đầu   : viết ANSWER Q3; lần theo đoạn tìm kiếm có hai phần tử.
 *   Tra cứu   : inclusive và half-open interval.
 *   Hoàn thành khi: ANSWER Q3 giải thích biên không đổi làm vòng lặp không tiến triển.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Khi nào binary search trên đáp án không hợp lệ?
 *   Bắt đầu   : viết ANSWER Q4; xét predicate đổi trạng thái nhiều lần.
 *   Tra cứu   : tính đơn điệu của predicate.
 *   Hoàn thành khi: ANSWER Q4 nêu vì sao predicate không đơn điệu có thể bỏ qua đáp án.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Cần trả về gì nếu không có giá trị thỏa predicate?
 *   Bắt đầu   : viết ANSWER Q5; phân biệt điểm chèn với chỉ số phần tử.
 *   Tra cứu   : quy ước sentinel của bài toán cụ thể.
 *   Hoàn thành khi: ANSWER Q5 nêu kết quả biên và quy ước caller phải thống nhất.
 * <p>
 * B1: Cài lower bound và điểm đầu tiên predicate đúng. Thay TODO trong các khối SOLUTION.
 */
class Ex01_BinarySearch {
    /**
     * Returns the first index whose value is at least {@code target}, or {@code values.length}.
     * The caller must provide a nondecreasing array. The array is not modified.
     * Runs in O(log n) time and O(1) extra space.
     */
    static int lowerBound(int[] values, int target) {
        throw new UnsupportedOperationException("TODO B1");
    }

    static int firstTrue(int low, int high, IntPredicate predicate) {
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
