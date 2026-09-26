package phase01.d13_reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Reflection — Bài 2: MiniContainer — dependency injection bằng reflection
 *
 * Nguồn: 01-java-core-advanced.md, mục 13 (Reflection), câu 2, 3, 6.
 * Cần làm trước: Ex01_InspectAndInvoke (getDeclaredMethod, setAccessible, invoke).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_MiniDiContainerTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q2 [CODE + TỰ TRẢ LỜI] Spring có thể sử dụng reflection ở đâu?
 *   Bắt đầu   : đọc các class mẫu Repo/Service/Controller/CycleA/CycleB/TwoConstructors bên
 *               dưới; cài đặt MiniContainer.get(Class) theo Javadoc của method.
 *   Kiểm chứng: Ctrl+N mở java.lang.reflect.Constructor, Ctrl+F12 xem newInstance(Object...);
 *               Debug q02_get_buildsFullDependencyChain, F7 Step Into vào constructor.newInstance
 *               để thấy chuỗi Repo → Service → Controller được dựng đệ quy.
 *   Code      : MiniContainer.get(Class<T> type) — singleton lười (cache theo type); lấy
 *               getConstructors() (chỉ constructor public); nhiều hơn một → IllegalArgumentException;
 *               giải từng tham số bằng cách gọi get() đệ quy; interface/abstract → IllegalArgumentException;
 *               đang giải một type mà type đó lại xuất hiện lại (vòng lặp) → IllegalStateException
 *               với message chứa tên cả hai class trong vòng lặp.
 *   Hoàn thành khi: các test q02_* xanh; viết xong khối ANSWER Q2 nêu được ít nhất 2 nơi Spring
 *               dùng reflection ngoài constructor injection (ví dụ đọc annotation, tạo proxy).
 *
 * Q3 [DỰ ĐOÁN] Reflection có nhược điểm gì?
 *   Bắt đầu   : điền hằng Q3_COMPILER_CHECKS_REFLECTIVE_CALL_NAMES (thay null); xem lại Q5 ở
 *               Ex01 (getDeclaredMethod với tên sai vẫn biên dịch được, chỉ lỗi lúc chạy).
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_* xanh; kể được ít nhất 3 nhược điểm: mất compile-time checking, chậm
 *               hơn gọi trực tiếp, phá vỡ encapsulation (setAccessible vượt qua private/final).
 *
 * Q6 [THÍ NGHIỆM + TỰ TRẢ LỜI] Vì sao business code thông thường không nên lạm dụng reflection?
 *   Bắt đầu   : chạy main() bên dưới (Shift+F10) để xem báo cáo thời gian gọi trực tiếp so với
 *               Method.invoke và MethodHandle.invokeExact trên cùng một method square(int).
 *   Kiểm chứng: chạy q06_experimentRuns (smoke test, n nhỏ); muốn số liệu rõ hơn thì chạy main
 *               với n lớn hơn và so sánh nhiều lần (kết quả không tất định, không dùng để assert).
 *   Hoàn thành khi: q06_* xanh; viết xong khối OBSERVATION Q6 (số liệu quan sát được khi tự chạy
 *               main trên máy bạn) và khối ANSWER Q6 (giải thích bằng lời, có nêu trade-off).
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

        Repo repo() {
            return repo;
        }
    }

    static final class Controller {
        private final Service service;

        public Controller(Service service) {
            this.service = service;
        }

        Service service() {
            return service;
        }
    }

    static final class CycleA {
        private final CycleB b;

        public CycleA(CycleB b) {
            this.b = b;
        }

        CycleB b() {
            return b;
        }
    }

    static final class CycleB {
        private final CycleA a;

        public CycleB(CycleA a) {
            this.a = a;
        }

        CycleA a() {
            return a;
        }
    }

    static final class TwoConstructors {
        public TwoConstructors() {
        }

        public TwoConstructors(Repo r) {
        }
    }

    // Q3 — sự thật cố định của reflection trong Java: xem lại Q5 ở Ex01 (getDeclaredMethod với
    // tên "revael" gõ sai vẫn biên dịch được, javac không hề kiểm tra chuỗi tên method).
    static final Boolean Q3_COMPILER_CHECKS_REFLECTIVE_CALL_NAMES = false; // SOLUTION-VALUE

    /**
     * Container DI tối giản: {@link #get(Class)} tự dựng instance bằng constructor public duy
     * nhất của {@code type}, đệ quy giải từng tham số constructor, cache lại thành singleton.
     *
     * <p>Không hỗ trợ field/setter injection, chỉ constructor injection — đủ để minh họa cách
     * Spring dùng reflection dựng bean.
     */
    static final class MiniContainer {
        private final Map<Class<?>, Object> singletons = new HashMap<>();
        private final Set<Class<?>> resolving = new LinkedHashSet<>();

        /**
         * Trả về (và cache) instance duy nhất của {@code type}.
         *
         * @throws IllegalArgumentException nếu {@code type} là interface/abstract class, hoặc
         *                                   có khác đúng một constructor public
         * @throws IllegalStateException    nếu {@code type} phụ thuộc (trực tiếp hay gián tiếp)
         *                                   vào chính nó — message chứa tên mọi class trong vòng lặp
         */
        <T> T get(Class<T> type) {
            // SOLUTION-BEGIN throw Q2
            Object cached = singletons.get(type);
            if (cached != null) {
                return type.cast(cached);
            }
            if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
                throw new IllegalArgumentException(
                        "Không thể khởi tạo interface/abstract class: " + type.getName());
            }
            if (resolving.contains(type)) {
                throw cycleException(type);
            }
            resolving.add(type);
            try {
                Constructor<?>[] constructors = type.getConstructors();
                if (constructors.length != 1) {
                    throw new IllegalArgumentException(type.getSimpleName()
                            + " phải có đúng một constructor public, hiện có " + constructors.length + ".");
                }
                Constructor<?> constructor = constructors[0];
                Class<?>[] paramTypes = constructor.getParameterTypes();
                Object[] args = new Object[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    args[i] = get(paramTypes[i]);
                }
                Object instance = constructor.newInstance(args);
                singletons.put(type, instance);
                return type.cast(instance);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(
                        "Không khởi tạo được " + type.getName() + " bằng reflection.", e);
            } finally {
                resolving.remove(type);
            }
            // SOLUTION-END
        }

        private IllegalStateException cycleException(Class<?> type) {
            StringBuilder chain = new StringBuilder();
            for (Class<?> c : resolving) {
                chain.append(c.getSimpleName()).append(" -> ");
            }
            chain.append(type.getSimpleName());
            return new IllegalStateException("Phát hiện vòng lặp dependency: " + chain);
        }
    }

    /** Cho sẵn: method đơn giản dùng để so 3 cách gọi ở Q6 (trực tiếp/Method.invoke/MethodHandle). */
    static int square(int x) {
        return x * x;
    }

    /**
     * Đo thô (không thay cho benchmark thật) thời gian gọi {@code n} lần method {@link #square(int)}
     * bằng ba cách: gọi trực tiếp, {@link Method#invoke}, và {@link MethodHandle#invokeExact}. Có
     * vòng warm-up trước khi đo để JIT kịp làm nóng. Giai đoạn 2 sẽ học đo đúng cách bằng JMH.
     */
    static String runExperiment(int n) {
        try {
            Method method = Ex02_MiniDiContainer.class.getDeclaredMethod("square", int.class);
            MethodHandle handle = MethodHandles.lookup()
                    .findStatic(Ex02_MiniDiContainer.class, "square", MethodType.methodType(int.class, int.class));

            for (int i = 0; i < 2_000; i++) {
                square(i);
                method.invoke(null, i);
                int warmupResult = (int) handle.invokeExact(i);
                if (warmupResult < 0) {
                    throw new AssertionError("square(int) không thể âm.");
                }
            }

            long sumDirect = 0;
            long startDirect = System.nanoTime();
            for (int i = 0; i < n; i++) {
                sumDirect += square(i);
            }
            long directNanos = System.nanoTime() - startDirect;

            long sumReflect = 0;
            long startReflect = System.nanoTime();
            for (int i = 0; i < n; i++) {
                sumReflect += (int) method.invoke(null, i);
            }
            long reflectNanos = System.nanoTime() - startReflect;

            long sumHandle = 0;
            long startHandle = System.nanoTime();
            for (int i = 0; i < n; i++) {
                sumHandle += (int) handle.invokeExact(i);
            }
            long handleNanos = System.nanoTime() - startHandle;

            return "n=" + n + " tổng(direct=" + sumDirect + ", reflect=" + sumReflect + ", handle=" + sumHandle
                    + ") | Gọi trực tiếp: " + directNanos + " ns; Method.invoke: " + reflectNanos
                    + " ns; MethodHandle.invokeExact: " + handleNanos
                    + " ns (đo thô bằng System.nanoTime(), có warm-up; Giai đoạn 2 học đo đúng cách bằng JMH).";
        } catch (Throwable t) {
            throw new IllegalStateException("Không chạy được thí nghiệm reflection.", t);
        }
    }

    public static void main(String[] args) {
        System.out.println(runExperiment(1_000_000));
    }
}

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Ngoài dựng bean bằng constructor (như MiniContainer ở trên), Spring còn dùng reflection để:
 * (1) đọc annotation trên class/field/method (@Component, @Autowired, @Value...) để biết bean
 * nào cần tạo và dependency nào cần inject; (2) field/setter injection — set giá trị field private
 * bằng Field.set sau setAccessible(true), không qua constructor; (3) tạo proxy động (JDK dynamic
 * proxy hoặc CGLIB subclass) để chèn logic AOP như @Transactional, @Cacheable; (4) gọi method
 * lifecycle như @PostConstruct bằng Method.invoke. Trade-off: linh hoạt, giảm boilerplate, nhưng
 * lỗi cấu hình (thiếu bean, sai kiểu) chỉ lộ ra lúc chạy ứng dụng, không phải lúc biên dịch.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Reflection (Method.invoke) chậm hơn gọi trực tiếp vì mỗi lần gọi phải kiểm tra quyền truy cập,
 * đóng gói/mở gói tham số kiểu nguyên thủy qua Object[], và JIT khó inline qua lớp bọc phản chiếu
 * — dù JVM có "inflate" thành bytecode accessor sau nhiều lần gọi để đỡ chậm hơn. MethodHandle
 * (invokeExact) được thiết kế để JIT tối ưu tốt hơn Method.invoke, thường nhanh gần bằng gọi trực
 * tiếp sau warm-up. Ngoài tốc độ, reflection còn mất compile-time checking (tên sai chỉ lộ lúc
 * chạy — xem Q5 ở Ex01) và có thể phá encapsulation qua setAccessible. Vì vậy business code
 * thông thường nên gọi trực tiếp; reflection chỉ nên dùng ở lớp framework (DI, serialization) nơi
 * cần tính tổng quát và chấp nhận đổi tốc độ + an toàn kiểu để lấy sự linh hoạt.
 * SOLUTION-END
 */

/* OBSERVATION Q6:
 * SOLUTION-BEGIN
 * Chạy main() với n = 1 000 000 ba lần trên máy tác giả (JDK 21, sau warm-up 2 000 lần), đơn vị
 * nanosecond cho toàn bộ 1 000 000 lần gọi: gọi trực tiếp ~3 599 000–4 134 300 ns; Method.invoke
 * ~15 881 700–18 139 400 ns (chậm hơn trực tiếp khoảng 4–5 lần, ổn định qua các lần chạy); còn
 * MethodHandle.invokeExact ~3 399 000–5 192 500 ns — nằm sát gọi trực tiếp, có lần còn nhanh hơn
 * (nằm trong nhiễu đo đạc), khác hẳn Method.invoke luôn chậm rõ rệt. Kết quả khớp với lý do:
 * Method.invoke phải đóng/mở gói tham số qua Object[] và kiểm tra quyền truy cập ở mỗi lần gọi;
 * MethodHandle được JIT tối ưu gần bằng lệnh gọi tĩnh sau warm-up. Số ns tuyệt đối dao động giữa
 * các lần chạy (phụ thuộc máy, tải hệ thống) nên không hard-code vào test — chỉ dùng để quan sát
 * thứ tự tương đối, không dùng để assert chính xác.
 * SOLUTION-END
 */
