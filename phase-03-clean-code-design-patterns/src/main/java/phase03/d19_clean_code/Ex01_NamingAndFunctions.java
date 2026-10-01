package phase03.d19_clean_code;

import java.util.List;

/**
 * Clean Code — Bài 1: tên và hàm
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 19 (Clean Code Fundamentals), câu 1, 2, 3, 4, 5, 6.
 * Cần làm trước: d12_builder.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_NamingAndFunctionsTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Clean code có đồng nghĩa code ngắn không?
 *   Bắt đầu   : điền Q1_CLEAN_CODE_MEANS_SHORT.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu đọc hiểu nhanh hơn là ít dòng.
 * <p>
 * Q2 [DỰ ĐOÁN] Một method 100 dòng có luôn sai không?
 *   Bắt đầu   : điền Q2_HUNDRED_LINE_METHOD bằng một giá trị của {@code Verdict}.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu tiêu chí là số trách nhiệm, không phải số dòng.
 * <p>
 * Q3 [DỰ ĐOÁN] Khi nào duplicate code tốt hơn premature abstraction?
 *   Bắt đầu   : điền Q3_DUPLICATE_BETTER_THAN_PREMATURE.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nêu hai đoạn giống nhau nhưng đổi vì lý do khác nhau.
 * <p>
 * Q4 [DỰ ĐOÁN] Comment nhiều có phải code tốt không?
 *   Bắt đầu   : điền Q4_MORE_COMMENTS bằng một giá trị của {@code Verdict}.
 *   Kiểm chứng: chạy q04_prediction.
 *   Hoàn thành khi: q04_prediction xanh và ANSWER Q4 nêu comment giải thích why, không lặp what.
 * <p>
 * Q5 [DỰ ĐOÁN] Boolean parameter thường báo hiệu vấn đề thiết kế gì?
 *   Bắt đầu   : điền Q5_BOOLEAN_PARAMETER bằng một giá trị của {@code Signal}.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 nêu method đang làm hai việc.
 * <p>
 * Q6 [CODE] Method tên {@code process()} cho biết vấn đề gì?
 *   Bắt đầu   : cài {@code describe} và {@code totalWithShipping} với tên nói rõ việc và đơn vị.
 *               Không đặt tên chung chung; {@code process} cho sẵn là ví dụ xấu, đừng sửa.
 *   Kiểm chứng: chạy q06_describeNamesUnit và q06_totalWithShipping.
 *   Hoàn thành khi: hai test xanh và ANSWER Q6 nêu tên không cho biết method trả gì.
 */
public class Ex01_NamingAndFunctions {

    /** Method 100 dòng có luôn sai không. */
    public enum Verdict {
        ALWAYS_WRONG,
        DEPENDS_ON_RESPONSIBILITIES,
        NEVER_WRONG
    }

    /** Boolean parameter báo hiệu gì. */
    public enum Signal {
        FASTER_RUNTIME,
        METHOD_DOES_TWO_THINGS,
        WEAK_TYPING
    }

    public record LineItem(String sku, long unitCents, int qty) {
    }

    // Q1 — clean code có đồng nghĩa code ngắn.
    static final Boolean Q1_CLEAN_CODE_MEANS_SHORT = false; // SOLUTION-VALUE

    // Q2 — method 100 dòng có luôn sai.
    static final Verdict Q2_HUNDRED_LINE_METHOD = Verdict.DEPENDS_ON_RESPONSIBILITIES; // SOLUTION-VALUE

    // Q3 — duplicate tốt hơn premature abstraction khi nào.
    static final Boolean Q3_DUPLICATE_BETTER_THAN_PREMATURE = true; // SOLUTION-VALUE

    // Q4 — comment nhiều có phải tốt.
    static final Verdict Q4_MORE_COMMENTS = Verdict.DEPENDS_ON_RESPONSIBILITIES; // SOLUTION-VALUE

    // Q5 — boolean parameter báo hiệu gì.
    static final Signal Q5_BOOLEAN_PARAMETER = Signal.METHOD_DOES_TWO_THINGS; // SOLUTION-VALUE

    /** Cho sẵn, không sửa. Tên không cho biết method nhận gì và trả gì. */
    static String process(LineItem item, int flag) {
        return item.sku() + ":" + item.unitCents() * item.qty() + ":" + flag;
    }

    /** Trả chuỗi "{sku} x{qty} = {lineTotalCents} cents". */
    static String describe(LineItem item) {
        // SOLUTION-BEGIN throw Q6
        return item.sku() + " x" + item.qty() + " = " + item.unitCents() * item.qty() + " cents";
        // SOLUTION-END
    }

    /** Tổng tiền hàng cộng phí vận chuyển, đơn vị cent. */
    static long totalWithShipping(List<LineItem> items, int shippingCents) {
        // SOLUTION-BEGIN throw Q6
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Danh sách mặt hàng không được rỗng.");
        }
        long total = shippingCents;
        for (LineItem item : items) {
            total += item.unitCents() * item.qty();
        }
        return total;
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Không. Clean code là đọc hiểu nhanh và sửa an toàn, không phải ít dòng.
 * Nén nhiều việc vào một dòng làm khó đọc và khó đặt breakpoint.
 * Một method dài nhưng một trách nhiệm có thể rõ hơn nhiều method vụn gọi nhau.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Không luôn sai. Thước đo là số trách nhiệm và số nhánh, không phải số dòng.
 * Một bảng tra dài hoặc một switch ổn định có thể vẫn rõ ràng.
 * Method dài mà trộn validate, tính toán và lưu trữ thì nên tách theo trách nhiệm.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Khi hai đoạn chỉ tình cờ giống nhau, còn lý do thay đổi khác nhau.
 * Gộp chúng tạo một abstraction phải chiều cả hai, và thay đổi một bên sẽ phá bên kia.
 * Chờ đến lần thay đổi thứ hai thực sự để biết chúng có cùng lý do hay không.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Không. Comment lặp lại điều code đã nói làm nhiễu và dễ lạc hậu khi code đổi.
 * Comment còn giá trị khi giải thích vì sao, ví dụ ràng buộc bên ngoài hoặc quyết định đánh đổi.
 * Nếu cần comment để hiểu code làm gì, tên và cấu trúc nên được sửa trước.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Boolean parameter thường nghĩa là method làm hai việc, thật hoặc giả.
 * Chỗ gọi `send(true)` không nói lên điều gì khi đọc.
 * Tách thành hai method có tên theo việc, hoặc dùng enum có nhãn rõ.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Tên chung chung như process không cho biết đầu vào, đầu ra, hay tác dụng phụ.
 * Người đọc phải mở thân method để biết nó làm gì, kể cả khi chỉ cần đọc lời gọi.
 * Tên nên nói việc và đơn vị, ví dụ totalWithShipping và describe.
 * SOLUTION-END
 */
