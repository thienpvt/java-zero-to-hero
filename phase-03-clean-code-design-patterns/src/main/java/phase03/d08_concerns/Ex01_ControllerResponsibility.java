package phase03.d08_concerns;

/**
 * Separation of Concerns — Bài 1: controller làm gì
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 8 (Separation of Concerns), câu 1, 3, 5.
 * Cần làm trước: d07_composition.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_ControllerResponsibilityTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Controller nên chịu trách nhiệm gì?
 *   Bắt đầu   : điền Q1_CONTROLLER_RESPONSIBILITY bằng một giá trị của {@code Duty}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu dịch HTTP sang lời gọi nghiệp vụ.
 * <p>
 * Q3 [DỰ ĐOÁN] Repository có nên gửi email không?
 *   Bắt đầu   : điền Q3_REPOSITORY_SENDS_EMAIL.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nêu việc gửi thông báo là trách nhiệm khác.
 * <p>
 * Q5 [CODE] Separation of Concerns giúp testability thế nào?
 *   Bắt đầu   : cài {@code OrderController.handle}. Chỉ đọc body, gọi service, đóng gói response.
 *               Không tính giá, không validate nghiệp vụ trong method này.
 *   Kiểm chứng: chạy q05_okResponse và q05_badRequestOnEmpty.
 *   Hoàn thành khi: hai test xanh; ANSWER Q5 nêu service test được mà không cần HTTP.
 */
public class Ex01_ControllerResponsibility {

    /** Trách nhiệm của controller. */
    public enum Duty {
        CALCULATE_PRICE,
        TRANSLATE_HTTP_TO_BUSINESS_CALL,
        PERSIST_AND_EMAIL
    }

    public record HttpRequest(String path, String body) {
    }

    public record HttpResponse(int status, String body) {
    }

    // Q1 — controller chịu trách nhiệm gì.
    static final Duty Q1_CONTROLLER_RESPONSIBILITY = null;

    // Q3 — repository có nên gửi email.
    static final Boolean Q3_REPOSITORY_SENDS_EMAIL = null;

    /** Cho sẵn: nghiệp vụ thuần, không biết HTTP. */
    public static final class OrderApplicationService {

        public long place(String body) {
            if (body == null || body.isBlank()) {
                throw new IllegalArgumentException("Body trống.");
            }
            return Long.parseLong(body.strip());
        }
    }

    /** Chỉ dịch HTTP sang lời gọi nghiệp vụ, không chứa chính sách nghiệp vụ. */
    public static final class OrderController {

        private final OrderApplicationService service;

        public OrderController(OrderApplicationService service) {
            this.service = service;
        }

        public HttpResponse handle(HttpRequest request) {
            throw new UnsupportedOperationException("TODO Q5");
        }
    }
}

/* ANSWER Q1:
 *
 */

/* ANSWER Q3:
 *
 */

/* ANSWER Q5:
 *
 */
