package phase03.d14_decorator;

import java.util.List;

/**
 * Decorator — Bài 2: thứ tự ghép chuỗi
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 14, câu 3.
 * Cần làm trước: Ex01_LoggingAndMetrics.
 * Cách làm: chạy test trong Ex02_ChainOrderTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Ví dụ Q3 [DỰ ĐOÁN + CODE] Có thể chain nhiều decorator không?
 *   Bắt đầu   : điền Q3_CHAINABLE, rồi cài {@code wrap} theo thứ tự: trong cùng là service,
 *               bọc metrics, rồi bọc logging ngoài cùng.
 *   Kiểm chứng: chạy q03_prediction và q03_outerLogRunsFirst.
 *   Hoàn thành khi: hai test xanh và ANSWER Q3 nêu lớp ngoài cùng chạy trước.
 */
public class Ex02_ChainOrder {

    // Q3 — có ghép được nhiều decorator.
    static final Boolean Q3_CHAINABLE = true; // SOLUTION-VALUE

    /** Ghép metrics bên trong, logging bên ngoài. */
    public static Ex01_LoggingAndMetrics.PaymentService wrap(
            Ex01_LoggingAndMetrics.PaymentService inner, List<String> logSink, List<Long> metricSink) {
        // SOLUTION-BEGIN throw Q3
        Ex01_LoggingAndMetrics.PaymentService withMetrics =
                new Ex01_LoggingAndMetrics.MetricsPaymentService(inner, metricSink);
        return new Ex01_LoggingAndMetrics.LoggingPaymentService(withMetrics, logSink);
        // SOLUTION-END
    }
}

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Ghép được nhiều decorator vì mọi lớp cùng interface.
 * Thứ tự ghép quyết định thứ tự chạy: lớp ngoài cùng chạy trước khi gọi vào trong.
 * Vì vậy đổi thứ tự ghép là đổi hành vi quan sát được, dù cùng bộ decorator.
 * SOLUTION-END
 */
