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
    static final Goal Q8_GOAL = null;
}

/* ANSWER Q7:
 *
 */

/* ANSWER Q8:
 *
 */
