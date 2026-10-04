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
            // SOLUTION-BEGIN throw B12
            if (item == null || item.product == null || item.product.getId() == null
                    || item.product.getId() <= 0 || item.quantity < 1 || item.quantity > 1000)
                throw new IllegalArgumentException("Cần product đã lưu và quantity 1..1000");
            if (item.order != null && org.hibernate.Hibernate.unproxy(item.order) != this)
                throw new IllegalArgumentException("Item đã thuộc order khác");
            if (items.contains(item)) return;
            if (items.stream().anyMatch(existing -> existing.product.getId().equals(item.product.getId())))
                throw new IllegalArgumentException("Product trùng trong order");
            item.unitPrice = item.product.getPrice(); // accessor initializes lazy proxy; server-owned snapshot
            item.order = this;
            items.add(item);
            // SOLUTION-END
        }
        public void removeItem(OrderItem item) {
            // SOLUTION-BEGIN throw B12
            if (item == null || org.hibernate.Hibernate.unproxy(item.order) != this || !items.remove(item))
                throw new IllegalArgumentException("Item không thuộc order");
            item.order = null;
            // SOLUTION-END
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
 * SOLUTION-BEGIN
 * Owning side OrderItem.order quyết định order_id ghi vào DB; mappedBy chỉ chỉ ra inverse collection.
 * Cả hai phía trong bộ nhớ vẫn cần đồng bộ để API và persistence context không mâu thuẫn.
 * SOLUTION-END
 */
/* OBSERVATION Q2 / ANSWER Q2:
 * SOLUTION-BEGIN
 * q02 chỉ thêm vào inverse list, item.order vẫn null; persist/flush bị từ chối bởi nullability/FK.
 * Không suy ra cascade PERSIST tự điền owning side: cascade chỉ lan thao tác lifecycle.
 * Thí nghiệm dùng PostgreSQL 18.0, bốn bảng, một order và một product; rollback sau lỗi.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Cascade REMOVE lan xóa parent sang entity được tham chiếu; nếu product dùng chung thì mất dữ liệu
 * của order khác. OrphanRemoval xóa child owned khi bị tách khỏi quan hệ, không chỉ đặt FK null.
 * Lab chọn cascade PERSIST + orphanRemoval cho items; không REMOVE product/customer. Xóa parent qua
 * ORM xóa child owned; DELETE bằng JDBC dùng ON DELETE CASCADE của DB, không callbacks/context ORM.
 * SOLUTION-END
 */
/* ANSWER Q4:
 * SOLUTION-BEGIN
 * OrderItem là dòng của đúng một order, không có ý nghĩa độc lập; orphanRemoval phù hợp.
 * Product có lifecycle riêng, nhiều order tham chiếu, nên không cascade REMOVE từ item sang product.
 * SOLUTION-END
 */
/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Hai tham chiếu phải cùng thay đổi; public mutable list dễ bỏ qua owning FK. Helper giữ invariant,
 * read API trả snapshot bất biến. Mapping hai chiều còn gây vòng serialization/toString và lazy loads.
 * SOLUTION-END
 */
