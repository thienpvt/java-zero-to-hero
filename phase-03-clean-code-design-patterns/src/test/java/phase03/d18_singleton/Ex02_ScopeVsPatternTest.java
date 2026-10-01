package phase03.d18_singleton;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_ScopeVsPatternTest {

    private static final String HINT_Q5 =
            "Hai cái cùng scope một instance, khác ở chỗ ai tạo và giữ instance: container hay static.";
    private static final String HINT_Q6 =
            "Bean singleton dùng chung một instance cho mọi request; field mutable là trạng thái chia sẻ giữa các request.";

    @Test
    @DisplayName("Q5 dự đoán: Spring singleton bean có giống GoF Singleton")
    void q05_prediction() {
        assertPrediction("Q5_SPRING_SINGLETON_SAME_AS_GOF",
                Ex02_ScopeVsPattern.Verdict.SAME_SCOPE_DIFFERENT_OWNERSHIP,
                Ex02_ScopeVsPattern.Q5_SPRING_SINGLETON_SAME_AS_GOF, HINT_Q5);
    }

    @Test
    @DisplayName("Q6 dự đoán: mutable field trong singleton bean có vấn đề")
    void q06_prediction() {
        assertPrediction("Q6_MUTABLE_FIELD_IN_SINGLETON_BEAN", true,
                Ex02_ScopeVsPattern.Q6_MUTABLE_FIELD_IN_SINGLETON_BEAN, HINT_Q6);
    }

    @Test
    @DisplayName("Q6: hai caller dùng chung một field, trạng thái cộng dồn")
    void q06_sharedFieldAcrossCallers() {
        Ex02_ScopeVsPattern.MutableCounterService shared = new Ex02_ScopeVsPattern.MutableCounterService();
        assertEquals(1, shared.increment());
        assertEquals(2, shared.increment());
        assertEquals(2, shared.count(), "Field nằm trên instance dùng chung nên các caller thấy lẫn nhau.");
    }
}
