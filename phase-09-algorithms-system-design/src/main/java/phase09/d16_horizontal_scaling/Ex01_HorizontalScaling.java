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
 * SOLUTION-BEGIN
 * Request kế tiếp có thể đến instance không giữ session nên mất state hoặc người dùng không nhất quán.
 * Chuyển state sang nơi dùng chung phù hợp; affinity chỉ giữ routing và không chịu được mọi failure.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Sticky session giảm đổi instance giữa request khi instance còn sống; nó không chia sẻ hay bảo vệ state.
 * Instance chết/rebalance vẫn mất affinity; state cần nơi bền vững/chia sẻ theo requirement.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Mỗi instance có pool riêng; tổng connection/concurrency tăng theo instance và có thể vượt DB quota.
 * Thêm app capacity khi DB đang bão hòa khuếch đại contention, query load và latency.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * DB connections/query rate, cache, payment/downstream quota, thread/queue và bandwidth cần budget tổng.
 * Giới hạn per-instance phải phù hợp số replica dự kiến và tổng quota; theo dõi saturation thực tế.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Đo khi chưa rõ app hay dependency là bottleneck, hoặc cần biết giới hạn hiện tại trước scale.
 * So throughput, latency/error, CPU và DB pool wait/quota trong cùng workload; không coi thêm instance là bằng chứng.
 * SOLUTION-END
 */
