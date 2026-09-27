package phase02.d18_diagnostics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase02.support.Predictions.assertPrediction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_EvidenceTest {

    private static final String HINT_Q1_DEADLOCK =
            "Đọc chuỗi DEADLOCK_DUMP đã dán, không gọi jcmd. Dòng Found one Java-level deadlock chứa chữ deadlock. contains phân biệt hoa thường.";

    private static final String HINT_Q1_IO =
            "Đọc chuỗi IO_DUMP đã dán. WAITING và socketRead là chờ I/O, không phải vòng lock. contains(\"deadlock\") trên chuỗi này là false.";

    private static final String HINT_Q2 =
            "HEAP_NOTE mô tả used tăng rồi giảm sau GC. Object đã được thu thì không phải leak. Một nhịp tăng chưa chứng minh memory leak, nên hằng cố định là false.";

    @Test
    @DisplayName("Q1 dự đoán: dump đã dán có chứa deadlock hay không")
    void q01_prediction() {
        assertTrue(Ex01_Evidence.DEADLOCK_DUMP.contains("Found one Java-level deadlock"),
                "DEADLOCK_DUMP phải là dump đã dán, có dòng Found one Java-level deadlock.");
        assertTrue(Ex01_Evidence.IO_DUMP.contains("WAITING"),
                "IO_DUMP phải có trạng thái WAITING.");
        assertTrue(Ex01_Evidence.IO_DUMP.contains("socketRead"),
                "IO_DUMP phải có socketRead.");
        assertFalse(Ex01_Evidence.IO_DUMP.contains("deadlock"),
                "IO_DUMP không được chứa chữ deadlock.");

        boolean deadlockDumpHasDeadlock = Ex01_Evidence.DEADLOCK_DUMP.contains("deadlock");
        boolean ioDumpHasDeadlock = Ex01_Evidence.IO_DUMP.contains("deadlock");
        assertPrediction("Q1_DEADLOCK_DUMP_HAS_DEADLOCK", deadlockDumpHasDeadlock,
                Ex01_Evidence.Q1_DEADLOCK_DUMP_HAS_DEADLOCK, HINT_Q1_DEADLOCK);
        assertPrediction("Q1_IO_DUMP_HAS_DEADLOCK", ioDumpHasDeadlock,
                Ex01_Evidence.Q1_IO_DUMP_HAS_DEADLOCK, HINT_Q1_IO);
    }

    @Test
    @DisplayName("Q1: khối ANSWER Q1 đã được viết")
    void q01_answerWritten() throws IOException {
        String body = answerBody("Q1");
        assertFalse(body.isBlank(),
                "TODO Q1: viết khối ANSWER Q1, phân biệt dump deadlock với thread chờ I/O.");
        assertTrue(body.contains("deadlock") && body.contains("socketRead"),
                "ANSWER Q1 phải nêu deadlock và socketRead trên dump đã dán.");
    }

    @Test
    @DisplayName("Q2 dự đoán: heap tăng có chứng minh memory leak không")
    void q02_prediction() {
        assertTrue(Ex01_Evidence.HEAP_NOTE.contains("tăng") && Ex01_Evidence.HEAP_NOTE.contains("giảm"),
                "HEAP_NOTE phải mô tả heap tăng rồi giảm sau GC.");
        assertTrue(Ex01_Evidence.HEAP_NOTE.contains("GC"),
                "HEAP_NOTE phải nhắc GC.");
        assertPrediction("Q2_RISING_HEAP_PROVES_LEAK", false,
                Ex01_Evidence.Q2_RISING_HEAP_PROVES_LEAK, HINT_Q2);
    }

    @Test
    @DisplayName("Q2: khối ANSWER Q2 đã được viết")
    void q02_answerWritten() throws IOException {
        String body = answerBody("Q2");
        assertFalse(body.isBlank(),
                "TODO Q2: viết khối ANSWER Q2, giải thích vì sao heap tăng chưa chứng minh memory leak.");
        assertTrue(body.contains("GC") && body.toLowerCase().contains("leak"),
                "ANSWER Q2 phải nêu GC và leak.");
    }

    @Test
    @DisplayName("Q3: khối ANSWER Q3 đã được viết")
    void q03_answerWritten() throws IOException {
        String body = answerBody("Q3");
        assertFalse(body.isBlank(),
                "TODO Q3: viết khối ANSWER Q3, nêu JFR thấy allocation và lock contention.");
        assertTrue(body.contains("JFR") && body.contains("allocation") && body.contains("contention"),
                "ANSWER Q3 phải nêu JFR, allocation và contention.");
    }

    @Test
    @DisplayName("Q4: khối ANSWER Q4 đã được viết")
    void q04_answerWritten() throws IOException {
        String body = answerBody("Q4");
        assertFalse(body.isBlank(),
                "TODO Q4: viết khối ANSWER Q4, kiểm tra pool, lock, I/O, không thêm thread vô hạn.");
        assertTrue(body.contains("pool") && body.contains("lock") && body.contains("I/O")
                        && body.contains("vô hạn"),
                "ANSWER Q4 phải nêu pool, lock, I/O và không thêm thread vô hạn.");
    }

    @Test
    @DisplayName("Q5: khối ANSWER Q5 đã được viết")
    void q05_answerWritten() throws IOException {
        String body = answerBody("Q5");
        assertFalse(body.isBlank(),
                "TODO Q5: viết khối ANSWER Q5, cùng workload, warm-up, nhiều lần, ghi JDK và flag.");
        String folded = body.toLowerCase();
        assertTrue(folded.contains("workload") && folded.contains("warm-up")
                        && body.contains("JDK") && folded.contains("flag"),
                "ANSWER Q5 phải nêu workload, warm-up, JDK và flag.");
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
        String relative = "src/main/java/phase02/d18_diagnostics/Ex01_Evidence.java";
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
                "Không thấy Ex01_Evidence.java từ " + System.getProperty("user.dir"));
    }
}
