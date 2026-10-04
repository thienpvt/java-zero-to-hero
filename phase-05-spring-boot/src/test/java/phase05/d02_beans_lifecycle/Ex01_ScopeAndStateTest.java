package phase05.d02_beans_lifecycle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Ex01_ScopeAndStateTest {
    @Test
    @DisplayName("B02: cùng service dùng identity của từng lời gọi, không giữ currentUser")
    void requestIdentityIsPassedToOperation() {
        var service = Ex01_ScopeAndState.statelessService();

        assertEquals("alice", service.greet("alice"));
        assertEquals("bob", service.greet("bob"));
        assertEquals("alice", service.greet("alice"));
    }
}
