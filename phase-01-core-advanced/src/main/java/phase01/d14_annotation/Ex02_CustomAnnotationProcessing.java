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
 * <p>
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
 * <p>
 * Q5 [CODE + TỰ TRẢ LỜI] Spring xử lý những annotation như {@code @Service} bằng cơ chế nào?
 *   Bắt đầu   : đọc ba class mẫu Mailer, Clock2 (có @Component) và NotAComponent (không có).
 *   Kiểm chứng: chạy q05_*; Alt+F8 (Evaluate Expression) trong lúc debug và gọi
 *               Mailer.class.isAnnotationPresent(Component.class) để tự thấy true/false.
 *   Code      : cài đặt instantiateComponents(List&lt;Class&lt;?&gt;&gt;): lọc bằng
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
     * @return danh sách {@code "&lt;tên field&gt;: &lt;thông báo lỗi&gt;"}, sắp theo thứ tự tên field tăng
     *     dần; rỗng nếu {@code bean} hợp lệ
     */
    static List<String> validate(Object bean) {
        throw new UnsupportedOperationException("TODO Q6");
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
        throw new UnsupportedOperationException("TODO Q5");
    }
}

/* ANSWER Q5:
 *
 */

/* ANSWER Q6:
 *
 */
