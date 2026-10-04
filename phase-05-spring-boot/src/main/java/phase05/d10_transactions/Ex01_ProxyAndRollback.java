package phase05.d10_transactions;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.*;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * <h2>Chủ đề 10 — Transaction và proxy boundary</h2>
 * <p>Nguồn: {@code 05-spring-boot.md}, §10, Q1–Q5, B10. Tiên quyết: JDBC, Spring proxy.</p>
 * <p>B10: mở {@code OrderService.placeOrder} trong IntelliJ, hoàn thiện B10; chạy
 * {@code Ex01_ProxyAndRollbackTest}. Debugger dừng ở failure seam sau stock update;
 * kiểm tra dữ liệu bằng connection khác, không dùng transaction tự rollback của test.</p>
 * Q1 [DỰ ĐOÁN] Vì sao self-invocation bỏ qua transaction proxy?
 * <p>Dự đoán SELF_HAS_TRANSACTION; đọc Spring transaction proxy mode, breakpoint vào selfInvocation.
 * Hoàn thành: external call có transaction, this-call không có transaction mới.</p>
 * Q2 [CODE] Mặc định checked exception có rollback không?
 * <p>Mở checkedDefault/checkedRollback; đọc rollback rules, không bật global ALL_EXCEPTIONS.
 * Hoàn thành: checked mặc định commit event, rollbackFor không để lại event mới.</p>
 * Q3 [TỰ TRẢ LỜI] {@code readOnly=true} có bảo đảm DB từ chối write không?
 * <p>Mở ANSWER Q3; đọc TransactionDefinition.isReadOnly và PostgreSQL read-only transaction.
 * Hoàn thành: phân biệt hint với enforceReadOnly/DB permission; không khái quát mọi manager.</p>
 * Q4 [THÍ NGHIỆM] {@code UnexpectedRollbackException} có thể xuất hiện khi nào?
 * <p>Chạy smoke Q4 rồi test rollback-only, debugger tại OuterService và inner REQUIRED bean.
 * Hoàn thành: ghi vì sao catch exception không xóa rollback-only vào ANSWER Q4.</p>
 * Q5 [CODE] Vì sao không gọi dịch vụ payment từ xa bên trong DB transaction dài?
 * <p>Mở placeOrder và failure seam; đọc Spring transaction resource scope.
 * Hoàn thành: use case chỉ ghi DB nguyên tử, RuntimeException/Error rollback; không gọi HTTP thật.</p>
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
public final class Ex01_ProxyAndRollback {
    private Ex01_ProxyAndRollback() {}
    public static final Boolean SELF_HAS_TRANSACTION = null;
    public record LineRequest(long productId, int quantity) {}
    public record CreateOrderRequest(List<LineRequest> items) {}
    public record OrderItemResponse(long productId, int quantity, BigDecimal unitPrice) {}
    public record OrderResponse(long id, String status, String currency, BigDecimal total,
            List<OrderItemResponse> items, Instant createdAt) {}
    public static class CheckedFailure extends Exception {}
    public static class StockConflict extends RuntimeException {}

    @Configuration(proxyBeanMethods = false) @EnableTransactionManagement
    public static class Transactions {}

    public static AnnotationConfigApplicationContext context(DataSource dataSource, Runnable afterStockUpdate) {
        throw new UnsupportedOperationException("TODO B10");
    }
    public static class OrderService {
        private final JdbcTemplate jdbc;
        private final Runnable afterStockUpdate;
        public OrderService(JdbcTemplate jdbc, Runnable afterStockUpdate) {
            this.jdbc = jdbc; this.afterStockUpdate = afterStockUpdate;
        }
        @Transactional public boolean transactionActive() {
            throw new UnsupportedOperationException("TODO B10");
        }
        public boolean selfInvocation() {
            throw new UnsupportedOperationException("TODO B10");
        }
        @Transactional public OrderResponse placeOrder(String subject, CreateOrderRequest request) {
            throw new UnsupportedOperationException("TODO B10");
        }
        @Transactional public void checkedDefault() throws CheckedFailure {
            throw new UnsupportedOperationException("TODO B10");
        }
        @Transactional(rollbackFor = CheckedFailure.class) public void checkedRollback() throws CheckedFailure {
            throw new UnsupportedOperationException("TODO B10");
        }
        @Transactional public void runtimeFailure() {
            throw new UnsupportedOperationException("TODO B10");
        }
        @Transactional public void errorFailure() {
            throw new UnsupportedOperationException("TODO B10");
        }
    }
    public static class OuterService {
        private final JdbcTemplate jdbc;
        private final OrderService inner;
        public OuterService(JdbcTemplate jdbc, OrderService inner) { this.jdbc = jdbc; this.inner = inner; }
        @Transactional public int successfulWrite() {
            throw new UnsupportedOperationException("TODO B10");
        }
        @Transactional public void catchRequiredFailure() {
            throw new UnsupportedOperationException("TODO B10");
        }
    }
}
