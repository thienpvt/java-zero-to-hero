package phase01.d05_equals_hashcode;

import java.util.Arrays;
import java.util.Objects;

/**
 * equals() và hashCode() — Bài 3: record và entity mutable trong HashSet
 *
 * Nguồn: 01-java-core-advanced.md, mục 5 (equals() và hashCode()), câu 6, 7.
 * Cần làm trước: Ex02_HashCodeRules (quan hệ equals/hashCode).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex03_RecordsAndMutableEntitiesTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q6 [DỰ ĐOÁN + CODE] Với record, equals/hashCode được xử lý thế nào?
 *   Bắt đầu   : đọc record Point và Blob bên dưới; Ctrl+F12 trên Point để thấy compiler
 *               không sinh method equals/hashCode "thấy được" trong source nhưng chúng vẫn
 *               tồn tại (record tự sinh dựa trên tất cả component); điền 3 hằng số
 *               Q6_RECORD_EQUAL_BY_VALUE, Q6_RECORD_SAME_HASH, Q6_ARRAY_COMPONENT_EQUAL_BY_CONTENT.
 *   Kiểm chứng: Alt+F8 Evaluate Expression thử new Point(1, 2).equals(new Point(1, 2));
 *               rồi thử với hai Blob cùng nội dung byte[] nhưng khác instance mảng để
 *               thấy record KHÔNG so nội dung mảng (equals mặc định của record dùng
 *               Objects.equals cho từng component, mà với mảng nghĩa là so theo tham chiếu).
 *   Code      : cài đặt compact constructor (sao chép mảng vào, dùng Objects.requireNonNull),
 *               accessor data() (trả bản sao), equals()/hashCode() (dùng Arrays.equals/
 *               Arrays.hashCode — Ctrl+N → Arrays để xem các overload) và toString() của SafeBlob.
 *   Hoàn thành khi: q06_* xanh; giải thích được vì sao record tự sinh equals cho record
 *               thường (Point) nhưng riêng component kiểu mảng (Blob.data) phải tự viết
 *               equals/hashCode bằng Arrays.equals/Arrays.hashCode mới so được theo nội dung,
 *               và vì sao SafeBlob cần defensive copy ở cả constructor và accessor.
 *
 * Q7 [DỰ ĐOÁN + CODE] Vì sao mutable entity có thể gây vấn đề khi nằm trong HashSet?
 *   Bắt đầu   : đọc class Customer bên dưới — equals()/hashCode() dựa trên cả id và email
 *               (email có thể đổi sau khi tạo); điền 3 hằng số Q7_CONTAINS_AFTER_MUTATION,
 *               Q7_REMOVE_AFTER_MUTATION_SUCCEEDS, Q7_SIZE_AFTER_READD (thay null).
 *   Kiểm chứng: đặt breakpoint trong HashMap.getNode (Ctrl+N → HashMap, Ctrl+F12 → getNode),
 *               Debug q07_prediction_mutationBreaksHashSetLookup, F7 Step Into để thấy
 *               hashCode() mới của Customer (sau khi đổi email) trỏ sang bucket khác nên
 *               contains()/remove() không tìm lại được entry cũ.
 *   Code      : cài đặt equals()/hashCode() của StableCustomer chỉ dựa trên id (String.equals,
 *               String.hashCode) — bỏ qua email vì email có thể đổi.
 *   Hoàn thành khi: q07_* xanh; giải thích được bằng lời tại sao nên chọn field bất biến
 *               (immutable identity, ví dụ id) làm cơ sở cho equals/hashCode của entity có
 *               thể bị mutate, để entity vẫn "tìm lại được" trong HashSet/HashMap sau khi đổi
 *               các field khác.
 */
public class Ex03_RecordsAndMutableEntities {

    /** Record thường — dùng để quan sát equals/hashCode do compiler tự sinh theo giá trị. */
    record Point(int x, int y) {
    }

    /** Record có component kiểu mảng — equals/hashCode mặc định so mảng theo tham chiếu. */
    record Blob(byte[] data) {
    }

    // Q6 — kịch bản: Point(1,2) so với Point(1,2) khác instance; Blob cùng nội dung khác mảng.
    static final Boolean Q6_RECORD_EQUAL_BY_VALUE = true; // SOLUTION-VALUE
    static final Boolean Q6_RECORD_SAME_HASH = true; // SOLUTION-VALUE
    static final Boolean Q6_ARRAY_COMPONENT_EQUAL_BY_CONTENT = false; // SOLUTION-VALUE

    /**
     * Record an toàn cho dữ liệu nhị phân: defensive copy ở cả compact constructor và
     * accessor, equals/hashCode/toString tự viết lại dựa trên nội dung mảng (không dùng
     * bản mặc định do record tự sinh, vì bản mặc định so mảng theo tham chiếu).
     */
    record SafeBlob(byte[] data) {

        SafeBlob {
            // SOLUTION-BEGIN throw Q6
            Objects.requireNonNull(data, "data");
            data = data.clone();
            // SOLUTION-END
        }

        @Override
        public byte[] data() {
            // SOLUTION-BEGIN throw Q6
            return data.clone();
            // SOLUTION-END
        }

        @Override
        public boolean equals(Object o) {
            // SOLUTION-BEGIN throw Q6
            return o instanceof SafeBlob other && Arrays.equals(data, other.data);
            // SOLUTION-END
        }

        @Override
        public int hashCode() {
            // SOLUTION-BEGIN throw Q6
            return Arrays.hashCode(data);
            // SOLUTION-END
        }

        @Override
        public String toString() {
            // SOLUTION-BEGIN throw Q6
            return "SafeBlob" + Arrays.toString(data);
            // SOLUTION-END
        }
    }

    /**
     * Entity mutable: equals()/hashCode() dựa trên cả id và email, nên hashCode() đổi khi
     * email đổi — minh họa vấn đề kinh điển của mutable key/element trong HashSet/HashMap.
     */
    static final class Customer {
        final String id;
        String email;

        Customer(String id, String email) {
            this.id = Objects.requireNonNull(id, "id");
            this.email = Objects.requireNonNull(email, "email");
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Customer other && id.equals(other.id) && email.equals(other.email);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, email);
        }

        @Override
        public String toString() {
            return "Customer[" + id + ", " + email + "]";
        }
    }

    // Q7 — kịch bản: add Customer, đổi email (đổi hashCode), rồi contains/remove/re-add.
    static final Boolean Q7_CONTAINS_AFTER_MUTATION = false; // SOLUTION-VALUE
    static final Boolean Q7_REMOVE_AFTER_MUTATION_SUCCEEDS = false; // SOLUTION-VALUE
    static final Integer Q7_SIZE_AFTER_READD = 2; // SOLUTION-VALUE

    /**
     * Entity ổn định: equals()/hashCode() chỉ dựa trên {@code id} bất biến, nên vẫn
     * "tìm lại được" trong HashSet/HashMap dù {@code email} bị đổi sau khi thêm vào.
     */
    static final class StableCustomer {
        final String id;
        String email;

        StableCustomer(String id, String email) {
            this.id = Objects.requireNonNull(id, "id");
            this.email = Objects.requireNonNull(email, "email");
        }

        @Override
        public boolean equals(Object o) {
            // SOLUTION-BEGIN throw Q7
            return o instanceof StableCustomer other && id.equals(other.id);
            // SOLUTION-END
        }

        @Override
        public int hashCode() {
            // SOLUTION-BEGIN throw Q7
            return id.hashCode();
            // SOLUTION-END
        }

        @Override
        public String toString() {
            return "StableCustomer[" + id + ", " + email + "]";
        }
    }
}
