package phase09.d19_messaging_retry;

/**
 * Messaging, retry và failure handling.
 * <p>
 * Nguồn: 09-algorithms-system-design.md, mục 19, câu 1–5 và Bài thực hành.
 * Cần làm trước: transaction/invariant order/inventory, idempotency và nhiều instance.
 * Cách làm: mở file trong IDE, điền ANSWER Q1–Q5, dùng eventFlowTemplate() để viết/vẽ B1.
 * Không có prose keyword grader hoặc heading-only JUnit; người đọc review lập luận và failure timeline.
 * <p>
 * Q1 [TỰ TRẢ LỜI] Vì sao consumer phải chấp nhận duplicate message?
 *   Bắt đầu: cho consumer commit rồi mất ACK.
 *   Tra cứu: at-least-once, redelivery và idempotent consumer.
 *   Hoàn thành khi: ANSWER Q1 xác định duplicate và atomic business-effect boundary.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Outbox giải quyết dual-write nào và không giải quyết điều gì?
 *   Bắt đầu: cho DB commit thành công nhưng broker publish thất bại.
 *   Tra cứu: outbox transaction, relay và delivery semantics.
 *   Hoàn thành khi: ANSWER Q2 tách commit DB/outbox khỏi publish và duplicate consumer.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Retry vô hạn có thể gây hậu quả gì?
 *   Bắt đầu: dependency vẫn lỗi trong khi request mới tiếp tục đến.
 *   Tra cứu: retry budget, backoff/jitter, dead-letter và backpressure.
 *   Hoàn thành khi: ANSWER Q3 có giới hạn attempt/deadline và lỗi không retryable.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Exactly-once delivery có đồng nghĩa exactly-once business effect không?
 *   Bắt đầu: external payment thành công nhưng local commit/ACK thất bại.
 *   Tra cứu: transaction scope, deduplication và idempotency retention.
 *   Hoàn thành khi: ANSWER Q4 nêu boundary mà broker guarantee không bao phủ.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Vì sao local Java lock không bảo vệ inventory giữa nhiều instance?
 *   Bắt đầu: hai JVM giữ hai lock riêng và cùng đọc stock.
 *   Tra cứu: conditional DB update, row lock và optimistic version.
 *   Hoàn thành khi: ANSWER Q5 đặt invariant tại shared data owner/transaction.
 * <p>
 * B1 [TỰ TRẢ LỜI]: vẽ tạo order và phát event; đánh dấu commit point, duplicate,
 * timeout, retry policy, idempotency boundary và backlog. Điền template, review thủ công:
 * order+inventory+outbox commit có giữ invariant không, relay crash từng điểm có mất/nhân effect không,
 * consumer dedup có cùng transaction với effect không, retry/queue có bound và tuổi message không.
 * Hoàn thành khi: có timeline commit/ACK/crash, owner, giới hạn cụ thể có lý do và recovery/DLQ flow.
 * Đây là mô hình giả định; không tuyên bố broker/payment hay production failure đã được đo.
 */
class Ex01_MessagingRetry {
    static String eventFlowTemplate() {
        return "Order request and data owner:\nInventory invariant and transaction:\n"
                + "DB/outbox commit point:\nRelay publish and ACK/crash timeline:\n"
                + "Consumer duplicate and idempotency boundary/key/retention:\n"
                + "Timeout and retryable errors:\nRetry attempts/deadline/backoff/jitter:\n"
                + "Queue bounds/backpressure/backlog age/DLQ/replay:\n"
                + "Multiple-instance concurrency control:\nAssumptions and evidence still needed:\n";
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Consumer có thể commit effect rồi mất ACK; broker redeliver vì không biết lần trước đã hoàn tất.
 * Relay cũng có thể publish thành công rồi crash trước ghi sent. Dùng event ID/idempotency key;
 * ghi dedup và business effect cùng transaction, retention phù hợp replay, không ACK trước commit.
 * SOLUTION-END
 */
/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Outbox ghi state nghiệp vụ và event trong cùng DB transaction, tránh DB commit nhưng publish bị mất
 * hoặc publish dù DB rollback. Relay gửi sau commit; vẫn có lag, duplicate và cần xử lý backlog.
 * Không tự làm consumer/payment idempotent, không tạo ACID transaction xuyên DB/broker/provider.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Retry vô hạn giữ tài nguyên, phình backlog, khuếch đại overload và nhân side effect nếu thiếu idempotency.
 * Chỉ retry lỗi tạm thời, giới hạn attempt/deadline theo budget, exponential backoff có cap/jitter;
 * lỗi validation/auth không retry. DLQ có owner, alert, sửa nguyên nhân và replay kiểm soát.
 * SOLUTION-END
 */
/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Không. Delivery guarantee có scope broker/transaction cụ thể, không bảo đảm effect ngoài scope chỉ xảy ra một lần.
 * External payment có thể thành công trước timeout; gọi lại cần provider idempotency key và reconciliation.
 * DB consumer cần unique dedup key cùng transaction; retention và compensation phải theo nghiệp vụ.
 * SOLUTION-END
 */
/* ANSWER Q5:
 * SOLUTION-BEGIN
 * synchronized/ReentrantLock chỉ chia sẻ state trong một JVM; instance khác giữ lock khác nên vẫn tranh chấp stock.
 * Data owner dùng conditional update stock >= quantity, version check hoặc row lock trong DB transaction;
 * unique order/idempotency constraint chặn duplicate. Không dựa vào local lock để giữ invariant phân tán.
 * SOLUTION-END
 */
/* MODEL B1:
 * SOLUTION-BEGIN
 * Ví dụ giả định: API authorize/validate, unique order key; transaction conditional reserve inventory + order + outbox.
 * Commit là điểm xác nhận durable order; relay publish event ID, crash trước sent flag có thể publish lại.
 * Consumer transaction insert unique event ID + effect rồi ACK; duplicate đọc trạng thái đã xử lý, không nhân effect.
 * Payment dùng key ổn định và pending state/reconcile, không hứa rollback payment bằng DB rollback.
 * Policy minh họa cần xác minh SLO: tối đa 3 attempt trong deadline 5s, backoff cap 1s có jitter;
 * không retry validation/auth, queue bounded, đo oldest message age và depth, alert theo budget.
 * DLQ do operator sở hữu; replay cùng ID sau sửa nguyên nhân. Shared DB constraint/transaction giữ stock giữa JVM.
 * Đây là design model, chưa có broker/app thật để đo backlog hoặc failure; người đọc review các crash timeline.
 * SOLUTION-END
 */
