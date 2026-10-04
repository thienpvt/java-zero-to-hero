package phase04.d08_isolation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

/**
 * Isolation — B8: hai connection đọc trước khi ghi hai hàng khác nhau.
 * <p>Nguồn: 04-database-persistence.md, mục 8. Cần trước: JDBC, transaction và MVCC.
 * Bắt đầu: hoàn thành run; chạy Ex01_IsolationAnomalyTest trên PostgreSQL 18.
 * Debug: ghi snapshot, commit/SQLSTATE và đọc stock qua connection thứ ba.
 * Tài liệu: https://www.postgresql.org/docs/18/transaction-iso.html
 * Hoàn thành: tái hiện write skew, Serializable hủy một transaction, invariant được bảo vệ.
 * Q1 [TỰ TRẢ LỜI] Dirty read khác non-repeatable read thế nào?
 * Bắt đầu: viết ANSWER Q1, phân biệt dữ liệu chưa commit với hai lần đọc dữ liệu đã commit.
 * Hoàn thành khi chỉ rõ transaction nào commit giữa các lần đọc.
 * Q2 [TỰ TRẢ LỜI] `READ COMMITTED` cho snapshot ở phạm vi nào trong PostgreSQL?
 * Bắt đầu: viết ANSWER Q2 và chạy thí nghiệm hai lần SELECT với writer commit ở giữa.
 * Hoàn thành khi phân biệt snapshot statement với snapshot transaction.
 * Q3 [TỰ TRẢ LỜI] Vì sao PostgreSQL `REPEATABLE READ` vẫn có thể có serialization anomaly?
 * Bắt đầu: viết ANSWER Q3, xét invariant phụ thuộc hai hàng được ghi riêng.
 * Hoàn thành khi giải thích write skew dù đọc ổn định và không có phantom.
 * Q4 [TỰ TRẢ LỜI] Vì sao test race dựa trên `sleep` không đáng tin?
 * Bắt đầu: viết ANSWER Q4, thay delay bằng gate có timeout.
 * Hoàn thành khi gate xác nhận cả hai đã đọc, không chờ peer đang cần lock của mình.
 * Q5 [CODE] Test cần assert invariant nào khi hai request mua cùng sản phẩm?
 * Bắt đầu: hoàn thành reserve và chạy q05 với hai transaction riêng.
 * Hoàn thành khi lượng được chấp nhận cộng stock cuối bằng stock đầu, không oversell.
 */
public class Ex01_IsolationAnomaly {
    public enum IsolationLevel {
        READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
        READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
        REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
        SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);
        final int jdbc;
        IsolationLevel(int jdbc) { this.jdbc = jdbc; }
    }
    public record Observation(List<String> events, boolean invariantHeld) {}

    static Observation run(DataSource ds, IsolationLevel level) throws SQLException {
        // SOLUTION-BEGIN throw B8
        if (ds == null || level == null) throw new IllegalArgumentException("DataSource và level bắt buộc");
        var gate = new java.util.concurrent.CyclicBarrier(2);
        var pool = java.util.concurrent.Executors.newFixedThreadPool(2);
        var futures = new java.util.ArrayList<java.util.concurrent.Future<List<String>>>();
        var events = new java.util.ArrayList<String>();
        try {
            for (int id = 1; id <= 2; id++) {
                final int product = id;
                futures.add(pool.submit(() -> {
                    var seen = new java.util.ArrayList<String>();
                    try (var c = ds.getConnection()) {
                        c.setTransactionIsolation(level.jdbc);
                        c.setAutoCommit(false);
                        try {
                            int total;
                            try (var s = c.createStatement()) {
                                s.setQueryTimeout(10);
                                s.execute("SET LOCAL lock_timeout='5s'");
                                try (var r = s.executeQuery("SELECT sum(stock) FROM products WHERE id IN (1,2)")) {
                                    r.next();
                                    total = r.getInt(1);
                                }
                            }
                            seen.add("tx" + product + " read=" + total);
                            gate.await(5, java.util.concurrent.TimeUnit.SECONDS);
                            if (total > 1) {
                                try (var s = c.prepareStatement("UPDATE products SET stock=0 WHERE id=?")) {
                                    s.setQueryTimeout(10);
                                    s.setInt(1, product);
                                    if (s.executeUpdate() != 1) throw new SQLException("Fixture thiếu product", "02000");
                                }
                            }
                            c.commit();
                            seen.add("tx" + product + " commit");
                        } catch (SQLException failure) {
                            try { c.rollback(); } catch (SQLException rollback) { failure.addSuppressed(rollback); }
                            if (!"40001".equals(failure.getSQLState())) throw failure;
                            seen.add("tx" + product + " SQLSTATE=" + failure.getSQLState());
                        } catch (Exception | Error failure) {
                            try { c.rollback(); } catch (SQLException rollback) { failure.addSuppressed(rollback); }
                            if (failure instanceof InterruptedException) Thread.currentThread().interrupt();
                            throw failure;
                        }
                    }
                    return seen;
                }));
            }
            for (var future : futures) events.addAll(future.get(15, java.util.concurrent.TimeUnit.SECONDS));
            try (var c = ds.getConnection(); var s = c.createStatement()) {
                s.setQueryTimeout(10);
                try (var r = s.executeQuery("SELECT sum(stock) FROM products WHERE id IN (1,2)")) {
                    r.next();
                    return new Observation(List.copyOf(events), r.getInt(1) >= 1);
                }
            }
        } catch (InterruptedException failure) {
            Thread.currentThread().interrupt();
            throw new SQLException("Isolation lab interrupted", "HY008", failure);
        } catch (java.util.concurrent.ExecutionException failure) {
            if (failure.getCause() instanceof SQLException sql) throw sql;
            throw new SQLException("Isolation worker failed", failure.getCause());
        } catch (java.util.concurrent.TimeoutException failure) {
            throw new SQLException("Isolation future deadline", "57014", failure);
        } finally {
            futures.forEach(f -> f.cancel(true));
            pool.shutdownNow();
            try {
                if (!pool.awaitTermination(12, java.util.concurrent.TimeUnit.SECONDS))
                    throw new SQLException("Isolation executor deadline", "57014");
            } catch (InterruptedException failure) {
                Thread.currentThread().interrupt();
            }
        }
        // SOLUTION-END
    }

    static boolean reserve(Connection c, long productId, int quantity) throws SQLException {
        // SOLUTION-BEGIN throw Q5
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
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Dirty read thấy thay đổi chưa commit của transaction khác; non-repeatable read thấy hai giá trị
 * khác nhau của cùng hàng vì writer commit giữa hai lần đọc. PostgreSQL không cho dirty read:
 * READ UNCOMMITTED thực tế có hành vi READ COMMITTED.
 * SOLUTION-END
 */
/* ANSWER Q2:
 * SOLUTION-BEGIN
 * READ COMMITTED lấy snapshot mới cho mỗi statement, không giữ một snapshot cho toàn transaction.
 * SELECT sau writer commit có thể thấy dữ liệu mới. REPEATABLE READ giữ snapshot ổn định từ
 * statement dữ liệu đầu tiên; không chỉ từ thời điểm gọi setAutoCommit(false).
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * PostgreSQL REPEATABLE READ ngăn non-repeatable read và phantom nhưng vẫn cho write skew.
 * Hai transaction đọc cùng điều kiện nhiều hàng rồi ghi hai hàng khác nhau có thể cùng commit,
 * phá invariant tổng thể. Serializable phát hiện phụ thuộc nguy hiểm và báo 40001; retry toàn TX.
 * SOLUTION-END
 */
/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Sleep chỉ trì hoãn, không chứng minh worker đã tới mốc nào; scheduler và DB latency thay đổi.
 * Barrier sau cả hai SELECT, trước các UPDATE hàng riêng, xác định lịch và có timeout.
 * Luôn cancel futures, rollback/close và kết thúc executor khi một worker lỗi.
 * SOLUTION-END
 */
/* OBSERVATION B8 / ANSWER Q5:
 * SOLUTION-BEGIN
 * Dataset: products 1 và 2 stock=1; invariant ít nhất một đơn vị còn lại giữa hai hàng.
 * Cả hai đọc SUM=2 rồi mỗi TX đặt một hàng riêng về 0: RU/RC/RR cùng commit, tổng=0.
 * Serializable báo 40001 cho một TX; tổng cuối=1. Đây là invariant nhiều hàng, không oversell.
 * Với cùng sản phẩm, conditional update phải kiểm tra affected count; accepted quantity +
 * stock cuối = stock đầu khi không restock, accepted <= stock đầu và stock cuối >= 0.
 * Kết quả lab không phải chứng minh mọi lịch production; DB atomicity/SSI bảo vệ các lịch đó.
 * SOLUTION-END
 */
