package phase00.d07_equality;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase00.support.Predictions.assertPrediction;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_ProductCodeTest {

    private static final String HINT_Q2 =
            "Debug HashSet.add lần hai với NameOnly. Ctrl+Q trên Object.hashCode: equals true thì hashCode phải trùng.";

    @Test
    @DisplayName("Q2 dự đoán: equals mà quên hashCode thì HashSet không gộp")
    void q02_prediction() {
        Set<Ex02_ProductCode.NameOnly> names = new HashSet<>();
        names.add(new Ex02_ProductCode.NameOnly("AB"));
        names.add(new Ex02_ProductCode.NameOnly("AB"));
        assertPrediction("Q2_SET_SIZE", names.size(), Ex02_ProductCode.Q2_SET_SIZE, HINT_Q2);
        assertPrediction("Q2_CONTAINS_EQUAL_INSTANCE",
                names.contains(new Ex02_ProductCode.NameOnly("AB")),
                Ex02_ProductCode.Q2_CONTAINS_EQUAL_INSTANCE,
                HINT_Q2);
    }

    @Test
    @DisplayName("Q2 code: hai ProductCode cùng value chỉ chiếm một chỗ trong HashSet")
    void q02_productCodeCollapsesEqualValues() {
        Set<Ex02_ProductCode.ProductCode> codes = new HashSet<>();
        codes.add(new Ex02_ProductCode.ProductCode("AB"));
        codes.add(new Ex02_ProductCode.ProductCode("AB"));
        assertEquals(1, codes.size());
        assertTrue(codes.contains(new Ex02_ProductCode.ProductCode("AB")));
    }
}
