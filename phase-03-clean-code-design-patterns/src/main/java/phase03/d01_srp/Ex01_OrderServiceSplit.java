package phase03.d01_srp;

import java.util.ArrayList;
import java.util.List;

/**
 * SRP — Bài 1: tách OrderService
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 1 (Single Responsibility Principle), câu 1, 2, 3, 4, 5, 6.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_OrderServiceSplitTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] SRP có nghĩa là "một class chỉ được có một method" không?
 *   Bắt đầu   : điền Q1_SRP_MEANS_ONE_METHOD (thay null).
 *   Kiểm chứng: chạy q01_prediction. Ctrl+Q trên {@code OrderService} đọc đoạn mô tả.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nói được "một lý do để thay đổi" khác "một method".
 * <p>
 * Q2 [TỰ TRẢ LỜI] "One reason to change" nên hiểu thế nào?
 *   Bắt đầu   : viết khối ANSWER Q2.
 *   Hoàn thành khi: ANSWER Q2 gắn "reason" với một actor hoặc một nguồn yêu cầu thay đổi.
 * <p>
 * Q3 [DỰ ĐOÁN] Class quá lớn thường thể hiện dấu hiệu gì?
 *   Bắt đầu   : điền Q3_LARGE_CLASS_SIGNAL bằng một giá trị của {@code Signal}.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nói được vì sao độ dài chỉ là triệu chứng, không phải nguyên nhân.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Tách class quá nhỏ có thể gây vấn đề gì?
 *   Bắt đầu   : viết khối ANSWER Q4.
 *   Hoàn thành khi: ANSWER Q4 nêu chi phí điều hướng và coupling giữa các mảnh vụn.
 * <p>
 * Q5 [CODE] Với OrderService trong tài liệu, bạn sẽ tách trách nhiệm thế nào?
 *   Bắt đầu   : cài {@code totalCents}, {@code statusFor}, {@code notificationsFor}, {@code placeOrder}.
 *               {@code OrderService} cho sẵn là bản trộn trách nhiệm, đừng sửa file đó.
 *               {@code placeOrder} gọi ba method kia, không tự tính lại.
 *   Kiểm chứng: chạy q05_totalCents, q05_statusFor, q05_notificationsFor, q05_placeOrder.
 *               Ctrl+B vào {@code totalCents} trong {@code placeOrder} để thấy lời gọi tách rời.
 *   Hoàn thành khi: bốn test xanh; method nào cũng kiểm chứng được một mình.
 * <p>
 * Q6 [TỰ TRẢ LỜI] Làm sao biết việc tách abstraction thực sự có ích?
 *   Bắt đầu   : viết khối ANSWER Q6.
 *   Hoàn thành khi: ANSWER Q6 kể một thay đổi cụ thể đang tồn tại, không phải giả định về tương lai.
 */
public class Ex01_OrderServiceSplit {

    /** Dấu hiệu khi một class quá lớn. */
    public enum Signal {
        LOW_COHESION,
        MANY_LINES,
        MANY_FIELDS
    }

    public record LineItem(String sku, long unitCents, int qty) {
    }

    public record OrderRequest(String customer, List<LineItem> items, boolean vip) {
    }

    // Q1 — SRP có đồng nghĩa "một class một method" hay không.
    static final Boolean Q1_SRP_MEANS_ONE_METHOD = null;

    // Q3 — class lớn thể hiện dấu hiệu nào.
    static final Signal Q3_LARGE_CLASS_SIGNAL = null;

    /**
     * Cho sẵn, không sửa. Bản trộn validate, price, persist, charge, notify, invoice trong một method.
     */
    static final class OrderService {

        String createOrder(OrderRequest request) {
            if (request.items().isEmpty()) {
                throw new IllegalArgumentException("Đơn không có mặt hàng.");
            }
            long total = 0;
            for (LineItem item : request.items()) {
                total += item.unitCents() * item.qty();
            }
            String status = request.vip() ? "VIP" : "STANDARD";
            return status + ":" + total;
        }
    }

    /**
     * Tổng tiền của danh sách mặt hàng.
     *
     * @throws IllegalArgumentException nếu danh sách null hoặc rỗng
     */
    static long totalCents(List<LineItem> items) {
        throw new UnsupportedOperationException("TODO Q5");
    }

    /** Trạng thái đơn theo hạng khách. */
    static String statusFor(OrderRequest request) {
        throw new UnsupportedOperationException("TODO Q5");
    }

    /** Một thông báo cho mỗi mặt hàng, theo thứ tự trong đơn. */
    static List<String> notificationsFor(OrderRequest request) {
        throw new UnsupportedOperationException("TODO Q5");
    }

    /**
     * Luồng đặt hàng đã tách trách nhiệm: kiểm tra, tính tiền, lấy trạng thái, sinh thông báo.
     * Trả trạng thái và tổng tiền kèm các thông báo mặt hàng theo thứ tự.
     */
    static String placeOrder(OrderRequest request) {
        throw new UnsupportedOperationException("TODO Q5");
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

/* ANSWER Q6:
 *
 */
