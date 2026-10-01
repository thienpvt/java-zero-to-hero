package phase03.d19_clean_code;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Clean Code — Bài 2: xử lý lỗi
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 19, câu 7, 8.
 * Cần làm trước: Ex01_NamingAndFunctions.
 * Cách làm: chạy test trong Ex02_ErrorHandlingTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q7 [CODE] Exception nên được translate ở boundary nào?
 *   Bắt đầu   : cài {@code quote}. Bắt {@code RateUnavailableException} của provider và ném
 *               {@code QuoteFailedException} giữ nguyên cause. Không dùng exception cho luồng thường.
 *   Kiểm chứng: chạy q07_translatesAtBoundary và q07_preservesCause.
 *   Hoàn thành khi: hai test xanh và ANSWER Q7 nêu dịch lỗi một lần ở lớp biên.
 * <p>
 * Q8 [DỰ ĐOÁN + CODE] Catch {@code Exception} rồi log và tiếp tục có rủi ro gì?
 *   Bắt đầu   : điền Q8_CATCH_AND_CONTINUE, rồi cài {@code readFirstLine} để IOException thành
 *               {@code UncheckedIOException} chứa cause, không trả chuỗi rỗng.
 *   Kiểm chứng: chạy q08_prediction và q08_missingFileThrowsWithCause.
 *   Hoàn thành khi: hai test xanh và ANSWER Q8 nêu lỗi bị che làm trạng thái sai lan tiếp.
 */
public class Ex02_ErrorHandling {

    // Q7 — nơi dịch lỗi.
    static final String Q7_TRANSLATE_BOUNDARY = null;

    // Q8 — catch Exception rồi tiếp tục có rủi ro gì.
    static final Boolean Q8_CATCH_AND_CONTINUE = null;

    /** Provider ngoài báo không lấy được tỉ giá. */
    public static class RateUnavailableException extends RuntimeException {

        public RateUnavailableException(String message) {
            super(message);
        }
    }

    /** Lỗi của lớp biên, ngôn ngữ của ứng dụng. */
    public static class QuoteFailedException extends RuntimeException {

        public QuoteFailedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /** Cổng lấy tỉ giá, có thể là hệ ngoài. */
    public interface RateProvider {

        long rate(String currency);
    }

    /** Dịch lỗi provider thành lỗi của ứng dụng, giữ nguyên nguyên nhân. */
    public static long quote(RateProvider provider, String currency) {
        throw new UnsupportedOperationException("TODO Q7");
    }

    /** Đọc dòng đầu của file; lỗi I/O thành lỗi unchecked có nguyên nhân. */
    public static String readFirstLine(Path path) {
        throw new UnsupportedOperationException("TODO Q8");
    }
}

/* ANSWER Q7:
 *
 */

/* ANSWER Q8:
 *
 */
