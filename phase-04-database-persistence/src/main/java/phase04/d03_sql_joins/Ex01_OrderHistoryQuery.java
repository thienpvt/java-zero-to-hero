package phase04.d03_sql_joins;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 * Truy vấn nhiều bảng — B3: đọc lịch sử đơn theo khách hàng.
 * <p>Nguồn: 04-database-persistence.md, mục 3. Cần làm trước: JOIN và aggregate SQL.
 * Cách làm: chạy {@code Ex01_OrderHistoryQueryTest}; so sánh khách có đơn với khách chưa có đơn.
 * Hoàn thành khi tổng, số dòng item, customer scope và LEFT JOIN đều đúng.
 * Q1 [TỰ TRẢ LỜI] {@code LEFT JOIN} tạo kết quả gì khi không có dòng khớp?
 * Bắt đầu: viết ANSWER Q1; hoàn thành khi mô tả hàng trái vẫn còn và cột phải là NULL.
 * Q2 [TỰ TRẢ LỜI] Vì sao {@code column = NULL} không dùng để kiểm tra null?
 * Bắt đầu: viết ANSWER Q2; hoàn thành khi giải thích logic ba giá trị và cách dùng IS NULL.
 * Q3 [TỰ TRẢ LỜI] {@code WHERE} khác {@code HAVING} ở đâu?
 * Bắt đầu: viết ANSWER Q3; hoàn thành khi phân biệt lọc hàng đầu vào và nhóm aggregate.
 * Q4 [THÍ NGHIỆM] Join orders với order_items có thể làm tổng đơn hàng bị nhân đôi thế nào?
 * Bắt đầu: đối chiếu B3 với order nhiều item; hoàn thành khi tính mỗi giá snapshot nhân quantity đúng một lần.
 * Q5 [TỰ TRẢ LỜI] Khi nào window function phù hợp hơn aggregate làm mất chi tiết từng dòng?
 * Bắt đầu: viết ANSWER Q5; hoàn thành khi nêu use case cần aggregate nhưng vẫn giữ từng dòng.
 */
public class Ex01_OrderHistoryQuery {
    public record OrderHistory(long orderId, Instant createdAt, String status, long itemCount, BigDecimal total) {}
    public record CustomerOrderSummary(long customerId, String customerName, long orderCount) {}

    static List<OrderHistory> forCustomer(DataSource ds, long customerId) {
        // SOLUTION-BEGIN throw B3
        String sql = """
                SELECT o.id, o.created_at, o.status, COUNT(i.id) AS item_count,
                       COALESCE(SUM(i.quantity * i.unit_price), 0.00) AS total
                FROM orders o LEFT JOIN order_items i ON i.order_id = o.id
                WHERE o.customer_id = ?
                GROUP BY o.id, o.created_at, o.status
                ORDER BY o.created_at, o.id
                """;
        try (var connection = ds.getConnection(); var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, customerId);
            try (var result = statement.executeQuery()) {
                List<OrderHistory> rows = new ArrayList<>();
                while (result.next()) {
                    rows.add(new OrderHistory(result.getLong("id"), result.getTimestamp("created_at").toInstant(),
                            result.getString("status"), result.getLong("item_count"), result.getBigDecimal("total")));
                }
                return List.copyOf(rows);
            }
        } catch (SQLException failure) {
            throw new IllegalStateException("Không thể truy vấn lịch sử đơn hàng", failure);
        }
        // SOLUTION-END
    }

    static List<CustomerOrderSummary> allCustomers(DataSource ds) {
        // SOLUTION-BEGIN throw B3
        String sql = """
                SELECT c.id AS customer_id, c.name, COUNT(o.id) AS order_count
                FROM customers c LEFT JOIN orders o ON o.customer_id = c.id
                GROUP BY c.id, c.name
                ORDER BY c.id
                """;
        try (var connection = ds.getConnection(); var statement = connection.prepareStatement(sql);
             var result = statement.executeQuery()) {
            List<CustomerOrderSummary> rows = new ArrayList<>();
            while (result.next()) {
                rows.add(new CustomerOrderSummary(result.getLong("customer_id"), result.getString("name"),
                        result.getLong("order_count")));
            }
            return List.copyOf(rows);
        } catch (SQLException failure) {
            throw new IllegalStateException("Không thể truy vấn tổng hợp khách hàng", failure);
        }
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * LEFT JOIN giữ mọi hàng ở phía trái. Khi không có hàng phải khớp, các cột phía phải nhận NULL.
 * SOLUTION-END
 */
/* ANSWER Q2:
 * SOLUTION-BEGIN
 * NULL biểu thị chưa biết; so sánh thông thường với NULL cho kết quả UNKNOWN chứ không phải TRUE/FALSE.
 * Dùng IS NULL hoặc IS NOT NULL để kiểm tra trạng thái này.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * WHERE lọc từng hàng trước GROUP BY và aggregate; HAVING lọc các nhóm sau khi aggregate.
 * SOLUTION-END
 */
/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Window function phù hợp khi cần giá trị tổng hợp theo nhóm nhưng vẫn muốn giữ chi tiết từng hàng,
 * ví dụ xếp hạng từng order theo tổng mua trong cùng customer.
 * SOLUTION-END
 */
