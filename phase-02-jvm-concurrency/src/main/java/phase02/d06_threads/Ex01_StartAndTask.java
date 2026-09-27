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
 *   Bắt đầu   : cài {@code callResult()} để chạy một {@code Callable&lt;String&gt;} và trả giá trị.
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
    static final Boolean Q2_RUN_USES_CALLER_THREAD = true; // SOLUTION-VALUE

    // Q2 — true nếu tên startOnNewThread() ghi được trùng tên thread đang gọi.
    static final Boolean Q2_START_USES_CALLER_THREAD = false; // SOLUTION-VALUE

    /**
     * Tạo thread tên {@code worker-q2}, gọi {@code run()} chứ không gọi {@code start()},
     * rồi trả tên đã ghi vào {@code observedName}.
     */
    static String runOnCaller() {
        // SOLUTION-BEGIN throw Q2
        Thread worker = new Thread(() -> observedName = Thread.currentThread().getName(), "worker-q2");
        worker.run();
        return observedName;
        // SOLUTION-END
    }

    /**
     * Tạo thread tên {@code worker-q2}, gọi {@code start()}, chờ tối đa 10 giây,
     * rồi trả tên đã ghi vào {@code observedName}.
     *
     * @throws IllegalStateException nếu bị ngắt khi chờ, hoặc thread vẫn còn sống sau 10 giây
     */
    static String startOnNewThread() {
        // SOLUTION-BEGIN throw Q2
        Thread worker = new Thread(() -> observedName = Thread.currentThread().getName(), "worker-q2");
        worker.start();
        try {
            worker.join(10_000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Bị ngắt khi chờ thread worker-q2.", ex);
        }
        if (worker.isAlive()) {
            throw new IllegalStateException("Thread worker-q2 vẫn còn sống sau 10 giây.");
        }
        return observedName;
        // SOLUTION-END
    }

    /**
     * Chạy một {@code Callable&lt;String&gt;} trên một thread và trả chuỗi {@code ok}.
     *
     * @throws IllegalStateException nếu bị ngắt khi chờ, thread vẫn còn sống sau 10 giây,
     *         hoặc Callable ném lỗi
     */
    static String callResult() {
        // SOLUTION-BEGIN throw Q3
        FutureTask<String> task = new FutureTask<>(() -> "ok");
        Thread worker = new Thread(task, "worker-q3");
        worker.start();
        try {
            worker.join(10_000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Bị ngắt khi chờ thread worker-q3.", ex);
        }
        if (worker.isAlive()) {
            throw new IllegalStateException("Thread worker-q3 vẫn còn sống sau 10 giây.");
        }
        try {
            return task.get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Bị ngắt khi lấy kết quả Callable.", ex);
        } catch (ExecutionException ex) {
            throw new IllegalStateException("Callable ném lỗi.", ex);
        }
        // SOLUTION-END
    }

    /**
     * Cho biết {@code Runnable.run} có trả một giá trị cho caller hay không.
     * Kết quả là hằng, không phụ thuộc dữ liệu chạy.
     */
    static boolean runnableReturnsValue() {
        // SOLUTION-BEGIN throw Q3
        return false;
        // SOLUTION-END
    }

    /**
     * Chờ {@code worker} kết thúc, tối đa 10 giây.
     *
     * @throws IllegalStateException nếu bị ngắt khi chờ, hoặc {@code worker} vẫn còn sống sau 10 giây
     */
    static void await(Thread worker) {
        // SOLUTION-BEGIN throw Q4
        try {
            worker.join(10_000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Bị ngắt khi chờ thread " + worker.getName() + ".", ex);
        }
        if (worker.isAlive()) {
            throw new IllegalStateException("Thread " + worker.getName() + " vẫn còn sống sau 10 giây.");
        }
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Process là chương trình đang chạy, có không gian địa chỉ và tài nguyên hệ điều hành riêng.
 * Thread là luồng thực thi bên trong process, dùng chung heap của process đó.
 * Mỗi thread có stack và program counter riêng, nên tạo thread rẻ hơn tạo process.
 * Một process có thể chứa nhiều thread. Chúng nhìn thấy dữ liệu dùng chung trên heap.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * start() bảo JVM xếp lịch một thread mới rồi trả về; thân Runnable chạy trên thread đó.
 * run() gọi Runnable ngay trên thread đang gọi method, không tạo thread mới.
 * runOnCaller() ghi tên thread gọi method. startOnNewThread() ghi tên worker-q2.
 * join sau start() công bố ghi của worker cho thread đang chờ.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Runnable.run là void và không khai báo checked exception.
 * Callable.call trả một kết quả và có thể ném Exception.
 * callResult chạy Callable rồi lấy chuỗi qua FutureTask. runnableReturnsValue là false vì run không trả giá trị.
 * Muốn chạy nền và nhận kết quả thì dùng Callable với Future, không dùng mỗi Runnable.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * join() chặn thread gọi cho đến khi thread đích kết thúc, hoặc đến hết thời gian truyền vào.
 * Khi join trả về và thread đích đã chết, các ghi của thread đích happen-before lời gọi join.
 * await dùng join tối đa 10 giây. Nếu thread còn sống thì báo lỗi, không chờ vô hạn.
 * Nhờ đó caller đọc counter sau await thì thấy giá trị worker đã ghi.
 * SOLUTION-END
 */
