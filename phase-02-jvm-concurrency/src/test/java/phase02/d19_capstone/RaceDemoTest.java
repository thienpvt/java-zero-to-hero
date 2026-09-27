package phase02.d19_capstone;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class RaceDemoTest {

    @Test
    @DisplayName("B1 thí nghiệm: runExperiment trả báo cáo không rỗng, không kiểm số dư")
    void b01_experimentRuns() {
        String report = RaceDemo.runExperiment(4);
        assertTrue(report != null && !report.isBlank(), "runExperiment phải trả báo cáo không rỗng.");
        assertTrue(report.contains("Account"), "Báo cáo phải có chữ Account.");
    }
}
