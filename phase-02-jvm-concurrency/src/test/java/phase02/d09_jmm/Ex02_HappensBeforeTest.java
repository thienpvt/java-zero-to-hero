package phase02.d09_jmm;

import static phase02.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_HappensBeforeTest {

    @Test
    @DisplayName("Q8 dự đoán: double-checked locking có cần volatile không")
    void q08_prediction() {
        assertPrediction("Q8_DCL_NEEDS_VOLATILE", true,
                Ex02_HappensBefore.Q8_DCL_NEEDS_VOLATILE,
                "Không volatile, thread khác có thể thấy reference trước khi constructor ghi xong các field.");
    }
}
