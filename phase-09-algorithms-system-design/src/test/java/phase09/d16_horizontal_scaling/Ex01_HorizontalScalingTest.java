package phase09.d16_horizontal_scaling;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Ex01_HorizontalScalingTest {
    @Test
    @DisplayName("B1 cung cấp outline trống cho failure của hai instance")
    void b1_providesBlankTwoInstanceSections() {
        String template = Ex01_HorizontalScaling.twoInstanceFailureTemplate();

        assertTrue(template.contains("Instance A failure:"));
        assertTrue(template.contains("Session state:"));
        assertTrue(template.contains("Database pool/quota:"));
        assertTrue(template.contains("First bottleneck to measure:"));
    }
}
