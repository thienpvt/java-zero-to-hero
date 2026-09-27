package phase02.d07_race;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase02.support.Predictions.assertPrediction;

import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_CounterTest {

    @Test
    @DisplayName("Q1 thí nghiệm: runExperiment trả báo cáo có chữ Counter")
    void q01_experimentRuns() {
        String report = Ex01_Counter.runExperiment(1_000);
        assertTrue(report != null && !report.isBlank(), "runExperiment phải trả báo cáo không rỗng.");
        assertTrue(report.contains("Counter"), "Báo cáo phải có chữ Counter.");
    }

    @Test
    @DisplayName("Q1 dự đoán: Counter chưa đồng bộ có thread-safe không")
    void q01_prediction() {
        assertPrediction("Q1_UNSAFE_COUNTER_IS_THREAD_SAFE", false,
                Ex01_Counter.Q1_UNSAFE_COUNTER_IS_THREAD_SAFE,
                "count++ là đọc, sửa, ghi trên field thường. Không có khóa thì Counter không thread-safe.");
    }

    @Test
    @DisplayName("Q4 dự đoán: race condition có luôn reproduce được không")
    void q04_prediction() {
        assertPrediction("Q4_RACE_ALWAYS_REPRODUCES", false,
                Ex01_Counter.Q4_RACE_ALWAYS_REPRODUCES,
                "Lịch thread phụ thuộc tải, số lõi và JIT. Một lần chạy đúng tổng không chứng minh hết race.");
    }

    @Test
    @DisplayName("Q6: SafeCounter, 4 thread x 10000 increment, count bằng 40000")
    void q06_safeCounterReaches40000() {
        Ex01_Counter.SafeCounter counter = new Ex01_Counter.SafeCounter();
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
            }, "safe-counter-" + i);
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
