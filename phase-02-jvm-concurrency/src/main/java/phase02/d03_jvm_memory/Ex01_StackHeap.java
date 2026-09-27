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
    static final Store Q1_OBJECT = Store.HEAP; // SOLUTION-VALUE

    // Q2 — biến primitive khai báo trong method. Chọn một giá trị của Store.
    static final Store Q2_LOCAL_PRIMITIVE = Store.STACK; // SOLUTION-VALUE

    // Q3 — true nếu reference và object bắt buộc nằm cùng một vùng nhớ.
    static final Boolean Q3_SAME_PLACE = false; // SOLUTION-VALUE

    // Q4 — true nếu các thread dùng chung một stack.
    static final Boolean Q4_STACK_SHARED = false; // SOLUTION-VALUE

    // Q5 — true nếu các thread dùng chung heap.
    static final Boolean Q5_HEAP_SHARED = true; // SOLUTION-VALUE
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Object tạo bằng new thường nằm trên heap.
 * Frame stack chỉ giữ biến local, tham số và reference trỏ tới object đó.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Local primitive (int, long, boolean, ...) nằm trong frame stack của method.
 * Nó không được cấp phát thành một object trên heap.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Không. Reference có thể nằm trong frame stack (biến local) hoặc trong field của object khác trên heap.
 * Object mà reference trỏ tới vẫn thường nằm trên heap, nên hai thứ không bắt buộc cùng một nơi.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Không. Mỗi thread có stack riêng cho các frame của nó.
 * Thread khác không đọc biến local trên stack này.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Có. Heap là vùng dùng chung: nhiều thread có thể giữ reference tới cùng một object.
 * Vì vậy phải đồng bộ khi các thread cùng sửa trạng thái của object đó. GC cũng chạy trên heap.
 * SOLUTION-END
 */
