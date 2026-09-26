package phase00.d03_flow;

import static phase00.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_BreakAndArgumentsTest {

    private static final String HINT_Q2 =
            "Debug vòng for, F8 từng i, xem sum dừng ở đâu khi gặp break và khi gặp continue.";

    private static final String HINT_Q3 =
            "F7 vào reassign và mutate. So value của biến caller ở test với tham số boxed trong method.";

    @Test
    @DisplayName("Q2 dự đoán: break thoát vòng, continue bỏ qua một vòng")
    void q02_prediction() {
        assertPrediction("Q2_BREAK_SUM",
                Ex01_BreakAndArguments.sumWithBreak(), Ex01_BreakAndArguments.Q2_BREAK_SUM, HINT_Q2);
        assertPrediction("Q2_CONTINUE_SUM",
                Ex01_BreakAndArguments.sumWithContinue(), Ex01_BreakAndArguments.Q2_CONTINUE_SUM, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: gán lại tham số không đổi caller; sửa field thì caller thấy")
    void q03_prediction() {
        Ex01_BreakAndArguments.Box reassigned = new Ex01_BreakAndArguments.Box(1);
        Ex01_BreakAndArguments.reassign(reassigned);
        assertPrediction("Q3_REASSIGN_VISIBLE",
                reassigned.value == 99, Ex01_BreakAndArguments.Q3_REASSIGN_VISIBLE, HINT_Q3);

        Ex01_BreakAndArguments.Box mutated = new Ex01_BreakAndArguments.Box(1);
        Ex01_BreakAndArguments.mutate(mutated);
        assertPrediction("Q3_MUTATE_VISIBLE",
                mutated.value == 99, Ex01_BreakAndArguments.Q3_MUTATE_VISIBLE, HINT_Q3);
    }
}
