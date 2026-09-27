package phase02.d14_completable_future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static phase02.support.Predictions.assertPrediction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_ErrorsAndPoolTest {

    private static final String HINT_Q6 =
            "Javadoc của supplyAsync(Supplier) nói dùng ForkJoinPool.commonPool(). Đừng đoán theo tên thread, vì common pool có thể chạy inline trên caller.";

    @Test
    @DisplayName("Q4: exceptionally trả 0 khi stage lỗi")
    void q04_zeroWhenFailed() {
        CompletableFuture<Integer> failed = new CompletableFuture<>();
        failed.completeExceptionally(new IllegalStateException("stage lỗi"));
        CompletableFuture<Integer> recovered = Ex02_ErrorsAndPool.zeroOnFailure(failed);
        assertEquals(0, recovered.join(), "Khi stage lỗi, exceptionally phải trả 0.");
    }

    @Test
    @DisplayName("Q4: exceptionally giữ giá trị khi stage thành công")
    void q04_keepsSuccess() {
        CompletableFuture<Integer> recovered =
                Ex02_ErrorsAndPool.zeroOnFailure(CompletableFuture.completedFuture(4));
        assertEquals(4, recovered.join(), "Khi stage thành công, exceptionally phải giữ nguyên giá trị.");
    }

    @Test
    @DisplayName("Q6 dự đoán: supplyAsync() không truyền Executor chạy ở đâu")
    void q06_defaultPool() {
        // Cố định theo Javadoc. Không gọi supplyAsync và không assert tên thread.
        assertPrediction("Q6_DEFAULT_POOL", "ForkJoinPool.commonPool",
                Ex02_ErrorsAndPool.Q6_DEFAULT_POOL, HINT_Q6);
    }

    @Test
    @DisplayName("Q7: khối ANSWER Q7 đã được viết")
    void q07_answerWritten() throws IOException {
        assertFalse(answerBody("Q7").isBlank(),
                "TODO Q7: viết khối ANSWER Q7, nêu vì sao blocking task trên common pool có thể làm pool đói.");
    }

    @Test
    @DisplayName("Q8: supplier ghi tên thread của executor, thenApply đọc tên đó")
    void q08_recordsAppThread() throws InterruptedException {
        AtomicReference<String> threadName = new AtomicReference<>();
        Thread[] worker = new Thread[1];
        ThreadFactory factory = task -> {
            Thread thread = new Thread(task, "app");
            thread.setDaemon(true);
            worker[0] = thread;
            return thread;
        };
        ExecutorService executor = Executors.newSingleThreadExecutor(factory);
        try {
            CompletableFuture<Integer> started = Ex02_ErrorsAndPool.supplyOn(executor, threadName);
            String recorded = readRecordedName(started, threadName);
            assertEquals("app", recorded, "thenApply phải đọc đúng tên thread mà supplier đã ghi.");
        } finally {
            executor.shutdown();
            if (worker[0] != null) {
                worker[0].join(10_000);
                if (worker[0].isAlive()) {
                    executor.shutdownNow();
                    fail("Thread còn sống sau 10 giây: " + worker[0].getName());
                }
            }
        }
    }

    private static String readRecordedName(CompletableFuture<Integer> started, AtomicReference<String> threadName) {
        try {
            return started.thenApply(ignored -> threadName.get()).get(10, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            fail("Future chưa xong sau 10 giây.");
        } catch (ExecutionException ex) {
            throw new AssertionError("supplyOn hoàn thành với lỗi: " + ex.getCause(), ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            fail("Bị ngắt trong khi chờ supplyOn.");
        }
        throw new AssertionError("Không đọc được tên thread.");
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
        String relative = "src/main/java/phase02/d14_completable_future/Ex02_ErrorsAndPool.java";
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
                "Không thấy Ex02_ErrorsAndPool.java từ " + System.getProperty("user.dir"));
    }
}
