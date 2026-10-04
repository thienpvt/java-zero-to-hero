package phase09.d02_array_string;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Ex01_ArrayStringTest {
    @Test
    @DisplayName("B1 đảo code point, giữ nguyên cặp surrogate")
    void b01ReversesCodePoints() {
        assertEquals("", Ex01_ArrayString.reverseCodePoints(""));
        assertEquals("🧠ba", Ex01_ArrayString.reverseCodePoints("ab🧠"));
        assertEquals("cba", Ex01_ArrayString.reverseCodePoints("abc"));
        assertEquals(12, Ex01_ArrayString.checkedArea(3, 4));
        assertEquals(0, Ex01_ArrayString.checkedArea(0, Integer.MAX_VALUE));
    }

    @Test
    @DisplayName("B1 từ chối null, kích thước âm và overflow")
    void b01RejectsInvalidInputs() {
        assertThrows(NullPointerException.class, () -> Ex01_ArrayString.reverseCodePoints(null));
        assertThrows(IllegalArgumentException.class, () -> Ex01_ArrayString.checkedArea(-1, 2));
        assertThrows(ArithmeticException.class, () -> Ex01_ArrayString.checkedArea(Integer.MAX_VALUE, 2));
    }
}
