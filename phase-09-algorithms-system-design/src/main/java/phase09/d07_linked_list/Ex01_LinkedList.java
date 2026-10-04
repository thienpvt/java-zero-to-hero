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
        throw new UnsupportedOperationException("TODO B1");
    }

    static Node cycleEntry(Node head) {
        throw new UnsupportedOperationException("TODO B1");
    }
}

/* ANSWER Q1:
 *
 */

/* ANSWER Q2:
 *
 */

/* ANSWER Q3:
 *
 */

/* ANSWER Q4:
 *
 */

/* ANSWER Q5:
 *
 */
