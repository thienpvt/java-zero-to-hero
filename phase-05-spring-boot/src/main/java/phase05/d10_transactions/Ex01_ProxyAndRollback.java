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
 * SOLUTION-BEGIN
 * Proxy intercept external call; this.transactionActive gọi target trực tiếp nên annotation không chạy.
 * Gọi từ bean khác hoặc tách use case boundary; không dựa self-invocation cho propagation mới.
 * SOLUTION-END
 * ANSWER Q2:
 * SOLUTION-BEGIN
 * Mặc định RuntimeException/Error rollback, checked exception commit nếu không có rollback rule khác.
 * rollbackFor=CheckedFailure.class cấu hình rõ trường hợp checked cần rollback; test dùng DB độc lập.
 * SOLUTION-END
 * ANSWER Q3:
 * SOLUTION-BEGIN
 * readOnly là hint cho transaction manager/provider, không bảo đảm mọi DB/manager từ chối write.
 * PostgreSQL transaction thực sự READ ONLY có thể từ chối; cần kiểm chứng manager/config cụ thể.
 * SOLUTION-END
 * ANSWER Q4:
 * SOLUTION-BEGIN
 * Inner REQUIRED qua proxy chia sẻ transaction và runtime failure đánh dấu rollback-only.
 * Outer bắt exception vẫn không thể commit; UnexpectedRollbackException báo rollback thay vì thành công giả.
 * SOLUTION-END
 * ANSWER Q5:
 * SOLUTION-BEGIN
 * HTTP payment không thuộc ACID của DB, giữ lock lâu và có thể thành công dù DB rollback.
 * B10 không làm payment; production cần boundary/idempotency và cơ chế phối hợp riêng khi có remote I/O.
 * SOLUTION-END
 */
public final class Ex01_ProxyAndRollback {
    private Ex01_ProxyAndRollback() {}
    public static final Boolean SELF_HAS_TRANSACTION = false; // SOLUTION-VALUE
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
        // SOLUTION-BEGIN throw B10
        var context = new AnnotationConfigApplicationContext();
        context.register(Transactions.class);
        context.registerBean(DataSourceTransactionManager.class, () -> new DataSourceTransactionManager(dataSource));
        context.registerBean(JdbcTemplate.class, () -> new JdbcTemplate(dataSource));
        context.registerBean(OrderService.class, () -> new OrderService(context.getBean(JdbcTemplate.class), afterStockUpdate));
        context.registerBean(OuterService.class, () -> new OuterService(context.getBean(JdbcTemplate.class), context.getBean(OrderService.class)));
        try { context.refresh(); return context; }
        catch (RuntimeException | Error failure) { context.close(); throw failure; }
        // SOLUTION-END
    }
    public static class OrderService {
        private final JdbcTemplate jdbc;
        private final Runnable afterStockUpdate;
        public OrderService(JdbcTemplate jdbc, Runnable afterStockUpdate) {
            this.jdbc = jdbc; this.afterStockUpdate = afterStockUpdate;
        }
        @Transactional public boolean transactionActive() {
            // SOLUTION-BEGIN throw B10
            return TransactionSynchronizationManager.isActualTransactionActive();
            // SOLUTION-END
        }
        public boolean selfInvocation() {
            // SOLUTION-BEGIN throw B10
            return transactionActive();
            // SOLUTION-END
        }
        @Transactional public OrderResponse placeOrder(String subject, CreateOrderRequest request) {
            // SOLUTION-BEGIN throw B10
            if (subject == null || subject.isBlank() || request == null || request.items() == null || request.items().isEmpty())
                throw new IllegalArgumentException("subject/items");
            var seen = new java.util.HashSet<Long>();
            for (var line : request.items())
                if (line == null || line.productId() < 1 || line.quantity() < 1 || line.quantity() > 1000 || !seen.add(line.productId()))
                    throw new IllegalArgumentException("line");
            long owner = jdbc.queryForObject("select id from customers where subject=?", Long.class, subject);
            var lines = request.items().stream().map(line -> new OrderItemResponse(line.productId(), line.quantity(),
                    jdbc.queryForObject("select price from products where id=?", BigDecimal.class, line.productId()))).toList();
            BigDecimal total = lines.stream().map(line -> line.unitPrice().multiply(BigDecimal.valueOf(line.quantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            long id = jdbc.queryForObject("insert into orders(customer_id,status,currency,total) values (?,'NEW','USD',?) returning id",
                    Long.class, owner, total);
            for (var line : lines) {
                jdbc.update("insert into order_items(order_id,product_id,quantity,unit_price) values (?,?,?,?)",
                        id, line.productId(), line.quantity(), line.unitPrice());
                if (jdbc.update("update products set stock=stock-? where id=? and stock>=?",
                        line.quantity(), line.productId(), line.quantity()) != 1) throw new StockConflict();
            }
            afterStockUpdate.run();
            Instant createdAt = jdbc.queryForObject("select created_at from orders where id=?",
                    (rs, row) -> rs.getTimestamp(1).toInstant(), id);
            return new OrderResponse(id, "NEW", "USD", total, lines, createdAt);
            // SOLUTION-END
        }
        @Transactional public void checkedDefault() throws CheckedFailure {
            // SOLUTION-BEGIN throw B10
            jdbc.update("insert into events values ('checked-default')");
            throw new CheckedFailure();
            // SOLUTION-END
        }
        @Transactional(rollbackFor = CheckedFailure.class) public void checkedRollback() throws CheckedFailure {
            // SOLUTION-BEGIN throw B10
            jdbc.update("insert into events values ('checked-explicit')");
            throw new CheckedFailure();
            // SOLUTION-END
        }
        @Transactional public void runtimeFailure() {
            // SOLUTION-BEGIN throw B10
            jdbc.update("insert into events values ('runtime')");
            throw new IllegalStateException("injected runtime");
            // SOLUTION-END
        }
        @Transactional public void errorFailure() {
            // SOLUTION-BEGIN throw B10
            jdbc.update("insert into events values ('error')");
            throw new AssertionError("injected error");
            // SOLUTION-END
        }
    }
    public static class OuterService {
        private final JdbcTemplate jdbc;
        private final OrderService inner;
        public OuterService(JdbcTemplate jdbc, OrderService inner) { this.jdbc = jdbc; this.inner = inner; }
        @Transactional public int successfulWrite() {
            // SOLUTION-BEGIN throw B10
            return jdbc.update("insert into events values ('control')");
            // SOLUTION-END
        }
        @Transactional public void catchRequiredFailure() {
            // SOLUTION-BEGIN throw B10
            jdbc.update("insert into events values ('outer')");
            try { inner.runtimeFailure(); }
            catch (IllegalStateException expected) { /* outer continues, shared transaction remains rollback-only */ }
            // SOLUTION-END
        }
    }
}
