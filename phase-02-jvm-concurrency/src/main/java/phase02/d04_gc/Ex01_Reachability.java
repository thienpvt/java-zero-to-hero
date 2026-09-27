package phase02.d04_gc;

/**
 * GC — Bài 1: Reachability
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 4 (Object Lifecycle &amp; Garbage Collection), câu 1, 2, 3, 4, 10.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_ReachabilityTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] Khi nào object đủ điều kiện để GC?
 *   Bắt đầu   : viết khối ANSWER Q1. Phân biệt "đủ điều kiện bị thu" với "collector đã chạy".
 *   Tra cứu   : Ctrl+N → java.lang.ref.Reference → Ctrl+Q, để thấy reference không phải điểm xuất phát của GC.
 *   Hoàn thành khi: viết xong khối ANSWER Q1, có nêu điều kiện reachability.
 * <p>
 * Q2 [DỰ ĐOÁN] GC có chạy ngay khi object không còn reference không?
 *   Bắt đầu   : điền hằng Q2_GC_RUNS_IMMEDIATELY (thay null).
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: test q02_prediction xanh.
 * <p>
 * Q3 [TỰ TRẢ LỜI] GC Root là gì?
 *   Bắt đầu   : làm Q1 trước, rồi viết khối ANSWER Q3. Nêu ít nhất hai ví dụ điểm mà collector bắt đầu dò.
 *   Hoàn thành khi: viết xong khối ANSWER Q3.
 * <p>
 * Q4 [DỰ ĐOÁN] Circular references có gây memory leak giống reference counting không?
 *   Bắt đầu   : điền hằng Q4_CYCLE_LEAKS_LIKE_REFCOUNT (thay null).
 *   Kiểm chứng: chạy q04_prediction. Đừng viết test chờ collector thu một chu trình.
 *   Hoàn thành khi: test q04_prediction xanh và viết xong khối ANSWER Q4.
 * <p>
 * Q10 [DỰ ĐOÁN] Có thể ép JVM thực hiện GC bằng {@code System.gc()} không?
 *   Bắt đầu   : điền hằng Q10_SYSTEM_GC_GUARANTEES_COLLECTION (thay null).
 *   Tra cứu   : Ctrl+N → System → Ctrl+F12 → gc → Ctrl+Q.
 *   Kiểm chứng: chạy q10_prediction. Không assert một object cụ thể đã bị thu.
 *   Hoàn thành khi: test q10_prediction xanh.
 */
public class Ex01_Reachability {

    // Q2 — hằng hỏi liệu GC có chạy ngay khi reference cuối cùng biến mất.
    static final Boolean Q2_GC_RUNS_IMMEDIATELY = false; // SOLUTION-VALUE

    // Q4 — hằng hỏi liệu chu trình tham chiếu có leak như reference counting thuần.
    static final Boolean Q4_CYCLE_LEAKS_LIKE_REFCOUNT = false; // SOLUTION-VALUE

    // Q10 — hằng hỏi liệu System.gc() có bảo đảm một collection xảy ra.
    static final Boolean Q10_SYSTEM_GC_GUARANTEES_COLLECTION = false; // SOLUTION-VALUE
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Object đủ điều kiện để GC khi không còn reachable từ GC root qua strong reference.
 * Biến local trên stack, field static và tham chiếu JNI là những gốc thường gặp.
 * "Đủ điều kiện" chỉ có nghĩa là collector được phép thu; không có nghĩa là nó thu ngay.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Không. Mất reference cuối chỉ làm object đủ điều kiện bị thu.
 * JVM tự chọn thời điểm chạy GC theo heap, collector và tải của process.
 * Ứng dụng không có lời gọi nào khiến collector chạy đồng bộ ngay tại lúc đó.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * GC root là điểm xuất phát của phép dò reachability, không phải một object trên heap.
 * Ví dụ: biến local của thread đang chạy, field static của class đã nạp, và tham chiếu JNI.
 * Object còn một đường strong reference từ một root thì chưa đủ điều kiện để GC.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Không. HotSpot dò từ GC root, không dùng reference counting thuần.
 * Hai object trỏ lẫn nhau nhưng cả nhóm không còn nối với root vẫn bị thu.
 * Reference counting thuần mới kẹt chu trình vì bộ đếm của mỗi object vẫn lớn hơn 0.
 * SOLUTION-END
 */

/* ANSWER Q10:
 * SOLUTION-BEGIN
 * System.gc() chỉ là đề nghị. JVM có quyền bỏ qua.
 * Lời gọi không bảo đảm một collection sẽ chạy, cũng không bảo đảm một object cụ thể bị thu.
 * Đừng dùng nó để sửa leak hay để làm mốc trong test.
 * SOLUTION-END
 */
