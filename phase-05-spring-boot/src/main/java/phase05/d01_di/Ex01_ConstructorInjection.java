package phase05.d01_di;

/**
 * <h2>Chủ đề 1 — Dependency Injection</h2>
 * <p>Nguồn: phase-05-spring-boot, chủ đề Dependency Injection.</p>
 * <p>Tiên quyết: class, interface và constructor Java.</p>
 * <p>Trong IntelliJ, mở {@code OrderService} rồi đặt breakpoint tại {@code repository.save}.
 * Thử nghiệm bằng fake repository trong test; không cần khởi tạo Spring.</p>
 * <p>Hoàn thành khi service nhận repository qua constructor và test chạy độc lập.</p>
 * <ul>
 *   <li>Q1 [DỰ ĐOÁN] IoC khác DI ở điểm nào?</li>
 *   <li>Q2 [CODE] Vì sao constructor injection thường rõ hơn field injection?</li>
 *   <li>Q3 [TỰ TRẢ LỜI] Một class thuần Java có cần annotation Spring để test không?</li>
 *   <li>Q4 [THÍ NGHIỆM] Khi nào wiring thủ công dễ hiểu hơn component scan?</li>
 *   <li>Q5 [CODE] DI có tự làm thiết kế tốt nếu dependency graph vẫn rối không?</li>
 * </ul>
 * ANSWER Q1:
 * SOLUTION-BEGIN
 * IoC đảo quyền tạo/chọn dependency; DI là một cách hiện thực IoC.
 * SOLUTION-END
 * ANSWER Q2:
 * SOLUTION-BEGIN
 * Constructor làm dependency bắt buộc hiện rõ và cho phép object bất biến.
 * SOLUTION-END
 * ANSWER Q3:
 * SOLUTION-BEGIN
 * Không; test truyền fake trực tiếp, không cần container hay annotation.
 * SOLUTION-END
 * ANSWER Q4:
 * SOLUTION-BEGIN
 * Wiring thủ công hữu ích ở graph nhỏ, test hoặc khi muốn thấy toàn bộ dependency.
 * SOLUTION-END
 * ANSWER Q5:
 * SOLUTION-BEGIN
 * Không; DI không sửa trách nhiệm chồng chéo hay graph phụ thuộc quá rối.
 * SOLUTION-END
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
        // SOLUTION-BEGIN throw B01
        return new OrderService(repository);
        // SOLUTION-END
    }

    public static OrderService visibleComposition(OrderRepository repository) {
        return new OrderService(repository);
    }
}
