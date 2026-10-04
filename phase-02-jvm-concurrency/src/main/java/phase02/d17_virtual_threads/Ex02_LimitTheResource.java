package phase02.d17_virtual_threads;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Virtual threads — Bài 2: Giới hạn tài nguyên
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 17 (Virtual threads), câu 2, 4.
 * Cần làm trước: Ex01_IoNotCpu.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex02_LimitTheResourceTest bằng nút ▶
 * (Ctrl+Shift+F10).
 * <p>
 * Q2 [CODE] Vì sao 100.000 virtual threads vẫn có thể làm cạn DB connection pool?
 *   Bắt đầu   : cài {@code maxInFlight} và {@code acquiredWithTry}. Dùng
 *               {@code Executors.newVirtualThreadPerTaskExecutor}, {@code Semaphore} và
 *               {@code AtomicInteger}. {@code acquiredWithTry} chỉ gọi {@code tryAcquire}, không chờ,
 *               và nhả permit sau khi mọi task đã gọi xong. Không dùng {@code Thread.sleep}.
 *               Không pool virtual thread.
 *   Tra cứu   : Ctrl+N → Semaphore → Ctrl+F12 → tryAcquire, rồi acquire, Ctrl+Q.
 *               Ctrl+N → Executors → Ctrl+F12 → newVirtualThreadPerTaskExecutor → Ctrl+Q.
 *   Kiểm chứng: chạy q02_maxInFlightStaysWithinPermits và q02_acquiredWithTryReturnsTwenty.
 *   Hoàn thành khi: hai test q02_* xanh và viết xong khối ANSWER Q2.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Timeout và hủy request nên được truyền tới tác vụ con như thế nào?
 *   Bắt đầu   : viết khối ANSWER Q4. Nêu cách hủy một {@code Future} để ngắt tác vụ con.
 *               Không viết code gọi mạng.
 *   Tra cứu   : Ctrl+N → Future → Ctrl+F12 → cancel → Ctrl+Q.
 *   Hoàn thành khi: viết xong khối ANSWER Q4.
 */
public class Ex02_LimitTheResource {

    /**
     * {@code tasks} virtual thread. Mỗi thread {@code acquire} một {@code Semaphore(permits)},
     * ghi số đang giữ bằng {@code AtomicInteger}, và giữ permit đến khi đủ thread đã chiếm.
     * Sau đó nhả. Trả mức đồng thời cao nhất.
     * Không dùng {@code Thread.sleep}. Không pool virtual thread.
     *
     * @throws IllegalArgumentException nếu {@code tasks < 0} hoặc {@code permits < 0}
     * @throws IllegalStateException nếu còn thread sống sau 10 giây
     */
    static int maxInFlight(int tasks, int permits) {
        throw new UnsupportedOperationException("TODO Q2");
    }

    /**
     * {@code tasks} virtual thread. Mỗi thread gọi {@code tryAcquire} một lần và không chờ.
     * Đếm số lần chiếm được. Nhả permit chỉ sau khi mọi thread đã gọi xong.
     * Không dùng {@code Thread.sleep}. Không pool virtual thread.
     *
     * @throws IllegalArgumentException nếu {@code tasks < 0} hoặc {@code permits < 0}
     * @throws IllegalStateException nếu còn thread sống sau 10 giây
     */
    static int acquiredWithTry(int tasks, int permits) {
        throw new UnsupportedOperationException("TODO Q2");
    }

    private static void joinAll(Thread[] workers) {
        for (Thread worker : workers) {
            if (worker == null) {
                throw new IllegalStateException("Thread còn sống sau khi join tối đa 10 giây.");
            }
            try {
                worker.join(10_000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Bị ngắt khi chờ virtual thread.", ex);
            }
            if (worker.isAlive()) {
                throw new IllegalStateException(
                        "Thread còn sống sau khi join tối đa 10 giây: " + worker.getName());
            }
        }
    }
}

/* ANSWER Q2:
 *
 */

/* ANSWER Q4:
 *
 */
