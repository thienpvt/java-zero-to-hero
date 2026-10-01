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
class Ex01_LoggingAndMetricsTest {

    private static final String HINT_Q1 =
            "Inheritance nổ tổ hợp subclass; decorator ghép lúc runtime.";
    private static final String HINT_Q2 =
            "Decorator thêm hành vi và luôn chuyển tiếp; proxy kiểm soát truy cập.";
    private static final String HINT_Q4 =
            "Java I/O bọc stream bằng stream khác cùng interface.";

    @Test
    @DisplayName("Q1 dự đoán: decorator khác inheritance thế nào")
    void q01_prediction() {
        assertPrediction("Q1_DECORATOR_VS_INHERITANCE",
                Ex01_LoggingAndMetrics.Difference.COMPOSE_AT_RUNTIME_VS_SUBCLASS_EXPLOSION,
                Ex01_LoggingAndMetrics.Q1_DECORATOR_VS_INHERITANCE, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: decorator khác proxy ở đâu")
    void q02_prediction() {
        assertPrediction("Q2_DECORATOR_VS_PROXY",
                Ex01_LoggingAndMetrics.Difference2.ADDS_BEHAVIOR_VS_CONTROLS_ACCESS,
                Ex01_LoggingAndMetrics.Q2_DECORATOR_VS_PROXY, HINT_Q2);
    }

    @Test
    @DisplayName("Q3: hai decorator đều ghi nhận")
    void q03_bothSinksRecorded() {
        List<String> logs = new ArrayList<>();
        List<Long> metrics = new ArrayList<>();
        Ex01_LoggingAndMetrics.PaymentService service =
                new Ex01_LoggingAndMetrics.LoggingPaymentService(
                        new Ex01_LoggingAndMetrics.MetricsPaymentService(
                                new Ex01_LoggingAndMetrics.PlainPayment(), metrics),
                        logs);
        assertEquals("paid:4000", service.pay(4_000));
        assertEquals(List.of("log:4000"), logs);
        assertEquals(List.of(4_000L), metrics);
    }

    @Test
    @DisplayName("Q4 dự đoán: Java I/O dùng ý tưởng decorator thế nào")
    void q04_prediction() {
        assertPrediction("Q4_JAVA_IO",
                Ex01_LoggingAndMetrics.IoIdea.WRAP_A_STREAM_WITH_ANOTHER_OF_THE_SAME_INTERFACE,
                Ex01_LoggingAndMetrics.Q4_JAVA_IO, HINT_Q4);
    }
}
