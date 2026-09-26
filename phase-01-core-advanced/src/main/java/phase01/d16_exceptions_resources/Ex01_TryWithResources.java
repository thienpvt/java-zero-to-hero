package phase01.d16_exceptions_resources;

import java.util.List;

/**
 * Exception handling và quản lý tài nguyên — Bài 1: try-with-resources và thứ tự đóng
 *
 * Nguồn: 01-java-core-advanced.md, mục 16 (Exception handling và quản lý tài nguyên), câu 1–2.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_TryWithResourcesTest bằng nút ▶
 * cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q1 [DỰ ĐOÁN + CODE] `try-with-resources` đóng tài nguyên theo thứ tự nào khi khai báo nhiều tài nguyên?
 *   Bắt đầu   : đọc class TrackedResource bên dưới; thử viết `try (var a = new TrackedResource("A", log,
 *               false); var b = new TrackedResource("B", log, false)) {}` trong Alt+F8 (Evaluate Expression)
 *               khi debug, rồi điền hằng số Q1_CLOSE_ORDER (thay null).
 *   Kiểm chứng: chạy q01_closeOrderPrediction; đặt breakpoint trong TrackedResource.close() (Ctrl+N →
 *               gõ "Ex01_TryWithResources" → mở, Ctrl+F12 để thấy method close()), Debug test, sau khi
 *               đã điền dự đoán, ghi lại tên resource mỗi lần breakpoint dừng (F9 để sang lần kế).
 *   Code      : cài đặt closeAll(List<? extends AutoCloseable>) — đóng các resource theo thứ tự
 *               ngược lại so với danh sách truyền vào, đóng hết mọi resource dù có lỗi xảy ra; lỗi
 *               đầu tiên gặp phải được ném ra, các lỗi đóng sau đó được gắn vào bằng addSuppressed().
 *   Hoàn thành khi: mọi test q01_* xanh; giải thích được vì sao try-with-resources đóng resource khai
 *               báo sau cùng trước tiên (giống cách pop phần tử ra khỏi một ngăn xếp/stack).
 *
 * Q2 [DỰ ĐOÁN] Nếu thao tác chính và `close()` cùng ném exception thì kiểm tra thông tin lỗi ở đâu?
 *   Bắt đầu   : đọc q02_primaryAndSuppressed để thấy thân try ném exception gì và resource nào
 *               có failOnClose; điền Q2_PRIMARY_MESSAGE và Q2_SUPPRESSED_COUNT (thay null) dựa trên
 *               exception bắt được ở catch — sau khi đã tự debug, đừng chép message trước.
 *   Kiểm chứng: đặt breakpoint ngay tại dòng catch trong test, Debug q02_primaryAndSuppressed, dùng
 *               Alt+F8 (Evaluate Expression) gọi caught.getMessage() và caught.getSuppressed() để xem
 *               trực tiếp mảng suppressed exception.
 *   Code      : không có (chỉ đọc, không cài đặt thêm cho câu này).
 *   Hoàn thành khi: test q02_primaryAndSuppressed xanh; giải thích được exception của thân try là lỗi
 *               "chính" (lấy bằng getMessage()/catch), còn exception ném ra từ close() không bị mất mà
 *               nằm trong Throwable.getSuppressed() của exception chính.
 */
public class Ex01_TryWithResources {

    /** Resource giả để quan sát thứ tự và cách gộp lỗi khi đóng bằng try-with-resources. */
    static final class TrackedResource implements AutoCloseable {
        private final String name;
        private final List<String> log;
        private final boolean failOnClose;

        TrackedResource(String name, List<String> log, boolean failOnClose) {
            this.name = name;
            this.log = log;
            this.failOnClose = failOnClose;
        }

        /**
         * Ghi {@code name} vào {@code log} rồi, nếu {@code failOnClose} là true, ném
         * {@code IllegalStateException("close " + name)}.
         */
        @Override
        public void close() {
            log.add(name);
            if (failOnClose) {
                throw new IllegalStateException("close " + name);
            }
        }
    }

    // Q1 — kịch bản: try (A; B) {} không lỗi, rồi log được nối bằng ","
    static final String Q1_CLOSE_ORDER = null;

    // Q2 — kịch bản: thân try ném exception, đồng thời resource A có failOnClose.
    // Ghi message của exception bắt được ở catch và số phần tử getSuppressed().
    static final String Q2_PRIMARY_MESSAGE = null;
    static final Integer Q2_SUPPRESSED_COUNT = null;

    /**
     * Đóng tất cả {@code resources} theo thứ tự ngược lại so với danh sách truyền vào, đóng hết dù
     * có resource ném lỗi khi đóng.
     *
     * @param resources danh sách resource cần đóng; có thể rỗng
     * @throws Exception lỗi đầu tiên gặp phải khi đóng (theo thứ tự ngược); mọi lỗi đóng gặp phải
     *         sau đó được gắn vào exception này bằng {@link Throwable#addSuppressed(Throwable)}
     */
    static void closeAll(List<? extends AutoCloseable> resources) throws Exception {
        throw new UnsupportedOperationException("TODO Q1");
    }
}
