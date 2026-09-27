package phase02.d16_thread_safety;

/**
 * Thread safety — Bài 2: ThreadLocal
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 16 (Thread Safety Design), câu 6, 7.
 * Cần làm trước: Ex01_NoSharedMutableState.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex02_ThreadLocalTest bằng nút ▶
 * (Ctrl+Shift+F10).
 * <p>
 * Q6 [CODE] ThreadLocal giải quyết vấn đề gì?
 *   Bắt đầu   : cài {@code setUser(String)} và {@code user()} trên {@code USER}.
 *               Ctrl+N mở {@code java.lang.ThreadLocal}, Ctrl+Q đọc đoạn đầu, Ctrl+F12 tìm
 *               {@code set} và {@code get}. Đừng dùng một field {@code String} cho mọi thread.
 *   Kiểm chứng: chạy q06_eachThreadKeepsOwnName. Hai thread ghi tên mình đã đọc vào mảng kết quả.
 *               Cổng {@code CountDownLatch}, {@code join} tối đa 10 giây. Không dùng {@code Thread.sleep}.
 *   Hoàn thành khi: q06_* xanh; mỗi thread đọc đúng tên nó đã gắn, và viết xong khối ANSWER Q6.
 * <p>
 * Q7 [CODE] ThreadLocal có thể gây memory/resource leak như thế nào khi dùng với thread pool?
 *   Bắt đầu   : cài {@code clear()} để thread đang gọi không còn đọc được tên cũ.
 *               Ctrl+F12 trong {@code ThreadLocal}, tìm method gỡ entry khỏi thread hiện tại.
 *   Kiểm chứng: chạy q07_clearThenGetIsNull. Sau clear, {@code user()} là null.
 *   Hoàn thành khi: q07_* xanh và viết xong khối ANSWER Q7, nêu vì sao pool giữ thread thì giá trị sót lại.
 */
public class Ex02_ThreadLocal {

    /** Bản giá trị gắn với từng thread. */
    static final ThreadLocal<String> USER = new ThreadLocal<>();

    /**
     * Gắn {@code name} với thread đang gọi.
     */
    static void setUser(String name) {
        throw new UnsupportedOperationException("TODO Q6");
    }

    /**
     * Tên mà thread đang gọi đã gắn, hoặc null khi chưa gắn hoặc đã gỡ.
     */
    static String user() {
        throw new UnsupportedOperationException("TODO Q6");
    }

    /**
     * Gỡ tên của thread đang gọi.
     */
    static void clear() {
        throw new UnsupportedOperationException("TODO Q7");
    }
}

/* ANSWER Q6:
 *
 */

/* ANSWER Q7:
 *
 */
