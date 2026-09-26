package phase01.d04_hashmap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase01.support.Predictions.assertPrediction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.support.Complexity;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex05_MapVariantsTest {

    private static final String HINT_Q13 =
            "HashMap không đồng bộ hóa bucket khi nhiều luồng cùng ghi; dùng ConcurrentHashMap để an toàn.";
    private static final String HINT_Q14 =
            "HashMap coi null là một key hợp lệ như mọi key khác; TreeMap cần so sánh nên không nhận null key mặc định.";
    private static final String HINT_Q15 =
            "TreeMap dựa trên cây đỏ-đen (Red-Black Tree); HashMap không cam kết thứ tự lặp giữa các lần chạy/resize.";

    @Test
    @DisplayName("Q13 dự đoán: HashMap có thread-safe không")
    void q13_hashMapIsThreadSafe_prediction() {
        assertPrediction("Q13_HASHMAP_IS_THREAD_SAFE", false,
                Ex05_MapVariants.Q13_HASHMAP_IS_THREAD_SAFE, HINT_Q13);
    }

    @Test
    @DisplayName("Q13 concurrentCounter: 4 luồng x 10 000 lần merge() cho tổng chính xác")
    void q13_concurrentCounter_fourThreadsMergeToExactTotal() throws InterruptedException {
        Map<String, Integer> counter = Ex05_MapVariants.concurrentCounter();
        int threadCount = 4;
        int perThread = 10_000;
        Thread[] threads = new Thread[threadCount];
        for (int t = 0; t < threadCount; t++) {
            threads[t] = new Thread(() -> {
                for (int i = 0; i < perThread; i++) {
                    counter.merge("k" + (i % 100), 1, Integer::sum);
                }
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        long total = counter.values().stream().mapToLong(Integer::longValue).sum();
        assertEquals(40_000L, total, "Tổng đếm qua ConcurrentHashMap.merge() đa luồng phải chính xác và tất định.");
    }

    @Test
    @DisplayName("Q13 thí nghiệm: runExperiment chạy xong và trả báo cáo không rỗng")
    void q13_experimentRuns() {
        String report = Ex05_MapVariants.runExperiment(1_000);
        assertFalse(report.isBlank(), "Báo cáo thí nghiệm không được rỗng.");
    }

    @Test
    @DisplayName("Q14 dự đoán: HashMap với hai lần put null key")
    void q14_hashMap_twoNullKeyPuts_prediction() {
        Map<String, String> map = new HashMap<>();
        map.put(null, "a");
        map.put(null, "b");
        assertPrediction("Q14_SIZE_AFTER_TWO_NULL_KEYS", map.size(),
                Ex05_MapVariants.Q14_SIZE_AFTER_TWO_NULL_KEYS, HINT_Q14);
        assertPrediction("Q14_VALUE_FOR_NULL_KEY", map.get(null),
                Ex05_MapVariants.Q14_VALUE_FOR_NULL_KEY, HINT_Q14);
    }

    @Test
    @DisplayName("Q14 dự đoán: TreeMap không cho phép null key")
    void q14_treeMap_nullKey_throws_prediction() {
        String exceptionName;
        try {
            new TreeMap<String, String>().put(null, "x");
            exceptionName = "không ném gì cả";
        } catch (RuntimeException ex) {
            exceptionName = ex.getClass().getSimpleName();
        }
        assertPrediction("Q14_TREEMAP_NULL_KEY_EXCEPTION", exceptionName,
                Ex05_MapVariants.Q14_TREEMAP_NULL_KEY_EXCEPTION, HINT_Q14);
    }

    @Test
    @DisplayName("Q15 dự đoán: độ phức tạp get() của TreeMap")
    void q15_treeMapGetComplexity_prediction() {
        assertPrediction("Q15_TREEMAP_GET", Complexity.O_LOG_N,
                Ex05_MapVariants.Q15_TREEMAP_GET, HINT_Q15);
    }

    @Test
    @DisplayName("Q15 dự đoán: HashMap có bảo đảm thứ tự lặp không")
    void q15_hashMapGuaranteesOrder_prediction() {
        assertPrediction("Q15_HASHMAP_GUARANTEES_ORDER", false,
                Ex05_MapVariants.Q15_HASHMAP_GUARANTEES_ORDER, HINT_Q15);
    }

    @Test
    @DisplayName("Q15 wordCountsInFirstSeenOrder: giữ thứ tự xuất hiện đầu tiên và đếm đúng")
    void q15_wordCountsInFirstSeenOrder_keepsInsertionOrder() {
        Map<String, Integer> counts = Ex05_MapVariants.wordCountsInFirstSeenOrder(List.of("b", "a", "b", "c"));
        assertEquals(List.of("b", "a", "c"), List.copyOf(counts.keySet()));
        assertEquals(2, counts.get("b"));
    }

    @Test
    @DisplayName("Q15 wordCountsSorted: khóa được sắp xếp tăng dần")
    void q15_wordCountsSorted_ordersKeysAscending() {
        SortedMap<String, Integer> counts = Ex05_MapVariants.wordCountsSorted(List.of("b", "a", "b", "c"));
        assertEquals(List.of("a", "b", "c"), List.copyOf(counts.keySet()));
        assertEquals(2, counts.get("b"));
    }

    @Test
    @DisplayName("Q15 lruCache: loại phần tử ít dùng gần nhất khi vượt maxEntries")
    void q15_lruCache_evictsLeastRecentlyUsed() {
        Map<String, Integer> cache = Ex05_MapVariants.lruCache(2);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.get("a");
        cache.put("c", 3);
        assertEquals(List.of("a", "c"), List.copyOf(cache.keySet()));
    }

    @Test
    @DisplayName("Q15 lruCache: maxEntries nhỏ hơn 1 phải báo lỗi")
    void q15_lruCache_rejectsNonPositiveMaxEntries() {
        assertThrows(IllegalArgumentException.class, () -> Ex05_MapVariants.lruCache(0));
    }
}
