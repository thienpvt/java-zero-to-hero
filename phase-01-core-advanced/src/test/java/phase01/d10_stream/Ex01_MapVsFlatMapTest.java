package phase01.d10_stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase01.support.Predictions.assertPrediction;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d10_stream.Ex01_MapVsFlatMap.Order;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_MapVsFlatMapTest {

    private static final String HINT_Q1 =
            "map() sinh đúng 1 phần tử kết quả cho mỗi phần tử nguồn; flatMap() làm phẳng "
                    + "từng phần tử nguồn thành nhiều phần tử rồi trộn vào một Stream duy nhất.";

    @Test
    @DisplayName("Q1 dự đoán: map() giữ số phần tử, flatMap() làm phẳng thành nhiều phần tử")
    void q01_prediction() {
        long mapCount = Stream.of(List.of(1, 2), List.of(3)).map(l -> l).count();
        long flatMapCount = Stream.of(List.of(1, 2), List.of(3)).flatMap(List::stream).count();

        assertPrediction("Q1_MAP_COUNT", (int) mapCount, Ex01_MapVsFlatMap.Q1_MAP_COUNT, HINT_Q1);
        assertPrediction("Q1_FLATMAP_COUNT", (int) flatMapCount, Ex01_MapVsFlatMap.Q1_FLATMAP_COUNT, HINT_Q1);
    }

    @Test
    @DisplayName("Q1 itemCounts: map mỗi Order sang số lượng items")
    void q01_itemCounts_mapMoiOrderSangSoLuongItems() {
        List<Order> orders = List.of(
                new Order("o1", List.of("pen", "ink")),
                new Order("o2", List.of("ink", "pad")));

        assertEquals(List.of(2, 2), Ex01_MapVsFlatMap.itemCounts(orders));
    }

    @Test
    @DisplayName("Q1 allItemsDistinctSorted: flatMap toàn bộ items, loại trùng, sắp xếp")
    void q01_allItemsDistinctSorted_flatMapLoaiTrungVaSapXep() {
        List<Order> orders = List.of(
                new Order("o1", List.of("pen", "ink")),
                new Order("o2", List.of("ink", "pad")));

        assertEquals(List.of("ink", "pad", "pen"), Ex01_MapVsFlatMap.allItemsDistinctSorted(orders));
    }
}
