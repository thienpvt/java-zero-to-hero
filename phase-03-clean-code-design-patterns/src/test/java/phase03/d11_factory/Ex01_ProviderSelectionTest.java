package phase03.d11_factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ProviderSelectionTest {

    private static final String HINT_Q1 =
            "Factory gom chỗ quyết định lớp cụ thể vào một nơi, phần còn lại chỉ thấy interface.";
    private static final String HINT_Q4 =
            "Dữ liệu lúc chạy là đầu vào để factory quyết định trả implementation nào.";
    private static final String HINT_Q5 =
            "Gom việc tạo object không làm nó mở rộng được nếu vẫn phải sửa switch cho mỗi provider mới.";

    @Test
    @DisplayName("Q1 dự đoán: Factory giải quyết vấn đề gì")
    void q01_prediction() {
        assertPrediction("Q1_FACTORY_PROBLEM",
                Ex01_ProviderSelection.Problem.CENTRALIZE_WHICH_CONCRETE_CLASS_TO_CREATE,
                Ex01_ProviderSelection.Q1_FACTORY_PROBLEM, HINT_Q1);
    }

    @Test
    @DisplayName("Q4 dự đoán: factory chọn theo dữ liệu runtime")
    void q04_prediction() {
        assertPrediction("Q4_RUNTIME_DATA_SELECTION", true,
                Ex01_ProviderSelection.Q4_RUNTIME_DATA_SELECTION, HINT_Q4);
    }

    @Test
    @DisplayName("Q4: create trả đúng provider")
    void q04_createReturnsRightProvider() {
        assertEquals("stripe:250", Ex01_ProviderSelection.ProviderFactory.create("stripe").charge(250));
        assertEquals("momo:250", Ex01_ProviderSelection.ProviderFactory.create("momo").charge(250));
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_ProviderSelection.ProviderFactory.create("paypal"));
    }

    @Test
    @DisplayName("Q5 dự đoán: switch khổng lồ còn extensible")
    void q05_prediction() {
        assertPrediction("Q5_GIANT_SWITCH_EXTENSIBLE", false,
                Ex01_ProviderSelection.Q5_GIANT_SWITCH_EXTENSIBLE, HINT_Q5);
    }
}
