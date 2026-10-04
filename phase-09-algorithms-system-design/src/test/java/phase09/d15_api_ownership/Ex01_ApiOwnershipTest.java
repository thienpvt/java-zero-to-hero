package phase09.d15_api_ownership;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Ex01_ApiOwnershipTest {
    @Test
    @DisplayName("B1 cung cấp outline trống cho luồng order API")
    void b1_providesBlankOrderFlowSections() {
        String template = Ex01_ApiOwnership.orderFlowTemplate();

        assertTrue(template.contains("Authorization:"));
        assertTrue(template.contains("Transaction boundary:"));
        assertTrue(template.contains("Idempotency key and duplicate request:"));
        assertTrue(template.contains("Stable error contract:"));
    }
}
