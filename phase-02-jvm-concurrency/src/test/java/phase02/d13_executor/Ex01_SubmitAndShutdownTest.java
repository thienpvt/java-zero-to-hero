package phase02.d13_executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static phase02.support.Predictions.assertPrediction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_SubmitAndShutdownTest {

    private static final String HINT_Q2 =
            "submit trả Future để lấy kết quả hoặc exception. execute nhận Runnable và trả void.";

    @Test
    @DisplayName("Q1: khối ANSWER Q1 đã được viết")
    void q01_answerWritten() throws IOException {
        assertFalse(answerBody("Ex01_SubmitAndShutdown.java", "Q1").isBlank(),
                "TODO Q1: viết khối ANSWER Q1, nêu chi phí tạo thread và trần số worker.");
    }

    @Test
    @DisplayName("Q2 dự đoán: submit có trả về Future không")
    void q02_prediction() {
        assertPrediction("Q2_SUBMIT_RETURNS_FUTURE", true,
                Ex01_SubmitAndShutdown.Q2_SUBMIT_RETURNS_FUTURE, HINT_Q2);
    }

    @Test
    @DisplayName("Q2: submitJob gọi submit và Future.get trả ok")
    void q02_submitReturnsOk() throws Exception {
        TrackingExecutor es = new TrackingExecutor("q02-submit");
        try {
            Future<?> future = Ex01_SubmitAndShutdown.submitJob(es);
            assertEquals(1, es.submitCalls, "submitJob phải gọi submit đúng một lần.");
            assertEquals(0, es.executeCalls, "submitJob không gọi execute trực tiếp.");
            assertNotNull(future, "submit phải trả về Future, không phải null.");
            assertEquals("ok", valueWithin(future), "Future.get của submitJob phải trả chuỗi ok.");
        } finally {
            close(es);
        }
    }

    @Test
    @DisplayName("Q2: executeJob gọi execute, không gọi submit")
    void q02_executeCallsExecute() {
        TrackingExecutor es = new TrackingExecutor("q02-execute");
        try {
            Ex01_SubmitAndShutdown.executeJob(es);
            assertEquals(1, es.executeCalls, "executeJob phải gọi execute đúng một lần.");
            assertEquals(0, es.submitCalls, "executeJob không gọi submit.");
        } finally {
            close(es);
        }
    }

    @Test
    @DisplayName("Q3: resultOf trả giá trị của Callable")
    void q03_resultOfCallable() {
        ExecutorService es = Executors.newSingleThreadExecutor(daemon("q03-worker"));
        try {
            String value = Ex01_SubmitAndShutdown.resultOf(es, () -> "gia-tri-q3");
            assertEquals("gia-tri-q3", value, "Future.get phải trả đúng giá trị Callable đã trả.");
        } finally {
            close(es);
        }
    }

    @Test
    @DisplayName("Q4: shutdownNow bỏ task đã vào queue và isShutdown")
    void q04_shutdownNowSkipsQueuedTask() throws InterruptedException {
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch gate = new CountDownLatch(1);
        AtomicInteger queuedRuns = new AtomicInteger();
        ThreadPoolExecutor es = new ThreadPoolExecutor(
                1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1),
                daemon("q04-worker"));
        try {
            es.execute(() -> {
                started.countDown();
                try {
                    gate.await();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            });
            assertTrue(started.await(10, TimeUnit.SECONDS), "Worker chưa bắt đầu trong 10 giây.");
            es.execute(queuedRuns::incrementAndGet);
            assertEquals(1, es.getQueue().size(), "Task thứ hai phải đang nằm trong queue trước khi stop.");
            Ex01_SubmitAndShutdown.stop(es, true);
            assertTrue(es.isShutdown(), "stop(es, true) phải shutdown executor.");
            assertEquals(0, es.getQueue().size(), "shutdownNow phải bỏ task còn trong queue.");
            assertEquals(0, queuedRuns.get(), "Task đã vào queue không được chạy sau shutdownNow.");
        } finally {
            gate.countDown();
            close(es);
        }
    }

    @Test
    @DisplayName("Q4: now = false thì shutdown rồi awaitTermination 2 giây")
    void q04_shutdownThenAwait() {
        TrackingExecutor es = new TrackingExecutor("q04-await");
        try {
            Ex01_SubmitAndShutdown.stop(es, false);
            assertTrue(es.isShutdown(), "stop(es, false) phải shutdown executor.");
            assertTrue(es.shutdownCalled, "now = false phải gọi shutdown.");
            assertFalse(es.shutdownNowCalled, "now = false không được gọi shutdownNow.");
            assertTrue(es.awaitCalled, "now = false phải gọi awaitTermination.");
            assertEquals(2L, es.awaitTimeout, "awaitTermination phải nhận timeout 2.");
            assertEquals(TimeUnit.SECONDS, es.awaitUnit, "awaitTermination phải dùng TimeUnit.SECONDS.");
        } finally {
            close(es);
        }
    }

    private static Object valueWithin(Future<?> future) throws Exception {
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            fail("Future của submitJob chưa xong sau 10 giây.");
            return null;
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

    private static ThreadFactory daemon(String name) {
        return task -> {
            Thread thread = new Thread(task, name);
            thread.setDaemon(true);
            return thread;
        };
    }

    private static String answerBody(String fileName, String id) throws IOException {
        String source = Files.readString(exerciseSource(fileName), StandardCharsets.UTF_8);
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

    private static Path exerciseSource(String fileName) {
        String relative = "src/main/java/phase02/d13_executor/" + fileName;
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
        throw new IllegalStateException("Không thấy " + fileName + " từ " + System.getProperty("user.dir"));
    }

    /**
     * Đếm {@code execute} và {@code submit} riêng. {@code submit} ủy thác cho delegate,
     * không đi qua {@code execute} của lớp này.
     */
    private static final class TrackingExecutor extends AbstractExecutorService {
        private final ExecutorService delegate;
        private int executeCalls;
        private int submitCalls;
        private boolean shutdownCalled;
        private boolean shutdownNowCalled;
        private boolean awaitCalled;
        private long awaitTimeout;
        private TimeUnit awaitUnit;

        TrackingExecutor(String threadName) {
            delegate = Executors.newSingleThreadExecutor(daemon(threadName));
        }

        @Override
        public void execute(Runnable command) {
            executeCalls++;
            delegate.execute(command);
        }

        @Override
        public Future<?> submit(Runnable task) {
            submitCalls++;
            return delegate.submit(task);
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            submitCalls++;
            return delegate.submit(task, result);
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            submitCalls++;
            return delegate.submit(task);
        }

        @Override
        public void shutdown() {
            shutdownCalled = true;
            delegate.shutdown();
        }

        @Override
        public List<Runnable> shutdownNow() {
            shutdownNowCalled = true;
            return delegate.shutdownNow();
        }

        @Override
        public boolean isShutdown() {
            return delegate.isShutdown();
        }

        @Override
        public boolean isTerminated() {
            return delegate.isTerminated();
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            awaitCalled = true;
            awaitTimeout = timeout;
            awaitUnit = unit;
            return delegate.awaitTermination(timeout, unit);
        }
    }
}
