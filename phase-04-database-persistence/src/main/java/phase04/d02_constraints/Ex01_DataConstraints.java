package phase04.d02_constraints;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import javax.sql.DataSource;

/**
 * Kiểu dữ liệu và constraint — B2: kiểm chứng invariant ngay tại database.
 * <p>Nguồn: 04-database-persistence.md, mục 2. Cần làm trước: JDBC và kiểu SQL cơ bản.
 * Cách làm: chạy {@code Ex01_DataConstraintsTest}; thử cả dữ liệu sai lẫn explicit NULL.
 * Hoàn thành khi giá hợp lệ đọc đúng và các ghi vi phạm bị DB từ chối với SQLSTATE.
 * Q1 [TỰ TRẢ LỜI] Vì sao {@code double} không phù hợp để biểu diễn tiền?
 * Bắt đầu: viết ANSWER Q1; hoàn thành khi giải thích được sai số biểu diễn nhị phân.
 * Q2 [CODE] Constraint nào đảm bảo quantity của order item lớn hơn 0?
 * Bắt đầu: kiểm tra B2; hoàn thành khi CHECK cùng NOT NULL giới hạn quantity từ 1 đến 1000.
 * Q3 [TỰ TRẢ LỜI] Vì sao invariant quan trọng nên được bảo vệ cả ở DB?
 * Bắt đầu: viết ANSWER Q3; hoàn thành khi nêu được writer khác Java cũng phải tuân thủ.
 * Q4 [TỰ TRẢ LỜI] Khi nào dùng {@code TIMESTAMP WITH TIME ZONE} thay cho {@code DATE}?
 * Bắt đầu: viết ANSWER Q4; hoàn thành khi phân biệt instant với ngày lịch.
 * Q5 [THÍ NGHIỆM] Default có tự áp dụng khi ứng dụng ghi explicit {@code NULL} không?
 * Bắt đầu: chạy B2 với explicit NULL; hoàn thành khi ghi SQLSTATE và giải thích default chỉ áp dụng khi bỏ cột hoặc dùng DEFAULT.
 */
public class Ex01_DataConstraints {
    /** Đọc giá sản phẩm từ PostgreSQL; ID không tồn tại là lỗi gọi rõ ràng. */
    static BigDecimal priceFor(long productId, DataSource ds) {
        // SOLUTION-BEGIN throw B2
        try (var connection = ds.getConnection();
             var statement = connection.prepareStatement("SELECT price FROM products WHERE id = ?")) {
            statement.setLong(1, productId);
            try (var result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new NoSuchElementException("Không tìm thấy product id=" + productId);
                }
                return result.getBigDecimal("price");
            }
        } catch (SQLException failure) {
            throw new IllegalStateException("Không thể đọc giá sản phẩm", failure);
        }
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * double dùng IEEE 754 nhị phân nên nhiều phần thập phân, ví dụ 0.1, không biểu diễn chính xác.
 * NUMERIC/BigDecimal biểu diễn số thập phân theo precision và scale đã chọn, phù hợp để lưu tiền.
 * SOLUTION-END
 */
/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Dùng `CHECK (quantity BETWEEN 1 AND 1000)` để giới hạn miền; `NOT NULL` riêng biệt cấm giá trị thiếu.
 * Nếu chỉ có CHECK, SQL cho kết quả NULL là chưa biết và vẫn chấp nhận dòng, nên cần cả hai constraint.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Constraint ở DB bảo vệ invariant trước mọi writer, kể cả SQL thủ công, batch hoặc chương trình khác.
 * Validation ở Java vẫn hữu ích cho thông báo sớm nhưng không thay thế ranh giới dữ liệu dùng chung.
 * SOLUTION-END
 */
/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Dùng TIMESTAMP WITH TIME ZONE khi cần lưu một instant như thời điểm tạo đơn; DATE chỉ lưu ngày lịch.
 * PostgreSQL chuẩn hóa instant, không giữ tên timezone gốc để hiển thị.
 * SOLUTION-END
 */
/* OBSERVATION Q5 / ANSWER Q5:
 * SOLUTION-BEGIN
 * Default áp dụng khi bỏ cột `created_at` khỏi INSERT hoặc ghi từ khóa `DEFAULT`; explicit `NULL` không kích hoạt nó.
 * `orders.created_at` có NOT NULL nhưng không có default, nên INSERT mẫu có `NULL` bị từ chối.
 * SOLUTION-END
 */
