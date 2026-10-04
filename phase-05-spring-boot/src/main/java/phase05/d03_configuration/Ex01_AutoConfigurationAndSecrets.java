package phase05.d03_configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * <h2>Chủ đề 3 — Auto-configuration, profile và secrets</h2>
 * <p>Nguồn: phase-05-spring-boot, chủ đề Configuration and Auto-configuration.</p>
 * <p>Tiên quyết: bean condition và property. Chạy test với context runner để kiểm tra
 * bean mặc định và điều kiện back-off; chỉ truyền map giá trị giả trong test.</p>
 * <p>Profile chọn cấu hình môi trường, không phải kho secret. Signing key production
 * phải được cung cấp rõ ràng; thiếu thì fail-fast, không đọc secret của máy cá nhân.</p>
 * <p>Hoàn thành khi auto-config có bằng chứng điều kiện, test-key an toàn và thiếu key bị từ chối.</p>
 * <ul>
 *   <li>Q1 [DỰ ĐOÁN] Điều kiện nào thường khiến auto-configuration tạo hoặc bỏ qua bean?</li>
 *   <li>Q2 [CODE] Khi cấu hình không như mong đợi, tìm bằng chứng ở đâu trước khi thêm bean mới?</li>
 *   <li>Q3 [TỰ TRẢ LỜI] Profile khác secret management thế nào?</li>
 *   <li>Q4 [THÍ NGHIỆM] Vì sao giá trị mặc định an toàn khác với hard-code credential?</li>
 *   <li>Q5 [CODE] Nên phản ứng thế nào khi thiếu signing key ở môi trường production?</li>
 * </ul>
 * ANSWER Q1:
 * SOLUTION-BEGIN
 * Bean conditions theo classpath/property và {@code @ConditionalOnMissingBean} quyết định tạo hoặc back off.
 * SOLUTION-END
 * ANSWER Q2:
 * SOLUTION-BEGIN
 * Xem condition evaluation report, property source và log trước khi thêm bean.
 * SOLUTION-END
 * ANSWER Q3:
 * SOLUTION-BEGIN
 * Profile chọn cấu hình; secret cần nguồn credential chuyên biệt và quyền truy cập riêng.
 * SOLUTION-END
 * ANSWER Q4:
 * SOLUTION-BEGIN
 * Local default không phải credential production; credential hard-code khó xoay vòng.
 * SOLUTION-END
 * ANSWER Q5:
 * SOLUTION-BEGIN
 * Fail fast khi thiếu production signing key; không dùng key giả hoặc key máy developer.
 * SOLUTION-END
 */
public final class Ex01_AutoConfigurationAndSecrets {
    private Ex01_AutoConfigurationAndSecrets() {}

    public record Greeting(String message) {}

    @AutoConfiguration
    public static class GreetingAutoConfiguration {
        @Bean
        @ConditionalOnMissingBean(Greeting.class)
        Greeting greeting() {
            return new Greeting("auto-configured");
        }
    }

    @Configuration(proxyBeanMethods = false)
    public static class UserGreetingConfiguration {
        @Bean Greeting greeting() { return new Greeting("local"); }
    }

    public static String signingKey(Map<String, String> properties) {
        String value = properties.get("app.signing-key");
        if (value == null || value.isBlank()) throw new IllegalStateException("Missing app.signing-key");
        return value;
    }

    public static String localDatabaseUrl() {
        return "jdbc:postgresql://localhost/lab";
    }

    /** B03: local/test values remain explicit; required signing key never has a credential default. */
    public static String productionSigningKey(Map<String, String> properties) {
        // SOLUTION-BEGIN throw B03
        return signingKey(properties);
        // SOLUTION-END
    }

    public static String selectedProfile(Map<String, String> properties) {
        return properties.getOrDefault("spring.profiles.active", "local");
    }
}
