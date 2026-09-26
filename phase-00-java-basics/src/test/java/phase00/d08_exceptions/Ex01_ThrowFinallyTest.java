package phase00.d08_exceptions;

import static phase00.support.Predictions.assertPrediction;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase00.support.Compiles;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ThrowFinallyTest {

    private static final String HINT_Q2 =
            "F7 vào raise(). Bỏ comment checkedSample và đọc lỗi đỏ: IOException là checked.";

    private static final String HINT_Q3 =
            "Breakpoint trong finally và Door.close. Ctrl+Q trên AutoCloseable.";

    @Test
    @DisplayName("Q2 dự đoán: throw bắn exception; checked exception phải khai báo hoặc bắt")
    void q02_prediction() {
        assertPrediction("Q2_THROW_SENDS_EXCEPTION",
                Ex01_ThrowFinally.catchRaise(), Ex01_ThrowFinally.Q2_THROW_SENDS_EXCEPTION, HINT_Q2);
        assertPrediction("Q2_CHECKED_WITHOUT_DECLARE_COMPILES",
                Compiles.NO, Ex01_ThrowFinally.Q2_CHECKED_WITHOUT_DECLARE_COMPILES, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: finally vẫn chạy khi thân ném lỗi; try-with-resources gọi close")
    void q03_prediction() {
        assertPrediction("Q3_FINALLY_RUNS_WHEN_BODY_THROWS",
                Ex01_ThrowFinally.finallyRunsAfterThrow(),
                Ex01_ThrowFinally.Q3_FINALLY_RUNS_WHEN_BODY_THROWS,
                HINT_Q3);
        List<String> events = Ex01_ThrowFinally.useDoor();
        assertPrediction("Q3_TRY_WITH_RESOURCES_CLOSES",
                events.equals(List.of("open", "close")),
                Ex01_ThrowFinally.Q3_TRY_WITH_RESOURCES_CLOSES,
                HINT_Q3);
    }
}
