package phase02.d06_threads;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase02.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_InterruptAndDaemonTest {

    private static final String HINT_Q5 =
            "interrupt() chỉ đặt cờ và làm một số lời gọi đang chặn ném InterruptedException. Thread không bị dừng nếu thân code không hợp tác.";

    private static final String HINT_Q7 =
            "JVM thoát khi không còn thread non-daemon. Daemon không giữ tiến trình sống.";

    @Test
    @DisplayName("Q5 dự đoán: interrupt có buộc thread dừng ngay không")
    void q05_prediction() {
        assertPrediction("Q5_INTERRUPT_STOPS_IMMEDIATELY", false,
                Ex02_InterruptAndDaemon.Q5_INTERRUPT_STOPS_IMMEDIATELY, HINT_Q5);
    }

    @Test
    @DisplayName("Q6: khôi phục cờ interrupt đúng một lần, không nuốt InterruptedException")
    void q06_restoresInterruptFlagOnce() {
        Thread.interrupted();
        try {
            IllegalStateException thrown = null;
            try {
                Ex02_InterruptAndDaemon.rethrowOrRestore(() -> {
                    throw new InterruptedException("tác vụ bị ngắt");
                });
            } catch (IllegalStateException ex) {
                thrown = ex;
            }
            assertNotNull(thrown,
                    "Phải ném IllegalStateException và giữ InterruptedException làm cause.");
            assertInstanceOf(InterruptedException.class, thrown.getCause(),
                    "Cause phải là InterruptedException vừa bị ném từ body.");
            assertTrue(Thread.interrupted(),
                    "Sau khi xử lý InterruptedException, Thread.interrupted() phải trả true đúng một lần.");
            assertFalse(Thread.interrupted(),
                    "Lần gọi Thread.interrupted() tiếp theo phải trả false.");
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    @DisplayName("Q7: daemon() trả thread đã setDaemon(true)")
    void q07_daemon_isDaemon() {
        Thread worker = Ex02_InterruptAndDaemon.daemon();
        assertTrue(worker.isDaemon(), "daemon() phải trả thread có isDaemon() là true.");
    }

    @Test
    @DisplayName("Q7 dự đoán: daemon thread có giữ JVM sống không")
    void q07_prediction() {
        assertPrediction("Q7_DAEMON_KEEPS_JVM", false,
                Ex02_InterruptAndDaemon.Q7_DAEMON_KEEPS_JVM, HINT_Q7);
    }
}
