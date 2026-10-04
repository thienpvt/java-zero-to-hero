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
 * four timestamped cumulative Hikari acquisition snapshots. See phase-09-algorithms-system-design/README.md.
 * Phase05 capstone application has not been built: production measurement is PENDING / NOT RUN.
 * Run only with local app and valid telemetry; missing token/app/CSV must fail explicitly, never report zero wait.
 * Static examples, if any, are illustrative only and cannot be presented as measured evidence.
 */
final class Ex01_DesignCapstone {
    private Ex01_DesignCapstone() {}
}
