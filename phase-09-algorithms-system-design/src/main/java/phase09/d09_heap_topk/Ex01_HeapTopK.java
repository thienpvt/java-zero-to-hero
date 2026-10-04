package phase09.d09_heap_topk;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Heap và Top-K.
 * <p>
 * Nguồn: 09-algorithms-system-design.md, mục 9 (Heap và Top-K), câu 1–5.
 * Cần làm trước: mục 6 (Stack, Queue và Deque), mục 8 (Tree và Binary Search Tree).
 * Cách làm: trả lời từng câu, chạy test trong Ex01_HeapTopKTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] Root của `PriorityQueue` mặc định là phần tử nào?
 *   Bắt đầu   : viết ANSWER Q1; xác định thứ tự ưu tiên mặc định.
 *   Tra cứu   : contract của PriorityQueue.
 *   Hoàn thành khi: ANSWER Q1 nêu phần tử ở root theo natural ordering.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Vì sao heap không thể trả lời mọi truy vấn thứ tự như sorted array?
 *   Bắt đầu   : viết ANSWER Q2; phân biệt quan hệ cha-con và thứ tự toàn phần.
 *   Tra cứu   : heap invariant.
 *   Hoàn thành khi: ANSWER Q2 giải thích vì sao không thể truy vấn tùy ý như mảng đã sort.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Khi nào heap kích thước k tốt hơn sort toàn bộ?
 *   Bắt đầu   : viết ANSWER Q3; so sánh chi phí theo n và k.
 *   Tra cứu   : độ phức tạp top-K.
 *   Hoàn thành khi: ANSWER Q3 nêu điều kiện k nhỏ hơn nhiều so với n.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Comparator dùng phép trừ có nguy cơ gì?
 *   Bắt đầu   : viết ANSWER Q4; thử hai giá trị int cực trị.
 *   Tra cứu   : overflow số nguyên có dấu.
 *   Hoàn thành khi: ANSWER Q4 nêu phép so sánh an toàn.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Cần quy định thế nào khi nhiều phần tử đồng hạng ở top-K?
 *   Bắt đầu   : viết ANSWER Q5; chọn contract tie-break.
 *   Tra cứu   : tính xác định của output.
 *   Hoàn thành khi: ANSWER Q5 nêu quy tắc ổn định khi phần tử bằng nhau.
 * <p>
 * B1: Tìm top-K theo thứ tự giảm dần bằng heap; phần tử bằng nhau giữ thứ tự xuất hiện.
 */
class Ex01_HeapTopK {
    static List<Integer> topK(int[] values, int k) {
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
