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
        throw new UnsupportedOperationException("TODO B3");
    }

    static List<CustomerOrderSummary> allCustomers(DataSource ds) {
        throw new UnsupportedOperationException("TODO B3");
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
/* OBSERVATION Q4 / ANSWER Q4:
 *
 */
/* ANSWER Q5:
 *
 */
