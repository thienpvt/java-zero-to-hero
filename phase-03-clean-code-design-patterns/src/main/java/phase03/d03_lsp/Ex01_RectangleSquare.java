package phase03.d03_lsp;

/**
 * LSP — Bài 1: Rectangle và Square
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 3 (Liskov Substitution Principle), câu 1, 2, 3, 5.
 * Cần làm trước: d02_ocp.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_RectangleSquareTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] LSP khác inheritance syntax như thế nào?
 *   Bắt đầu   : viết khối ANSWER Q1.
 *   Hoàn thành khi: ANSWER Q1 tách "extends biên dịch được" khỏi "caller không phải đổi kỳ vọng".
 * <p>
 * Q2 [DỰ ĐOÁN] Rectangle/Square problem thể hiện vấn đề gì?
 *   Bắt đầu   : điền Q2_SQUARE_IS_LSP_SUBTYPE.
 *   Kiểm chứng: chạy q02_prediction và q02_growWidthBreaksRectangle.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nói được caller giả định width và height độc lập.
 * <p>
 * Q3 [DỰ ĐOÁN] Subtype throw UnsupportedOperationException cho method của parent là dấu hiệu gì?
 *   Bắt đầu   : điền Q3_UNSUPPORTED_IS_SMELL.
 *   Kiểm chứng: chạy q03_prediction. Ctrl+B trên {@code rotate} xem ai khai báo nó.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 phân biệt "không hỗ trợ" với "hợp đồng hẹp hơn".
 * <p>
 * Q5 [CODE] Interface quá lớn khiến implementation vi phạm contract thế nào?
 *   Bắt đầu   : cài {@code rotate} và {@code areaMm2} trên {@code Square} sao cho {@code Square}
 *               không còn phải ném exception; nếu hình không xoay được, trả chính diện tích đó.
 *   Kiểm chứng: chạy q05_rotateWorksForBoth.
 *   Hoàn thành khi: q05_rotateWorksForBoth xanh, không đường nào ném UnsupportedOperationException.
 */
public class Ex01_RectangleSquare {

    /** Hình có diện tích và có thể xoay mà không đổi diện tích. */
    public interface Shape {

        long areaMm2();

        /** Trả diện tích sau khi xoay. */
        long rotate();
    }

    public static final class Rectangle implements Shape {

        private int width;
        private int height;

        public Rectangle(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        @Override
        public long areaMm2() {
            return (long) width * height;
        }

        @Override
        public long rotate() {
            // SOLUTION-BEGIN throw Q5
            int swap = width;
            width = height;
            height = swap;
            return areaMm2();
            // SOLUTION-END
        }
    }

    public static final class Square implements Shape {

        private final int side;

        public Square(int side) {
            this.side = side;
        }

        @Override
        public long areaMm2() {
            // SOLUTION-BEGIN throw Q5
            return (long) side * side;
            // SOLUTION-END
        }

        @Override
        public long rotate() {
            // SOLUTION-BEGIN throw Q5
            return areaMm2();
            // SOLUTION-END
        }
    }

    // Q2 — Square có phải subtype đúng LSP của Rectangle trong ví dụ này.
    static final Boolean Q2_SQUARE_IS_LSP_SUBTYPE = false; // SOLUTION-VALUE

    // Q3 — ném UnsupportedOperationException cho method của parent là dấu hiệu xấu.
    static final Boolean Q3_UNSUPPORTED_IS_SMELL = true; // SOLUTION-VALUE

    /** Cho sẵn: caller giả định width và height độc lập. */
    static long growWidth(Shape shape, int delta) {
        if (!(shape instanceof Rectangle rectangle)) {
            throw new IllegalArgumentException("Chỉ nhận Rectangle.");
        }
        rectangle.setWidth(delta);
        return rectangle.areaMm2();
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Inheritance syntax chỉ nói compiler chấp nhận lời gọi.
 * LSP nói về kỳ vọng của caller: mọi điều đúng với base type phải còn đúng với subtype.
 * Một class extends được vẫn có thể phá kỳ vọng đó ở runtime.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Caller giả định đổi width không ảnh hưởng height, đúng với Rectangle.
 * Nếu Square là subtype và đổi width kéo theo height, diện tích trả về khác kỳ vọng.
 * Caller buộc phải kiểm tra kiểu cụ thể, tức là không thay thế được — vi phạm LSP.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Method của parent nằm trong hợp đồng mà mọi subtype phải giữ.
 * Ném UnsupportedOperationException nghĩa là implementation không giữ được hợp đồng đó.
 * Đó là dấu hiệu interface đã gộp hai khả năng không cùng tồn tại, hoặc inheritance bị dùng sai.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Interface quá lớn buộc mọi implementation khai báo cả method không liên quan.
 * Implementation nào không làm được phải ném exception, và caller không biết trước là có.
 * Tách interface theo khả năng để mỗi implementation chỉ hứa điều nó giữ được.
 * SOLUTION-END
 */
