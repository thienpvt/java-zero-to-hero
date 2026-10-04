package phase05.d17_capstone;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import phase05.d17_capstone.CapstoneFailures.NotFound;

/** B3: native conditional stock; never mix managed Product dirty-flush with bulk updates. */
@Repository
public class ProductRepository {
    private final JdbcTemplate jdbc;
    public ProductRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    BigDecimal price(long id) {
        throw new UnsupportedOperationException("TODO B3");
    }
    /** Affected rows decide: 0 means insufficient stock, never a Java check-then-update. */
    boolean decrement(long id, int quantity) {
        throw new UnsupportedOperationException("TODO B3");
    }
    void restock(long id, int quantity) {
        throw new UnsupportedOperationException("TODO B3");
    }
    long count() {
        throw new UnsupportedOperationException("TODO B3");
    }
    /** {@code sort} already allowlisted by service; only fixed SQL fragments reach ORDER BY. */
    List<ProductResponse> page(int size, long offset, String sort) {
        throw new UnsupportedOperationException("TODO B3");
    }
}
