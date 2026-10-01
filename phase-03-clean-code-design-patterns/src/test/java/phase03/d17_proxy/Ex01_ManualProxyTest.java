package phase03.d17_proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ManualProxyTest {

    private static final String HINT_Q1 =
            "Proxy quyết định có cho lời gọi tới đích hay không; decorator luôn chuyển tiếp.";
    private static final String HINT_Q2 =
            "Spring transaction hoạt động bằng proxy bọc quanh bean; transaction chỉ mở khi lời gọi đi qua proxy.";
    private static final String HINT_Q5 =
            "Security, transaction, lazy loading, logging và remote call là các concern đi quanh nhiều method.";

    @Test
    @DisplayName("Q1 dự đoán: proxy khác decorator ở đâu")
    void q01_prediction() {
        assertPrediction("Q1_PROXY_VS_DECORATOR",
                Ex01_ManualProxy.Difference.CONTROLS_ACCESS_VS_ADDS_BEHAVIOR,
                Ex01_ManualProxy.Q1_PROXY_VS_DECORATOR, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: @Transactional liên quan proxy thế nào")
    void q02_prediction() {
        assertPrediction("Q2_TRANSACTIONAL_PROXY", Ex01_ManualProxy.Mechanism.PROXY_AROUND_THE_BEAN,
                Ex01_ManualProxy.Q2_TRANSACTIONAL_PROXY, HINT_Q2);
    }

    @Test
    @DisplayName("Q3: admin đi qua, vai trò khác bị chặn và không chạm đích")
    void q03_adminPassesAndOthersBlocked() {
        Ex01_ManualProxy.RealAccount real = new Ex01_ManualProxy.RealAccount();
        Ex01_ManualProxy.Account adminProxy = new Ex01_ManualProxy.GuardProxy(real, "admin");
        adminProxy.deposit(1_000);
        assertEquals("real:1000", adminProxy.balance());

        Ex01_ManualProxy.RealAccount blocked = new Ex01_ManualProxy.RealAccount();
        Ex01_ManualProxy.Account guestProxy = new Ex01_ManualProxy.GuardProxy(blocked, "guest");
        assertThrows(SecurityException.class, () -> guestProxy.deposit(1_000));
        assertTrue(blocked.audit.isEmpty(), "Proxy chặn thì đích không được gọi.");
    }

    @Test
    @DisplayName("Q5 dự đoán: proxy hay thêm concern nào")
    void q05_prediction() {
        assertPrediction("Q5_CROSS_CUTTING",
                Ex01_ManualProxy.Concern.SECURITY_TRANSACTION_LAZY_LOADING,
                Ex01_ManualProxy.Q5_CROSS_CUTTING, HINT_Q5);
    }
}
