package phase01.d02_arraylist;

import java.util.Arrays;

/**
 * ArrayList — Bài 1: Capacity, size và cơ chế resize
 *
 * Nguồn: 01-java-core-advanced.md, mục 2 (ArrayList), câu 2, 1, 3, 4, 6.
 * Cần làm trước: không (Q1 và Q2 đi cặp trong file này).
 * Cách làm: Q1 và Q2 đi cặp — cài đặt các phần TODO của cả hai câu rồi mới chạy test q01_*
 * và q02_*; các câu khác làm lần lượt theo thứ tự Javadoc. Chạy test trong
 * Ex01_CapacityAndResizeTest bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh
 * mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q2 [CODE + TỰ TRẢ LỜI] Tại sao {@code add()} thường được gọi là amortized O(1)?
 *   Bắt đầu   : cùng Q1, cài đặt add(E), add(int, E), remove(int) và get(int) (TODO của
 *               cả Q1 lẫn Q2) trước khi chạy test q01_* hoặc q02_*.
 *   Kiểm chứng: đặt breakpoint trong add(E) của bạn, Ctrl+N → ArrayList → Ctrl+F12 → add,
 *               so với cách JDK thật làm; chạy q02_*, sau khi đã cài đặt, tự xem những lần
 *               add nào gọi grow().
 *   Code      : add(E) nối vào cuối, chỉ gọi grow() khi mảng đầy; add(int, E) dùng
 *               System.arraycopy để dời phần tử từ index về phải trước khi chèn;
 *               remove(int) dời phần tử phía sau về trái rồi trả phần tử bị xóa.
 *   Hoàn thành khi: đã cài xong TODO Q1 và Q2, các test q02_* xanh; giải thích được vì sao
 *               chi phí trung bình trên nhiều lần add vẫn là O(1) dù có vài lần grow() tốn O(n).
 * <p>
 * Q1 [CODE + TỰ TRẢ LỜI] Tại sao {@code ArrayList.get()} là O(1)?
 *   Bắt đầu   : cùng Q2 (xem mục Q2), cài đặt get(int index) bằng cách ép kiểu trực tiếp
 *               (E) elementData[index]; chỉ chạy q01_* sau khi cả get và add đã xong.
 *   Kiểm chứng: đặt breakpoint ngay trong get(), Debug q01_get_traVePhanTuDungViTri
 *               (Ctrl+Shift+F10), F8 qua từng bước, sau khi đã cài đặt, tự đếm số phép
 *               truy cập mảng và số vòng lặp.
 *   Code      : E get(int index) — kiểm tra biên [0, size) trước, ngoài phạm vi ném
 *               IndexOutOfBoundsException.
 *   Hoàn thành khi: các test q01_* xanh; giải thích được vì sao truy cập mảng theo chỉ số
 *               luôn là một phép tính địa chỉ hằng số, không phụ thuộc số phần tử.
 * <p>
 * Q3 [DỰ ĐOÁN + CODE + TỰ TRẢ LỜI] Điều gì xảy ra khi backing array hết capacity?
 *   Bắt đầu   : điền hằng số Q3_RESIZES_FOR_100_ADDS (đọc thêm Q6 bên dưới), rồi cài
 *               đặt grow().
 *   Kiểm chứng: Ctrl+N → ArrayList → Ctrl+F12 → grow, đặt breakpoint ở dòng tính
 *               capacity mới, Debug q03_grow_chuoiCapacityDungCongThuc, F8 qua từng lần
 *               grow để so công thức oldCapacity + (oldCapacity &gt;&gt; 1).
 *   Code      : grow() cấp mảng mới bằng Arrays.copyOf với capacity mới =
 *               max(old + (old &gt;&gt; 1), old + 1); tăng resizeCount mỗi lần gọi.
 *   Hoàn thành khi: các test q03_* xanh; giải thích được vì sao tăng capacity theo tỉ lệ
 *               (không phải +1 mỗi lần) giữ chi phí add() là amortized O(1).
 * <p>
 * Q4 [DỰ ĐOÁN] {@code size} và {@code capacity} khác nhau thế nào?
 *   Bắt đầu   : thêm 11 phần tử vào một MiniArrayList() mới, điền Q4_SIZE_AFTER_11_ADDS
 *               và Q4_CAPACITY_AFTER_11_ADDS.
 *   Kiểm chứng: chạy q04_duDoan_sizeVaCapacitySau11LanAdd; nếu sai, Alt+F8 Evaluate
 *               Expression list.size() và list.capacity() ngay sau lần add thứ 11.
 *   Hoàn thành khi: test q04_* xanh; giải thích được size là số phần tử thực có,
 *               capacity là độ dài mảng nền, hai giá trị không nhất thiết bằng nhau.
 * <p>
 * Q6 [DỰ ĐOÁN + TỰ TRẢ LỜI] Khi nào nên truyền {@code initialCapacity}?
 *   Bắt đầu   : đọc lại Q3, điền Q6_RESIZES_WITH_INITIAL_CAPACITY_100.
 *   Kiểm chứng: chạy q06_duDoan_soLanResizeVoiInitialCapacity100, so với
 *               q03_duDoan_soLanResizeSau100LanAdd để thấy khác biệt khi biết trước
 *               kích thước.
 *   Hoàn thành khi: test q06_* xanh; giải thích được khi biết trước (hoặc ước lượng
 *               được) số phần tử tối đa nên truyền initialCapacity để tránh resize thừa.
 */
public class Ex01_CapacityAndResize {

    /** Bản rút gọn của ArrayList dùng để quan sát capacity/size/resizeCount. */
    static final class MiniArrayList<E> {

        private Object[] elementData;
        private int size;
        private int resizeCount;

        MiniArrayList() {
            this(10);
        }

        MiniArrayList(int initialCapacity) {
            if (initialCapacity < 0) {
                throw new IllegalArgumentException("initialCapacity phải >= 0, nhận: " + initialCapacity);
            }
            this.elementData = new Object[initialCapacity];
        }

        int size() {
            return size;
        }

        int capacity() {
            return elementData.length;
        }

        int resizeCount() {
            return resizeCount;
        }

        /**
         * Trả về phần tử tại {@code index}.
         *
         * @throws IndexOutOfBoundsException nếu {@code index} ngoài phạm vi {@code [0, size)}
         */
        @SuppressWarnings("unchecked")
        E get(int index) {
            throw new UnsupportedOperationException("TODO Q1");
        }

        /**
         * Thêm {@code element} vào cuối danh sách; tự động {@link #grow()} nếu mảng đã đầy.
         */
        void add(E element) {
            throw new UnsupportedOperationException("TODO Q2");
        }

        /**
         * Tăng capacity của {@code elementData}: capacity mới =
         * {@code max(old + (old >> 1), old + 1)}. Tăng {@code resizeCount} mỗi lần gọi.
         */
        private void grow() {
            throw new UnsupportedOperationException("TODO Q3");
        }

        /**
         * Chèn {@code element} tại {@code index}, dời các phần tử từ {@code index} về sau
         * sang phải một vị trí.
         *
         * @throws IndexOutOfBoundsException nếu {@code index} ngoài phạm vi {@code [0, size]}
         */
        void add(int index, E element) {
            throw new UnsupportedOperationException("TODO Q2");
        }

        /**
         * Xóa và trả về phần tử tại {@code index}, dời các phần tử phía sau về trái một vị trí.
         *
         * @throws IndexOutOfBoundsException nếu {@code index} ngoài phạm vi {@code [0, size)}
         */
        @SuppressWarnings("unchecked")
        E remove(int index) {
            throw new UnsupportedOperationException("TODO Q2");
        }
    }

    // Q3 — kịch bản: MiniArrayList() (capacity 10 mặc định), add 100 phần tử liên tiếp.
    // Đếm số lần grow() (resizeCount), không chép công thức capacity vào hằng số.
    static final Integer Q3_RESIZES_FOR_100_ADDS = null;

    // Q4 — kịch bản: MiniArrayList() (capacity 10 mặc định), add 11 phần tử liên tiếp.
    static final Integer Q4_SIZE_AFTER_11_ADDS = null;
    static final Integer Q4_CAPACITY_AFTER_11_ADDS = null;

    // Q6 — kịch bản: MiniArrayList(100) (biết trước kích thước), add 100 phần tử liên tiếp.
    static final Integer Q6_RESIZES_WITH_INITIAL_CAPACITY_100 = null;
}

/* ANSWER Q1:
 *
 */

/* ANSWER Q2:
 *
 */

/* ANSWER Q3:
 *
 */

/* ANSWER Q6:
 *
 */
