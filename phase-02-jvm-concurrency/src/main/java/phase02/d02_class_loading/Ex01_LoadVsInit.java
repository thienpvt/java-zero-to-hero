package phase02.d02_class_loading;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class Loading — Bài 1: Load và initialization
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 2 (Class Loading), câu 1–3.
 * Cần làm trước: d01_bytecode.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_LoadVsInitTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] Class được JVM load khi nào?
 *   Bắt đầu   : làm Q3 trước, rồi viết ANSWER Q1 dựa trên hai method vừa chạy.
 *   Kiểm chứng: Ctrl+N → Class → Ctrl+F12 → forName, Ctrl+Q đọc tham số initialize.
 *   Hoàn thành khi: viết xong khối ANSWER Q1.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Loading và initialization khác nhau thế nào?
 *   Bắt đầu   : làm Q3 trước; đối chiếu hitsAfterLoadWithoutInit() với hitsAfterInit(), rồi viết ANSWER Q2.
 *   Kiểm chứng: đặt breakpoint tại touch(), Debug test q03_*, F7 (Step Into). Alt+F8 xem HITS.get()
 *               và UNINIT_HITS.get() sau mỗi lời gọi.
 *   Hoàn thành khi: viết xong khối ANSWER Q2.
 * <p>
 * Q3 [DỰ ĐOÁN + CODE] Static field được initialize khi nào?
 *   Bắt đầu   : đọc Probe, ProbeUninit, HITS và UNINIT_HITS bên dưới; cài hai method; điền hai hằng
 *               Q3_HITS_* (thay null).
 *   Kiểm chứng: chạy q03_*. Ctrl+Q trên Class.forName để đọc initialize. Debug, F7 vào touch(),
 *               Alt+F8 xem bộ đếm của đúng class vừa load.
 *   Code      : hitsAfterLoadWithoutInit gọi
 *               {@code Class.forName(ProbeUninit.class.getName(), false, loader)} rồi trả
 *               {@code UNINIT_HITS.get()}. hitsAfterInit gọi
 *               {@code Class.forName(Probe.class.getName(), true, loader)} hoặc đọc {@code Probe.VALUE},
 *               rồi trả {@code HITS.get()}. ProbeUninit chỉ dành cho nhánh chưa initialize; Probe dành
 *               cho nhánh initialize. Đừng dùng chung một class cho cả hai method.
 *   Hoàn thành khi: các test q03_* xanh.
 */
public class Ex01_LoadVsInit {

    static final AtomicInteger HITS = new AtomicInteger();

    static final AtomicInteger UNINIT_HITS = new AtomicInteger();

    static final Integer Q3_HITS_AFTER_LOAD_WITHOUT_INIT = 0; // SOLUTION-VALUE

    static final Integer Q3_HITS_AFTER_INIT = 1; // SOLUTION-VALUE

    static final class Probe {
        @SuppressWarnings("unused")
        static int loads;

        static final int VALUE = touch();

        private static int touch() {
            return HITS.incrementAndGet();
        }
    }

    /** Class này chưa bị initialize bởi hitsAfterInit. */
    static final class ProbeUninit {
        static final int VALUE = touch();

        private static int touch() {
            return UNINIT_HITS.incrementAndGet();
        }
    }

    static int hitsAfterLoadWithoutInit() throws ClassNotFoundException {
        // SOLUTION-BEGIN throw Q3
        ClassLoader loader = ProbeUninit.class.getClassLoader();
        Class.forName(ProbeUninit.class.getName(), false, loader);
        return UNINIT_HITS.get();
        // SOLUTION-END
    }

    static int hitsAfterInit() throws ClassNotFoundException {
        // SOLUTION-BEGIN throw Q3
        ClassLoader loader = Probe.class.getClassLoader();
        Class.forName(Probe.class.getName(), true, loader);
        return HITS.get();
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * JVM load class khi cần bytecode của nó lần đầu: sử dụng chủ động (new, gọi method static,
 * đọc hoặc gán field static không phải hằng biên dịch), Class.forName, hoặc khi class khác
 * đang linking phải phân giải tham chiếu tới nó.
 * Load tạo đối tượng Class. Việc đó có thể xảy ra trước initialization.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Loading tìm bytecode và tạo Class. Linking (verification, preparation, resolution) gắn class
 * vào JVM; preparation chỉ gán giá trị mặc định cho field static, chưa chạy touch().
 * Initialization chạy static initializer đúng một lần. forName với initialize = false vẫn load
 * ProbeUninit nhưng UNINIT_HITS giữ nguyên; forName với initialize = true làm Probe chạy touch().
 * SOLUTION-END
 */
