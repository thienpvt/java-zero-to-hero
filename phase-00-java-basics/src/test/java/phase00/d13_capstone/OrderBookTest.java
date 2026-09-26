package phase00.d13_capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase00.d13_capstone.Order.Status;

@TestMethodOrder(MethodOrderer.MethodName.class)
class OrderBookTest {

    private static final LocalDate DAY = LocalDate.of(2026, 9, 26);

    private static Order order(String id, String customer, Status status) {
        return new Order(id, customer, DAY, new BigDecimal("10"), status);
    }

    @Test
    @DisplayName("B2: thêm, tìm, liệt kê theo khách; id trùng và id không có thì lỗi")
    void b02_addFindAndList() {
        OrderBook book = new OrderBook();
        book.add(order("o1", "c1", Status.NEW));
        book.add(order("o2", "c2", Status.NEW));
        book.add(order("o3", "c1", Status.PAID));

        assertEquals("o1", book.find("o1").id());
        List<Order> ofCustomer = book.byCustomer("c1");
        assertEquals(List.of("o1", "o3"), ofCustomer.stream().map(Order::id).toList());
        assertThrows(UnsupportedOperationException.class, () -> ofCustomer.add(order("x", "c1", Status.NEW)));
        assertThrows(IllegalArgumentException.class, () -> book.add(order("o1", "c9", Status.NEW)));
        assertThrows(NoSuchElementException.class, () -> book.find("khong-co"));
    }

    @Test
    @DisplayName("B2: đổi trạng thái sai qua sổ đơn vẫn bị từ chối")
    void b02_illegalTransition() {
        OrderBook book = new OrderBook();
        book.add(order("o1", "c1", Status.NEW));
        assertThrows(IllegalStateException.class, () -> book.changeStatus("o1", Status.SHIPPED));
        assertEquals(Status.NEW, book.find("o1").status());
    }
}
