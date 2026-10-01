package phase03.d02_ocp;

/**
 * OCP — Bài 2: điểm mở rộng
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 2, câu 2, 5.
 * Cần làm trước: Ex01_PaymentSwitch.
 * Cách làm: chạy test trong Ex02_ExtensionPointTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Ví dụ Q2 [CODE] Một điểm mở rộng nhận policy mới mà không sửa caller.
 *   Bắt đầu   : cài {@code feeWith}. Không viết switch trong method này.
 *   Kiểm chứng: chạy q02_feeWith.
 *   Hoàn thành khi: q02_feeWith xanh và ANSWER Q2 nói được caller không đổi khi thêm policy.
 * <p>
 * Ví dụ Q5 [DỰ ĐOÁN] Chưa có biến thể thứ hai thì có nên tạo điểm mở rộng?
 *   Bắt đầu   : điền Q5_ABSTRACT_BEFORE_SECOND_VARIANT.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 nêu chi phí của một interface chỉ có một implementation.
 */
public class Ex02_ExtensionPoint {

    // Q5 — có nên dựng abstraction trước khi có biến thể thứ hai.
    static final Boolean Q5_ABSTRACT_BEFORE_SECOND_VARIANT = null;

    public static final Ex01_PaymentSwitch.PaymentPolicy FIXED =
            new Ex01_PaymentSwitch.PaymentPolicy() {
                @Override
                public long feeCents(long amountCents) {
                    return 0;
                }

                @Override
                public String name() {
                    return "FIXED";
                }
            };

    /** Phí do policy quyết định. Không switch ở đây. */
    static long feeWith(Ex01_PaymentSwitch.PaymentPolicy policy, long amountCents) {
        throw new UnsupportedOperationException("TODO Q2");
    }
}

/* ANSWER Q2:
 *
 */

/* ANSWER Q5:
 *
 */
