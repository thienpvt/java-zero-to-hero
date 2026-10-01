package phase03.d14_decorator;

import java.util.List;

/**
 * Decorator — Bài 1: logging và metrics quanh cùng service
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 14 (Decorator Pattern), câu 1, 2, 3, 4, 5.
 * Cần làm trước: d13_adapter.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_LoggingAndMetricsTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Decorator khác inheritance thế nào?
 *   Bắt đầu   : điền Q1_DECORATOR_VS_INHERITANCE bằng một giá trị của {@code Difference}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu ghép lúc runtime thay vì nổ tổ hợp subclass.
 * <p>
 * Q2 [DỰ ĐOÁN] Decorator khác Proxy ở mục đích gì?
 *   Bắt đầu   : điền Q2_DECORATOR_VS_PROXY bằng một giá trị của {@code Difference2}.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu decorator thêm hành vi, proxy kiểm soát truy cập.
 * <p>
 * Q3 [CODE] Có thể chain nhiều decorator không?
 *   Bắt đầu   : cài {@code LoggingPaymentService.pay} và {@code MetricsPaymentService.pay}.
 *               Mỗi bên ghi nhận rồi hoặc trước khi gọi {@code inner.pay} và trả nguyên kết quả.
 *   Kiểm chứng: chạy q03_bothSinksRecorded.
 *   Hoàn thành khi: q03_bothSinksRecorded xanh.
 * <p>
 * Q4 [DỰ ĐOÁN] Java I/O sử dụng ý tưởng decorator như thế nào?
 *   Bắt đầu   : điền Q4_JAVA_IO bằng một giá trị của {@code IoIdea}.
 *   Kiểm chứng: chạy q04_prediction. Ctrl+N mở {@code BufferedInputStream} xem constructor.
 *   Hoàn thành khi: q04_prediction xanh và ANSWER Q4 nêu bọc một stream bằng stream khác cùng interface.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Decorator có thể làm call stack khó hiểu như thế nào?
 *   Bắt đầu   : viết khối ANSWER Q5.
 *   Hoàn thành khi: ANSWER Q5 nêu phải đọc cấu hình ghép để biết thứ tự chạy.
 */
public class Ex01_LoggingAndMetrics {

    /** Decorator khác inheritance ở đâu. */
    public enum Difference {
        SAME_THING,
        COMPOSE_AT_RUNTIME_VS_SUBCLASS_EXPLOSION,
        FASTER_CALLS
    }

    /** Decorator khác Proxy ở đâu. */
    public enum Difference2 {
        SAME_THING,
        ADDS_BEHAVIOR_VS_CONTROLS_ACCESS,
        PROXY_IS_FASTER
    }

    /** Ý tưởng decorator trong Java I/O. */
    public enum IoIdea {
        ONE_STREAM_FOR_EVERYTHING,
        WRAP_A_STREAM_WITH_ANOTHER_OF_THE_SAME_INTERFACE,
        STREAMS_USE_INHERITANCE_ONLY
    }

    public interface PaymentService {

        String pay(long amountCents);
    }

    /** Cho sẵn: service thật, không biết logging hay metrics. */
    public static final class PlainPayment implements PaymentService {

        @Override
        public String pay(long amountCents) {
            return "paid:" + amountCents;
        }
    }

    // Q1 — decorator khác inheritance.
    static final Difference Q1_DECORATOR_VS_INHERITANCE = Difference.COMPOSE_AT_RUNTIME_VS_SUBCLASS_EXPLOSION; // SOLUTION-VALUE
    // Q2 — decorator khác proxy.
    static final Difference2 Q2_DECORATOR_VS_PROXY = Difference2.ADDS_BEHAVIOR_VS_CONTROLS_ACCESS; // SOLUTION-VALUE
    // Q4 — ý tưởng decorator trong Java I/O.
    static final IoIdea Q4_JAVA_IO = IoIdea.WRAP_A_STREAM_WITH_ANOTHER_OF_THE_SAME_INTERFACE; // SOLUTION-VALUE
    /** Ghi log trước khi chuyển tiếp. */
    public static final class LoggingPaymentService implements PaymentService {

        private final PaymentService inner;
        private final List<String> sink;

        public LoggingPaymentService(PaymentService inner, List<String> sink) {
            this.inner = inner;
            this.sink = sink;
        }

        @Override
        public String pay(long amountCents) {
            // SOLUTION-BEGIN throw Q3
            sink.add("log:" + amountCents);
            return inner.pay(amountCents);
            // SOLUTION-END
        }
    }

    /** Ghi metric sau khi chuyển tiếp. */
    public static final class MetricsPaymentService implements PaymentService {

        private final PaymentService inner;
        private final List<Long> sink;

        public MetricsPaymentService(PaymentService inner, List<Long> sink) {
            this.inner = inner;
            this.sink = sink;
        }

        @Override
        public String pay(long amountCents) {
            // SOLUTION-BEGIN throw Q3
            String result = inner.pay(amountCents);
            sink.add(amountCents);
            return result;
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Inheritance phải tạo một subclass cho mỗi tổ hợp: log, metric, log+metric.
 * Decorator ghép lúc runtime bằng cách bọc service này trong service khác cùng interface.
 * Thêm một hành vi là thêm một lớp, không thêm tổ hợp cho các hành vi đã có.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Decorator thêm hành vi cho lời gọi và luôn chuyển tiếp tới đối tượng bị bọc.
 * Proxy kiểm soát truy cập: có thể chặn, hoãn, hoặc tạo đối tượng đích khi cần.
 * Trong thực tế hai cái nhìn giống nhau về code, khác nhau ở ý định.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Java I/O bọc một stream bằng một stream khác cùng interface, ví dụ BufferedInputStream bọc InputStream.
 * Mỗi lớp thêm một hành vi như đệm hoặc nén mà vẫn là InputStream.
 * Muốn tổ hợp thì lồng nhiều lớp, không tạo subclass cho từng tổ hợp.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Call stack có nhiều tầng cùng tên method, nên đọc stack khó biết tầng nào vừa chạy.
 * Thứ tự chạy phụ thuộc thứ tự ghép trong cấu hình, không nằm trong một file dễ thấy.
 * Debug phải nhớ cấu hình ghép, và một decorator quên chuyển tiếp sẽ im lặng cắt chuỗi.
 * SOLUTION-END
 */
