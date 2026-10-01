package phase03.d11_factory;

/**
 * Factory — Bài 2: khi new trực tiếp là hợp lý
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 11, câu 2, 3.
 * Cần làm trước: Ex01_ProviderSelection.
 * Cách làm: chạy test trong Ex02_WhenNewIsFineTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q2 [DỰ ĐOÁN + CODE] Khi nào new trực tiếp hoàn toàn hợp lý?
 *   Bắt đầu   : điền Q2_NEW_IS_FINE, rồi cài {@code Coupon.apply} chỉ tính phần trăm.
 *   Kiểm chứng: chạy q02_prediction và q02_applyCoupon.
 *   Hoàn thành khi: hai test xanh và ANSWER Q2 nêu value object không có biến thể.
 * <p>
 * Q3 [DỰ ĐOÁN] Factory khác Builder thế nào?
 *   Bắt đầu   : điền Q3_FACTORY_VS_BUILDER bằng một giá trị của {@code Difference}.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nêu factory chọn lớp, builder ráp field.
 */
public class Ex02_WhenNewIsFine {

    /** Khác nhau giữa Factory và Builder. */
    public enum Difference {
        SAME_THING,
        FACTORY_CHOOSES_CLASS_BUILDER_ASSEMBLES_FIELDS,
        BUILDER_IS_FASTER
    }

    // Q2 — new trực tiếp hợp lý khi nào.
    static final Boolean Q2_NEW_IS_FINE = true; // SOLUTION-VALUE

    // Q3 — Factory khác Builder thế nào.
    static final Difference Q3_FACTORY_VS_BUILDER = Difference.FACTORY_CHOOSES_CLASS_BUILDER_ASSEMBLES_FIELDS; // SOLUTION-VALUE
    /** Value object nhỏ, không có biến thể, dựng bằng new là đủ. */
    public record Coupon(String code, int percent) {

        public Coupon {
            if (percent < 0 || percent > 100) {
                throw new IllegalArgumentException("Phần trăm phải trong khoảng 0..100: " + percent);
            }
        }

        public long apply(long totalCents) {
            // SOLUTION-BEGIN throw Q2
            return totalCents - totalCents * percent / 100;
            // SOLUTION-END
        }
    }
}

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * new trực tiếp hợp lý khi chỉ có một lớp cụ thể và không có quy tắc chọn nào.
 * Value object nhỏ như Coupon không có biến thể, nên factory chỉ thêm một lớp gọi.
 * Quy tắc kiểm tra nằm trong constructor, đủ để không tạo được object sai.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Factory trả về một trong nhiều lớp cùng interface, quyết định là chọn lớp nào.
 * Builder ráp nhiều field cho một lớp đã biết, quyết định là điền gì theo thứ tự nào.
 * Có thể dùng cùng nhau: builder ráp tham số, factory chọn lớp rồi gọi builder.
 * SOLUTION-END
 */
