package phase03.d07_composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ReuseWithoutIsATest {

    private static final String HINT_Q1 =
            "is-a thật với cùng ngữ nghĩa mới dùng inheritance.";
    private static final String HINT_Q2 =
            "Chỉ muốn dùng lại vài dòng code thì đó là has-a, không phải is-a.";
    private static final String HINT_Q4 =
            "Hành vi rải ở nhiều tầng làm đọc một tầng không đủ hiểu.";

    @Test
    @DisplayName("Q1 dự đoán: inheritance phù hợp khi nào")
    void q01_prediction() {
        assertPrediction("Q1_INHERITANCE_FIT", Ex01_ReuseWithoutIsA.Fit.TRUELY_IS_A,
                Ex01_ReuseWithoutIsA.Q1_INHERITANCE_FIT, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: composition tốt hơn khi nào")
    void q02_prediction() {
        assertPrediction("Q2_COMPOSITION_FIT", Ex01_ReuseWithoutIsA.Fit.REUSE_CODE,
                Ex01_ReuseWithoutIsA.Q2_COMPOSITION_FIT, HINT_Q2);
    }

    @Test
    @DisplayName("Q2: OrderService chuyển tiếp cho Logger")
    void q02_orderServiceDelegates() {
        Ex01_ReuseWithoutIsA.OrderService service =
                new Ex01_ReuseWithoutIsA.OrderService(new Ex01_ReuseWithoutIsA.ConsoleLogger());
        assertEquals("log:đặt o-1", service.place("o-1"));
    }

    @Test
    @DisplayName("Q4 dự đoán: hierarchy sâu khó bảo trì vì sao")
    void q04_prediction() {
        assertPrediction("Q4_DEEP_HIERARCHY_PAIN",
                Ex01_ReuseWithoutIsA.Pain.BEHAVIOR_SPREAD_ACROSS_LEVELS,
                Ex01_ReuseWithoutIsA.Q4_DEEP_HIERARCHY_PAIN, HINT_Q4);
    }

    @Test
    @DisplayName("Q5: run gọi ba bước theo đúng thứ tự")
    void q05_runCallsStepsInOrder() {
        Ex01_ReuseWithoutIsA.AbstractImporter importer =
                new Ex01_ReuseWithoutIsA.AbstractImporter() {
                    @Override
                    protected String read() {
                        return "raw";
                    }

                    @Override
                    protected String validate(String raw) {
                        return "v:" + raw;
                    }

                    @Override
                    protected String persist(String valid) {
                        return "p:" + valid;
                    }
                };
        assertEquals("p:v:raw", importer.run());
        assertEquals("log:đặt o-1", new Ex01_ReuseWithoutIsA.OrderService(
                new Ex01_ReuseWithoutIsA.ConsoleLogger()).place("o-1"));
    }
}
