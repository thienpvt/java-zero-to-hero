package phase01.d02_arraylist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * ArrayList — Bài 3: Ba cái bẫy thường gặp (for-each + remove, null, thread-safety)
 *
 * Nguồn: 01-java-core-advanced.md, mục 2 (ArrayList), câu 9, 8, 7.
 * Cần làm trước: không (bài này dùng thẳng java.util.ArrayList thật, không cần
 * MiniArrayList của Ex01_CapacityAndResize).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex03_PitfallsTest bằng
 * nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q9 [DỰ ĐOÁN + CODE] Khi iterate rồi remove sai cách có thể xảy ra chuyện gì?
 *   Bắt đầu   : đọc q09_duDoan_forEachRemovePhanTuDau và q09_duDoan_forEachRemovePhanTuKeCuoi
 *               trong test (chạy for-each rồi gọi list.remove(Object) thay vì
 *               iterator.remove()); điền 2 hằng số Q9_*.
 *   Kiểm chứng: đặt breakpoint trong ArrayList$Itr.next() và Itr.checkForComodification
 *               (Ctrl+N → ArrayList → Ctrl+F12 → Itr), Debug 2 test trên, F8 để thấy
 *               modCount vs expectedModCount, và vì sao hasNext() đôi khi trả false trước
 *               khi kịp gọi next() lần cuối.
 *   Code      : static void removeBlank(List&lt;String&gt;) — xóa tại chỗ các phần tử
 *               null hoặc isBlank() bằng Iterator.remove() hoặc List.removeIf() (an toàn,
 *               không ném ConcurrentModificationException).
 *   Hoàn thành khi: các test q09_* xanh; giải thích được vì sao removeBlank() an toàn còn
 *               vòng for-each gọi list.remove() trực tiếp thì phụ thuộc vị trí phần tử bị xóa.
 *
 * Q8 [DỰ ĐOÁN] `ArrayList` có thể chứa `null` không?
 *   Bắt đầu   : viết thử code add(null) hai lần vào một ArrayList rồi điền 2 hằng số
 *               Q8_*.
 *   Kiểm chứng: chạy q08_*; nếu còn nghi ngờ, Ctrl+N → ArrayList → Ctrl+F12 → add, đọc
 *               Javadoc (Ctrl+Q) xem add() nói gì về null — sau khi đã điền dự đoán.
 *   Hoàn thành khi: test q08_* xanh; giải thích được ArrayList xử lý null thế nào so với
 *               một số cấu trúc khác (ví dụ TreeSet theo natural order).
 *
 * Q7 [DỰ ĐOÁN + CODE + THÍ NGHIỆM] `ArrayList` có thread-safe không?
 *   Bắt đầu   : điền Q7_ARRAYLIST_IS_THREAD_SAFE (thay null) sau khi đã đọc source add()
 *               hoặc chạy thí nghiệm; cài đặt threadSafeList().
 *   Kiểm chứng: chạy main() với perThread lớn (100_000) nhiều lần, sau khi đã điền dự
 *               đoán, tự so size cuối của "ArrayList thường" với "Collections.synchronizedList".
 *   Code      : static &lt;T&gt; List&lt;T&gt; threadSafeList() → trả về
 *               Collections.synchronizedList(new ArrayList&lt;&gt;()).
 *   Hoàn thành khi: test q07_* xanh; giải thích được vì sao add() của ArrayList thường
 *               không atomic (race trên size và trên ô mảng) còn synchronizedList thì có,
 *               và vì sao vẫn phải tự lock ngoài khi for-each trên synchronizedList.
 */
public class Ex03_Pitfalls {

    // Q9 — kịch bản: for-each trên new ArrayList<>(List.of("a", "b", "c")), gặp phần tử thì
    // gọi list.remove(phầnTử) (không dùng iterator.remove()). Ghi lại simple name của
    // exception bị ném ra, hoặc "none" nếu không ném gì.
    static final String Q9_FOREACH_REMOVE_FIRST_EXCEPTION = "ConcurrentModificationException"; // SOLUTION-VALUE
    // Kịch bản tương tự nhưng remove phần tử kế cuối ("b" trong 3 phần tử) — có ném lỗi không?
    static final Boolean Q9_FOREACH_REMOVE_SECOND_LAST_THROWS = false; // SOLUTION-VALUE

    // Q8 — kịch bản: ArrayList<String> rồi add(null) hai lần.
    static final Boolean Q8_CAN_ADD_NULL = true; // SOLUTION-VALUE
    static final Integer Q8_SIZE_AFTER_TWO_NULLS = 2; // SOLUTION-VALUE

    // Q7 — kịch bản: nhiều thread cùng add vào một ArrayList thường. Hằng hỏi: add() có
    // được đồng bộ hóa (thread-safe) không? Đọc source hoặc chạy main() rồi mới điền.
    static final Boolean Q7_ARRAYLIST_IS_THREAD_SAFE = false; // SOLUTION-VALUE

    /**
     * Xóa tại chỗ (trong {@code list}) mọi phần tử {@code null} hoặc {@code isBlank()}.
     *
     * @throws NullPointerException nếu {@code list} là {@code null}
     */
    static void removeBlank(List<String> list) {
        // SOLUTION-BEGIN throw Q9
        list.removeIf(s -> s == null || s.isBlank());
        // SOLUTION-END
    }

    /**
     * Trả về một {@code List} bọc {@code ArrayList} bằng khóa đồng bộ chung
     * ({@link Collections#synchronizedList}), giúp mỗi lần gọi method (ví dụ
     * {@code add}) trở thành atomic khi có nhiều thread cùng dùng.
     */
    static <T> List<T> threadSafeList() {
        // SOLUTION-BEGIN throw Q7
        return Collections.synchronizedList(new ArrayList<>());
        // SOLUTION-END
    }

    /**
     * Cho sẵn: chạy {@code threads} thread, mỗi thread add {@code perThread} phần tử vào
     * {@code list} sau khi tất cả cùng xuất phát qua một {@link CountDownLatch}; chờ tất cả
     * hoàn thành rồi trả về {@code list.size()}. Nếu bất kỳ thread nào ném exception, các
     * exception đó được gom lại và ném lại (gộp vào 1 {@link RuntimeException}, các lỗi còn
     * lại nằm trong {@code getSuppressed()}).
     */
    static int concurrentAdds(List<Integer> list, int threads, int perThread) {
        CountDownLatch startGate = new CountDownLatch(1);
        List<Throwable> failures = new CopyOnWriteArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            for (int t = 0; t < threads; t++) {
                pool.submit(() -> {
                    try {
                        startGate.await();
                        for (int i = 0; i < perThread; i++) {
                            list.add(i);
                        }
                    } catch (Throwable e) {
                        failures.add(e);
                    }
                });
            }
            startGate.countDown();
            pool.shutdown();
            try {
                pool.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Bị ngắt khi chờ các thread add hoàn thành.", e);
            }
        } finally {
            pool.shutdownNow();
        }
        if (!failures.isEmpty()) {
            RuntimeException combined =
                    new RuntimeException(failures.size() + " thread lỗi khi add, lỗi đầu tiên: " + failures.get(0));
            for (Throwable f : failures) {
                combined.addSuppressed(f);
            }
            throw combined;
        }
        return list.size();
    }

    /**
     * Cho sẵn: so sánh {@code list.size()} cuối cùng sau khi {@code threads} = 4 thread cùng
     * add {@code perThread} phần tử, giữa một {@code ArrayList} thường (không đồng bộ) và
     * kết quả của {@link #threadSafeList()}; trả về báo cáo dạng văn bản.
     */
    static String runExperiment(int perThread) {
        int expected = 4 * perThread;
        StringBuilder report = new StringBuilder();
        report.append("perThread=").append(perThread).append(", expected size=").append(expected).append('\n');
        try {
            int size = concurrentAdds(new ArrayList<>(), 4, perThread);
            report.append("ArrayList thường          : size cuối = ").append(size).append('\n');
        } catch (RuntimeException e) {
            report.append("ArrayList thường          : NÉM LỖI - ").append(e.getMessage()).append('\n');
        }
        int safeSize = concurrentAdds(threadSafeList(), 4, perThread);
        report.append("Collections.synchronizedList: size cuối = ").append(safeSize).append('\n');
        return report.toString();
    }

    public static void main(String[] args) {
        System.out.println(runExperiment(100_000));
    }
}

/* OBSERVATION Q7:
 * SOLUTION-BEGIN
 * ArrayList thường không đồng bộ: add() đọc size, ghi phần tử vào elementData[size], rồi
 * size++ — ba bước này không atomic. Khi nhiều thread add đồng thời, chúng có thể đọc cùng
 * giá trị size, ghi đè lên cùng một ô (mất dữ liệu, size cuối nhỏ hơn tổng mong đợi), hoặc
 * cùng lúc trigger grow() gây ArrayIndexOutOfBoundsException khi ghi vào mảng cũ đã bị thay
 * bởi thread khác. Collections.synchronizedList bọc mọi method bằng cùng một lock nên mỗi
 * lần add() trở thành atomic — kết quả size cuối luôn đúng và tất định. Trade-off: for-each
 * trên synchronizedList KHÔNG tự động an toàn (chỉ từng lệnh gọi method riêng lẻ được lock),
 * nên vẫn phải tự "synchronized (list) { for (... : list) ... }" khi iterate.
 * SOLUTION-END
 */
