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
        // SOLUTION-BEGIN throw B1
        if (values == null) throw new NullPointerException("values");
        if (k < 0 || k > values.length) throw new IllegalArgumentException("k must be between zero and n");
        if (k == 0) return List.of();
        record Ranked(int value, int index) { }
        PriorityQueue<Ranked> heap = new PriorityQueue<>((left, right) -> {
            int byValue = Integer.compare(left.value(), right.value());
            return byValue != 0 ? byValue : Integer.compare(right.index(), left.index());
        });
        for (int i = 0; i < values.length; i++) {
            Ranked candidate = new Ranked(values[i], i);
            if (heap.size() < k) heap.add(candidate);
            else {
                Ranked smallest = heap.peek();
                if (values[i] > smallest.value()) {
                    heap.remove();
                    heap.add(candidate);
                }
            }
        }
        List<Ranked> ranked = new ArrayList<>(heap);
        ranked.sort((left, right) -> {
            int byValue = Integer.compare(right.value(), left.value());
            return byValue != 0 ? byValue : Integer.compare(left.index(), right.index());
        });
        return ranked.stream().map(Ranked::value).toList();
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * PriorityQueue mặc định dùng natural ordering và đặt phần tử nhỏ nhất ở root.
 * Với comparator tùy chỉnh, root là phần tử có ưu tiên cao nhất theo comparator đó.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Heap chỉ bảo đảm quan hệ ưu tiên giữa cha và con, không sắp thứ tự các nhánh với nhau.
 * Muốn truy vấn tùy ý theo thứ tự, cần lấy nhiều phần tử hoặc dùng cấu trúc đã sort.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Heap giữ k phần tử tốt nhất xử lý n phần tử trong O(n log k), dùng O(k) bộ nhớ.
 * Nó thường có lợi khi k nhỏ hơn nhiều so với n; sort toàn bộ tốn O(n log n).
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Phép trừ có thể overflow và làm comparator trả dấu sai, phá thứ tự heap.
 * Dùng Integer.compare(a, b) để so sánh an toàn.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Contract cần nói rõ có giữ phần tử đồng hạng nào khi ranh giới top-K cắt giữa tie hay không.
 * Có thể dùng thứ tự xuất hiện làm tie-break; kết quả khi đó xác định và ổn định.
 * SOLUTION-END
 */
