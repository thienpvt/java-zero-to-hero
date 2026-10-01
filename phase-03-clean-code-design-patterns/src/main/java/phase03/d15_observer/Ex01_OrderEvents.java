package phase03.d15_observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer — Bài 1: sự kiện đơn hàng
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 15 (Observer Pattern), câu 1, 2, 3, 4, 5, 6.
 * Cần làm trước: d14_decorator.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_OrderEventsTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Observer giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q1_OBSERVER_PROBLEM bằng một giá trị của {@code Problem}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu publisher không biết subscriber cụ thể.
 * <p>
 * Q2 [DỰ ĐOÁN] Observer khác message broker như Kafka thế nào?
 *   Bắt đầu   : điền Q2_OBSERVER_VS_KAFKA bằng một giá trị của {@code Difference}.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu trong process và mất khi process chết.
 * <p>
 * Q3 [DỰ ĐOÁN + CODE] Nếu một listener fail thì các listener khác nên xử lý thế nào?
 *   Bắt đầu   : điền Q3_ONE_LISTENER_FAILS, rồi cài {@code publish}. Một listener ném lỗi không được
 *               làm dừng các listener khác; lỗi phải được ghi lại và ném sau khi đã gọi hết.
 *   Kiểm chứng: chạy q03_prediction và q03_oneFailureDoesNotStopOthers.
 *   Hoàn thành khi: hai test xanh và ANSWER Q3 nêu cô lập lỗi theo từng listener.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Observer synchronous có thể ảnh hưởng latency ra sao?
 *   Bắt đầu   : viết khối ANSWER Q4.
 *   Hoàn thành khi: ANSWER Q4 nêu caller chờ tổng thời gian mọi listener.
 * <p>
 * Q5 [DỰ ĐOÁN] Event ordering có quan trọng không?
 *   Bắt đầu   : điền Q5_ORDERING bằng một giá trị của {@code Verdict}.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 nêu listener thấy cùng thứ tự với lúc publish.
 * <p>
 * Q6 [CODE] Listener không được unregister có thể gây vấn đề gì?
 *   Bắt đầu   : cài {@code unsubscribe}. Sau khi bỏ, listener đó không được nhận sự kiện nữa.
 *   Kiểm chứng: chạy q06_unsubscribedListenerMissesEvent.
 *   Hoàn thành khi: q06_unsubscribedListenerMissesEvent xanh và ANSWER Q6 nêu giữ tham chiếu làm rò rỉ.
 */
public class Ex01_OrderEvents {

    /** Vấn đề Observer giải quyết. */
    public enum Problem {
        FASTER_PUBLISH,
        PUBLISHER_DOES_NOT_KNOW_CONCRETE_SUBSCRIBERS,
        FEWER_CLASSES
    }

    /** Observer khác message broker ở đâu. */
    public enum Difference {
        SAME_THING,
        IN_PROCESS_AND_LOST_IF_PROCESS_DIES,
        KAFKA_IS_SYNCHRONOUS
    }

    /** Thứ tự sự kiện có quan trọng không. */
    public enum Verdict {
        NEVER_MATTERS,
        MATTERS_FOR_LISTENERS,
        ONLY_MATTERS_FOR_KAFKA
    }

    public interface OrderListener {

        void onOrder(String orderId);
    }

    // Q1 — Observer giải quyết vấn đề gì.
    static final Problem Q1_OBSERVER_PROBLEM = Problem.PUBLISHER_DOES_NOT_KNOW_CONCRETE_SUBSCRIBERS; // SOLUTION-VALUE
    // Q2 — Observer khác Kafka ở đâu.
    static final Difference Q2_OBSERVER_VS_KAFKA = Difference.IN_PROCESS_AND_LOST_IF_PROCESS_DIES; // SOLUTION-VALUE
    // Q3 — một listener lỗi thì các listener khác vẫn chạy.
    static final Boolean Q3_ONE_LISTENER_FAILS = true; // SOLUTION-VALUE

    // Q5 — thứ tự sự kiện có quan trọng.
    static final Verdict Q5_ORDERING = Verdict.MATTERS_FOR_LISTENERS; // SOLUTION-VALUE

    /** Gọi listener theo thứ tự đăng ký; lỗi được gom lại và ném sau. */
    public static final class OrderPublisher {

        private final List<OrderListener> listeners = new ArrayList<>();

        public void subscribe(OrderListener listener) {
            listeners.add(listener);
        }

        public void unsubscribe(OrderListener listener) {
            // SOLUTION-BEGIN throw Q6
            listeners.remove(listener);
            // SOLUTION-END
        }

        public int listenerCount() {
            return listeners.size();
        }

        public void publish(String orderId) {
            // SOLUTION-BEGIN throw Q3
            RuntimeException firstFailure = null;
            for (OrderListener listener : List.copyOf(listeners)) {
                try {
                    listener.onOrder(orderId);
                } catch (RuntimeException failure) {
                    if (firstFailure == null) {
                        firstFailure = failure;
                    } else {
                        firstFailure.addSuppressed(failure);
                    }
                }
            }
            if (firstFailure != null) {
                throw firstFailure;
            }
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Observer cho publisher thông báo nhiều subscriber mà không biết lớp cụ thể của họ.
 * Thêm subscriber là đăng ký thêm, không sửa publisher.
 * Nhờ vậy luồng chính không phụ thuộc số lượng hay loại người nghe.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Observer chạy trong cùng process và cùng bộ nhớ, lời gọi là gọi method.
 * Broker như Kafka nằm ngoài process, giữ message bền và cho nhiều consumer group.
 * Sự kiện observer mất khi process chết trước khi listener chạy xong.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Một listener lỗi không nên làm các listener khác bỏ lượt.
 * Mỗi lời gọi được bọc riêng, lỗi được gom lại và ném sau khi đã gọi hết.
 * Nếu để lỗi lan ngay, thứ tự đăng ký quyết định ai được nhận, rất khó đoán.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Observer synchronous làm caller chờ toàn bộ listener chạy xong.
 * Một listener chậm hoặc chờ I/O kéo latency của luồng chính.
 * Cách giảm là chỉ ghi sự kiện vào queue rồi xử lý ở luồng khác, đổi lại mất tính tức thời.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Có. Listener thấy sự kiện cùng thứ tự với lúc publish vì cùng một luồng.
 * Nếu xử lý bất đồng bộ, thứ tự đó không còn bảo đảm trừ khi có cơ chế riêng.
 * Nhiều nghiệp vụ, ví dụ created trước paid, phụ thuộc thứ tự này.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Listener không được unregister vẫn được publisher giữ tham chiếu.
 * Nếu listener đã hết dùng ở nơi khác, object không được thu hồi, đó là rò rỉ.
 * Publisher còn gọi vào listener đã chết logic, sinh lỗi hoặc việc thừa.
 * SOLUTION-END
 */
