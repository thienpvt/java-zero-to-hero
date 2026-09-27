package phase02.d13_executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ExecutorService — Bài 2: Kích thước pool và rejection
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 13 (ExecutorService), câu 5–9.
 * Cần làm trước: Ex01_SubmitAndShutdown.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex02_PoolSizeAndRejectionTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q5 [TỰ TRẢ LỜI] Thread pool quá lớn gây vấn đề gì?
 *   Bắt đầu   : viết khối ANSWER Q5. Nêu bộ nhớ stack và chi phí chuyển ngữ cảnh.
 *   Hoàn thành khi: viết xong khối ANSWER Q5.
 * <p>
 * Q6 [TỰ TRẢ LỜI] Thread pool quá nhỏ gây vấn đề gì?
 *   Bắt đầu   : làm Q5 trước, rồi viết khối ANSWER Q6. Nêu task nằm trong queue và độ trễ.
 *   Hoàn thành khi: viết xong khối ANSWER Q6.
 * <p>
 * Q7 [TỰ TRẢ LỜI] CPU-bound và I/O-bound workload có cùng chiến lược pool size không?
 *   Bắt đầu   : điền Q7_SAME_POOL_SIZE_FOR_IO_AND_CPU (thay null) và viết khối ANSWER Q7.
 *               Ctrl+N → Runtime → Ctrl+F12 → availableProcessors → Ctrl+Q.
 *   Kiểm chứng: chạy q07_prediction.
 *   Hoàn thành khi: q07_prediction xanh và viết xong khối ANSWER Q7.
 * <p>
 * Q8 [DỰ ĐOÁN] Unbounded queue có rủi ro gì?
 *   Bắt đầu   : điền Q8_UNBOUNDED_QUEUE_CAN_OOM (thay null). Ctrl+N → LinkedBlockingQueue → Ctrl+Q,
 *               đọc sức chứa mặc định. Nhớ fixed pool của Executors dùng queue đó.
 *   Kiểm chứng: chạy q08_prediction.
 *   Hoàn thành khi: q08_prediction xanh và viết xong khối ANSWER Q8.
 * <p>
 * Q9 [CODE] Rejection policy được sử dụng khi nào?
 *   Bắt đầu   : cài {@code bounded(int, int)} bằng {@code ThreadPoolExecutor}, queue
 *               {@code ArrayBlockingQueue} đúng sức chứa {@code queue}, policy {@code AbortPolicy}.
 *               {@code n} là cả core lẫn maximum. Worker để daemon.
 *   Tra cứu   : Ctrl+N → ThreadPoolExecutor → Ctrl+F12 chọn constructor có queue và handler, Ctrl+Q.
 *               Ctrl+B vào {@code AbortPolicy}.
 *   Kiểm chứng: chạy q09_rejectsWhenPoolAndQueueAreFull. Submit khi pool và queue đều đầy phải ném
 *               {@code RejectedExecutionException}. Test đóng executor trong finally.
 *               Không dùng {@code Thread.sleep}.
 *   Hoàn thành khi: test q09_* xanh và viết xong khối ANSWER Q9.
 */
public class Ex02_PoolSizeAndRejection {

    // Q7 — CPU-bound và I/O-bound có cùng chiến lược pool size hay không.
    static final Boolean Q7_SAME_POOL_SIZE_FOR_IO_AND_CPU = null;

    // Q8 — hàng đợi không giới hạn có thể làm cạn bộ nhớ hay không.
    static final Boolean Q8_UNBOUNDED_QUEUE_CAN_OOM = null;

    /**
     * Pool {@code n} worker daemon, queue {@code ArrayBlockingQueue} sức chứa {@code queue},
     * policy {@code AbortPolicy}.
     */
    static ExecutorService bounded(int n, int queue) {
        throw new UnsupportedOperationException("TODO Q9");
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

/* ANSWER Q9:
 *
 */
