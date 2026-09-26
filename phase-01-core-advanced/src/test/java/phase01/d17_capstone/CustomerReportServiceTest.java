package phase01.d17_capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import phase01.d17_capstone.CustomerReportService.CustomerSummary;

@TestMethodOrder(MethodOrderer.MethodName.class)
class CustomerReportServiceTest {

    // 5 order hợp lệ từ orders-sample.csv (o1, o2, o3, o4, o6) — xem CsvOrderParserTest.
    private static final Order O1 = new Order(
            "o1", "khách-Đạt", Instant.parse("2026-01-01T17:30:00Z"), new BigDecimal("100.00"), Order.Status.PAID);
    private static final Order O2 = new Order(
            "o2", "khách-Đạt", Instant.parse("2026-01-03T01:00:00Z"), new BigDecimal("50.50"), Order.Status.PAID);
    private static final Order O3 = new Order(
            "o3", "an", Instant.parse("2026-01-02T08:00:00Z"), new BigDecimal("20.00"), Order.Status.NEW);
    private static final Order O4 = new Order(
            "o4", "an", Instant.parse("2026-01-01T00:00:00Z"), new BigDecimal("999.00"), Order.Status.CANCELLED);
    private static final Order O6 = new Order(
            "o6", "chi", Instant.parse("2026-01-05T00:00:00Z"), new BigDecimal("7.00"), Order.Status.CANCELLED);
    private static final List<Order> VALID_ORDERS = List.of(O1, O2, O3, O4, O6);

    private static List<CustomerSummary> summarize(String variant, CustomerReportService service, List<Order> orders) {
        return "loop".equals(variant) ? service.summarizeWithLoop(orders) : service.summarizeWithStream(orders);
    }

    @ParameterizedTest
    @ValueSource(strings = {"loop", "stream"})
    @DisplayName("B4 tổng hợp (Asia/Ho_Chi_Minh): đúng orderCount/totalPaid/firstOrderDate, đúng thứ tự, bỏ khách chỉ có CANCELLED")
    void b04_summarizeWithHoChiMinhZoneReturnsExpectedRows(String variant) {
        CustomerReportService service = new CustomerReportService(ZoneId.of("Asia/Ho_Chi_Minh"));

        List<CustomerSummary> rows = summarize(variant, service, VALID_ORDERS);

        assertEquals(List.of("an", "khách-Đạt"), rows.stream().map(CustomerSummary::customerId).toList(),
                "Kết quả phải có đúng 2 khách theo thứ tự tăng dần; 'chi' (chỉ có order CANCELLED) không xuất hiện.");

        CustomerSummary an = rows.get(0);
        assertEquals(1, an.orderCount(), "'an' chỉ còn 1 order (o3) sau khi bỏ order CANCELLED (o4).");
        assertEquals(0, an.totalPaid().compareTo(BigDecimal.ZERO),
                "'an' không có order PAID nên totalPaid phải là 0.");
        assertEquals(LocalDate.of(2026, 1, 2), an.firstOrderDate(),
                "firstOrderDate của 'an' là ngày của o3 (2026-01-02T08:00:00Z) theo Asia/Ho_Chi_Minh.");

        CustomerSummary khachDat = rows.get(1);
        assertEquals("khách-Đạt", khachDat.customerId());
        assertEquals(2, khachDat.orderCount(), "'khách-Đạt' có 2 order (o1, o2).");
        assertEquals(0, khachDat.totalPaid().compareTo(new BigDecimal("150.50")),
                "totalPaid của 'khách-Đạt' phải là tổng 100.00 + 50.50 = 150.50 (cả hai đều PAID).");
        assertEquals(LocalDate.of(2026, 1, 2), khachDat.firstOrderDate(),
                "firstOrderDate phải là ngày của o1 (sớm nhất, 2026-01-01T17:30:00Z) theo Asia/Ho_Chi_Minh (UTC+7 -> 2026-01-02).");
    }

    @ParameterizedTest
    @ValueSource(strings = {"loop", "stream"})
    @DisplayName("B4 tổng hợp (UTC): đổi ZoneId làm đổi firstOrderDate của khách-Đạt")
    void b04_summarizeWithUtcZoneChangesFirstOrderDate(String variant) {
        CustomerReportService service = new CustomerReportService(ZoneId.of("UTC"));

        List<CustomerSummary> rows = summarize(variant, service, VALID_ORDERS);

        CustomerSummary khachDat = rows.stream()
                .filter(row -> row.customerId().equals("khách-Đạt"))
                .findFirst()
                .orElseThrow();
        assertEquals(LocalDate.of(2026, 1, 1), khachDat.firstOrderDate(),
                "Ở UTC, o1 (2026-01-01T17:30:00Z) rơi vào ngày 2026-01-01, khác với 2026-01-02 ở Asia/Ho_Chi_Minh.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"loop", "stream"})
    @DisplayName("B4 tổng hợp: danh sách kết quả trả về không cho sửa")
    void b04_summarizeResultIsUnmodifiable(String variant) {
        CustomerReportService service = new CustomerReportService(ZoneId.of("UTC"));

        List<CustomerSummary> rows = summarize(variant, service, VALID_ORDERS);

        assertThrows(UnsupportedOperationException.class,
                () -> rows.add(new CustomerSummary("z", 0, BigDecimal.ZERO, LocalDate.of(2026, 1, 1))),
                "List trả về phải bất biến; gọi add() phải ném UnsupportedOperationException.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"loop", "stream"})
    @DisplayName("B4 tổng hợp: danh sách order rỗng cho ra báo cáo rỗng")
    void b04_summarizeWithEmptyOrdersReturnsEmptyList(String variant) {
        CustomerReportService service = new CustomerReportService(ZoneId.of("UTC"));

        List<CustomerSummary> rows = summarize(variant, service, List.of());

        assertTrue(rows.isEmpty(), "Danh sách order rỗng phải cho ra báo cáo rỗng.");
    }
}
