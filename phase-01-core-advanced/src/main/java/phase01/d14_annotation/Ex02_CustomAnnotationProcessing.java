package phase01.d14_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Annotation — Bài 2: Custom annotation và xử lý bằng reflection
 *
 * Nguồn: 01-java-core-advanced.md, mục 14 (Annotation), câu 5–6.
 * Cần làm trước: Ex01_RetentionAndTarget (đọc annotation bằng reflection: isAnnotationPresent,
 *   getAnnotation, @Target).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_CustomAnnotationProcessingTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q6 [CODE + TỰ TRẢ LỜI] Khi nào nên tạo custom annotation?
 *   Bắt đầu   : đọc class SignupForm bên dưới: username có @NotBlank @MaxLength(10),
 *               email có @NotBlank, nickname không gắn annotation nào.
 *   Kiểm chứng: chạy q06_*; đặt breakpoint đầu validate() (Ctrl+N → gõ tên file → Ctrl+F12 để
 *               nhảy tới validate), Debug test, F8 để thấy field.getAnnotation(NotBlank.class)
 *               trả null với field nickname nhưng trả về instance thật với username/email.
 *   Code      : cài đặt validate(Object bean): dùng bean.getClass().getDeclaredFields(),
 *               Field.setAccessible(true), Field.get(bean) để đọc giá trị; field vi phạm
 *               @NotBlank khi giá trị null hoặc String.isBlank(); vi phạm @MaxLength khi chuỗi
 *               dài hơn MaxLength.value(); trả danh sách đã sắp theo tên field.
 *   Hoàn thành khi: q06_* xanh; viết xong khối ANSWER Q6 nêu được lợi ích (khai báo ràng buộc
 *               ngắn gọn, tái dùng cho nhiều field/class) và chi phí (cần một processor riêng
 *               đọc annotation, lỗi cấu hình chỉ lộ ra lúc chạy chứ không phải lúc gõ code).
 *
 * Q5 [CODE + TỰ TRẢ LỜI] Spring xử lý những annotation như `@Service` bằng cơ chế nào?
 *   Bắt đầu   : đọc ba class mẫu Mailer, Clock2 (có @Component) và NotAComponent (không có).
 *   Kiểm chứng: chạy q05_*; Alt+F8 (Evaluate Expression) trong lúc debug và gọi
 *               Mailer.class.isAnnotationPresent(Component.class) để tự thấy true/false.
 *   Code      : cài đặt instantiateComponents(List<Class<?>>): lọc bằng
 *               Class.isAnnotationPresent(Component.class), tạo instance bằng
 *               Class.getDeclaredConstructor().newInstance(), giữ nguyên thứ tự candidates
 *               bằng LinkedHashMap.
 *   Hoàn thành khi: q05_* xanh; viết xong khối ANSWER Q5 nêu được Spring quét classpath
 *               (component scan) để tìm class có @Component (hoặc annotation meta-annotate bởi
 *               @Component như @Service), dùng reflection tạo bean rồi có thể bọc thêm
 *               proxy/BeanPostProcessor.
 */
public class Ex02_CustomAnnotationProcessing {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface NotBlank {
        String message() default "không được để trống";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface MaxLength {
        int value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Component {
    }

    static final class SignupForm {
        @NotBlank
        @MaxLength(10)
        private String username;

        @NotBlank
        private String email;

        private String nickname;

        SignupForm(String username, String email, String nickname) {
            this.username = username;
            this.email = email;
            this.nickname = nickname;
        }
    }

    @Component
    static final class Mailer {
        public Mailer() {
        }
    }

    @Component
    static final class Clock2 {
        public Clock2() {
        }
    }

    static final class NotAComponent {
        public NotAComponent() {
        }
    }

    /**
     * Kiểm tra {@code bean} theo các annotation validation khai báo trên field, kể cả field
     * {@code private}: {@code @NotBlank} vi phạm khi giá trị {@code null} hoặc rỗng/blank;
     * {@code @MaxLength} vi phạm khi độ dài chuỗi lớn hơn giá trị cho phép.
     *
     * @param bean object cần kiểm tra, các field validation phải là {@link String}
     * @return danh sách {@code "<tên field>: <thông báo lỗi>"}, sắp theo thứ tự tên field tăng
     *     dần; rỗng nếu {@code bean} hợp lệ
     */
    static List<String> validate(Object bean) {
        // SOLUTION-BEGIN throw Q6
        List<String> violations = new ArrayList<>();
        for (Field field : bean.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value;
            try {
                value = field.get(bean);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Không đọc được field " + field.getName(), e);
            }
            NotBlank notBlank = field.getAnnotation(NotBlank.class);
            if (notBlank != null && (value == null || value.toString().isBlank())) {
                violations.add(field.getName() + ": " + notBlank.message());
            }
            MaxLength maxLength = field.getAnnotation(MaxLength.class);
            if (maxLength != null && value != null && value.toString().length() > maxLength.value()) {
                violations.add(field.getName() + ": dài tối đa " + maxLength.value() + " ký tự");
            }
        }
        violations.sort(Comparator.naturalOrder());
        return violations;
        // SOLUTION-END
    }

    /**
     * Tạo instance cho mỗi class trong {@code candidates} có gắn {@code @Component}, dùng
     * constructor không tham số; class không có {@code @Component} bị bỏ qua.
     *
     * @param candidates danh sách class cần xét, theo đúng thứ tự cần giữ lại trong kết quả
     * @return map giữ nguyên thứ tự của {@code candidates}; khoá là class, giá trị là instance
     *     vừa tạo bằng constructor không tham số
     */
    static Map<Class<?>, Object> instantiateComponents(List<Class<?>> candidates) {
        // SOLUTION-BEGIN throw Q5
        Map<Class<?>, Object> result = new LinkedHashMap<>();
        for (Class<?> candidate : candidates) {
            if (!candidate.isAnnotationPresent(Component.class)) {
                continue;
            }
            try {
                Constructor<?> constructor = candidate.getDeclaredConstructor();
                result.put(candidate, constructor.newInstance());
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Không tạo được instance của " + candidate.getName(), e);
            }
        }
        return result;
        // SOLUTION-END
    }
}

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Spring quét classpath lúc khởi động (component scan) để tìm class có @Component hoặc
 * annotation meta-annotate bởi @Component (@Service, @Repository, @Controller...), rồi dùng
 * reflection để tạo bean qua constructor (ưu tiên constructor có @Autowired nếu có nhiều),
 * tiêm dependency vào field/setter @Autowired, và có thể bọc bean bằng proxy
 * (BeanPostProcessor, AOP) trước khi đưa vào ApplicationContext.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Nên tạo custom annotation khi có một ràng buộc/metadata lặp lại ở nhiều field/class (validation,
 * cấu hình, đánh dấu để framework xử lý) và annotation giúp code khai báo ngắn, dễ đọc hơn so với
 * gọi hàm thủ công lặp đi lặp lại. Trade-off: annotation chỉ là dữ liệu, luôn cần một processor
 * (reflection, annotation processor, bytecode weaving) đọc và diễn giải nó thì mới có hành vi; lỗi
 * cấu hình (ví dụ quên gắn annotation) chỉ lộ ra lúc chạy chứ không phải lúc gõ code như gọi hàm
 * bình thường.
 * SOLUTION-END
 */
