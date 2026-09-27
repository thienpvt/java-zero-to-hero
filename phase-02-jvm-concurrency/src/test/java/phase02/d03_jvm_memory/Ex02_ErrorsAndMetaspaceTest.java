package phase02.d03_jvm_memory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase02.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase02.d03_jvm_memory.Ex02_ErrorsAndMetaspace.MetaspaceHolds;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_ErrorsAndMetaspaceTest {

    private static final String HINT_Q8 =
            "Metaspace giữ class metadata (cấu trúc class, method, constant pool), không giữ instance.";

    private static final String HINT_Q9 =
            "GC chỉ thu object không còn reachable. Reference còn bị giữ (cache, listener, static) thì object vẫn sống.";

    @Test
    @DisplayName("Q6: đệ quy không đáy ném StackOverflowError")
    void q06_recurseThrowsStackOverflowError() {
        assertThrows(StackOverflowError.class, Ex02_ErrorsAndMetaspace::recurse,
                "recurse() phải làm đầy stack của thread hiện tại.");
    }

    @Test
    @DisplayName("Q8 dự đoán: Metaspace chứa gì")
    void q08_metaspaceHolds() {
        assertPrediction("Q8_METASPACE_HOLDS",
                MetaspaceHolds.CLASS_METADATA, Ex02_ErrorsAndMetaspace.Q8_METASPACE_HOLDS, HINT_Q8);
    }

    @Test
    @DisplayName("Q9 dự đoán: memory leak có thể xảy ra dù có GC không")
    void q09_leakPossibleWithGc() {
        assertPrediction("Q9_LEAK_POSSIBLE_WITH_GC",
                true, Ex02_ErrorsAndMetaspace.Q9_LEAK_POSSIBLE_WITH_GC, HINT_Q9);
    }
}
