package phase01.d16_exceptions_resources;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase01.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d16_exceptions_resources.Ex01_TryWithResources.TrackedResource;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_TryWithResourcesTest {

    private static final String HINT_Q1 =
            "try-with-resources đóng theo thứ tự ngược với thứ tự khai báo, giống pop khỏi stack;"
                    + " xem TrackedResource.close().";
    private static final String HINT_Q2 =
            "Exception của thân try là exception chính (bắt được ở catch); exception ném ra từ"
                    + " close() nằm trong caught.getSuppressed(), không bị mất.";

    @Test
    @DisplayName("Q1 dự đoán: try (A; B) {} đóng theo thứ tự nào")
    void q01_closeOrderPrediction() {
        List<String> log = new ArrayList<>();
        try (TrackedResource a = new TrackedResource("A", log, false);
                TrackedResource b = new TrackedResource("B", log, false)) {
            // không làm gì trong thân try, chỉ quan sát thứ tự đóng qua log
        }
        String actual = String.join(",", log);

        assertPrediction("Q1_CLOSE_ORDER", actual, Ex01_TryWithResources.Q1_CLOSE_ORDER, HINT_Q1);
    }

    @Test
    @DisplayName("Q1 code: closeAll đóng theo thứ tự ngược danh sách khi không có lỗi")
    void q01_closeAllClosesInReverseOrderWhenNoError() throws Exception {
        List<String> log = new ArrayList<>();
        TrackedResource a = new TrackedResource("A", log, false);
        TrackedResource b = new TrackedResource("B", log, false);
        TrackedResource c = new TrackedResource("C", log, false);

        Ex01_TryWithResources.closeAll(List.of(a, b, c));

        assertEquals("C,B,A", String.join(",", log), "Phải đóng theo thứ tự ngược danh sách truyền vào.");
    }

    @Test
    @DisplayName("Q1 code: closeAll đóng hết mọi resource, lỗi đầu tiên là chính, lỗi sau bị suppressed")
    void q01_closeAllClosesAllAndSuppressesLaterErrors() {
        List<String> log = new ArrayList<>();
        TrackedResource a = new TrackedResource("A", log, true);
        TrackedResource b = new TrackedResource("B", log, false);
        TrackedResource c = new TrackedResource("C", log, true);

        Exception thrown = assertThrows(IllegalStateException.class,
                () -> Ex01_TryWithResources.closeAll(List.of(a, b, c)));

        assertEquals("close C", thrown.getMessage(),
                "Đóng theo thứ tự ngược (C trước) nên lỗi khi đóng C phải là exception chính.");
        assertEquals(1, thrown.getSuppressed().length, "Lỗi khi đóng A phải bị gắn vào làm suppressed.");
        assertEquals("close A", thrown.getSuppressed()[0].getMessage(), "Suppressed exception phải là lỗi đóng A.");
        assertEquals("C,B,A", String.join(",", log), "Cả 3 resource đều phải được đóng dù có lỗi xảy ra.");
    }

    @Test
    @DisplayName("Q1 code: closeAll với danh sách rỗng không ném lỗi")
    void q01_closeAllEmptyListDoesNotThrow() {
        assertDoesNotThrow(() -> Ex01_TryWithResources.closeAll(List.of()));
    }

    @Test
    @DisplayName("Q2 dự đoán: thân try và close() cùng ném lỗi")
    void q02_primaryAndSuppressed() {
        List<String> log = new ArrayList<>();
        TrackedResource a = new TrackedResource("A", log, true);

        IllegalStateException caught = assertThrows(IllegalStateException.class, () -> {
            try (a) {
                throw new IllegalStateException("body failed");
            }
        });

        assertPrediction("Q2_PRIMARY_MESSAGE", caught.getMessage(), Ex01_TryWithResources.Q2_PRIMARY_MESSAGE, HINT_Q2);
        assertPrediction(
                "Q2_SUPPRESSED_COUNT", caught.getSuppressed().length, Ex01_TryWithResources.Q2_SUPPRESSED_COUNT, HINT_Q2);
    }
}
