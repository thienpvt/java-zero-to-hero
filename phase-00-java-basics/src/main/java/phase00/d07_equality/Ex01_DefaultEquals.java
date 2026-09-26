package phase00.d07_equality;

import java.util.Objects;

/**
 * So sánh object — Bài 1: equals và toString mặc định
 *
 * Nguồn: 00-java-basics-review.md, mục 7 (Object, so sánh và biểu diễn dữ liệu), câu 1, 3.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_DefaultEqualsTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q1 [DỰ ĐOÁN] Hai object {@code new Customer("A")} khác nhau có mặc định {@code equals()} là true không?
 *   Bắt đầu   : đọc class Customer — không override equals. Điền Q1_DEFAULT_EQUALS.
 *   Kiểm chứng: Debug q01_prediction, Alt+F8 gọi {@code left.equals(right)}. Ctrl+N mở class {@code Object},
 *               Ctrl+F12 tìm {@code equals}, Ctrl+Q đọc Javadoc.
 *   Hoàn thành khi: q01_prediction xanh; nói được equals mặc định so cùng object, không so field.
 *
 * Q3 [DỰ ĐOÁN + TỰ TRẢ LỜI] {@code toString()} nên chứa thông tin gì và tránh lộ thông tin gì?
 *   Bắt đầu   : điền Q3_DEFAULT_TOSTRING_CONTAINS_NAME, rồi viết ANSWER Q3.
 *   Kiểm chứng: Alt+F8 in {@code new Customer("A").toString()}. Ctrl+Q trên {@code Object.toString}.
 *   Hoàn thành khi: q03_prediction xanh; ANSWER Q3 nêu thứ nên có trong toString và thứ không được đưa vào.
 */
public class Ex01_DefaultEquals {

    static final class Customer {
        final String name;

        Customer(String name) {
            this.name = Objects.requireNonNull(name, "name");
        }
    }

    // Q1 — new Customer("A").equals(new Customer("A")) theo equals mặc định.
    static final Boolean Q1_DEFAULT_EQUALS = false; // SOLUTION-VALUE

    // Q3 — toString() mặc định của Customer("A") có chứa chữ "A" không?
    static final Boolean Q3_DEFAULT_TOSTRING_CONTAINS_NAME = false; // SOLUTION-VALUE

    /* ANSWER Q3:
     * SOLUTION-BEGIN
     * toString phục vụ debug và log: tên kiểu cùng vài field nhận diện (mã, tên hiển thị).
     * Không đưa mật khẩu, token, số thẻ đầy đủ, hay dữ liệu cá nhân không cần để chẩn đoán.
     * toString mặc định của Object chỉ có tên class và identity hash, không có field.
     * SOLUTION-END
     */
}
