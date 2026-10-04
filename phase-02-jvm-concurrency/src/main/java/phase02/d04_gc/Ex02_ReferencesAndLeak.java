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
 *               {@code surviveOrClear(WeakReference<String>)} để trả referent hiện tại.
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
    static final Boolean Q5_WEAK_DOES_NOT_PREVENT_GC = null;

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
        throw new UnsupportedOperationException("TODO Q5");
    }

    /**
     * Cache theo key: entry không được giữ key sống chỉ vì key nằm trong cache.
     */
    static <K, V> Map<K, V> cache() {
        throw new UnsupportedOperationException("TODO Q6");
    }
}

/* ANSWER Q5:
 *
 */

/* ANSWER Q6:
 *
 */

/* ANSWER Q9:
 *
 */
