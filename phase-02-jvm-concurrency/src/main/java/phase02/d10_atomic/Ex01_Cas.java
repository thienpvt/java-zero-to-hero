package phase02.d10_atomic;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Atomic classes — Bài 1: CAS
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 10 (Atomic Classes), câu 1, 2, 3, 4, 6.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_CasTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] AtomicInteger khác volatile int thế nào?
 *   Bắt đầu   : điền Q1_ATOMIC_IS_JUST_VOLATILE (thay null). Ctrl+N mở AtomicInteger, Ctrl+Q
 *               đọc đoạn đầu Javadoc, đối chiếu với volatile.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và viết xong khối ANSWER Q1.
 * <p>
 * Q2 [CODE] CAS là gì?
 *   Bắt đầu   : cài đặt {@code incrementTo(AtomicInteger, int)} bằng vòng {@code compareAndSet}
 *               tự viết. Không gọi {@code incrementAndGet}. {@code incrementTo(int)} cho sẵn:
 *               tạo bộ đếm từ 0 rồi gọi method đó.
 *   Kiểm chứng: chạy q02_oneThreadReaches1000 và q02_fourThreadsTotal4000. Bốn thread cùng một
 *               {@code AtomicInteger}, cổng {@code CountDownLatch}, {@code join} tối đa 10 giây.
 *               Không dùng {@code Thread.sleep}. Ctrl+B vào {@code compareAndSet} nếu muốn đọc chữ ký.
 *   Hoàn thành khi: một thread đi từ 0 lên 1000; bốn thread trên cùng một bộ đếm cộng đủ 4000.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Vì sao CAS có thể tránh lock trong một số trường hợp?
 *   Bắt đầu   : viết khối ANSWER Q3. Nhắc trường hợp không có thread khác đang sửa cùng biến.
 *   Hoàn thành khi: viết xong khối ANSWER Q3.
 * <p>
 * Q4 [TỰ TRẢ LỜI] CAS có nhược điểm gì khi contention cao?
 *   Bắt đầu   : viết khối ANSWER Q4. Nói vòng thử lại chiếm CPU thế nào.
 *   Hoàn thành khi: viết xong khối ANSWER Q4.
 * <p>
 * Q6 [DỰ ĐOÁN] ABA problem là gì ở mức khái niệm?
 *   Bắt đầu   : điền Q6_ABA_POSSIBLE (thay null). Viết ANSWER Q6 với một ví dụ stack và node.
 *               Không cần dựng test tái hiện ABA.
 *   Kiểm chứng: chạy q06_prediction.
 *   Hoàn thành khi: q06_prediction xanh và ANSWER Q6 có ví dụ stack/node.
 */
public class Ex01_Cas {

    // Q1 — AtomicInteger có chỉ là một volatile int hay không.
    static final Boolean Q1_ATOMIC_IS_JUST_VOLATILE = false; // SOLUTION-VALUE

    // Q6 — CAS trên một ô nhớ có thể gặp ABA hay không.
    static final Boolean Q6_ABA_POSSIBLE = true; // SOLUTION-VALUE

    /**
     * Tăng một {@code AtomicInteger} mới từ 0 đúng {@code n} lần.
     * Method này cho sẵn. Phần cần viết là overload nhận {@code AtomicInteger}.
     *
     * @throws IllegalArgumentException nếu {@code n &lt; 0}
     */
    static int incrementTo(int n) {
        return incrementTo(new AtomicInteger(), n);
    }

    /**
     * Tăng {@code current} đúng {@code n} lần bằng vòng {@code compareAndSet}.
     * Trả giá trị của bộ đếm sau những lần tăng của lời gọi này.
     *
     * @throws IllegalArgumentException nếu {@code current} là null hoặc {@code n &lt; 0}
     */
    static int incrementTo(AtomicInteger current, int n) {
        // SOLUTION-BEGIN throw Q2
        if (current == null) {
            throw new IllegalArgumentException("AtomicInteger không được null.");
        }
        if (n < 0) {
            throw new IllegalArgumentException("Số lần tăng không được âm: " + n);
        }
        for (int step = 0; step < n; step++) {
            int seen;
            do {
                seen = current.get();
            } while (!current.compareAndSet(seen, seen + 1));
        }
        return current.get();
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * AtomicInteger không phải volatile int. volatile chỉ làm một lần đọc hoặc một lần ghi được nhìn thấy.
 * Phép đọc-sửa-ghi như cộng một vẫn là nhiều bước, dù biến có volatile.
 * AtomicInteger dùng CAS để ghi giá trị mới chỉ khi ô nhớ vẫn bằng giá trị vừa đọc.
 * Vì vậy tăng một biến có thể nguyên tử mà không cần khóa monitor.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * CAS nghĩa là compare-and-set: ghi giá trị mới chỉ khi giá trị hiện tại vẫn bằng giá trị đã đọc.
 * Vòng lặp đọc giá trị, gọi compareAndSet, thất bại thì đọc lại. Không gọi incrementAndGet.
 * Khi bốn thread cùng tăng một AtomicInteger, mỗi lần CAS thành công là một lần cộng không mất.
 * Tổng sau khi mọi thread kết thúc bằng đúng số lần tăng đã yêu cầu.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Khi không có thread khác sửa cùng biến, compareAndSet thành công ngay lần đầu.
 * Thread không lấy monitor, không vào hàng đợi khóa và không nhường CPU cho lần chờ đó.
 * CAS tránh lock trên đường không tranh chấp; chỉ khi thất bại mới phải thử lại.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Contention cao thì nhiều thread cùng thất bại CAS và quay lại ngay.
 * Vòng thử lại đó là spin: thread giữ lõi CPU thay vì ngủ như khi chờ một lock.
 * Số lần thử tăng theo số thread, nên throughput có thể kém hơn khóa làm thread park.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * ABA xảy ra được. CAS chỉ thấy giá trị bằng A, không thấy giá trị đã từng thành B rồi trở lại A.
 * Ví dụ stack: head là node A. Thread 1 đọc A và node kế của A.
 * Thread 2 pop A, pop node B, rồi push lại đúng node A. Head lại là A nhưng danh sách phía sau đã khác.
 * CAS của thread 1 từ A sang next cũ thành công và nối stack vào một node không còn đúng chỗ.
 * SOLUTION-END
 */
