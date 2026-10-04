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
        // SOLUTION-BEGIN throw B4
        // Recheck domain invariants at service boundary; record constructors also guard HTTP input.
        if (request == null || request.items() == null || request.items().isEmpty()) throw new InvalidOrder();
        Set<Long> ids = new HashSet<>();
        for (var line : request.items()) {
            if (line == null || line.productId() <= 0 || line.quantity() < 1 || line.quantity() > 1000
                    || !ids.add(line.productId())) throw new InvalidOrder();
        }
        // Sorted copy: every transaction locks product rows in the same order, avoiding deadlock.
        var lines = request.items().stream().sorted(Comparator.comparingLong(LineRequest::productId)).toList();
        var order = orders.insert(orders.customerId(subject));
        List<OrderItemResponse> snapshots = new ArrayList<>();
        for (var line : lines) {
            var price = products.price(line.productId());
            if (!products.decrement(line.productId(), line.quantity())) throw new Conflict();
            orders.insertItem(order.id(), line.productId(), line.quantity(), price);
            snapshots.add(new OrderItemResponse(line.productId(), line.quantity(), price));
        }
        return response(order, snapshots);
        // SOLUTION-END
    }
    @Override @Transactional(readOnly = true) public OrderResponse getOrder(String subject, long orderId) {
        // SOLUTION-BEGIN throw B4
        var order = orders.owned(subject, orderId);
        return response(order, orders.items(List.of(orderId)).getOrDefault(orderId, List.of()));
        // SOLUTION-END
    }
    @Override @Transactional public OrderResponse cancelOrder(String subject, long orderId) {
        // SOLUTION-BEGIN throw B4
        if (!orders.cancel(subject, orderId)) {
            orders.owned(subject, orderId); // foreign/missing: NotFound; owned but not NEW: Conflict
            throw new Conflict();
        }
        // Only the guarded-update winner reaches restock, inside the same transaction.
        var items = orders.items(List.of(orderId)).getOrDefault(orderId, List.of());
        for (var item : items) products.restock(item.productId(), item.quantity());
        return response(orders.owned(subject, orderId), items);
        // SOLUTION-END
    }
    @Override @Transactional(readOnly = true) public PageResponse<ProductResponse> productPage(int page, int size, String sort) {
        // SOLUTION-BEGIN throw B3
        validatePage(page, size);
        if (sort == null || !Set.of("id", "name", "price").contains(sort)) throw new InvalidPage();
        long count = products.count();
        return new PageResponse<>(List.copyOf(products.page(size, (long) page * size, sort)), page, size, count, pages(count, size));
        // SOLUTION-END
    }
    @Override @Transactional(readOnly = true) public PageResponse<OrderResponse> orderPage(String subject, int page, int size) {
        // SOLUTION-BEGIN throw B3
        validatePage(page, size);
        long count = orders.countOwned(subject);
        var headers = orders.pageOwned(subject, size, (long) page * size);
        var items = orders.items(headers.stream().map(OrderRepository.Header::id).toList());
        return new PageResponse<>(headers.stream().map(order -> response(order, items.getOrDefault(order.id(), List.of()))).toList(),
                page, size, count, pages(count, size));
        // SOLUTION-END
    }
    private static void validatePage(int page, int size) {
        // SOLUTION-BEGIN throw B3
        if (page < 0 || size < 1 || size > 100) throw new InvalidPage();
        // SOLUTION-END
    }
    private static int pages(long count, int size) {
        // SOLUTION-BEGIN throw B3
        return Math.toIntExact(count / size + (count % size == 0 ? 0 : 1));
        // SOLUTION-END
    }
    private static OrderResponse response(OrderRepository.Header order, List<OrderItemResponse> items) {
        // SOLUTION-BEGIN throw B4
        var total = items.stream().map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
        return new OrderResponse(order.id(), order.status(), order.currency(), total, List.copyOf(items), order.createdAt());
        // SOLUTION-END
    }
}
