package phase02.d13_executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static phase02.support.Predictions.assertPrediction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_PoolSizeAndRejectionTest {

    private static final String HINT_Q7 =
            "CPU-bound thường gần số lõi. I/O-bound cần nhiều worker hơn vì phần lớn thời gian là chờ. Không dùng chung một công thức.";

    private static final String HINT_Q8 =
            "Queue không trần thì task xếp chồng đến khi hết heap. Fixed pool của Executors dùng LinkedBlockingQueue không giới hạn thực tế.";

    @Test
    @DisplayName("Q5: khối ANSWER Q5 đã được viết")
    void q05_answerWritten() throws IOException {
        assertFalse(answerBody("Q5").isBlank(),
                "TODO Q5: viết khối ANSWER Q5, nêu stack và chi phí chuyển ngữ cảnh.");
    }

    @Test
    @DisplayName("Q6: khối ANSWER Q6 đã được viết")
    void q06_answerWritten() throws IOException {
        assertFalse(answerBody("Q6").isBlank(),
                "TODO Q6: viết khối ANSWER Q6, nêu task chờ trong queue và độ trễ.");
    }

    @Test
    @DisplayName("Q7 dự đoán: CPU-bound và I/O-bound có cùng pool size không")
    void q07_prediction() {
        assertPrediction("Q7_SAME_POOL_SIZE_FOR_IO_AND_CPU", false,
                Ex02_PoolSizeAndRejection.Q7_SAME_POOL_SIZE_FOR_IO_AND_CPU, HINT_Q7);
    }

    @Test
    @DisplayName("Q8 dự đoán: unbounded queue có thể làm cạn bộ nhớ không")
    void q08_prediction() {
        assertPrediction("Q8_UNBOUNDED_QUEUE_CAN_OOM", true,
                Ex02_PoolSizeAndRejection.Q8_UNBOUNDED_QUEUE_CAN_OOM, HINT_Q8);
    }

    @Test
    @DisplayName("Q9: pool và queue đầy thì submit ném RejectedExecutionException")
    void q09_rejectsWhenPoolAndQueueAreFull() throws InterruptedException {
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch gate = new CountDownLatch(1);
        ExecutorService es = Ex02_PoolSizeAndRejection.bounded(1, 1);
        try {
            assertInstanceOf(ThreadPoolExecutor.class, es, "bounded phải trả ThreadPoolExecutor.");
            ThreadPoolExecutor pool = (ThreadPoolExecutor) es;
            assertInstanceOf(ArrayBlockingQueue.class, pool.getQueue(), "Queue phải là ArrayBlockingQueue.");
            assertEquals(1, pool.getCorePoolSize(), "core phải bằng n.");
            assertEquals(1, pool.getMaximumPoolSize(), "maximum phải bằng n.");
            assertEquals(1, pool.getQueue().remainingCapacity(), "Sức chứa queue phải bằng đối số queue.");
            assertInstanceOf(ThreadPoolExecutor.AbortPolicy.class, pool.getRejectedExecutionHandler(),
                    "Policy phải là AbortPolicy.");
            AtomicReference<Thread> worker = new AtomicReference<>();
            es.execute(() -> {
                worker.set(Thread.currentThread());
                started.countDown();
                try {
                    gate.await();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            });
            assertTrue(started.await(10, TimeUnit.SECONDS), "Worker chưa bắt đầu trong 10 giây.");
            assertTrue(worker.get().isDaemon(), "Worker của bounded phải là daemon.");
            try {
                es.submit(() -> "xep-hang");
            } catch (RejectedExecutionException ex) {
                fail("Task thứ hai phải vào queue khi một worker đang bận và queue còn chỗ.");
            }
            assertThrows(RejectedExecutionException.class,
                    () -> es.submit(() -> "qua-suc"),
                    "Khi pool và queue đều đầy, submit phải ném RejectedExecutionException.");
        } finally {
            gate.countDown();
            close(es);
        }
    }

    private static void close(ExecutorService es) {
        if (es == null) {
            return;
        }
        es.shutdownNow();
        try {
            if (!es.awaitTermination(10, TimeUnit.SECONDS)) {
                fail("Executor còn thread sống sau 10 giây.");
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            fail("Bị ngắt khi chờ executor tắt.");
        }
    }

    private static String answerBody(String id) throws IOException {
        String source = Files.readString(exerciseSource(), StandardCharsets.UTF_8);
        String header = "/* ANSWER " + id + ":";
        int start = source.indexOf(header);
        if (start < 0) {
            throw new AssertionError("TODO " + id + ": thiếu khối ANSWER " + id);
        }
        int end = source.indexOf("*/", start);
        if (end < 0) {
            throw new AssertionError("TODO " + id + ": khối ANSWER " + id + " chưa đóng");
        }
        StringBuilder text = new StringBuilder();
        for (String line : source.substring(start + header.length(), end).split("\\R")) {
            String cleaned = line.replaceFirst("^\\s*\\*?", "").trim();
            if (cleaned.isBlank() || cleaned.equals("SOLUTION-BEGIN") || cleaned.equals("SOLUTION-END")) {
                continue;
            }
            if (!text.isEmpty()) {
                text.append('\n');
            }
            text.append(cleaned);
        }
        return text.toString();
    }

    private static Path exerciseSource() {
        String relative = "src/main/java/phase02/d13_executor/Ex02_PoolSizeAndRejection.java";
        String fromRoot = "phase-02-jvm-concurrency/" + relative;
        Path dir = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        for (int i = 0; i < 8 && dir != null; i++) {
            Path inModule = dir.resolve(relative);
            if (Files.isRegularFile(inModule)) {
                return inModule;
            }
            Path inRepo = dir.resolve(fromRoot);
            if (Files.isRegularFile(inRepo)) {
                return inRepo;
            }
            dir = dir.getParent();
        }
        throw new IllegalStateException(
                "Không thấy Ex02_PoolSizeAndRejection.java từ " + System.getProperty("user.dir"));
    }
}
