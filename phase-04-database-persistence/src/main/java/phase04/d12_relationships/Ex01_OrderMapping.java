package phase04.d12_relationships;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Quan hệ JPA — B12: API thêm/xóa child và kiểm chứng FK/cascade trên PostgreSQL.
 * <p>Nguồn: 04-database-persistence.md, mục 12. Cần trước: FK, lifecycle B11, owning/inverse side.
 * Bắt đầu: cài Order.addItem/removeItem, chạy Ex01_OrderMappingTest; debug trước flush và đọc FK.
 * Tra cứu: Jakarta Persistence 3.1 OneToMany/ManyToOne, orphanRemoval; Hibernate ORM 6.6 associations.
 * Hoàn thành: hai phía nhất quán, orphan bị DELETE, xóa parent không xóa product/customer dùng chung.
 * Q1 [TỰ TRẢ LỜI] Owning side quyết định điều gì khi lưu quan hệ?
 * Bắt đầu: viết ANSWER Q1, kiểm tra FK B12; xong khi nêu bên quyết định SQL liên kết.
 * Q2 [THÍ NGHIỆM] Vì sao chỉ đổi inverse side có thể không ghi FK như mong đợi?
 * Bắt đầu: chạy q02, ghi OBSERVATION Q2; xong khi đối chiếu item.order và lỗi FK/NULL.
 * Q3 [TỰ TRẢ LỜI] {@code CascadeType.REMOVE} và {@code orphanRemoval} có thể gây mất dữ liệu nào?
 * Bắt đầu: viết ANSWER Q3; xong khi phân biệt child owned và product dùng chung.
 * Q4 [CODE] Khi nào child entity có lifecycle phụ thuộc parent?
 * Bắt đầu: cài removeItem; debug q04; xong khi child bị xóa, product còn và FK đã ghi đúng.
 * Q5 [TỰ TRẢ LỜI] Vì sao mapping hai chiều làm tăng độ phức tạp?
 * Bắt đầu: viết ANSWER Q5; xong khi nêu đồng bộ hai phía, mutation và vòng tham chiếu.
 */
public class Ex01_OrderMapping {
    // Provided mapping fixtures. Learner target: domain helper behavior, not annotation metadata.
    @Entity(name = "MappingCustomer") @Table(name = "customers") @Access(AccessType.FIELD)
    public static class Customer {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") Long id;
        @Column(name = "name", nullable = false, unique = true, columnDefinition = "text") String name;
        protected Customer() {}
    }
    @Entity(name = "MappingProduct") @Table(name = "products") @Access(AccessType.FIELD)
    public static class Product {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") Long id;
        @Column(name = "name", nullable = false, unique = true, columnDefinition = "text") String name;
        @Column(name = "price", nullable = false, precision = 19, scale = 2) BigDecimal price;
        @Column(name = "stock", nullable = false) int stock;
        protected Product() {}
        public Long getId() { return id; }
        public BigDecimal getPrice() { return price; }
    }
    @Entity(name = "MappingOrder") @Table(name = "orders") @Access(AccessType.FIELD)
    public static class Order {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") Long id;
        @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "customer_id", nullable = false) Customer customer;
        @Column(name = "status", nullable = false, columnDefinition = "text") String status = "NEW";
        @Column(name = "created_at", nullable = false) Instant createdAt;
        @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, orphanRemoval = true)
        List<OrderItem> items = new ArrayList<>();
        protected Order() {}
        Order(Customer customer, Instant createdAt) { this.customer = customer; this.createdAt = createdAt; }
        public List<OrderItem> items() { return List.copyOf(items); }

        public void addItem(OrderItem item) {
            throw new UnsupportedOperationException("TODO B12");
        }
        public void removeItem(OrderItem item) {
            throw new UnsupportedOperationException("TODO B12");
        }
    }
    @Entity(name = "MappingOrderItem") @Table(name = "order_items", uniqueConstraints = @UniqueConstraint(columnNames = {"order_id", "product_id"}))
    @Access(AccessType.FIELD)
    public static class OrderItem {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") Long id;
        @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "order_id", nullable = false) Order order;
        // Product is shared: deliberately no cascade REMOVE or orphanRemoval here.
        @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "product_id", nullable = false) Product product;
        @Column(name = "quantity", nullable = false) int quantity;
        @Column(name = "unit_price", nullable = false, precision = 19, scale = 2) BigDecimal unitPrice;
        protected OrderItem() {}
        OrderItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }
}

/* ANSWER Q1:
 *
 */
/* OBSERVATION Q2 / ANSWER Q2:
 *
 */
/* ANSWER Q3:
 *
 */
/* ANSWER Q4:
 *
 */
/* ANSWER Q5:
 *
 */
