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
 *   Bắt đầu   : cài đặt {@code takeSum(BlockingQueue<Integer>, int)}. Một thread khác {@code put}
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
    static final Boolean Q5_WRITE_COPIES_ARRAY = null;

    // Q7 — offer trên ArrayBlockingQueue đã đầy có bị từ chối hay không.
    static final Boolean Q7_OFFER_FALSE_WHEN_FULL = null;

    /**
     * Thread khác {@code put} các số từ 0 đến {@code n - 1} vào {@code queue}.
     * Method này {@code take} đúng {@code n} phần tử và trả tổng của chúng.
     *
     * @param queue queue đang rỗng; producer và consumer dùng chung queue này
     * @param n số phần tử cần lấy
     * @return tổng các phần tử đã {@code take}
     * @throws IllegalArgumentException nếu {@code queue} là null hoặc {@code n < 0}
     * @throws InterruptedException nếu bị ngắt trong lúc {@code take} hoặc {@code join}
     */
    static int takeSum(BlockingQueue<Integer> queue, int n) throws InterruptedException {
        throw new UnsupportedOperationException("TODO Q6");
    }
}

/* ANSWER Q4:
 *
 */

/* ANSWER Q5:
 *
 */

/* ANSWER Q6:
 *
 */

/* ANSWER Q7:
 *
 */
