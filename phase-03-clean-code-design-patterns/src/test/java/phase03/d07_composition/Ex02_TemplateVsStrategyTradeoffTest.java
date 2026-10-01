package phase03.d07_composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_TemplateVsStrategyTradeoffTest {

    private static final String HINT_Q5 =
            "Khác nhau ở chỗ lịch chạy cố định trong class cha hay do caller chọn lúc runtime.";

    private static final Ex02_TemplateVsStrategyTradeoff.ImportSteps CSV =
            new Ex02_TemplateVsStrategyTradeoff.ImportSteps() {
                @Override
                public String read() {
                    return "csv-row";
                }

                @Override
                public String validate(String raw) {
                    return "valid:" + raw;
                }

                @Override
                public String persist(String valid) {
                    return "saved:" + valid;
                }
            };

    @Test
    @DisplayName("Q5 dự đoán: trade-off giữa hai cách")
    void q05_prediction() {
        assertPrediction("Q5_HYBRID_TRADEOFF",
                Ex02_TemplateVsStrategyTradeoff.Tradeoff.FIXED_SKELETON_VS_SWAPPABLE_STEPS,
                Ex02_TemplateVsStrategyTradeoff.Q5_HYBRID_TRADEOFF, HINT_Q5);
    }

    @Test
    @DisplayName("Q5: hai đường cho cùng kết quả")
    void q05_sameResultBothWays() {
        assertEquals("saved:valid:csv-row", new Ex02_TemplateVsStrategyTradeoff.CsvImporter().run());
        assertEquals("saved:valid:csv-row",
                new Ex02_TemplateVsStrategyTradeoff.StrategyImporter(CSV).importData());
    }

    @Test
    @DisplayName("Q5: strategy đổi được bước lúc runtime")
    void q05_strategySwappableAtRuntime() {
        Ex02_TemplateVsStrategyTradeoff.ImportSteps json =
                new Ex02_TemplateVsStrategyTradeoff.ImportSteps() {
                    @Override
                    public String read() {
                        return "json-doc";
                    }

                    @Override
                    public String validate(String raw) {
                        return "valid:" + raw;
                    }

                    @Override
                    public String persist(String valid) {
                        return "saved:" + valid;
                    }
                };
        assertEquals("saved:valid:json-doc",
                new Ex02_TemplateVsStrategyTradeoff.StrategyImporter(json).importData());
    }
}
