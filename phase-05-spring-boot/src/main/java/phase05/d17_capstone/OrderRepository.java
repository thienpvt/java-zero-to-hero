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
        throw new UnsupportedOperationException("TODO B3");
    }
    Header insert(long customer) {
        throw new UnsupportedOperationException("TODO B3");
    }
    void insertItem(long order, long product, int quantity, BigDecimal price) {
        throw new UnsupportedOperationException("TODO B3");
    }
    Header owned(String subject, long id) {
        throw new UnsupportedOperationException("TODO B3");
    }
    boolean cancel(String subject, long id) {
        throw new UnsupportedOperationException("TODO B3");
    }
    long countOwned(String subject) {
        throw new UnsupportedOperationException("TODO B3");
    }
    List<Header> pageOwned(String subject, int size, long offset) {
        throw new UnsupportedOperationException("TODO B3");
    }
    Map<Long, List<OrderItemResponse>> items(List<Long> ids) {
        throw new UnsupportedOperationException("TODO B3");
    }
    private static Header header(ResultSet rs) throws SQLException {
        throw new UnsupportedOperationException("TODO B3");
    }
}
