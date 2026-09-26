package phase01.d16_exceptions_resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Exception handling và quản lý tài nguyên — Bài 2: đọc file an toàn và chuyển lỗi tầng ứng dụng
 *
 * Nguồn: 01-java-core-advanced.md, mục 16 (Exception handling và quản lý tài nguyên), câu 3–5.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_FileIoTest bằng nút ▶ cạnh tên
 * test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q3 [DỰ ĐOÁN + CODE] Vì sao `Files.readString()` không phù hợp với file nhiều GB?
 *   Bắt đầu   : gõ `Files.readString(` ở đâu đó, đặt con trỏ lên tên method rồi Ctrl+Q để xem Javadoc,
 *               tự kết luận method này đọc file thế nào; điền Q3_READSTRING_LOADS_WHOLE_FILE (thay null)
 *               sau khi đã đọc.
 *   Kiểm chứng: chạy q03_readStringLoadsWholeFilePrediction (đây là hằng số cố định lấy từ Javadoc
 *               JDK vì tạo file thật nhiều GB trong test là không thực tế).
 *   Code      : cài đặt countNonBlankLines(Path) — đọc file theo kiểu streaming (Files.lines(Path,
 *               Charset) trong try-with-resources, hoặc Files.newBufferedReader) thay vì đọc hết vào
 *               một String, đếm số dòng còn nội dung sau khi trim.
 *   Hoàn thành khi: mọi test q03_* xanh; giải thích được sự khác nhau về bộ nhớ giữa readString (tải
 *               toàn bộ file thành 1 String) và Files.lines (chỉ giữ một vùng đệm nhỏ, xử lý từng dòng).
 *
 * Q4 [CODE] Khi nào nên chuyển một `IOException` thành lỗi ở tầng ứng dụng?
 *   Bắt đầu   : đọc class ConfigException và method swallowingLoad (bản xấu, không sửa) bên dưới;
 *               cài đặt readConfigValue(Path, String) dùng Files.readAllLines(Path, Charset).
 *   Kiểm chứng: đặt breakpoint ở đầu method, Debug q04_readConfigValueMissingFileThrowsWithCause,
 *               dùng F7 (Step Into) đi vào Files.readAllLines để thấy NoSuchFileException được ném
 *               từ tầng I/O, sau đó xem nó được bọc lại thành ConfigException ở catch bên ngoài.
 *   Code      : parse từng dòng dạng "key=value" (trim hai phía key/value, bỏ dòng trống và dòng bắt
 *               đầu bằng "#"); nếu đọc file lỗi → ném ConfigException("Không đọc được cấu hình: " +
 *               file, cause); nếu không thấy key → ném ConfigException("Thiếu khóa: " + key) (không cause).
 *   Hoàn thành khi: mọi test q04_* xanh; giải thích được vì sao phải giữ nguyên cause khi bọc exception
 *               (Ctrl+Click vào constructor RuntimeException(String, Throwable) để xem hợp đồng).
 *
 * Q5 [TỰ TRẢ LỜI] Vì sao `catch (Exception e) {}` khiến việc vận hành khó khăn?
 *   Bắt đầu   : đọc swallowingLoad(Path) bên dưới — bản "xấu" nuốt mọi Exception rồi trả null.
 *   Tra cứu   : so sánh với readConfigValue ở Q4 (giữ cause, phân loại rõ từng loại lỗi); nghĩ xem khi
 *               swallowingLoad trả null, người vận hành hệ thống biết được gì về nguyên nhân thật không.
 *   Hoàn thành khi: viết xong khối ANSWER Q5, nêu được ít nhất một hậu quả vận hành cụ thể (ví dụ:
 *               mất log/stack trace, không phân biệt được lỗi I/O với "file rỗng", che luôn cả lỗi
 *               logic ngoài ý muốn) và cách làm tốt hơn.
 */
public class Ex02_FileIo {

    /** Lỗi ở tầng ứng dụng khi đọc/parse file cấu hình, thay cho việc để lộ IOException thô. */
    static final class ConfigException extends RuntimeException {
        ConfigException(String message) {
            super(message);
        }

        ConfigException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Bản "xấu" minh hoạ lỗi hay gặp: đọc file rồi nuốt mọi exception, luôn trả {@code null} mà
     * không log, không phân biệt "file rỗng" với "đọc file bị lỗi". Dùng để so sánh khi trả lời Q5 —
     * không sửa hàm này.
     *
     * @param file đường dẫn file cần đọc
     * @return nội dung file, hoặc {@code null} nếu có bất kỳ lỗi nào xảy ra trong lúc đọc
     */
    static String swallowingLoad(Path file) {
        try {
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    // Q3 — kịch bản: Files.readString có nạp toàn bộ nội dung file vào bộ nhớ không?
    // Đọc Javadoc JDK (Ctrl+Q) rồi mới điền.
    static final Boolean Q3_READSTRING_LOADS_WHOLE_FILE = null;

    /**
     * Đếm số dòng còn nội dung sau khi trim trong {@code file}, đọc theo kiểu streaming (không tải
     * toàn bộ file vào bộ nhớ như {@code Files.readString}).
     *
     * @param file đường dẫn tới file văn bản UTF-8
     * @return số dòng có ít nhất một ký tự không phải khoảng trắng sau khi trim
     * @throws IOException nếu không đọc được file (không tồn tại, không có quyền, lỗi I/O khác...)
     */
    static long countNonBlankLines(Path file) throws IOException {
        throw new UnsupportedOperationException("TODO Q3");
    }

    /**
     * Đọc giá trị của {@code key} từ file cấu hình dạng {@code key=value} (một cặp mỗi dòng; dòng
     * trống và dòng bắt đầu bằng {@code #} bị bỏ qua; khoảng trắng hai đầu key/value bị trim).
     *
     * @param file đường dẫn file cấu hình UTF-8
     * @param key  khóa cần tìm
     * @return giá trị tương ứng với {@code key}
     * @throws ConfigException nếu không đọc được file (bọc {@link IOException} gốc làm cause), hoặc
     *         nếu file không chứa {@code key} (không có cause)
     */
    static String readConfigValue(Path file, String key) {
        throw new UnsupportedOperationException("TODO Q4");
    }
}

/* ANSWER Q5:
 *
 */
