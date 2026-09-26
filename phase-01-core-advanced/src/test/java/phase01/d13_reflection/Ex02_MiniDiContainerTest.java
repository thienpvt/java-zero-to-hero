package phase01.d13_reflection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import phase01.d13_reflection.Ex02_MiniDiContainer.Repo;
import phase01.d13_reflection.Ex02_MiniDiContainer.Service;
import phase01.d13_reflection.Ex02_MiniDiContainer.TwoConstructors;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_MiniDiContainerTest {

    @Test
    @DisplayName("Q2 get: dựng đủ chuỗi Controller -> Service -> Repo")
    void q02_get_buildsFullDependencyChain() {
        MiniContainer container = new MiniContainer();

        Controller controller = container.get(Controller.class);

        assertNotNull(controller.getService(), "Controller phải có Service được inject.");
        assertNotNull(controller.getService().getRepo(), "Service phải có Repo được inject.");
    }

    @Test
    @DisplayName("Q2 get: gọi hai lần cùng type trả về đúng một instance (singleton)")
    void q02_get_returnsSameSingletonOnSecondCall() {
        MiniContainer container = new MiniContainer();

        Controller first = container.get(Controller.class);
        Controller second = container.get(Controller.class);

        assertSame(first, second, "Gọi get(Controller.class) hai lần phải trả về cùng một instance.");
    }

    @Test
    @DisplayName("Q2 get: Service bên trong Controller là cùng instance với get(Service.class) trực tiếp")
    void q02_get_serviceInsideControllerIsSameSingletonAsDirectGet() {
        MiniContainer container = new MiniContainer();

        Controller controller = container.get(Controller.class);
        Service directService = container.get(Service.class);

        assertSame(controller.getService(), directService,
                "Service dùng trong Controller phải là cùng singleton với get(Service.class) trực tiếp.");
    }

    @Test
    @DisplayName("Q2 get: vòng lặp phụ thuộc CycleA/CycleB ném IllegalStateException nêu tên cả hai class")
    void q02_get_detectsCycle_throwsIllegalStateExceptionWithBothClassNames() {
        MiniContainer container = new MiniContainer();

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> container.get(CycleA.class));

        assertTrue(thrown.getMessage().contains("CycleA"), "Message phải chứa tên class CycleA.");
        assertTrue(thrown.getMessage().contains("CycleB"), "Message phải chứa tên class CycleB.");
    }

    @Test
    @DisplayName("Q2 get: class có nhiều hơn một constructor public ném IllegalArgumentException")
    void q02_get_typeWithTwoPublicConstructors_throwsIllegalArgumentException() {
        MiniContainer container = new MiniContainer();

        assertThrows(IllegalArgumentException.class, () -> container.get(TwoConstructors.class));
    }

    @Test
    @DisplayName("Q2 get: interface không thể tự khởi tạo, ném IllegalArgumentException")
    void q02_get_interfaceType_throwsIllegalArgumentException() {
        MiniContainer container = new MiniContainer();

        assertThrows(IllegalArgumentException.class, () -> container.get(Runnable.class));
    }

    @Test
    @DisplayName("Q2 get: Repo không có dependency vẫn dựng được (constructor không tham số)")
    void q02_get_noDependencyType_buildsDirectly() {
        MiniContainer container = new MiniContainer();

        Repo repo = container.get(Repo.class);

        assertNotNull(repo, "Repo không phụ thuộc gì nên phải dựng được ngay.");
    }

    @Test
    @DisplayName("Q3 dự đoán: compiler không kiểm tra tên gọi reflective lúc biên dịch")
    void q03_duDoan_compilerChecksReflectiveCallNames() {
        // Cố định: xem lại Q5 của Ex01_InspectAndInvoke — tên method/field truyền cho
        // reflection API là String, javac không có cách nào kiểm tra tên đó tồn tại.
        boolean actual = false;

        assertPrediction("Q3_COMPILER_CHECKS_REFLECTIVE_CALL_NAMES", actual,
                Ex02_MiniDiContainer.Q3_COMPILER_CHECKS_REFLECTIVE_CALL_NAMES,
                "getDeclaredConstructor/getDeclaredMethod nhận String/Class, không phải lời gọi method"
                        + " thật nên javac không kiểm tra được tên hay chữ ký.");
    }

    @Test
    @DisplayName("Q6 thí nghiệm: runExperiment chạy và báo cáo đủ 3 cách gọi")
    void q06_experimentRuns() {
        String report = Ex02_MiniDiContainer.runExperiment(10_000);

        assertTrue(report.contains("Direct"), "Báo cáo phải có dòng cho cách gọi Direct.");
        assertTrue(report.contains("Method.invoke"), "Báo cáo phải có dòng cho Method.invoke.");
        assertTrue(report.contains("MethodHandle.invokeExact"), "Báo cáo phải có dòng cho MethodHandle.invokeExact.");
    }
}
