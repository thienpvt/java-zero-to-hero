package phase01.d01_generics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase01.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.support.Compiles;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex03_GenericMethodVsWildcardTest {

    private static final String HINT_Q8 =
            "Với List<?> list, compiler không biết ? là kiểu gì nên chỉ add(null) được, "
                    + "không add(\"x\") được; muốn set() an toàn kiểu phải capture wildcard qua "
                    + "một helper method generic như swapHelper.";

    @Test
    @DisplayName("Q8 dự đoán: List<?> có add String hay add null được không?")
    void q08_prediction() {
        assertPrediction("Q8_ADD_STRING_TO_WILDCARD_LIST_COMPILES",
                Compiles.NO, Ex03_GenericMethodVsWildcard.Q8_ADD_STRING_TO_WILDCARD_LIST_COMPILES, HINT_Q8);
        assertPrediction("Q8_ADD_NULL_TO_WILDCARD_LIST_COMPILES",
                Compiles.YES, Ex03_GenericMethodVsWildcard.Q8_ADD_NULL_TO_WILDCARD_LIST_COMPILES, HINT_Q8);
    }

    @Test
    @DisplayName("Q8 swapFirstLast: đổi chỗ phần tử đầu và cuối")
    void q08_swapFirstLast_swapsEnds() {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
        Ex03_GenericMethodVsWildcard.swapFirstLast(list);
        assertEquals(List.of(3, 2, 1), list);
    }

    @Test
    @DisplayName("Q8 swapFirstLast: danh sách 1 phần tử giữ nguyên")
    void q08_swapFirstLast_singleElementUnchanged() {
        List<String> list = new ArrayList<>(List.of("a"));
        Ex03_GenericMethodVsWildcard.swapFirstLast(list);
        assertEquals(List.of("a"), list);
    }

    @Test
    @DisplayName("Q8 swapFirstLast: danh sách rỗng giữ nguyên")
    void q08_swapFirstLast_emptyUnchanged() {
        List<Object> list = new ArrayList<>();
        Ex03_GenericMethodVsWildcard.swapFirstLast(list);
        assertEquals(List.of(), list);
    }
}
