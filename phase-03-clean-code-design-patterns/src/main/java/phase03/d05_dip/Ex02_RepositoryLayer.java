package phase03.d05_dip;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * DIP — Bài 2: repository nằm ở layer nào
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 5, câu 4, 6.
 * Cần làm trước: Ex01_GatewayDirection.
 * Cách làm: chạy test trong Ex02_RepositoryLayerTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q4 [DỰ ĐOÁN] Repository interface nên nằm ở layer nào trong Clean Architecture?
 *   Bắt đầu   : điền Q4_REPOSITORY_INTERFACE_LAYER bằng một giá trị của {@code Layer}.
 *   Kiểm chứng: chạy q04_prediction và q04_inMemorySatisfiesDomainContract.
 *   Hoàn thành khi: q04_prediction xanh và ANSWER Q4 nêu interface gần domain, adapter ở infrastructure.
 * <p>
 * Ví dụ Q6 [CODE] DIP giúp testing thế nào?
 *   Bắt đầu   : cài {@code InMemoryOrders.save} và {@code InMemoryOrders.find}.
 *   Kiểm chứng: chạy q06_saveThenFind.
 *   Hoàn thành khi: q06_saveThenFind xanh và ANSWER Q6 nêu fake in-memory không cần database.
 */
public class Ex02_RepositoryLayer {

    /** Layer có thể chứa repository interface. */
    public enum Layer {
        CONTROLLER,
        DOMAIN_OR_APPLICATION,
        INFRASTRUCTURE
    }

    public record OrderRecord(String id, long totalCents) {
    }

    /** Contract nghiệp vụ, không lộ database. */
    public interface OrderRepository {

        void save(OrderRecord order);

        Optional<OrderRecord> find(String id);
    }

    // Q4 — repository interface nên ở layer nào.
    static final Layer Q4_REPOSITORY_INTERFACE_LAYER = Layer.DOMAIN_OR_APPLICATION; // SOLUTION-VALUE

    /** Adapter in-memory cho test, thay database. */
    public static final class InMemoryOrders implements OrderRepository {

        private final Map<String, OrderRecord> rows = new HashMap<>();

        @Override
        public void save(OrderRecord order) {
            // SOLUTION-BEGIN throw Q6
            rows.put(order.id(), order);
            // SOLUTION-END
        }

        @Override
        public Optional<OrderRecord> find(String id) {
            // SOLUTION-BEGIN throw Q6
            return Optional.ofNullable(rows.get(id));
            // SOLUTION-END
        }
    }
}

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Interface repository thuộc domain hoặc application vì nó mô tả nhu cầu lưu trữ của nghiệp vụ.
 * Adapter triển khai interface đó nằm ở infrastructure, cùng chỗ với database thật.
 * Hướng phụ thuộc chạy từ infrastructure vào domain, không phải ngược lại.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Nghiệp vụ nhận repository từ ngoài nên test thay bằng bản in-memory.
 * Không cần database, không cần dữ liệu mẫu, không cần dọn bảng.
 * Lỗi lưu cũng dựng được bằng một repository luôn ném exception.
 * SOLUTION-END
 */
