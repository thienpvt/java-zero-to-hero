package phase05.d17_capstone;

/** Frozen Tasks 6–7 contract. subject comes only from authenticated principal mapping. */
public interface OrderService {
    OrderResponse placeOrder(String subject, CreateOrderRequest request);
    OrderResponse getOrder(String subject, long orderId);
    OrderResponse cancelOrder(String subject, long orderId);
    PageResponse<ProductResponse> productPage(int page, int size, String sort);
    PageResponse<OrderResponse> orderPage(String subject, int page, int size);
}
