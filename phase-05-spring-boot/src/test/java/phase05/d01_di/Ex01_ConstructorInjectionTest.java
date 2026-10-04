package phase05.d01_di;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Ex01_ConstructorInjectionTest {
    @Test
    @DisplayName("B01: service nhận fake repository qua constructor, không cần Spring")
    void serviceUsesHandWrittenFakeWithoutSpring() {
        List<String> saved = new ArrayList<>();
        Ex01_ConstructorInjection.OrderRepository fake = saved::add;
        var service = Ex01_ConstructorInjection.service(fake);

        service.place("order-1");

        assertEquals(List.of("order-1"), saved);
    }

    @Test
    @DisplayName("B01: dependency bắt buộc không được thiếu")
    void serviceRequiresRepository() {
        List<String> saved = new ArrayList<>();
        Ex01_ConstructorInjection.service(saved::add).place("control");
        assertEquals(List.of("control"), saved);

        assertThrows(NullPointerException.class, () -> Ex01_ConstructorInjection.service(null));
    }
}
