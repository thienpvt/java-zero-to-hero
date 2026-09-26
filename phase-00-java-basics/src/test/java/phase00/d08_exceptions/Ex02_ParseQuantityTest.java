package phase00.d08_exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_ParseQuantityTest {

    @Test
    @DisplayName("Q1 code: parse số đã trim, kể cả 0")
    void q01_parsesTrimmedNumber() {
        assertEquals(3, Ex02_ParseQuantity.parseQuantity("3"));
        assertEquals(0, Ex02_ParseQuantity.parseQuantity("0"));
        assertEquals(4, Ex02_ParseQuantity.parseQuantity(" 4 "));
    }

    @Test
    @DisplayName("Q1 code: trống, không phải số và số âm có lỗi phân biệt được")
    void q01_rejectsBlankAndInvalid() {
        IllegalArgumentException blank = assertThrows(IllegalArgumentException.class,
                () -> Ex02_ParseQuantity.parseQuantity(null));
        assertEquals("số lượng trống", blank.getMessage());
        assertNull(blank.getCause());
        assertThrows(IllegalArgumentException.class, () -> Ex02_ParseQuantity.parseQuantity(""));
        assertThrows(IllegalArgumentException.class, () -> Ex02_ParseQuantity.parseQuantity("  "));

        IllegalArgumentException notNumber = assertThrows(IllegalArgumentException.class,
                () -> Ex02_ParseQuantity.parseQuantity("12a"));
        assertEquals(NumberFormatException.class, notNumber.getCause().getClass());

        IllegalArgumentException negative = assertThrows(IllegalArgumentException.class,
                () -> Ex02_ParseQuantity.parseQuantity("-1"));
        assertNull(negative.getCause());
    }
}
