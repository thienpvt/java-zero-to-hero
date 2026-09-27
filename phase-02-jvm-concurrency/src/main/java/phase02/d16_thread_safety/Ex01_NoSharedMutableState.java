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
    static final Boolean Q1_STATELESS_SAFE = true; // SOLUTION-VALUE

    // Q3 — hằng hỏi biến cục bộ có cần synchronize không.
    static final Boolean Q3_LOCAL_NEEDS_SYNC = false; // SOLUTION-VALUE

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
        // SOLUTION-BEGIN throw Q1
        int sum = 0;
        for (Integer value : xs) {
            sum += value;
        }
        return sum;
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Stateless service không giữ field thay đổi mà nhiều thread cùng dùng.
 * Mỗi lời gọi chỉ đọc tham số và biến cục bộ trên stack của thread đó.
 * Không có ghi dùng chung nên các thread không ghi đè kết quả của nhau.
 * Vì vậy service dạng này thường thread-safe mà không cần khóa.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Object bất biến không đổi sau khi tạo, nên nhiều thread có thể đọc cùng lúc.
 * Không có ghi thì không có race và không cần synchronized chỉ để đọc.
 * Muốn trạng thái mới thì tạo object mới. Ai đang giữ bản cũ vẫn thấy dữ liệu nhất quán.
 * Cách này bỏ shared mutable state thay vì khóa quanh field bị sửa.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Không. Biến cục bộ nằm trên stack của từng lời gọi, thread khác không thấy.
 * Mỗi thread có bản riêng, nên không có shared mutable state để bảo vệ.
 * synchronize chỉ cần khi nhiều thread dùng chung một field hoặc object bị ghi.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Singleton của Spring là một instance cho mọi request, nhiều thread gọi cùng lúc.
 * Bug xuất hiện khi service giữ field mutable, ví dụ bộ đếm, cache, hoặc dữ liệu của request.
 * Request sau ghi đè field mà request trước đang đọc, hoặc hai request cộng dồn sai.
 * Dữ liệu theo request phải là biến cục bộ, tham số, hoặc object bất biến. Field mutable cần đồng bộ.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Shared mutable state là dữ liệu nhiều thread cùng truy cập và ít nhất một thread ghi.
 * Field của singleton, biến static không cố định, hoặc phần tử collection dùng chung đều có thể là vậy.
 * Thiếu đồng bộ thì các lần ghi xen kẽ, hoặc thread khác không nhìn thấy giá trị mới.
 * Đây là nguồn race và lỗi visibility. Ưu tiên bỏ chia sẻ trước khi nghĩ tới khóa.
 * SOLUTION-END
 */
