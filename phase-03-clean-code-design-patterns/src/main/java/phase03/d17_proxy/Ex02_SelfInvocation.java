package phase03.d17_proxy;

/**
 * Proxy — Bài 2: self-invocation và lazy loading
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 17, câu 3, 4.
 * Cần làm trước: Ex01_ManualProxy.
 * Cách làm: chạy test trong Ex02_SelfInvocationTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Ví dụ Q3 [DỰ ĐOÁN + CODE] Self-invocation có thể gây vấn đề với proxy-based AOP vì sao?
 *   Bắt đầu   : điền Q3_SELF_INVOCATION bằng một giá trị của {@code Reason}, rồi cài
 *               {@code SelfInvoking.outer} gọi {@code this.inner()}.
 *   Kiểm chứng: chạy q03_prediction và q03_selfInvocationBypassesProxy.
 *   Hoàn thành khi: hai test xanh và ANSWER Q3 nêu lời gọi nội bộ không đi qua proxy.
 * <p>
 * Q4 [DỰ ĐOÁN] Lazy-loading proxy trong Hibernate hoạt động ở mức khái niệm thế nào?
 *   Bắt đầu   : điền Q4_LAZY_PROXY bằng một giá trị của {@code Idea}.
 *   Kiểm chứng: chạy q04_prediction.
 *   Hoàn thành khi: q04_prediction xanh và ANSWER Q4 nêu proxy giữ dữ liệu tối thiểu và nạp khi có lời gọi.
 */
public class Ex02_SelfInvocation {

    /** Vì sao self-invocation không qua proxy. */
    public enum Reason {
        SAME_THREAD,
        CALL_GOES_DIRECTLY_ON_THE_TARGET_OBJECT,
        PROXY_IS_OPTIONAL
    }

    /** Ý tưởng lazy-loading proxy. */
    public enum Idea {
        LOAD_EVERYTHING_EAGERLY,
        PROXY_HOLDS_MINIMAL_DATA_AND_LOADS_ON_CALL,
        NO_PROXY_IS_USED
    }

    // Q3 — self-invocation không đi qua proxy.
    static final Reason Q3_SELF_INVOCATION = Reason.CALL_GOES_DIRECTLY_ON_THE_TARGET_OBJECT; // SOLUTION-VALUE

    // Q4 — lazy-loading proxy hoạt động thế nào.
    static final Idea Q4_LAZY_PROXY = Idea.PROXY_HOLDS_MINIMAL_DATA_AND_LOADS_ON_CALL; // SOLUTION-VALUE

    /** Cho sẵn: mô phỏng lời gọi nội bộ, không qua proxy nào. */
    public static final class SelfInvoking {

        public final java.util.List<String> calls = new java.util.ArrayList<>();

        public String outer() {
            // SOLUTION-BEGIN throw Q3
            return "outer->" + this.inner();
            // SOLUTION-END
        }

        public String inner() {
            calls.add("inner");
            return "inner";
        }
    }
}

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Self-invocation là lời gọi method trên chính object đích, ví dụ this.inner().
 * Nó không đi qua proxy vì proxy chỉ chặn được lời gọi từ ngoài vào.
 * Vì vậy @Transactional trên inner() không có tác dụng khi outer() gọi nó từ bên trong.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Proxy lazy-loading giữ dữ liệu tối thiểu, thường chỉ khoá chính.
 * Khi có lời gọi tới một field chưa nạp, proxy mới truy vấn database và thay vào giá trị thật.
 * Nhờ vậy quan hệ không bị nạp nếu code không dùng tới nó.
 * SOLUTION-END
 */
