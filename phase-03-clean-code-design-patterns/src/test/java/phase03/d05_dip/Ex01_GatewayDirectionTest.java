package phase03.d05_dip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_GatewayDirectionTest {

    private static final String HINT_Q1 =
            "DIP là hướng phụ thuộc lúc biên dịch; DI là cơ chế đưa dependency vào lúc runtime.";
    private static final String HINT_Q3 =
            "Interface chỉ đáng tạo khi có biến thể thật hoặc khi cần cắt một hệ ngoài khỏi test.";
    private static final String HINT_Q5 =
            "Một implementation nội bộ không cần interface. Implementation là hệ ngoài thì interface có ích cho test.";

    private static final Ex01_GatewayDirection.PaymentGateway FAKE =
            amount -> new Ex01_GatewayDirection.PaymentResult(amount, "fake");

    @Test
    @DisplayName("Q1 dự đoán: DIP khác DI thế nào")
    void q01_prediction() {
        assertPrediction("Q1_DIP_VS_DI", Ex01_GatewayDirection.Difference.DIRECTION_VS_MECHANISM,
                Ex01_GatewayDirection.Q1_DIP_VS_DI, HINT_Q1);
    }

    @Test
    @DisplayName("Q2: checkout đi qua interface, không qua class cụ thể")
    void q02_checkoutThroughInterface() {
        Ex01_GatewayDirection.Checkout checkout = new Ex01_GatewayDirection.Checkout(FAKE);
        assertEquals(new Ex01_GatewayDirection.PaymentResult(500, "fake"), checkout.checkout(500));
        assertThrows(IllegalArgumentException.class, () -> checkout.checkout(0));
    }

    @Test
    @DisplayName("Q3 dự đoán: interface cho mọi class")
    void q03_prediction() {
        assertPrediction("Q3_INTERFACE_FOR_EVERY_CLASS", false,
                Ex01_GatewayDirection.Q3_INTERFACE_FOR_EVERY_CLASS, HINT_Q3);
    }

    @Test
    @DisplayName("Q5 dự đoán: một implementation có luôn cần interface")
    void q05_prediction() {
        assertPrediction("Q5_ONE_IMPL_NEEDS_INTERFACE", false,
                Ex01_GatewayDirection.Q5_ONE_IMPL_NEEDS_INTERFACE, HINT_Q5);
    }
}
