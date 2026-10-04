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
 * SOLUTION-BEGIN
 * Proxy chỉ thấy call đến proxy; this-call trong target không đi vòng qua proxy.
 * Đặt boundary ở bean khác hoặc method external; không dùng new Worker để chứng minh advice.
 * SOLUTION-END
 * ANSWER Q2:
 * SOLUTION-BEGIN
 * Transaction/method security áp dụng policy chung ở bean boundary, tránh lặp boilerplate.
 * Business flow và side effect chính nên gọi tường minh; B12 dùng transaction advice sẵn có.
 * SOLUTION-END
 * ANSWER Q3:
 * SOLUTION-BEGIN
 * Aspect tự tạo order/payment, thay đổi input hoặc phụ thuộc thứ tự ngầm làm use case khó đọc.
 * Khó tìm nơi side effect phát sinh là tín hiệu đưa business logic về application service.
 * SOLUTION-END
 * ANSWER Q4:
 * SOLUTION-BEGIN
 * Body có password/token/PII; log tồn lâu và có người đọc khác với quyền request.
 * safeLog chỉ xuất operation trong allowlist cố định, giới hạn kích thước và bỏ toàn bộ secret field.
 * SOLUTION-END
 * ANSWER Q5:
 * SOLUTION-BEGIN
 * Integration test lấy proxy từ Spring context chứng minh advice; PostgreSQL chứng minh commit/rollback.
 * Security cần filter/method boundary và authentication thật phù hợp; mock target unit test không chứng minh proxy.
 * SOLUTION-END
 */
public final class Ex01_ProxyBoundary {
    private Ex01_ProxyBoundary() {}
    public static final Boolean SELF_HAS_TRANSACTION = false; // SOLUTION-VALUE
    public record RequestBody(String operation, String password, String token, String email) {}
    @Configuration(proxyBeanMethods = false) @EnableTransactionManagement
    public static class Transactions {}
    public static AnnotationConfigApplicationContext context(DataSource dataSource) {
        // SOLUTION-BEGIN throw B12
        var context = new AnnotationConfigApplicationContext();
        context.register(Transactions.class);
        context.registerBean(DataSourceTransactionManager.class, () -> new DataSourceTransactionManager(dataSource));
        context.registerBean(Worker.class, () -> new Worker(new JdbcTemplate(dataSource)));
        context.registerBean(Caller.class, () -> new Caller(context.getBean(Worker.class)));
        try { context.refresh(); return context; }
        catch (RuntimeException | Error failure) { context.close(); throw failure; }
        // SOLUTION-END
    }
    public static String safeLog(RequestBody request) {
        // SOLUTION-BEGIN throw B12
        String operation = request == null ? null : request.operation();
        return "operation=" + ("login".equals(operation) || "place-order".equals(operation) ? operation : "unknown");
        // SOLUTION-END
    }
    public static class Worker {
        private final JdbcTemplate jdbc;
        public Worker(JdbcTemplate jdbc) { this.jdbc = jdbc; }
        @Transactional public boolean transactionActive() {
            // SOLUTION-BEGIN throw B12
            return TransactionSynchronizationManager.isActualTransactionActive();
            // SOLUTION-END
        }
        public boolean selfActive() {
            // SOLUTION-BEGIN throw B12
            return transactionActive();
            // SOLUTION-END
        }
        @Transactional public void success() {
            // SOLUTION-BEGIN throw B12
            jdbc.update("insert into audit_events values ('success')");
            // SOLUTION-END
        }
        @Transactional public void failure() {
            // SOLUTION-BEGIN throw B12
            jdbc.update("insert into audit_events values ('before-failure')");
            throw new IllegalStateException("injected failure");
            // SOLUTION-END
        }
        public void selfFailure() {
            // SOLUTION-BEGIN throw B12
            failure();
            // SOLUTION-END
        }
    }
    public static class Caller {
        private final Worker worker;
        public Caller(Worker worker) { this.worker = worker; }
        public boolean externalActive() {
            // SOLUTION-BEGIN throw B12
            return worker.transactionActive();
            // SOLUTION-END
        }
        public void externalSuccess() {
            // SOLUTION-BEGIN throw B12
            worker.success();
            // SOLUTION-END
        }
        public void externalFailure() {
            // SOLUTION-BEGIN throw B12
            worker.failure();
            // SOLUTION-END
        }
    }
}
