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
        throw new UnsupportedOperationException("TODO Q2");
    }

    /**
     * {@code tryLock}. Nếu lấy được khóa và số dư &gt; 0 thì giảm 1.
     * Nếu đã lock thì {@code unlock} trước khi trả về.
     *
     * @return {@code true} khi đã giảm số dư
     */
    static boolean tryWithdraw(ReentrantLock lock, AtomicInteger balance) {
        throw new UnsupportedOperationException("TODO Q3");
    }

    /**
     * Cùng thread gọi {@code lock} hai lần rồi {@code unlock} hai lần.
     *
     * @return hold count lúc đang giữ cả hai lần
     */
    static int lockTwice(ReentrantLock lock) {
        throw new UnsupportedOperationException("TODO Q4");
    }
}

/* ANSWER Q1:
 *
 */

/* ANSWER Q2:
 *
 */

/* ANSWER Q3:
 *
 */

/* ANSWER Q4:
 *
 */

/* ANSWER Q5:
 *
 */
