package phase02.d13_executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * ExecutorService — Bài 1: submit và shutdown
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 13 (ExecutorService), câu 1–4.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_SubmitAndShutdownTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] Tại sao dùng thread pool thay vì tạo Thread liên tục?
 *   Bắt đầu   : viết khối ANSWER Q1. Nêu chi phí tạo thread và việc giới hạn số worker.
 *   Hoàn thành khi: viết xong khối ANSWER Q1.
 * <p>
 * Q2 [DỰ ĐOÁN + CODE] {@code execute()} và {@code submit()} khác nhau thế nào?
 *   Bắt đầu   : cài {@code submitJob(ExecutorService)} bằng {@code submit}, và
 *               {@code executeJob(ExecutorService)} bằng {@code execute}. Điền
 *               Q2_SUBMIT_RETURNS_FUTURE (thay null).
 *   Tra cứu   : Ctrl+N → ExecutorService → Ctrl+F12 → submit, rồi execute, Ctrl+Q.
 *   Kiểm chứng: chạy q02_prediction, q02_submitReturnsOk và q02_executeCallsExecute.
 *   Hoàn thành khi: ba test q02_* xanh và viết xong khối ANSWER Q2.
 * <p>
 * Q3 [CODE] Future dùng để làm gì?
 *   Bắt đầu   : cài {@code resultOf} để {@code submit} một {@code Callable} rồi lấy giá trị
 *               bằng {@code Future.get}.
 *   Tra cứu   : Ctrl+N → Future → Ctrl+Q. Ctrl+F12 tìm {@code get}.
 *   Kiểm chứng: chạy q03_resultOfCallable.
 *   Hoàn thành khi: test q03_* xanh và viết xong khối ANSWER Q3.
 * <p>
 * Q4 [CODE] {@code shutdown()} và {@code shutdownNow()} khác nhau thế nào?
 *   Bắt đầu   : cài {@code stop(ExecutorService, boolean)}. {@code now} thì {@code shutdownNow}.
 *               Ngược lại thì {@code shutdown} rồi {@code awaitTermination(2, SECONDS)}.
 *   Tra cứu   : Ctrl+F12 trong ExecutorService, Ctrl+Q trên {@code shutdown} và {@code shutdownNow}.
 *   Kiểm chứng: chạy q04_shutdownNowSkipsQueuedTask và q04_shutdownThenAwait. Task đã vào queue
 *               không được chạy sau shutdownNow. Cổng {@code CountDownLatch} chưa mở.
 *               Không dùng {@code Thread.sleep}.
 *   Hoàn thành khi: hai test q04_* xanh, {@code isShutdown} đúng, và viết xong khối ANSWER Q4.
 */
public class Ex01_SubmitAndShutdown {

    // Q2 — submit có trả Future cho caller hay không.
    static final Boolean Q2_SUBMIT_RETURNS_FUTURE = true; // SOLUTION-VALUE

    /**
     * Gửi một {@code Callable} trả chuỗi {@code ok} bằng {@code submit}.
     */
    static Future<?> submitJob(ExecutorService es) {
        // SOLUTION-BEGIN throw Q2
        return es.submit(() -> "ok");
        // SOLUTION-END
    }

    /**
     * Đưa một {@code Runnable} kết thúc ngay vào {@code execute}.
     */
    static void executeJob(ExecutorService es) {
        // SOLUTION-BEGIN throw Q2
        es.execute(() -> { });
        // SOLUTION-END
    }

    /**
     * Gửi {@code task} bằng {@code submit}, rồi trả giá trị {@code Future.get} chờ tối đa 10 giây.
     *
     * @throws IllegalStateException nếu bị ngắt, quá hạn, hoặc {@code Callable} ném lỗi
     */
    static <T> T resultOf(ExecutorService es, Callable<T> task) {
        // SOLUTION-BEGIN throw Q3
        Future<T> future = es.submit(task);
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Bị ngắt khi chờ Future.", ex);
        } catch (TimeoutException ex) {
            throw new IllegalStateException("Future chưa xong sau 10 giây.", ex);
        } catch (ExecutionException ex) {
            throw new IllegalStateException("Callable ném lỗi.", ex);
        }
        // SOLUTION-END
    }

    /**
     * {@code now} thì {@code shutdownNow}. Ngược lại {@code shutdown} rồi
     * {@code awaitTermination(2, TimeUnit.SECONDS)}.
     *
     * @throws IllegalStateException nếu bị ngắt khi chờ pool tắt
     */
    static void stop(ExecutorService es, boolean now) {
        // SOLUTION-BEGIN throw Q4
        if (now) {
            es.shutdownNow();
            return;
        }
        es.shutdown();
        try {
            es.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Bị ngắt khi chờ executor tắt.", ex);
        }
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Tạo Thread mới mỗi việc tốn stack, bộ nhớ native và một thread của hệ điều hành.
 * Pool giữ sẵn một số worker, xếp việc vào queue, rồi tái sử dụng worker rảnh.
 * Số thread có trần nên tải tăng không làm cạn thread của máy.
 * Tạo Thread liên tục không có hàng đợi và không có giới hạn đó.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * execute nhận Runnable và trả void. Caller không có chỗ lấy kết quả hay exception của task.
 * submit nhận Runnable hoặc Callable và trả Future.
 * Future.get chờ task xong rồi trả giá trị, hoặc bọc lỗi trong ExecutionException.
 * submitJob dùng submit để lấy chuỗi ok. executeJob chỉ gọi execute.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Future là tay cầm kết quả của task chạy bất đồng bộ, chưa có giá trị ngay lúc submit.
 * get chờ xong rồi trả giá trị Callable, hoặc ném ExecutionException nếu task lỗi.
 * Có thể hỏi isDone hoặc hủy bằng cancel. resultOf submit rồi get trong 10 giây.
 * Không có Future thì caller không nhận được giá trị Callable trả về.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * shutdown ngừng nhận task mới, nhưng task đã submit, kể cả task đang nằm trong queue, vẫn được chạy.
 * shutdownNow cũng ngừng nhận task mới, cố interrupt worker đang chạy, và trả về các task chưa bắt đầu.
 * Những task còn trong queue sau shutdownNow không được chạy. stop(es, true) gọi shutdownNow.
 * stop(es, false) gọi shutdown rồi awaitTermination 2 giây để chờ task đã nhận kết thúc.
 * SOLUTION-END
 */
