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
    static final Boolean Q8_DCL_NEEDS_VOLATILE = true; // SOLUTION-VALUE
}

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Visibility là một thread có nhìn thấy kết quả ghi của thread khác hay không.
 * Không có quan hệ happens-before, thread đọc có thể vẫn thấy giá trị cũ.
 * volatile, monitor và các điểm đồng bộ khác làm ghi trở nên nhìn thấy được.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Happens-before là quan hệ thứ tự một phần của Java Memory Model.
 * Nếu hành động A happens-before hành động B thì kết quả của A phải nhìn thấy được tại B.
 * Ghi một field volatile happens-before lần đọc field đó sau này.
 * Nhả monitor happens-before lần lấy cùng monitor của thread khác.
 * SOLUTION-END
 */

/* ANSWER Q8:
 * SOLUTION-BEGIN
 * Có. Double-checked locking cần volatile trên reference được công bố.
 * Không volatile thì thread khác có thể thấy reference trước khi constructor xong.
 * Thread đó dùng object khi các field ghi trong constructor chưa được nhìn thấy.
 * volatile buộc mọi ghi của constructor happens-before lần đọc reference khác null.
 * SOLUTION-END
 */
