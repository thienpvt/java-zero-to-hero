package phase01.d04_hashmap;

import java.util.HashMap;
import java.util.Map;
import phase01.support.Complexity;

/**
 * HashMap — Bài 3: Resize và treeify
 *
 * Nguồn: 01-java-core-advanced.md, mục 4 (HashMap), câu 7, 8, 11, 12.
 * Cần làm trước: Ex01_LookupAndCollision (khái niệm bucket, collision).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex03_ResizeAndTreeifyTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q7 [DỰ ĐOÁN + CODE] Load factor dùng để làm gì?
 *   Bắt đầu   : Ctrl+N mở HashMap, Ctrl+F12 tìm DEFAULT_INITIAL_CAPACITY và
 *               DEFAULT_LOAD_FACTOR trong source; điền hằng số Q7_DEFAULT_THRESHOLD.
 *   Kiểm chứng: chạy q07_defaultThreshold_prediction, sau đó cài đặt tableSizeFor,
 *               capacityFor, resizesNeeded và chạy các test q07_* còn lại.
 *   Code      : tableSizeFor(capacity) trả lũy thừa 2 nhỏ nhất ≥ capacity (biên: ≤1 → 1,
 *               &gt; 1&lt;&lt;30 → 1&lt;&lt;30); capacityFor(expectedSize) = tableSizeFor(ceil(expectedSize / 0.75))
 *               (expectedSize &lt; 0 → IllegalArgumentException); resizesNeeded(inserts, initialTableSize)
 *               mô phỏng: threshold = (int)(tableSize * 0.75), mỗi lần size vượt threshold sau khi
 *               thêm một phần tử thì gấp đôi tableSize, trả về số lần gấp đôi
 *               (initialTableSize phải là lũy thừa 2 dương, ngược lại IllegalArgumentException).
 *   Hoàn thành khi: mọi test q07_* xanh + giải thích được vì sao load factor càng nhỏ thì
 *               resize càng sớm (đổi tốc độ lookup lấy bộ nhớ).
 * <p>
 * Q8 [THÍ NGHIỆM] Resize ảnh hưởng performance thế nào?
 *   Bắt đầu   : đọc runExperiment(int) cho sẵn bên dưới (so thời gian put n entry vào
 *               new HashMap&lt;&gt;() so với HashMap.newHashMap(n)); chạy q08_experimentRuns.
 *   Kiểm chứng: chạy main(String[]) (biểu tượng ▶ cạnh main) với n lớn, đọc báo cáo in ra
 *               console; so hai con số thời gian.
 *   Hoàn thành khi: q08_experimentRuns xanh + viết xong khối OBSERVATION Q8 nêu vì sao báo
 *               trước capacity giúp tránh resize giữa chừng.
 * <p>
 * Q11 [DỰ ĐOÁN] Worst-case lookup của HashMap là gì?
 *   Bắt đầu   : Ctrl+Q trên javadoc đầu class java.util.HashMap, đọc đoạn nói về việc bucket
 *               chuyển thành cây khi vượt ngưỡng; điền hai hằng số Q11_WORST_CASE_*.
 *   Kiểm chứng: chạy q11_*_prediction; đối chiếu với ANSWER Q1/Q3 của Ex01 (bucket dạng chuỗi).
 *   Hoàn thành khi: q11_* xanh.
 * <p>
 * Q12 [DỰ ĐOÁN + THÍ NGHIỆM] Java hiện đại xử lý bucket có quá nhiều collision như thế nào?
 *   Bắt đầu   : Ctrl+N → HashMap → Ctrl+F12 → tìm hằng số TREEIFY_THRESHOLD và
 *               MIN_TREEIFY_CAPACITY trong source; điền hai hằng số Q12_*.
 *   Kiểm chứng: chạy q12_*_prediction, sau đó chạy lại q08_experimentRuns/main để xem phần
 *               so sánh lookup trên 5 000 key va chạm cùng hash giữa key Comparable và không.
 *   Hoàn thành khi: q12_* xanh + viết xong khối OBSERVATION Q12 giải thích vì sao key
 *               Comparable giúp cây so sánh hiệu quả hơn khi bucket bị treeify.
 */
public class Ex03_ResizeAndTreeify {

    // Q7 — kịch bản: ngưỡng resize mặc định của java.util.HashMap (capacity ban đầu nhân
    // load factor, làm tròn về int). Hai hằng JDK là package-private, đọc bằng Ctrl+F12
    // rồi tự tính — sau khi đã điền dự đoán mới chạy test.
    static final Integer Q7_DEFAULT_THRESHOLD = 12; // SOLUTION-VALUE

    /** Lũy thừa 2 nhỏ nhất ≥ capacity (thuật toán bit-or-shift giống HashMap.tableSizeFor cũ). */
    static int tableSizeFor(int capacity) {
        // SOLUTION-BEGIN throw Q7
        if (capacity <= 1) {
            return 1;
        }
        if (capacity > (1 << 30)) {
            return 1 << 30;
        }
        int n = capacity - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return n + 1;
        // SOLUTION-END
    }

    /**
     * Capacity cần để chứa {@code expectedSize} phần tử mà không phải resize, theo load factor 0.75.
     *
     * @throws IllegalArgumentException nếu {@code expectedSize} âm
     */
    static int capacityFor(int expectedSize) {
        // SOLUTION-BEGIN throw Q7
        if (expectedSize < 0) {
            throw new IllegalArgumentException("expectedSize không được âm: " + expectedSize);
        }
        return tableSizeFor((int) Math.ceil(expectedSize / 0.75));
        // SOLUTION-END
    }

    /**
     * Mô phỏng số lần bảng phải gấp đôi khi thêm tuần tự {@code inserts} phần tử vào bảng
     * bắt đầu với kích thước {@code initialTableSize}, load factor 0.75.
     *
     * @throws IllegalArgumentException nếu {@code initialTableSize} không phải lũy thừa 2 dương
     */
    static int resizesNeeded(int inserts, int initialTableSize) {
        // SOLUTION-BEGIN throw Q7
        if (initialTableSize <= 0 || (initialTableSize & (initialTableSize - 1)) != 0) {
            throw new IllegalArgumentException(
                    "initialTableSize phải là lũy thừa của 2 và dương: " + initialTableSize);
        }
        int tableSize = initialTableSize;
        int threshold = (int) (tableSize * 0.75);
        int size = 0;
        int resizes = 0;
        for (int i = 0; i < inserts; i++) {
            size++;
            if (size > threshold) {
                tableSize *= 2;
                threshold = (int) (tableSize * 0.75);
                resizes++;
            }
        }
        return resizes;
        // SOLUTION-END
    }

    /** Key va chạm hash cố ý, KHÔNG Comparable — dùng để so sánh với ComparableCollidingKey. */
    private static final class CollidingKey {
        final int id;

        CollidingKey(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof CollidingKey k && k.id == id;
        }

        @Override
        public int hashCode() {
            return 7;
        }
    }

    /** Key va chạm hash cố ý, có Comparable — HashMap dùng compareTo để tách nhánh khi treeify. */
    private static final class ComparableCollidingKey implements Comparable<ComparableCollidingKey> {
        final int id;

        ComparableCollidingKey(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ComparableCollidingKey k && k.id == id;
        }

        @Override
        public int hashCode() {
            return 7;
        }

        @Override
        public int compareTo(ComparableCollidingKey other) {
            return Integer.compare(id, other.id);
        }
    }

    private static long timePutDefaultCapacity(int n) {
        long start = System.nanoTime();
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            map.put(i, i);
        }
        return System.nanoTime() - start;
    }

    private static long timePutPreSizedCapacity(int n) {
        long start = System.nanoTime();
        Map<Integer, Integer> map = HashMap.newHashMap(n);
        for (int i = 0; i < n; i++) {
            map.put(i, i);
        }
        return System.nanoTime() - start;
    }

    private static long timeLookupComparableCollidingKeys(int n) {
        Map<ComparableCollidingKey, Integer> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            map.put(new ComparableCollidingKey(i), i);
        }
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            map.get(new ComparableCollidingKey(i));
        }
        return System.nanoTime() - start;
    }

    private static long timeLookupNonComparableCollidingKeys(int n) {
        Map<CollidingKey, Integer> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            map.put(new CollidingKey(i), i);
        }
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            map.get(new CollidingKey(i));
        }
        return System.nanoTime() - start;
    }

    /**
     * Đo thô (System.nanoTime, có warm-up) hai kịch bản: (1) put n entry vào HashMap mặc định
     * so với HashMap đã báo trước capacity; (2) lookup 5 000 key va chạm cùng hashCode khi key
     * có Comparable so với không. Chỉ để quan sát bằng mắt trong main(), không dùng để assert
     * số đo chính xác trong test (đo đúng cách sẽ học ở Giai đoạn 2 với JMH).
     */
    static String runExperiment(int n) {
        int warmup = Math.max(1, Math.min(n, 500));
        timePutDefaultCapacity(warmup);
        timePutPreSizedCapacity(warmup);

        long defaultNanos = timePutDefaultCapacity(n);
        long preSizedNanos = timePutPreSizedCapacity(n);

        int collisionCount = 5_000;
        long comparableNanos = timeLookupComparableCollidingKeys(collisionCount);
        long nonComparableNanos = timeLookupNonComparableCollidingKeys(collisionCount);

        StringBuilder report = new StringBuilder();
        report.append("Put ").append(n).append(" entry — new HashMap<>(): ")
                .append(defaultNanos / 1_000_000.0).append(" ms; HashMap.newHashMap(n): ")
                .append(preSizedNanos / 1_000_000.0).append(" ms (báo trước capacity thường ít resize hơn)\n");
        report.append("Lookup ").append(collisionCount).append(" key cùng hashCode — Comparable: ")
                .append(comparableNanos / 1_000_000.0).append(" ms; không Comparable: ")
                .append(nonComparableNanos / 1_000_000.0)
                .append(" ms (bucket bị treeify, key Comparable giúp so sánh trong cây hiệu quả hơn)");
        return report.toString();
    }

    public static void main(String[] args) {
        System.out.println(runExperiment(50_000));
    }

    // Q11 — kịch bản: worst-case lookup khi mọi key rơi vào một bucket dạng danh sách liên
    // kết, so với bucket đã treeify và key Comparable. Điền Complexity, không đo runtime.
    static final Complexity Q11_WORST_CASE_LINKED_BUCKET = Complexity.O_N; // SOLUTION-VALUE
    static final Complexity Q11_WORST_CASE_TREE_BIN_COMPARABLE_KEYS = Complexity.O_LOG_N; // SOLUTION-VALUE

    // Q12 — TREEIFY_THRESHOLD và MIN_TREEIFY_CAPACITY của java.util.HashMap (package-private
    // trong source JDK nên không gọi được trực tiếp, chỉ đọc bằng Ctrl+F12 rồi chép lại).
    static final Integer Q12_TREEIFY_THRESHOLD = 8; // SOLUTION-VALUE
    static final Integer Q12_MIN_TREEIFY_CAPACITY = 64; // SOLUTION-VALUE
}

/* OBSERVATION Q8:
 * SOLUTION-BEGIN
 * HashMap.newHashMap(n) cấp sẵn bảng đủ lớn cho n phần tử nên tránh được các lần gấp đôi +
 * rehash toàn bộ entry giữa chừng; với n lớn (ví dụ 50 000), bản báo trước capacity thường đo
 * nhanh hơn rõ rệt so với new HashMap<>() (phải resize nhiều lần từ 16 lên dần). Trade-off: phải
 * biết trước (ước lượng) số phần tử, và nếu ước lượng dư quá nhiều thì tốn bộ nhớ vô ích.
 * SOLUTION-END
 */

/* OBSERVATION Q12:
 * SOLUTION-BEGIN
 * Khi một bucket có ≥ TREEIFY_THRESHOLD (8) entry và bảng có capacity ≥ MIN_TREEIFY_CAPACITY
 * (64), HashMap chuyển bucket đó từ danh sách liên kết sang cây đỏ-đen (TreeNode), đưa worst-case
 * lookup trong bucket từ O(n) xuống O(log n). Cây cần một cách sắp thứ tự: nếu key Comparable thì
 * dùng compareTo() (ổn định, nhanh); nếu không, HashMap dùng thứ tự phụ (so tên class, rồi
 * System.identityHashCode) chỉ để tie-break, không hiệu quả bằng so sánh Comparable thật.
 * SOLUTION-END
 */
