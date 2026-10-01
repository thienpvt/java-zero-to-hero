package phase03.d04_isp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_MachineRolesTest {

    private static final String HINT_Q1 =
            "ISP nhắm vào việc client phải biết và phụ thuộc những method nó không gọi.";
    private static final String HINT_Q2 =
            "Nhỏ hơn chỉ tốt khi cắt theo vai trò. Cắt mỗi method một interface thì phải ghép nhiều type ở mọi chỗ dùng.";
    private static final String HINT_Q3 =
            "UnsupportedOperationException nghĩa là implementation bị ép hứa điều nó không làm được.";
    private static final String HINT_Q4 =
            "ISP là cohesion nhìn từ phía client của interface.";

    @Test
    @DisplayName("Q1 dự đoán: ISP giải quyết vấn đề gì")
    void q01_prediction() {
        assertPrediction("Q1_ISP_PURPOSE",
                Ex01_MachineRoles.Purpose.CLIENT_NOT_DEPEND_ON_UNUSED_METHODS,
                Ex01_MachineRoles.Q1_ISP_PURPOSE, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: interface càng nhỏ càng tốt")
    void q02_prediction() {
        assertPrediction("Q2_SMALLER_ALWAYS_BETTER", false,
                Ex01_MachineRoles.Q2_SMALLER_ALWAYS_BETTER, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: nhiều UnsupportedOperationException là dấu hiệu xấu")
    void q03_prediction() {
        assertPrediction("Q3_UNSUPPORTED_IS_SMELL", true,
                Ex01_MachineRoles.Q3_UNSUPPORTED_IS_SMELL, HINT_Q3);
    }

    @Test
    @DisplayName("Q4 dự đoán: ISP liên quan cohesion")
    void q04_prediction() {
        assertPrediction("Q4_ISP_IS_COHESION", true,
                Ex01_MachineRoles.Q4_ISP_IS_COHESION, HINT_Q4);
    }

    @Test
    @DisplayName("Q5: SimplePrinter chỉ in")
    void q05_simplePrinterOnlyPrints() {
        Ex01_MachineRoles.Printer printer = new Ex01_MachineRoles.SimplePrinter();
        assertEquals("print:hoá đơn", printer.print("hoá đơn"));
        assertEquals(1, Ex01_MachineRoles.SimplePrinter.class.getDeclaredMethods().length,
                "SimplePrinter chỉ được khai báo một method.");
    }

    @Test
    @DisplayName("Q5: AllInOne giữ cả ba vai trò")
    void q05_allInOneDoesAll() {
        Ex01_MachineRoles.Printer printer = new Ex01_MachineRoles.SimplePrinter();
        assertEquals("print:hoá đơn", printer.print("hoá đơn"));
        Ex01_MachineRoles.AllInOne allInOne = new Ex01_MachineRoles.AllInOne();
        assertEquals("scan:page-1", allInOne.scan());
        assertEquals("fax:0900", allInOne.fax("0900"));
    }
}
