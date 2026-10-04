package phase04.d14_locking;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Stock locking — B14: conditional update, optimistic version và pessimistic row lock.
 * <p>Nguồn: 04-database-persistence.md, mục 14. Cần trước: JDBC transaction, affected count.
 * Bắt đầu: cài reserve; chạy Ex01_StockLockingTest với PostgreSQL 18 và hai connection riêng.
 * Debug: đọc stock đã commit bằng connection thứ ba; không dùng Java lock cho correctness.
 * Tài liệu: https://www.postgresql.org/docs/18/explicit-locking.html
 * Hoàn thành: accepted quantity + stock cuối = stock đầu; thiếu/mất product trả false.
 * Caller sở hữu commit/rollback cho mọi method; quantity 1–1000, productId dương trước SQL.
 * Optimistic comparison dùng products.version chỉ trong fixture cục bộ, không đổi schema chung.
 * Q1 [TỰ TRẢ LỜI] Vì sao read-then-write không điều kiện có race condition?
 * Bắt đầu: viết ANSWER Q1 với hai reader cùng thấy stock cũ.
 * Hoàn thành khi giải thích lost update và oversell dù mỗi UPDATE riêng nguyên tử.
 * Q2 [CODE] Số dòng update có ý nghĩa gì với conditional stock update?
 * Bắt đầu: cài reserve, chạy q02 trên product đủ/thiếu/không tồn tại.
 * Hoàn thành khi boolean phản ánh affected count, không coi zero rows là thành công.
 * Q3 [TỰ TRẢ LỜI] Optimistic và pessimistic locking đánh đổi điều gì?
 * Bắt đầu: viết ANSWER Q3 và cài hai method so sánh trong B14.
 * Hoàn thành khi nêu conflict/retry so với chờ lock và giữ transaction ngắn.
 * Q4 [TỰ TRẢ LỜI] Vì sao Java lock không đủ cho nhiều instance ứng dụng?
 * Bắt đầu: viết ANSWER Q4, xét hai JVM dùng connection riêng.
 * Hoàn thành khi đặt invariant tại database, không giả định JVM chung.
 * Q5 [CODE] Làm sao đảm bảo invariant `stock >= 0` cả khi có nhiều writer?
 * Bắt đầu: chạy q05 và B14 với barrier trước contested UPDATE/lock.
 * Hoàn thành khi kiểm tra conservation, at most inventory accepted và DB CHECK.
 */
public class Ex01_StockLocking {
    static boolean reserve(Connection c, long productId, int quantity) throws SQLException {
        // SOLUTION-BEGIN throw Q2
        if (productId < 1 || quantity < 1 || quantity > 1000)
            throw new IllegalArgumentException("Product dương và quantity 1–1000");
        try (var s = c.prepareStatement("UPDATE products SET stock=stock-? WHERE id=? AND stock>=?")) {
            s.setQueryTimeout(10);
            s.setInt(1, quantity);
            s.setLong(2, productId);
            s.setInt(3, quantity);
            return s.executeUpdate() == 1;
        }
        // SOLUTION-END
    }

    static boolean reserveOptimistic(Connection c, long productId, int quantity, int expectedVersion)
            throws SQLException {
        // SOLUTION-BEGIN throw B14
        if (productId < 1 || quantity < 1 || quantity > 1000 || expectedVersion < 0)
            throw new IllegalArgumentException("Product/quantity/version không hợp lệ");
        try (var s = c.prepareStatement(
                "UPDATE products SET stock=stock-?,version=version+1 WHERE id=? AND stock>=? AND version=?")) {
            s.setQueryTimeout(10);
            s.setInt(1, quantity);
            s.setLong(2, productId);
            s.setInt(3, quantity);
            s.setInt(4, expectedVersion);
            return s.executeUpdate() == 1;
        }
        // SOLUTION-END
    }

    static boolean reservePessimistic(Connection c, long productId, int quantity) throws SQLException {
        // SOLUTION-BEGIN throw Q5
        if (productId < 1 || quantity < 1 || quantity > 1000)
            throw new IllegalArgumentException("Product dương và quantity 1–1000");
        if (c.getAutoCommit()) throw new IllegalArgumentException("Caller phải mở transaction");
        try (var read = c.prepareStatement("SELECT stock FROM products WHERE id=? FOR UPDATE")) {
            read.setQueryTimeout(10);
            read.setLong(1, productId);
            try (var r = read.executeQuery()) {
                if (!r.next() || r.getInt(1) < quantity) return false;
            }
        }
        try (var write = c.prepareStatement("UPDATE products SET stock=stock-? WHERE id=?")) {
            write.setQueryTimeout(10);
            write.setInt(1, quantity);
            write.setLong(2, productId);
            return write.executeUpdate() == 1;
        }
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Hai request đọc cùng stock rồi ghi giá trị tính từ stock cũ có thể cùng báo mua thành công.
 * UPDATE không điều kiện không liên kết kiểm tra với giảm stock; lost update che lượng đã bán.
 * Dùng atomic conditional update/affected count hoặc lock/version đúng trong cùng transaction.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Optimistic kiểm tra version khi ghi, không giữ row lock từ lúc đọc; conflict cần đọc lại/retry
 * cả quyết định nghiệp vụ. Pessimistic SELECT FOR UPDATE giữ lock tới commit/rollback; writer
 * khác chờ, có nguy cơ deadlock và tăng latency. Comparison version là field fixture local.
 * SOLUTION-END
 */
/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Java lock chỉ phối hợp thread dùng cùng lock object trong một JVM. JVM khác và SQL client
 * không tham gia lock đó. PostgreSQL row lock/atomic predicate và constraint cùng bảo vệ DB.
 * SOLUTION-END
 */
/* OBSERVATION B14 / ANSWER Q2,Q5:
 * SOLUTION-BEGIN
 * Stock đầu 1, hai TX mua 1: một affected row, một zero rows, accepted=1, stock cuối=0.
 * Optimistic cùng version: một cập nhật version, peer conflict. Pessimistic peer chờ rồi đọc
 * stock sau commit, từ chối khi hết. CHECK stock>=0 là lớp DB cuối; không thay affected count.
 * Conservation accepted quantity + final stock = initial stock khi không có restock.
 * SOLUTION-END
 */
