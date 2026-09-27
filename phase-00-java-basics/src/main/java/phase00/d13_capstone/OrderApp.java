package phase00.d13_capstone;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Bài tổng hợp — in đơn và điểm vào dòng lệnh
 *
 * Nguồn: 00-java-basics-review.md, mục "Bài tổng hợp — Quản lý đơn hàng ở bộ nhớ", bước 4, 5, 6.
 * Cần làm trước: Order, OrderBook, OrderFile.
 * Cách làm: cài {@code render}, chạy OrderAppTest. Viết ANSWER B5 và ANSWER B6.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * B4 [CODE] Tách xử lý nhập liệu, quy tắc đơn và in kết quả.
 *   Bắt đầu   : cài {@code render}. {@code main} đã cho sẵn: đọc file bằng {@code OrderFile}, in bằng {@code render}.
 *   Kiểm chứng: Debug q test một đơn, Alt+F8 xem chuỗi đang ghép. Không dùng {@code instanceof}.
 *   Code      : mỗi đơn một dòng {@code id | customerCode | createdOn | total | status},
 *               {@code total} dùng {@code toPlainString()}, các dòng nối bằng {@code \n}.
 *               List rỗng → chuỗi rỗng. Không thêm dòng trống cuối.
 *   Hoàn thành khi: OrderAppTest xanh.
 * <p>
 * B5 [TỰ TRẢ LỜI] Test cho id trùng, đơn không tồn tại, trạng thái chuyển sai, số tiền âm, file rỗng
 *     và một đường thành công.
 *   Bắt đầu   : mở OrderTest, OrderBookTest, OrderFileTest. Viết ANSWER B5: mỗi ca chặn lỗi gì.
 *   Hoàn thành khi: ANSWER B5 kể đủ sáu ca và loại exception hoặc kết quả của từng ca.
 * <p>
 * B6 [TỰ TRẢ LỜI] Thêm một quyết định thiết kế: vì sao {@code Map}, {@code enum}, {@code BigDecimal},
 *     và exception được xử lý ở đâu.
 *   Bắt đầu   : viết ANSWER B6 sau khi test capstone xanh.
 *   Hoàn thành khi: ANSWER B6 trả lời được bốn ý đó bằng lời của bạn.
 *
 * &lt;&lt;&lt;TAG0&gt;&gt;&gt;Chạy từ dòng lệnh (sau khi test xanh). Console Windows cần UTF-8:
 * &lt;pre&gt;{@code
 * chcp 65001
 * .\mvnw.cmd -q -pl phase-00-java-basics compile
 * java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp phase-00-java-basics/target/classes phase00.d13_capstone.OrderApp phase-00-java-basics/src/test/resources/phase00/d13_capstone/orders-sample.txt
 * }&lt;/pre&gt;
 */
public final class OrderApp {

    private OrderApp() {
    }

    static String render(List<Order> orders) {
        throw new UnsupportedOperationException("TODO B4");
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Cách dùng: java ... phase00.d13_capstone.OrderApp <file>");
            System.exit(2);
            return;
        }
        try {
            System.out.println(render(OrderFile.read(Path.of(args[0]))));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    /* ANSWER B5:
     *
     */

    /* ANSWER B6:
     *
     */
}
