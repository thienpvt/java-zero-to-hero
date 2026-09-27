package phase02.d19_capstone;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bài tích hợp — SafeAccount (khóa cặp kiểm tra và cập nhật)
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục Practical Exercise.
 * Cần làm trước: Account, d08_synchronized, d11_locks.
 * Cách làm: làm lần lượt B2 rồi B3; chạy test trong SafeAccountTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * B2 [CODE] Sửa rút tiền bằng {@code synchronized} trên {@code this} cho cả lần kiểm tra và lần cập nhật.
 *   Bắt đầu   : cài thân {@code withdraw}. Method đã {@code synchronized}. So sánh số dư rồi trừ
 *               trong thân đó. Trả {@code false} khi số dư không đủ và không đổi số dư.
 *   Kiểm chứng: chạy b02_eightThreadsSynchronizedSumIs1000. Nếu sai, Debug test, F7 vào withdraw.
 *   Hoàn thành khi: b02 xanh. Tám thread rút 1 cho đến khi method trả {@code false}; tổng lần
 *               thành công cộng số dư cuối bằng số dư đầu.
 * <p>
 * B3 [CODE] {@code withdrawLocked(ReentrantLock)} khóa đúng cặp kiểm tra và cập nhật, {@code unlock}
 *     trong {@code finally}.
 *   Bắt đầu   : cài {@code withdrawLocked}. Ctrl+N mở {@code ReentrantLock}, Ctrl+Q đọc {@code lock}
 *               và {@code unlock}. Mỗi lời gọi rút 1. Chỉ giữ khóa quanh cặp đọc số dư và ghi số dư.
 *   Kiểm chứng: chạy b03_eightThreadsLockSumIs1000. Nếu thread không kết thúc, F7 vào withdrawLocked
 *               và xem {@code unlock} có nằm trong {@code finally} không.
 *   Hoàn thành khi: b03 xanh, cùng bất biến với B2, và khóa không còn bị giữ sau khi các thread kết thúc.
 * <p>
 * B4 [TỰ TRẢ LỜI] Nếu {@code Account} nằm trong hệ phân tán thì khóa Java có đủ không?
 *   Bắt đầu   : viết khối ANSWER B4 sau khi b02 và b03 xanh.
 *   Hoàn thành khi: ANSWER B4 giải thích khóa của một JVM không bao phủ số dư nằm ở database
 *               của process khác.
 */
public class SafeAccount {

    private BigDecimal balance;

    public SafeAccount(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal balance() {
        return balance;
    }

    /**
     * Rút {@code amount} khi số dư đủ. Monitor của {@code this} giữ cả lần kiểm tra và lần gán.
     *
     * @param amount số tiền rút
     * @return {@code true} khi đã trừ {@code amount}
     */
    public synchronized boolean withdraw(BigDecimal amount) {
        throw new UnsupportedOperationException("TODO B2");
    }

    /**
     * Rút {@code BigDecimal.ONE} khi số dư đủ. {@code lock} chỉ quanh cặp kiểm tra và cập nhật;
     * {@code unlock} nằm trong {@code finally}.
     *
     * @param lock khóa dùng chung cho mọi thread rút trên account này
     * @return {@code true} khi đã trừ 1
     */
    public boolean withdrawLocked(ReentrantLock lock) {
        throw new UnsupportedOperationException("TODO B3");
    }
}

/* ANSWER B4:
 *
 */
