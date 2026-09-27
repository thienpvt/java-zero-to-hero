package phase01.d15_modern_java;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import phase01.support.Compiles;

/**
 * Modern Java — Bài 1: record
 *
 * Nguồn: 01-java-core-advanced.md, mục 15 (Java hiện đại: record, sealed type, pattern matching), câu 1–2.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_RecordsTest bằng nút ▶
 * cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q1 [DỰ ĐOÁN + CODE] {@code record} khác POJO thông thường ở điểm nào và khi nào vẫn cần class thường?
 *   Bắt đầu   : đọc record Point bên dưới; bỏ comment lần lượt hai đoạn mẫu ngay trên
 *               Q1_RECORD_EXTENDS_CLASS_COMPILES và Q1_RECORD_IMPLEMENTS_INTERFACE_COMPILES,
 *               build (Ctrl+F9) để thấy dòng nào đỏ, rồi comment lại và điền hằng số.
 *   Kiểm chứng: chạy q01_recordToString; đặt breakpoint trong compact constructor của
 *               Email (Ctrl+N → Ex01_Records → cuộn tới record Email), Debug test
 *               q01_emailNormalizesTrimAndLowercase, dùng F7 (Step Into) để xem thứ tự
 *               kiểm tra null → trim/lowercase → vị trí '@'.
 *   Code      : cài đặt compact constructor của record Email: null → NullPointerException;
 *               trim rồi toLowerCase(Locale.ROOT); không có đúng một '@' hoặc phần trước/sau
 *               '@' rỗng → IllegalArgumentException.
 *   Hoàn thành khi: các test q01_* xanh; giải thích được record tự sinh những gì
 *               (constructor, accessor, equals/hashCode/toString) từ danh sách component,
 *               và vì sao class thường vẫn cần khi cần kế thừa class khác hoặc field mutable.
 * <p>
 * Q2 [DỰ ĐOÁN + CODE] Một component {@code List} trong record có tự trở nên immutable không? Bạn sẽ bảo vệ nó thế nào?
 *   Bắt đầu   : đọc record RawOrder bên dưới, chạy q02_rawOrderLeaksMutationWithoutCopy để
 *               thấy sửa list nguồn sau khi tạo record vẫn lộ ra ngoài, rồi điền
 *               Q2_LIST_COMPONENT_CHANGES_WITHOUT_COPY.
 *   Kiểm chứng: Alt+F8 (Evaluate Expression) trên order.lines().getClass() trong khi debug
 *               test q02_orderCopiesListAndRejectsMutation để thấy kiểu thực tế là một
 *               List bất biến; Ctrl+Click vào List.copyOf để đọc Javadoc.
 *   Code      : cài đặt compact constructor của record Order: Objects.requireNonNull(id);
 *               lines = List.copyOf(lines).
 *   Hoàn thành khi: q02_* xanh; giải thích được vì sao record chỉ tự bảo vệ được reference
 *               của field (không cho gán lại), không tự bảo vệ nội dung mutable bên trong.
 */
public class Ex01_Records {

    record Point(int x, int y) {
    }

    // Q1 — mẫu: record extends một class thường. Bỏ comment, xem IDE báo gì, rồi comment
    // lại — sau khi đã điền dự đoán.
    // static class Shape {
    // }
    // record BadPoint(int x, int y) extends Shape {
    // }
    static final Compiles Q1_RECORD_EXTENDS_CLASS_COMPILES = Compiles.NO; // SOLUTION-VALUE

    // Q1 — mẫu: record implements một interface và cài method của interface đó.
    // Bỏ comment, xem IDE báo gì, rồi comment lại — sau khi đã điền dự đoán.
    // interface HasArea {
    //     double area();
    // }
    // record SquareShape(double side) implements HasArea {
    //     public double area() {
    //         return side * side;
    //     }
    // }
    static final Compiles Q1_RECORD_IMPLEMENTS_INTERFACE_COMPILES = Compiles.YES; // SOLUTION-VALUE

    static final String Q1_RECORD_TO_STRING = "Point[x=1, y=2]"; // SOLUTION-VALUE

    record Email(String value) {
        Email {
            // SOLUTION-BEGIN throw Q1
            Objects.requireNonNull(value, "value không được null");
            String normalized = value.trim().toLowerCase(Locale.ROOT);
            int at = normalized.indexOf('@');
            boolean missingOrDuplicateAt = at < 0 || at != normalized.lastIndexOf('@');
            boolean emptySideOfAt = at == 0 || at == normalized.length() - 1;
            if (missingOrDuplicateAt || emptySideOfAt) {
                throw new IllegalArgumentException("Email không hợp lệ: " + value);
            }
            value = normalized;
            // SOLUTION-END
        }
    }

    record RawOrder(String id, List<String> lines) {
    }

    static final Boolean Q2_LIST_COMPONENT_CHANGES_WITHOUT_COPY = true; // SOLUTION-VALUE

    record Order(String id, List<String> lines) {
        Order {
            // SOLUTION-BEGIN throw Q2
            Objects.requireNonNull(id, "id không được null");
            lines = List.copyOf(lines);
            // SOLUTION-END
        }
    }
}
