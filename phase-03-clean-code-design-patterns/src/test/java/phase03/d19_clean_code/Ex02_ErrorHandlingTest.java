package phase03.d19_clean_code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_ErrorHandlingTest {

    private static final String HINT_Q7 =
            "Dịch lỗi ở lớp biên, nơi lỗi hệ ngoài đi vào, và giữ nguyên cause.";
    private static final String HINT_Q8 =
            "Catch rồi tiếp tục làm lỗi biến mất khỏi luồng điều khiển, trạng thái sai lan sang bước sau.";

    @Test
    @DisplayName("Q7: quote dịch lỗi provider thành lỗi ứng dụng")
    void q07_translatesAtBoundary() {
        assertThrows(Ex02_ErrorHandling.QuoteFailedException.class,
                () -> Ex02_ErrorHandling.quote(currency -> {
                    throw new Ex02_ErrorHandling.RateUnavailableException("timeout");
                }, "USD"));
    }

    @Test
    @DisplayName("Q7: lỗi dịch giữ nguyên cause")
    void q07_preservesCause() {
        Ex02_ErrorHandling.QuoteFailedException failure =
                assertThrows(Ex02_ErrorHandling.QuoteFailedException.class,
                        () -> Ex02_ErrorHandling.quote(currency -> {
                            throw new Ex02_ErrorHandling.RateUnavailableException("timeout");
                        }, "USD"));
        assertInstanceOf(Ex02_ErrorHandling.RateUnavailableException.class, failure.getCause());
        assertEquals("Không lấy được tỉ giá cho USD", failure.getMessage());
        assertEquals(25_400, Ex02_ErrorHandling.quote(currency -> 25_400, "USD"));
    }

    @Test
    @DisplayName("Q8 dự đoán: catch Exception rồi tiếp tục có rủi ro")
    void q08_prediction() {
        assertPrediction("Q8_CATCH_AND_CONTINUE", true,
                Ex02_ErrorHandling.Q8_CATCH_AND_CONTINUE, HINT_Q8);
    }

    @Test
    @DisplayName("Q8: file thiếu thì ném lỗi unchecked giữ cause")
    void q08_missingFileThrowsWithCause(@TempDir Path tempDir) {
        UncheckedIOException failure = assertThrows(UncheckedIOException.class,
                () -> Ex02_ErrorHandling.readFirstLine(tempDir.resolve("missing.txt")));
        assertInstanceOf(java.nio.file.NoSuchFileException.class, failure.getCause());
    }
}
