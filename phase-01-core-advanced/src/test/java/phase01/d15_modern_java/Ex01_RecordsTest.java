package phase01.d15_modern_java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase01.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d15_modern_java.Ex01_Records.Email;
import phase01.d15_modern_java.Ex01_Records.Order;
import phase01.d15_modern_java.Ex01_Records.Point;
import phase01.d15_modern_java.Ex01_Records.RawOrder;
import phase01.support.Compiles;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_RecordsTest {

    private static final String HINT_Q1_COMPILES =
            "Record ngầm định extends java.lang.Record nên không thể extends thêm class nào khác, "
                    + "nhưng vẫn implements interface được như class thường.";
    private static final String HINT_Q2 =
            "Accessor tự sinh của record trả về chính field, không copy; nếu field là List thì "
                    + "list nguồn và list trong record là cùng một object.";

    @Test
    @DisplayName("Q1 dự đoán: record có được extends class / implements interface không")
    void q01_recordCompilesPredictions() {
        assertPrediction("Q1_RECORD_EXTENDS_CLASS_COMPILES",
                Compiles.NO, Ex01_Records.Q1_RECORD_EXTENDS_CLASS_COMPILES, HINT_Q1_COMPILES);
        assertPrediction("Q1_RECORD_IMPLEMENTS_INTERFACE_COMPILES",
                Compiles.YES, Ex01_Records.Q1_RECORD_IMPLEMENTS_INTERFACE_COMPILES, HINT_Q1_COMPILES);
    }

    @Test
    @DisplayName("Q1 dự đoán: toString() tự sinh của record Point(1, 2)")
    void q01_recordToString() {
        String actual = new Point(1, 2).toString();
        assertPrediction("Q1_RECORD_TO_STRING", actual, Ex01_Records.Q1_RECORD_TO_STRING,
                "toString() tự sinh có dạng TênRecord[component1=giá trị1, component2=giá trị2].");
    }

    @Test
    @DisplayName("Q1 Email: trim, lowercase và giữ nguyên phần trước/sau @")
    void q01_emailNormalizesTrimAndLowercase() {
        Email email = new Email(" An@Mail.COM ");
        assertEquals("an@mail.com", email.value(),
                "Email phải được trim và chuyển thành chữ thường bằng Locale.ROOT.");
    }

    @Test
    @DisplayName("Q1 Email: từ chối chuỗi không có đúng một @ hoặc rỗng hai bên @")
    void q01_emailRejectsInvalidFormats() {
        for (String invalid : List.of("abc", "a@", "@b", "a@b@c")) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> new Email(invalid),
                    "Email \"" + invalid + "\" phải bị từ chối bằng IllegalArgumentException.");
            assertEquals("Email không hợp lệ: " + invalid, ex.getMessage());
        }
    }

    @Test
    @DisplayName("Q1 Email: giá trị null bị từ chối bằng NullPointerException")
    void q01_emailRejectsNull() {
        assertThrows(NullPointerException.class, () -> new Email(null),
                "Email null phải bị từ chối bằng NullPointerException.");
    }

    @Test
    @DisplayName("Q2 dự đoán: sửa list nguồn của RawOrder sau khi tạo có lộ ra ngoài không")
    void q02_rawOrderLeaksMutationWithoutCopy() {
        List<String> src = new ArrayList<>(List.of("a"));
        RawOrder raw = new RawOrder("1", src);
        int sizeBefore = raw.lines().size();
        src.add("b");
        int sizeAfter = raw.lines().size();

        assertPrediction("Q2_LIST_COMPONENT_CHANGES_WITHOUT_COPY",
                sizeAfter != sizeBefore, Ex01_Records.Q2_LIST_COMPONENT_CHANGES_WITHOUT_COPY, HINT_Q2);
    }

    @Test
    @DisplayName("Q2 Order: copy list nguồn, không bị ảnh hưởng khi sửa nguồn và không cho add")
    void q02_orderCopiesListAndRejectsMutation() {
        List<String> src = new ArrayList<>(List.of("a", "b"));
        Order order = new Order("1", src);
        src.add("c");

        assertEquals(List.of("a", "b"), order.lines(),
                "lines() phải là bản sao tại thời điểm tạo, không đổi theo list nguồn.");
        List<String> lines = order.lines();
        assertThrows(UnsupportedOperationException.class, () -> lines.add("x"),
                "lines() phải là list bất biến, không cho add trực tiếp.");
    }

    @Test
    @DisplayName("Q2 Order: id null bị từ chối bằng NullPointerException")
    void q02_orderRejectsNullId() {
        assertThrows(NullPointerException.class, () -> new Order(null, List.of("a")),
                "id null phải bị từ chối bằng NullPointerException.");
    }
}
