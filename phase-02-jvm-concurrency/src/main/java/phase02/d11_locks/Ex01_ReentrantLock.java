package phase02.d11_locks;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock API — Bài 1: ReentrantLock
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 11 (Lock API), câu 1, 2, 3, 4, 5.
 * Cần làm trước: d08_synchronized.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_ReentrantLockTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] ReentrantLock cung cấp gì hơn synchronized?
 *   Bắt đầu   : viết khối ANSWER Q1. Ctrl+N mở java.util.concurrent.locks.ReentrantLock, Ctrl+Q đọc
 *               phần đầu Javadoc. Ctrl+F12 nhìn {@code tryLock}, {@code lockInterruptibly},
 *               {@code newCondition} và constructor có tham số fairness.
 *   Hoàn thành khi: viết xong khối ANSWER Q1.
 * <p>
 * Q2 [CODE] Vì sao unlock phải nằm trong finally?
 *   Bắt đầu   : cài đặt {@code withLock(ReentrantLock, Runnable)}: {@code lock}, chạy {@code body}
 *               trong {@code try}, {@code unlock} trong {@code finally}.
 *   Kiểm chứng: chạy q02_unlocksWhenBodyThrows. Nếu sai, Debug test, F7 vào withLock.
 *   Hoàn thành khi: q02 xanh; body ném {@code IllegalStateException} và sau đó {@code tryLock} lấy được khóa.
 * <p>
 * Q3 [CODE] {@code tryLock()} hữu ích khi nào?
 *   Bắt đầu   : cài đặt {@code tryWithdraw(ReentrantLock, AtomicInteger)}. Dùng {@code tryLock}.
 *               Chỉ giảm số dư khi đã lấy khóa và số dư &gt; 0. Nếu đã lock thì {@code unlock} trước khi trả về.
 *   Kiểm chứng: chạy q03_decrementsWhenPositive và q03_returnsFalseWhenLockHeld. Không dùng
 *               {@code Thread.sleep}. Thread giữ khóa và thread gọi tryWithdraw {@code join} tối đa 10 giây.
 *   Hoàn thành khi: hai test q03_* xanh; lời gọi không kẹt khi thread khác đang giữ khóa.
 * <p>
 * Q4 [CODE] Reentrant nghĩa là gì?
 *   Bắt đầu   : cài đặt {@code lockTwice(ReentrantLock)} để cùng một thread gọi {@code lock} hai lần
 *               rồi {@code unlock} hai lần. Trả {@code getHoldCount()} lúc đang giữ cả hai lần.
 *               Ctrl+F12 trong ReentrantLock, Ctrl+Q trên {@code getHoldCount}.
 *   Kiểm chứng: chạy q04_sameThreadLocksTwice. Nếu thread không kết thúc, Debug test, F7 vào lockTwice.
 *   Hoàn thành khi: q04 xanh; method chạy xong và khóa không còn bị giữ.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Khi nào synchronized đơn giản và tốt hơn ReentrantLock?
 *   Bắt đầu   : viết khối ANSWER Q5 sau khi đã chạy q02 và q04.
 *   Tra cứu   : Ctrl+N gõ Gate, mở phase02.d08_synchronized, Ctrl+F12 nhìn enter và leave.
 *   Hoàn thành khi: viết xong khối ANSWER Q5.
 */
public class Ex01_ReentrantLock {

    /**
     * Giữ {@code lock}, chạy {@code body}, rồi {@code unlock} trong {@code finally}.
     */
    static void withLock(ReentrantLock lock, Runnable body) {
        // SOLUTION-BEGIN throw Q2
        lock.lock();
        try {
            body.run();
        } finally {
            lock.unlock();
        }
        // SOLUTION-END
    }

    /**
     * {@code tryLock}. Nếu lấy được khóa và số dư &gt; 0 thì giảm 1.
     * Nếu đã lock thì {@code unlock} trước khi trả về.
     *
     * @return {@code true} khi đã giảm số dư
     */
    static boolean tryWithdraw(ReentrantLock lock, AtomicInteger balance) {
        // SOLUTION-BEGIN throw Q3
        if (!lock.tryLock()) {
            return false;
        }
        try {
            if (balance.get() <= 0) {
                return false;
            }
            balance.decrementAndGet();
            return true;
        } finally {
            lock.unlock();
        }
        // SOLUTION-END
    }

    /**
     * Cùng thread gọi {@code lock} hai lần rồi {@code unlock} hai lần.
     *
     * @return hold count lúc đang giữ cả hai lần
     */
    static int lockTwice(ReentrantLock lock) {
        // SOLUTION-BEGIN throw Q4
        lock.lock();
        try {
            lock.lock();
            try {
                return lock.getHoldCount();
            } finally {
                lock.unlock();
            }
        } finally {
            lock.unlock();
        }
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * ReentrantLock cho tryLock, tryLock có hạn giờ, và lockInterruptibly.
 * Constructor chọn fairness. Một lock có nhiều Condition, mỗi Condition một hàng chờ.
 * synchronized chỉ có một wait set trên monitor và không có tryLock hay fairness.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Nếu body ném và unlock nằm sau khối try, dòng unlock không chạy, thread vẫn giữ khóa.
 * Thread khác gọi lock sẽ chờ mãi vì không ai nhả.
 * finally chạy dù body ném hay return, nên unlock luôn được gọi khi đã lock.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * tryLock trả về ngay, không chờ khi thread khác đang giữ khóa.
 * Hữu ích khi thread còn việc khác nếu không lấy được khóa, hoặc muốn tránh chờ vô hạn.
 * tryWithdraw chỉ giảm số dư khi tryLock thành công và số dư còn dương, rồi unlock nếu đã lock.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Reentrant nghĩa là cùng một thread lấy lại được lock nó đang giữ, không tự chờ.
 * lock hai lần làm hold count thành 2. Phải unlock đúng hai lần thì hold count về 0.
 * Thread khác vẫn bị chặn khi hold count còn lớn hơn 0.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * synchronized đơn giản hơn khi chỉ cần loại trừ lẫn nhau và khối tự nhả monitor khi ra khỏi khối.
 * Không phải nhớ unlock, và JVM tối ưu intrinsic lock tốt cho trường hợp này.
 * Chọn ReentrantLock khi cần tryLock, fairness, lockInterruptibly hoặc nhiều Condition.
 * SOLUTION-END
 */
