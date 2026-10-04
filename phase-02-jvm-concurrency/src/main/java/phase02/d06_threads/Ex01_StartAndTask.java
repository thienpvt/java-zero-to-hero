package phase02.d06_threads;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Thread — Bài 1: start, Callable và join
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 6 (Thread Fundamentals), câu 1–4.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_StartAndTaskTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] Process và Thread khác nhau thế nào?
 *   Bắt đầu   : viết khối ANSWER Q1. Nêu không gian địa chỉ và thứ mà mỗi thread sở hữu riêng.
 *   Hoàn thành khi: viết xong khối ANSWER Q1.
 * <p>
 * Q2 [DỰ ĐOÁN + CODE] {@code thread.start()} khác {@code thread.run()} thế nào?
 *   Bắt đầu   : cài {@code runOnCaller()} và {@code startOnNewThread()}. Đặt tên thread
 *               {@code worker-q2}. Thân công việc ghi {@code Thread.currentThread().getName()}
 *               vào {@code observedName}. Điền hai hằng Q2_* bằng true hoặc false sau khi so
 *               tên đó với {@code Thread.currentThread().getName()} (thay null). Đừng gọi hai
 *               method này trong initializer của hằng: thread mới phải chờ class khởi tạo xong.
 *   Kiểm chứng: chạy q02_runUsesCallerThread và q02_startUsesNewThread.
 *   Hoàn thành khi: hai test q02_* xanh và viết xong khối ANSWER Q2.
 * <p>
 * Q3 [CODE] Runnable và Callable khác nhau thế nào?
 *   Bắt đầu   : cài {@code callResult()} để chạy một {@code Callable<String>} và trả giá trị.
 *               Cài {@code runnableReturnsValue()}.
 *   Tra cứu   : Ctrl+N → Callable → Ctrl+Q, rồi Ctrl+N → Runnable → Ctrl+Q. So chữ ký {@code call} và {@code run}.
 *   Kiểm chứng: chạy q03_callResult và q03_runnableReturnsValue.
 *   Hoàn thành khi: hai test q03_* xanh và viết xong khối ANSWER Q3.
 * <p>
 * Q4 [CODE] {@code join()} dùng để làm gì?
 *   Bắt đầu   : cài {@code await(Thread)} để chờ {@code worker} kết thúc.
 *   Tra cứu   : Ctrl+N → Thread → Ctrl+F12 → join → Ctrl+Q.
 *   Kiểm chứng: chạy q04_await_workerIncrementsCounter.
 *   Hoàn thành khi: test q04_* xanh và viết xong khối ANSWER Q4.
 */
public class Ex01_StartAndTask {

    /** Tên {@code Thread.currentThread()} mà thân công việc của Q2 vừa ghi. */
    static String observedName;

    // Q2 — true nếu tên runOnCaller() ghi được trùng tên thread đang gọi.
    static final Boolean Q2_RUN_USES_CALLER_THREAD = null;

    // Q2 — true nếu tên startOnNewThread() ghi được trùng tên thread đang gọi.
    static final Boolean Q2_START_USES_CALLER_THREAD = null;

    /**
     * Tạo thread tên {@code worker-q2}, gọi {@code run()} chứ không gọi {@code start()},
     * rồi trả tên đã ghi vào {@code observedName}.
     */
    static String runOnCaller() {
        throw new UnsupportedOperationException("TODO Q2");
    }

    /**
     * Tạo thread tên {@code worker-q2}, gọi {@code start()}, chờ tối đa 10 giây,
     * rồi trả tên đã ghi vào {@code observedName}.
     *
     * @throws IllegalStateException nếu bị ngắt khi chờ, hoặc thread vẫn còn sống sau 10 giây
     */
    static String startOnNewThread() {
        throw new UnsupportedOperationException("TODO Q2");
    }

    /**
     * Chạy một {@code Callable<String>} trên một thread và trả chuỗi {@code ok}.
     *
     * @throws IllegalStateException nếu bị ngắt khi chờ, thread vẫn còn sống sau 10 giây,
     *         hoặc Callable ném lỗi
     */
    static String callResult() {
        throw new UnsupportedOperationException("TODO Q3");
    }

    /**
     * Cho biết {@code Runnable.run} có trả một giá trị cho caller hay không.
     * Kết quả là hằng, không phụ thuộc dữ liệu chạy.
     */
    static boolean runnableReturnsValue() {
        throw new UnsupportedOperationException("TODO Q3");
    }

    /**
     * Chờ {@code worker} kết thúc, tối đa 10 giây.
     *
     * @throws IllegalStateException nếu bị ngắt khi chờ, hoặc {@code worker} vẫn còn sống sau 10 giây
     */
    static void await(Thread worker) {
        throw new UnsupportedOperationException("TODO Q4");
    }
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
