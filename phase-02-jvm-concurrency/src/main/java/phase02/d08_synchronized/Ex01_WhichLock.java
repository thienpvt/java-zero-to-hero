package phase02.d08_synchronized;

/**
 * synchronized — Bài 1: Khóa nào
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 8 (synchronized), câu 1, 2, 3, 4, 5, 8.
 * Cần làm trước: d07_race (SafeCounter).
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_WhichLockTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] {@code synchronized} lock object nào?
 *   Bắt đầu   : điền hằng Q1_LOCKS_ONE_GLOBAL (thay null).
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và viết xong khối ANSWER Q1.
 * <p>
 * Q2 [DỰ ĐOÁN] synchronized instance method lock gì?
 *   Bắt đầu   : Ctrl+F12 trong lớp này, nhìn method instance có {@code synchronized},
 *               điền Q2_INSTANCE_LOCK (thay null).
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và viết xong khối ANSWER Q2.
 * <p>
 * Q3 [DỰ ĐOÁN] synchronized static method lock gì?
 *   Bắt đầu   : điền Q3_STATIC_LOCK (thay null). So với Q2: method static không có instance.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và viết xong khối ANSWER Q3.
 * <p>
 * Q4 [CODE] Hai synchronized methods của cùng object có chạy song song được không?
 *   Bắt đầu   : cài đặt {@code Gate.enter()} và {@code Gate.leave()} để đếm số người đang ở trong,
 *               tổng lần vào và tổng lần ra. Cả hai method synchronized trên cùng instance.
 *   Kiểm chứng: chạy q04_sameObjectCounters. Bốn thread cùng xuất phát qua {@code CountDownLatch},
 *               mỗi thread gọi enter rồi leave 1000 lần. Không dùng {@code Thread.sleep}.
 *   Hoàn thành khi: q04 xanh; {@code inside()} bằng 0 và tổng enter bằng tổng leave.
 * <p>
 * Q5 [DỰ ĐOÁN] Nếu synchronize trên hai object khác nhau thì sao?
 *   Bắt đầu   : điền Q5_DIFFERENT_INSTANCES_BLOCK (thay null). Nhớ Q1: mỗi object một monitor.
 *   Kiểm chứng: chạy q05_prediction. Đừng viết test chờ timeout để xem hai instance có chặn nhau.
 *   Hoàn thành khi: q05_prediction xanh và viết xong khối ANSWER Q5.
 * <p>
 * Q8 [CODE] Java intrinsic locks có reentrant không?
 *   Bắt đầu   : cài đặt {@code Reentry.depth()} để gọi {@code nested()} — method synchronized khác
 *               trên cùng {@code this} — và trả độ sâu của lời gọi lồng nhau. Điền Q8_REENTRANT
 *               (thay null) theo việc lời gọi đó chạy xong trên chính thread gọi nó.
 *   Kiểm chứng: chạy q08_reentryDepth trên thread của test. F7 vào depth() nếu muốn thấy
 *               lời gọi synchronized lồng nhau. Không cần {@code join}: không có thread khác.
 *   Hoàn thành khi: q08_reentryDepth xanh và depth() không tự deadlock.
 */
public class Ex01_WhichLock {

    // Q1 — synchronized có dùng một khóa toàn cục cho mọi object không?
    static final Boolean Q1_LOCKS_ONE_GLOBAL = null;

    // Q2 — monitor mà synchronized instance method lấy.
    static final String Q2_INSTANCE_LOCK = null;

    // Q3 — monitor mà synchronized static method lấy.
    static final String Q3_STATIC_LOCK = null;

    // Q5 — hai instance khác nhau có chặn lẫn nhau không?
    static final Boolean Q5_DIFFERENT_INSTANCES_BLOCK = null;

    // Q8 — cùng thread gọi depth() có lấy lại được monitor hay bị kẹt.
    static final Boolean Q8_REENTRANT = null;

    /** Hai method synchronized cùng instance. Bộ đếm chỉ đúng khi chúng không chạy song song. */
    static final class Gate {
        private int inside;
        private int entered;
        private int left;

        synchronized void enter() {
            throw new UnsupportedOperationException("TODO Q4");
        }

        synchronized void leave() {
            throw new UnsupportedOperationException("TODO Q4");
        }

        synchronized int inside() {
            return inside;
        }

        synchronized int entered() {
            return entered;
        }

        synchronized int left() {
            return left;
        }
    }

    /**
     * {@code depth()} gọi {@code nested()} trong khi đang giữ monitor của chính instance.
     * Cùng thread phải vào được lần thứ hai.
     */
    static final class Reentry {
        synchronized int depth() {
            throw new UnsupportedOperationException("TODO Q8");
        }

        private synchronized int nested() {
            throw new UnsupportedOperationException("TODO Q8");
        }
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

/* ANSWER Q8:
 *
 */
