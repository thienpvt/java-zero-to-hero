package phase03.d09_layers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_DependencyDirectionTest {

    private static final String HINT_Q4 =
            "Controller biết HTTP nên không dùng lại được cho job nền và khó test.";
    private static final String HINT_Q5 =
            "Layer chỉ đáng thêm khi cô lập một thay đổi thật.";

    @Test
    @DisplayName("Q4 dự đoán: business logic ở controller hay domain")
    void q04_prediction() {
        assertPrediction("Q4_BUSINESS_IN_CONTROLLER", false,
                Ex02_DependencyDirection.Q4_BUSINESS_IN_CONTROLLER, HINT_Q4);
    }

    @Test
    @DisplayName("Q5 dự đoán: mọi project cần đủ năm layer")
    void q05_prediction() {
        assertPrediction("Q5_ALWAYS_FIVE_LAYERS", false,
                Ex02_DependencyDirection.Q5_ALWAYS_FIVE_LAYERS, HINT_Q5);
    }

    @Test
    @DisplayName("Q5: policy nghiệp vụ thuần chạy không cần hạ tầng")
    void q05_domainImportsNothingOutside() {
        assertEquals(900, new Ex02_DependencyDirection.OrderPolicy().applyVipDiscount(1_000));
        assertTrue(Ex02_DependencyDirection.DomainBoundary.isAllowed("java.util.List"));
        assertEquals(4, Ex02_DependencyDirection.DomainBoundary.domainImports().size());
    }

    @Test
    @DisplayName("Q6: domain chặn prefix hạ tầng")
    void q06_domainImportsNothingOutside() {
        assertTrue(Ex02_DependencyDirection.DomainBoundary.forbiddenPrefixes()
                .stream().noneMatch(Ex02_DependencyDirection.DomainBoundary::isAllowed),
                "Không prefix hạ tầng nào được nằm trong danh sách cho phép của domain.");
    }
}
