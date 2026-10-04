package phase05.d01_di;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Ex01_ConstructorInjectionTest {
    @Test
    @DisplayName("Q01: service thuần Java nhận repository qua constructor và chạy với fake")
    void serviceUsesHandWrittenFakeWithoutSpring() {
        List<String> saved = new ArrayList<>();
        Ex01_ConstructorInjection.OrderRepository fake = saved::add;
        var service = new Ex01_ConstructorInjection.OrderService(fake);

        service.place("order-1");

        assertEquals(List.of("order-1"), saved);
    }

    @Test
    @DisplayName("Q02: đặt tên dependency rõ ràng và không khởi tạo repository bên trong service")
    void solutionFactoryUsesInjectedRepository() {
        List<String> saved = new ArrayList<>();

        Ex01_ConstructorInjection.service(saved::add).place("order-3");

        assertEquals(List.of("order-3"), saved);
    }

    @Test
    @DisplayName("Q03: service thuần Java có thể được test bằng constructor mà không có annotation Spring")
    void plainJavaServiceWorksWithoutSpringAnnotations() {
        var service = new Ex01_ConstructorInjection.OrderService(order -> assertEquals("plain", order));

        service.place("plain");
    }

    @Test
    @DisplayName("Q04: wiring thủ công làm rõ dependency của một graph nhỏ")
    void manualWiringMakesSmallGraphExplicit() {
        List<String> saved = new ArrayList<>();
        var repository = (Ex01_ConstructorInjection.OrderRepository) saved::add;
        var service = new Ex01_ConstructorInjection.OrderService(repository);

        service.place("manual");

        assertEquals(List.of("manual"), saved);
    }

    @Test
    @DisplayName("Q05: constructor injection không che giấu dependency graph")
    void serviceCompositionRemainsVisible() {
        List<String> saved = new ArrayList<>();
        var repository = (Ex01_ConstructorInjection.OrderRepository) saved::add;
        var service = new Ex01_ConstructorInjection.OrderService(repository);

        service.place("visible");

        assertEquals(List.of("visible"), saved);
    }

    @Test
    @DisplayName("Q02: constructor không chấp nhận dependency thiếu")
    void constructorRequiresRepository() {
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class,
                () -> new Ex01_ConstructorInjection.OrderService(null));
    }

    @Test
    @DisplayName("Q02: đặt tên dependency rõ ràng và không khởi tạo repository bên trong service")
    void constructorRequiresExplicitRepository() {
        List<String> saved = new ArrayList<>();
        var service = new Ex01_ConstructorInjection.OrderService(saved::add);

        service.place("order-2");

        assertEquals(List.of("order-2"), saved);
    }
}
