package phase01.d08_immutability;

import java.util.List;
import java.util.Objects;

/**
 * Immutability — Bài 2: Defensive copy và record có sao chép dữ liệu lồng bên trong không
 *
 * Nguồn: 01-java-core-advanced.md, mục 8 (Immutability), câu 3, 5.
 * Cần làm trước: Ex01_FinalIsNotImmutable (biết `final` chỉ khoá biến, không khoá nội dung).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_DefensiveCopyTest bằng
 * nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q3 [CODE] Defensive copy là gì?
 *   Bắt đầu   : đọc class LeakyTeam bên dưới — constructor gán thẳng tham số vào field,
 *               members() trả thẳng field. Đây là lỗi rò rỉ tham chiếu (reference leak).
 *   Kiểm chứng: chạy các test q03_*; nếu đỏ vì TODO, cài đặt constructor của SafeTeam
 *               (Ctrl+N gõ "SafeTeam" để nhảy tới) dùng đúng hai bước ở phần Code.
 *   Code      : constructor của SafeTeam: gán {@code name} bằng
 *               {@code Objects.requireNonNull(name, ...)}; gán {@code members} bằng
 *               {@code List.copyOf(members)} (Ctrl+Click vào {@code List.copyOf} để đọc
 *               Javadoc — nó tự ném NullPointerException nếu list hoặc phần tử null, và
 *               trả về list không sửa được).
 *   Hoàn thành khi: các test q03_* xanh; giải thích được vì sao sửa list nguồn sau khi
 *               tạo SafeTeam không ảnh hưởng team.members(), nhưng với LeakyTeam thì có
 *               (thử debug so sánh: đặt breakpoint tại dòng members.add trong test, F7
 *               Step Into để thấy field members của LeakyTeam trỏ thẳng vào list nguồn).
 *
 * Q5 [DỰ ĐOÁN + CODE] Record có tự động đảm bảo deep immutability không?
 *   Bắt đầu   : đọc record Bag bên dưới (không sao chép {@code items}); điền hằng
 *               Q5_RECORD_IS_DEEPLY_IMMUTABLE (thay null).
 *   Kiểm chứng: chạy q05_prediction — test sửa list nguồn sau khi tạo Bag rồi so sánh
 *               bag.items(). Muốn thấy tận mắt: đặt breakpoint ngay trong constructor
 *               ngầm định của record Bag (Ctrl+F12 trên Bag để thấy constructor được
 *               sinh), Debug test, F7 Step Into vào Bag(List<String>).
 *   Code      : compact constructor của record Playlist: gán
 *               {@code name = Objects.requireNonNull(name, ...)}; gán
 *               {@code songs = List.copyOf(songs)} — giống hai bước ở Q3, nhưng viết
 *               trong compact constructor (không có tham số, không có dấu {@code ()}).
 *   Hoàn thành khi: các test q05_* xanh; giải thích được: record chỉ đảm bảo *field* là
 *               final (không gán lại được), hoàn toàn không tự sao chép object mutable
 *               lồng bên trong — muốn deep immutable phải tự defensive-copy trong compact
 *               constructor, đúng như Playlist vừa cài đặt.
 */
public class Ex02_DefensiveCopy {

    static final class LeakyTeam {
        private final List<String> members;

        LeakyTeam(List<String> members) {
            this.members = members;
        }

        List<String> members() {
            return members;
        }
    }

    static final class SafeTeam {
        private final String name;
        private final List<String> members;

        /**
         * @throws NullPointerException nếu {@code name} là null, {@code members} là null,
         *         hoặc {@code members} chứa phần tử null
         */
        SafeTeam(String name, List<String> members) {
            throw new UnsupportedOperationException("TODO Q3");
        }

        String name() {
            return name;
        }

        List<String> members() {
            return members;
        }
    }

    record Bag(List<String> items) {
    }

    // Q5 — kịch bản: List<String> src = new ArrayList<>(List.of("a")); Bag bag = new Bag(src);
    // src.add("b"); bag.items() có chứa "b" không?
    static final Boolean Q5_RECORD_IS_DEEPLY_IMMUTABLE = null;

    record Playlist(String name, List<String> songs) {
        /**
         * @throws NullPointerException nếu {@code name} là null, {@code songs} là null,
         *         hoặc {@code songs} chứa phần tử null
         */
        Playlist {
            throw new UnsupportedOperationException("TODO Q5");
        }
    }
}
