package phase05.d03_configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Properties;

/**
 * <h2>Chủ đề 3 — Auto-configuration, profile và secrets</h2>
 * <p>Nguồn: {@code 05-spring-boot.md}, §3 Auto-configuration, profiles và cấu hình an toàn,
 * Q1–Q5 và B03.</p>
 * <p>Tiên quyết: bean condition, property và profile.</p>
 * <p>Bắt đầu trong IntelliJ: mở {@code databaseUrl} và {@code productionSigningKey}, hoàn thiện B03
 * rồi chạy test với Properties/map giá trị giả; không đọc secret của máy cá nhân.</p>
 * <p>Nghiên cứu: dùng debugger/context runner với cấu hình được cung cấp để đọc condition
 * evaluation report và quan sát back-off; đọc Spring Boot Reference, Auto-configuration.</p>
 * <p>Hoàn thành khi B03 chọn URL khác nhau theo profile local/test, nhận key được cấp rõ ràng,
 * cấu hình thiếu fail-fast không lộ secret và viết đủ ANSWER Q1–Q5. Câu hỏi viết không chấm tự động.</p>
 * Q1 [DỰ ĐOÁN] Điều kiện nào thường khiến auto-configuration tạo hoặc bỏ qua bean?
 * Q2 [CODE] Khi cấu hình không như mong đợi, tìm bằng chứng ở đâu trước khi thêm bean mới?
 * Q3 [TỰ TRẢ LỜI] Profile khác secret management thế nào?
 * Q4 [THÍ NGHIỆM] Vì sao giá trị mặc định an toàn khác với hard-code credential?
 * Q5 [CODE] Nên phản ứng thế nào khi thiếu signing key ở môi trường production?
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

    /** B03: chọn URL database theo profile local/test; không đọc credential của máy. */
    public static String databaseUrl(String profile, Properties properties) {
        throw new UnsupportedOperationException("TODO B03");
    }

    /** B03: local/test values remain explicit; required signing key never has a credential default. */
    public static String productionSigningKey(Map<String, String> properties) {
        throw new UnsupportedOperationException("TODO B03");
    }

}
