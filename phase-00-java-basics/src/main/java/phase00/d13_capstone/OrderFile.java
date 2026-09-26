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
 * B3 [CODE] Nhập file UTF-8, báo lỗi theo số dòng. Không cần parser CSV đầy đủ.
 *   Bắt đầu   : đọc hợp đồng định dạng ngay dưới đây, cài {@code read}.
 *   Kiểm chứng: Debug test dòng sai, xem {@code lineNo}. Mở {@code orders-sample.txt} trong resources.
 *   Code      : mỗi dòng không trống có đúng 5 field {@code id,customerCode,yyyy-MM-dd,amount,STATUS}.
 *               Không có dấu phẩy trong field, không có quote. Dòng trống bỏ qua nhưng vẫn tính vào số dòng.
 *               File không tồn tại → {@code NoSuchFileException}. Dòng sai, ngày/tiền/trạng thái không đọc được,
 *               hoặc id trùng trong file → {@code IllegalArgumentException} có chữ {@code dòng <n>}.
 *               Tiền tạo bằng {@code new BigDecimal(chuỗi)}.
 *   Hoàn thành khi: OrderFileTest xanh, kể cả file mẫu tiếng Việt và file rỗng.
 *
 * <p>Giới hạn cố ý: không hỗ trợ field có dấu phẩy, không hỗ trợ dấu ngoặc kép, một đơn một dòng.
 */
public final class OrderFile {

    private OrderFile() {
    }

    public static List<Order> read(Path path) throws IOException {
        // SOLUTION-BEGIN throw B3
        if (!Files.exists(path)) {
            throw new NoSuchFileException(path.toString());
        }
        List<Order> orders = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        int lineNo = 0;
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (line.isBlank()) {
                    continue;
                }
                orders.add(parseLine(line, lineNo, seen));
            }
        }
        return List.copyOf(orders);
        // SOLUTION-END
    }

    private static Order parseLine(String line, int lineNo, Set<String> seen) {
        // SOLUTION-BEGIN throw B3
        String[] parts = line.split(",", -1);
        if (parts.length != 5) {
            throw new IllegalArgumentException("dòng " + lineNo + ": cần đúng 5 field");
        }
        String id = parts[0].trim();
        String customerCode = parts[1].trim();
        if (id.isEmpty() || customerCode.isEmpty()) {
            throw new IllegalArgumentException("dòng " + lineNo + ": id hoặc mã khách trống");
        }
        if (!seen.add(id)) {
            throw new IllegalArgumentException("dòng " + lineNo + ": trùng id: " + id);
        }
        LocalDate createdOn;
        try {
            createdOn = LocalDate.parse(parts[2].trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("dòng " + lineNo + ": ngày không hợp lệ", ex);
        }
        BigDecimal total;
        try {
            total = new BigDecimal(parts[3].trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("dòng " + lineNo + ": tiền không hợp lệ", ex);
        }
        Status status;
        try {
            status = Status.valueOf(parts[4].trim());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("dòng " + lineNo + ": trạng thái không hợp lệ", ex);
        }
        try {
            return new Order(id, customerCode, createdOn, total, status);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("dòng " + lineNo + ": " + ex.getMessage(), ex);
        }
        // SOLUTION-END
    }
}
