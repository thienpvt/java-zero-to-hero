package phase01.d10_stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase01.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex04_ParallelStreamsTest {

    @Test
    @DisplayName("Q8 dự đoán: parallelStream() không phải luôn nhanh hơn stream tuần tự")
    void q08_prediction() {
        assertPrediction("Q8_PARALLEL_ALWAYS_FASTER", false, Ex04_ParallelStreams.Q8_PARALLEL_ALWAYS_FASTER,
                "Chạy main() và so các dòng tuần tự/song song trong báo cáo runExperiment.");
    }

    @Test
    @DisplayName("Q8 thí nghiệm: runExperiment chạy và trả báo cáo có đủ 3 phần (a)(b)(c)")
    void q08_experimentRuns() {
        String report = Ex04_ParallelStreams.runExperiment(10_000);

        assertTrue(report.contains("(a)"), "Báo cáo phải có phần (a) LongStream.");
        assertTrue(report.contains("(b)"), "Báo cáo phải có phần (b) List<Integer> boxed.");
        assertTrue(report.contains("(c)"), "Báo cáo phải có phần (c) I/O giả.");
    }

    @Test
    @DisplayName("Q9 dự đoán: parallel stream chạy trên thread của ForkJoinPool.commonPool()")
    void q09_prediction() {
        assertPrediction("Q9_COMMON_POOL_THREAD_NAME_PREFIX",
                "ForkJoinPool.commonPool-worker-", Ex04_ParallelStreams.Q9_COMMON_POOL_THREAD_NAME_PREFIX,
                "Chạy main() và đọc các dòng \"Thread: ForkJoinPool.commonPool-worker-...\" được in ra.");
    }
}
