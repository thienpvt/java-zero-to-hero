package phase01.d04_hashmap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import phase01.support.Complexity;

/**
 * HashMap — Bài 5: Các biến thể Map
 *
 * Nguồn: 01-java-core-advanced.md, mục 4 (HashMap), câu 13–15.
 * Cần làm trước: Ex01_LookupAndCollision, Ex04_MutableKey.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex05_MapVariantsTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q13 [DỰ ĐOÁN + THÍ NGHIỆM] `HashMap` có thread-safe không?
 *   Bắt đầu   : điền hằng số Q13_HASHMAP_IS_THREAD_SAFE; cài đặt concurrentCounter().
 *   Kiểm chứng: chạy q13_concurrentCounter_fourThreadsMergeToExactTotal (tất định — merge()
 *               của ConcurrentHashMap nguyên tử); sau đó chạy q13_experimentRuns/main để
 *               thấy runExperiment cho sẵn so sánh HashMap thường (kết quả có thể sai/đứng)
 *               với ConcurrentHashMap (luôn đúng).
 *   Code      : concurrentCounter() trả về một ConcurrentHashMap rỗng.
 *   Hoàn thành khi: mọi test q13_* xanh + giải thích được vì sao HashMap thường không an
 *               toàn khi nhiều luồng cùng ghi (đọc/ghi cấu trúc bucket không đồng bộ).
 *
 * Q14 [DỰ ĐOÁN] `HashMap` cho phép bao nhiêu null key?
 *   Bắt đầu   : viết đoạn code tạm trong Evaluate Expression (Alt+F8) hoặc Scratch file:
 *               put(null, "a") rồi put(null, "b") vào một HashMap; điền 2 hằng số Q14_* đầu.
 *   Kiểm chứng: chạy q14_hashMap_twoNullKeyPuts_prediction; sau đó thử put(null, "x") vào
 *               TreeMap (Ctrl+N mở TreeMap, đọc Javadoc bằng Ctrl+Q) để điền hằng số thứ ba.
 *   Hoàn thành khi: mọi test q14_* xanh.
 *
 * Q15 [DỰ ĐOÁN + CODE] So sánh `HashMap`, `LinkedHashMap`, `TreeMap`.
 *   Bắt đầu   : điền 2 hằng số Q15_TREEMAP_GET và Q15_HASHMAP_GUARANTEES_ORDER; cài đặt
 *               wordCountsInFirstSeenOrder, wordCountsSorted, lruCache.
 *   Kiểm chứng: chạy các test q15_*; đặt breakpoint trong LinkedHashMap.afterNodeAccess
 *               (Ctrl+N → LinkedHashMap → Ctrl+F12) để thấy accessOrder=true di chuyển
 *               entry ra cuối danh sách mỗi lần get().
 *   Code      : wordCountsInFirstSeenOrder dùng LinkedHashMap (accessOrder mặc định false)
 *               để giữ thứ tự xuất hiện đầu tiên; wordCountsSorted dùng TreeMap để có khóa
 *               sắp xếp tăng dần; lruCache(maxEntries) dùng LinkedHashMap(..., accessOrder=true)
 *               kèm override removeEldestEntry để loại phần tử ít dùng gần nhất khi vượt
 *               maxEntries (maxEntries &lt; 1 → IllegalArgumentException).
 *   Hoàn thành khi: mọi test q15_* xanh + giải thích được sự khác biệt về thứ tự lặp và độ
 *               phức tạp get() giữa ba loại Map.
 */
public class Ex05_MapVariants {

    // Q13 — cố định: HashMap không đồng bộ hóa bất kỳ thao tác nào.
    static final Boolean Q13_HASHMAP_IS_THREAD_SAFE = false; // SOLUTION-VALUE

    /** Map đếm dùng được an toàn từ nhiều luồng cùng lúc. */
    static Map<String, Integer> concurrentCounter() {
        // SOLUTION-BEGIN throw Q13
        return new ConcurrentHashMap<>();
        // SOLUTION-END
    }

    private static String raceMerge(Map<String, Integer> map, int perThread, int keyCount) {
        int threadCount = 4;
        Thread[] threads = new Thread[threadCount];
        AtomicReference<Throwable> failure = new AtomicReference<>();
        for (int t = 0; t < threadCount; t++) {
            threads[t] = new Thread(() -> {
                try {
                    for (int i = 0; i < perThread; i++) {
                        map.merge("k" + (i % keyCount), 1, Integer::sum);
                    }
                } catch (Throwable ex) {
                    failure.set(ex);
                }
            });
            threads[t].setDaemon(true);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join(5_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (failure.get() != null) {
            return "ném lỗi " + failure.get().getClass().getSimpleName() + " (dữ liệu đua tranh)";
        }
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                return "một số luồng chưa xong sau 5s (nghi kẹt do đua tranh trên cấu trúc bucket)";
            }
        }
        long total = map.values().stream().mapToLong(Integer::longValue).sum();
        long expected = (long) threadCount * perThread;
        return "tổng đếm = " + total + " (kỳ vọng " + expected + ")"
                + (total == expected ? " — KHỚP" : " — SAI do mất update (race condition)");
    }

    /**
     * Đo thô hai kịch bản 4 luồng cùng merge(key, 1, Integer::sum) trên {@code perThread} lần
     * mỗi luồng, trải trên 100 key: một trên HashMap thường (không đồng bộ, kết quả có thể sai
     * hoặc treo), một trên ConcurrentHashMap (luôn đúng). Chỉ để quan sát trong main(), không
     * assert số đo trong test vì HashMap thường ở đây có hành vi không tất định khi có race.
     */
    static String runExperiment(int perThread) {
        StringBuilder report = new StringBuilder();
        report.append("HashMap thường, 4 luồng x ").append(perThread).append(" lần merge(): ")
                .append(raceMerge(new HashMap<>(), perThread, 100)).append('\n');
        report.append("ConcurrentHashMap, 4 luồng x ").append(perThread).append(" lần merge(): ")
                .append(raceMerge(concurrentCounter(), perThread, 100));
        return report.toString();
    }

    public static void main(String[] args) {
        System.out.println(runExperiment(1_000));
    }

    // Q14 — kịch bản: HashMap<String, String> put(null, "a") rồi put(null, "b").
    static final Integer Q14_SIZE_AFTER_TWO_NULL_KEYS = 1; // SOLUTION-VALUE
    static final String Q14_VALUE_FOR_NULL_KEY = "b"; // SOLUTION-VALUE
    // Q14 — kịch bản: new TreeMap<String, String>().put(null, "x").
    static final String Q14_TREEMAP_NULL_KEY_EXCEPTION = "NullPointerException"; // SOLUTION-VALUE

    // Q15 — cố định: TreeMap dựa trên cây đỏ-đen (Red-Black Tree).
    static final Complexity Q15_TREEMAP_GET = Complexity.O_LOG_N; // SOLUTION-VALUE
    // Q15 — cố định: HashMap không cam kết thứ tự lặp (phụ thuộc hash/bucket/resize).
    static final Boolean Q15_HASHMAP_GUARANTEES_ORDER = false; // SOLUTION-VALUE

    /** Đếm số lần xuất hiện của mỗi từ, giữ đúng thứ tự từ xuất hiện lần đầu tiên. */
    static Map<String, Integer> wordCountsInFirstSeenOrder(List<String> words) {
        // SOLUTION-BEGIN throw Q15
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String word : words) {
            counts.merge(word, 1, Integer::sum);
        }
        return counts;
        // SOLUTION-END
    }

    /** Đếm số lần xuất hiện của mỗi từ, khóa được sắp xếp tăng dần theo thứ tự tự nhiên. */
    static SortedMap<String, Integer> wordCountsSorted(List<String> words) {
        // SOLUTION-BEGIN throw Q15
        SortedMap<String, Integer> counts = new TreeMap<>();
        for (String word : words) {
            counts.merge(word, 1, Integer::sum);
        }
        return counts;
        // SOLUTION-END
    }

    /**
     * Cache LRU (Least Recently Used) đơn giản: khi vượt {@code maxEntries}, tự loại phần tử
     * ít được truy cập gần đây nhất.
     *
     * @throws IllegalArgumentException nếu {@code maxEntries} nhỏ hơn 1
     */
    static <K, V> Map<K, V> lruCache(int maxEntries) {
        // SOLUTION-BEGIN throw Q15
        if (maxEntries < 1) {
            throw new IllegalArgumentException("maxEntries phải >= 1: " + maxEntries);
        }
        return new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxEntries;
            }
        };
        // SOLUTION-END
    }
}
