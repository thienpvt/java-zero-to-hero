package phase02.d04_gc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static phase02.support.Predictions.assertPrediction;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_ReferencesAndLeakTest {

    @Test
    @DisplayName("Q5 dự đoán: weak reference có ngăn GC không")
    void q05_prediction() {
        byte[] data = new byte[] {1, 2, 3};
        WeakReference<byte[]> ref = Ex02_ReferencesAndLeak.weakOf(data);
        assertSame(data, ref.get(),
                "Khi mảng còn biến cục bộ, weakOf phải trả về đúng referent đó.");
        assertPrediction("Q5_WEAK_DOES_NOT_PREVENT_GC", true,
                Ex02_ReferencesAndLeak.Q5_WEAK_DOES_NOT_PREVENT_GC,
                "Strong reference mới giữ object sống. WeakReference không chặn GC khi không còn strong ref. Đừng assert referent đã bị clear.");
    }

    @Test
    @DisplayName("Q5: surviveOrClear trả referent khi chuỗi còn sống")
    void q05_surviveOrClear_returnsReferentWhileStrong() {
        assertReferentWhileStrong("alpha");
        assertReferentWhileStrong("beta");
    }

    @Test
    @DisplayName("Q6: cache là WeakHashMap, get/put khi key còn sống")
    void q06_cache_getPutWhileKeyStronglyReachable() {
        Map<Object, String> map = Ex02_ReferencesAndLeak.cache();
        Object key = new Object();
        map.put(key, "cached");
        assertInstanceOf(WeakHashMap.class, map, "cache() phải trả về WeakHashMap.");
        assertEquals("cached", map.get(key),
                "Khi key còn reachable mạnh, get phải trả value vừa put.");
        // Bỏ strong ref cục bộ. Không assert size hay get sau bước này.
        key = null;
    }

    private static void assertReferentWhileStrong(String text) {
        WeakReference<String> ref = new WeakReference<>(text);
        assertEquals(text, Ex02_ReferencesAndLeak.surviveOrClear(ref),
                "Khi chuỗi còn reachable mạnh, surviveOrClear phải trả đúng referent.");
    }
}
