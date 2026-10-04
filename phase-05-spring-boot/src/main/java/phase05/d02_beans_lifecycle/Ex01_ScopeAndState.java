package phase05.d02_beans_lifecycle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * <h2>Chủ đề 2 — Bean scope và state</h2>
 * <p>Nguồn: phase-05-spring-boot, chủ đề Bean Scope and Lifecycle.</p>
 * <p>Tiên quyết: bean, singleton và prototype. Mở {@code ScopeExamples}, đặt breakpoint
 * tại hai lần lấy bean trong test, rồi quan sát identity.</p>
 * <p>Thử truyền user vào method thay vì lưu vào field; không dùng dữ liệu người dùng
 * mutable trong singleton. Prototype được container tạo mới nhưng lifecycle sau inject
 * không được container tự quản lý đầy đủ.</p>
 * <p>Hoàn thành khi test chứng minh scope và method dùng đúng identity từng request.</p>
 * <ul>
 *   <li>Q1 [DỰ ĐOÁN] Spring singleton scope bảo đảm điều gì và không bảo đảm điều gì?</li>
 *   <li>Q2 [CODE] Vì sao field mutable trong controller/service có thể hỏng dưới tải đồng thời?</li>
 *   <li>Q3 [TỰ TRẢ LỜI] Scope {@code prototype} có nghĩa là Spring quản lý toàn bộ lifecycle sau khi inject không?</li>
 *   <li>Q4 [THÍ NGHIỆM] Khi nào chọn {@code @Bean} thay vì {@code @Component}?</li>
 *   <li>Q5 [CODE] Có thể giải quyết mọi state bằng cách đổi bean sang request scope không?</li>
 * </ul>
 * ANSWER Q1:
 * SOLUTION-BEGIN
 * Singleton là một instance mỗi bean definition trong context; không bảo đảm thread-safety hay toàn JVM.
 * SOLUTION-END
 * ANSWER Q2:
 * SOLUTION-BEGIN
 * Request đồng thời có thể ghi đè field dùng chung; truyền identity theo method input.
 * SOLUTION-END
 * ANSWER Q3:
 * SOLUTION-BEGIN
 * Không; Spring tạo prototype nhưng caller quản lý lifecycle sau injection.
 * SOLUTION-END
 * ANSWER Q4:
 * SOLUTION-BEGIN
 * {@code @Bean} cho wiring ngoài/thư viện; {@code @Component} cho class ứng dụng.
 * SOLUTION-END
 * ANSWER Q5:
 * SOLUTION-BEGIN
 * Không; scope không thay thế thiết kế state rõ ràng; singleton cần proxy khi giữ request scope.
 * SOLUTION-END
 */
public final class Ex01_ScopeAndState {
    private Ex01_ScopeAndState() {}

    public static final class SingletonExample {}
    public static final class PrototypeExample {}

    @Configuration(proxyBeanMethods = false)
    public static class ScopeExamples {
        @Bean
        @Scope("singleton")
        SingletonExample singletonExample() {
            return new SingletonExample();
        }

        @Bean
        @Scope("prototype")
        PrototypeExample prototypeExample() {
            return new PrototypeExample();
        }
    }

    public static final class GreetingService {
        public String greet(String user) {
            return user;
        }
    }

    /** B02: identity is an argument, never mutable singleton request state. */
    public static GreetingService statelessService() {
        // SOLUTION-BEGIN throw B02
        return new GreetingService();
        // SOLUTION-END
    }

    public static GreetingService requestScopedAlternative() {
        return new GreetingService();
    }
}
