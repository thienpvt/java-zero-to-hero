package phase03.d18_singleton;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_EnumSingletonAndStateTest {

    private static final String HINT_Q1 =
            "Singleton bảo đảm một instance dùng chung; nếu chỉ cần chia sẻ thì truyền qua constructor rõ hơn.";
    private static final String HINT_Q2 =
            "Biến ẩn mà mọi chỗ đọc ghi được chính là global state.";
    private static final String HINT_Q3 =
            "Không có điểm chèn fake, và trạng thái rò giữa các test.";
    private static final String HINT_Q4 =
            "Lazy-init không đồng bộ có thể để hai thread cùng tạo instance.";
    private static final String HINT_Q7 =
            "Enum chỉ có một hằng, serialization trả cùng hằng, reflection không gọi được constructor enum.";

    @AfterEach
    void clearHolder() {
        Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE.clear();
    }

    @Test
    @DisplayName("Q1 dự đoán: Singleton giải quyết vấn đề gì")
    void q01_prediction() {
        assertPrediction("Q1_SINGLETON_PROBLEM",
                Ex01_EnumSingletonAndState.Problem.SINGLE_SHARED_INSTANCE,
                Ex01_EnumSingletonAndState.Q1_SINGLETON_PROBLEM, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: Singleton là global state")
    void q02_prediction() {
        assertPrediction("Q2_SINGLETON_GLOBAL_STATE", true,
                Ex01_EnumSingletonAndState.Q2_SINGLETON_GLOBAL_STATE, HINT_Q2);
    }

    @Test
    @DisplayName("Q2: trạng thái rò từ lời gọi này sang lời gọi khác")
    void q02_stateLeaksBetweenCalls() {
        Ex01_EnumSingletonAndState.ConfigHolder holder =
                Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE;
        holder.put("region", "ap-southeast-1");
        assertEquals("ap-southeast-1",
                Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE.get("region"),
                "Instance khác phải thấy cùng trạng thái — đó là global state.");
    }

    @Test
    @DisplayName("Q3 dự đoán: Singleton khó testing")
    void q03_prediction() {
        assertPrediction("Q3_SINGLETON_TESTING_PAIN", true,
                Ex01_EnumSingletonAndState.Q3_SINGLETON_TESTING_PAIN, HINT_Q3);
    }

    @Test
    @DisplayName("Q4 dự đoán: Singleton mặc định thread-safe")
    void q04_prediction() {
        assertPrediction("Q4_SINGLETON_THREAD_SAFE", false,
                Ex01_EnumSingletonAndState.Q4_SINGLETON_THREAD_SAFE, HINT_Q4);
    }

    @Test
    @DisplayName("Q7: enum singleton chỉ một instance")
    void q07_enumSingletonSingleInstance() {
        assertSame(Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE,
                Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE);
        assertEquals(1, Ex01_EnumSingletonAndState.ConfigHolder.values().length);
    }

    @Test
    @DisplayName("Q7: trạng thái dùng chung giữa hai lời gọi")
    void q07_sharedStateVisible() {
        Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE.put("k", "v1");
        Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE.put("k", "v2");
        assertEquals("v2", Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE.get("k"));
        Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE.clear();
        assertEquals(null, Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE.get("k"));
    }
}
