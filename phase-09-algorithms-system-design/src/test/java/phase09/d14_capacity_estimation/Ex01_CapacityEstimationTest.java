package phase09.d14_capacity_estimation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Ex01_CapacityEstimationTest {
    @Test
    @DisplayName("B1 quy đổi requests mỗi ngày thành RPS với scale sáu")
    void b1_convertsDailyRequestsToRps() {
        assertEquals(new BigDecimal("11.574074"), Ex01_CapacityEstimation.averageRps(1_000_000));
    }

    @Test
    @DisplayName("B1 tính storage logical có kiểm tra overflow")
    void b1_calculatesLogicalStorageAndChecksOverflow() {
        assertEquals(new BigDecimal("747520000000"),
                Ex01_CapacityEstimation.storageBytes(1_000_000, 2_048, 365));
        assertThrows(ArithmeticException.class,
                () -> Ex01_CapacityEstimation.storageBytes(Long.MAX_VALUE, 2, 1));
    }

    @Test
    @DisplayName("B1 áp dụng overhead và replica mà không overflow hệ số int")
    void b1_calculatesPhysicalStorage() {
        assertEquals(new BigDecimal("4485120000000"),
                Ex01_CapacityEstimation.physicalBytes(new BigDecimal("747520000000"), 2, 3));
        assertEquals(new BigDecimal("21474836470"),
                Ex01_CapacityEstimation.physicalBytes(BigDecimal.TEN, Integer.MAX_VALUE, 1));
    }

    @Test
    @DisplayName("B1 từ chối đầu vào không dương")
    void b1_rejectsNonPositiveInputs() {
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_CapacityEstimation.averageRps(0));
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_CapacityEstimation.storageBytes(1, 0, 1));
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_CapacityEstimation.physicalBytes(BigDecimal.ZERO, 2, 3));
    }
}
