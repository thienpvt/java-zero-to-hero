package phase09.d13_requirements_slo;

/**
 * Requirements, scope và SLO.
 * <p>
 * Nguồn: 09-algorithms-system-design.md, mục 13 (Requirements, scope và SLO), câu 1–5.
 * Cần làm trước: xác định use case, actor và boundary của một ứng dụng.
 * Cách làm: trả lời Q1–Q5 rồi điền brief B1; dùng nút ▶ trong Ex01_RequirementsSloTest.
 * <p>
 * Q1 [TỰ TRẢ LỜI] Functional requirement khác non-functional requirement thế nào?
 *   Bắt đầu: phân loại một hành động người dùng và một mục tiêu chất lượng.
 *   Tra cứu: use case và cách đo latency/availability.
 *   Hoàn thành khi: ANSWER Q1 phân biệt hành vi hệ thống với thuộc tính chất lượng có thể đo.
 * <p>
 * Q2 [TỰ TRẢ LỜI] SLI, SLO và SLA khác nhau thế nào?
 *   Bắt đầu: chọn một chỉ số latency hoặc availability.
 *   Tra cứu: định nghĩa chỉ số, mục tiêu nội bộ và cam kết dịch vụ.
 *   Hoàn thành khi: ANSWER Q2 phân biệt phép đo, mục tiêu và cam kết cùng hậu quả.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Vì sao cần giới hạn scope trước khi chọn công nghệ?
 *   Bắt đầu: liệt kê non-goals cho luồng order.
 *   Tra cứu: requirement, giả định tải và chi phí vận hành.
 *   Hoàn thành khi: ANSWER Q3 liên hệ scope với quyết định công nghệ và phần phức tạp được tránh.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Khi nào modular monolith là lựa chọn hợp lý?
 *   Bắt đầu: xét một ứng dụng chưa có bằng chứng cần tách service.
 *   Tra cứu: boundary module, deployment và nhu cầu scale độc lập.
 *   Hoàn thành khi: ANSWER Q4 nêu điều kiện phù hợp và dấu hiệu nên đánh giá lại.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Requirement nào ảnh hưởng trực tiếp đến consistency hoặc durability?
 *   Bắt đầu: xét order, inventory và dữ liệu cần giữ.
 *   Tra cứu: invariant nghiệp vụ, transaction và retention.
 *   Hoàn thành khi: ANSWER Q5 nối yêu cầu không mất/cập nhật đúng dữ liệu với lựa chọn consistency/durability.
 * <p>
 * B1: Viết brief một trang cho luồng order; không chọn kiến trúc trước khi ghi unknowns.
 */
class Ex01_RequirementsSlo {
    static String requirementsChecklist() {
        return "Actors:\nFunctional requirements:\nNon-functional requirements:\n"
                + "Scope and non-goals:\nAssumptions and workload:\nSLO/SLI:\n"
                + "Consistency and durability:\nRetention and privacy:\nUnknowns:\n";
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
