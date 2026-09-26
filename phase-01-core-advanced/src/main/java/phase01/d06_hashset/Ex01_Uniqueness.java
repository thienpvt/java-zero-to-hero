package phase01.d06_hashset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * HashSet — Bài 1: Uniqueness và lookup
 *
 * Nguồn: 01-java-core-advanced.md, mục 6 (HashSet), câu 1, 2, 4.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_UniquenessTest bằng nút ▶
 * cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q1 [DỰ ĐOÁN] HashSet phát hiện duplicate như thế nào?
 *   Bắt đầu   : điền 2 hằng số Q1_* bên dưới (thay null) bằng dự đoán của bạn.
 *   Kiểm chứng: chạy q01_prediction; nếu Q1_BACKING_MAP_CLASS sai, Ctrl+N mở HashSet,
 *               Ctrl+B vào add(E e) để đọc thân method (map.put(e, PRESENT) == null),
 *               Ctrl+F12 tìm field PRESENT ngay trong class HashSet.
 *   Code      : không.
 *   Hoàn thành khi: q01_prediction xanh và bạn giải thích được vì sao add() trả về
 *               boolean thay vì ném exception khi phần tử đã tồn tại.
 *
 * Q2 [CODE + THÍ NGHIỆM] HashSet khác ArrayList ở lookup thế nào?
 *   Bắt đầu   : cài đặt duplicates(List) và hasDuplicates(Collection) bên dưới.
 *   Kiểm chứng: chạy các test q02_duplicates... và q02_hasDuplicates...; sau đó chạy
 *               q02_experimentRuns hoặc main() (nút ▶ cạnh main) để tự thấy thời gian
 *               contains() thật trên ArrayList so với HashSet.
 *   Code      : duplicates(List<T>), hasDuplicates(Collection<T>).
 *   Hoàn thành khi: các test q02_* xanh và bạn đọc xong OBSERVATION Q2, giải thích được
 *               vì sao ArrayList.contains là O(n) (duyệt tuần tự so equals) còn
 *               HashSet.contains gần O(1) (tính hashCode() rồi nhảy thẳng tới bucket).
 *
 * Q4 [DỰ ĐOÁN + TỰ TRẢ LỜI] Vì sao mutable object trong HashSet nguy hiểm?
 *   Bắt đầu   : đọc class Tag bên dưới (equals/hashCode theo name), điền hằng số
 *               Q4_REMOVE_AFTER_MUTATION_WORKS.
 *   Kiểm chứng: chạy q04_prediction; nếu sai, đặt breakpoint trong HashMap.removeNode
 *               (Ctrl+N → HashMap → Ctrl+F12 → removeNode), Debug test q04_prediction,
 *               dùng F7 để thấy bucket tính từ hashCode() mới không chứa entry cũ.
 *   Code      : không.
 *   Hoàn thành khi: viết xong khối ANSWER Q4, so sánh được với d05_equals_hashcode,
 *               Ex03_RecordsAndMutableEntities, Q7 (Customer/StableCustomer).
 */
public class Ex01_Uniqueness {

    // Q1 — kịch bản: Set<String> set = new HashSet<>(); set.add("a"); set.add("a") lần hai
    static final Boolean Q1_ADD_DUPLICATE_RETURNS = false; // SOLUTION-VALUE
    static final String Q1_BACKING_MAP_CLASS = "HashMap"; // SOLUTION-VALUE

    /**
     * Trả về các phần tử bị trùng trong {@code items}, mỗi phần tử trùng xuất hiện đúng
     * một lần trong kết quả, theo thứ tự của lần xuất hiện <b>thứ hai</b> trong {@code items}.
     *
     * @throws NullPointerException nếu {@code items} là {@code null}
     */
    static <T> List<T> duplicates(List<T> items) {
        // SOLUTION-BEGIN throw Q2
        Objects.requireNonNull(items, "items");
        Set<T> seen = new HashSet<>();
        Set<T> duplicates = new LinkedHashSet<>();
        for (T item : items) {
            if (!seen.add(item)) {
                duplicates.add(item);
            }
        }
        return new ArrayList<>(duplicates);
        // SOLUTION-END
    }

    /**
     * @return {@code true} nếu {@code items} có ít nhất một phần tử xuất hiện nhiều hơn một lần.
     * @throws NullPointerException nếu {@code items} là {@code null}
     */
    static <T> boolean hasDuplicates(Collection<T> items) {
        // SOLUTION-BEGIN throw Q2
        Objects.requireNonNull(items, "items");
        return new HashSet<>(items).size() != items.size();
        // SOLUTION-END
    }

    /**
     * Đo thô thời gian gọi {@code contains()} {@code n} lần trên một {@code ArrayList} và
     * trên một {@code HashSet}, cả hai đều có {@code n} phần tử {@code Integer}. Có vòng
     * warm-up để JIT kịp tối ưu trước khi đo — đo đúng cách bằng JMH sẽ học ở Giai đoạn 2.
     */
    static String runExperiment(int n) {
        List<Integer> arrayList = new ArrayList<>();
        Set<Integer> hashSet = new HashSet<>();
        for (int i = 0; i < n; i++) {
            arrayList.add(i);
            hashSet.add(i);
        }
        for (int i = 0; i < 3; i++) {
            timeContainsAll(arrayList, n);
            timeContainsAll(hashSet, n);
        }
        long arrayNanos = timeContainsAll(arrayList, n);
        long hashNanos = timeContainsAll(hashSet, n);
        return String.format(Locale.ROOT,
                "contains() x %d lần: ArrayList = %.2f ms, HashSet = %.2f ms",
                n, arrayNanos / 1_000_000.0, hashNanos / 1_000_000.0);
    }

    private static long timeContainsAll(Collection<Integer> collection, int n) {
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            collection.contains(i);
        }
        return System.nanoTime() - start;
    }

    public static void main(String[] args) {
        System.out.println(runExperiment(20_000));
    }

    /** Thực thể có field mutable tham gia equals/hashCode — minh họa bug khi dùng trong HashSet. */
    static final class Tag {
        String name;

        Tag(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Tag t && Objects.equals(t.name, name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }

        @Override
        public String toString() {
            return "Tag[name=" + name + "]";
        }
    }

    // Q4 — kịch bản: set.add(new Tag("a")); rồi tag.name = "b"; set.remove(tag)
    static final Boolean Q4_REMOVE_AFTER_MUTATION_WORKS = false; // SOLUTION-VALUE
}

/* OBSERVATION Q2:
 * SOLUTION-BEGIN
 * Số liệu mẫu trên máy phát triển (tương đối, không phải benchmark chuẩn): với n = 20 000,
 * ArrayList.contains() tốn khoảng 80 ms cho toàn bộ n lần gọi (mỗi lần duyệt tuần tự, so
 * equals từng phần tử, O(n)) trong khi HashSet.contains() tốn dưới 1 ms cho toàn bộ n lần
 * gọi (mỗi lần chỉ tính hashCode() rồi nhảy thẳng tới bucket, trung bình O(1)) — chênh lệch
 * cỡ vài trăm lần. Chênh lệch càng rõ khi n càng lớn vì thời gian của ArrayList cho toàn bộ
 * thí nghiệm tăng theo n^2 (n lần gọi, mỗi lần O(n)), còn HashSet chỉ tăng theo n.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * hashCode() của Tag phụ thuộc name; khi name đổi sau khi add(), bucket tính từ hashCode()
 * mới không còn chứa phần tử đó nữa — remove()/contains() dùng key hiện tại (đã đổi) thất
 * bại, còn phần tử cũ vẫn "kẹt" trong bucket cũ (size không giảm) — cùng bản chất với vấn đề
 * ở d05_equals_hashcode, Ex03_RecordsAndMutableEntities Q7 (Customer/StableCustomer): field
 * tham gia equals/hashCode đổi sau khi add cũng làm contains/remove bằng key hiện tại thất bại.
 * Phòng tránh: chỉ cho field bất biến tham gia equals/hashCode (như StableCustomer chỉ dùng
 * id), hoặc remove trước khi đổi field rồi add lại như đã thấy ở d04_hashmap.changeIdSafely.
 * SOLUTION-END
 */
