package phase02.d19_capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class SafeAccountTest {

    @Test
    @DisplayName("B2: 8 thread rút 1 bằng withdraw synchronized cho đến khi false, tổng cộng số dư bằng 1000")
    void b02_eightThreadsSynchronizedSumIs1000() {
        BigDecimal opening = new BigDecimal("1000");
        SafeAccount account = new SafeAccount(opening);
        int successes = drain(8, () -> account.withdraw(BigDecimal.ONE));
        assertDrained(account, opening, successes);
    }

    @Test
    @DisplayName("B3: 8 thread rút 1 bằng withdrawLocked cho đến khi false, tổng cộng số dư bằng 1000")
    void b03_eightThreadsLockSumIs1000() {
        BigDecimal opening = new BigDecimal("1000");
        SafeAccount account = new SafeAccount(opening);
        ReentrantLock lock = new ReentrantLock();
        int successes = drain(8, () -> account.withdrawLocked(lock));
        assertDrained(account, opening, successes);
        assertFalse(lock.isLocked(), "Sau khi các thread kết thúc, ReentrantLock không còn bị giữ.");
    }

    private static void assertDrained(SafeAccount account, BigDecimal opening, int successes) {
        assertEquals(0, account.balance().compareTo(BigDecimal.ZERO),
                "Rút 1 cho đến khi trả false từ số dư 1000 thì số dư cuối phải bằng 0.");
        assertEquals(1000, successes, "Số lần rút 1 thành công phải bằng 1000.");
        assertEquals(0, account.balance().add(BigDecimal.valueOf(successes)).compareTo(opening),
                "Tổng lần rút thành công cộng số dư cuối phải bằng 1000.");
    }

    private static int drain(int threads, BooleanSupplier withdraw) {
        CountDownLatch start = new CountDownLatch(1);
        int[] counts = new int[threads];
        Throwable[] failures = new Throwable[threads];
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            int index = i;
            workers[i] = new Thread(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    failures[index] = new IllegalStateException("Thread bị ngắt khi chờ cổng xuất phát.", e);
                    return;
                }
                try {
                    while (withdraw.getAsBoolean()) {
                        counts[index]++;
                    }
                } catch (Throwable thrown) {
                    failures[index] = thrown;
                }
            }, "withdraw-" + i);
            workers[i].setDaemon(true);
            workers[i].start();
        }
        start.countDown();
        for (int i = 0; i < threads; i++) {
            try {
                workers[i].join(10_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Thread test bị ngắt khi join.", e);
            }
            assertFalse(workers[i].isAlive(),
                    "Thread còn sống sau khi join tối đa 10 giây: " + workers[i].getName());
            Throwable failure = failures[i];
            if (failure instanceof RuntimeException runtime) {
                throw runtime;
            }
            if (failure instanceof Error error) {
                throw error;
            }
            if (failure != null) {
                throw new IllegalStateException(failure);
            }
        }
        int successes = 0;
        for (int count : counts) {
            successes += count;
        }
        return successes;
    }
}
