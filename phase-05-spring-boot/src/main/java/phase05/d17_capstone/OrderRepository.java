package phase05.d17_capstone;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;
import phase05.d17_capstone.CapstoneFailures.NotFound;

/** B3: projections and owner predicates execute in DB before pagination; no entity graph crosses HTTP. */
@Repository
public class OrderRepository {
    private final JdbcTemplate jdbc;
    public OrderRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    record Header(long id, String status, String currency, Instant createdAt) {}

    long customerId(String subject) {
        // SOLUTION-BEGIN throw B3
        return jdbc.query("select id from customers where subject=?", (rs, row) -> rs.getLong(1), subject)
                .stream().findFirst().orElseThrow(NotFound::new);
        // SOLUTION-END
    }
    Header insert(long customer) {
        // SOLUTION-BEGIN throw B3
        return jdbc.queryForObject("insert into orders(customer_id,status,currency,created_at) "
                + "values(?,'NEW','USD',current_timestamp) returning id,status,currency,created_at",
                (rs, row) -> header(rs), customer);
        // SOLUTION-END
    }
    void insertItem(long order, long product, int quantity, BigDecimal price) {
        // SOLUTION-BEGIN throw B3
        jdbc.update("insert into order_items(order_id,product_id,quantity,unit_price) values(?,?,?,?)",
                order, product, quantity, price);
        // SOLUTION-END
    }
    Header owned(String subject, long id) {
        // SOLUTION-BEGIN throw B3
        return jdbc.query("select o.id,o.status,o.currency,o.created_at from orders o join customers c "
                + "on c.id=o.customer_id where c.subject=? and o.id=?", (rs, row) -> header(rs), subject, id)
                .stream().findFirst().orElseThrow(NotFound::new);
        // SOLUTION-END
    }
    boolean cancel(String subject, long id) {
        // SOLUTION-BEGIN throw B3
        return jdbc.update("update orders set status='CANCELLED' where id=? and status='NEW' "
                + "and customer_id=(select id from customers where subject=?)", id, subject) == 1;
        // SOLUTION-END
    }
    long countOwned(String subject) {
        // SOLUTION-BEGIN throw B3
        return jdbc.query("select count(o.id) from customers c left join orders o on o.customer_id=c.id "
                + "where c.subject=? group by c.id", (rs, row) -> rs.getLong(1), subject)
                .stream().findFirst().orElseThrow(NotFound::new);
        // SOLUTION-END
    }
    List<Header> pageOwned(String subject, int size, long offset) {
        // SOLUTION-BEGIN throw B3
        return jdbc.query("select o.id,o.status,o.currency,o.created_at from orders o join customers c "
                + "on c.id=o.customer_id where c.subject=? order by o.created_at,o.id limit ? offset ?",
                (rs, row) -> header(rs), subject, size, offset);
        // SOLUTION-END
    }
    Map<Long, List<OrderItemResponse>> items(List<Long> ids) {
        // SOLUTION-BEGIN throw B3
        if (ids.isEmpty()) return Map.of(); // empty page: genuinely no item rows to read
        Map<Long, List<OrderItemResponse>> result = new HashMap<>();
        jdbc.query("select order_id,product_id,quantity,unit_price from order_items where order_id in ("
                + String.join(",", Collections.nCopies(ids.size(), "?")) + ") order by order_id,product_id",
                (RowCallbackHandler) rs -> result.computeIfAbsent(rs.getLong(1), ignored -> new ArrayList<>())
                        .add(new OrderItemResponse(rs.getLong(2), rs.getInt(3), rs.getBigDecimal(4))), ids.toArray());
        result.replaceAll((id, lines) -> List.copyOf(lines));
        return result;
        // SOLUTION-END
    }
    private static Header header(ResultSet rs) throws SQLException {
        // SOLUTION-BEGIN throw B3
        return new Header(rs.getLong("id"), rs.getString("status"), rs.getString("currency"),
                rs.getTimestamp("created_at").toInstant());
        // SOLUTION-END
    }
}
