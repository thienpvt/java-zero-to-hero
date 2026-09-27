package phase01.d07_comparable_comparator;

import java.util.Comparator;

/**
 * Comparable & Comparator — Bài 2: Comparator composition và xử lý null
 *
 * Nguồn: 01-java-core-advanced.md, mục 7 (Comparable & Comparator), câu 5, 6.
 * Cần làm trước: Ex01_NaturalVsCustomOrder (Comparator.comparing, thenComparing).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_ComparatorCompositionTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q5 [CODE] Làm thế nào sort theo nhiều field?
 *   Bắt đầu   : đọc record Employee bên dưới; Ctrl+F12 trên Comparator để xem
 *               thenComparing(Function) và thenComparing(Function, Comparator), và
 *               Comparator.reverseOrder().
 *   Kiểm chứng: chạy q05_*; nếu đỏ, đặt breakpoint trong lambda so sánh của test, Debug
 *               test, F8 từng bước xem department → age → name được so theo thứ tự nào.
 *   Code      : cài đặt byDepartmentThenAgeDescThenName() — department tăng dần, rồi
 *               (cùng department) age giảm dần, rồi (cùng age) name tăng dần.
 *   Hoàn thành khi: q05_* xanh; giải thích được các thenComparing được áp dụng từ trái
 *               sang phải, chỉ dùng field sau khi mọi field trước đã so bằng nhau (== 0).
 * <p>
 * Q6 [DỰ ĐOÁN + CODE] Làm thế nào xử lý null khi sort?
 *   Bắt đầu   : đọc hằng số Q6_SORT_NULL_AGE_EXCEPTION bên dưới; bỏ comment dòng ví dụ
 *               cạnh nó, Alt+F8 (Evaluate Expression) thử
 *               Comparator.comparing(Employee::age).compare(emp1, empVoiAgeNull) để tự
 *               thấy ngoại lệ trước khi điền dự đoán, rồi comment lại.
 *   Kiểm chứng: chạy q06_*; nếu sai, Ctrl+B vào Integer.compareTo(Integer) để thấy lệnh
 *               gọi anotherInteger.value trên tham số null gây NullPointerException.
 *   Code      : cài đặt byAgeNullsLast() (dùng Comparator.nullsLast) và
 *               byDepartmentNullsFirstThenName() (dùng Comparator.nullsFirst).
 *   Hoàn thành khi: q06_* xanh; giải thích được vì sao Comparator.comparing() trần không
 *               an toàn khi key có thể null, còn nullsFirst/nullsLast bọc thêm để xử lý.
 */
public class Ex02_ComparatorComposition {

    record Employee(String department, String name, Integer age) {
    }

    // Q6 — ví dụ: Comparator.comparing(Employee::age).compare(emp1, empVoiAgeNull)
    static final String Q6_SORT_NULL_AGE_EXCEPTION = null;

    static Comparator<Employee> byDepartmentThenAgeDescThenName() {
        throw new UnsupportedOperationException("TODO Q5");
    }

    static Comparator<Employee> byAgeNullsLast() {
        throw new UnsupportedOperationException("TODO Q6");
    }

    static Comparator<Employee> byDepartmentNullsFirstThenName() {
        throw new UnsupportedOperationException("TODO Q6");
    }
}
