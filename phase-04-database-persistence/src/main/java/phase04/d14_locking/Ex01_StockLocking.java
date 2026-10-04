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
        throw new UnsupportedOperationException("TODO Q2");
    }

    static boolean reserveOptimistic(Connection c, long productId, int quantity, int expectedVersion)
            throws SQLException {
        throw new UnsupportedOperationException("TODO B14");
    }

    static boolean reservePessimistic(Connection c, long productId, int quantity) throws SQLException {
        throw new UnsupportedOperationException("TODO Q5");
    }
}

/* ANSWER Q1:
 *
 */
/* ANSWER Q3:
 *
 */
/* ANSWER Q4:
 *
 */
/* OBSERVATION B14 / ANSWER Q2,Q5:
 *
 */
