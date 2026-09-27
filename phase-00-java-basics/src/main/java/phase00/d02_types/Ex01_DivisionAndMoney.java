package phase00.d02_types;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Kiểu và phép toán — Bài 1: Chia số và tiền
 *
 * Nguồn: 00-java-basics-review.md, mục 2 (Biến, kiểu dữ liệu và phép toán), câu 1, 2.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_DivisionAndMoneyTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q1 [DỰ ĐOÁN + CODE] {@code 5 / 2} và {@code 5 / 2.0} cho kết quả khác nhau thế nào?
 *   Bắt đầu   : điền Q1_FIVE_DIV_TWO và Q1_FIVE_DIV_TWO_DOUBLE trước khi chạy test.
 *               Sau đó cài {@code total}.
 *   Kiểm chứng: Alt+F8 (Evaluate Expression) gõ {@code 5 / 2} và {@code 5 / 2.0}.
 *               Ctrl+N mở {@code BigDecimal}, Ctrl+Q đọc constructor nhận {@code String}.
 *   Code      : {@code total} cộng tiền từng dòng bằng {@code BigDecimal} tạo từ chuỗi
 *               {@code unitPrice}. Giá hoặc số lượng âm, hay giá không phải số, thì
 *               {@code IllegalArgumentException}. Số 0 hợp lệ. List/item/giá null thì
 *               {@code NullPointerException}.
 *   Hoàn thành khi: q01_* xanh; nói được vì sao không cộng tiền bằng {@code double}.
 * <p>
 * Q2 [DỰ ĐOÁN] Điều gì xảy ra nếu {@code Integer x = null; int y = x;}?
 *   Bắt đầu   : điền Q2_UNBOX_NULL_EXCEPTION bằng tên lớp đơn giản, không kèm package.
 *   Kiểm chứng: Debug q02_prediction, breakpoint ở dòng gán {@code int y = x}, F8 bước
 *               qua và đọc exception. Ctrl+Q trên kiểu {@code int} và {@code Integer}.
 *   Hoàn thành khi: q02_prediction xanh; giải thích được unbox {@code null}.
 */
public class Ex01_DivisionAndMoney {

    // Q1 — kết quả của biểu thức 5 / 2, kiểu int.
    static final Integer Q1_FIVE_DIV_TWO = 2; // SOLUTION-VALUE

    // Q1 — Double.toString(5 / 2.0).
    static final String Q1_FIVE_DIV_TWO_DOUBLE = "2.5"; // SOLUTION-VALUE

    // Q2 — tên lớp exception khi unbox Integer null sang int.
    static final String Q2_UNBOX_NULL_EXCEPTION = "NullPointerException"; // SOLUTION-VALUE

    /** Một dòng hàng: đơn giá viết bằng chuỗi thập phân, số lượng là số nguyên. */
    public record LineItem(String unitPrice, int quantity) {
    }

    /**
     * Tổng tiền của các dòng. Đơn giá tạo bằng {@code new BigDecimal(unitPrice)}.
     *
     * @throws NullPointerException     nếu list, dòng, hoặc đơn giá là null
     * @throws IllegalArgumentException nếu giá không phải số, giá âm, hoặc số lượng âm
     */
    static BigDecimal total(List<LineItem> items) {
        // SOLUTION-BEGIN throw Q1
        Objects.requireNonNull(items, "items");
        BigDecimal sum = BigDecimal.ZERO;
        for (LineItem item : items) {
            Objects.requireNonNull(item, "item");
            Objects.requireNonNull(item.unitPrice(), "unitPrice");
            BigDecimal price;
            try {
                price = new BigDecimal(item.unitPrice());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("giá không hợp lệ: " + item.unitPrice(), ex);
            }
            if (price.signum() < 0 || item.quantity() < 0) {
                throw new IllegalArgumentException("giá và số lượng không được âm");
            }
            sum = sum.add(price.multiply(BigDecimal.valueOf(item.quantity())));
        }
        return sum;
        // SOLUTION-END
    }
}
