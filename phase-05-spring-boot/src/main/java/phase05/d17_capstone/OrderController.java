package phase05.d17_capstone;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

/** B6: trusted JWT principal only. Task7 owns verification/filter/scopes, not this controller. */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orders;
    public OrderController(OrderService orders) { this.orders = orders; }
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> create(JwtAuthenticationToken principal, @Valid @RequestBody CreateOrderRequest request) {
        // SOLUTION-BEGIN throw B6
        var created = orders.placeOrder(principal.getToken().getSubject(), request);
        return ResponseEntity.created(java.net.URI.create("/api/orders/" + created.id())).body(created);
        // SOLUTION-END
    }
    @GetMapping("/{id}") public OrderResponse get(JwtAuthenticationToken principal, @PathVariable long id) {
        // SOLUTION-BEGIN throw B6
        return orders.getOrder(principal.getToken().getSubject(), id);
        // SOLUTION-END
    }
    @PostMapping("/{id}/cancel") public OrderResponse cancel(JwtAuthenticationToken principal, @PathVariable long id) {
        // SOLUTION-BEGIN throw B6
        return orders.cancelOrder(principal.getToken().getSubject(), id);
        // SOLUTION-END
    }
    @GetMapping public PageResponse<OrderResponse> list(JwtAuthenticationToken principal,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        // SOLUTION-BEGIN throw B6
        return orders.orderPage(principal.getToken().getSubject(), page, size);
        // SOLUTION-END
    }
}
