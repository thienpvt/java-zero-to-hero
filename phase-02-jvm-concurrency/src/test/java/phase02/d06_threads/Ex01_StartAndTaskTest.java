package phase02.d06_threads;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static phase02.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_StartAndTaskTest {

    private static final String HINT_Q2_RUN =
            "run() thực thi Runnable trên thread đang gọi method. Tên ghi lại trùng tên thread đó, không phải worker-q2.";

    private static final String HINT_Q2_START =
            "start() chạy Runnable trên thread mới tên worker-q2. Tên đó khác thread đang gọi.";

    @Test
    @DisplayName("Q2: run() ghi tên thread đang gọi, không phải worker-q2")
    void q02_runUsesCallerThread() {
        String caller = Thread.currentThread().getName();
        String seen = Ex01_StartAndTask.runOnCaller();
        assertNotEquals("worker-q2", seen,
                "run() phải chạy trên thread đang gọi, không phải thread tên worker-q2.");
        assertPrediction("Q2_RUN_USES_CALLER_THREAD", caller.equals(seen),
                Ex01_StartAndTask.Q2_RUN_USES_CALLER_THREAD, HINT_Q2_RUN);
    }

    @Test
    @DisplayName("Q2: start() ghi tên worker-q2, khác thread đang gọi")
    void q02_startUsesNewThread() {
        String caller = Thread.currentThread().getName();
        assertNotEquals("worker-q2", caller,
                "Thread đang chạy test không được mang tên worker-q2.");
        String seen = Ex01_StartAndTask.startOnNewThread();
        assertEquals("worker-q2", seen,
                "start() phải chạy thân công việc trên thread tên worker-q2.");
        assertPrediction("Q2_START_USES_CALLER_THREAD", caller.equals(seen),
                Ex01_StartAndTask.Q2_START_USES_CALLER_THREAD, HINT_Q2_START);
    }

    @Test
    @DisplayName("Q3: Callable trả về chuỗi ok")
    void q03_callResult() {
        assertEquals("ok", Ex01_StartAndTask.callResult(),
                "callResult() phải trả giá trị của Callable, là chuỗi ok.");
    }

    @Test
    @DisplayName("Q3: Runnable.run không trả giá trị")
    void q03_runnableReturnsValue() {
        assertFalse(Ex01_StartAndTask.runnableReturnsValue(),
                "Runnable.run là void nên không trả giá trị cho caller.");
    }

    @Test
    @DisplayName("Q4: await chờ worker tăng counter rồi mới đọc")
    void q04_await_workerIncrementsCounter() {
        int[] counter = {0};
        Thread worker = new Thread(() -> counter[0]++, "worker-q4");
        worker.start();
        Ex01_StartAndTask.await(worker);
        assertFalse(worker.isAlive(), "Thread worker-q4 vẫn còn sống sau 10 giây.");
        assertEquals(1, counter[0], "Sau await, counter phải bằng 1.");
    }
}
