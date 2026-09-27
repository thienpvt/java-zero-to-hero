package phase02.d17_virtual_threads;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_LimitTheResourceTest {

    @Test
    @DisplayName("Q2: maxInFlight(30, 20) lớn hơn 0 và không vượt 20 permit")
    void q02_maxInFlightStaysWithinPermits() {
        int max = Ex02_LimitTheResource.maxInFlight(30, 20);
        assertTrue(max > 0, "Phải có ít nhất một task giữ permit.");
        assertTrue(max <= 20, "Số đồng thời không được vượt số permit của Semaphore.");
    }

    @Test
    @DisplayName("Q2: acquiredWithTry(30, 20) trả 20")
    void q02_acquiredWithTryReturnsTwenty() {
        assertEquals(20, Ex02_LimitTheResource.acquiredWithTry(30, 20),
                "30 task, 20 permit, tryAcquire không chờ: số lần chiếm thành công phải là 20.");
        assertEquals(4, Ex02_LimitTheResource.acquiredWithTry(4, 10),
                "Khi task ít hơn permit, mọi tryAcquire thành công.");
        assertEquals(3, Ex02_LimitTheResource.acquiredWithTry(5, 3),
                "Khi permit ít hơn task, chỉ đúng số permit được chiếm.");
    }

    @Test
    @DisplayName("Q2: khối ANSWER Q2 đã được viết")
    void q02_answerWritten() throws IOException {
        assertAnswerWritten("Q2");
    }

    @Test
    @DisplayName("Q4: khối ANSWER Q4 đã được viết")
    void q04_answerWritten() throws IOException {
        assertAnswerWritten("Q4");
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
        String relative = "src/main/java/phase02/d17_virtual_threads/Ex02_LimitTheResource.java";
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
                "Không thấy Ex02_LimitTheResource.java từ " + System.getProperty("user.dir"));
    }
}
