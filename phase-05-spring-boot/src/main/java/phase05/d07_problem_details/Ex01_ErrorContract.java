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
            throw new UnsupportedOperationException("TODO B07");
        }

        @GetMapping("/api/products/{id}")
        public ProductResponse find(@PathVariable long id) {
            throw new UnsupportedOperationException("TODO B07");
        }
    }

    // Explicit standalone registration only; no selector so pre-handler media errors are covered.
    @RestControllerAdvice
    public static class Errors extends ResponseEntityExceptionHandler {
        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException failure,
                HttpHeaders headers, HttpStatusCode status, WebRequest request) {
            throw new UnsupportedOperationException("TODO B07");
        }

        @Override
        protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException failure,
                HttpHeaders headers, HttpStatusCode status, WebRequest request) {
            throw new UnsupportedOperationException("TODO B07");
        }

        @Override
        protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException failure,
                HttpHeaders headers, HttpStatusCode status, WebRequest request) {
            throw new UnsupportedOperationException("TODO B07");
        }

        @ExceptionHandler(NotFound.class)
        public ProblemDetail notFound(NotFound failure) {
            throw new UnsupportedOperationException("TODO B07");
        }

        @ExceptionHandler(Conflict.class)
        public ProblemDetail conflict(Conflict failure) {
            throw new UnsupportedOperationException("TODO B07");
        }

        @ExceptionHandler(DataAccessException.class)
        public ProblemDetail database(DataAccessException failure) {
            throw new UnsupportedOperationException("TODO B07");
        }
    }

    public static ProductController controller(Products products) {
        throw new UnsupportedOperationException("TODO B07");
    }

    public static Errors advice() {
        throw new UnsupportedOperationException("TODO B07");
    }

    /** Filter-boundary Basic demonstration only; Task 4 owns bearer/JWT verification. */
    public static AuthenticationEntryPoint authenticationEntryPoint(JacksonJsonHttpMessageConverter converter) {
        throw new UnsupportedOperationException("TODO B07");
    }

    public static AccessDeniedHandler accessDeniedHandler(JacksonJsonHttpMessageConverter converter) {
        throw new UnsupportedOperationException("TODO B07");
    }

    private static ProblemDetail problem(HttpStatus status, String type, String detail) {
        throw new UnsupportedOperationException("TODO B07");
    }
}
