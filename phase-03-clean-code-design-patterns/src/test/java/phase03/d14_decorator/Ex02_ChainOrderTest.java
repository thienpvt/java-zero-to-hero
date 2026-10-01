package phase03.d14_decorator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_ChainOrderTest {

    private static final String HINT_Q3 =
            "Mọi lớp cùng interface nên ghép được; lớp ngoài cùng chạy trước.";

    @Test
    @DisplayName("Q3 dự đoán: ghép được nhiều decorator")
    void q03_prediction() {
        assertPrediction("Q3_CHAINABLE", true, Ex02_ChainOrder.Q3_CHAINABLE, HINT_Q3);
    }

    @Test
    @DisplayName("Q3: logging ngoài cùng ghi trước metrics")
    void q03_outerLogRunsFirst() {
        List<String> logs = new ArrayList<>();
        List<Long> metrics = new ArrayList<>();
        Ex01_LoggingAndMetrics.PaymentService chain =
                Ex02_ChainOrder.wrap(new Ex01_LoggingAndMetrics.PlainPayment(), logs, metrics);
        assertEquals("paid:1000", chain.pay(1_000));
        assertEquals(List.of("log:1000"), logs);
        assertEquals(List.of(1_000L), metrics);
    }
}
