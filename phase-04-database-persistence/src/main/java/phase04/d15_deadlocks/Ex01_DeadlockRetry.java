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
        // SOLUTION-BEGIN throw B15
        if (ds == null || work == null || maxAttempts < 1 || maxAttempts > 10)
            throw new IllegalArgumentException("DataSource/work bắt buộc; maxAttempts 1–10");
        // ponytail: lab ceiling 10; production contention needs measured backoff and request budget.
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            if (Thread.currentThread().isInterrupted())
                throw new SQLException("Transaction retry interrupted", "HY008");
            boolean retryable = false;
            try (var c = ds.getConnection()) {
                c.setAutoCommit(false);
                try {
                    T value = work.run(c);
                    c.commit();
                    return value;
                } catch (SQLException | RuntimeException | Error failure) {
                    if (failure instanceof SQLException sql)
                        retryable = "40001".equals(sql.getSQLState()) || "40P01".equals(sql.getSQLState());
                    try { c.rollback(); } catch (SQLException rollback) { failure.addSuppressed(rollback); }
                    throw failure;
                }
            } catch (SQLException failure) {
                String state = failure.getSQLState();
                System.out.println("retry attempt=" + attempt + " SQLSTATE=" + state);
                // Never replay after successful commit when close fails, or on acquisition/setup failure.
                if (Thread.currentThread().isInterrupted() || attempt == maxAttempts || !retryable) throw failure;
            }
        }
        throw new AssertionError("Unreachable bounded retry");
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Deadlock là vòng phụ thuộc lock: A giữ 1 đợi 2, B giữ 2 đợi 1. PostgreSQL hủy victim với
 * 40P01. Lock wait timeout không đòi vòng, chỉ vượt budget chờ lock, thường 55P03;
 * statement timeout/cancel là 57014. Query timeout 10s lớn hơn deadlock_timeout mặc định 1s.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * 40001 là serialization failure, có thể xuất hiện tại statement hoặc commit. PostgreSQL SSI
 * hủy một TX write skew dù ghi hai hàng riêng. Phải đọc lại và chạy toàn transaction mới;
 * 40P01 cũng retryable. Không retry mọi SQLException hoặc commit network failure 08xxx.
 * SOLUTION-END
 */
/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Retry có thể thanh toán hai lần vì rollback DB không hoàn tác payment API đã thành công.
 * SqlWork lab chỉ DB. Ngoài lab cần idempotency/boundary/outbox phù hợp; outbox không biến
 * remote API thành một phần của DB rollback, và lỗi commit không rõ phải đối soát.
 * SOLUTION-END
 */
/* OBSERVATION B15 / ANSWER Q2,Q4:
 * SOLUTION-BEGIN
 * Hai TX update hai product theo thứ tự đối nghịch; barrier chỉ sau hai first locks riêng.
 * Một 40P01, victim rollback rồi connection mới chạy lại toàn work. Khi dùng thứ tự 1,2
 * thống nhất và gate trước lock, không có vòng. Serializable hai snapshot SUM=2 rồi ghi
 * hai hàng riêng sinh 40001; fresh retry đọc tổng mới và không phá invariant tổng>=1.
 * Giới hạn attempts ngăn storm/livelock và latency vô hạn; lab ceiling 10, không infinite spin.
 * SOLUTION-END
 */
