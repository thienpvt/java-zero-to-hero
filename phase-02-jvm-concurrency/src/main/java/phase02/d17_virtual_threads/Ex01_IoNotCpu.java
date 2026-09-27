package phase02.d17_virtual_threads;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Virtual threads — Bài 1: I/O chứ không phải CPU
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 17 (Virtual threads), câu 1, 3, 5.
 * Cần làm trước: d13_executor, Ex01_SubmitAndShutdown.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_IoNotCpuTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] Virtual threads tăng khả năng phục vụ nhiều request chờ I/O theo cơ chế nào?
 *   Bắt đầu   : chạy q01_experimentRuns hoặc main(), đọc báo cáo của runExperiment.
 *               Viết khối ANSWER Q1 về carrier khi tác vụ chờ I/O.
 *   Tra cứu   : Ctrl+N → Thread → Ctrl+F12 → isVirtual → Ctrl+Q. Ctrl+N → Executors →
 *               Ctrl+F12 → newVirtualThreadPerTaskExecutor → Ctrl+Q.
 *   Kiểm chứng: báo cáo có chữ virtual.
 *   Hoàn thành khi: q01_experimentRuns xanh và viết xong khối ANSWER Q1.
 * <p>
 * Q3 [DỰ ĐOÁN] Khi nào virtual threads không giúp tăng throughput?
 *   Bắt đầu   : điền Q3_VIRTUAL_SPEEDS_CPU_BOUND (thay null). Nghĩ tác vụ chỉ tính toán, không chờ I/O.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và viết xong khối ANSWER Q3.
 * <p>
 * Q5 [DỰ ĐOÁN] Vì sao không nên pool virtual threads chỉ để giới hạn concurrency?
 *   Bắt đầu   : điền Q5_POOL_VIRTUAL_THREADS_TO_LIMIT (thay null). Đọc Javadoc của
 *               newVirtualThreadPerTaskExecutor, rồi viết khối ANSWER Q5.
 *   Tra cứu   : Ctrl+N → Executors → Ctrl+F12 → newVirtualThreadPerTaskExecutor → Ctrl+Q.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh và viết xong khối ANSWER Q5.
 */
public class Ex01_IoNotCpu {

    // Q3 — hằng hỏi virtual thread có làm tác vụ CPU-bound nhanh hơn hay không.
    static final Boolean Q3_VIRTUAL_SPEEDS_CPU_BOUND = false; // SOLUTION-VALUE

    // Q5 — hằng hỏi có nên pool virtual thread chỉ để giới hạn concurrency hay không.
    static final Boolean Q5_POOL_VIRTUAL_THREADS_TO_LIMIT = false; // SOLUTION-VALUE

    /**
     * Cho sẵn, không cần sửa. Tạo {@code n} virtual thread bằng
     * {@code Executors.newVirtualThreadPerTaskExecutor}. Mỗi thread đọc
     * {@code Thread.currentThread().isVirtual()}. Báo cáo có chữ virtual.
     *
     * @throws IllegalArgumentException nếu {@code n &lt; 1}
     * @throws IllegalStateException nếu còn thread sống sau 10 giây
     */
    static String runExperiment(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("n phải lớn hơn 0.");
        }
        AtomicInteger virtual = new AtomicInteger();
        CountDownLatch done = new CountDownLatch(n);
        Thread[] workers = new Thread[n];
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        try {
            for (int i = 0; i < n; i++) {
                int id = i;
                executor.submit(() -> {
                    workers[id] = Thread.currentThread();
                    try {
                        if (Thread.currentThread().isVirtual()) {
                            virtual.incrementAndGet();
                        }
                    } finally {
                        done.countDown();
                    }
                });
            }
            if (!done.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Thread còn sống sau khi join tối đa 10 giây.");
            }
            int seen = virtual.get();
            return "virtual threads=" + seen + "/" + n + " isVirtual=" + (seen == n);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Bị ngắt khi chờ virtual thread.", ex);
        } finally {
            executor.shutdown();
            joinAll(workers);
        }
    }

    private static void joinAll(Thread[] workers) {
        for (Thread worker : workers) {
            if (worker == null) {
                throw new IllegalStateException("Thread còn sống sau khi join tối đa 10 giây.");
            }
            try {
                worker.join(10_000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Bị ngắt khi chờ virtual thread.", ex);
            }
            if (worker.isAlive()) {
                throw new IllegalStateException(
                        "Thread còn sống sau khi join tối đa 10 giây: " + worker.getName());
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(runExperiment(8));
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Virtual thread được mount lên một pool carrier nhỏ, là platform thread.
 * Khi request chờ I/O, virtual thread nhả carrier để carrier chạy request khác.
 * Nhiều request đang chờ vẫn được phục vụ, không cần một platform thread cho mỗi request.
 * runExperiment dùng newVirtualThreadPerTaskExecutor và thấy isVirtual.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Không giúp khi tác vụ chiếm CPU suốt và không chờ I/O.
 * Lúc đang tính, virtual thread giữ một carrier. Số carrier gần với số lõi.
 * Thêm virtual thread không làm một phép tính nhanh hơn, nên throughput CPU-bound không tăng.
 * Q3_VIRTUAL_SPEEDS_CPU_BOUND là false.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Virtual thread rẻ: mỗi task một thread mới, qua newVirtualThreadPerTaskExecutor.
 * Pool chúng chỉ để giới hạn số lượng thì thêm queue và tái sử dụng không đúng mô hình.
 * Trần phải nằm ở tài nguyên khan: semaphore, pool kết nối, rate limit và timeout.
 * Q5_POOL_VIRTUAL_THREADS_TO_LIMIT là false. Không pool virtual thread chỉ để chặn concurrency.
 * SOLUTION-END
 */
