package phase05.d07_problem_details;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * <h2>Chủ đề 7 — Error contract và Problem Details</h2>
 * <p>Nguồn: {@code 05-spring-boot.md}, §7, Q1–Q5 và B07.
 * Tiên quyết: HTTP, validation MVC, servlet security filter, RFC 9457.</p>
 * <p>B07: hoàn thiện các body được đánh dấu; chạy {@code Ex01_ErrorContractTest}.
 * Handler auth dùng contract Basic riêng, không cấu hình live authentication/JWT hoặc chứng minh decoder.</p>
 * Q1 [DỰ ĐOÁN] Vì sao lỗi validation thường là 4xx nhưng lỗi DB bất ngờ thường là 5xx?
 * <p>Nguồn: §7 Q1. Tiên quyết: client/server responsibility.
 * Cách làm: đọc Spring MVC Error Responses; dự đoán status, debugger validation và DB fake.
 * Hoàn thành: phân biệt 400 với 500 trong ANSWER Q1, không biến mọi exception thành 400.</p>
 * Q2 [CODE] {@code ProblemDetail} giải quyết phần nào của error contract?
 * <p>Nguồn: §7 Q2. Tiên quyết: JSON media type và URI type.
 * Cách làm: mở {@code problem}, đọc ProblemDetail Javadoc/RFC 9457, hoàn thiện B07;
 * debugger kiểm tra type/status/instance. Hoàn thành: MVC trả application/problem+json ổn định.</p>
 * Q3 [TỰ TRẢ LỜI] Vì sao Security exception không nhất thiết tới {@code @RestControllerAdvice}?
 * <p>Nguồn: §7 Q3. Tiên quyết: filter nằm trước DispatcherServlet.
 * Cách làm: đọc Spring Security ExceptionTranslationFilter, debugger handler auth trực tiếp
 * ngoài MockMvc. Hoàn thành: ANSWER Q3 nêu entry point và denied handler riêng.</p>
 * Q4 [THÍ NGHIỆM] Thông tin nào không được trả trong error body?
 * <p>Nguồn: §7 Q4. Tiên quyết: bí mật/PII và persistence errors.
 * Cách làm: đọc RFC 9457 Security Considerations, chạy fake exception có sentinel SQL/token;
 * debugger xem body công khai. Hoàn thành: ghi dữ liệu bị loại bỏ trong ANSWER Q4.</p>
 * Q5 [CODE] Có nên trả nguyên thông điệp exception từ persistence layer không?
 * <p>Nguồn: §7 Q5. Tiên quyết: exception translation.
 * Cách làm: mở {@code database}, đọc DataAccessException Javadoc; hoàn thiện B07,
 * debugger so sánh exception với safe detail. Hoàn thành: DB bất ngờ trả 500 không lộ nội bộ.</p>
 * ANSWER Q1:
 * SOLUTION-BEGIN
 * Validation DTO sai là input client có thể sửa, thường 400; JSON/media type lỗi cũng là 4xx.
 * DB bất ngờ là lỗi phía server, thường 500; conflict nghiệp vụ đã xác định mới ánh xạ 409.
 * SOLUTION-END
 * ANSWER Q2:
 * SOLUTION-BEGIN
 * ProblemDetail chuẩn hóa type/title/status/detail/instance và media type RFC 9457.
 * API vẫn phải chọn type ổn định, phân loại lỗi và detail an toàn; nó không tự xử lý security filter.
 * SOLUTION-END
 * ANSWER Q3:
 * SOLUTION-BEGIN
 * Security filter có thể từ chối trước DispatcherServlet nên MVC advice chưa được gọi.
 * AuthenticationEntryPoint xử lý 401/challenge; AccessDeniedHandler xử lý thiếu quyền 403 riêng.
 * SOLUTION-END
 * ANSWER Q4:
 * SOLUTION-BEGIN
 * Không trả stack trace, SQL/schema/class nội bộ, token, credential, secret hay dữ liệu cá nhân.
 * Chỉ trả thông tin công khai allowlist; không echo rejected value hoặc exception message.
 * SOLUTION-END
 * ANSWER Q5:
 * SOLUTION-BEGIN
 * Không; message persistence có thể chứa SQL, constraint/table name hoặc dữ liệu riêng tư.
 * Ánh xạ exception đã biết sang type/detail công khai; lỗi DB bất ngờ giữ 500 và body an toàn.
 * SOLUTION-END
 */
public final class Ex01_ErrorContract {
    private Ex01_ErrorContract() {}

    public record CreateProductRequest(@NotBlank String name) {}
    public record ProductResponse(long id, String name) {}
    public interface Products {
        ProductResponse create(CreateProductRequest request);
        ProductResponse find(long id);
    }
    public static class NotFound extends RuntimeException { public NotFound(String message) { super(message); } }
    public static class Conflict extends RuntimeException { public Conflict(String message) { super(message); } }

    @RestController
    public static class ProductController {
        private final Products products;
        public ProductController(Products products) { this.products = products; }

        @PostMapping(path = "/api/products", consumes = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
            // SOLUTION-BEGIN throw B07
            var created = products.create(request);
            return ResponseEntity.created(URI.create("/api/products/" + created.id())).body(created);
            // SOLUTION-END
        }

        @GetMapping("/api/products/{id}")
        public ProductResponse find(@PathVariable long id) {
            // SOLUTION-BEGIN throw B07
            return products.find(id);
            // SOLUTION-END
        }
    }

    // Explicit standalone registration only; no selector so pre-handler media errors are covered.
    @RestControllerAdvice
    public static class Errors extends ResponseEntityExceptionHandler {
        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException failure,
                HttpHeaders headers, HttpStatusCode status, WebRequest request) {
            // SOLUTION-BEGIN throw B07
            return handleExceptionInternal(failure, problem(HttpStatus.BAD_REQUEST, "validation", "Request fields are invalid."),
                    headers, HttpStatus.BAD_REQUEST, request);
            // SOLUTION-END
        }

        @Override
        protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException failure,
                HttpHeaders headers, HttpStatusCode status, WebRequest request) {
            // SOLUTION-BEGIN throw B07
            return handleExceptionInternal(failure, problem(HttpStatus.BAD_REQUEST, "malformed-json", "Request JSON is invalid."),
                    headers, HttpStatus.BAD_REQUEST, request);
            // SOLUTION-END
        }

        @Override
        protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException failure,
                HttpHeaders headers, HttpStatusCode status, WebRequest request) {
            // SOLUTION-BEGIN throw B07
            return handleExceptionInternal(failure, problem(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "unsupported-media-type",
                    "Use application/json."), headers, HttpStatus.UNSUPPORTED_MEDIA_TYPE, request);
            // SOLUTION-END
        }

        @ExceptionHandler(NotFound.class)
        public ProblemDetail notFound(NotFound failure) {
            // SOLUTION-BEGIN throw B07
            return problem(HttpStatus.NOT_FOUND, "not-found", "Resource not found.");
            // SOLUTION-END
        }

        @ExceptionHandler(Conflict.class)
        public ProblemDetail conflict(Conflict failure) {
            // SOLUTION-BEGIN throw B07
            return problem(HttpStatus.CONFLICT, "conflict", "Resource state conflicts with this request.");
            // SOLUTION-END
        }

        @ExceptionHandler(DataAccessException.class)
        public ProblemDetail database(DataAccessException failure) {
            // SOLUTION-BEGIN throw B07
            return problem(HttpStatus.INTERNAL_SERVER_ERROR, "internal", "Request could not be completed.");
            // SOLUTION-END
        }
    }

    public static ProductController controller(Products products) {
        // SOLUTION-BEGIN throw B07
        return new ProductController(products);
        // SOLUTION-END
    }

    public static Errors advice() {
        // SOLUTION-BEGIN throw B07
        return new Errors();
        // SOLUTION-END
    }

    /** Filter-boundary Basic demonstration only; Task 4 owns bearer/JWT verification. */
    public static AuthenticationEntryPoint authenticationEntryPoint(JacksonJsonHttpMessageConverter converter) {
        // SOLUTION-BEGIN throw B07
        return (request, response, failure) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"phase05-lab\"");
            var body = problem(HttpStatus.UNAUTHORIZED, "authentication", "Authentication is required.");
            body.setInstance(URI.create(request.getRequestURI()));
            converter.write(body, MediaType.APPLICATION_PROBLEM_JSON, new ServletServerHttpResponse(response));
        };
        // SOLUTION-END
    }

    public static AccessDeniedHandler accessDeniedHandler(JacksonJsonHttpMessageConverter converter) {
        // SOLUTION-BEGIN throw B07
        return (request, response, failure) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            // Basic 403 is insufficient permission, not a fresh authentication challenge.
            var body = problem(HttpStatus.FORBIDDEN, "forbidden", "Permission is required.");
            body.setInstance(URI.create(request.getRequestURI()));
            converter.write(body, MediaType.APPLICATION_PROBLEM_JSON, new ServletServerHttpResponse(response));
        };
        // SOLUTION-END
    }

    private static ProblemDetail problem(HttpStatus status, String type, String detail) {
        // SOLUTION-BEGIN throw B07
        var body = ProblemDetail.forStatusAndDetail(status, detail);
        body.setType(URI.create("urn:phase05:problem:" + type));
        body.setTitle(status.getReasonPhrase());
        return body;
        // SOLUTION-END
    }
}
