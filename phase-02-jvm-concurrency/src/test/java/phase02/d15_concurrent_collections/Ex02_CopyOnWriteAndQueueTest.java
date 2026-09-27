package phase02.d15_concurrent_collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase02.support.Predictions.assertPrediction;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_CopyOnWriteAndQueueTest {

    private static final String HINT_Q5 =
            "add của CopyOnWriteArrayList cấp một mảng mới và chép phần tử sang đó. Write trả giá bằng bản sao cả mảng.";

    private static final String HINT_Q7 =
            "ArrayBlockingQueue dung lượng 1 đã có một phần tử thì offer tiếp trả false và không chờ. put mới là lời gọi chờ khi đầy.";

    @Test
    @DisplayName("Q5 dự đoán: write của CopyOnWriteArrayList có sao chép mảng không")
    void q05_prediction() {
        assertPrediction("Q5_WRITE_COPIES_ARRAY", true,
                Ex02_CopyOnWriteAndQueue.Q5_WRITE_COPIES_ARRAY, HINT_Q5);
    }

    @Test
    @DisplayName("Q6: takeSum lấy đúng các số producer đã put, tổng từ 0 đến n-1")
    void q06_takeSumOfZeroThroughNMinusOne() throws InterruptedException {
        int n = 1_000;
        LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
        AtomicInteger sum = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        List<Throwable> failures = new CopyOnWriteArrayList<>();
        Thread worker = new Thread(() -> {
            try {
                start.await();
                sum.set(Ex02_CopyOnWriteAndQueue.takeSum(queue, n));
            } catch (Throwable failure) {
                failures.add(failure);
            }
        }, "take-sum");
        worker.setDaemon(true);
        worker.start();
        start.countDown();
        worker.join(10_000);
        assertFalse(worker.isAlive(),
                () -> "Thread còn sống sau khi join tối đa 10 giây: " + worker.getName());
        rethrowFailures(failures);

        int expected = 0;
        for (int i = 0; i < n; i++) {
            expected += i;
        }
        assertEquals(expected, sum.get(), "Tổng các số từ 0 đến n-1 phải đúng.");
        assertTrue(queue.isEmpty(), "Sau khi take đủ n phần tử, queue phải rỗng.");
    }

    @Test
    @DisplayName("Q7 dự đoán: offer trên ArrayBlockingQueue dung lượng 1 đã đầy")
    void q07_offerWhenCapacityOneIsFull() {
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(1);
        boolean firstAccepted = queue.offer(1);
        boolean secondAccepted = queue.offer(2);
        assertTrue(firstAccepted, "offer khi queue còn chỗ phải được nhận.");
        boolean offerFalseWhenFull = firstAccepted && !secondAccepted;
        assertPrediction("Q7_OFFER_FALSE_WHEN_FULL", offerFalseWhenFull,
                Ex02_CopyOnWriteAndQueue.Q7_OFFER_FALSE_WHEN_FULL, HINT_Q7);
    }

    private static void rethrowFailures(List<Throwable> failures) {
        if (failures.isEmpty()) {
            return;
        }
        Throwable first = failures.getFirst();
        for (int i = 1; i < failures.size(); i++) {
            first.addSuppressed(failures.get(i));
        }
        if (first instanceof RuntimeException runtime) {
            throw runtime;
        }
        if (first instanceof Error error) {
            throw error;
        }
        throw new IllegalStateException(first);
    }
}
