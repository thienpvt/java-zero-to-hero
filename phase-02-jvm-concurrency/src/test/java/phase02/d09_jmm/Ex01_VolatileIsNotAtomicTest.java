package phase02.d09_jmm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static phase02.support.Predictions.assertPrediction;

import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_VolatileIsNotAtomicTest {

    @Test
    @DisplayName("Q2 dự đoán: volatile có đảm bảo atomicity không")
    void q02_prediction() {
        assertPrediction("Q2_VOLATILE_MAKES_INCREMENT_ATOMIC", false,
                Ex01_VolatileIsNotAtomic.Q2_VOLATILE_MAKES_INCREMENT_ATOMIC,
                "volatile tạo happens-before giữa lần ghi và lần đọc. Nó không gom các bước của một phép cộng thành một thao tác nguyên tử.");
    }

    @Test
    @DisplayName("Q3 dự đoán: volatile int rồi count++ có thread-safe không")
    void q03_prediction() {
        assertPrediction("Q3_VOLATILE_COUNT_PLUS_PLUS_SAFE", false,
                Ex01_VolatileIsNotAtomic.Q3_VOLATILE_COUNT_PLUS_PLUS_SAFE,
                "count++ vẫn là đọc, cộng, ghi. volatile chỉ rào từng lần đọc hoặc ghi, không loại trừ thread khác xen vào giữa.");
    }

    @Test
    @DisplayName("Q3: AtomicCount, 4 thread x 10000 increment, count bằng 40000")
    void q03_atomicCountReaches40000() {
        Ex01_VolatileIsNotAtomic.AtomicCount counter = new Ex01_VolatileIsNotAtomic.AtomicCount();
        int threads = 4;
        int perThread = 10_000;
        CountDownLatch start = new CountDownLatch(1);
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Thread bị ngắt khi chờ cổng xuất phát.", e);
                }
                for (int n = 0; n < perThread; n++) {
                    counter.increment();
                }
            }, "atomic-count-" + i);
            workers[i].setDaemon(true);
            workers[i].start();
        }
        start.countDown();
        for (Thread worker : workers) {
            try {
                worker.join(10_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Thread test bị ngắt khi join.", e);
            }
            assertFalse(worker.isAlive(),
                    "Thread còn sống sau khi join tối đa 10 giây: " + worker.getName());
        }
        assertEquals(40_000, counter.count(),
                "4 thread, mỗi thread 10000 increment, count() phải bằng 40000.");
    }
}
