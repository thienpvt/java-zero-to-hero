package phase00.d11_io_time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * File và thời gian — Bài 1: LocalDate và Instant
 *
 * Nguồn: 00-java-basics-review.md, mục 11 (File I/O và thời gian cơ bản), câu 3.
 * Cần làm trước: không.
 * Cách làm: điền hai hằng Q3_*, viết ANSWER Q3, chạy Ex01_LocalDateAndInstantTest.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q3 [DỰ ĐOÁN + TỰ TRẢ LỜI] {@code LocalDate} và {@code Instant} biểu diễn những thông tin khác nhau nào?
 *   Bắt đầu   : điền hai hằng trước khi chạy. Không dùng múi giờ mặc định của máy — test truyền
 *               {@code Asia/Ho_Chi_Minh} và {@code UTC}.
 *   Kiểm chứng: Alt+F8 in {@code LocalDate.of(2026, 9, 26)} và hai {@code Instant} trong test.
 *               Ctrl+N mở {@code LocalDate} và {@code Instant}, Ctrl+Q đọc câu đầu của class.
 *   Hoàn thành khi: q03_prediction xanh; ANSWER Q3 phân biệt ngày lịch, điểm trên dòng thời gian,
 *               và vì sao {@code LocalDateTime} không phải timestamp có múi giờ.
 */
public class Ex01_LocalDateAndInstant {

    static final ZoneId HCM = ZoneId.of("Asia/Ho_Chi_Minh");
    static final ZoneId UTC = ZoneId.of("UTC");

    // Q3 — LocalDate.of(2026, 9, 26).toString() có chứa offset (+ hoặc Z) không?
    static final Boolean Q3_LOCAL_DATE_TEXT_HAS_ZONE = false; // SOLUTION-VALUE

    // Q3 — cùng LocalDateTime 2026-09-26T08:00 tại HCM và tại UTC có cùng Instant không?
    static final Boolean Q3_SAME_CLOCK_TIME_DIFFERENT_ZONES_SAME_INSTANT = false; // SOLUTION-VALUE

    static boolean localDateTextHasZone(LocalDate date) {
        String text = date.toString();
        return text.contains("+") || text.contains("Z");
    }

    static boolean sameInstant(LocalDateTime clockTime) {
        return clockTime.atZone(HCM).toInstant().equals(clockTime.atZone(UTC).toInstant());
    }

    /* ANSWER Q3:
     * SOLUTION-BEGIN
     * LocalDate chỉ là một ngày trên lịch (năm-tháng-ngày), không giờ và không múi giờ.
     * Instant là một điểm trên dòng thời gian tuyệt đối, so được giữa các máy mà không cần biết
     * đồng hồ địa phương. LocalDateTime là "giờ trên mặt đồng hồ" chưa gắn múi: cùng 08:00 ở
     * Asia/Ho_Chi_Minh và ở UTC là hai Instant khác nhau. Không dùng LocalDateTime làm timestamp.
     * SOLUTION-END
     */
}
