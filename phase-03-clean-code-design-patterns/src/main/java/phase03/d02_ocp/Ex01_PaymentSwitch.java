package phase03.d02_ocp;

/**
 * OCP — Bài 1: switch theo payment type
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 2 (Open/Closed Principle), câu 1, 2, 3, 4, 5.
 * Cần làm trước: d01_srp.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_PaymentSwitchTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] OCP thực sự muốn giảm vấn đề gì?
 *   Bắt đầu   : điền Q1_OCP_REDUCES bằng một giá trị của {@code Risk}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nói được rủi ro là sửa code đang chạy.
 * <p>
 * Q2 [CODE] Vì sao switch lớn theo payment type có thể là dấu hiệu cần abstraction?
 *   Bắt đầu   : cài {@code feeCents} theo đúng switch trong tài liệu (CARD 2%, WALLET 1%, còn lại 0),
 *               rồi cài {@code CardPolicy} và {@code WalletPolicy} cùng trả đúng phần trăm đó.
 *   Kiểm chứng: chạy q02_switchFees và q02_policyFees. Ctrl+B trên {@code PaymentPolicy} xem contract.
 *   Hoàn thành khi: hai đường cho cùng một số cho CARD và WALLET; ANSWER Q2 nêu mỗi lần thêm loại
 *               thanh toán phải mở lại đúng method switch.
 * <p>
 * Q3 [DỰ ĐOÁN] Có phải mọi {@code if} đều vi phạm OCP?
 *   Bắt đầu   : điền Q3_EVERY_IF_VIOLATES_OCP.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nói điều kiện ổn định không phải điểm mở rộng.
 * <p>
 * Q4 [TỰ TRẢ LỜI] OCP có thể dẫn tới over-engineering như thế nào?
 *   Bắt đầu   : viết khối ANSWER Q4.
 *   Hoàn thành khi: ANSWER Q4 nêu abstraction tạo trước khi có biến thể thứ hai.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Khi requirement chưa ổn định, có nên abstract mọi thứ ngay không?
 *   Bắt đầu   : viết khối ANSWER Q5.
 *   Hoàn thành khi: ANSWER Q5 gắn việc abstract với số biến thể đang tồn tại.
 */
public class Ex01_PaymentSwitch {

    /** Rủi ro OCP muốn giảm. */
    public enum Risk {
        RUNTIME_BUG,
        RECOMPILE_COST,
        EDITING_WORKING_CODE
    }

    // Q1 — OCP giảm rủi ro nào.
    static final Risk Q1_OCP_REDUCES = null;

    // Q3 — mọi if đều vi phạm OCP hay không.
    static final Boolean Q3_EVERY_IF_VIOLATES_OCP = null;

    /** Cho sẵn, không sửa. Mỗi loại thanh toán mới là một nhánh switch mới. */
    static long feeCents(String type, long amountCents) {
        return switch (type) {
            case "CARD" -> amountCents * 2 / 100;
            case "WALLET" -> amountCents * 1 / 100;
            default -> 0;
        };
    }

    /** Contract cho phí thanh toán. */
    interface PaymentPolicy {

        long feeCents(long amountCents);

        String name();
    }

    static final class CardPolicy implements PaymentPolicy {

        @Override
        public long feeCents(long amountCents) {
            throw new UnsupportedOperationException("TODO Q2");
        }

        @Override
        public String name() {
            throw new UnsupportedOperationException("TODO Q2");
        }
    }

    static final class WalletPolicy implements PaymentPolicy {

        @Override
        public long feeCents(long amountCents) {
            throw new UnsupportedOperationException("TODO Q2");
        }

        @Override
        public String name() {
            throw new UnsupportedOperationException("TODO Q2");
        }
    }
}

/* ANSWER Q1:
 *
 */

/* ANSWER Q2:
 *
 */
