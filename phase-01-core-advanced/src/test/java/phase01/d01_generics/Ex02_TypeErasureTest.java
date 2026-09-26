package phase01.d01_generics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase01.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.support.Compiles;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_TypeErasureTest {

    private static final String HINT_Q5 =
            "new T() không được vì JVM không biết T là gì lúc chạy (type erasure); "
                    + "dùng Supplier<T> hoặc Class<T>.getDeclaredConstructor().newInstance() thay thế.";
    private static final String HINT_Q6 =
            "Chạy javap -c -p trên Ex02_TypeErasure.class (Alt+F12) để thấy cả hai ArrayList "
                    + "chỉ còn lại một kiểu bytecode ArrayList sau khi erasure.";
    private static final String HINT_Q10 =
            "raw.add(42) không kiểm tra kiểu vì raw type bỏ qua generic; checkcast ẩn chỉ chèn ở get().";
    private static final String HINT_Q7 =
            "List<String> không tồn tại như một kiểu runtime riêng sau erasure nên JVM không "
                    + "thể kiểm tra instanceof List<String> — chỉ còn kiểm tra được List hoặc List<?>.";

    @Test
    @DisplayName("Q5 dự đoán: new T() trong generic method có biên dịch được không?")
    void q05_prediction() {
        assertPrediction("Q5_NEW_T_COMPILES", Compiles.NO, Ex02_TypeErasure.Q5_NEW_T_COMPILES, HINT_Q5);
    }

    @Test
    @DisplayName("Q5 createN: tạo đúng n instance khác nhau qua Supplier")
    void q05_createN_createsDistinctInstances() {
        List<StringBuilder> result = Ex02_TypeErasure.createN(StringBuilder::new, 3);
        assertEquals(3, result.size());
        assertNotSame(result.get(0), result.get(1));
        assertNotSame(result.get(1), result.get(2));
        assertNotSame(result.get(0), result.get(2));
    }

    @Test
    @DisplayName("Q5 createN: n âm ném IllegalArgumentException")
    void q05_createN_negativeNThrows() {
        assertThrows(IllegalArgumentException.class, () -> Ex02_TypeErasure.createN(StringBuilder::new, -1));
    }

    @Test
    @DisplayName("Q5 newInstance: gọi constructor không tham số qua reflection")
    void q05_newInstance_createsEmptyArrayList() {
        Object result = Ex02_TypeErasure.newInstance(ArrayList.class);
        assertInstanceOf(ArrayList.class, result);
        assertEquals(0, ((List<?>) result).size());
    }

    @Test
    @DisplayName("Q5 newInstance: không có constructor không tham số thì bọc lỗi, giữ nguyên cause")
    void q05_newInstance_wrapsReflectiveExceptionAsIllegalArgument() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Ex02_TypeErasure.newInstance(Integer.class));
        assertInstanceOf(NoSuchMethodException.class, ex.getCause());
    }

    @Test
    @DisplayName("Q6 dự đoán: hai ArrayList với type argument khác nhau có cùng Class runtime?")
    void q06_prediction() {
        boolean actual = new ArrayList<String>().getClass() == new ArrayList<Integer>().getClass();
        assertPrediction("Q6_SAME_RUNTIME_CLASS", actual, Ex02_TypeErasure.Q6_SAME_RUNTIME_CLASS, HINT_Q6);
    }

    @Test
    @DisplayName("Q7 isListOfStrings: mọi phần tử là String thì true")
    void q07_isListOfStrings_allStringsReturnsTrue() {
        assertTrue(Ex02_TypeErasure.isListOfStrings(List.of("a", "b")));
    }

    @Test
    @DisplayName("Q7 isListOfStrings: có phần tử không phải String thì false")
    void q07_isListOfStrings_mixedTypesReturnsFalse() {
        assertFalse(Ex02_TypeErasure.isListOfStrings(List.of("a", 1)));
    }

    @Test
    @DisplayName("Q7 isListOfStrings: list rỗng thì true")
    void q07_isListOfStrings_emptyListReturnsTrue() {
        assertTrue(Ex02_TypeErasure.isListOfStrings(List.of()));
    }

    @Test
    @DisplayName("Q7 isListOfStrings: không phải List thì false")
    void q07_isListOfStrings_notAListReturnsFalse() {
        assertFalse(Ex02_TypeErasure.isListOfStrings("abc"));
    }

    @Test
    @DisplayName("Q7 isListOfStrings: có phần tử null thì false")
    void q07_isListOfStrings_nullElementReturnsFalse() {
        assertFalse(Ex02_TypeErasure.isListOfStrings(Arrays.asList("a", null)));
    }

    @Test
    @DisplayName("Q10 dự đoán: heap pollution thất bại ở bước nào, ADD hay GET?")
    @SuppressWarnings({"rawtypes", "unchecked"})
    void q10_prediction() {
        List<String> strings = new ArrayList<>(List.of("a"));
        List raw = strings;
        Ex02_TypeErasure.FailurePoint actual;
        try {
            raw.add(42);
            try {
                @SuppressWarnings("unused")
                String s = strings.get(1);
                actual = null;
            } catch (ClassCastException e) {
                actual = Ex02_TypeErasure.FailurePoint.GET;
            }
        } catch (ClassCastException e) {
            actual = Ex02_TypeErasure.FailurePoint.ADD;
        }
        assertPrediction("Q10_HEAP_POLLUTION_FAILS_AT",
                actual, Ex02_TypeErasure.Q10_HEAP_POLLUTION_FAILS_AT, HINT_Q10);
    }
}
