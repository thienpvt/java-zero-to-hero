package phase01.d12_datetime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase01.support.Predictions.assertPrediction;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_DurationPeriodDstTest {

    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
    private static final ZoneId HCM = ZoneId.of("Asia/Ho_Chi_Minh");

    private static final String HINT_Q4 =
            "plus(Period) cộng theo lịch (ngày/tháng/năm) rồi tính lại offset; plus(Duration) "
                    + "cộng thẳng theo giây tuyệt đối. Băng qua mốc DST 2026-03-08 hai cách này ra "
                    + "giờ hiển thị khác nhau.";
    private static final String HINT_Q6 =
            "Khi rơi vào 'gap' DST, ZonedDateTime.ofLocal tự đẩy giờ về sau khi gap kết thúc; khi "
                    + "rơi vào 'overlap', JDK chọn offset sớm hơn (offset trước khi lùi giờ).";

    @Test
    @DisplayName("Q4 dự đoán: cộng 1 ngày kiểu Period so với 24 giờ kiểu Duration quanh mốc DST")
    void q04_prediction() {
        ZonedDateTime start = ZonedDateTime.of(2026, 3, 7, 12, 0, 0, 0, NEW_YORK);

        int periodHour = start.plus(Period.ofDays(1)).getHour();
        int durationHour = start.plus(Duration.ofHours(24)).getHour();

        assertPrediction("Q4_PLUS_PERIOD_1_DAY_LOCAL_HOUR", periodHour,
                Ex02_DurationPeriodDst.Q4_PLUS_PERIOD_1_DAY_LOCAL_HOUR, HINT_Q4);
        assertPrediction("Q4_PLUS_DURATION_24H_LOCAL_HOUR", durationHour,
                Ex02_DurationPeriodDst.Q4_PLUS_DURATION_24H_LOCAL_HOUR, HINT_Q4);
    }

    @Test
    @DisplayName("Q6 dự đoán: giờ bị đẩy khi rơi vào gap, offset được chọn khi rơi vào overlap")
    void q06_prediction() {
        int gapHour = LocalDateTime.of(2026, 3, 8, 2, 30).atZone(NEW_YORK).getHour();
        String overlapOffset = LocalDateTime.of(2026, 11, 1, 1, 30).atZone(NEW_YORK).getOffset().getId();
        int hoursInDstStartDay = Ex02_DurationPeriodDst.hoursInLocalDay(LocalDate.of(2026, 3, 8), NEW_YORK);

        assertPrediction("Q6_GAP_TIME_RESOLVED_HOUR", gapHour,
                Ex02_DurationPeriodDst.Q6_GAP_TIME_RESOLVED_HOUR, HINT_Q6);
        assertPrediction("Q6_OVERLAP_CHOSEN_OFFSET", overlapOffset,
                Ex02_DurationPeriodDst.Q6_OVERLAP_CHOSEN_OFFSET, HINT_Q6);
        assertPrediction("Q6_HOURS_IN_DST_START_DAY", hoursInDstStartDay,
                Ex02_DurationPeriodDst.Q6_HOURS_IN_DST_START_DAY, HINT_Q6);
    }

    @Test
    @DisplayName("Q6 hoursInLocalDay: ngày DST bắt đầu ở New York chỉ có 23 giờ")
    void q06_hoursInLocalDayOnDstStart() {
        assertEquals(23, Ex02_DurationPeriodDst.hoursInLocalDay(LocalDate.of(2026, 3, 8), NEW_YORK),
                "2026-03-08 là ngày DST bắt đầu tại America/New_York, đồng hồ nhảy từ 2:00 lên 3:00.");
    }

    @Test
    @DisplayName("Q6 hoursInLocalDay: ngày DST kết thúc ở New York có 25 giờ")
    void q06_hoursInLocalDayOnDstEnd() {
        assertEquals(25, Ex02_DurationPeriodDst.hoursInLocalDay(LocalDate.of(2026, 11, 1), NEW_YORK),
                "2026-11-01 là ngày DST kết thúc tại America/New_York, đồng hồ lùi từ 2:00 về 1:00.");
    }

    @Test
    @DisplayName("Q6 hoursInLocalDay: ngày thường ở New York vẫn có đúng 24 giờ")
    void q06_hoursInLocalDayOnRegularDay() {
        assertEquals(24, Ex02_DurationPeriodDst.hoursInLocalDay(LocalDate.of(2026, 6, 1), NEW_YORK),
                "Ngày không nằm quanh mốc chuyển DST thì vẫn đủ 24 giờ.");
    }

    @Test
    @DisplayName("Q6 hoursInLocalDay: Asia/Ho_Chi_Minh không áp dụng DST nên luôn 24 giờ")
    void q06_hoursInLocalDayNoDstZone() {
        assertEquals(24, Ex02_DurationPeriodDst.hoursInLocalDay(LocalDate.of(2026, 3, 8), HCM),
                "Asia/Ho_Chi_Minh không áp dụng DST nên mọi ngày đều đúng 24 giờ.");
    }

    @Test
    @DisplayName("Q6 hoursBetween: chỉ 23 giờ tuyệt đối trôi qua đêm chuyển DST tại New York")
    void q06_hoursBetweenAcrossDstStart() {
        ZonedDateTime from = ZonedDateTime.of(2026, 3, 7, 12, 0, 0, 0, NEW_YORK);
        ZonedDateTime to = ZonedDateTime.of(2026, 3, 8, 12, 0, 0, 0, NEW_YORK);

        assertEquals(23, Ex02_DurationPeriodDst.hoursBetween(from, to),
                "Từ 12:00 hôm trước đến 12:00 hôm sau chỉ trôi qua 23 giờ tuyệt đối vì mất 1 giờ do DST.");
    }
}
