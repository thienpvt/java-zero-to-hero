package phase01.d07_comparable_comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase01.support.Predictions.assertPrediction;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d07_comparable_comparator.Ex03_InconsistentWithEquals.Person;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex03_InconsistentWithEqualsTest {

    private static final String HINT_Q4 =
            "TreeSet dùng Comparator.compare() để xét trùng lặp; age bằng nhau bị coi là trùng, entry sau bị bỏ.";

    @Test
    @DisplayName("Q4 dự đoán: TreeSet với Comparator chỉ so age")
    void q04_prediction_treeSetWithAgeOnlyComparatorDropsElements() {
        Person an = new Person("An", 30);
        Person binh = new Person("Bình", 30);
        Person chi = new Person("Chi", 25);

        Set<Person> treeSet = new TreeSet<>(Comparator.comparingInt(Person::age));
        treeSet.add(an);
        treeSet.add(binh);
        treeSet.add(chi);

        Set<Person> hashSet = new HashSet<>();
        hashSet.add(an);
        hashSet.add(binh);
        hashSet.add(chi);

        assertPrediction("Q4_TREESET_SIZE", treeSet.size(),
                Ex03_InconsistentWithEquals.Q4_TREESET_SIZE, HINT_Q4);
        assertPrediction("Q4_HASHSET_SIZE", hashSet.size(),
                Ex03_InconsistentWithEquals.Q4_HASHSET_SIZE,
                "HashSet dùng equals()/hashCode() của record Person; cả 3 người khác name hoặc age nên đều khác nhau.");
        assertPrediction("Q4_TREESET_CONTAINS_BINH", treeSet.contains(binh),
                Ex03_InconsistentWithEquals.Q4_TREESET_CONTAINS_BINH,
                "contains() cũng dùng Comparator.compare(); Bình bị coi là 'có trong set' vì trùng age với An đã có sẵn.");
    }

    @Test
    @DisplayName("Q4 consistentByAge: TreeSet giữ đủ 3 người, sort theo age rồi name")
    void q04_consistentByAge_keepsAllThreePeopleSortedByAgeThenName() {
        Person an = new Person("An", 30);
        Person binh = new Person("Bình", 30);
        Person chi = new Person("Chi", 25);

        Set<Person> treeSet = new TreeSet<>(Ex03_InconsistentWithEquals.consistentByAge());
        treeSet.add(an);
        treeSet.add(binh);
        treeSet.add(chi);

        assertEquals(3, treeSet.size(), "consistentByAge() phải phân biệt được cả 3 người.");
        assertEquals(List.of(chi, an, binh), List.copyOf(treeSet),
                "Thứ tự trong TreeSet phải là Chi, An, Bình (age tăng dần, cùng age thì name tăng dần).");
    }
}
