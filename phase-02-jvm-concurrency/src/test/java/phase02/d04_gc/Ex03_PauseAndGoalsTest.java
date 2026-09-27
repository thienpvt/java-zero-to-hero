package phase02.d04_gc;

import static phase02.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase02.d04_gc.Ex03_PauseAndGoals.Goal;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex03_PauseAndGoalsTest {

    @Test
    @DisplayName("Q8 dự đoán: GC tối ưu latency, throughput, hay cả hai đều là đánh đổi")
    void q08_prediction() {
        assertPrediction("Q8_GOAL", Goal.BOTH_TRADEOFF,
                Ex03_PauseAndGoals.Q8_GOAL,
                "Không có một mục tiêu duy nhất. Collector chọn đánh đổi giữa pause và lượng việc hoàn thành.");
    }
}
