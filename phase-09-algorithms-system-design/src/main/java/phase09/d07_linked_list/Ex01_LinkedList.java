package phase09.d07_linked_list;

/**
 * Linked List.
 * <p>
 * Nguồn: 09-algorithms-system-design.md, mục 7 (Linked List), câu 1–5.
 * Cần làm trước: mục 2 (Array và String), mục 6 (Stack, Queue và Deque).
 * Cách làm: trả lời từng câu, chạy test trong Ex01_LinkedListTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] Chèn O(1) trong linked list có bao gồm chi phí tìm vị trí không?
 *   Bắt đầu   : viết ANSWER Q1; tách tìm node khỏi cập nhật liên kết.
 *   Tra cứu   : truy cập theo index của linked list.
 *   Hoàn thành khi: ANSWER Q1 phân biệt tìm vị trí với thay đổi liên kết.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Fast/slow pointer phát hiện cycle dựa trên lý do gì?
 *   Bắt đầu   : viết ANSWER Q2; xét khoảng cách tương đối sau mỗi bước.
 *   Tra cứu   : Floyd cycle detection.
 *   Hoàn thành khi: ANSWER Q2 giải thích fast pointer bắt kịp slow trong cycle.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Dummy node giảm trường hợp biên nào?
 *   Bắt đầu   : viết ANSWER Q3; xét thao tác tại đầu danh sách.
 *   Tra cứu   : chèn hoặc xóa node đầu.
 *   Hoàn thành khi: ANSWER Q3 nêu cách dummy node thống nhất xử lý head.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Vì sao linked list thường kém cache locality hơn array?
 *   Bắt đầu   : viết ANSWER Q4; so sánh vùng nhớ liên tục với node phân tán.
 *   Tra cứu   : cache line và con trỏ next.
 *   Hoàn thành khi: ANSWER Q4 liên hệ truy cập con trỏ với cache miss.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Khi nào dùng collection chuẩn thay vì tự cài linked list?
 *   Bắt đầu   : viết ANSWER Q5; xét nhu cầu API và quyền kiểm soát node.
 *   Tra cứu   : Java Collections Framework.
 *   Hoàn thành khi: ANSWER Q5 nêu lợi ích bảo trì và trường hợp cần cấu trúc tùy chỉnh.
 * <p>
 * B1: Đảo danh sách liên kết không cycle và tìm entry của cycle bằng Floyd.
 */
class Ex01_LinkedList {
    static final class Node {
        final int value;
        Node next;

        Node(int value) { this.value = value; }
    }

    static Node reverse(Node head) {
        // SOLUTION-BEGIN throw B1
        if (cycleEntry(head) != null) throw new IllegalArgumentException("cannot reverse a cyclic list");
        Node previous = null;
        Node current = head;
        while (current != null) {
            Node next = current.next;
            current.next = previous;
            previous = current;
            current = next;
        }
        return previous;
        // SOLUTION-END
    }

    static Node cycleEntry(Node head) {
        // SOLUTION-BEGIN throw B1
        Node slow = head, fast = head;
        do {
            if (fast == null || fast.next == null) return null;
            slow = slow.next;
            fast = fast.next.next;
        } while (slow != fast);
        Node fromHead = head;
        while (fromHead != slow) {
            fromHead = fromHead.next;
            slow = slow.next;
        }
        return fromHead;
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Cập nhật liên kết có thể là O(1) khi đã có node cần thiết.
 * Tìm node theo vị trí thường tốn O(n), nên chi phí tìm kiếm không nằm trong nhận xét O(1) của thao tác chèn.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Trong cycle, fast tiến hai bước còn slow tiến một bước mỗi vòng, nên khoảng cách tương đối thay đổi một đơn vị.
 * Khoảng cách đó xét modulo độ dài cycle nên cuối cùng bằng không; hai con trỏ gặp nhau.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Dummy node đứng trước head thật, nhờ đó thao tác đầu danh sách cũng có node predecessor.
 * Cùng một cách cập nhật liên kết có thể dùng cho head và các vị trí khác, giảm nhánh xử lý riêng.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Array thường lưu phần tử liền nhau, còn linked list phải theo con trỏ next tới node có thể nằm xa trong bộ nhớ.
 * Truy cập tuần tự vì thế dễ bỏ lỡ cache line hơn và cần nhiều lần phụ thuộc vào việc đọc con trỏ.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Dùng collection chuẩn khi API thông thường đủ dùng để giảm mã tự quản lý và lỗi biên.
 * Tự cài linked list khi cần kiểm soát node hoặc invariant đặc thù mà collection chuẩn không biểu đạt được.
 * SOLUTION-END
 */
