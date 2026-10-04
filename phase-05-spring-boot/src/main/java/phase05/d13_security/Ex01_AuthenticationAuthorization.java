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
 * SOLUTION-BEGIN
 * Authentication xác minh caller; authorization quyết định quyền. Token hợp lệ thiếu scope vẫn nhận 403, token invalid nhận 401.
 * SOLUTION-END
 * ANSWER Q2:
 * SOLUTION-BEGIN
 * Role/scope chỉ cho phép loại thao tác. Service phải kiểm tra owner từ principal cho từng order; guessed ID của owner khác trả 404 cho cả đọc, sửa và hủy.
 * SOLUTION-END
 * ANSWER Q3:
 * SOLUTION-BEGIN
 * Principal đã được authentication xác thực là nguồn ID; JWT subject được map nội bộ. Không tin customerId trong body, query hoặc header tự khai báo.
 * SOLUTION-END
 * ANSWER Q4:
 * SOLUTION-BEGIN
 * 401 khi thiếu hoặc sai authentication; 403 khi principal hợp lệ thiếu quyền. Quan sát WWW-Authenticate Bearer; insufficient_scope khác invalid_token.
 * SOLUTION-END
 * ANSWER Q5:
 * SOLUTION-BEGIN
 * Resource server chuẩn đã xử lý Bearer header, chữ ký, validator, authority mapping và challenge. Cấu hình SecurityFilterChain explicit, stateless, deny-by-default; không tự viết JWT filter.
 * SOLUTION-END
 */
public final class Ex01_AuthenticationAuthorization {
    private Ex01_AuthenticationAuthorization() {}
    public record Order(long id, String owner, int quantity, boolean cancelled) {}
    public record ChangeQuantity(@Min(1) @Max(1000) int quantity) {}

    public static Orders orders(Map<Long, Order> records) {
        // SOLUTION-BEGIN throw B13
        return new Orders(records);
        // SOLUTION-END
    }

    public static SecurityFilterChain security(HttpSecurity http, JwtDecoder decoder) throws Exception {
        // SOLUTION-BEGIN throw B13
        return http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/products").hasAuthority("SCOPE_products.read")
                        .requestMatchers(HttpMethod.GET, "/api/orders", "/api/orders/**").hasAuthority("SCOPE_orders.read")
                        .requestMatchers(HttpMethod.POST, "/api/orders", "/api/orders/*/cancel").hasAuthority("SCOPE_orders.write")
                        .requestMatchers(HttpMethod.PATCH, "/api/orders/*").hasAuthority("SCOPE_orders.write")
                        .anyRequest().denyAll())
                .oauth2ResourceServer(resource -> resource.jwt(jwt -> jwt.decoder(decoder))).build();
        // SOLUTION-END
    }

    // ponytail: single-thread topic fake; capstone replaces records with transactional PostgreSQL ownership checks.
    public static final class Orders {
        private final Map<Long, Order> records;
        public Orders(Map<Long, Order> records) { this.records = new java.util.HashMap<>(records); }
        public Order read(String subject, long id) {
            // SOLUTION-BEGIN throw B13
            var order = records.get(id);
            if (subject == null || subject.isBlank() || order == null || !subject.equals(order.owner())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
            }
            return order;
            // SOLUTION-END
        }
        public Order changeQuantity(String subject, long id, int quantity) {
            // SOLUTION-BEGIN throw B13
            var order = read(subject, id);
            if (quantity < 1 || quantity > 1000) throw new IllegalArgumentException("Quantity out of range");
            if (order.cancelled()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Order already cancelled");
            var changed = new Order(id, order.owner(), quantity, false);
            records.put(id, changed);
            return changed;
            // SOLUTION-END
        }
        public Order cancel(String subject, long id) {
            // SOLUTION-BEGIN throw B13
            var order = read(subject, id);
            if (order.cancelled()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Order already cancelled");
            var cancelled = new Order(id, order.owner(), order.quantity(), true);
            records.put(id, cancelled);
            return cancelled;
            // SOLUTION-END
        }
    }

    @RestController
    public static class Controller {
        private final Orders orders;
        public Controller(Orders orders) { this.orders = orders; }
        @GetMapping("/api/orders/{id}")
        public Order read(@AuthenticationPrincipal Jwt jwt, @PathVariable long id) {
            // SOLUTION-BEGIN throw B13
            return orders.read(jwt.getSubject(), id);
            // SOLUTION-END
        }
        @PatchMapping("/api/orders/{id}")
        public Order change(@AuthenticationPrincipal Jwt jwt, @PathVariable long id,
                            @Valid @RequestBody ChangeQuantity request) {
            // SOLUTION-BEGIN throw B13
            return orders.changeQuantity(jwt.getSubject(), id, request.quantity());
            // SOLUTION-END
        }
        @PostMapping("/api/orders/{id}/cancel")
        public Order cancel(@AuthenticationPrincipal Jwt jwt, @PathVariable long id) {
            // SOLUTION-BEGIN throw B13
            return orders.cancel(jwt.getSubject(), id);
            // SOLUTION-END
        }
        @GetMapping("/api/products")
        public Map<String, String> products() { return Map.of("name", "local-product"); }
        @GetMapping("/unlisted")
        public String unlisted() { return "must remain denied"; }
    }
}
