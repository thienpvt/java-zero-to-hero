package phase02.d05_jit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static phase02.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_WarmupTest {

    @Test
    @DisplayName("Q1 dự đoán: JIT có làm cùng việc với javac không")
    void q01_prediction() {
        assertPrediction("Q1_JIT_SAME_AS_JAVAC", false,
                Ex01_Warmup.Q1_JIT_SAME_AS_JAVAC,
                "javac tạo bytecode trước khi chạy. JIT trong JVM dịch method nóng thành mã máy lúc chạy. Hai bước khác nhau, nên hằng này là false.");
    }

    @Test
    @DisplayName("Q5 thí nghiệm: runExperiment trả về báo cáo không rỗng")
    void q05_experimentRuns() {
        String report = Ex01_Warmup.runExperiment(1_000);

        assertFalse(report.isBlank(), "runExperiment phải trả về báo cáo không rỗng.");
    }
}
