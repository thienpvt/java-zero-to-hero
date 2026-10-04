package phase09.d16_horizontal_scaling;

/**
 * Horizontal scaling và load balancing.
 * <p>
 * Nguồn: 09-algorithms-system-design.md, mục 16 (Horizontal scaling và load balancing), câu 1–5.
 * Cần làm trước: phân biệt instance state và dependency dùng chung.
 * Cách làm: trả lời Q1–Q5, vẽ B1 gồm hai app instance và DB, chạy Ex01_HorizontalScalingTest.
 * <p>
 * Q1 [TỰ TRẢ LỜI] Vì sao session trong memory gây vấn đề khi có nhiều instance?
 *   Bắt đầu: gửi hai request liên tiếp tới hai instance khác nhau.
 *   Tra cứu: load balancing, restart và nơi lưu session bền vững.
 *   Hoàn thành khi: ANSWER Q1 mô tả state không được chia sẻ và cách affinity chỉ né lỗi.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Sticky session giải quyết và không giải quyết điều gì?
 *   Bắt đầu: xem request được ghim vào một instance.
 *   Tra cứu: instance failure, rebalance và state replication.
 *   Hoàn thành khi: ANSWER Q2 phân biệt routing affinity với state durability/availability.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Vì sao scale app có thể làm database tệ hơn?
 *   Bắt đầu: nhân connection pool tối đa với số instance.
 *   Tra cứu: DB quota, query load và downstream concurrency.
 *   Hoàn thành khi: ANSWER Q3 nêu cách tổng tải vượt capacity dù từng instance khỏe.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Những dependency nào cần capacity budget chung?
 *   Bắt đầu: liệt kê dependency mỗi request có thể gọi.
 *   Tra cứu: connection pool, rate limit, quota và fan-out.
 *   Hoàn thành khi: ANSWER Q4 nêu budget tổng theo instance và cách giới hạn đồng thời.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Khi nào cần đo trước khi thêm instance?
 *   Bắt đầu: xác định saturation/error đang xảy ra ở thành phần nào.
 *   Tra cứu: latency, throughput, pool wait, CPU và downstream quota.
 *   Hoàn thành khi: ANSWER Q5 nêu evidence bottleneck và metric cần so trước/sau.
 * <p>
 * B1: mô tả hai app instance + DB, session, pool/quota, mất một instance và bottleneck cần đo đầu tiên.
 */
class Ex01_HorizontalScaling {
    static String twoInstanceFailureTemplate() {
        return "Instance A failure:\nInstance B behavior:\nSession state:\n"
                + "Database pool/quota:\nDownstream capacity budget:\n"
                + "First bottleneck to measure:\nEvidence before adding instances:\n";
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
