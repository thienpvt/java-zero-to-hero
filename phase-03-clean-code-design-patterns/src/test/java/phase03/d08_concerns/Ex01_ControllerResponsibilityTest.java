package phase03.d08_concerns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ControllerResponsibilityTest {

    private static final String HINT_Q1 =
            "Controller làm việc giao thức: nhận request, gọi nghiệp vụ, trả response.";
    private static final String HINT_Q3 =
            "Gửi email là một hệ ngoài khác, không thuộc trách nhiệm lưu trữ.";

    private static final Ex01_ControllerResponsibility.OrderController CONTROLLER =
            new Ex01_ControllerResponsibility.OrderController(
                    new Ex01_ControllerResponsibility.OrderApplicationService());

    @Test
    @DisplayName("Q1 dự đoán: controller chịu trách nhiệm gì")
    void q01_prediction() {
        assertPrediction("Q1_CONTROLLER_RESPONSIBILITY",
                Ex01_ControllerResponsibility.Duty.TRANSLATE_HTTP_TO_BUSINESS_CALL,
                Ex01_ControllerResponsibility.Q1_CONTROLLER_RESPONSIBILITY, HINT_Q1);
    }

    @Test
    @DisplayName("Q3 dự đoán: repository có gửi email")
    void q03_prediction() {
        assertPrediction("Q3_REPOSITORY_SENDS_EMAIL", false,
                Ex01_ControllerResponsibility.Q3_REPOSITORY_SENDS_EMAIL, HINT_Q3);
    }

    @Test
    @DisplayName("Q5: request hợp lệ trả 200")
    void q05_okResponse() {
        assertEquals(new Ex01_ControllerResponsibility.HttpResponse(200, "total:1500"),
                CONTROLLER.handle(new Ex01_ControllerResponsibility.HttpRequest("/orders", "1500")));
    }

    @Test
    @DisplayName("Q5: body trống trả 400")
    void q05_badRequestOnEmpty() {
        Ex01_ControllerResponsibility.HttpResponse response =
                CONTROLLER.handle(new Ex01_ControllerResponsibility.HttpRequest("/orders", " "));
        assertEquals(400, response.status());
        assertEquals("Body trống.", response.body());
    }
}
