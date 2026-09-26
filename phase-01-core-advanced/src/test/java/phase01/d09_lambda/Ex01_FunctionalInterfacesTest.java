package phase01.d09_lambda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static phase01.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d09_lambda.Ex01_FunctionalInterfaces.Validator;
import phase01.support.Compiles;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_FunctionalInterfacesTest {

    private static final String HINT_Q1_COMPILES =
            "Bỏ comment interface TwoAbstract mẫu trong Ex01_FunctionalInterfaces để thấy lỗi đỏ.";
    private static final String HINT_Q1_DEFAULT =
            "Validator có 1 abstract method (validate) + 1 default method (and) vẫn là functional interface hợp lệ.";

    @Test
    @DisplayName("Q1 dự đoán: functional interface với 2 abstract method + default method")
    void q01_prediction() {
        assertPrediction("Q1_TWO_ABSTRACT_METHODS_WITH_ANNOTATION_COMPILES", Compiles.NO,
                Ex01_FunctionalInterfaces.Q1_TWO_ABSTRACT_METHODS_WITH_ANNOTATION_COMPILES, HINT_Q1_COMPILES);

        Validator<String> v = value -> true;
        assertPrediction("Q1_DEFAULT_METHODS_ALLOWED", v.validate("x"),
                Ex01_FunctionalInterfaces.Q1_DEFAULT_METHODS_ALLOWED, HINT_Q1_DEFAULT);
    }

    @Test
    @DisplayName("Q1 and: true khi cả hai validator đều true")
    void q01_and_trueWhenBothTrue() {
        Validator<String> alwaysTrue = value -> true;
        Validator<String> combined = alwaysTrue.and(alwaysTrue);

        assertTrue(combined.validate("bất kỳ"), "Cả hai vế true thì and() phải trả true.");
    }

    @Test
    @DisplayName("Q1 and: false khi một trong hai vế false")
    void q01_and_falseWhenEitherFalse() {
        Validator<String> alwaysTrue = value -> true;
        Validator<String> alwaysFalse = value -> false;

        assertFalse(alwaysFalse.and(alwaysTrue).validate("x"), "Vế đầu false thì and() phải trả false.");
        assertFalse(alwaysTrue.and(alwaysFalse).validate("x"), "Vế sau false thì and() phải trả false.");
    }

    @Test
    @DisplayName("Q1 and: NPE khi other null")
    void q01_and_throwsNpeWhenOtherIsNull() {
        Validator<String> alwaysTrue = value -> true;

        assertThrows(NullPointerException.class, () -> alwaysTrue.and(null),
                "and(null) phải ném NullPointerException.");
    }

    @Test
    @DisplayName("Q1 and: short-circuit, không gọi vế sau khi vế đầu false")
    void q01_and_shortCircuitsWhenFirstIsFalse() {
        Validator<String> alwaysFalse = value -> false;
        Validator<String> failIfCalled = value -> {
            fail("other không được gọi khi vế đầu đã false (short-circuit).");
            return true;
        };

        assertFalse(alwaysFalse.and(failIfCalled).validate("x"), "Kết quả phải là false.");
    }

    @Test
    @DisplayName("Q1 maxLength: max âm ném IllegalArgumentException")
    void q01_maxLength_rejectsNegativeMax() {
        assertThrows(IllegalArgumentException.class, () -> Ex01_FunctionalInterfaces.maxLength(-1),
                "max âm phải ném IllegalArgumentException.");
    }

    @Test
    @DisplayName("Q1 notBlank + maxLength(3): kết hợp bằng and()")
    void q01_notBlankAndMaxLength_combination() {
        Validator<String> validator = Ex01_FunctionalInterfaces.notBlank().and(Ex01_FunctionalInterfaces.maxLength(3));

        assertTrue(validator.validate("abc"), "\"abc\" không blank và độ dài 3 <= 3 phải true.");
        assertFalse(validator.validate("abcd"), "\"abcd\" dài 4 > 3 phải false.");
        assertFalse(validator.validate(" "), "\" \" là blank phải false.");
    }

    @Test
    @DisplayName("Q1 maxLength(3): giá trị null luôn false")
    void q01_maxLength_nullIsFalse() {
        assertFalse(Ex01_FunctionalInterfaces.maxLength(3).validate(null), "maxLength phải false với null.");
    }

    @Test
    @DisplayName("Q2 dự đoán: kiểu trả về của Predicate.test(T)")
    void q02_prediction() throws NoSuchMethodException {
        String actual = Predicate.class.getMethod("test", Object.class).getReturnType().getName();

        assertPrediction("Q2_PREDICATE_TEST_RETURN_TYPE", actual,
                Ex01_FunctionalInterfaces.Q2_PREDICATE_TEST_RETURN_TYPE,
                "Predicate.class.getMethod(\"test\", Object.class).getReturnType().getName()");
    }

    @Test
    @DisplayName("Q3 dự đoán: kiểu trả về của Consumer.accept(T)")
    void q03_prediction() throws NoSuchMethodException {
        String actual = Consumer.class.getMethod("accept", Object.class).getReturnType().getName();

        assertPrediction("Q3_CONSUMER_ACCEPT_RETURN_TYPE", actual,
                Ex01_FunctionalInterfaces.Q3_CONSUMER_ACCEPT_RETURN_TYPE,
                "Consumer.class.getMethod(\"accept\", Object.class).getReturnType().getName()");
    }

    @Test
    @DisplayName("Q3 process: lọc, biến đổi rồi gọi audit theo thứ tự")
    void q03_process_filtersTransformsAndAudits() {
        List<String> input = List.of("a", "", "bc");
        List<String> audited = new ArrayList<>();

        List<String> result = Ex01_FunctionalInterfaces.process(input, s -> !s.isBlank(),
                s -> s.toUpperCase(Locale.ROOT), audited::add);

        assertEquals(List.of("A", "BC"), result, "Kết quả phải là các phần tử không blank, đã viết hoa.");
        assertEquals(List.of("A", "BC"), audited, "audit phải nhận đúng thứ tự các kết quả đã biến đổi.");
    }

    @Test
    @DisplayName("Q3 process: không sửa đổi list input")
    void q03_process_doesNotMutateInput() {
        List<String> input = List.of("a", "bc");

        Ex01_FunctionalInterfaces.process(input, s -> true, s -> s, s -> {
        });

        assertEquals(List.of("a", "bc"), input, "Input phải giữ nguyên sau khi process().");
    }
}
