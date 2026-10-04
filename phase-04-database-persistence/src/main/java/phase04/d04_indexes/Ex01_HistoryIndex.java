package phase04.d04_indexes;

/**
 * Index và chi phí truy vấn — B4: so sánh plan và insert trước/sau composite index.
 * <p>Nguồn: 04-database-persistence.md, mục 4. Cần làm trước: B1–B3, B-tree và EXPLAIN.
 * Bắt đầu: viết indexDdl(), chạy Ex01_HistoryIndexTest trên PostgreSQL 18 disposable.
 * Kiểm tra: pg_indexes, 20000 orders/100 customers, cùng batch 500 hàng trước/sau.
 * Hoàn thành: catalog đúng, lưu plan/rows/time và giải thích chi phí ghi; không đặt ngưỡng tốc độ.
 * Q1 [TỰ TRẢ LỜI] Độ chọn lọc của cột ảnh hưởng quyết định dùng index thế nào?
 * Bắt đầu: viết ANSWER Q1; kiểm tra tỷ lệ hàng khớp, hoàn thành khi giải thích chi phí đọc.
 * Q2 [TỰ TRẢ LỜI] Composite index {@code (a, b)} thường hữu ích cho điều kiện nào?
 * Bắt đầu: viết ANSWER Q2; kiểm tra thứ tự cột, hoàn thành khi phân biệt prefix và range.
 * Q3 [TỰ TRẢ LỜI] Vì sao index làm chậm một số thao tác ghi?
 * Bắt đầu: viết ANSWER Q3; kiểm tra batch B4, hoàn thành khi nêu chi phí bảo trì index.
 * Q4 [TỰ TRẢ LỜI] Tại sao sequential scan đôi khi nhanh hơn index scan?
 * Bắt đầu: viết ANSWER Q4; kiểm tra tỷ lệ hàng cần đọc, hoàn thành khi nêu locality và heap access.
 * Q5 [THÍ NGHIỆM] Bằng chứng nào đủ để đề xuất index mới?
 * Bắt đầu: chạy q05_experimentRuns và B4; ghi OBSERVATION Q5 với dataset, plan và chi phí ghi.
 * Hoàn thành khi giải thích evidence và giới hạn của một lần đo, không suy rộng thành benchmark.
 */
public class Ex01_HistoryIndex {
    static String indexDdl() {
        // SOLUTION-BEGIN throw B4
        return "CREATE INDEX order_history_customer_time_id ON orders(customer_id, created_at, id)";
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Index thường có ích khi predicate chọn ít hàng, giảm số heap page phải đọc.
 * Khi nhiều hàng khớp, sequential scan có thể rẻ hơn; planner còn xét statistics và locality.
 * SOLUTION-END
 */
/* ANSWER Q2:
 * SOLUTION-BEGIN
 * B-tree (a,b) thường hợp với điều kiện trên prefix a và a equality kết hợp range/order trên b.
 * Chỉ lọc b không đảm bảo hiệu quả như prefix; phải xem workload, statistics và plan thực tế.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * INSERT/DELETE và UPDATE cột index phải bảo trì thêm cấu trúc index, phát sinh I/O và WAL.
 * Index chiếm dung lượng/cache; số index tăng có thể làm giảm throughput ghi.
 * SOLUTION-END
 */
/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Đọc phần lớn bảng tuần tự giảm truy cập heap rời rạc và chi phí tra index.
 * Bảng nhỏ hoặc nhiều hàng khớp có thể hợp sequential scan dù index tồn tại.
 * SOLUTION-END
 */
/* OBSERVATION Q5:
 * Dataset/plan/rows/time trước và sau: ____________________
 * Chi phí ghi và giới hạn kết luận: ____________________
 * SOLUTION-BEGIN
 * Mẫu kết luận: đối chiếu cùng dữ liệu và truy vấn, actual/estimated rows, buffers, latency và insert.
 * Một lần đo cache nóng không đủ khẳng định index tốt cho production; cần workload đại diện và đo lặp.
 * SOLUTION-END
 */
