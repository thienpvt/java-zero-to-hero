package phase01.d04_hashmap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase01.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.support.Complexity;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex03_ResizeAndTreeifyTest {

    private static final String HINT_Q7 =
            "DEFAULT_INITIAL_CAPACITY (16) * DEFAULT_LOAD_FACTOR (0.75) của java.util.HashMap.";
    private static final String HINT_Q11 =
            "Bucket dạng chuỗi liên kết phải duyệt tuần tự (O(n)); bucket bị treeify với key Comparable dùng cây đỏ-đen (O(log n)).";
    private static final String HINT_Q12 =
            "Đọc hằng số TREEIFY_THRESHOLD và MIN_TREEIFY_CAPACITY trong source java.util.HashMap (Ctrl+F12).";

    @Test
    @DisplayName("Q7 dự đoán: threshold mặc định của HashMap theo hằng số nguồn JDK")
    void q07_defaultThreshold_prediction() {
        int actualDefaultThreshold = (int) (16 * 0.75f);
        assertPrediction("Q7_DEFAULT_THRESHOLD", actualDefaultThreshold,
                Ex03_ResizeAndTreeify.Q7_DEFAULT_THRESHOLD, HINT_Q7);
    }

    @Test
    @DisplayName("Q7 tableSizeFor: các mốc biên và mốc thường")
    void q07_tableSizeFor_basicAndBoundaries() {
        assertEquals(1, Ex03_ResizeAndTreeify.tableSizeFor(0));
        assertEquals(1, Ex03_ResizeAndTreeify.tableSizeFor(1));
        assertEquals(16, Ex03_ResizeAndTreeify.tableSizeFor(16));
        assertEquals(32, Ex03_ResizeAndTreeify.tableSizeFor(17));
    }

    @Test
    @DisplayName("Q7 tableSizeFor: capacity vượt quá 1<<30 phải bị chặn lại ở 1<<30")
    void q07_tableSizeFor_overflowClampedToMaximum() {
        assertEquals(1 << 30, Ex03_ResizeAndTreeify.tableSizeFor((1 << 30) + 1));
    }

    @Test
    @DisplayName("Q7 capacityFor: các mốc thường")
    void q07_capacityFor_basicCases() {
        assertEquals(1, Ex03_ResizeAndTreeify.capacityFor(0));
        assertEquals(16, Ex03_ResizeAndTreeify.capacityFor(12));
        assertEquals(32, Ex03_ResizeAndTreeify.capacityFor(13));
        assertEquals(256, Ex03_ResizeAndTreeify.capacityFor(100));
    }

    @Test
    @DisplayName("Q7 capacityFor: expectedSize âm phải báo lỗi")
    void q07_capacityFor_negativeExpectedSize_throws() {
        assertThrows(IllegalArgumentException.class, () -> Ex03_ResizeAndTreeify.capacityFor(-1));
    }

    @Test
    @DisplayName("Q7 resizesNeeded: các mốc thường")
    void q07_resizesNeeded_basicCases() {
        assertEquals(0, Ex03_ResizeAndTreeify.resizesNeeded(12, 16));
        assertEquals(1, Ex03_ResizeAndTreeify.resizesNeeded(13, 16));
        assertEquals(4, Ex03_ResizeAndTreeify.resizesNeeded(100, 16));
        assertEquals(0, Ex03_ResizeAndTreeify.resizesNeeded(100, 256));
    }

    @Test
    @DisplayName("Q7 resizesNeeded: initialTableSize không phải lũy thừa 2 phải báo lỗi")
    void q07_resizesNeeded_nonPowerOfTwoInitialSize_throws() {
        assertThrows(IllegalArgumentException.class, () -> Ex03_ResizeAndTreeify.resizesNeeded(10, 12));
    }

    @Test
    @DisplayName("Q8 thí nghiệm: runExperiment chạy xong và trả báo cáo không rỗng")
    void q08_experimentRuns() {
        String report = Ex03_ResizeAndTreeify.runExperiment(2_000);
        assertFalse(report.isBlank(), "Báo cáo thí nghiệm không được rỗng.");
    }

    @Test
    @DisplayName("Q11 dự đoán: worst-case lookup khi bucket là chuỗi liên kết")
    void q11_worstCaseLinkedBucket_prediction() {
        assertPrediction("Q11_WORST_CASE_LINKED_BUCKET", Complexity.O_N,
                Ex03_ResizeAndTreeify.Q11_WORST_CASE_LINKED_BUCKET, HINT_Q11);
    }

    @Test
    @DisplayName("Q11 dự đoán: worst-case lookup khi bucket đã treeify với key Comparable")
    void q11_worstCaseTreeBinComparableKeys_prediction() {
        assertPrediction("Q11_WORST_CASE_TREE_BIN_COMPARABLE_KEYS", Complexity.O_LOG_N,
                Ex03_ResizeAndTreeify.Q11_WORST_CASE_TREE_BIN_COMPARABLE_KEYS, HINT_Q11);
    }

    @Test
    @DisplayName("Q12 dự đoán: ngưỡng treeify của HashMap")
    void q12_treeifyThreshold_prediction() {
        assertPrediction("Q12_TREEIFY_THRESHOLD", 8,
                Ex03_ResizeAndTreeify.Q12_TREEIFY_THRESHOLD, HINT_Q12);
    }

    @Test
    @DisplayName("Q12 dự đoán: capacity tối thiểu để HashMap cho phép treeify")
    void q12_minTreeifyCapacity_prediction() {
        assertPrediction("Q12_MIN_TREEIFY_CAPACITY", 64,
                Ex03_ResizeAndTreeify.Q12_MIN_TREEIFY_CAPACITY, HINT_Q12);
    }

    @Test
    @DisplayName("Q7 kiểm tra thêm: tableSizeFor là hàm đơn điệu không giảm")
    void q07_tableSizeFor_isMonotonic() {
        int previous = Ex03_ResizeAndTreeify.tableSizeFor(1);
        for (int capacity = 2; capacity <= 1000; capacity++) {
            int current = Ex03_ResizeAndTreeify.tableSizeFor(capacity);
            assertTrue(current >= previous, "tableSizeFor phải không giảm khi capacity tăng.");
            previous = current;
        }
    }
}
