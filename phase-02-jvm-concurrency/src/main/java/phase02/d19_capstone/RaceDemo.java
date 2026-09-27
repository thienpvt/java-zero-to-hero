package phase02.d19_capstone;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Bài tích hợp — RaceDemo (nhiều thread rút trên {@code Account})
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục Practical Exercise.
 * Cần làm trước: Account trong package này.
 * Cách làm: {@code runExperiment} và {@code main} cho sẵn, không cần sửa. Chạy b01_experimentRuns
 * bằng nút ▶ (Ctrl+Shift+F10), rồi viết OBSERVATION B1 ở cuối file.
 * <p>
 * Thí nghiệm của B1 dùng {@code ExecutorService} và cổng {@code CountDownLatch}. Mỗi task gọi
 * {@code Account.withdraw}. Pool chờ tối đa 10 giây. Báo cáo có chữ Account và số dư nhìn thấy
 * sau khi các task kết thúc hoặc hết giờ. Test không assert số dư đó: một lần chạy đúng không
 * chứng minh thread-safe.
 */
public class RaceDemo {

    private static final int WITHDRAWALS_EACH = 100;

    /**
     * Cho sẵn, không cần sửa. {@code threads} task cùng rút {@code BigDecimal.ONE} trên một
     * {@code Account}, qua cổng xuất phát, rồi pool dừng trong tối đa 10 giây.
     */
    static String runExperiment(int threads) {
        if (threads < 1) {
            throw new IllegalArgumentException("threads phải lớn hơn 0.");
        }
        Account account = new Account(new BigDecimal("1000"));
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            for (int i = 0; i < threads; i++) {
                pool.execute(() -> {
                    try {
                        start.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    for (int n = 0; n < WITHDRAWALS_EACH; n++) {
                        account.withdraw(BigDecimal.ONE);
                    }
                });
            }
            start.countDown();
        } finally {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                pool.shutdownNow();
            }
        }
        return "Account threads=" + threads + " balance=" + account.balance();
    }

    public static void main(String[] args) {
        int threads = args.length == 0 ? 8 : Integer.parseInt(args[0]);
        System.out.println(runExperiment(threads));
    }
}

/* OBSERVATION B1:
 * SOLUTION-BEGIN
 * Hai thread có thể cùng đọc một số dư đủ tiền, cùng qua compareTo, rồi cùng ghi balance.subtract.
 * Lần ghi sau đè lần ghi trước nên một lần rút biến mất và số dư cao hơn số lần đã trừ.
 * Báo cáo in balance sau khi pool dừng. Con số đó đổi theo lịch thread, nên bài không assert số dư.
 * SOLUTION-END
 */
