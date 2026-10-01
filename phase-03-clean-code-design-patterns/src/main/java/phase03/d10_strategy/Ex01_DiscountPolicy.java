package phase03.d10_strategy;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Strategy — Bài 1: DiscountPolicy
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 10 (Strategy Pattern), câu 1, 2, 3, 4, 5, 6.
 * Cần làm trước: d09_layers.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_DiscountPolicyTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Strategy giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q1_STRATEGY_PROBLEM bằng một giá trị của {@code Problem}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu tách thuật toán khỏi nơi dùng nó.
 * <p>
 * Q2 [DỰ ĐOÁN] Strategy tốt hơn switch khi nào?
 *   Bắt đầu   : điền Q2_STRATEGY_BEATS_SWITCH.
 *   Kiểm chứng: chạy q02_prediction và q02_policiesGiveExpectedDiscounts.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu nhánh đổi độc lập và hay đổi.
 * <p>
 * Q3 [DỰ ĐOÁN] Có phải có hai nhánh if là nên tạo Strategy không?
 *   Bắt đầu   : điền Q3_TWO_BRANCHES_NEED_STRATEGY.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nêu nhánh ổn định không cần tách.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Strategy liên quan OCP thế nào?
 *   Bắt đầu   : viết khối ANSWER Q4.
 *   Hoàn thành khi: ANSWER Q4 nêu thêm policy là thêm class, không sửa caller.
 * <p>
 * Q5 [CODE] Strategy runtime selection được thực hiện thế nào?
 *   Bắt đầu   : cài {@code PricingContext.best} trả mức giảm lớn nhất trong danh sách.
 *   Kiểm chứng: chạy q05_bestPicksLargest.
 *   Hoàn thành khi: q05_bestPicksLargest xanh.
 * <p>
 * Q6 [TỰ TRẢ LỜI] Có thể kết hợp Strategy với Spring DI ra sao?
 *   Bắt đầu   : viết khối ANSWER Q6.
 *   Hoàn thành khi: ANSWER Q6 nêu Spring inject một danh sách implementation theo cùng interface.
 */
public class Ex01_DiscountPolicy {

    /** Vấn đề Strategy giải quyết. */
    public enum Problem {
        MEMORY_USAGE,
        CHOOSING_BEHAVIOR_AMONG_VARIANTS,
        COMPILE_SPEED
    }

    /** Contract chung cho mọi cách giảm giá. */
    public interface DiscountPolicy {

        long discount(long totalCents);

        String name();
    }

    public static final class NormalDiscount implements DiscountPolicy {

        @Override
        public long discount(long totalCents) {
            // SOLUTION-BEGIN throw Q5
            return 0;
            // SOLUTION-END
        }

        @Override
        public String name() {
            // SOLUTION-BEGIN throw Q5
            return "normal";
            // SOLUTION-END
        }
    }

    public static final class VipDiscount implements DiscountPolicy {

        @Override
        public long discount(long totalCents) {
            // SOLUTION-BEGIN throw Q5
            return totalCents * 10 / 100;
            // SOLUTION-END
        }

        @Override
        public String name() {
            // SOLUTION-BEGIN throw Q5
            return "vip";
            // SOLUTION-END
        }
    }

    public static final class CampaignDiscount implements DiscountPolicy {

        @Override
        public long discount(long totalCents) {
            // SOLUTION-BEGIN throw Q5
            return totalCents >= 5_000 ? 1_000 : 0;
            // SOLUTION-END
        }

        @Override
        public String name() {
            // SOLUTION-BEGIN throw Q5
            return "campaign";
            // SOLUTION-END
        }
    }

    // Q1 — Strategy giải quyết vấn đề gì.
    static final Problem Q1_STRATEGY_PROBLEM = Problem.CHOOSING_BEHAVIOR_AMONG_VARIANTS; // SOLUTION-VALUE

    // Q2 — Strategy tốt hơn switch khi nào.
    static final Boolean Q2_STRATEGY_BEATS_SWITCH = true; // SOLUTION-VALUE

    // Q3 — hai nhánh if đã cần Strategy.
    static final Boolean Q3_TWO_BRANCHES_NEED_STRATEGY = false; // SOLUTION-VALUE

    /** Chọn policy tốt nhất trong danh sách cấu hình sẵn. */
    public static final class PricingContext {

        private final List<DiscountPolicy> policies;

        public PricingContext(List<DiscountPolicy> policies) {
            this.policies = List.copyOf(policies);
        }

        public DiscountPolicy best(long totalCents) {
            // SOLUTION-BEGIN throw Q5
            return policies.stream()
                    .max(java.util.Comparator.comparingLong(policy -> policy.discount(totalCents)))
                    .orElseThrow(() -> new NoSuchElementException("Không có policy nào."));
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Strategy tách một thuật toán khỏi nơi dùng nó.
 * Nơi dùng giữ cùng một lời gọi, còn biến thể nằm trong các implementation riêng.
 * Nhờ vậy chọn biến thể là việc cấu hình, không phải sửa luồng chính.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Strategy tốt hơn khi các nhánh đổi độc lập và thường xuyên, ví dụ thêm loại giảm giá mỗi chiến dịch.
 * Mỗi policy được test riêng, và switch không phải mở lại để thêm nhánh.
 * Nếu các nhánh ổn định và ít, chính switch trong một method lại dễ đọc hơn.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Không. Hai nhánh với điều kiện ổn định thì if là đủ.
 * Số nhánh không phải tiêu chí; tiêu chí là nhánh đó có phải điểm mở rộng thường xuyên đổi.
 * Tách sớm chỉ thêm lớp phải đọc mà không giảm được thay đổi nào.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Thêm một policy mới là thêm một class implement interface, caller không đổi.
 * Vì vậy luồng chính đóng với sửa đổi nhưng mở với mở rộng.
 * Kèm theo là policy mới test riêng, không chạy lại mọi nhánh cũ.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Định nghĩa một interface policy, đánh dấu mọi implementation là bean.
 * Spring inject cả List hoặc Map các bean cùng interface vào nơi cần.
 * Chọn policy lúc runtime trở thành tra cứu trong danh sách đó, không có switch trong code.
 * SOLUTION-END
 */
