package phase02.d04_gc;

import static phase02.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ReachabilityTest {

    @Test
    @DisplayName("Q2 dự đoán: GC có chạy ngay khi object mất reference không")
    void q02_prediction() {
        assertPrediction("Q2_GC_RUNS_IMMEDIATELY", false,
                Ex01_Reachability.Q2_GC_RUNS_IMMEDIATELY,
                "Mất reference chỉ làm object đủ điều kiện bị thu. JVM không hứa chạy GC ngay lúc đó.");
    }

    @Test
    @DisplayName("Q4 dự đoán: chu trình tham chiếu có leak như reference counting không")
    void q04_prediction() {
        assertPrediction("Q4_CYCLE_LEAKS_LIKE_REFCOUNT", false,
                Ex01_Reachability.Q4_CYCLE_LEAKS_LIKE_REFCOUNT,
                "JVM dò từ GC root, không đếm reference. Chu trình không còn nối với root vẫn có thể bị thu.");
    }

    @Test
    @DisplayName("Q10 dự đoán: System.gc() có bảo đảm collection không")
    void q10_prediction() {
        assertPrediction("Q10_SYSTEM_GC_GUARANTEES_COLLECTION", false,
                Ex01_Reachability.Q10_SYSTEM_GC_GUARANTEES_COLLECTION,
                "System.gc() chỉ là đề nghị. Đặc tả không bảo đảm collection sẽ chạy hay một object cụ thể sẽ bị thu.");
    }
}
