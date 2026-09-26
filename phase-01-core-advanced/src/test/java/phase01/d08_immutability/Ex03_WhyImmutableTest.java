package phase01.d08_immutability;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex03_WhyImmutableTest {

    @Test
    @DisplayName("Q6 thí nghiệm: runExperiment trả về báo cáo không rỗng")
    void q06_experimentRuns() {
        String report = Ex03_WhyImmutable.runExperiment(2_000);

        assertFalse(report.isBlank(), "Báo cáo thí nghiệm không được rỗng.");
        assertTrue(report.contains("n=2000"), "Báo cáo phải nêu rõ n đã chạy.");
    }
}
