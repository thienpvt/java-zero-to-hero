package phase04.d13_fetching;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Projection — B13: lịch sử order gồm customer, items, product name, phân trang tại DB.
 * <p>Nguồn: 04-database-persistence.md, mục 13. Cần trước: JOIN, pagination B6, lifecycle B11.
 * Bắt đầu: cài page; chạy Ex01_OrderHistoryProjectionTest, debug query và StatementInspector sau seed.
 * Tra cứu: Hibernate ORM 6.6 HQL projections/pagination; Jakarta Persistence 3.1 Tuple.
 * Hoàn thành: page đúng owner/thứ tự/đủ items, DTO bất biến, số SQL không tăng theo từng order.
 * Q1 [CODE] N+1 thường hình thành qua chuỗi truy vấn nào?
 * Bắt đầu: cài page thay tải lazy trong vòng lặp; debug q01; xong khi có hàng đúng và SQL bounded.
 * Q2 [TỰ TRẢ LỜI] Vì sao đổi mọi quan hệ sang EAGER không phải cách sửa tổng quát?
 * Bắt đầu: viết ANSWER Q2; xong khi nêu overfetch và secondary SELECT còn có thể xảy ra.
 * Q3 [TỰ TRẢ LỜI] Fetch join nhiều collection có thể gây vấn đề gì?
 * Bắt đầu: viết ANSWER Q3; xong khi nêu row multiplication và pagination collection.
 * Q4 [TỰ TRẢ LỜI] Khi nào projection hiệu quả hơn tải entity đầy đủ?
 * Bắt đầu: viết ANSWER Q4; xong khi nêu read-only shape và dữ liệu cần cho màn hình.
 * Q5 [THÍ NGHIỆM] Bằng chứng nào chứng minh N+1 đã được loại bỏ?
 * Bắt đầu: chạy q01 với 3/12 orders và page size 12; ghi OBSERVATION Q5 cùng SQL.
 * Xong khi kết quả đầy đủ giống nhau, query count bị chặn và không đo seed như query người học.
 */
public class Ex01_OrderHistoryProjection {
    public record OrderSummary(long orderId, Instant createdAt, String customerName, List<ItemSummary> items) {
        public OrderSummary { items = List.copyOf(items); }
    }
    public record ItemSummary(String productName, int quantity, BigDecimal unitPrice) {}

    static List<OrderSummary> page(EntityManager em, long customerId, int page, int size) {
        throw new UnsupportedOperationException("TODO B13");
    }

    // Provided annotation fixtures, explicit domain classes only; learner target is read query.
    @Entity(name = "HistoryCustomer") @Table(name = "customers") @Access(AccessType.FIELD)
    public static class Customer {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") Long id;
        @Column(name = "name", nullable = false, unique = true, columnDefinition = "text") String name;
        protected Customer() {}
    }
    @Entity(name = "HistoryProduct") @Table(name = "products") @Access(AccessType.FIELD)
    public static class Product {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") Long id;
        @Column(name = "name", nullable = false, unique = true, columnDefinition = "text") String name;
        @Column(name = "price", nullable = false, precision = 19, scale = 2) BigDecimal price;
        @Column(name = "stock", nullable = false) int stock;
        protected Product() {}
    }
    @Entity(name = "HistoryOrder") @Table(name = "orders") @Access(AccessType.FIELD)
    public static class Order {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") Long id;
        @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "customer_id", nullable = false) Customer customer;
        @Column(name = "status", nullable = false, columnDefinition = "text") String status;
        @Column(name = "created_at", nullable = false) Instant createdAt;
        protected Order() {}
    }
    @Entity(name = "HistoryOrderItem") @Table(name = "order_items", uniqueConstraints = @UniqueConstraint(columnNames = {"order_id", "product_id"}))
    @Access(AccessType.FIELD)
    public static class OrderItem {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") Long id;
        @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "order_id", nullable = false) Order order;
        @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "product_id", nullable = false) Product product;
        @Column(name = "quantity", nullable = false) int quantity;
        @Column(name = "unit_price", nullable = false, precision = 19, scale = 2) BigDecimal unitPrice;
        protected OrderItem() {}
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
/* OBSERVATION Q5 / ANSWER Q5:
 *
 */
