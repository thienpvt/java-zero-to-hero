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
    static final Fix Q1_TELESCOPING_SOLVED = Fix.NAMED_STEPS_FOR_OPTIONAL_PARAMETERS; // SOLUTION-VALUE

    // Q2 — Builder khác Factory ở đâu.
    static final Difference Q2_BUILDER_VS_FACTORY = Difference.BUILDER_ASSEMBLES_FIELDS_FACTORY_CHOOSES_CLASS; // SOLUTION-VALUE
    // Q3 — builder có giúp object immutable.
    static final Boolean Q3_IMMUTABLE = true; // SOLUTION-VALUE

    // Q5 — validation nên ở builder hay constructor.
    static final Location Q5_VALIDATION_LOCATION = Location.OBJECT_CONSTRUCTOR; // SOLUTION-VALUE

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
                // SOLUTION-BEGIN throw Q3
                this.name = name;
                return this;
                // SOLUTION-END
            }

            public Builder email(String email) {
                // SOLUTION-BEGIN throw Q3
                this.email = email;
                return this;
                // SOLUTION-END
            }

            public Builder age(int age) {
                // SOLUTION-BEGIN throw Q3
                this.age = age;
                return this;
                // SOLUTION-END
            }

            public User build() {
                // SOLUTION-BEGIN throw Q3
                return new User(name, email, age);
                // SOLUTION-END
            }
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Telescoping constructor là nhiều constructor với tham số tuỳ chọn, chỗ gọi phải nhớ thứ tự.
 * Builder cho mỗi tham số một method có tên, nên chỗ gọi đọc lên là hiểu.
 * Tham số tuỳ chọn bỏ qua được, không cần constructor riêng cho mỗi tổ hợp.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Builder ráp nhiều field cho một lớp đã biết, quyết định là điền gì theo thứ tự nào.
 * Factory chọn một trong nhiều lớp cùng interface, không quan tâm ráp field.
 * Có thể kết hợp: factory chọn lớp rồi trả về builder của lớp đó.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Builder giúp immutable bằng cách tách quá trình ráp khỏi object đã ráp.
 * Trong lúc ráp, field còn đổi được; build tạo object với mọi field final.
 * Object đã tạo không có setter, nên không ai sửa được sau đó.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Với hai field và không có tham số tuỳ chọn, builder thêm một lớp phải đọc và một lời gọi dài hơn.
 * Constructor hai tham số đọc lên đã rõ, không có thứ tự dễ nhầm.
 * Builder đáng dùng khi nhiều tham số tuỳ chọn hoặc validation cần nhiều bước.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Constructor của object là chốt cuối, mọi object đều đi qua đó dù tạo bằng builder hay cách khác.
 * Builder nên kiểm để báo lỗi sớm ngay tại bước đặt sai.
 * Nếu chỉ kiểm ở builder, một đường tạo khác có thể bỏ qua và tạo ra object sai.
 * SOLUTION-END
 */
