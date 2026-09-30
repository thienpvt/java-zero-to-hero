package phase03.d03_lsp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_RectangleSquareTest {

    private static final String HINT_Q2 =
            "Caller giả định width và height độc lập. Đổi width mà height đổi theo là phá kỳ vọng đó.";
    private static final String HINT_Q3 =
            "Method của parent nằm trong hợp đồng chung. Không giữ được hợp đồng đó là dấu hiệu inheritance hoặc interface sai.";

    @Test
    @DisplayName("Q2 dự đoán: Square có phải subtype đúng LSP của Rectangle")
    void q02_prediction() {
        assertPrediction("Q2_SQUARE_IS_LSP_SUBTYPE", false,
                Ex01_RectangleSquare.Q2_SQUARE_IS_LSP_SUBTYPE, HINT_Q2);
    }

    @Test
    @DisplayName("Q2: caller Rectangle bị phá nếu Square độc lập cạnh")
    void q02_growWidthBreaksRectangle() {
        Ex01_RectangleSquare.Rectangle rectangle = new Ex01_RectangleSquare.Rectangle(2, 3);
        assertEquals(6, rectangle.rotate(), "Xoay không đổi diện tích hình chữ nhật.");
    }

    @Test
    @DisplayName("Q3 dự đoán: UnsupportedOperationException là dấu hiệu xấu")
    void q03_prediction() {
        assertPrediction("Q3_UNSUPPORTED_IS_SMELL", true,
                Ex01_RectangleSquare.Q3_UNSUPPORTED_IS_SMELL, HINT_Q3);
    }

    @Test
    @DisplayName("Q5: rotate chạy cho cả Rectangle và Square, không ném exception")
    void q05_rotateWorksForBoth() {
        assertEquals(12, new Ex01_RectangleSquare.Rectangle(4, 3).rotate());
        assertEquals(9, new Ex01_RectangleSquare.Square(3).rotate());
    }
}
