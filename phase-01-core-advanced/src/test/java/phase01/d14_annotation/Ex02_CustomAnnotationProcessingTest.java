package phase01.d14_annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d14_annotation.Ex02_CustomAnnotationProcessing.Clock2;
import phase01.d14_annotation.Ex02_CustomAnnotationProcessing.Mailer;
import phase01.d14_annotation.Ex02_CustomAnnotationProcessing.NotAComponent;
import phase01.d14_annotation.Ex02_CustomAnnotationProcessing.SignupForm;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_CustomAnnotationProcessingTest {

    @Test
    @DisplayName("Q5 code: chỉ tạo instance cho class có @Component, giữ đúng thứ tự")
    void q05_instantiateComponents_returnsOnlyComponentsInOrder() {
        List<Class<?>> candidates = List.of(Mailer.class, NotAComponent.class, Clock2.class);

        Map<Class<?>, Object> result = Ex02_CustomAnnotationProcessing.instantiateComponents(candidates);

        assertEquals(List.of(Mailer.class, Clock2.class), List.copyOf(result.keySet()),
                "Chỉ giữ class có @Component (Mailer, Clock2), bỏ NotAComponent, đúng thứ tự candidates.");
        assertInstanceOf(Mailer.class, result.get(Mailer.class),
                "Value ứng với khoá Mailer.class phải là instance thật của Mailer.");
        assertInstanceOf(Clock2.class, result.get(Clock2.class),
                "Value ứng với khoá Clock2.class phải là instance thật của Clock2.");
    }

    @Test
    @DisplayName("Q6 code: form hợp lệ trả về danh sách lỗi rỗng")
    void q06_validate_validForm_returnsEmpty() {
        SignupForm form = new SignupForm("an123", "an@example.com", null);

        assertEquals(List.of(), Ex02_CustomAnnotationProcessing.validate(form),
                "username hợp lệ (<=10 ký tự, không blank), email không blank thì không có lỗi.");
    }

    @Test
    @DisplayName("Q6 code: username blank và email null báo lỗi, sắp theo tên field")
    void q06_validate_blankUsernameAndNullEmail_returnsSortedMessages() {
        SignupForm form = new SignupForm(" ", null, "bất kỳ");

        assertEquals(
                List.of("email: không được để trống", "username: không được để trống"),
                Ex02_CustomAnnotationProcessing.validate(form),
                "Cả hai field vi phạm @NotBlank; danh sách phải sắp theo tên field (email trước username).");
    }

    @Test
    @DisplayName("Q6 code: username dài hơn 10 ký tự báo lỗi MaxLength")
    void q06_validate_usernameLongerThanTen_returnsMaxLengthMessage() {
        SignupForm form = new SignupForm("12345678901", "an@example.com", null);

        assertEquals(List.of("username: dài tối đa 10 ký tự"),
                Ex02_CustomAnnotationProcessing.validate(form),
                "username dài 11 ký tự vượt @MaxLength(10) nên phải báo đúng số ký tự cho phép.");
    }
}
