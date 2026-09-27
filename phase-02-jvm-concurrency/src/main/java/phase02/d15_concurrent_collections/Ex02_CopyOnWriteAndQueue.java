package phase02.d15_concurrent_collections;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * Concurrent collections — Bài 2: CopyOnWriteArrayList và BlockingQueue
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 15 (Concurrent Collections), câu 4, 5, 6, 7.
 * Cần làm trước: Ex01_ConcurrentMap.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex02_CopyOnWriteAndQueueTest bằng nút ▶
 * (Ctrl+Shift+F10).
 * <p>
 * Q4 [TỰ TRẢ LỜI] CopyOnWriteArrayList tốt cho workload nào?
 *   Bắt đầu   : viết khối ANSWER Q4. Ctrl+N mở CopyOnWriteArrayList, Ctrl+Q đọc đoạn đầu Javadoc.
 *   Hoàn thành khi: viết xong khối ANSWER Q4, có nêu tương quan giữa số lần đọc và số lần ghi.
 * <p>
 * Q5 [DỰ ĐOÁN] Vì sao write của CopyOnWriteArrayList đắt?
 *   Bắt đầu   : điền Q5_WRITE_COPIES_ARRAY (thay null). Ctrl+N mở CopyOnWriteArrayList, Ctrl+F12
 *               tìm add, Ctrl+B vào thân method và xem write đụng mảng phần tử thế nào.
 *               Viết khối ANSWER Q5.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05 xanh và ANSWER Q5 giải thích chi phí của write.
 * <p>
 * Q6 [CODE] BlockingQueue giải quyết producer-consumer như thế nào?
 *   Bắt đầu   : cài đặt {@code takeSum(BlockingQueue&lt;Integer&gt;, int)}. Một thread khác {@code put}
 *               các số từ 0 đến n - 1. Method này {@code take} đúng n phần tử và trả tổng.
 *               Viết khối ANSWER Q6.
 *   Kiểm chứng: chạy q06_takeSumOfZeroThroughNMinusOne. {@code join} tối đa 10 giây, fail nếu thread
 *               còn sống. Không dùng {@code Thread.sleep}. Ctrl+B vào {@code BlockingQueue.take} và
 *               {@code BlockingQueue.put} nếu muốn đọc chữ ký.
 *   Hoàn thành khi: q06 xanh, tổng đúng bằng tổng các số từ 0 đến n - 1.
 * <p>
 * Q7 [DỰ ĐOÁN + CODE] {@code put()} và {@code offer()} khác nhau thế nào?
 *   Bắt đầu   : điền Q7_OFFER_FALSE_WHEN_FULL (thay null). Ctrl+N mở ArrayBlockingQueue, Ctrl+F12
 *               tìm offer và put, Ctrl+Q đọc từng method. Viết khối ANSWER Q7.
 *   Kiểm chứng: chạy q07_offerWhenCapacityOneIsFull. Test chỉ gọi {@code offer} trên queue dung lượng 1.
 *               Không gọi {@code put} trong test này.
 *   Hoàn thành khi: q07 xanh và ANSWER Q7 phân biệt hai method.
 */
public class Ex02_CopyOnWriteAndQueue {

    // Q5 — write của CopyOnWriteArrayList có sao chép mảng phần tử hay không.
    static final Boolean Q5_WRITE_COPIES_ARRAY = true; // SOLUTION-VALUE

    // Q7 — offer trên ArrayBlockingQueue đã đầy có bị từ chối hay không.
    static final Boolean Q7_OFFER_FALSE_WHEN_FULL = true; // SOLUTION-VALUE

    /**
     * Thread khác {@code put} các số từ 0 đến {@code n - 1} vào {@code queue}.
     * Method này {@code take} đúng {@code n} phần tử và trả tổng của chúng.
     *
     * @param queue queue đang rỗng; producer và consumer dùng chung queue này
     * @param n số phần tử cần lấy
     * @return tổng các phần tử đã {@code take}
     * @throws IllegalArgumentException nếu {@code queue} là null hoặc {@code n &lt; 0}
     * @throws InterruptedException nếu bị ngắt trong lúc {@code take} hoặc {@code join}
     */
    static int takeSum(BlockingQueue<Integer> queue, int n) throws InterruptedException {
        // SOLUTION-BEGIN throw Q6
        if (queue == null) {
            throw new IllegalArgumentException("Queue không được null.");
        }
        if (n < 0) {
            throw new IllegalArgumentException("Số phần tử không được âm: " + n);
        }
        CountDownLatch start = new CountDownLatch(1);
        Thread producer = new Thread(() -> {
            try {
                start.await();
                for (int i = 0; i < n; i++) {
                    queue.put(i);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }, "take-sum-producer");
        producer.setDaemon(true);
        producer.start();
        start.countDown();
        int sum = 0;
        try {
            for (int i = 0; i < n; i++) {
                sum += queue.take();
            }
        } finally {
            producer.join(10_000);
        }
        if (producer.isAlive()) {
            producer.interrupt();
            throw new IllegalStateException(
                    "Thread còn sống sau khi join tối đa 10 giây: " + producer.getName());
        }
        return sum;
        // SOLUTION-END
    }
}

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * CopyOnWriteArrayList hợp khi đọc rất nhiều và ghi rất ít.
 * Lần đọc không cần khóa: iterator đi trên mảng ổn định lúc nó được tạo.
 * Danh sách listener hoặc cấu hình ít đổi là workload điển hình.
 * Ghi dày thì chi phí sao chép át lợi ích của việc đọc không khóa.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Mỗi lần ghi, lớp này sao chép cả mảng phần tử rồi thay tham chiếu bằng mảng mới.
 * Chi phí tỉ lệ với số phần tử, và iterator cũ vẫn giữ mảng trước đó.
 * Ghi thường xuyên sẽ cấp phát và sao chép liên tục.
 * Vì vậy write đắt, cấu trúc này không dành cho workload ghi nhiều.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * BlockingQueue cho producer đưa phần tử và chờ khi đầy, consumer lấy phần tử và chờ khi rỗng.
 * Hai bên không cần vòng lặp bận để dò queue rỗng hoặc đầy.
 * takeSum để một thread put các số, còn lời gọi take đúng số phần tử đó rồi cộng lại.
 * Tổng là tổng các phần tử producer đã đưa vào.
 * SOLUTION-END
 */

/* ANSWER Q7:
 * SOLUTION-BEGIN
 * offer cố thêm một phần tử và trả ngay: được nhận hoặc bị từ chối khi không còn chỗ.
 * put thêm phần tử và chờ cho đến khi có chỗ, không trả về chỉ vì queue đang đầy.
 * ArrayBlockingQueue dung lượng 1 đã chứa một phần tử thì lần offer tiếp theo bị từ chối.
 * Không gọi put trên queue đầy trong test, vì lời gọi đó có thể chờ mãi nếu không có thread take.
 * SOLUTION-END
 */
