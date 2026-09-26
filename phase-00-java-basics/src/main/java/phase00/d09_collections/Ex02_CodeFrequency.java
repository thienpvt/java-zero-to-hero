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
        throw new UnsupportedOperationException("TODO Q4");
    }

    /* ANSWER Q4:
     *
     */
}
