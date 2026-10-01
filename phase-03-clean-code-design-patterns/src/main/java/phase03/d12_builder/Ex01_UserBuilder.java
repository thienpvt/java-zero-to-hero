package phase03.d12_builder;

/**
 * Builder — Bài 1: User
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 12 (Builder Pattern), câu 1, 2, 3, 4, 5.
 * Cần làm trước: d11_factory.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_UserBuilderTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Builder giải quyết telescoping constructor thế nào?
 *   Bắt đầu   : điền Q1_TELESCOPING_SOLVED bằng một giá trị của {@code Fix}.
 *   Kiểm chứng: chạy q01_prediction. Ctrl+F12 trên {@code User} xem constructor.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu tên tham số thay vì vị trí.
 * <p>
 * Q2 [DỰ ĐOÁN] Builder và Factory khác nhau ở đâu?
 *   Bắt đầu   : điền Q2_BUILDER_VS_FACTORY bằng một giá trị của {@code Difference}.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu builder không chọn lớp.
 * <p>
 * Q3 [DỰ ĐOÁN + CODE] Builder có giúp object immutable không?
 *   Bắt đầu   : điền Q3_IMMUTABLE, rồi cài {@code Builder.build} tạo {@code User} với field final.
 *   Kiểm chứng: chạy q03_prediction và q03_buildIsImmutable.
 *   Hoàn thành khi: hai test xanh và ANSWER Q3 nêu builder tách quá trình ráp khỏi object đã ráp.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Builder có đáng dùng với object chỉ có hai field không?
 *   Bắt đầu   : viết khối ANSWER Q4.
 *   Hoàn thành khi: ANSWER Q4 nêu chi phí lớn hơn lợi ích với ít field và không có tham số tuỳ chọn.
 * <p>
 * Q5 [DỰ ĐOÁN] Validation nên xảy ra ở builder hay object constructor?
 *   Bắt đầu   : điền Q5_VALIDATION_LOCATION bằng một giá trị của {@code Location}.
 *   Kiểm chứng: chạy q05_prediction và q05_buildRejectsBadEmail.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 nêu constructor là chốt cuối, builder kiểm sớm.
 */
public class Ex01_UserBuilder {

    /** Builder khắc phục telescoping constructor bằng cách nào. */
    public enum Fix {
        FEWER_PARAMETERS,
        NAMED_STEPS_FOR_OPTIONAL_PARAMETERS,
        FASTER_CONSTRUCTION
    }

    /** Builder khác Factory ở đâu. */
    public enum Difference {
        SAME_THING,
        BUILDER_ASSEMBLES_FIELDS_FACTORY_CHOOSES_CLASS,
        BUILDER_IS_ALWAYS_FASTER
    }

    /** Nơi kiểm tra dữ liệu khi tạo object. */
    public enum Location {
        BUILDER_ONLY,
        OBJECT_CONSTRUCTOR,
        NOWHERE
    }

    // Q1 — builder giải quyết telescoping constructor thế nào.
    static final Fix Q1_TELESCOPING_SOLVED = null;

    // Q2 — Builder khác Factory ở đâu.
    static final Difference Q2_BUILDER_VS_FACTORY = null;
    // Q3 — builder có giúp object immutable.
    static final Boolean Q3_IMMUTABLE = null;

    // Q5 — validation nên ở builder hay constructor.
    static final Location Q5_VALIDATION_LOCATION = null;

    /** Immutable: mọi field final, constructor private. */
    public static final class User {

        private final String name;
        private final String email;
        private final int age;

        private User(String name, String email, int age) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Tên không được trống.");
            }
            if (email == null || !email.contains("@")) {
                throw new IllegalArgumentException("Email không hợp lệ: " + email);
            }
            if (age < 0) {
                throw new IllegalArgumentException("Tuổi không được âm: " + age);
            }
            this.name = name;
            this.email = email;
            this.age = age;
        }

        public String name() {
            return name;
        }

        public String email() {
            return email;
        }

        public int age() {
            return age;
        }

        public static Builder builder() {
            return new Builder();
        }

        /** Ráp dần từng field, build mới tạo object. */
        public static final class Builder {

            private String name;
            private String email;
            private int age;

            public Builder name(String name) {
                throw new UnsupportedOperationException("TODO Q3");
            }

            public Builder email(String email) {
                throw new UnsupportedOperationException("TODO Q3");
            }

            public Builder age(int age) {
                throw new UnsupportedOperationException("TODO Q3");
            }

            public User build() {
                throw new UnsupportedOperationException("TODO Q3");
            }
        }
    }
}

/* ANSWER Q1:
 *
 */

/* ANSWER Q2:
 *
 */

/* ANSWER Q3:
 *
 */

/* ANSWER Q4:
 *
 */

/* ANSWER Q5:
 *
 */
