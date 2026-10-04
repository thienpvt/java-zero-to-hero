package phase05.d08_jpa_queries;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * <h2>Chủ đề 8 — Spring Data JPA và hành vi query</h2>
 * <p>Nguồn: {@code 05-spring-boot.md}, §8, Q1–Q5, B08. Tiên quyết: JPA, SQL, PostgreSQL.</p>
 * <p>B08: mở {@code OrderQueries}, hoàn thiện vùng B08; chạy {@code Ex01_QueryBehaviorTest}.
 * Fixture/mapping có sẵn, không có OSIV; từng lần đọc đóng session trước khi trả DTO.
 * Debugger xem SQL trong StatementInspector, không đếm repository call.</p>
 * Q1 [DỰ ĐOÁN] {@code save} khác commit như thế nào?
 * <p>Mở test Q1 trong IntelliJ, dự đoán số row connection khác thấy sau persist/flush;
 * đọc EntityManager.flush Javadoc. Hoàn thành: dự đoán và bằng chứng trước/sau commit khớp.</p>
 * Q2 [CODE] Vì sao test chỉ đếm repository call chưa đủ để phát hiện N+1?
 * <p>Mở {@code naiveOrderPage}, đọc Hibernate fetching guide; breakpoint khi truy cập items.
 * Hoàn thành: inspector đo N+1 thật, còn projection B08 có số SELECT hữu hạn.</p>
 * Q3 [TỰ TRẢ LỜI] Collection fetch join ảnh hưởng pagination ra sao?
 * <p>Mở ANSWER Q3, đọc Hibernate pagination/fetch join warning; xem LIMIT tại DB.
 * Hoàn thành: giải thích row nhân lên và paging trong memory, không viết test từ khóa.</p>
 * Q4 [THÍ NGHIỆM] Vì sao map entity sang DTO sau khi persistence context đóng cần query/fetch plan rõ?
 * <p>Chạy smoke Q4, debugger duyệt DTO sau session.close; đọc LazyInitializationException.
 * Hoàn thành: ghi quan sát vào ANSWER Q4; không dùng thời gian làm assertion.</p>
 * Q5 [CODE] Khi nào projection hiệu quả hơn load entity graph đầy đủ?
 * <p>Mở {@code orderPage}, đọc Hibernate native projection; chỉ tải cột DTO cần.
 * Hoàn thành: owner predicate trước LIMIT, thứ tự created_at/id, size 1–100, DTO tách session.</p>
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
public final class Ex01_QueryBehavior {
    private Ex01_QueryBehavior() {}
    public static final Integer VISIBLE_BEFORE_COMMIT = null;

    // Provided topic-local mapping and DTO shape; no entity/component scans across sibling labs.
    @Entity(name = "QueryOrder") @Table(name = "orders")
    public static class OrderEntity {
        @Id public Long id;
        @Column(name = "customer_id", nullable = false) public long customerId;
        @Column(nullable = false, length = 20) public String status = "NEW";
        @Column(nullable = false, length = 3) public String currency = "USD";
        @Column(nullable = false, precision = 19, scale = 2) public BigDecimal total = BigDecimal.ONE;
        @Column(name = "created_at", nullable = false) public Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        @OneToMany(mappedBy = "order", fetch = FetchType.LAZY) public List<ItemEntity> items = new ArrayList<>();
        public OrderEntity() {}
        public OrderEntity(long id, long customerId) { this.id = id; this.customerId = customerId; }
    }
    @Entity(name = "QueryItem") @Table(name = "order_items")
    public static class ItemEntity {
        @Id public Long id;
        @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id", nullable = false) public OrderEntity order;
        @Column(name = "product_id", nullable = false) public long productId;
        @Column(nullable = false) public int quantity;
        @Column(name = "unit_price", nullable = false, precision = 19, scale = 2) public BigDecimal unitPrice;
        public ItemEntity() {}
    }
    public record OrderItemResponse(long productId, int quantity, BigDecimal unitPrice) {}
    public record OrderResponse(long id, String status, String currency, BigDecimal total,
            List<OrderItemResponse> items, Instant createdAt) {}
    public record PageResponse<T>(List<T> items, int page, int size, long totalItems, int totalPages) {}

    public static class OrderQueries {
        private final SessionFactory sessions;
        public OrderQueries(SessionFactory sessions) { this.sessions = sessions; }
        public List<OrderResponse> naiveOrderPage(String subject) {
            throw new UnsupportedOperationException("TODO B08");
        }
        public PageResponse<OrderResponse> orderPage(String subject, int page, int size) {
            throw new UnsupportedOperationException("TODO B08");
        }
    }

    @RestController
    public static class OrderController {
        private final OrderQueries queries;
        public OrderController(OrderQueries queries) { this.queries = queries; }
        @GetMapping("/api/orders")
        public PageResponse<OrderResponse> page(@RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "20") int size) {
            throw new UnsupportedOperationException("TODO B08");
        }
    }
}
