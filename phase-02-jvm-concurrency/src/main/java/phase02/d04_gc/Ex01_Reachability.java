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
    static final Boolean Q2_GC_RUNS_IMMEDIATELY = null;

    // Q4 — hằng hỏi liệu chu trình tham chiếu có leak như reference counting thuần.
    static final Boolean Q4_CYCLE_LEAKS_LIKE_REFCOUNT = null;

    // Q10 — hằng hỏi liệu System.gc() có bảo đảm một collection xảy ra.
    static final Boolean Q10_SYSTEM_GC_GUARANTEES_COLLECTION = null;
}

/* ANSWER Q1:
 *
 */

/* ANSWER Q2:
 *
 */

/* ANSWER Q3:
 *
 */

/* ANSWER Q4:
 *
 */

/* ANSWER Q10:
 *
 */
