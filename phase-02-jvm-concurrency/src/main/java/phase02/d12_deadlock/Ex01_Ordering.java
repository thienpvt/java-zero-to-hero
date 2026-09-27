package phase02.d12_deadlock;

/**
 * Deadlock — Bài 1: Thứ tự khóa
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 12 (Deadlock), câu 1, 2, 3, 6, 7.
 * Cần làm trước: d08_synchronized (Ex01_WhichLock).
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_OrderingTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] Deadlock là gì?
 *   Bắt đầu   : viết khối ANSWER Q1. Mô tả trạng thái các thread và vì sao không bên nào tiến được.
 *   Hoàn thành khi: viết xong khối ANSWER Q1.
 * <p>
 * Q2 [DỰ ĐOÁN] Hai threads phải có ít nhất bao nhiêu lock để tạo ví dụ deadlock điển hình?
 *   Bắt đầu   : điền hằng Q2_MIN_LOCKS (thay null).
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và viết xong khối ANSWER Q2.
 * <p>
 * Q3 [CODE] Lock ordering giúp tránh deadlock thế nào?
 *   Bắt đầu   : cài {@code transfer(Object, Object, Runnable)}. Lấy khóa theo
 *               {@code System.identityHashCode} tăng dần. Nếu hai mã bằng nhau thì lấy
 *               {@code TIE} trước, rồi mới lấy hai object. {@code move} chạy khi đang giữ các khóa đó.
 *   Kiểm chứng: chạy q03_oneTransfer và q03_oppositeTransfers. Hai thread chuyển ngược chiều
 *               1000 lần, cổng {@code CountDownLatch}, {@code join} tối đa 10 giây.
 *               Không dùng {@code Thread.sleep}. Không viết test chờ deadlock thật.
 *   Hoàn thành khi: hai test q03_* xanh; cả hai thread kết thúc; tổng tiền không đổi.
 * <p>
 * Q6 [DỰ ĐOÁN + TỰ TRẢ LỜI] Làm sao diagnose deadlock trong JVM?
 *   Bắt đầu   : Ctrl+F12 tìm hằng {@code DUMP}. Đó là thread dump đã dán sẵn, không phải kết quả
 *               của {@code jcmd}. Điền Q6_DUMP_SHOWS_DEADLOCK theo {@code DUMP.contains("deadlock")}
 *               (thay null). Không gọi {@code jcmd}, {@code jstack} hay JFR.
 *   Kiểm chứng: chạy q06_prediction. Muốn tính biểu thức trên chuỗi: Debug test, Alt+F8.
 *   Hoàn thành khi: q06_prediction xanh và viết xong khối ANSWER Q6.
 * <p>
 * Q7 [DỰ ĐOÁN + TỰ TRẢ LỜI] Thread dump có thể giúp gì?
 *   Bắt đầu   : đọc tiếp {@code DUMP}, viết khối ANSWER Q7. Nêu monitor và stack trong đoạn dump.
 *   Hoàn thành khi: viết xong khối ANSWER Q7.
 */
public class Ex01_Ordering {

    /** Khóa phụ khi hai object có cùng {@code System.identityHashCode}. */
    static final Object TIE = new Object();

    /**
     * Đoạn thread dump đã dán sẵn. Đọc chuỗi này; đừng gọi {@code jcmd} trong test.
     * Có dòng {@code Found one Java-level deadlock} và hai thread {@code waiting to lock}.
     */
    static final String DUMP = ""
            + "Found one Java-level deadlock:\n"
            + "=============================\n"
            + "\"transfer-a\":\n"
            + "  waiting to lock <0x00000000ab10e0a0> (a java.lang.Object)\n"
            + "  which is held by \"transfer-b\"\n"
            + "\"transfer-b\":\n"
            + "  waiting to lock <0x00000000ab10e0b8> (a java.lang.Object)\n"
            + "  which is held by \"transfer-a\"\n"
            + "\n"
            + "Java stack information for the threads listed above:\n"
            + "===================================================\n"
            + "\"transfer-a\":\n"
            + "\tat phase02.d12_deadlock.Ex01_Ordering.transfer(Ex01_Ordering.java:1)\n"
            + "\t- waiting to lock <0x00000000ab10e0a0> (a java.lang.Object)\n"
            + "\t- locked <0x00000000ab10e0b8> (a java.lang.Object)\n"
            + "\"transfer-b\":\n"
            + "\tat phase02.d12_deadlock.Ex01_Ordering.transfer(Ex01_Ordering.java:1)\n"
            + "\t- waiting to lock <0x00000000ab10e0b8> (a java.lang.Object)\n"
            + "\t- locked <0x00000000ab10e0a0> (a java.lang.Object)\n";

    // Q2 — số lock tối thiểu của ví dụ deadlock điển hình với hai thread.
    static final Integer Q2_MIN_LOCKS = null;

    // Q6 — kết quả DUMP.contains("deadlock"). Đọc chuỗi DUMP, đừng gọi jcmd.
    static final Boolean Q6_DUMP_SHOWS_DEADLOCK = null;

    /**
     * Hai số dư. {@code credit} và {@code debit} không tự lấy khóa.
     * Gọi chúng từ {@code move} khi {@code transfer} đang giữ monitor của cả hai account.
     */
    static final class Account {
        private int balance;

        Account(int opening) {
            if (opening < 0) {
                throw new IllegalArgumentException("Số dư mở không được âm: " + opening);
            }
            this.balance = opening;
        }

        void credit(int amount) {
            balance += amount;
        }

        void debit(int amount) {
            balance -= amount;
        }

        int balance() {
            return balance;
        }
    }

    /**
     * Lấy monitor của {@code a} và {@code b} theo {@code System.identityHashCode} tăng dần,
     * rồi chạy {@code move}. Nếu hai mã hash bằng nhau thì lấy {@code TIE} trước.
     */
    static void transfer(Object a, Object b, Runnable move) {
        throw new UnsupportedOperationException("TODO Q3");
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

/* ANSWER Q6:
 *
 */

/* ANSWER Q7:
 *
 */
