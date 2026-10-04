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
        throw new UnsupportedOperationException("TODO B8");
    }

    static boolean reserve(Connection c, long productId, int quantity) throws SQLException {
        throw new UnsupportedOperationException("TODO Q5");
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
/* ANSWER Q4:
 *
 */
/* OBSERVATION B8 / ANSWER Q5:
 *
 */
