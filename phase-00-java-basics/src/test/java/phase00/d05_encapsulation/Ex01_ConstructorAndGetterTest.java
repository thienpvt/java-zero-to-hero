package phase00.d05_encapsulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase00.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase00.support.Compiles;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ConstructorAndGetterTest {

    private static final String HINT_Q1 =
            "Ctrl+F12 trên OnlyArg: constructor không có kiểu trả về. Bỏ comment new OnlyArg() trong test.";

    private static final String HINT_Q2 =
            "Bỏ comment Box.read(). Static không có instance để lấy field value.";

    private static final String HINT_Q4 =
            "Debug LeakyBag: view.add làm size nội bộ đổi. Ctrl+Q trên List.copyOf.";

    @Test
    @DisplayName("Q1 dự đoán: constructor không có kiểu trả về; constructor mặc định biến mất khi đã khai báo")
    void q01_prediction() {
        assertPrediction("Q1_CONSTRUCTOR_HAS_RETURN_TYPE",
                false, Ex01_ConstructorAndGetter.Q1_CONSTRUCTOR_HAS_RETURN_TYPE, HINT_Q1);
        Ex01_ConstructorAndGetter.NoCtor created = new Ex01_ConstructorAndGetter.NoCtor();
        assertPrediction("Q1_NO_CTOR_CAN_BE_NEW",
                created != null, Ex01_ConstructorAndGetter.Q1_NO_CTOR_CAN_BE_NEW, HINT_Q1);
        // new Ex01_ConstructorAndGetter.OnlyArg();
        assertPrediction("Q1_DEFAULT_CTOR_AFTER_CUSTOM",
                Compiles.NO, Ex01_ConstructorAndGetter.Q1_DEFAULT_CTOR_AFTER_CUSTOM, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: static method không đọc trực tiếp instance field")
    void q02_prediction() {
        assertPrediction("Q2_STATIC_READS_INSTANCE_FIELD",
                Compiles.NO, Ex01_ConstructorAndGetter.Q2_STATIC_READS_INSTANCE_FIELD, HINT_Q2);
    }

    @Test
    @DisplayName("Q4 dự đoán: getter trả list nội bộ để caller sửa được state")
    void q04_leakyGetter() {
        Ex01_ConstructorAndGetter.LeakyBag bag =
                new Ex01_ConstructorAndGetter.LeakyBag(new ArrayList<>(List.of("a")));
        List<String> view = bag.tags();
        int before = bag.size();
        view.add("b");
        assertPrediction("Q4_LEAKY_GETTER_SEES_CALLER_ADD",
                bag.size() > before, Ex01_ConstructorAndGetter.Q4_LEAKY_GETTER_SEES_CALLER_ADD, HINT_Q4);
    }

    @Test
    @DisplayName("Q4 code: Bag.tags() không cho caller sửa list nội bộ")
    void q04_defensiveGetter() {
        Ex01_ConstructorAndGetter.Bag bag =
                new Ex01_ConstructorAndGetter.Bag(new ArrayList<>(List.of("a")));
        List<String> view = bag.tags();
        assertThrows(UnsupportedOperationException.class, () -> view.add("b"));
        assertEquals(1, bag.size());
    }
}
