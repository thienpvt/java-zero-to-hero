package phase02.d11_locks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ReentrantLockTest {

    @Test
    @DisplayName("Q2: body ném IllegalStateException thì finally vẫn unlock, tryLock sau đó thành công")
    void q02_unlocksWhenBodyThrows() {
        ReentrantLock lock = new ReentrantLock();
        boolean[] ran = { false };
        Ex01_ReentrantLock.withLock(lock, () -> ran[0] = true);
        assertTrue(ran[0], "withLock phải chạy body.");
        assertFalse(lock.isHeldByCurrentThread(), "withLock phải unlock sau khi body chạy xong.");
        assertFalse(lock.isLocked(), "Sau withLock, không còn thread nào giữ khóa.");

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () ->
                Ex01_ReentrantLock.withLock(lock, () -> {
                    throw new IllegalStateException("Thân critical section ném.");
                }));
        assertEquals("Thân critical section ném.", thrown.getMessage(),
                "Ngoại lệ của body phải thoát ra ngoài withLock.");
        assertFalse(lock.isHeldByCurrentThread(), "Sau khi body ném, thread test không còn giữ khóa.");
        assertTrue(lock.tryLock(), "Sau khi body ném, tryLock phải thành công.");
        assertEquals(1, lock.getHoldCount(),
                "tryLock sau khi đã unlock phải giữ đúng một lần, không phải lần reentrant còn sót.");
        lock.unlock();
        assertFalse(lock.isLocked(), "Test phải nhả lần tryLock vừa lấy.");
    }

    @Test
    @DisplayName("Q3: số dư dương thì giảm 1 và nhả khóa; số dư không dương thì trả false")
    void q03_decrementsWhenPositive() {
        ReentrantLock lock = new ReentrantLock();
        AtomicInteger balance = new AtomicInteger(2);
        assertTrue(Ex01_ReentrantLock.tryWithdraw(lock, balance),
                "Số dư dương và khóa rảnh thì tryWithdraw trả true.");
        assertEquals(1, balance.get(), "Mỗi lần rút thành công giảm đúng 1.");
        assertFalse(lock.isLocked(), "Sau tryWithdraw, khóa phải được nhả.");
        assertTrue(lock.tryLock(), "Sau khi nhả, tryLock phải thành công.");
        lock.unlock();

        assertTrue(Ex01_ReentrantLock.tryWithdraw(lock, balance),
                "Số dư còn 1 thì lần rút tiếp theo vẫn thành công.");
        assertEquals(0, balance.get(), "Hai lần rút từ số dư 2 phải về 0.");
        assertFalse(Ex01_ReentrantLock.tryWithdraw(lock, balance),
                "Số dư không dương thì không rút và trả false.");
        assertEquals(0, balance.get(), "Lần rút thất bại không được đổi số dư.");
        assertFalse(lock.isLocked(), "Dù không rút được, khóa đã lấy vẫn phải được nhả.");
    }

    @Test
    @DisplayName("Q3: tryLock khi thread khác đang giữ khóa trả false và không kẹt")
    void q03_returnsFalseWhenLockHeld() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        AtomicInteger balance = new AtomicInteger(4);
        CountDownLatch held = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        Thread holder = new Thread(() -> {
            lock.lock();
            try {
                held.countDown();
                release.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }, "holder");
        holder.setDaemon(true);
        holder.start();
        assertTrue(held.await(10, TimeUnit.SECONDS),
                "Thread giữ khóa không báo đã giữ khóa trong 10 giây.");

        boolean[] withdrew = { true };
        Throwable[] failure = { null };
        Thread caller = new Thread(() -> {
            try {
                withdrew[0] = Ex01_ReentrantLock.tryWithdraw(lock, balance);
            } catch (Throwable thrown) {
                failure[0] = thrown;
            }
        }, "withdraw");
        caller.setDaemon(true);
        caller.start();
        try {
            caller.join(10_000);
            assertFalse(caller.isAlive(), "Thread còn sống sau 10 giây: withdraw");
            if (failure[0] instanceof RuntimeException runtime) {
                throw runtime;
            }
            if (failure[0] instanceof Error error) {
                throw error;
            }
            if (failure[0] != null) {
                throw new IllegalStateException(failure[0]);
            }
            assertFalse(withdrew[0], "tryLock khi thread khác đang giữ khóa phải trả false.");
            assertEquals(4, balance.get(), "Không được giảm số dư khi không lấy được khóa.");
        } finally {
            release.countDown();
            holder.join(10_000);
        }
        assertFalse(holder.isAlive(), "Thread còn sống sau 10 giây: holder");
        assertTrue(Ex01_ReentrantLock.tryWithdraw(lock, balance),
                "Sau khi holder nhả khóa, tryWithdraw phải rút được.");
        assertEquals(3, balance.get(), "Một lần rút thành công từ số dư 4 còn 3.");
        assertFalse(lock.isLocked(), "Sau lần rút thành công, khóa phải được nhả.");
    }

    @Test
    @DisplayName("Q4: cùng thread lock hai lần rồi unlock hai lần, không kẹt")
    void q04_sameThreadLocksTwice() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        int[] holdCount = { -1 };
        Throwable[] failure = { null };
        Thread same = new Thread(() -> {
            try {
                holdCount[0] = Ex01_ReentrantLock.lockTwice(lock);
            } catch (Throwable thrown) {
                failure[0] = thrown;
            }
        }, "reenter");
        same.setDaemon(true);
        same.start();
        same.join(10_000);
        assertFalse(same.isAlive(), "Thread còn sống sau 10 giây: reenter");
        if (failure[0] instanceof RuntimeException runtime) {
            throw runtime;
        }
        if (failure[0] instanceof Error error) {
            throw error;
        }
        if (failure[0] != null) {
            throw new IllegalStateException(failure[0]);
        }
        assertEquals(2, holdCount[0], "Cùng thread lock hai lần thì hold count phải là 2.");
        assertFalse(lock.isLocked(), "Unlock hai lần phải nhả hết khóa.");
        assertTrue(lock.tryLock(), "Sau hai lần unlock, tryLock của thread khác phải thành công.");
        assertEquals(1, lock.getHoldCount(),
                "tryLock sau khi nhả hết phải giữ đúng một lần.");
        lock.unlock();
    }
}
