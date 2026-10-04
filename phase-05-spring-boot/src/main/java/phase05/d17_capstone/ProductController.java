package phase05.d17_capstone;

import org.springframework.web.bind.annotation.*;

/** B6: product read DTO; scope enforcement belongs to Task7. */
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final OrderService orders;
    public ProductController(OrderService orders) { this.orders = orders; }
    @GetMapping public PageResponse<ProductResponse> list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "id") String sort) {
        throw new UnsupportedOperationException("TODO B6");
    }
}
