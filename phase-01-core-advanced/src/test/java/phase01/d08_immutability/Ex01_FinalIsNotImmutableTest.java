package phase01.d08_immutability;

import static phase01.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.support.Compiles;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_FinalIsNotImmutableTest {

    private static final String HINT_Q1 =
            "final trên class chỉ cấm kế thừa (extends); field bên trong instance vẫn đổi được bình thường.";

    private static final String HINT_Q2 =
            "final trên biến chỉ khoá tham chiếu (không gán lại được), không khoá nội dung object trỏ tới; "
                    + "List.of(...) mới là immutable thật, còn Collections.unmodifiableList chỉ là view.";

    @Test
    @DisplayName("Q1 dự đoán: final class có ngăn field bên trong đổi giá trị không?")
    void q01_prediction() {
        Ex01_FinalIsNotImmutable.Counter counter = new Ex01_FinalIsNotImmutable.Counter();
        int before = counter.value();
        counter.increment();
        int after = counter.value();

        assertPrediction("Q1_FINAL_CLASS_STATE_CHANGED",
                before != after, Ex01_FinalIsNotImmutable.Q1_FINAL_CLASS_STATE_CHANGED, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: final List<String> có thật sự immutable không?")
    void q02_prediction() {
        final List<String> list = new ArrayList<>();
        boolean added = list.add("a");
        assertPrediction("Q2_CAN_ADD_TO_FINAL_LIST",
                added, Ex01_FinalIsNotImmutable.Q2_CAN_ADD_TO_FINAL_LIST, HINT_Q2);

        assertPrediction("Q2_REASSIGN_FINAL_LIST_COMPILES",
                Compiles.NO, Ex01_FinalIsNotImmutable.Q2_REASSIGN_FINAL_LIST_COMPILES, HINT_Q2);

        String actualException = "(không ném ngoại lệ)";
        try {
            List.of("a").add("b");
        } catch (RuntimeException e) {
            actualException = e.getClass().getSimpleName();
        }
        assertPrediction("Q2_LIST_OF_ADD_EXCEPTION",
                actualException, Ex01_FinalIsNotImmutable.Q2_LIST_OF_ADD_EXCEPTION, HINT_Q2);

        List<String> backing = new ArrayList<>(List.of("a"));
        List<String> view = Collections.unmodifiableList(backing);
        backing.add("b");
        assertPrediction("Q2_UNMODIFIABLE_VIEW_SEES_BACKING_CHANGE",
                view.contains("b"), Ex01_FinalIsNotImmutable.Q2_UNMODIFIABLE_VIEW_SEES_BACKING_CHANGE, HINT_Q2);
    }
}
