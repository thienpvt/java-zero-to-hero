package phase05.d02_beans_lifecycle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class Ex01_ScopeAndStateTest {

    @Test
    @DisplayName("Q03: prototype scope tạo instance mới trong cùng context")
    void prototypeScopeReturnsNewInstance() {
        try (var context = new AnnotationConfigApplicationContext(Ex01_ScopeAndState.ScopeExamples.class)) {
            assertNotSame(context.getBean(Ex01_ScopeAndState.PrototypeExample.class),
                    context.getBean(Ex01_ScopeAndState.PrototypeExample.class));
        }
    }

    @Test
    @DisplayName("Q01: singleton được chia sẻ trong cùng context nhưng không được coi là thread-safe")
    void singletonScopeReusesInstance() {
        try (var context = new AnnotationConfigApplicationContext(Ex01_ScopeAndState.ScopeExamples.class)) {
            var firstContextBean = context.getBean(Ex01_ScopeAndState.SingletonExample.class);
            var sameContextBean = context.getBean(Ex01_ScopeAndState.SingletonExample.class);
            assertSame(firstContextBean, sameContextBean);
            try (var anotherContext = new AnnotationConfigApplicationContext(Ex01_ScopeAndState.ScopeExamples.class)) {
                org.junit.jupiter.api.Assertions.assertNotSame(firstContextBean,
                        anotherContext.getBean(Ex01_ScopeAndState.SingletonExample.class));
            }
        }
    }

    @Test
    @DisplayName("Q04: class cấu hình tạo bean qua factory method @Bean")
    void configurationFactoryCreatesSingletonExample() {
        try (var context = new AnnotationConfigApplicationContext(Ex01_ScopeAndState.ScopeExamples.class)) {
            assertSame(context.getBean(Ex01_ScopeAndState.SingletonExample.class),
                    context.getBean(Ex01_ScopeAndState.SingletonExample.class));
        }
    }

    @Test
    @DisplayName("Q02: singleton không chứa currentUser có thể thay đổi giữa request")
    void requestIdentityIsPassedToOperation() {
        var service = new Ex01_ScopeAndState.GreetingService();

        assertSame("alice", service.greet("alice"));
        assertSame("bob", service.greet("bob"));
    }
}
