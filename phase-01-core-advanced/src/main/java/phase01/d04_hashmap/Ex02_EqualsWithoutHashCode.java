package phase01.d04_hashmap;

import java.util.Objects;

/**
 * HashMap — Bài 2: equals() không hashCode()
 *
 * Nguồn: 01-java-core-advanced.md, mục 4 (HashMap), câu 6.
 * Cần làm trước: Ex01_LookupAndCollision (luồng hashCode → bucket → equals).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_EqualsWithoutHashCodeTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q6 [DỰ ĐOÁN + CODE] Vì sao phải override {@code hashCode()} khi override {@code equals()}?
 *   Bắt đầu   : đọc class EqualsOnlyPoint bên dưới (equals() theo x, y nhưng hashCode()
 *               cộng thêm salt tăng dần — mô phỏng tất định hashCode() mặc định của Object,
 *               vốn khác nhau giữa các instance dù equals() coi là bằng nhau); điền hằng số
 *               Q6_HASHSET_SIZE_TWO_EQUAL_POINTS.
 *   Kiểm chứng: chạy q06_hashSetSizeTwoEqualPoints_prediction; nếu sai, đặt breakpoint tại
 *               HashMap.putVal (HashSet dùng HashMap bên trong), Step Into (F7) để thấy hai
 *               EqualsOnlyPoint(1,2) rơi vào hai bucket khác nhau vì hashCode khác nhau.
 *   Code      : cài đặt equals(Object) và hashCode() của class Point sao cho tuân theo
 *               contract: hai object equals() true phải có cùng hashCode().
 *   Hoàn thành khi: mọi test q06_* xanh + giải thích được vì sao thiếu hashCode() nhất quán
 *               làm HashSet/HashMap "mất" phần tử trùng logic.
 */
public class Ex02_EqualsWithoutHashCode {

    /**
     * equals() theo (x, y) nhưng hashCode() còn cộng thêm salt tăng dần mỗi lần tạo instance —
     * mô phỏng tất định trường hợp "chỉ override equals(), quên override hashCode()": hai
     * instance được equals() coi là bằng nhau nhưng hashCode() lại khác nhau.
     */
    static final class EqualsOnlyPoint {
        private static int saltCounter;

        final int x;
        final int y;
        final int salt;

        EqualsOnlyPoint(int x, int y) {
            this.x = x;
            this.y = y;
            this.salt = saltCounter++;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof EqualsOnlyPoint p && p.x == x && p.y == y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, salt);
        }
    }

    // Q6 — kịch bản: HashSet chứa hai EqualsOnlyPoint(1, 2) tạo từ hai lệnh new khác nhau.
    static final Integer Q6_HASHSET_SIZE_TWO_EQUAL_POINTS = 2; // SOLUTION-VALUE

    /** Điểm 2D bất biến, tuân theo contract equals()/hashCode(). */
    static final class Point {
        final int x;
        final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            // SOLUTION-BEGIN throw Q6
            return o instanceof Point p && p.x == x && p.y == y;
            // SOLUTION-END
        }

        @Override
        public int hashCode() {
            // SOLUTION-BEGIN throw Q6
            return Objects.hash(x, y);
            // SOLUTION-END
        }
    }
}
