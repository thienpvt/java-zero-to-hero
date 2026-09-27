package phase02.d04_gc;

/**
 * GC — Bài 3: Pause và mục tiêu
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 4 (Object Lifecycle &amp; Garbage Collection), câu 7, 8.
 * Cần làm trước: Ex01_Reachability.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex03_PauseAndGoalsTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q7 [TỰ TRẢ LỜI] Stop-the-world nghĩa là gì?
 *   Bắt đầu   : viết khối ANSWER Q7. Nói rõ thread ứng dụng bị ảnh hưởng thế nào trong khoảng pause.
 *   Hoàn thành khi: viết xong khối ANSWER Q7.
 * <p>
 * Q8 [TỰ TRẢ LỜI] GC tối ưu điều gì: latency hay throughput?
 *   Bắt đầu   : chọn một giá trị {@code Goal} cho hằng Q8_GOAL (thay null) và viết khối ANSWER Q8.
 *   Kiểm chứng: chạy q08_prediction.
 *   Hoàn thành khi: test q08_prediction xanh và ANSWER Q8 giải thích lựa chọn Goal.
 */
public class Ex03_PauseAndGoals {

    enum Goal {
        LATENCY,
        THROUGHPUT,
        BOTH_TRADEOFF
    }

    // Q8 — chọn mục tiêu mà câu hỏi đang hỏi. Ba lựa chọn nằm ở enum Goal.
    static final Goal Q8_GOAL = Goal.BOTH_TRADEOFF; // SOLUTION-VALUE
}

/* ANSWER Q7:
 * SOLUTION-BEGIN
 * Stop-the-world là khoảng GC tạm dừng các thread ứng dụng để nhìn heap nhất quán.
 * Trong khoảng đó mutator không chạy, nên ứng dụng thấy một pause.
 * Một số collector rút ngắn pause nhưng vẫn có pha dừng thế giới; không phải mọi GC dừng từ đầu đến cuối.
 * SOLUTION-END
 */

/* ANSWER Q8:
 * SOLUTION-BEGIN
 * Không có một câu trả lời chỉ latency hoặc chỉ throughput.
 * Throughput là lượng việc ứng dụng hoàn thành; latency là độ trễ từng lần, gồm cả pause.
 * Collector chọn một đánh đổi: pause ngắn thường tốn throughput, throughput cao thường chấp nhận pause dài hơn.
 * SOLUTION-END
 */
