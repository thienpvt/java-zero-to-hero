package phase01.d03_linkedlist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase01.support.Predictions.assertPrediction;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d03_linkedlist.Ex01_NodeTraversal.MiniLinkedList;
import phase01.d03_linkedlist.Ex01_NodeTraversal.MiniLinkedList.Node;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_NodeTraversalTest {

    private static final String HINT_Q1 =
            "index < size/2 duyệt từ first (bước = index), ngược lại duyệt từ last (bước = size - 1 - index).";
    private static final String HINT_Q2 =
            "insertAfter chỉ O(1) khi đã cầm sẵn Node; tìm Node để chèn (nodeAt) vẫn có thể mất O(n).";

    private static MiniLinkedList<Integer> buildList(int n) {
        MiniLinkedList<Integer> list = new MiniLinkedList<>();
        for (int i = 0; i < n; i++) {
            list.addLast(i);
        }
        return list;
    }

    @Test
    @DisplayName("Q1 dự đoán: get() duyệt từ đầu gần hơn và ghi số bước vào lastTraversalSteps")
    void q01_get_reportsStepsFromNearerEnd() {
        MiniLinkedList<Integer> list = buildList(10_000);

        Integer valueAt5000 = list.get(5000);
        assertEquals(5000, valueAt5000, "get(5000) phải trả đúng phần tử tại vị trí 5000.");
        assertPrediction("Q1_STEPS_GET_5000_OF_10000", list.lastTraversalSteps(),
                Ex01_NodeTraversal.Q1_STEPS_GET_5000_OF_10000, HINT_Q1);

        Integer valueAt10 = list.get(10);
        assertEquals(10, valueAt10, "get(10) phải trả đúng phần tử tại vị trí 10.");
        assertPrediction("Q1_STEPS_GET_10_OF_10000", list.lastTraversalSteps(),
                Ex01_NodeTraversal.Q1_STEPS_GET_10_OF_10000, HINT_Q1);
    }

    @Test
    @DisplayName("Q1 get(9990) trên list 10 000 phần tử: duyệt từ last, chỉ tốn 9 bước")
    void q01_get_nearLastEndTakesFewSteps() {
        MiniLinkedList<Integer> list = buildList(10_000);

        Integer value = list.get(9990);

        assertEquals(9990, value, "get(9990) phải trả đúng phần tử tại vị trí 9990.");
        assertEquals(9, list.lastTraversalSteps(),
                "get(9990) trên 10 000 phần tử phải duyệt từ last với 9 bước (size - 1 - index).");
    }

    @Test
    @DisplayName("Q1 get ngoài khoảng chỉ số phải ném IndexOutOfBoundsException")
    void q01_get_outOfRangeThrowsIndexOutOfBounds() {
        MiniLinkedList<Integer> list = buildList(10_000);

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(10_000),
                "index == size() phải là ngoài khoảng hợp lệ.");
    }

    @Test
    @DisplayName("Q2 addFirst/addLast: toList() và toListBackward() phải khớp thứ tự ngược nhau")
    void q02_addFirstAndAddLast_matchForwardAndBackwardOrder() {
        MiniLinkedList<Integer> list = new MiniLinkedList<>();
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);
        list.addFirst(0);
        list.addLast(4);

        assertEquals(List.of(0, 1, 2, 3, 4), list.toList(), "Thứ tự xuôi phải là 0,1,2,3,4.");
        assertEquals(List.of(4, 3, 2, 1, 0), list.toListBackward(), "Thứ tự ngược phải đảo lại đúng thứ tự xuôi.");
    }

    @Test
    @DisplayName("Q2 insertAfter ở giữa: nối đủ prev/next cả hai chiều")
    void q02_insertAfterMiddle_linksBothDirections() {
        MiniLinkedList<String> list = new MiniLinkedList<>();
        list.addLast("a");
        list.addLast("b");
        list.addLast("c");

        Node<String> nodeB = list.nodeAt(1);
        list.insertAfter(nodeB, "x");

        assertEquals(List.of("a", "b", "x", "c"), list.toList(), "Chèn 'x' sau 'b' phải cho thứ tự xuôi a,b,x,c.");
        assertEquals(List.of("c", "x", "b", "a"), list.toListBackward(),
                "toListBackward phải đảo đúng thứ tự sau khi chèn, chứng tỏ prev cũng được nối đúng.");
    }

    @Test
    @DisplayName("Q2 insertAfter sau node cuối: phải cập nhật lại last")
    void q02_insertAfterLast_updatesLastReference() {
        MiniLinkedList<String> list = new MiniLinkedList<>();
        list.addLast("a");
        list.addLast("b");

        Node<String> nodeB = list.nodeAt(1);
        list.insertAfter(nodeB, "c");

        assertEquals(List.of("a", "b", "c"), list.toList(), "Chèn sau node cuối 'b' phải cho a,b,c.");
        assertEquals(List.of("c", "b", "a"), list.toListBackward(),
                "toListBackward xuất phát từ last; nếu last không được cập nhật thành 'c' thì dãy này sẽ sai/thiếu.");
    }

    @Test
    @DisplayName("Q2 dự đoán: chèn giữa list có O(1) không nếu tính luôn bước tìm node")
    void q02_insertMiddleIncludingSearch_isNotO1() {
        boolean actualInsertIsO1WhenSearchIncluded = false;

        assertPrediction("Q2_INSERT_MIDDLE_O1_INCLUDING_SEARCH", actualInsertIsO1WhenSearchIncluded,
                Ex01_NodeTraversal.Q2_INSERT_MIDDLE_O1_INCLUDING_SEARCH, HINT_Q2);
    }
}
