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
 *
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
 *
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
    static final Compiles Q4_MODIFY_CAPTURED_LOCAL_COMPILES = Compiles.NO; // SOLUTION-VALUE

    // Q4 — mẫu mutateField: lambda gán lại field tĩnh sharedCounter.
    // Bỏ comment, xem IDE báo gì, rồi comment lại — sau khi đã điền dự đoán:
    // static int sharedCounter = 0;
    // static Runnable mutateField() {
    //     return () -> { sharedCounter = sharedCounter + 1; };
    // }
    static final Compiles Q4_MODIFY_FIELD_IN_LAMBDA_COMPILES = Compiles.YES; // SOLUTION-VALUE

    // Q4 — mẫu mutateArrayElement: lambda gán box[0], không gán lại biến box.
    // Bỏ comment, xem IDE báo gì, rồi comment lại — sau khi đã điền dự đoán:
    // static Runnable mutateArrayElement(int[] box) {
    //     return () -> { box[0] = box[0] + 1; };
    // }
    static final Compiles Q4_MODIFY_ARRAY_ELEMENT_COMPILES = Compiles.YES; // SOLUTION-VALUE

    /**
     * Tạo một {@link Supplier} vô trạng thái bên ngoài: mỗi lần {@code get()} trả về
     * {@code start}, {@code start + 1}, {@code start + 2}, ... theo đúng thứ tự gọi.
     *
     * @param start giá trị bắt đầu
     * @return supplier tăng dần độc lập với mọi supplier khác tạo từ lệnh gọi khác
     */
    static Supplier<Integer> counter(int start) {
        // SOLUTION-BEGIN throw Q4
        AtomicInteger current = new AtomicInteger(start);
        return current::getAndIncrement;
        // SOLUTION-END
    }

    static Function<String, Integer> length() {
        // SOLUTION-BEGIN throw Q5
        return String::length;
        // SOLUTION-END
    }

    static BiPredicate<String, String> equalsIgnoreCase() {
        // SOLUTION-BEGIN throw Q5
        return String::equalsIgnoreCase;
        // SOLUTION-END
    }

    static Supplier<List<String>> listFactory() {
        // SOLUTION-BEGIN throw Q5
        return ArrayList::new;
        // SOLUTION-END
    }
}

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Không khác về bản chất chạy: cả hai đều được biên dịch thành một lệnh invokedynamic tại chỗ
 * gọi, bootstrap qua LambdaMetafactory để tạo instance implement functional interface — không có
 * class ẩn danh riêng như kiểu cũ (anonymous inner class). Khác biệt nằm ở chỗ method reference
 * "trỏ thẳng" tới một method đã tồn tại (String.length, String.equalsIgnoreCase, constructor của
 * ArrayList) nên javac KHÔNG cần sinh thêm method ẩn danh chứa thân lambda — đã kiểm chứng bằng
 * javap -c -p: length()/equalsIgnoreCase()/listFactory() chỉ có một invokedynamic, không có method
 * lambda$...$n nào trong class. Một lambda viết tay tương đương như s -> s.length() thì luôn sinh
 * thêm một method private static lambda$length$0(String) chứa đúng thân đó. Trade-off: method
 * reference ngắn, dễ đọc khi đã có method sẵn khớp signature; lambda linh hoạt hơn khi cần thân
 * nhiều dòng hoặc logic không map 1:1 với method có sẵn.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Không nên. Lambda hợp với biểu thức ngắn, một hành vi rõ ràng; khi thân dài (20–30 dòng) hoặc có
 * nhiều side effect (log, I/O, sửa state ngoài), lambda mất các lợi thế của method thường: không
 * có tên riêng để đọc lướt hiểu ý định, stack trace chỉ hiện lambda$tênMethod$n nên khó biết đang ở
 * bước nào khi debug, và IDE khó tách nhỏ/step qua từng đoạn như với method có tên. Nên rút thành
 * method hoặc class riêng (đặt tên theo hành vi) rồi truyền method reference vào, giữ lambda chỉ để
 * nối các bước ngắn với nhau.
 * SOLUTION-END
 */
