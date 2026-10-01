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
    static final Boolean Q1_CLEAN_CODE_MEANS_SHORT = null;

    // Q2 — method 100 dòng có luôn sai.
    static final Verdict Q2_HUNDRED_LINE_METHOD = null;

    // Q3 — duplicate tốt hơn premature abstraction khi nào.
    static final Boolean Q3_DUPLICATE_BETTER_THAN_PREMATURE = null;

    // Q4 — comment nhiều có phải tốt.
    static final Verdict Q4_MORE_COMMENTS = null;

    // Q5 — boolean parameter báo hiệu gì.
    static final Signal Q5_BOOLEAN_PARAMETER = null;

    /** Cho sẵn, không sửa. Tên không cho biết method nhận gì và trả gì. */
    static String process(LineItem item, int flag) {
        return item.sku() + ":" + item.unitCents() * item.qty() + ":" + flag;
    }

    /** Trả chuỗi "{sku} x{qty} = {lineTotalCents} cents". */
    static String describe(LineItem item) {
        throw new UnsupportedOperationException("TODO Q6");
    }

    /** Tổng tiền hàng cộng phí vận chuyển, đơn vị cent. */
    static long totalWithShipping(List<LineItem> items, int shippingCents) {
        throw new UnsupportedOperationException("TODO Q6");
    }
}

/* ANSWER Q1:
 *
 */

/* ANSWER Q2:
 *
 */

/* ANSWER Q3:
 *
 */

/* ANSWER Q4:
 *
 */

/* ANSWER Q5:
 *
 */

/* ANSWER Q6:
 *
 */
