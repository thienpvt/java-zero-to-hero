package phase00.d05_encapsulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_BankAccountTest {

    @Test
    @DisplayName("Q3 code: nạp rồi rút, số dư đầu bằng 0 vẫn hợp lệ")
    void q03_depositAndWithdraw() {
        Ex02_BankAccount account = new Ex02_BankAccount("A-01", BigDecimal.ZERO);
        account.deposit(new BigDecimal("10.50"));
        account.withdraw(new BigDecimal("0.50"));
        assertEquals("A-01", account.id());
        assertEquals(0, new BigDecimal("10.00").compareTo(account.balance()));
    }

    @Test
    @DisplayName("Q3 code: từ chối id trống, số dư âm, nạp/rút không dương và rút quá số dư")
    void q03_rejectsInvalidOperations() {
        assertThrows(IllegalArgumentException.class,
                () -> new Ex02_BankAccount("  ", BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> new Ex02_BankAccount("A-01", new BigDecimal("-1")));
        Ex02_BankAccount account = new Ex02_BankAccount("A-01", new BigDecimal("5"));
        assertThrows(IllegalArgumentException.class, () -> account.deposit(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(new BigDecimal("-1")));
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(new BigDecimal("6")));
        assertEquals(0, new BigDecimal("5").compareTo(account.balance()));
    }
}
