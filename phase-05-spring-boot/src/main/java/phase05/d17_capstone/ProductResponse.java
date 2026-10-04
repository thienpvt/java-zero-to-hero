package phase05.d17_capstone;

import java.math.BigDecimal;

public record ProductResponse(long id, String name, BigDecimal price, int stock) {}
