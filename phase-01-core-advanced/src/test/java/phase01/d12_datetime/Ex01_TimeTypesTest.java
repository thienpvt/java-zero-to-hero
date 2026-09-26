package phase01.d12_datetime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase01.support.Predictions.assertPrediction;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_TimeTypesTest {

    private static final ZoneId HCM = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final ZoneId LONDON = ZoneId.of("Europe/London");
    private static final ZoneId UTC = ZoneId.of("UTC");

    private static final String HINT_Q1 =
            "LocalDateTime không mang timezone; atZone gắn một múi giờ cụ thể rồi mới ra Instant, "
                    + "nên hai múi giờ khác nhau ra hai Instant khác nhau.";
    private static final String HINT_Q3 =
            "ZonedDateTime.equals so cả zone hiển thị lẫn instant; isEqual chỉ so mốc thời gian "
                    + "tuyệt đối, bỏ qua zone.";

    @Test
    @DisplayName("Q1 dự đoán: cùng LocalDateTime, hai múi giờ có ra cùng Instant không")
    void q01_prediction() {
        LocalDateTime local = LocalDateTime.of(2026, 1, 1, 9, 0);
        Instant hcmInstant = local.atZone(HCM).toInstant();
        Instant londonInstant = local.atZone(LONDON).toInstant();

        assertPrediction("Q1_SAME_LOCAL_DATETIME_SAME_INSTANT_IN_TWO_ZONES",
                hcmInstant.equals(londonInstant),
                Ex01_TimeTypes.Q1_SAME_LOCAL_DATETIME_SAME_INSTANT_IN_TWO_ZONES, HINT_Q1);
    }

    @Test
    @DisplayName("Q1 toInstant: 09:00 giờ Asia/Ho_Chi_Minh phải ra đúng Instant UTC tương ứng")
    void q01_toInstantConvertsUsingGivenZone() {
        LocalDateTime local = LocalDateTime.of(2026, 1, 1, 9, 0);

        Instant result = Ex01_TimeTypes.toInstant(local, HCM);

        assertEquals(Instant.parse("2026-01-01T02:00:00Z"), result,
                "09:00 giờ Asia/Ho_Chi_Minh (UTC+7) phải là 02:00 UTC cùng ngày.");
    }

    @Test
    @DisplayName("Q1 localDateOf: cùng một Instant có thể ra ngày khác nhau ở hai múi giờ")
    void q01_localDateOfDiffersByZone() {
        Instant timestamp = Instant.parse("2026-01-01T17:30:00Z");

        assertEquals(LocalDate.of(2026, 1, 2), Ex01_TimeTypes.localDateOf(timestamp, HCM),
                "17:30 UTC là 00:30 ngày kế tiếp ở Asia/Ho_Chi_Minh (UTC+7).");
        assertEquals(LocalDate.of(2026, 1, 1), Ex01_TimeTypes.localDateOf(timestamp, UTC),
                "Theo chính múi giờ UTC thì vẫn còn ngày 2026-01-01.");
    }

    @Test
    @DisplayName("Q3 dự đoán: hai ZonedDateTime cùng thời điểm nhưng khác múi giờ hiển thị")
    void q03_prediction() {
        Instant instant = Instant.parse("2026-01-01T02:00:00Z");
        ZonedDateTime hcm = instant.atZone(HCM);
        ZonedDateTime utc = instant.atZone(UTC);

        assertPrediction("Q3_ZONED_EQUALS", hcm.equals(utc), Ex01_TimeTypes.Q3_ZONED_EQUALS, HINT_Q3);
        assertPrediction("Q3_ZONED_IS_EQUAL", hcm.isEqual(utc), Ex01_TimeTypes.Q3_ZONED_IS_EQUAL, HINT_Q3);
    }
}
