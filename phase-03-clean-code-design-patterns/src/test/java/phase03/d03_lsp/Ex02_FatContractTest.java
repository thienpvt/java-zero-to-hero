package phase03.d03_lsp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_FatContractTest {

    private static final String HINT_Q4 =
            "has-a thì composition; is-a thật sự thì mới inheritance.";

    @Test
    @DisplayName("Q4 dự đoán: composition tốt hơn inheritance khi nào")
    void q04_prediction() {
        assertPrediction("Q4_COMPOSITION_OVER_INHERITANCE", true,
                Ex02_FatContract.Q4_COMPOSITION_OVER_INHERITANCE, HINT_Q4);
    }

    @Test
    @DisplayName("Q4: DocumentPrinter chuyển tiếp cho Printer")
    void q04_printerDelegates() {
        Ex02_FatContract.DocumentPrinter documentPrinter =
                new Ex02_FatContract.DocumentPrinter(new Ex02_FatContract.ConsolePrinter());
        assertEquals("print:hoá đơn", documentPrinter.print("hoá đơn"));
    }
}
