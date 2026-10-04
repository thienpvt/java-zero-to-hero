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
/* MODEL B1:
 *
 */
