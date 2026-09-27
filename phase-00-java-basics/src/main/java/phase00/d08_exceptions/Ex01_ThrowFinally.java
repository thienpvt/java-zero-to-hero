package phase00.d08_exceptions;

import java.util.ArrayList;
import java.util.List;
import phase00.support.Compiles;

/**
 * Exception — Bài 1: throw, finally và try-with-resources
 *
 * Nguồn: 00-java-basics-review.md, mục 8 (Exception và quản lý tài nguyên), câu 2, 3, 4.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_ThrowFinallyTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q2 [DỰ ĐOÁN] {@code throws} và {@code throw} khác nhau ra sao?
 *   Bắt đầu   : điền Q2_THROW_SENDS_EXCEPTION và Q2_CHECKED_WITHOUT_DECLARE_COMPILES.
 *               Mẫu checked exception nằm trong comment của {@code checkedSample}.
 *   Kiểm chứng: Debug q02_prediction, F7 vào {@code raise()}. Bỏ comment thân {@code checkedSample},
 *               xem lỗi đỏ, comment lại. Ctrl+Q trên {@code throws} trong một method JDK có {@code throws IOException}
 *               (Ctrl+N → {@code Reader}, Ctrl+F12 → {@code read}).
 *   Hoàn thành khi: q02_prediction xanh; nói được throw bắn exception, throws khai báo trên chữ ký.
 * <p>
 * Q3 [DỰ ĐOÁN] {@code finally} và {@code try-with-resources} giải quyết những vấn đề gì?
 *   Bắt đầu   : đọc {@code finallyRunsAfterThrow} và {@code useDoor}. Điền hai hằng Q3_*.
 *   Kiểm chứng: Debug q03_prediction. Breakpoint trong khối {@code finally} và trong {@code Door.close}.
 *               Ctrl+Q trên {@code AutoCloseable}.
 *   Hoàn thành khi: q03_prediction xanh; nói được finally vẫn chạy khi thân ném lỗi, try-with-resources gọi close.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Vì sao {@code catch (Exception e) { return null; }} thường làm khó tìm lỗi?
 *   Bắt đầu   : viết ANSWER Q4 sau khi đã thấy Q2 và Q3.
 *   Tra cứu   : Ctrl+Q trên {@code Throwable.printStackTrace} và trên {@code getCause}.
 *   Hoàn thành khi: ANSWER Q4 nêu caller mất những thông tin gì.
 */
public class Ex01_ThrowFinally {

    static void raise() {
        throw new IllegalStateException("boom");
    }

    static boolean catchRaise() {
        try {
            raise();
            return false;
        } catch (IllegalStateException ex) {
            return true;
        }
    }

    // static void checkedSample() {
    //     throw new java.io.IOException("x");
    // }

    // Q2 — catchRaise() có nhận được exception từ throw không?
    static final Boolean Q2_THROW_SENDS_EXCEPTION = true; // SOLUTION-VALUE

    // Q2 — throw new IOException trong method không khai báo throws có biên dịch không?
    static final Compiles Q2_CHECKED_WITHOUT_DECLARE_COMPILES = Compiles.NO; // SOLUTION-VALUE

    static boolean finallyRunsAfterThrow() {
        boolean[] ran = {false};
        try {
            throw new IllegalStateException("boom");
        } catch (IllegalStateException ex) {
            // nuốt để quan sát finally, không return ở đây
        } finally {
            ran[0] = true;
        }
        return ran[0];
    }

    // Q3 — finallyRunsAfterThrow()
    static final Boolean Q3_FINALLY_RUNS_WHEN_BODY_THROWS = true; // SOLUTION-VALUE

    static final class Door implements AutoCloseable {
        final List<String> events;

        Door(List<String> events) {
            this.events = events;
        }

        void open() {
            events.add("open");
        }

        @Override
        public void close() {
            events.add("close");
        }
    }

    static List<String> useDoor() {
        List<String> events = new ArrayList<>();
        try (Door door = new Door(events)) {
            door.open();
        }
        return List.copyOf(events);
    }

    // Q3 — useDoor() có ghi "close" sau "open" không?
    static final Boolean Q3_TRY_WITH_RESOURCES_CLOSES = true; // SOLUTION-VALUE

    /* ANSWER Q4:
     * SOLUTION-BEGIN
     * catch (Exception) nuốt mọi lỗi, kể cả lỗi lập trình. return null biến thất bại thành một giá trị
     * "không có gì", caller không phân biệt được dữ liệu trống với thao tác hỏng, và mất stack trace
     * cùng kiểu exception gốc. Lỗi thành bug im, khó tìm hơn là để exception chạy lên biên xử lý.
     * SOLUTION-END
     */
}
