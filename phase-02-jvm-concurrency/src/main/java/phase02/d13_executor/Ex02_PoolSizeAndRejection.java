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
    static final Boolean Q7_SAME_POOL_SIZE_FOR_IO_AND_CPU = false; // SOLUTION-VALUE

    // Q8 — hàng đợi không giới hạn có thể làm cạn bộ nhớ hay không.
    static final Boolean Q8_UNBOUNDED_QUEUE_CAN_OOM = true; // SOLUTION-VALUE

    /**
     * Pool {@code n} worker daemon, queue {@code ArrayBlockingQueue} sức chứa {@code queue},
     * policy {@code AbortPolicy}.
     */
    static ExecutorService bounded(int n, int queue) {
        // SOLUTION-BEGIN throw Q9
        ThreadFactory workers = task -> {
            Thread worker = new Thread(task, "bounded-worker");
            worker.setDaemon(true);
            return worker;
        };
        return new ThreadPoolExecutor(
                n,
                n,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queue),
                workers,
                new ThreadPoolExecutor.AbortPolicy());
        // SOLUTION-END
    }
}

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Quá nhiều thread tốn bộ nhớ stack và thời gian chuyển ngữ cảnh.
 * Với việc tính CPU, thread nhiều hơn số lõi thường không tăng throughput.
 * Máy có thể hết thread native. Lập lịch nuốt phần thời gian đáng lẽ để tính.
 * Pool lớn cũng khó kìm tài nguyên dùng chung, ví dụ số kết nối.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Quá ít worker thì task nằm lâu trong queue, độ trễ tăng.
 * CPU hoặc thiết bị I/O có thể nhàn trong khi việc vẫn chờ được chạy.
 * Throughput bị trần bởi số worker dù máy còn tài nguyên.
 * Queue dài thêm còn giữ bộ nhớ nếu bên gửi không chậm lại.
 * SOLUTION-END
 */

/* ANSWER Q7:
 * SOLUTION-BEGIN
 * Không cùng một chiến lược. CPU-bound thường để số worker gần số lõi.
 * I/O-bound để nhiều hơn vì worker dành phần lớn thời gian chờ, không chiếm CPU.
 * Cùng một con số thì hoặc thiếu song song khi chờ I/O, hoặc thừa thread khi tính CPU.
 * availableProcessors cho biết số lõi, không phải kích thước pool cho mọi workload.
 * SOLUTION-END
 */

/* ANSWER Q8:
 * SOLUTION-BEGIN
 * Hàng đợi không giới hạn nhận task mãi, không có điểm từ chối.
 * Fixed pool của Executors dùng LinkedBlockingQueue sức chứa mặc định rất lớn.
 * Producer nhanh hơn worker thì số task chờ tăng, mỗi task giữ bộ nhớ.
 * Heap có thể cạn và ném OutOfMemoryError. Queue có trần cùng rejection tạo backpressure.
 * SOLUTION-END
 */

/* ANSWER Q9:
 * SOLUTION-BEGIN
 * Rejection policy chạy khi executor không nhận thêm task.
 * Với ThreadPoolExecutor, điều đó xảy ra khi worker đang bận và queue đã đầy, hoặc pool đã shutdown.
 * AbortPolicy ném RejectedExecutionException cho caller. bounded dùng policy này.
 * Policy khác có thể chạy trên caller, bỏ task mới, hoặc bỏ task cũ. Xong việc thì shutdown pool.
 * SOLUTION-END
 */
