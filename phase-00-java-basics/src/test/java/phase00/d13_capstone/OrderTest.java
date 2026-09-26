package phase00.d13_capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase00.d13_capstone.Order.Status;

@TestMethodOrder(MethodOrderer.MethodName.class)
class OrderTest {

    private static final LocalDate DAY = LocalDate.of(2026, 9, 26);

    @Test
    @DisplayName("B1: tổng tiền 0 hợp lệ, tiền âm và id trống thì không tạo đơn")
    void b01_rejectsNegativeTotalAndBlankId() {
        Order free = new Order("o1", "c1", DAY, BigDecimal.ZERO, Status.NEW);
        assertEquals(0, BigDecimal.ZERO.compareTo(free.total()));
        assertThrows(IllegalArgumentException.class,
                () -> new Order("o1", "c1", DAY, new BigDecimal("-0.01"), Status.NEW));
        assertThrows(IllegalArgumentException.class,
                () -> new Order("  ", "c1", DAY, BigDecimal.ONE, Status.NEW));
    }

    @Test
    @DisplayName("B1: NEW sang PAID được, NEW sang DONE thì không")
    void b01_statusTransition() {
        Order order = new Order("o1", "c1", DAY, new BigDecimal("10.00"), Status.NEW);
        order.changeStatus(Status.PAID);
        assertEquals(Status.PAID, order.status());
        Order fresh = new Order("o2", "c1", DAY, new BigDecimal("10.00"), Status.NEW);
        assertThrows(IllegalStateException.class, () -> fresh.changeStatus(Status.DONE));
        assertEquals(Status.NEW, fresh.status());
    }
}
