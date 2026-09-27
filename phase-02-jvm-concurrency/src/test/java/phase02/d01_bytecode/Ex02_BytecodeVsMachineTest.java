package phase02.d01_bytecode;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase02.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_BytecodeVsMachineTest {

    private static final String HINT_Q5 =
            "JIT dịch bytecode của đoạn code nóng thành machine code lúc chạy. Hằng này là true.";

    @Test
    @DisplayName("Q5 thí nghiệm: runExperiment trả báo cáo không rỗng")
    void q05_experimentRuns() {
        String report = Ex02_BytecodeVsMachine.runExperiment(32);

        assertNotNull(report, "runExperiment phải trả về chuỗi không rỗng.");
        assertFalse(report.isBlank(), "runExperiment phải trả về chuỗi không rỗng.");
        assertTrue(report.contains("interpreter-or-jit"),
                "runExperiment phải ghi nhận interpreter-or-jit, không ghi số đo thời gian.");
    }

    @Test
    @DisplayName("Q5 dự đoán: JIT có biên dịch bytecode nóng không")
    void q05_prediction() {
        assertPrediction("Q5_JIT_COMPILES_HOT_BYTECODE",
                true,
                Ex02_BytecodeVsMachine.Q5_JIT_COMPILES_HOT_BYTECODE,
                HINT_Q5);
    }
}
