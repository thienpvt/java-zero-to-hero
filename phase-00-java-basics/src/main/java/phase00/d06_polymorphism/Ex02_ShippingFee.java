package phase00.d06_polymorphism;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Đa hình — Bài 2: Phí vận chuyển qua interface
 *
 * Nguồn: 00-java-basics-review.md, mục 6 (Kế thừa, đa hình, interface và abstract class), câu 4.
 * Cần làm trước: Ex01_Dispatch.
 * Cách làm: cài {@code fee} của hai class và {@code quote}; chạy Ex02_ShippingFeeTest.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q4 [TỰ TRẢ LỜI + CODE] Khi nào interface phù hợp hơn abstract class?
 *   Bắt đầu   : cài ba method đánh dấu TODO Q4. {@code quote} chỉ được gọi {@code policy.fee}.
 *   Kiểm chứng: chạy q04_quotesWithoutInstanceof. Nếu test implementation thứ ba fail, đặt breakpoint
 *               trong {@code quote} và xem có nhánh {@code instanceof} hay không.
 *   Code      : {@code FixedFee.fee} trả đúng số đã truyền khi {@code grams >= 0} và phí không âm;
 *               ngược lại {@code IllegalArgumentException}. {@code WeightFee.fee} tính
 *               {@code perKg * grams / 1000}, chia scale 0, {@code RoundingMode.HALF_UP}, cùng quy tắc âm.
 *               {@code quote} không kiểm tra tên class.
 *   Hoàn thành khi: q04_* xanh; ANSWER Q4 nói vì sao các policy này là interface chứ không phải abstract class.
 */
public class Ex02_ShippingFee {

    public interface ShippingFeePolicy {
        BigDecimal fee(int grams);
    }

    public static final class FixedFee implements ShippingFeePolicy {
        private final BigDecimal amount;

        public FixedFee(BigDecimal amount) {
            this.amount = Objects.requireNonNull(amount, "amount");
        }

        @Override
        public BigDecimal fee(int grams) {
            // SOLUTION-BEGIN throw Q4
            if (grams < 0 || amount.signum() < 0) {
                throw new IllegalArgumentException("grams và phí không được âm");
            }
            return amount;
            // SOLUTION-END
        }
    }

    public static final class WeightFee implements ShippingFeePolicy {
        private final BigDecimal perKg;

        public WeightFee(BigDecimal perKg) {
            this.perKg = Objects.requireNonNull(perKg, "perKg");
        }

        @Override
        public BigDecimal fee(int grams) {
            // SOLUTION-BEGIN throw Q4
            if (grams < 0 || perKg.signum() < 0) {
                throw new IllegalArgumentException("grams và phí không được âm");
            }
            return perKg.multiply(BigDecimal.valueOf(grams))
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            // SOLUTION-END
        }
    }

    static BigDecimal quote(ShippingFeePolicy policy, int grams) {
        // SOLUTION-BEGIN throw Q4
        Objects.requireNonNull(policy, "policy");
        return policy.fee(grams);
        // SOLUTION-END
    }

    /* ANSWER Q4:
     * SOLUTION-BEGIN
     * FixedFee và WeightFee không dùng chung field hay đoạn cài đặt, chỉ chung hợp đồng "tính phí từ số gram".
     * Interface đủ. Abstract class hợp hơn khi nhiều kiểu cần cùng state hoặc helper dùng field chung.
     * Một class chỉ extends được một class, nhưng implements được nhiều interface — thêm một lý do để
     * giữ policy là interface nếu sau này một kiểu vừa là policy vừa là thứ khác.
     * SOLUTION-END
     */
}
