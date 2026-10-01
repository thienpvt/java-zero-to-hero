package phase03.d08_concerns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_PolicyPlacementTest {

    private static final String HINT_Q2 =
            "Quy tắc nghiệp vụ không phụ thuộc giao thức hay lưu trữ, nên nó ở domain.";
    private static final String HINT_Q4 =
            "Entity phụ thuộc request thì chỉ dựng được trong ngữ cảnh web.";

    private static final Ex02_PolicyPlacement.OrderValidator VALIDATOR =
            new Ex02_PolicyPlacement.OrderValidator();

    @Test
    @DisplayName("Q2 dự đoán: validation nghiệp vụ ở đâu")
    void q02_prediction() {
        assertPrediction("Q2_VALIDATION_LOCATION", Ex02_PolicyPlacement.Location.DOMAIN,
                Ex02_PolicyPlacement.Q2_VALIDATION_LOCATION, HINT_Q2);
    }

    @Test
    @DisplayName("Q2: validator từ chối đơn không có mặt hàng")
    void q02_validatorRejectsEmptyItems() {
        assertFalse(VALIDATOR.validate(0, 1_000).valid());
        assertEquals("Đơn phải có ít nhất một mặt hàng.", VALIDATOR.validate(0, 1_000).message());
        assertEquals("ok", VALIDATOR.validate(1, 1_000).message());
    }

    @Test
    @DisplayName("Q4 dự đoán: entity có phụ thuộc HTTP request")
    void q04_prediction() {
        assertPrediction("Q4_ENTITY_DEPENDS_ON_REQUEST", false,
                Ex02_PolicyPlacement.Q4_ENTITY_DEPENDS_ON_REQUEST, HINT_Q4);
    }
}
