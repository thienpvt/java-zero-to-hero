package phase01.d05_equals_hashcode;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase01.support.Predictions.assertPrediction;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d05_equals_hashcode.Ex03_RecordsAndMutableEntities.Blob;
import phase01.d05_equals_hashcode.Ex03_RecordsAndMutableEntities.Customer;
import phase01.d05_equals_hashcode.Ex03_RecordsAndMutableEntities.Point;
import phase01.d05_equals_hashcode.Ex03_RecordsAndMutableEntities.SafeBlob;
import phase01.d05_equals_hashcode.Ex03_RecordsAndMutableEntities.StableCustomer;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex03_RecordsAndMutableEntitiesTest {

    private static final String HINT_Q6 =
            "Record tự sinh equals/hashCode theo Objects.equals cho từng component;"
                    + " với component kiểu mảng, Objects.equals so theo tham chiếu, không so nội dung.";
    private static final String HINT_Q7 =
            "hashCode() của Customer đổi theo email; sau khi mutate, bucket mới không khớp"
                    + " bucket cũ nên contains()/remove() không tìm lại được entry.";

    @Test
    @DisplayName("Q6 dự đoán: hai Point cùng giá trị khác instance có equals không")
    void q06_prediction_pointRecordEqualByValue() {
        Point a = new Point(1, 2);
        Point b = new Point(1, 2);
        assertPrediction("Q6_RECORD_EQUAL_BY_VALUE", a.equals(b),
                Ex03_RecordsAndMutableEntities.Q6_RECORD_EQUAL_BY_VALUE, HINT_Q6);
    }

    @Test
    @DisplayName("Q6 dự đoán: hai Point equals thì có cùng hashCode không")
    void q06_prediction_pointRecordSameHash() {
        Point a = new Point(1, 2);
        Point b = new Point(1, 2);
        assertPrediction("Q6_RECORD_SAME_HASH", a.hashCode() == b.hashCode(),
                Ex03_RecordsAndMutableEntities.Q6_RECORD_SAME_HASH, HINT_Q6);
    }

    @Test
    @DisplayName("Q6 dự đoán: hai Blob cùng nội dung mảng khác instance có equals theo nội dung không")
    void q06_prediction_blobArrayComponentEqualByContent() {
        Blob a = new Blob(new byte[] {1, 2, 3});
        Blob b = new Blob(new byte[] {1, 2, 3});
        assertPrediction("Q6_ARRAY_COMPONENT_EQUAL_BY_CONTENT", a.equals(b),
                Ex03_RecordsAndMutableEntities.Q6_ARRAY_COMPONENT_EQUAL_BY_CONTENT, HINT_Q6);
    }

    @Test
    @DisplayName("Q6 SafeBlob cùng nội dung khác instance thì equals và cùng hashCode")
    void q06_safeBlob_sameContentDifferentInstance_areEqualAndSameHash() {
        SafeBlob a = new SafeBlob(new byte[] {1, 2, 3});
        SafeBlob b = new SafeBlob(new byte[] {1, 2, 3});

        assertTrue(a.equals(b), "Hai SafeBlob cùng nội dung byte[] phải equals nhờ Arrays.equals.");
        assertEquals(a.hashCode(), b.hashCode(), "Equals thì phải cùng hashCode.");
    }

    @Test
    @DisplayName("Q6 SafeBlob: sửa mảng gốc sau khi tạo không ảnh hưởng blob")
    void q06_safeBlob_mutatingOriginalArrayAfterConstruction_doesNotAffectBlob() {
        byte[] original = {1, 2, 3};
        SafeBlob blob = new SafeBlob(original);

        original[0] = 99;

        assertArrayEquals(new byte[] {1, 2, 3}, blob.data(),
                "Compact constructor phải clone mảng đầu vào; sửa mảng gốc không được ảnh hưởng blob.");
    }

    @Test
    @DisplayName("Q6 SafeBlob: sửa mảng lấy từ data() không ảnh hưởng blob")
    void q06_safeBlob_mutatingArrayFromAccessor_doesNotAffectBlob() {
        SafeBlob blob = new SafeBlob(new byte[] {1, 2, 3});

        byte[] exposed = blob.data();
        exposed[0] = 99;

        assertArrayEquals(new byte[] {1, 2, 3}, blob.data(),
                "Accessor data() phải trả bản sao; sửa mảng trả về không được ảnh hưởng blob.");
    }

    @Test
    @DisplayName("Q6 SafeBlob: tạo với data null phải ném NullPointerException")
    void q06_safeBlob_nullData_throwsNpe() {
        assertThrows(NullPointerException.class, () -> new SafeBlob(null),
                "Compact constructor phải Objects.requireNonNull(data) trước khi clone.");
    }

    @Test
    @DisplayName("Q7 dự đoán: Customer mutable làm hỏng lookup trong HashSet")
    void q07_prediction_mutationBreaksHashSetLookup() {
        Set<Customer> set = new HashSet<>();
        Customer customer = new Customer("c1", "old@x.com");
        set.add(customer);

        customer.email = "new@x.com";

        assertPrediction("Q7_CONTAINS_AFTER_MUTATION", set.contains(customer),
                Ex03_RecordsAndMutableEntities.Q7_CONTAINS_AFTER_MUTATION, HINT_Q7);
        assertPrediction("Q7_REMOVE_AFTER_MUTATION_SUCCEEDS", set.remove(customer),
                Ex03_RecordsAndMutableEntities.Q7_REMOVE_AFTER_MUTATION_SUCCEEDS, HINT_Q7);

        set.add(customer);
        assertPrediction("Q7_SIZE_AFTER_READD", set.size(),
                Ex03_RecordsAndMutableEntities.Q7_SIZE_AFTER_READD,
                "remove() ở trên thất bại nên entry cũ (bucket cũ) vẫn còn trong set;"
                        + " add() lại tạo thêm một entry ở bucket mới → size tăng lên 2.");
    }

    @Test
    @DisplayName("Q7 StableCustomer: đổi email sau khi add vẫn contains/remove được")
    void q07_stableCustomer_mutatingEmailAfterAdd_stillFoundAndRemovable() {
        Set<StableCustomer> set = new HashSet<>();
        StableCustomer customer = new StableCustomer("c1", "old@x.com");
        set.add(customer);

        customer.email = "new@x.com";

        assertTrue(set.contains(customer),
                "equals()/hashCode() chỉ dựa trên id nên đổi email không làm mất lookup.");
        assertTrue(set.remove(customer),
                "remove() phải thành công vì hashCode() không đổi theo email.");
    }
}
