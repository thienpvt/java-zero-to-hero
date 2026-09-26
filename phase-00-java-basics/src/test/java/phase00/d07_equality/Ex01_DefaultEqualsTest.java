package phase00.d07_equality;

import static phase00.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_DefaultEqualsTest {

    private static final String HINT_Q1 =
            "Ctrl+Q trên Object.equals. Alt+F8: new Customer(\"A\").equals(new Customer(\"A\")).";

    private static final String HINT_Q3 =
            "Alt+F8 in toString() của Customer. Ctrl+Q trên Object.toString.";

    @Test
    @DisplayName("Q1 dự đoán: equals mặc định không coi hai Customer cùng tên là bằng nhau")
    void q01_prediction() {
        Ex01_DefaultEquals.Customer left = new Ex01_DefaultEquals.Customer("A");
        Ex01_DefaultEquals.Customer right = new Ex01_DefaultEquals.Customer("A");
        assertPrediction("Q1_DEFAULT_EQUALS",
                left.equals(right), Ex01_DefaultEquals.Q1_DEFAULT_EQUALS, HINT_Q1);
    }

    @Test
    @DisplayName("Q3 dự đoán: toString mặc định không chứa tên khách")
    void q03_prediction() {
        String text = new Ex01_DefaultEquals.Customer("A").toString();
        assertPrediction("Q3_DEFAULT_TOSTRING_CONTAINS_NAME",
                text.contains("A"), Ex01_DefaultEquals.Q3_DEFAULT_TOSTRING_CONTAINS_NAME, HINT_Q3);
    }
}
