package phase01.d10_stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase01.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d10_stream.Ex02_LazinessAndPipeline.User;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_LazinessAndPipelineTest {

    @Test
    @DisplayName("Q2 dự đoán: pipeline filter+map chưa có terminal operation thì chưa chạy gì")
    void q02_prediction() {
        List<String> log = new ArrayList<>();

        Stream.of(1, 2, 3)
                .filter(n -> {
                    log.add("filter " + n);
                    return n % 2 == 0;
                })
                .map(n -> {
                    log.add("map " + n);
                    return n * n;
                });
        // Cố ý KHÔNG gọi bất kỳ terminal operation nào (không .toList(), .forEach(),
        // .count()...) — đây chính là điều muốn quan sát.

        assertPrediction("Q2_LOG_SIZE_WITHOUT_TERMINAL", log.size(),
                Ex02_LazinessAndPipeline.Q2_LOG_SIZE_WITHOUT_TERMINAL,
                "Không có terminal operation nào để \"kéo\" phần tử qua pipeline nên các "
                        + "lambda của filter/map không bao giờ được gọi.");
    }

    @Test
    @DisplayName("Q4 dự đoán: filter() và map() xen kẽ theo từng phần tử, dừng ngay khi findFirst() thấy kết quả")
    void q04_prediction() {
        List<String> log = new ArrayList<>();

        Stream.of("a", "bb", "ccc")
                .filter(s -> {
                    log.add("filter " + s);
                    return s.length() > 1;
                })
                .map(s -> {
                    log.add("map " + s);
                    return s;
                })
                .findFirst();

        assertPrediction("Q4_LOG_WITH_FIND_FIRST", String.join(", ", log),
                Ex02_LazinessAndPipeline.Q4_LOG_WITH_FIND_FIRST,
                "Đặt breakpoint trong lambda của filter, Debug, F8 nhiều lần để thấy \"a\" bị "
                        + "loại (không tới map) còn \"bb\" qua filter rồi tới map, sau đó "
                        + "findFirst() dừng ngay (không có \"filter ccc\").");
    }

    @Test
    @DisplayName("Q3 dự đoán: gọi terminal operation lần hai trên cùng Stream sẽ ném lỗi")
    void q03_prediction() {
        Stream<Integer> stream = Stream.of(1, 2, 3);
        stream.count();

        Exception thrown = assertThrows(Exception.class, stream::count);

        assertPrediction("Q3_REUSE_EXCEPTION", thrown.getClass().getSimpleName(),
                Ex02_LazinessAndPipeline.Q3_REUSE_EXCEPTION,
                "Đọc message của exception (\"stream has already been operated upon or "
                        + "closed\") — Ctrl+N → AbstractPipeline để xem nơi nó được ném ra.");
    }

    @Test
    @DisplayName("Q11 dự đoán: filter trước map giúp map chạy ít lần hơn map trước filter")
    void q11_prediction() {
        List<Integer> numbers = IntStream.rangeClosed(1, 10).boxed().toList();

        int filterFirstCalls = Ex02_LazinessAndPipeline.mapCallsWhenFilterFirst(numbers);
        int mapFirstCalls = Ex02_LazinessAndPipeline.mapCallsWhenMapFirst(numbers);

        assertPrediction("Q11_MAP_CALLS_WHEN_FILTER_FIRST", filterFirstCalls,
                Ex02_LazinessAndPipeline.Q11_MAP_CALLS_WHEN_FILTER_FIRST,
                "Chỉ 2 trong 10 số 1..10 chia hết cho 5, nên khi filter đứng trước, map chỉ "
                        + "chạy trên 2 phần tử còn lại.");
        assertPrediction("Q11_MAP_CALLS_WHEN_MAP_FIRST", mapFirstCalls,
                Ex02_LazinessAndPipeline.Q11_MAP_CALLS_WHEN_MAP_FIRST,
                "Khi map đứng trước filter, map phải chạy trên toàn bộ 10 phần tử trước khi "
                        + "filter kịp loại bớt.");
    }

    @Test
    @DisplayName("Q11 code: thứ tự filter/map ảnh hưởng số lần map được gọi")
    void q11_mapCallCountsMatchPipelineOrder() {
        List<Integer> numbers = IntStream.rangeClosed(1, 10).boxed().toList();

        assertEquals(2, Ex02_LazinessAndPipeline.mapCallsWhenFilterFirst(numbers),
                "Filter n % 5 == 0 đứng trước thì map chỉ gặp 5 và 10.");
        assertEquals(10, Ex02_LazinessAndPipeline.mapCallsWhenMapFirst(numbers),
                "Map đứng trước thì phải chạy trên cả 10 số trước khi filter loại bớt.");
    }

    @Test
    @DisplayName("Ví dụ activeUniqueEmails: chỉ email của user active, loại trùng, giữ thứ tự gặp đầu")
    void vd_activeUniqueEmails_locUserActiveVaLoaiTrungGiuThuTuGapDau() {
        List<User> users = List.of(
                new User("a@x.com", true),
                new User("b@x.com", false),
                new User("a@x.com", true),
                new User("c@x.com", true));

        List<String> result = Ex02_LazinessAndPipeline.activeUniqueEmails(users);

        assertEquals(List.of("a@x.com", "c@x.com"), result);
    }
}
