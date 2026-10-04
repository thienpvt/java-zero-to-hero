package phase05.d17_capstone;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

/** B2: provided HTTP validation metadata; constructor also guards non-MVC callers. */
public record LineRequest(@Positive long productId, @Min(1) @Max(1000) int quantity) {
    public LineRequest {
        throw new UnsupportedOperationException("TODO B2");
    }
}
