package phase01.d02_arraylist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase01.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex03_PitfallsTest {

    /**
     * Chạy for-each trên một bản sao mutable của {@code source}; mỗi khi gặp phần tử bằng
     * {@code toRemove}, gọi trực tiếp {@code list.remove(...)} (KHÔNG dùng
     * {@code iterator.remove()} — đây chính là cách làm sai đang muốn quan sát).
     *
     * @return simple name của exception bị ném ra, hoặc {@code "none"} nếu không ném gì
     */
    private static String observeForEachRemoveException(List<String> source, String toRemove) {
        List<String> list = new ArrayList<>(source);
        try {
            for (String s : list) {
                if (s.equals(toRemove)) {
                    list.remove(s);
                }
            }
            return "none";
        } catch (RuntimeException e) {
            return e.getClass().getSimpleName();
        }
    }

    @Test
    @DisplayName("Q7 threadSafeList: 4 thread x 10 000 add cho size cuối tất định = 40 000")
    void q07_concurrentAdds_withThreadSafeList_isDeterministic() {
        List<Integer> list = Ex03_Pitfalls.threadSafeList();

        int finalSize = Ex03_Pitfalls.concurrentAdds(list, 4, 10_000);

        assertEquals(40_000, finalSize,
                "Với synchronizedList, 4 thread x 10 000 add phải luôn ra đúng 40 000 phần tử.");
    }

    @Test
    @DisplayName("Q7 thí nghiệm: runExperiment chạy và có báo cáo cho cả hai loại list")
    void q07_experimentRuns() {
        String report = Ex03_Pitfalls.runExperiment(1_000);

        assertTrue(report.contains("ArrayList thường"), "Báo cáo phải có dòng cho ArrayList thường.");
        assertTrue(report.contains("synchronizedList"), "Báo cáo phải có dòng cho synchronizedList.");
    }

    @Test
    @DisplayName("Q8 dự đoán: ArrayList có cho add(null) và giữ đúng size không")
    void q08_duDoan_canAddNull_vaSizeSauHaiLanAddNull() {
        List<String> list = new ArrayList<>();
        boolean threwOnAddNull;
        try {
            list.add(null);
            threwOnAddNull = false;
        } catch (RuntimeException e) {
            threwOnAddNull = true;
        }
        list.add(null);

        assertPrediction("Q8_CAN_ADD_NULL", !threwOnAddNull, Ex03_Pitfalls.Q8_CAN_ADD_NULL,
                "Thử list.add(null) trên java.util.ArrayList thật, xem có exception nào không.");
        assertPrediction("Q8_SIZE_AFTER_TWO_NULLS", list.size(), Ex03_Pitfalls.Q8_SIZE_AFTER_TWO_NULLS,
                "ArrayList không loại trùng, mỗi add(null) đều tăng size lên 1.");
        assertNull(list.get(0), "Phần tử tại vị trí 0 phải là null vì đã add(null).");
    }

    @Test
    @DisplayName("Q9 dự đoán: for-each rồi list.remove() ngay phần tử đầu gặp")
    void q09_duDoan_forEachRemovePhanTuDau() {
        String actual = observeForEachRemoveException(List.of("a", "b", "c"), "a");

        assertPrediction("Q9_FOREACH_REMOVE_FIRST_EXCEPTION", actual,
                Ex03_Pitfalls.Q9_FOREACH_REMOVE_FIRST_EXCEPTION,
                "Đặt breakpoint trong ArrayList$Itr.checkForComodification để xem modCount vs "
                        + "expectedModCount ngay sau khi remove phần tử đầu.");
    }

    @Test
    @DisplayName("Q9 dự đoán: for-each rồi list.remove() phần tử kế cuối có ném lỗi không")
    void q09_duDoan_forEachRemovePhanTuKeCuoi() {
        String actual = observeForEachRemoveException(List.of("a", "b", "c"), "b");
        boolean threw = !"none".equals(actual);

        assertPrediction("Q9_FOREACH_REMOVE_SECOND_LAST_THROWS", threw,
                Ex03_Pitfalls.Q9_FOREACH_REMOVE_SECOND_LAST_THROWS,
                "Sau khi remove phần tử kế cuối, cursor == size mới nên hasNext() trả false — "
                        + "đặt breakpoint trong ArrayList$Itr.hasNext() để kiểm chứng.");
    }

    @Test
    @DisplayName("Q9 removeBlank: xóa tại chỗ phần tử null hoặc rỗng/toàn khoảng trắng")
    void q09_removeBlank_xoaPhanTuNullVaBlank() {
        List<String> list = new ArrayList<>(Arrays.asList("a", "", " ", null, "b"));

        Ex03_Pitfalls.removeBlank(list);

        assertEquals(List.of("a", "b"), list, "Chỉ còn lại các phần tử không null và không blank.");
    }
}
