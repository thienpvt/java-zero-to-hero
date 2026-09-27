package phase00.d09_collections;

/**
 * Collections — Bài 1: Set, Map và thứ tự
 *
 * Nguồn: 00-java-basics-review.md, mục 9 (Collections ở mức sử dụng), câu 1, 2, 3.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_SetAndMapTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q1 [DỰ ĐOÁN + TỰ TRẢ LỜI] Vì sao dùng {@code Set} để phát hiện mã đơn hàng trùng?
 *   Bắt đầu   : điền Q1_SET_SIZE_AFTER_DUPLICATE, rồi viết ANSWER Q1.
 *   Kiểm chứng: Debug q01_prediction, xem size sau hai lần add cùng một chuỗi.
 *               Ctrl+Q trên {@code Set.add}.
 *   Hoàn thành khi: q01_prediction xanh; ANSWER Q1 nói Set gộp phần tử theo equals.
 * <p>
 * Q2 [DỰ ĐOÁN] {@code Map.get(key)} trả {@code null} có luôn chứng minh key không tồn tại không?
 *   Bắt đầu   : điền Q2_GET_NULL_PROVES_ABSENT.
 *   Kiểm chứng: trong q02_prediction, map đã {@code put("a", null)}. Alt+F8 xem {@code get("a")}
 *               và {@code containsKey("a")}. Ctrl+Q trên {@code Map.get}.
 *   Hoàn thành khi: q02_prediction xanh; nói được khi nào cần {@code containsKey}.
 * <p>
 * Q3 [DỰ ĐOÁN] Muốn giữ thứ tự thêm phần tử, {@code HashSet} có bảo đảm không?
 *   Bắt đầu   : điền Q3_HASHSET_PRESERVES_INSERTION_ORDER và Q3_LINKED_HASH_SET_PRESERVES_ORDER.
 *   Kiểm chứng: Ctrl+N mở {@code HashSet}, Ctrl+Q đọc đoạn nói về order. Với LinkedHashSet,
 *               Debug q03_prediction và xem {@code List.copyOf(set)}.
 *   Hoàn thành khi: q03_prediction xanh; không kết luận thứ tự HashSet bằng một lần chạy thử.
 */
public class Ex01_SetAndMap {

    // Q1 — HashSet thêm "A" hai lần: size bằng bao nhiêu?
    static final Integer Q1_SET_SIZE_AFTER_DUPLICATE = 1; // SOLUTION-VALUE

    // Q2 — put("a", null) rồi (get("a") == null && !containsKey("a")) có đúng không?
    static final Boolean Q2_GET_NULL_PROVES_ABSENT = false; // SOLUTION-VALUE

    // Q3 — Javadoc HashSet có bảo đảm thứ tự chèn không?
    static final Boolean Q3_HASHSET_PRESERVES_INSERTION_ORDER = false; // SOLUTION-VALUE

    // Q3 — LinkedHashSet thêm a, b, c có duyệt ra đúng thứ tự đó không?
    static final Boolean Q3_LINKED_HASH_SET_PRESERVES_ORDER = true; // SOLUTION-VALUE

    /* ANSWER Q1:
     * SOLUTION-BEGIN
     * Set.add trả false và không thêm lần nữa khi phần tử mới equals một phần tử đã có.
     * Đưa mã đơn vào Set: lần gặp lại chính là mã trùng, không cần tự viết vòng so sánh.
     * Muốn vừa phát hiện trùng vừa giữ thứ tự gặp thì dùng LinkedHashSet, không phải HashSet.
     * SOLUTION-END
     */
}
