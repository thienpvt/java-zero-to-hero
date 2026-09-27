package phase02.d03_jvm_memory;

/**
 * JVM Memory — Bài 2: Lỗi bộ nhớ và Metaspace
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 3 (JVM Memory), câu 6–9.
 * Cần làm trước: Ex01_StackHeap.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_ErrorsAndMetaspaceTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 * <p>
 * Q6 [DỰ ĐOÁN + CODE] StackOverflowError xảy ra khi nào?
 *   Bắt đầu   : cài đặt {@code recurse()} và {@code recurse(int)} để gọi đệ quy không dừng,
 *               mỗi lần truyền thêm một {@code int}. Không gọi hai method này từ khối static.
 *   Kiểm chứng: chạy q06_recurseThrowsStackOverflowError. Muốn thấy frame chồng lên nhau:
 *               Ctrl+F12 trong class này, đặt breakpoint ở {@code recurse(int)}, Debug test,
 *               bấm F7 vài lần rồi dừng (đừng bước đến khi tràn). Ctrl+N, gõ
 *               StackOverflowError, Ctrl+Q để đọc mô tả của JDK.
 *   Code      : {@code recurse()} gọi {@code recurse(0)}; {@code recurse(int)} gọi lại chính nó
 *               với đối số lớn hơn một.
 *   Hoàn thành khi: q06_recurseThrowsStackOverflowError xanh; ANSWER Q6 nêu điều kiện
 *               stack của một thread bị tràn.
 * <p>
 * Q7 [TỰ TRẢ LỜI] OutOfMemoryError có những nguyên nhân nào?
 *   Bắt đầu   : Ctrl+N, gõ OutOfMemoryError, Ctrl+Q đọc Javadoc, rồi Ctrl+F12 xem constructor.
 *               Chú ý các thông báo heap, metaspace, native thread và direct buffer.
 *   Kiểm chứng: không có test riêng; đối chiếu ANSWER Q7 với Javadoc vừa mở.
 *   Hoàn thành khi: ANSWER Q7 nêu ít nhất bốn nguyên nhân khác nhau và không gom mọi
 *               OutOfMemoryError về một mình heap đầy.
 * <p>
 * Q8 [DỰ ĐOÁN] Metaspace chứa gì?
 *   Bắt đầu   : điền hằng {@code Q8_METASPACE_HOLDS} (thay null) bằng một giá trị của
 *               enum {@code MetaspaceHolds}.
 *   Kiểm chứng: chạy q08_metaspaceHolds.
 *   Hoàn thành khi: q08_metaspaceHolds xanh; ANSWER Q8 phân biệt metadata của class
 *               với instance nằm trên heap.
 * <p>
 * Q9 [TỰ TRẢ LỜI + DỰ ĐOÁN] Memory leak có thể xảy ra trong Java dù có GC không?
 *   Bắt đầu   : điền hằng {@code Q9_LEAK_POSSIBLE_WITH_GC} (thay null), rồi viết ANSWER Q9.
 *   Kiểm chứng: chạy q09_leakPossibleWithGc.
 *   Hoàn thành khi: q09_leakPossibleWithGc xanh; ANSWER Q9 giải thích GC thu object theo
 *               reachability và nêu một ví dụ object vẫn bị giữ nên không được thu.
 */
public class Ex02_ErrorsAndMetaspace {

    /** Nội dung có thể gán cho Metaspace khi trả lời Q8. */
    enum MetaspaceHolds {
        CLASS_METADATA,
        LIVE_OBJECTS,
        THREAD_FRAMES
    }

    /**
     * Đệ quy không có điểm dừng. Mỗi frame nhận thêm một {@code int}.
     */
    static void recurse() {
        throw new UnsupportedOperationException("TODO Q6");
    }

    private static void recurse(int depth) {
        throw new UnsupportedOperationException("TODO Q6");
    }

    // Q8 — chọn một giá trị của MetaspaceHolds.
    static final MetaspaceHolds Q8_METASPACE_HOLDS = null;

    // Q9 — true nếu GC vẫn để lọt memory leak.
    static final Boolean Q9_LEAK_POSSIBLE_WITH_GC = null;
}

/* ANSWER Q6:
 *
 */

/* ANSWER Q7:
 *
 */

/* ANSWER Q8:
 *
 */

/* ANSWER Q9:
 *
 */
