package phase05.d02_beans_lifecycle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * <h2>Chủ đề 2 — Bean scope và state</h2>
 * <p>Nguồn: {@code 05-spring-boot.md}, §2 Beans, cấu hình và lifecycle, Q1–Q5 và B02.</p>
 * <p>Tiên quyết: bean, singleton và prototype.</p>
 * <p>Bắt đầu trong IntelliJ: mở {@code statelessService}, hoàn thiện B02 rồi chạy test
 * truyền nhiều identity vào cùng service.</p>
 * <p>Nghiên cứu: dùng debugger và {@code ScopeExamples} được cung cấp để quan sát identity
 * khi lấy bean; đọc Spring Framework Reference, Bean Scopes. Giải thích hai request ghi
 * đè {@code currentUser} nếu field thuộc singleton.</p>
 * <p>Hoàn thành khi B02 không giữ state request, method dùng đúng identity từng lời gọi
 * và viết đủ ANSWER Q1–Q5. Câu hỏi viết được tự đối chiếu, không chấm tự động.</p>
 * Q1 [DỰ ĐOÁN] Spring singleton scope bảo đảm điều gì và không bảo đảm điều gì?
 * Q2 [CODE] Vì sao field mutable trong controller/service có thể hỏng dưới tải đồng thời?
 * Q3 [TỰ TRẢ LỜI] Scope {@code prototype} có nghĩa là Spring quản lý toàn bộ lifecycle sau khi inject không?
 * Q4 [THÍ NGHIỆM] Khi nào chọn {@code @Bean} thay vì {@code @Component}?
 * Q5 [CODE] Có thể giải quyết mọi state bằng cách đổi bean sang request scope không?
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

    @FunctionalInterface
    public interface GreetingService {
        String greet(String user);
    }

    /** B02: identity is an argument, never mutable singleton request state. */
    public static GreetingService statelessService() {
        // SOLUTION-BEGIN throw B02
        return user -> user;
        // SOLUTION-END
    }
}
