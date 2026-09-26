package phase00.d04_text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase00.d04_text.Ex02_StatusCount.Status;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_StatusCountTest {

    @Test
    @DisplayName("Q4 code: NEW,DONE,NEW đếm đúng từng trạng thái")
    void q04_countsKnownStatuses() {
        Map<Status, Integer> counts = Ex02_StatusCount.count("NEW,DONE,NEW");
        assertEquals(2, counts.get(Status.NEW));
        assertEquals(1, counts.get(Status.DONE));
    }

    @Test
    @DisplayName("Q4 code: chuỗi trống cho map rỗng; null và trạng thái lạ bị từ chối")
    void q04_rejectsNullBlankAndUnknown() {
        assertEquals(Map.of(), Ex02_StatusCount.count("   "));
        assertThrows(IllegalArgumentException.class, () -> Ex02_StatusCount.count(null));
        IllegalArgumentException unknown = assertThrows(IllegalArgumentException.class,
                () -> Ex02_StatusCount.count("NEW,HOLD"));
        assertTrue(unknown.getMessage().contains("HOLD"));
        assertThrows(IllegalArgumentException.class, () -> Ex02_StatusCount.count("NEW,"));
    }
}
