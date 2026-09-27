package phase01.d06_hashset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * HashSet — Bài 2: HashSet, LinkedHashSet, TreeSet
 *
 * Nguồn: 01-java-core-advanced.md, mục 6 (HashSet), câu 3, 5, 6.
 * Cần làm trước: Ex01_Uniqueness (luồng add() → backing HashMap).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_SetVariantsTest bằng nút ▶
 * cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q3 [DỰ ĐOÁN] HashSet có đảm bảo iteration order không?
 *   Bắt đầu   : chạy main() bên dưới (nút ▶ cạnh main) để thấy thứ tự in ra thật của
 *               "banana","apple","cherry","date" sau khi add vào HashSet.
 *   Kiểm chứng: điền Q3_HASHSET_GUARANTEES_ORDER trước; sau khi đã điền dự đoán, Ctrl+Q
 *               khi con trỏ ở tên HashSet và đọc đoạn Javadoc về iteration order.
 *   Code      : không.
 *   Hoàn thành khi: q03_prediction xanh, giải thích được thứ tự in ra phụ thuộc
 *               hashCode() và cấu trúc bucket của HashMap phía sau, không phải thứ tự add.
 * <p>
 * Q5 [CODE] {@code LinkedHashSet} khác HashSet ra sao?
 *   Bắt đầu   : cài đặt distinctInFirstSeenOrder(List) bên dưới bằng LinkedHashSet.
 *   Kiểm chứng: chạy q05_*; Ctrl+N mở LinkedHashSet, Ctrl+F12 tìm nested class Entry của
 *               LinkedHashMap, xem field before/after (danh sách liên kết đôi giữ thứ tự)
 *               so với HashMap.Node (chỉ có next).
 *   Code      : distinctInFirstSeenOrder(List&lt;T&gt;).
 *   Hoàn thành khi: test q05_* xanh, giải thích được LinkedHashSet giữ đúng thứ tự chèn
 *               nhờ danh sách liên kết phụ, đổi lại tốn thêm bộ nhớ/thời gian so với HashSet.
 * <p>
 * Q6 [DỰ ĐOÁN + CODE] {@code TreeSet} cần Comparable/Comparator vì sao?
 *   Bắt đầu   : điền Q6_TREESET_NON_COMPARABLE_EXCEPTION; cài đặt distinctSorted(Collection)
 *               và byAgeThenName(Collection) bên dưới.
 *   Kiểm chứng: chạy q06_prediction; sau khi đã điền dự đoán, Debug q06_prediction hoặc
 *               Ctrl+B vào TreeSet.add(E) rồi F7 Step Into tới TreeMap.put để quan sát
 *               exception khi phần tử không Comparable và TreeSet không có Comparator.
 *   Code      : distinctSorted, byAgeThenName (dùng
 *               Comparator.comparingInt(Person::age).thenComparing(Person::name)).
 *   Hoàn thành khi: test q06_* xanh, giải thích được TreeSet dùng compare()/compareTo()
 *               (không phải equals/hashCode) để vừa sắp xếp vừa coi hai phần tử compare
 *               bằng 0 là "trùng" nên bị loại.
 */
public class Ex02_SetVariants {

    // Q3 — kịch bản: HashSet có cam kết thứ tự duyệt không? Đọc Javadoc HashSet, rồi điền.
    static final Boolean Q3_HASHSET_GUARANTEES_ORDER = null;

    /**
     * Trả về các phần tử phân biệt trong {@code items}, theo đúng thứ tự lần xuất hiện
     * &lt;b&gt;đầu tiên&lt;/b&gt; của mỗi phần tử.
     *
     * @throws NullPointerException nếu {@code items} là {@code null}
     */
    static <T> List<T> distinctInFirstSeenOrder(List<T> items) {
        throw new UnsupportedOperationException("TODO Q5");
    }

    // Q6 — kịch bản: new TreeSet<Object>().add(new Object())
    static final String Q6_TREESET_NON_COMPARABLE_EXCEPTION = null;

    /** Người, dùng cho các câu về TreeSet: sắp theo tuổi rồi theo tên khi cần thứ tự tất định. */
    record Person(String name, int age) {
    }

    /**
     * Trả về các phần tử phân biệt trong {@code items}, sắp xếp tăng dần theo thứ tự tự nhiên.
     *
     * @throws NullPointerException nếu {@code items} là {@code null}
     */
    static <T extends Comparable<? super T>> List<T> distinctSorted(Collection<T> items) {
        throw new UnsupportedOperationException("TODO Q6");
    }

    /**
     * Trả về {@code people} dưới dạng {@link NavigableSet}, sắp theo tuổi tăng dần, cùng
     * tuổi thì sắp theo tên; hai người cùng tuổi và cùng tên coi là trùng (chỉ giữ một).
     *
     * @throws NullPointerException nếu {@code people} là {@code null}
     */
    static NavigableSet<Person> byAgeThenName(Collection<Person> people) {
        throw new UnsupportedOperationException("TODO Q6");
    }

    public static void main(String[] args) {
        Set<String> fruits = new HashSet<>();
        fruits.add("banana");
        fruits.add("apple");
        fruits.add("cherry");
        fruits.add("date");
        System.out.println("Thứ tự thêm : banana, apple, cherry, date");
        System.out.println("Thứ tự duyệt: " + fruits);
    }
}
