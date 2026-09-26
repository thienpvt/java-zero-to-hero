package phase00.d02_types;

/**
 * Kiểu và phép toán — Bài 2: Tràn int và final trên tham chiếu
 *
 * Nguồn: 00-java-basics-review.md, mục 2 (Biến, kiểu dữ liệu và phép toán), câu 3, 4.
 * Cần làm trước: Ex01_DivisionAndMoney.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_OverflowAndFinalTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q3 [DỰ ĐOÁN] Khi cộng vượt quá giới hạn của {@code int}, Java có tự chuyển sang {@code long} không?
 *   Bắt đầu   : điền Q3_OVERFLOW_PROMOTES_TO_LONG và Q3_MAX_INT_PLUS_ONE.
 *   Kiểm chứng: Alt+F8 gõ {@code Integer.MAX_VALUE + 1} và {@code Integer.MAX_VALUE + 1L}.
 *               Ctrl+Q trên {@code Integer.MAX_VALUE} và {@code Integer.MIN_VALUE}.
 *   Hoàn thành khi: q03_prediction xanh; nói được biểu thức {@code int + int} vẫn là {@code int}.
 *
 * Q4 [DỰ ĐOÁN] {@code final} trên biến tham chiếu có làm object được trỏ tới bất biến không?
 *   Bắt đầu   : đọc class Bucket bên dưới, điền Q4_FINAL_REFERENCE_FREEZES_TARGET.
 *   Kiểm chứng: Debug q04_prediction, breakpoint ở {@code bucket.n = 7}, Alt+F8 xem {@code bucket.n}
 *               trước và sau. Thử bỏ comment phép gán lại biến {@code final} trong test để thấy lỗi đỏ.
 *   Hoàn thành khi: q04_prediction xanh; phân biệt được khóa tham chiếu với khóa object.
 */
public class Ex02_OverflowAndFinal {

    // Q3 — (long) (Integer.MAX_VALUE + 1) có bằng Integer.MAX_VALUE + 1L không?
    static final Boolean Q3_OVERFLOW_PROMOTES_TO_LONG = false; // SOLUTION-VALUE

    // Q3 — giá trị int của Integer.MAX_VALUE + 1.
    static final Integer Q3_MAX_INT_PLUS_ONE = Integer.MIN_VALUE; // SOLUTION-VALUE

    static final class Bucket {
        int n;
    }

    // Q4 — final Bucket bucket = new Bucket(); bucket.n = 7; field n có bị đóng băng không?
    static final Boolean Q4_FINAL_REFERENCE_FREEZES_TARGET = false; // SOLUTION-VALUE
}
