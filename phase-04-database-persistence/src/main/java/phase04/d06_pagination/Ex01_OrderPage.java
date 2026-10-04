package phase04.d06_pagination;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 * Pagination — B6: so sánh offset và keyset khi dữ liệu thay đổi.
 * <p>Nguồn: 04-database-persistence.md, mục 6. Cần làm trước: B3–B5, ORDER BY và bind JDBC.
 * Bắt đầu: viết offsetPage/after, chạy Ex01_OrderPageTest; page zero-based, size 1–100.
 * Kiểm tra: chỉ đọc customer sở hữu, ASC(created_at,id), tie timestamp, insert giữa requests.
 * Hoàn thành: List bất biến, trang rỗng đúng, validation rõ và giải thích keyset không là global snapshot.
 * Q1 [TỰ TRẢ LỜI] Vì sao {@code ORDER BY created_at} chưa chắc tạo thứ tự ổn định?
 * Bắt đầu: viết ANSWER Q1; kiểm tra timestamp trùng, hoàn thành khi nêu tie-breaker unique.
 * Q2 [TỰ TRẢ LỜI] Keyset pagination cần giữ thông tin gì từ trang trước?
 * Bắt đầu: viết ANSWER Q2; kiểm tra hàng cuối trang, hoàn thành khi nêu đủ tuple và phạm vi customer.
 * Q3 [TỰ TRẢ LỜI] Offset lớn có thể tốn kém vì sao?
 * Bắt đầu: viết ANSWER Q3; kiểm tra số hàng bị bỏ qua, hoàn thành khi phân biệt offset và seek.
 * Q4 [THÍ NGHIỆM] Dữ liệu thay đổi giữa hai request ảnh hưởng trang offset thế nào?
 * Bắt đầu: chạy b06_insertBetweenRequests với ids 1..4 và insert (5,t15), ghi OBSERVATION Q4.
 * Hoàn thành khi ghi chính xác cả hai trang tiếp và giới hạn của cursor giữa requests.
 * Q5 [TỰ TRẢ LỜI] Rủi ro của phân trang kết hợp fetch collection là gì?
 * Bắt đầu: viết ANSWER Q5; kiểm tra join nhân hàng, hoàn thành khi nêu phân trang root trước.
 */
public class Ex01_OrderPage {
    public record OrderRow(long id, Instant createdAt, String status) {}

    /** page >= 0; size 1..100; checked long offset, không tràn int khi page rất lớn. */
    static List<OrderRow> offsetPage(DataSource ds, long customerId, int page, int size) {
        // SOLUTION-BEGIN throw B6
        validate(ds, customerId, size);
        if (page < 0) throw new IllegalArgumentException("page phải >= 0");
        long offset = Math.multiplyExact((long) page, size);
        String sql = """
                SELECT id, created_at, status FROM orders
                WHERE customer_id = ? ORDER BY created_at, id LIMIT ? OFFSET ?
                """;
        try (var c = ds.getConnection(); var s = c.prepareStatement(sql)) {
            s.setLong(1, customerId);
            s.setInt(2, size);
            s.setLong(3, offset);
            s.setQueryTimeout(10);
            return read(s);
        } catch (SQLException failure) {
            throw new IllegalStateException("Không thể đọc trang offset", failure);
        }
        // SOLUTION-END
    }

    /** Cursor time không null, customer/id dương; tuple strict greater; mỗi request đọc trạng thái DB hiện tại. */
    static List<OrderRow> after(DataSource ds, long customerId, Instant createdAt, long id, int size) {
        // SOLUTION-BEGIN throw B6
        validate(ds, customerId, size);
        if (createdAt == null || id <= 0) throw new IllegalArgumentException("Cursor time và ID dương bắt buộc");
        String sql = """
                SELECT id, created_at, status FROM orders
                WHERE customer_id = ? AND (created_at, id) > (?, ?)
                ORDER BY created_at, id LIMIT ?
                """;
        try (var c = ds.getConnection(); var s = c.prepareStatement(sql)) {
            s.setLong(1, customerId);
            s.setObject(2, createdAt.atOffset(java.time.ZoneOffset.UTC));
            s.setLong(3, id);
            s.setInt(4, size);
            s.setQueryTimeout(10);
            return read(s);
        } catch (SQLException failure) {
            throw new IllegalStateException("Không thể đọc trang keyset", failure);
        }
        // SOLUTION-END
    }

    private static void validate(DataSource ds, long customerId, int size) {
        // SOLUTION-BEGIN throw B6
        if (ds == null || customerId <= 0 || size < 1 || size > 100)
            throw new IllegalArgumentException("DataSource, customer ID dương và size 1..100 bắt buộc");
        // SOLUTION-END
    }

    private static List<OrderRow> read(java.sql.PreparedStatement s) throws SQLException {
        // SOLUTION-BEGIN throw B6
        try (var r = s.executeQuery()) {
            List<OrderRow> rows = new ArrayList<>();
            while (r.next()) rows.add(new OrderRow(r.getLong("id"), r.getTimestamp("created_at").toInstant(), r.getString("status")));
            return List.copyOf(rows);
        }
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Nhiều order có thể trùng created_at; DB không cam kết thứ tự giữa các hàng bằng timestamp.
 * Thêm id unique làm tie-breaker và dùng cùng thứ tự ASC(created_at,id) cho mọi trang.
 * SOLUTION-END
 */
/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Giữ created_at và id của hàng cuối, cùng chiều sort và phạm vi customer/filter của truy vấn.
 * Trang sau dùng tuple strict greater; cursor không phải snapshot toàn bộ dữ liệu.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * OFFSET vẫn cần tìm/đọc và bỏ qua các hàng trước trang, nên chi phí tăng theo độ sâu.
 * Keyset có thể seek theo index phù hợp; không tự cho phép nhảy tới số trang tùy ý.
 * SOLUTION-END
 */
/* OBSERVATION Q4:
 * Trang đầu / insert / trang offset tiếp / trang keyset tiếp: ____________________
 * Phạm vi ổn định và thay đổi vẫn nhìn thấy sau cursor: ____________________
 * SOLUTION-BEGIN
 * Với 1:t10,2:t20,3:t30,4:t40, trang đầu size 2 là [1,2]; insert 5:t15 làm offset page 1 thành [2,3].
 * after(t20,2) trả [3,4], tránh insert trước cursor nhưng vẫn thấy insert sau cursor; không global snapshot.
 * SOLUTION-END
 */
/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Join collection nhân hàng root; LIMIT/OFFSET có thể cắt collection hoặc trả thiếu/duplicate root.
 * Phân trang root IDs trước rồi tải collection/projection theo các IDs; kiểm tra số truy vấn và thứ tự.
 * SOLUTION-END
 */
