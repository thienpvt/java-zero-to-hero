package phase00.d03_flow;

/**
 * Luồng và phương thức — Bài 1: break, continue và truyền đối số
 *
 * Nguồn: 00-java-basics-review.md, mục 3 (Điều khiển luồng và phương thức), câu 2, 3.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_BreakAndArgumentsTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q2 [DỰ ĐOÁN] {@code break} và {@code continue} ảnh hưởng vòng lặp ra sao?
 *   Bắt đầu   : đọc {@code sumWithBreak} và {@code sumWithContinue}, điền hai hằng Q2_*
 *               bằng tổng bạn tính tay, trước khi chạy test.
 *   Kiểm chứng: Debug q02_prediction, breakpoint trong vòng for, F8 từng vòng, Alt+F8 xem {@code s}.
 *   Hoàn thành khi: q02_prediction xanh; nói được break thoát hẳn vòng, continue bỏ phần còn lại
 *               của vòng hiện tại.
 *
 * Q3 [DỰ ĐOÁN] Một phương thức gán lại tham số object có thay tham chiếu ở phía caller không?
 *     Thay đổi state của object đó thì sao?
 *   Bắt đầu   : đọc {@code reassign} và {@code mutate}, điền Q3_REASSIGN_VISIBLE và Q3_MUTATE_VISIBLE.
 *   Kiểm chứng: Debug q03_prediction. Đặt breakpoint ở dòng gán {@code boxed = new Box(99)}
 *               và ở {@code boxed.value = 99}. Alt+F8 so {@code boxed} bên trong method với
 *               biến {@code caller} ở test (xem identity hash hoặc value).
 *   Hoàn thành khi: q03_prediction xanh; nói được Java truyền bản sao của tham chiếu.
 */
public class Ex01_BreakAndArguments {

    static int sumWithBreak() {
        int sum = 0;
        for (int i = 1; i <= 5; i++) {
            if (i == 4) {
                break;
            }
            sum += i;
        }
        return sum;
    }

    static int sumWithContinue() {
        int sum = 0;
        for (int i = 1; i <= 5; i++) {
            if (i == 4) {
                continue;
            }
            sum += i;
        }
        return sum;
    }

    // Q2 — tổng mà sumWithBreak và sumWithContinue trả về.
    static final Integer Q2_BREAK_SUM = 6; // SOLUTION-VALUE
    static final Integer Q2_CONTINUE_SUM = 11; // SOLUTION-VALUE

    static final class Box {
        int value;

        Box(int value) {
            this.value = value;
        }
    }

    static void reassign(Box boxed) {
        boxed = new Box(99);
    }

    static void mutate(Box boxed) {
        boxed.value = 99;
    }

    // Q3 — sau reassign(caller), caller.value có thành 99 không?
    static final Boolean Q3_REASSIGN_VISIBLE = false; // SOLUTION-VALUE

    // Q3 — sau mutate(caller), caller.value có thành 99 không?
    static final Boolean Q3_MUTATE_VISIBLE = true; // SOLUTION-VALUE
}
