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
    static final Problem Q1_OBSERVER_PROBLEM = null;
    // Q2 — Observer khác Kafka ở đâu.
    static final Difference Q2_OBSERVER_VS_KAFKA = null;
    // Q3 — một listener lỗi thì các listener khác vẫn chạy.
    static final Boolean Q3_ONE_LISTENER_FAILS = null;

    // Q5 — thứ tự sự kiện có quan trọng.
    static final Verdict Q5_ORDERING = null;

    /** Gọi listener theo thứ tự đăng ký; lỗi được gom lại và ném sau. */
    public static final class OrderPublisher {

        private final List<OrderListener> listeners = new ArrayList<>();

        public void subscribe(OrderListener listener) {
            listeners.add(listener);
        }

        public void unsubscribe(OrderListener listener) {
            throw new UnsupportedOperationException("TODO Q6");
        }

        public int listenerCount() {
            return listeners.size();
        }

        public void publish(String orderId) {
            throw new UnsupportedOperationException("TODO Q3");
        }
    }
}

/* ANSWER Q1:
 *
 */

/* ANSWER Q2:
 *
 */

/* ANSWER Q3:
 *
 */

/* ANSWER Q4:
 *
 */

/* ANSWER Q5:
 *
 */

/* ANSWER Q6:
 *
 */
