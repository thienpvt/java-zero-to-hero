package phase01.d01_generics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase01.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.support.Compiles;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_InvarianceAndWildcardsTest {

    private static final String HINT_Q1 =
            "Bỏ comment từng dòng mẫu cạnh mỗi hằng Compiles trong Ex01 để xem dòng nào đỏ lúc biên dịch; "
                    + "mảng covariant lỗi lúc chạy (ArrayStoreException), generic invariant lỗi lúc biên dịch.";

    @Test
    @DisplayName("Q1 dự đoán: List<String> có là subtype của List<Object>? Mảng covariant ném lỗi gì?")
    void q01_prediction() {
        assertPrediction("Q1_LIST_STRING_TO_LIST_OBJECT_COMPILES",
                Compiles.NO, Ex01_InvarianceAndWildcards.Q1_LIST_STRING_TO_LIST_OBJECT_COMPILES, HINT_Q1);
        assertPrediction("Q1_STRING_ARRAY_TO_OBJECT_ARRAY_COMPILES",
                Compiles.YES, Ex01_InvarianceAndWildcards.Q1_STRING_ARRAY_TO_OBJECT_ARRAY_COMPILES, HINT_Q1);

        Object[] arr = new String[1];
        String actualException = "(không ném ngoại lệ)";
        try {
            arr[0] = 1;
        } catch (RuntimeException e) {
            actualException = e.getClass().getSimpleName();
        }
        assertPrediction("Q1_ARRAY_STORE_EXCEPTION",
                actualException, Ex01_InvarianceAndWildcards.Q1_ARRAY_STORE_EXCEPTION, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 sum: cộng doubleValue của List<Integer>")
    void q02_sum_intsSumToSix() {
        assertEquals(6.0, Ex01_InvarianceAndWildcards.sum(List.of(1, 2, 3)));
    }

    @Test
    @DisplayName("Q2 sum: cộng doubleValue của List<Double>")
    void q02_sum_doublesSumToFour() {
        assertEquals(4.0, Ex01_InvarianceAndWildcards.sum(List.of(1.5, 2.5)));
    }

    @Test
    @DisplayName("Q2 sum: collection rỗng trả về 0.0")
    void q02_sum_emptyReturnsZero() {
        assertEquals(0.0, Ex01_InvarianceAndWildcards.sum(List.<Number>of()));
    }

    @Test
    @DisplayName("Q3 addNumbers: thêm 0,1,2 vào List<Number>")
    void q03_addNumbers_intoListOfNumber() {
        List<Number> target = new ArrayList<>();
        Ex01_InvarianceAndWildcards.addNumbers(target, 3);
        assertEquals(List.of(0, 1, 2), target);
    }

    @Test
    @DisplayName("Q3 addNumbers: thêm 0,1,2 vào List<Object>")
    void q03_addNumbers_intoListOfObject() {
        List<Object> target = new ArrayList<>();
        Ex01_InvarianceAndWildcards.addNumbers(target, 3);
        assertEquals(List.of(0, 1, 2), target);
    }

    @Test
    @DisplayName("Q3 addNumbers: count âm ném IllegalArgumentException")
    void q03_addNumbers_negativeCountThrows() {
        List<Number> target = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> Ex01_InvarianceAndWildcards.addNumbers(target, -1));
    }

    @Test
    @DisplayName("Q4 copy: nối List<Integer> vào cuối List<Number> theo PECS")
    void q04_copy_appendsSrcOntoDst() {
        List<Integer> src = new ArrayList<>(List.of(1, 2));
        List<Number> dst = new ArrayList<>(List.of(0.5));
        Ex01_InvarianceAndWildcards.copy(dst, src);
        assertEquals(List.of(0.5, 1, 2), dst);
    }

    @Test
    @DisplayName("Q4 max: trả phần tử lớn nhất theo Comparator")
    void q04_max_returnsLargestByComparator() {
        Comparator<Number> byDoubleValue = Comparator.comparingDouble(Number::doubleValue);
        Integer result = Ex01_InvarianceAndWildcards.max(List.of(3, 7, 5), byDoubleValue);
        assertEquals(7, result.intValue());
    }

    @Test
    @DisplayName("Q4 max: collection rỗng ném NoSuchElementException")
    void q04_max_emptyThrowsNoSuchElementException() {
        List<Integer> empty = List.of();
        Comparator<Number> byDoubleValue = Comparator.comparingDouble(Number::doubleValue);
        assertThrows(NoSuchElementException.class, () -> Ex01_InvarianceAndWildcards.max(empty, byDoubleValue));
    }
}
