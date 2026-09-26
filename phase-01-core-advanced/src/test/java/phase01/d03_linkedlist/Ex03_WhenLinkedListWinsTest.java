package phase01.d03_linkedlist;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex03_WhenLinkedListWinsTest {

    @Test
    @DisplayName("Q4 insertAfterEach trên LinkedList: chèn ngay sau mỗi phần tử khớp")
    void q04_insertAfterEach_onLinkedList_insertsAfterEachMatch() {
        List<String> list = new LinkedList<>(List.of("a", "x", "b", "x"));

        Ex03_WhenLinkedListWins.insertAfterEach(list, "x"::equals, "y");

        assertEquals(List.of("a", "x", "y", "b", "x", "y"), list,
                "Mỗi 'x' phải có 'y' chèn ngay sau, phần tử vừa chèn không bị xét lại.");
    }

    @Test
    @DisplayName("Q4 insertAfterEach trên ArrayList: chèn ngay sau mỗi phần tử khớp")
    void q04_insertAfterEach_onArrayList_insertsAfterEachMatch() {
        List<String> list = new ArrayList<>(List.of("a", "x", "b", "x"));

        Ex03_WhenLinkedListWins.insertAfterEach(list, "x"::equals, "y");

        assertEquals(List.of("a", "x", "y", "b", "x", "y"), list,
                "Kết quả phải giống LinkedList: mỗi 'x' có 'y' chèn ngay sau.");
    }

    @Test
    @DisplayName("Q4 insertAfterEach trên list rỗng: giữ nguyên, không lỗi")
    void q04_insertAfterEach_onEmptyList_staysEmpty() {
        List<String> linked = new LinkedList<>();
        Ex03_WhenLinkedListWins.insertAfterEach(linked, "x"::equals, "y");
        assertEquals(List.of(), linked, "List rỗng phải giữ nguyên rỗng (LinkedList).");

        List<String> array = new ArrayList<>();
        Ex03_WhenLinkedListWins.insertAfterEach(array, "x"::equals, "y");
        assertEquals(List.of(), array, "List rỗng phải giữ nguyên rỗng (ArrayList).");
    }

    @Test
    @DisplayName("Q4 khi toInsert cũng khớp match: không lặp vô hạn, chỉ chèn đúng một lần mỗi vị trí khớp")
    void q04_insertAfterEach_whenInsertedValueMatchesPredicate_doesNotLoopForever() {
        List<String> linked = new LinkedList<>(List.of("x"));
        Ex03_WhenLinkedListWins.insertAfterEach(linked, "x"::equals, "x");
        assertEquals(List.of("x", "x"), linked, "'x' chèn sau 'x' không được lặp lại vô hạn (LinkedList).");

        List<String> array = new ArrayList<>(List.of("x"));
        Ex03_WhenLinkedListWins.insertAfterEach(array, "x"::equals, "x");
        assertEquals(List.of("x", "x"), array, "'x' chèn sau 'x' không được lặp lại vô hạn (ArrayList).");
    }
}
