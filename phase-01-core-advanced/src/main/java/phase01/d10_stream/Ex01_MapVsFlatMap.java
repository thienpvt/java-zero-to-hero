package phase01.d10_stream;

import java.util.List;

/**
 * Stream API — Bài 1: map() vs flatMap()
 *
 * Nguồn: 01-java-core-advanced.md, mục 10 (Stream API), câu 1.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_MapVsFlatMapTest bằng
 * nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q1 [DỰ ĐOÁN + CODE] `map()` và `flatMap()` khác nhau thế nào?
 *   Bắt đầu   : trong q01_prediction, chạy thử
 *               Stream.of(List.of(1, 2), List.of(3)).map(l -> l).count() và bản
 *               .flatMap(List::stream).count(); điền 2 hằng số Q1_*.
 *   Kiểm chứng: đặt breakpoint trong lambda của itemCounts/allItemsDistinctSorted, Debug
 *               test tương ứng, dùng F8 để thấy map() sinh ra đúng 1 phần tử kết quả cho
 *               mỗi Order đầu vào, còn flatMap() "làm phẳng" từng Order thành nhiều phần tử
 *               (mỗi item một phần tử) rồi trộn tất cả vào một Stream duy nhất.
 *   Code      : static List&lt;Integer&gt; itemCounts(List&lt;Order&gt; orders) — map mỗi
 *               Order sang số lượng items của nó; static List&lt;String&gt;
 *               allItemsDistinctSorted(List&lt;Order&gt; orders) — flatMap tất cả items của
 *               mọi Order thành một Stream&lt;String&gt;, loại trùng, sắp xếp.
 *   Hoàn thành khi: test q01_* xanh; giải thích được vì sao map() giữ nguyên số phần tử
 *               (bằng số Order) còn flatMap() cho ra số phần tử bằng tổng kích thước của
 *               tất cả các List con.
 */
public class Ex01_MapVsFlatMap {

    // Q1 — kịch bản: Stream.of(List.of(1, 2), List.of(3)); map(l -> l) so với
    // flatMap(List::stream); đếm số phần tử bằng count().
    static final Integer Q1_MAP_COUNT = 2; // SOLUTION-VALUE
    static final Integer Q1_FLATMAP_COUNT = 3; // SOLUTION-VALUE

    /** Một đơn hàng, gồm mã đơn và danh sách tên các mặt hàng (có thể trùng tên). */
    record Order(String id, List<String> items) {
    }

    /**
     * Trả về số lượng mặt hàng của từng đơn hàng trong {@code orders}, theo đúng thứ tự
     * trong {@code orders} — mỗi Order map sang đúng một số nguyên.
     *
     * @throws NullPointerException nếu {@code orders} là {@code null}
     */
    static List<Integer> itemCounts(List<Order> orders) {
        // SOLUTION-BEGIN throw Q1
        return orders.stream().map(o -> o.items().size()).toList();
        // SOLUTION-END
    }

    /**
     * Trả về tất cả tên mặt hàng xuất hiện trong {@code orders} (gộp từ mọi đơn hàng bằng
     * flatMap), loại trùng, sắp xếp tăng dần theo thứ tự tự nhiên của chuỗi.
     *
     * @throws NullPointerException nếu {@code orders} là {@code null}
     */
    static List<String> allItemsDistinctSorted(List<Order> orders) {
        // SOLUTION-BEGIN throw Q1
        return orders.stream()
                .flatMap(o -> o.items().stream())
                .distinct()
                .sorted()
                .toList();
        // SOLUTION-END
    }
}
