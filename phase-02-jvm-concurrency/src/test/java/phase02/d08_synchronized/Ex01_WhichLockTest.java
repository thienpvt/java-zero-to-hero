package phase02.d08_synchronized;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static phase02.support.Predictions.assertPrediction;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase02.d08_synchronized.Ex01_WhichLock.Gate;
import phase02.d08_synchronized.Ex01_WhichLock.Reentry;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_WhichLockTest {

    private static final String HINT_Q1 =
            "Mỗi object có monitor riêng. synchronized không dùng một khóa chung cho mọi object.";

    private static final String HINT_Q2 =
            "Instance method không khai báo object khóa. Monitor là đối tượng method được gọi trên đó.";

    private static final String HINT_Q3 =
            "Static method không có instance. Monitor gắn với đối tượng đại diện cho chính lớp.";

    private static final String HINT_Q5 =
            "Hai instance là hai monitor. Giữ monitor của object này không chặn object kia.";

    private static final String HINT_Q8 =
            "Cùng một thread lấy lại intrinsic lock thì không chờ chính nó. depth() phải chạy xong.";

    private static final int THREADS = 4;
    private static final int PER_THREAD = 1_000;

    @Test
    @DisplayName("Q1 dự đoán: synchronized có khóa một monitor toàn cục không")
    void q01_prediction() {
        assertPrediction("Q1_LOCKS_ONE_GLOBAL", false,
                Ex01_WhichLock.Q1_LOCKS_ONE_GLOBAL, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: synchronized instance method khóa monitor nào")
    void q02_prediction() {
        assertPrediction("Q2_INSTANCE_LOCK", "this",
                Ex01_WhichLock.Q2_INSTANCE_LOCK, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: synchronized static method khóa monitor nào")
    void q03_prediction() {
        assertPrediction("Q3_STATIC_LOCK", "Class",
                Ex01_WhichLock.Q3_STATIC_LOCK, HINT_Q3);
    }

    @Test
    @DisplayName("Q4: bốn thread enter/leave, tổng vào bằng tổng ra và inside về 0")
    void q04_sameObjectCounters() throws InterruptedException {
        Gate probe = new Gate();
        probe.enter();
        probe.leave();
        assertEquals(0, probe.inside(), "Một cặp enter/leave trên một thread phải đưa inside về 0.");
        assertEquals(probe.entered(), probe.left(), "Tổng enter và tổng leave của cặp đó phải bằng nhau.");

        Gate gate = new Gate();
        CountDownLatch start = new CountDownLatch(1);
        List<Throwable> failures = new CopyOnWriteArrayList<>();
        Thread[] workers = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            workers[i] = new Thread(() -> {
                try {
                    start.await();
                    for (int n = 0; n < PER_THREAD; n++) {
                        gate.enter();
                        gate.leave();
                    }
                } catch (Throwable failure) {
                    failures.add(failure);
                }
            }, "gate-" + i);
            workers[i].setDaemon(true);
        }
        for (Thread worker : workers) {
            worker.start();
        }
        start.countDown();
        joinAll(workers);
        rethrowFailures(failures);

        int entered = gate.entered();
        int left = gate.left();
        assertEquals(THREADS * PER_THREAD, entered, "Mỗi lần enter phải được đếm đúng một lần.");
        assertEquals(entered, left, "Tổng enter phải bằng tổng leave.");
        assertEquals(0, gate.inside(), "Mỗi enter có một leave thì inside kết thúc bằng 0.");
    }

    @Test
    @DisplayName("Q5 dự đoán: hai object khác nhau có chặn lẫn nhau không")
    void q05_prediction() {
        assertPrediction("Q5_DIFFERENT_INSTANCES_BLOCK", false,
                Ex01_WhichLock.Q5_DIFFERENT_INSTANCES_BLOCK, HINT_Q5);
    }

    @Test
    @DisplayName("Q8: cùng thread gọi depth() lồng synchronized và hằng reentrant tính từ kết quả")
    void q08_reentryDepth() {
        int depth = new Reentry().depth();
        assertEquals(2, depth,
                "depth() phải cộng lần vào ngoài với lần vào method synchronized lồng bên trong.");
        assertPrediction("Q8_REENTRANT", depth == 2, Ex01_WhichLock.Q8_REENTRANT, HINT_Q8);
    }

    private static void joinAll(Thread... threads) throws InterruptedException {
        for (Thread thread : threads) {
            thread.join(10_000);
        }
        for (Thread thread : threads) {
            assertFalse(thread.isAlive(), () -> "Thread còn sống sau 10 giây: " + thread.getName());
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
