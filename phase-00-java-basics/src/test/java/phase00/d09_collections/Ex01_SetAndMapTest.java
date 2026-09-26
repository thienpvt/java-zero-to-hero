package phase00.d09_collections;

import static phase00.support.Predictions.assertPrediction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_SetAndMapTest {

    private static final String HINT_Q1 = "Ctrl+Q trên Set.add. Xem size sau hai lần add cùng một mã.";

    private static final String HINT_Q2 =
            "put(\"a\", null) rồi so get(\"a\") với containsKey(\"a\"). Ctrl+Q trên Map.get.";

    private static final String HINT_Q3 =
            "Ctrl+Q trên class HashSet, tìm đoạn nói về order. Đừng kết luận bằng một lần duyệt HashSet.";

    @Test
    @DisplayName("Q1 dự đoán: Set gộp mã trùng")
    void q01_prediction() {
        Set<String> codes = new HashSet<>();
        codes.add("A");
        codes.add("A");
        assertPrediction("Q1_SET_SIZE_AFTER_DUPLICATE",
                codes.size(), Ex01_SetAndMap.Q1_SET_SIZE_AFTER_DUPLICATE, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: get trả null không chứng minh key vắng mặt")
    void q02_prediction() {
        Map<String, Integer> stock = new HashMap<>();
        stock.put("a", null);
        boolean provesAbsent = stock.get("a") == null && !stock.containsKey("a");
        assertPrediction("Q2_GET_NULL_PROVES_ABSENT",
                provesAbsent, Ex01_SetAndMap.Q2_GET_NULL_PROVES_ABSENT, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: HashSet không hứa thứ tự chèn; LinkedHashSet thì có")
    void q03_prediction() {
        assertPrediction("Q3_HASHSET_PRESERVES_INSERTION_ORDER",
                false, Ex01_SetAndMap.Q3_HASHSET_PRESERVES_INSERTION_ORDER, HINT_Q3);
        Set<String> ordered = new LinkedHashSet<>();
        ordered.add("a");
        ordered.add("b");
        ordered.add("c");
        assertPrediction("Q3_LINKED_HASH_SET_PRESERVES_ORDER",
                List.copyOf(ordered).equals(List.of("a", "b", "c")),
                Ex01_SetAndMap.Q3_LINKED_HASH_SET_PRESERVES_ORDER,
                HINT_Q3);
    }
}
