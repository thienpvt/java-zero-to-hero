package phase04.d10_jdbc_transactions;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.sql.DataSource;

/**
 * JDBC transaction và batch — B10: nhiều order items cùng commit hoặc rollback.
 * <p>Nguồn: 04-database-persistence.md, mục 10. Cần trước: JDBC và schema domain được cung cấp.
 * Bắt đầu: cài insertItems, chạy Ex01_JdbcBatchTest trên PostgreSQL 18.
 * Giá USD lấy từ products trong transaction bằng BigDecimal; caller chỉ cung cấp product và quantity.
 * B10 cố ý để DB CHECK/UNIQUE/FK từ chối member sai, không chấp nhận quantity ngoài 1–1000.
 * Hoàn thành khi counts đúng cho control hợp lệ và lỗi constraint sau member đầu rollback cả batch.
 * Q1 [TỰ TRẢ LỜI] Auto-commit làm mỗi câu lệnh có ranh giới transaction thế nào?
 * Bắt đầu: viết ANSWER Q1, so sánh nhiều câu lệnh liên tiếp với một transaction tường minh.
 * Hoàn thành khi phân biệt statement thành công trước lỗi và atomicity toàn nghiệp vụ.
 * Q2 [CODE] Khi nào cần rollback trong {@code catch} và lỗi rollback cần được xử lý ra sao?
 * Bắt đầu: cài đường lỗi insertItems, chạy q02 và b10 rồi kiểm tra DB bằng connection mới.
 * Hoàn thành khi lỗi gốc vẫn được trả về, lỗi cleanup không thay lỗi nghiệp vụ.
 * Q3 [TỰ TRẢ LỜI] Batch cải thiện chi phí nào, và không đảm bảo điều gì?
 * Bắt đầu: viết ANSWER Q3, tách tối ưu gọi driver khỏi đảm bảo transaction.
 * Hoàn thành khi nêu chi phí được giảm và giới hạn về atomicity/constraint.
 * Q4 [CODE] Vì sao batch result cần được kiểm tra?
 * Bắt đầu: đối chiếu mỗi count với member trong q04; phân biệt count số với sentinel JDBC.
 * Hoàn thành khi không commit nếu kết quả không chứng minh từng member đã thành công.
 * Q5 [TỰ TRẢ LỜI] Có nên retry mọi SQLException không? Vì sao?
 * Bắt đầu: viết ANSWER Q5, dùng SQLSTATE của b10 làm ví dụ phân loại lỗi.
 * Hoàn thành khi phân biệt lỗi cần sửa dữ liệu với lỗi tạm thời và giới hạn retry.
 */
public class Ex01_JdbcBatch {
    public record Line(long productId, int quantity) {}

    static int[] insertItems(DataSource ds, long orderId, List<Line> items) throws SQLException {
        // SOLUTION-BEGIN throw B10
        if (items == null || items.isEmpty() || items.stream().anyMatch(line -> line == null)) {
            throw new IllegalArgumentException("Batch phải có các Line không null");
        }
        try (var connection = ds.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int[] counts;
                try (var priceQuery = connection.prepareStatement("SELECT price FROM products WHERE id=?");
                     var batch = connection.prepareStatement(
                        "INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (?,?,?,?)")) {
                    for (var line : items) {
                        priceQuery.setLong(1, line.productId());
                        BigDecimal price;
                        try (var result = priceQuery.executeQuery()) {
                            if (!result.next()) throw new SQLException("Không tìm thấy product", "02000");
                            price = result.getBigDecimal("price");
                        }
                        batch.setLong(1, orderId);
                        batch.setLong(2, line.productId());
                        batch.setInt(3, line.quantity());
                        batch.setBigDecimal(4, price);
                        batch.addBatch();
                    }
                    counts = batch.executeBatch();
                    if (counts.length != items.size()) throw new SQLException("Thiếu batch result", "HY000");
                    for (int count : counts) {
                        if (count != 1 && count != Statement.SUCCESS_NO_INFO) {
                            throw new SQLException("Batch member không thành công", "HY000");
                        }
                    }
                }
                connection.commit();
                return counts;
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
 * Auto-commit mặc định commit mỗi statement khi hoàn tất; statement trước có thể đã commit dù statement sau lỗi.
 * Muốn atomicity cho toàn batch nghiệp vụ phải tắt auto-commit, kiểm tra kết quả rồi commit một lần.
 * SOLUTION-END
 */
/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Sau khi bắt đầu transaction tường minh, lỗi SQL/runtime trước commit cần rollback toàn transaction.
 * Throw lại cùng exception gốc để giữ SQLSTATE/cause/nextException; addSuppressed cho lỗi rollback.
 * try-with-resources đóng connection và tự suppress lỗi close; không trả counts thành công trên đường lỗi.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Batch giảm số lần gọi/round trip và chi phí gửi nhiều statement tùy driver/cấu hình.
 * Nó không tự tạo atomicity, không bỏ qua constraints, và không đảm bảo mọi member thành công.
 * Giá vẫn đọc từ DB, không lấy từ caller; transaction tường minh mới rollback cả đơn vị nghiệp vụ.
 * SOLUTION-END
 */
/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Phải kiểm tra số counts và mỗi member: insert một hàng cần 1, hoặc SUCCESS_NO_INFO là thành công không rõ số.
 * EXECUTE_FAILED, 0 hoặc count khác mong đợi không chứng minh insert thành công; rollback thay vì commit.
 * BatchUpdateException giữ SQLSTATE, updateCounts và exception chain; counts khi lỗi không chứng minh đã commit.
 * B10 ghi nhận PostgreSQL 18.0 CHECK quantity trả 23514, UNIQUE trả 23505, FK order trả 23503.
 * SOLUTION-END
 */
/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Không retry mọi SQLException: lỗi constraint như 23514/23505 cần sửa input, retry nguyên trạng vẫn sai.
 * Chỉ retry lỗi tạm thời đã phân loại như 40001/40P01, có giới hạn và chạy lại toàn transaction.
 * Mất kết nối lúc commit có thể khiến kết quả chưa rõ; cần idempotency/đối soát, không retry mù.
 * Không có payment/email hoặc tác dụng mạng bên ngoài trong transaction này.
 * SOLUTION-END
 */
