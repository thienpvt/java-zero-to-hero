package phase02.d17_virtual_threads;

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
class Ex01_IoNotCpuTest {

    private static final String HINT_Q3 =
            "Tác vụ CPU-bound giữ carrier suốt thời gian tính. Thêm virtual thread không làm phép tính nhanh hơn. Hằng này là false.";

    private static final String HINT_Q5 =
            "Mỗi task một virtual thread mới. Giới hạn bằng semaphore hoặc pool kết nối, không pool virtual thread. Hằng này là false.";

    @Test
    @DisplayName("Q1 thí nghiệm: runExperiment báo cáo có chữ virtual")
    void q01_experimentRuns() {
        String report = Ex01_IoNotCpu.runExperiment(4);
        assertTrue(report != null && !report.isBlank(), "runExperiment phải trả báo cáo không rỗng.");
        assertTrue(report.contains("virtual"), "Báo cáo phải có chữ virtual.");
        assertTrue(report.contains("isVirtual=true"), "Mỗi task phải chạy trên virtual thread.");
    }

    @Test
    @DisplayName("Q1: khối ANSWER Q1 đã được viết")
    void q01_answerWritten() throws IOException {
        assertAnswerWritten("Q1");
    }

    @Test
    @DisplayName("Q3 dự đoán: virtual thread có làm CPU-bound nhanh hơn không")
    void q03_prediction() {
        assertPrediction("Q3_VIRTUAL_SPEEDS_CPU_BOUND", false,
                Ex01_IoNotCpu.Q3_VIRTUAL_SPEEDS_CPU_BOUND, HINT_Q3);
    }

    @Test
    @DisplayName("Q3: khối ANSWER Q3 đã được viết")
    void q03_answerWritten() throws IOException {
        assertAnswerWritten("Q3");
    }

    @Test
    @DisplayName("Q5 dự đoán: có nên pool virtual thread chỉ để giới hạn concurrency không")
    void q05_prediction() {
        assertPrediction("Q5_POOL_VIRTUAL_THREADS_TO_LIMIT", false,
                Ex01_IoNotCpu.Q5_POOL_VIRTUAL_THREADS_TO_LIMIT, HINT_Q5);
    }

    @Test
    @DisplayName("Q5: khối ANSWER Q5 đã được viết")
    void q05_answerWritten() throws IOException {
        assertAnswerWritten("Q5");
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
        String relative = "src/main/java/phase02/d17_virtual_threads/Ex01_IoNotCpu.java";
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
        throw new IllegalStateException("Không thấy Ex01_IoNotCpu.java từ " + System.getProperty("user.dir"));
    }
}
