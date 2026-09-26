package phase01.d13_reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import phase01.support.Compiles;

/**
 * Reflection — Bài 1: Soi và gọi field/method bằng reflection
 *
 * Nguồn: 01-java-core-advanced.md, mục 13 (Reflection), câu 1, 4, 5.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_InspectAndInvokeTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q1 [CODE] Reflection giải quyết vấn đề gì?
 *   Bắt đầu   : đọc class SecretBox bên dưới; cài đặt declaredFieldNames(Class&lt;?&gt;).
 *   Kiểm chứng: Ctrl+N → Class → Ctrl+F12 → getDeclaredFields, Ctrl+Q trên Field để đọc
 *               Javadoc isSynthetic(); chạy q01_* để tự thấy kết quả là [secret, size].
 *   Code      : static List&lt;String&gt; declaredFieldNames(Class&lt;?&gt; type) — lấy tên các
 *               field khai báo trực tiếp (getDeclaredFields, không tính field kế thừa), bỏ
 *               field synthetic (isSynthetic()), sắp xếp tăng dần theo tên.
 *   Hoàn thành khi: test q01_* xanh; giải thích được reflection cho phép đọc metadata
 *               (field/method/constructor) của một Class tại runtime dựa trên chính đối
 *               tượng Class, mà code viết tay gọi thẳng field/method không làm được khi
 *               type cụ thể chỉ biết lúc chạy (ví dụ framework nhận Class từ cấu hình).
 *
 * Q4 [DỰ ĐOÁN + CODE] Có thể gọi private method bằng reflection không?
 *   Bắt đầu   : cài đặt invokePrivate(...); điền
 *               Q4_SETACCESSIBLE_ON_STRING_VALUE_EXCEPTION (chạy thử đoạn code trong
 *               main() để thấy tên exception thật).
 *   Kiểm chứng: đặt breakpoint tại AccessibleObject.checkCanSetAccessible (Ctrl+N →
 *               AccessibleObject → Ctrl+F12 → checkCanSetAccessible), Debug
 *               q04_setAccessibleOnStringValue_throwsInaccessibleObjectException, F7 để
 *               thấy điều kiện nào false khiến exception bị ném khi caller là code của
 *               bạn nhưng declaringClass (String) nằm trong module java.base không mở.
 *   Code      : static Object invokePrivate(Object target, String methodName,
 *               Class&lt;?&gt;[] parameterTypes, Object... args) — getDeclaredMethod,
 *               setAccessible(true), invoke; nếu InvocationTargetException có cause là
 *               RuntimeException thì ném lại chính cause đó (không bọc thêm); mọi
 *               ReflectiveOperationException khác (kể cả InvocationTargetException với
 *               cause không phải RuntimeException) → bọc trong IllegalStateException,
 *               giữ nguyên cause gốc.
 *   Hoàn thành khi: các test q04_* xanh; giải thích được vì sao module java.base không
 *               "opens java.lang" cho code ứng dụng (từ JDK 9, JEP 261) nên setAccessible
 *               trên field private của String ném InaccessibleObjectException, còn field/
 *               method private của class tự viết (không khai báo module hạn chế) thì gọi
 *               được bình thường.
 *
 * Q5 [DỰ ĐOÁN] Reflection ảnh hưởng compile-time checking ra sao?
 *   Bắt đầu   : điền Q5_TYPO_METHOD_NAME_COMPILES = Compiles.YES và
 *               Q5_TYPO_FAILS_AT_RUNTIME_WITH; đọc chú thích ngay dưới hai hằng số.
 *   Kiểm chứng: chạy q05_* để thấy getDeclaredMethod("revael", String.class) — tên method
 *               bị viết sai (đúng là "reveal") — biên dịch hoàn toàn bình thường vì tham
 *               số chỉ là một String, và lỗi NoSuchMethodException chỉ xuất hiện lúc chạy.
 *   Hoàn thành khi: test q05_* xanh; giải thích được getDeclaredMethod/getDeclaredField
 *               nhận tên dưới dạng String nên javac không thể kiểm tra method/field đó có
 *               tồn tại hay không, khác với lời gọi trực tiếp box.reveal(...) — trường hợp
 *               này javac báo lỗi ngay tại thời điểm biên dịch nếu method không tồn tại.
 */
public class Ex01_InspectAndInvoke {

    static final class SecretBox {
        private String secret = "s3cr3t";
        public int size = 3;

        private String reveal(String prefix) {
            return prefix + secret;
        }

        private void explode() {
            throw new IllegalStateException("boom");
        }
    }

    // Q5 — mẫu: SecretBox.class.getDeclaredMethod("revael", String.class) (đúng tên method
    // là "reveal", ở đây cố tình viết sai "revael"). Dòng gọi thật nằm trong test q05_*;
    // ở đây chỉ ghi lại hai hằng số dự đoán.
    static final Compiles Q5_TYPO_METHOD_NAME_COMPILES = Compiles.YES; // SOLUTION-VALUE
    static final String Q5_TYPO_FAILS_AT_RUNTIME_WITH = "NoSuchMethodException"; // SOLUTION-VALUE

    // Q4 — kịch bản: String.class.getDeclaredField("value").setAccessible(true).
    static final String Q4_SETACCESSIBLE_ON_STRING_VALUE_EXCEPTION = "InaccessibleObjectException"; // SOLUTION-VALUE

    /**
     * Trả về tên các field khai báo trực tiếp trong {@code type} (không tính field kế
     * thừa từ lớp cha), bỏ field synthetic, sắp xếp tăng dần theo tên.
     *
     * @throws NullPointerException nếu {@code type} là {@code null}
     */
    static List<String> declaredFieldNames(Class<?> type) {
        // SOLUTION-BEGIN throw Q1
        List<String> names = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            if (!field.isSynthetic()) {
                names.add(field.getName());
            }
        }
        names.sort(Comparator.naturalOrder());
        return names;
        // SOLUTION-END
    }

    /**
     * Gọi method (kể cả private) tên {@code methodName} nhận tham số kiểu
     * {@code parameterTypes} trên {@code target}, truyền {@code args}, bằng reflection.
     *
     * @throws IllegalStateException nếu không tìm được/gọi được method (ví dụ tên sai,
     *                                sai chữ ký) hoặc method đích ném ra exception không
     *                                phải {@link RuntimeException}; cause gốc được giữ lại
     * @throws RuntimeException      chính exception (không bọc thêm) mà method đích ném
     *                                ra, nếu đó là một {@link RuntimeException}
     */
    static Object invokePrivate(Object target, String methodName, Class<?>[] parameterTypes, Object... args) {
        // SOLUTION-BEGIN throw Q4
        try {
            Method method = target.getClass().getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException runtimeCause) {
                throw runtimeCause;
            }
            throw new IllegalStateException(
                    "Method '" + methodName + "' ném lỗi không phải RuntimeException khi invoke.", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Không thể gọi method '" + methodName + "' bằng reflection.", e);
        }
        // SOLUTION-END
    }

    public static void main(String[] args) {
        SecretBox box = new SecretBox();
        System.out.println("declaredFieldNames(SecretBox) = " + declaredFieldNames(SecretBox.class));
        System.out.println(
                "invokePrivate(reveal) = " + invokePrivate(box, "reveal", new Class<?>[] {String.class}, "x-"));
        try {
            invokePrivate(box, "explode", new Class<?>[0]);
        } catch (IllegalStateException e) {
            System.out.println(
                    "invokePrivate(explode) ném lại chính exception gốc: " + e.getClass().getSimpleName()
                            + "(\"" + e.getMessage() + "\")");
        }
        try {
            String.class.getDeclaredField("value").setAccessible(true);
            System.out.println("setAccessible trên String.value: không ném gì (không mong đợi trên JDK module).");
        } catch (ReflectiveOperationException | RuntimeException e) {
            System.out.println("setAccessible trên String.value ném: " + e.getClass().getSimpleName());
        }
    }
}
