package phase02.d16_thread_safety;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static phase02.support.Predictions.assertPrediction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_NoSharedMutableStateTest {

    private static final String HINT_Q1 =
            "Stateless service không giữ field thay đổi dùng chung. Mỗi lời gọi chỉ dùng tham số và biến cục bộ, nên các thread không ghi đè kết quả của nhau.";

    private static final String HINT_Q3 =
            "Biến cục bộ nằm trên stack của từng thread. Thread khác không đọc được biến đó, nên không synchronize chỉ vì nó là biến local.";

    @Test
    @DisplayName("Q1 dự đoán: stateless service thường có thread-safe không")
    void q01_prediction() {
        assertPrediction("Q1_STATELESS_SAFE", true,
                Ex01_NoSharedMutableState.Q1_STATELESS_SAFE, HINT_Q1);
    }

    @Test
    @DisplayName("Q1: addAll cộng danh sách, square giữ bình phương thuần")
    void q01_addAllSums() {
        assertEquals(15, Ex01_NoSharedMutableState.addAll(List.of(1, 2, 3, 4, 5)),
                "Tổng 1+2+3+4+5 phải bằng 15.");
        assertEquals(0, Ex01_NoSharedMutableState.addAll(List.of()),
                "Danh sách rỗng có tổng 0.");
        assertEquals(-2, Ex01_NoSharedMutableState.addAll(List.of(3, -5)),
                "Tổng phải tính cả số âm.");
        assertEquals(9, Ex01_NoSharedMutableState.square(3),
                "square(3) phải bằng 9.");
        assertEquals(4, Ex01_NoSharedMutableState.square(-2),
                "square(-2) phải bằng 4.");
    }

    @Test
    @DisplayName("Q1: khối ANSWER Q1 đã được viết")
    void q01_answerWritten() throws IOException {
        assertAnswerWritten("Q1");
    }

    @Test
    @DisplayName("Q2: khối ANSWER Q2 đã được viết")
    void q02_answerWritten() throws IOException {
        assertAnswerWritten("Q2");
    }

    @Test
    @DisplayName("Q3 dự đoán: biến cục bộ có cần synchronize không")
    void q03_prediction() {
        assertPrediction("Q3_LOCAL_NEEDS_SYNC", false,
                Ex01_NoSharedMutableState.Q3_LOCAL_NEEDS_SYNC, HINT_Q3);
    }

    @Test
    @DisplayName("Q3: khối ANSWER Q3 đã được viết")
    void q03_answerWritten() throws IOException {
        assertAnswerWritten("Q3");
    }

    @Test
    @DisplayName("Q4: khối ANSWER Q4 đã được viết")
    void q04_answerWritten() throws IOException {
        assertAnswerWritten("Q4");
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
        String relative = "src/main/java/phase02/d16_thread_safety/Ex01_NoSharedMutableState.java";
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
                "Không thấy Ex01_NoSharedMutableState.java từ " + System.getProperty("user.dir"));
    }
}
