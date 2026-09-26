package phase01.d12_datetime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d12_datetime.Ex03_AppointmentModeling.Appointment;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex03_AppointmentModelingTest {

    @Test
    @DisplayName("Q7 parse: giờ hẹn mùa hè (EDT, UTC-4) tính đúng Instant")
    void q07_parseComputesInstantInSummerOffset() {
        Appointment appointment = Ex03_AppointmentModeling.parse("2026-07-01T09:00", "America/New_York");

        assertEquals(Instant.parse("2026-07-01T13:00:00Z"), appointment.startInstant(),
                "Tháng 7 New York theo giờ EDT (UTC-4): 09:00 giờ địa phương là 13:00 UTC.");
    }

    @Test
    @DisplayName("Q7 parse: giờ hẹn mùa đông (EST, UTC-5) tính ra Instant khác mùa hè")
    void q07_parseComputesInstantInWinterOffset() {
        Appointment appointment = Ex03_AppointmentModeling.parse("2026-12-01T09:00", "America/New_York");

        assertEquals(Instant.parse("2026-12-01T14:00:00Z"), appointment.startInstant(),
                "Tháng 12 New York theo giờ EST (UTC-5): 09:00 giờ địa phương là 14:00 UTC.");
    }

    @Test
    @DisplayName("Q7 parse: zone ID không hợp lệ phải báo lỗi DateTimeException")
    void q07_parseRejectsInvalidZoneId() {
        assertThrows(DateTimeException.class,
                () -> Ex03_AppointmentModeling.parse("2026-07-01T09:00", "Not/AZone"),
                "Zone ID không tồn tại phải làm ZoneId.of ném DateTimeException.");
    }

    @Test
    @DisplayName("Q7 startInstant: tính đúng theo giờ địa phương và zone đã lưu trong record")
    void q07_startInstantReflectsStoredZone() {
        Appointment appointment = new Appointment(LocalDateTime.of(2026, 7, 1, 9, 0),
                ZoneId.of("America/New_York"));

        assertEquals(Instant.parse("2026-07-01T13:00:00Z"), appointment.startInstant(),
                "startInstant() phải tính theo đúng localStart và zone đã lưu trong record.");
    }
}
