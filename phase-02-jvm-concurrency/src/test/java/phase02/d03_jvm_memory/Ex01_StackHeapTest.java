package phase02.d03_jvm_memory;

import static phase02.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase02.d03_jvm_memory.Ex01_StackHeap.Store;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_StackHeapTest {

    private static final String HINT_Q1 =
            "Object tạo bằng new nằm trên heap; frame stack chỉ giữ reference tới object đó.";

    private static final String HINT_Q2 =
            "Biến primitive khai báo trong method nằm trong frame stack của lần gọi đó.";

    private static final String HINT_Q3 =
            "Reference có thể ở stack (biến local) hoặc trong field trên heap; object thì ở heap.";

    private static final String HINT_Q4 =
            "Mỗi thread có một stack riêng. Biến local của thread này không nằm trên stack thread kia.";

    private static final String HINT_Q5 =
            "Heap là vùng dùng chung: nhiều thread có thể cùng giữ reference tới một object.";

    @Test
    @DisplayName("Q1 dự đoán: object Java thường nằm ở vùng nhớ nào")
    void q01_objectStore() {
        assertPrediction("Q1_OBJECT", Store.HEAP, Ex01_StackHeap.Q1_OBJECT, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: local primitive nằm ở vùng nhớ nào")
    void q02_localPrimitive() {
        assertPrediction("Q2_LOCAL_PRIMITIVE", Store.STACK, Ex01_StackHeap.Q2_LOCAL_PRIMITIVE, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: reference và object có bắt buộc cùng một nơi không")
    void q03_referenceAndObject() {
        assertPrediction("Q3_SAME_PLACE", false, Ex01_StackHeap.Q3_SAME_PLACE, HINT_Q3);
    }

    @Test
    @DisplayName("Q4 dự đoán: stack có được dùng chung giữa các thread không")
    void q04_stackShared() {
        assertPrediction("Q4_STACK_SHARED", false, Ex01_StackHeap.Q4_STACK_SHARED, HINT_Q4);
    }

    @Test
    @DisplayName("Q5 dự đoán: heap có được dùng chung không")
    void q05_heapShared() {
        assertPrediction("Q5_HEAP_SHARED", true, Ex01_StackHeap.Q5_HEAP_SHARED, HINT_Q5);
    }
}
