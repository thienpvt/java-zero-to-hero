package phase03.d13_adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ProviderAdaptersTest {

    private static final String HINT_Q1 =
            "Adapter chuyển interface hệ ngoài về interface mà application mong muốn.";
    private static final String HINT_Q2 =
            "Adapter đổi interface; decorator giữ nguyên interface và thêm hành vi.";
    private static final String HINT_Q3 =
            "Adapter là chỗ duy nhất biết SDK, nên đổi SDK chỉ sửa một chỗ.";
    private static final String HINT_Q4 =
            "Có interface của application thì test thay bằng fake, không cần SDK hay mạng.";

    @Test
    @DisplayName("Q1 dự đoán: Adapter giải quyết vấn đề gì")
    void q01_prediction() {
        assertPrediction("Q1_ADAPTER_PROBLEM",
                Ex01_ProviderAdapters.ProblemType.EXTERNAL_INTERFACE_DIFFERS_FROM_APP_INTERFACE,
                Ex01_ProviderAdapters.Q1_ADAPTER_PROBLEM, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: Adapter khác Decorator thế nào")
    void q02_prediction() {
        assertPrediction("Q2_ADAPTER_VS_DECORATOR",
                Ex01_ProviderAdapters.Difference.ADAPTER_CHANGES_INTERFACE_DECORATOR_KEEPS_IT,
                Ex01_ProviderAdapters.Q2_ADAPTER_VS_DECORATOR, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: bọc SDK có lợi")
    void q03_prediction() {
        assertPrediction("Q3_WRAP_SDK_BENEFIT", true,
                Ex01_ProviderAdapters.Q3_WRAP_SDK_BENEFIT, HINT_Q3);
    }

    @Test
    @DisplayName("Q4 dự đoán: adapter giúp testing")
    void q04_prediction() {
        assertPrediction("Q4_ADAPTER_HELPS_TESTING", true,
                Ex01_ProviderAdapters.Q4_ADAPTER_HELPS_TESTING, HINT_Q4);
    }

    @Test
    @DisplayName("Q4: adapter dịch lời gọi sang SDK")
    void q04_adapterTranslatesCall() {
        Ex01_ProviderAdapters.LegacyStripeSdk sdk = new Ex01_ProviderAdapters.LegacyStripeSdk();
        Ex01_ProviderAdapters.ProviderGateway gateway = new Ex01_ProviderAdapters.StripeAdapter(sdk);
        assertEquals("stripe:4000", gateway.charge(4_000));
        assertEquals(4_000, sdk.lastCharge());

        sdk.setOk(false);
        assertThrows(IllegalStateException.class, () -> gateway.charge(4_000));
    }
}
