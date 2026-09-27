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
    static final Boolean Q1_JIT_SAME_AS_JAVAC = false; // SOLUTION-VALUE

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
 * SOLUTION-BEGIN
 * javac và JIT không làm cùng một việc. javac chạy trước, dịch source thành bytecode trong file class.
 * JIT nằm trong JVM: nó theo dõi method và nhánh nóng, rồi dịch bytecode đó thành mã máy.
 * Tối ưu của JIT dùng thông tin lúc chạy, nên javac không thay thế được bước này.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Lúc mới chạy, nhiều method còn được thông dịch và JIT chưa biên dịch xong.
 * JVM đếm lời gọi và đường đi; khi một đoạn trở thành hot, JIT dịch nó thành mã máy đã tối ưu.
 * Sau warm-up, phần nóng chạy mã máy đó nên thường nhanh hơn giai đoạn đầu.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * JVM ghi nhận kiểu thật, nhánh nào hay được chọn, và lời gọi nào luôn tới cùng một method.
 * JIT dùng profile đó để inline, bỏ kiểm tra không còn cần, và chuyên biệt hóa mã theo đường đi nóng.
 * javac không thấy dữ liệu lúc chạy nên không làm được các tối ưu này.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Đo ngay từ đầu trộn thời gian thông dịch, thời gian JIT biên dịch, và mã máy đã tối ưu.
 * Warm-up cho phần nóng được biên dịch và ổn định trước khi bắt đầu đo.
 * Không warm-up thì số liệu không đại diện cho ứng dụng đã chạy lâu.
 * SOLUTION-END
 */

/* OBSERVATION Q5:
 * SOLUTION-BEGIN
 * Vòng không có tác dụng phụ và không ai đọc kết quả là dead code. JIT được phép xóa cả vòng.
 * nanoTime bọc vòng rỗng có thể đo khoảng thời gian gần như không còn vòng nào.
 * runExperiment cộng một biểu thức và đưa tổng vào báo cáo để kết quả được dùng; dù vậy số nanoTime vẫn không phải benchmark.
 * SOLUTION-END
 */
