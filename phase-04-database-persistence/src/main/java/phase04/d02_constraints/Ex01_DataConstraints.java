package phase04.d02_constraints;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import javax.sql.DataSource;

/**
 * Kiểu dữ liệu và constraint — B2: kiểm chứng invariant ngay tại database.
 * <p>Nguồn: 04-database-persistence.md, mục 2. Cần làm trước: JDBC và kiểu SQL cơ bản.
 * Cách làm: chạy {@code Ex01_DataConstraintsTest}; thử cả dữ liệu sai lẫn explicit NULL.
 * Hoàn thành khi giá hợp lệ đọc đúng và các ghi vi phạm bị DB từ chối với SQLSTATE.
 * Q1 [TỰ TRẢ LỜI] Vì sao {@code double} không phù hợp để biểu diễn tiền?
 * Bắt đầu: viết ANSWER Q1; hoàn thành khi giải thích được sai số biểu diễn nhị phân.
 * Q2 [CODE] Constraint nào đảm bảo quantity của order item lớn hơn 0?
 * Bắt đầu: kiểm tra B2; hoàn thành khi CHECK cùng NOT NULL giới hạn quantity từ 1 đến 1000.
 * Q3 [TỰ TRẢ LỜI] Vì sao invariant quan trọng nên được bảo vệ cả ở DB?
 * Bắt đầu: viết ANSWER Q3; hoàn thành khi nêu được writer khác Java cũng phải tuân thủ.
 * Q4 [TỰ TRẢ LỜI] Khi nào dùng {@code TIMESTAMP WITH TIME ZONE} thay cho {@code DATE}?
 * Bắt đầu: viết ANSWER Q4; hoàn thành khi phân biệt instant với ngày lịch.
 * Q5 [THÍ NGHIỆM] Default có tự áp dụng khi ứng dụng ghi explicit {@code NULL} không?
 * Bắt đầu: chạy B2 với explicit NULL; hoàn thành khi ghi SQLSTATE và giải thích default chỉ áp dụng khi bỏ cột hoặc dùng DEFAULT.
 */
public class Ex01_DataConstraints {
    /** Đọc giá sản phẩm từ PostgreSQL; ID không tồn tại là lỗi gọi rõ ràng. */
    static BigDecimal priceFor(long productId, DataSource ds) {
        throw new UnsupportedOperationException("TODO B2");
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
/* ANSWER Q4:
 *
 */
/* OBSERVATION Q5 / ANSWER Q5:
 *
 */
