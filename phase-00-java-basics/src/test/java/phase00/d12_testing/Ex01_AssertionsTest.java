package phase00.d12_testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase00.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_AssertionsTest {

    private static final String HINT_Q2 =
            "Nếu checkedDivide luôn return 0, test chỉ gọi hàm vẫn xanh. JUnit không tự biết kết quả đúng.";

    @Test
    @DisplayName("Q2 dự đoán: gọi method mà không assert không chứng minh kết quả")
    void q02_prediction() {
        assertPrediction("Q2_CALL_WITHOUT_ASSERT_PROVES_RESULT",
                false, Ex01_Assertions.Q2_CALL_WITHOUT_ASSERT_PROVES_RESULT, HINT_Q2);
    }

    @Test
    @DisplayName("Q2 code: chia đúng ca thường, ca biên và ca chia cho 0")
    void q02_normalBoundaryAndError() {
        assertEquals(5, Ex01_Assertions.checkedDivide(10, 2), "ca thường 10/2");
        assertEquals(0, Ex01_Assertions.checkedDivide(0, 5), "ca biên: số bị chia bằng 0");
        assertThrows(IllegalArgumentException.class, () -> Ex01_Assertions.checkedDivide(1, 0));
    }
}
