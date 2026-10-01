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
            throw new UnsupportedOperationException("TODO Q5");
        }

        @Override
        public String name() {
            throw new UnsupportedOperationException("TODO Q5");
        }
    }

    public static final class VipDiscount implements DiscountPolicy {

        @Override
        public long discount(long totalCents) {
            throw new UnsupportedOperationException("TODO Q5");
        }

        @Override
        public String name() {
            throw new UnsupportedOperationException("TODO Q5");
        }
    }

    public static final class CampaignDiscount implements DiscountPolicy {

        @Override
        public long discount(long totalCents) {
            throw new UnsupportedOperationException("TODO Q5");
        }

        @Override
        public String name() {
            throw new UnsupportedOperationException("TODO Q5");
        }
    }

    // Q1 — Strategy giải quyết vấn đề gì.
    static final Problem Q1_STRATEGY_PROBLEM = null;

    // Q2 — Strategy tốt hơn switch khi nào.
    static final Boolean Q2_STRATEGY_BEATS_SWITCH = null;

    // Q3 — hai nhánh if đã cần Strategy.
    static final Boolean Q3_TWO_BRANCHES_NEED_STRATEGY = null;

    /** Chọn policy tốt nhất trong danh sách cấu hình sẵn. */
    public static final class PricingContext {

        private final List<DiscountPolicy> policies;

        public PricingContext(List<DiscountPolicy> policies) {
            this.policies = List.copyOf(policies);
        }

        public DiscountPolicy best(long totalCents) {
            throw new UnsupportedOperationException("TODO Q5");
        }
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

/* ANSWER Q6:
 *
 */
