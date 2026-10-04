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
        // SOLUTION-BEGIN throw B17
        if (ds == null || customerId <= 0 || items == null || items.isEmpty())
            throw new IllegalArgumentException("DataSource/customer/nonempty items required");
        var lines = new ArrayList<>(items); // never sort or mutate caller list
        var ids = new HashSet<Long>();
        for (Line line : lines) {
            if (line == null || line.productId() <= 0 || line.quantity() < 1 || line.quantity() > 1000
                    || !ids.add(line.productId()))
                throw new IllegalArgumentException("Positive unique product ids; quantity 1–1000");
        }
        lines.sort(Comparator.comparingLong(Line::productId)); // same lock order across transactions
        try (var c = ds.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (var owner = c.prepareStatement("SELECT id FROM customers WHERE id=? FOR KEY SHARE")) {
                    owner.setQueryTimeout(10); owner.setLong(1, customerId);
                    try (var rows = owner.executeQuery()) {
                        if (!rows.next()) throw new NoSuchElementException("Missing customer " + customerId);
                    }
                }
                long orderId;
                try (var order = c.prepareStatement(
                        "INSERT INTO orders(customer_id,status,created_at) VALUES (?,'NEW',CURRENT_TIMESTAMP) RETURNING id")) {
                    order.setQueryTimeout(10); order.setLong(1, customerId);
                    try (var rows = order.executeQuery()) {
                        if (!rows.next()) throw new SQLException("Order insert returned no id");
                        orderId = rows.getLong(1);
                    }
                }
                try (var product = c.prepareStatement("SELECT price FROM products WHERE id=? FOR UPDATE");
                     var stock = c.prepareStatement("UPDATE products SET stock=stock-? WHERE id=? AND stock>=?");
                     var item = c.prepareStatement(
                             "INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (?,?,?,?)")) {
                    product.setQueryTimeout(10); stock.setQueryTimeout(10); item.setQueryTimeout(10);
                    for (Line line : lines) {
                        product.setLong(1, line.productId());
                        BigDecimal price;
                        try (var rows = product.executeQuery()) {
                            if (!rows.next()) throw new NoSuchElementException("Missing product " + line.productId());
                            price = rows.getBigDecimal(1);
                        }
                        stock.setInt(1, line.quantity()); stock.setLong(2, line.productId()); stock.setInt(3, line.quantity());
                        if (stock.executeUpdate() != 1) throw new InsufficientStockException(line.productId());
                        item.setLong(1, orderId); item.setLong(2, line.productId());
                        item.setInt(3, line.quantity()); item.setBigDecimal(4, price);
                        if (item.executeUpdate() != 1) throw new SQLException("Item insert affected unexpected rows");
                    }
                }
                c.commit();
                return orderId;
            } catch (SQLException | RuntimeException | Error failure) {
                try { c.rollback(); } catch (SQLException | RuntimeException | Error rollback) { failure.addSuppressed(rollback); }
                throw failure;
            }
        }
        // SOLUTION-END
    }

    public static List<OrderHistory> ordersForCustomer(DataSource ds, long customerId, int page, int size)
            throws SQLException {
        // SOLUTION-BEGIN throw Q4
        if (ds == null || customerId <= 0 || page < 0 || size < 1 || size > 100)
            throw new IllegalArgumentException("DataSource/positive customer required; page>=0, size 1–100");
        String sql = """
                SELECT o.id, o.created_at, o.status, COUNT(i.id) AS item_count,
                       COALESCE(SUM(i.quantity * i.unit_price),0.00) AS total
                FROM (SELECT id, created_at, status FROM orders WHERE customer_id=?
                      ORDER BY created_at,id LIMIT ? OFFSET ?) o
                LEFT JOIN order_items i ON i.order_id=o.id
                GROUP BY o.id,o.created_at,o.status
                ORDER BY o.created_at,o.id
                """;
        try (var c = ds.getConnection(); var query = c.prepareStatement(sql)) {
            query.setQueryTimeout(10);
            query.setLong(1, customerId); query.setInt(2, size); query.setLong(3, (long) page * size);
            var history = new ArrayList<OrderHistory>();
            try (var rows = query.executeQuery()) {
                while (rows.next()) history.add(new OrderHistory(rows.getLong("id"),
                        rows.getObject("created_at", java.time.OffsetDateTime.class).toInstant(),
                        rows.getString("status"), rows.getLong("item_count"), rows.getBigDecimal("total")));
            }
            return List.copyOf(history);
        }
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Đọc products.price từ DB dưới row lock, lưu BigDecimal unit_price snapshot USD vào item.
 * Caller chỉ gửi productId/quantity; không nhận client price/total. Giá catalog đổi không
 * làm đổi tổng lịch sử; tổng là SUM(quantity*unit_price) NUMERIC, không qua double.
 * SOLUTION-END
 */
/* ANSWER Q2,Q3,Q4 / OBSERVATION B17:
 * SOLUTION-BEGIN
 * Validate toàn input trước connection/write. Một transaction giữ owner key share và locks
 * product theo id tăng dần; conditional UPDATE kiểm tra affected rows. Order/item/stock cùng
 * rollback với SQL/Runtime/Error, rollback lỗi suppressed trên lỗi gốc. Không retry commit
 * không rõ kết quả; sequence gaps sau rollback bình thường, không tái dùng generated id.
 * Hai request backend riêng gate trước lock, futures/query timeout, stock=1 chỉ bán một.
 * SQL scope customer trước LIMIT/OFFSET, projection aggregate một query, COUNT item rows
 * không phải tổng quantity; created_at,id ASC tie-break, offset long tránh overflow.
 * SOLUTION-END
 */
/* OBSERVATION Q5:
 * SOLUTION-BEGIN
 * q05 lưu PostgreSQL/JDK, seed 2001 orders/items, SQL thực thi và bound params, query count=1
 * cho trang 20 rows, EXPLAIN ANALYZE BUFFERS trước/sau provided_plan_history(customer_id).
 * Index scaffold chỉ phục vụ customer predicate, không cung cấp đáp án composite index d04.
 * Plan ghi Seq Scan order_items trước/sau: không suy ra unique index đã được dùng cho join.
 * Index tốn write/storage; cache và dataset nhỏ không đại diện production, không timing SLA.
 * DB-only TX ngắn, price snapshot dưới product lock chống đổi giá giữa đọc/ghi; lock order
 * giảm deadlock giữa chính flow này, không bảo đảm mọi writer khác cùng tuân thủ.
 * SOLUTION-END
 */
