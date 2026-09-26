package phase01.d10_stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase01.support.Predictions.assertPrediction;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex03_ReduceVsCollectTest {

    @Test
    @DisplayName("Q5 sumWithReduce: tổng danh sách số, rỗng trả 0")
    void q05_sumWithReduce_tongDanhSachSoRongTra0() {
        assertEquals(6, Ex03_ReduceVsCollect.sumWithReduce(List.of(1, 2, 3)));
        assertEquals(0, Ex03_ReduceVsCollect.sumWithReduce(List.of()));
    }

    @Test
    @DisplayName("Q5 maxWithReduce: giá trị lớn nhất, rỗng trả Optional rỗng")
    void q05_maxWithReduce_giaTriLonNhatRongTraOptionalRong() {
        assertEquals(Optional.of(3), Ex03_ReduceVsCollect.maxWithReduce(List.of(1, 2, 3)));
        assertEquals(Optional.empty(), Ex03_ReduceVsCollect.maxWithReduce(List.of()));
    }

    @Test
    @DisplayName("Q6 dự đoán: Stream.toList() bất biến; Collectors.toList() (trên JDK này) còn add() được")
    void q06_prediction() {
        boolean streamToListModifiable;
        try {
            Stream.of(1).toList().add(2);
            streamToListModifiable = true;
        } catch (UnsupportedOperationException e) {
            streamToListModifiable = false;
        }
        boolean collectorsToListAddWorks;
        try {
            Stream.of(1).collect(Collectors.toList()).add(2);
            collectorsToListAddWorks = true;
        } catch (UnsupportedOperationException e) {
            collectorsToListAddWorks = false;
        }

        assertPrediction("Q6_STREAM_TOLIST_IS_MODIFIABLE", streamToListModifiable,
                Ex03_ReduceVsCollect.Q6_STREAM_TOLIST_IS_MODIFIABLE,
                "Ctrl+Q trên Stream.toList() đọc \"unmodifiable List\".");
        assertPrediction("Q6_COLLECTORS_TOLIST_ADD_WORKS_ON_THIS_JDK", collectorsToListAddWorks,
                Ex03_ReduceVsCollect.Q6_COLLECTORS_TOLIST_ADD_WORKS_ON_THIS_JDK,
                "Ctrl+Q trên Collectors.toList() đọc \"no guarantees ... mutability\" — chỉ là "
                        + "chi tiết cài đặt của JDK này, không phải hợp đồng được đảm bảo.");
    }

    @Test
    @DisplayName("Q6 countByFirstLetter: đếm theo chữ đầu, không phân biệt hoa/thường, bỏ chuỗi rỗng")
    void q06_countByFirstLetter_demTheoChuDauKhongPhanBietHoaThuongBoChuoiRong() {
        Map<Character, Long> result =
                Ex03_ReduceVsCollect.countByFirstLetter(List.of("apple", "Avocado", "banana", ""));

        assertEquals(Map.of('a', 2L, 'b', 1L), result);
    }

    @Test
    @DisplayName("Q6 joinWithCollect: nối các phần tử bằng separator")
    void q06_joinWithCollect_noiCacPhanTuBangSeparator() {
        assertEquals("a-b", Ex03_ReduceVsCollect.joinWithCollect(List.of("a", "b"), "-"));
    }

    @Test
    @DisplayName("Q7 squares: bình phương đúng thứ tự, không side effect dù dùng parallelStream")
    void q07_squares_binhPhuongDungThuTuKhongSideEffectDuDungParallelStream() {
        List<Integer> numbers = IntStream.rangeClosed(1, 10_000).boxed().toList();
        List<Integer> expected = numbers.stream().map(n -> n * n).toList();

        assertEquals(expected, Ex03_ReduceVsCollect.squares(numbers));
    }

    @Test
    @DisplayName("Q7 thí nghiệm: runExperiment chạy và trả báo cáo có nội dung")
    void q07_experimentRuns() {
        String report = Ex03_ReduceVsCollect.runExperiment(1_000);

        assertTrue(report.contains("n=1000"), "Báo cáo phải nêu rõ n đã dùng.");
        assertTrue(report.contains("squaresBuggy"), "Báo cáo phải nói về squaresBuggy.");
    }
}
