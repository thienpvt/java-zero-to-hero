package phase01.d12_datetime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Date & Time API — Bài 1: LocalDateTime, Instant, ZonedDateTime
 *
 * Nguồn: 01-java-core-advanced.md, mục 12 (Date & Time API), câu 1, 3, 2.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_TimeTypesTest bằng nút ▶
 * cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q1 [DỰ ĐOÁN] `LocalDateTime` có timezone không?
 *   Bắt đầu   : điền hằng số Q1_SAME_LOCAL_DATETIME_SAME_INSTANT_IN_TWO_ZONES (thay null); tạo
 *               `LocalDateTime.of(2026, 1, 1, 9, 0)`, gắn `atZone(ZoneId.of("Asia/Ho_Chi_Minh"))`
 *               và `atZone(ZoneId.of("Europe/London"))`, so `toInstant()` của hai kết quả.
 *   Kiểm chứng: Alt+F8 (Evaluate Expression) trên hai `toInstant()` khi debug test q01_prediction,
 *               hoặc Ctrl+N → `LocalDateTime` → Ctrl+Q để đọc Javadoc mô tả nó "does not store
 *               or represent a time-zone".
 *   Code      : cài đặt `toInstant(LocalDateTime, ZoneId)` và `localDateOf(Instant, ZoneId)`
 *               bằng `atZone`/`toInstant`/`toLocalDate`.
 *   Hoàn thành khi: các test q01_* xanh; giải thích được vì sao cùng một `LocalDateTime` lại ra
 *               hai `Instant` khác nhau tuỳ múi giờ gắn vào.
 *
 * Q3 [DỰ ĐOÁN] `Instant` và `ZonedDateTime` khác nhau thế nào?
 *   Bắt đầu   : điền Q3_ZONED_EQUALS, Q3_ZONED_IS_EQUAL; tạo
 *               `Instant.parse("2026-01-01T02:00:00Z")`, gắn `atZone(ZoneId.of("Asia/Ho_Chi_Minh"))`
 *               và `atZone(ZoneId.of("UTC"))`, so `equals()` rồi `isEqual()` giữa hai kết quả.
 *   Kiểm chứng: F7 Step Into vào `ZonedDateTime.equals` (Ctrl+N → ZonedDateTime → Ctrl+F12 →
 *               equals) để thấy nó so cả zone lẫn instant, còn `isEqual` chỉ so mốc thời gian.
 *   Hoàn thành khi: q03_prediction xanh; giải thích được vì sao hai `ZonedDateTime` cùng một
 *               thời điểm tuyệt đối nhưng khác múi giờ hiển thị lại có `equals` == false.
 *
 * Q2 [TỰ TRẢ LỜI] Khi lưu timestamp vào database nên cân nhắc `Instant` vì sao?
 *   Bắt đầu   : làm Q1 và Q3 trước, quan sát lại vì sao `LocalDateTime` không xác định duy nhất
 *               một thời điểm tuyệt đối trên trục thời gian.
 *   Tra cứu   : Ctrl+N → `Instant` → Ctrl+Q, đọc đoạn Javadoc mô tả nó là "instantaneous point
 *               on the time-line", dùng làm mốc tuyệt đối, không phụ thuộc múi giờ đọc dữ liệu.
 *   Hoàn thành khi: viết xong khối ANSWER Q2, có nêu ít nhất một rủi ro khi lưu `LocalDateTime`
 *               (không kèm zone) thay vì `Instant`.
 */
public class Ex01_TimeTypes {

    // Q1 — kịch bản: LocalDateTime.of(2026, 1, 1, 9, 0) gắn Asia/Ho_Chi_Minh và Europe/London
    static final Boolean Q1_SAME_LOCAL_DATETIME_SAME_INSTANT_IN_TWO_ZONES = null;

    // Q3 — kịch bản: Instant.parse("2026-01-01T02:00:00Z") gắn Asia/Ho_Chi_Minh và UTC
    static final Boolean Q3_ZONED_EQUALS = null;
    static final Boolean Q3_ZONED_IS_EQUAL = null;

    /**
     * Chuyển giờ địa phương {@code local} (không mang thông tin múi giờ) sang mốc thời gian
     * tuyệt đối, dựa theo quy tắc của múi giờ {@code zone}.
     *
     * @param local giờ địa phương cần chuyển, không {@code null}
     * @param zone múi giờ dùng để xác định offset tại thời điểm {@code local}, không {@code null}
     * @return mốc thời gian tuyệt đối tương ứng
     */
    static Instant toInstant(LocalDateTime local, ZoneId zone) {
        throw new UnsupportedOperationException("TODO Q1");
    }

    /**
     * Trả về ngày theo lịch địa phương của mốc thời gian {@code timestamp} khi quan sát tại
     * múi giờ {@code zone}.
     *
     * @param timestamp mốc thời gian tuyệt đối, không {@code null}
     * @param zone múi giờ dùng để quy đổi ra ngày địa phương, không {@code null}
     * @return ngày địa phương tương ứng, tuỳ zone mà có thể lệch ngày so với UTC
     */
    static LocalDate localDateOf(Instant timestamp, ZoneId zone) {
        throw new UnsupportedOperationException("TODO Q1");
    }
}

/* ANSWER Q2:
 *
 */
