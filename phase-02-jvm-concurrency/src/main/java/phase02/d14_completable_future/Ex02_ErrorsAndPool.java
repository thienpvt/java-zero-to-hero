package phase02.d14_completable_future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

/**
 * CompletableFuture — Bài 2: Exception và executor
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 14 (CompletableFuture), câu 4, 6, 7, 8.
 * Cần làm trước: Ex01_Compose.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex02_ErrorsAndPoolTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q4 [CODE] Exception được propagate như thế nào?
 *   Bắt đầu   : cài {@code zeroOnFailure}. Dùng {@code exceptionally} để trả 0 khi stage lỗi,
 *               và giữ nguyên giá trị khi stage thành công. Ctrl+Q trên {@code exceptionally}.
 *   Kiểm chứng: chạy q04_zeroWhenFailed và q04_keepsSuccess.
 *   Hoàn thành khi: hai test q04_* xanh.
 * <p>
 * Q6 [DỰ ĐOÁN] {@code supplyAsync()} mặc định chạy ở đâu?
 *   Bắt đầu   : điền hằng Q6_DEFAULT_POOL (thay null). Ctrl+N mở CompletableFuture, Ctrl+Q trên
 *               {@code supplyAsync(Supplier)}. Không đo tên thread: common pool có thể chạy ngay trên caller.
 *   Kiểm chứng: chạy q06_defaultPool.
 *   Hoàn thành khi: q06_defaultPool xanh và viết xong khối ANSWER Q6.
 * <p>
 * Q7 [TỰ TRẢ LỜI] Vì sao blocking task trong common pool có thể gây vấn đề?
 *   Bắt đầu   : viết khối ANSWER Q7 sau khi đã điền Q6. Nêu worker bị chiếm và việc các task khác
 *               không còn chỗ chạy.
 *   Hoàn thành khi: viết xong khối ANSWER Q7.
 * <p>
 * Q8 [CODE] Khi nào nên truyền custom Executor?
 *   Bắt đầu   : cài {@code supplyOn(Executor, AtomicReference)}. Dùng
 *               {@code CompletableFuture.supplyAsync(supplier, executor)}.
 *               Trong supplier, ghi {@code Thread.currentThread().getName()} vào reference.
 *   Kiểm chứng: chạy q08_recordsAppThread. Test tạo executor một thread bằng thread factory,
 *               gọi {@code thenApply} để đọc tên đã ghi, {@code get} tối đa 10 giây, rồi shutdown.
 *               Không assert tên thread của common pool.
 *   Hoàn thành khi: q08_recordsAppThread xanh; tên ghi trong supplier là tên thread của executor.
 */
public class Ex02_ErrorsAndPool {

    // Q6 — pool mặc định của supplyAsync() khi không truyền Executor. Không đo tên thread.
    static final String Q6_DEFAULT_POOL = null;

    /**
     * Khi {@code in} lỗi, stage trả về 0. Khi {@code in} thành công, giữ nguyên giá trị.
     */
    static CompletableFuture<Integer> zeroOnFailure(CompletableFuture<Integer> in) {
        throw new UnsupportedOperationException("TODO Q4");
    }

    /**
     * Chạy supplier trên {@code executor} bằng {@code supplyAsync(supplier, executor)}.
     * Supplier ghi {@code Thread.currentThread().getName()} vào {@code threadName}.
     */
    static CompletableFuture<Integer> supplyOn(Executor executor, AtomicReference<String> threadName) {
        throw new UnsupportedOperationException("TODO Q8");
    }
}

/* ANSWER Q4:
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
