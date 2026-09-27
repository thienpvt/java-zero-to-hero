package phase02.d16_thread_safety;

import java.util.List;

/**
 * Thread safety — Bài 1: Không chia sẻ mutable state
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 16 (Thread Safety Design), câu 1, 2, 3, 4, 5.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_NoSharedMutableStateTest bằng nút ▶
 * (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Stateless service thường thread-safe vì sao?
 *   Bắt đầu   : đọc {@code square(int)}, hàm đã làm xong. Điền Q1_STATELESS_SAFE (thay null).
 *               Cài {@code addAll(List&lt;Integer&gt;)} để cộng mọi phần tử bằng biến cục bộ,
 *               cùng kiểu với square: không field.
 *   Kiểm chứng: chạy q01_prediction và q01_addAllSums. Nếu tổng sai, Debug test, F7 vào addAll.
 *   Hoàn thành khi: các test q01_* xanh và viết xong khối ANSWER Q1.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Immutable object giúp concurrency thế nào?
 *   Bắt đầu   : viết khối ANSWER Q2. Nói rõ nhiều thread đọc một object không bị sửa thì cần gì.
 *   Hoàn thành khi: viết xong khối ANSWER Q2.
 * <p>
 * Q3 [DỰ ĐOÁN] Local variable có cần synchronize không?
 *   Bắt đầu   : điền Q3_LOCAL_NEEDS_SYNC (thay null). Nhìn biến {@code sum} trong addAll.
 *               Đừng tạo hai thread để chứng minh biến cục bộ bị mất.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và viết xong khối ANSWER Q3.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Spring singleton service có thể gặp concurrency bug khi nào?
 *   Bắt đầu   : viết khối ANSWER Q4. Nêu một field của service mà mọi request cùng đụng vào.
 *   Hoàn thành khi: viết xong khối ANSWER Q4.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Shared mutable state là gì?
 *   Bắt đầu   : làm Q1 và Q4 trước, rồi viết khối ANSWER Q5. Nêu dữ liệu nào được chia sẻ và ai ghi.
 *   Hoàn thành khi: viết xong khối ANSWER Q5.
 */
public class Ex01_NoSharedMutableState {

    // Q1 — hằng hỏi stateless service thường có thread-safe không.
    static final Boolean Q1_STATELESS_SAFE = null;

    // Q3 — hằng hỏi biến cục bộ có cần synchronize không.
    static final Boolean Q3_LOCAL_NEEDS_SYNC = null;

    /**
     * Ví dụ đã làm xong: bình phương {@code n} bằng biến cục bộ, không field.
     */
    static int square(int n) {
        int value = n * n;
        return value;
    }

    /**
     * Cộng mọi phần tử của {@code xs}. Chỉ dùng biến cục bộ, không ghi field.
     */
    static int addAll(List<Integer> xs) {
        throw new UnsupportedOperationException("TODO Q1");
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
