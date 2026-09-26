package phase01.d14_annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import phase01.support.Compiles;

/**
 * Annotation — Bài 1: Retention và Target
 *
 * Nguồn: 01-java-core-advanced.md, mục 14 (Annotation), câu 1–4.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_RetentionAndTargetTest bằng nút ▶
 * cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q1 [DỰ ĐOÁN] Annotation bản thân nó có tự thực thi logic không?
 *   Bắt đầu   : đọc method greet(String) trong Annotated bên dưới; nó chỉ trả "Hi " + name,
 *               không có dòng nào đọc annotation @LogCalls.
 *   Kiểm chứng: chạy q01_prediction; đặt breakpoint đầu greet() (Ctrl+N → gõ Ex01_RetentionAndTarget
 *               → mở class rồi tìm Annotated.greet), Debug test (Shift+F9), F8 step qua để thấy
 *               không có đoạn code nào ghi vào Annotated.CALL_LOG.
 *   Code      : không.
 *   Hoàn thành khi: q01_prediction xanh; giải thích được annotation chỉ là metadata — cần một
 *               "processor" (reflection, annotation processor, bytecode weaving) đọc và diễn giải
 *               nó thì mới sinh hành vi, tự thân annotation không chạy gì cả.
 *
 * Q2 [DỰ ĐOÁN] `RetentionPolicy.RUNTIME` có ý nghĩa gì?
 *   Bắt đầu   : điền Q2_RUNTIME_NOTE_VISIBLE (thay null) trước khi chạy test.
 *   Kiểm chứng: chạy q02_prediction; trong lúc debug, Alt+F8 (Evaluate Expression) và gõ
 *               Annotated.class.isAnnotationPresent(RuntimeNote.class), sau khi đã điền dự
 *               đoán, tự đọc giá trị boolean.
 *   Code      : không.
 *   Hoàn thành khi: q02_prediction xanh; giải thích được RUNTIME giữ annotation tới giai đoạn nào.
 *
 * Q3 [DỰ ĐOÁN] SOURCE và RUNTIME khác nhau thế nào?
 *   Bắt đầu   : điền Q3_SOURCE_NOTE_VISIBLE và Q3_CLASS_NOTE_VISIBLE.
 *   Kiểm chứng: chạy q03_prediction; sau đó mở Terminal (Alt+F12), build package
 *               (`.\mvnw.cmd -q -pl phase-01-core-advanced compile`) rồi chạy
 *               `javap -v -p phase-01-core-advanced\target\classes\phase01\d14_annotation\Ex01_RetentionAndTarget$Annotated.class`
 *               và tìm hai khối `RuntimeVisibleAnnotations` / `RuntimeInvisibleAnnotations`.
 *   Code      : không.
 *   Hoàn thành khi: q03_prediction xanh; đọc được output javap và giải thích được SOURCE,
 *               CLASS và RUNTIME mỗi cái còn lại ở đâu (.class và/hoặc runtime).
 *
 * Q4 [DỰ ĐOÁN + CODE] `@Target` dùng để làm gì?
 *   Bắt đầu   : điền Q4_FIELD_ONLY_ON_METHOD_COMPILES; bỏ comment đoạn mẫu ngay cạnh hằng số,
 *               xem IDE báo gì, rồi comment lại — sau khi đã điền dự đoán — để file biên dịch được.
 *   Kiểm chứng: chạy q04_prediction và q04_targetsOf; Ctrl+B (hoặc Ctrl+Click) trên Target trong
 *               khai báo @LogCalls để xem @Target khai báo method value() kiểu ElementType[].
 *   Code      : cài đặt targetsOf(Class) đọc annotation @Target bằng
 *               Class.getAnnotation(Target.class); không có @Target thì trả EnumSet rỗng.
 *   Hoàn thành khi: q04_* xanh; giải thích được @Target giới hạn annotation được đặt ở đâu
 *               (method, field, type...) và compiler kiểm tra ràng buộc này tại compile-time.
 */
public class Ex01_RetentionAndTarget {

    @Retention(RetentionPolicy.SOURCE)
    @interface SourceNote {
    }

    @Retention(RetentionPolicy.CLASS)
    @interface ClassNote {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface RuntimeNote {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface LogCalls {
    }

    @Target(ElementType.FIELD)
    @interface FieldOnly {
    }

    @SourceNote
    @ClassNote
    @RuntimeNote
    static final class Annotated {
        static final List<String> CALL_LOG = new ArrayList<>();

        @LogCalls
        String greet(String name) {
            return "Hi " + name;
        }
    }

    // Q1 — kịch bản: gọi greet("An") khi method có @LogCalls. CALL_LOG có thêm dòng không?
    static final Boolean Q1_ANNOTATION_ALONE_PRODUCES_LOG = null;

    // Q2 — kịch bản: method có @RuntimeNote. isAnnotationPresent lúc chạy có true không?
    static final Boolean Q2_RUNTIME_NOTE_VISIBLE = null;

    // Q3 — kịch bản: method có @SourceNote và @ClassNote. isAnnotationPresent lúc chạy
    // có true không? Đọc RetentionPolicy, rồi javap nếu cần, sau khi đã điền dự đoán.
    static final Boolean Q3_SOURCE_NOTE_VISIBLE = null;
    static final Boolean Q3_CLASS_NOTE_VISIBLE = null;

    // Q4 — mẫu: @FieldOnly (Target FIELD) đặt trên một method.
    // Bỏ comment hai dòng dưới, xem IDE báo gì, rồi comment lại — sau khi đã điền dự đoán:
    // @FieldOnly
    // void sampleMethodWithWrongTarget() {
    // }
    static final Compiles Q4_FIELD_ONLY_ON_METHOD_COMPILES = null;

    /**
     * Đọc {@code @Target} khai báo trên {@code annotationType} và trả về tập {@link ElementType}
     * mà annotation đó được cho phép áp dụng.
     *
     * @param annotationType kiểu annotation cần kiểm tra
     * @return tập {@link ElementType} lấy từ {@code @Target}; rỗng nếu {@code annotationType}
     *     không khai báo {@code @Target} (nghĩa là không có ràng buộc nào được ghi rõ)
     */
    static Set<ElementType> targetsOf(Class<? extends Annotation> annotationType) {
        throw new UnsupportedOperationException("TODO Q4");
    }
}
