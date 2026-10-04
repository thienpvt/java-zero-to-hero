package phase05.d15_testing;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * <h2>Chủ đề 15 — Test theo boundary</h2>
 * <p>Nguồn: 05-spring-boot.md §15, Q1–Q5, B15.
 * Tiên quyết: Java 21, HTTP, Spring Security/JUnit, PostgreSQL thật.
 * IntelliJ: mở Ex01_TestBoundariesTest, chạy test rồi đặt breakpoint trong body B15.
 * Chỉ cấu hình explicit của topic; không scan phase05 hoặc dùng identity provider mạng.</p>
 * Q1 [DỰ ĐOÁN] Test nào chứng minh validation/binding của MVC?
 * <p>Nguồn §15 Q1; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở Controller.create, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_TestBoundariesTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q1; không suy luận từ mock decoder.</p>
 * Q2 [CODE] Vì sao unit test repository mock không chứng minh SQL đúng?
 * <p>Nguồn §15 Q2; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở schemaSql, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_TestBoundariesTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q2; không suy luận từ mock decoder.</p>
 * Q3 [TỰ TRẢ LỜI] Vì sao H2 không đủ cho PostgreSQL lock/migration behavior?
 * <p>Nguồn §15 Q3; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở PostgreSQL constraint, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_TestBoundariesTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q3; không suy luận từ mock decoder.</p>
 * Q4 [THÍ NGHIỆM] Khi nào `@SpringBootTest` đáng chi phí?
 * <p>Nguồn §15 Q4; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở unit/MVC/filter tests, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_TestBoundariesTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q4; không suy luận từ mock decoder.</p>
 * Q5 [CODE] Vì sao test token decoder thật khác test controller với mock claims?
 * <p>Nguồn §15 Q5; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở Controller.owner, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_TestBoundariesTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q5; không suy luận từ mock decoder.</p>
 * ANSWER Q1:
 * SOLUTION-BEGIN
 * MVC test gửi request thật qua binding/validation kiểm tra 400, JSON/status và không gọi service. Unit test DTO không chứng minh DispatcherServlet boundary.
 * SOLUTION-END
 * ANSWER Q2:
 * SOLUTION-BEGIN
 * Mock repository không chạy JDBC/SQL; chỉ chứng minh logic dùng stub. SQL syntax, schema, constraint và rollback cần PostgreSQL thật.
 * SOLUTION-END
 * ANSWER Q3:
 * SOLUTION-BEGIN
 * H2 khác dialect, migration và MVCC/lock của PostgreSQL. Chạy migration/constraint trên PostgreSQL 18 disposable, không embedded replacement hoặc auto-skip.
 * SOLUTION-END
 * ANSWER Q4:
 * SOLUTION-BEGIN
 * Khi cần kiểm chứng nhiều lớp wiring, real HTTP/security/DB/Actuator. Giữ unit không context, MVC ở boundary hẹp; không full Boot cho mọi test.
 * SOLUTION-END
 * ANSWER Q5:
 * SOLUTION-BEGIN
 * Decoder thật xác minh token signed và claims; MockMvc jwt() chèn principal, bỏ qua chữ ký/decoder. Mock claims chỉ chứng minh controller ownership/mapping, không chứng minh JWT an toàn.
 * SOLUTION-END
 */
public final class Ex01_TestBoundaries {
    private Ex01_TestBoundaries() {}
    public record Request(@NotBlank String name, @Min(1) int quantity) {}
    public record Created(long id, String owner, String name, int quantity) {}
    public interface Store { long insert(String owner, String name, int quantity); }

    public static Service service(Store store) {
        // SOLUTION-BEGIN throw B15
        return new Service(java.util.Objects.requireNonNull(store));
        // SOLUTION-END
    }

    public static Controller controller(Service service) {
        // SOLUTION-BEGIN throw B15
        return new Controller(java.util.Objects.requireNonNull(service));
        // SOLUTION-END
    }

    public static String schemaSql() {
        // SOLUTION-BEGIN throw B15
        return """
                CREATE TABLE boundary_orders (
                    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    owner TEXT NOT NULL CHECK (length(trim(owner)) > 0),
                    name TEXT NOT NULL CHECK (length(trim(name)) > 0),
                    quantity INTEGER NOT NULL CHECK (quantity >= 1)
                )
                """;
        // SOLUTION-END
    }

    public static final class Service {
        private final Store store;
        public Service(Store store) { this.store = store; }
        public Created create(String subject, Request request) {
            // SOLUTION-BEGIN throw B15
            if (subject == null || subject.isBlank() || request == null || request.name() == null
                    || request.name().isBlank() || request.quantity() < 1) {
                throw new IllegalArgumentException("Owner and valid request are required");
            }
            return new Created(store.insert(subject, request.name(), request.quantity()), subject, request.name(), request.quantity());
            // SOLUTION-END
        }
    }

    @RestController
    public static class Controller {
        private final Service service;
        public Controller(Service service) { this.service = service; }
        @PostMapping("/api/orders")
        public ResponseEntity<Created> create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody Request request) {
            // SOLUTION-BEGIN throw B15
            var created = service.create(jwt.getSubject(), request);
            return ResponseEntity.created(URI.create("/api/orders/" + created.id())).body(created);
            // SOLUTION-END
        }
        @GetMapping("/api/orders/current")
        public String owner(@AuthenticationPrincipal Jwt jwt) {
            // SOLUTION-BEGIN throw B15
            return jwt.getSubject();
            // SOLUTION-END
        }
    }
}
