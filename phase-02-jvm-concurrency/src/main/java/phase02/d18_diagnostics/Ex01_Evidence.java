package phase02.d18_diagnostics;

/**
 * Chẩn đoán — Bài 1: Bằng chứng
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 18 (Chẩn đoán JVM bằng bằng chứng), câu 1–5.
 * Cần làm trước: d12_deadlock (Ex01_Ordering).
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_EvidenceTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Thread dump cho thấy deadlock và thread chờ I/O khác nhau ra sao?
 *   Bắt đầu   : đọc {@code DEADLOCK_DUMP} và {@code IO_DUMP}. Đó là dump đã dán sẵn, không phải
 *               kết quả của {@code jcmd}. Điền Q1_DEADLOCK_DUMP_HAS_DEADLOCK và
 *               Q1_IO_DUMP_HAS_DEADLOCK (thay null) bằng {@code contains("deadlock")} trên từng chuỗi.
 *               Không gọi {@code jcmd}, {@code jstack} hay JFR.
 *   Kiểm chứng: chạy q01_prediction. Muốn tính biểu thức trên chuỗi: Debug test, Alt+F8.
 *   Hoàn thành khi: q01_prediction xanh và viết xong khối ANSWER Q1, nêu khác nhau giữa vòng chờ
 *               lock và thread đang chờ I/O.
 * <p>
 * Q2 [DỰ ĐOÁN] Tại sao heap sử dụng tăng chưa chứng minh có memory leak?
 *   Bắt đầu   : đọc {@code HEAP_NOTE}. Điền Q2_RISING_HEAP_PROVES_LEAK (thay null).
 *               Hằng này cố định: không gọi GC và không đo heap lúc chạy.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và viết xong khối ANSWER Q2, giải thích vì sao một nhịp
 *               tăng chưa đủ để kết luận leak.
 * <p>
 * Q3 [TỰ TRẢ LỜI] JFR hữu ích gì khi tìm contention hoặc allocation tăng?
 *   Bắt đầu   : viết khối ANSWER Q3. Nêu sự kiện JFR cho allocation và cho lock contention.
 *               Không gọi {@code jcmd} và không bật JFR trong test.
 *   Hoàn thành khi: viết xong khối ANSWER Q3.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Khi queue tăng dần nhưng CPU thấp, bạn sẽ kiểm tra gì trước?
 *   Bắt đầu   : viết khối ANSWER Q4. Nêu pool, lock và I/O. Đừng kết luận bằng cách thêm thread
 *               vô hạn.
 *   Hoàn thành khi: viết xong khối ANSWER Q4.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Làm thế nào so sánh một thay đổi GC hoặc pool size mà không nhầm do workload khác nhau?
 *   Bắt đầu   : viết khối ANSWER Q5. Nêu cùng workload, warm-up, nhiều lần chạy, và ghi JDK cùng flag.
 *   Hoàn thành khi: viết xong khối ANSWER Q5.
 */
public class Ex01_Evidence {

    /**
     * Đoạn thread dump đã dán sẵn. Có dòng {@code Found one Java-level deadlock}.
     * Đọc chuỗi này; đừng gọi {@code jcmd} trong test.
     */
    static final String DEADLOCK_DUMP = ""
            + "Found one Java-level deadlock:\n"
            + "=============================\n"
            + "\"worker-a\":\n"
            + "  waiting to lock <0x00000000d18a0010> (a java.lang.Object)\n"
            + "  which is held by \"worker-b\"\n"
            + "\"worker-b\":\n"
            + "  waiting to lock <0x00000000d18a0028> (a java.lang.Object)\n"
            + "  which is held by \"worker-a\"\n";

    /**
     * Đoạn dump đã dán sẵn. Có {@code WAITING} và {@code socketRead}, không có chữ deadlock.
     * Đọc chuỗi này; đừng gọi {@code jstack} trong test.
     */
    static final String IO_DUMP = ""
            + "\"parked-io\" #8\n"
            + "   java.lang.Thread.State: WAITING (parking)\n"
            + "\tat jdk.internal.misc.Unsafe.park(Native Method)\n"
            + "\"socket-io\" #9\n"
            + "   java.lang.Thread.State: RUNNABLE\n"
            + "\tat java.net.SocketInputStream.socketRead0(Native Method)\n"
            + "\tat java.net.SocketInputStream.socketRead(SocketInputStream.java:116)\n";

    /**
     * Ghi chú đã dán sẵn: heap used tăng rồi giảm sau GC.
     * Không đo heap và không gọi {@code System.gc()}.
     */
    static final String HEAP_NOTE = ""
            + "Used heap tăng từ 200 MB lên 700 MB trong lúc cấp phát. "
            + "Sau GC, used heap giảm về 220 MB. "
            + "Đường used lên rồi xuống theo nhịp collector, không giữ mức cao sau khi GC thu xong.";

    // Q1 — kết quả contains("deadlock") trên DEADLOCK_DUMP và trên IO_DUMP. Đọc chuỗi đã dán, đừng gọi jcmd.
    static final Boolean Q1_DEADLOCK_DUMP_HAS_DEADLOCK = null;
    static final Boolean Q1_IO_DUMP_HAS_DEADLOCK = null;

    // Q2 — heap used tăng có chứng minh memory leak hay không. Hằng cố định, không gọi GC.
    static final Boolean Q2_RISING_HEAP_PROVES_LEAK = null;
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

/* ANSWER Q5:
 *
 */
