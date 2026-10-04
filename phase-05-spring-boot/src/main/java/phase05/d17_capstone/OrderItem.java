package phase05.d17_capstone;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/** Provided mapping/identity boilerplate; unitPrice is immutable-in-use USD snapshot, not product lookup. */
@Entity
@Table(name = "order_items")
@IdClass(OrderItem.Key.class)
public class OrderItem {
    @Id @Column(name = "order_id", nullable = false) public Long orderId;
    @Id @Column(name = "product_id", nullable = false) public Long productId;
    @Column(nullable = false) public int quantity;
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2) public BigDecimal unitPrice;

    public static class Key implements Serializable {
        public Long orderId;
        public Long productId;
        public Key() {}
        public Key(Long orderId, Long productId) { this.orderId = orderId; this.productId = productId; }
        @Override public boolean equals(Object other) {
            return other instanceof Key key && Objects.equals(orderId, key.orderId)
                    && Objects.equals(productId, key.productId);
        }
        @Override public int hashCode() { return Objects.hash(orderId, productId); }
    }
}
