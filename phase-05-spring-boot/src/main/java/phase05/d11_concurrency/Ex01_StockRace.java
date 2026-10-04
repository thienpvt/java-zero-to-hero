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
public final class Ex01_StockRace {
    private Ex01_StockRace() {}
    public static final Integer NAIVE_SUCCESSFUL_ORDERS = null;
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
        throw new UnsupportedOperationException("TODO B11");
    }
    public static boolean unsafeBuy(Connection connection, int quantity, Runnable afterRead) throws SQLException {
        throw new UnsupportedOperationException("TODO B11");
    }
    public static boolean optimisticBuy(Session session, int quantity, Runnable afterRead) {
        throw new UnsupportedOperationException("TODO B11");
    }
    public static int conflictStatus() {
        throw new UnsupportedOperationException("TODO B11");
    }
    public static class PurchaseService {
        private final JdbcTemplate jdbc;
        private final Runnable beforeWrite;
        public PurchaseService(JdbcTemplate jdbc, Runnable beforeWrite) { this.jdbc = jdbc; this.beforeWrite = beforeWrite; }
        @Transactional(timeout = 10) public boolean buy(int quantity) {
            throw new UnsupportedOperationException("TODO B11");
        }
    }
}
