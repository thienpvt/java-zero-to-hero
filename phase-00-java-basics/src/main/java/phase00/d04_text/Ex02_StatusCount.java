package phase00.d04_text;

import java.util.EnumMap;
import java.util.Map;

/**
 * Array, String, enum — Bài 2: Đếm trạng thái
 *
 * Nguồn: 00-java-basics-review.md, mục 4 (Array, String và enum), câu 4.
 * Cần làm trước: Ex01_ArrayAndString.
 * Cách làm: cài {@code count}, chạy Ex02_StatusCountTest (Ctrl+Shift+F10).
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q4 [TỰ TRẢ LỜI + CODE] Khi nào {@code enum} tốt hơn những chuỗi trạng thái rải rác trong code?
 *   Bắt đầu   : đọc enum {@code Status}, cài {@code count}, rồi viết ANSWER Q4.
 *   Kiểm chứng: Debug q04_countsKnownStatuses, F7 vào {@code Status.valueOf}. Thử truyền {@code "HOLD"}
 *               và đọc message. Ctrl+Q trên {@code Enum.valueOf}.
 *   Code      : {@code count} tách chuỗi bằng dấu phẩy. {@code null} → {@code IllegalArgumentException}.
 *               Chuỗi trống/toàn khoảng trắng → map rỗng. Token trim xong mà rỗng hoặc không phải
 *               {@code NEW}/{@code DONE} → {@code IllegalArgumentException} (token lạ: message chứa token).
 *               {@code "NEW,DONE,NEW"} đếm NEW = 2, DONE = 1.
 *   Hoàn thành khi: q04_* xanh; ANSWER Q4 nêu ít nhất một lỗi mà chuỗi tự do chỉ phát hiện lúc chạy.
 */
public class Ex02_StatusCount {

    public enum Status {
        NEW,
        DONE
    }

    /**
     * @throws IllegalArgumentException nếu dòng null, có token rỗng, hoặc có trạng thái lạ
     */
    static Map<Status, Integer> count(String csv) {
        // SOLUTION-BEGIN throw Q4
        if (csv == null) {
            throw new IllegalArgumentException("dòng null");
        }
        if (csv.isBlank()) {
            return Map.of();
        }
        Map<Status, Integer> counts = new EnumMap<>(Status.class);
        for (String part : csv.split(",", -1)) {
            String token = part.trim();
            if (token.isEmpty()) {
                throw new IllegalArgumentException("trạng thái trống");
            }
            Status status;
            try {
                status = Status.valueOf(token);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("trạng thái lạ: " + token, ex);
            }
            counts.merge(status, 1, Integer::sum);
        }
        return Map.copyOf(counts);
        // SOLUTION-END
    }

    /* ANSWER Q4:
     * SOLUTION-BEGIN
     * enum khóa tập trạng thái thành các hằng compiler biết. Gõ sai tên là lỗi đỏ, đổi tên thì IDE
     * tìm hết chỗ dùng. Chuỗi "NEW"/"DONE" rải trong code chỉ vỡ khi chạy (gõ "New", thêm trạng thái
     * mà quên một nhánh). Enum còn đi được với switch và không cần quy ước chữ hoa bằng lời.
     * SOLUTION-END
     */
}
