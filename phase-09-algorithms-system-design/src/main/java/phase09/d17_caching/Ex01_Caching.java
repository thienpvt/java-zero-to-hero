package phase09.d17_caching;

/**
 * Caching.
 * <p>
 * Nguồn: 09-algorithms-system-design.md, mục 17 (Caching), câu 1–5.
 * Cần làm trước: phân biệt source of truth với dữ liệu dẫn xuất có thể stale.
 * Cách làm: trả lời Q1–Q5, chọn read endpoint cho B1 rồi chạy Ex01_CachingTest.
 * <p>
 * Q1 [TỰ TRẢ LỜI] TTL dài giảm tải nhưng tăng loại rủi ro nào?
 *   Bắt đầu: xét dữ liệu đổi ngay sau khi cache được nạp.
 *   Tra cứu: freshness contract, invalidation và dữ liệu stale.
 *   Hoàn thành khi: ANSWER Q1 nêu stale window và ảnh hưởng của nó với nghiệp vụ.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Cache-aside có race condition nào khi cập nhật dữ liệu?
 *   Bắt đầu: xen kẽ DB update, cache read và invalidation.
 *   Tra cứu: thứ tự ghi/xóa và request đang tải giá trị cũ.
 *   Hoàn thành khi: ANSWER Q2 mô tả cách cache cũ có thể được ghi lại sau invalidation.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Cache miss đồng thời nhiều request có thể gây gì?
 *   Bắt đầu: cho nhiều request cùng miss một key.
 *   Tra cứu: backend fan-in, hot key và request coalescing.
 *   Hoàn thành khi: ANSWER Q3 nêu stampede và biện pháp giới hạn tải.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Khi nào stale data không chấp nhận được?
 *   Bắt đầu: xác định invariant và hậu quả nếu đọc bản cũ.
 *   Tra cứu: order/inventory so với dữ liệu hiển thị ít rủi ro.
 *   Hoàn thành khi: ANSWER Q4 gắn freshness với requirement, không khẳng định mọi dữ liệu đều cần real-time.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Cache outage nên làm request fail-open hay fail-closed dựa trên điều gì?
 *   Bắt đầu: giả định cache không dùng được và trace đường đọc.
 *   Tra cứu: nguồn dữ liệu, security boundary, backend capacity và stale contract.
 *   Hoàn thành khi: ANSWER Q5 chọn hành vi theo an toàn/capacity và chỉ ra trường hợp fallback không được phép.
 * <p>
 * B1: chọn read endpoint; ghi key, TTL, invalidation, freshness, outage và metric đo lợi ích.
 */
class Ex01_Caching {
    static String cacheDecisionTemplate() {
        return "Read endpoint and source of truth:\nCache key:\nTTL and freshness contract:\n"
                + "Invalidation and stampede:\nMemory/eviction bound:\n"
                + "Outage behavior and metrics:\nEvidence cache reduces a bottleneck:\n";
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
