package phase05.d17_capstone;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** B2: customer identity, price and currency never come from request body. */
public record CreateOrderRequest(@NotEmpty List<@NotNull @Valid LineRequest> items) {
    public CreateOrderRequest {
        throw new UnsupportedOperationException("TODO B2");
    }
}
