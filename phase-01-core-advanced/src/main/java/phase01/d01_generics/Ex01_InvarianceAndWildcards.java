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
 * Q1 [DỰ ĐOÁN] `List<String>` có phải subtype của `List<Object>` không? Tại sao?
 *   Bắt đầu   : điền 3 hằng số Q1_* bên dưới (thay null); bỏ comment từng dòng mẫu
 *               ngay cạnh mỗi hằng `Compiles` để tự thấy dòng nào bị gạch đỏ lúc biên dịch,
 *               rồi comment lại trước khi chạy test.
 *   Kiểm chứng: chạy q01_prediction; hằng Q1_ARRAY_STORE_EXCEPTION được test tính bằng
 *               cách chạy thật `Object[] arr = new String[1]; arr[0] = 1;` và bắt lỗi.
 *   Hoàn thành khi: q01_prediction xanh; giải thích được vì sao mảng cho biên dịch qua
 *               (covariant) rồi lỗi lúc chạy, còn generic chặn ngay lúc biên dịch (invariant).
 *
 * Q9 [TỰ TRẢ LỜI] Generic invariance là gì?
 *   Bắt đầu   : làm Q1 trước, dùng chính kết quả quan sát được ở Q1 để trả lời.
 *   Kiểm chứng: Tra cứu — không có test riêng, đọc lại 3 hằng Q1_* vừa điền.
 *   Hoàn thành khi: viết xong khối ANSWER Q9 ở cuối file, có nêu định nghĩa invariance.
 *
 * Q2 [CODE] Khi nào dùng `? extends T`?
 *   Bắt đầu   : cài đặt sum(Collection<? extends Number> numbers) bên dưới.
 *   Kiểm chứng: chạy q02_sum_*; đặt breakpoint ở dòng `total += n.doubleValue();`,
 *               Debug test, dùng F7 để xem từng phần tử được cộng vào total.
 *   Code      : sum trả tổng doubleValue() của mọi phần tử; collection rỗng trả 0.0.
 *   Hoàn thành khi: các test q02_* xanh; giải thích được vì sao `? extends Number`
 *               cho phép truyền cả `List<Integer>` và `List<Double>` mà `Number` không cho.
 *
 * Q3 [CODE] Khi nào dùng `? super T`?
 *   Bắt đầu   : cài đặt addNumbers(List<? super Integer> target, int count) bên dưới.
 *   Kiểm chứng: chạy q03_*; thử tạm đổi tham số thành `List<Integer>` rồi gọi với
 *               `List<Number>` để tự thấy lỗi đỏ (đổi lại `? super Integer` trước khi nộp).
 *   Code      : thêm các số 0..count-1 vào target theo thứ tự tăng dần;
 *               count < 0 ném IllegalArgumentException, không đổi target.
 *   Hoàn thành khi: các test q03_* xanh; giải thích được vì sao `? super Integer` nhận
 *               được `List<Number>`/`List<Object>` nhưng không cho đọc phần tử ra kiểu Integer.
 *
 * Q4 [CODE] Giải thích PECS (Producer Extends, Consumer Super) bằng ví dụ thực tế.
 *   Bắt đầu   : `Ctrl+N` → gõ `Collections` → `Ctrl+F12` → tìm method `copy`, đọc chữ ký
 *               `copy(List<? super T> dest, List<? extends T> src)` của JDK để so sánh.
 *   Kiểm chứng: chạy q04_*; đặt breakpoint đầu copy() và max(), Debug test, F7 qua từng bước.
 *   Code      : copy() nối toàn bộ src vào cuối dst, giữ thứ tự; max() trả phần tử lớn nhất
 *               theo comparator, collection rỗng ném NoSuchElementException.
 *   Hoàn thành khi: các test q04_* xanh; giải thích được: src là Producer (chỉ đọc ra T
 *               nên dùng extends), dst là Consumer (chỉ nhận T vào nên dùng super).
 */
public class Ex01_InvarianceAndWildcards {

    // Q1 — mẫu: bỏ comment dòng dưới để tự thấy lỗi đỏ lúc biên dịch (generic invariance)
    // List<Object> objects = new ArrayList<String>();
    static final Compiles Q1_LIST_STRING_TO_LIST_OBJECT_COMPILES = Compiles.NO; // SOLUTION-VALUE

    // Q1 — mẫu: bỏ comment dòng dưới, KHÔNG lỗi biên dịch vì mảng là covariant
    // Object[] objects = new String[1];
    static final Compiles Q1_STRING_ARRAY_TO_OBJECT_ARRAY_COMPILES = Compiles.YES; // SOLUTION-VALUE

    // Q1 — tên ngoại lệ ném ra lúc chạy khi gán sai kiểu phần tử vào mảng covariant
    static final String Q1_ARRAY_STORE_EXCEPTION = "ArrayStoreException"; // SOLUTION-VALUE

    /**
     * Tổng {@code doubleValue()} của mọi phần tử trong {@code numbers}.
     *
     * @return {@code 0.0} nếu {@code numbers} rỗng
     */
    static double sum(Collection<? extends Number> numbers) {
        // SOLUTION-BEGIN throw Q2
        double total = 0.0;
        for (Number n : numbers) {
            total += n.doubleValue();
        }
        return total;
        // SOLUTION-END
    }

    /**
     * Thêm các số nguyên {@code 0..count-1} (theo thứ tự tăng dần) vào {@code target}.
     *
     * @throws IllegalArgumentException nếu {@code count < 0}; khi đó {@code target} giữ nguyên
     */
    static void addNumbers(List<? super Integer> target, int count) {
        // SOLUTION-BEGIN throw Q3
        if (count < 0) {
            throw new IllegalArgumentException("count không được âm: " + count);
        }
        for (int i = 0; i < count; i++) {
            target.add(i);
        }
        // SOLUTION-END
    }

    /**
     * Nối toàn bộ {@code src} vào cuối {@code dst}, giữ nguyên thứ tự (ví dụ PECS: src là
     * Producer nên dùng {@code extends}, dst là Consumer nên dùng {@code super}).
     */
    static <T> void copy(List<? super T> dst, List<? extends T> src) {
        // SOLUTION-BEGIN throw Q4
        for (T item : src) {
            dst.add(item);
        }
        // SOLUTION-END
    }

    /**
     * Phần tử lớn nhất trong {@code items} theo {@code comparator}.
     *
     * @throws NoSuchElementException nếu {@code items} rỗng
     */
    static <T> T max(Collection<? extends T> items, Comparator<? super T> comparator) {
        // SOLUTION-BEGIN throw Q4
        T best = null;
        boolean found = false;
        for (T item : items) {
            if (!found || comparator.compare(item, best) > 0) {
                best = item;
                found = true;
            }
        }
        if (!found) {
            throw new NoSuchElementException("items rỗng, không có phần tử lớn nhất");
        }
        return best;
        // SOLUTION-END
    }
}

/* ANSWER Q9:
 * SOLUTION-BEGIN
 * Generic invariance nghĩa là List<A> và List<B> không có quan hệ subtype với nhau cho
 * dù A là subtype của B (trừ khi A và B là cùng một kiểu). Ở Q1, List<String> KHÔNG phải
 * subtype của List<Object> nên gán trực tiếp bị lỗi biên dịch — khác với mảng String[]
 * là subtype của Object[] (covariant), gán được nhưng đổi lại rủi ro ArrayStoreException
 * lúc chạy như quan sát ở Q1. Invariance đổi lỗi từ runtime sang compile-time, đánh đổi
 * sự linh hoạt để lấy an toàn kiểu ngay lúc biên dịch.
 * SOLUTION-END
 */
