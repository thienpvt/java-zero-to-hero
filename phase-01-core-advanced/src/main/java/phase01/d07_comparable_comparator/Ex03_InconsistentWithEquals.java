package phase01.d07_comparable_comparator;

import java.util.Comparator;

/**
 * Comparable & Comparator — Bài 3: Comparator không consistent với equals
 *
 * Nguồn: 01-java-core-advanced.md, mục 7 (Comparable & Comparator), câu 4.
 * Cần làm trước: Ex01_NaturalVsCustomOrder (Comparator.comparingInt, thenComparing).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex03_InconsistentWithEqualsTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q4 [DỰ ĐOÁN + CODE] Nếu Comparator không consistent với equals thì điều gì có thể
 *     xảy ra với TreeSet?
 *   Bắt đầu   : đọc record Person bên dưới; đặt con trỏ lên "TreeSet" trong test, Ctrl+Q
 *               để đọc đoạn Javadoc "Note that the ordering maintained by a set ... must
 *               be consistent with equals if it is to correctly implement the Set interface".
 *   Kiểm chứng: chạy q04_prediction; nếu sai, đặt breakpoint trong TreeMap.put
 *               (Ctrl+N → TreeMap → Ctrl+F12 → put), Debug test, F7 Step Into để thấy
 *               compare() trả 0 giữa hai người khác nhau khiến entry mới không được thêm.
 *   Code      : cài đặt consistentByAge() — so age rồi (cùng age) so name, để không còn
 *               hai người khác nhau bị Comparator coi là "bằng nhau" một cách sai lệch.
 *   Hoàn thành khi: q04_* xanh; giải thích được vì sao TreeSet dùng Comparator.compare()
 *               (không dùng equals()) để quyết định trùng lặp, nên Comparator chỉ so age
 *               (bỏ qua name) làm mất người có cùng tuổi dù họ không equals() nhau.
 */
public class Ex03_InconsistentWithEquals {

    record Person(String name, int age) {
    }

    // Q4 — kịch bản: TreeSet(Comparator.comparingInt(Person::age)) thêm An(30), Bình(30), Chi(25)
    static final Integer Q4_TREESET_SIZE = 2; // SOLUTION-VALUE
    static final Integer Q4_HASHSET_SIZE = 3; // SOLUTION-VALUE
    static final Boolean Q4_TREESET_CONTAINS_BINH = true; // SOLUTION-VALUE

    static Comparator<Person> consistentByAge() {
        // SOLUTION-BEGIN throw Q4
        return Comparator.comparingInt(Person::age).thenComparing(Person::name);
        // SOLUTION-END
    }
}
