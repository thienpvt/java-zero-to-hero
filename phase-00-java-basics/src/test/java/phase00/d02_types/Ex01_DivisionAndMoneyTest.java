package phase00.d02_types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase00.support.Predictions.assertPrediction;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase00.d02_types.Ex01_DivisionAndMoney.LineItem;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_DivisionAndMoneyTest {

    private static final String HINT_Q1 =
            "Alt+F8 đánh giá 5 / 2 và 5 / 2.0. Ctrl+Q trên constructor BigDecimal(String).";

    private static final String HINT_Q2 =
            "Debug dòng int y = x khi x là Integer null; đọc tên lớp của exception.";

    @Test
    @DisplayName("Q1 dự đoán: 5 / 2 khác 5 / 2.0")
    void q01_prediction() {
        assertPrediction("Q1_FIVE_DIV_TWO", 5 / 2, Ex01_DivisionAndMoney.Q1_FIVE_DIV_TWO, HINT_Q1);
        assertPrediction("Q1_FIVE_DIV_TWO_DOUBLE",
                Double.toString(5 / 2.0), Ex01_DivisionAndMoney.Q1_FIVE_DIV_TWO_DOUBLE, HINT_Q1);
    }

    @Test
    @DisplayName("Q1 code: total cộng BigDecimal đúng phần thập phân, kể cả số lượng 0")
    void q01_totalAddsDecimalPrices() {
        BigDecimal actual = Ex01_DivisionAndMoney.total(List.of(
                new LineItem("0.10", 1),
                new LineItem("0.20", 1),
                new LineItem("1.50", 0)));
        assertEquals(0, new BigDecimal("0.30").compareTo(actual),
                () -> "0.10 + 0.20 + 0 lần 1.50 phải ra 0.30, thực tế " + actual);
    }

    @Test
    @DisplayName("Q1 code: list rỗng cho tổng 0")
    void q01_emptyListIsZero() {
        assertEquals(0, BigDecimal.ZERO.compareTo(Ex01_DivisionAndMoney.total(List.of())));
    }

    @Test
    @DisplayName("Q1 code: giá âm, số lượng âm và giá không phải số đều bị từ chối")
    void q01_rejectsInvalidMoney() {
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_DivisionAndMoney.total(List.of(new LineItem("-0.01", 1))));
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_DivisionAndMoney.total(List.of(new LineItem("1.00", -1))));
        IllegalArgumentException badPrice = assertThrows(IllegalArgumentException.class,
                () -> Ex01_DivisionAndMoney.total(List.of(new LineItem("abc", 1))));
        assertEquals(NumberFormatException.class, badPrice.getCause().getClass());
    }

    @Test
    @DisplayName("Q2 dự đoán: unbox Integer null")
    void q02_prediction() {
        String exceptionName = "(không ném)";
        try {
            Integer x = null;
            int y = x;
            if (y == 0) {
                exceptionName = "(không ném)";
            }
        } catch (RuntimeException ex) {
            exceptionName = ex.getClass().getSimpleName();
        }
        assertPrediction("Q2_UNBOX_NULL_EXCEPTION",
                exceptionName, Ex01_DivisionAndMoney.Q2_UNBOX_NULL_EXCEPTION, HINT_Q2);
    }
}
