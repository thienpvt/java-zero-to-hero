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
            throw new UnsupportedOperationException("TODO Q4");
        }
    }

    public static final class WeightFee implements ShippingFeePolicy {
        private final BigDecimal perKg;

        public WeightFee(BigDecimal perKg) {
            this.perKg = Objects.requireNonNull(perKg, "perKg");
        }

        @Override
        public BigDecimal fee(int grams) {
            throw new UnsupportedOperationException("TODO Q4");
        }
    }

    static BigDecimal quote(ShippingFeePolicy policy, int grams) {
        throw new UnsupportedOperationException("TODO Q4");
    }

    /* ANSWER Q4:
     *
     */
}
