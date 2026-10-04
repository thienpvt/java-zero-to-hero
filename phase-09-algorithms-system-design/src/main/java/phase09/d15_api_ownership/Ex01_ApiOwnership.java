package phase09.d15_api_ownership;

/**
 * API, data ownership và invariant.
 * <p>
 * Nguồn: 09-algorithms-system-design.md, mục 15 (API, data ownership và invariant), câu 1–5.
 * Cần làm trước: xác định use case, boundary API và dữ liệu cần bảo vệ.
 * Cách làm: trả lời Q1–Q5, vẽ B1 từ request đến database, rồi chạy Ex01_ApiOwnershipTest.
 * <p>
 * Q1 [TỰ TRẢ LỜI] Idempotency cần thiết ở endpoint nào và vì sao?
 *   Bắt đầu: xét retry sau timeout khi client chưa biết request đã commit chưa.
 *   Tra cứu: key, phạm vi lưu, thời hạn và response khi duplicate.
 *   Hoàn thành khi: ANSWER Q1 nêu endpoint có side effect và giới hạn của idempotency.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Ai chịu trách nhiệm giữ invariant của order và inventory?
 *   Bắt đầu: ghi invariant cụ thể, không chỉ kiểm tra UI.
 *   Tra cứu: data owner, transaction boundary và concurrent requests.
 *   Hoàn thành khi: ANSWER Q2 chỉ ra nơi kiểm tra/ghi nguyên tử và cách bảo vệ giữa instance.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Khi nào API DTO nên độc lập persistence entity?
 *   Bắt đầu: so sánh client contract với schema lưu trữ.
 *   Tra cứu: validation, versioning và thay đổi schema.
 *   Hoàn thành khi: ANSWER Q3 nêu lợi ích và chi phí mapping khi hai mô hình tách nhau.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Authorization nên kiểm tra quyền truy cập record thế nào?
 *   Bắt đầu: dùng principal hiện tại và record được yêu cầu.
 *   Tra cứu: ownership/tenant scope và truy vấn có giới hạn quyền.
 *   Hoàn thành khi: ANSWER Q4 giải thích server kiểm tra quyền trên record, không tin ID/client UI.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Error response nào cần ổn định cho client?
 *   Bắt đầu: lập lỗi validation, auth, conflict và dependency.
 *   Tra cứu: status, machine-readable code và dữ liệu không được lộ.
 *   Hoàn thành khi: ANSWER Q5 nêu contract ổn định, không phụ thuộc exception nội bộ.
 * <p>
 * B1: vẽ request flow tạo order, đánh dấu validation, authorization, owner, transaction và duplicate response.
 */
class Ex01_ApiOwnership {
    static String orderFlowTemplate() {
        return "Request and validation:\nAuthorization:\nData owner:\nDTO/entity boundary:\n"
                + "Transaction boundary:\nOrder/inventory invariant:\nIdempotency key and duplicate request:\n"
                + "Stable error contract:\n";
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
