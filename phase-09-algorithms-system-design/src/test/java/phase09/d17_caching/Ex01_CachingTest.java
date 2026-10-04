package phase09.d17_caching;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Ex01_CachingTest {
    @Test
    @DisplayName("B1 cung cấp outline trống cho quyết định cache")
    void b1_providesBlankCacheDecisionSections() {
        String template = Ex01_Caching.cacheDecisionTemplate();

        assertTrue(template.contains("Cache key:"));
        assertTrue(template.contains("TTL and freshness contract:"));
        assertTrue(template.contains("Invalidation and stampede:"));
        assertTrue(template.contains("Outage behavior and metrics:"));
    }
}
