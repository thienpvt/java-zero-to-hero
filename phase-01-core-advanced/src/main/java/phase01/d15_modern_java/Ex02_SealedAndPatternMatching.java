package phase01.d15_modern_java;

import phase01.support.Compiles;

/**
 * Modern Java — Bài 2: sealed type và pattern matching
 *
 * Nguồn: 01-java-core-advanced.md, mục 15 (Java hiện đại: record, sealed type, pattern matching), câu 3–5.
 * Cần làm trước: Ex01_Records (cú pháp record).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_SealedAndPatternMatchingTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q3 [CODE] `sealed` mang lại lợi ích gì cho `switch` trên các subtype?
 *   Bắt đầu   : đọc sealed interface Shape và 3 record cài đặt bên dưới; cài đặt area().
 *   Kiểm chứng: tạm xóa nhánh `case Square` trong switch của area(), build (Ctrl+F9), tự
 *               đọc thông báo của compiler về exhaustiveness dù không có default — rồi hoàn
 *               tác lại nhánh đã xóa. Ghi nguyên văn thông báo đó vào ANSWER Q4.
 *   Code      : cài đặt static double area(Shape shape) bằng switch pattern matching trên
 *               Circle/Rectangle/Square, không viết default.
 *   Hoàn thành khi: q03_* xanh; giải thích được vì sao switch không default vẫn an toàn
 *               (sealed cho compiler biết toàn bộ tập subtype để kiểm tra exhaustiveness).
 *
 * Q4 [TỰ TRẢ LỜI] Khi thêm subtype mới, compiler có thể giúp tìm những chỗ nào cần sửa?
 *   Bắt đầu   : thêm `record Triangle(double base, double height) implements Shape` vào
 *               danh sách `permits` của Shape, build (Ctrl+F9), đọc thông báo compiler xuất hiện ở
 *               area() (switch không còn exhaustive), rồi hoàn tác cả hai thay đổi.
 *   Kiểm chứng: ghi lại đúng nội dung lỗi compiler đã thấy vào ANSWER Q4.
 *   Hoàn thành khi: viết xong khối ANSWER Q4, có trích dẫn nguyên văn thông báo lỗi compiler.
 *
 * Q5 [DỰ ĐOÁN + CODE] `switch` expression khác statement ở giá trị trả về và tính đầy đủ của nhánh thế nào?
 *   Bắt đầu   : bỏ comment đoạn mẫu cạnh Q5_SWITCH_EXPRESSION_MISSING_CASE_COMPILES (thiếu
 *               nhánh Square), build (Ctrl+F9), xem IDE báo gì, rồi comment lại — sau khi
 *               đã điền dự đoán.
 *   Kiểm chứng: chạy q05_prediction; đặt breakpoint trong describeWithoutNullCase
 *               (Ctrl+N → Ex02_SealedAndPatternMatching), Debug với input null, sau khi đã
 *               điền dự đoán, tự đọc simple name của exception.
 *   Code      : cài đặt static String describe(Object value) bằng switch pattern matching
 *               kèm guard `when`.
 *   Hoàn thành khi: test q05_* xanh; giải thích được khi nào compiler bắt buộc switch
 *               phải đủ nhánh (expression vs statement, sealed vs kiểu cũ) và hành vi khi
 *               selector là null với switch pattern không có {@code case null}.
 */
public class Ex02_SealedAndPatternMatching {

    sealed interface Shape permits Circle, Rectangle, Square {
    }

    record Circle(double radius) implements Shape {
    }

    record Rectangle(double width, double height) implements Shape {
    }

    record Square(double side) implements Shape {
    }

    static double area(Shape shape) {
        throw new UnsupportedOperationException("TODO Q3");
    }

    // Q5 — mẫu: switch expression trên Shape, thiếu nhánh Square. Bỏ comment, xem IDE báo
    // gì, rồi comment lại — sau khi đã điền dự đoán.
    // static double areaMissingCase(Shape shape) {
    //     return switch (shape) {
    //         case Circle c -> Math.PI * c.radius() * c.radius();
    //         case Rectangle r -> r.width() * r.height();
    //     };
    // }
    static final Compiles Q5_SWITCH_EXPRESSION_MISSING_CASE_COMPILES = null;

    static final String Q5_SWITCH_ON_NULL_WITHOUT_CASE_NULL = null;

    /**
     * Cho sẵn để minh họa Q5: gọi với {@code null} và quan sát (Debug hoặc chạy test q05_*)
     * khi switch pattern matching trên kiểu tham chiếu không có nhánh {@code case null}
     * tường minh.
     *
     * <p>Switch statement kiểu cũ cũng có thể NPE trên {@code null}, nhưng không phải vì
     * unboxing trong mọi trường hợp: switch trên {@code String} dereference {@code hashCode()}
     * ẩn, switch trên enum dereference {@code ordinal()}. Chỉ switch trên kiểu boxed
     * ({@code Integer}, {@code Long}, ...) NPE vì unboxing ({@code intValue()} và tương đương).
     */
    static String describeWithoutNullCase(Object value) {
        return switch (value) {
            case Integer i -> "số nguyên";
            case String s -> "chuỗi";
            default -> "khác";
        };
    }

    static String describe(Object value) {
        throw new UnsupportedOperationException("TODO Q5");
    }
}

/* ANSWER Q4:
 *
 */
