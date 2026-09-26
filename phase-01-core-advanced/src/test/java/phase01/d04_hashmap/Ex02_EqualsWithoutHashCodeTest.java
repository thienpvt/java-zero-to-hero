package phase01.d04_hashmap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase01.support.Predictions.assertPrediction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d04_hashmap.Ex02_EqualsWithoutHashCode.EqualsOnlyPoint;
import phase01.d04_hashmap.Ex02_EqualsWithoutHashCode.Point;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_EqualsWithoutHashCodeTest {

    private static final String HINT_Q6 =
            "EqualsOnlyPoint cộng thêm salt tăng dần vào hashCode() nên hai instance (1,2) có hashCode khác nhau"
                    + " dù equals() coi là bằng nhau; HashSet rơi vào 2 bucket khác nhau nên size = 2.";

    @Test
    @DisplayName("Q6 dự đoán: HashSet hai EqualsOnlyPoint bằng nhau vẫn có size 2")
    void q06_hashSetSizeTwoEqualPoints_prediction() {
        Set<EqualsOnlyPoint> set = new HashSet<>();
        set.add(new EqualsOnlyPoint(1, 2));
        set.add(new EqualsOnlyPoint(1, 2));
        assertPrediction("Q6_HASHSET_SIZE_TWO_EQUAL_POINTS", set.size(),
                Ex02_EqualsWithoutHashCode.Q6_HASHSET_SIZE_TWO_EQUAL_POINTS, HINT_Q6);
    }

    @Test
    @DisplayName("Q6 Point.equals: hai điểm cùng x, y phải bằng nhau và cùng hashCode")
    void q06_point_equalCoordinates_areEqualAndSameHashCode() {
        Point a = new Point(1, 2);
        Point b = new Point(1, 2);
        assertTrue(a.equals(b), "Hai Point cùng x, y phải equals() true.");
        assertEquals(a.hashCode(), b.hashCode(), "Hai object equals() true phải có cùng hashCode().");
    }

    @Test
    @DisplayName("Q6 Point.equals: x hoặc y khác nhau thì không bằng nhau")
    void q06_point_differentCoordinates_areNotEqual() {
        Point a = new Point(1, 2);
        assertFalse(a.equals(new Point(9, 2)), "x khác nhau thì không được equals().");
        assertFalse(a.equals(new Point(1, 9)), "y khác nhau thì không được equals().");
    }

    @Test
    @DisplayName("Q6 Point.equals: so với null hoặc kiểu khác phải là false")
    void q06_point_equalsNullOrOtherType_isFalse() {
        Point a = new Point(1, 2);
        assertFalse(a.equals(null), "equals(null) phải trả false, không được NullPointerException.");
        assertFalse(a.equals("1,2"), "equals(kiểu khác) phải trả false.");
    }

    @Test
    @DisplayName("Q6 Point trong HashSet: hai điểm bằng nhau chỉ chiếm 1 phần tử")
    void q06_point_hashSetOfEqualPoints_hasSizeOne() {
        Set<Point> set = new HashSet<>();
        set.add(new Point(1, 2));
        set.add(new Point(1, 2));
        assertEquals(1, set.size(), "Point tuân theo contract nên hai điểm bằng nhau chỉ là 1 phần tử.");
    }

    @Test
    @DisplayName("Q6 Point trong HashMap: get bằng instance mới vẫn tìm được value")
    void q06_point_hashMapGetByNewEqualInstance_findsValue() {
        Map<Point, String> map = new HashMap<>();
        map.put(new Point(1, 2), "origin-ish");
        assertEquals("origin-ish", map.get(new Point(1, 2)),
                "Point tuân theo contract equals/hashCode nên get bằng instance mới vẫn tìm được value.");
    }
}
