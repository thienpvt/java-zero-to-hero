package phase04.d15_deadlocks;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * Deadlock/retry — B15: whole transaction, fresh connection, tối đa 10 attempts trong lab.
 * <p>Nguồn: 04-database-persistence.md, mục 15. Cần trước: JDBC rollback và PostgreSQL row lock.
 * Bắt đầu: hoàn thành retryTransaction, chạy Ex01_DeadlockRetryTest trên PostgreSQL 18.
 * Debug: log attempt/SQLSTATE, lock 1 rồi 2 nhất quán; future/query/gate luôn có deadline.
 * Tài liệu: https://www.postgresql.org/docs/18/mvcc-serialization-failure-handling.html
 * Hoàn thành: 40P01/40001 thật, bounded fresh retries, lỗi khác không retry, rollback nguyên tử.
 * SqlWork chỉ tác dụng DB; không gọi payment/email. Caller không tự commit trong work.
 * Q1 [TỰ TRẢ LỜI] Deadlock khác lock wait timeout như thế nào?
 * Bắt đầu: viết ANSWER Q1 với vòng chờ hai row và chờ đơn không có vòng.
 * Hoàn thành khi phân biệt SQLSTATE và deadline bảo vệ test khỏi treo.
 * Q2 [CODE] Vì sao chỉ retry câu lệnh bị lỗi có thể sai?
 * Bắt đầu: cài retryTransaction, chạy q02 kiểm tra đọc lại state và rollback attempt cũ.
 * Hoàn thành khi mỗi attempt mở connection mới, run work một lần, commit một lần.
 * Q3 [TỰ TRẢ LỜI] SQLSTATE `40001` báo loại lỗi nào trong PostgreSQL?
 * Bắt đầu: viết ANSWER Q3, chạy B15 Serializable write skew trên hai hàng riêng.
 * Hoàn thành khi phân biệt serialization failure với 40P01 và lỗi mạng commit không rõ.
 * Q4 [CODE] Retry không giới hạn có thể gây vấn đề gì?
 * Bắt đầu: giới hạn maxAttempts 1–10, chạy q04 cho cả retryable và nonretryable SQLSTATE.
 * Hoàn thành khi hết budget ném lỗi cuối, Runtime/Error không retry, interrupt giữ flag.
 * Q5 [TỰ TRẢ LỜI] Vì sao gọi payment API trong transaction khiến retry nguy hiểm?
 * Bắt đầu: viết ANSWER Q5 với payment thành công trước DB rollback.
 * Hoàn thành khi chỉ rõ rollback DB không hoàn tác external side effect.
 */
public class Ex01_DeadlockRetry {
    @FunctionalInterface
    interface SqlWork<T> { T run(Connection connection) throws SQLException; }

    static <T> T retryTransaction(DataSource ds, int maxAttempts, SqlWork<T> work) throws SQLException {
        throw new UnsupportedOperationException("TODO B15");
    }
}

/* ANSWER Q1:
 *
 */
/* ANSWER Q3:
 *
 */
/* ANSWER Q5:
 *
 */
/* OBSERVATION B15 / ANSWER Q2,Q4:
 *
 */
