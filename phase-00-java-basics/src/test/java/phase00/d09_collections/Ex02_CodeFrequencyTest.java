package phase00.d09_collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase00.d09_collections.Ex02_CodeFrequency.CodeStats;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_CodeFrequencyTest {

    @Test
    @DisplayName("Q4 code: list rỗng cho ba cấu trúc rỗng")
    void q04_emptyList() {
        CodeStats stats = Ex02_CodeFrequency.summarize(List.of());
        assertEquals(0, stats.counts().size());
        assertEquals(List.of(), stats.duplicates());
        assertEquals(List.of(), stats.uniques());
    }

    @Test
    @DisplayName("Q4 code: mã trùng và mã duy nhất theo thứ tự gặp đầu tiên")
    void q04_countsDuplicatesInFirstSeenOrder() {
        CodeStats stats = Ex02_CodeFrequency.summarize(List.of("A", "B", "A", "C", "B"));
        assertEquals(2, stats.counts().get("A"));
        assertEquals(2, stats.counts().get("B"));
        assertEquals(1, stats.counts().get("C"));
        assertEquals(List.of("A", "B"), stats.duplicates());
        assertEquals(List.of("C"), stats.uniques());
        assertThrows(UnsupportedOperationException.class, () -> stats.duplicates().add("Z"));
    }
}
