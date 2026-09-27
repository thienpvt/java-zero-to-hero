package phase00.d10_generics;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import phase00.support.Compiles;

/**
 * Generics — Bài 1: List có kiểu và method generic
 *
 * Nguồn: 00-java-basics-review.md, mục 10 (Generics ở mức cơ bản), câu 1, 2, 3.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_GenericFirstTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q1 [TỰ TRẢ LỜI] {@code List&lt;String&gt;} giúp tránh lỗi nào so với raw {@code List}?
 *   Bắt đầu   : làm Q2 trước để thấy raw list vỡ lúc nào, rồi viết ANSWER Q1.
 *   Tra cứu   : Ctrl+Q trên {@code List}. Đọc cảnh báo của IDE trên {@code rawListFailure}.
 *   Hoàn thành khi: ANSWER Q1 nói lỗi bị chặn lúc biên dịch thay vì lúc lấy phần tử ra.
 * <p>
 * Q2 [DỰ ĐOÁN] Có thể thêm {@code Integer} vào {@code List&lt;String&gt;} bình thường không?
 *   Bắt đầu   : điền Q2_ADD_INTEGER_COMPILES. Bỏ comment hai dòng trong {@code rejectedAdd} để xem lỗi đỏ,
 *               rồi comment lại. Điền Q2_RAW_GET_EXCEPTION bằng tên lớp từ {@code rawListFailure}.
 *   Kiểm chứng: chạy q02_prediction. Debug {@code rawListFailure}, F8 tới {@code return names.get(0)}.
 *   Hoàn thành khi: q02_prediction xanh.
 * <p>
 * Q3 [CODE] Khi nào một phương thức tiện ích nên là {@code &lt;T&gt; T first(List&lt;T&gt; items)}?
 *   Bắt đầu   : cài {@code first}. Cùng một thân hàm phải chạy được với {@code String} và {@code Integer}.
 *   Kiểm chứng: chạy q03_firstOfStringAndInteger. Ctrl+Q trên {@code List.get}.
 *   Code      : list {@code null} → {@code NullPointerException}. List rỗng → {@code NoSuchElementException}.
 *               Ngược lại trả {@code get(0)}.
 *   Hoàn thành khi: q03_* xanh; nói được {@code &lt;T&gt;} để không viết một bản cho mỗi kiểu phần tử.
 */
public class Ex01_GenericFirst {

    @SuppressWarnings("unused")
    static void rejectedAdd() {
        // List<String> names = new ArrayList<>();
        // names.add(Integer.valueOf(1));
    }

    static final Compiles Q2_ADD_INTEGER_COMPILES = Compiles.NO; // SOLUTION-VALUE

    @SuppressWarnings({"rawtypes", "unchecked"})
    static String rawListFailure() {
        List raw = new ArrayList();
        raw.add(Integer.valueOf(1));
        List<String> names = raw;
        return names.get(0);
    }

    // Q2 — tên lớp exception khi rawListFailure() chạy.
    static final String Q2_RAW_GET_EXCEPTION = "ClassCastException"; // SOLUTION-VALUE

    static <T> T first(List<T> items) {
        // SOLUTION-BEGIN throw Q3
        Objects.requireNonNull(items, "items");
        if (items.isEmpty()) {
            throw new NoSuchElementException("list rỗng");
        }
        return items.get(0);
        // SOLUTION-END
    }

    /* ANSWER Q1:
     * SOLUTION-BEGIN
     * List<String> để compiler từ chối names.add(một Integer) ngay khi biên dịch. Raw List nhận mọi
     * object; lỗi chỉ hiện khi lấy phần tử ra và gán sang String — ClassCastException lúc chạy,
     * xa chỗ đã add nhầm. Generic không thay runtime kiểm tra từng lần get; nó chặn sớm ở source.
     * SOLUTION-END
     */

    /* ANSWER Q3:
     * SOLUTION-BEGIN
     * Method generic <T> T first(List<T>) khi thuật toán không quan tâm T là gì, chỉ cần cùng một kiểu
     * cho đầu vào và giá trị trả về. Viết một lần, gọi với List<String> hay List<Integer> đều được,
     * và compiler giữ kiểu trả về đúng chỗ gọi. Nếu cố định List<String> thì không tái sử dụng được.
     * SOLUTION-END
     */
}
