package phase03.d20_testability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ExplicitDependenciesTest {

    private static final String HINT_Q1 =
            "Dependency nhận lúc tạo object nên test thay được bằng fake, không cần mạng hay database.";
    private static final String HINT_Q2 =
            "Static thuần không trạng thái, không gọi ngoài, thì test trực tiếp rất dễ.";
    private static final String HINT_Q5 =
            "Test khó viết là tín hiệu coupling cao và trạng thái ẩn, không phải thiếu hạ tầng test.";

    @Test
    @DisplayName("Q1 dự đoán: vì sao constructor injection tăng testability")
    void q01_prediction() {
        assertPrediction("Q1_CTOR_INJECTION_TESTABILITY",
                Ex01_ExplicitDependencies.Reason.DEPENDENCIES_REPLACEABLE_AT_CREATION,
                Ex01_ExplicitDependencies.Q1_CTOR_INJECTION_TESTABILITY, HINT_Q1);
    }

    @Test
    @DisplayName("Q1: fake thay cho hệ thật, không cần mạng")
    void q01_fakesReplaceRealities() {
        List<String> saved = new ArrayList<>();
        Ex01_ExplicitDependencies.CheckoutService service =
                new Ex01_ExplicitDependencies.CheckoutService(
                        amount -> "fake-charge:" + amount,
                        (orderId, totalCents) -> saved.add(orderId + ":" + totalCents));
        assertEquals("fake-charge:4000", service.checkout("o-1", 4_000));
        assertEquals(List.of("o-1:4000"), saved);
    }

    @Test
    @DisplayName("Q2 dự đoán: static utility có luôn xấu")
    void q02_prediction() {
        assertPrediction("Q2_STATIC_ALWAYS_BAD", false,
                Ex01_ExplicitDependencies.Q2_STATIC_ALWAYS_BAD, HINT_Q2);
    }

    @Test
    @DisplayName("Q5 dự đoán: test khó viết phản ánh vấn đề gì")
    void q05_prediction() {
        assertPrediction("Q5_HARD_TO_TEST_SIGNAL",
                Ex01_ExplicitDependencies.Signal.HIGH_COUPLING_AND_HIDDEN_STATE,
                Ex01_ExplicitDependencies.Q5_HARD_TO_TEST_SIGNAL, HINT_Q5);
    }
}
