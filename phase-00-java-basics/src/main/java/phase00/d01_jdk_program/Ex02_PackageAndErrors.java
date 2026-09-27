package phase00.d01_jdk_program;

import phase00.support.Compiles;

/**
 * JDK và JVM — Bài 2: Package, import, lỗi biên dịch và lỗi lúc chạy
 *
 * Nguồn: 00-java-basics-review.md, mục 1 (JDK, JVM và cấu trúc chương trình), câu 2, 3.
 * Cần làm trước: Ex01_JdkJvm.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_PackageAndErrorsTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q2 [DỰ ĐOÁN + TỰ TRẢ LỜI] Khi nào cần {@code import}? Class cùng package có cần import không?
 *   Bắt đầu   : đọc class Adder ngay trong file này (cùng package, không có dòng import
 *               nào trỏ tới nó) và các chỗ dùng {@code String}. Điền hai hằng Q2_*.
 *   Kiểm chứng: tạm xóa dòng {@code import} của một kiểu ở package khác (ví dụ
 *               {@code java.util.List} trong file test) để xem IDE báo đỏ, rồi hoàn tác.
 *               Ctrl+Click vào tên class cùng package không nhảy qua một câu import.
 *   Hoàn thành khi: q02_prediction xanh; ANSWER Q2 phân biệt được cùng package,
 *               {@code java.lang}, và package khác.
 * <p>
 * Q3 [DỰ ĐOÁN + CODE] Lỗi compile khác exception lúc chạy như thế nào?
 *   Bắt đầu   : với Q3_WRONG_ARGUMENT_COMPILES, bỏ comment dòng mẫu trong {@code Adder}
 *               để xem lỗi đỏ, rồi comment lại. Điền nốt hai hằng Q3_*. Sau đó cài {@code sum}.
 *   Kiểm chứng: chạy q03_prediction rồi q03_sum. Đặt breakpoint ở {@code return a / b}
 *               trong {@code divide}, Debug test, F7 để thấy phép chia chạy trên JVM
 *               chứ không bị javac chặn.
 *   Code      : {@code Adder.sum(int, int)} trả tổng hai tham số.
 *   Hoàn thành khi: q03_* xanh; giải thích được lỗi kiểu tham số chết ở javac, còn
 *               chia cho 0 chết lúc JVM đang chạy.
 */
public class Ex02_PackageAndErrors {

    static final class Adder {
        static int sum(int left, int right) {
            // int bad = sum("1", right);
            // SOLUTION-BEGIN throw Q3
            return left + right;
            // SOLUTION-END
        }
    }

    // Q2 — class Adder ở cùng file/cùng package với Ex02 có cần import để gọi không?
    static final Boolean Q2_SAME_PACKAGE_NEEDS_IMPORT = false; // SOLUTION-VALUE

    // Q2 — String (java.lang) có cần import tường minh không?
    static final Boolean Q2_JAVA_LANG_NEEDS_IMPORT = false; // SOLUTION-VALUE

    // Q3 — Adder.sum("1", 2) có biên dịch không? Xem dòng comment trong Adder.sum.
    static final Compiles Q3_WRONG_ARGUMENT_COMPILES = Compiles.NO; // SOLUTION-VALUE

    // Q3 — divide(1, zero) với zero là biến: đây có phải lỗi biên dịch không?
    static final Boolean Q3_DIVIDE_BY_ZERO_IS_COMPILE_ERROR = false; // SOLUTION-VALUE

    // Q3 — nếu có exception, tên lớp đơn giản là gì?
    static final String Q3_DIVIDE_BY_ZERO_EXCEPTION = "ArithmeticException"; // SOLUTION-VALUE

    /** Chia hai số. Divisor phải là biến khi muốn quan sát lỗi lúc chạy — {@code 1/0} hằng số không biên dịch. */
    static int divide(int dividend, int divisor) {
        return dividend / divisor;
    }

    /* ANSWER Q2:
     * SOLUTION-BEGIN
     * Cần import khi dùng kiểu ở package khác (trừ java.lang, vốn được import sẵn).
     * Hai class cùng package — kể cả hai class trong cùng một file — gọi nhau trực tiếp,
     * không viết import. Import không nhúng code; nó chỉ viết tắt tên đầy đủ.
     * SOLUTION-END
     */

    /* ANSWER Q3:
     * SOLUTION-BEGIN
     * Lỗi compile do javac phát hiện trước khi có file .class để chạy: sai kiểu tham số,
     * thiếu dấu, tên chưa khai báo. Chương trình không khởi động.
     * Exception lúc chạy xảy ra trên JVM khi bytecode đã chạy: ví dụ chia cho 0 với
     * divisor không phải hằng số, hoặc unbox null. Stack trace chỉ vị trí đang thực thi.
     * SOLUTION-END
     */
}
