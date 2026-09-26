package phase01.d13_reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    /** Fixture tĩnh (không có this$0) để kiểm tra thứ tự sắp xếp không phụ thuộc khai báo. */
    private static class ZebraThenApple {
        int zebra;
        int apple;
    }

    @Test
    @DisplayName("Q1 declaredFieldNames: SecretBox trả về [secret, size] theo thứ tự tăng dần")
    void q01_declaredFieldNames_secretBox_returnsSortedNonSyntheticFields() {
        List<String> names = Ex01_InspectAndInvoke.declaredFieldNames(SecretBox.class);

        assertEquals(List.of("secret", "size"), names,
                "Phải lấy đúng 2 field khai báo trực tiếp trong SecretBox, sắp xếp tăng dần theo tên.");
    }

    @Test
    @DisplayName("Q1 declaredFieldNames: kết quả luôn sắp xếp tăng dần bất kể thứ tự khai báo")
    void q01_declaredFieldNames_sortsAscendingRegardlessOfDeclarationOrder() {
        List<String> names = Ex01_InspectAndInvoke.declaredFieldNames(ZebraThenApple.class);

        assertEquals(List.of("apple", "zebra"), names,
                "Dù 'zebra' khai báo trước 'apple', kết quả phải sắp xếp tăng dần theo tên field.");
    }

    @Test
    @DisplayName("Q4 invokePrivate: gọi private method reveal(String) trả về đúng giá trị")
    void q04_invokePrivate_revealPrivateMethod_returnsValue() {
        SecretBox box = new SecretBox();

        Object result = Ex01_InspectAndInvoke.invokePrivate(box, "reveal", new Class<?>[] {String.class}, "x-");

        assertEquals("x-s3cr3t", result, "invokePrivate phải gọi được private method reveal và trả đúng kết quả.");
    }

    @Test
    @DisplayName("Q4 invokePrivate: method đích ném RuntimeException thì ném lại đúng exception đó")
    void q04_invokePrivate_targetThrowsRuntimeException_rethrowsSameException() {
        SecretBox box = new SecretBox();

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> Ex01_InspectAndInvoke.invokePrivate(box, "explode", new Class<?>[0]));

        assertEquals("boom", thrown.getMessage(),
                "Exception ném lại phải đúng là IllegalStateException(\"boom\") mà explode() ném ra, không bọc thêm.");
    }

    @Test
    @DisplayName("Q4 invokePrivate: tên method sai ném IllegalStateException có cause NoSuchMethodException")
    void q04_invokePrivate_wrongMethodName_throwsIllegalStateExceptionWithNoSuchMethodExceptionCause() {
        SecretBox box = new SecretBox();

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> Ex01_InspectAndInvoke.invokePrivate(box, "khongTonTai", new Class<?>[0]));

        assertEquals(NoSuchMethodException.class, thrown.getCause().getClass(),
                "Tên method sai phải bị NoSuchMethodException từ getDeclaredMethod, bọc trong IllegalStateException.");
    }

    @Test
    @DisplayName("Q4 dự đoán: setAccessible trên String.value ném exception gì")
    void q04_duDoan_setAccessibleOnStringValue_throwsInaccessibleObjectException() {
        String actualExceptionSimpleName;
        try {
            Field value = String.class.getDeclaredField("value");
            value.setAccessible(true);
            actualExceptionSimpleName = "none";
        } catch (ReflectiveOperationException | RuntimeException e) {
            actualExceptionSimpleName = e.getClass().getSimpleName();
        }

        assertPrediction("Q4_SETACCESSIBLE_ON_STRING_VALUE_EXCEPTION", actualExceptionSimpleName,
                Ex01_InspectAndInvoke.Q4_SETACCESSIBLE_ON_STRING_VALUE_EXCEPTION,
                "Module java.base không \"opens java.lang\" cho code của bạn — đặt breakpoint trong"
                        + " AccessibleObject.checkCanSetAccessible để xem điều kiện chặn.");
    }

    @Test
    @DisplayName("Q5 dự đoán: getDeclaredMethod với tên method sai vẫn biên dịch được")
    void q05_duDoan_typoMethodNameCompiles() {
        // Chính dòng gọi bên dưới là chứng cứ: nếu nó không biên dịch được, cả file test này
        // sẽ không build được. Tên "revael" là lỗi đánh máy cố ý của "reveal".
        Compiles actual = Compiles.YES;

        assertPrediction("Q5_TYPO_METHOD_NAME_COMPILES", actual, Ex01_InspectAndInvoke.Q5_TYPO_METHOD_NAME_COMPILES,
                "getDeclaredMethod nhận tên method dưới dạng String nên javac không kiểm được tên đó có tồn tại"
                        + " hay không; dòng gọi luôn biên dịch được dù tên sai.");
    }

    @Test
    @DisplayName("Q5 dự đoán: getDeclaredMethod với tên method sai ném lỗi gì lúc chạy")
    void q05_duDoan_typoMethodNameFailsAtRuntimeWith() {
        String actualExceptionSimpleName;
        try {
            SecretBox.class.getDeclaredMethod("revael", String.class);
            actualExceptionSimpleName = "none";
        } catch (ReflectiveOperationException e) {
            actualExceptionSimpleName = e.getClass().getSimpleName();
        }

        assertPrediction("Q5_TYPO_FAILS_AT_RUNTIME_WITH", actualExceptionSimpleName,
                Ex01_InspectAndInvoke.Q5_TYPO_FAILS_AT_RUNTIME_WITH,
                "getDeclaredMethod tìm method theo tên tại runtime; tên không khớp method nào thì ném gì?");
    }
}
