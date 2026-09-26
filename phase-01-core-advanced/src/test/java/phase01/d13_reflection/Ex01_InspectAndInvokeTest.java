package phase01.d13_reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase01.support.Predictions.assertPrediction;

import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d13_reflection.Ex01_InspectAndInvoke.SecretBox;
import phase01.support.Compiles;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_InspectAndInvokeTest {

    @Test
    @DisplayName("Q1 declaredFieldNames: liệt kê field private lẫn public, sắp xếp tăng dần")
    void q01_declaredFieldNames_listsAllFields() {
        List<String> names = Ex01_InspectAndInvoke.declaredFieldNames(SecretBox.class);

        assertEquals(List.of("secret", "size"), names,
                "Phải liệt kê đủ field private (secret) và public (size), sắp xếp theo tên.");
    }

    @Test
    @DisplayName("Q4 invokePrivate: gọi được private method reveal và trả đúng kết quả")
    void q04_invokePrivate_callsPrivateMethod() {
        SecretBox box = new SecretBox();

        Object result = Ex01_InspectAndInvoke.invokePrivate(box, "reveal", new Class<?>[] {String.class}, "x-");

        assertEquals("x-s3cr3t", result, "invokePrivate phải trả đúng giá trị mà method private trả về.");
    }

    @Test
    @DisplayName("Q4 invokePrivate: RuntimeException do chính method ném ra được giữ nguyên")
    void q04_invokePrivate_rethrowsRuntimeExceptionCause() {
        SecretBox box = new SecretBox();

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> Ex01_InspectAndInvoke.invokePrivate(box, "explode", new Class<?>[0]));

        assertEquals("boom", ex.getMessage(), "Message phải giữ nguyên message gốc \"boom\" của explode().");
    }

    @Test
    @DisplayName("Q4 invokePrivate: tên method sai bị bọc thành IllegalStateException với cause NoSuchMethodException")
    void q04_invokePrivate_wrapsMissingMethodName() {
        SecretBox box = new SecretBox();

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> Ex01_InspectAndInvoke.invokePrivate(box, "revael", new Class<?>[] {String.class}, "x-"));

        assertTrue(ex.getCause() instanceof NoSuchMethodException,
                "Cause phải là NoSuchMethodException khi tên method không tồn tại, cause hiện tại: " + ex.getCause());
    }

    @Test
    @DisplayName("Q4 dự đoán: setAccessible trên String.value bị module java.base chặn")
    void q04_setAccessibleOnStringValue_isBlocked() {
        String actual;
        try {
            Field value = String.class.getDeclaredField("value");
            value.setAccessible(true);
            actual = "KHONG_NEM_GI";
        } catch (ReflectiveOperationException | RuntimeException e) {
            actual = e.getClass().getSimpleName();
        }

        assertPrediction("Q4_SETACCESSIBLE_ON_STRING_VALUE_EXCEPTION", actual,
                Ex01_InspectAndInvoke.Q4_SETACCESSIBLE_ON_STRING_VALUE_EXCEPTION,
                "module java.base không opens java.lang cho code của bạn; xem Javadoc"
                        + " AccessibleObject.setAccessible.");
    }

    @Test
    @DisplayName("Q5 dự đoán: gọi getDeclaredMethod với tên gõ sai biên dịch được")
    void q05_typoMethodName_compilesButFailsAtRuntime() {
        assertPrediction("Q5_TYPO_METHOD_NAME_COMPILES", Compiles.YES,
                Ex01_InspectAndInvoke.Q5_TYPO_METHOD_NAME_COMPILES,
                "getDeclaredMethod nhận String cho tên method, compiler không biết method này có tồn tại hay không.");

        String actual;
        try {
            SecretBox.class.getDeclaredMethod("revael", String.class);
            actual = "KHONG_NEM_GI";
        } catch (NoSuchMethodException e) {
            actual = e.getClass().getSimpleName();
        }

        assertPrediction("Q5_TYPO_FAILS_AT_RUNTIME_WITH", actual,
                Ex01_InspectAndInvoke.Q5_TYPO_FAILS_AT_RUNTIME_WITH,
                "Tên method sai chỉ lộ ra khi JVM tìm method lúc chạy, không phải lúc javac biên dịch.");
    }
}
