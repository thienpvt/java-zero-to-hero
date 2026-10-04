package phase05.d04_mvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h2>Chủ đề 4 — HTTP request pipeline và Spring MVC</h2>
 * <p>Nguồn: {@code 05-spring-boot.md}, §4 Spring MVC và vòng đời HTTP request, Q1–Q5 và B04.</p>
 * <p>Tiên quyết: servlet filter, MVC interceptor, JSON và constructor injection.</p>
 * <p>Bắt đầu trong IntelliJ: mở {@code controller}, hoàn thiện B04 rồi chạy MockMvc test
 * cho JSON hợp lệ, JSON hỏng và media type không hỗ trợ.</p>
 * <p>Nghiên cứu: đặt breakpoint tại {@code OrderController.create} và service; đọc Spring
 * Framework Reference, Web MVC, Message Converters để theo dõi binding/serialization.</p>
 * <p>Test MVC tắt filter riêng cho bài học pipeline, không chứng minh xác thực của capstone.</p>
 * <p>Hoàn thành khi B04 dùng dependency được truyền vào, request lỗi bị từ chối trước service,
 * response hợp lệ là JSON và viết đủ ANSWER Q1–Q5. Câu hỏi viết không chấm tự động.</p>
 * Q1 [DỰ ĐOÁN] Filter và Spring MVC interceptor khác vị trí trong request pipeline ra sao?
 * Q2 [CODE] Deserialization khác serialization thế nào?
 * Q3 [TỰ TRẢ LỜI] Vì sao controller không nên gọi {@code new} để khởi tạo dependency?
 * Q4 [THÍ NGHIỆM] Binding lỗi có xảy ra trước khi service được gọi không?
 * Q5 [CODE] API nên trả gì khi client gửi JSON sai cú pháp hoặc media type không hỗ trợ?
 * ANSWER Q1:
 *
 * ANSWER Q2:
 *
 * ANSWER Q3:
 *
 * ANSWER Q4:
 *
 * ANSWER Q5:
 *
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
        @Lazy // Khởi tạo khi request đầu tiên dùng controller, không khi context khởi động.
        OrderController orderController(OrderService service) { return controller(service); }
    }

    /** B04: valid DTO is delegated; binding/media errors occur before service invocation. */
    public static OrderController controller(OrderService service) {
        throw new UnsupportedOperationException("TODO B04");
    }
}
