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
 * <p>
 * B3 [CODE] Chọn {@code Map}/{@code Set} phù hợp để tổng hợp và giữ thứ tự báo cáo; giải thích {@code equals}/{@code hashCode}
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
 * <p>
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
     * &lt;&lt;&lt;TAG0&gt;&gt;&gt;Bỏ order {@code CANCELLED}; khách chỉ toàn order {@code CANCELLED} không xuất hiện
     * trong kết quả; kết quả sắp theo {@code customerId} tăng dần và không thể sửa được.
     */
    public List<CustomerSummary> summarizeWithLoop(List<Order> orders) {
        // SOLUTION-BEGIN throw B4
        // B3: LinkedHashMap giữ thứ tự khách hàng xuất hiện lần đầu trong khi gom nhóm.
        Map<String, List<Order>> byCustomer = new java.util.LinkedHashMap<>();
        for (Order order : orders) {
            if (order.status() == Order.Status.CANCELLED) {
                continue;
            }
            byCustomer.computeIfAbsent(order.customerId(), key -> new ArrayList<>()).add(order);
        }
        List<CustomerSummary> result = new ArrayList<>();
        for (Map.Entry<String, List<Order>> entry : byCustomer.entrySet()) {
            List<Order> customerOrders = entry.getValue();
            BigDecimal totalPaid = BigDecimal.ZERO;
            Order earliest = null;
            for (Order order : customerOrders) {
                if (order.status() == Order.Status.PAID) {
                    totalPaid = totalPaid.add(order.amount());
                }
                if (earliest == null || order.createdAt().isBefore(earliest.createdAt())) {
                    earliest = order;
                }
            }
            LocalDate firstOrderDate = earliest.createdAt().atZone(reportZone).toLocalDate();
            result.add(new CustomerSummary(entry.getKey(), customerOrders.size(), totalPaid, firstOrderDate));
        }
        result.sort(Comparator.comparing(CustomerSummary::customerId));
        return List.copyOf(result);
        // SOLUTION-END
    }

    /**
     * Tương đương {@link #summarizeWithLoop(List)} nhưng cài bằng Stream, không dùng biến
     * gom nhóm bị sửa đổi (mutate) dùng chung giữa các phần tử của stream.
     */
    public List<CustomerSummary> summarizeWithStream(List<Order> orders) {
        // SOLUTION-BEGIN throw B4
        Map<String, List<Order>> byCustomer = orders.stream()
                .filter(order -> order.status() != Order.Status.CANCELLED)
                .collect(Collectors.groupingBy(Order::customerId, java.util.LinkedHashMap::new, Collectors.toList()));
        return byCustomer.entrySet().stream()
                .map(entry -> {
                    List<Order> customerOrders = entry.getValue();
                    BigDecimal totalPaid = customerOrders.stream()
                            .filter(order -> order.status() == Order.Status.PAID)
                            .map(Order::amount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    Instant firstInstant = customerOrders.stream()
                            .map(Order::createdAt)
                            .min(Comparator.naturalOrder())
                            .orElseThrow();
                    LocalDate firstOrderDate = firstInstant.atZone(reportZone).toLocalDate();
                    return new CustomerSummary(entry.getKey(), customerOrders.size(), totalPaid, firstOrderDate);
                })
                .sorted(Comparator.comparing(CustomerSummary::customerId))
                .toList();
        // SOLUTION-END
    }
}

/* ANSWER B4:
 * SOLUTION-BEGIN
 * Hai bản cho cùng kết quả nhưng khác cách quản lý trạng thái. Bản vòng lặp dùng các biến
 * cục bộ có thể gán lại (totalPaid, earliest) bên trong một for — dễ đọc tuần tự, nhưng nếu
 * lỡ khai báo biến đó ở ngoài stream/forEach và để nhiều luồng cùng sửa thì sẽ có race; ở đây
 * vẫn an toàn vì mỗi vòng lặp qua customerOrders là tuần tự, đơn luồng. Bản Stream không có
 * biến nào bị "sửa dần" qua nhiều bước: totalPaid được tính bằng reduce (mỗi bước tạo giá trị
 * mới, không mutate), firstInstant bằng min(), rồi map() tạo trực tiếp một CustomerSummary bất
 * biến cho mỗi khách — không có forEach nào ghi vào một List/Map bên ngoài dùng chung. Nhược
 * điểm của bản Stream: nhiều bước trung gian (groupingBy → entrySet().stream() → map) khó
 * debug từng dòng hơn vòng lặp, và groupingBy tốn thêm một Map trung gian; ưu điểm là an toàn
 * hơn khi sau này người khác cố "tối ưu" bằng parallel Stream (không có state mutable dùng
 * chung nên song song hoá không gây lỗi, trong khi bản vòng lặp nếu song song hoá ẩu sẽ hỏng
 * ngay tại các biến totalPaid/earliest bị nhiều luồng cùng gán).
 * SOLUTION-END
 */
