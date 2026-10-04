package phase05.d13_security;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/**
 * <h2>Chủ đề 13 — Spring Security</h2>
 * <p>Nguồn: 05-spring-boot.md §13, Q1–Q5, B13.
 * Tiên quyết: Java 21, HTTP, Spring Security/JUnit, PostgreSQL thật.
 * IntelliJ: mở Ex01_AuthenticationAuthorizationTest, chạy test rồi đặt breakpoint trong body B13.
 * Chỉ cấu hình explicit của topic; không scan phase05 hoặc dùng identity provider mạng.</p>
 * Q1 [DỰ ĐOÁN] Authentication khác authorization thế nào?
 * <p>Nguồn §13 Q1; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở security, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_AuthenticationAuthorizationTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q1; không suy luận từ mock decoder.</p>
 * Q2 [CODE] Vì sao role ở endpoint chưa đủ để bảo vệ order của khách hàng khác?
 * <p>Nguồn §13 Q2; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở Orders.read/changeQuantity/cancel, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_AuthenticationAuthorizationTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q2; không suy luận từ mock decoder.</p>
 * Q3 [TỰ TRẢ LỜI] Ai là nguồn sự thật của current user ID?
 * <p>Nguồn §13 Q3; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở principal JWT, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_AuthenticationAuthorizationTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q3; không suy luận từ mock decoder.</p>
 * Q4 [THÍ NGHIỆM] `401` và `403` nên được phát ra ở điều kiện nào?
 * <p>Nguồn §13 Q4; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở filter request 401/403, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_AuthenticationAuthorizationTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q4; không suy luận từ mock decoder.</p>
 * Q5 [CODE] Vì sao nên ưu tiên security filter/config chuẩn thay vì JWT filter tự viết?
 * <p>Nguồn §13 Q5; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở security, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_AuthenticationAuthorizationTest. Hoàn thành: quan sát status/state
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
public final class Ex01_AuthenticationAuthorization {
    private Ex01_AuthenticationAuthorization() {}
    public record Order(long id, String owner, int quantity, boolean cancelled) {}
    public record ChangeQuantity(@Min(1) @Max(1000) int quantity) {}

    public static Orders orders(Map<Long, Order> records) {
        throw new UnsupportedOperationException("TODO B13");
    }

    public static SecurityFilterChain security(HttpSecurity http, JwtDecoder decoder) throws Exception {
        throw new UnsupportedOperationException("TODO B13");
    }

    // ponytail: single-thread topic fake; capstone replaces records with transactional PostgreSQL ownership checks.
    public static final class Orders {
        private final Map<Long, Order> records;
        public Orders(Map<Long, Order> records) { this.records = new java.util.HashMap<>(records); }
        public Order read(String subject, long id) {
            throw new UnsupportedOperationException("TODO B13");
        }
        public Order changeQuantity(String subject, long id, int quantity) {
            throw new UnsupportedOperationException("TODO B13");
        }
        public Order cancel(String subject, long id) {
            throw new UnsupportedOperationException("TODO B13");
        }
    }

    @RestController
    public static class Controller {
        private final Orders orders;
        public Controller(Orders orders) { this.orders = orders; }
        @GetMapping("/api/orders/{id}")
        public Order read(@AuthenticationPrincipal Jwt jwt, @PathVariable long id) {
            throw new UnsupportedOperationException("TODO B13");
        }
        @PatchMapping("/api/orders/{id}")
        public Order change(@AuthenticationPrincipal Jwt jwt, @PathVariable long id,
                            @Valid @RequestBody ChangeQuantity request) {
            throw new UnsupportedOperationException("TODO B13");
        }
        @PostMapping("/api/orders/{id}/cancel")
        public Order cancel(@AuthenticationPrincipal Jwt jwt, @PathVariable long id) {
            throw new UnsupportedOperationException("TODO B13");
        }
        @GetMapping("/api/products")
        public Map<String, String> products() { return Map.of("name", "local-product"); }
        @GetMapping("/unlisted")
        public String unlisted() { return "must remain denied"; }
    }
}
