package phase00.d13_capstone;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import phase00.d13_capstone.Order.Status;

/**
 * Bài tổng hợp — đọc file đơn
 *
 * Nguồn: 00-java-basics-review.md, mục "Bài tổng hợp — Quản lý đơn hàng ở bộ nhớ", bước 3.
 * Cần làm trước: Order (B1).
 * Cách làm: cài {@code read}, chạy OrderFileTest.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * B3 [CODE] Nhập file UTF-8, báo lỗi theo số dòng. Không cần parser CSV đầy đủ.
 *   Bắt đầu   : đọc hợp đồng định dạng ngay dưới đây, cài {@code read}.
 *   Kiểm chứng: Debug test dòng sai, xem {@code lineNo}. Mở {@code orders-sample.txt} trong resources.
 *   Code      : mỗi dòng không trống có đúng 5 field {@code id,customerCode,yyyy-MM-dd,amount,STATUS}.
 *               Không có dấu phẩy trong field, không có quote. Dòng trống bỏ qua nhưng vẫn tính vào số dòng.
 *               File không tồn tại → {@code NoSuchFileException}. Dòng sai, ngày/tiền/trạng thái không đọc được,
 *               hoặc id trùng trong file → {@code IllegalArgumentException} có chữ {@code dòng &lt;n&gt;}.
 *               Tiền tạo bằng {@code new BigDecimal(chuỗi)}.
 *   Hoàn thành khi: OrderFileTest xanh, kể cả file mẫu tiếng Việt và file rỗng.
 *
 * &lt;&lt;&lt;TAG0&gt;&gt;&gt;Giới hạn cố ý: không hỗ trợ field có dấu phẩy, không hỗ trợ dấu ngoặc kép, một đơn một dòng.
 */
public final class OrderFile {

    private OrderFile() {
    }

    public static List<Order> read(Path path) throws IOException {
        throw new UnsupportedOperationException("TODO B3");
    }

    private static Order parseLine(String line, int lineNo, Set<String> seen) {
        throw new UnsupportedOperationException("TODO B3");
    }
}
