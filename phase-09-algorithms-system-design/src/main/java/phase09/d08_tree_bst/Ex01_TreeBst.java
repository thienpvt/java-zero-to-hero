package phase09.d08_tree_bst;

import java.util.List;

/**
 * Tree và Binary Search Tree.
 * <p>
 * Nguồn: 09-algorithms-system-design.md, mục 8 (Tree và Binary Search Tree), câu 1–5.
 * Cần làm trước: mục 4 (Two Pointers và Sliding Window), mục 6 (Stack, Queue và Deque).
 * Cách làm: trả lời từng câu, chạy test trong Ex01_TreeBstTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] Inorder traversal của BST có đặc tính gì?
 *   Bắt đầu   : viết ANSWER Q1; theo thứ tự node trái, gốc, phải.
 *   Tra cứu   : invariant của BST.
 *   Hoàn thành khi: ANSWER Q1 nêu thứ tự giá trị thu được khi khóa không trùng.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Tại sao BST thường không đảm bảo O(log n)?
 *   Bắt đầu   : viết ANSWER Q2; xét node được chèn theo thứ tự tăng.
 *   Tra cứu   : chiều cao cây và chi phí tìm kiếm.
 *   Hoàn thành khi: ANSWER Q2 liên hệ chiều cao với thời gian thao tác.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Cây lệch có chiều cao và chi phí tìm kiếm thế nào?
 *   Bắt đầu   : viết ANSWER Q3; so sánh cây cân bằng với một nhánh.
 *   Tra cứu   : số node trên đường từ root tới leaf.
 *   Hoàn thành khi: ANSWER Q3 nêu trường hợp chiều cao tuyến tính theo số node.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Khi nào traversal iterative an toàn hơn recursive?
 *   Bắt đầu   : viết ANSWER Q4; xét chiều sâu không kiểm soát.
 *   Tra cứu   : call stack và explicit stack.
 *   Hoàn thành khi: ANSWER Q4 giải thích cách tránh giới hạn call stack.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Heap có thỏa tính chất BST không?
 *   Bắt đầu   : viết ANSWER Q5; phân biệt thứ tự cha-con với thứ tự trái-phải.
 *   Tra cứu   : heap property và BST ordering.
 *   Hoàn thành khi: ANSWER Q5 phân biệt hai invariant và kiểu truy vấn mỗi cấu trúc hỗ trợ.
 * <p>
 * B1: Cài inorder iterative và xác nhận BST bằng giới hạn giá trị nghiêm ngặt.
 */
class Ex01_TreeBst {
    static final class Node {
        final int value;
        Node left;
        Node right;

        Node(int value) { this.value = value; }
    }

    static List<Integer> inorder(Node root) {
        // SOLUTION-BEGIN throw B1
        java.util.ArrayList<Integer> result = new java.util.ArrayList<>();
        java.util.ArrayDeque<Node> stack = new java.util.ArrayDeque<>();
        java.util.Set<Node> seen = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
        Node current = root;
        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                if (!seen.add(current)) throw new IllegalArgumentException("input must be a tree");
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            result.add(current.value);
            current = current.right;
        }
        return List.copyOf(result);
        // SOLUTION-END
    }

    static boolean isBst(Node root) {
        // SOLUTION-BEGIN throw B1
        record Entry(Node node, long minimum, long maximum) { }
        java.util.ArrayDeque<Entry> stack = new java.util.ArrayDeque<>();
        java.util.Set<Node> seen = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
        if (root != null) stack.push(new Entry(root, Long.MIN_VALUE, Long.MAX_VALUE));
        while (!stack.isEmpty()) {
            Entry entry = stack.pop();
            Node node = entry.node();
            if (!seen.add(node) || node.value <= entry.minimum() || node.value >= entry.maximum()) return false;
            if (node.right != null) stack.push(new Entry(node.right, node.value, entry.maximum()));
            if (node.left != null) stack.push(new Entry(node.left, entry.minimum(), node.value));
        }
        return true;
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Với BST không có khóa trùng, mọi giá trị nhánh trái nhỏ hơn node và mọi giá trị nhánh phải lớn hơn node.
 * Inorder đi trái, thăm gốc, rồi đi phải nên tạo dãy tăng dần.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * BST không tự cân bằng có thể trở thành cây lệch khi thứ tự chèn không thuận lợi.
 * Khi đó chiều cao tăng tuyến tính theo số node và tìm kiếm có thể tốn O(n), không phải O(log n).
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Cây lệch có thể có một node trên mỗi mức, nên chiều cao là O(n) với n node.
 * Tìm kiếm đi theo nhánh đó cũng có thể thăm O(n) node.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Đệ quy dùng call stack nên cây sâu tùy ý có thể làm tràn stack.
 * Traversal iterative lưu node cần xử lý trong cấu trúc dữ liệu tường minh, tránh phụ thuộc độ sâu đệ quy.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Heap thường chỉ yêu cầu quan hệ thứ tự giữa cha và con, không sắp thứ tự trái với phải.
 * BST yêu cầu mọi khóa ở nhánh trái nhỏ hơn node và khóa nhánh phải lớn hơn node.
 * Vì vậy heap lấy cực trị hiệu quả nhưng không thay BST cho truy vấn tìm kiếm có thứ tự.
 * SOLUTION-END
 */
