package phase09.d06_stack_queue_deque;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Stack, Queue và Deque.
 * <p>
 * Nguồn: 09-algorithms-system-design.md, mục 6 (Stack, Queue và Deque), câu 1–5.
 * Cần làm trước: mục 2 (Array và String), mục 4 (Two Pointers và Sliding Window).
 * Cách làm: trả lời từng câu, chạy test trong Ex01_StackQueueDequeTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] Bài toán nào tự nhiên dùng LIFO, bài toán nào dùng FIFO?
 *   Bắt đầu   : viết ANSWER Q1; so sánh undo với xử lý theo thứ tự đến.
 *   Tra cứu   : thứ tự lấy phần tử của stack và queue.
 *   Hoàn thành khi: ANSWER Q1 liên hệ đúng thứ tự xử lý với hai loại bài toán.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Vì sao `ArrayDeque` thường phù hợp hơn `Stack`?
 *   Bắt đầu   : viết ANSWER Q2; xét API và cấu trúc đồng bộ.
 *   Tra cứu   : Java Collections Framework.
 *   Hoàn thành khi: ANSWER Q2 nêu API hiện đại và lựa chọn cấu trúc theo yêu cầu đồng bộ.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Monotonic stack duy trì invariant gì?
 *   Bắt đầu   : viết ANSWER Q3; xác định chiều tăng hoặc giảm cần cho bài toán.
 *   Tra cứu   : next greater element.
 *   Hoàn thành khi: ANSWER Q3 nêu thứ tự đơn điệu và điều kiện pop.
 * <p>
 * Q4 [TỰ TRẢ LỜI] BFS cần queue để bảo đảm đặc tính nào?
 *   Bắt đầu   : viết ANSWER Q4; xét thứ tự xử lý theo từng lớp.
 *   Tra cứu   : shortest path trong graph không trọng số.
 *   Hoàn thành khi: ANSWER Q4 giải thích vì sao node gần hơn được xử lý trước.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Hành vi khi deque rỗng cần được thiết kế thế nào?
 *   Bắt đầu   : viết ANSWER Q5; phân biệt thao tác kiểm tra và lấy phần tử.
 *   Tra cứu   : cặp phương thức offer/poll và add/remove.
 *   Hoàn thành khi: ANSWER Q5 nêu rõ sentinel hoặc exception của API được chọn.
 * <p>
 * B1: Kiểm tra ngoặc và tìm đường đi ngắn nhất trong lưới không trọng số.
 */
class Ex01_StackQueueDeque {
    static boolean balancedBrackets(String input) {
        throw new UnsupportedOperationException("TODO B1");
    }

    static int shortestGridPath(char[][] grid) {
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
