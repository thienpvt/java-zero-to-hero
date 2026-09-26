package phase00.d13_capstone;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import phase00.d13_capstone.Order.Status;

/**
 * Bài tổng hợp — OrderBook
 *
 * Nguồn: 00-java-basics-review.md, mục "Bài tổng hợp — Quản lý đơn hàng ở bộ nhớ", bước 2.
 * Cần làm trước: Order (B1).
 * Cách làm: cài {@code add}, {@code find}, {@code byCustomer}. Chạy OrderBookTest.
 *
 * ─────────────────────────────────────────────────────────────────────
 * B2 [CODE] Lưu đơn trong {@code Map<String, Order>}; thêm mới, tìm theo id, đổi trạng thái, liệt kê theo khách.
 *   Bắt đầu   : cài ba method TODO B2. {@code changeStatus} đã gọi {@code find} rồi {@code Order.changeStatus}.
 *   Kiểm chứng: Debug test id trùng và test không có đơn. Alt+F8 xem {@code orders.containsKey}.
 *   Code      : {@code LinkedHashMap} để giữ thứ tự thêm. {@code add} khi id đã có →
 *               {@code IllegalArgumentException} chứa id. {@code find} không có id →
 *               {@code NoSuchElementException}. {@code byCustomer} theo thứ tự thêm, list không cho sửa.
 *   Hoàn thành khi: OrderBookTest xanh; nói được vì sao key của map là id chứ không phải mã khách.
 */
public final class OrderBook {

    private final Map<String, Order> orders = new LinkedHashMap<>();

    public void add(Order order) {
        throw new UnsupportedOperationException("TODO B2");
    }

    public Order find(String id) {
        throw new UnsupportedOperationException("TODO B2");
    }

    public void changeStatus(String id, Status next) {
        find(id).changeStatus(next);
    }

    public List<Order> byCustomer(String customerCode) {
        throw new UnsupportedOperationException("TODO B2");
    }
}
