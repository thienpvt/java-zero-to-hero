package phase01.d11_optional;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Optional — Bài 1: Tạo và unwrap Optional
 *
 * Nguồn: 01-java-core-advanced.md, mục 11 (Optional), câu 1–3.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_CreateAndUnwrapTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q1 [DỰ ĐOÁN] {@code Optional.of()} và {@code ofNullable()} khác nhau thế nào?
 *   Bắt đầu   : đọc Javadoc của hai method (Ctrl+N → gõ Optional → Enter → Ctrl+F12 →
 *               chọn of/ofNullable → Ctrl+Q), rồi điền 2 hằng số Q1_* bên dưới (thay null).
 *   Kiểm chứng: chạy q01_prediction; nếu sai, đặt breakpoint ngay dòng gọi
 *               {@code Optional.of(null)} trong test, Debug (Shift+F9), F7 Step Into vào
 *               Optional.of để thấy lời gọi Objects.requireNonNull bên trong.
 *   Code      : không.
 *   Hoàn thành khi: q01_prediction xanh; giải thích được khi nào nên dùng of() (chắc chắn
 *               không null) thay vì ofNullable() (giá trị có thể null).
 * <p>
 * Q2 [DỰ ĐOÁN] {@code orElse()} và {@code orElseGet()} khác gì?
 *   Bắt đầu   : đọc expensiveDefault() và DEFAULT_CALLS bên dưới, điền 2 hằng số Q2_*.
 *   Kiểm chứng: chạy q02_prediction; nếu sai, đặt breakpoint trong expensiveDefault()
 *               (Ctrl+N → Ex01_CreateAndUnwrap → Ctrl+F12 → expensiveDefault), Debug
 *               q02_prediction, xem breakpoint có dừng lại khi gọi orElse so với orElseGet
 *               hay không (đếm số lần dừng).
 *   Code      : không.
 *   Hoàn thành khi: q02_prediction xanh; giải thích được orElse và orElseGet khác nhau ở
 *               thời điểm tham số được tính.
 * <p>
 * Q3 [DỰ ĐOÁN + CODE] Tại sao khác biệt trên có thể ảnh hưởng performance?
 *   Bắt đầu   : dựa vào kết quả quan sát ở Q2, cài đặt displayName(nickname, fullNameLookup)
 *               bên dưới sao cho fullNameLookup chỉ được gọi khi thật sự cần dùng đến.
 *   Kiểm chứng: chạy q03_displayName_*; nếu fullNameLookup bị gọi khi không cần, test dùng
 *               {@code fail(...)} trong lambda supplier sẽ đỏ ngay — đặt breakpoint trong
 *               lambda đó (Alt+F12 mở Terminal nếu cần javap -c -p target/classes để xem
 *               bytecode gọi invokedynamic của lambda).
 *   Code      : displayName(Optional&lt;String&gt; nickname, Supplier&lt;String&gt; fullNameLookup).
 *   Hoàn thành khi: mọi test q03_* xanh; giải thích được vì sao orElseGet (lazy) tránh được
 *               chi phí tính giá trị mặc định khi Optional đã có giá trị, đối lập với orElse
 *               (eager) ở Q2 — chi phí này đáng kể khi giá trị mặc định tốn kém (gọi DB, I/O).
 */
public class Ex01_CreateAndUnwrap {

    /** Số lần {@link #expensiveDefault()} đã được gọi — dùng để đo orElse so với orElseGet. */
    static final AtomicInteger DEFAULT_CALLS = new AtomicInteger();

    /**
     * Giả lập việc tính giá trị mặc định "đắt" (ví dụ gọi DB/network); tăng {@link #DEFAULT_CALLS}
     * mỗi lần được gọi rồi trả về {@code "default"}.
     */
    static String expensiveDefault() {
        DEFAULT_CALLS.incrementAndGet();
        return "default";
    }

    // Q1 — Optional.of(null) ném exception gì?
    static final String Q1_OF_NULL_EXCEPTION = "NullPointerException"; // SOLUTION-VALUE
    // Q1 — Optional.ofNullable(null).isPresent() có trả true không?
    static final Boolean Q1_OF_NULLABLE_NULL_IS_PRESENT = false; // SOLUTION-VALUE

    // Q2 — Optional.of("x").orElse(expensiveDefault()): expensiveDefault() có bị gọi không?
    static final Integer Q2_ORELSE_CALLS_WHEN_PRESENT = 1; // SOLUTION-VALUE
    // Q2 — Optional.of("x").orElseGet(Ex01_CreateAndUnwrap::expensiveDefault): có bị gọi không?
    static final Integer Q2_ORELSEGET_CALLS_WHEN_PRESENT = 0; // SOLUTION-VALUE

    /**
     * Trả về biệt danh nếu {@code nickname} có giá trị và không blank; ngược lại gọi
     * {@code fullNameLookup} để lấy tên đầy đủ — chỉ gọi khi thật sự cần, không được gọi
     * trước khi biết {@code nickname} có hợp lệ hay không.
     *
     * @param nickname biệt danh, có thể rỗng/blank hoặc không có giá trị
     * @param fullNameLookup nguồn cung cấp tên đầy đủ khi không có biệt danh hợp lệ
     * @return biệt danh hợp lệ, hoặc kết quả của {@code fullNameLookup.get()}
     */
    static String displayName(Optional<String> nickname, Supplier<String> fullNameLookup) {
        // SOLUTION-BEGIN throw Q3
        return nickname.filter(n -> !n.isBlank()).orElseGet(fullNameLookup);
        // SOLUTION-END
    }
}
