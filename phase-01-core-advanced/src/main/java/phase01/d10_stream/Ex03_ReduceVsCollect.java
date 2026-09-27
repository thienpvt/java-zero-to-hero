package phase01.d10_stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Stream API — Bài 3: reduce() vs collect()
 *
 * Nguồn: 01-java-core-advanced.md, mục 10 (Stream API), câu 5, 6, 7.
 * Cần làm trước: Ex02_LazinessAndPipeline (đã biết pipeline chạy dọc theo từng phần tử).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex03_ReduceVsCollectTest bằng
 * nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q5 [CODE] {@code reduce()} dùng để làm gì?
 *   Bắt đầu   : cài đặt sumWithReduce bằng Stream.reduce(identity, accumulator) và
 *               maxWithReduce bằng Stream.reduce(accumulator) (KHÔNG identity).
 *   Kiểm chứng: chạy q05_*; Ctrl+N → Stream → Ctrl+F12 → tìm 2 overload
 *               reduce(T, BinaryOperator&lt;T&gt;) và reduce(BinaryOperator&lt;T&gt;), Ctrl+Q
 *               đọc Javadoc mỗi overload để thấy kiểu trả về khác nhau (T so với
 *               Optional&lt;T&gt;).
 *   Code      : static int sumWithReduce(List&lt;Integer&gt; numbers); static
 *               Optional&lt;Integer&gt; maxWithReduce(List&lt;Integer&gt; numbers).
 *   Hoàn thành khi: test q05_* xanh; giải thích được vì sao bản có identity luôn trả về
 *               một T hợp lệ (kể cả stream rỗng, trả identity) còn bản không identity phải
 *               trả Optional&lt;T&gt; vì không có giá trị "trung lập" hợp lý cho mọi phép
 *               toán (ví dụ max của tập rỗng là gì?).
 * <p>
 * Q6 [DỰ ĐOÁN + CODE] {@code collect()} và {@code reduce()} khác nhau thế nào?
 *   Bắt đầu   : trong q06_prediction, thử add() vào kết quả của Stream.of(1).toList() và
 *               vào kết quả của Stream.of(1).collect(Collectors.toList()); điền 2 hằng số
 *               Q6_*.
 *   Kiểm chứng: Ctrl+Q trên Stream.toList() và trên Collectors.toList(), đọc Javadoc xem
 *               list trả về có được phép sửa không và hợp đồng có cam kết mutability không
 *               — sau khi đã điền dự đoán.
 *   Code      : static Map&lt;Character, Long&gt; countByFirstLetter(List&lt;String&gt;
 *               words) dùng Collectors.groupingBy + Collectors.counting; static String
 *               joinWithCollect(List&lt;String&gt; parts, String separator) dùng
 *               Collectors.joining.
 *   Hoàn thành khi: test q06_* xanh; giải thích được collect() nhận một Collector biết
 *               cách gộp kết quả từ nhiều phần (combiner) nên hợp cho kết quả có cấu trúc
 *               phức tạp (Map, String nối), còn reduce() hợp cho phép toán kết hợp đơn giản
 *               trả về một giá trị cùng kiểu với phần tử stream.
 * <p>
 * Q7 [DỰ ĐOÁN + CODE + THÍ NGHIỆM] Side effect trong Stream có vấn đề gì?
 *   Bắt đầu   : đọc squaresBuggy (cho sẵn) bên dưới — nó add() vào một ArrayList thường
 *               (không đồng bộ) từ nhiều thread của parallelStream().forEach(); điền
 *               Q7_BUGGY_VERSION_ALWAYS_CORRECT sau khi đã chạy thí nghiệm (thay null).
 *   Kiểm chứng: chạy main() vài lần, đọc báo cáo của runExperiment, sau khi đã điền dự
 *               đoán, tự xem squaresBuggy trên danh sách lớn có luôn đúng size không.
 *   Code      : static List&lt;Integer&gt; squares(List&lt;Integer&gt; numbers) — dùng
 *               map() (không side effect) để giữ đúng thứ tự và luôn ra đúng kết quả dù có
 *               dùng parallelStream().
 *   Hoàn thành khi: test q07_* xanh; giải thích được add() vào một collection chia sẻ
 *               trong forEach() của parallel stream là side effect có race condition, còn
 *               map() không side effect (chỉ tính giá trị mới từ đầu vào, không đọc/ghi gì
 *               chung) nên luôn an toàn để chạy song song.
 */
public class Ex03_ReduceVsCollect {

    /**
     * Tổng các số trong {@code numbers} bằng {@code reduce()} có identity; stream rỗng trả
     * về identity ({@code 0}).
     *
     * @throws NullPointerException nếu {@code numbers} là {@code null}
     */
    static int sumWithReduce(List<Integer> numbers) {
        throw new UnsupportedOperationException("TODO Q5");
    }

    /**
     * Giá trị lớn nhất trong {@code numbers} bằng {@code reduce()} KHÔNG identity; stream
     * rỗng trả về {@link Optional#empty()}.
     *
     * @throws NullPointerException nếu {@code numbers} là {@code null}
     */
    static Optional<Integer> maxWithReduce(List<Integer> numbers) {
        throw new UnsupportedOperationException("TODO Q5");
    }

    // Q6 — kịch bản: thử add() vào Stream.of(1).toList().
    static final Boolean Q6_STREAM_TOLIST_IS_MODIFIABLE = null;
    // Q6 — kịch bản: thử add() vào Stream.of(1).collect(Collectors.toList()).
    static final Boolean Q6_COLLECTORS_TOLIST_ADD_WORKS_ON_THIS_JDK = null;

    /**
     * Đếm số từ (bỏ qua chuỗi rỗng) theo chữ cái đầu, so khóa không phân biệt hoa/thường
     * bằng {@link Character#toLowerCase(char)}.
     *
     * @throws NullPointerException nếu {@code words} là {@code null}
     */
    static Map<Character, Long> countByFirstLetter(List<String> words) {
        throw new UnsupportedOperationException("TODO Q6");
    }

    /**
     * Nối các phần tử trong {@code parts} bằng {@code separator}.
     *
     * @throws NullPointerException nếu {@code parts} hoặc {@code separator} là {@code null}
     */
    static String joinWithCollect(List<String> parts, String separator) {
        throw new UnsupportedOperationException("TODO Q6");
    }

    // Q7 — kịch bản: squaresBuggy ghi vào một ArrayList chia sẻ từ parallelStream().forEach.
    // Hằng hỏi: mọi lần chạy có luôn ra đúng kết quả không? Chạy main() rồi mới điền.
    static final Boolean Q7_BUGGY_VERSION_ALWAYS_CORRECT = null;

    /**
     * Cho sẵn: cách làm SAI — tính bình phương từng số bằng {@code parallelStream().forEach}
     * ghi kết quả (side effect) vào một {@code ArrayList} thường (không đồng bộ). Nhiều
     * thread cùng {@code add()} có thể làm mất phần tử, sai thứ tự, hoặc ném exception.
     */
    static List<Integer> squaresBuggy(List<Integer> numbers) {
        List<Integer> result = new ArrayList<>();
        numbers.parallelStream().forEach(n -> result.add(n * n));
        return result;
    }

    /**
     * Tính bình phương từng số trong {@code numbers}, không side effect (chỉ dùng
     * {@code map()}), giữ đúng thứ tự gặp trong {@code numbers} dù có dùng
     * {@code parallelStream()}.
     *
     * @throws NullPointerException nếu {@code numbers} là {@code null}
     */
    static List<Integer> squares(List<Integer> numbers) {
        throw new UnsupportedOperationException("TODO Q7");
    }

    /**
     * Cho sẵn: chạy {@link #squaresBuggy(List)} trên {@code 1..n} lặp 20 lần, đếm số lần
     * kết quả SAI (size khác n, thứ tự khác thứ tự tăng dần, hoặc ném exception); trả về
     * báo cáo dạng văn bản. Đo thô, chỉ để quan sát — Giai đoạn 2 học đo đúng cách bằng JMH.
     */
    static String runExperiment(int n) {
        List<Integer> numbers = IntStream.rangeClosed(1, n).boxed().toList();
        List<Integer> expected = numbers.stream().map(x -> x * x).toList();
        int wrongRuns = 0;
        for (int i = 0; i < 20; i++) {
            try {
                List<Integer> actual = squaresBuggy(numbers);
                if (!actual.equals(expected)) {
                    wrongRuns++;
                }
            } catch (RuntimeException e) {
                wrongRuns++;
            }
        }
        return "n=" + n + ": squaresBuggy sai " + wrongRuns + "/20 lần chạy (size hoặc thứ tự "
                + "khác kỳ vọng, hoặc ném exception do race trên ArrayList không đồng bộ).";
    }

    public static void main(String[] args) {
        System.out.println(runExperiment(100_000));
    }
}

/* OBSERVATION Q7:
 *
 */
