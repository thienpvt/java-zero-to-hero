package phase01.d05_equals_hashcode;

import java.util.Locale;
import java.util.Objects;

/**
 * equals() và hashCode() — Bài 2: Quan hệ một chiều giữa hashCode và equals
 *
 * Nguồn: 01-java-core-advanced.md, mục 5 (equals() và hashCode()), câu 2, 3, 4.
 * Cần làm trước: Ex01_EqualsContract (contract equals()).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_HashCodeRulesTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q2 [DỰ ĐOÁN] Hai object cùng hashCode có bắt buộc equals không?
 *   Bắt đầu   : Alt+F8 mở Evaluate Expression, thử "Aa".hashCode() và "BB".hashCode()
 *               để tự thấy hai chuỗi khác nhau vẫn cho cùng hashCode (hash collision).
 *   Kiểm chứng: chạy q02_prediction_sameHashDoesNotImplyEquals; Ctrl+Q trên String.hashCode
 *               để đọc công thức s[0]*31^(n-1) + ... + s[n-1] rồi tự tính tay để hiểu vì sao trùng.
 *   Code      : không có; câu này chỉ dự đoán dựa trên việc chạy code thật trong test.
 *   Hoàn thành khi: q02_* xanh; giải thích được đây là hash collision bình thường,
 *               không phải lỗi của hashCode().
 *
 * Q3 [DỰ ĐOÁN] Hai object equals có bắt buộc cùng hashCode không?
 *   Bắt đầu   : Ctrl+N → gõ "Object" → mở class, Ctrl+F12 tìm method hashCode(),
 *               Ctrl+Q xem Javadoc đoạn "If two objects are equal according to the
 *               equals(Object) method, then calling hashCode() ... must produce the
 *               same integer result".
 *   Kiểm chứng: Alt+F8 Evaluate Expression thử new String("test").hashCode() hai lần
 *               (hai instance khác nhau, cùng value) để tự thấy luôn trùng.
 *   Code      : không có; câu này chỉ dự đoán dựa trên việc chạy code thật trong test.
 *   Hoàn thành khi: q03_* xanh; giải thích được vì sao đây là yêu cầu bắt buộc
 *               (nếu không, HashMap/HashSet sẽ tìm hai object "bằng nhau" ở hai bucket khác nhau).
 *
 * Q4 [DỰ ĐOÁN + CODE] Nếu chỉ override equals mà không override hashCode thì bug xuất hiện ở đâu?
 *   Bắt đầu   : đọc class EqualsOnlyUser bên dưới — có equals() theo email nhưng KHÔNG
 *               override hashCode() (nên dùng hashCode() mặc định của Object, dựa trên
 *               identity); điền 2 hằng số Q4_HASHSET_SIZE_TWO_EQUAL_USERS,
 *               Q4_HASHSET_CONTAINS_EQUAL_USER (thay null).
 *   Kiểm chứng: đặt breakpoint trong HashMap.hash (Ctrl+N → HashMap, Ctrl+F12 tìm hash),
 *               Debug q04_prediction_equalsOnlyUserBreaksHashSet, F7 Step Into để thấy
 *               hai EqualsOnlyUser "equals" nhưng hashCode khác nhau nên HashSet coi
 *               là hai phần tử khác nhau.
 *   Code      : cài đặt equals()/hashCode() của User theo email đã chuẩn hóa
 *               (String.equals cho equals, String.hashCode cho hashCode).
 *   Hoàn thành khi: q04_* xanh; giải thích được bằng lời vì sao "override equals mà
 *               quên override hashCode" là bug kinh điển — HashSet/HashMap không còn
 *               nhận ra hai object "bằng nhau" theo equals() là trùng lặp.
 */
public class Ex02_HashCodeRules {

    // Q2 — kịch bản: "Aa".hashCode() == "BB".hashCode() (hash collision) nhưng "Aa".equals("BB").
    static final Boolean Q2_SAME_HASH_IMPLIES_EQUALS = false; // SOLUTION-VALUE

    // Q3 — kịch bản: hai String equals nhau (ví dụ new String("test") hai lần) có cùng hashCode.
    static final Boolean Q3_EQUALS_REQUIRES_SAME_HASH = true; // SOLUTION-VALUE

    /**
     * Minh họa bug kinh điển: equals() theo email nhưng không override hashCode(),
     * nên hai instance "bằng nhau" theo equals() vẫn có hashCode khác nhau (identity hashCode
     * mặc định của Object — xác suất hai identity hashCode trùng nhau là không đáng kể).
     */
    static final class EqualsOnlyUser {
        final String email;

        EqualsOnlyUser(String email) {
            this.email = Objects.requireNonNull(email, "email");
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof EqualsOnlyUser other && email.equals(other.email);
        }

        // Không override hashCode(): dùng Object.hashCode() (identity) mặc định.
    }

    // Q4 — kịch bản: add hai EqualsOnlyUser cùng email (equals true) vào HashSet.
    static final Integer Q4_HASHSET_SIZE_TWO_EQUAL_USERS = 2; // SOLUTION-VALUE
    static final Boolean Q4_HASHSET_CONTAINS_EQUAL_USER = false; // SOLUTION-VALUE

    /** User có equals()/hashCode() nhất quán, chuẩn hóa email trước khi so sánh. */
    static final class User {
        final String email;
        final String displayName;

        User(String email, String displayName) {
            this.email = email.trim().toLowerCase(Locale.ROOT);
            this.displayName = displayName;
        }

        @Override
        public boolean equals(Object o) {
            // SOLUTION-BEGIN throw Q4
            return o instanceof User other && email.equals(other.email);
            // SOLUTION-END
        }

        @Override
        public int hashCode() {
            // SOLUTION-BEGIN throw Q4
            return email.hashCode();
            // SOLUTION-END
        }

        @Override
        public String toString() {
            return "User[" + email + ", " + displayName + "]";
        }
    }
}
