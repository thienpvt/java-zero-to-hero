package phase00.d01_jdk_program;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase00.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase00.support.Compiles;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_PackageAndErrorsTest {

    private static final String HINT_Q2 =
            "Mở Ex02_PackageAndErrors: Adder được gọi không qua import; String nằm ở java.lang.";

    private static final String HINT_Q3 =
            "Bỏ comment dòng Adder.sum với đối số String để thấy lỗi đỏ. Debug divide(1, 0) "
                    + "với biến divisor và đọc tên exception.";

    @Test
    @DisplayName("Q2 dự đoán: cùng package và java.lang có cần import không?")
    void q02_prediction() {
        assertPrediction("Q2_SAME_PACKAGE_NEEDS_IMPORT",
                false, Ex02_PackageAndErrors.Q2_SAME_PACKAGE_NEEDS_IMPORT, HINT_Q2);
        assertPrediction("Q2_JAVA_LANG_NEEDS_IMPORT",
                false, Ex02_PackageAndErrors.Q2_JAVA_LANG_NEEDS_IMPORT, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: sai kiểu là lỗi compile; chia cho 0 bằng biến là lỗi lúc chạy")
    void q03_prediction() {
        assertPrediction("Q3_WRONG_ARGUMENT_COMPILES",
                Compiles.NO, Ex02_PackageAndErrors.Q3_WRONG_ARGUMENT_COMPILES, HINT_Q3);

        int divisor = 0;
        String exceptionName = "(không ném)";
        boolean compileError = false;
        try {
            Ex02_PackageAndErrors.divide(1, divisor);
        } catch (RuntimeException ex) {
            exceptionName = ex.getClass().getSimpleName();
        }
        assertPrediction("Q3_DIVIDE_BY_ZERO_IS_COMPILE_ERROR",
                compileError, Ex02_PackageAndErrors.Q3_DIVIDE_BY_ZERO_IS_COMPILE_ERROR, HINT_Q3);
        assertPrediction("Q3_DIVIDE_BY_ZERO_EXCEPTION",
                exceptionName, Ex02_PackageAndErrors.Q3_DIVIDE_BY_ZERO_EXCEPTION, HINT_Q3);
    }

    @Test
    @DisplayName("Q3 code: Adder.sum cộng hai số nguyên")
    void q03_sum() {
        assertEquals(5, Ex02_PackageAndErrors.Adder.sum(2, 3));
        assertEquals(0, Ex02_PackageAndErrors.Adder.sum(-1, 1));
    }
}
