package phase02.d04_gc;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * GC — Bài 2: Reference và cache
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 4 (Object Lifecycle &amp; Garbage Collection), câu 5, 6, 9.
 * Cần làm trước: Ex01_Reachability.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex02_ReferencesAndLeakTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q5 [DỰ ĐOÁN + CODE] Strong và WeakReference khác nhau thế nào?
 *   Bắt đầu   : điền hằng Q5_WEAK_DOES_NOT_PREVENT_GC (thay null). Cài đặt
 *               {@code surviveOrClear(WeakReference&lt;String&gt;)} để trả referent hiện tại.
 *               Method đó không gọi {@code System.gc()} và không gọi {@code clear()}.
 *               {@code weakOf(byte[])} cho sẵn, dùng khi muốn bọc một mảng byte.
 *   Tra cứu   : Ctrl+N → WeakReference → Ctrl+Q, đọc {@code get()}.
 *   Kiểm chứng: chạy q05_prediction và q05_surviveOrClear_returnsReferentWhileStrong.
 *               Không assert một {@code WeakReference} đã bị clear.
 *   Hoàn thành khi: hai test q05_* xanh và viết xong khối ANSWER Q5.
 * <p>
 * Q6 [CODE] WeakHashMap hữu ích trong trường hợp nào?
 *   Bắt đầu   : Ctrl+N → WeakHashMap → Ctrl+Q, rồi cài đặt {@code cache()} cho cache theo key.
 *   Kiểm chứng: chạy q06_cache_getPutWhileKeyStronglyReachable. Test chỉ kiểm tra kiểu map
 *               và get/put khi key còn reachable mạnh; không kiểm tra size sau khi bỏ key.
 *   Hoàn thành khi: test q06_* xanh và viết xong khối ANSWER Q6.
 * <p>
 * Q9 [TỰ TRẢ LỜI] Vì sao cache không giới hạn có thể gây memory leak?
 *   Bắt đầu   : làm Q6 trước, rồi viết khối ANSWER Q9.
 *   Hoàn thành khi: viết xong khối ANSWER Q9, có nêu vì sao entry vẫn reachable.
 */
public class Ex02_ReferencesAndLeak {

    // Q5 — hằng hỏi liệu weak reference có ngăn GC hay không.
    static final Boolean Q5_WEAK_DOES_NOT_PREVENT_GC = true; // SOLUTION-VALUE

    /**
     * Cho sẵn: bọc {@code data} trong một {@code WeakReference}. Không gọi {@code System.gc()}.
     */
    static WeakReference<byte[]> weakOf(byte[] data) {
        return new WeakReference<>(data);
    }

    /**
     * Trả referent hiện tại của {@code ref}.
     * Không gọi {@code System.gc()} và không gọi {@code clear()}.
     *
     * @return referent nếu còn, {@code null} nếu reference đã bị clear
     */
    static String surviveOrClear(WeakReference<String> ref) {
        // SOLUTION-BEGIN throw Q5
        return ref.get();
        // SOLUTION-END
    }

    /**
     * Cache theo key: entry không được giữ key sống chỉ vì key nằm trong cache.
     */
    static <K, V> Map<K, V> cache() {
        // SOLUTION-BEGIN throw Q6
        return new WeakHashMap<>();
        // SOLUTION-END
    }
}

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Strong reference giữ object sống: còn strong ref thì GC không thu object đó.
 * WeakReference không ngăn GC. Khi không còn strong ref, collector có thể thu referent và clear weak ref.
 * surviveOrClear chỉ đọc get(). Không gọi System.gc() và không tự clear reference.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * WeakHashMap hợp với cache mà key còn được nơi khác giữ bằng strong reference.
 * Khi key không còn strong ref bên ngoài, entry có thể bị bỏ nên cache không giữ object sống mãi.
 * Đừng dùng khi entry phải còn đến lúc chủ động xóa.
 * SOLUTION-END
 */

/* ANSWER Q9:
 * SOLUTION-BEGIN
 * Cache không giới hạn giữ strong reference tới mọi entry đã put và không bao giờ bỏ bớt.
 * Những entry đó vẫn reachable từ cache, nên GC không thu được, heap tăng theo dữ liệu từng được cache.
 * Phòng: trần kích thước, hết hạn, hoặc key weak khi entry chỉ cần sống cùng key bên ngoài.
 * SOLUTION-END
 */
