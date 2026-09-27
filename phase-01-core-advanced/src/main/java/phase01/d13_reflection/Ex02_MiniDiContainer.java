package phase01.d13_reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Reflection — Bài 2: Mini DI container (tự viết) và chi phí gọi reflective
 *
 * Nguồn: 01-java-core-advanced.md, mục 13 (Reflection), câu 2, 3, 6.
 * Cần làm trước: Ex01_InspectAndInvoke (invokePrivate, hiểu setAccessible/invoke).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_MiniDiContainerTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q2 [CODE + TỰ TRẢ LỜI] Spring có thể sử dụng reflection ở đâu?
 *   Bắt đầu   : đọc các class mẫu Repo/Service/Controller/CycleA/CycleB/TwoConstructors
 *               bên dưới; cài đặt MiniContainer.get(Class&lt;T&gt;).
 *   Kiểm chứng: Ctrl+N → Class → Ctrl+F12 → getDeclaredConstructors, đặt breakpoint ngay
 *               đầu get(...), Debug q02_get_buildsFullDependencyChain, F7 để thấy đệ quy
 *               get(Repo.class) → get(Service.class) → get(Controller.class) xảy ra theo
 *               thứ tự nào; Alt+F8 Evaluate Expression trên resolving để xem tập class
 *               đang được dựng dở khi test vòng lặp chạy.
 *   Code      : static final class MiniContainer { &lt;T&gt; T get(Class&lt;T&gt; type) } —
 *               cache singleton theo Class; nếu type là interface/abstract → IAE; chọn
 *               constructor public duy nhất của type (getDeclaredConstructors lọc
 *               Modifier.isPublic, khác 1 kết quả → IAE); với mỗi tham số constructor,
 *               gọi get(paramType) đệ quy để lấy dependency (cũng được cache); nếu type
 *               đang được dựng dở mà bị get lại (đang có trong tập "resolving") → ISE có
 *               message chứa tên cả hai class trong vòng lặp; dựng instance bằng
 *               constructor.newInstance(args) rồi cache lại trước khi trả về.
 *   Hoàn thành khi: các test q02_* xanh; giải thích được bằng lời (khối ANSWER Q2) Spring
 *               dùng đúng cơ chế reflection tương tự (đọc constructor/field để autowire,
 *               tạo bean) ở những điểm nào.
 * <p>
 * Q3 [DỰ ĐOÁN] Reflection có nhược điểm gì?
 *   Bắt đầu   : điền Q3_COMPILER_CHECKS_REFLECTIVE_CALL_NAMES (thay null). Đối chiếu Q5 của
 *               Ex01: tên method/field truyền vào reflection API là String — tự xem javac
 *               có kiểm được lời gọi đó không, rồi mới điền.
 *   Kiểm chứng: chạy q03_*; nhìn lại IllegalArgumentException ở Q2 khi TwoConstructors có
 *               2 constructor public — đó là lỗi runtime, không phải lỗi compile, dù bạn
 *               gọi get(TwoConstructors.class) hoàn toàn hợp lệ về mặt cú pháp.
 *   Hoàn thành khi: test q03_* xanh; giải thích được (cùng Q6) các nhược điểm: mất type
 *               safety lúc compile, chậm hơn gọi trực tiếp, code khó đọc/khó debug, dễ vi
 *               phạm encapsulation.
 * <p>
 * Q6 [THÍ NGHIỆM + TỰ TRẢ LỜI] Vì sao business code thông thường không nên lạm dụng
 *     reflection?
 *   Bắt đầu   : chạy main() với n lớn (10_000_000), đọc báo cáo runExperiment so 3 cách
 *               gọi square(x): trực tiếp, Method.invoke, MethodHandle.invokeExact.
 *   Kiểm chứng: đặt breakpoint trong Method.invoke (Ctrl+N → Method → Ctrl+F12 → invoke),
 *               F7 vào MethodAccessor để thấy có một tầng gọi gián tiếp so với gọi trực
 *               tiếp; chạy q06_experimentRuns để thấy runExperiment luôn trả báo cáo có
 *               đủ ba nhãn.
 *   Hoàn thành khi: test q06_* xanh; viết xong khối OBSERVATION Q6 (số liệu từ lần bạn tự
 *               chạy main()) và ANSWER Q6 (trade-off tốc độ/độ linh hoạt).
 */
public class Ex02_MiniDiContainer {

    static final class Repo {
        public Repo() {
        }
    }

    static final class Service {
        private final Repo repo;

        public Service(Repo repo) {
            this.repo = repo;
        }

        Repo getRepo() {
            return repo;
        }
    }

    static final class Controller {
        private final Service service;

        public Controller(Service service) {
            this.service = service;
        }

        Service getService() {
            return service;
        }
    }

    static final class CycleA {
        private final CycleB b;

        public CycleA(CycleB b) {
            this.b = b;
        }

        CycleB getB() {
            return b;
        }
    }

    static final class CycleB {
        private final CycleA a;

        public CycleB(CycleA a) {
            this.a = a;
        }

        CycleA getA() {
            return a;
        }
    }

    static final class TwoConstructors {
        public TwoConstructors() {
        }

        public TwoConstructors(Repo r) {
        }
    }

    // Q3 — kịch bản: MiniDi gọi getDeclaredConstructor bằng Class lấy lúc chạy.
    // Hằng hỏi: javac có kiểm tra được tên/chữ ký lời gọi reflection đó không?
    // Đối chiếu Q5 của Ex01_InspectAndInvoke, rồi mới điền.
    static final Boolean Q3_COMPILER_CHECKS_REFLECTIVE_CALL_NAMES = null;

    /**
     * Container DI tí hon: lazy singleton theo {@link Class}, tự chọn constructor public
     * duy nhất và giải các tham số của nó bằng đệ quy.
     *
     * @throws IllegalArgumentException nếu {@code type} là interface/abstract class, hoặc
     *                                    không có đúng một constructor public
     * @throws IllegalStateException    nếu phát hiện vòng lặp phụ thuộc (message chứa tên
     *                                    các class trong vòng lặp), hoặc constructor ném
     *                                    lỗi khi {@code newInstance}
     */
    static final class MiniContainer {
        private final Map<Class<?>, Object> singletons = new HashMap<>();
        private final Set<Class<?>> resolving = new LinkedHashSet<>();

        <T> T get(Class<T> type) {
            throw new UnsupportedOperationException("TODO Q2");
        }
    }

    /** Cho sẵn: phép tính đơn giản dùng làm mục tiêu gọi bằng 3 cách khác nhau ở Q6. */
    static int square(int x) {
        return x * x;
    }

    /**
     * Cho sẵn: so sánh thời gian gọi {@link #square(int)} {@code n} lần bằng 3 cách — gọi
     * trực tiếp, {@link Method#invoke} bằng reflection, và {@link MethodHandle#invokeExact}
     * — có vòng warm-up trước khi đo, đo thô bằng {@link System#nanoTime()} (Giai đoạn 2 sẽ
     * học đo đúng cách bằng JMH). Trả về báo cáo dạng văn bản có đủ 3 nhãn.
     */
    static String runExperiment(int n) {
        try {
            Method reflectMethod = Ex02_MiniDiContainer.class.getDeclaredMethod("square", int.class);
            MethodHandle handle = MethodHandles.lookup()
                    .findStatic(Ex02_MiniDiContainer.class, "square", MethodType.methodType(int.class, int.class));

            // Warm-up: chạy vài nghìn lần trước để JIT có cơ hội biên dịch/inline.
            for (int i = 0; i < 2_000; i++) {
                square(i);
                reflectMethod.invoke(null, i);
                int ignored = (int) handle.invokeExact(i);
            }

            long directStart = System.nanoTime();
            long directSum = 0;
            for (int i = 0; i < n; i++) {
                directSum += square(i);
            }
            long directNanos = System.nanoTime() - directStart;

            long invokeStart = System.nanoTime();
            long invokeSum = 0;
            for (int i = 0; i < n; i++) {
                invokeSum += (int) reflectMethod.invoke(null, i);
            }
            long invokeNanos = System.nanoTime() - invokeStart;

            long handleStart = System.nanoTime();
            long handleSum = 0;
            for (int i = 0; i < n; i++) {
                handleSum += (int) handle.invokeExact(i);
            }
            long handleNanos = System.nanoTime() - handleStart;

            return "n=" + n + ", tong kiem tra (phai bang nhau)=" + directSum + "/" + invokeSum + "/" + handleSum
                    + "\nDirect              : " + directNanos + " ns\n"
                    + "Method.invoke       : " + invokeNanos + " ns\n"
                    + "MethodHandle.invokeExact: " + handleNanos + " ns\n";
        } catch (Throwable e) {
            throw new IllegalStateException("Lỗi khi chạy thí nghiệm Q6.", e);
        }
    }

    public static void main(String[] args) {
        System.out.println(runExperiment(10_000_000));
    }
}

/* ANSWER Q2:
 *
 */

/* OBSERVATION Q6:
 *
 */

/* ANSWER Q6:
 *
 */
