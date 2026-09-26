package phase01.d11_optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase01.support.Predictions.assertPrediction;

import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d11_optional.Ex02_MapFlatMapAndUsage.Address;
import phase01.d11_optional.Ex02_MapFlatMapAndUsage.Customer;
import phase01.support.Compiles;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_MapFlatMapAndUsageTest {

    private static final String HINT_Q5 =
            "Optional là kiểu reference bình thường nên biến vẫn nhận được null; Optional.empty().get() ném NoSuchElementException.";
    private static final String HINT_Q6 =
            "map(f) với f trả Optional<U> tạo Optional<Optional<U>> (lồng); flatMap(f) làm phẳng thành Optional<U>.";

    @Test
    @DisplayName("Q5 dự đoán: get() trên Optional trống và gán null cho biến Optional")
    void q05_prediction() {
        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> Optional.empty().get());
        assertPrediction("Q5_GET_ON_EMPTY_EXCEPTION",
                ex.getClass().getSimpleName(), Ex02_MapFlatMapAndUsage.Q5_GET_ON_EMPTY_EXCEPTION, HINT_Q5);

        assertPrediction("Q5_ASSIGN_NULL_TO_OPTIONAL_COMPILES",
                Compiles.YES, Ex02_MapFlatMapAndUsage.Q5_ASSIGN_NULL_TO_OPTIONAL_COMPILES, HINT_Q5);
    }

    @Test
    @DisplayName("Q6 dự đoán: map() với hàm trả Optional có lồng Optional không")
    void q06_prediction() {
        Customer customer = new Customer("C1", new Address("Hà Nội", "100000"));
        Object mapped = Optional.of(customer).map(Ex02_MapFlatMapAndUsage::addressOpt).get();
        assertPrediction("Q6_MAP_WITH_OPTIONAL_FUNCTION_NESTS",
                mapped instanceof Optional, Ex02_MapFlatMapAndUsage.Q6_MAP_WITH_OPTIONAL_FUNCTION_NESTS, HINT_Q6);
    }

    @Test
    @DisplayName("Q6 code: cityUpper viết hoa city hợp lệ theo Locale.ROOT")
    void q06_cityUpper_returnsUpperCasedCity() {
        Customer customer = new Customer("C1", new Address("hà nội", "100000"));
        assertEquals(Optional.of("HÀ NỘI"), Ex02_MapFlatMapAndUsage.cityUpper(customer),
                "City hợp lệ phải được viết hoa theo Locale.ROOT.");
    }

    @Test
    @DisplayName("Q6 code: cityUpper trả empty khi address null")
    void q06_cityUpper_emptyWhenAddressNull() {
        Customer customer = new Customer("C1", null);
        assertEquals(Optional.empty(), Ex02_MapFlatMapAndUsage.cityUpper(customer),
                "Address null phải cho kết quả empty, không được ném NullPointerException.");
    }

    @Test
    @DisplayName("Q6 code: cityUpper trả empty khi city blank")
    void q06_cityUpper_emptyWhenCityBlank() {
        Customer customer = new Customer("C1", new Address(" ", "100000"));
        assertEquals(Optional.empty(), Ex02_MapFlatMapAndUsage.cityUpper(customer),
                "City blank phải cho kết quả empty.");
    }

    @Test
    @DisplayName("Q6 code: cityUpper trả empty khi customer null")
    void q06_cityUpper_emptyWhenCustomerNull() {
        assertEquals(Optional.empty(), Ex02_MapFlatMapAndUsage.cityUpper(null),
                "Customer null phải cho kết quả empty.");
    }

    @Test
    @DisplayName("Q6 code: zipOf dùng flatMap lấy zip khi address có zip")
    void q06_zipOf_returnsZipWhenPresent() {
        Customer customer = new Customer("C1", new Address("Hà Nội", "700000"));
        assertEquals(Optional.of("700000"), Ex02_MapFlatMapAndUsage.zipOf(Optional.of(customer)),
                "zipOf phải lấy zip từ address qua flatMap, không lồng Optional.");
    }

    @Test
    @DisplayName("Q6 code: zipOf trả empty khi zip null")
    void q06_zipOf_emptyWhenZipNull() {
        Customer customer = new Customer("C1", new Address("Hà Nội", null));
        assertEquals(Optional.empty(), Ex02_MapFlatMapAndUsage.zipOf(Optional.of(customer)),
                "zip null phải cho kết quả empty.");
    }
}
