package phase01.d06_hashset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase01.support.Predictions.assertPrediction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d06_hashset.Ex01_Uniqueness.Tag;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_UniquenessTest {

    private static final String HINT_Q1 =
            "Ctrl+B vào HashSet.add(E) để đọc map.put(e, PRESENT) == null.";

    @Test
    @DisplayName("Q1 dự đoán: add phần tử trùng và class của backing map")
    void q01_prediction() {
        Set<String> set = new HashSet<>();
        set.add("a");
        boolean secondAddReturns = set.add("a");

        assertPrediction("Q1_ADD_DUPLICATE_RETURNS",
                secondAddReturns, Ex01_Uniqueness.Q1_ADD_DUPLICATE_RETURNS, HINT_Q1);
        assertPrediction("Q1_BACKING_MAP_CLASS",
                "HashMap", Ex01_Uniqueness.Q1_BACKING_MAP_CLASS, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 duplicates: giữ lại phần tử trùng theo thứ tự lần xuất hiện thứ hai")
    void q02_duplicates_ordersBySecondOccurrence() {
        List<String> input = List.of("a", "b", "a", "c", "b", "a");

        assertEquals(List.of("a", "b"), Ex01_Uniqueness.duplicates(input));
    }

    @Test
    @DisplayName("Q2 duplicates: danh sách rỗng trả về danh sách rỗng")
    void q02_duplicates_emptyListReturnsEmptyList() {
        assertEquals(List.of(), Ex01_Uniqueness.duplicates(List.of()));
    }

    @Test
    @DisplayName("Q2 thí nghiệm: so lookup ArrayList và HashSet")
    void q02_experimentRuns() {
        String report = Ex01_Uniqueness.runExperiment(2_000);

        assertFalse(report.isBlank(), "runExperiment phải trả về báo cáo không rỗng.");
    }

    @Test
    @DisplayName("Q2 hasDuplicates: phát hiện đúng có/không có phần tử trùng")
    void q02_hasDuplicates_detectsPresenceOfDuplicates() {
        assertFalse(Ex01_Uniqueness.hasDuplicates(List.of(1, 2, 3)));
        assertTrue(Ex01_Uniqueness.hasDuplicates(List.of(1, 2, 1)));
    }

    @Test
    @DisplayName("Q4 dự đoán: remove sau khi mutate field tham gia hashCode")
    void q04_prediction() {
        Set<Tag> set = new HashSet<>();
        Tag tag = new Tag("a");
        set.add(tag);
        tag.name = "b";

        boolean removed = set.remove(tag);

        assertPrediction("Q4_REMOVE_AFTER_MUTATION_WORKS",
                removed, Ex01_Uniqueness.Q4_REMOVE_AFTER_MUTATION_WORKS,
                "hashCode() mới trỏ sang bucket khác; đặt breakpoint trong HashMap.removeNode.");
    }
}
