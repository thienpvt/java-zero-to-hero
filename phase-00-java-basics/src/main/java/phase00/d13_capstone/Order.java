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
 * <p>
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
        throw new UnsupportedOperationException("TODO B1");
    }

    public void changeStatus(Status next) {
        throw new UnsupportedOperationException("TODO B1");
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
     *
     */
}
