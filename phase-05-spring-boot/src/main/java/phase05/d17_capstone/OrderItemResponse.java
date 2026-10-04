package phase05.d17_capstone;

import java.math.BigDecimal;

/** Server-owned snapshot USD price. */
public record OrderItemResponse(long productId, int quantity, BigDecimal unitPrice) {}
