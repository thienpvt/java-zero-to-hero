package phase09.d03_hash_table;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Ex01_HashTableTest {
    @Test
    @DisplayName("B1 đếm tần suất và trả map bất biến")
    void b01CountsDuplicatesAndReturnsImmutableMap() {
        var frequencies = Ex01_HashTable.frequencies(List.of("xoài", "táo", "xoài"));
        assertEquals(2, frequencies.get("xoài"));
        assertEquals(1, frequencies.get("táo"));
        assertTrue(Ex01_HashTable.frequencies(List.of()).isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> frequencies.put("mận", 1));
        assertEquals("key", new Ex01_HashTable.Key("key").value());
    }

    @Test
    @DisplayName("B1 từ chối list null và phần tử null")
    void b01RejectsNullBoundaries() {
        assertThrows(NullPointerException.class, () -> Ex01_HashTable.frequencies(null));
        assertThrows(NullPointerException.class, () -> Ex01_HashTable.frequencies(java.util.Arrays.asList("a", null)));
        assertThrows(NullPointerException.class, () -> new Ex01_HashTable.Key(null));
    }
}
