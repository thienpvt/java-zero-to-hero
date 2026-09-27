package phase02.d10_atomic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static phase02.support.Predictions.assertPrediction;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_CasTest {

    private static final String HINT_Q1 =
            "volatile chỉ bảo đảm một lần đọc hoặc một lần ghi được nhìn thấy. AtomicInteger còn nguyên tử hóa đọc-sửa-ghi bằng CAS.";

    private static final String HINT_Q6 =
            "CAS chỉ so sánh giá trị hiện tại với giá trị đã đọc. A đổi thành B rồi trở lại A thì lần so sánh vẫn thấy A.";

    private static final int THREADS = 4;
    private static final int PER_THREAD = 1_000;

    @Test
    @DisplayName("Q1 dự đoán: AtomicInteger có chỉ là volatile int không")
    void q01_prediction() {
        assertPrediction("Q1_ATOMIC_IS_JUST_VOLATILE", false,
                Ex01_Cas.Q1_ATOMIC_IS_JUST_VOLATILE, HINT_Q1);
    }

    @Test
    @DisplayName("Q2: một thread tăng từ 0 lên 1000 bằng CAS")
    void q02_oneThreadReaches1000() {
        assertEquals(1_000, Ex01_Cas.incrementTo(1_000),
                "Một thread gọi incrementTo(1000) phải trả 1000.");
    }

    @Test
    @DisplayName("Q2: bốn thread cùng tăng một AtomicInteger, tổng bằng 4000")
    void q02_fourThreadsTotal4000() throws InterruptedException {
        AtomicInteger shared = new AtomicInteger();
        assertEquals(1, Ex01_Cas.incrementTo(shared, 1),
                "Một lần tăng trên bộ đếm chung phải trả 1.");
        shared.set(0);

        CountDownLatch start = new CountDownLatch(1);
        List<Throwable> failures = new CopyOnWriteArrayList<>();
        Thread[] workers = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            workers[i] = new Thread(() -> {
                try {
                    start.await();
                    Ex01_Cas.incrementTo(shared, PER_THREAD);
                } catch (Throwable failure) {
                    failures.add(failure);
                }
            }, "cas-" + i);
            workers[i].setDaemon(true);
        }
        for (Thread worker : workers) {
            worker.start();
        }
        start.countDown();
        joinAll(workers);
        rethrowFailures(failures);
        assertEquals(THREADS * PER_THREAD, shared.get(),
                "4 thread, mỗi thread 1000 lần CAS, tổng phải bằng 4000.");
    }

    @Test
    @DisplayName("Q6 dự đoán: ABA có thể xảy ra với CAS không")
    void q06_prediction() {
        assertPrediction("Q6_ABA_POSSIBLE", true,
                Ex01_Cas.Q6_ABA_POSSIBLE, HINT_Q6);
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
