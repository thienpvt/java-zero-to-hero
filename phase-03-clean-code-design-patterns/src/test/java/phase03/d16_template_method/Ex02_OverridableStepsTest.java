package phase03.d16_template_method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_OverridableStepsTest {

    private static final String HINT_Q4 =
            "Mỗi hook thêm một trạng thái subclass phải hiểu và một chỗ class cha không kiểm soát được.";
    private static final String HINT_Q5 =
            "Strategy đổi được lịch chạy và tập bước lúc runtime; Template Method chỉ đổi nội dung bước.";

    static final class RecordingImporter extends Ex02_OverridableSteps.HookImporter {

        final List<String> hooks = new ArrayList<>();

        @Override
        protected List<String> read(String raw) {
            return List.of(raw.split(","));
        }

        @Override
        protected String validate(String row) {
            return row.strip();
        }

        @Override
        protected String persist(String valid) {
            return "csv:" + valid;
        }

        @Override
        protected void afterPersist(List<String> persisted) {
            hooks.addAll(persisted);
        }
    }

    @Test
    @DisplayName("Q4 dự đoán: quá nhiều hook gây vấn đề")
    void q04_prediction() {
        assertPrediction("Q4_TOO_MANY_HOOKS", true,
                Ex02_OverridableSteps.Q4_TOO_MANY_HOOKS, HINT_Q4);
    }

    @Test
    @DisplayName("Q4: hook chạy sau persist")
    void q04_hookRunsAfterPersist() {
        RecordingImporter importer = new RecordingImporter();
        assertEquals(List.of("csv:a", "csv:b"), importer.importData("a,b"));
        assertEquals(List.of("csv:a", "csv:b"), importer.hooks);
    }

    @Test
    @DisplayName("Q5 dự đoán: Strategy linh hoạt hơn khi nào")
    void q05_prediction() {
        assertPrediction("Q5_STRATEGY_MORE_FLEXIBLE",
                Ex02_OverridableSteps.When.WHEN_ORDER_OR_STEP_SET_CHANGES_AT_RUNTIME,
                Ex02_OverridableSteps.Q5_STRATEGY_MORE_FLEXIBLE, HINT_Q5);
    }
}
