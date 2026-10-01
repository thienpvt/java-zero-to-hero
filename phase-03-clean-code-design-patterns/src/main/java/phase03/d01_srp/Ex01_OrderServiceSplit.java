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
    static final Boolean Q1_SRP_MEANS_ONE_METHOD = false; // SOLUTION-VALUE

    // Q3 — class lớn thể hiện dấu hiệu nào.
    static final Signal Q3_LARGE_CLASS_SIGNAL = Signal.LOW_COHESION; // SOLUTION-VALUE

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
        // SOLUTION-BEGIN throw Q5
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Đơn không có mặt hàng.");
        }
        long total = 0;
        for (LineItem item : items) {
            total += item.unitCents() * item.qty();
        }
        return total;
        // SOLUTION-END
    }

    /** Trạng thái đơn theo hạng khách. */
    static String statusFor(OrderRequest request) {
        // SOLUTION-BEGIN throw Q5
        return request.vip() ? "VIP" : "STANDARD";
        // SOLUTION-END
    }

    /** Một thông báo cho mỗi mặt hàng, theo thứ tự trong đơn. */
    static List<String> notificationsFor(OrderRequest request) {
        // SOLUTION-BEGIN throw Q5
        List<String> messages = new ArrayList<>();
        for (LineItem item : request.items()) {
            messages.add("Đã đặt " + item.sku());
        }
        return messages;
        // SOLUTION-END
    }

    /**
     * Luồng đặt hàng đã tách trách nhiệm: kiểm tra, tính tiền, lấy trạng thái, sinh thông báo.
     * Trả trạng thái và tổng tiền kèm các thông báo mặt hàng theo thứ tự.
     */
    static String placeOrder(OrderRequest request) {
        // SOLUTION-BEGIN throw Q5
        long total = totalCents(request.items());
        return statusFor(request) + ":" + total + "|" + String.join(",", notificationsFor(request));
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Không. SRP nói về lý do để thay đổi, không đếm method.
 * Một class có nhiều method vẫn đúng SRP nếu mọi method phục vụ cùng một nhóm trách nhiệm.
 * Ngược lại, hai method phục vụ hai actor khác nhau đã là hai lý do thay đổi.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * "Reason to change" là một nguồn yêu cầu thay đổi, thường là một actor hoặc một hệ thống ngoài.
 * Nếu quy tắc thuế đổi mà phải sửa class này, đó là một lý do.
 * Nếu định dạng hoá đơn đổi mà cũng phải sửa chính class đó, đó là lý do thứ hai.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Dấu hiệu là cohesion thấp: các field và method phục vụ nhiều nhóm trách nhiệm rời nhau.
 * Nhiều dòng hoặc nhiều field chỉ là triệu chứng dễ thấy của cohesion thấp, không phải nguyên nhân.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Tách quá nhỏ tạo nhiều class chỉ gọi nhau một lần, thêm chi phí điều hướng khi đọc code.
 * Abstraction giả còn che mất luồng nghiệp vụ và làm thay đổi nhỏ phải sửa nhiều file.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Việc tách có ích khi tồn tại một thay đổi cụ thể đã xảy ra hoặc đang chờ, ví dụ đổi cách tính giá.
 * Khi đó chỉ một class đổi và test của nó đổi theo, không phải sửa luồng đặt hàng.
 * Nếu lý do chỉ là "tương lai có thể cần", chưa đủ để tách.
 * SOLUTION-END
 */
