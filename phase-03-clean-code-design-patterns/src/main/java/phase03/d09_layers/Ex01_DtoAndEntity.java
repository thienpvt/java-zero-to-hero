package phase03.d09_layers;

import java.util.List;

/**
 * Layered Architecture — Bài 1: DTO và entity
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 9 (Layered Architecture), câu 1, 2, 3.
 * Cần làm trước: d08_concerns.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_DtoAndEntityTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Vì sao không nên expose JPA Entity trực tiếp từ REST API trong nhiều hệ thống?
 *   Bắt đầu   : điền Q1_EXPOSE_ENTITY bằng một giá trị của {@code Risk}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu đổi schema làm đổi API.
 * <p>
 * Q2 [DỰ ĐOÁN] DTO giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q2_DTO_PURPOSE bằng một giá trị của {@code Purpose}.
 *   Kiểm chứng: chạy q02_prediction và q02_mapperHidesInternalField.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu contract API tách khỏi mô hình lưu trữ.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Mapping thủ công và mapper library có trade-off gì?
 *   Bắt đầu   : viết khối ANSWER Q3.
 *   Hoàn thành khi: ANSWER Q3 nêu kiểm soát tường minh so với ít code lặp.
 * <p>
 * Q6 [CODE] Layered architecture có nhược điểm gì?
 *   Bắt đầu   : cài {@code OrderMapper.toResponse}. Bản response không được có field nội bộ.
 *   Kiểm chứng: chạy q06_responseHasOnlyContractFields.
 *   Hoàn thành khi: q06_responseHasOnlyContractFields xanh; ANSWER Q6 nêu chi phí đi qua nhiều tầng.
 */
public class Ex01_DtoAndEntity {

    /** Rủi ro khi expose entity trực tiếp. */
    public enum Risk {
        SLOWER_QUERY,
        SCHEMA_CHANGE_BREAKS_API,
        MORE_CLASSES
    }

    /** Việc DTO làm. */
    public enum Purpose {
        SPEED_UP_QUERY,
        SEPARATE_API_CONTRACT_FROM_STORAGE_MODEL,
        REDUCE_CLASSES
    }

    /** Cho sẵn: entity lưu trữ, có field nội bộ không thuộc contract API. */
    public record OrderEntity(String id, long totalCents, String internalNote) {
    }

    /** Contract API, chỉ có field cho client. */
    public record OrderResponse(String id, long totalCents) {
    }

    // Q1 — expose entity trực tiếp có rủi ro gì.
    static final Risk Q1_EXPOSE_ENTITY = null;

    // Q2 — DTO giải quyết vấn đề gì.
    static final Purpose Q2_DTO_PURPOSE = null;

    /** Chuyển entity sang contract API. */
    public static final class OrderMapper {

        private OrderMapper() {
        }

        public static OrderResponse toResponse(OrderEntity entity) {
            throw new UnsupportedOperationException("TODO Q6");
        }

        public static List<OrderResponse> toResponses(List<OrderEntity> entities) {
            throw new UnsupportedOperationException("TODO Q6");
        }
    }
}

/* ANSWER Q1:
 *
 */

/* ANSWER Q2:
 *
 */

/* ANSWER Q3:
 *
 */

/* ANSWER Q6:
 *
 */
