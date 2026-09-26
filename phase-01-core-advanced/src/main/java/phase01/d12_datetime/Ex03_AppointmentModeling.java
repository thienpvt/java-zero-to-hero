package phase01.d12_datetime;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * Date & Time API — Bài 3: Mô hình hoá cuộc hẹn qua nhiều múi giờ
 *
 * Nguồn: 01-java-core-advanced.md, mục 12 (Date & Time API), câu 7.
 * Cần làm trước: Ex02_DurationPeriodDst (gap/overlap khi luật DST đổi).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex03_AppointmentModelingTest bằng
 * nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q7 [CODE + TỰ TRẢ LỜI] Một cuộc hẹn lúc 09:00 theo múi giờ địa phương nên lưu những thông tin
 *     gì để không lệch sau thay đổi DST?
 *   Bắt đầu   : đọc record `Appointment` bên dưới (constructor nén đã cho sẵn, chỉ kiểm tra
 *               null); cài đặt `startInstant()` bằng `localStart.atZone(zone).toInstant()` và
 *               `parse(String, String)` bằng `LocalDateTime.parse(isoLocalDateTime)` +
 *               `ZoneId.of(zoneId)`.
 *   Kiểm chứng: chạy q07_parseComputesInstantInSummerOffset và
 *               q07_parseComputesInstantInWinterOffset, so sánh Instant trả về để thấy cùng
 *               "09:00 giờ địa phương" nhưng ra giờ UTC khác nhau giữa mùa hè (EDT) và mùa đông
 *               (EST); dùng Ctrl+B trên `ZoneId.of` để xem điều kiện ném `DateTimeException`.
 *   Code      : cài đặt `startInstant()` và `parse(String, String)` như trên.
 *   Hoàn thành khi: các test q07_* xanh; viết xong khối ANSWER Q7 giải thích tại sao phải lưu
 *               cả giờ địa phương lẫn zone (không chỉ offset cố định hay Instant đã tính sẵn)
 *               mới giữ đúng ý định ban đầu của người đặt hẹn khi luật DST đổi.
 */
public class Ex03_AppointmentModeling {

    /**
     * Một cuộc hẹn lưu giờ địa phương {@code localStart} kèm {@code zone} nơi diễn ra, thay vì
     * chỉ lưu offset cố định hoặc {@code Instant} đã tính sẵn — để tính đúng mốc tuyệt đối cả
     * khi quy tắc DST của {@code zone} thay đổi sau này.
     */
    record Appointment(LocalDateTime localStart, ZoneId zone) {

        Appointment {
            Objects.requireNonNull(localStart, "localStart không được null");
            Objects.requireNonNull(zone, "zone không được null");
        }

        /**
         * Mốc thời gian tuyệt đối của cuộc hẹn, tính theo quy tắc DST hiện hành của
         * {@link #zone} tại giờ địa phương {@link #localStart}.
         *
         * @return mốc thời gian tuyệt đối tương ứng
         */
        Instant startInstant() {
            // SOLUTION-BEGIN throw Q7
            return localStart.atZone(zone).toInstant();
            // SOLUTION-END
        }
    }

    /**
     * Phân tích {@code isoLocalDateTime} (định dạng ISO-8601 không mang múi giờ, ví dụ
     * {@code "2026-07-01T09:00"}) và {@code zoneId} (ví dụ {@code "America/New_York"}) thành
     * một {@link Appointment}.
     *
     * @param isoLocalDateTime giờ hẹn địa phương dạng ISO-8601, không {@code null}
     * @param zoneId mã múi giờ (ví dụ {@code "America/New_York"}), không {@code null}
     * @return cuộc hẹn tương ứng
     * @throws DateTimeException nếu {@code zoneId} không phải một múi giờ hợp lệ
     */
    static Appointment parse(String isoLocalDateTime, String zoneId) {
        // SOLUTION-BEGIN throw Q7
        LocalDateTime localStart = LocalDateTime.parse(isoLocalDateTime);
        ZoneId zone = ZoneId.of(zoneId);
        return new Appointment(localStart, zone);
        // SOLUTION-END
    }
}

/* ANSWER Q7:
 * SOLUTION-BEGIN
 * Nên lưu giờ địa phương (LocalDateTime, ví dụ "09:00") cùng với zone ID (ví dụ
 * "America/New_York"), không chỉ lưu offset cố định (ví dụ "-04:00") hay Instant đã tính sẵn.
 * Chỉ tính Instant khi cần dùng (hiển thị, so sánh, sắp lịch), bằng cách tra quy tắc DST hiện
 * hành của chính zone đó tại thời điểm tính.
 * Lý do: luật DST của một zone có thể thay đổi qua các năm (chính phủ đổi luật DST), nên nếu
 * chỉ lưu offset/Instant tại thời điểm tạo cuộc hẹn, khi luật đổi thì "09:00 giờ địa phương"
 * ban đầu sẽ lệch giờ hiển thị thực tế — mất đúng ý định ban đầu của người đặt hẹn. Lưu cả zone
 * giúp hệ thống luôn tính lại đúng theo luật hiện hành, kể cả khi luật đổi sau khi đã đặt hẹn.
 * SOLUTION-END
 */
