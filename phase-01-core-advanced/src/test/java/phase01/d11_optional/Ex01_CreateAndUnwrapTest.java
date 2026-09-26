package phase01.d11_optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static phase01.support.Predictions.assertPrediction;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_CreateAndUnwrapTest {

    private static final String HINT_Q1 =
            "Optional.of gọi Objects.requireNonNull nên ném NPE ngay; ofNullable bọc null thành Optional.empty().";
    private static final String HINT_Q2 =
            "orElse(T) nhận giá trị đã được TÍNH SẴN (tham số luôn được evaluate); orElseGet(Supplier) chỉ gọi get() khi Optional trống.";

    @Test
    @DisplayName("Q1 dự đoán: Optional.of(null) và ofNullable(null) khác nhau thế nào")
    void q01_prediction() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> Optional.of(null));
        assertPrediction("Q1_OF_NULL_EXCEPTION",
                npe.getClass().getSimpleName(), Ex01_CreateAndUnwrap.Q1_OF_NULL_EXCEPTION, HINT_Q1);

        boolean isPresent = Optional.ofNullable(null).isPresent();
        assertPrediction("Q1_OF_NULLABLE_NULL_IS_PRESENT",
                isPresent, Ex01_CreateAndUnwrap.Q1_OF_NULLABLE_NULL_IS_PRESENT, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: orElse() luôn gọi tham số, orElseGet() chỉ gọi khi trống")
    void q02_prediction() {
        Ex01_CreateAndUnwrap.DEFAULT_CALLS.set(0);
        Optional.of("x").orElse(Ex01_CreateAndUnwrap.expensiveDefault());
        assertPrediction("Q2_ORELSE_CALLS_WHEN_PRESENT",
                Ex01_CreateAndUnwrap.DEFAULT_CALLS.get(), Ex01_CreateAndUnwrap.Q2_ORELSE_CALLS_WHEN_PRESENT, HINT_Q2);

        Ex01_CreateAndUnwrap.DEFAULT_CALLS.set(0);
        Optional.of("x").orElseGet(Ex01_CreateAndUnwrap::expensiveDefault);
        assertPrediction("Q2_ORELSEGET_CALLS_WHEN_PRESENT",
                Ex01_CreateAndUnwrap.DEFAULT_CALLS.get(), Ex01_CreateAndUnwrap.Q2_ORELSEGET_CALLS_WHEN_PRESENT, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 code: displayName trả biệt danh hợp lệ, không gọi fullNameLookup")
    void q03_displayName_returnsNicknameWithoutCallingLookup() {
        String result = Ex01_CreateAndUnwrap.displayName(Optional.of("Bee"),
                () -> fail("fullNameLookup không được gọi khi nickname đã hợp lệ"));
        assertEquals("Bee", result, "Nickname hợp lệ phải được trả về nguyên vẹn.");
    }

    @Test
    @DisplayName("Q3 code: displayName rơi về fullNameLookup khi nickname blank")
    void q03_displayName_fallsBackWhenNicknameBlank() {
        String result = Ex01_CreateAndUnwrap.displayName(Optional.of(" "), () -> "Nguyen Van A");
        assertEquals("Nguyen Van A", result, "Nickname blank phải rơi về fullNameLookup.");
    }

    @Test
    @DisplayName("Q3 code: displayName rơi về fullNameLookup khi không có nickname")
    void q03_displayName_fallsBackWhenNicknameEmpty() {
        String result = Ex01_CreateAndUnwrap.displayName(Optional.empty(), () -> "Nguyen Van A");
        assertEquals("Nguyen Van A", result, "Không có nickname phải rơi về fullNameLookup.");
    }
}
