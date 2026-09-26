package phase01.d17_capstone;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Bài tích hợp — Order (dữ liệu đơn hàng đọc từ CSV)
 *
 * Nguồn: 01-java-core-advanced.md, mục "Bài thực hành tích hợp", bước 1.
 * Cần làm trước: không.
 * Cách làm: đọc Javadoc bước B1 bên dưới. Class này cho sẵn toàn bộ (không có TODO);
 * mục tiêu là hiểu lựa chọn kiểu dữ liệu trước khi sang {@code CsvOrderParser} (B2).
 *
 * ─────────────────────────────────────────────────────────────────────
 * B1 [TỰ TRẢ LỜI] Dùng `record` cho dữ liệu đầu vào; phân biệt timestamp tuyệt đối và ngày theo
 *     múi giờ của báo cáo.
 *   Bắt đầu   : mở record {@code Order} và enum {@code Status} bên dưới (Ctrl+N → Order);
 *               toàn bộ đã cho sẵn, không cần sửa gì ở bước này.
 *   Kiểm chứng: đặt breakpoint ngay dòng {@code amount.signum() < 0} trong compact
 *               constructor, chạy {@code CsvOrderParserTest} bằng Debug (Shift+F9) để
 *               xem giá trị các field khi một dòng CSV được parse thành {@code Order};
 *               dùng F7 (Step Into) vào {@code Instant.parse(...)} ở CsvOrderParser để
 *               thấy chuỗi ISO-8601 được quy đổi thành mốc UTC.
 *   Code      : không có — record, enum và compact constructor đã hoàn chỉnh.
 *   Hoàn thành khi: giải thích được vì sao {@code createdAt} là {@code Instant} (một điểm
 *               tuyệt đối trên dòng thời gian UTC) chứ không phải {@code LocalDateTime}
 *               (chỉ là "giờ trên lịch", không gắn múi giờ); viết vào khối {@code ANSWER B1}
 *               ở cuối file.
 */
public record Order(String id, String customerId, Instant createdAt, BigDecimal amount, Status status) {

    /**
     * @throws NullPointerException     nếu bất kỳ field nào là {@code null}
     * @throws IllegalArgumentException nếu {@code amount} âm
     */
    public Order {
        Objects.requireNonNull(id, "id không được null");
        Objects.requireNonNull(customerId, "customerId không được null");
        Objects.requireNonNull(createdAt, "createdAt không được null");
        Objects.requireNonNull(amount, "amount không được null");
        Objects.requireNonNull(status, "status không được null");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount không được âm: " + amount);
        }
    }

    /** Trạng thái của một đơn hàng. */
    public enum Status {
        NEW,
        PAID,
        CANCELLED
    }
}

/* ANSWER B1:
 *
 */
