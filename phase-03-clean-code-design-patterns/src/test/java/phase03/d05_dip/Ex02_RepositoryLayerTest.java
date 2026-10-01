package phase03.d05_dip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_RepositoryLayerTest {

    private static final String HINT_Q4 =
            "Interface mô tả nhu cầu nghiệp vụ nên ở gần domain; adapter nằm ở infrastructure và phụ thuộc vào nó.";

    @Test
    @DisplayName("Q4 dự đoán: repository interface ở layer nào")
    void q04_prediction() {
        assertPrediction("Q4_REPOSITORY_INTERFACE_LAYER",
                Ex02_RepositoryLayer.Layer.DOMAIN_OR_APPLICATION,
                Ex02_RepositoryLayer.Q4_REPOSITORY_INTERFACE_LAYER, HINT_Q4);
    }

    @Test
    @DisplayName("Q4: adapter in-memory thoả contract domain")
    void q04_inMemorySatisfiesDomainContract() {
        Ex02_RepositoryLayer.OrderRepository repository = new Ex02_RepositoryLayer.InMemoryOrders();
        repository.save(new Ex02_RepositoryLayer.OrderRecord("o-1", 4_000));
        assertEquals(4_000, repository.find("o-1").orElseThrow().totalCents());
    }

    @Test
    @DisplayName("Q6: save rồi find trả đúng bản ghi")
    void q06_saveThenFind() {
        Ex02_RepositoryLayer.InMemoryOrders orders = new Ex02_RepositoryLayer.InMemoryOrders();
        orders.save(new Ex02_RepositoryLayer.OrderRecord("o-2", 1_500));
        assertEquals(1_500, orders.find("o-2").orElseThrow().totalCents());
        assertTrue(orders.find("missing").isEmpty(), "Bản ghi không tồn tại phải trả Optional rỗng.");
    }
}
