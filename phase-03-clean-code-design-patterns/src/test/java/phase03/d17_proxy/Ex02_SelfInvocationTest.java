package phase03.d17_proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_SelfInvocationTest {

    private static final String HINT_Q3 =
            "this.inner() gọi thẳng trên object đích, không đi qua proxy nên AOP không áp dụng.";
    private static final String HINT_Q4 =
            "Proxy lazy giữ dữ liệu tối thiểu và truy vấn thêm khi có lời gọi tới field chưa nạp.";

    @Test
    @DisplayName("Q3 dự đoán: vì sao self-invocation không qua proxy")
    void q03_prediction() {
        assertPrediction("Q3_SELF_INVOCATION",
                Ex02_SelfInvocation.Reason.CALL_GOES_DIRECTLY_ON_THE_TARGET_OBJECT,
                Ex02_SelfInvocation.Q3_SELF_INVOCATION, HINT_Q3);
    }

    @Test
    @DisplayName("Q3: lời gọi nội bộ chạy thẳng trên object")
    void q03_selfInvocationBypassesProxy() {
        Ex02_SelfInvocation.SelfInvoking target = new Ex02_SelfInvocation.SelfInvoking();
        assertEquals("outer->inner", target.outer());
        assertEquals(1, target.calls.size());
    }

    @Test
    @DisplayName("Q4 dự đoán: lazy-loading proxy hoạt động thế nào")
    void q04_prediction() {
        assertPrediction("Q4_LAZY_PROXY",
                Ex02_SelfInvocation.Idea.PROXY_HOLDS_MINIMAL_DATA_AND_LOADS_ON_CALL,
                Ex02_SelfInvocation.Q4_LAZY_PROXY, HINT_Q4);
    }
}
