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
    static final Boolean Q1_LOCKS_ONE_GLOBAL = false; // SOLUTION-VALUE

    // Q2 — monitor mà synchronized instance method lấy.
    static final String Q2_INSTANCE_LOCK = "this"; // SOLUTION-VALUE

    // Q3 — monitor mà synchronized static method lấy.
    static final String Q3_STATIC_LOCK = "Class"; // SOLUTION-VALUE

    // Q5 — hai instance khác nhau có chặn lẫn nhau không?
    static final Boolean Q5_DIFFERENT_INSTANCES_BLOCK = false; // SOLUTION-VALUE

    // Q8 — cùng thread gọi depth() có lấy lại được monitor hay bị kẹt.
    static final Boolean Q8_REENTRANT = true; // SOLUTION-VALUE

    /** Hai method synchronized cùng instance. Bộ đếm chỉ đúng khi chúng không chạy song song. */
    static final class Gate {
        private int inside;
        private int entered;
        private int left;

        synchronized void enter() {
            // SOLUTION-BEGIN throw Q4
            inside++;
            entered++;
            // SOLUTION-END
        }

        synchronized void leave() {
            // SOLUTION-BEGIN throw Q4
            inside--;
            left++;
            // SOLUTION-END
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
            // SOLUTION-BEGIN throw Q8
            return 1 + nested();
            // SOLUTION-END
        }

        private synchronized int nested() {
            // SOLUTION-BEGIN throw Q8
            return 1;
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * synchronized khóa monitor của một object, không phải một khóa toàn cục của JVM.
 * Hai thread chỉ loại trừ nhau khi cùng tranh một monitor.
 * Hai object khác nhau là hai monitor, dù cùng một lớp.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * synchronized instance method khóa monitor của this.
 * this là instance nhận lời gọi, không phải một object khóa riêng.
 * Thread khác gọi method synchronized trên cùng instance phải chờ monitor được nhả.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * synchronized static method không có this.
 * Nó khóa monitor của đối tượng Class đại diện cho lớp khai báo method.
 * Monitor đó khác monitor của từng instance, nên không chặn method instance synchronized.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Hai method synchronized của cùng object dùng chung monitor this nên không chạy song song.
 * Thread khác muốn vào enter hoặc leave phải chờ thread đang giữ monitor nhả khóa.
 * Vì vậy entered, left và inside không mất cập nhật: mỗi lần vào có một lần ra thì inside về 0.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Mỗi object một monitor. Synchronize trên object này không giữ monitor của object kia.
 * Thread đang ở trong method synchronized của instance A không chặn thread vào instance B.
 * Muốn loại trừ chung thì cả hai phía phải synchronize trên cùng một object.
 * SOLUTION-END
 */

/* ANSWER Q8:
 * SOLUTION-BEGIN
 * Intrinsic lock reentrant: cùng một thread lấy lại monitor nó đang giữ, không tự chờ.
 * depth() đã giữ this rồi gọi nested() cũng synchronized trên this, nên lời gọi lồng nhau chạy tiếp.
 * Độ sâu là lần vào ngoài cộng lần vào trong. Thread khác vẫn bị chặn khi monitor đang bị giữ.
 * SOLUTION-END
 */
