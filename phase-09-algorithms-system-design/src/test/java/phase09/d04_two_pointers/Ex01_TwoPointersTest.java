package phase09.d04_two_pointers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Ex01_TwoPointersTest {
    @Test
    @DisplayName("B1 tìm cặp đầu tiên theo chỉ số và cửa sổ ngắn nhất")
    void b01FindsPairsAndMinimumWindow() {
        assertArrayEquals(new int[]{0, 1}, Ex01_TwoPointers.pairSumSorted(new int[]{1, 2, 2, 3}, 3));
        assertArrayEquals(new int[]{0, 1}, Ex01_TwoPointers.pairSumSorted(new int[]{2, 2, 9}, 4));
        assertArrayEquals(new int[]{-1, -1}, Ex01_TwoPointers.pairSumSorted(new int[]{1, 2, 4}, 10));
        assertArrayEquals(new int[]{-1, -1}, Ex01_TwoPointers.pairSumSorted(new int[0], 0));
        assertArrayEquals(new int[]{0, 1}, Ex01_TwoPointers.pairSumSorted(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, -1));
        assertEquals(2, Ex01_TwoPointers.minWindowLength(new int[]{2, 3, 1, 2, 4, 3}, 7));
        assertEquals(0, Ex01_TwoPointers.minWindowLength(new int[]{1, 1}, 5));
        assertEquals(0, Ex01_TwoPointers.minWindowLength(new int[0], 1));
    }

    @Test
    @DisplayName("B1 cửa sổ tuyến tính khớp oracle vét cạn có seed")
    void b01MinimumWindowMatchesBruteForceOracle() {
        Random random = new Random(904);
        for (int trial = 0; trial < 300; trial++) {
            int[] values = new int[random.nextInt(13)];
            for (int i = 0; i < values.length; i++) values[i] = random.nextInt(8);
            long target = 1 + random.nextInt(22);
            assertEquals(bruteForceWindow(values, target), Ex01_TwoPointers.minWindowLength(values, target));
        }
        assertThrows(IllegalArgumentException.class, () -> Ex01_TwoPointers.pairSumSorted(new int[]{2, 1}, 3));
        assertThrows(IllegalArgumentException.class, () -> Ex01_TwoPointers.minWindowLength(new int[]{1, -1}, 1));
        assertThrows(IllegalArgumentException.class, () -> Ex01_TwoPointers.minWindowLength(new int[]{1}, 0));
    }

    private int bruteForceWindow(int[] values, long target) {
        int minimum = Integer.MAX_VALUE;
        for (int start = 0; start < values.length; start++) {
            long sum = 0;
            for (int end = start; end < values.length; end++) {
                sum += values[end];
                if (sum >= target) minimum = Math.min(minimum, end - start + 1);
            }
        }
        return minimum == Integer.MAX_VALUE ? 0 : minimum;
    }
}
