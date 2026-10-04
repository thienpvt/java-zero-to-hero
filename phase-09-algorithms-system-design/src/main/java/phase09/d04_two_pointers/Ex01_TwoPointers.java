package phase09.d04_two_pointers;

/**
 * Two Pointers và Sliding Window.
 * Nguồn: 09-algorithms-system-design.md, mục 4, câu 1–5.
 * Cần làm trước: ôn mảng đã sắp xếp, vòng lặp và tính chất số không âm.
 * Cách làm: trả lời từng câu trong ANSWER tương ứng; chạy test B1 và so oracle vét cạn.
 *
 * Q1 [TỰ TRẢ LỜI] Điều kiện nào giúp sliding window duy trì được kết quả khi co/mở cửa sổ?
 *   Bắt đầu: xác định cách thay đổi cửa sổ ảnh hưởng đơn điệu đến điều kiện cần đạt.
 *   Tra cứu: mục 4 tài liệu nguồn; thử mở rộng/co cửa sổ trên dữ liệu có điều kiện.
 *   Hoàn thành khi: nêu được invariant và vì sao mỗi lần co/mở không bỏ qua nghiệm tốt hơn.
 * Q2 [TỰ TRẢ LỜI] Vì sao tổng đoạn con với số âm có thể không dùng được cách co cửa sổ thông thường?
 *   Bắt đầu: thêm một số âm vào cửa sổ và theo dõi tổng.
 *   Tra cứu: mục 4 tài liệu nguồn; so sánh tổng trước/sau khi thêm hoặc bỏ phần tử âm.
 *   Hoàn thành khi: giải thích được tổng không còn đơn điệu theo thao tác co/mở.
 * Q3 [TỰ TRẢ LỜI] Trong two pointers trên array đã sort, vì sao có thể di chuyển một pointer mà không bỏ sót nghiệm?
 *   Bắt đầu: xét tổng hai đầu khi tổng nhỏ hơn hoặc lớn hơn target.
 *   Tra cứu: mục 4 tài liệu nguồn; chứng minh bằng thứ tự tăng của array.
 *   Hoàn thành khi: chứng minh được dịch đầu trái lên hoặc đầu phải xuống loại bỏ vùng không thể có nghiệm.
 * Q4 [TỰ TRẢ LỜI] Invariant của cửa sổ là gì?
 *   Bắt đầu: viết điều kiện luôn đúng sau mỗi bước mở/co cửa sổ.
 *   Tra cứu: định nghĩa loop invariant và mục 4 tài liệu nguồn.
 *   Hoàn thành khi: phát biểu được invariant cụ thể cho bài cửa sổ đang xét.
 * Q5 [TỰ TRẢ LỜI] Cần kiểm tra trường hợp input rỗng nào?
 *   Bắt đầu: xác định kết quả hợp lệ khi không có phần tử để xét.
 *   Tra cứu: contract của pairSumSorted/minWindowLength và test B1.
 *   Hoàn thành khi: nêu được kết quả rỗng đúng contract và không truy cập phần tử ngoài biên.
 * B1 [CODE]: cài pairSumSorted và minWindowLength; chạy b01FindsPairsAndMinimumWindow cùng hai test oracle/validation.
 */

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Cửa sổ cần có invariant có thể phục hồi khi thêm/bỏ phần tử và predicate thay đổi đơn điệu.
 * Với số không âm, tổng không giảm khi mở rộng và không tăng khi co, nên có thể co tham lam khi đạt target.
 * SOLUTION-END
 */
/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Số âm có thể làm tổng giảm khi mở rộng hoặc tăng khi co.
 * Vì predicate không đơn điệu theo kích thước cửa sổ, thao tác co tham lam có thể bỏ qua đoạn con hợp lệ.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Nếu tổng nhỏ hơn target, giữ phần tử trái và giảm phần tử phải chỉ làm tổng nhỏ hơn hoặc bằng, nên dịch trái lên.
 * Nếu tổng lớn hơn target, giữ phải và tăng phần tử trái chỉ làm tổng lớn hơn hoặc bằng, nên dịch phải xuống.
 * Thứ tự array bảo đảm vùng bị loại không thể chứa nghiệm.
 * SOLUTION-END
 */
/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Invariant là điều kiện luôn đúng ở đầu/cuối mỗi vòng lặp, chẳng hạn sum bằng tổng values[start..end].
 * Với sliding window nonnegative, mọi cửa sổ đã co để đạt target đều không còn tiền tố trái nào bỏ được.
 * SOLUTION-END
 */
/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Với array rỗng, pairSumSorted trả {-1,-1}; minWindowLength trả 0 vì không có đoạn thỏa điều kiện.
 * Kiểm tra độ dài trước khi đọc phần tử để tránh truy cập ngoài biên.
 * SOLUTION-END
 */
class Ex01_TwoPointers {
    static int[] pairSumSorted(int[] values, int target) {
        // SOLUTION-BEGIN throw B1
        java.util.Objects.requireNonNull(values, "values");
        for (int i = 1; i < values.length; i++) {
            if (values[i] < values[i - 1]) throw new IllegalArgumentException("values must be ascending");
        }
        int left = 0, right = values.length - 1;
        while (left < right) {
            long sum = (long) values[left] + values[right];
            if (sum == target) {
                int first = left + 1;
                while ((long) values[left] + values[first] < target) first++;
                return new int[]{left, first};
            }
            if (sum < target) left++;
            else right--;
        }
        return new int[]{-1, -1};
        // SOLUTION-END
    }

    static int minWindowLength(int[] values, long target) {
        // SOLUTION-BEGIN throw B1
        java.util.Objects.requireNonNull(values, "values");
        if (target <= 0) throw new IllegalArgumentException("target must be positive");
        for (int value : values) {
            if (value < 0) throw new IllegalArgumentException("values must be nonnegative");
        }
        int start = 0, minimum = Integer.MAX_VALUE;
        long sum = 0;
        for (int end = 0; end < values.length; end++) {
            sum += values[end];
            while (sum >= target) {
                minimum = Math.min(minimum, end - start + 1);
                sum -= values[start++];
            }
        }
        return minimum == Integer.MAX_VALUE ? 0 : minimum;
        // SOLUTION-END
    }
}
