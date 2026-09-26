package phase00.d02_types;

import static phase00.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_OverflowAndFinalTest {

    private static final String HINT_Q3 =
            "Alt+F8: Integer.MAX_VALUE + 1 và Integer.MAX_VALUE + 1L. Ctrl+Q trên Integer.MAX_VALUE.";

    private static final String HINT_Q4 =
            "final khóa biến, không khóa field của object. Thử gán lại biến final để thấy lỗi đỏ, "
                    + "rồi vẫn gán được bucket.n.";

    @Test
    @DisplayName("Q3 dự đoán: int cộng tràn không tự thành long")
    void q03_prediction() {
        boolean promoted = (long) (Integer.MAX_VALUE + 1) == (Integer.MAX_VALUE + 1L);
        assertPrediction("Q3_OVERFLOW_PROMOTES_TO_LONG",
                promoted, Ex02_OverflowAndFinal.Q3_OVERFLOW_PROMOTES_TO_LONG, HINT_Q3);
        assertPrediction("Q3_MAX_INT_PLUS_ONE",
                Integer.MAX_VALUE + 1, Ex02_OverflowAndFinal.Q3_MAX_INT_PLUS_ONE, HINT_Q3);
    }

    @Test
    @DisplayName("Q4 dự đoán: final trên tham chiếu không đóng băng object")
    void q04_prediction() {
        final Ex02_OverflowAndFinal.Bucket bucket = new Ex02_OverflowAndFinal.Bucket();
        int before = bucket.n;
        bucket.n = 7;
        // bucket = new Ex02_OverflowAndFinal.Bucket();
        assertPrediction("Q4_FINAL_REFERENCE_FREEZES_TARGET",
                before == bucket.n, Ex02_OverflowAndFinal.Q4_FINAL_REFERENCE_FREEZES_TARGET, HINT_Q4);
    }
}
