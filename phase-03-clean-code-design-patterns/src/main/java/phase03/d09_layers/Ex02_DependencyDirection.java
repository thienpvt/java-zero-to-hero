package phase03.d09_layers;

import java.util.Arrays;
import java.util.List;

/**
 * Layered Architecture — Bài 2: hướng phụ thuộc
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 9, câu 4, 5, 6.
 * Cần làm trước: Ex01_DtoAndEntity.
 * Cách làm: chạy test trong Ex02_DependencyDirectionTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q4 [DỰ ĐOÁN] Business logic nên nằm ở controller hay service/domain?
 *   Bắt đầu   : điền Q4_BUSINESS_IN_CONTROLLER.
 *   Kiểm chứng: chạy q04_prediction.
 *   Hoàn thành khi: q04_prediction xanh và ANSWER Q4 nêu lý do tái dùng và test.
 * <p>
 * Q5 [DỰ ĐOÁN] Có phải mọi project đều cần 5 layer không?
 *   Bắt đầu   : điền Q5_ALWAYS_FIVE_LAYERS.
 *   Kiểm chứng: chạy q05_prediction và q05_domainImportsNothingOutside.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 nêu layer chỉ thêm khi có thay đổi độc lập.
 * <p>
 * Ví dụ Q6 [CODE] Domain không được import controller, SDK ngoài hay JPA entity.
 *   Bắt đầu   : cài {@code domainImports} trả danh sách tên import của {@code OrderPolicy}
 *               đọc qua {@code OrderPolicy.class} và annotation nếu có; ở đây trả List.of() cho
 *               lớp sạch và để test tự kiểm điều kiện rỗng.
 *   Kiểm chứng: chạy q06_domainImportsNothingOutside.
 *   Hoàn thành khi: q06_domainImportsNothingOutside xanh.
 */
public class Ex02_DependencyDirection {

    // Q4 — business logic nên ở controller hay service/domain.
    static final Boolean Q4_BUSINESS_IN_CONTROLLER = false; // SOLUTION-VALUE

    // Q5 — mọi project đều cần đủ năm layer.
    static final Boolean Q5_ALWAYS_FIVE_LAYERS = false; // SOLUTION-VALUE

    /** Cho sẵn: policy nghiệp vụ thuần, không import gì ngoài JDK. */
    public static final class OrderPolicy {

        public long applyVipDiscount(long totalCents) {
            return totalCents * 9 / 10;
        }
    }

    /** Tên các kiểu mà domain được phép tham chiếu. */
    public static final class DomainBoundary {

        private DomainBoundary() {
        }

        public static List<String> domainImports() {
            // SOLUTION-BEGIN throw Q6
            return List.of("java.lang", "java.util", "java.math", "phase03");
            // SOLUTION-END
        }

        public static boolean isAllowed(String importName) {
            return domainImports().stream().anyMatch(importName::startsWith);
        }

        public static List<String> forbiddenPrefixes() {
            return Arrays.asList("org.springframework", "jakarta.persistence", "com.stripe", "org.apache.kafka");
        }
    }
}

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Logic thuộc service hoặc domain, không thuộc controller.
 * Controller biết HTTP nên không dùng lại được cho job nền, và test phải dựng cả request.
 * Domain thuần test bằng lời gọi trực tiếp, không cần hạ tầng.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Không. Layer đáng tồn tại khi nó cô lập một thay đổi thật, ví dụ đổi giao thức hoặc đổi database.
 * Với ứng dụng nhỏ chỉ một cách dùng, thêm tầng chỉ tăng số file phải đọc.
 * Sơ đồ trong tài liệu là luồng xử lý, không phải yêu cầu đủ mặt mọi lớp.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Domain chỉ được tham chiếu kiểu của chính nó và JDK.
 * Import SDK nhà cung cấp hoặc JPA entity làm domain phụ thuộc hạ tầng, đảo ngược hướng mong muốn.
 * Adapter triển khai interface của domain và nằm ở infrastructure.
 * SOLUTION-END
 */
