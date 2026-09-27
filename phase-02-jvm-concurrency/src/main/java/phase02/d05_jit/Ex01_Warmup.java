package phase02.d05_jit;

/**
 * JIT — Bài 1: Warm-up
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 5 (JIT Compiler), câu 1–5.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_WarmupTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] JIT khác {@code javac} như thế nào?
 *   Bắt đầu   : điền hằng Q1_JIT_SAME_AS_JAVAC (thay null), rồi viết khối ANSWER Q1.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nói hai bước khác nhau ở chỗ nào.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Vì sao Java application có thể nhanh hơn sau warm-up?
 *   Bắt đầu   : viết khối ANSWER Q2. Nói rõ method đang ở giai đoạn nào trước khi đoạn nóng được biên dịch.
 *   Hoàn thành khi: viết xong khối ANSWER Q2.
 * <p>
 * Q3 [TỰ TRẢ LỜI] JVM có thể tối ưu dựa trên runtime information như thế nào?
 *   Bắt đầu   : viết khối ANSWER Q3. Nêu ít nhất một tối ưu mà chỉ số liệu lúc chạy mới cho phép.
 *   Hoàn thành khi: viết xong khối ANSWER Q3.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Benchmark Java tại sao cần warm-up?
 *   Bắt đầu   : viết khối ANSWER Q4. Nói rõ số đo lấy quá sớm đang trộn những giai đoạn nào.
 *   Hoàn thành khi: viết xong khối ANSWER Q4.
 * <p>
 * Q5 [THÍ NGHIỆM] Vì sao benchmark bằng {@code System.nanoTime()} loop đơn giản dễ sai?
 *   Bắt đầu   : đọc runExperiment(int). Ctrl+N → System → Ctrl+F12 → nanoTime → Ctrl+Q.
 *               Chạy q05_experimentRuns, rồi chạy main() (nút ▶ cạnh main) và đọc dòng cảnh báo.
 *   Kiểm chứng: q05_experimentRuns chỉ kiểm tra báo cáo không rỗng. Không so số nanoTime.
 *   Hoàn thành khi: q05_experimentRuns xanh và OBSERVATION Q5 giải thích vì sao vòng rỗng dễ bị JIT xóa.
 */
public class Ex01_Warmup {

    // Q1 — hằng hỏi liệu JIT có làm cùng một việc với javac.
    static final Boolean Q1_JIT_SAME_AS_JAVAC = null;

    /**
     * Chạy một vòng {@code nanoTime} cộng biểu thức đơn giản và trả báo cáo.
     * Báo cáo luôn có lời cảnh báo dead-code elimination.
     *
     * @param n số lần lặp
     * @return báo cáo văn bản không rỗng; số nanoTime trong báo cáo không phải kết luận benchmark
     */
    static String runExperiment(int n) {
        long start = System.nanoTime();
        long sum = 0;
        for (int i = 0; i < n; i++) {
            sum += i + 1L;
        }
        long elapsedNs = System.nanoTime() - start;
        return "n=" + n
                + "\ntổng=" + sum
                + "\nnanoTime=" + elapsedNs
                + "\nCảnh báo dead-code elimination: đừng dùng số nanoTime ở trên làm kết luận."
                + " Vòng rỗng, khi kết quả không được đọc, dễ bị JIT xóa.";
    }

    public static void main(String[] args) {
        System.out.println(runExperiment(1_000_000));
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

/* OBSERVATION Q5:
 *
 */
