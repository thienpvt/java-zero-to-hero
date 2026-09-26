package phase01.d06_hashset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase01.support.Predictions.assertPrediction;

import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d06_hashset.Ex02_SetVariants.Person;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_SetVariantsTest {

    @Test
    @DisplayName("Q3 dự đoán: HashSet có đảm bảo iteration order không")
    void q03_prediction() {
        assertPrediction("Q3_HASHSET_GUARANTEES_ORDER",
                false, Ex02_SetVariants.Q3_HASHSET_GUARANTEES_ORDER,
                "Ctrl+Q trên HashSet để đọc Javadoc \"no guarantees ... iteration order\".");
    }

    @Test
    @DisplayName("Q5 distinctInFirstSeenOrder: giữ đúng thứ tự lần xuất hiện đầu tiên")
    void q05_distinctInFirstSeenOrder_keepsFirstSeenOrder() {
        List<String> input = List.of("c", "a", "c", "b", "a");

        assertEquals(List.of("c", "a", "b"), Ex02_SetVariants.distinctInFirstSeenOrder(input));
    }

    @Test
    @DisplayName("Q6 dự đoán: TreeSet không Comparable ném ClassCastException")
    void q06_prediction() {
        TreeSet<Object> set = new TreeSet<>();

        Exception thrown = assertThrows(RuntimeException.class, () -> set.add(new Object()));

        assertPrediction("Q6_TREESET_NON_COMPARABLE_EXCEPTION",
                thrown.getClass().getSimpleName(), Ex02_SetVariants.Q6_TREESET_NON_COMPARABLE_EXCEPTION,
                "Ctrl+B vào TreeSet.add(E), F7 Step Into tới TreeMap.put, xem compare(k1, k2).");
    }

    @Test
    @DisplayName("Q6 distinctSorted: loại trùng và sắp tăng dần")
    void q06_distinctSorted_removesDuplicatesAndSorts() {
        assertEquals(List.of(1, 2, 3), Ex02_SetVariants.distinctSorted(List.of(3, 1, 3, 2)));
    }

    @Test
    @DisplayName("Q6 byAgeThenName: sắp theo tuổi rồi theo tên, loại người trùng cả hai")
    void q06_byAgeThenName_ordersByAgeThenName() {
        Person an = new Person("An", 30);
        Person binh = new Person("Bình", 25);
        Person chi = new Person("Chi", 30);

        NavigableSet<Person> result = Ex02_SetVariants.byAgeThenName(List.of(an, binh, chi));

        assertEquals(List.of(binh, an, chi), List.copyOf(result));
        assertEquals(3, result.size());
    }
}
