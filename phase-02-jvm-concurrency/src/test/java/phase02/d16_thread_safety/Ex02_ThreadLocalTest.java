package phase02.d16_thread_safety;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_ThreadLocalTest {

    @Test
    @DisplayName("Q6: hai thread gắn hai tên, mỗi bên đọc đúng tên của mình")
    void q06_eachThreadKeepsOwnName() throws InterruptedException {
        String[] seen = new String[2];
        Throwable[] failures = new Throwable[2];
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch bothSet = new CountDownLatch(2);
        Thread first = new Thread(
                () -> recordOwnName(start, bothSet, "An", seen, 0, failures), "user-an");
        Thread second = new Thread(
                () -> recordOwnName(start, bothSet, "Binh", seen, 1, failures), "user-binh");
        first.setDaemon(true);
        second.setDaemon(true);
        first.start();
        second.start();
        start.countDown();
        joinWithinTenSeconds(first);
        joinWithinTenSeconds(second);
        rethrowFirst(failures);
        assertEquals("An", seen[0], "Thread user-an phải đọc đúng tên An, không đọc tên của thread kia.");
        assertEquals("Binh", seen[1], "Thread user-binh phải đọc đúng tên Binh, không đọc tên của thread kia.");
        assertNull(Ex02_ThreadLocal.user(),
                "Thread đang chạy test không được đọc tên mà thread kia đã gắn.");
    }

    @Test
    @DisplayName("Q6: khối ANSWER Q6 đã được viết")
    void q06_answerWritten() throws IOException {
        assertAnswerWritten("Q6");
    }

    @Test
    @DisplayName("Q7: sau clear, user() là null")
    void q07_clearThenGetIsNull() {
        Ex02_ThreadLocal.setUser("Mai");
        assertEquals("Mai", Ex02_ThreadLocal.user(),
                "Trước clear, thread này phải đọc đúng tên vừa gắn.");
        Ex02_ThreadLocal.clear();
        assertNull(Ex02_ThreadLocal.user(), "Sau clear, get phải là null.");
    }

    @Test
    @DisplayName("Q7: khối ANSWER Q7 đã được viết")
    void q07_answerWritten() throws IOException {
        assertAnswerWritten("Q7");
    }

    private static void recordOwnName(CountDownLatch start, CountDownLatch bothSet, String name,
            String[] seen, int index, Throwable[] failures) {
        try {
            start.await();
            Ex02_ThreadLocal.setUser(name);
            bothSet.countDown();
            bothSet.await();
            seen[index] = Ex02_ThreadLocal.user();
        } catch (Throwable failure) {
            failures[index] = failure;
        }
    }

    private static void joinWithinTenSeconds(Thread thread) throws InterruptedException {
        thread.join(10_000);
        assertFalse(thread.isAlive(),
                "Thread còn sống sau khi join tối đa 10 giây: " + thread.getName());
    }

    private static void rethrowFirst(Throwable[] failures) {
        Throwable first = null;
        for (Throwable failure : failures) {
            if (failure == null) {
                continue;
            }
            if (first == null) {
                first = failure;
            } else {
                first.addSuppressed(failure);
            }
        }
        if (first == null) {
            return;
        }
        if (first instanceof RuntimeException runtime) {
            throw runtime;
        }
        if (first instanceof Error error) {
            throw error;
        }
        throw new IllegalStateException("Thread kết thúc vì lỗi.", first);
    }

    private static void assertAnswerWritten(String id) throws IOException {
        assertFalse(answerBody(id).isBlank(),
                "TODO " + id + ": viết khối ANSWER " + id + ".");
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
        String relative = "src/main/java/phase02/d16_thread_safety/Ex02_ThreadLocal.java";
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
                "Không thấy Ex02_ThreadLocal.java từ " + System.getProperty("user.dir"));
    }
}
