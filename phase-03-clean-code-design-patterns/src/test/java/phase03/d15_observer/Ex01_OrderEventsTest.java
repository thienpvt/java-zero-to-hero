package phase03.d15_observer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_OrderEventsTest {

    private static final String HINT_Q1 =
            "Publisher không biết lớp cụ thể của subscriber; thêm subscriber là đăng ký thêm.";
    private static final String HINT_Q2 =
            "Observer sống trong process; broker giữ message bền ngoài process.";
    private static final String HINT_Q3 =
            "Lỗi của một listener phải được cô lập để các listener khác vẫn nhận sự kiện.";
    private static final String HINT_Q5 =
            "Observer synchronous cho listener thấy cùng thứ tự với lúc publish.";

    @Test
    @DisplayName("Q1 dự đoán: Observer giải quyết vấn đề gì")
    void q01_prediction() {
        assertPrediction("Q1_OBSERVER_PROBLEM",
                Ex01_OrderEvents.Problem.PUBLISHER_DOES_NOT_KNOW_CONCRETE_SUBSCRIBERS,
                Ex01_OrderEvents.Q1_OBSERVER_PROBLEM, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: Observer khác Kafka thế nào")
    void q02_prediction() {
        assertPrediction("Q2_OBSERVER_VS_KAFKA",
                Ex01_OrderEvents.Difference.IN_PROCESS_AND_LOST_IF_PROCESS_DIES,
                Ex01_OrderEvents.Q2_OBSERVER_VS_KAFKA, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: một listener lỗi vẫn gọi listener khác")
    void q03_prediction() {
        assertPrediction("Q3_ONE_LISTENER_FAILS", true,
                Ex01_OrderEvents.Q3_ONE_LISTENER_FAILS, HINT_Q3);
    }

    @Test
    @DisplayName("Q3: listener lỗi không chặn listener sau")
    void q03_oneFailureDoesNotStopOthers() {
        Ex01_OrderEvents.OrderPublisher publisher = new Ex01_OrderEvents.OrderPublisher();
        List<String> seen = new ArrayList<>();
        publisher.subscribe(orderId -> {
            throw new IllegalStateException("listener 1 hỏng");
        });
        publisher.subscribe(seen::add);
        assertThrows(IllegalStateException.class, () -> publisher.publish("o-1"));
        assertEquals(List.of("o-1"), seen, "Listener thứ hai vẫn phải nhận sự kiện.");
    }

    @Test
    @DisplayName("Q5 dự đoán: thứ tự sự kiện có quan trọng")
    void q05_prediction() {
        assertPrediction("Q5_ORDERING", Ex01_OrderEvents.Verdict.MATTERS_FOR_LISTENERS,
                Ex01_OrderEvents.Q5_ORDERING, HINT_Q5);
    }

    @Test
    @DisplayName("Q6: listener đã unsubscribe không nhận sự kiện")
    void q06_unsubscribedListenerMissesEvent() {
        Ex01_OrderEvents.OrderPublisher publisher = new Ex01_OrderEvents.OrderPublisher();
        List<String> seen = new ArrayList<>();
        Ex01_OrderEvents.OrderListener listener = seen::add;
        publisher.subscribe(listener);
        publisher.publish("o-1");
        assertEquals(1, publisher.listenerCount());
        publisher.unsubscribe(listener);
        assertEquals(0, publisher.listenerCount());
        publisher.publish("o-2");
        assertEquals(List.of("o-1"), seen, "Listener đã bỏ không được nhận o-2.");
    }
}
