package phase01.d07_comparable_comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase01.support.Predictions.assertPrediction;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d07_comparable_comparator.Ex01_NaturalVsCustomOrder.Product;
import phase01.d07_comparable_comparator.Ex01_NaturalVsCustomOrder.Version;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_NaturalVsCustomOrderTest {

    @Test
    @DisplayName("Q1 compareTo: so major, minor, patch theo thứ tự")
    void q01_versionCompareTo_comparesMajorMinorPatchInOrder() {
        assertTrue(Version.parse("1.2.0").compareTo(Version.parse("1.10.0")) < 0,
                "1.2.0 phải nhỏ hơn 1.10.0 vì so minor: 2 < 10.");
        assertTrue(Version.parse("2.0.0").compareTo(Version.parse("1.99.99")) > 0,
                "2.0.0 phải lớn hơn 1.99.99 vì so major: 2 > 1.");
        assertEquals(0, Version.parse("1.2.3").compareTo(Version.parse("1.2.3")),
                "Hai Version giống nhau phải compareTo bằng 0.");
    }

    @Test
    @DisplayName("Q1 compareTo: dùng để sort danh sách Version theo giá trị số")
    void q01_versionCompareTo_sortsListNumerically() {
        List<Version> versions = Arrays.asList(
                Version.parse("1.10.0"), Version.parse("1.2.0"),
                Version.parse("1.2.10"), Version.parse("1.2.9"));
        versions.sort(Version::compareTo);

        assertEquals(List.of("1.2.0", "1.2.9", "1.2.10", "1.10.0"),
                versions.stream().map(Version::toString).toList(),
                "Sort theo Version.compareTo phải theo thứ tự số (major/minor/patch), không theo thứ tự chuỗi.");
    }

    @Test
    @DisplayName("Q2 Product.byPrice: sort tăng dần theo giá")
    void q02_productByPrice_sortsAscendingByPrice() {
        List<Product> products = Arrays.asList(
                new Product("C", new BigDecimal("30.00")),
                new Product("A", new BigDecimal("10.00")),
                new Product("B", new BigDecimal("20.00")));
        products.sort(Product.byPrice());

        assertEquals(List.of("A", "B", "C"),
                products.stream().map(Product::name).toList(),
                "Product.byPrice() phải sort tăng dần theo price.");
    }

    @Test
    @DisplayName("Q2 Product.byNameIgnoreCase: sort tên không phân biệt hoa thường")
    void q02_productByNameIgnoreCase_sortsCaseInsensitively() {
        List<Product> products = Arrays.asList(
                new Product("banana", BigDecimal.ONE),
                new Product("Apple", BigDecimal.ONE),
                new Product("cherry", BigDecimal.ONE));
        products.sort(Product.byNameIgnoreCase());

        assertEquals(List.of("Apple", "banana", "cherry"),
                products.stream().map(Product::name).toList(),
                "Product.byNameIgnoreCase() phải sort theo tên bỏ qua hoa/thường.");
    }

    @Test
    @DisplayName("Q3 dự đoán: thứ tự String \"1.10.0\" và \"1.2.0\"")
    void q03_prediction_stringNaturalOrderComparesCharByChar() {
        assertPrediction("Q3_STRING_ORDER_PUTS_1_10_BEFORE_1_2",
                "1.10.0".compareTo("1.2.0") < 0,
                Ex01_NaturalVsCustomOrder.Q3_STRING_ORDER_PUTS_1_10_BEFORE_1_2,
                "String.compareTo so từng ký tự theo UTF-16 code unit: ký tự '1' (0x31) < '2' (0x32) tại vị trí thứ 3.");
    }
}
