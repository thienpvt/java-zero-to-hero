package phase00.d04_text;

/**
 * Array, String, enum — Bài 1: Chỉ số, so sánh chuỗi và bất biến
 *
 * Nguồn: 00-java-basics-review.md, mục 4 (Array, String và enum), câu 1, 2, 3.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_ArrayAndStringTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q1 [DỰ ĐOÁN] {@code new int[3]} có các index hợp lệ nào?
 *   Bắt đầu   : điền Q1_FIRST_INDEX, Q1_LAST_INDEX_OF_LENGTH_3 và Q1_INDEX_3_EXCEPTION
 *               (tên lớp đơn giản).
 *   Kiểm chứng: Debug q01_prediction, Alt+F8 gõ {@code values.length} và {@code values[3]}.
 *   Hoàn thành khi: q01_prediction xanh; nói được index chạy từ 0 đến length - 1.
 *
 * Q2 [DỰ ĐOÁN] Vì sao không dùng {@code ==} để kiểm tra hai chuỗi có cùng nội dung?
 *   Bắt đầu   : điền Q2_DOUBLE_EQUALS_SAME_CONTENT và Q2_EQUALS_SAME_CONTENT.
 *   Kiểm chứng: Alt+F8 với {@code new String("hi") == new String("hi")} và {@code .equals}.
 *               Ctrl+Q trên {@code String.equals}.
 *   Hoàn thành khi: q02_prediction xanh; phân biệt được cùng object với cùng nội dung.
 *
 * Q3 [DỰ ĐOÁN] {@code String} bất biến nghĩa là gì với lời gọi {@code text.toUpperCase()}?
 *   Bắt đầu   : điền Q3_TO_UPPER_MUTATES_RECEIVER — lời gọi có sửa chính biến {@code text} không?
 *   Kiểm chứng: Debug q03_prediction, Alt+F8 xem {@code text} và giá trị {@code toUpperCase} trả về.
 *               Nhớ truyền {@code Locale.ROOT}. Ctrl+Q trên {@code String.toUpperCase}.
 *   Hoàn thành khi: q03_prediction xanh; nói được method trả chuỗi mới, receiver giữ nguyên.
 */
public class Ex01_ArrayAndString {

    static final Integer Q1_FIRST_INDEX = 0; // SOLUTION-VALUE
    static final Integer Q1_LAST_INDEX_OF_LENGTH_3 = 2; // SOLUTION-VALUE
    static final String Q1_INDEX_3_EXCEPTION = "ArrayIndexOutOfBoundsException"; // SOLUTION-VALUE

    // Q2 — new String("hi") == new String("hi") ?
    static final Boolean Q2_DOUBLE_EQUALS_SAME_CONTENT = false; // SOLUTION-VALUE

    // Q2 — new String("hi").equals(new String("hi")) ?
    static final Boolean Q2_EQUALS_SAME_CONTENT = true; // SOLUTION-VALUE

    // Q3 — sau text.toUpperCase(Locale.ROOT), text có bị đổi nội dung không?
    static final Boolean Q3_TO_UPPER_MUTATES_RECEIVER = false; // SOLUTION-VALUE
}
