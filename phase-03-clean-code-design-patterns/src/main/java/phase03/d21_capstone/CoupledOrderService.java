package phase03.d21_capstone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bản trước refactor — cho sẵn, KHÔNG sửa.
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục Architecture Exercise.
 * Đây là order flow trộn validate, pricing, persistence, charge, inventory, invoice, notify và publish
 * trong một method. Nhiệm vụ của B1 là ghi lại hành vi quan sát được của nó, không sửa file này.
 */
public class CoupledOrderService {

    private final Map<String, String> savedOrders = new HashMap<>();
    private final List<String> events = new ArrayList<>();
    private final List<String> charges = new ArrayList<>();
    private int nextOrderNumber = 1;

    /** Trả về chuỗi trạng thái quan sát được, đúng bản trộn trách nhiệm. */
    public String createOrder(String customer, List<int[]> items, boolean vip) {
        if (customer == null || customer.isBlank()) {
            throw new IllegalArgumentException("Thiếu khách hàng.");
        }
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Đơn không có mặt hàng.");
        }
        long total = 0;
        for (int[] item : items) {
            total += (long) item[0] * item[1];
        }
        if (vip) {
            total = total * 9 / 10;
        }
        String orderId = "o-" + nextOrderNumber++;
        if (total > 100_000) {
            return "DECLINED:" + orderId + ":" + total;
        }
        charges.add(orderId + ":" + total);
        savedOrders.put(orderId, total + ":" + customer);
        for (int[] item : items) {
            if (item[0] < 0) {
                throw new IllegalStateException("Không thể giữ hàng cho " + orderId);
            }
        }
        events.add("created:" + orderId);
        return "OK:" + orderId + ":" + total;
    }

    public Map<String, String> savedOrders() {
        return Map.copyOf(savedOrders);
    }

    public List<String> events() {
        return List.copyOf(events);
    }

    public List<String> charges() {
        return List.copyOf(charges);
    }
}
