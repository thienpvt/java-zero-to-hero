package phase02.d01_bytecode;

/**
 * Bytecode — Bài 2: Bytecode và machine code
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 1 (Compilation &amp; Bytecode), câu 3, 4, 5.
 * Cần làm trước: Ex01_CompileAndClassFile.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex02_BytecodeVsMachineTest bằng nút ▶
 * (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Vì sao Java được gọi là platform-independent?
 *   Bắt đầu   : Ctrl+N mở Ex01_CompileAndClassFile, đọc lại pipeline source, bytecode, JVM.
 *   Kiểm chứng: viết khối ANSWER Q3 ở cuối file.
 *   Hoàn thành khi: ANSWER Q3 giải thích vai trò của file class và của JVM trên từng nền tảng.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Bytecode khác machine code thế nào?
 *   Bắt đầu   : so sánh sản phẩm của javac với lệnh của một CPU.
 *   Kiểm chứng: viết khối ANSWER Q4 ở cuối file.
 *   Hoàn thành khi: ANSWER Q4 phân biệt tập lệnh JVM với mã máy của một kiến trúc CPU.
 * <p>
 * Q5 [DỰ ĐOÁN + THÍ NGHIỆM] JIT giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q5_JIT_COMPILES_HOT_BYTECODE (thay null). Chạy main (nút ▶ cạnh main)
 *               hoặc test q05_experimentRuns.
 *   Kiểm chứng: chạy q05_prediction và q05_experimentRuns. Đọc chuỗi in ra. Không đo thời gian
 *               và không kết luận JIT đã chạy chỉ vì vòng lặp kết thúc.
 *   Code      : không. runExperiment cho sẵn.
 *   Hoàn thành khi: hai test q05_* xanh và OBSERVATION Q5 nói JIT giải quyết vấn đề gì.
 */
public class Ex02_BytecodeVsMachine {

    // Q5 — JIT có biên dịch bytecode của đoạn code nóng thành machine code không?
    static final Boolean Q5_JIT_COMPILES_HOT_BYTECODE = true; // SOLUTION-VALUE

    /**
     * Chạy một vòng nhỏ rồi một vòng cộng dồn {@code n} bước. Chuỗi trả về có
     * {@code interpreter-or-jit}. Không đo thời gian.
     *
     * @throws IllegalArgumentException nếu {@code n} nhỏ hơn 1
     */
    static String runExperiment(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("n phải lớn hơn hoặc bằng 1.");
        }
        int smallSteps = Math.min(8, n);
        long smallSum = accumulate(smallSteps);
        long largeSum = accumulate(n);
        return "interpreter-or-jit small=" + smallSum + " large=" + largeSum;
    }

    private static long accumulate(int steps) {
        long sum = 0;
        for (int i = 1; i <= steps; i++) {
            sum += i;
        }
        return sum;
    }

    public static void main(String[] args) {
        System.out.println(runExperiment(4_000));
    }
}

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Cùng một file .class chạy được trên mọi hệ điều hành có JVM tương ứng.
 * javac không phát machine code của một CPU; sản phẩm phân phối là bytecode của JVM.
 * Phần gắn với nền tảng nằm ở bản JVM, không nằm ở file class.
 * Vì vậy Java được gọi là platform-independent ở mức bytecode.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Bytecode là tập lệnh của JVM, hướng stack, được verifier kiểm tra, giống nhau trên mọi CPU.
 * Machine code là lệnh của một CPU cụ thể, ví dụ x86-64 hoặc ARM, và không mang sang kiến trúc khác.
 * javac dừng ở bytecode. Interpreter đọc bytecode; JIT có thể phát machine code cho đoạn nóng.
 * SOLUTION-END
 */

/* OBSERVATION Q5:
 * SOLUTION-BEGIN
 * JIT giải quyết bytecode bị interpreter chạy chậm trên đoạn nóng: JVM dịch những đoạn đó
 * thành machine code lúc chạy, còn file class vẫn là bytecode di động.
 * Thí nghiệm chạy một vòng nhỏ rồi một vòng cộng dồn theo n và in interpreter-or-jit.
 * Không đo thời gian, vì một vòng nanoTime không chứng minh JIT đã biên dịch.
 * SOLUTION-END
 */
