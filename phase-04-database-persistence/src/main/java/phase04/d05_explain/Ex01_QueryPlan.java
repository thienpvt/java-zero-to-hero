package phase04.d05_explain;

import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * Execution plan — B5: lưu và giải thích plan trước/sau một thay đổi.
 * <p>Nguồn: 04-database-persistence.md, mục 5. Cần làm trước: B3–B4 và JDBC cơ bản.
 * Bắt đầu: viết explain(ds,customerId), chạy Ex01_QueryPlanTest trên DB disposable.
 * Kiểm tra: output từng dòng của EXPLAIN ANALYZE BUFFERS, 20000 orders/100 customers.
 * Hoàn thành: lưu plan, xác định node tốn kém, giải thích rows/buffers và điều kiện kết luận thay đổi.
 * Q1 [TỰ TRẢ LỜI] {@code EXPLAIN} khác {@code EXPLAIN ANALYZE} ở điểm nào?
 * Bắt đầu: viết ANSWER Q1; kiểm tra B5, hoàn thành khi phân biệt ước lượng và thực thi.
 * Q2 [TỰ TRẢ LỜI] Vì sao actual rows lệch xa estimated rows có thể làm plan kém?
 * Bắt đầu: viết ANSWER Q2; kiểm tra từng node, hoàn thành khi nối sai cardinality với chọn plan.
 * Q3 [TỰ TRẢ LỜI] {@code BUFFERS} cung cấp loại bằng chứng nào?
 * Bắt đầu: viết ANSWER Q3; kiểm tra output B5, hoàn thành khi phân biệt hit/read và latency.
 * Q4 [TỰ TRẢ LỜI] Vì sao không được chạy {@code EXPLAIN ANALYZE} câu lệnh sửa dữ liệu tùy tiện?
 * Bắt đầu: viết ANSWER Q4; kiểm tra tác động thực thi, hoàn thành khi nêu rủi ro side effect.
 * Q5 [TỰ TRẢ LỜI] Một plan có cost thấp hơn có luôn đồng nghĩa latency production thấp hơn không?
 * Bắt đầu: viết ANSWER Q5; kiểm tra dataset/cache, hoàn thành khi nêu giới hạn cost model.
 */
public class Ex01_QueryPlan {
    static String explain(DataSource ds, long customerId) {
        throw new UnsupportedOperationException("TODO B5");
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
/* ANSWER Q5:
 *
 */
/* B5 GHI CHÚ PLAN:
 * Node tốn kém/estimated và actual rows/buffers: ____________________
 * Lý do chọn thay đổi, giới hạn dataset và điều kiện kết luận đổi: ____________________
 *
 */
