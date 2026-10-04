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
        // SOLUTION-BEGIN throw B1
        java.util.Objects.requireNonNull(values, "values");
        int low = 0, high = values.length;
        while (low < high) {
            int middle = low + (high - low) / 2;
            if (values[middle] < target) low = middle + 1;
            else high = middle;
        }
        return low;
        // SOLUTION-END
    }

    static int firstTrue(int low, int high, IntPredicate predicate) {
        // SOLUTION-BEGIN throw B1
        java.util.Objects.requireNonNull(predicate, "predicate");
        if (low >= high || predicate.test(low) || !predicate.test(high)) {
            throw new IllegalArgumentException("expected low < high, false at low, and true at high");
        }
        while ((long) high - low > 1) {
            int middle = (int) ((long) low + ((long) high - low) / 2);
            if (predicate.test(middle)) high = middle;
            else low = middle;
        }
        return high;
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Binary search cần dữ liệu được sắp theo một thứ tự phù hợp hoặc predicate đơn điệu trên miền tìm kiếm.
 * Invariant giúp mỗi lần loại bỏ một nửa miền vẫn giữ được vị trí đáp án.
 * Nếu thứ tự hoặc tính đơn điệu không đúng, đáp án có thể nằm trong phần đã loại.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Tìm một occurrence có thể trả về bất cứ vị trí nào có giá trị bằng target.
 * Lower bound trả về vị trí đầu tiên có giá trị lớn hơn hoặc bằng target, kể cả khi có phần tử trùng.
 * Nếu mọi giá trị nhỏ hơn target, kết quả là độ dài mảng, tức điểm chèn ở cuối.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Khi còn hai ứng viên, midpoint có thể bằng low; cập nhật low = midpoint không làm miền nhỏ đi.
 * Vòng lặp tiếp tục xử lý đúng miền cũ và không kết thúc.
 * Cập nhật biên phải loại midpoint khỏi miền đang xét hoặc chuyển sang interval half-open nhất quán.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Binary search trên đáp án không hợp lệ nếu predicate không đơn điệu theo thứ tự đáp án.
 * Khi trạng thái đúng rồi sai rồi lại đúng, việc loại một nửa có thể loại mất nghiệm.
 * Cần chứng minh tính đơn điệu trước khi áp dụng cách tìm này.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Với lower bound, không có phần tử thỏa điều kiện thì trả về điểm chèn n, ngay sau phần tử cuối.
 * Đây là vị trí chứ không phải một index hợp lệ để đọc phần tử.
 * Với predicate trên miền riêng, sentinel cần theo quy ước của bài toán và caller phải xử lý nhất quán.
 * SOLUTION-END
 */
