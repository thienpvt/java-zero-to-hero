package phase01.d03_linkedlist;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * LinkedList — Bài 2: Memory và cache locality
 *
 * Nguồn: 01-java-core-advanced.md, mục 3 (LinkedList), câu 3, 5–6.
 * Cần làm trước: không.
 * Cách làm: đọc runExperiment bên dưới (đã cho sẵn, không cần sửa), chạy q03_experimentRuns,
 * rồi chạy main() (nút ▶ cạnh main hoặc Ctrl+Shift+F10 nếu chạy như test/ứng dụng) để xem báo
 * cáo đầy đủ với n lớn; sau đó điền các khối OBSERVATION/ANSWER ở cuối file.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q3 [THÍ NGHIỆM + TỰ TRẢ LỜI] LinkedList sử dụng nhiều memory hơn ArrayList vì sao?
 *   Bắt đầu   : chạy q03_experimentRuns; sau đó chạy main() với n vài triệu để đọc dòng
 *               "byte/phần tử" trong báo cáo.
 *   Kiểm chứng: Ctrl+N → gõ "LinkedList" → xem field first/last kiểu Node (inner class);
 *               Ctrl+N → "ArrayList" → xem field elementData kiểu Object[] để so sánh cách lưu.
 *   Hoàn thành khi: viết xong khối OBSERVATION Q3 (số đo bạn quan sát được) và ANSWER Q3
 *               (giải thích vì sao); giải thích được số "byte/phần tử" ở đây chỉ là số đo thô
 *               tương đối (qua Runtime.totalMemory()-freeMemory()), không phải kích thước tuyệt đối.
 * <p>
 * Q5 [THÍ NGHIỆM] Vì sao trên workload thực tế ArrayList thường nhanh hơn LinkedList?
 *   Bắt đầu   : dùng lại báo cáo của q03_experimentRuns/main(), đọc phần đo thời gian duyệt
 *               (int[] so với ArrayList so với LinkedList).
 *   Kiểm chứng: chạy lại main() vài lần, so sánh xu hướng số ns giữa ba cấu trúc (số tuyệt đối
 *               dao động do JIT/GC nhưng thứ tự nhanh–chậm thường ổn định qua nhiều lần chạy).
 *   Hoàn thành khi: viết xong khối OBSERVATION Q5 nêu cấu trúc nào nhanh hơn và liên hệ với Q6.
 * <p>
 * Q6 [TỰ TRẢ LỜI] CPU cache locality ảnh hưởng thế nào?
 *   Bắt đầu   : làm Q3 và Q5 trước, dùng chính quan sát đó để trả lời.
 *   Tra cứu   : tìm khái niệm "CPU cache line" (thường 64 byte) và "spatial locality"; liên hệ
 *               việc ArrayList lưu liên tiếp trong một mảng, còn Node của LinkedList được cấp
 *               phát rải rác trên heap (mỗi lần "new Node&lt;&gt;()" có thể nằm ở vùng nhớ khác nhau).
 *   Hoàn thành khi: viết xong khối ANSWER Q6, nêu được vì sao duyệt mảng liên tiếp tận dụng
 *               cache tốt hơn "pointer chasing" qua các Node rải rác.
 */
public class Ex02_MemoryAndLocality {

    /**
     * Sinh báo cáo so sánh ArrayList và LinkedList trên hai khía cạnh:
     * (a) ước lượng thô byte/phần tử qua chênh lệch {@code Runtime.totalMemory() -
     * freeMemory()} trước/sau khi thêm {@code n} phần tử, có {@code System.gc()} trước mỗi
     * lần đo — đây là số đo thô, chỉ dùng để so sánh tương đối, không phải kích thước chính xác;
     * (b) thời gian duyệt tổng bằng for-each trên {@code int[]}, {@code ArrayList<Integer>} và
     * {@code LinkedList<Integer>}, đo thô bằng {@code System.nanoTime()} sau một vòng warm-up.
     *
     * @param n số phần tử dùng cho cả hai phép đo (nên dùng số nhỏ trong test, số lớn khi chạy main)
     * @return báo cáo dạng văn bản, không rỗng
     */
    static String runExperiment(int n) {
        StringBuilder report = new StringBuilder();

        System.gc();
        long beforeArrayList = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        List<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            arrayList.add(i);
        }
        System.gc();
        long afterArrayList = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long arrayListBytes = afterArrayList - beforeArrayList;

        System.gc();
        long beforeLinkedList = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        List<Integer> linkedList = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            linkedList.add(i);
        }
        System.gc();
        long afterLinkedList = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long linkedListBytes = afterLinkedList - beforeLinkedList;

        report.append(String.format(
                "Bộ nhớ thô (%,d phần tử): ArrayList ~%.1f byte/phần tử, LinkedList ~%.1f byte/phần tử"
                        + " (số đo thô qua Runtime.totalMemory()-freeMemory(), chỉ để so sánh tương đối).%n",
                n, arrayListBytes / (double) n, linkedListBytes / (double) n));

        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = i;
        }

        // Vòng warm-up để JIT biên dịch nóng trước khi đo, giảm nhiễu cho lần đo thật.
        sumArray(array);
        sumList(arrayList);
        sumList(linkedList);

        long t0 = System.nanoTime();
        long arraySum = sumArray(array);
        long t1 = System.nanoTime();
        long arrayListSum = sumList(arrayList);
        long t2 = System.nanoTime();
        long linkedListSum = sumList(linkedList);
        long t3 = System.nanoTime();

        report.append(String.format(
                "Duyệt tổng %,d phần tử (đo thô System.nanoTime, đã warm-up): int[]=%,d ns (tổng=%d),"
                        + " ArrayList=%,d ns (tổng=%d), LinkedList=%,d ns (tổng=%d).",
                n, (t1 - t0), arraySum, (t2 - t1), arrayListSum, (t3 - t2), linkedListSum));

        return report.toString();
    }

    private static long sumArray(int[] array) {
        long sum = 0;
        for (int value : array) {
            sum += value;
        }
        return sum;
    }

    private static long sumList(List<Integer> list) {
        long sum = 0;
        for (int value : list) {
            sum += value;
        }
        return sum;
    }

    /**
     * In báo cáo với n lớn (vài triệu) để thấy rõ chênh lệch memory và thời gian duyệt.
     * Giai đoạn 2 (JMH) mới học cách đo hiệu năng đúng chuẩn (warm-up, fork, nhiều iteration).
     */
    public static void main(String[] args) {
        System.out.println(runExperiment(2_000_000));
    }
}

/* OBSERVATION Q3:
 * SOLUTION-BEGIN
 * Chạy runExperiment(2_000_000) trên JDK 21 64-bit (compressed oops mặc định): dòng
 * "byte/phần tử" thường cho ArrayList<Integer> khoảng 24–32 byte/phần tử, còn
 * LinkedList<Integer> khoảng 48–56 byte/phần tử — gấp khoảng 1.5–2 lần ArrayList. Số tuyệt
 * đối dao động giữa các lần chạy vì phụ thuộc thời điểm GC/kích thước heap hiện tại, nhưng
 * LinkedList luôn cho số byte/phần tử lớn hơn rõ rệt so với ArrayList, đúng như giải thích ở
 * ANSWER Q3.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Mỗi phần tử của ArrayList chỉ tốn một ô tham chiếu trong mảng Object[] (elementData) được
 * cấp phát liên tục, cộng với object Integer mà nó trỏ tới (có thể tái dùng qua Integer cache
 * cho giá trị nhỏ). Mỗi phần tử của LinkedList lại cần thêm một object Node riêng biệt: object
 * header (thường 12–16 byte tuỳ JVM) cộng 3 tham chiếu (item, prev, next), rồi Node đó mới trỏ
 * tới object Integer. Vì vậy tổng chi phí byte/phần tử của LinkedList luôn lớn hơn ArrayList,
 * kể cả khi chỉ so sánh tương đối bằng Runtime.totalMemory()-freeMemory() như trên.
 * SOLUTION-END
 */

/* OBSERVATION Q5:
 * SOLUTION-BEGIN
 * Ở phần đo thời gian duyệt trong báo cáo, thứ tự thường là int[] nhanh nhất, ArrayList<Integer>
 * theo sát ngay sau, còn LinkedList<Integer> chậm hơn rõ rệt (thường vài lần) dù cả ba đều chỉ
 * làm một lượt for-each O(n). Số ns tuyệt đối dao động giữa các lần chạy (JIT chưa warm-up hết,
 * GC chạy giữa lúc đo), nhưng thứ tự nhanh–chậm giữa ba cấu trúc ổn định qua nhiều lần chạy lại
 * main() với n lớn.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * CPU đọc bộ nhớ theo từng cache line (thường 64 byte) và tự động prefetch các cache line kế
 * tiếp khi phát hiện truy cập tuần tự (spatial locality). ArrayList lưu các tham chiếu liên tiếp
 * trong một mảng nên duyệt for-each gần như luôn "trúng cache" — CPU nạp trước dữ liệu sắp cần.
 * LinkedList thì mỗi Node được cấp phát riêng lẻ trên heap tại thời điểm gọi new, không đảm bảo
 * nằm gần nhau; duyệt qua next/prev là "pointer chasing" — mỗi bước có thể nhảy tới một vùng nhớ
 * khác, gây cache miss và phải chờ nạp lại từ bộ nhớ chính (chậm hơn cache nhiều bậc). Đây là lý
 * do chính khiến ArrayList thường nhanh hơn LinkedList trên workload thực tế, dù độ phức tạp
 * Big-O của việc duyệt là O(n) như nhau ở cả hai.
 * SOLUTION-END
 */
