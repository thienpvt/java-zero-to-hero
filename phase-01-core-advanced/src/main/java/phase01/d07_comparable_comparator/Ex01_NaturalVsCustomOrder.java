package phase01.d07_comparable_comparator;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;

/**
 * Comparable & Comparator — Bài 1: Natural order và Comparator cơ bản
 *
 * Nguồn: 01-java-core-advanced.md, mục 7 (Comparable & Comparator), câu 1, 3, 2.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_NaturalVsCustomOrderTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q1 [CODE] Comparable và Comparator khác nhau thế nào?
 *   Bắt đầu   : đọc class Version bên dưới; Ctrl+B trên "Comparable<Version>" để mở
 *               interface Comparable trong JDK, chú ý chỉ có một method compareTo(T).
 *   Kiểm chứng: viết xong compareTo, Ctrl+Shift+F10 chạy q01_*; nếu đỏ, đặt breakpoint
 *               ở dòng đầu compareTo, Debug test, F8 từng bước xem so major/minor/patch
 *               theo thứ tự nào.
 *   Code      : cài đặt Version.compareTo(Version other) — so major, rồi minor, rồi patch
 *               (dùng Integer.compare, trả sớm khi khác 0).
 *   Hoàn thành khi: q01_* xanh; giải thích được vì sao Comparable là "tự nhiên" (Version tự
 *               biết cách so với Version khác, chỉ một cách so duy nhất), khác với Comparator
 *               ở Q2 (áp từ bên ngoài, có thể có nhiều cách so cho cùng một class).
 *
 * Q3 [DỰ ĐOÁN + CODE] Natural ordering nghĩa là gì?
 *   Bắt đầu   : đọc hằng số Q3_STRING_ORDER_PUTS_1_10_BEFORE_1_2 bên dưới; bỏ comment
 *               dòng ví dụ ngay cạnh, Alt+F8 (Evaluate Expression) gõ
 *               "1.10.0".compareTo("1.2.0"), ghi nhận dấu kết quả, rồi comment dòng ví dụ
 *               lại — điền hằng sau khi đã tự chạy.
 *   Kiểm chứng: chạy q03_*; nếu sai, Ctrl+B trên String.compareTo, đọc Javadoc cách so sánh
 *               — sau khi đã điền dự đoán.
 *   Code      : không cần viết thêm — chỉ điền hằng số dự đoán ở trên.
 *   Hoàn thành khi: q03_* xanh; giải thích được vì sao thứ tự natural của String khác
 *               Version.compareTo ở Q1.
 *
 * Q2 [CODE] Khi nào nên dùng Comparator thay vì Comparable?
 *   Bắt đầu   : đọc record Product bên dưới — Product không implements Comparable vì
 *               không có một cách sắp xếp "tự nhiên" duy nhất (có thể theo giá, theo tên...).
 *   Kiểm chứng: Ctrl+F12 trên Comparator để xem danh sách static method comparing/
 *               thenComparing; đặt con trỏ lên String.CASE_INSENSITIVE_ORDER, Ctrl+Q để
 *               đọc Javadoc field này.
 *   Code      : cài đặt Product.byPrice() và Product.byNameIgnoreCase().
 *   Hoàn thành khi: q02_* xanh; giải thích được vì sao dùng Comparator (bên ngoài, nhiều
 *               instance khác nhau) phù hợp hơn implements Comparable (chỉ một cách so)
 *               khi một class cần nhiều tiêu chí sort khác nhau.
 */
public class Ex01_NaturalVsCustomOrder {

    static final class Version implements Comparable<Version> {
        final int major;
        final int minor;
        final int patch;

        Version(int major, int minor, int patch) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
        }

        static Version parse(String text) {
            String[] parts = text.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Định dạng phải là major.minor.patch: " + text);
            }
            try {
                return new Version(
                        Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Định dạng phải là major.minor.patch: " + text, e);
            }
        }

        @Override
        public int compareTo(Version other) {
            throw new UnsupportedOperationException("TODO Q1");
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Version v && major == v.major && minor == v.minor && patch == v.patch;
        }

        @Override
        public int hashCode() {
            return Objects.hash(major, minor, patch);
        }

        @Override
        public String toString() {
            return major + "." + minor + "." + patch;
        }
    }

    // Q3 — ví dụ: "1.10.0".compareTo("1.2.0")
    static final Boolean Q3_STRING_ORDER_PUTS_1_10_BEFORE_1_2 = null;

    record Product(String name, BigDecimal price) {

        static Comparator<Product> byPrice() {
            throw new UnsupportedOperationException("TODO Q2");
        }

        static Comparator<Product> byNameIgnoreCase() {
            throw new UnsupportedOperationException("TODO Q2");
        }
    }
}
