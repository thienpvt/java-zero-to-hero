package phase05.d11_concurrency;

import jakarta.persistence.*;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.hibernate.Session;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.*;

/**
 * <h2>Chủ đề 11 — Tồn kho đồng thời và locking</h2>
 * <p>Nguồn: {@code 05-spring-boot.md}, §11, Q1–Q5, B11. Tiên quyết: transaction, isolation, JPA.</p>
 * <p>B11: mở {@code PurchaseService.buy} trong IntelliJ; hoàn thiện B11, chạy
 * {@code Ex01_StockRaceTest}. Hai worker dùng connection/transaction riêng, barrier hữu hạn ngay
 * trước cạnh tranh. Debugger xem stock, affected rows, version; không Thread.sleep/synchronized.</p>
 * Q1 [DỰ ĐOÁN] Vì sao check rồi update trong Java có race condition?
 * <p>Dự đoán NAIVE_SUCCESSFUL_ORDERS; đọc PostgreSQL READ COMMITTED, chạy unsafeBuy trong DB bỏ đi.
 * Hoàn thành: hai order có thể bán một stock dù stock cuối không âm.</p>
 * Q2 [CODE] {@code @Version} phát hiện điều gì?
 * <p>Mở optimisticBuy, đọc Hibernate optimistic locking; breakpoint sau read trước flush.
 * Hoàn thành: stale version gây conflict, transaction thua không để lại order.</p>
 * Q3 [TỰ TRẢ LỜI] Conditional update giải quyết phần nào của race?
 * <p>Mở ANSWER Q3 và PostgreSQL UPDATE docs. Hoàn thành: giải thích predicate/affected rows
 * tại DB và vì sao ghi order phải chung transaction; không test từ khóa.</p>
 * Q4 [THÍ NGHIỆM] Vì sao {@code synchronized} không thể thay row lock/version trong ứng dụng nhiều instance?
 * <p>Chạy smoke Q4; debugger xem backend PID hai worker ở B11, đọc monitor/JVM scope.
 * Hoàn thành: ghi phạm vi lock Java vào ANSWER Q4; không dựng distributed-lock framework.</p>
 * Q5 [CODE] Khi conflict, API có thể trả status nào và vì sao?
 * <p>Mở conflictStatus/buy, đọc HTTP 409; giữ invalid quantity là lỗi input riêng.
 * Hoàn thành: một winner, stock conservation và status conflict 409; HTTP capstone chưa thuộc B11.</p>
 * ANSWER Q1:
 * SOLUTION-BEGIN
 * Hai transaction cùng đọc stock=1 rồi cùng ghi stock=0 và order; mất một decrement.
 * Check Java không khóa DB; test naive cố ý chứng minh oversell, không dùng đường này để sửa.
 * SOLUTION-END
 * ANSWER Q2:
 * SOLUTION-BEGIN
 * Hibernate thêm version cũ vào UPDATE predicate. Writer thứ hai stale version không update được,
 * OptimisticLockException làm transaction rollback, gồm order insert trước flush.
 * SOLUTION-END
 * ANSWER Q3:
 * SOLUTION-BEGIN
 * UPDATE stock=stock-qty WHERE stock>=qty kiểm tra/giảm nguyên tử tại row DB.
 * affected rows=0 là conflict, không tạo order; affected rows=1 và order phải cùng transaction.
 * SOLUTION-END
 * ANSWER Q4:
 * SOLUTION-BEGIN
 * Monitor chỉ bảo vệ object trong một JVM, không chặn instance khác hoặc writer SQL trực tiếp.
 * B11 không dùng Java lock làm boundary; backend PID độc lập vẫn giữ conservation nhờ DB.
 * SOLUTION-END
 * ANSWER Q5:
 * SOLUTION-BEGIN
 * 409 biểu thị xung đột stock/version với trạng thái hiện tại, client có thể đọc lại hoặc retry hữu hạn.
 * Quantity ngoài 1–1000 là invalid input, không tự trở thành stock conflict; không retry vô hạn.
 * SOLUTION-END
 */
public final class Ex01_StockRace {
    private Ex01_StockRace() {}
    public static final Integer NAIVE_SUCCESSFUL_ORDERS = 2; // SOLUTION-VALUE
    // Provided topic-local optimistic mapping; version column belongs only to this lab schema.
    @Entity(name = "StockProduct") @Table(name = "products")
    public static class Product {
        @Id public Long id;
        @Column(nullable = false) public int stock;
        @Version @Column(nullable = false) public long version;
        public Product() {}
    }
    @Configuration(proxyBeanMethods = false) @EnableTransactionManagement
    public static class Transactions {}
    public static AnnotationConfigApplicationContext context(DataSource dataSource, Runnable beforeWrite) {
        // SOLUTION-BEGIN throw B11
        var context = new AnnotationConfigApplicationContext();
        context.register(Transactions.class);
        context.registerBean(DataSourceTransactionManager.class, () -> new DataSourceTransactionManager(dataSource));
        context.registerBean(PurchaseService.class, () -> new PurchaseService(new JdbcTemplate(dataSource), beforeWrite));
        try { context.refresh(); return context; }
        catch (RuntimeException | Error failure) { context.close(); throw failure; }
        // SOLUTION-END
    }
    public static boolean unsafeBuy(Connection connection, int quantity, Runnable afterRead) throws SQLException {
        // SOLUTION-BEGIN throw B11
        if (quantity < 1 || quantity > 1000) throw new IllegalArgumentException("quantity");
        int stock;
        try (var statement = connection.createStatement()) {
            statement.execute("set local lock_timeout='5s'");
            statement.execute("set local statement_timeout='10s'");
            try (var rows = statement.executeQuery("select stock from products where id=1")) {
                rows.next(); stock = rows.getInt(1);
            }
        }
        afterRead.run();
        if (stock < quantity) return false;
        // ponytail: intentionally unsafe comparison; never use stale absolute UPDATE as purchase fix.
        try (var update = connection.prepareStatement("update products set stock=? where id=1");
             var order = connection.prepareStatement("insert into orders(quantity) values (?)")) {
            update.setInt(1, stock - quantity); update.executeUpdate();
            order.setInt(1, quantity); order.executeUpdate();
        }
        return true;
        // SOLUTION-END
    }
    public static boolean optimisticBuy(Session session, int quantity, Runnable afterRead) {
        // SOLUTION-BEGIN throw B11
        if (quantity < 1 || quantity > 1000) throw new IllegalArgumentException("quantity");
        session.createNativeMutationQuery("set local lock_timeout='5s'").executeUpdate();
        session.createNativeMutationQuery("set local statement_timeout='10s'").executeUpdate();
        var product = session.find(Product.class, 1L);
        afterRead.run();
        if (product.stock < quantity) return false;
        product.stock -= quantity;
        session.createNativeMutationQuery("insert into orders(quantity) values (:quantity)")
                .setParameter("quantity", quantity).executeUpdate();
        session.flush();
        return true;
        // SOLUTION-END
    }
    public static int conflictStatus() {
        // SOLUTION-BEGIN throw B11
        return 409;
        // SOLUTION-END
    }
    public static class PurchaseService {
        private final JdbcTemplate jdbc;
        private final Runnable beforeWrite;
        public PurchaseService(JdbcTemplate jdbc, Runnable beforeWrite) { this.jdbc = jdbc; this.beforeWrite = beforeWrite; }
        @Transactional(timeout = 10) public boolean buy(int quantity) {
            // SOLUTION-BEGIN throw B11
            if (quantity < 1 || quantity > 1000) throw new IllegalArgumentException("quantity");
            jdbc.execute("set local lock_timeout='5s'");
            jdbc.execute("set local statement_timeout='10s'");
            beforeWrite.run();
            if (jdbc.update("update products set stock=stock-? where id=1 and stock>=?", quantity, quantity) == 0)
                return false;
            jdbc.update("insert into orders(quantity) values (?)", quantity);
            return true;
            // SOLUTION-END
        }
    }
}
