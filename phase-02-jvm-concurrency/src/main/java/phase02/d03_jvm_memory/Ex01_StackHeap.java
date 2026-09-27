package phase02.d03_jvm_memory;

/**
 * JVM Memory — Bài 1: Stack và heap
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 3 (JVM Memory), câu 1–5.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_StackHeapTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 * <p>
 * Q1 [DỰ ĐOÁN] Object Java thường được lưu ở đâu?
 *   Bắt đầu   : điền hằng {@code Q1_OBJECT} (thay null) bằng một giá trị của enum {@code Store}.
 *   Kiểm chứng: chạy q01_objectStore.
 *   Hoàn thành khi: q01_objectStore xanh; ANSWER Q1 nêu vùng nhớ của object và thứ mà
 *               frame của method thực sự giữ.
 * <p>
 * Q2 [DỰ ĐOÁN] Local primitive nằm ở đâu?
 *   Bắt đầu   : điền hằng {@code Q2_LOCAL_PRIMITIVE} (thay null) bằng một giá trị của enum {@code Store}.
 *   Kiểm chứng: chạy q02_localPrimitive.
 *   Hoàn thành khi: q02_localPrimitive xanh; ANSWER Q2 phân biệt primitive cục bộ với object.
 * <p>
 * Q3 [DỰ ĐOÁN] Reference và object có nhất thiết nằm cùng nơi không?
 *   Bắt đầu   : điền hằng {@code Q3_SAME_PLACE} (thay null).
 *   Kiểm chứng: chạy q03_referenceAndObject.
 *   Hoàn thành khi: q03_referenceAndObject xanh; ANSWER Q3 nói reference có thể nằm ở đâu
 *               so với object nó trỏ tới.
 * <p>
 * Q4 [DỰ ĐOÁN] Stack có shared giữa threads không?
 *   Bắt đầu   : điền hằng {@code Q4_STACK_SHARED} (thay null).
 *   Kiểm chứng: chạy q04_stackShared.
 *   Hoàn thành khi: q04_stackShared xanh; ANSWER Q4 nêu mỗi thread sở hữu stack thế nào.
 * <p>
 * Q5 [DỰ ĐOÁN] Heap có shared không?
 *   Bắt đầu   : điền hằng {@code Q5_HEAP_SHARED} (thay null).
 *   Kiểm chứng: chạy q05_heapShared.
 *   Hoàn thành khi: q05_heapShared xanh; ANSWER Q5 nêu hệ quả khi nhiều thread cùng giữ
 *               reference tới một object.
 */
public class Ex01_StackHeap {

    /** Vùng nhớ dùng cho các hằng dự đoán Q1 và Q2. */
    enum Store {
        HEAP, STACK, METASPACE
    }

    // Q1 — object tạo bằng new. Chọn một giá trị của Store.
    static final Store Q1_OBJECT = null;

    // Q2 — biến primitive khai báo trong method. Chọn một giá trị của Store.
    static final Store Q2_LOCAL_PRIMITIVE = null;

    // Q3 — true nếu reference và object bắt buộc nằm cùng một vùng nhớ.
    static final Boolean Q3_SAME_PLACE = null;

    // Q4 — true nếu các thread dùng chung một stack.
    static final Boolean Q4_STACK_SHARED = null;

    // Q5 — true nếu các thread dùng chung heap.
    static final Boolean Q5_HEAP_SHARED = null;
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

/* ANSWER Q4:
 *
 */

/* ANSWER Q5:
 *
 */
