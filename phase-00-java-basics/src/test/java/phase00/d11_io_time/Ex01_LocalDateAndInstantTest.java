package phase00.d11_io_time;

import static phase00.support.Predictions.assertPrediction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_LocalDateAndInstantTest {

    private static final String HINT_Q3 =
            "Ctrl+Q trên class LocalDate và Instant. Alt+F8 so hai Instant của cùng giờ đồng hồ ở hai ZoneId.";

    @Test
    @DisplayName("Q3 dự đoán: LocalDate không mang múi giờ; cùng giờ đồng hồ ở hai zone không cùng Instant")
    void q03_prediction() {
        LocalDate date = LocalDate.of(2026, 9, 26);
        assertPrediction("Q3_LOCAL_DATE_TEXT_HAS_ZONE",
                Ex01_LocalDateAndInstant.localDateTextHasZone(date),
                Ex01_LocalDateAndInstant.Q3_LOCAL_DATE_TEXT_HAS_ZONE,
                HINT_Q3);
        LocalDateTime clock = LocalDateTime.of(2026, 9, 26, 8, 0);
        assertPrediction("Q3_SAME_CLOCK_TIME_DIFFERENT_ZONES_SAME_INSTANT",
                Ex01_LocalDateAndInstant.sameInstant(clock),
                Ex01_LocalDateAndInstant.Q3_SAME_CLOCK_TIME_DIFFERENT_ZONES_SAME_INSTANT,
                HINT_Q3);
    }
}
