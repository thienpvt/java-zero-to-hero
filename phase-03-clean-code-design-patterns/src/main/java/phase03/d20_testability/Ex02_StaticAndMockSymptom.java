package phase03.d20_testability;

import java.lang.reflect.Field;

/**
 * Testability — Bài 2: gọi hệ ngoài và mock quá nhiều
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 20, câu 3, 4.
 * Cần làm trước: Ex01_ExplicitDependencies.
 * Cách làm: chạy test trong Ex02_StaticAndMockSymptomTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q3 [DỰ ĐOÁN] Vì sao gọi trực tiếp external API trong business method làm unit test khó?
 *   Bắt đầu   : điền Q3_DIRECT_EXTERNAL_CALL bằng một giá trị của {@code Why}.
 *   Kiểm chứng: chạy q03_prediction và q03_injectedCallerHasReplaceableField.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nêu test phải dựng mạng hoặc chờ thật.
 * <p>
 * Q4 [DỰ ĐOÁN + CODE] Mock quá nhiều dependency có thể báo hiệu điều gì?
 *   Bắt đầu   : điền Q4_TOO_MANY_MOCKS bằng một giá trị của {@code Signal}, rồi cài
 *               {@code dependencyCount} đếm field không static của một lớp.
 *   Kiểm chứng: chạy q04_prediction và q04_dependencyCount.
 *   Hoàn thành khi: hai test xanh và ANSWER Q4 nêu class đang gộp nhiều trách nhiệm.
 */
public class Ex02_StaticAndMockSymptom {

    /** Vì sao gọi trực tiếp external API làm test khó. */
    public enum Why {
        SLOWER_COMPILATION,
        TEST_NEEDS_REAL_NETWORK_OR_WAITING,
        MORE_FILES
    }

    /** Mock quá nhiều dependency báo hiệu gì. */
    public enum Signal {
        FASTER_TESTS,
        CLASS_HAS_TOO_MANY_RESPONSIBILITIES,
        STRONG_TYPING
    }

    // Q3 — gọi trực tiếp external API làm test khó vì sao.
    static final Why Q3_DIRECT_EXTERNAL_CALL = Why.TEST_NEEDS_REAL_NETWORK_OR_WAITING; // SOLUTION-VALUE

    // Q4 — mock quá nhiều dependency báo hiệu gì.
    static final Signal Q4_TOO_MANY_MOCKS = Signal.CLASS_HAS_TOO_MANY_RESPONSIBILITIES; // SOLUTION-VALUE

    /** Cổng gọi hệ ngoài, thay được trong test. */
    public interface ApiGateway {

        String fetch(String key);
    }

    /** Cho sẵn: gọi static trực tiếp, không có đường thay thế. */
    public static final class DirectApiCaller {

        public String fetch(String key) {
            return StaticApi.fetch(key);
        }
    }

    /** Thay được vì dependency là field. */
    public static final class InjectedApiCaller {

        private final ApiGateway gateway;

        public InjectedApiCaller(ApiGateway gateway) {
            this.gateway = gateway;
        }

        public String fetch(String key) {
            return gateway.fetch(key);
        }
    }

    /** Đếm dependency, tức field không static. */
    public static int dependencyCount(Class<?> type) {
        // SOLUTION-BEGIN throw Q4
        int count = 0;
        for (Field field : type.getDeclaredFields()) {
            if (!field.isSynthetic() && !java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                count++;
            }
        }
        return count;
        // SOLUTION-END
    }

    /** Cho sẵn, không sửa: static gọi hệ ngoài. */
    static final class StaticApi {

        private StaticApi() {
        }

        static String fetch(String key) {
            return "static:" + key;
        }
    }
}

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Gọi trực tiếp external API nghĩa là test phải có mạng, tài khoản, và dữ liệu thật.
 * Kết quả phụ thuộc hệ ngoài nên test lúc xanh lúc đỏ mà không do code đổi.
 * Đường lỗi như timeout gần như không dựng lại được một cách tất định.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Mock quá nhiều dependency thường nghĩa là class gộp nhiều nhóm trách nhiệm.
 * Mỗi nhóm kéo theo vài collaborator phải mock riêng khi test.
 * Cách xử lý là tách class theo trách nhiệm, không phải viết thêm mock.
 * SOLUTION-END
 */
