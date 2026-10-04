package phase05.d17_capstone;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(long id, String status, String currency, BigDecimal total,
                            List<OrderItemResponse> items, Instant createdAt) {}
