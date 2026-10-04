package phase05.d17_capstone;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/** Provided JPA mapping/fields; price is USD, stock belongs to products. */
@Entity
@Table(name = "products")
public class Product {
    @Id public Long id;
    @Column(nullable = false, columnDefinition = "text") public String name;
    @Column(nullable = false, precision = 19, scale = 2) public BigDecimal price;
    @Column(nullable = false) public int stock;
}
