package phase00.d13_capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;
import phase00.d13_capstone.Order.Status;

@TestMethodOrder(MethodOrderer.MethodName.class)
class OrderFileTest {

    @TempDir
    Path dir;

    @Test
    @DisplayName("B3: file mẫu UTF-8 đọc được 3 đơn, bỏ dòng trống")
    void b03_readsSampleFile() throws IOException, URISyntaxException {
        Path sample = Path.of(getClass().getResource("/phase00/d13_capstone/orders-sample.txt").toURI());
        List<Order> orders = OrderFile.read(sample);
        assertEquals(List.of("o1", "o2", "o3"), orders.stream().map(Order::id).toList());
        assertEquals("khách-An", orders.get(0).customerCode());
        assertEquals("khách-Bình", orders.get(2).customerCode());
        assertEquals(0, new BigDecimal("10.50").compareTo(orders.get(0).total()));
        assertEquals(0, BigDecimal.ZERO.compareTo(orders.get(1).total()));
        assertEquals(Status.SHIPPED, orders.get(2).status());
    }

    @Test
    @DisplayName("B3: file rỗng trả list rỗng")
    void b03_emptyFile() throws IOException {
        Path file = dir.resolve("empty.txt");
        Files.writeString(file, "", StandardCharsets.UTF_8);
        assertEquals(List.of(), OrderFile.read(file));
    }

    @Test
    @DisplayName("B3: file không có, dòng sai và id trùng báo theo số dòng")
    void b03_missingBadLineAndDuplicate() throws IOException {
        assertThrows(NoSuchFileException.class, () -> OrderFile.read(dir.resolve("khong-co.txt")));

        Path bad = dir.resolve("bad.txt");
        Files.writeString(bad, "o1,c1,2026-09-26,1,NEW\n\nnot-a-line\n", StandardCharsets.UTF_8);
        IllegalArgumentException lineError = assertThrows(IllegalArgumentException.class, () -> OrderFile.read(bad));
        assertTrue(lineError.getMessage().contains("dòng 3"));

        Path duplicate = dir.resolve("dup.txt");
        Files.writeString(duplicate, "o1,c1,2026-09-26,1,NEW\no1,c2,2026-09-26,2,NEW\n", StandardCharsets.UTF_8);
        IllegalArgumentException dup = assertThrows(IllegalArgumentException.class, () -> OrderFile.read(duplicate));
        assertTrue(dup.getMessage().contains("dòng 2"));
        assertTrue(dup.getMessage().contains("o1"));
    }
}
