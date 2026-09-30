package phase03.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/** So sánh dự đoán của ứng viên với giá trị thực tế, kèm gợi ý khi sai. */
public final class Predictions {

    private Predictions() {
    }

    public static <T> void assertPrediction(String constantName, T actual, T predicted, String hint) {
        assertNotNull(predicted, () -> "Chưa điền dự đoán " + constantName
                + ": thay null bằng giá trị bạn nghĩ là đúng.");
        assertEquals(actual, predicted, () -> constantName + ": bạn dự đoán " + predicted
                + " nhưng thực tế là " + actual + ". Gợi ý: " + hint);
    }
}
