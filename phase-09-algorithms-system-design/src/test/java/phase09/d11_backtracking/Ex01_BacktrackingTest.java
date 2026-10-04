package phase09.d11_backtracking;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Ex01_BacktrackingTest {
    @Test
    void combinationsAreLexicographicAndHandleEmptyAndImpossibleCases() {
        assertEquals(List.of(List.of()), Ex01_Backtracking.combinations(0, 0));
        assertEquals(List.of(List.of(1), List.of(2), List.of(3)), Ex01_Backtracking.combinations(3, 1));
        assertEquals(List.of(List.of(1, 2), List.of(1, 3), List.of(2, 3)),
                Ex01_Backtracking.combinations(3, 2));
        assertThrows(IllegalArgumentException.class, () -> Ex01_Backtracking.combinations(2, 3));
        assertThrows(IllegalArgumentException.class, () -> Ex01_Backtracking.combinations(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> Ex01_Backtracking.combinations(2, -1));
    }

    @Test
    void duplicateValuesGenerateOnlyUniqueCombinations() {
        int[] values = {2, 1, 2, 1};
        assertEquals(List.of(List.of(1, 1), List.of(1, 2), List.of(2, 2)),
                Ex01_Backtracking.combinations(values, 2));
        assertEquals(List.of(List.of(4, 4)), Ex01_Backtracking.combinations(new int[]{4, 4}, 2));
        assertEquals(List.of(List.of()), Ex01_Backtracking.combinations(new int[]{1, 1}, 0));
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_Backtracking.combinations(new int[]{1}, 2));
        assertThrows(NullPointerException.class, () -> Ex01_Backtracking.combinations(null, 0));
        assertEquals(List.of(2, 1, 2, 1), java.util.Arrays.stream(values).boxed().toList());
    }
}
