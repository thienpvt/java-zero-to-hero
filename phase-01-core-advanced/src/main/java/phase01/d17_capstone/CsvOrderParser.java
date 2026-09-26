package phase01.d17_capstone;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Bài tích hợp — CsvOrderParser (đọc file CSV thành Order + danh sách lỗi)
 *
 * Nguồn: 01-java-core-advanced.md, mục "Bài thực hành tích hợp", bước 2 và 3.
 * Cần làm trước: Order (B1) — biết cấu trúc và ràng buộc của một đơn hàng hợp lệ.
 * Cách làm: cài {@code parse(...)} rồi chạy CsvOrderParserTest bằng nút ▶ (Ctrl+Shift+F10);
 * làm từng test một, đọc thông báo lỗi để biết còn thiếu trường hợp nào.
 *
 * ─────────────────────────────────────────────────────────────────────
 * B2 [CODE] Parse bằng `Files.newBufferedReader()` và `try-with-resources`; ghi nhận dòng lỗi
 *     có số dòng mà không nuốt exception I/O.
 *   Bắt đầu   : mở CsvOrderParserTest, đọc test đọc file mẫu
 *               (orders-sample.csv) trước để biết hình dạng input/output mong đợi.
 *   Kiểm chứng: đặt breakpoint trên biến đếm số dòng bạn khai báo trong vòng đọc, Debug test
 *               đọc file mẫu, dùng Evaluate Expression (Alt+F8) để xem nội dung dòng ứng với
 *               số dòng đó — đối chiếu với các dòng lỗi mà test kỳ vọng.
 *   Code      : cài {@code parse(Path file)}: đọc UTF-8 trong try-with-resources; dòng 1
 *               phải khớp {@code HEADER} (sai → 1 lỗi ở dòng 1, dừng ngay); bỏ qua dòng
 *               trống; mỗi dòng dữ liệu tách bằng {@code split(",", -1)} phải ra đúng 5
 *               trường rồi parse riêng từng trường {@code Instant}/{@code BigDecimal}/
 *               {@code Order.Status} — mỗi trường có {@code try/catch} riêng để biết đúng
 *               trường nào sai và ghi một mô tả tiếng Việt nêu rõ trường + giá trị gốc (không
 *               lộ nguyên văn thông báo lỗi tiếng Anh của JDK, ví dụ không dùng trực tiếp
 *               {@code e.getMessage()} của {@code DateTimeParseException}); lỗi ở một dòng
 *               chỉ ghi nhận rồi đọc tiếp, không dừng cả file; {@code IOException} (kể cả
 *               {@code NoSuchFileException}) phải được ném ra nguyên vẹn, không bắt/nuốt
 *               trong try-with-resources.
 *   Hoàn thành khi: mọi test trong CsvOrderParserTest xanh; giải thích được vì sao dùng
 *               try-with-resources (đóng {@code BufferedReader} kể cả khi có exception).
 *
 * B3 [CODE] Chọn `Map`/`Set` phù hợp để phát hiện ID trùng; giải thích `equals`/`hashCode` của key.
 *   Bắt đầu   : trong {@code parse(...)}, bạn sẽ khai báo một tập hợp để nhớ các id đã chấp nhận
 *               trước khi quyết định một dòng sau có trùng id hay không.
 *   Kiểm chứng: chạy test có id trùng (o1 xuất hiện lại), đặt breakpoint ngay lệnh thêm id vào
 *               tập hợp bạn vừa khai báo, F7 Step Into để xem {@code hashCode}/{@code equals}
 *               của {@code String} quyết định trùng hay không.
 *   Code      : khai báo một {@code Set} các id đã chấp nhận (cùng thân {@code parse} với B2);
 *               dòng dữ liệu hợp lệ nhưng id đã có → ghi {@code LineError} mô tả trùng id,
 *               không thêm order thứ hai (giữ bản đầu tiên).
 *   Hoàn thành khi: giải thích được vì sao kiểu {@code Set} bạn chọn phù hợp; viết vào khối
 *               {@code ANSWER B3}.
 */
public final class CsvOrderParser {

    /** Dòng tiêu đề CSV hợp lệ duy nhất mà {@link #parse(Path)} chấp nhận. */
    public static final String HEADER = "id,customerId,createdAt,amount,status";

    /** Một lỗi khi parse: số dòng (1-based, tính cả header) và mô tả tiếng Việt. */
    public record LineError(int lineNumber, String message) {
    }

    /** Kết quả parse: danh sách order hợp lệ và danh sách lỗi, cả hai đều bất biến. */
    public record ParseResult(List<Order> orders, List<LineError> errors) {
        public ParseResult {
            orders = List.copyOf(orders);
            errors = List.copyOf(errors);
        }
    }

    /**
     * Đọc file CSV UTF-8 {@code file} và trả về các order hợp lệ cùng danh sách lỗi.
     *
     * <p>Quy tắc: dòng 1 phải đúng {@link #HEADER} (sai → 1 lỗi ở dòng 1, dừng luôn, không
     * order nào); dòng trống bị bỏ qua; mỗi dòng dữ liệu phải tách ra đúng 5 trường và parse
     * thành công {@code Instant}, {@code BigDecimal}, {@code Order.Status} (sai bất kỳ phần
     * nào → 1 {@link LineError} ở đúng số dòng đó, rồi đọc tiếp); id trùng với một order đã
     * chấp nhận trước đó → 1 {@link LineError}, giữ nguyên order đầu tiên.
     *
     * @throws IOException nếu không đọc được file (kể cả {@code NoSuchFileException} khi
     *                      file không tồn tại) — không bị bắt/nuốt bên trong method này
     */
    public ParseResult parse(Path file) throws IOException {
        throw new UnsupportedOperationException("TODO B2");
    }
}

/* ANSWER B3:
 *
 */
