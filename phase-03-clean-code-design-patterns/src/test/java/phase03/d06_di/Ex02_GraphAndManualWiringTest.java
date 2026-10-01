package phase03.d06_di;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_GraphAndManualWiringTest {

    private static final String HINT_Q5 =
            "Nhiều dependency thường là nhiều nhóm trách nhiệm, không phải lý do để thêm container.";

    static final class RecordingRepository implements Ex02_GraphAndManualWiring.OrderRepository {

        final List<String> saved = new ArrayList<>();

        @Override
        public void save(String order) {
            saved.add(order);
        }
    }

    static final class RecordingNotifier implements Ex02_GraphAndManualWiring.Notifier {

        final List<String> messages = new ArrayList<>();

        @Override
        public void notify(String message) {
            messages.add(message);
        }
    }

    @Test
    @DisplayName("Q5 dự đoán: 12 dependencies là dấu hiệu gì")
    void q05_prediction() {
        assertPrediction("Q5_TWELVE_DEPS_SIGNAL",
                Ex02_GraphAndManualWiring.Signal.MANY_RESPONSIBILITIES,
                Ex02_GraphAndManualWiring.Q5_TWELVE_DEPS_SIGNAL, HINT_Q5);
    }

    @Test
    @DisplayName("Q5: build nối graph và không cần container")
    void q05_buildWiresGraph() {
        RecordingRepository repository = new RecordingRepository();
        RecordingNotifier notifier = new RecordingNotifier();
        Ex02_GraphAndManualWiring.OrderApplicationService service =
                Ex02_GraphAndManualWiring.OrderGraph.build(base -> base + 100, repository, notifier);
        assertEquals(1_100, service.place(1_000));
        assertEquals(List.of("order:1100"), repository.saved);
        assertEquals(List.of("Đã đặt order:1100"), notifier.messages);
        assertEquals(List.of("RecordingRepository", "RecordingNotifier"), service.dependencies().subList(1, 3));
    }
}
