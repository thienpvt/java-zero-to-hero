package phase03.d12_builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase03.support.Predictions.assertPrediction;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_UserBuilderTest {

    private static final String HINT_Q1 =
            "Builder cho mỗi tham số một method có tên, nên chỗ gọi không phải nhớ thứ tự.";
    private static final String HINT_Q2 =
            "Builder ráp field của một lớp; factory chọn lớp nào trong nhiều lớp.";
    private static final String HINT_Q3 =
            "Object tạo xong có field final và không setter thì không sửa được nữa.";
    private static final String HINT_Q5 =
            "Mọi object đều đi qua constructor, nên đó là chốt cuối cho validation.";

    @Test
    @DisplayName("Q1 dự đoán: builder khắc phục telescoping thế nào")
    void q01_prediction() {
        assertPrediction("Q1_TELESCOPING_SOLVED",
                Ex01_UserBuilder.Fix.NAMED_STEPS_FOR_OPTIONAL_PARAMETERS,
                Ex01_UserBuilder.Q1_TELESCOPING_SOLVED, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: Builder khác Factory thế nào")
    void q02_prediction() {
        assertPrediction("Q2_BUILDER_VS_FACTORY",
                Ex01_UserBuilder.Difference.BUILDER_ASSEMBLES_FIELDS_FACTORY_CHOOSES_CLASS,
                Ex01_UserBuilder.Q2_BUILDER_VS_FACTORY, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: builder giúp object immutable")
    void q03_prediction() {
        assertPrediction("Q3_IMMUTABLE", true, Ex01_UserBuilder.Q3_IMMUTABLE, HINT_Q3);
    }

    @Test
    @DisplayName("Q3: build tạo object với mọi field final")
    void q03_buildIsImmutable() throws NoSuchFieldException {
        Ex01_UserBuilder.User user = Ex01_UserBuilder.User.builder()
                .name("Alice")
                .email("alice@example.com")
                .age(30)
                .build();
        assertEquals("Alice", user.name());
        assertEquals("alice@example.com", user.email());
        assertEquals(30, user.age());
        for (Field field : Ex01_UserBuilder.User.class.getDeclaredFields()) {
            if (!field.isSynthetic() && !Modifier.isStatic(field.getModifiers())) {
                assertTrue(Modifier.isFinal(field.getModifiers()),
                        "Field " + field.getName() + " phải là final.");
            }
        }
    }

    @Test
    @DisplayName("Q5 dự đoán: validation ở builder hay constructor")
    void q05_prediction() {
        assertPrediction("Q5_VALIDATION_LOCATION", Ex01_UserBuilder.Location.OBJECT_CONSTRUCTOR,
                Ex01_UserBuilder.Q5_VALIDATION_LOCATION, HINT_Q5);
    }

    @Test
    @DisplayName("Q5: build từ chối email sai và tên trống")
    void q05_buildRejectsBadEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_UserBuilder.User.builder().name("Bob").email("bob").age(1).build());
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_UserBuilder.User.builder().name(" ").email("b@x").age(1).build());
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_UserBuilder.User.builder().name("Bob").email("b@x").age(-1).build());
    }
}
