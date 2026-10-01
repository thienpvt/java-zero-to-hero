package phase03.d08_concerns;

/**
 * Separation of Concerns — Bài 2: chỗ đặt chính sách
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 8, câu 2, 4, 6.
 * Cần làm trước: Ex01_ControllerResponsibility.
 * Cách làm: chạy test trong Ex02_PolicyPlacementTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q2 [DỰ ĐOÁN] Validation business rule nên nằm ở đâu?
 *   Bắt đầu   : điền Q2_VALIDATION_LOCATION bằng một giá trị của {@code Location}.
 *   Kiểm chứng: chạy q02_prediction và q02_validatorRejectsEmptyItems.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu chính sách nghiệp vụ ở domain.
 * <p>
 * Q4 [DỰ ĐOÁN] Entity có nên phụ thuộc HTTP request không?
 *   Bắt đầu   : điền Q4_ENTITY_DEPENDS_ON_REQUEST.
 *   Kiểm chứng: chạy q04_prediction.
 *   Hoàn thành khi: q04_prediction xanh và ANSWER Q4 nêu entity sẽ phải build theo framework.
 * <p>
 * Q6 [TỰ TRẢ LỜI] Có thể phân layer quá mức không?
 *   Bắt đầu   : viết khối ANSWER Q6.
 *   Hoàn thành khi: ANSWER Q6 nêu tầng chuyển tiếp không thêm trách nhiệm.
 */
public class Ex02_PolicyPlacement {

    /** Nơi đặt validation nghiệp vụ. */
    public enum Location {
        CONTROLLER,
        DOMAIN,
        DATABASE
    }

    public record ValidationResult(boolean valid, String message) {
    }

    // Q2 — validation nghiệp vụ nên ở đâu.
    static final Location Q2_VALIDATION_LOCATION = Location.DOMAIN; // SOLUTION-VALUE

    // Q4 — entity có phụ thuộc HTTP request.
    static final Boolean Q4_ENTITY_DEPENDS_ON_REQUEST = false; // SOLUTION-VALUE

    /** Chính sách nghiệp vụ thuần, không biết HTTP. */
    public static final class OrderValidator {

        public ValidationResult validate(int itemCount, long totalCents) {
            // SOLUTION-BEGIN throw Q2
            if (itemCount <= 0) {
                return new ValidationResult(false, "Đơn phải có ít nhất một mặt hàng.");
            }
            if (totalCents <= 0) {
                return new ValidationResult(false, "Tổng tiền phải dương.");
            }
            return new ValidationResult(true, "ok");
            // SOLUTION-END
        }
    }
}

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Quy tắc nghiệp vụ thuộc domain vì nó không phụ thuộc giao thức hay lưu trữ.
 * Controller chỉ chuyển lỗi đó thành mã trạng thái phù hợp.
 * Nếu để trong controller, quy tắc không dùng lại được cho job khác và mỗi giao thức phải lặp lại.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Không. Entity phụ thuộc HTTP request nghĩa là entity chỉ dựng được trong một ngữ cảnh web.
 * Mọi job nền hoặc test phải giả lập request, và đổi framework sẽ đụng vào domain.
 * Chuyển đổi từ request sang kiểu domain là việc của lớp biên.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Có. Layer quá mức là tầng chỉ chuyển tiếp lời gọi mà không thêm trách nhiệm nào.
 * Mỗi tầng thêm một lần đọc và một chỗ có thể đặt sai logic.
 * Tầng đáng tồn tại khi nó dịch một mô hình hoặc cô lập một hệ ngoài.
 * SOLUTION-END
 */
