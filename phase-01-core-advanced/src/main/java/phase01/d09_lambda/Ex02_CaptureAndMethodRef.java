package phase01.d09_lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import phase01.support.Compiles;

/**
 * Lambda & Functional Interface — Bài 2: Capture biến local và method reference
 *
 * Nguồn: 01-java-core-advanced.md, mục 9 (Lambda & Functional Interface), câu 4–6.
 * Cần làm trước: Ex01_FunctionalInterfaces (Validator, Predicate/Function/Consumer).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_CaptureAndMethodRefTest bằng nút
 * ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q4 [DỰ ĐOÁN + CODE] Vì sao local variable được lambda capture phải effectively final?
 *   Bắt đầu   : điền 3 hằng số Q4_* bên dưới (thay null) dựa trên 3 đoạn code mẫu comment ngay
 *               cạnh mỗi hằng.
 *   Kiểm chứng: bỏ comment từng đoạn mẫu (brokenCapture, mutateField, mutateArrayElement), xem
 *               IDE báo gì với từng đoạn, rồi comment lại — sau khi đã điền dự đoán; chạy
 *               q04_prediction.
 *   Code      : cài đặt counter(int start) (phần TODO Q4) dùng AtomicInteger — mỗi lần get() trả
 *               lần lượt start, start+1, start+2, ...
 *   Hoàn thành khi: q04_* xanh; giải thích được compiler chỉ chặn *gán lại* biến local được capture
 *               (x = x + 1), không chặn việc đổi state bên trong field tĩnh hoặc phần tử của array
 *               mà biến local đó tham chiếu tới (biến tham chiếu không đổi, chỉ nội dung đổi).
 * <p>
 * Q5 [CODE + TỰ TRẢ LỜI] Method reference có khác lambda về bản chất không?
 *   Bắt đầu   : cài đặt length(), equalsIgnoreCase(), listFactory() bằng method reference.
 *   Kiểm chứng: build project (Ctrl+F9), mở Terminal (Alt+F12), chạy
 *               javap -c -p target/classes/phase01/d09_lambda/Ex02_CaptureAndMethodRef.class,
 *               so sánh bytecode của length(), equalsIgnoreCase() và listFactory() với một lambda
 *               viết tay như {@code s -> s.length()}: tìm lệnh invokedynamic và method dạng
 *               lambda$, tự ghi nhận method nào sinh thêm method ẩn — sau khi đã cài đặt.
 *               Đây là kiểm tra tĩnh trên bytecode, không đặt breakpoint.
 *   Code      : length() (String::length), equalsIgnoreCase() (String::equalsIgnoreCase),
 *               listFactory() (ArrayList::new) — mỗi cái một phần TODO Q5.
 *   Hoàn thành khi: q05_* xanh và khối ANSWER Q5 đã viết, nêu được javap cho thấy method reference
 *               và lambda khác nhau thế nào ở bytecode.
 * <p>
 * Q6 [TỰ TRẢ LỜI] Lambda có phù hợp với logic dài và nhiều side effect không?
 *   Bắt đầu   : đọc lại process() ở Ex01_FunctionalInterfaces và counter() ở trên, tưởng tượng viết
 *               thêm 20–30 dòng side effect (log, I/O, đổi state ngoài) ngay trong một lambda.
 *   Tra cứu   : không có API cụ thể cho câu này — tự suy luận từ trade-off đọc/debug (stack trace
 *               chỉ ghi lambda$tênMethod$n, không có tên method riêng để đặt breakpoint chính xác,
 *               khó Step Into (F7) từng đoạn như một method thường).
 *   Hoàn thành khi: viết xong khối ANSWER Q6, nêu rõ dấu hiệu nên rút lambda dài ra thành method
 *               hoặc class riêng.
 */
public class Ex02_CaptureAndMethodRef {

    // Q4 — mẫu brokenCapture: lambda gán lại biến local x.
    // Bỏ comment, xem IDE báo gì, rồi comment lại — sau khi đã điền dự đoán:
    // static Runnable brokenCapture(int x) {
    //     return () -> { x = x + 1; System.out.println(x); };
    // }
    static final Compiles Q4_MODIFY_CAPTURED_LOCAL_COMPILES = null;

    // Q4 — mẫu mutateField: lambda gán lại field tĩnh sharedCounter.
    // Bỏ comment, xem IDE báo gì, rồi comment lại — sau khi đã điền dự đoán:
    // static int sharedCounter = 0;
    // static Runnable mutateField() {
    //     return () -> { sharedCounter = sharedCounter + 1; };
    // }
    static final Compiles Q4_MODIFY_FIELD_IN_LAMBDA_COMPILES = null;

    // Q4 — mẫu mutateArrayElement: lambda gán box[0], không gán lại biến box.
    // Bỏ comment, xem IDE báo gì, rồi comment lại — sau khi đã điền dự đoán:
    // static Runnable mutateArrayElement(int[] box) {
    //     return () -> { box[0] = box[0] + 1; };
    // }
    static final Compiles Q4_MODIFY_ARRAY_ELEMENT_COMPILES = null;

    /**
     * Tạo một {@link Supplier} vô trạng thái bên ngoài: mỗi lần {@code get()} trả về
     * {@code start}, {@code start + 1}, {@code start + 2}, ... theo đúng thứ tự gọi.
     *
     * @param start giá trị bắt đầu
     * @return supplier tăng dần độc lập với mọi supplier khác tạo từ lệnh gọi khác
     */
    static Supplier<Integer> counter(int start) {
        throw new UnsupportedOperationException("TODO Q4");
    }

    static Function<String, Integer> length() {
        throw new UnsupportedOperationException("TODO Q5");
    }

    static BiPredicate<String, String> equalsIgnoreCase() {
        throw new UnsupportedOperationException("TODO Q5");
    }

    static Supplier<List<String>> listFactory() {
        throw new UnsupportedOperationException("TODO Q5");
    }
}

/* ANSWER Q5:
 *
 */

/* ANSWER Q6:
 *
 */
