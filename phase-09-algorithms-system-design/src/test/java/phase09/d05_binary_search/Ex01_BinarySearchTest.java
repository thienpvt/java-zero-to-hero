package phase09.d05_binary_search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Ex01_BinarySearchTest {
    @Test
    @DisplayName("B1 lower bound xử lý mảng rỗng, phần tử trùng và biên")
    void b01LowerBoundBoundaries() {
        assertEquals(0, Ex01_BinarySearch.lowerBound(new int[0], 9));
        assertEquals(0, Ex01_BinarySearch.lowerBound(new int[]{2, 2, 3}, 1));
        assertEquals(0, Ex01_BinarySearch.lowerBound(new int[]{2, 2, 3}, 2));
        assertEquals(1, Ex01_BinarySearch.lowerBound(new int[]{1, 2, 2, 3}, 2));
        assertEquals(4, Ex01_BinarySearch.lowerBound(new int[]{1, 2, 2, 3}, 9));
        assertEquals(1, Ex01_BinarySearch.lowerBound(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, Integer.MAX_VALUE));
        assertThrows(NullPointerException.class, () -> Ex01_BinarySearch.lowerBound(null, 0));
        int[] sorted = {1, 2, 2, 3};
        assertEquals(1, Ex01_BinarySearch.lowerBound(sorted, 2));
        assertEquals(1, sorted[0]);
        assertEquals(2, sorted[1]);
    }

    @Test
    @DisplayName("B1 firstTrue tìm đúng biên và không tràn midpoint")
    void b01FirstTrueBoundaries() {
        assertEquals(1, Ex01_BinarySearch.firstTrue(0, 1, value -> value >= 1));
        assertEquals(0, Ex01_BinarySearch.firstTrue(-4, 0, value -> value >= 0));
        assertEquals(9, Ex01_BinarySearch.firstTrue(0, 9, value -> value >= 9));
        assertEquals(0, Ex01_BinarySearch.firstTrue(Integer.MIN_VALUE, 0, value -> value >= 0));
        assertEquals(Integer.MAX_VALUE,
                Ex01_BinarySearch.firstTrue(Integer.MIN_VALUE, Integer.MAX_VALUE, value -> value == Integer.MAX_VALUE));
        assertThrows(IllegalArgumentException.class, () -> Ex01_BinarySearch.firstTrue(1, 1, value -> value > 0));
        assertThrows(IllegalArgumentException.class, () -> Ex01_BinarySearch.firstTrue(0, 3, value -> value >= 0));
        assertThrows(IllegalArgumentException.class, () -> Ex01_BinarySearch.firstTrue(0, 3, value -> value > 3));
        assertThrows(NullPointerException.class, () -> Ex01_BinarySearch.firstTrue(0, 1, null));
    }

    @Test
    @DisplayName("B1 lower bound khớp oracle tuyến tính với seed cố định")
    void b01LowerBoundMatchesLinearOracle() {
        Random random = new Random(905);
        for (int trial = 0; trial < 300; trial++) {
            int[] values = new int[random.nextInt(30)];
            for (int i = 0; i < values.length; i++) values[i] = random.nextInt(21) - 10;
            java.util.Arrays.sort(values);
            int target = random.nextInt(25) - 12;
            int expected = 0;
            while (expected < values.length && values[expected] < target) expected++;
            assertEquals(expected, Ex01_BinarySearch.lowerBound(values, target));
        }
    }
}
