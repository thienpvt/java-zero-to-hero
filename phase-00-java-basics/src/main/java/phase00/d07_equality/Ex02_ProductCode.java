package phase00.d07_equality;

import java.util.Objects;

/**
 * So sánh object — Bài 2: ProductCode và HashSet
 *
 * Nguồn: 00-java-basics-review.md, mục 7 (Object, so sánh và biểu diễn dữ liệu), câu 2.
 * Cần làm trước: Ex01_DefaultEquals.
 * Cách làm: điền hai hằng Q2_* rồi cài equals/hashCode của ProductCode. Chạy Ex02_ProductCodeTest.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q2 [DỰ ĐOÁN + CODE] Vì sao override {@code equals()} mà quên {@code hashCode()} có thể làm
 *     {@code HashSet} hoạt động sai kỳ vọng?
 *   Bắt đầu   : đọc {@code NameOnly} (có equals, không hashCode). Điền Q2_SET_SIZE và
 *               Q2_CONTAINS_EQUAL_INSTANCE theo kịch bản trên từng hằng. Rồi cài ProductCode.
 *   Kiểm chứng: Debug q02_prediction, breakpoint khi {@code set.add} lần thứ hai. Ctrl+N → {@code HashMap},
 *               Ctrl+F12 tìm {@code hash}, F7 để thấy hai object equals vẫn có thể khác hash.
 *               Ctrl+Q trên {@code Object.hashCode}.
 *   Code      : {@code ProductCode.equals} và {@code hashCode} theo {@code value}. Hai mã cùng value
 *               là một phần tử trong {@code HashSet}.
 *   Hoàn thành khi: q02_* xanh; giải thích được HashSet tìm theo hashCode trước, rồi mới equals.
 */
public class Ex02_ProductCode {

    static final class NameOnly {
        final String name;

        NameOnly(String name) {
            this.name = Objects.requireNonNull(name, "name");
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof NameOnly that && name.equals(that.name);
        }
    }

    // Q2 — HashSet thêm hai NameOnly cùng tên: size bằng bao nhiêu?
    static final Integer Q2_SET_SIZE = null;

    // Q2 — set.contains(một NameOnly thứ ba cùng tên) sau hai lần add?
    static final Boolean Q2_CONTAINS_EQUAL_INSTANCE = null;

    static final class ProductCode {
        private final String value;

        ProductCode(String value) {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("mã trống");
            }
            this.value = value;
        }

        @Override
        public boolean equals(Object other) {
            throw new UnsupportedOperationException("TODO Q2");
        }

        @Override
        public int hashCode() {
            throw new UnsupportedOperationException("TODO Q2");
        }
    }
}
