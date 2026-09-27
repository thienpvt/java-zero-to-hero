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
        // SOLUTION-BEGIN throw Q6
        USER.set(name);
        // SOLUTION-END
    }

    /**
     * Tên mà thread đang gọi đã gắn, hoặc null khi chưa gắn hoặc đã gỡ.
     */
    static String user() {
        // SOLUTION-BEGIN throw Q6
        return USER.get();
        // SOLUTION-END
    }

    /**
     * Gỡ tên của thread đang gọi.
     */
    static void clear() {
        // SOLUTION-BEGIN throw Q7
        USER.remove();
        // SOLUTION-END
    }
}

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * ThreadLocal giữ một bản giá trị riêng cho mỗi thread, thay cho field dùng chung.
 * Hai thread cùng set hai tên thì mỗi bên chỉ get được tên của chính nó.
 * Đó là thread confinement: dữ liệu không chia sẻ nên không cần khóa cho chính giá trị đó.
 * Nó không làm một field static bình thường trở nên an toàn. Chỉ map gắn với thread hiện tại là riêng.
 * SOLUTION-END
 */

/* ANSWER Q7:
 * SOLUTION-BEGIN
 * Thread pool tái sử dụng thread, nên ThreadLocal của task trước còn trên thread đó.
 * Task sau gọi get và đọc user hoặc tài nguyên của task trước.
 * Thread còn sống giữ reference, object không được thu: rò rỉ bộ nhớ hoặc connection.
 * clear phải remove trong finally khi task kết thúc. Không remove thì pool giữ mãi.
 * SOLUTION-END
 */
