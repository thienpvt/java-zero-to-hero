package phase04.d11_entity_lifecycle;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Vòng đời JPA — B11: tạo order, flush, commit, detach và merge.
 * <p>Nguồn: 04-database-persistence.md, mục 11. Cần trước: transaction JDBC, persistence context.
 * Bắt đầu: chạy Ex01_EntityLifecycleTest, đặt breakpoint tại persist/flush/commit/merge.
 * Tra cứu: Jakarta Persistence 3.1 EntityManager; Hibernate ORM 6.6 User Guide, flushing.
 * Hoàn thành: đối chiếu state với hàng đọc qua connection độc lập, xóa hàng thí nghiệm.
 * Q1 [TỰ TRẢ LỜI] {@code flush} khác commit thế nào?
 * Bắt đầu: viết ANSWER Q1, đối chiếu B11; xong khi phân biệt đồng bộ SQL và visibility/độ bền.
 * Q2 [CODE] Dirty checking xảy ra khi nào?
 * Bắt đầu: cài cancel; debug q02; xong khi managed đổi status được ghi mà không gọi merge.
 * Q3 [CODE] Vì sao giá trị trả về từ {@code merge} cần được dùng đúng?
 * Bắt đầu: cài mergeDetached và observe; debug q03; xong khi kiểm tra cả hai instance.
 * Q4 [TỰ TRẢ LỜI] Entity detached thay đổi có tự persist không?
 * Bắt đầu: viết ANSWER Q4, chạy thí nghiệm state; xong khi kiểm tra DB sau detach.
 * Q5 [TỰ TRẢ LỜI] Generated ID làm equals/hashCode khó ở điểm nào?
 * Bắt đầu: viết ANSWER Q5 với lựa chọn cho HashSet; xong khi nêu rủi ro hash đổi sau persist.
 */
public class Ex01_EntityLifecycle {
    public record LifecycleObservation(boolean managedBeforeFlush, boolean rowVisibleBeforeCommit,
                                       boolean mergeReturnedManaged) {}

    static LifecycleObservation observe(EntityManagerFactory emf, long id) {
        throw new UnsupportedOperationException("TODO B11");
    }

    static void cancel(EntityManager em, long id) {
        throw new UnsupportedOperationException("TODO B11");
    }

    static Order mergeDetached(EntityManager em, Order detached) {
        throw new UnsupportedOperationException("TODO B11");
    }

    // Provided mapping fixture; target is lifecycle behavior, not annotations/equality framework.
    @Entity(name = "LifecycleOrder") @Table(name = "orders") @Access(AccessType.FIELD)
    public static class Order {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") Long id;
        @Column(name = "customer_id", nullable = false) long customerId;
        @Column(name = "status", nullable = false, columnDefinition = "text") String status = "NEW";
        @Column(name = "created_at", nullable = false) Instant createdAt;
        protected Order() {}
        Order(long customerId, Instant createdAt) { this.customerId = customerId; this.createdAt = createdAt; }
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
/* ANSWER Q5:
 *
 */
