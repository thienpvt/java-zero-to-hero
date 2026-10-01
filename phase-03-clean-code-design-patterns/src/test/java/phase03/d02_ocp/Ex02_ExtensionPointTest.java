package phase03.d02_ocp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_ExtensionPointTest {

    private static final String HINT_Q5 =
            "Một interface chỉ có một implementation chưa chứng minh được là cần thiết; chi phí đọc và bảo trì thì có thật.";

    @Test
    @DisplayName("Q2: điểm mở rộng nhận policy mới không sửa caller")
    void q02_feeWith() {
        assertEquals(200, Ex02_ExtensionPoint.feeWith(new Ex01_PaymentSwitch.CardPolicy(), 10_000));
        assertEquals(0, Ex02_ExtensionPoint.feeWith(Ex02_ExtensionPoint.FIXED, 10_000));
    }

    @Test
    @DisplayName("Q5 dự đoán: có nên abstract trước biến thể thứ hai")
    void q05_prediction() {
        assertPrediction("Q5_ABSTRACT_BEFORE_SECOND_VARIANT", false,
                Ex02_ExtensionPoint.Q5_ABSTRACT_BEFORE_SECOND_VARIANT, HINT_Q5);
    }
}
