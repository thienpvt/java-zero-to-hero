package phase00.d06_polymorphism;

import static phase00.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase00.support.Compiles;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_DispatchTest {

    private static final String HINT_Q1 =
            "Gán Dog và Cat vào biến Speaker. Bỏ comment new Speaker() để thấy lỗi đỏ.";

    private static final String HINT_Q2 =
            "F7 Step Into animal.speak() khi animal trỏ tới DogAnimal.";

    private static final String HINT_Q3 =
            "Dán mẫu 'class Child extends Animal, DogAnimal {}' vào file tạm và đọc lỗi đỏ.";

    @Test
    @DisplayName("Q1 dự đoán: biến interface trỏ được mọi implementation, không new được interface")
    void q01_prediction() {
        Ex01_Dispatch.Speaker dog = new Ex01_Dispatch.Dog();
        Ex01_Dispatch.Speaker cat = new Ex01_Dispatch.Cat();
        // new Ex01_Dispatch.Speaker();
        boolean bothWork = dog.speak() != null && cat.speak() != null;
        assertPrediction("Q1_CAN_POINT_AT_DOG_AND_CAT",
                bothWork, Ex01_Dispatch.Q1_CAN_POINT_AT_DOG_AND_CAT, HINT_Q1);
        assertPrediction("Q1_NEW_INTERFACE",
                Compiles.NO, Ex01_Dispatch.Q1_NEW_INTERFACE, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: gọi qua tham chiếu cha vẫn chạy bản override")
    void q02_prediction() {
        Ex01_Dispatch.Animal animal = new Ex01_Dispatch.DogAnimal();
        assertPrediction("Q2_SPEAK_THROUGH_ANIMAL_REF",
                animal.speak(), Ex01_Dispatch.Q2_SPEAK_THROUGH_ANIMAL_REF, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: một class không extends hai class")
    void q03_prediction() {
        assertPrediction("Q3_CLASS_EXTENDS_TWO_CLASSES",
                Compiles.NO, Ex01_Dispatch.Q3_CLASS_EXTENDS_TWO_CLASSES, HINT_Q3);
    }
}
