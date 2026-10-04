package phase09.d09_heap_topk;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Ex01_HeapTopKTest {
    @Test
    void topKHandlesBoundariesDuplicatesAndIntegerExtremes() {
        assertEquals(List.of(), Ex01_HeapTopK.topK(new int[]{1}, 0));
        assertEquals(List.of(9, 9, 4), Ex01_HeapTopK.topK(new int[]{4, 9, 9, 2}, 3));
        assertEquals(List.of(Integer.MAX_VALUE, 0, Integer.MIN_VALUE),
                Ex01_HeapTopK.topK(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, 0}, 3));
        assertThrows(IllegalArgumentException.class, () -> Ex01_HeapTopK.topK(new int[]{1}, 2));
        assertThrows(NullPointerException.class, () -> Ex01_HeapTopK.topK(null, 0));
    }

    @Test
    void topKMatchesSortOracleForSeededArrays() {
        Random random = new Random(912);
        for (int trial = 0; trial < 200; trial++) {
            int[] values = random.ints(random.nextInt(30), -20, 21).toArray();
            int k = random.nextInt(values.length + 1);
            List<Integer> expected = Arrays.stream(values).boxed().sorted(Comparator.reverseOrder())
                    .limit(k).toList();
            assertEquals(expected, Ex01_HeapTopK.topK(values, k));
        }
    }
}
