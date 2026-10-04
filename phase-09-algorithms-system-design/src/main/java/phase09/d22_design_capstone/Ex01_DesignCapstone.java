package phase09.d22_design_capstone;

/**
 * System Design — Capstone
 * <p>
 * Source prompt verbatim (09-algorithms-system-design.md):
 * <pre>
 * Phát triển thiết kế cho service đã xây ở các giai đoạn trước; không cần triển khai lại distributed system chỉ để chứng minh sơ đồ. Capstone bao gồm một thí nghiệm local bounded như bài thực hành mục 18: dataset và concurrency cố định, một thay đổi có lý do, đo before/after throughput, p95, error rate và pool wait; mọi dữ liệu/database dùng phải disposable. Tách rõ giả định thiết kế khỏi số đo thí nghiệm.
 *
 * Nộp sơ đồ request/data flow và ghi chú ngắn gồm:
 *
 * - Requirement, SLO và giả định tải có đơn vị; phân biệt ước lượng với số đo.
 * - API, data ownership, transaction boundary và invariant order/inventory.
 * - Lý do chọn modular monolith hay thành phần bổ sung; nêu rõ non-goals.
 * - Cách giữ đúng tồn kho khi có nhiều request/instance; không coi Java lock trong process là distributed lock.
 * - Ít nhất ba failure scenario, timeout/retry/idempotency behavior và tín hiệu quan sát.
 * - Security boundary, dữ liệu nhạy cảm, retention và access control.
 * - Trade-off được chọn, lựa chọn bị loại và bằng chứng cần thu thập trước khi đổi kiến trúc.
 * - Kết quả thí nghiệm local: setup, dataset, concurrency, thay đổi duy nhất, throughput/p95/error rate/pool wait trước và sau; ghi rõ đây là phép đo cục bộ, không phải SLO production.
 * </pre>
 * <p>
 * Submit diagram/notes for previously built service; distributed-system reimplementation is not required.
 * Review this submission manually. No prose grader and no new backend.
 * <p>
 * Submission checklist:
 * <ul>
 *   <li>Requirement, SLO and load assumptions with units; separate estimates from measurements.</li>
 *   <li>API, data ownership, transaction boundary and order/inventory invariant.</li>
 *   <li>Reason for modular monolith or additions; explicit non-goals.</li>
 *   <li>Correct inventory under concurrent requests/instances; in-process Java lock is not distributed.</li>
 *   <li>At least three failure scenarios, timeout/retry/idempotency behavior and observable signals.</li>
 *   <li>Security boundary, sensitive data, retention and access control.</li>
 *   <li>Chosen trade-offs, rejected alternatives and evidence needed before architecture changes.</li>
 *   <li>Local experiment setup, fixed disposable dataset/concurrency, one justified change, throughput/p95/error rate/
 *       pool acquisition evidence before and after; state this is local measurement, not production SLO evidence.</li>
 * </ul>
 * <p>
 * Topic 18 harness reuses d18_database_scaling/Ex01_DatabaseScaling: bounded loopback HTTP GET and
 * four timestamped cumulative Hikari acquisition snapshots. See phase-09-algorithms-system-design/README.md
 * and evidence/2026-10-05-pool-comparison/measurement.txt for completed historical local evidence.
 * Missing token/app/CSV must fail explicitly, never report zero wait. Local result is not production SLO evidence.
 * Static examples, if any, are illustrative only and cannot be presented as measured evidence.
 */
/* MODEL B1:
 * SOLUTION-BEGIN
 * Reference reasoning — proposed design, not a description of a built Phase05 capstone.
 *
 * Assumptions and target: modular order service serves browse, create-order and read-order flows.
 * Illustrative SLO target is 99.9% monthly availability and p95 read latency below 300 ms;
 * these are design targets, not measured SLOs. Workload estimate: 100 read RPS and 10 write RPS
 * average, peak factor 5; confirm from product traffic before sizing. Keep estimates separate from
 * production evidence. Store orders for the stated business retention period; exact legal/product
 * retention is an input still requiring confirmation.
 *
 * Request/data flow: client calls authenticated API; product reads use GET /api/products?page=0&size=20.
 * Order creation validates request and authorization, then one application service coordinates a
 * database transaction owning order, order_items and inventory changes. Database owns durable state
 * and constraints; API owns request/response contract. Return an order ID/status only after commit.
 * Order reads authorize access to that specific customer/order record. A future POST order endpoint
 * should accept an idempotency key scoped to principal and operation, persist key + outcome in the same
 * transaction, and return the original outcome on replay. This is a proposed extension, not a claim
 * that current Phase05 APIs implement idempotency.
 *
 * Start with a modular monolith: one deployable and one relational transaction boundary make order /
 * inventory invariants and local operations understandable; modules separate catalog, ordering and
 * persistence ownership without premature distributed transactions. Non-goals: microservices,
 * distributed cache/broker, multi-region writes, and production load claims. Add components only after
 * measured bottleneck or independent scaling/availability requirements justify their operational cost.
 *
 * Inventory invariant across concurrent requests and instances: never rely on a Java in-process lock.
 * In the order transaction, conditionally decrement with `UPDATE products SET stock = stock - :qty`
 * `WHERE id = :id AND stock >= :qty`; require exactly one affected row per item. Insert order rows and
 * commit together; any missing/insufficient item rolls back the whole transaction. Database row locks /
 * atomic conditional update serialize competing writers across instances. Enforce positive quantity,
 * foreign keys and unique order identity at the database boundary as defense in depth. Retry a detected
 * serialization/deadlock failure only within a small bounded budget; rerun the whole transaction.
 *
 * Failure scenarios and signals:
 * 1. Database slow/unavailable: set a short request/connection-acquisition timeout within the API
 *    deadline; fail with stable retryable 503, do not wait indefinitely. Observe p95/p99, error rate,
 *    pool acquisition duration, active/timeout connections and DB saturation. Shed load rather than
 *    retrying every request without a budget.
 * 2. Client times out after order commit, then retries: GET is safe to retry. For the proposed POST,
 *    the persisted idempotency key returns the same committed order instead of creating a duplicate;
 *    without that extension, caller must not assume replay safety. Observe duplicate-key/replay counts,
 *    request correlation ID and order outcome.
 * 3. Two instances reserve final stock concurrently: the conditional update lets at most one transaction
 *    affect a row; the loser rolls back and receives an explicit unavailable-stock conflict. Observe
 *    rejected reservations, transaction conflicts/deadlocks and stock/order invariant checks.
 * 4. One app instance is lost: load balancer removes unhealthy instance; requests may be retried only
 *    under the same operation idempotency contract. Observe instance health, 5xx and recovery time.
 *
 * Security: validate bearer authentication and required scope at API boundary; authorize each order by
 *    owner, not merely by possession of an ID. Use least-privilege DB credentials and TLS in deployed
 *    environments. Never log bearer tokens, payment secrets or unnecessary personal data; log
 *    correlation ID and outcome only. Use disposable local data for experiments; delete it afterward.
 * Confirm retention/deletion policy with product/legal owner before production use.
 *
 * Trade-off: choose synchronous relational transaction for stock correctness over eventual consistency
 *    or a broker-first workflow, which could oversell or require compensation. Reject an in-process lock
 *    because multiple instances do not share it. Reconsider an outbox for reliable post-commit events,
 *    read replicas for proven read pressure, or service extraction only after evidence: query plans,
 *    sustained CPU/IO/pool saturation, measured replica lag, independent team/deploy need, and an
 *    explicit consistency/failure model.
 *
 * Reproducible local experiment: a bounded run completed historically; see
 *    phase-09-algorithms-system-design/evidence/2026-10-05-pool-comparison/measurement.txt. For repetition,
 *    keep dataset, page size 20, concurrency 4, cap 1000 requests and
 *    duration 5 seconds fixed; perform two warmups; compare baseline pool=4 against one changed setting
 *    (for example pool=8), changing nothing else. Use existing
 *    phase09.d18_database_scaling.Ex01_DatabaseScaling only: bounded loopback GET
 *    `/api/products?page=0&size=20`, `PHASE09_BEARER_TOKEN` from the environment, saved baseline/changed
 *    summaries and the existing four-snapshot CSV (`run,snapshot,capturedAtUtc,metricName,pool,count,totalTimeSeconds`).
 *    Export protected `hikaricp.connections.acquire` with metrics.read; compare only matching, fresh
 *    start/end snapshots bracketing each request window. Record setup, app/DB versions, dataset,
 *    concurrency, timestamps, chosen setting, throughput, p95, errors, and acquisition delta/mean
 *    separately. Acquisition is pool wait plus acquisition overhead, not pure queue wait; do not
 *    compare it as HTTP latency. Missing app/token/telemetry or zero counter delta is a failed run.
 *    A bounded local run completed historically; its actual values and limits are in
 *    phase-09-algorithms-system-design/evidence/2026-10-05-pool-comparison/measurement.txt.
 *    That one local pair does not establish production SLOs or prove general improvement. These static
 *    assumptions and scenarios are illustrative reference reasoning; reviewer should assess correctness, not keywords.
 * SOLUTION-END
 */
final class Ex01_DesignCapstone {
    private Ex01_DesignCapstone() {}
}
