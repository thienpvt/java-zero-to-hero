package phase05.d06_dto_validation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <h2>Chủ đề 6 — DTO, validation và invariant</h2>
 * <p>Nguồn: {@code 05-spring-boot.md}, §6, Q1–Q5 và B06.
 * Tiên quyết: MVC binding, Jakarta Validation, SQL CHECK/NOT NULL, BigDecimal.</p>
 * <p>B06: hoàn thiện mapping/factory và SQL trong Java, chạy {@code Ex01_BoundaryValidationTest}.
 * PostgreSQL bắt buộc, container riêng mỗi class; đây không phải capstone schema/migration.</p>
 * Q1 [DỰ ĐOÁN] Vì sao không trả JPA entity từ controller?
 * <p>Nguồn: §6 Q1. Tiên quyết: entity relation và serialization.
 * Cách làm: đọc Spring MVC Message Converters; dự đoán response, debugger xem fake entity
 * có relation tự trỏ và internalNote. Hoàn thành: response chỉ có DTO, viết ANSWER Q1.</p>
 * Q2 [CODE] {@code @Valid} kiểm tra loại quy tắc nào, và không thể thay thế quy tắc nào?
 * <p>Nguồn: §6 Q2. Tiên quyết: Jakarta constraint và service invariant.
 * Cách làm: mở {@code create}, đọc RequestBody Validation; hoàn thiện B06, debugger
 * binding trước service. Hoàn thành: field lỗi trả 400, stock conflict vẫn do service xử lý.</p>
 * Q3 [TỰ TRẢ LỜI] Validation DTO có thể ngăn race condition giữa hai transaction không?
 * <p>Nguồn: §6 Q3. Tiên quyết: transaction và cạnh tranh.
 * Cách làm: đọc PostgreSQL transaction isolation, debugger phân biệt request hợp lệ với
 * trạng thái stock tại thời điểm ghi. Hoàn thành: ANSWER Q3 nêu cơ chế DB cần thêm.</p>
 * Q4 [THÍ NGHIỆM] Vì sao cần cả check nghiệp vụ lẫn constraint DB?
 * <p>Nguồn: §6 Q4. Tiên quyết: SQLState và JDBC autocommit.
 * Cách làm: đọc PostgreSQL Constraints, chạy test insert trực tiếp bỏ qua MVC; debugger
 * SQLState và truy vấn connection mới. Hoàn thành: ghi quan sát 23502/23514 vào ANSWER Q4.</p>
 * Q5 [CODE] Khi nào response DTO nên khác request DTO?
 * <p>Nguồn: §6 Q5. Tiên quyết: input trust và tiền tệ.
 * Cách làm: mở {@code ProductResponse}, đọc BigDecimal.setScale; hoàn thiện mapper B06,
 * debugger tổng phía server. Hoàn thành: id/currency/total do server cấp, không lộ relation.</p>
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
public final class Ex01_BoundaryValidation {
    private Ex01_BoundaryValidation() {}

    // Provided binding metadata; learner owns mapping, HTTP adapter and DB constraints below.
    public record CreateProductRequest(@NotBlank String name,
            @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal price,
            @NotNull @Min(1) @Max(1000) Integer quantity) {}
    public record ProductResponse(long id, String name, BigDecimal price, int quantity, String currency, BigDecimal total) {}

    @Entity(name = "BoundaryProduct")
    @Table(name = "products")
    public static class ProductEntity {
        @Id @GeneratedValue public Long id;
        public String name;
        public BigDecimal price;
        public Integer quantity;
        public String internalNote;
        @ManyToOne public ProductEntity related;
    }

    public interface Products { ProductEntity create(CreateProductRequest request); }
    public static class StockConflict extends RuntimeException {}

    @RestController
    public static class ProductController {
        private final Products products;
        public ProductController(Products products) { this.products = products; }

        @PostMapping(path = "/api/products", consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
            throw new UnsupportedOperationException("TODO B06");
        }

        @ExceptionHandler(org.springframework.web.ErrorResponseException.class)
        public ProblemDetail invalidInput(org.springframework.web.ErrorResponseException failure) {
            throw new UnsupportedOperationException("TODO B06");
        }

        @ExceptionHandler(StockConflict.class)
        public ResponseEntity<Void> conflict() {
            throw new UnsupportedOperationException("TODO B06");
        }
    }

    public static ProductController controller(Products products) {
        throw new UnsupportedOperationException("TODO B06");
    }

    /** B06: topic-local JDBC schema, not capstone/Flyway migration or Hibernate proof. */
    public static String schemaSql() {
        throw new UnsupportedOperationException("TODO B06");
    }
}
