package phase02.d08_synchronized;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase02.support.Predictions.assertPrediction;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase02.d08_synchronized.Ex02_VisibilityAndScope.Account;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_VisibilityAndScopeTest {

    private static final String HINT_Q6 =
            "Nhả monitor happens-before lần lấy cùng monitor. Thread sau phải thấy ghi đã xong trước khi nhả khóa, không chỉ thấy phép toán atomic.";

    private static final int OPENING = 1_000;
    private static final int THREADS = 8;
    private static final int ATTEMPTS_PER_THREAD = 250;

    @Test
    @DisplayName("Q6 dự đoán: synchronized có đảm bảo visibility không")
    void q06_prediction() {
        assertPrediction("Q6_ALSO_VISIBILITY", true,
                Ex02_VisibilityAndScope.Q6_ALSO_VISIBILITY, HINT_Q6);
    }

    @Test
    @DisplayName("Q7: rút một lần trừ đúng số dư và ghi mô tả lần rút")
    void q07_withdrawOne() {
        Account account = new Account(3);
        account.withdraw(1);
        assertEquals(2, account.balance(), "Rút 1 từ số dư 3 phải còn 2 khi đọc dưới cùng monitor.");
        assertEquals(1, account.successfulWithdrawals(), "Một lần rút hợp lệ phải được đếm.");
        assertFalse(account.lastAttempt().isBlank(), "Mô tả lần rút phải được ghi, không chỉ trừ số dư.");
    }

    @Test
    @DisplayName("Q7: nhiều thread rút 1, lần thành công cộng số dư bằng số dư mở, không thấy số âm")
    void q07_concurrentWithdrawals() throws InterruptedException {
        Account warmup = new Account(1);
        warmup.withdraw(1);

        Account account = new Account(OPENING);
        CountDownLatch start = new CountDownLatch(1);
        List<Throwable> failures = new CopyOnWriteArrayList<>();
        AtomicBoolean sample = new AtomicBoolean(true);
        AtomicInteger lowest = new AtomicInteger(Integer.MAX_VALUE);
        Thread reader = new Thread(() -> {
            while (sample.get()) {
                observe(account, lowest, failures);
                Thread.onSpinWait();
            }
            observe(account, lowest, failures);
        }, "balance-reader");
        reader.setDaemon(true);

        Thread[] workers = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            workers[i] = new Thread(() -> {
                try {
                    start.await();
                    for (int n = 0; n < ATTEMPTS_PER_THREAD; n++) {
                        account.withdraw(1);
                    }
                } catch (Throwable failure) {
                    failures.add(failure);
                }
            }, "withdraw-" + i);
            workers[i].setDaemon(true);
        }

        reader.start();
        for (Thread worker : workers) {
            worker.start();
        }
        start.countDown();
        joinAll(workers);
        sample.set(false);
        joinAll(reader);
        rethrowFailures(failures);

        int balance = account.balance();
        int successes = account.successfulWithdrawals();
        assertTrue(balance >= 0, "balance() dưới cùng monitor không được trả số dư âm.");
        assertTrue(lowest.get() >= 0, "Các lần đọc balance() trong lúc rút không được thấy số dư âm.");
        assertTrue(lowest.get() != Integer.MAX_VALUE, "Phải đọc được số dư ít nhất một lần dưới monitor.");
        assertEquals(OPENING, successes + balance,
                "Số lần rút thành công cộng số dư cuối phải bằng số dư mở.");
        assertEquals(OPENING, successes, "Mỗi đơn vị của số dư mở được rút đúng một lần.");
        assertEquals(0, balance, "Hết lượt rút hợp lệ thì số dư cuối về 0.");
    }

    private static void observe(Account account, AtomicInteger lowest, List<Throwable> failures) {
        try {
            int value = account.balance();
            lowest.accumulateAndGet(value, Math::min);
        } catch (Throwable failure) {
            failures.add(failure);
            lowest.set(Integer.MIN_VALUE);
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
