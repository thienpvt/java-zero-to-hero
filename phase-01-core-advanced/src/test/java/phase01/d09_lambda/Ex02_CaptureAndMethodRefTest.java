package phase01.d09_lambda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase01.support.Predictions.assertPrediction;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.support.Compiles;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_CaptureAndMethodRefTest {

    private static final String HINT_Q4 =
            "Chỉ đoạn brokenCapture (gán lại biến local x trong lambda) báo lỗi 'must be final or effectively final'; "
                    + "mutateField và mutateArrayElement không gán lại biến local nên vẫn biên dịch.";

    @Test
    @DisplayName("Q4 dự đoán: 3 kịch bản sửa dữ liệu trong lambda có biên dịch được không")
    void q04_prediction() {
        assertPrediction("Q4_MODIFY_CAPTURED_LOCAL_COMPILES", Compiles.NO,
                Ex02_CaptureAndMethodRef.Q4_MODIFY_CAPTURED_LOCAL_COMPILES, HINT_Q4);
        assertPrediction("Q4_MODIFY_FIELD_IN_LAMBDA_COMPILES", Compiles.YES,
                Ex02_CaptureAndMethodRef.Q4_MODIFY_FIELD_IN_LAMBDA_COMPILES, HINT_Q4);
        assertPrediction("Q4_MODIFY_ARRAY_ELEMENT_COMPILES", Compiles.YES,
                Ex02_CaptureAndMethodRef.Q4_MODIFY_ARRAY_ELEMENT_COMPILES, HINT_Q4);
    }

    @Test
    @DisplayName("Q4 counter: get() trả giá trị tăng dần bắt đầu từ start")
    void q04_counter_incrementsFromStart() {
        Supplier<Integer> counter = Ex02_CaptureAndMethodRef.counter(5);

        assertEquals(5, counter.get(), "Lần get() đầu tiên phải trả đúng start.");
        assertEquals(6, counter.get(), "Lần get() thứ hai phải trả start + 1.");
        assertEquals(7, counter.get(), "Lần get() thứ ba phải trả start + 2.");
    }

    @Test
    @DisplayName("Q4 counter: hai counter độc lập nhau")
    void q04_counter_instancesAreIndependent() {
        Supplier<Integer> first = Ex02_CaptureAndMethodRef.counter(0);
        Supplier<Integer> second = Ex02_CaptureAndMethodRef.counter(100);

        assertEquals(0, first.get());
        assertEquals(100, second.get());
        assertEquals(1, first.get(), "Counter thứ nhất không bị ảnh hưởng bởi counter thứ hai.");
        assertEquals(101, second.get(), "Counter thứ hai không bị ảnh hưởng bởi counter thứ nhất.");
    }

    @Test
    @DisplayName("Q5 length: String::length trả đúng độ dài chuỗi")
    void q05_length_returnsStringLength() {
        Function<String, Integer> length = Ex02_CaptureAndMethodRef.length();

        assertEquals(3, length.apply("abc"), "length(\"abc\") phải là 3.");
        assertEquals(0, length.apply(""), "length(\"\") phải là 0.");
    }

    @Test
    @DisplayName("Q5 equalsIgnoreCase: String::equalsIgnoreCase so sánh không phân biệt hoa thường")
    void q05_equalsIgnoreCase_ignoresCase() {
        BiPredicate<String, String> equalsIgnoreCase = Ex02_CaptureAndMethodRef.equalsIgnoreCase();

        assertTrue(equalsIgnoreCase.test("A", "a"), "\"A\" và \"a\" phải được coi là bằng nhau.");
        assertFalse(equalsIgnoreCase.test("A", "b"), "\"A\" và \"b\" không được coi là bằng nhau.");
    }

    @Test
    @DisplayName("Q5 listFactory: ArrayList::new tạo instance mới, có thể thêm phần tử")
    void q05_listFactory_createsIndependentMutableLists() {
        Supplier<List<String>> factory = Ex02_CaptureAndMethodRef.listFactory();

        List<String> first = factory.get();
        List<String> second = factory.get();
        first.add("x");

        assertNotSame(first, second, "Hai lần get() phải trả về hai instance khác nhau.");
        assertEquals(List.of("x"), first, "List phải add được (không phải List.of bất biến).");
        assertTrue(second.isEmpty(), "List thứ hai không bị ảnh hưởng bởi list thứ nhất.");
    }
}
