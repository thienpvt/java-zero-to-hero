package phase01.d12_datetime;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Date & Time API — Bài 2: Duration, Period và lỗi DST
 *
 * Nguồn: 01-java-core-advanced.md, mục 12 (Date & Time API), câu 4, 6, 5.
 * Cần làm trước: Ex01_TimeTypes (Instant, ZonedDateTime, atZone).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_DurationPeriodDstTest bằng
 * nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q4 [DỰ ĐOÁN] {@code Duration} và {@code Period} khác nhau thế nào?
 *   Bắt đầu   : điền Q4_PLUS_PERIOD_1_DAY_LOCAL_HOUR, Q4_PLUS_DURATION_24H_LOCAL_HOUR; tạo
 *               {@code ZonedDateTime.of(2026, 3, 7, 12, 0, 0, 0, ZoneId.of("America/New_York"))},
 *               cộng {@code Period.ofDays(1)} rồi cộng {@code Duration.ofHours(24)}, đọc {@code getHour()} của
 *               mỗi kết quả.
 *   Kiểm chứng: F7 Step Into vào {@code ZonedDateTime.plus(Period)} (Ctrl+N → ZonedDateTime →
 *               Ctrl+F12 → plus) để thấy nó cộng theo lịch (ngày/tháng/năm) rồi tính lại offset,
 *               còn {@code plus(Duration)} cộng thẳng theo giây tuyệt đối rồi mới quy đổi lại giờ hiển thị.
 *   Hoàn thành khi: q04_prediction xanh; giải thích được vì sao cộng đúng 24 giờ (Duration) lại
 *               ra giờ hiển thị khác cộng đúng 1 ngày (Period) khi băng qua mốc DST 2026-03-08.
 * <p>
 * Q6 [DỰ ĐOÁN + CODE] DST có thể gây những bug nào?
 *   Bắt đầu   : điền Q6_GAP_TIME_RESOLVED_HOUR, Q6_OVERLAP_CHOSEN_OFFSET,
 *               Q6_HOURS_IN_DST_START_DAY; tạo {@code LocalDateTime.of(2026, 3, 8, 2, 30)} và
 *               {@code LocalDateTime.of(2026, 11, 1, 1, 30)}, gắn
 *               {@code atZone(ZoneId.of("America/New_York"))}, đọc {@code getHour()} và
 *               {@code getOffset().getId()} của từng kết quả.
 *   Kiểm chứng: đặt breakpoint trong {@code ZonedDateTime.ofLocal} (Ctrl+N → ZonedDateTime →
 *               Ctrl+F12 → ofLocal), Debug q06_prediction, dùng F7 để thấy cách JDK chọn offset
 *               khi giờ địa phương rơi vào "khoảng trống" (gap, đồng hồ nhảy tới) và "chồng lấp"
 *               (overlap, đồng hồ lùi lại và có hai offset hợp lệ).
 *   Code      : cài đặt {@code hoursInLocalDay(LocalDate, ZoneId)} bằng {@code atStartOfDay} của ngày đó và
 *               ngày kế tiếp rồi lấy {@code Duration.between}; `hoursBetween(ZonedDateTime,
 *               ZonedDateTime){@code  cũng bằng }Duration.between`.
 *   Hoàn thành khi: các test q06_* xanh; giải thích được vì sao một "ngày" ở múi giờ có DST có
 *               thể không phải đúng 24 giờ.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Vì sao xử lý timezone bằng cộng/trừ offset thủ công dễ lỗi?
 *   Bắt đầu   : làm Q6 trước, dùng chính kết quả gap/overlap/hoursInLocalDay quan sát được để
 *               lập luận.
 *   Tra cứu   : Ctrl+N → {@code ZoneId} → Ctrl+Q, đọc đoạn Javadoc mô tả mỗi zone có "rules" quyết
 *               định offset thay đổi theo thời gian (bao gồm cả khi luật DST của một nước đổi).
 *   Hoàn thành khi: viết xong khối ANSWER Q5, có liên hệ tới Q6 (gap/overlap) như ví dụ cụ thể.
 */
public class Ex02_DurationPeriodDst {

    // Q4 — kịch bản: bắt đầu 2026-03-07T12:00 America/New_York (trước mốc DST bắt đầu 2026-03-08)
    static final Integer Q4_PLUS_PERIOD_1_DAY_LOCAL_HOUR = null;
    static final Integer Q4_PLUS_DURATION_24H_LOCAL_HOUR = null;

    // Q6 — kịch bản: DST tại America/New_York năm 2026 (bắt đầu 2026-03-08, kết thúc 2026-11-01)
    static final Integer Q6_GAP_TIME_RESOLVED_HOUR = null;
    static final String Q6_OVERLAP_CHOSEN_OFFSET = null;
    static final Integer Q6_HOURS_IN_DST_START_DAY = null;

    /**
     * Số giờ thực tế trôi qua trong ngày {@code date} tại múi giờ {@code zone}, tính bằng
     * khoảng cách tuyệt đối giữa nửa đêm đầu ngày ({@code date.atStartOfDay(zone)}) và nửa đêm
     * ngày kế tiếp. Ngày DST "bắt đầu" (đồng hồ nhảy tới) và ngày DST "kết thúc" (đồng hồ
     * lùi lại) có thể khác 24 giờ — tự đo rồi điền hằng Q6_HOURS_IN_DST_START_DAY.
     *
     * @param date ngày địa phương cần tính, không {@code null}
     * @param zone múi giờ áp dụng, không {@code null}
     * @return số giờ tuyệt đối trôi qua trong ngày đó
     */
    static int hoursInLocalDay(LocalDate date, ZoneId zone) {
        throw new UnsupportedOperationException("TODO Q6");
    }

    /**
     * Số giờ tuyệt đối giữa hai mốc {@code from} và {@code to} (theo instant thực tế, không
     * phải hiệu số giờ đọc trên mặt đồng hồ địa phương).
     *
     * @param from mốc bắt đầu, không {@code null}
     * @param to mốc kết thúc, không {@code null}
     * @return số giờ tuyệt đối giữa hai mốc; có thể âm nếu {@code to} trước {@code from}
     */
    static long hoursBetween(ZonedDateTime from, ZonedDateTime to) {
        throw new UnsupportedOperationException("TODO Q6");
    }
}

/* ANSWER Q5:
 *
 */
