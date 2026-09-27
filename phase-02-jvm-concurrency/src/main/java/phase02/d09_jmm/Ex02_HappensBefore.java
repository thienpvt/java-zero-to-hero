package phase02.d09_jmm;

/**
 * volatile và JMM — Bài 2: Happens-before
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 9 (volatile và Java Memory Model), câu 4, 5, 8.
 * Cần làm trước: Ex01_VolatileIsNotAtomic.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex02_HappensBeforeTest bằng nút ▶
 * (Ctrl+Shift+F10).
 * <p>
 * Q4 [TỰ TRẢ LỜI] Visibility nghĩa là gì?
 *   Bắt đầu   : viết khối ANSWER Q4.
 *   Hoàn thành khi: viết xong khối ANSWER Q4.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Happens-before nghĩa là gì?
 *   Bắt đầu   : viết khối ANSWER Q5. Nêu quan hệ này áp lên lần ghi và lần đọc sau đó thế nào.
 *   Hoàn thành khi: viết xong khối ANSWER Q5.
 * <p>
 * Q8 [DỰ ĐOÁN + TỰ TRẢ LỜI] Double-checked locking cần volatile vì sao?
 *   Bắt đầu   : điền Q8_DCL_NEEDS_VOLATILE (thay null). Viết khối ANSWER Q8.
 *               Đừng dựng hai thread để chứng minh double-checked locking bị hỏng.
 *   Kiểm chứng: chạy q08_prediction.
 *   Hoàn thành khi: q08_prediction xanh và viết xong khối ANSWER Q8.
 */
public class Ex02_HappensBefore {

    // Q8 — hằng hỏi reference trong double-checked locking có cần volatile không.
    static final Boolean Q8_DCL_NEEDS_VOLATILE = null;
}

/* ANSWER Q4:
 *
 */

/* ANSWER Q5:
 *
 */

/* ANSWER Q8:
 *
 */
