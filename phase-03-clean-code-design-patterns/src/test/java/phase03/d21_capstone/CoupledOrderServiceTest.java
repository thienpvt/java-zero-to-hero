package phase03.d21_capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class CoupledOrderServiceTest {

    private static final List<int[]> ITEMS = List.of(new int[] {1_000, 3}, new int[] {500, 2});

    @Test
    @DisplayName("Hành vi trước refactor: đơn thường")
    void b01_standardOrder() {
        CoupledOrderService service = new CoupledOrderService();
        assertEquals("OK:o-1:4000", service.createOrder("alice", ITEMS, false));
        assertEquals(List.of("o-1:4000"), service.charges());
        assertEquals("{o-1=4000:alice}", service.savedOrders().toString());
        assertEquals(List.of("created:o-1"), service.events());
    }

    @Test
    @DisplayName("Hành vi trước refactor: đơn VIP giảm 10 phần trăm")
    void b01_vipOrder() {
        CoupledOrderService service = new CoupledOrderService();
        assertEquals("OK:o-1:3600", service.createOrder("alice", ITEMS, true));
    }

    @Test
    @DisplayName("Hành vi trước refactor: quá 100000 cent bị từ chối, không charge, không lưu")
    void b01_declinedOrder() {
        CoupledOrderService service = new CoupledOrderService();
        assertEquals("DECLINED:o-1:200000", service.createOrder("bob", List.of(new int[] {100_000, 2}), false));
        assertTrue(service.charges().isEmpty(), "Đơn bị từ chối không được charge.");
        assertTrue(service.savedOrders().isEmpty(), "Đơn bị từ chối không được lưu.");
    }

    @Test
    @DisplayName("Hành vi trước refactor: thiếu khách hoặc rỗng mặt hàng bị chặn sớm")
    void b01_validation() {
        CoupledOrderService service = new CoupledOrderService();
        assertThrows(IllegalArgumentException.class, () -> service.createOrder(" ", ITEMS, false));
        assertThrows(IllegalArgumentException.class, () -> service.createOrder("alice", List.of(), false));
    }
}
