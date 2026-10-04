package phase05.d04_mvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h2>Chủ đề 4 — HTTP request pipeline và Spring MVC</h2>
 * <p>Nguồn: phase-05-spring-boot, chủ đề Request Pipeline.</p>
 * <p>Tiên quyết: servlet filter, MVC interceptor, JSON và controller. Trong IntelliJ,
 * chạy MockMvc test cho JSON hợp lệ, JSON hỏng và media type không hỗ trợ; quan sát
 * service chỉ được gọi sau bước binding thành công.</p>
 * <p>Controller nhận DTO và dependency qua constructor; test MVC này tắt filter riêng
 * cho bài học pipeline, không chứng minh xác thực của capstone.</p>
 * <p>Hoàn thành khi request lỗi bị từ chối trước service và response hợp lệ là JSON.</p>
 * <ul>
 *   <li>Q1 [DỰ ĐOÁN] Filter và Spring MVC interceptor khác vị trí trong request pipeline ra sao?</li>
 *   <li>Q2 [CODE] Deserialization khác serialization thế nào?</li>
 *   <li>Q3 [TỰ TRẢ LỜI] Vì sao controller không nên gọi {@code new} để khởi tạo dependency?</li>
 *   <li>Q4 [THÍ NGHIỆM] Binding lỗi có xảy ra trước khi service được gọi không?</li>
 *   <li>Q5 [CODE] API nên trả gì khi client gửi JSON sai cú pháp hoặc media type không hỗ trợ?</li>
 * </ul>
 * ANSWER Q1:
 * SOLUTION-BEGIN
 * Filter chạy trước DispatcherServlet; interceptor chạy quanh handler MVC sau mapping.
 * SOLUTION-END
 * ANSWER Q2:
 * SOLUTION-BEGIN
 * Deserialization đổi JSON thành object; serialization đổi object thành response JSON.
 * SOLUTION-END
 * ANSWER Q3:
 * SOLUTION-BEGIN
 * Gọi {@code new} ghép controller với implementation và bỏ qua container wiring/test seam.
 * SOLUTION-END
 * ANSWER Q4:
 * SOLUTION-BEGIN
 * Có; converter binding lỗi dừng request trước khi handler gọi service.
 * SOLUTION-END
 * ANSWER Q5:
 * SOLUTION-BEGIN
 * JSON sai cú pháp trả 400; media type không hỗ trợ trả 415, không gọi service.
 * SOLUTION-END
 */
public final class Ex01_RequestPipeline {
    private Ex01_RequestPipeline() {}

    public record CreateOrderRequest(String reference) {}
    public record CreateOrderResponse(String result) {}

    public interface OrderService {
        String place(CreateOrderRequest request);
    }

    @RestController
    public static class OrderController {
        private final OrderService service;
        public OrderController(OrderService service) { this.service = service; }

        @PostMapping(path = "/api/orders", consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
        public CreateOrderResponse create(@RequestBody CreateOrderRequest request) {
            return new CreateOrderResponse(service.place(request));
        }
    }

    @Configuration(proxyBeanMethods = false)
    public static class MvcConfiguration {
        @Bean
        OrderController orderController(OrderService service) { return new OrderController(service); }
    }

    /** B04: valid DTO is delegated; binding/media errors occur before service invocation. */
    public static OrderController controller(OrderService service) {
        // SOLUTION-BEGIN throw B04
        return new OrderController(service);
        // SOLUTION-END
    }
}
