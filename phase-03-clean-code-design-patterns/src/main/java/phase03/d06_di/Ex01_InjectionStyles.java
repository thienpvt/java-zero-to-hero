package phase03.d06_di;

/**
 * DI — Bài 1: ba cách đưa dependency vào
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 6 (Dependency Injection), câu 1, 2, 3, 4, 6, 7.
 * Cần làm trước: d05_dip.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_InjectionStylesTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] DI giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q1_DI_PROBLEM bằng một giá trị của {@code Problem}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nói object không tự dựng dependency của mình.
 * <p>
 * Q2 [TỰ TRẢ LỜI] DI và IoC khác nhau thế nào?
 *   Bắt đầu   : viết khối ANSWER Q2.
 *   Hoàn thành khi: ANSWER Q2 nói IoC là chiều đảo quyền điều khiển, DI là một cách thực hiện.
 * <p>
 * Q3 [DỰ ĐOÁN] Vì sao constructor injection thường được ưu tiên?
 *   Bắt đầu   : điền Q3_CTOR_PREFERRED bằng một giá trị của {@code Why}.
 *   Kiểm chứng: chạy q03_prediction và q03_ctorFailsFast.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nêu object không tồn tại ở trạng thái thiếu dependency.
 * <p>
 * Q4 [DỰ ĐOÁN] Field injection gây khó khăn gì cho unit test?
 *   Bắt đầu   : điền Q4_FIELD_INJECTION_TEST_PAIN.
 *   Kiểm chứng: chạy q04_prediction.
 *   Hoàn thành khi: q04_prediction xanh và ANSWER Q4 nêu phải reflection hoặc container mới đặt được dependency.
 * <p>
 * Q6 [DỰ ĐOÁN] DI container có phải điều kiện bắt buộc để áp dụng DI không?
 *   Bắt đầu   : điền Q6_CONTAINER_REQUIRED.
 *   Kiểm chứng: chạy q06_prediction.
 *   Hoàn thành khi: q06_prediction xanh.
 * <p>
 * Q7 [CODE] Có thể thực hiện DI bằng Java thuần không?
 *   Bắt đầu   : cài {@code ReportService.render} và {@code ReportService.withClock}.
 *               {@code render} chỉ gọi {@code clock.now()}; {@code withClock} trả một service mới.
 *   Kiểm chứng: chạy q07_renderUsesInjectedClock và q07_withClockSwaps.
 *   Hoàn thành khi: hai test xanh mà không có thư viện nào ngoài JDK.
 */
public class Ex01_InjectionStyles {

    /** Vấn đề DI giải quyết. */
    public enum Problem {
        SLOW_STARTUP,
        OBJECT_BUILDS_ITS_OWN_DEPENDENCIES,
        FEWER_CLASSES
    }

    /** Lý do constructor injection được ưu tiên. */
    public enum Why {
        FASTER_RUNTIME,
        FAILS_FAST_AND_ALWAYS_COMPLETE,
        SHORTER_SIGNATURE
    }

    public interface Clock {

        String now();
    }

    public static final class FixedClock implements Clock {

        private final String value;

        public FixedClock(String value) {
            this.value = value;
        }

        @Override
        public String now() {
            return value;
        }
    }

    // Q1 — DI giải quyết vấn đề nào.
    static final Problem Q1_DI_PROBLEM = Problem.OBJECT_BUILDS_ITS_OWN_DEPENDENCIES; // SOLUTION-VALUE

    // Q3 — vì sao ưu tiên constructor injection.
    static final Why Q3_CTOR_PREFERRED = Why.FAILS_FAST_AND_ALWAYS_COMPLETE; // SOLUTION-VALUE

    // Q4 — field injection gây khó gì cho test.
    static final Boolean Q4_FIELD_INJECTION_TEST_PAIN = true; // SOLUTION-VALUE

    // Q6 — DI container có bắt buộc.
    static final Boolean Q6_CONTAINER_REQUIRED = false; // SOLUTION-VALUE

    /** Dependency bắt buộc, nhận qua constructor. */
    public static final class ReportService {

        private final Clock clock;

        public ReportService(Clock clock) {
            if (clock == null) {
                throw new IllegalArgumentException("Clock không được null.");
            }
            this.clock = clock;
        }

        public String render(String title) {
            // SOLUTION-BEGIN throw Q7
            return title + "@" + clock.now();
            // SOLUTION-END
        }

        /** Đổi clock cho lần dùng sau mà không sửa object hiện có. */
        public ReportService withClock(Clock other) {
            // SOLUTION-BEGIN throw Q7
            return new ReportService(other);
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * DI giải quyết việc object tự dựng dependency của mình bằng new.
 * Khi dependency đến từ ngoài, có thể thay bằng fake trong test và đổi cấu hình mà không sửa class.
 * Kèm theo là dependency của object trở nên thấy được ngay ở constructor.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * IoC là chiều đảo quyền điều khiển: framework gọi code của mình thay vì mình gọi framework.
 * DI là một cách thực hiện IoC ở mức dependency: ai đó đưa dependency vào thay vì object tự lấy.
 * DI không cần framework; IoC rộng hơn DI.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Constructor injection bắt buộc dependency phải có ngay khi tạo object.
 * Object không tồn tại ở trạng thái nửa vời, nên không có NullPointerException lúc gọi method.
 * Danh sách dependency hiện rõ ở signature, dài quá thì thấy ngay là class đang làm quá nhiều.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Field injection để field private không có đường đặt từ ngoài khi tạo object.
 * Test phải dùng reflection hoặc dựng cả container mới nhét được dependency.
 * Constructor injection cho test gọi new trực tiếp với fake, không cần hạ tầng nào.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Không. Container chỉ tự động hoá việc dựng và nối dependency.
 * DI bằng tay, một factory gọi new theo thứ tự, là DI đầy đủ và không cần thư viện.
 * Container đáng dùng khi graph lớn và cấu hình theo môi trường.
 * SOLUTION-END
 */
