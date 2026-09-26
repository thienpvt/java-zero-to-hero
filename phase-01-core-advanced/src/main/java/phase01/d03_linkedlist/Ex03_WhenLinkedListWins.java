package phase01.d03_linkedlist;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;

/**
 * LinkedList — Bài 3: Khi nào LinkedList thực sự thắng
 *
 * Nguồn: 01-java-core-advanced.md, mục 3 (LinkedList), câu 4.
 * Cần làm trước: Ex01_NodeTraversal (đã hiểu vì sao random access của LinkedList chậm và
 * insert O(1) chỉ đúng khi đã cầm sẵn Node).
 * Cách làm: cài đặt insertAfterEach bên dưới, chạy Ex03_WhenLinkedListWinsTest bằng nút ▶
 * cạnh tên test (Ctrl+Shift+F10); sau đó viết khối ANSWER Q4 ở cuối file.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q4 [CODE + TỰ TRẢ LỜI] Khi nào LinkedList thực sự có lợi?
 *   Bắt đầu   : cài đặt insertAfterEach(List, Predicate, String) bằng java.util.ListIterator
 *               — một lượt duyệt, gọi ListIterator.add ngay sau khi next() vừa trả phần tử khớp.
 *   Kiểm chứng: Ctrl+B/Ctrl+Click vào ListIterator.add để đọc Javadoc (Ctrl+Q) về vị trí phần
 *               tử mới so với con trỏ next()/previous(); Debug một test q04_* với breakpoint
 *               trong vòng while, dùng F8 Step Over để xem list thay đổi qua từng bước.
 *   Code      : insertAfterEach(List<String> list, Predicate<String> match, String toInsert) —
 *               dùng list.listIterator(); với mỗi phần tử khớp match, gọi it.add(toInsert) ngay
 *               sau lời gọi it.next() vừa trả phần tử đó; không được xét lại phần tử mới chèn
 *               (kể cả khi toInsert cũng khớp match).
 *   Hoàn thành khi: q04_* xanh cho cả LinkedList và ArrayList; viết xong khối ANSWER Q4 nêu
 *               được tình huống LinkedList thực sự có lợi so với ArrayList.
 */
public class Ex03_WhenLinkedListWins {

    /**
     * Duyệt {@code list} một lượt bằng {@link ListIterator}; với mỗi phần tử khớp
     * {@code match}, chèn {@code toInsert} ngay sau phần tử đó. Phần tử vừa được chèn
     * không bị xét lại trong cùng lượt duyệt, dù nó có khớp {@code match} hay không.
     *
     * @param list     danh sách cần sửa tại chỗ (có thể là LinkedList hoặc ArrayList)
     * @param match    điều kiện để chèn ngay sau một phần tử
     * @param toInsert giá trị được chèn ngay sau mỗi phần tử khớp {@code match}
     */
    static void insertAfterEach(List<String> list, Predicate<String> match, String toInsert) {
        // SOLUTION-BEGIN throw Q4
        ListIterator<String> it = list.listIterator();
        while (it.hasNext()) {
            String current = it.next();
            if (match.test(current)) {
                it.add(toInsert);
            }
        }
        // SOLUTION-END
    }
}

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * LinkedList thực sự có lợi khi: (1) bạn đã "cầm sẵn" một Node/ListIterator đang đứng đúng
 * tại vị trí cần chèn hoặc xóa — không phải duyệt lại từ đầu để tìm vị trí đó — ví dụ chèn xen
 * kẽ trong một lượt duyệt như insertAfterEach ở trên, nơi chi phí tìm vị trí (duyệt for-each)
 * đã được "trả" đúng một lần cho cả việc đọc và việc chèn; (2) cần thêm/xóa liên tục ở hai đầu
 * (dùng như deque) mà không cần truy cập ngẫu nhiên theo index. Trong thực tế, java.util.ArrayDeque
 * thường nhanh hơn LinkedList cho vai trò deque/queue vì không cần cấp phát Node riêng và có
 * locality tốt hơn (xem Ex02), nên LinkedList ngày nay hiếm khi là lựa chọn tối ưu tuyệt đối —
 * nó chủ yếu còn hữu ích khi thuật toán cần thao tác trực tiếp trên Node (giữ tham chiếu Node
 * qua nhiều bước) hoặc chèn/xóa nhiều lần ở giữa một danh sách rất dài trong khi vẫn giữ
 * nguyên vị trí iterator giữa các lần, tránh phải dịch phần tử như ArrayList.
 * SOLUTION-END
 */
