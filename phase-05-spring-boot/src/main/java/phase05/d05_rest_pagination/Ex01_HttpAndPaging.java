package phase05.d05_rest_pagination;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * <h2>Chủ đề 5 — REST, HTTP status và pagination</h2>
 * <p>Nguồn: {@code 05-spring-boot.md}, §5, Q1–Q5 và B05. Tiên quyết: MVC, DTO, HTTP.</p>
 * <p>B05: hoàn thiện factory và các method được đánh dấu, chạy {@code Ex01_HttpAndPagingTest}.
 * Boundary {@code Products} dùng fake, không chứng minh database paging hoặc JWT.</p>
 * Q1 [DỰ ĐOÁN] {@code 401} và {@code 403} khác nhau thế nào?
 * <p>Nguồn: §5 Q1. Tiên quyết: authentication/authorization.
 * Cách làm: mở Spring Security Reference, Basic Authentication; dự đoán status và challenge
 * trước khi đặt breakpoint vào handler B07. Hoàn thành: giải thích hai trường hợp trong ANSWER Q1.</p>
 * Q2 [CODE] Khi nào trả {@code 201} thay vì {@code 200}?
 * <p>Nguồn: §5 Q2. Tiên quyết: POST và URI tài nguyên.
 * Cách làm: mở {@code ProductController.create}, đọc ResponseEntity Javadoc, hoàn thiện B05;
 * debugger kiểm tra URI. Hoàn thành: test POST trả 201, Location và JSON tài nguyên mới.</p>
 * Q3 [TỰ TRẢ LỜI] Vì sao pagination cần sort ổn định?
 * <p>Nguồn: §5 Q3. Tiên quyết: thứ tự và khóa duy nhất.
 * Cách làm: mở test hai trang; debugger xem ID khi name/price bằng nhau, đọc Stream.sorted.
 * Hoàn thành: ANSWER Q3 phân biệt tie-breaker với snapshot khi dữ liệu thay đổi.</p>
 * Q4 [THÍ NGHIỆM] Rủi ro của page size không giới hạn là gì?
 * <p>Nguồn: §5 Q4. Tiên quyết: bộ nhớ và tải request.
 * Cách làm: đọc RequestParam Javadoc, chạy test size 100/101; debugger tại {@code page},
 * không cấp phát dữ liệu khổng lồ. Hoàn thành: ghi giới hạn quan sát được vào ANSWER Q4.</p>
 * Q5 [CODE] {@code 409} phù hợp với xung đột nào trong đặt hàng?
 * <p>Nguồn: §5 Q5. Tiên quyết: business invariant và HTTP status.
 * Cách làm: mở {@code conflict}, đọc ExceptionHandler Javadoc, bật fake stock conflict;
 * debugger kiểm tra không có Location. Hoàn thành: test conflict trả 409, không tạo thành công.</p>
 * ANSWER Q1:
 * SOLUTION-BEGIN
 * 401: chưa xác thực hoặc credential không hợp lệ; challenge mô tả cơ chế xác thực.
 * 403: đã xác thực nhưng thiếu quyền; gửi lại credential không tự cấp quyền còn thiếu.
 * SOLUTION-END
 * ANSWER Q2:
 * SOLUTION-BEGIN
 * 201 dùng khi request tạo tài nguyên mới; Location chỉ URI tài nguyên được tạo.
 * 200 phù hợp đọc hoặc xử lý thành công không tạo tài nguyên mới; GET không ghi dữ liệu.
 * SOLUTION-END
 * ANSWER Q3:
 * SOLUTION-BEGIN
 * name/price có thể bằng nhau; thêm ID duy nhất tránh đảo thứ tự giữa các trang cùng dữ liệu.
 * Tie-breaker không tạo snapshot: thay đổi đồng thời vẫn có thể dịch offset; cân nhắc cursor khi cần.
 * SOLUTION-END
 * ANSWER Q4:
 * SOLUTION-BEGIN
 * Size không giới hạn có thể làm tăng tải DB, serialization, bộ nhớ và thời gian đáp ứng.
 * B05 từ chối size ngoài 1–100, page âm và sort ngoài allowlist trước khi đọc boundary.
 * SOLUTION-END
 * ANSWER Q5:
 * SOLUTION-BEGIN
 * 409 phù hợp thiếu stock sau cạnh tranh, version conflict hoặc hủy order đã hủy.
 * JSON hỏng là 400, thiếu quyền là 403; lỗi DB bất ngờ không tự trở thành 409.
 * SOLUTION-END
 */
public final class Ex01_HttpAndPaging {
    private Ex01_HttpAndPaging() {}

    // Provided binding shape; implement HTTP/paging policy inside marked B05 bodies.
    public record CreateProductRequest(@NotBlank String name,
            @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal price) {}
    public record ProductResponse(long id, String name, BigDecimal price) {}
    public record PageResponse(List<ProductResponse> items, int page, int size, long totalItems, int totalPages) {}
    public interface Products {
        List<ProductResponse> all();
        ProductResponse create(CreateProductRequest request);
    }
    public static class StockConflict extends RuntimeException {}

    @RestController
    public static class ProductController {
        private final Products products;
        public ProductController(Products products) { this.products = products; }

        @GetMapping(path = "/api/products", produces = MediaType.APPLICATION_JSON_VALUE)
        public PageResponse page(@RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "id") String sort) {
            // SOLUTION-BEGIN throw B05
            if (page < 0 || size < 1 || size > 100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            Comparator<ProductResponse> order = switch (sort) {
                case "id" -> Comparator.comparingLong(ProductResponse::id);
                case "name" -> Comparator.comparing(ProductResponse::name).thenComparingLong(ProductResponse::id);
                case "price" -> Comparator.comparing(ProductResponse::price).thenComparingLong(ProductResponse::id);
                default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            };
            // ponytail: small fake boundary only; move LIMIT/ORDER BY into repository for real datasets.
            List<ProductResponse> all = products.all();
            var items = all.stream().sorted(order).skip((long) page * size).limit(size).toList();
            return new PageResponse(items, page, size, all.size(), (int) ((all.size() + (long) size - 1) / size));
            // SOLUTION-END
        }

        @PostMapping(path = "/api/products", consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
            // SOLUTION-BEGIN throw B05
            if (request.price() == null || (long) request.price().precision() - request.price().scale() > 17) {
                var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Price exceeds supported precision.");
                problem.setType(URI.create("urn:phase05:problem:validation"));
                throw new org.springframework.web.ErrorResponseException(HttpStatus.BAD_REQUEST, problem, null);
            }
            var created = products.create(new CreateProductRequest(request.name(), request.price().setScale(2)));
            return ResponseEntity.created(URI.create("/api/products/" + created.id())).body(created);
            // SOLUTION-END
        }

        @ExceptionHandler(org.springframework.web.ErrorResponseException.class)
        public ProblemDetail invalidInput(org.springframework.web.ErrorResponseException failure) {
            // SOLUTION-BEGIN throw B05
            return failure.getBody();
            // SOLUTION-END
        }

        @ExceptionHandler(StockConflict.class)
        public ResponseEntity<Void> conflict() {
            // SOLUTION-BEGIN throw B05
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
            // SOLUTION-END
        }
    }

    public static ProductController controller(Products products) {
        // SOLUTION-BEGIN throw B05
        return new ProductController(products);
        // SOLUTION-END
    }
}
