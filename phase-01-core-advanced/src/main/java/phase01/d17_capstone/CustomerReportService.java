package phase01.d17_capstone;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Bài tích hợp — CustomerReportService (tổng hợp đơn hàng thành báo cáo theo khách hàng)
 *
 * Nguồn: 01-java-core-advanced.md, mục "Bài thực hành tích hợp", bước 3 và 4.
 * Cần làm trước: CsvOrderParser (B2, B3) — cần {@code List<Order>} hợp lệ để tổng hợp.
 * Cách làm: cài lần lượt {@code summarizeWithLoop} rồi {@code summarizeWithStream}; cả hai
 * phải cho cùng kết quả trên cùng input (CustomerReportServiceTest chạy chung một bộ test
 * cho cả hai bằng {@code @ParameterizedTest}).
 *
 * ─────────────────────────────────────────────────────────────────────
 * B3 [CODE] Chọn `Map`/`Set` phù hợp để tổng hợp và giữ thứ tự báo cáo; giải thích `equals`/`hashCode`
 *     của key.
 *   Bắt đầu   : đọc constructor và record {@code CustomerSummary} cho sẵn bên dưới trước khi
 *               cài B4.
 *   Kiểm chứng: đặt breakpoint ngay chỗ bạn gom nhóm theo {@code customerId} trong
 *               {@code summarizeWithLoop}, Debug test với 3 khách hàng khác nhau, sau khi đã
 *               viết code, tự xem thứ tự các nhóm so với list input.
 *   Code      : không có phần TODO riêng — {@code Map} dùng để gom nhóm nằm trong thân
 *               {@code summarizeWithLoop} (bước B4). Viết vào khối {@code ANSWER B3} trong
 *               {@code CsvOrderParser.java} (dùng chung cho {@code Set} ở B2 và {@code Map} ở đây).
 *   Hoàn thành khi: giải thích được vì sao kiểu {@code Map} bạn chọn giữ được thứ tự khách
 *               hàng xuất hiện lần đầu, trước khi sắp lại theo customerId.
 *
 * B4 [CODE] Viết hai phiên bản tổng hợp bằng vòng lặp và Stream; tránh state mutable dùng
 *     chung trong Stream.
 *   Bắt đầu   : mở {@code CustomerReportServiceTest#b04_summarizeWithHoChiMinhZoneReturnsExpectedRows},
 *               đọc kỳ vọng cho từng khách hàng (an, khách-Đạt) trước khi cài code.
 *   Kiểm chứng: chạy test với tham số "loop" rồi "stream" (▶ cạnh từng dòng
 *               {@code @ParameterizedTest}, Ctrl+Shift+F10); nếu ngày báo cáo lệch zone, đặt
 *               breakpoint tại chỗ bạn đổi {@code Instant} thành ngày theo {@code reportZone},
 *               F7 Step Into để xem mốc tuyệt đối được gắn zone thế nào.
 *   Code      : cài {@code summarizeWithLoop} (vòng lặp for, biến gom nhóm cục bộ) và
 *               {@code summarizeWithStream} (Stream, không có biến ngoài bị forEach sửa đổi
 *               dùng chung giữa các phần tử); cả hai: bỏ order {@code CANCELLED}; orderCount
 *               = số order còn lại của khách; totalPaid = tổng amount các order {@code PAID}
 *               ({@code BigDecimal.ZERO} nếu không có); firstOrderDate = ngày (theo
 *               {@code reportZone}) của createdAt sớm nhất; khách chỉ có order
 *               {@code CANCELLED} không xuất hiện; kết quả sắp theo customerId tăng dần và
 *               bất biến ({@code List.copyOf}/{@code toList()}).
 *   Hoàn thành khi: mọi test tham số ("loop", "stream") trong CustomerReportServiceTest xanh;
 *               viết vào khối {@code ANSWER B4} để so sánh hai cách viết.
 */
public final class CustomerReportService {

    private final ZoneId reportZone;

    public CustomerReportService(ZoneId reportZone) {
        this.reportZone = Objects.requireNonNull(reportZone, "reportZone không được null");
    }

    /** Báo cáo một khách hàng: số order (đã bỏ CANCELLED), tổng tiền đã trả, ngày đặt sớm nhất. */
    public record CustomerSummary(String customerId, int orderCount, BigDecimal totalPaid, LocalDate firstOrderDate) {
    }

    /**
     * Tổng hợp {@code orders} thành báo cáo theo khách hàng, cài bằng vòng lặp {@code for}.
     *
     * <p>Bỏ order {@code CANCELLED}; khách chỉ toàn order {@code CANCELLED} không xuất hiện
     * trong kết quả; kết quả sắp theo {@code customerId} tăng dần và không thể sửa được.
     */
    public List<CustomerSummary> summarizeWithLoop(List<Order> orders) {
        throw new UnsupportedOperationException("TODO B4");
    }

    /**
     * Tương đương {@link #summarizeWithLoop(List)} nhưng cài bằng Stream, không dùng biến
     * gom nhóm bị sửa đổi (mutate) dùng chung giữa các phần tử của stream.
     */
    public List<CustomerSummary> summarizeWithStream(List<Order> orders) {
        throw new UnsupportedOperationException("TODO B4");
    }
}

/* ANSWER B4:
 *
 */
