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
        // SOLUTION-BEGIN throw B7
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Order phải có items");
        var productIds = new HashSet<Long>();
        for (var line : items) {
            if (line == null || line.quantity() < 1 || line.quantity() > 1000
                    || !productIds.add(line.productId())) {
                throw new IllegalArgumentException("Line không hợp lệ hoặc product trùng");
            }
        }
        try (var connection = ds.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (var order = connection.prepareStatement(
                        "INSERT INTO orders(id,customer_id,status,created_at) VALUES (?,?,'NEW',CURRENT_TIMESTAMP)")) {
                    order.setLong(1, orderId);
                    order.setLong(2, customerId);
                    order.executeUpdate();
                }
                try (var reserve = connection.prepareStatement(
                        "UPDATE products SET stock=stock-? WHERE id=? AND stock>=? RETURNING price");
                     var item = connection.prepareStatement(
                        "INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (?,?,?,?)")) {
                    for (var line : items) {
                        reserve.setInt(1, line.quantity());
                        reserve.setLong(2, line.productId());
                        reserve.setInt(3, line.quantity());
                        BigDecimal price;
                        try (var result = reserve.executeQuery()) {
                            if (!result.next()) throw new InsufficientStockException(line.productId());
                            price = result.getBigDecimal("price");
                        }
                        item.setLong(1, orderId);
                        item.setLong(2, line.productId());
                        item.setInt(3, line.quantity());
                        item.setBigDecimal(4, price);
                        item.executeUpdate();
                    }
                }
                connection.commit();
            } catch (SQLException | RuntimeException failure) {
                try { connection.rollback(); } catch (SQLException rollback) { failure.addSuppressed(rollback); }
                throw failure;
            }
        }
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Một transaction chứa insert order, mọi item với đơn giá lấy từ products, và mọi giảm stock có điều kiện.
 * Chỉ commit sau tất cả thao tác; lỗi bất kỳ rollback toàn transaction. Không bao gồm tác dụng mạng bên ngoài.
 * SOLUTION-END
 */
/* ANSWER Q2:
 * SOLUTION-BEGIN
 * PostgreSQL MVCC cho reader đọc phiên bản hàng phù hợp snapshot, thường không chặn writer và ngược lại.
 * Writer cùng sửa một hàng vẫn có thể chờ row lock; snapshot không thay thế invariant hoặc conditional update.
 * Snapshot và anomaly còn phụ thuộc isolation level.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Rollback local DB không hoàn tác payment đã thành công; commit DB cũng không đảm bảo payment thành công.
 * Không có atomic commit chung với API bên ngoài. Dùng idempotency, trạng thái nghiệp vụ và compensation;
 * outbox có thể lưu ý định gửi cùng transaction, không biến API thành một phần của rollback DB.
 * SOLUTION-END
 */
/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Transaction dài giữ lock và connection lâu, tăng contention/deadlock; snapshot cũ cản thu hồi phiên bản MVCC.
 * Giữ phần kiểm tra/ghi DB ngắn; không chờ email, payment hay người dùng khi đang giữ transaction.
 * SOLUTION-END
 */
/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Đây là lỗi notification sau commit, không phải thất bại atomic của order đã lưu.
 * Giữ order đã commit; retry email có idempotency, ghi trạng thái gửi hoặc outbox, không rollback tưởng tượng.
 * SOLUTION-END
 */
