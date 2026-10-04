package phase01.d03_linkedlist;

import java.util.ArrayList;
import java.util.List;

/**
 * LinkedList — Bài 1: Node và duyệt (traversal)
 *
 * Nguồn: 01-java-core-advanced.md, mục 3 (LinkedList), câu 2, 1.
 * Cần làm trước: không (Q1 và Q2 đi cặp trong file này).
 * Cách làm: Q1 và Q2 đi cặp — cài đặt các phần TODO của cả hai câu rồi mới chạy test q01_*
 * và q02_*; chạy test trong Ex01_NodeTraversalTest bằng nút ▶ cạnh tên test
 * (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q2 [DỰ ĐOÁN + CODE] Insert giữa LinkedList có thực sự O(1) không?
 *   Bắt đầu   : cùng Q1, cài đặt addFirst/addLast, insertAfter, nodeAt và get (TODO của
 *               cả Q1 lẫn Q2); điền Q2_INSERT_MIDDLE_O1_INCLUDING_SEARCH (thay null);
 *               chỉ chạy q01_* hoặc q02_* khi các method trên đã xong.
 *   Kiểm chứng: chạy các test q02_*; sau một lời gọi insertAfter, dùng Alt+F8 Evaluate
 *               Expression để đọc node.next.prev và node.prev.next ngay tại breakpoint,
 *               tự xác nhận liên kết hai chiều đã được nối đúng.
 *   Code      : insertAfter(Node&lt;E&gt; node, E e) chỉ nối lại prev/next quanh vị trí chèn (O(1)),
 *               cập nhật last khi node đang là phần tử cuối — không được duyệt list.
 *   Hoàn thành khi: đã cài xong TODO Q1 và Q2, q02_* xanh; giải thích được phần nào của
 *               thao tác chèn là O(1) và phần nào có thể tốn O(n) nếu tính cả bước tìm node.
 * <p>
 * Q1 [DỰ ĐOÁN + CODE] {@code LinkedList.get(5000)} hoạt động thế nào?
 *   Bắt đầu   : cùng Q2 (xem mục Q2), cài đặt nodeAt(int) và get(int) (thay phần TODO);
 *               điền hai hằng số Q1_STEPS_* (thay null); chỉ chạy q01_* sau khi cả get và
 *               addLast đã xong.
 *   Kiểm chứng: chạy các test q01_*; đặt breakpoint trong java.util.LinkedList.node(int)
 *               (Ctrl+N → LinkedList → Ctrl+F12 → node(int)), Debug một test bất kỳ gọi
 *               get(...), dùng F7 Step Into để so sánh chiến lược duyệt với nodeAt của bạn.
 *   Code      : nodeAt(int index) duyệt từ đầu gần hơn (first hoặc last) và ghi số bước vào
 *               lastTraversalSteps; ngoài khoảng [0, size) ném IndexOutOfBoundsException.
 *               get(int index) trả item của node tại index đó.
 *   Hoàn thành khi: các test q01_* xanh; giải thích được vì sao số bước của get không phải
 *               lúc nào cũng bằng index.
 */
public class Ex01_NodeTraversal {

    static final class MiniLinkedList<E> {

        static final class Node<E> {
            E item;
            Node<E> prev;
            Node<E> next;
        }

        private Node<E> first;
        private Node<E> last;
        private int size;
        private int lastTraversalSteps;

        int size() {
            return size;
        }

        int lastTraversalSteps() {
            return lastTraversalSteps;
        }

        Node<E> first() {
            return first;
        }

        List<E> toList() {
            List<E> out = new ArrayList<>();
            for (Node<E> n = first; n != null; n = n.next) {
                out.add(n.item);
            }
            return out;
        }

        List<E> toListBackward() {
            List<E> out = new ArrayList<>();
            for (Node<E> n = last; n != null; n = n.prev) {
                out.add(n.item);
            }
            return out;
        }

        /**
         * Thêm {@code e} vào cuối danh sách. O(1).
         */
        void addLast(E e) {
            // SOLUTION-BEGIN throw Q2
            Node<E> node = new Node<>();
            node.item = e;
            node.prev = last;
            node.next = null;
            if (last == null) {
                first = node;
            } else {
                last.next = node;
            }
            last = node;
            size++;
            // SOLUTION-END
        }

        /**
         * Thêm {@code e} vào đầu danh sách. O(1).
         */
        void addFirst(E e) {
            // SOLUTION-BEGIN throw Q2
            Node<E> node = new Node<>();
            node.item = e;
            node.next = first;
            node.prev = null;
            if (first == null) {
                last = node;
            } else {
                first.prev = node;
            }
            first = node;
            size++;
            // SOLUTION-END
        }

        /**
         * Trả node tại {@code index} (0-based). Duyệt từ đầu gần hơn (first hoặc last) để
         * giảm số bước, và ghi số bước thực tế đã đi vào {@link #lastTraversalSteps}.
         *
         * @throws IndexOutOfBoundsException nếu {@code index < 0 || index >= size()}
         */
        Node<E> nodeAt(int index) {
            // SOLUTION-BEGIN throw Q1
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
            }
            Node<E> node;
            int steps;
            if (index < (size >> 1)) {
                node = first;
                steps = index;
                for (int i = 0; i < steps; i++) {
                    node = node.next;
                }
            } else {
                node = last;
                steps = size - 1 - index;
                for (int i = 0; i < steps; i++) {
                    node = node.prev;
                }
            }
            lastTraversalSteps = steps;
            return node;
            // SOLUTION-END
        }

        /**
         * Trả phần tử tại {@code index}, dựa trên {@link #nodeAt(int)}.
         *
         * @throws IndexOutOfBoundsException nếu {@code index} ngoài khoảng hợp lệ
         */
        E get(int index) {
            // SOLUTION-BEGIN throw Q1
            return nodeAt(index).item;
            // SOLUTION-END
        }

        /**
         * Chèn {@code e} ngay sau {@code node}, O(1) — chỉ nối lại prev/next quanh vị trí
         * chèn, không duyệt list. Cập nhật {@link #last} khi {@code node} đang là phần tử cuối.
         *
         * @return node mới được tạo cho {@code e}
         */
        Node<E> insertAfter(Node<E> node, E e) {
            // SOLUTION-BEGIN throw Q2
            Node<E> newNode = new Node<>();
            newNode.item = e;
            newNode.prev = node;
            newNode.next = node.next;
            if (node.next != null) {
                node.next.prev = newNode;
            } else {
                last = newNode;
            }
            node.next = newNode;
            size++;
            return newNode;
            // SOLUTION-END
        }
    }

    // Q1 — kịch bản: MiniLinkedList<Integer> có 10 000 phần tử giá trị 0..9999 (thêm bằng
    // addLast theo thứ tự), gọi get(index) rồi đọc lastTraversalSteps() ngay sau đó.
    static final Integer Q1_STEPS_GET_5000_OF_10000 = 4999; // SOLUTION-VALUE
    static final Integer Q1_STEPS_GET_10_OF_10000 = 10; // SOLUTION-VALUE

    // Q2 — kịch bản: chèn vào giữa khi chưa cầm sẵn Node, tính cả bước tìm vị trí.
    // Hằng hỏi: toàn bộ thao tác đó có luôn là O(1) không?
    static final Boolean Q2_INSERT_MIDDLE_O1_INCLUDING_SEARCH = false; // SOLUTION-VALUE
}
