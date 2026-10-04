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
        throw new UnsupportedOperationException("TODO B6");
    }

    /** Cursor time không null, customer/id dương; tuple strict greater; mỗi request đọc trạng thái DB hiện tại. */
    static List<OrderRow> after(DataSource ds, long customerId, Instant createdAt, long id, int size) {
        throw new UnsupportedOperationException("TODO B6");
    }

    private static void validate(DataSource ds, long customerId, int size) {
        throw new UnsupportedOperationException("TODO B6");
    }

    private static List<OrderRow> read(java.sql.PreparedStatement s) throws SQLException {
        throw new UnsupportedOperationException("TODO B6");
    }
}

/* ANSWER Q1:
 *
 */
/* ANSWER Q2:
 *
 */
/* ANSWER Q3:
 *
 */
/* OBSERVATION Q4:
 * Trang đầu / insert / trang offset tiếp / trang keyset tiếp: ____________________
 * Phạm vi ổn định và thay đổi vẫn nhìn thấy sau cursor: ____________________
 *
 */
/* ANSWER Q5:
 *
 */
