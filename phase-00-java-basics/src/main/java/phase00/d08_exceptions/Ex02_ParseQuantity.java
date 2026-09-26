package phase00.d08_exceptions;

/**
 * Exception — Bài 2: Parse số lượng
 *
 * Nguồn: 00-java-basics-review.md, mục 8 (Exception và quản lý tài nguyên), câu 1.
 * Cần làm trước: Ex01_ThrowFinally.
 * Cách làm: cài {@code parseQuantity}, chạy Ex02_ParseQuantityTest.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q1 [TỰ TRẢ LỜI + CODE] {@code NumberFormatException} có thể xảy ra khi nào? Nên bắt ở đâu trong bài nhập liệu?
 *   Bắt đầu   : cài {@code parseQuantity}, rồi viết ANSWER Q1.
 *   Kiểm chứng: Debug q01_parsesTrimmedNumber với {@code " 4 "} và q01_rejectsBlankAndInvalid với {@code "12a"}.
 *               F7 vào {@code Integer.parseInt}. Ctrl+Q trên method đó, đọc {@code @throws}.
 *   Code      : {@code null} hoặc chuỗi trống/toàn khoảng trắng → {@code IllegalArgumentException}
 *               message {@code "số lượng trống"}, không có cause. Chuỗi không phải số nguyên →
 *               {@code IllegalArgumentException} có cause {@code NumberFormatException}. Số âm →
 *               {@code IllegalArgumentException} không có cause. {@code 0} và số dương được trả về.
 *               Có thể {@code trim} trước khi parse.
 *   Hoàn thành khi: q01_* xanh; ANSWER Q1 nói chỗ bắt exception là biên nhập liệu, không nuốt rồi trả null.
 */
public class Ex02_ParseQuantity {

    static int parseQuantity(String raw) {
        throw new UnsupportedOperationException("TODO Q1");
    }

    /* ANSWER Q1:
     *
     */
}
