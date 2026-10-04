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
 * SOLUTION-BEGIN
 * Liveness hỏi process còn sống/cần restart; readiness hỏi có nhận traffic không. ACCEPTING_TRAFFIC và REFUSING_TRAFFIC thay readiness, không tự đổi liveness.
 * SOLUTION-END
 * ANSWER Q2:
 * SOLUTION-BEGIN
 * Management có dữ liệu vận hành nhạy cảm. Default chỉ health; local-lab mới metrics ở loopback/test scope và cần SCOPE_metrics.read, không expose wildcard.
 * SOLUTION-END
 * ANSWER Q3:
 * SOLUTION-BEGIN
 * Dependency chập chờn không nhất thiết process hỏng. Gắn dependency vào liveness có thể gây restart storm; readiness có thể từ chối traffic độc lập.
 * SOLUTION-END
 * ANSWER Q4:
 * SOLUTION-BEGIN
 * User ID tạo cardinality không bị chặn, tốn bộ nhớ/storage và lộ PII. Dùng bounded pool tag; acquisition timer COUNT/TOTAL_TIME seconds, không usage hoặc HTTP latency.
 * SOLUTION-END
 * ANSWER Q5:
 * SOLUTION-BEGIN
 * Không log password, credential, Bearer token, signing key, secret, payload/PII nhạy cảm hoặc response body. Log event code/status an toàn, không echo exception/request values.
 * SOLUTION-END
 */
public final class Ex01_HealthAndLogging {
    private Ex01_HealthAndLogging() {}

    public static Map<String, Object> properties(boolean localLab) {
        // SOLUTION-BEGIN throw B16
        return Map.of("management.endpoints.web.exposure.include", localLab ? "health,metrics" : "health",
                "management.endpoint.health.show-details", "never",
                "management.endpoint.health.probes.enabled", true,
                "server.address", "127.0.0.1");
        // SOLUTION-END
    }

    public static SecurityFilterChain security(HttpSecurity http, JwtDecoder decoder) throws Exception {
        // SOLUTION-BEGIN throw B16
        var bearer401 = new BearerTokenAuthenticationEntryPoint();
        var bearer403 = new BearerTokenAccessDeniedHandler();
        org.springframework.security.web.AuthenticationEntryPoint unauthorized = (request, response, failure) -> {
            var safeFailure = failure;
            if (failure instanceof OAuth2AuthenticationException oauth) {
                var error = oauth.getError();
                OAuth2Error safeError = error instanceof BearerTokenError bearer
                        ? new BearerTokenError(error.getErrorCode(), bearer.getHttpStatus(), null, error.getUri(), bearer.getScope())
                        : new OAuth2Error(error.getErrorCode(), null, error.getUri());
                safeFailure = new OAuth2AuthenticationException(safeError);
            }
            bearer401.commence(request, response, safeFailure);
            // No OAuth2 failure means no Bearer credential, not an invalid token; lab has no metadata endpoint.
            if (!(failure instanceof OAuth2AuthenticationException)) response.setHeader("WWW-Authenticate", "Bearer");
            response.setContentType("application/problem+json");
            response.getWriter().write("{\"type\":\"urn:phase05:authentication\",\"title\":\"Unauthorized\",\"status\":401,\"detail\":\"Authentication is required.\"}");
            Logger.getLogger(Ex01_HealthAndLogging.class.getName()).warning("security.authentication_failed");
        };
        org.springframework.security.web.access.AccessDeniedHandler forbidden = (request, response, failure) -> {
            bearer403.handle(request, response, failure);
            response.setContentType("application/problem+json");
            response.getWriter().write("{\"type\":\"urn:phase05:forbidden\",\"title\":\"Forbidden\",\"status\":403,\"detail\":\"Permission is required.\"}");
            Logger.getLogger(Ex01_HealthAndLogging.class.getName()).warning("security.access_denied");
        };
        return http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/metrics", "/actuator/metrics/**").hasAuthority("SCOPE_metrics.read")
                        .requestMatchers(HttpMethod.GET, "/api/products").hasAuthority("SCOPE_products.read")
                        .requestMatchers("/actuator/**").hasAuthority("SCOPE_metrics.read")
                        .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ERROR).permitAll()
                        .anyRequest().denyAll())
                .exceptionHandling(errors -> errors.authenticationEntryPoint(unauthorized).accessDeniedHandler(forbidden))
                .oauth2ResourceServer(resource -> resource.jwt(jwt -> jwt.decoder(decoder))
                        .authenticationEntryPoint(unauthorized).accessDeniedHandler(forbidden)).build();
        // SOLUTION-END
    }

    @RestController
    public static class Products {
        @GetMapping("/api/products")
        public Map<String, String> products() { return Map.of("name", "test-product"); }
    }
}
