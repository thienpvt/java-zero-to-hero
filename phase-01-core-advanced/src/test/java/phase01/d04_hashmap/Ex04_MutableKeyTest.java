package phase01.d04_hashmap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase01.support.Predictions.assertPrediction;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d04_hashmap.Ex04_MutableKey.MutableKey;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex04_MutableKeyTest {

    private static final String HINT_Q10 =
            "hashCode() mới trỏ sang bucket khác; đặt breakpoint trong HashMap.getNode để xem bucket được tìm.";

    @Test
    @DisplayName("Q10 dự đoán: key bị đổi id sau khi put")
    void q10_prediction() {
        Map<MutableKey, String> map = new HashMap<>();
        MutableKey key = new MutableKey(1);
        map.put(key, "A");
        key.id = 2;

        assertPrediction("Q10_GET_BY_SAME_REFERENCE_FINDS_VALUE",
                map.get(key) != null, Ex04_MutableKey.Q10_GET_BY_SAME_REFERENCE_FINDS_VALUE, HINT_Q10);
        assertPrediction("Q10_GET_BY_NEW_KEY_1_FINDS_VALUE",
                map.get(new MutableKey(1)) != null, Ex04_MutableKey.Q10_GET_BY_NEW_KEY_1_FINDS_VALUE, HINT_Q10);
        assertPrediction("Q10_GET_BY_NEW_KEY_2_FINDS_VALUE",
                map.get(new MutableKey(2)) != null, Ex04_MutableKey.Q10_GET_BY_NEW_KEY_2_FINDS_VALUE, HINT_Q10);
        assertPrediction("Q10_SIZE_AFTER_MUTATION",
                map.size(), Ex04_MutableKey.Q10_SIZE_AFTER_MUTATION,
                "Entry cũ không bị xóa khi field của key đổi.");
    }

    @Test
    @DisplayName("Q10 changeIdSafely: sau khi đổi id vẫn tìm được value")
    void q10_changeIdSafely_keepsValueReachable() {
        Map<MutableKey, String> map = new HashMap<>();
        MutableKey key = new MutableKey(1);
        map.put(key, "A");

        Ex04_MutableKey.changeIdSafely(map, key, 2);

        assertEquals(2, key.id, "Id của key phải được đổi thành 2.");
        assertEquals("A", map.get(key), "Tìm bằng chính key sau khi đổi phải ra value cũ.");
        assertEquals("A", map.get(new MutableKey(2)), "Tìm bằng key mới id=2 phải ra value cũ.");
        assertEquals(1, map.size(), "Map chỉ được có đúng 1 entry.");
    }

    @Test
    @DisplayName("Q10 changeIdSafely: key không có trong map thì báo lỗi và không đổi map")
    void q10_changeIdSafely_rejectsMissingKey() {
        Map<MutableKey, String> map = new HashMap<>();
        map.put(new MutableKey(1), "A");
        MutableKey missing = new MutableKey(99);

        assertThrows(IllegalArgumentException.class, () -> Ex04_MutableKey.changeIdSafely(map, missing, 5));
        assertEquals(99, missing.id, "Không được đổi id khi key không có trong map.");
        assertEquals(Map.of(new MutableKey(1), "A"), map, "Map phải giữ nguyên.");
    }
}
