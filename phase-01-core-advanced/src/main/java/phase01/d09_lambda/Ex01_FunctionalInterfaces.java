package phase01.d09_lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import phase01.support.Compiles;

/**
 * Lambda & Functional Interface — Bài 1: Functional interface, Predicate/Function/Consumer
 *
 * Nguồn: 01-java-core-advanced.md, mục 9 (Lambda & Functional Interface), câu 1–3.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_FunctionalInterfacesTest bằng nút ▶
 * cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q1 [DỰ ĐOÁN + CODE] Functional Interface là gì?
 *   Bắt đầu   : mở interface Validator ngay bên dưới (Ctrl+N gõ Ex01_FunctionalInterfaces), điền
 *               2 hằng số Q1_* (thay null) dựa trên đoạn interface TwoAbstract mẫu.
 *   Kiểm chứng: bỏ comment đoạn interface TwoAbstract mẫu, xem IDE báo gì, rồi comment lại
 *               — sau khi đã điền dự đoán; chạy q01_prediction để xác nhận cả 2 hằng.
 *   Code      : cài đặt and() (phần TODO Q1): true khi cả hai validate() đều true; NPE ngay khi
 *               other == null; short-circuit (không gọi other.validate khi vế đầu false).
 *               notBlank(); maxLength(int max) (max &lt; 0 → IllegalArgumentException; value
 *               null → false).
 *   Hoàn thành khi: q01_* xanh; giải thích được @FunctionalInterface cho phép bao nhiêu abstract
 *               method, và default method có bị tính vào số đó không.
 * <p>
 * Q2 [DỰ ĐOÁN + CODE] {@code Predicate} và {@code Function} khác nhau thế nào?
 *   Bắt đầu   : Ctrl+N mở Predicate (java.util.function.Predicate), Ctrl+F12 xem method test(T).
 *   Kiểm chứng: chạy q02_prediction; Ctrl+Q trên Predicate rồi trên Function để so Javadoc.
 *   Code      : dùng chung method process() cài ở câu Q3 (phần TODO Q3) — Predicate.test() quyết
 *               định phần tử nào được giữ, Function.apply() biến đổi phần tử đó.
 *   Hoàn thành khi: q02_prediction xanh; giải thích được kiểu trả về của Predicate.test so với
 *               Function.apply, và mỗi cái dùng để làm gì.
 * <p>
 * Q3 [DỰ ĐOÁN + CODE] {@code Consumer} có return value không?
 *   Bắt đầu   : Ctrl+N mở Consumer, Ctrl+F12 xem method accept(T).
 *   Kiểm chứng: chạy q03_prediction.
 *   Code      : cài đặt process(List&lt;String&gt;, Predicate&lt;String&gt;,
 *               Function&lt;String,String&gt;, Consumer&lt;String&gt;) (phần TODO Q3): lọc theo
 *               keep, biến đổi theo transform, gọi audit với từng kết quả đã biến đổi theo đúng
 *               thứ tự, trả về list mới (không sửa input).
 *   Hoàn thành khi: q03_* xanh; giải thích được Consumer.accept có trả giá trị không, và side
 *               effect nằm ở đâu.
 */
public class Ex01_FunctionalInterfaces {

    @FunctionalInterface
    interface Validator<T> {
        boolean validate(T value);

        default Validator<T> and(Validator<? super T> other) {
            throw new UnsupportedOperationException("TODO Q1");
        }
    }

    // Bỏ comment đoạn dưới, xem IDE báo gì, rồi comment lại — sau khi đã điền dự đoán:
    // @FunctionalInterface
    // interface TwoAbstract<T> {
    //     boolean validate(T value);
    //     boolean validateStrict(T value);
    // }

    // Q1 — có biên dịch được không?
    static final Compiles Q1_TWO_ABSTRACT_METHODS_WITH_ANNOTATION_COMPILES = null;
    // Q1 — default method có được tính là abstract method thứ hai không?
    static final Boolean Q1_DEFAULT_METHODS_ALLOWED = null;

    // Q2 — kiểu trả về nguyên thủy của Predicate<T>.test(T)
    static final String Q2_PREDICATE_TEST_RETURN_TYPE = null;
    // Q3 — kiểu trả về của Consumer<T>.accept(T)
    static final String Q3_CONSUMER_ACCEPT_RETURN_TYPE = null;

    static Validator<String> notBlank() {
        throw new UnsupportedOperationException("TODO Q1");
    }

    static Validator<String> maxLength(int max) {
        throw new UnsupportedOperationException("TODO Q1");
    }

    /**
     * Lọc {@code input} theo {@code keep}, biến đổi các phần tử còn lại bằng {@code transform},
     * gọi {@code audit} với từng kết quả đã biến đổi theo đúng thứ tự, rồi trả về danh sách mới.
     *
     * @return danh sách mới chứa các kết quả đã biến đổi; {@code input} không bị sửa đổi
     */
    static List<String> process(List<String> input, Predicate<String> keep, Function<String, String> transform,
            Consumer<String> audit) {
        throw new UnsupportedOperationException("TODO Q3");
    }
}
