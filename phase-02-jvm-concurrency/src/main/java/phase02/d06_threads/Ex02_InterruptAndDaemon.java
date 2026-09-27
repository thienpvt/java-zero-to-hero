package phase02.d06_threads;

import java.util.concurrent.Callable;

/**
 * Thread — Bài 2: interrupt và daemon
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 6 (Thread Fundamentals), câu 5–8.
 * Cần làm trước: Ex01_StartAndTask.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex02_InterruptAndDaemonTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q5 [DỰ ĐOÁN] Interrupt có buộc thread dừng ngay lập tức không?
 *   Bắt đầu   : điền hằng Q5_INTERRUPT_STOPS_IMMEDIATELY (thay null).
 *   Tra cứu   : Ctrl+N → Thread → Ctrl+F12 → interrupt → Ctrl+Q.
 *   Kiểm chứng: chạy q05_prediction. Đừng tạo thread để chứng minh một thread đã dừng.
 *   Hoàn thành khi: test q05_prediction xanh và viết xong khối ANSWER Q5.
 * <p>
 * Q6 [CODE] InterruptedException nên được xử lý thế nào?
 *   Bắt đầu   : đọc Javadoc của {@code rethrowOrRestore}, rồi cài method đó.
 *   Kiểm chứng: chạy q06_restoresInterruptFlagOnce trên thread hiện tại.
 *   Hoàn thành khi: test q06_* xanh và viết xong khối ANSWER Q6.
 * <p>
 * Q7 [DỰ ĐOÁN + CODE] Daemon thread là gì?
 *   Bắt đầu   : cài {@code daemon()} trả một thread đã {@code setDaemon(true)}.
 *               Điền hằng Q7_DAEMON_KEEPS_JVM (thay null).
 *   Tra cứu   : Ctrl+N → Thread → Ctrl+F12 → setDaemon → Ctrl+Q.
 *   Kiểm chứng: chạy q07_prediction và q07_daemon_isDaemon. Không gọi {@code start()} trong test.
 *   Hoàn thành khi: hai test q07_* xanh và viết xong khối ANSWER Q7.
 * <p>
 * Q8 [TỰ TRẢ LỜI] Tạo một thread mới cho mỗi request có vấn đề gì?
 *   Bắt đầu   : làm Q7 trước, rồi viết khối ANSWER Q8.
 *   Hoàn thành khi: ANSWER Q8 nêu chi phí tạo thread và hệ quả khi số request tăng.
 */
public class Ex02_InterruptAndDaemon {

    // Q5 — hằng hỏi liệu interrupt có buộc thread dừng ngay hay không.
    static final Boolean Q5_INTERRUPT_STOPS_IMMEDIATELY = false; // SOLUTION-VALUE

    // Q7 — hằng hỏi liệu daemon thread có giữ JVM sống hay không.
    static final Boolean Q7_DAEMON_KEEPS_JVM = false; // SOLUTION-VALUE

    /**
     * Gọi {@code body}. Nếu {@code body} ném {@code InterruptedException}, khôi phục cờ interrupt
     * của thread hiện tại rồi ném {@code IllegalStateException} với exception đó làm cause.
     * Không nuốt {@code InterruptedException} và không xóa cờ vừa khôi phục.
     * Sau khi method xử lý xong, {@code Thread.interrupted()} trả {@code true} đúng một lần.
     *
     * @throws IllegalStateException khi {@code body} ném {@code InterruptedException} hoặc exception khác
     */
    static void rethrowOrRestore(Callable<Void> body) {
        // SOLUTION-BEGIN throw Q6
        try {
            body.call();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Tác vụ bị ngắt.", ex);
        } catch (Exception ex) {
            throw new IllegalStateException("Callable ném lỗi.", ex);
        }
        // SOLUTION-END
    }

    /**
     * Tạo một thread mới, gọi {@code setDaemon(true)}, rồi trả về thread đó.
     * Không gọi {@code start()}.
     */
    static Thread daemon() {
        // SOLUTION-BEGIN throw Q7
        Thread worker = new Thread(() -> { }, "worker-q7");
        worker.setDaemon(true);
        return worker;
        // SOLUTION-END
    }
}

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Không. interrupt chỉ đặt cờ ngắt. Nếu thread đang nằm trong một số lời gọi chặn, lời gọi đó ném InterruptedException.
 * Thread không bị giết. Nó phải tự đọc cờ hoặc bắt InterruptedException rồi tự dừng.
 * Vòng lặp không kiểm tra cờ thì thread tiếp tục chạy.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * InterruptedException nghĩa là lời gọi chặn bị ngắt, và cờ interrupt thường đã bị xóa.
 * Nếu method chưa xử lý xong việc hủy, phải gọi Thread.currentThread().interrupt() để khôi phục cờ.
 * Không nuốt exception bằng catch rỗng. rethrowOrRestore bọc nó trong IllegalStateException và giữ cause.
 * Caller gọi Thread.interrupted() sẽ thấy true đúng một lần, rồi cờ trở lại false.
 * SOLUTION-END
 */

/* ANSWER Q7:
 * SOLUTION-BEGIN
 * Daemon thread phục vụ nền. JVM thoát khi mọi thread không phải daemon đã kết thúc, kể cả khi daemon còn chạy.
 * setDaemon(true) phải gọi trước start(). Daemon không giữ JVM sống.
 * Đừng giao cho daemon việc phải hoàn tất, ví dụ ghi dữ liệu ra đĩa.
 * SOLUTION-END
 */

/* ANSWER Q8:
 * SOLUTION-BEGIN
 * Mỗi request một thread mới tốn stack riêng và chi phí tạo, hủy thread của hệ điều hành.
 * Số thread tăng theo số request, không có trần, nên tải cao gây context switch dày và có thể cạn bộ nhớ.
 * Request đến nhanh hơn tốc độ xử lý thì không có hàng đợi chặn bớt số thread.
 * Nên dùng pool có trần, hoặc virtual thread khi tác vụ chủ yếu chờ I/O.
 * SOLUTION-END
 */
