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
    static final Boolean Q5_INTERRUPT_STOPS_IMMEDIATELY = null;

    // Q7 — hằng hỏi liệu daemon thread có giữ JVM sống hay không.
    static final Boolean Q7_DAEMON_KEEPS_JVM = null;

    /**
     * Gọi {@code body}. Nếu {@code body} ném {@code InterruptedException}, khôi phục cờ interrupt
     * của thread hiện tại rồi ném {@code IllegalStateException} với exception đó làm cause.
     * Không nuốt {@code InterruptedException} và không xóa cờ vừa khôi phục.
     * Sau khi method xử lý xong, {@code Thread.interrupted()} trả {@code true} đúng một lần.
     *
     * @throws IllegalStateException khi {@code body} ném {@code InterruptedException} hoặc exception khác
     */
    static void rethrowOrRestore(Callable<Void> body) {
        throw new UnsupportedOperationException("TODO Q6");
    }

    /**
     * Tạo một thread mới, gọi {@code setDaemon(true)}, rồi trả về thread đó.
     * Không gọi {@code start()}.
     */
    static Thread daemon() {
        throw new UnsupportedOperationException("TODO Q7");
    }
}

/* ANSWER Q5:
 *
 */

/* ANSWER Q6:
 *
 */

/* ANSWER Q7:
 *
 */

/* ANSWER Q8:
 *
 */
