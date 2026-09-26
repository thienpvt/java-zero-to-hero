package phase01.d17_capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d17_capstone.CsvOrderParser.LineError;
import phase01.d17_capstone.CustomerReportService.CustomerSummary;

@TestMethodOrder(MethodOrderer.MethodName.class)
class ReportAppTest {

    @Test
    @DisplayName("B5 render: 1 dòng dữ liệu và 1 lỗi cho ra đúng định dạng theo đặc tả")
    void b05_renderFormatsOneRowAndOneError() {
        CustomerSummary row = new CustomerSummary("an", 2, new BigDecimal("150.50"), LocalDate.of(2026, 1, 2));
        LineError error = new LineError(6, "Ngày không hợp lệ");

        String rendered = ReportApp.render(List.of(row), List.of(error));

        String expected = "customerId | orders | totalPaid | firstOrderDate\n"
                + "an | 2 | 150.50 | 2026-01-02\n"
                + "\n"
                + "Dòng 6: Ngày không hợp lệ";
        assertEquals(expected, rendered,
                "Phải có dòng tiêu đề, dòng dữ liệu 'customerId | orderCount | totalPaid | firstOrderDate', "
                        + "một dòng trống rồi dòng lỗi 'Dòng <n>: <message>'.");
    }

    @Test
    @DisplayName("B5 render: nhiều dòng dữ liệu, không có lỗi thì không có dòng trống/lỗi ở cuối")
    void b05_renderWithMultipleRowsAndNoErrorsHasNoTrailingBlankLine() {
        CustomerSummary row1 = new CustomerSummary("an", 1, BigDecimal.ZERO, LocalDate.of(2026, 1, 2));
        CustomerSummary row2 = new CustomerSummary("khách-Đạt", 2, new BigDecimal("150.50"), LocalDate.of(2026, 1, 2));

        String rendered = ReportApp.render(List.of(row1, row2), List.of());

        String expected = "customerId | orders | totalPaid | firstOrderDate\n"
                + "an | 1 | 0 | 2026-01-02\n"
                + "khách-Đạt | 2 | 150.50 | 2026-01-02";
        assertEquals(expected, rendered, "Không có lỗi thì không được thêm dòng trống hay dòng lỗi nào ở cuối.");
    }

    @Test
    @DisplayName("B5 render: không có dữ liệu và không có lỗi thì chỉ có dòng tiêu đề")
    void b05_renderWithNoRowsAndNoErrorsReturnsHeaderOnly() {
        String rendered = ReportApp.render(List.of(), List.of());

        assertEquals("customerId | orders | totalPaid | firstOrderDate", rendered,
                "Không có dữ liệu và không có lỗi thì báo cáo chỉ còn đúng dòng tiêu đề.");
    }
}
