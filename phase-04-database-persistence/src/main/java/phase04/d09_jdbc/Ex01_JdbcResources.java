package phase04.d09_jdbc;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

/**
 * JDBC resources và pool — B9: lookup và giảm stock bằng JDBC trực tiếp.
 * <p>Nguồn: 04-database-persistence.md, mục 9. Cần trước: JDBC và schema domain được cung cấp.
 * Bắt đầu: cài ba phương thức, chạy Ex01_JdbcResourcesTest trên PostgreSQL 18.
 * findOrder chỉ nhận ID số; thử input giống SQL bằng findProductByName riêng.
 * Hoàn thành: đọc đúng dữ liệu, cập nhật an toàn, đóng tài nguyên cả khi SQL lỗi và trả Hikari lease.
 * Q1 [CODE] {@code PreparedStatement} bảo vệ dữ liệu đầu vào theo cách nào?
 * Bắt đầu: cài lookup ID và tên; đối chiếu tên bình thường với tên có dấu nháy trong q01.
 * Hoàn thành khi trả đúng literal đã lưu, không làm đổi cấu trúc truy vấn.
 * Q2 [TỰ TRẢ LỜI] Vì sao không thể bind tên cột bằng parameter placeholder thông thường?
 * Bắt đầu: viết ANSWER Q2, phân biệt identifier trong cú pháp với giá trị đầu vào.
 * Hoàn thành khi nêu cách chọn sort/cột động không tin caller.
 * Q3 [TỰ TRẢ LỜI] {@code Connection.close()} có ý nghĩa gì khi connection do pool cấp?
 * Bắt đầu: viết ANSWER Q3, phân biệt logical lease với connection vật lý.
 * Hoàn thành khi giải thích lifecycle từ acquire đến tái sử dụng.
 * Q4 [THÍ NGHIỆM] Điều gì xảy ra nếu không đóng {@code ResultSet}/statement/connection?
 * Bắt đầu: chạy q04 và b09 với pool một connection; ghi OBSERVATION Q4 từ kết quả thật.
 * Hoàn thành khi quan sát thứ tự close và acquire lại sau SQL failure trong thời hạn test.
 * Q5 [TỰ TRẢ LỜI] Vì sao bắt SQLException rồi tiếp tục trả response thành công là nguy hiểm?
 * Bắt đầu: viết ANSWER Q5; đối chiếu đường lỗi bảng không tồn tại trong b09 với kết quả rỗng hợp lệ.
 * Hoàn thành khi phân biệt lỗi DB với không tìm thấy dữ liệu và giữ chẩn đoán lỗi.
 */
public class Ex01_JdbcResources {
    public record OrderRow(long id, long customerId, Instant createdAt, String status) {}
    public record Product(long id, String name, int stock, BigDecimal price) {}

    static Optional<OrderRow> findOrder(DataSource ds, long id) throws SQLException {
        throw new UnsupportedOperationException("TODO B9");
    }

    static List<Product> findProductByName(DataSource ds, String name) throws SQLException {
        throw new UnsupportedOperationException("TODO B9");
    }

    static int reduceStock(DataSource ds, long id, int quantity) throws SQLException {
        throw new UnsupportedOperationException("TODO B9");
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
/* OBSERVATION Q4:
 *
 */
/* ANSWER Q5:
 *
 */
