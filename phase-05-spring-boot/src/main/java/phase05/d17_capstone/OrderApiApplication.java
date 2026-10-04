package phase05.d17_capstone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/** App shell only; no topic exercise configurations enter capstone scan. */
@SpringBootApplication(scanBasePackages = "phase05.d17_capstone")
@EntityScan(basePackageClasses = Customer.class)
@EnableJpaRepositories(basePackageClasses = OrderApiApplication.class)
public class OrderApiApplication {
    public static void main(String[] args) { SpringApplication.run(OrderApiApplication.class, args); }
}
