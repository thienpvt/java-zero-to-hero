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
        // SOLUTION-BEGIN throw B5
        if (ds == null || customerId <= 0) throw new IllegalArgumentException("DataSource và customer ID dương bắt buộc");
        String sql = """
                EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)
                SELECT id, created_at, status FROM orders
                WHERE customer_id = ? ORDER BY created_at, id
                """;
        try (var c = ds.getConnection(); var s = c.prepareStatement(sql)) {
            s.setLong(1, customerId);
            s.setQueryTimeout(10);
            try (var r = s.executeQuery()) {
                var text = new StringBuilder();
                while (r.next()) text.append(r.getString(1)).append('\n');
                return text.toString();
            }
        } catch (SQLException failure) {
            throw new IllegalStateException("Không thể đọc execution plan", failure);
        }
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * EXPLAIN chỉ lập plan với chi phí và số hàng ước lượng; ANALYZE thực thi truy vấn.
 * ANALYZE bổ sung số hàng, loops và thời gian thực tế; kết quả phụ thuộc dataset và trạng thái cache.
 * SOLUTION-END
 */
/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Sai cardinality có thể khiến planner chọn join order, join algorithm hoặc scan không phù hợp.
 * Kiểm tra statistics, dữ liệu lệch và tương quan cột; đối chiếu estimated/actual rows từng node.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * BUFFERS báo các block hit/read/dirtied/written, kể cả shared/local/temp khi có.
 * Shared read không tự chứng minh physical disk I/O vì OS cache; hit cũng không bảo đảm latency thấp.
 * SOLUTION-END
 */
/* ANSWER Q4:
 * SOLUTION-BEGIN
 * ANALYZE thực thi INSERT/UPDATE/DELETE, có thể ghi dữ liệu, giữ lock và kích hoạt trigger.
 * Rollback không hoàn tác mọi tác động ngoài DB hoặc sequence; B5 chỉ SELECT trên DB disposable.
 * SOLUTION-END
 */
/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Cost là đơn vị tương đối của planner, không phải milliseconds hay SLA production.
 * Cache, I/O, concurrency và phân bố dữ liệu làm latency đổi; cần đo workload đại diện.
 * SOLUTION-END
 */
/* B5 GHI CHÚ PLAN:
 * Node tốn kém/estimated và actual rows/buffers: ____________________
 * Lý do chọn thay đổi, giới hạn dataset và điều kiện kết luận đổi: ____________________
 * SOLUTION-BEGIN
 * So sánh hai plan cùng customer trước/sau provided index; đọc actual time, rows, loops và buffers.
 * Không chọn node theo cost đơn lẻ; dataset và cache của lab không thay thế evidence production.
 * SOLUTION-END
 */
