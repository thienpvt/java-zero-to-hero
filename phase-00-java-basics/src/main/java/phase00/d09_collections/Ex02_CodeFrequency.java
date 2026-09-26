package phase00.d09_collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Collections — Bài 2: Đếm mã sản phẩm
 *
 * Nguồn: 00-java-basics-review.md, mục 9 (Collections ở mức sử dụng), câu 4.
 * Cần làm trước: Ex01_SetAndMap.
 * Cách làm: cài {@code summarize}, chạy Ex02_CodeFrequencyTest.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q4 [TỰ TRẢ LỜI + CODE] Khi cần đếm số lần xuất hiện của một từ, bạn chọn cấu trúc nào?
 *   Bắt đầu   : cài {@code summarize}, rồi viết ANSWER Q4.
 *   Kiểm chứng: Debug q04_countsDuplicatesInFirstSeenOrder với list {@code A, B, A, C, B}.
 *               Alt+F8 xem map sau từng phần tử.
 *   Code      : {@code null} list hoặc phần tử → {@code NullPointerException}. List rỗng →
 *               counts, duplicates và uniques đều rỗng. {@code duplicates} là mã xuất hiện hơn một lần,
 *               theo thứ tự gặp lần đầu. {@code uniques} là mã xuất hiện đúng một lần, cùng quy tắc thứ tự.
 *               Hai list trả về không cho sửa.
 *   Hoàn thành khi: q04_* xanh; ANSWER Q4 nêu {@code Map} từ mã sang số lần, và vì sao cần loại map giữ thứ tự.
 */
public class Ex02_CodeFrequency {

    public record CodeStats(Map<String, Integer> counts, List<String> duplicates, List<String> uniques) {
    }

    static CodeStats summarize(List<String> codes) {
        // SOLUTION-BEGIN throw Q4
        Objects.requireNonNull(codes, "codes");
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String code : codes) {
            counts.merge(Objects.requireNonNull(code, "code"), 1, Integer::sum);
        }
        List<String> duplicates = new ArrayList<>();
        List<String> uniques = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > 1) {
                duplicates.add(entry.getKey());
            } else {
                uniques.add(entry.getKey());
            }
        }
        return new CodeStats(Collections.unmodifiableMap(counts), List.copyOf(duplicates), List.copyOf(uniques));
        // SOLUTION-END
    }

    /* ANSWER Q4:
     * SOLUTION-BEGIN
     * Đếm từ bằng Map<String, Integer>: key là từ, value là số lần. HashMap đủ nếu không cần thứ tự.
     * Khi phải liệt kê theo lần gặp đầu tiên, dùng LinkedHashMap rồi tách mã có count > 1 và count == 1.
     * Set chỉ biết có/không, không giữ số lần.
     * SOLUTION-END
     */
}
