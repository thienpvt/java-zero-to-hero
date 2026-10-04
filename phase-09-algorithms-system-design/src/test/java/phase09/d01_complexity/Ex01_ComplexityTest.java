package phase09.d01_complexity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Ex01_ComplexityTest {
    @Test
    @DisplayName("B1 đếm cặp theo vị trí, kể cả giá trị trùng")
    void b01CountsDistinctPositionsAndDuplicates() {
        int[] values = {2, 2, 2, 7, -3};
        assertEquals(4, Ex01_Complexity.countPairsQuadratic(values, 4));
        assertEquals(4, Ex01_Complexity.countPairsLinear(values, 4));
        assertEquals(1, Ex01_Complexity.countPairsLinear(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0}, -1));
        assertEquals(0, Ex01_Complexity.countPairsQuadratic(new int[0], 0));
        assertEquals("n=4; quadratic=6; linear=4", Ex01_Complexity.experimentReport(4));
    }

    @Test
    @DisplayName("B1 đối chiếu cách tuyến tính với vét cạn có seed")
    void b01LinearMatchesQuadraticOracle() {
        Random random = new Random(901);
        for (int trial = 0; trial < 300; trial++) {
            int[] values = new int[random.nextInt(13)];
            for (int i = 0; i < values.length; i++) values[i] = random.nextInt(11) - 5;
            int target = random.nextInt(21) - 10;
            assertEquals(Ex01_Complexity.countPairsQuadratic(values, target),
                    Ex01_Complexity.countPairsLinear(values, target));
        }
        assertThrows(NullPointerException.class, () -> Ex01_Complexity.countPairsLinear(null, 0));
        assertThrows(IllegalArgumentException.class, () -> Ex01_Complexity.experimentReport(-1));
    }
}
