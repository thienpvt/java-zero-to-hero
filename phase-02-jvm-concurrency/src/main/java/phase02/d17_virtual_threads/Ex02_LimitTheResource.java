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
        // SOLUTION-BEGIN throw Q2
        if (tasks < 0 || permits < 0) {
            throw new IllegalArgumentException("tasks và permits không được âm.");
        }
        if (tasks == 0 || permits == 0) {
            return 0;
        }
        Semaphore semaphore = new Semaphore(permits);
        AtomicInteger inFlight = new AtomicInteger();
        AtomicInteger max = new AtomicInteger();
        CountDownLatch holding = new CountDownLatch(Math.min(tasks, permits));
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch finished = new CountDownLatch(tasks);
        Thread[] workers = new Thread[tasks];
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        try {
            for (int i = 0; i < tasks; i++) {
                int id = i;
                executor.submit(() -> {
                    workers[id] = Thread.currentThread();
                    boolean held = false;
                    try {
                        semaphore.acquire();
                        held = true;
                        int now = inFlight.incrementAndGet();
                        max.accumulateAndGet(now, Math::max);
                        holding.countDown();
                        release.await();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    } finally {
                        if (held) {
                            inFlight.decrementAndGet();
                            semaphore.release();
                        }
                        finished.countDown();
                    }
                });
            }
            if (!holding.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Thread còn sống sau khi join tối đa 10 giây.");
            }
            release.countDown();
            if (!finished.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Thread còn sống sau khi join tối đa 10 giây.");
            }
            return max.get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Bị ngắt khi chờ virtual thread.", ex);
        } finally {
            release.countDown();
            executor.shutdownNow();
            joinAll(workers);
        }
        // SOLUTION-END
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
        // SOLUTION-BEGIN throw Q2
        if (tasks < 0 || permits < 0) {
            throw new IllegalArgumentException("tasks và permits không được âm.");
        }
        Semaphore semaphore = new Semaphore(permits);
        AtomicInteger acquired = new AtomicInteger();
        CountDownLatch done = new CountDownLatch(tasks);
        Thread[] workers = new Thread[tasks];
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        try {
            for (int i = 0; i < tasks; i++) {
                int id = i;
                executor.submit(() -> {
                    workers[id] = Thread.currentThread();
                    try {
                        if (semaphore.tryAcquire()) {
                            acquired.incrementAndGet();
                        }
                    } finally {
                        done.countDown();
                    }
                });
            }
            if (!done.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Thread còn sống sau khi join tối đa 10 giây.");
            }
            int won = acquired.get();
            if (won > 0) {
                semaphore.release(won);
            }
            return won;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Bị ngắt khi chờ virtual thread.", ex);
        } finally {
            executor.shutdownNow();
            joinAll(workers);
        }
        // SOLUTION-END
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
 * SOLUTION-BEGIN
 * 100.000 virtual thread vẫn xin kết nối từ một pool có trần nhỏ hơn nhiều.
 * Virtual thread không nhân số kết nối DB. Hết permit thì request chờ hoặc bị từ chối.
 * maxInFlight giữ Semaphore(permits) và ghi số đang giữ bằng AtomicInteger. Mức cao nhất không vượt permits.
 * acquiredWithTry gọi tryAcquire, không chờ, và chỉ nhả sau khi mọi task đã gọi. 30 task và 20 permit chiếm được 20.
 * Giới hạn tài nguyên khan, không giới hạn bằng cách pool virtual thread.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Hạn của request phải thành hạn của tác vụ con, rồi hủy phần còn chạy.
 * Future.cancel(true) ngắt thread của task. Lời gọi đang chặn ném InterruptedException.
 * Tác vụ con thấy interrupt thì dừng và nhả tài nguyên, không nuốt cờ ngắt.
 * Không cần gọi mạng: interrupt đi theo future của task con.
 * SOLUTION-END
 */
