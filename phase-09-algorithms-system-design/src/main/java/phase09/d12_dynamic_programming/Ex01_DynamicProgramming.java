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
        // SOLUTION-BEGIN throw B1
        if (denominations == null) throw new NullPointerException("denominations");
        if (amount < 0 || amount > 10_000) throw new IllegalArgumentException("amount out of range");
        if (denominations.length > 32) throw new IllegalArgumentException("too many denominations");
        int[] coins = denominations.clone();
        Arrays.sort(coins);
        for (int i = 0; i < coins.length; i++) {
            if (coins[i] <= 0 || (i > 0 && coins[i] == coins[i - 1])) {
                throw new IllegalArgumentException("denominations must be positive and unique");
            }
        }
        int unreachable = amount + 1;
        int[] count = new int[amount + 1];
        int[] choice = new int[amount + 1];
        Arrays.fill(count, unreachable);
        count[0] = 0;
        for (int value = 1; value <= amount; value++) {
            for (int coin : coins) {
                if (coin > value) break;
                if (count[value - coin] == unreachable) continue;
                int candidate = count[value - coin] + 1;
                if (candidate < count[value]) {
                    count[value] = candidate;
                    choice[value] = coin;
                } else if (candidate == count[value] && coin < choice[value]) {
                    choice[value] = coin;
                }
            }
        }
        if (count[amount] == unreachable) return Optional.empty();
        java.util.ArrayList<Integer> result = new java.util.ArrayList<>(count[amount]);
        for (int remaining = amount; remaining > 0; ) {
            int selected = 0;
            for (int coin : coins) {
                if (coin <= remaining && count[remaining - coin] == count[remaining] - 1) {
                    selected = coin;
                    break;
                }
            }
            result.add(selected);
            remaining -= selected;
        }
        return Optional.of(List.copyOf(result));
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * State dp[x] biểu diễn số coin ít nhất cần để tạo amount x từ các denomination đã cho.
 * State đủ khi recurrence xét mọi coin cuối cùng và trạng thái nhỏ hơn chứa đủ thông tin cho lựa chọn đó.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Base case sai làm recurrence khởi đầu từ giá trị sai, gây kết quả lệch hoặc coi trạng thái unreachable là reachable.
 * Với bài coin, dp[0] phải bằng 0; mọi giá trị khác ban đầu unreachable.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Memoization hợp với state space thưa khi chỉ một phần nhỏ state được yêu cầu, nhưng dùng call stack và lookup overhead.
 * Tabulation tránh recursion, dễ kiểm soát thứ tự và bộ nhớ, phù hợp khi cần tính phần lớn state.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Có thể giảm bộ nhớ khi mỗi state chỉ phụ thuộc vào một cửa sổ hữu hạn của các state trước.
 * Iteration order phải giữ các giá trị còn cần trước khi ghi đè; nếu recurrence cần toàn bộ lịch sử thì không thể bỏ chúng.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Chọn kiểu đủ rộng theo giới hạn đầu vào và kiểm tra overflow trước khi cộng nếu cận chưa được chứng minh.
 * Nhận diện sentinel unreachable trước phép cộng; không cộng mù vào sentinel.
 * SOLUTION-END
 */
