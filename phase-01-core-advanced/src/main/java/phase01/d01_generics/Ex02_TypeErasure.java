package phase01.d01_generics;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import phase01.support.Compiles;

/**
 * Generics — Bài 2: Type Erasure
 *
 * Nguồn: 01-java-core-advanced.md, mục 1 (Generics), câu 5, 6, 10, 7, 11.
 * Cần làm trước: Ex01_InvarianceAndWildcards (khái niệm wildcard cơ bản).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_TypeErasureTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q5 [DỰ ĐOÁN + CODE] Tại sao không thể viết: `T value = new T();`
 *   Bắt đầu   : điền hằng Q5_NEW_T_COMPILES; bỏ comment thân method mẫu ngay dưới hằng,
 *               xem IDE báo gì, rồi comment lại — sau khi đã điền dự đoán.
 *   Kiểm chứng: chạy q05_*; đặt breakpoint trong newInstance(), Debug test, F7 vào
 *               `Class.getDeclaredConstructor()` (Ctrl+B trên tên method) để thấy cơ chế
 *               thay thế cho `new T()`.
 *   Code      : createN gọi factory.get() đúng n lần (n < 0 ném IllegalArgumentException);
 *               newInstance gọi constructor không tham số qua reflection, bọc
 *               ReflectiveOperationException thành IllegalArgumentException (giữ cause).
 *   Hoàn thành khi: các test q05_* xanh; giải thích được vì sao JVM không biết T là gì
 *               lúc chạy nên compiler không cho gọi `new T()` trực tiếp.
 *
 * Q6 [DỰ ĐOÁN] Type Erasure là gì?
 *   Bắt đầu   : điền hằng Q6_SAME_RUNTIME_CLASS.
 *   Kiểm chứng: chạy q06_prediction; sau đó `Alt+F12` mở Terminal trong IntelliJ, build
 *               (`mvnw.cmd -q -pl phase-01-core-advanced test-compile`) rồi chạy
 *               `javap -c -p target/test-classes/phase01/d01_generics/Ex02_TypeErasureTest.class`,
 *               trong method `q10_prediction()` tìm lệnh `checkcast` lớp `java/lang/String`
 *               ngay sau `invokeinterface java/util/List.get` (ứng với dòng `strings.get(1)`
 *               trong test) — đó chính là checkcast ẩn do compiler chèn vì type erasure.
 *   Hoàn thành khi: q06_prediction xanh; giải thích được vì sao hai ArrayList với type
 *               argument khác nhau lại có cùng một đối tượng Class lúc runtime.
 *
 * Q10 [DỰ ĐOÁN + TỰ TRẢ LỜI] Tại sao generic giúp type safety nhưng không hoàn toàn tồn
 *     tại ở runtime?
 *   Bắt đầu   : điền hằng Q10_HEAP_POLLUTION_FAILS_AT (kiểu FailurePoint khai báo bên dưới).
 *   Kiểm chứng: chạy q10_prediction; đặt breakpoint tại `raw.add(42)` và tại
 *               `strings.get(1)`, Debug test, F7 — sau khi đã điền dự đoán, tự xem bước
 *               nào ném ClassCastException.
 *   Hoàn thành khi: q10_prediction xanh và viết xong khối ANSWER Q10 ở cuối file.
 *
 * Q7 [DỰ ĐOÁN + CODE] Tại sao Java không cho phép: `if (obj instanceof List<String>)`
 *   Bắt đầu   : điền hằng Q7_INSTANCEOF_LIST_STRING_COMPILES; bỏ comment 2 dòng mẫu
 *               ngay dưới hằng, xem IDE báo gì, rồi comment lại — sau khi đã điền dự đoán.
 *   Kiểm chứng: chạy q07_*; đặt breakpoint trong isListOfStrings, F7 qua từng phần tử.
 *   Code      : isListOfStrings trả true nếu obj là List<?> và mọi phần tử là String
 *               (list rỗng → true; có phần tử null → false; không phải List → false).
 *   Hoàn thành khi: các test q07_* xanh; giải thích được vì `List<String>` không tồn tại
 *               như một kiểu runtime riêng (chỉ còn `List`) nên JVM không kiểm tra được.
 *
 * Q11 [TỰ TRẢ LỜI] Vì sao `List<String>` và `List<Integer>` không phân biệt được bằng
 *     `instanceof` tại runtime?
 *   Bắt đầu   : làm Q6 và Q7 trước, liên hệ kết quả javap ở Q6 để trả lời.
 *   Kiểm chứng: Tra cứu — không có test riêng.
 *   Hoàn thành khi: viết xong khối ANSWER Q11 ở cuối file.
 */
public class Ex02_TypeErasure {

    /** Bước mà heap pollution thực sự lộ ra lỗi ClassCastException khi dùng raw type. */
    enum FailurePoint {
        ADD,
        GET
    }

    // Q5 — mẫu: bỏ comment method dưới đây, xem IDE báo gì, rồi comment lại
    // — sau khi đã điền dự đoán:
    // static <T> T brokenFactory() {
    //     return new T();
    // }
    static final Compiles Q5_NEW_T_COMPILES = null;

    /**
     * Gọi {@code factory.get()} đúng {@code n} lần và trả về danh sách các kết quả.
     *
     * @throws IllegalArgumentException nếu {@code n < 0}
     */
    static <T> List<T> createN(Supplier<? extends T> factory, int n) {
        throw new UnsupportedOperationException("TODO Q5");
    }

    /**
     * Tạo instance mới của {@code type} bằng constructor không tham số (thay cho {@code new T()}
     * không thể viết trực tiếp trong generic method vì type erasure).
     *
     * @throws IllegalArgumentException nếu {@code type} không có constructor không tham số truy
     *     cập được hoặc việc khởi tạo thất bại; nguyên nhân gốc được giữ lại qua {@code getCause()}
     */
    static <T> T newInstance(Class<T> type) {
        throw new UnsupportedOperationException("TODO Q5");
    }

    // Q6 — kịch bản: so sánh Class runtime của hai ArrayList với type argument khác nhau
    static final Boolean Q6_SAME_RUNTIME_CLASS = null;

    // Q10 — kịch bản: List<String> strings = new ArrayList<>(List.of("a")); List raw = strings;
    // raw.add(42); rồi strings.get(1) — bước nào thực sự ném ClassCastException?
    static final FailurePoint Q10_HEAP_POLLUTION_FAILS_AT = null;

    // Q7 — mẫu: Object sample, rồi `sample instanceof List<String>`.
    // Bỏ comment 2 dòng dưới, xem IDE báo gì, rồi comment lại — sau khi đã điền dự đoán.
    // Object sample = List.of("a");
    // if (sample instanceof List<String>) {}
    static final Compiles Q7_INSTANCEOF_LIST_STRING_COMPILES = null;

    /**
     * {@code true} nếu {@code obj} là một {@code List<?>} và mọi phần tử của nó là {@code String}.
     * List rỗng trả về {@code true}; nếu có phần tử {@code null} thì trả về {@code false}.
     */
    static boolean isListOfStrings(Object obj) {
        throw new UnsupportedOperationException("TODO Q7");
    }
}

/* ANSWER Q10:
 *
 */

/* ANSWER Q11:
 *
 */
