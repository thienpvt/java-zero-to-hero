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
    static final Boolean Q1_ATOMIC_IS_JUST_VOLATILE = null;

    // Q6 — CAS trên một ô nhớ có thể gặp ABA hay không.
    static final Boolean Q6_ABA_POSSIBLE = null;

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
        throw new UnsupportedOperationException("TODO Q2");
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

/* ANSWER Q6:
 *
 */
