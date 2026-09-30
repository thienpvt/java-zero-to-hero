package phase03.d01_srp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_OrderServiceSplitTest {

    private static final String HINT_Q1 =
            "SRP nói về lý do để thay đổi. Một class có nhiều method vẫn có thể chỉ có một nhóm trách nhiệm.";
    private static final String HINT_Q3 =
            "Độ dài chỉ là triệu chứng. Nguyên nhân là các phần của class không cùng phục vụ một nhóm trách nhiệm.";

    private static final Ex01_OrderServiceSplit.OrderRequest ORDER =
            new Ex01_OrderServiceSplit.OrderRequest("alice",
                    List.of(new Ex01_OrderServiceSplit.LineItem("book", 1_000, 3),
                            new Ex01_OrderServiceSplit.LineItem("pen", 500, 2)),
                    true);

    @Test
    @DisplayName("Q1 dự đoán: SRP có nghĩa một class một method không")
    void q01_prediction() {
        assertPrediction("Q1_SRP_MEANS_ONE_METHOD", false,
                Ex01_OrderServiceSplit.Q1_SRP_MEANS_ONE_METHOD, HINT_Q1);
    }

    @Test
    @DisplayName("Q3 dự đoán: class quá lớn thể hiện dấu hiệu gì")
    void q03_prediction() {
        assertPrediction("Q3_LARGE_CLASS_SIGNAL", Ex01_OrderServiceSplit.Signal.LOW_COHESION,
                Ex01_OrderServiceSplit.Q3_LARGE_CLASS_SIGNAL, HINT_Q3);
    }

    @Test
    @DisplayName("Q5: tổng tiền tính tách khỏi luồng đặt hàng")
    void q05_totalCents() {
        assertEquals(4_000, Ex01_OrderServiceSplit.totalCents(ORDER.items()),
                "1000*3 + 500*2 phải bằng 4000.");
    }

    @Test
    @DisplayName("Q5: trạng thái tính tách khỏi luồng đặt hàng")
    void q05_statusFor() {
        assertEquals("VIP", Ex01_OrderServiceSplit.statusFor(ORDER));
    }

    @Test
    @DisplayName("Q5: thông báo sinh tách khỏi luồng đặt hàng")
    void q05_notificationsFor() {
        assertEquals(List.of("Đã đặt book", "Đã đặt pen"),
                Ex01_OrderServiceSplit.notificationsFor(ORDER));
    }

    @Test
    @DisplayName("Q5: luồng đặt hàng ghép ba trách nhiệm đã tách")
    void q05_placeOrder() {
        assertEquals("VIP:4000", Ex01_OrderServiceSplit.placeOrder(ORDER));
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_OrderServiceSplit.placeOrder(
                        new Ex01_OrderServiceSplit.OrderRequest("bob", List.of(), false)));
    }
}
