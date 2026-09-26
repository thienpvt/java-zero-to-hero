package phase00.d03_flow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase00.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_GradeAndOverloadTest {

    private static final String HINT_Q4 =
            "F7 Step Into từ format(\"hi\") và từ format((Object) \"hi\"). "
                    + "Overload nhìn kiểu lúc biên dịch; override nhìn object lúc chạy.";

    @Test
    @DisplayName("Q1 code: gradeIf và gradeSwitch cùng thang, kể cả biên và điểm không hợp lệ")
    void q01_grades() {
        int[] samples = {-1, 0, 1, 49, 50, 79, 80, 99, 100, 101};
        for (int score : samples) {
            int current = score;
            if (current < 0 || current > 100) {
                assertThrows(IllegalArgumentException.class, () -> Ex02_GradeAndOverload.gradeIf(current));
                assertThrows(IllegalArgumentException.class, () -> Ex02_GradeAndOverload.gradeSwitch(current));
            } else {
                assertEquals(Ex02_GradeAndOverload.gradeIf(current), Ex02_GradeAndOverload.gradeSwitch(current),
                        () -> "hai bản phải giống nhau tại điểm " + current);
            }
        }
        assertEquals("Không đạt", Ex02_GradeAndOverload.gradeIf(0));
        assertEquals("Không đạt", Ex02_GradeAndOverload.gradeIf(49));
        assertEquals("Đạt", Ex02_GradeAndOverload.gradeIf(50));
        assertEquals("Đạt", Ex02_GradeAndOverload.gradeIf(79));
        assertEquals("Giỏi", Ex02_GradeAndOverload.gradeIf(80));
        assertEquals("Giỏi", Ex02_GradeAndOverload.gradeIf(100));
    }

    @Test
    @DisplayName("Q4 dự đoán: overload theo kiểu tham chiếu, override theo object thật")
    void q04_prediction() {
        Ex02_GradeAndOverload.Printer printer = new Ex02_GradeAndOverload.Printer();
        assertPrediction("Q4_FORMAT_STRING_LITERAL",
                printer.format("hi"), Ex02_GradeAndOverload.Q4_FORMAT_STRING_LITERAL, HINT_Q4);

        Ex02_GradeAndOverload.Printer child = new Ex02_GradeAndOverload.LoudPrinter();
        assertPrediction("Q4_FORMAT_OBJECT_ON_CHILD",
                child.format((Object) "hi"), Ex02_GradeAndOverload.Q4_FORMAT_OBJECT_ON_CHILD, HINT_Q4);
    }
}
