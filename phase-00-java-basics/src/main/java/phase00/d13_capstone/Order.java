package phase00.d13_capstone;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Bài tổng hợp — Order
 *
 * Nguồn: 00-java-basics-review.md, mục "Bài tổng hợp — Quản lý đơn hàng ở bộ nhớ", bước 1.
 * Cần làm trước: các mục 2, 4, 5, 8.
 * Cách làm: cài constructor và {@code changeStatus}, chạy OrderTest (Ctrl+Shift+F10).
 *
 * ─────────────────────────────────────────────────────────────────────
 * B1 [CODE + TỰ TRẢ LỜI] {@code Order} có id, mã khách, ngày tạo, tổng tiền và {@code Status}.
 *     Không cho phép tổng tiền âm.
 *   Bắt đầu   : cài hai khối TODO B1. Getter đã cho sẵn. Không thêm setter.
 *   Kiểm chứng: Debug q test tiền âm, breakpoint ở {@code total.signum()}. Rồi Debug một lần chuyển
 *               {@code NEW} → {@code DONE}.
 *   Code      : mọi field null → {@code NullPointerException}. Id hoặc mã khách trống →
 *               {@code IllegalArgumentException}. {@code total} âm → {@code IllegalArgumentException};
 *               {@code 0} hợp lệ. Chuyển trạng thái: {@code NEW} → {@code PAID} hoặc {@code CANCELLED};
 *               {@code PAID} → {@code SHIPPED} hoặc {@code CANCELLED}; {@code SHIPPED} → {@code DONE};
 *               {@code DONE} và {@code CANCELLED} không đi tiếp. Sai quy tắc → {@code IllegalStateException}.
 *   Hoàn thành khi: OrderTest xanh; ANSWER B1 nói vì sao không có setter cho {@code total}.
 */
public final class Order {

    /** Trạng thái đơn. Tập này cố định, nên dùng enum thay vì chuỗi. */
    public enum Status {
        NEW,
        PAID,
        SHIPPED,
        DONE,
        CANCELLED
    }

    private final String id;
    private final String customerCode;
    private final LocalDate createdOn;
    private final BigDecimal total;
    private Status status;

    public Order(String id, String customerCode, LocalDate createdOn, BigDecimal total, Status status) {
        // SOLUTION-BEGIN throw B1
        this.id = Objects.requireNonNull(id, "id");
        this.customerCode = Objects.requireNonNull(customerCode, "customerCode");
        this.createdOn = Objects.requireNonNull(createdOn, "createdOn");
        this.total = Objects.requireNonNull(total, "total");
        this.status = Objects.requireNonNull(status, "status");
        if (id.isBlank() || customerCode.isBlank()) {
            throw new IllegalArgumentException("id và mã khách không được trống");
        }
        if (total.signum() < 0) {
            throw new IllegalArgumentException("tổng tiền không được âm");
        }
        // SOLUTION-END
    }

    public void changeStatus(Status next) {
        // SOLUTION-BEGIN throw B1
        Objects.requireNonNull(next, "status");
        boolean allowed = switch (status) {
            case NEW -> next == Status.PAID || next == Status.CANCELLED;
            case PAID -> next == Status.SHIPPED || next == Status.CANCELLED;
            case SHIPPED -> next == Status.DONE;
            case DONE, CANCELLED -> false;
        };
        if (!allowed) {
            throw new IllegalStateException("không chuyển được " + status + " → " + next);
        }
        status = next;
        // SOLUTION-END
    }

    public String id() {
        return id;
    }

    public String customerCode() {
        return customerCode;
    }

    public LocalDate createdOn() {
        return createdOn;
    }

    public BigDecimal total() {
        return total;
    }

    public Status status() {
        return status;
    }

    /* ANSWER B1:
     * SOLUTION-BEGIN
     * total là BigDecimal và không có setter: số âm bị chặn ngay trong constructor, caller không gán lại
     * thành số âm sau đó. Đổi trạng thái đi qua changeStatus để bảng chuyển ở một chỗ, không sửa field từ ngoài.
     * SOLUTION-END
     */
}
