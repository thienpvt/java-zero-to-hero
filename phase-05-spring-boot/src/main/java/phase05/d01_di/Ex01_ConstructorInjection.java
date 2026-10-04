package phase05.d01_di;

/**
 * <h2>Chủ đề 1 — Dependency Injection</h2>
 * <p>Nguồn: {@code 05-spring-boot.md}, §1 Spring Core, IoC và Dependency Injection, Q1–Q5 và B01.</p>
 * <p>Tiên quyết: class, interface và constructor Java.</p>
 * <p>Bắt đầu trong IntelliJ: mở {@code service}, hoàn thiện B01 rồi chạy test với fake repository.</p>
 * <p>Nghiên cứu: đặt breakpoint tại {@code repository.save}; đối chiếu constructor injection
 * trong Spring Framework Reference, Core Technologies.</p>
 * <p>Hoàn thành khi B01 dùng repository được truyền vào, test không cần Spring và viết đủ
 * ANSWER Q1–Q5. Câu hỏi viết được tự đối chiếu, không chấm tự động.</p>
 * Q1 [DỰ ĐOÁN] IoC khác DI ở điểm nào?
 * Q2 [CODE] Vì sao constructor injection thường rõ hơn field injection?
 * Q3 [TỰ TRẢ LỜI] Một class thuần Java có cần annotation Spring để test không?
 * Q4 [THÍ NGHIỆM] Khi nào wiring thủ công dễ hiểu hơn component scan?
 * Q5 [CODE] DI có tự làm thiết kế tốt nếu dependency graph vẫn rối không?
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
public final class Ex01_ConstructorInjection {
    private Ex01_ConstructorInjection() {}

    @FunctionalInterface
    public interface OrderRepository {
        void save(String orderId);
    }

    public static final class OrderService {
        private final OrderRepository repository;

        public OrderService(OrderRepository repository) {
            this.repository = java.util.Objects.requireNonNull(repository);
        }

        public void place(String orderId) {
            repository.save(java.util.Objects.requireNonNull(orderId));
        }
    }

    /** B01: pure service constructor-injects repository; fake test runs without Spring. */
    public static OrderService service(OrderRepository repository) {
        throw new UnsupportedOperationException("TODO B01");
    }
}
