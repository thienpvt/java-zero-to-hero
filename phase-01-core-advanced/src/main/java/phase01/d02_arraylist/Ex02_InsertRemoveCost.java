package phase01.d02_arraylist;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * ArrayList — Bài 2: Chi phí insert ở đầu so với LinkedList
 *
 * Nguồn: 01-java-core-advanced.md, mục 2 (ArrayList), câu 5, 10.
 * Cần làm trước: Ex01_CapacityAndResize (đã hiểu backing array và grow()).
 * Cách làm: chạy test q05_experimentRuns (Ctrl+Shift+F10) để chắc code biên dịch và
 * chạy được; sau đó chạy main() (nút ▶ cạnh main, hoặc Alt+F12 rồi
 * .\mvnw.cmd -q -pl phase-01-core-advanced exec:java -Dexec.mainClass=...) với n lớn để
 * tự đo bảng số liệu thật, rồi viết OBSERVATION bằng lời của bạn ở cuối file.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q5 [THÍ NGHIỆM] Tại sao insert ở đầu `ArrayList` là O(n)?
 *   Bắt đầu   : đọc runExperiment() bên dưới, chạy q05_experimentRuns trước để chắc nó
 *               không lỗi.
 *   Kiểm chứng: chạy main() với n = 50_000, so cột "ArrayList - đầu" với
 *               "ArrayDeque - addFirst"; đặt breakpoint trong ArrayList.add(int, E)
 *               (Ctrl+N → ArrayList → Ctrl+F12 → add) để thấy System.arraycopy chạy
 *               mỗi lần chèn ở đầu.
 *   Hoàn thành khi: viết xong OBSERVATION Q5 với số liệu đo được và giải thích vì sao
 *               phải dời toàn bộ mảng phía sau mỗi lần chèn ở đầu.
 *
 * Q10 [THÍ NGHIỆM] Khi nào ArrayList tốt hơn LinkedList dù phải insert/remove?
 *   Bắt đầu   : dùng lại runExperiment(), so cột "ArrayList - giữa" với
 *               "LinkedList - giữa".
 *   Kiểm chứng: đặt breakpoint trong LinkedList.node(int) (Ctrl+N → LinkedList →
 *               Ctrl+F12 → node) để thấy traversal O(n) tìm node trước khi insert O(1)
 *               vào giữa hai node.
 *   Hoàn thành khi: viết xong OBSERVATION Q10 nêu rõ trade-off cache locality của mảng
 *               liền vùng nhớ so với chi phí con trỏ/traversal của linked node.
 */
public class Ex02_InsertRemoveCost {

    /**
     * Đo (thô, có warm-up) thời gian chèn {@code n} phần tử ở đầu/giữa/cuối cho
     * {@code ArrayList} và {@code LinkedList}, và {@code ArrayDeque.addFirst}; trả về báo
     * cáo dạng văn bản, mỗi dòng một kịch bản. Đây chỉ là đo thô bằng
     * {@code System.nanoTime()}; Giai đoạn 2 sẽ học cách đo đúng bằng JMH.
     */
    static String runExperiment(int n) {
        StringBuilder report = new StringBuilder();
        report.append("n=").append(n).append('\n');
        report.append(measure("ArrayList - đầu", () -> insertAtFront(new ArrayList<>(), n)));
        report.append(measure("ArrayList - giữa", () -> insertAtMiddle(new ArrayList<>(), n)));
        report.append(measure("ArrayList - cuối", () -> insertAtEnd(new ArrayList<>(), n)));
        report.append(measure("LinkedList - đầu", () -> insertAtFront(new LinkedList<>(), n)));
        report.append(measure("LinkedList - giữa", () -> insertAtMiddle(new LinkedList<>(), n)));
        report.append(measure("LinkedList - cuối", () -> insertAtEnd(new LinkedList<>(), n)));
        report.append(measure("ArrayDeque - addFirst", () -> insertArrayDequeFirst(n)));
        return report.toString();
    }

    private static String measure(String label, Runnable action) {
        action.run(); // warm-up: bỏ qua JIT chưa "nóng" và lần cấp bộ nhớ đầu tiên
        long start = System.nanoTime();
        action.run();
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        return String.format("%-24s %6d ms%n", label, elapsedMs);
    }

    private static void insertAtFront(List<Integer> list, int n) {
        for (int i = 0; i < n; i++) {
            list.add(0, i);
        }
    }

    private static void insertAtMiddle(List<Integer> list, int n) {
        for (int i = 0; i < n; i++) {
            list.add(list.size() / 2, i);
        }
    }

    private static void insertAtEnd(List<Integer> list, int n) {
        for (int i = 0; i < n; i++) {
            list.add(i);
        }
    }

    private static void insertArrayDequeFirst(int n) {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            deque.addFirst(i);
        }
    }

    public static void main(String[] args) {
        System.out.println(runExperiment(50_000));
    }
}

/* OBSERVATION Q5:
 * SOLUTION-BEGIN
 * Số liệu mẫu (máy phát triển, n = 50 000, có thể khác trên máy khác):
 * ArrayList - đầu ~ vài trăm ms; ArrayDeque - addFirst ~ dưới 10 ms; LinkedList - đầu cũng
 * chỉ vài ms. ArrayList.add(0, x) phải System.arraycopy toàn bộ size phần tử hiện có sang
 * phải 1 ô trước khi ghi phần tử mới vào ô trống — chi phí tuyến tính theo size, nên chèn n
 * phần tử liên tiếp ở đầu tốn O(n^2) tổng. LinkedList/ArrayDeque chỉ tạo 1 node mới trỏ vào
 * đầu — O(1) mỗi lần, không phải dời gì cả.
 * SOLUTION-END
 */

/* OBSERVATION Q10:
 * SOLUTION-BEGIN
 * Chèn/xóa giữa cả hai đều tốn O(n) để tới được vị trí (ArrayList dời mảng bằng arraycopy,
 * LinkedList phải duyệt con trỏ next/prev từ đầu hoặc cuối tới node cần chèn), nhưng
 * ArrayList thường vẫn nhanh hơn trong thực tế vì mảng nằm liền một vùng nhớ — CPU cache
 * đọc tuần tự rất hiệu quả (cache locality), trong khi mỗi node của LinkedList là một object
 * riêng nằm rải rác trên heap, mỗi bước next/prev thường là một cache miss, cộng thêm chi phí
 * bộ nhớ cho 2 con trỏ + header object mỗi node. LinkedList chỉ thật sự có lợi khi chỉ cần
 * insert/remove ở đầu hoặc cuối (dùng như Deque/Queue) mà không cần random access theo index.
 * SOLUTION-END
 */
