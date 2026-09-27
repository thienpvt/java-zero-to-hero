package phase02.d15_concurrent_collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static phase02.support.Predictions.assertPrediction;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ConcurrentMapTest {

    private static final String HINT_Q3 =
            "Gọi put(null, 1) trên ConcurrentHashMap. NullPointerException nghĩa là key null không được nhận.";

    private static final int THREADS = 4;
    private static final int MERGES_PER_THREAD = 10_000;
    private static final int KEYS = 100;

    @Test
    @DisplayName("Q2: bốn thread merge 10000 lần trên 100 key, tổng value bằng 40000")
    void q02_fourThreadsMergeSum40000() throws InterruptedException {
        Map<String, Integer> counts = Ex01_ConcurrentMap.counts();
        assertInstanceOf(ConcurrentHashMap.class, counts,
                "counts() phải trả về ConcurrentHashMap.");

        CountDownLatch start = new CountDownLatch(1);
        List<Throwable> failures = new CopyOnWriteArrayList<>();
        Thread[] workers = new Thread[THREADS];
        for (int t = 0; t < THREADS; t++) {
            workers[t] = new Thread(() -> {
                try {
                    start.await();
                    for (int i = 0; i < MERGES_PER_THREAD; i++) {
                        counts.merge("k" + (i % KEYS), 1, Integer::sum);
                    }
                } catch (Throwable failure) {
                    failures.add(failure);
                }
            }, "merge-" + t);
            workers[t].setDaemon(true);
        }
        for (Thread worker : workers) {
            worker.start();
        }
        start.countDown();
        joinAll(workers);
        rethrowFailures(failures);

        int sum = 0;
        for (int value : counts.values()) {
            sum += value;
        }
        assertEquals(THREADS * MERGES_PER_THREAD, sum,
                "4 thread, mỗi thread merge 10000 lần trên 100 key, tổng value phải bằng 40000.");
    }

    @Test
    @DisplayName("Q3 dự đoán: ConcurrentHashMap có nhận null key không")
    void q03_nullKeyFromPut() {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        boolean allowsNullKey = true;
        try {
            map.put(null, 1);
        } catch (NullPointerException ex) {
            allowsNullKey = false;
        }
        assertPrediction("Q3_ALLOWS_NULL_KEY", allowsNullKey,
                Ex01_ConcurrentMap.Q3_ALLOWS_NULL_KEY, HINT_Q3);
    }

    private static void joinAll(Thread... threads) throws InterruptedException {
        for (Thread thread : threads) {
            thread.join(10_000);
        }
        for (Thread thread : threads) {
            assertFalse(thread.isAlive(),
                    () -> "Thread còn sống sau khi join tối đa 10 giây: " + thread.getName());
        }
    }

    private static void rethrowFailures(List<Throwable> failures) {
        if (failures.isEmpty()) {
            return;
        }
        Throwable first = failures.getFirst();
        for (int i = 1; i < failures.size(); i++) {
            first.addSuppressed(failures.get(i));
        }
        if (first instanceof RuntimeException runtime) {
            throw runtime;
        }
        if (first instanceof Error error) {
            throw error;
        }
        throw new IllegalStateException(first);
    }
}
