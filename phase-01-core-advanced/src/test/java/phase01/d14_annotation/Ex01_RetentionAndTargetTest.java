package phase01.d14_annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase01.support.Predictions.assertPrediction;

import java.lang.annotation.ElementType;
import java.util.EnumSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d14_annotation.Ex01_RetentionAndTarget.Annotated;
import phase01.d14_annotation.Ex01_RetentionAndTarget.ClassNote;
import phase01.d14_annotation.Ex01_RetentionAndTarget.FieldOnly;
import phase01.d14_annotation.Ex01_RetentionAndTarget.LogCalls;
import phase01.d14_annotation.Ex01_RetentionAndTarget.RuntimeNote;
import phase01.d14_annotation.Ex01_RetentionAndTarget.SourceNote;
import phase01.support.Compiles;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_RetentionAndTargetTest {

    private static final String HINT_RETENTION =
            "SOURCE bị compiler bỏ hẳn khỏi .class; CLASS còn trong .class nhưng JVM không nạp vào "
                    + "runtime; chỉ RUNTIME đọc được bằng isAnnotationPresent lúc chạy.";

    @Test
    @DisplayName("Q1 dự đoán: annotation có tự thực thi logic không")
    void q01_prediction() {
        Annotated annotated = new Annotated();
        String result = annotated.greet("An");

        assertEquals("Hi An", result, "greet() chỉ nối chuỗi, không liên quan gì tới annotation.");
        assertPrediction("Q1_ANNOTATION_ALONE_PRODUCES_LOG",
                !Annotated.CALL_LOG.isEmpty(),
                Ex01_RetentionAndTarget.Q1_ANNOTATION_ALONE_PRODUCES_LOG,
                "Không có processor nào đọc @LogCalls nên gọi greet() không ghi gì vào CALL_LOG.");
    }

    @Test
    @DisplayName("Q2 dự đoán: RetentionPolicy.RUNTIME có ý nghĩa gì")
    void q02_prediction() {
        assertPrediction("Q2_RUNTIME_NOTE_VISIBLE",
                Annotated.class.isAnnotationPresent(RuntimeNote.class),
                Ex01_RetentionAndTarget.Q2_RUNTIME_NOTE_VISIBLE,
                "RUNTIME được giữ trong .class và JVM nạp vào runtime nên isAnnotationPresent thấy được.");
    }

    @Test
    @DisplayName("Q3 dự đoán: SOURCE và RUNTIME khác nhau ở khả năng đọc bằng reflection")
    void q03_prediction() {
        assertPrediction("Q3_SOURCE_NOTE_VISIBLE",
                Annotated.class.isAnnotationPresent(SourceNote.class),
                Ex01_RetentionAndTarget.Q3_SOURCE_NOTE_VISIBLE,
                HINT_RETENTION);
        assertPrediction("Q3_CLASS_NOTE_VISIBLE",
                Annotated.class.isAnnotationPresent(ClassNote.class),
                Ex01_RetentionAndTarget.Q3_CLASS_NOTE_VISIBLE,
                HINT_RETENTION);
    }

    @Test
    @DisplayName("Q4 dự đoán: đặt @FieldOnly lên method có biên dịch được không")
    void q04_prediction() {
        assertPrediction("Q4_FIELD_ONLY_ON_METHOD_COMPILES",
                Compiles.NO,
                Ex01_RetentionAndTarget.Q4_FIELD_ONLY_ON_METHOD_COMPILES,
                "@FieldOnly chỉ khai báo @Target(FIELD). javac báo "
                        + "\"annotation interface not applicable to this kind of declaration\".");
    }

    @Test
    @DisplayName("Q4 targetsOf: đọc đúng @Target khai báo trên annotation")
    void q04_targetsOf() {
        assertEquals(Set.of(ElementType.METHOD), Ex01_RetentionAndTarget.targetsOf(LogCalls.class),
                "LogCalls khai báo @Target(METHOD).");
        assertEquals(Set.of(ElementType.FIELD), Ex01_RetentionAndTarget.targetsOf(FieldOnly.class),
                "FieldOnly khai báo @Target(FIELD).");
        assertEquals(EnumSet.noneOf(ElementType.class), Ex01_RetentionAndTarget.targetsOf(RuntimeNote.class),
                "RuntimeNote không khai báo @Target nên targetsOf phải trả về tập rỗng.");
    }
}
