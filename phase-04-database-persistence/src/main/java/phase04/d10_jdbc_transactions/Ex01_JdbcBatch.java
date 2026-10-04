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
        throw new UnsupportedOperationException("TODO B10");
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
