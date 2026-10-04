package phase09.d13_requirements_slo;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Ex01_RequirementsSloTest {
    @Test
    @DisplayName("B1 cung cấp brief trống để người học tự ghi requirements")
    void b1_providesBlankBriefSections() {
        String template = Ex01_RequirementsSlo.requirementsChecklist();

        assertTrue(template.contains("Actors:"));
        assertTrue(template.contains("Functional requirements:"));
        assertTrue(template.contains("SLO/SLI:"));
        assertTrue(template.contains("Unknowns:"));
    }
}
