package phase01.d08_immutability;

import phase01.support.Compiles;

/**
 * Immutability — Bài 1: `final` không đồng nghĩa immutable
 *
 * Nguồn: 01-java-core-advanced.md, mục 8 (Immutability), câu 1, 2.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_FinalIsNotImmutableTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q1 [DỰ ĐOÁN] `final class` có đồng nghĩa immutable không?
 *   Bắt đầu   : đọc class Counter bên dưới (Ctrl+N gõ "Ex01_FinalIsNotImmutable" nếu cần
 *               nhảy nhanh); điền hằng Q1_FINAL_CLASS_STATE_CHANGED (thay null).
 *   Kiểm chứng: chạy q01_prediction; test tạo Counter, gọi increment(), so value() trước
 *               và sau. Muốn xem tận mắt: đặt breakpoint ở dòng value++ trong increment(),
 *               Debug test, dùng Alt+F8 (Evaluate Expression) gõ "value" để xem field đổi.
 *   Hoàn thành khi: q01_prediction xanh; giải thích được `final` trên class chỉ cấm viết
 *               `class X extends Counter` (không cho kế thừa), hoàn toàn không liên quan
 *               đến việc field bên trong instance có bị đổi giá trị được hay không.
 *
 * Q2 [DỰ ĐOÁN] `final List<String>` có immutable không?
 *   Bắt đầu   : điền 4 hằng Q2_* bên dưới (thay null từng hằng theo đúng kiểu khai báo).
 *               Với Q2_REASSIGN_FINAL_LIST_COMPILES: bỏ comment 2 dòng trong
 *               reassignScratchpad() ở dưới để tự thấy dòng nào gạch đỏ lúc biên dịch,
 *               rồi comment lại (không xoá) trước khi chạy test.
 *   Kiểm chứng: chạy q02_prediction. Với Q2_LIST_OF_ADD_EXCEPTION và
 *               Q2_UNMODIFIABLE_VIEW_SEES_BACKING_CHANGE, đặt con trỏ lên `List.of` hoặc
 *               `Collections.unmodifiableList` trong file test rồi Ctrl+B (hoặc Ctrl+Click)
 *               để nhảy tới source JDK, đọc Javadoc bằng Ctrl+Q ngay tại đó.
 *   Hoàn thành khi: q02_prediction xanh; giải thích được `final` trên biến chỉ khoá *tham
 *               chiếu* (không được gán lại biến đó sang object khác), hoàn toàn không khoá
 *               *nội dung* mà object đó trỏ tới — khác với `List.of(...)` (immutable thật,
 *               ném UnsupportedOperationException khi sửa) và với view bọc qua
 *               `Collections.unmodifiableList` (chỉ cấm sửa qua view, backing list gốc vẫn
 *               sửa được và view thấy ngay thay đổi đó).
 */
public class Ex01_FinalIsNotImmutable {

    static final class Counter {
        private int value;

        void increment() {
            value++;
        }

        int value() {
            return value;
        }
    }

    // Q1 — kịch bản: final Counter c = new Counter(); int before = c.value();
    // c.increment(); int after = c.value(); so before với after.
    static final Boolean Q1_FINAL_CLASS_STATE_CHANGED = true; // SOLUTION-VALUE

    // Q2 — kịch bản: final List<String> list = new ArrayList<>(); list.add("a");
    // add() có chạy được không (không ném ngoại lệ)?
    static final Boolean Q2_CAN_ADD_TO_FINAL_LIST = true; // SOLUTION-VALUE

    // Q2 — mẫu: bỏ comment 2 dòng trong reassignScratchpad() dưới đây để tự thấy lỗi đỏ
    // lúc biên dịch (gán lại giá trị cho biến final), rồi comment lại trước khi chạy test.
    static final Compiles Q2_REASSIGN_FINAL_LIST_COMPILES = Compiles.NO; // SOLUTION-VALUE

    // Q2 — kịch bản: List.of("a").add("b") ném ngoại lệ gì (tên lớp, không kèm package)?
    static final String Q2_LIST_OF_ADD_EXCEPTION = "UnsupportedOperationException"; // SOLUTION-VALUE

    // Q2 — kịch bản: List<String> backing = new ArrayList<>(List.of("a"));
    // List<String> view = Collections.unmodifiableList(backing); backing.add("b");
    // view.contains("b") có true không?
    static final Boolean Q2_UNMODIFIABLE_VIEW_SEES_BACKING_CHANGE = true; // SOLUTION-VALUE

    /**
     * Chỗ trống dùng để thử gán lại một biến {@code final} (không được gọi ở đâu cả; chỉ
     * để bỏ comment và quan sát lỗi biên dịch, sau đó comment lại).
     */
    private static void reassignScratchpad() {
        // final java.util.List<String> list = new java.util.ArrayList<>();
        // list = new java.util.ArrayList<>();
    }
}
