package phase03.d02_ocp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_PaymentSwitchTest {

    private static final String HINT_Q1 =
            "OCP không phải về tốc độ biên dịch, mà về việc phải mở lại code đang chạy đúng để thêm một biến thể.";
    private static final String HINT_Q3 =
            "Điều kiện ổn định, ví dụ \"số dư đủ không\", không phải điểm mở rộng. OCP nhắm vào chỗ có biến thể thay đổi.";

    @Test
    @DisplayName("Q1 dự đoán: OCP giảm rủi ro nào")
    void q01_prediction() {
        assertPrediction("Q1_OCP_REDUCES", Ex01_PaymentSwitch.Risk.EDITING_WORKING_CODE,
                Ex01_PaymentSwitch.Q1_OCP_REDUCES, HINT_Q1);
    }

    @Test
    @DisplayName("Q2: switch cho đúng phí CARD và WALLET")
    void q02_switchFees() {
        assertEquals(200, Ex01_PaymentSwitch.feeCents("CARD", 10_000));
        assertEquals(200, new Ex01_PaymentSwitch.CardPolicy().feeCents(10_000));
        assertEquals(100, Ex01_PaymentSwitch.feeCents("WALLET", 10_000));
        assertEquals(100, new Ex01_PaymentSwitch.WalletPolicy().feeCents(10_000));
    }

    @Test
    @DisplayName("Q2: policy cho cùng phí với switch")
    void q02_policyFees() {
        assertEquals(200, Ex01_PaymentSwitch.feeCents("CARD", 10_000));
        assertEquals(200, new Ex01_PaymentSwitch.CardPolicy().feeCents(10_000));
        assertEquals(100, Ex01_PaymentSwitch.feeCents("WALLET", 10_000));
        assertEquals(100, new Ex01_PaymentSwitch.WalletPolicy().feeCents(10_000));
        assertEquals("CARD", new Ex01_PaymentSwitch.CardPolicy().name());
    }

    @Test
    @DisplayName("Q3 dự đoán: mọi if có vi phạm OCP không")
    void q03_prediction() {
        assertPrediction("Q3_EVERY_IF_VIOLATES_OCP", false,
                Ex01_PaymentSwitch.Q3_EVERY_IF_VIOLATES_OCP, HINT_Q3);
    }
}
