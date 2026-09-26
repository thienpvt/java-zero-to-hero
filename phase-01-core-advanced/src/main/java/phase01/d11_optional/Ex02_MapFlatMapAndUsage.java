package phase01.d11_optional;

import java.util.Locale;
import java.util.Optional;
import phase01.support.Compiles;

/**
 * Optional — Bài 2: map/flatMap và cách dùng Optional cho đúng
 *
 * Nguồn: 01-java-core-advanced.md, mục 11 (Optional), câu 4–6.
 * Cần làm trước: Ex01_CreateAndUnwrap (of/ofNullable, orElse/orElseGet).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_MapFlatMapAndUsageTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q6 [DỰ ĐOÁN + CODE] `map()` và `flatMap()` trên Optional khác nhau thế nào?
 *   Bắt đầu   : đọc addressOpt() và legacyCityUpper() bên dưới, điền hằng số Q6_* (thay null).
 *   Kiểm chứng: chạy q06_prediction; nếu sai, Ctrl+N → gõ Optional → Enter → Ctrl+F12 →
 *               so sánh chữ ký {@code <U> Optional<U> map(Function<? super T,? extends U>)}
 *               với {@code <U> Optional<U> flatMap(Function<? super T,? extends Optional<? extends U>>)}.
 *   Code      : cityUpper(Customer), zipOf(Optional&lt;Customer&gt;).
 *   Hoàn thành khi: mọi test q06_* xanh; giải thích được vì sao map(addressOpt) tạo ra
 *               Optional&lt;Optional&lt;Address&gt;&gt; (phải gọi thêm get()/flatMap để bóc lớp
 *               ngoài) còn flatMap(addressOpt) làm phẳng ngay thành Optional&lt;Address&gt;.
 *
 * Q5 [DỰ ĐOÁN] Optional có thực sự loại bỏ NullPointerException không?
 *   Bắt đầu   : bỏ comment dòng {@code Optional<String> o = null;} ngay trên hằng số Q5_*
 *               bên dưới, xem IDE báo gì, rồi comment lại — sau khi đã điền dự đoán;
 *               điền 2 hằng số Q5_*.
 *   Kiểm chứng: chạy q05_prediction; sau khi đã điền dự đoán, Debug q05_prediction (F7
 *               Step Into vào Optional.get()) để quan sát exception thật khi gọi get trên
 *               empty — so với dự đoán Q5_GET_ON_EMPTY_EXCEPTION.
 *   Code      : không.
 *   Hoàn thành khi: q05_prediction xanh; giải thích được Optional chỉ tránh NPE khi API
 *               *trả về* Optional được unwrap đúng cách (map/flatMap/orElseGet...) — biến
 *               kiểu Optional bản thân nó vẫn có thể là null (Optional không phải "NPE-proof").
 *
 * Q4 [TỰ TRẢ LỜI] Optional có nên dùng làm method argument không?
 *   Bắt đầu   : làm Q5 và Q6 trước rồi mới trả lời câu này.
 *   Tra cứu   : Ctrl+N → gõ Optional → Enter → Ctrl+Q xem đoạn "API Note" đầu Javadoc class
 *               ("Optional is primarily intended for use as a method return type...").
 *   Hoàn thành khi: viết xong khối ANSWER Q4 ở cuối file, có nêu ít nhất một cách thay thế
 *               (overload method, hoặc nhận giá trị thường và để caller tự xử lý null).
 */
public class Ex02_MapFlatMapAndUsage {

    /** Địa chỉ; {@code city}/{@code zip} có thể là {@code null} (dữ liệu thiếu). */
    record Address(String city, String zip) {
    }

    /** Khách hàng; {@code address} có thể là {@code null} (khách chưa khai báo địa chỉ). */
    record Customer(String id, Address address) {
    }

    /** Bọc {@link Customer#address()} thành Optional; {@code c} null hoặc address null → empty. */
    static Optional<Address> addressOpt(Customer c) {
        return c == null ? Optional.empty() : Optional.ofNullable(c.address());
    }

    /**
     * Chuỗi if-null lồng nhau kiểu cũ (không dùng Optional), chỉ để đối chiếu phong cách với
     * {@link #cityUpper(Customer)}. Trả về {@code null} nếu bất kỳ mắt xích nào thiếu hoặc
     * city blank.
     */
    static String legacyCityUpper(Customer c) {
        if (c == null) {
            return null;
        }
        Address a = c.address();
        if (a == null) {
            return null;
        }
        String city = a.city();
        if (city == null || city.isBlank()) {
            return null;
        }
        return city.toUpperCase(Locale.ROOT);
    }

    // Q6 — Optional.of(c).map(Ex02_MapFlatMapAndUsage::addressOpt).get() có phải instanceof Optional?
    static final Boolean Q6_MAP_WITH_OPTIONAL_FUNCTION_NESTS = null;

    /**
     * Trả city viết hoa ({@link Locale#ROOT}) nếu {@code customer}, address của nó và city
     * đều tồn tại và city không blank; ngược lại trả về {@link Optional#empty()}.
     *
     * @param customer khách hàng, có thể {@code null}
     * @return Optional chứa city viết hoa, hoặc empty
     */
    static Optional<String> cityUpper(Customer customer) {
        throw new UnsupportedOperationException("TODO Q6");
    }

    /**
     * Lấy zip từ address của {@code customer} bằng {@code flatMap}, không lồng Optional.
     *
     * @param customer Optional chứa khách hàng (có thể rỗng)
     * @return Optional chứa zip, hoặc empty nếu thiếu customer/address/zip
     */
    static Optional<String> zipOf(Optional<Customer> customer) {
        throw new UnsupportedOperationException("TODO Q6");
    }

    // Q5 — bỏ comment dòng dưới để xem có lỗi biên dịch không, rồi comment lại:
    // Optional<String> o = null;
    static final Compiles Q5_ASSIGN_NULL_TO_OPTIONAL_COMPILES = null;
    // Q5 — Optional.empty().get() ném exception gì?
    static final String Q5_GET_ON_EMPTY_EXCEPTION = null;
}

/* ANSWER Q4:
 *
 */
