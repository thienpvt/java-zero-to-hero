package phase09.d14_capacity_estimation;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Ước lượng tải và dung lượng.
 * <p>
 * Nguồn: 09-algorithms-system-design.md, mục 14 (Ước lượng tải và dung lượng), câu 1–5.
 * Cần làm trước: đổi đơn vị ngày/giây và bytes/record nhất quán.
 * Cách làm: trả lời Q1–Q5, đối chiếu công thức B1 với test; averageRps làm tròn 6 chữ số HALF_UP.
 * <p>
 * Q1 [TỰ TRẢ LỜI] Vì sao peak RPS quan trọng hơn trung bình trong sizing?
 *   Bắt đầu: so sánh average và burst trong cùng một ngày.
 *   Tra cứu: năng lực tức thời của app, DB và downstream.
 *   Hoàn thành khi: ANSWER Q1 giải thích trung bình có thể che burst và hậu quả lên dependency.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Cần đưa yếu tố nào vào ước lượng storage theo thời gian?
 *   Bắt đầu: viết phép nhân số record với bytes/record.
 *   Tra cứu: retention, overhead, replica và đơn vị.
 *   Hoàn thành khi: ANSWER Q2 nêu các hệ số và phân biệt logical với physical bytes.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Làm sao phân biệt giả định với số đo?
 *   Bắt đầu: ghi nguồn và phương pháp của mỗi con số.
 *   Tra cứu: workload model so với telemetry có thời gian/môi trường.
 *   Hoàn thành khi: ANSWER Q3 gắn nhãn estimated/measured và chỉ ra sai số.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Read/write ratio ảnh hưởng lựa chọn nào?
 *   Bắt đầu: tách số read và write request.
 *   Tra cứu: query/index, cache và write amplification.
 *   Hoàn thành khi: ANSWER Q4 liên hệ tỷ lệ với bottleneck giả định, không chọn công nghệ chỉ từ tỷ lệ.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Ước lượng nào nên xác minh bằng load test hoặc production metrics?
 *   Bắt đầu: liệt kê giả định quyết định capacity.
 *   Tra cứu: peak, fan-out, payload và contention.
 *   Hoàn thành khi: ANSWER Q5 chọn giả định rủi ro cao và bằng chứng phù hợp để kiểm tra.
 * <p>
 * B1: dùng đúng giả định nguồn; đây là ước lượng bài tập, không phải benchmark hay production measurement.
 * 1,000,000 orders/day; 10 reads và 1 write mỗi order; peak factor 10 trên tổng average API RPS;
 * 2 KiB logical/order (gồm items); retention 365 days; overhead ×2; replication copies ×3.
 * Tính average read/write riêng, total average rồi peak; làm tròn average trước khi nhân peak theo helper.
 * Logical bytes = orders × bytes/order × days; physical bytes = logical × overhead × copies.
 */
class Ex01_CapacityEstimation {
    private static final BigDecimal SECONDS_PER_DAY = BigDecimal.valueOf(86_400);

    static BigDecimal averageRps(long requestsPerDay) {
        // SOLUTION-BEGIN throw B1
        if (requestsPerDay <= 0) throw new IllegalArgumentException("requestsPerDay must be positive");
        return BigDecimal.valueOf(requestsPerDay).divide(SECONDS_PER_DAY, 6, RoundingMode.HALF_UP);
        // SOLUTION-END
    }

    static BigDecimal storageBytes(long orders, long bytesPerOrder, int days) {
        // SOLUTION-BEGIN throw B1
        if (orders <= 0 || bytesPerOrder <= 0 || days <= 0) {
            throw new IllegalArgumentException("orders, bytesPerOrder, and days must be positive");
        }
        return BigDecimal.valueOf(Math.multiplyExact(Math.multiplyExact(orders, bytesPerOrder), days));
        // SOLUTION-END
    }

    static BigDecimal physicalBytes(BigDecimal logical, int overhead, int replicas) {
        // SOLUTION-BEGIN throw B1
        if (logical == null) throw new NullPointerException("logical");
        if (logical.signum() <= 0 || overhead <= 0 || replicas <= 0) {
            throw new IllegalArgumentException("logical, overhead, and replicas must be positive");
        }
        return logical.multiply(BigDecimal.valueOf(overhead)).multiply(BigDecimal.valueOf(replicas));
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Average RPS che burst: dependency phải chịu lưu lượng trong khoảng thời gian ngắn, không phải trung bình ngày.
 * Dùng peak factor là giả định để sizing sơ bộ; cần đo peak thực tế trước quyết định capacity.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Storage = records × bytes/record × retention; ghi rõ byte unit và có tính items trong record hay không.
 * Logical storage chưa gồm overhead và copies; physical estimate nhân lần lượt các hệ số đó.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Ước lượng là đầu vào/giả định của mô hình; số đo phải có nguồn telemetry, cửa sổ thời gian,
 * môi trường và phương pháp thu thập. Ghi sai số/giới hạn để không gọi estimate là production fact.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Read/write ratio giúp tìm workload cần xác minh: query/index và read capacity khác write cost,
 * transaction và write amplification. Tỷ lệ tự nó không đủ để kết luận cần cache hay replica.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Xác minh peak/fan-out/payload hoặc contention có ảnh hưởng capacity lớn nhất bằng load test bounded
 * cùng production metrics phù hợp. Ghi dataset, concurrency, lỗi và giới hạn phép đo.
 * SOLUTION-END
 */
