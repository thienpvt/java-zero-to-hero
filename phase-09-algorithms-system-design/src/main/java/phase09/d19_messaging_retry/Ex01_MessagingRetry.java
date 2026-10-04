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
