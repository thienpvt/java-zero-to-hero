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
    static final Location Q2_VALIDATION_LOCATION = null;

    // Q4 — entity có phụ thuộc HTTP request.
    static final Boolean Q4_ENTITY_DEPENDS_ON_REQUEST = null;

    /** Chính sách nghiệp vụ thuần, không biết HTTP. */
    public static final class OrderValidator {

        public ValidationResult validate(int itemCount, long totalCents) {
            throw new UnsupportedOperationException("TODO Q2");
        }
    }
}

/* ANSWER Q2:
 *
 */

/* ANSWER Q4:
 *
 */

/* ANSWER Q6:
 *
 */
