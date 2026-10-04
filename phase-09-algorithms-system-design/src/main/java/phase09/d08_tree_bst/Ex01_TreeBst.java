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
        throw new UnsupportedOperationException("TODO B1");
    }

    static boolean isBst(Node root) {
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
