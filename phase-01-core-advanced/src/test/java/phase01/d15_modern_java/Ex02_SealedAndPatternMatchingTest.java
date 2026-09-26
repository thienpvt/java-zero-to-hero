package phase01.d15_modern_java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase01.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d15_modern_java.Ex02_SealedAndPatternMatching.Circle;
import phase01.d15_modern_java.Ex02_SealedAndPatternMatching.Rectangle;
import phase01.d15_modern_java.Ex02_SealedAndPatternMatching.Square;
import phase01.support.Compiles;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_SealedAndPatternMatchingTest {

    @Test
    @DisplayName("Q3 area: tính diện tích đúng công thức cho từng subtype của Shape")
    void q03_areaComputesPerShape() {
        assertEquals(Math.PI, Ex02_SealedAndPatternMatching.area(new Circle(1)), 1e-9,
                "Diện tích hình tròn phải là PI * r^2.");
        assertEquals(6.0, Ex02_SealedAndPatternMatching.area(new Rectangle(2, 3)), 1e-9,
                "Diện tích hình chữ nhật phải là width * height.");
        assertEquals(16.0, Ex02_SealedAndPatternMatching.area(new Square(4)), 1e-9,
                "Diện tích hình vuông phải là side * side.");
    }

    @Test
    @DisplayName("Q5 dự đoán: switch expression thiếu case và ném NPE khi thiếu case null")
    void q05_prediction() {
        assertPrediction("Q5_SWITCH_EXPRESSION_MISSING_CASE_COMPILES",
                Compiles.NO, Ex02_SealedAndPatternMatching.Q5_SWITCH_EXPRESSION_MISSING_CASE_COMPILES,
                "Switch expression trên sealed interface phải cover hết permits, thiếu 1 case là lỗi "
                        + "\"the switch expression does not cover all possible input values\".");

        NullPointerException npe = assertThrows(NullPointerException.class,
                () -> Ex02_SealedAndPatternMatching.describeWithoutNullCase(null));
        assertPrediction("Q5_SWITCH_ON_NULL_WITHOUT_CASE_NULL",
                npe.getClass().getSimpleName(), Ex02_SealedAndPatternMatching.Q5_SWITCH_ON_NULL_WITHOUT_CASE_NULL,
                "Switch pattern trên kiểu tham chiếu không có case null sẽ tự ném NullPointerException "
                        + "ngay khi selector là null, trước khi thử match bất kỳ case nào.");
    }

    @Test
    @DisplayName("Q5 describe: null trả về \"null\"")
    void q05_describeReturnsNullForNullInput() {
        assertEquals("null", Ex02_SealedAndPatternMatching.describe(null));
    }

    @Test
    @DisplayName("Q5 describe: Integer âm trả về \"số âm\"")
    void q05_describeReturnsNegativeLabelForNegativeInteger() {
        assertEquals("số âm", Ex02_SealedAndPatternMatching.describe(-5));
    }

    @Test
    @DisplayName("Q5 describe: Integer không âm trả về \"số nguyên\"")
    void q05_describeReturnsIntegerLabelForNonNegativeInteger() {
        assertEquals("số nguyên", Ex02_SealedAndPatternMatching.describe(5));
    }

    @Test
    @DisplayName("Q5 describe: chuỗi rỗng trả về \"chuỗi rỗng\"")
    void q05_describeReturnsEmptyStringLabelForEmptyString() {
        assertEquals("chuỗi rỗng", Ex02_SealedAndPatternMatching.describe(""));
    }

    @Test
    @DisplayName("Q5 describe: chuỗi không rỗng trả về \"chuỗi\"")
    void q05_describeReturnsStringLabelForNonEmptyString() {
        assertEquals("chuỗi", Ex02_SealedAndPatternMatching.describe("abc"));
    }

    @Test
    @DisplayName("Q5 describe: instance của Shape trả về \"hình\"")
    void q05_describeReturnsShapeLabelForShapeInstance() {
        assertEquals("hình", Ex02_SealedAndPatternMatching.describe(new Circle(1)));
    }

    @Test
    @DisplayName("Q5 describe: kiểu không khớp nhánh nào trả về \"khác\"")
    void q05_describeReturnsOtherLabelForUnhandledType() {
        assertEquals("khác", Ex02_SealedAndPatternMatching.describe(3.14));
    }
}
