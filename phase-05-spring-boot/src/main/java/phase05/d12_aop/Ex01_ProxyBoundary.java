package phase05.d12_aop;

import javax.sql.DataSource;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.*;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * <h2>Chủ đề 12 — AOP và cross-cutting concern</h2>
 * <p>Nguồn: {@code 05-spring-boot.md}, §12, Q1–Q5, B12. Tiên quyết: Spring bean, proxy, transaction.</p>
 * <p>B12: mở {@code Worker} trong IntelliJ, hoàn thiện B12; chạy {@code Ex01_ProxyBoundaryTest}.
 * Debugger xem AopUtils và transactionActive. Caller là bean khác, không có aspect mới hoặc scan rộng.</p>
 * Q1 [DỰ ĐOÁN] Vì sao proxy không tự intercept lời gọi nội bộ cùng object?
 * <p>Dự đoán SELF_HAS_TRANSACTION; đọc Spring AOP proxying, breakpoint this-call.
 * Hoàn thành: externalActive true còn selfActive false qua proxy thật.</p>
 * Q2 [CODE] Khi nào AOP phù hợp hơn lời gọi method tường minh?
 * <p>Mở Caller.externalFailure và transaction advice có sẵn; đọc TransactionInterceptor.
 * Hoàn thành: cross-cutting transaction rollback thực tế, business call vẫn tường minh.</p>
 * Q3 [TỰ TRẢ LỜI] Dấu hiệu nào cho thấy aspect đang che giấu business flow?
 * <p>Mở ANSWER Q3; debugger truy vết Caller/Worker. Đọc Spring AOP advice ordering.
 * Hoàn thành: nêu dấu hiệu side effect/order khó tìm, không dùng test no-op để chấm lý thuyết.</p>
 * Q4 [THÍ NGHIỆM] Vì sao log toàn bộ request body nguy hiểm?
 * <p>Chạy smoke Q4 với sentinel, xem output; đọc logging data minimization.
 * Hoàn thành: ghi quan sát vào ANSWER Q4; không log body rồi redact bằng regex tổng quát.</p>
 * Q5 [CODE] Nên kiểm chứng transaction/security aspect ở test nào?
 * <p>Mở test Q5/B12; kiểm tra bean từ context và dữ liệu commit qua connection khác.
 * Hoàn thành: real transaction advice và sentinel redaction; security/filter thật thuộc d13/d14.</p>
 * ANSWER Q1:
 *
 * ANSWER Q2:
 *
 * ANSWER Q3:
 *
 * ANSWER Q4:
 *
 * ANSWER Q5:
 *
 */
public final class Ex01_ProxyBoundary {
    private Ex01_ProxyBoundary() {}
    public static final Boolean SELF_HAS_TRANSACTION = null;
    public record RequestBody(String operation, String password, String token, String email) {}
    @Configuration(proxyBeanMethods = false) @EnableTransactionManagement
    public static class Transactions {}
    public static AnnotationConfigApplicationContext context(DataSource dataSource) {
        throw new UnsupportedOperationException("TODO B12");
    }
    public static String safeLog(RequestBody request) {
        throw new UnsupportedOperationException("TODO B12");
    }
    public static class Worker {
        private final JdbcTemplate jdbc;
        public Worker(JdbcTemplate jdbc) { this.jdbc = jdbc; }
        @Transactional public boolean transactionActive() {
            throw new UnsupportedOperationException("TODO B12");
        }
        public boolean selfActive() {
            throw new UnsupportedOperationException("TODO B12");
        }
        @Transactional public void success() {
            throw new UnsupportedOperationException("TODO B12");
        }
        @Transactional public void failure() {
            throw new UnsupportedOperationException("TODO B12");
        }
        public void selfFailure() {
            throw new UnsupportedOperationException("TODO B12");
        }
    }
    public static class Caller {
        private final Worker worker;
        public Caller(Worker worker) { this.worker = worker; }
        public boolean externalActive() {
            throw new UnsupportedOperationException("TODO B12");
        }
        public void externalSuccess() {
            throw new UnsupportedOperationException("TODO B12");
        }
        public void externalFailure() {
            throw new UnsupportedOperationException("TODO B12");
        }
    }
}
