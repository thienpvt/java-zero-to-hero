package phase02.d07_race;

import java.util.concurrent.CountDownLatch;

/**
 * Race condition — Bài 1: Counter
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 7 (Race Condition), câu 1, 2, 3, 4, 5, 6.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_CounterTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Code trên có thread-safe không?
 *   Bắt đầu   : đọc class Counter bên dưới, đừng sửa nó. Điền hằng Q1_UNSAFE_COUNTER_IS_THREAD_SAFE
 *               (thay null). Chạy q01_experimentRuns hoặc main(), ghi {@code count} in ra vào OBSERVATION Q1.
 *   Kiểm chứng: chạy q01_prediction. Ctrl+N mở lớp này, Ctrl+F12 tìm increment, F7 vào {@code count++}.
 *   Hoàn thành khi: q01_prediction xanh và OBSERVATION Q1 ghi {@code count} bạn thấy khi tự chạy.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Vì sao {@code count++} không atomic?
 *   Bắt đầu   : viết khối ANSWER Q2. Nêu các bước của {@code count++} trên field {@code count}.
 *   Tra cứu   : Ctrl+N mở lớp này, Ctrl+F12 chọn increment, Debug q01_experimentRuns, F7 từng bước.
 *   Hoàn thành khi: viết xong khối ANSWER Q2.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Race condition là gì?
 *   Bắt đầu   : viết khối ANSWER Q3. Nói rõ kết quả phụ thuộc lịch của các thread thế nào.
 *   Hoàn thành khi: viết xong khối ANSWER Q3.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Race condition có luôn reproduce được không?
 *   Bắt đầu   : chạy runExperiment vài lần, ghi vào OBSERVATION Q1, rồi điền hằng
 *               Q4_RACE_ALWAYS_REPRODUCES (thay null).
 *   Kiểm chứng: chạy q04_prediction.
 *   Hoàn thành khi: q04_prediction xanh và khối ANSWER Q4 giải thích lựa chọn của bạn.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Tại sao bug concurrency thường khó debug?
 *   Bắt đầu   : viết khối ANSWER Q5 từ những lần bạn tự chạy runExperiment và từ việc gắn debugger.
 *   Tra cứu   : Ctrl+N mở lớp này, Ctrl+F12 tìm runExperiment, đọc cổng {@code CountDownLatch} và {@code join}.
 *   Hoàn thành khi: viết xong khối ANSWER Q5.
 * <p>
 * Q6 [CODE] Có thể sửa Counter bằng những cách nào?
 *   Bắt đầu   : cài đặt thân {@code increment} và {@code count} của SafeCounter. Viết khối ANSWER Q6
 *               nêu các cách sửa, không chỉ cách đang nằm trong chữ ký method.
 *   Kiểm chứng: chạy q06_safeCounterReaches40000. Nếu sai, Ctrl+F12 tìm increment, Debug, F7.
 *   Hoàn thành khi: test q06 xanh và khối ANSWER Q6 nêu hơn một cách sửa Counter.
 */
public class Ex01_Counter {

    /** Ví dụ mục 7. Giữ nguyên class này. */
    static final class Counter {
        private int count;

        void increment() {
            count++;
        }

        int count() {
            return count;
        }
    }

    // Q1 — hằng hỏi Counter ở ví dụ mục 7 có thread-safe không.
    static final Boolean Q1_UNSAFE_COUNTER_IS_THREAD_SAFE = false; // SOLUTION-VALUE

    // Q4 — hằng hỏi race condition có luôn reproduce được không.
    static final Boolean Q4_RACE_ALWAYS_REPRODUCES = false; // SOLUTION-VALUE

    /** Q6 — điền thân {@code increment} và {@code count}. */
    static final class SafeCounter {
        private int count;

        synchronized void increment() {
            // SOLUTION-BEGIN throw Q6
            count++;
            // SOLUTION-END
        }

        synchronized int count() {
            // SOLUTION-BEGIN throw Q6
            return count;
            // SOLUTION-END
        }
    }

    /**
     * Cho sẵn, không cần sửa. Bốn thread cùng gọi {@code increment} trên một {@code Counter},
     * qua cổng {@code CountDownLatch}, rồi {@code join} tối đa 10 giây.
     * Báo cáo luôn có chữ Counter và {@code count} nhìn thấy sau khi các thread kết thúc hoặc hết giờ join.
     */
    static String runExperiment(int perThread) {
        Counter counter = new Counter();
        int threads = 4;
        CountDownLatch start = new CountDownLatch(1);
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                for (int n = 0; n < perThread; n++) {
                    counter.increment();
                }
            });
            workers[i].setDaemon(true);
            workers[i].start();
        }
        start.countDown();
        for (Thread worker : workers) {
            try {
                worker.join(10_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return "Counter count=" + counter.count()
                + " threads=" + threads
                + " perThread=" + perThread;
    }

    public static void main(String[] args) {
        System.out.println(runExperiment(50_000));
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Không. Counter không thread-safe.
 * increment chỉ là count++ trên field thường, không khóa và không volatile.
 * Hai thread xen kẽ đọc-sửa-ghi thì một lần ghi đè làm mất một lần tăng.
 * SOLUTION-END
 */

/* OBSERVATION Q1:
 * SOLUTION-BEGIN
 * runExperiment in count sau 4 thread. Có lần count nhỏ hơn 4 * perThread, có lần bằng đúng tổng.
 * count++ không được khóa, nên hai thread có thể đọc cùng một giá trị rồi ghi đè nhau.
 * Một lần chạy ra đúng tổng không chứng minh Counter thread-safe.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * count++ không phải một thao tác nguyên tử.
 * Bytecode là đọc field (getfield), cộng một (iadd), rồi ghi lại (putfield).
 * Thread khác có thể chạy giữa bước đọc và bước ghi, nên cả hai cùng ghi một kết quả.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Race condition là lỗi khi kết quả phụ thuộc thứ tự lịch của các thread trên dữ liệu dùng chung.
 * Có ít nhất một lần ghi, và thiếu đồng bộ nên một số lịch ra kết quả sai, một số lịch tình cờ đúng.
 * Đoạn code đơn luồng vẫn có thể đúng; lỗi chỉ xuất hiện khi các thao tác xen kẽ.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Không. Race không phải lúc nào cũng reproduce.
 * Lịch thread đổi theo số lõi, tải máy, JIT và cả debugger, nên cùng một binary lúc mất cập nhật lúc không.
 * Một lần chạy đúng tổng không chứng minh hết race, nên bài này không assert Counter mất cập nhật.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Bug concurrency không tất định: cùng input, lần này đúng, lần khác sai.
 * Debugger hoặc log làm đổi lịch, và stack trace không chỉ ra thread kia đã xen vào giữa đọc và ghi.
 * Lỗi thường chỉ lộ khi có tải và nhiều lõi, nên khó tái hiện trên máy dev.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * SafeCounter khóa increment và count bằng synchronized trên this: loại xen kẽ và tạo happens-before.
 * Cách khác: AtomicInteger.incrementAndGet, ReentrantLock, hoặc LongAdder khi nhiều thread chỉ cộng.
 * volatile không đủ, vì nó không biến count++ thành nguyên tử.
 * SOLUTION-END
 */
