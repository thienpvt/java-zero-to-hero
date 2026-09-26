package phase01.d02_arraylist;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_InsertRemoveCostTest {

    @Test
    @DisplayName("Q5/Q10 thí nghiệm: runExperiment chạy và có báo cáo cho ArrayList/LinkedList")
    void q05_experimentRuns() {
        String report = Ex02_InsertRemoveCost.runExperiment(1_000);

        assertTrue(report.contains("ArrayList"), "Báo cáo phải có số liệu đo cho ArrayList.");
        assertTrue(report.contains("LinkedList"), "Báo cáo phải có số liệu đo cho LinkedList.");
    }
}
