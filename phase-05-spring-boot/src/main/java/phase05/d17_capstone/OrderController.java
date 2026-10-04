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
        throw new UnsupportedOperationException("TODO B6");
    }
    @GetMapping("/{id}") public OrderResponse get(JwtAuthenticationToken principal, @PathVariable long id) {
        throw new UnsupportedOperationException("TODO B6");
    }
    @PostMapping("/{id}/cancel") public OrderResponse cancel(JwtAuthenticationToken principal, @PathVariable long id) {
        throw new UnsupportedOperationException("TODO B6");
    }
    @GetMapping public PageResponse<OrderResponse> list(JwtAuthenticationToken principal,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        throw new UnsupportedOperationException("TODO B6");
    }
}
