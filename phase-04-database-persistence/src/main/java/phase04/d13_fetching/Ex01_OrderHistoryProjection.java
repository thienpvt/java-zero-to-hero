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
        // SOLUTION-BEGIN throw B13
        if (em == null || customerId <= 0 || page < 0 || size < 1 || size > 100)
            throw new IllegalArgumentException("EntityManager/owner/page/size không hợp lệ");
        long offset = (long) page * size;
        if (offset > Integer.MAX_VALUE) throw new IllegalArgumentException("Page offset quá lớn");
        var parents = em.createQuery("""
                select o.id as id, o.createdAt as createdAt, c.name as customerName
                from HistoryOrder o join o.customer c
                where c.id = :customerId order by o.createdAt, o.id
                """, Tuple.class).setParameter("customerId", customerId)
                .setFirstResult((int) offset).setMaxResults(size).getResultList();
        if (parents.isEmpty()) return List.of();
        var itemsByOrder = new LinkedHashMap<Long, List<ItemSummary>>();
        for (var parent : parents) itemsByOrder.put(parent.get("id", Long.class), new ArrayList<>());
        var items = em.createQuery("""
                select i.order.id as orderId, p.name as productName, i.quantity as quantity, i.unitPrice as unitPrice
                from HistoryOrderItem i join i.product p
                where i.order.id in :ids order by i.order.id, i.id
                """, Tuple.class).setParameter("ids", itemsByOrder.keySet()).getResultList();
        for (var item : items) {
            itemsByOrder.get(item.get("orderId", Long.class)).add(new ItemSummary(item.get("productName", String.class),
                    item.get("quantity", Integer.class), item.get("unitPrice", BigDecimal.class)));
        }
        var result = new ArrayList<OrderSummary>();
        for (var parent : parents) {
            long id = parent.get("id", Long.class);
            result.add(new OrderSummary(id, parent.get("createdAt", Instant.class),
                    parent.get("customerName", String.class), itemsByOrder.get(id)));
        }
        return List.copyOf(result);
        // SOLUTION-END
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
 * SOLUTION-BEGIN
 * Một SELECT lấy orders, rồi truy cập lazy customer/items/product trong vòng lặp phát SELECT phụ cho
 * từng association chưa tải. Page dùng projection bounded thay chuỗi tải entity từng hàng.
 * SOLUTION-END
 */
/* ANSWER Q2:
 * SOLUTION-BEGIN
 * EAGER buộc tải dữ liệu không cần, có thể dùng secondary SELECT chứ không bảo đảm một JOIN duy nhất;
 * tốn bộ nhớ/băng thông và áp dụng cho mọi use case. Chọn fetch plan/projection theo truy vấn cụ thể.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Nhiều collection tạo tích số hàng, duplicate parent, và có thể MultipleBagFetchException.
 * LIMIT trên collection join cắt item hoặc Hibernate phân trang trong bộ nhớ; không fetch collection
 * cùng phân trang. Lab giới hạn parent ở DB trước, sau đó lấy items của đúng page đó.
 * SOLUTION-END
 */
/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Màn hình read-only chỉ cần vài cột và không sửa entity: projection tránh tải toàn graph/snapshot
 * dirty checking. DTO giữ giá snapshot order_items.unit_price, không thay bằng giá product hiện tại.
 * SOLUTION-END
 */
/* OBSERVATION Q5 / ANSWER Q5:
 * SOLUTION-BEGIN
 * PostgreSQL 18.0; fixtures có 3 rồi 12 orders của Mai, mỗi order 2 items, và 1 order của An.
 * Giữ page size=12 để phủ cả 3/12 orders; reset StatementInspector sau seed; EntityManager mới mỗi lần.
 * Lưu SQL và số SELECT từ q01: kiểm chứng đủ DTO/owner/items, SELECT <=3 và tăng <=1.
 * Không suy luận từ latency, mock repository calls hoặc 0 query với kết quả rỗng.
 * SOLUTION-END
 */
