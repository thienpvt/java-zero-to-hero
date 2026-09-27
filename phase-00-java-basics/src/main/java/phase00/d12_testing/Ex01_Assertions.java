package phase00.d12_testing;

/**
 * Test và build — Bài 1: Assertion, ca biên và cách đọc test fail
 *
 * Nguồn: 00-java-basics-review.md, mục 12 (Test, debug và build cơ bản), câu 1, 2, 3.
 * Cần làm trước: đã chạy được {@code .\\mvnw.cmd -pl phase-00-java-basics test} ít nhất một lần.
 * Cách làm: viết ANSWER Q1, điền hằng Q2, cài {@code checkedDivide}, viết ANSWER Q3.
 * Chạy Ex01_AssertionsTest bằng nút ▶ (Ctrl+Shift+F10).
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q1 [TỰ TRẢ LỜI] Unit test khác việc chạy {@code main} và nhìn output ở đâu?
 *   Bắt đầu   : viết ANSWER Q1.
 *   Tra cứu   : mở một test bất kỳ ở giai đoạn này, thấy {@code assertEquals} / {@code assertThrows}.
 *               So với một {@code main} chỉ {@code System.out.println}.
 *   Hoàn thành khi: ANSWER Q1 nói test kiểm tra được bằng máy và chạy lại trong cùng một lệnh.
 * <p>
 * Q2 [DỰ ĐOÁN + CODE] Vì sao test cần kiểm tra kết quả cụ thể thay vì chỉ gọi method không ném lỗi?
 *   Bắt đầu   : điền Q2_CALL_WITHOUT_ASSERT_PROVES_RESULT, rồi cài {@code checkedDivide}.
 *   Kiểm chứng: tưởng tượng {@code checkedDivide} luôn {@code return 0}. Test nào chỉ gọi hàm sẽ vẫn xanh.
 *               Chạy q02_normalBoundaryAndError — ba ca: thường, biên, lỗi.
 *   Code      : divisor {@code 0} → {@code IllegalArgumentException}. Còn lại trả {@code dividend / divisor}.
 *   Hoàn thành khi: q02_* xanh; nói được một lần chạy không ném lỗi không chứng minh kết quả đúng.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Khi test fail, bạn kiểm tra expected/actual và stack trace theo thứ tự nào?
 *   Bắt đầu   : cố ý đổi {@code 5} trong đầu bạn thành một số khác, hình dung message fail, rồi viết ANSWER Q3.
 *               Không commit bản làm test đỏ.
 *   Kiểm chứng: message của {@code assertEquals} ghi expected và actual trước; stack trace phía dưới chỉ dòng test
 *               và dòng code. Đọc từ trên xuống.
 *   Hoàn thành khi: ANSWER Q3 nêu đúng thứ tự: message expected/actual, rồi dòng stack trong code của mình.
 */
public class Ex01_Assertions {

    // Q2 — test chỉ gọi method, không assert, có chứng minh kết quả đúng không?
    static final Boolean Q2_CALL_WITHOUT_ASSERT_PROVES_RESULT = null;

    static int checkedDivide(int dividend, int divisor) {
        throw new UnsupportedOperationException("TODO Q2");
    }

    /* ANSWER Q1:
     *
     */

    /* ANSWER Q3:
     *
     */
}
