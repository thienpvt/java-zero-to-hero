package phase01.d02_arraylist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase01.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d02_arraylist.Ex01_CapacityAndResize.MiniArrayList;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_CapacityAndResizeTest {

    @Test
    @DisplayName("Q1 get() ngoài biên ném IndexOutOfBoundsException")
    void q01_get_ngoaiBien_nemIndexOutOfBoundsException() {
        MiniArrayList<String> list = new MiniArrayList<>();
        list.add("a");
        list.add("b");

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1),
                "get(-1) phải ném IndexOutOfBoundsException.");
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(list.size()),
                "get(size) phải ném IndexOutOfBoundsException vì index hợp lệ là [0, size).");
    }

    @Test
    @DisplayName("Q1 get() trả về đúng phần tử theo chỉ số")
    void q01_get_traVePhanTuDungViTri() {
        MiniArrayList<String> list = new MiniArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        assertEquals("a", list.get(0), "get(0) phải trả về phần tử đầu tiên đã add.");
        assertEquals("c", list.get(2), "get(2) phải trả về phần tử thứ ba đã add.");
    }

    @Test
    @DisplayName("Q2 add() nối vào cuối và tự resize khi đầy")
    void q02_add_vuotCapacity_thiGoiGrow() {
        MiniArrayList<Integer> list = new MiniArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        assertEquals(10, list.capacity(), "Capacity mặc định là 10, chưa cần resize sau 10 lần add.");
        assertEquals(0, list.resizeCount(), "Chưa resize lần nào sau 10 lần add.");

        list.add(10);
        assertEquals(15, list.capacity(), "Sau lần add thứ 11, capacity phải tăng lên 15.");
        assertEquals(1, list.resizeCount(), "Phải có đúng 1 lần resize sau lần add thứ 11.");
        assertEquals(11, list.size(), "size phải là 11 sau 11 lần add.");
        assertEquals(0, list.get(0), "Phần tử cũ phải còn đúng vị trí sau khi resize.");
        assertEquals(10, list.get(10), "Phần tử mới add phải nằm ở vị trí cuối.");
    }

    @Test
    @DisplayName("Q2 add(index, element) chèn và dời phần tử sang phải")
    void q02_addTaiViTri_domPhanTuSangPhai() {
        MiniArrayList<String> list = new MiniArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        list.add(1, "X");

        assertEquals(4, list.size(), "size phải tăng lên 4 sau khi chèn thêm 1 phần tử.");
        assertEquals("a", list.get(0));
        assertEquals("X", list.get(1), "Phần tử mới phải nằm đúng vị trí chèn.");
        assertEquals("b", list.get(2), "Phần tử cũ tại vị trí 1 phải bị dời sang vị trí 2.");
        assertEquals("c", list.get(3), "Phần tử cũ tại vị trí 2 phải bị dời sang vị trí 3.");

        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, "y"),
                "add(index, e) với index âm phải ném IndexOutOfBoundsException.");
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(list.size() + 1, "y"),
                "add(index, e) với index > size phải ném IndexOutOfBoundsException.");
    }

    @Test
    @DisplayName("Q2 remove(index) dời phần tử sang trái và trả về phần tử bị xóa")
    void q02_remove_domPhanTuSangTraiVaTraVePhanTuBiXoa() {
        MiniArrayList<String> list = new MiniArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        String removed = list.remove(1);

        assertEquals("b", removed, "remove(1) phải trả về đúng phần tử bị xóa.");
        assertEquals(2, list.size(), "size phải giảm 1 sau khi remove.");
        assertEquals("a", list.get(0));
        assertEquals("c", list.get(1), "Phần tử cuối phải được dời về vị trí 1 sau khi xóa vị trí 1.");
    }

    @Test
    @DisplayName("Q3 grow() tăng capacity đúng theo chuỗi công thức old + old/2")
    void q03_grow_chuoiCapacityDungCongThuc() {
        MiniArrayList<Integer> list = new MiniArrayList<>();
        List<Integer> capacitiesWhenChanged = new ArrayList<>();
        int previousCapacity = list.capacity();

        for (int i = 0; i < 100; i++) {
            list.add(i);
            if (list.capacity() != previousCapacity) {
                capacitiesWhenChanged.add(list.capacity());
                previousCapacity = list.capacity();
            }
        }

        assertEquals(List.of(15, 22, 33, 49, 73, 109), capacitiesWhenChanged,
                "Chuỗi capacity sau mỗi lần resize phải theo công thức max(old + old/2, old + 1), "
                        + "bắt đầu từ capacity mặc định 10.");
    }

    @Test
    @DisplayName("Q3 dự đoán: số lần resize sau 100 lần add với capacity mặc định")
    void q03_duDoan_soLanResizeSau100LanAdd() {
        MiniArrayList<Integer> list = new MiniArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }

        assertPrediction("Q3_RESIZES_FOR_100_ADDS", list.resizeCount(),
                Ex01_CapacityAndResize.Q3_RESIZES_FOR_100_ADDS,
                "Đếm số lần capacity đổi khi add 100 phần tử vào MiniArrayList() (capacity mặc định 10).");
    }

    @Test
    @DisplayName("Q4 dự đoán: size và capacity sau 11 lần add")
    void q04_duDoan_sizeVaCapacitySau11LanAdd() {
        MiniArrayList<Integer> list = new MiniArrayList<>();
        for (int i = 0; i < 11; i++) {
            list.add(i);
        }

        assertPrediction("Q4_SIZE_AFTER_11_ADDS", list.size(),
                Ex01_CapacityAndResize.Q4_SIZE_AFTER_11_ADDS,
                "size() là số phần tử thực có, luôn đúng bằng số lần add thành công.");
        assertPrediction("Q4_CAPACITY_AFTER_11_ADDS", list.capacity(),
                Ex01_CapacityAndResize.Q4_CAPACITY_AFTER_11_ADDS,
                "capacity() là độ dài mảng nền; xem lại công thức grow() ở Q3.");
    }

    @Test
    @DisplayName("Q6 dự đoán: số lần resize khi truyền initialCapacity đủ lớn")
    void q06_duDoan_soLanResizeVoiInitialCapacity100() {
        MiniArrayList<Integer> list = new MiniArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }

        assertPrediction("Q6_RESIZES_WITH_INITIAL_CAPACITY_100", list.resizeCount(),
                Ex01_CapacityAndResize.Q6_RESIZES_WITH_INITIAL_CAPACITY_100,
                "So với q03_duDoan_soLanResizeSau100LanAdd: capacity đã đủ lớn từ đầu thì có còn resize không?");
    }

    @Test
    @DisplayName("Q6 MiniArrayList(0) vẫn add được, capacity tăng lên 1 sau lần add đầu tiên")
    void q06_capacityBanDauBangKhong_addMotPhanTu_capacityBangMot() {
        MiniArrayList<String> list = new MiniArrayList<>(0);

        list.add("a");

        assertEquals(1, list.capacity(), "capacity ban đầu 0, sau khi add 1 phần tử phải resize lên 1.");
        assertEquals(1, list.size());
        assertEquals("a", list.get(0));
    }
}
