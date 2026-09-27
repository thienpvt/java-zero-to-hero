package phase02.d10_atomic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static phase02.support.Predictions.assertPrediction;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase02.d10_atomic.Ex02_MultiVariable.Pair;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_MultiVariableTest {

    private static final String HINT_Q5 =
            "Một AtomicInteger chỉ bao một biến. Hai field trong cùng một giao dịch cần một monitor chung, hoặc một tham chiếu nguyên tử tới cả cặp.";

    private static final int THREADS = 4;
    private static final int PER_THREAD = 1_000;
    private static final int OPENING_A = 2_000;
    private static final int OPENING_B = 3_000;

    @Test
    @DisplayName("Q5 dự đoán: một AtomicInteger có bao hai field trong một giao dịch không")
    void q05_prediction() {
        assertPrediction("Q5_ONE_ATOMIC_COVERS_TWO_FIELDS", false,
                Ex02_MultiVariable.Q5_ONE_ATOMIC_COVERS_TWO_FIELDS, HINT_Q5);
    }

    @Test
    @DisplayName("Q5: một lần transfer đổi cả hai field và giữ tổng")
    void q05_oneTransferKeepsSum() {
        Pair pair = new Pair(OPENING_A, OPENING_B);
        int beforeA = pair.a();
        int beforeB = pair.b();
        int beforeSum = pair.sum();
        pair.transfer();
        assertEquals(beforeSum, pair.sum(), "Một lần transfer phải giữ nguyên a + b.");
        assertNotEquals(beforeA, pair.a(), "transfer phải đổi a.");
        assertNotEquals(beforeB, pair.b(), "transfer phải đổi b.");
    }

    @Test
    @DisplayName("Q5: bốn thread transfer 1000 lần, a + b bằng tổng đầu")
    void q05_fourThreadsKeepSum() throws InterruptedException {
        Pair warmup = new Pair(1, 1);
        int warmupSum = warmup.sum();
        warmup.transfer();
        assertEquals(warmupSum, warmup.sum(), "transfer trên một thread phải giữ tổng.");

        Pair pair = new Pair(OPENING_A, OPENING_B);
        int beforeA = pair.a();
        int beforeB = pair.b();
        int beforeSum = pair.sum();
        CountDownLatch start = new CountDownLatch(1);
        List<Throwable> failures = new CopyOnWriteArrayList<>();
        Thread[] workers = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            workers[i] = new Thread(() -> {
                try {
                    start.await();
                    for (int n = 0; n < PER_THREAD; n++) {
                        pair.transfer();
                    }
                } catch (Throwable failure) {
                    failures.add(failure);
                }
            }, "transfer-" + i);
            workers[i].setDaemon(true);
        }
        for (Thread worker : workers) {
            worker.start();
        }
        start.countDown();
        joinAll(workers);
        rethrowFailures(failures);

        assertEquals(beforeSum, pair.sum(),
                "4 thread, mỗi thread 1000 lần transfer, a + b phải bằng tổng đầu.");
        assertNotEquals(beforeA, pair.a(), "Sau các lần transfer, a phải khác giá trị mở.");
        assertNotEquals(beforeB, pair.b(), "Sau các lần transfer, b phải khác giá trị mở.");
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
