package phase03.d17_proxy;

import java.util.ArrayList;
import java.util.List;

/**
 * Proxy — Bài 1: kiểm soát truy cập
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 17 (Proxy Pattern), câu 1, 2, 5.
 * Cần làm trước: d16_template_method.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_ManualProxyTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Proxy khác Decorator thế nào?
 *   Bắt đầu   : điền Q1_PROXY_VS_DECORATOR bằng một giá trị của {@code Difference}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu proxy kiểm soát truy cập.
 * <p>
 * Q2 [DỰ ĐOÁN] Spring {@code @Transactional} có liên quan proxy như thế nào?
 *   Bắt đầu   : điền Q2_TRANSACTIONAL_PROXY bằng một giá trị của {@code Mechanism}.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu Spring bọc bean trong proxy để mở và đóng transaction.
 * <p>
 * Q3 [CODE] Proxy có thể thêm cross-cutting concern nào?
 *   Bắt đầu   : cài {@code GuardProxy.deposit} và {@code GuardProxy.balance}. Vai trò không phải
 *               {@code admin} thì ném {@code SecurityException} và không gọi tới account thật.
 *   Kiểm chứng: chạy q03_adminPassesAndOthersBlocked.
 *   Hoàn thành khi: q03_adminPassesAndOthersBlocked xanh.
 * <p>
 * Q5 [DỰ ĐOÁN] Proxy có thể thêm cross-cutting concern nào?
 *   Bắt đầu   : điền Q5_CROSS_CUTTING bằng một giá trị của {@code Concern}.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh.
 */
public class Ex01_ManualProxy {

    /** Proxy khác Decorator ở đâu. */
    public enum Difference {
        SAME_THING,
        CONTROLS_ACCESS_VS_ADDS_BEHAVIOR,
        PROXY_IS_SLOWER
    }

    /** Cơ chế đằng sau @Transactional. */
    public enum Mechanism {
        COMPILER_REWRITE,
        PROXY_AROUND_THE_BEAN,
        DATABASE_FEATURE
    }

    /** Cross-cutting concern proxy hay thêm. */
    public enum Concern {
        BUSINESS_PRICING,
        SECURITY_TRANSACTION_LAZY_LOADING,
        DOMAIN_VALIDATION
    }

    public interface Account {

        void deposit(long amountCents);

        String balance();
    }

    /** Cho sẵn: account thật, ghi lại các lời gọi. */
    public static final class RealAccount implements Account {

        public final List<String> audit = new ArrayList<>();
        private long balance;

        @Override
        public void deposit(long amountCents) {
            audit.add("deposit:" + amountCents);
            balance += amountCents;
        }

        @Override
        public String balance() {
            audit.add("balance");
            return "real:" + balance;
        }
    }

    // Q1 — proxy khác decorator.
    static final Difference Q1_PROXY_VS_DECORATOR = null;
    // Q2 — @Transactional liên quan proxy thế nào.
    static final Mechanism Q2_TRANSACTIONAL_PROXY = null;

    // Q5 — proxy hay thêm cross-cutting concern nào.
    static final Concern Q5_CROSS_CUTTING = null;

    /** Kiểm tra vai trò trước khi cho lời gọi đi qua. */
    public static final class GuardProxy implements Account {

        private final Account target;
        private final String role;

        public GuardProxy(Account target, String role) {
            this.target = target;
            this.role = role;
        }

        @Override
        public void deposit(long amountCents) {
            throw new UnsupportedOperationException("TODO Q3");
        }

        @Override
        public String balance() {
            throw new UnsupportedOperationException("TODO Q3");
        }

        private void requireAdmin() {
            if (!"admin".equals(role)) {
                throw new SecurityException("Không có quyền: " + role);
            }
        }
    }
}

/* ANSWER Q1:
 *
 */

/* ANSWER Q2:
 *
 */

/* ANSWER Q5:
 *
 */
