package phase00.d04_text;

import static phase00.support.Predictions.assertPrediction;

import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ArrayAndStringTest {

    private static final String HINT_Q1 =
            "Alt+F8: values.length và values[3]. Index cuối là length - 1.";

    private static final String HINT_Q2 =
            "Alt+F8 với hai new String(\"hi\"): toán tử == rồi .equals. Ctrl+Q trên String.equals.";

    private static final String HINT_Q3 =
            "Alt+F8 xem biến text sau toUpperCase(Locale.ROOT) và xem chuỗi method trả về. Ctrl+Q trên toUpperCase.";

    @Test
    @DisplayName("Q1 dự đoán: index hợp lệ của new int[3] và lỗi vượt chỉ số")
    void q01_prediction() {
        int[] values = new int[3];
        assertPrediction("Q1_FIRST_INDEX", 0, Ex01_ArrayAndString.Q1_FIRST_INDEX, HINT_Q1);
        assertPrediction("Q1_LAST_INDEX_OF_LENGTH_3",
                values.length - 1, Ex01_ArrayAndString.Q1_LAST_INDEX_OF_LENGTH_3, HINT_Q1);
        String exceptionName = "(không ném)";
        try {
            int ignored = values[3];
            if (ignored == 0) {
                exceptionName = "(không ném)";
            }
        } catch (RuntimeException ex) {
            exceptionName = ex.getClass().getSimpleName();
        }
        assertPrediction("Q1_INDEX_3_EXCEPTION",
                exceptionName, Ex01_ArrayAndString.Q1_INDEX_3_EXCEPTION, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: == không so nội dung chuỗi")
    void q02_prediction() {
        String left = new String("hi");
        String right = new String("hi");
        assertPrediction("Q2_DOUBLE_EQUALS_SAME_CONTENT",
                left == right, Ex01_ArrayAndString.Q2_DOUBLE_EQUALS_SAME_CONTENT, HINT_Q2);
        assertPrediction("Q2_EQUALS_SAME_CONTENT",
                left.equals(right), Ex01_ArrayAndString.Q2_EQUALS_SAME_CONTENT, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: toUpperCase không sửa chuỗi gốc")
    void q03_prediction() {
        String text = "ab";
        String upper = text.toUpperCase(Locale.ROOT);
        boolean mutated = !text.equals("ab");
        assertPrediction("Q3_TO_UPPER_MUTATES_RECEIVER",
                mutated, Ex01_ArrayAndString.Q3_TO_UPPER_MUTATES_RECEIVER, HINT_Q3);
        org.junit.jupiter.api.Assertions.assertEquals("AB", upper);
    }
}
