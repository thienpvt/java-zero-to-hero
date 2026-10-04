package phase01.d01_generics;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import phase01.support.Compiles;

/**
 * Generics — Bài 1: Invariance và wildcard (? extends / ? super)
 *
 * Nguồn: 01-java-core-advanced.md, mục 1 (Generics), câu 1, 9, 2, 3, 4.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_InvarianceAndWildcardsTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q1 [DỰ ĐOÁN] {@code List<String>} có phải subtype của {@code List<Object>} không? Tại sao?
 *   Bắt đầu   : điền 3 hằng số Q1_* bên dưới (thay null); bỏ comment từng dòng mẫu
 *               ngay cạnh mỗi hằng {@code Compiles}, xem IDE báo gì, rồi comment lại — sau khi
 *               đã điền dự đoán — trước khi chạy test.
 *   Kiểm chứng: chạy q01_prediction; hằng Q1_ARRAY_STORE_EXCEPTION được test tính bằng
 *               cách chạy thật {@code Object[] arr = new String[1]; arr[0] = 1;} và bắt lỗi.
 *   Hoàn thành khi: q01_prediction xanh; giải thích được quy tắc gán của mảng khác quy tắc
 *               gán của generic ở chỗ nào, và lỗi (nếu có) xảy ra lúc biên dịch hay lúc chạy.
 * <p>
 * Q9 [TỰ TRẢ LỜI] Generic invariance là gì?
 *   Bắt đầu   : làm Q1 trước, dùng chính kết quả quan sát được ở Q1 để trả lời.
 *   Kiểm chứng: Tra cứu — không có test riêng, đọc lại 3 hằng Q1_* vừa điền.
 *   Hoàn thành khi: viết xong khối ANSWER Q9 ở cuối file, có nêu định nghĩa invariance.
 * <p>
 * Q2 [CODE] Khi nào dùng {@code ? extends T}?
 *   Bắt đầu   : cài đặt sum(Collection&lt;? extends Number&gt; numbers) bên dưới.
 *   Kiểm chứng: chạy q02_sum_*; đặt breakpoint ở dòng {@code total += n.doubleValue();},
 *               Debug test, dùng F7 để xem từng phần tử được cộng vào total.
 *   Code      : sum trả tổng doubleValue() của mọi phần tử; collection rỗng trả 0.0.
 *   Hoàn thành khi: các test q02_* xanh; giải thích được vì sao {@code ? extends Number}
 *               cho phép truyền cả {@code List<Integer>} và {@code List<Double>} mà {@code Number} không cho.
 * <p>
 * Q3 [CODE] Khi nào dùng {@code ? super T}?
 *   Bắt đầu   : cài đặt addNumbers(List&lt;? super Integer&gt; target, int count) bên dưới.
 *   Kiểm chứng: chạy q03_*; thử tạm đổi tham số thành {@code List<Integer>} rồi gọi với
 *               {@code List<Number>}, xem IDE báo gì, rồi đổi lại {@code ? super Integer} trước khi nộp.
 *   Code      : thêm các số 0..count-1 vào target theo thứ tự tăng dần;
 *               count &lt; 0 ném IllegalArgumentException, không đổi target.
 *   Hoàn thành khi: các test q03_* xanh; giải thích được vì sao {@code ? super Integer} nhận
 *               được {@code List<Number>}/{@code List<Object>} nhưng không cho đọc phần tử ra kiểu Integer.
 * <p>
 * Q4 [CODE] Giải thích PECS bằng ví dụ thực tế.
 *   Bắt đầu   : PECS = Producer Extends, Consumer Super. {@code Ctrl+N} → gõ {@code Collections} →
 *               {@code Ctrl+F12} → tìm method {@code copy}, đọc chữ ký
 *               {@code copy(List<? super T> dest, List<? extends T> src)} của JDK để so sánh.
 *   Kiểm chứng: chạy q04_*; đặt breakpoint đầu copy() và max(), Debug test, F7 qua từng bước.
 *   Code      : copy() nối toàn bộ src vào cuối dst, giữ thứ tự; max() trả phần tử lớn nhất
 *               theo comparator, collection rỗng ném NoSuchElementException.
 *   Hoàn thành khi: các test q04_* xanh; giải thích được: src là Producer (chỉ đọc ra T
 *               nên dùng extends), dst là Consumer (chỉ nhận T vào nên dùng super).
 */
public class Ex01_InvarianceAndWildcards {

    // Q1 — mẫu: List<Object> objects = new ArrayList<String>();
    // Bỏ comment dòng dưới, xem IDE báo gì, rồi comment lại — sau khi đã điền dự đoán.
    // List<Object> objects = new ArrayList<String>();
    static final Compiles Q1_LIST_STRING_TO_LIST_OBJECT_COMPILES = null;

    // Q1 — mẫu: Object[] objects = new String[1];
    // Bỏ comment dòng dưới, xem IDE báo gì, rồi comment lại — sau khi đã điền dự đoán.
    // Object[] objects = new String[1];
    static final Compiles Q1_STRING_ARRAY_TO_OBJECT_ARRAY_COMPILES = null;

    // Q1 — kịch bản: Object[] trỏ tới new String[1], gán một phần tử không phải String.
    // Ghi simple name của exception lúc chạy.
    static final String Q1_ARRAY_STORE_EXCEPTION = null;

    /**
     * Tổng {@code doubleValue()} của mọi phần tử trong {@code numbers}.
     *
     * @return {@code 0.0} nếu {@code numbers} rỗng
     */
    static double sum(Collection<? extends Number> numbers) {
        throw new UnsupportedOperationException("TODO Q2");
    }

    /**
     * Thêm các số nguyên {@code 0..count-1} (theo thứ tự tăng dần) vào {@code target}.
     *
     * @throws IllegalArgumentException nếu {@code count < 0}; khi đó {@code target} giữ nguyên
     */
    static void addNumbers(List<? super Integer> target, int count) {
        throw new UnsupportedOperationException("TODO Q3");
    }

    /**
     * Nối toàn bộ {@code src} vào cuối {@code dst}, giữ nguyên thứ tự (ví dụ PECS: src là
     * Producer nên dùng {@code extends}, dst là Consumer nên dùng {@code super}).
     */
    static <T> void copy(List<? super T> dst, List<? extends T> src) {
        throw new UnsupportedOperationException("TODO Q4");
    }

    /**
     * Phần tử lớn nhất trong {@code items} theo {@code comparator}.
     *
     * @throws NoSuchElementException nếu {@code items} rỗng
     */
    static <T> T max(Collection<? extends T> items, Comparator<? super T> comparator) {
        throw new UnsupportedOperationException("TODO Q4");
    }
}

/* ANSWER Q9:
 *
 */
