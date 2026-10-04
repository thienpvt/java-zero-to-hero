package phase09.d20_reliability_observability_security;

/**
 * Reliability, observability và security.
 * <p>
 * Nguồn: 09-algorithms-system-design.md, mục 20, câu 1–5 và Bài thực hành.
 * Cần làm trước: SLI/SLO, dependency budget, authn/authz, log/metric/trace và secret handling.
 * Cách làm: mở file trong IDE, điền ANSWER Q1–Q5 rồi failureMatrixTemplate() cho B1.
 * Không chấm prose bằng từ khóa hoặc noop JUnit; người đọc review failure model và evidence.
 * <p>
 * Q1 [TỰ TRẢ LỜI] Vì sao p99 latency có thể quan trọng hơn average?
 *   Bắt đầu: cho 99 request nhanh và một request rất chậm.
 *   Tra cứu: tail latency, fan-out và latency SLO.
 *   Hoàn thành khi: ANSWER Q1 nêu average che đuôi phân bố nhưng percentile cần sample/window.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Timeout và circuit breaker giải quyết vấn đề khác nhau nào?
 *   Bắt đầu: dependency liên tục chậm/lỗi.
 *   Tra cứu: per-call deadline, open/half-open và concurrency limit.
 *   Hoàn thành khi: ANSWER Q2 không coi circuit breaker thay timeout hoặc capacity bound.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Cardinality cao trong metric có thể gây cost gì?
 *   Bắt đầu: dùng user ID/request ID làm metric label.
 *   Tra cứu: time series, label combinations và retention/storage.
 *   Hoàn thành khi: ANSWER Q3 có chi phí ingest/query và label thay thế bounded.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Correlation ID giúp điều tra luồng qua service thế nào?
 *   Bắt đầu: trace một request qua API, outbox và consumer.
 *   Tra cứu: propagation, structured log và trace context.
 *   Hoàn thành khi: ANSWER Q4 nối event nhưng không coi ID là quyền truy cập hay metric label.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Dữ liệu nào không nên ghi vào application log?
 *   Bắt đầu: kiểm tra header/token, body và exception path.
 *   Tra cứu: secrets, PII, least privilege, retention và redaction.
 *   Hoàn thành khi: ANSWER Q5 có allowlist thông tin an toàn và cả failure path.
 * <p>
 * B1 [TỰ TRẢ LỜI]: lập bảng dependency chậm, DB unavailable, mất một instance;
 * mỗi dòng có signal quan sát, timeout/concurrency/load bound, user behavior và recovery.
 * Thêm authn/authz, input validation, secrets/log privacy, retry budget và owner kiểm chứng.
 * Hoàn thành khi: failure không giữ cạn pool/thread, fallback không phá invariant/security,
 * recovery không retry storm; tách giả định thiết kế khỏi hành vi đã đo.
 * Failure injection vẫn NOT RUN; bounded local pool measurement trên Phase05 app đã chạy,
 * xem README và evidence/2026-10-05-pool-comparison/measurement.txt.
 */
class Ex01_ReliabilityObservabilitySecurity {
    static String failureMatrixTemplate() {
        return "Scenario | Signal/SLI | Timeout/concurrency/load bound | User behavior | Recovery/owner/evidence\n"
                + "Slow dependency | | | |\nDB unavailable | | | |\nInstance loss | | | |\n"
                + "Authn/authz and record ownership:\nInput validation/rate limit:\n"
                + "Secret source/least privilege/log allowlist/retention:\n"
                + "Retry budget/backpressure/circuit recovery:\nAssumptions versus measured evidence:\n";
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Average có thể thấp dù nhóm request đuôi rất chậm, làm user timeout hoặc vi phạm SLO.
 * Fan-out chờ nhánh chậm nhất càng nhạy tail. p99 cần đủ mẫu/window và error/saturation đi kèm;
 * không dùng percentile từ vài mẫu làm bằng chứng chắc chắn hoặc lấy mean của các percentile.
 * SOLUTION-END
 */
/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Timeout chặn một call chờ quá budget; breaker ngừng gửi thêm khi dependency lỗi/chậm liên tục,
 * rồi probe half-open để hồi phục. Breaker không cancel mọi in-flight call, không thay timeout,
 * concurrency bound, retry budget hay xử lý lỗi nghiệp vụ; fallback vẫn phải giữ invariant.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Mỗi tổ hợp label tạo time series, user/request ID làm số series tăng lớn, tốn RAM, ingest,
 * storage/retention và query CPU, có thể mất signal do quota. Dùng route template/status class/owner bounded;
 * ID chi tiết thuộc log/trace với sampling và privacy policy, không metric label.
 * SOLUTION-END
 */
/* ANSWER Q4:
 * SOLUTION-BEGIN
 * ID được propagate qua request/event nối structured log và trace của cùng luồng, giúp tìm điểm lỗi/chậm.
 * Validate/bound ID do client cung cấp hoặc tạo ID mới để tránh log injection; không chứa PII.
 * ID không chứng minh identity/authorization và không dùng làm high-cardinality metric label.
 * SOLUTION-END
 */
/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Không log password, bearer/JWT, API key, cookie/session, connection-string secret, payment data
 * hoặc PII/raw request/response body không cần thiết; exception/URL cũng có thể lộ chúng.
 * Allowlist route template/status/error code/duration và correlation ID đã kiểm tra; redaction,
 * access control/retention/audit theo requirement, không dựa vào debug flag để bảo vệ secret.
 * SOLUTION-END
 */
/* MODEL B1:
 * SOLUTION-BEGIN
 * Bảng giả định, chưa injection trên app thật:
 * dependency chậm | p95/p99, timeout, saturation | per-call deadline trong request budget + bulkhead
 * | lỗi ổn định hoặc fallback chỉ khi an toàn | giảm tải, probe bounded, retry jitter có cap.
 * DB unavailable | acquisition error, DB health, HTTP errors | short timeout + queue/concurrency bound
 * | không xác nhận order chưa commit, không fallback inventory stale | reconnect/backoff, reconcile unknown commit.
 * mất instance | health/LB errors và tải instance còn sống | shared quota/pool budget, drain
 * | idempotent retry có deadline, không phụ thuộc session memory | LB loại instance, rollout/restart và kiểm capacity.
 * Authn/authz kiểm record owner ở server, input bound/rate limit trước effect; secrets từ env/store least privilege.
 * Log allowlist không token/body/PII; correlation ID bounded, metrics label bounded; recovery có owner và SLO.
 * Bounded local pool measurement đã chạy; xem README và evidence/2026-10-05-pool-comparison/measurement.txt.
 * Failure injection vẫn NOT RUN; phép đo này không xác nhận production SLO hay failure behavior.
 * SOLUTION-END
 */
