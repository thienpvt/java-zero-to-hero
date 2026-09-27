package phase02.d08_synchronized;

/**
 * synchronized — Bài 2: Visibility và phạm vi khóa
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 8 (synchronized), câu 6, 7.
 * Cần làm trước: Ex01_WhichLock.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex02_VisibilityAndScopeTest bằng nút ▶
 * (Ctrl+Shift+F10).
 * <p>
 * Q6 [DỰ ĐOÁN] synchronized chỉ giải quyết atomicity hay còn visibility?
 *   Bắt đầu   : điền Q6_ALSO_VISIBILITY (thay null). Viết ANSWER Q6 và nhắc happens-before của monitor.
 *   Kiểm chứng: chạy q06_prediction. Đừng tạo hai thread để chứng minh biến thường bị mất.
 *   Hoàn thành khi: q06_prediction xanh và viết xong khối ANSWER Q6.
 * <p>
 * Q7 [CODE] Vì sao synchronized block đôi khi tốt hơn synchronized method?
 *   Bắt đầu   : cài đặt {@code Account.withdraw(int)} và {@code Account.balance()}.
 *               Phần mô tả lần rút không đụng số dư thì để ngoài khối synchronized.
 *               Chỉ đọc và trừ {@code balance} nằm trong {@code synchronized (this)}.
 *   Kiểm chứng: chạy q07_withdrawOne và q07_concurrentWithdrawals. Nhiều thread cùng rút 1
 *               từ một số dư mở, cổng {@code CountDownLatch}, {@code join} tối đa 10 giây.
 *               Không dùng {@code Thread.sleep}.
 *   Hoàn thành khi: hai test q07_* xanh; số lần rút thành công cộng số dư cuối bằng số dư mở;
 *               đọc {@code balance()} dưới cùng monitor không thấy số dư âm.
 */
public class Ex02_VisibilityAndScope {

    // Q6 — synchronized có lập happens-before (visibility) hay chỉ loại trừ lẫn nhau.
    static final Boolean Q6_ALSO_VISIBILITY = null;

    /**
     * Tài khoản. Khối synchronized chỉ bọc phần đụng số dư.
     * {@code lastAttempt} là mô tả lần rút, không tham gia số dư.
     */
    static final class Account {
        private int balance;
        private int successes;
        private String lastAttempt = "";

        Account(int openingBalance) {
            if (openingBalance < 0) {
                throw new IllegalArgumentException("Số dư mở không được âm: " + openingBalance);
            }
            this.balance = openingBalance;
        }

        void withdraw(int amount) {
            throw new UnsupportedOperationException("TODO Q7");
        }

        synchronized int balance() {
            throw new UnsupportedOperationException("TODO Q7");
        }

        synchronized int successfulWithdrawals() {
            throw new UnsupportedOperationException("TODO Q7");
        }

        String lastAttempt() {
            return lastAttempt;
        }
    }
}

/* ANSWER Q6:
 *
 */

/* ANSWER Q7:
 *
 */
