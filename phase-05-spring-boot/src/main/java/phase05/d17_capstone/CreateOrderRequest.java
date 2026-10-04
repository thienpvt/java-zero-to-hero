package phase05.d17_capstone;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** B2: customer identity, price and currency never come from request body. */
public record CreateOrderRequest(@NotEmpty List<@NotNull @Valid LineRequest> items) {
    public CreateOrderRequest {
        // SOLUTION-BEGIN throw B2
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("items must not be empty");
        if (items.stream().anyMatch(java.util.Objects::isNull)) {
            throw new IllegalArgumentException("items must not contain null");
        }
        items = List.copyOf(items);
        // SOLUTION-END
    }
}
