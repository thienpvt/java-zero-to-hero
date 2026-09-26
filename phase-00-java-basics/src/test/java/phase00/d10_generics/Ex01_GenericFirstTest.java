package phase00.d10_generics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase00.support.Predictions.assertPrediction;

import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase00.support.Compiles;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_GenericFirstTest {

    private static final String HINT_Q2 =
            "Bỏ comment names.add(Integer) trên List<String>. Debug rawListFailure tới dòng get(0).";

    @Test
    @DisplayName("Q2 dự đoán: List<String> không nhận Integer; raw list vỡ lúc lấy ra")
    void q02_prediction() {
        assertPrediction("Q2_ADD_INTEGER_COMPILES",
                Compiles.NO, Ex01_GenericFirst.Q2_ADD_INTEGER_COMPILES, HINT_Q2);
        String exceptionName = "(không ném)";
        try {
            Ex01_GenericFirst.rawListFailure();
        } catch (RuntimeException ex) {
            exceptionName = ex.getClass().getSimpleName();
        }
        assertPrediction("Q2_RAW_GET_EXCEPTION",
                exceptionName, Ex01_GenericFirst.Q2_RAW_GET_EXCEPTION, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 code: first chạy với String và Integer, list rỗng thì báo lỗi")
    void q03_firstOfStringAndInteger() {
        assertEquals("a", Ex01_GenericFirst.first(List.of("a", "b")));
        assertEquals(7, Ex01_GenericFirst.first(List.of(7, 8)));
        assertThrows(NoSuchElementException.class, () -> Ex01_GenericFirst.first(List.of()));
        assertThrows(NullPointerException.class, () -> Ex01_GenericFirst.first(null));
    }
}
