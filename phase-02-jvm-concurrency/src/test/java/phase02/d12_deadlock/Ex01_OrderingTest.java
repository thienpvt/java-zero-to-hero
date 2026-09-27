package phase02.d12_deadlock;

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
import phase02.d12_deadlock.Ex01_Ordering.Account;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_OrderingTest {

    private static final String HINT_Q2 =
            "Ví dụ điển hình là hai thread, mỗi bên giữ một lock và chờ lock của bên kia. Một lock thì không khép được vòng chờ.";

    private static final String HINT_Q6 =
            "Đọc chuỗi DUMP đã dán. Chữ deadlock nằm trong dòng Found one Java-level deadlock. contains phân biệt hoa thường.";

    @Test
    @DisplayName("Q2 dự đoán: hai thread cần ít nhất bao nhiêu lock cho deadlock điển hình")
    void q02_prediction() {
        assertPrediction("Q2_MIN_LOCKS", 2, Ex01_Ordering.Q2_MIN_LOCKS, HINT_Q2);
    }

    @Test
    @DisplayName("Q3: một lần chuyển trừ nguồn và cộng đích, tổng tiền không đổi")
    void q03_oneTransfer() {
        Account left = new Account(5);
        Account right = new Account(7);
        Ex01_Ordering.transfer(left, right, () -> {
            left.debit(1);
            right.credit(1);
        });
        assertEquals(4, left.balance(), "Nguồn phải giảm 1 sau một lần chuyển.");
        assertEquals(8, right.balance(), "Đích phải tăng 1 sau một lần chuyển.");
        assertEquals(12, left.balance() + right.balance(), "Một lần chuyển không được đổi tổng tiền.");
    }

    @Test
    @DisplayName("Q3: hai thread chuyển ngược chiều 1000 lần, cả hai kết thúc, tổng tiền không đổi")
    void q03_oppositeTransfers() throws InterruptedException {
        Account left = new Account(1_000);
        Account right = new Account(1_000);
        int opening = left.balance() + right.balance();
        int times = 1_000;
        AtomicInteger moves = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        List<Throwable> failures = new CopyOnWriteArrayList<>();

        Thread toRight = new Thread(() -> runTransfers(start, failures, times, () ->
                Ex01_Ordering.transfer(left, right, () -> {
                    left.debit(1);
                    right.credit(1);
                    moves.incrementAndGet();
                })), "transfer-to-right");
        Thread toLeft = new Thread(() -> runTransfers(start, failures, times, () ->
                Ex01_Ordering.transfer(right, left, () -> {
                    right.debit(1);
                    left.credit(1);
                    moves.incrementAndGet();
                })), "transfer-to-left");
        toRight.setDaemon(true);
        toLeft.setDaemon(true);
        toRight.start();
        toLeft.start();
        start.countDown();
        joinAll(toRight, toLeft);
        rethrowFailures(failures);

        assertEquals(times * 2, moves.get(), "Mỗi thread phải chuyển đủ 1000 lần.");
        assertEquals(opening, left.balance() + right.balance(),
                "Tổng tiền hai account phải giữ nguyên sau khi chuyển ngược chiều.");
    }

    @Test
    @DisplayName("Q6 dự đoán: chuỗi DUMP đã dán có cho thấy deadlock không")
    void q06_prediction() {
        boolean showsDeadlock = Ex01_Ordering.DUMP.contains("deadlock");
        assertPrediction("Q6_DUMP_SHOWS_DEADLOCK", showsDeadlock,
                Ex01_Ordering.Q6_DUMP_SHOWS_DEADLOCK, HINT_Q6);
    }

    private static void runTransfers(CountDownLatch start, List<Throwable> failures, int times, Runnable body) {
        try {
            start.await();
            for (int i = 0; i < times; i++) {
                body.run();
            }
        } catch (Throwable failure) {
            failures.add(failure);
        }
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
