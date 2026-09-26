package phase01.d05_equals_hashcode;

import java.util.Locale;
import java.util.Objects;

/**
 * equals() và hashCode() — Bài 1: Contract của equals(), so sánh == với equals()
 *
 * Nguồn: 01-java-core-advanced.md, mục 5 (equals() và hashCode()), câu 1, 5.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_EqualsContractTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q1 [DỰ ĐOÁN + CODE] Contract của `equals()` gồm những gì?
 *   Bắt đầu   : đọc class CaseInsensitiveName bên dưới — equals() của nó nhận cả
 *               CaseInsensitiveName và String, hashCode() dùng value.toLowerCase(Locale.ROOT);
 *               điền 2 hằng số Q1_NAME_EQUALS_STRING, Q1_STRING_EQUALS_NAME (thay null).
 *   Kiểm chứng: chạy q01_prediction_nameEqualsStringIsNotSymmetric; nếu sai, đặt breakpoint
 *               trong CaseInsensitiveName.equals (Ctrl+N mở file, click số dòng), Debug test,
 *               F7 Step Into để xem nhánh `instanceof String` được chọn khi so String.equals(name).
 *   Code      : cài đặt equals()/hashCode() của Money — so amountMinor và currency
 *               (dùng Objects.equals cho currency, Objects.hash cho hashCode).
 *   Hoàn thành khi: q01_* xanh; giải thích được bằng lời vì sao
 *               CaseInsensitiveName vi phạm tính đối xứng (symmetric) của contract equals
 *               (name.equals(string) có thể khác string.equals(name)).
 *
 * Q5 [DỰ ĐOÁN] `==` và `equals()` khác nhau thế nào?
 *   Bắt đầu   : Ctrl+N → gõ "Integer" → mở class, Ctrl+F12 xem danh sách method,
 *               tìm class lồng bên trong tên IntegerCache.
 *   Kiểm chứng: Ctrl+B (hoặc Ctrl+Click) vào Integer.valueOf(int) để đọc ngưỡng cache;
 *               Alt+F8 Evaluate Expression thử `Integer a = 200, b = 200; a == b`, sau khi
 *               đã điền dự đoán, tự đọc kết quả.
 *   Code      : không có; câu này chỉ dự đoán dựa trên việc chạy code thật trong test.
 *   Hoàn thành khi: q05_* xanh; giải thích được == khác equals thế nào với String tạo bằng
 *               new, String literal, và Integer nằm trong hay ngoài vùng cache.
 */
public class Ex01_EqualsContract {

    /**
     * Class minh họa việc vi phạm tính đối xứng (symmetric) của contract equals: so sánh
     * không phân biệt hoa/thường và chấp nhận cả kiểu {@link String} lẫn chính nó, nhưng
     * {@code String.equals} không biết gì về {@code CaseInsensitiveName} nên chỉ có một
     * chiều trả về true.
     */
    static final class CaseInsensitiveName {
        final String value;

        CaseInsensitiveName(String value) {
            this.value = Objects.requireNonNull(value, "value");
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof CaseInsensitiveName other) {
                return value.equalsIgnoreCase(other.value);
            }
            if (o instanceof String s) {
                return value.equalsIgnoreCase(s);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return value.toLowerCase(Locale.ROOT).hashCode();
        }

        @Override
        public String toString() {
            return "CaseInsensitiveName[" + value + "]";
        }
    }

    // Q1 — kịch bản: new CaseInsensitiveName("Alice").equals("alice") so với chiều ngược lại.
    static final Boolean Q1_NAME_EQUALS_STRING = null;
    static final Boolean Q1_STRING_EQUALS_NAME = null;

    /** Số tiền bất biến gồm giá trị nhỏ nhất (ví dụ cent) và mã tiền tệ ISO. */
    static final class Money {
        final long amountMinor;
        final String currency;

        Money(long amountMinor, String currency) {
            this.amountMinor = amountMinor;
            this.currency = Objects.requireNonNull(currency, "currency");
        }

        /**
         * Hai {@code Money} bằng nhau khi cùng {@code amountMinor} và cùng {@code currency}.
         */
        @Override
        public boolean equals(Object o) {
            throw new UnsupportedOperationException("TODO Q1");
        }

        @Override
        public int hashCode() {
            throw new UnsupportedOperationException("TODO Q1");
        }

        @Override
        public String toString() {
            return "Money[" + amountMinor + " " + currency + "]";
        }
    }

    // Q5 — kịch bản: new String("hi") == new String("hi") so với các biến thể literal/Integer cache.
    static final Boolean Q5_NEW_STRINGS_DOUBLE_EQUALS = null;
    static final Boolean Q5_STRING_LITERALS_DOUBLE_EQUALS = null;
    static final Boolean Q5_INTEGER_127_DOUBLE_EQUALS = null;
    static final Boolean Q5_INTEGER_128_DOUBLE_EQUALS = null;
}
