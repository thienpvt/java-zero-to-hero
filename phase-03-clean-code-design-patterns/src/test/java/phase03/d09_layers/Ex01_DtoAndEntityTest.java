package phase03.d09_layers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static phase03.support.Predictions.assertPrediction;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_DtoAndEntityTest {

    private static final String HINT_Q1 =
            "Entity đi theo schema; đổi schema là đổi luôn API nếu expose trực tiếp.";
    private static final String HINT_Q2 =
            "DTO là contract của API, tách khỏi mô hình lưu trữ.";

    private static final Ex01_DtoAndEntity.OrderEntity ENTITY =
            new Ex01_DtoAndEntity.OrderEntity("o-1", 4_000, "ghi chú nội bộ");

    @Test
    @DisplayName("Q1 dự đoán: expose entity trực tiếp có rủi ro gì")
    void q01_prediction() {
        assertPrediction("Q1_EXPOSE_ENTITY", Ex01_DtoAndEntity.Risk.SCHEMA_CHANGE_BREAKS_API,
                Ex01_DtoAndEntity.Q1_EXPOSE_ENTITY, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: DTO giải quyết vấn đề gì")
    void q02_prediction() {
        assertPrediction("Q2_DTO_PURPOSE",
                Ex01_DtoAndEntity.Purpose.SEPARATE_API_CONTRACT_FROM_STORAGE_MODEL,
                Ex01_DtoAndEntity.Q2_DTO_PURPOSE, HINT_Q2);
    }

    @Test
    @DisplayName("Q2: mapper che field nội bộ")
    void q02_mapperHidesInternalField() {
        Ex01_DtoAndEntity.OrderResponse response = Ex01_DtoAndEntity.OrderMapper.toResponse(ENTITY);
        assertEquals("o-1", response.id());
        assertEquals(4_000, response.totalCents());
        assertFalse(Arrays.stream(Ex01_DtoAndEntity.OrderResponse.class.getRecordComponents())
                        .map(RecordComponent::getName)
                        .toList()
                        .contains("internalNote"),
                "OrderResponse không được có field internalNote.");
    }

    @Test
    @DisplayName("Q6: response chỉ có field của contract")
    void q06_responseHasOnlyContractFields() {
        assertEquals(List.of("id", "totalCents"),
                Arrays.stream(Ex01_DtoAndEntity.OrderResponse.class.getRecordComponents())
                        .map(RecordComponent::getName).toList());
        assertEquals(1, Ex01_DtoAndEntity.OrderMapper.toResponses(List.of(ENTITY)).size());
        assertEquals(List.of("o-1"), Ex01_DtoAndEntity.OrderMapper.toResponses(List.of(ENTITY))
                .stream().map(Ex01_DtoAndEntity.OrderResponse::id).toList());
    }
}
