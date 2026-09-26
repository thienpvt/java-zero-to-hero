package phase00.d06_polymorphism;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase00.d06_polymorphism.Ex02_ShippingFee.FixedFee;
import phase00.d06_polymorphism.Ex02_ShippingFee.ShippingFeePolicy;
import phase00.d06_polymorphism.Ex02_ShippingFee.WeightFee;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_ShippingFeeTest {

    @Test
    @DisplayName("Q4 code: phí cố định và phí theo cân, không rẽ theo tên class")
    void q04_quotesWithoutInstanceof() {
        assertEquals(0, new BigDecimal("20000").compareTo(
                Ex02_ShippingFee.quote(new FixedFee(new BigDecimal("20000")), 1500)));
        assertEquals(0, new BigDecimal("37500").compareTo(
                Ex02_ShippingFee.quote(new WeightFee(new BigDecimal("25000")), 1500)));
        ShippingFeePolicy extra = grams -> new BigDecimal("9");
        assertEquals(0, new BigDecimal("9").compareTo(Ex02_ShippingFee.quote(extra, 3)));
    }

    @Test
    @DisplayName("Q4 code: số gram âm bị từ chối")
    void q04_rejectsNegativeGrams() {
        FixedFee fixed = new FixedFee(new BigDecimal("20000"));
        assertThrows(IllegalArgumentException.class, () -> Ex02_ShippingFee.quote(fixed, -1));
        WeightFee weight = new WeightFee(new BigDecimal("25000"));
        assertThrows(IllegalArgumentException.class, () -> Ex02_ShippingFee.quote(weight, -1));
    }
}
