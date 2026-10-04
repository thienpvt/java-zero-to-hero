package phase04.d07_transactions;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import javax.sql.DataSource;

/**
 * ACID và transaction boundary — B7: order, items và stock trong DB local.
 * <p>Nguồn: 04-database-persistence.md, mục 7. Cần trước: JDBC và bảng domain được cung cấp.
 * Bắt đầu: hoàn thành create, chạy Ex01_OrderTransactionTest trên PostgreSQL 18.
 * Giá USD dùng BigDecimal; Line không nhận giá hoặc tổng tiền từ caller.
 * Hoàn thành: control hợp lệ commit; valid-first/insufficient-second không để lại thay đổi.
 * Không gọi payment API, email hoặc mạng ngoài DB trong bài này.
 * Q1 [CODE] Transaction boundary của tạo order và giảm stock nên bao gồm thao tác nào?
 * Bắt đầu: cài create cho nhiều Line; kiểm tra q01 và b07 bằng connection mới sau mỗi lời gọi.
 * Hoàn thành khi order, snapshot items và stock cùng thành công hoặc cùng không thay đổi.
 * Q2 [TỰ TRẢ LỜI] MVCC giúp readers và writers tương tác ra sao?
 * Bắt đầu: viết ANSWER Q2, phân biệt snapshot với lock khi hai writer tranh một hàng.
 * Hoàn thành khi giải thích cả tương tác đọc/ghi và giới hạn của snapshot.
 * Q3 [TỰ TRẢ LỜI] Local DB transaction không đảm bảo điều gì với payment API?
 * Bắt đầu: viết ANSWER Q3 bằng một tình huống DB và nhà cung cấp payment có kết quả khác nhau.
 * Hoàn thành khi nêu phạm vi atomicity và cách xử lý tác dụng ngoài DB.
 * Q4 [TỰ TRẢ LỜI] Vì sao transaction dài có thể gây vấn đề cho hệ thống?
 * Bắt đầu: viết ANSWER Q4, xét lock, snapshot và tài nguyên khi request kéo dài.
 * Hoàn thành khi nêu chi phí và đề xuất ranh giới ngắn cho flow này.
 * Q5 [TỰ TRẢ LỜI] Khi DB commit thành công nhưng email gửi lỗi, hệ thống cần xem đây là loại failure nào?
 * Bắt đầu: viết ANSWER Q5, phân biệt kết quả đặt hàng với kết quả thông báo.
 * Hoàn thành khi mô tả trạng thái đã commit và cơ chế phục hồi gửi email.
 */
public class Ex01_OrderTransaction {
    public record Line(long productId, int quantity) {}
    public static final class InsufficientStockException extends RuntimeException {
        InsufficientStockException(long productId) { super("Không đủ stock cho product " + productId); }
    }

    static void create(DataSource ds, long orderId, long customerId, List<Line> items) throws SQLException {
        throw new UnsupportedOperationException("TODO B7");
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
