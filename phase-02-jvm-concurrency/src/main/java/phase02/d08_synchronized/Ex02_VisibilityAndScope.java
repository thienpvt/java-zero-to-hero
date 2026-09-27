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
    static final Boolean Q6_ALSO_VISIBILITY = true; // SOLUTION-VALUE

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
            // SOLUTION-BEGIN throw Q7
            lastAttempt = "rut " + amount;
            synchronized (this) {
                if (amount > 0 && balance >= amount) {
                    balance -= amount;
                    successes++;
                }
            }
            // SOLUTION-END
        }

        synchronized int balance() {
            // SOLUTION-BEGIN throw Q7
            return balance;
            // SOLUTION-END
        }

        synchronized int successfulWithdrawals() {
            // SOLUTION-BEGIN throw Q7
            return successes;
            // SOLUTION-END
        }

        String lastAttempt() {
            return lastAttempt;
        }
    }
}

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * synchronized không chỉ làm critical section atomic. Nhả monitor happens-before lần lấy cùng monitor sau đó.
 * Thread sau nhìn thấy mọi ghi mà thread trước đã thực hiện trước khi nhả khóa.
 * Đó là visibility. Biến thường không có quan hệ này nên không dùng hai thread để "chứng minh" mất giá trị.
 * SOLUTION-END
 */

/* ANSWER Q7:
 * SOLUTION-BEGIN
 * Synchronized method khóa cả phần không đụng trạng thái chung, critical section dài hơn mức cần.
 * withdraw ghi mô tả lần rút bên ngoài, rồi chỉ synchronize khối kiểm tra và trừ balance.
 * Cùng monitor this: lần nhả khóa happens-before balance() của thread khác, nên số dư đọc được không âm
 * và số lần rút thành công cộng số dư còn lại bằng số dư mở.
 * SOLUTION-END
 */
