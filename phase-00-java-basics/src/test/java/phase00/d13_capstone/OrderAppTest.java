package phase00.d13_capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase00.d13_capstone.Order.Status;

@TestMethodOrder(MethodOrderer.MethodName.class)
class OrderAppTest {

    @Test
    @DisplayName("B4: render một đơn và list rỗng")
    void b04_renderOneOrderAndEmpty() {
        Order order = new Order("o1", "khách-An", LocalDate.of(2026, 9, 26), new BigDecimal("10.50"), Status.NEW);
        assertEquals("o1 | khách-An | 2026-09-26 | 10.50 | NEW", OrderApp.render(List.of(order)));
        assertEquals("", OrderApp.render(List.of()));
    }
}
