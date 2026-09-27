package phase01.d10_stream;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Stream API — Bài 4: parallelStream(), thread pool và trade-off với blocking I/O
 *
 * Nguồn: 01-java-core-advanced.md, mục 10 (Stream API), câu 8, 9, 10.
 * Cần làm trước: Ex03_ReduceVsCollect (đã thấy side effect trong parallel stream nguy hiểm
 * ở Q7).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex04_ParallelStreamsTest bằng
 * nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q8 [DỰ ĐOÁN + THÍ NGHIỆM] {@code parallelStream()} có luôn nhanh hơn không?
 *   Bắt đầu   : điền Q8_PARALLEL_ALWAYS_FASTER sau khi đã chạy main() (thay null).
 *   Kiểm chứng: chạy main(), đọc báo cáo của runExperiment, sau khi đã điền dự đoán, tự so
 *               thời gian bản tuần tự và bản song song trên từng kích thước và từng kiểu
 *               (LongStream so với List đã boxed).
 *   Hoàn thành khi: test q08_* xanh; giải thích được ít nhất một trường hợp cụ thể trong
 *               báo cáo mà bản song song KHÔNG nhanh hơn bản tuần tự.
 * <p>
 * Q9 [DỰ ĐOÁN] Parallel stream sử dụng thread pool nào?
 *   Bắt đầu   : chạy main() (nút ▶ cạnh main) — các dòng in "Thread: ..." cho thấy tên
 *               thật của những thread thực thi forEach() của một parallel stream; điền
 *               Q9_COMMON_POOL_THREAD_NAME_PREFIX.
 *   Kiểm chứng: Ctrl+N → ForkJoinPool → Ctrl+F12 → tìm method static commonPool(); mọi
 *               parallel stream (không tự bọc trong một ForkJoinPool khác) đều mặc định
 *               chạy trên CÙNG một ForkJoinPool.commonPool() cho toàn JVM.
 *   Hoàn thành khi: test q09_* xanh; giải thích được vì sao 2 pipeline parallel stream
 *               khác nhau, chạy ở 2 nơi khác nhau trong CÙNG một JVM, vẫn có thể "giẫm chân"
 *               nhau (vì cùng chia sẻ đúng một pool, cùng một số thread cố định).
 * <p>
 * Q10 [THÍ NGHIỆM + TỰ TRẢ LỜI] Tại sao blocking I/O thường không phải use case tốt cho
 *     parallel stream?
 *   Bắt đầu   : đọc phần (c) trong runExperiment — 32 tác vụ Thread.sleep(20) (giả lập chờ
 *               I/O) chạy bằng parallelStream() so với Executors.newFixedThreadPool(32).
 *   Kiểm chứng: chạy main(), so 2 dòng thời gian ở phần (c); parallelStream() bị giới hạn
 *               bởi ForkJoinPool.commonPool().getParallelism() (mặc định = số core - 1) nên
 *               32 tác vụ "chờ" phải xếp hàng theo nhiều lô, còn fixedThreadPool(32) có sẵn
 *               32 thread nên chạy hết 32 tác vụ gần như cùng lúc.
 *   Hoàn thành khi: viết xong khối OBSERVATION Q10 (số liệu đo được thực tế) và khối
 *               ANSWER Q10 (giải thích); nêu được rằng commonPool được CHIA SẺ cho toàn
 *               JVM, nên "chiếm" nó bằng các thread đang block vì I/O còn ảnh hưởng tới
 *               những tác vụ song song CPU-bound khác không liên quan đang chờ dùng chung
 *               pool đó.
 */
public class Ex04_ParallelStreams {

    // Q8 — kịch bản: parallelStream so với stream tuần tự trên vài kích thước khác nhau.
    // Hằng hỏi: bản song song có luôn nhanh hơn không? Chạy main() rồi mới điền.
    static final Boolean Q8_PARALLEL_ALWAYS_FASTER = null;

    // Q9 — kịch bản: đọc tên thread in từ main() khi chạy parallel stream, chép phần tiền tố
    // đứng trước số thứ tự worker. Đối chiếu ForkJoinPool.commonPool() nếu cần.
    static final String Q9_COMMON_POOL_THREAD_NAME_PREFIX = null;

    /** Cho sẵn: đo thời gian (ms) chạy {@code action}, có 1 vòng warm-up bị bỏ qua trước đó. */
    private static long timeMillis(Runnable action) {
        action.run(); // warm-up: bỏ qua kết quả đo để JIT kịp biên dịch các lambda liên quan.
        long start = System.nanoTime();
        action.run();
        return (System.nanoTime() - start) / 1_000_000;
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Bị ngắt khi sleep giả lập I/O.", e);
        }
    }

    /** Cho sẵn: 32 tác vụ "I/O giả" (chỉ Thread.sleep) chạy bằng parallelStream(). */
    private static long fakeIoWithParallelStream() {
        return timeMillis(() -> IntStream.range(0, 32).parallel().forEach(i -> sleepQuietly(20)));
    }

    /** Cho sẵn: 32 tác vụ "I/O giả" chạy bằng một ExecutorService có đúng 32 thread. */
    private static long fakeIoWithFixedThreadPool() {
        ExecutorService pool = Executors.newFixedThreadPool(32);
        try {
            long start = System.nanoTime();
            for (int i = 0; i < 32; i++) {
                pool.submit(() -> sleepQuietly(20));
            }
            pool.shutdown();
            if (!pool.awaitTermination(1, TimeUnit.MINUTES)) {
                throw new IllegalStateException("fixedThreadPool không hoàn thành trong 1 phút.");
            }
            return (System.nanoTime() - start) / 1_000_000;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Bị ngắt khi chờ fixedThreadPool hoàn thành.", e);
        } finally {
            pool.shutdownNow();
        }
    }

    /**
     * Cho sẵn: đo thô (mỗi lần đo có 1 vòng warm-up, dùng {@code System.nanoTime()}) 3 kịch
     * bản song song vs tuần tự:
     * &lt;ul&gt;
     *   &lt;li&gt;(a) tổng {@code LongStream.rangeClosed(1, count)} tuần tự vs song song, với
     *       {@code count} nhỏ (100 000) và lớn (50 000 000);&lt;/li&gt;
     *   &lt;li&gt;(b) tổng một {@code List&lt;Integer&gt;} đã boxed (kích thước phụ thuộc {@code n}, tối
     *       đa 1 000 000) bằng {@code parallelStream()};&lt;/li&gt;
     *   &lt;li&gt;(c) 32 tác vụ "I/O giả" bằng {@code parallelStream()} so với một
     *       {@code ExecutorService} có 32 thread.&lt;/li&gt;
     * &lt;/ul&gt;
     * Trả về báo cáo dạng văn bản. Đo thô kiểu này DỄ SAI (nhiễu do GC, JIT chưa warm-up đủ,
     * tải máy hiện tại...) — Giai đoạn 2 học đo đúng cách bằng JMH.
     *
     * @throws IllegalArgumentException nếu {@code n &lt; 1}
     */
    static String runExperiment(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("n phải >= 1, nhận: " + n);
        }
        long smallCount = 100_000L;
        long largeCount = 50_000_000L;
        int boxedSize = Math.min(n, 1_000_000);

        StringBuilder report = new StringBuilder();
        report.append("ForkJoinPool.commonPool().getParallelism() = ")
                .append(ForkJoinPool.commonPool().getParallelism())
                .append('\n');

        report.append("(a) LongStream.rangeClosed(1, ").append(smallCount).append(") - tuần tự vs song song:\n");
        report.append("    tuần tự  : ").append(timeMillis(() -> LongStream.rangeClosed(1, smallCount).sum()))
                .append(" ms\n");
        report.append("    song song: ")
                .append(timeMillis(() -> LongStream.rangeClosed(1, smallCount).parallel().sum()))
                .append(" ms\n");

        report.append("(a) LongStream.rangeClosed(1, ").append(largeCount).append(") - tuần tự vs song song:\n");
        report.append("    tuần tự  : ").append(timeMillis(() -> LongStream.rangeClosed(1, largeCount).sum()))
                .append(" ms\n");
        report.append("    song song: ")
                .append(timeMillis(() -> LongStream.rangeClosed(1, largeCount).parallel().sum()))
                .append(" ms\n");

        List<Integer> boxed = IntStream.rangeClosed(1, boxedSize).boxed().toList();
        report.append("(b) List<Integer> boxed size=").append(boxedSize).append(" song song: ")
                .append(timeMillis(() -> boxed.parallelStream().mapToLong(Integer::longValue).sum()))
                .append(" ms\n");

        report.append("(c) 32 tác vụ I/O giả (Thread.sleep(20)):\n");
        report.append("    parallelStream()                 : ").append(fakeIoWithParallelStream())
                .append(" ms\n");
        report.append("    Executors.newFixedThreadPool(32) : ").append(fakeIoWithFixedThreadPool())
                .append(" ms\n");

        return report.toString();
    }

    public static void main(String[] args) {
        List<Integer> sample = List.of(1, 2, 3, 4, 5, 6, 7, 8);
        sample.parallelStream().forEach(i -> System.out.println("Thread: " + Thread.currentThread().getName()));
        System.out.println(runExperiment(100_000));
    }
}

/* OBSERVATION Q8:
 *
 */

/* OBSERVATION Q10:
 *
 */

/* ANSWER Q10:
 *
 */
