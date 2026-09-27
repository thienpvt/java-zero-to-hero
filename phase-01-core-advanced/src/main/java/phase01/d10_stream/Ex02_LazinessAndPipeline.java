package phase01.d10_stream;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Stream API — Bài 2: Lazy evaluation và thứ tự chạy pipeline
 *
 * Nguồn: 01-java-core-advanced.md, mục 10 (Stream API), câu 2, 4, 3, 11.
 * Cần làm trước: Ex01_MapVsFlatMap (đã quen map()/flatMap() cơ bản).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex02_LazinessAndPipelineTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q2 [DỰ ĐOÁN] Vì sao Stream intermediate operation là lazy?
 *   Bắt đầu   : trong q02_prediction, tạo một List&lt;String&gt; log rồi xây pipeline
 *               filter(...).map(...) có ghi log mỗi lần lambda chạy, nhưng KHÔNG gọi bất
 *               kỳ terminal operation nào (không toList(), forEach(), count()...); điền
 *               hằng số Q2_LOG_SIZE_WITHOUT_TERMINAL.
 *   Kiểm chứng: đặt breakpoint trong lambda của filter và map, Debug test, sau khi đã điền
 *               dự đoán, tự xem breakpoint có bị chạm không và log có bao nhiêu dòng.
 *   Hoàn thành khi: test q02_* xanh; giải thích được vì sao chỉ gọi filter()/map() không
 *               làm gì cả — mọi intermediate operation chỉ mô tả "công thức", chờ terminal
 *               operation gọi Iterator/Spliterator của Stream để thực sự chạy.
 * <p>
 * Q4 [DỰ ĐOÁN] {@code filter()} chạy lúc nào?
 *   Bắt đầu   : trong q04_prediction, ghép
 *               Stream.of("a", "bb", "ccc").filter(...).map(...).findFirst(), mỗi bước ghi
 *               "filter " + s / "map " + s vào log; điền hằng số Q4_LOG_WITH_FIND_FIRST
 *               (log nối các phần bằng ", ").
 *   Kiểm chứng: đặt breakpoint trong lambda của filter, Debug test, F8 (Step Over) nhiều
 *               lần, sau khi đã điền dự đoán, tự ghi lại thứ tự các dòng log cho tới khi
 *               findFirst() dừng.
 *   Hoàn thành khi: test q04_* xanh; giải thích được Stream xử lý DỌC theo từng phần tử
 *               một (filter rồi map rồi kiểm tra điều kiện dừng của findFirst) cho tới khi
 *               tìm được kết quả, không phải chạy filter cho tất cả phần tử trước rồi mới
 *               map.
 * <p>
 * Q3 [DỰ ĐOÁN] Stream có reusable không?
 *   Bắt đầu   : trong q03_prediction, tạo một biến Stream&lt;Integer&gt;, gọi count() lần
 *               đầu, rồi gọi count() lần hai trên CHÍNH biến đó; bắt exception ném ra, điền
 *               Q3_REUSE_EXCEPTION bằng simple name của exception.
 *   Kiểm chứng: Ctrl+N → AbstractPipeline, hoặc Ctrl+B từ dòng gọi count() lần hai, đọc
 *               simple name và message của exception — sau khi đã điền dự đoán.
 *   Hoàn thành khi: test q03_* xanh; giải thích được một Stream chỉ được "tiêu thụ"
 *               (traverse) đúng một lần bởi một terminal operation; muốn dùng lại logic
 *               phải tạo Stream mới từ source (ví dụ gọi lại list.stream()).
 * <p>
 * Q11 [DỰ ĐOÁN + CODE] Thứ tự operation có thể ảnh hưởng performance thế nào?
 *   Bắt đầu   : đọc mapCallsWhenFilterFirst/mapCallsWhenMapFirst bên dưới; cài đặt thân 2
 *               method bằng cách đặt filter(n % 5 == 0) trước hoặc sau map (map ở đây chỉ
 *               tăng một AtomicInteger để đếm số lần bị gọi, mô phỏng một phép tính tốn
 *               kém).
 *   Kiểm chứng: chạy q11_*, đặt breakpoint trong lambda map ở mỗi method, Debug, sau khi
 *               đã điền dự đoán, tự đếm số lần breakpoint bị chạm ở từng thứ tự filter/map.
 *   Code      : static int mapCallsWhenFilterFirst(List&lt;Integer&gt; numbers) — filter
 *               n % 5 == 0 rồi map (đếm); static int mapCallsWhenMapFirst(List&lt;Integer&gt;
 *               numbers) — map (đếm) rồi filter n % 5 == 0.
 *   Hoàn thành khi: test q11_* xanh; giải thích được vì sao đặt filter (giảm số phần tử
 *               cần xử lý) trước một bước tính toán tốn kém thường tiết kiệm hơn so với
 *               tính toán trên mọi phần tử rồi mới lọc.
 * <p>
 * Ví dụ [CODE] users.stream().filter(User::isActive).map(User::getEmail).distinct().toList()
 *   Bắt đầu   : đọc record User bên dưới, cài đặt activeUniqueEmails() đúng 4 bước pipeline
 *               nêu trong tài liệu: filter theo isActive(), map sang getEmail(), distinct(),
 *               toList().
 *   Kiểm chứng: chạy vd_*, đặt breakpoint trong distinct() (Ctrl+B) — nó phải tự giữ một
 *               tập hợp nội bộ các email đã thấy (stateful) để biết phần tử hiện tại có
 *               trùng hay không, khác với filter/map là stateless (không cần nhớ gì giữa
 *               các phần tử).
 *   Hoàn thành khi: test vd_* xanh; viết xong khối ANSWER V1 giải thích từng bước và thời
 *               điểm chạy của pipeline này.
 */
public class Ex02_LazinessAndPipeline {

    // Q2 — kịch bản: pipeline filter + map có ghi log, không gọi terminal operation nào.
    static final Integer Q2_LOG_SIZE_WITHOUT_TERMINAL = null;

    // Q4 — kịch bản: Stream.of("a", "bb", "ccc")
    //   .filter(s -> { log.add("filter " + s); return s.length() > 1; })
    //   .map(s -> { log.add("map " + s); return s; })
    //   .findFirst();  — log nối bằng ", ".
    static final String Q4_LOG_WITH_FIND_FIRST = null;

    // Q3 — kịch bản: gọi count() hai lần trên cùng một Stream.
    static final String Q3_REUSE_EXCEPTION = null;

    // Q11 — kịch bản: 10 số 1..10; so số lần lambda map được gọi khi filter (n % 5 == 0)
    // đứng trước map so với khi map đứng trước filter.
    static final Integer Q11_MAP_CALLS_WHEN_FILTER_FIRST = null;
    static final Integer Q11_MAP_CALLS_WHEN_MAP_FIRST = null;

    /**
     * Lọc {@code numbers} còn số chia hết cho 5 TRƯỚC, rồi mới đi qua bước "tính toán" (chỉ
     * đếm số lần gọi ở đây); trả về số lần lambda map được gọi.
     *
     * @throws NullPointerException nếu {@code numbers} là {@code null}
     */
    static int mapCallsWhenFilterFirst(List<Integer> numbers) {
        throw new UnsupportedOperationException("TODO Q11");
    }

    /**
     * Đi qua bước "tính toán" (chỉ đếm số lần gọi ở đây) trước, rồi mới lọc còn số chia hết
     * cho 5; trả về số lần lambda map được gọi.
     *
     * @throws NullPointerException nếu {@code numbers} là {@code null}
     */
    static int mapCallsWhenMapFirst(List<Integer> numbers) {
        throw new UnsupportedOperationException("TODO Q11");
    }

    /** Người dùng, dùng cho ví dụ pipeline lọc + map + distinct trong tài liệu. */
    record User(String email, boolean active) {

        boolean isActive() {
            return active;
        }

        String getEmail() {
            return email;
        }
    }

    /**
     * Trả về email của các User active, loại trùng, giữ đúng thứ tự gặp lần đầu — theo đúng
     * pipeline trong tài liệu: filter theo isActive(), map sang email, distinct(), toList().
     *
     * @throws NullPointerException nếu {@code users} là {@code null}
     */
    static List<String> activeUniqueEmails(List<User> users) {
        throw new UnsupportedOperationException("TODO V1");
    }
}

/* ANSWER V1 (giải thích pipeline của ví dụ users.stream()):
 *
 */
