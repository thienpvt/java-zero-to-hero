package phase02.d14_completable_future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static phase02.support.Predictions.assertPrediction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ComposeTest {

    private static final String HINT_Q5_GET =
            "get() khai báo checked exception. Khi stage đã completeExceptionally, lớp bọc bên ngoài là ExecutionException.";

    private static final String HINT_Q5_JOIN =
            "join() không khai báo checked exception. Khi stage lỗi, nó ném CompletionException.";

    @Test
    @DisplayName("Q1: khối ANSWER Q1 đã được viết")
    void q01_answerWritten() throws IOException {
        assertFalse(answerBody("Q1").isBlank(),
                "TODO Q1: viết khối ANSWER Q1, nêu điểm yếu của Future mà CompletableFuture giải quyết.");
    }

    @Test
    @DisplayName("Q2: thenApply hai lần, 3 thành 10")
    void q02_squareThenInc() {
        CompletableFuture<Integer> squared = Ex01_Compose.squareThenInc(CompletableFuture.completedFuture(3));
        assertEquals(10, squared.join(), "thenApply hai lần: 3 bình phương thành 9, rồi cộng 1 thành 10.");
    }

    @Test
    @DisplayName("Q2: thenCompose không lồng future")
    void q02_flatDoesNotNest() {
        CompletableFuture<Integer> flattened = Ex01_Compose.flat(CompletableFuture.completedFuture(3));
        Object value = flattened.join();
        assertEquals(3, value, "thenCompose tới completedFuture phải giữ nguyên giá trị 3.");
        assertFalse(value instanceof CompletableFuture,
                "thenCompose không được lồng future: join phải ra Integer, không phải CompletableFuture.");
    }

    @Test
    @DisplayName("Q3: thenCombine cộng 2 và 5 thành 7")
    void q03_sumTwoAndFive() {
        CompletableFuture<Integer> total = Ex01_Compose.sum(
                CompletableFuture.completedFuture(2),
                CompletableFuture.completedFuture(5));
        assertEquals(7, total.join(), "thenCombine phải cộng 2 và 5 thành 7.");
    }

    @Test
    @DisplayName("Q5 dự đoán: get() ném checked exception nào khi stage lỗi")
    void q05_getChecked() {
        CompletableFuture<Integer> failed = failedFuture();
        assertPrediction("Q5_GET_CHECKED", checkedNameFromGet(failed),
                Ex01_Compose.Q5_GET_CHECKED, HINT_Q5_GET);
    }

    @Test
    @DisplayName("Q5 dự đoán: join() có ném CompletionException khi stage lỗi không")
    void q05_joinCompletion() {
        CompletableFuture<Integer> failed = failedFuture();
        boolean threwCompletion = false;
        try {
            failed.join();
        } catch (CompletionException ex) {
            threwCompletion = true;
        }
        assertPrediction("Q5_JOIN_THROWS_COMPLETION", threwCompletion,
                Ex01_Compose.Q5_JOIN_THROWS_COMPLETION, HINT_Q5_JOIN);
    }

    private static CompletableFuture<Integer> failedFuture() {
        CompletableFuture<Integer> failed = new CompletableFuture<>();
        failed.completeExceptionally(new IllegalStateException("stage lỗi"));
        return failed;
    }

    private static String checkedNameFromGet(CompletableFuture<Integer> future) {
        try {
            future.get();
        } catch (ExecutionException ex) {
            return ex.getClass().getSimpleName();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return ex.getClass().getSimpleName();
        }
        throw new AssertionError("get() phải ném checked exception khi future đã completeExceptionally.");
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
        String relative = "src/main/java/phase02/d14_completable_future/Ex01_Compose.java";
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
                "Không thấy Ex01_Compose.java từ " + System.getProperty("user.dir"));
    }
}
