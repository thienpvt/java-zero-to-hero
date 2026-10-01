package phase03.d06_di;

import java.util.List;

/**
 * DI — Bài 2: graph và nối bằng tay
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 6, câu 5.
 * Cần làm trước: Ex01_InjectionStyles.
 * Cách làm: chạy test trong Ex02_GraphAndManualWiringTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q5 [DỰ ĐOÁN + CODE] Constructor có 12 dependencies cho thấy điều gì?
 *   Bắt đầu   : điền Q5_TWELVE_DEPS_SIGNAL, rồi cài {@code OrderGraph.build} nối ba dependency
 *               theo đúng thứ tự policy rồi repository rồi notifier.
 *   Kiểm chứng: chạy q05_prediction và q05_buildWiresGraph.
 *   Hoàn thành khi: hai test xanh và ANSWER Q5 nêu class đang gộp nhiều trách nhiệm.
 */
public class Ex02_GraphAndManualWiring {

    /** Dấu hiệu khi constructor có 12 dependencies. */
    public enum Signal {
        FAST_STARTUP,
        MANY_RESPONSIBILITIES,
        STRONG_TYPING
    }

    public interface PricingPolicy {

        long price(long baseCents);
    }

    public interface OrderRepository {

        void save(String order);
    }

    public interface Notifier {

        void notify(String message);
    }

    /** Nghiệp vụ nhỏ, ba dependency là đủ để thấy graph. */
    public static final class OrderApplicationService {

        private final PricingPolicy pricingPolicy;
        private final OrderRepository orderRepository;
        private final Notifier notifier;

        public OrderApplicationService(PricingPolicy pricingPolicy, OrderRepository orderRepository,
                Notifier notifier) {
            this.pricingPolicy = pricingPolicy;
            this.orderRepository = orderRepository;
            this.notifier = notifier;
        }

        public long place(long baseCents) {
            long total = pricingPolicy.price(baseCents);
            String order = "order:" + total;
            orderRepository.save(order);
            notifier.notify("Đã đặt " + order);
            return total;
        }

        public List<String> dependencies() {
            return List.of(pricingPolicy.getClass().getSimpleName(),
                    orderRepository.getClass().getSimpleName(),
                    notifier.getClass().getSimpleName());
        }
    }

    // Q5 — constructor 12 dependencies là dấu hiệu gì.
    static final Signal Q5_TWELVE_DEPS_SIGNAL = Signal.MANY_RESPONSIBILITIES; // SOLUTION-VALUE

    /** Nối graph bằng tay, không container. */
    public static final class OrderGraph {

        private OrderGraph() {
        }

        public static OrderApplicationService build(PricingPolicy pricingPolicy,
                OrderRepository orderRepository, Notifier notifier) {
            // SOLUTION-BEGIN throw Q5
            if (pricingPolicy == null || orderRepository == null || notifier == null) {
                throw new IllegalArgumentException("Mọi dependency phải khác null.");
            }
            return new OrderApplicationService(pricingPolicy, orderRepository, notifier);
            // SOLUTION-END
        }
    }
}

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Mười hai dependency là dấu hiệu class gộp nhiều nhóm trách nhiệm.
 * Mỗi nhóm kéo theo vài dependency riêng, và thay đổi ở nhóm này buộc sửa constructor.
 * Cách xử lý là tách theo trách nhiệm, không phải thêm một container để giấu signature.
 * SOLUTION-END
 */
