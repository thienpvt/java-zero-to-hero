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
        // SOLUTION-BEGIN throw B11
        if (emf == null || id <= 0) throw new IllegalArgumentException("Factory và order ID phải hợp lệ");
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                var original = em.find(Order.class, id);
                if (original == null) throw new IllegalArgumentException("Order không tồn tại");
                cancel(em, id);
                var inserted = new Order(original.customerId, Instant.now());
                if (em.contains(inserted)) throw new IllegalStateException("Transient đã managed");
                em.persist(inserted); // IDENTITY may INSERT here, before explicit flush.
                boolean managed = em.contains(inserted);
                em.flush();
                boolean visible;
                try (var independent = emf.createEntityManager()) {
                    visible = independent.find(Order.class, inserted.id) != null;
                }
                tx.commit();
                try (var independent = emf.createEntityManager()) {
                    if (independent.find(Order.class, inserted.id) == null)
                        throw new IllegalStateException("Commit chưa hiển thị hàng mới");
                    if (!"CANCELLED".equals(independent.find(Order.class, id).status))
                        throw new IllegalStateException("Dirty checking chưa ghi status");
                }
                em.detach(original);
                original.status = "NEW";
                try (var independent = emf.createEntityManager()) {
                    if (!"CANCELLED".equals(independent.find(Order.class, id).status))
                        throw new IllegalStateException("Detached thay đổi DB ngoài transaction");
                }
                tx.begin();
                var returned = mergeDetached(em, original);
                boolean merged = em.contains(returned) && returned != original && !em.contains(original);
                tx.commit();
                tx.begin();
                em.remove(inserted);
                if (em.contains(inserted)) throw new IllegalStateException("Removed vẫn managed");
                em.flush();
                tx.commit();
                try (var independent = emf.createEntityManager()) {
                    if (independent.find(Order.class, inserted.id) != null)
                        throw new IllegalStateException("Hàng thí nghiệm chưa xóa");
                }
                return new LifecycleObservation(managed, visible, merged);
            } finally {
                if (tx.isActive()) tx.rollback();
            }
        }
        // SOLUTION-END
    }

    static void cancel(EntityManager em, long id) {
        // SOLUTION-BEGIN throw B11
        if (em == null || id <= 0) throw new IllegalArgumentException("EntityManager và ID phải hợp lệ");
        var order = em.find(Order.class, id);
        if (order == null) throw new IllegalArgumentException("Order không tồn tại");
        order.status = "CANCELLED"; // dirty checking, not explicit UPDATE/merge
        // SOLUTION-END
    }

    static Order mergeDetached(EntityManager em, Order detached) {
        // SOLUTION-BEGIN throw B11
        if (em == null || detached == null || detached.id == null)
            throw new IllegalArgumentException("Cần order detached có ID");
        return em.merge(detached);
        // SOLUTION-END
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
 * SOLUTION-BEGIN
 * Flush đồng bộ state managed thành SQL trong transaction; constraint có thể lỗi ngay lúc đó.
 * Commit kết thúc transaction thành công. Flush không làm connection khác thấy hàng chưa commit.
 * IDENTITY có thể INSERT ngay tại persist để lấy ID, nên không suy luận insert chỉ xuất hiện tại flush.
 * SOLUTION-END
 */
/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Persistence context theo dõi entity managed; flush so sánh state hiện tại với snapshot và phát UPDATE.
 * Flush thường xảy ra trước commit hoặc query cần dữ liệu đồng bộ. Detached không còn được theo dõi.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Merge sao chép state vào instance managed rồi trả instance đó; đối tượng đầu vào vẫn detached.
 * Dùng instance trả về cho thay đổi tiếp theo; sửa đầu vào sau merge không tự cập nhật instance managed.
 * SOLUTION-END
 */
/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Không. Thay đổi detached nằm ngoài dirty checking; cần merge trong transaction và dùng kết quả.
 * Đọc qua connection mới để tránh nhầm state cache với dữ liệu đã commit.
 * SOLUTION-END
 */
/* ANSWER Q5:
 * SOLUTION-BEGIN
 * ID chưa có trước persist; hash dựa ID đổi khi DB cấp ID khiến HashSet không tìm được entry cũ.
 * Lab chọn reference identity, không coi hai instance khác nhau là cùng giá trị domain và không override
 * equals/hashCode. Nếu cần Set theo identity DB, chỉ thêm sau khi có ID; business key ổn định là lựa chọn
 * khác khi có invariant duy nhất/bất biến thật. Không dùng hai ID null để coi hai entity mới là bằng nhau.
 * SOLUTION-END
 */
