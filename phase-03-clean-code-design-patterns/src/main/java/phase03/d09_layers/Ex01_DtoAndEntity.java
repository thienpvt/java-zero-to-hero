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
    static final Risk Q1_EXPOSE_ENTITY = Risk.SCHEMA_CHANGE_BREAKS_API; // SOLUTION-VALUE

    // Q2 — DTO giải quyết vấn đề gì.
    static final Purpose Q2_DTO_PURPOSE = Purpose.SEPARATE_API_CONTRACT_FROM_STORAGE_MODEL; // SOLUTION-VALUE

    /** Chuyển entity sang contract API. */
    public static final class OrderMapper {

        private OrderMapper() {
        }

        public static OrderResponse toResponse(OrderEntity entity) {
            // SOLUTION-BEGIN throw Q6
            if (entity == null) {
                throw new IllegalArgumentException("Entity không được null.");
            }
            return new OrderResponse(entity.id(), entity.totalCents());
            // SOLUTION-END
        }

        public static List<OrderResponse> toResponses(List<OrderEntity> entities) {
            // SOLUTION-BEGIN throw Q6
            return entities.stream().map(OrderMapper::toResponse).toList();
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Entity phản ánh schema lưu trữ, nên đổi cột hoặc đổi quan hệ là đổi luôn response API.
 * Entity còn chứa field nội bộ không nên đi ra client, hoặc chứa quan hệ lazy gây lỗi tuần tự hoá.
 * Tách DTO giữ hai thứ đổi theo nhịp khác nhau.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * DTO định nghĩa contract của API, độc lập với bảng và cột.
 * Nó cho phép đổi schema mà không đổi response, và chỉ đưa ra field client cần.
 * Kèm theo là chỗ để gộp hoặc đổi tên field cho phù hợp ngôn ngữ của client.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Mapping thủ công cho kiểm soát tường minh: thấy rõ field nào đi đâu, dễ đặt breakpoint.
 * Mapper library ít code lặp nhưng giấu chi tiết và khó đoán khi tên field gần giống nhau.
 * Với vài DTO, viết tay rẻ hơn; nhiều DTO và quy tắc ánh xạ thì library hợp lý hơn.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Mỗi request đi qua nhiều tầng, và mỗi tầng thường chỉ chuyển tiếp dữ liệu.
 * Model phải chuyển đổi qua lại giữa entity, domain và DTO.
 * Chi phí đó đáng khi các tầng cô lập thay đổi thật, không đáng khi chỉ có một cách dùng.
 * SOLUTION-END
 */
