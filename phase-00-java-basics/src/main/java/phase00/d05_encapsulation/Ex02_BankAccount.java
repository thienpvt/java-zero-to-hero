package phase00.d05_encapsulation;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Đóng gói — Bài 2: Tài khoản ngân hàng
 *
 * Nguồn: 00-java-basics-review.md, mục 5 (Class, object và đóng gói), câu 3.
 * Cần làm trước: Ex01_ConstructorAndGetter.
 * Cách làm: cài constructor, {@code deposit}, {@code withdraw}; chạy Ex02_BankAccountTest.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q3 [TỰ TRẢ LỜI + CODE] Vì sao {@code balance} của tài khoản nên là {@code private}?
 *   Bắt đầu   : cài ba khối TODO Q3. Getter {@code id()} và {@code balance()} đã cho sẵn, không thêm setter.
 *   Kiểm chứng: chạy q03_depositAndWithdraw. Thử viết {@code account.balance = ...} ngoài class để thấy lỗi đỏ
 *               (rồi xóa dòng đó). Debug một lần rút quá số dư.
 *   Code      : id không được trống; số dư đầu không âm (0 được). Nạp và rút phải là số dương
 *               ({@code signum() > 0}). Không rút quá số dư. Null → {@code NullPointerException}.
 *               Vi phạm còn lại → {@code IllegalArgumentException}. Tiền là {@code BigDecimal}.
 *   Hoàn thành khi: q03_* xanh; ANSWER Q3 giải thích caller không gán số dư tùy ý được.
 */
public class Ex02_BankAccount {

    private final String id;
    private BigDecimal balance;

    Ex02_BankAccount(String id, BigDecimal opening) {
        // SOLUTION-BEGIN throw Q3
        this.id = Objects.requireNonNull(id, "id");
        if (id.isBlank()) {
            throw new IllegalArgumentException("id trống");
        }
        this.balance = Objects.requireNonNull(opening, "opening");
        if (opening.signum() < 0) {
            throw new IllegalArgumentException("số dư đầu âm");
        }
        // SOLUTION-END
    }

    void deposit(BigDecimal amount) {
        // SOLUTION-BEGIN throw Q3
        Objects.requireNonNull(amount, "amount");
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("chỉ nạp số dương");
        }
        balance = balance.add(amount);
        // SOLUTION-END
    }

    void withdraw(BigDecimal amount) {
        // SOLUTION-BEGIN throw Q3
        Objects.requireNonNull(amount, "amount");
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("chỉ rút số dương");
        }
        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException("rút quá số dư");
        }
        balance = balance.subtract(amount);
        // SOLUTION-END
    }

    String id() {
        return id;
    }

    BigDecimal balance() {
        return balance;
    }

    /* ANSWER Q3:
     * SOLUTION-BEGIN
     * balance private thì file khác không viết được account.balance = một số âm. Mọi thay đổi
     * phải đi qua deposit/withdraw, nơi số tiền được kiểm tra. Getter trả BigDecimal — kiểu này
     * bất biến — nên caller giữ bản sao giá trị cũng không sửa được số dư bên trong.
     * SOLUTION-END
     */
}
