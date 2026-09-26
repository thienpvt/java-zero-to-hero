package phase01.d03_linkedlist;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_MemoryAndLocalityTest {

    @Test
    @DisplayName("Q3/Q5 thí nghiệm: runExperiment sinh báo cáo memory và thời gian duyệt")
    void q03_experimentRuns() {
        String report = Ex02_MemoryAndLocality.runExperiment(10_000);

        assertFalse(report.isBlank(), "Báo cáo thí nghiệm không được rỗng.");
    }
}
