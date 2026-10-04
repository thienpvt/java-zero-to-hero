package phase05.d17_capstone;

import java.math.BigDecimal;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phase05.d17_capstone.CapstoneFailures.Conflict;
import phase05.d17_capstone.CapstoneFailures.InvalidOrder;
import phase05.d17_capstone.CapstoneFailures.InvalidPage;

/**
 * B4/B7: gọi public service qua proxy; transaction chỉ chứa DB, không payment/network.
 * B3: paging SQL, owner predicate trước LIMIT, DTO immutable gồm snapshot thật.
 * Mở OrderServicePostgresTest/StockConcurrencyTest; debugger affected rows và rollback.
 */
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orders;
    private final ProductRepository products;
    public OrderServiceImpl(OrderRepository orders, ProductRepository products) {
        this.orders = orders;
        this.products = products;
    }
    @Override @Transactional public OrderResponse placeOrder(String subject, CreateOrderRequest request) {
        throw new UnsupportedOperationException("TODO B4");
    }
    @Override @Transactional(readOnly = true) public OrderResponse getOrder(String subject, long orderId) {
        throw new UnsupportedOperationException("TODO B4");
    }
    @Override @Transactional public OrderResponse cancelOrder(String subject, long orderId) {
        throw new UnsupportedOperationException("TODO B4");
    }
    @Override @Transactional(readOnly = true) public PageResponse<ProductResponse> productPage(int page, int size, String sort) {
        throw new UnsupportedOperationException("TODO B3");
    }
    @Override @Transactional(readOnly = true) public PageResponse<OrderResponse> orderPage(String subject, int page, int size) {
        throw new UnsupportedOperationException("TODO B3");
    }
    private static void validatePage(int page, int size) {
        throw new UnsupportedOperationException("TODO B3");
    }
    private static int pages(long count, int size) {
        throw new UnsupportedOperationException("TODO B3");
    }
    private static OrderResponse response(OrderRepository.Header order, List<OrderItemResponse> items) {
        throw new UnsupportedOperationException("TODO B4");
    }
}
