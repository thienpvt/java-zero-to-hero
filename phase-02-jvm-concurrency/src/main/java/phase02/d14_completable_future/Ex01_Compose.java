package phase02.d14_completable_future;

import java.util.concurrent.CompletableFuture;

/**
 * CompletableFuture — Bài 1: Nối stage
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 14 (CompletableFuture), câu 1, 2, 3, 5.
 * Cần làm trước: d13_executor.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_ComposeTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] Future có điểm yếu gì mà CompletableFuture giải quyết?
 *   Bắt đầu   : viết khối ANSWER Q1. Ctrl+N mở java.util.concurrent.Future, Ctrl+Q đọc
 *               {@code get}. So với {@code CompletableFuture} (Ctrl+F12): các method nối stage.
 *   Hoàn thành khi: viết xong khối ANSWER Q1.
 * <p>
 * Q2 [CODE] {@code thenApply()} và {@code thenCompose()} khác nhau thế nào?
 *   Bắt đầu   : cài {@code squareThenInc} bằng {@code thenApply} hai lần: bình phương, rồi cộng 1.
 *               Cài {@code flat} bằng {@code thenCompose} tới {@code CompletableFuture.completedFuture}.
 *               Ctrl+Q trên hai method để thấy hàm của thenCompose trả về một future.
 *   Kiểm chứng: chạy q02_squareThenInc và q02_flatDoesNotNest.
 *   Hoàn thành khi: hai test q02_* xanh; join của flat là giá trị, không phải future lồng nhau.
 * <p>
 * Q3 [CODE] {@code thenCombine()} dùng để làm gì?
 *   Bắt đầu   : cài {@code sum} bằng {@code thenCombine} để cộng hai future.
 *               Ctrl+Q trên {@code thenCombine}.
 *   Kiểm chứng: chạy q03_sumTwoAndFive.
 *   Hoàn thành khi: q03_sumTwoAndFive xanh.
 * <p>
 * Q5 [DỰ ĐOÁN + CODE] {@code join()} và {@code get()} khác nhau thế nào?
 *   Bắt đầu   : điền Q5_GET_CHECKED và Q5_JOIN_THROWS_COMPLETION (thay null).
 *               Tạo future, gọi {@code completeExceptionally}, rồi thử {@code get} và {@code join}
 *               (Debug test, Alt+F8). Ctrl+Q trên hai method để thấy method nào khai báo checked exception.
 *   Kiểm chứng: chạy q05_getChecked và q05_joinCompletion. Test tự gọi get và join trên future
 *               đã completeExceptionally, rồi so với hai hằng.
 *   Hoàn thành khi: hai test q05_* xanh và viết xong khối ANSWER Q5.
 */
public class Ex01_Compose {

    // Q5 — tên lớp checked mà get() ném khi future đã completeExceptionally.
    static final String Q5_GET_CHECKED = null;

    // Q5 — join() có ném CompletionException khi future đã completeExceptionally hay không.
    static final Boolean Q5_JOIN_THROWS_COMPLETION = null;

    /**
     * Bình phương giá trị của {@code in}, rồi cộng 1. Mỗi bước một {@code thenApply}.
     */
    static CompletableFuture<Integer> squareThenInc(CompletableFuture<Integer> in) {
        throw new UnsupportedOperationException("TODO Q2");
    }

    /**
     * Nối {@code in} bằng {@code thenCompose} tới {@code CompletableFuture.completedFuture}
     * để kết quả là giá trị, không phải future lồng nhau.
     */
    static CompletableFuture<Integer> flat(CompletableFuture<Integer> in) {
        throw new UnsupportedOperationException("TODO Q2");
    }

    /**
     * Cộng kết quả của {@code left} và {@code right} bằng {@code thenCombine}.
     */
    static CompletableFuture<Integer> sum(CompletableFuture<Integer> left, CompletableFuture<Integer> right) {
        throw new UnsupportedOperationException("TODO Q3");
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

/* ANSWER Q5:
 *
 */
