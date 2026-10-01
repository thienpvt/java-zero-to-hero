package phase03.d20_testability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_StaticAndMockSymptomTest {

    private static final String HINT_Q3 =
            "Không có chỗ đặt fake thì test phải dùng mạng thật hoặc chờ thật.";
    private static final String HINT_Q4 =
            "Nhiều collaborator phải mock thường là nhiều nhóm trách nhiệm trong một class.";

    @Test
    @DisplayName("Q3 dự đoán: gọi trực tiếp external API làm test khó vì sao")
    void q03_prediction() {
        assertPrediction("Q3_DIRECT_EXTERNAL_CALL",
                Ex02_StaticAndMockSymptom.Why.TEST_NEEDS_REAL_NETWORK_OR_WAITING,
                Ex02_StaticAndMockSymptom.Q3_DIRECT_EXTERNAL_CALL, HINT_Q3);
    }

    @Test
    @DisplayName("Q3: caller có gateway thay được, caller static thì không")
    void q03_injectedCallerHasReplaceableField() {
        Ex02_StaticAndMockSymptom.InjectedApiCaller injected =
                new Ex02_StaticAndMockSymptom.InjectedApiCaller(key -> "fake:" + key);
        assertEquals("fake:k1", injected.fetch("k1"));
        assertEquals(1, Ex02_StaticAndMockSymptom.dependencyCount(
                Ex02_StaticAndMockSymptom.InjectedApiCaller.class));
        assertEquals(0, Ex02_StaticAndMockSymptom.dependencyCount(
                Ex02_StaticAndMockSymptom.DirectApiCaller.class));
    }

    @Test
    @DisplayName("Q4 dự đoán: mock quá nhiều dependency báo hiệu gì")
    void q04_prediction() {
        assertPrediction("Q4_TOO_MANY_MOCKS",
                Ex02_StaticAndMockSymptom.Signal.CLASS_HAS_TOO_MANY_RESPONSIBILITIES,
                Ex02_StaticAndMockSymptom.Q4_TOO_MANY_MOCKS, HINT_Q4);
    }

    @Test
    @DisplayName("Q4: dependencyCount đếm field không static")
    void q04_dependencyCount() {
        assertEquals(2, Ex02_StaticAndMockSymptom.dependencyCount(
                Ex01_ExplicitDependencies.CheckoutService.class));
        assertThrows(NullPointerException.class, () -> Ex02_StaticAndMockSymptom.dependencyCount(null));
    }
}
