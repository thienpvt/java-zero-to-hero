package phase01.d01_generics;

import java.util.List;
import phase01.support.Compiles;

/**
 * Generics — Bài 3: Generic method (type parameter) so với wildcard
 *
 * Nguồn: 01-java-core-advanced.md, mục 1 (Generics), câu 8.
 * Cần làm trước: Ex01_InvarianceAndWildcards (wildcard {@code ? extends}/{@code ? super}, PECS).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex03_GenericMethodVsWildcardTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q8 [DỰ ĐOÁN + CODE] So sánh: {@code &lt;T&gt; void process(List&lt;T&gt; list)} và {@code void process(List&lt;?&gt; list)}
 *   Bắt đầu   : điền 2 hằng Q8_* bên dưới; bỏ comment từng dòng mẫu ngay cạnh mỗi hằng
 *               (trên một biến {@code List&lt;?&gt; list}), xem IDE báo gì, rồi comment lại — sau khi
 *               đã điền dự đoán.
 *   Kiểm chứng: chạy q08_*; đặt breakpoint trong swapHelper, Debug test, F7 để thấy
 *               compiler "capture" wildcard {@code ?} của swapFirstLast thành một kiểu T cụ thể
 *               khi gọi sang swapHelper.
 *   Code      : swapFirstLast đổi chỗ phần tử đầu và cuối của list (list có &lt; 2 phần tử thì
 *               giữ nguyên); bắt buộc gọi helper private {@code swapHelper(List&lt;T&gt; list)} để có
 *               một tên kiểu T dùng chung cho get/set (wildcard capture) — không cast thủ công.
 *   Hoàn thành khi: các test q08_* xanh; viết xong khối ANSWER Q8 ở cuối file, giải thích
 *               khi nào chọn type parameter và khi nào chọn wildcard.
 */
public class Ex03_GenericMethodVsWildcard {

    // Q8 — mẫu: với một biến `List<?> list = new ArrayList<>(List.of("a"));`,
    // bỏ comment dòng dưới, xem IDE báo gì, rồi comment lại — sau khi đã điền dự đoán.
    // list.add("x");
    static final Compiles Q8_ADD_STRING_TO_WILDCARD_LIST_COMPILES = Compiles.NO; // SOLUTION-VALUE

    // Q8 — mẫu: cùng biến `List<?> list`, bỏ comment dòng dưới, xem IDE báo gì, rồi
    // comment lại — sau khi đã điền dự đoán.
    // list.add(null);
    static final Compiles Q8_ADD_NULL_TO_WILDCARD_LIST_COMPILES = Compiles.YES; // SOLUTION-VALUE

    /**
     * Đổi chỗ phần tử đầu và phần tử cuối của {@code list}. Nếu {@code list} có ít hơn 2
     * phần tử thì giữ nguyên.
     */
    static void swapFirstLast(List<?> list) {
        // SOLUTION-BEGIN throw Q8
        if (list.size() < 2) {
            return;
        }
        swapHelper(list);
        // SOLUTION-END
    }

    /**
     * Wildcard capture: gán một tên kiểu {@code T} cụ thể cho {@code ?} của
     * {@code swapFirstLast} để có thể {@code get}/{@code set} an toàn kiểu.
     */
    private static <T> void swapHelper(List<T> list) {
        // SOLUTION-BEGIN throw Q8
        T first = list.get(0);
        T last = list.get(list.size() - 1);
        list.set(0, last);
        list.set(list.size() - 1, first);
        // SOLUTION-END
    }
}

/* ANSWER Q8:
 * SOLUTION-BEGIN
 * Dùng type parameter <T> khi cần diễn tả quan hệ giữa nhiều tham số hoặc giữa tham số và
 * giá trị trả về — ví dụ cần lấy một phần tử ra (get) rồi ghi lại đúng kiểu đó (set) như
 * swapHelper, hoặc method trả về T dựa trên list đầu vào. Dùng wildcard <?> khi method chỉ
 * cần đọc phần tử ra Object hoặc gọi các thao tác không phụ thuộc kiểu phần tử cụ thể (như
 * size(), hoặc add(null)) và không cần liên kết kiểu đó với bất cứ chỗ nào khác — API gọn
 * hơn cho người gọi vì họ không cần biết/khai type argument. Đổi lại, List<?> gần như
 * read-only: không add(T)/set(T) được (trừ null) vì compiler không biết ? đại diện cho kiểu
 * gì, đó là lý do swapFirstLast phải "capture" wildcard qua swapHelper mới set() được.
 * SOLUTION-END
 */
