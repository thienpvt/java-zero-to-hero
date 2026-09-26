package phase01.d04_hashmap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase01.support.Predictions.assertPrediction;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d04_hashmap.Ex01_LookupAndCollision.ConstantHashKey;
import phase01.d04_hashmap.Ex01_LookupAndCollision.EqualsOnlyKey;
import phase01.d04_hashmap.Ex01_LookupAndCollision.GoodKey;
import phase01.d04_hashmap.Ex01_LookupAndCollision.HashCodeOnlyKey;
import phase01.d04_hashmap.Ex01_LookupAndCollision.MiniHashMap;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_LookupAndCollisionTest {

    private static final String HINT_Q2 =
            "HashCodeOnlyKey không override equals() nên equals() rơi về Object (so theo địa chỉ tham chiếu).";
    private static final String HINT_Q4 =
            "ConstantHashKey luôn hashCode()=42 nhưng equals() theo id nên hai id khác nhau vẫn là 2 entry riêng.";
    private static final String HINT_Q5 =
            "EqualsOnlyKey cộng thêm salt tăng dần vào hashCode() nên hai instance cùng id có hashCode khác nhau.";

    @Test
    @DisplayName("Q1 put/get cơ bản: get trả đúng value đã put")
    void q01_putAndGet_basic() {
        MiniHashMap<GoodKey, String> map = new MiniHashMap<>();
        map.put(new GoodKey(1), "A");
        assertEquals("A", map.get(new GoodKey(1)));
        assertEquals(1, map.size());
    }

    @Test
    @DisplayName("Q1 put trùng key: trả value cũ và size không đổi")
    void q01_put_duplicateKey_returnsOldValueAndKeepsSize() {
        MiniHashMap<GoodKey, String> map = new MiniHashMap<>();
        map.put(new GoodKey(1), "A");
        String old = map.put(new GoodKey(1), "B");
        assertEquals("A", old, "put lại key đã có phải trả value cũ.");
        assertEquals(1, map.size(), "size không được tăng khi thay value của key đã có.");
        assertEquals("B", map.get(new GoodKey(1)));
    }

    @Test
    @DisplayName("Q1 put/get: hỗ trợ key null")
    void q01_put_nullKey_supported() {
        MiniHashMap<GoodKey, String> map = new MiniHashMap<>();
        map.put(null, "N1");
        assertEquals("N1", map.get(null));
        String old = map.put(null, "N2");
        assertEquals("N1", old);
        assertEquals("N2", map.get(null));
        assertEquals(1, map.size());
    }

    @Test
    @DisplayName("Q1/Q3 100 ConstantHashKey: dồn vào đúng một bucket, get key cuối phải duyệt hết 100 entry")
    void q01_get_hundredConstantHashKeys_singleBucketWithProbeCount() {
        MiniHashMap<ConstantHashKey, Integer> map = new MiniHashMap<>();
        for (int id = 1; id <= 100; id++) {
            map.put(new ConstantHashKey(id), id);
        }

        int[] sizes = map.bucketSizes();
        int bucketsWithEntries = 0;
        int maxBucket = 0;
        for (int size : sizes) {
            if (size > 0) {
                bucketsWithEntries++;
            }
            maxBucket = Math.max(maxBucket, size);
        }
        assertEquals(1, bucketsWithEntries, "Tất cả 100 ConstantHashKey phải rơi vào đúng một bucket.");
        assertEquals(100, maxBucket, "Bucket duy nhất đó phải có đúng 100 entry.");

        Integer value = map.get(new ConstantHashKey(100));
        assertEquals(100, value, "get bằng instance mới cùng id=100 phải trả đúng value.");
        assertEquals(100, map.lastProbeCount(),
                "Key id=100 được put cuối cùng nên nằm cuối chuỗi: phải duyệt hết 100 entry mới thấy.");
    }

    @Test
    @DisplayName("Q1/Q3 100 GoodKey: hash tất định rải đều, không bucket nào vượt 10 phần tử")
    void q01_put_hundredGoodKeys_bucketsStayBalanced() {
        MiniHashMap<GoodKey, Integer> map = new MiniHashMap<>();
        for (int id = 0; id < 100; id++) {
            map.put(new GoodKey(id), id);
        }

        int[] sizes = map.bucketSizes();
        for (int size : sizes) {
            assertTrue(size <= 10, "Với GoodKey rải đều 16 bucket, không bucket nào được vượt 10 phần tử.");
        }
        assertEquals(100, map.size());
    }

    @Test
    @DisplayName("Q2 dự đoán: HashCodeOnlyKey (không override equals) không tìm được bằng instance mới")
    void q02_hashCodeOnlyKey_prediction() {
        Map<HashCodeOnlyKey, String> map = new HashMap<>();
        map.put(new HashCodeOnlyKey(1), "A");
        boolean found = map.get(new HashCodeOnlyKey(1)) != null;
        assertPrediction("Q2_HASHCODE_ONLY_KEY_FOUND_BY_EQUAL_INSTANCE", found,
                Ex01_LookupAndCollision.Q2_HASHCODE_ONLY_KEY_FOUND_BY_EQUAL_INSTANCE, HINT_Q2);
    }

    @Test
    @DisplayName("Q4 dự đoán: hai ConstantHashKey khác id vẫn là 2 entry trong HashMap")
    void q04_constantHashKey_twoUnequalKeys_prediction() {
        Map<ConstantHashKey, String> map = new HashMap<>();
        map.put(new ConstantHashKey(1), "A");
        map.put(new ConstantHashKey(2), "B");
        assertPrediction("Q4_SIZE_WITH_TWO_COLLIDING_UNEQUAL_KEYS", map.size(),
                Ex01_LookupAndCollision.Q4_SIZE_WITH_TWO_COLLIDING_UNEQUAL_KEYS, HINT_Q4);
    }

    @Test
    @DisplayName("Q5 dự đoán: EqualsOnlyKey (hashCode lệ thuộc salt) không tìm được bằng instance mới")
    void q05_equalsOnlyKey_prediction() {
        Map<EqualsOnlyKey, String> map = new HashMap<>();
        map.put(new EqualsOnlyKey(1), "A");
        boolean found = map.get(new EqualsOnlyKey(1)) != null;
        assertPrediction("Q5_EQUALS_ONLY_KEY_FOUND_BY_EQUAL_INSTANCE", found,
                Ex01_LookupAndCollision.Q5_EQUALS_ONLY_KEY_FOUND_BY_EQUAL_INSTANCE, HINT_Q5);
        assertNull(map.get(new EqualsOnlyKey(2)), "id khác thì chắc chắn không tìm thấy.");
    }
}
