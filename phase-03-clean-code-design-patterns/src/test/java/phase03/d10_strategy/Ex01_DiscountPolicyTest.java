package phase03.d10_strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_DiscountPolicyTest {

    private static final String HINT_Q1 =
            "Strategy tách thuật toán khỏi nơi dùng nó, để chọn biến thể là việc cấu hình.";
    private static final String HINT_Q2 =
            "Tiêu chí là số nhánh thay đổi độc lập và độ thường xuyên, không phải số nhánh.";
    private static final String HINT_Q3 =
            "Điều kiện ổn định và ít nhánh thì if dễ đọc hơn một interface.";

    private static final Ex01_DiscountPolicy.PricingContext CONTEXT =
            new Ex01_DiscountPolicy.PricingContext(List.of(
                    new Ex01_DiscountPolicy.NormalDiscount(),
                    new Ex01_DiscountPolicy.VipDiscount(),
                    new Ex01_DiscountPolicy.CampaignDiscount()));

    @Test
    @DisplayName("Q1 dự đoán: Strategy giải quyết vấn đề gì")
    void q01_prediction() {
        assertPrediction("Q1_STRATEGY_PROBLEM",
                Ex01_DiscountPolicy.Problem.CHOOSING_BEHAVIOR_AMONG_VARIANTS,
                Ex01_DiscountPolicy.Q1_STRATEGY_PROBLEM, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: Strategy tốt hơn switch khi nào")
    void q02_prediction() {
        assertPrediction("Q2_STRATEGY_BEATS_SWITCH", true,
                Ex01_DiscountPolicy.Q2_STRATEGY_BEATS_SWITCH, HINT_Q2);
    }

    @Test
    @DisplayName("Q2: mỗi policy cho đúng mức giảm")
    void q02_policiesGiveExpectedDiscounts() {
        assertEquals(0, new Ex01_DiscountPolicy.NormalDiscount().discount(10_000));
        assertEquals(1_000, new Ex01_DiscountPolicy.VipDiscount().discount(10_000));
        assertEquals(1_000, new Ex01_DiscountPolicy.CampaignDiscount().discount(5_000));
        assertEquals(0, new Ex01_DiscountPolicy.CampaignDiscount().discount(4_999));
    }

    @Test
    @DisplayName("Q3 dự đoán: hai nhánh if đã cần Strategy")
    void q03_prediction() {
        assertPrediction("Q3_TWO_BRANCHES_NEED_STRATEGY", false,
                Ex01_DiscountPolicy.Q3_TWO_BRANCHES_NEED_STRATEGY, HINT_Q3);
    }

    @Test
    @DisplayName("Q5: best chọn policy giảm nhiều nhất")
    void q05_bestPicksLargest() {
        assertEquals("vip", CONTEXT.best(10_000).name());
        assertEquals("vip", CONTEXT.best(1_000).name());
    }

    @Test
    @DisplayName("Q5: không có policy thì báo rõ")
    void q05_emptyContextFails() {
        Ex01_DiscountPolicy.PricingContext empty = new Ex01_DiscountPolicy.PricingContext(List.of());
        assertThrows(java.util.NoSuchElementException.class, () -> empty.best(1_000));
    }
}
