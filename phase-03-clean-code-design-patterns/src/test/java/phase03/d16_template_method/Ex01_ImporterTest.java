package phase03.d16_template_method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ImporterTest {

    private static final String HINT_Q1 =
            "Template Method cố định lịch chạy trong class cha và cho subclass cài từng bước.";
    private static final String HINT_Q2 =
            "Template Method dùng inheritance; Strategy dùng composition.";
    private static final String HINT_Q3 =
            "Class cha gọi ngược lên subclass qua các method abstract.";

    @Test
    @DisplayName("Q1 dự đoán: Template Method giải quyết vấn đề gì")
    void q01_prediction() {
        assertPrediction("Q1_TEMPLATE_PROBLEM",
                Ex01_Importer.Problem.FIXED_ALGORITHM_WITH_VARIABLE_STEPS,
                Ex01_Importer.Q1_TEMPLATE_PROBLEM, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: Template Method khác Strategy thế nào")
    void q02_prediction() {
        assertPrediction("Q2_TEMPLATE_VS_STRATEGY",
                Ex01_Importer.Difference.INHERITANCE_VS_COMPOSITION,
                Ex01_Importer.Q2_TEMPLATE_VS_STRATEGY, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: pattern dựa trên inheritance")
    void q03_prediction() {
        assertPrediction("Q3_USES_INHERITANCE", true, Ex01_Importer.Q3_USES_INHERITANCE, HINT_Q3);
    }

    @Test
    @DisplayName("Q4: importData chạy ba bước theo đúng thứ tự")
    void q04_importDataRunsSteps() {
        assertEquals(java.util.List.of("csv:a", "csv:b"),
                new Ex01_Importer.CsvImporter().importData("a, b"));
    }

    @Test
    @DisplayName("Q4: CSV và JSON dùng chung bộ khung, khác bước")
    void q04_csvAndJsonShareSkeleton() {
        assertEquals(java.util.List.of("csv:a", "csv:b"),
                new Ex01_Importer.CsvImporter().importData("a,b"));
        assertEquals(java.util.List.of("json:a", "json:b"),
                new Ex01_Importer.JsonImporter().importData("[a,b]"));
    }
}
