package phase01.d13_reflection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase01.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d13_reflection.Ex02_MiniDiContainer.Controller;
import phase01.d13_reflection.Ex02_MiniDiContainer.CycleA;
import phase01.d13_reflection.Ex02_MiniDiContainer.MiniContainer;
import phase01.d13_reflection.Ex02_MiniDiContainer.Service;
import phase01.d13_reflection.Ex02_MiniDiContainer.TwoConstructors;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_MiniDiContainerTest {

    @Test
    @DisplayName("Q2 get: dựng đủ chuỗi Repo -> Service -> Controller")
    void q02_get_buildsFullDependencyChain() {
        MiniContainer container = new MiniContainer();

        Controller controller = container.get(Controller.class);

        assertTrue(controller.service() != null, "Controller phải có Service được inject.");
        assertTrue(controller.service().repo() != null, "Service phải có Repo được inject.");
    }

    @Test
    @DisplayName("Q2 get: gọi hai lần trả về cùng một instance (singleton)")
    void q02_get_returnsSameSingletonInstance() {
        MiniContainer container = new MiniContainer();

        Controller first = container.get(Controller.class);
        Controller second = container.get(Controller.class);

        assertSame(first, second, "Hai lần get(Controller.class) phải trả về cùng một instance.");
    }

    @Test
    @DisplayName("Q2 get: Service bên trong Controller là cùng instance với get(Service.class)")
    void q02_get_sharesSameServiceInstanceAcrossGraph() {
        MiniContainer container = new MiniContainer();

        Controller controller = container.get(Controller.class);
        Service service = container.get(Service.class);

        assertSame(service, controller.service(),
                "Service lấy trực tiếp phải là cùng instance với Service nằm trong Controller.");
    }

    @Test
    @DisplayName("Q2 get: vòng lặp CycleA <-> CycleB báo lỗi có tên cả hai class")
    void q02_get_detectsCycleAndDoesNotHang() {
        MiniContainer container = new MiniContainer();

        IllegalStateException ex =
                assertThrows(IllegalStateException.class, () -> container.get(CycleA.class));

        assertTrue(ex.getMessage().contains("CycleA"), "Message phải chứa tên CycleA: " + ex.getMessage());
        assertTrue(ex.getMessage().contains("CycleB"), "Message phải chứa tên CycleB: " + ex.getMessage());
    }

    @Test
    @DisplayName("Q2 get: class có nhiều hơn một constructor public báo IllegalArgumentException")
    void q02_get_rejectsAmbiguousConstructors() {
        MiniContainer container = new MiniContainer();

        assertThrows(IllegalArgumentException.class, () -> container.get(TwoConstructors.class));
    }

    @Test
    @DisplayName("Q2 get: interface không thể khởi tạo báo IllegalArgumentException")
    void q02_get_rejectsInterface() {
        MiniContainer container = new MiniContainer();

        assertThrows(IllegalArgumentException.class, () -> container.get(Runnable.class));
    }

    @Test
    @DisplayName("Q3 dự đoán: compiler không kiểm tra tên method/field khi gọi qua reflection")
    void q03_prediction() {
        assertPrediction("Q3_COMPILER_CHECKS_REFLECTIVE_CALL_NAMES", false,
                Ex02_MiniDiContainer.Q3_COMPILER_CHECKS_REFLECTIVE_CALL_NAMES,
                "Xem lại Q5 ở Ex01: getDeclaredMethod với tên sai vẫn biên dịch được, chỉ lỗi lúc chạy.");
    }

    @Test
    @DisplayName("Q6 experiment: runExperiment trả báo cáo không rỗng, có cả 3 số đo")
    void q06_experimentRuns() {
        String report = Ex02_MiniDiContainer.runExperiment(1_000);

        assertFalse(report.isBlank(), "Báo cáo thí nghiệm không được rỗng.");
        assertTrue(report.contains("Method.invoke"), "Báo cáo phải nêu số đo của Method.invoke.");
        assertTrue(report.contains("MethodHandle.invokeExact"), "Báo cáo phải nêu số đo của MethodHandle.invokeExact.");
    }
}
