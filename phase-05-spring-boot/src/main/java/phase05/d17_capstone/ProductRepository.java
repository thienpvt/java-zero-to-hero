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
        // SOLUTION-BEGIN throw B3
        return jdbc.query("select price from products where id=?", (rs, row) -> rs.getBigDecimal(1), id)
                .stream().findFirst().orElseThrow(NotFound::new);
        // SOLUTION-END
    }
    /** Affected rows decide: 0 means insufficient stock, never a Java check-then-update. */
    boolean decrement(long id, int quantity) {
        // SOLUTION-BEGIN throw B3
        return jdbc.update("update products set stock=stock-? where id=? and stock>=?", quantity, id, quantity) == 1;
        // SOLUTION-END
    }
    void restock(long id, int quantity) {
        // SOLUTION-BEGIN throw B3
        // FK guarantees the row; 0 rows is an invariant break (500), integer overflow fails in PostgreSQL.
        if (jdbc.update("update products set stock=stock+? where id=?", quantity, id) != 1)
            throw new IllegalStateException("product row missing during restock");
        // SOLUTION-END
    }
    long count() {
        // SOLUTION-BEGIN throw B3
        return jdbc.queryForObject("select count(*) from products", Long.class);
        // SOLUTION-END
    }
    /** {@code sort} already allowlisted by service; only fixed SQL fragments reach ORDER BY. */
    List<ProductResponse> page(int size, long offset, String sort) {
        // SOLUTION-BEGIN throw B3
        String ordering = switch (sort) {
            case "id" -> "id";
            case "name" -> "name,id";
            case "price" -> "price,id";
            default -> throw new IllegalArgumentException("unchecked sort");
        };
        return jdbc.query("select id,name,price,stock from products order by " + ordering + " limit ? offset ?",
                (rs, row) -> new ProductResponse(rs.getLong(1), rs.getString(2), rs.getBigDecimal(3), rs.getInt(4)), size, offset);
        // SOLUTION-END
    }
}
