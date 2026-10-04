package phase04.d17_capstone;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import javax.sql.DataSource;

/**
 * Order/inventory persistence — B17; JDBC, PostgreSQL 18, giá BigDecimal USD cố định.
 * <p>Nguồn: 04-database-persistence.md, mục 17. Topic 16 cung cấp migrated schema.
 * customerId là caller đã tin cậy, SQL scope không thay thế HTTP authorization.
 * Missing customer/product: NoSuchElementException; thiếu stock: InsufficientStockException;
 * input sai: IllegalArgumentException; lỗi JDBC giữ SQLException, không trả fake success.
 * Q1 [TỰ TRẢ LỜI] Giá order item lấy từ đâu và vì sao?
 * Cần trước: NUMERIC và snapshot. Bắt đầu: viết ANSWER Q1, xem giá DB trước/sau mua.
 * Tài liệu: https://www.postgresql.org/docs/18/datatype-numeric.html
 * Debug: so sánh products.price/order_items.unit_price, không dùng double/client price.
 * Hoàn thành: tổng lịch sử giữ nguyên khi giá catalog đổi, currency lab luôn USD.
 * Q2 [CODE] Thành phần nào phải rollback cùng nhau khi một item thiếu stock?
 * Cần trước: transaction JDBC và constraint. Bắt đầu: cài createOrder, chạy q02/B17.
 * Tài liệu: https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/Connection.html
 * Debug: fresh connection kiểm tra orders/items/stock sau lỗi item thứ hai.
 * Hoàn thành: validate trước writes; rollback cả SQLException/RuntimeException/Error.
 * Q3 [CODE] Làm sao chứng minh hai request đồng thời không oversell?
 * Cần trước: affected-row count và row lock. Bắt đầu: hoàn thành createOrder, chạy q03.
 * Tài liệu: https://www.postgresql.org/docs/18/explicit-locking.html
 * Debug: hai backend PID, gate trước locks, timeout có giới hạn, không sleep/JVM lock.
 * Hoàn thành: accepted quantity + stock cuối = stock đầu, chỉ một request mua stock=1.
 * Q4 [CODE] Làm sao chứng minh query list không bị N+1 và pagination đúng?
 * Cần trước: JOIN/aggregate, ORDER BY và OFFSET. Bắt đầu: cài ordersForCustomer, chạy q04.
 * Tài liệu: https://www.postgresql.org/docs/18/queries-limit.html
 * Debug: đếm executeQuery thật, kiểm tra tie created_at và customer predicate trong DB.
 * Hoàn thành: một projection query/trang, page zero-based, size 1–100, không foreign rows.
 * Q5 [THÍ NGHIỆM] Bằng chứng nào cần lưu để giải thích lựa chọn index và transaction?
 * Cần trước: EXPLAIN ANALYZE BUFFERS. Bắt đầu: chạy q05, đọc file target/task6-evidence.
 * Tài liệu: https://www.postgresql.org/docs/18/using-explain.html
 * Debug: lưu version/dataset/SQL/params/query count/plan trước-sau, không timing golden.
 * Hoàn thành: OBSERVATION ghi trade-off index và transaction, không khẳng định production SLA.
 */
public class Ex01_OrderPersistence {
    public record Line(long productId, int quantity) {}
    public record OrderHistory(long orderId, Instant createdAt, String status, long itemCount, BigDecimal total) {}
    public static class InsufficientStockException extends RuntimeException {
        public InsufficientStockException(long productId) { super("Insufficient stock for product " + productId); }
    }

    public static long createOrder(DataSource ds, long customerId, List<Line> items) throws SQLException {
        throw new UnsupportedOperationException("TODO B17");
    }

    public static List<OrderHistory> ordersForCustomer(DataSource ds, long customerId, int page, int size)
            throws SQLException {
        throw new UnsupportedOperationException("TODO Q4");
    }
}

/* ANSWER Q1:
 *
 */
/* ANSWER Q2,Q3,Q4 / OBSERVATION B17:
 *
 */
/* OBSERVATION Q5:
 *
 */
