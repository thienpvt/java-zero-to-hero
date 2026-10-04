package phase05.d16_actuator;

import java.util.Map;
import java.util.logging.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.*;

/**
 * <h2>Chủ đề 16 — Health và logging</h2>
 * <p>Nguồn: 05-spring-boot.md §16, Q1–Q5, B16.
 * Tiên quyết: Java 21, HTTP, Spring Security/JUnit, PostgreSQL thật.
 * IntelliJ: mở Ex01_HealthAndLoggingTest, chạy test rồi đặt breakpoint trong body B16.
 * Chỉ cấu hình explicit của topic; không scan phase05 hoặc dùng identity provider mạng.</p>
 * Q1 [DỰ ĐOÁN] Liveness khác readiness thế nào?
 * <p>Nguồn §16 Q1; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở properties, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_HealthAndLoggingTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q1; không suy luận từ mock decoder.</p>
 * Q2 [CODE] Vì sao không expose mọi actuator endpoint ra internet?
 * <p>Nguồn §16 Q2; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở security, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_HealthAndLoggingTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q2; không suy luận từ mock decoder.</p>
 * Q3 [TỰ TRẢ LỜI] Vì sao dependency failure không phải lúc nào cũng nên làm liveness fail?
 * <p>Nguồn §16 Q3; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở availability events, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_HealthAndLoggingTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q3; không suy luận từ mock decoder.</p>
 * Q4 [THÍ NGHIỆM] Label metric theo user ID gây vấn đề gì?
 * <p>Nguồn §16 Q4; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở actual Hikari acquisition, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_HealthAndLoggingTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q4; không suy luận từ mock decoder.</p>
 * Q5 [CODE] Dữ liệu nào không được ghi vào log?
 * <p>Nguồn §16 Q5; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở error handlers, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_HealthAndLoggingTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q5; không suy luận từ mock decoder.</p>
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
public final class Ex01_HealthAndLogging {
    private Ex01_HealthAndLogging() {}

    public static Map<String, Object> properties(boolean localLab) {
        throw new UnsupportedOperationException("TODO B16");
    }

    public static SecurityFilterChain security(HttpSecurity http, JwtDecoder decoder) throws Exception {
        throw new UnsupportedOperationException("TODO B16");
    }

    @RestController
    public static class Products {
        @GetMapping("/api/products")
        public Map<String, String> products() { return Map.of("name", "test-product"); }
    }
}
