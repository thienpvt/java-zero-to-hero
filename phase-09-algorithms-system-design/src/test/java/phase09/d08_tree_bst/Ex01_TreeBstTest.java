package phase09.d08_tree_bst;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Ex01_TreeBstTest {
    @Test
    @DisplayName("B1 inorder iterative trả thứ tự trái-gốc-phải và danh sách độc lập")
    void b01InorderOrderAndResult() {
        Ex01_TreeBst.Node root = new Ex01_TreeBst.Node(4);
        root.left = new Ex01_TreeBst.Node(2);
        root.right = new Ex01_TreeBst.Node(7);
        root.left.left = new Ex01_TreeBst.Node(1);
        root.left.right = new Ex01_TreeBst.Node(3);
        List<Integer> result = Ex01_TreeBst.inorder(root);
        assertEquals(List.of(1, 2, 3, 4, 7), result);
        assertThrows(UnsupportedOperationException.class, () -> result.add(8));
        assertEquals(List.of(), Ex01_TreeBst.inorder(null));
    }

    @Test
    @DisplayName("B1 isBst xử lý cây rỗng, node trùng và giá trị integer biên")
    void b01BstBoundaries() {
        assertTrue(Ex01_TreeBst.isBst(null));
        Ex01_TreeBst.Node extremes = new Ex01_TreeBst.Node(0);
        extremes.left = new Ex01_TreeBst.Node(Integer.MIN_VALUE);
        extremes.right = new Ex01_TreeBst.Node(Integer.MAX_VALUE);
        assertTrue(Ex01_TreeBst.isBst(extremes));

        Ex01_TreeBst.Node duplicate = new Ex01_TreeBst.Node(2);
        duplicate.left = new Ex01_TreeBst.Node(2);
        assertFalse(Ex01_TreeBst.isBst(duplicate));
        Ex01_TreeBst.Node invalidSubtree = new Ex01_TreeBst.Node(10);
        invalidSubtree.left = new Ex01_TreeBst.Node(5);
        invalidSubtree.left.right = new Ex01_TreeBst.Node(12);
        assertFalse(Ex01_TreeBst.isBst(invalidSubtree));

        Ex01_TreeBst.Node cyclic = new Ex01_TreeBst.Node(1);
        cyclic.left = cyclic;
        assertThrows(IllegalArgumentException.class, () -> Ex01_TreeBst.inorder(cyclic));
        assertFalse(Ex01_TreeBst.isBst(cyclic));
    }

    @Test
    @DisplayName("B1 traversal iterative xử lý cây lệch sâu hai mươi nghìn node")
    void b01HandlesDeepTreeIteratively() {
        int size = 20_000;
        Ex01_TreeBst.Node root = new Ex01_TreeBst.Node(size - 1);
        Ex01_TreeBst.Node current = root;
        for (int value = size - 2; value >= 0; value--) {
            current.left = new Ex01_TreeBst.Node(value);
            current = current.left;
        }
        List<Integer> expected = new ArrayList<>(size);
        for (int value = 0; value < size; value++) expected.add(value);
        assertEquals(expected, Ex01_TreeBst.inorder(root));
        assertTrue(Ex01_TreeBst.isBst(root));
    }
}
