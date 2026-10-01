package phase03.d06_di;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_InjectionStylesTest {

    private static final String HINT_Q1 =
            "DI giải quyết việc object tự gọi new cho dependency của nó.";
    private static final String HINT_Q3 =
            "Constructor bắt dependency phải có ngay, nên object không tồn tại ở trạng thái thiếu dependency.";
    private static final String HINT_Q4 =
            "Field private không có đường đặt từ ngoài khi tạo object, test phải reflection hoặc dựng container.";
    private static final String HINT_Q6 =
            "Container chỉ tự động hoá việc nối dependency; DI bằng tay vẫn là DI đầy đủ.";

    @Test
    @DisplayName("Q1 dự đoán: DI giải quyết vấn đề gì")
    void q01_prediction() {
        assertPrediction("Q1_DI_PROBLEM",
                Ex01_InjectionStyles.Problem.OBJECT_BUILDS_ITS_OWN_DEPENDENCIES,
                Ex01_InjectionStyles.Q1_DI_PROBLEM, HINT_Q1);
    }

    @Test
    @DisplayName("Q3 dự đoán: vì sao ưu tiên constructor injection")
    void q03_prediction() {
        assertPrediction("Q3_CTOR_PREFERRED",
                Ex01_InjectionStyles.Why.FAILS_FAST_AND_ALWAYS_COMPLETE,
                Ex01_InjectionStyles.Q3_CTOR_PREFERRED, HINT_Q3);
    }

    @Test
    @DisplayName("Q3: thiếu dependency thì tạo object đã thất bại")
    void q03_ctorFailsFast() {
        assertThrows(IllegalArgumentException.class,
                () -> new Ex01_InjectionStyles.ReportService(null));
    }

    @Test
    @DisplayName("Q4 dự đoán: field injection khó test")
    void q04_prediction() {
        assertPrediction("Q4_FIELD_INJECTION_TEST_PAIN", true,
                Ex01_InjectionStyles.Q4_FIELD_INJECTION_TEST_PAIN, HINT_Q4);
    }

    @Test
    @DisplayName("Q6 dự đoán: DI container có bắt buộc")
    void q06_prediction() {
        assertPrediction("Q6_CONTAINER_REQUIRED", false,
                Ex01_InjectionStyles.Q6_CONTAINER_REQUIRED, HINT_Q6);
    }

    @Test
    @DisplayName("Q7: render dùng clock được đưa vào")
    void q07_renderUsesInjectedClock() {
        Ex01_InjectionStyles.ReportService service =
                new Ex01_InjectionStyles.ReportService(new Ex01_InjectionStyles.FixedClock("2026-09-30"));
        assertEquals("doanh thu@2026-09-30", service.render("doanh thu"));
    }

    @Test
    @DisplayName("Q7: withClock trả service mới dùng clock mới")
    void q07_withClockSwaps() {
        Ex01_InjectionStyles.ReportService service =
                new Ex01_InjectionStyles.ReportService(new Ex01_InjectionStyles.FixedClock("t1"));
        Ex01_InjectionStyles.ReportService swapped =
                service.withClock(new Ex01_InjectionStyles.FixedClock("t2"));
        assertNotSame(service, swapped);
        assertEquals("x@t2", swapped.render("x"));
        assertEquals("x@t1", service.render("x"), "Service gốc phải giữ clock cũ.");
    }
}
