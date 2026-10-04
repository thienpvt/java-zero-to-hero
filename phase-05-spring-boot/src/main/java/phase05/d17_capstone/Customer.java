package phase05.d17_capstone;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Provided JPA mapping/fields; API exposes DTOs, never entities. */
@Entity
@Table(name = "customers")
public class Customer {
    @Id public Long id;
    @Column(nullable = false, unique = true, columnDefinition = "text") public String subject;
}
