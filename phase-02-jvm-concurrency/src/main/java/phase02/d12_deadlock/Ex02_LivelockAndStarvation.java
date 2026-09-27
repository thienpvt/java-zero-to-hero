package phase02.d12_deadlock;

/**
 * Deadlock — Bài 2: Livelock và starvation
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 12 (Deadlock), câu 4, 5.
 * Cần làm trước: Ex01_Ordering.
 * Cách làm: làm lần lượt từng câu; viết khối ANSWER. Không có test mô phỏng vòng lặp không thoát.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Deadlock khác livelock thế nào?
 *   Bắt đầu   : viết khối ANSWER Q4. Phân biệt thread bị chặn với thread vẫn chạy nhưng không tiến.
 *   Hoàn thành khi: viết xong khối ANSWER Q4. Không mô phỏng livelock bằng vòng lặp không thoát.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Starvation là gì?
 *   Bắt đầu   : làm Q4 trước, rồi viết khối ANSWER Q5. Nêu một thread không nhận được tài nguyên
 *               trong khi các thread khác vẫn hoàn thành việc.
 *   Hoàn thành khi: viết xong khối ANSWER Q5.
 */
public class Ex02_LivelockAndStarvation {

    private Ex02_LivelockAndStarvation() {
    }
}

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Deadlock: các thread bị chặn, không chạy tiếp, vì mỗi bên đang chờ lock mà bên kia giữ.
 * Livelock: các thread vẫn chạy và đổi trạng thái, nhưng không hoàn thành việc.
 * Ví dụ: hai bên cùng nhường lock rồi cùng lấy lại, lặp mãi, không tiến.
 * Bài này không mô phỏng livelock bằng vòng lặp không thoát.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Starvation là một thread sẵn sàng làm việc nhưng hầu như không bao giờ nhận được tài nguyên.
 * Các thread khác cứ lấy lock hoặc CPU trước, nên thread đó không tiến dù hệ thống vẫn chạy.
 * Khác deadlock: không có vòng chờ, các thread khác vẫn hoàn thành việc.
 * Khác livelock: không phải mọi bên đều bận đổi trạng thái; có bên bị bỏ đói.
 * SOLUTION-END
 */
