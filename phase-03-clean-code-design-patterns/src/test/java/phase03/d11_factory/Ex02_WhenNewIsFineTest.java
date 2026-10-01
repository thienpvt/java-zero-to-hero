package phase03.d11_factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_WhenNewIsFineTest {

    private static final String HINT_Q2 =
            "Một lớp cụ thể, không có biến thể, thì new là đủ; factory chỉ thêm một lớp gọi.";
    private static final String HINT_Q3 =
            "Factory chọn lớp nào; builder ráp field của lớp đã biết.";

    @Test
    @DisplayName("Q2 dự đoán: new trực tiếp hợp lý khi nào")
    void q02_prediction() {
        assertPrediction("Q2_NEW_IS_FINE", true, Ex02_WhenNewIsFine.Q2_NEW_IS_FINE, HINT_Q2);
    }

    @Test
    @DisplayName("Q2: Coupon áp phần trăm và chặn giá trị sai")
    void q02_applyCoupon() {
        assertEquals(900, new Ex02_WhenNewIsFine.Coupon("SALE10", 10).apply(1_000));
        assertThrows(IllegalArgumentException.class, () -> new Ex02_WhenNewIsFine.Coupon("BAD", 101));
    }

    @Test
    @DisplayName("Q3 dự đoán: Factory khác Builder thế nào")
    void q03_prediction() {
        assertPrediction("Q3_FACTORY_VS_BUILDER",
                Ex02_WhenNewIsFine.Difference.FACTORY_CHOOSES_CLASS_BUILDER_ASSEMBLES_FIELDS,
                Ex02_WhenNewIsFine.Q3_FACTORY_VS_BUILDER, HINT_Q3);
    }
}
