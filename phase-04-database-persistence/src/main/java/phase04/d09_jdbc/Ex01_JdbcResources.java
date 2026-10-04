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
        // SOLUTION-BEGIN throw B9
        try (var connection = ds.getConnection(); var statement = connection.prepareStatement(
                "SELECT id,customer_id,created_at,status FROM orders WHERE id=?")) {
            statement.setLong(1, id);
            try (var result = statement.executeQuery()) {
                return result.next() ? Optional.of(new OrderRow(result.getLong("id"), result.getLong("customer_id"),
                        result.getTimestamp("created_at").toInstant(), result.getString("status"))) : Optional.empty();
            }
        }
        // SOLUTION-END
    }

    static List<Product> findProductByName(DataSource ds, String name) throws SQLException {
        // SOLUTION-BEGIN throw B9
        if (name == null) throw new IllegalArgumentException("Tên product không được null");
        try (var connection = ds.getConnection(); var statement = connection.prepareStatement(
                "SELECT id,name,stock,price FROM products WHERE name=?")) {
            statement.setString(1, name);
            try (var result = statement.executeQuery()) {
                List<Product> products = new ArrayList<>();
                while (result.next()) {
                    products.add(new Product(result.getLong("id"), result.getString("name"),
                            result.getInt("stock"), result.getBigDecimal("price")));
                }
                return List.copyOf(products);
            }
        }
        // SOLUTION-END
    }

    static int reduceStock(DataSource ds, long id, int quantity) throws SQLException {
        // SOLUTION-BEGIN throw B9
        if (quantity < 1 || quantity > 1000) throw new IllegalArgumentException("Quantity phải trong 1–1000");
        try (var connection = ds.getConnection(); var statement = connection.prepareStatement(
                "UPDATE products SET stock=stock-? WHERE id=? AND stock>=?")) {
            statement.setInt(1, quantity);
            statement.setLong(2, id);
            statement.setInt(3, quantity);
            return statement.executeUpdate();
        }
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * SQL cố định và parameter được gửi riêng; setString/setLong coi input là dữ liệu, không phải cú pháp SQL.
 * Input có dấu nháy vẫn được so khớp literal. ID long không phải thí nghiệm injection chuỗi.
 * SOLUTION-END
 */
/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Placeholder đứng ở vị trí giá trị, không tạo identifier hoặc keyword khi DB phân tích cú pháp.
 * Cột/sort động cần ánh xạ allowlist sang SQL cố định; không nối trực tiếp tên caller gửi.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * close kết thúc logical lease và trả connection về pool để tái sử dụng; thường không đóng socket vật lý.
 * Không dùng lại handle đã close. Pool giữ lifecycle vật lý và đóng chúng khi shutdown hoặc connection hỏng.
 * SOLUTION-END
 */
/* OBSERVATION Q4:
 * SOLUTION-BEGIN
 * q04 quan sát close ResultSet, PreparedStatement rồi Connection trên JDBC PostgreSQL thật.
 * b09 dùng Hikari tối đa một lease: active=0 sau thành công và SQLSTATE 42P01;
 * acquire/close/acquire lại chạy SELECT 1 trong 2 giây. Nếu quên close lease, pool một connection bị cạn,
 * lời gọi tiếp theo chờ rồi timeout; cursor/statement không đóng còn giữ tài nguyên phía driver/server.
 * Dataset: một order, ba products, một tên dạng injection; PostgreSQL 18.0, không dùng timing golden.
 * SOLUTION-END
 */
/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Trả thành công che lỗi ghi/đọc, khiến caller tin dữ liệu đã lưu hoặc không tồn tại và không thể phục hồi đúng.
 * Propagate SQLException nguyên bản để giữ SQLSTATE, cause và exception chain; không đổi lỗi thành empty/0 giả.
 * Không log URL/password; try-with-resources vẫn giải phóng tài nguyên khi exception lan ra.
 * SOLUTION-END
 */
