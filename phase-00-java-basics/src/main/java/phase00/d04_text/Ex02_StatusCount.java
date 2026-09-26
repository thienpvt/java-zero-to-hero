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
        throw new UnsupportedOperationException("TODO Q4");
    }

    /* ANSWER Q4:
     *
     */
}
