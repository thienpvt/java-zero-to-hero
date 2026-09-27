package phase02.d10_atomic;

/**
 * Atomic classes — Bài 2: Nhiều biến
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 10 (Atomic Classes), câu 5.
 * Cần làm trước: Ex01_Cas.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex02_MultiVariableTest bằng nút ▶
 * (Ctrl+Shift+F10).
 * <p>
 * Q5 [DỰ ĐOÁN + CODE] AtomicInteger có phù hợp để bảo vệ nhiều biến phải update cùng transaction không?
 *   Bắt đầu   : điền Q5_ONE_ATOMIC_COVERS_TWO_FIELDS (thay null). Trong {@code Pair.transfer()},
 *               đang giữ monitor, đổi cả {@code a} và {@code b} sao cho tổng không đổi và mỗi lời gọi
 *               làm cả hai field lệch khỏi giá trị lúc vào.
 *   Kiểm chứng: chạy q05_prediction, q05_oneTransferKeepsSum và q05_fourThreadsKeepSum.
 *               Bốn thread, mỗi thread gọi transfer 1000 lần, cổng {@code CountDownLatch},
 *               {@code join} tối đa 10 giây. Không dùng {@code Thread.sleep}.
 *   Hoàn thành khi: q05_* xanh; sau các lần transfer, {@code a + b} bằng tổng lúc mở
 *               và cả hai field khác giá trị mở.
 */
public class Ex02_MultiVariable {

    // Q5 — một AtomicInteger có bao được hai field trong cùng một giao dịch hay không.
    static final Boolean Q5_ONE_ATOMIC_COVERS_TWO_FIELDS = false; // SOLUTION-VALUE

    /** Hai {@code int} đổi trong cùng monitor. Tổng {@code a + b} không đổi. */
    static final class Pair {
        private int a;
        private int b;

        Pair(int a, int b) {
            this.a = a;
            this.b = b;
        }

        synchronized void transfer() {
            // SOLUTION-BEGIN throw Q5
            a++;
            b--;
            // SOLUTION-END
        }

        synchronized int a() {
            return a;
        }

        synchronized int b() {
            return b;
        }

        synchronized int sum() {
            return a + b;
        }
    }
}

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Một AtomicInteger chỉ nguyên tử hóa một biến, không gom hai field thành một giao dịch.
 * Hai lần CAS liên tiếp vẫn để thread khác chen giữa lần cập nhật thứ nhất và lần thứ hai.
 * Muốn đổi cả hai mà giữ tổng thì khóa một monitor bao cả hai, hoặc CAS một tham chiếu tới cả cặp.
 * SOLUTION-END
 */
