package phase01.d05_equals_hashcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import phase01.d05_equals_hashcode.Ex02_HashCodeRules.EqualsOnlyUser;
import phase01.d05_equals_hashcode.Ex02_HashCodeRules.User;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_HashCodeRulesTest {

    private static final String HINT_Q4 =
            "EqualsOnlyUser không override hashCode() nên dùng identity hashCode; hai instance"
                    + " 'equals' vẫn rơi vào bucket khác nhau trong HashSet.";

    @Test
    @DisplayName("Q2 dự đoán: \"Aa\" và \"BB\" cùng hashCode nhưng có equals không")
    void q02_prediction_sameHashDoesNotImplyEquals() {
        assertEquals("Aa".hashCode(), "BB".hashCode(),
                "Tiền điều kiện của câu hỏi: hai chuỗi này phải có cùng hashCode (hash collision).");

        assertPrediction("Q2_SAME_HASH_IMPLIES_EQUALS", "Aa".equals("BB"),
                Ex02_HashCodeRules.Q2_SAME_HASH_IMPLIES_EQUALS,
                "Cùng hashCode chỉ là trùng giá trị băm (collision); equals() vẫn so nội dung ký tự.");
    }

    @Test
    @DisplayName("Q3 dự đoán: hai String equals thì có cùng hashCode không")
    void q03_prediction_equalsRequiresSameHash() {
        String a = new String("test");
        String b = new String("test");
        assertTrue(a.equals(b), "Tiền điều kiện của câu hỏi: hai String này phải equals nhau.");

        assertPrediction("Q3_EQUALS_REQUIRES_SAME_HASH", a.hashCode() == b.hashCode(),
                Ex02_HashCodeRules.Q3_EQUALS_REQUIRES_SAME_HASH,
                "Object.hashCode() Javadoc: hai object equals() bắt buộc phải cho cùng hashCode().");
    }

    @Test
    @DisplayName("Q4 dự đoán: EqualsOnlyUser cùng email trong HashSet")
    void q04_prediction_equalsOnlyUserBreaksHashSet() {
        Set<EqualsOnlyUser> set = new HashSet<>();
        EqualsOnlyUser first = new EqualsOnlyUser("a@x.com");
        EqualsOnlyUser second = new EqualsOnlyUser("a@x.com");
        assertTrue(first.equals(second), "Tiền điều kiện: hai EqualsOnlyUser cùng email phải equals nhau.");

        set.add(first);
        set.add(second);

        assertPrediction("Q4_HASHSET_SIZE_TWO_EQUAL_USERS", set.size(),
                Ex02_HashCodeRules.Q4_HASHSET_SIZE_TWO_EQUAL_USERS, HINT_Q4);
        assertPrediction("Q4_HASHSET_CONTAINS_EQUAL_USER",
                set.contains(new EqualsOnlyUser("a@x.com")),
                Ex02_HashCodeRules.Q4_HASHSET_CONTAINS_EQUAL_USER, HINT_Q4);
    }

    @Test
    @DisplayName("Q4 User cùng email khác displayName thì equals và cùng hashCode")
    void q04_user_sameEmailDifferentDisplayName_areEqualAndSameHash() {
        User alice = new User("a@x.com", "Alice");
        User bob = new User("a@x.com", "Bob");

        assertTrue(alice.equals(bob), "Cùng email đã chuẩn hóa thì phải equals, bất kể displayName.");
        assertEquals(alice.hashCode(), bob.hashCode(), "Equals thì phải cùng hashCode.");

        Set<User> set = new HashSet<>();
        set.add(alice);
        set.add(bob);
        assertEquals(1, set.size(), "HashSet phải coi hai User trùng email là một phần tử.");
    }

    @Test
    @DisplayName("Q4 User chuẩn hóa email: khoảng trắng và hoa/thường không ảnh hưởng equals")
    void q04_user_emailNormalization_trimAndLowerCase_areEqual() {
        User padded = new User(" A@X.com ", "X");
        User compact = new User("a@x.com", "Y");

        assertTrue(padded.equals(compact),
                "email.trim().toLowerCase(Locale.ROOT) phải chuẩn hóa hai email này về cùng giá trị.");
    }

    @Test
    @DisplayName("Q4 HashMap.get bằng một instance User mới nhưng cùng email")
    void q04_user_hashMapGet_byNewEqualInstance() {
        Map<User, String> map = new HashMap<>();
        map.put(new User("a@x.com", "Alice"), "hồ sơ Alice");

        assertEquals("hồ sơ Alice", map.get(new User("a@x.com", "Alice khác instance")),
                "HashMap.get phải tìm thấy value qua một User mới nhưng cùng email.");
    }
}
