package phase02.d09_jmm;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * volatile và JMM — Bài 1: volatile không atomic
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 9 (volatile và Java Memory Model), câu 1, 2, 3, 6, 7.
 * Cần làm trước: d07_race (SafeCounter) và d08_synchronized (visibility của monitor).
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_VolatileIsNotAtomicTest bằng nút ▶
 * (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] {@code volatile} giải quyết vấn đề gì?
 *   Bắt đầu   : viết khối ANSWER Q1. Đọc class VolatileCounter bên dưới, đừng sửa nó.
 *   Tra cứu   : Ctrl+N mở lớp này, Ctrl+F12 chọn increment, F7 vào {@code count++}.
 *   Hoàn thành khi: viết xong khối ANSWER Q1.
 * <p>
 * Q2 [DỰ ĐOÁN] volatile có đảm bảo atomicity không?
 *   Bắt đầu   : đọc increment của VolatileCounter. Điền Q2_VOLATILE_MAKES_INCREMENT_ATOMIC
 *               (thay null). Viết khối ANSWER Q2.
 *   Kiểm chứng: chạy q02_prediction. Đừng viết test đếm VolatileCounter để chứng minh mất cập nhật.
 *   Hoàn thành khi: q02_prediction xanh và viết xong khối ANSWER Q2.
 * <p>
 * Q3 [DỰ ĐOÁN + CODE] {@code volatile int count; count++} có thread-safe không?
 *   Bắt đầu   : điền Q3_VOLATILE_COUNT_PLUS_PLUS_SAFE (thay null). Cài {@code increment()}
 *               và {@code count()} của AtomicCount. Đừng sửa VolatileCounter.
 *   Tra cứu   : Ctrl+N mở AtomicInteger, Ctrl+F12 xem method tăng giá trị, Ctrl+Q đọc Javadoc.
 *   Kiểm chứng: chạy q03_prediction và q03_atomicCountReaches40000. Bốn thread cùng xuất phát
 *               qua {@code CountDownLatch}, mỗi thread gọi increment 10000 lần, {@code join}
 *               tối đa 10 giây. Không assert số đếm của VolatileCounter.
 *   Hoàn thành khi: hai test q03_* xanh và viết xong khối ANSWER Q3.
 * <p>
 * Q6 [TỰ TRẢ LỜI] Khi nào volatile phù hợp?
 *   Bắt đầu   : viết khối ANSWER Q6. Xét một cờ {@code boolean} do đúng một thread ghi,
 *               các thread khác chỉ đọc.
 *   Hoàn thành khi: viết xong khối ANSWER Q6, có nêu trường hợp một writer.
 * <p>
 * Q7 [TỰ TRẢ LỜI] volatile và synchronized khác nhau thế nào?
 *   Bắt đầu   : viết khối ANSWER Q7. So hai cơ chế khi nhiều thread cùng đụng một biến,
 *               gồm mutual exclusion.
 *   Hoàn thành khi: viết xong khối ANSWER Q7.
 */
public class Ex01_VolatileIsNotAtomic {

    /** Ví dụ mục 9. Giữ nguyên class này. Test không đếm field này trên nhiều thread. */
    static final class VolatileCounter {
        private volatile int count;

        void increment() {
            count++;
        }

        int count() {
            return count;
        }
    }

    // Q2 — hằng hỏi volatile có biến thao tác nhiều bước thành nguyên tử không.
    static final Boolean Q2_VOLATILE_MAKES_INCREMENT_ATOMIC = false; // SOLUTION-VALUE

    // Q3 — hằng hỏi volatile int rồi count++ có an toàn khi nhiều thread cùng tăng không.
    static final Boolean Q3_VOLATILE_COUNT_PLUS_PLUS_SAFE = false; // SOLUTION-VALUE

    /** Q3 — điền thân {@code increment} và {@code count}. */
    static final class AtomicCount {
        private final AtomicInteger count = new AtomicInteger();

        void increment() {
            // SOLUTION-BEGIN throw Q3
            count.incrementAndGet();
            // SOLUTION-END
        }

        int count() {
            // SOLUTION-BEGIN throw Q3
            return count.get();
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * volatile giải quyết visibility: ghi field này happens-before lần đọc sau trên thread khác.
 * Thread đọc thấy giá trị vừa được ghi, cùng các ghi khác nằm trước lần ghi volatile đó.
 * Nó cũng ràng buộc ordering quanh biến đó.
 * Nó không biến một thao tác nhiều bước thành atomic.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Không. volatile không đảm bảo atomicity.
 * Nó chỉ bảo đảm visibility và ordering của từng lần đọc hoặc ghi field đó.
 * Phép gồm nhiều bước vẫn có thể bị thread khác xen vào giữa chừng.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Không. volatile int rồi count++ không thread-safe.
 * count++ vẫn là đọc field, cộng một, rồi ghi lại. volatile không gom ba bước đó thành một.
 * Hai thread có thể đọc cùng một giá trị rồi ghi đè, nên một lần tăng biến mất.
 * AtomicCount dùng AtomicInteger.incrementAndGet để cả bước tăng là một thao tác CAS.
 * synchronized quanh count++ cũng được, vì monitor vừa loại trừ vừa tạo happens-before.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * volatile phù hợp khi một thread ghi và các thread khác chỉ đọc một biến đơn.
 * Cờ boolean running do một writer gán, các worker chỉ đọc, là trường hợp điển hình.
 * Mỗi lần gán hoặc đọc là một thao tác, nên không cần mutual exclusion.
 * Nhiều thread cùng đọc-sửa-ghi, như count++, thì volatile không đủ.
 * SOLUTION-END
 */

/* ANSWER Q7:
 * SOLUTION-BEGIN
 * volatile không có mutual exclusion: nhiều thread vẫn thực hiện thao tác cùng lúc.
 * synchronized lấy monitor nên cùng một lúc chỉ một thread ở trong critical section.
 * Nhả monitor happens-before lần lấy monitor sau, nên synchronized cũng có visibility.
 * volatile rẻ hơn khi chỉ cần công bố một ghi; nó không bảo vệ một chuỗi nhiều bước.
 * SOLUTION-END
 */
