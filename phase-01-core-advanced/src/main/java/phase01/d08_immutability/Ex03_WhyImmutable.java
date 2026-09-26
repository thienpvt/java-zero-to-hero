package phase01.d08_immutability;

/**
 * Immutability — Bài 3: Vì sao immutable dễ thread-safe, và khi nào nó có nhược điểm
 *
 * Nguồn: 01-java-core-advanced.md, mục 8 (Immutability), câu 4, 6.
 * Cần làm trước: Ex02_DefensiveCopy (biết cách một object bất biến thật được xây dựng).
 * Cách làm: đọc record Point và các helper cho sẵn bên dưới; chạy q06_experimentRuns
 * bằng nút ▶ (Ctrl+Shift+F10) để xem báo cáo in ra ở tab "Test Results"; sau đó chạy
 * main() (nút ▶ cạnh {@code public static void main}) để xem số đo với n lớn hơn ở
 * Console. Viết câu trả lời vào khối ANSWER/OBSERVATION ở cuối file.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q4 [TỰ TRẢ LỜI] Vì sao immutable object dễ thread-safe?
 *   Bắt đầu   : đọc lại SafeTeam và Playlist ở Ex02_DefensiveCopy — chú ý là sau khi
 *               constructor chạy xong, không có method nào cho phép đổi state nữa.
 *   Kiểm chứng: Tra cứu — không có test riêng; tự hỏi "hai thread cùng đọc chung một
 *               SafeTeam thì có cần đồng bộ hoá (synchronized/lock) không? Vì sao?".
 *   Hoàn thành khi: viết xong khối ANSWER Q4 ở cuối file, có nêu rõ lý do liên quan đến
 *               việc không tồn tại "state thay đổi" để hai thread nhìn thấy sai lệch nhau.
 *
 * Q6 [THÍ NGHIỆM + TỰ TRẢ LỜI] Khi nào immutable object có nhược điểm?
 *   Bắt đầu   : đọc runExperiment(int n) bên dưới — so sánh nối chuỗi bằng {@code +=}
 *               (mỗi lần tạo String mới, immutable) với {@code StringBuilder} (mutable,
 *               sửa tại chỗ); và so sánh tạo n Point qua withX() liên tiếp (mỗi lần tạo
 *               object mới) với sửa tại chỗ một MutablePoint.
 *   Kiểm chứng: chạy q06_experimentRuns (n nhỏ, chỉ kiểm tra có báo cáo, không so tốc độ
 *               vì máy có thể chập chờn); sau đó tự chạy main() với n lớn (200 000) để
 *               thấy số nanoTime chênh lệch rõ giữa hai cách. Đặt breakpoint trong
 *               concatWithPlus và concatWithBuilder, Debug, F8 (Step Over) từng vòng lặp
 *               để thấy {@code +=} tạo String mới mỗi lần (Ctrl+B vào String.concat nếu
 *               muốn xem sâu hơn), còn StringBuilder.append sửa buffer nội bộ tại chỗ.
 *   Hoàn thành khi: q06_experimentRuns xanh; viết xong khối OBSERVATION Q6 với số đo thật
 *               từ main() của bạn, và khối ANSWER Q6 nêu được ít nhất một tình huống nên
 *               chọn mutable (vòng lặp nóng, nhiều lần sửa) thay vì tạo lại object mới.
 */
public class Ex03_WhyImmutable {

    record Point(int x, int y) {
        Point withX(int x) {
            return new Point(x, y);
        }
    }

    static final class MutablePoint {
        int x;
        int y;

        MutablePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void setX(int x) {
            this.x = x;
        }
    }

    private static String concatWithPlus(int n) {
        String s = "";
        for (int i = 0; i < n; i++) {
            s += "x";
        }
        return s;
    }

    private static String concatWithBuilder(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append("x");
        }
        return sb.toString();
    }

    private static Point createPointsImmutable(int n) {
        Point p = new Point(0, 0);
        for (int i = 0; i < n; i++) {
            p = p.withX(i);
        }
        return p;
    }

    private static MutablePoint createPointsMutable(int n) {
        MutablePoint p = new MutablePoint(0, 0);
        for (int i = 0; i < n; i++) {
            p.setX(i);
        }
        return p;
    }

    /**
     * Đo thô (không thay cho JMH ở Giai đoạn 2) chi phí giữa cách làm immutable (tạo object
     * mới mỗi lần) và mutable (sửa tại chỗ), cho hai kịch bản: nối chuỗi và cập nhật toạ độ.
     *
     * @return báo cáo văn bản nhiều dòng, không rỗng
     */
    static String runExperiment(int n) {
        // Vòng lặp warm-up để JIT ổn định trước khi đo — đo thô bằng nanoTime, không chính
        // xác như benchmark thật; Giai đoạn 2 sẽ học đo đúng cách bằng JMH.
        int warmup = Math.min(n, 200);
        concatWithPlus(warmup);
        concatWithBuilder(warmup);
        createPointsImmutable(warmup);
        createPointsMutable(warmup);

        long t1 = System.nanoTime();
        String plusResult = concatWithPlus(n);
        long t2 = System.nanoTime();
        String builderResult = concatWithBuilder(n);
        long t3 = System.nanoTime();
        Point lastImmutable = createPointsImmutable(n);
        long t4 = System.nanoTime();
        MutablePoint lastMutable = createPointsMutable(n);
        long t5 = System.nanoTime();

        return "n=" + n
                + "\n+=            : " + (t2 - t1) + " ns (độ dài kết quả=" + plusResult.length() + ")"
                + "\nStringBuilder : " + (t3 - t2) + " ns (độ dài kết quả=" + builderResult.length() + ")"
                + "\nPoint.withX   : " + (t4 - t3) + " ns (x cuối=" + lastImmutable.x() + ")"
                + "\nMutablePoint  : " + (t5 - t4) + " ns (x cuối=" + lastMutable.x + ")";
    }

    public static void main(String[] args) {
        System.out.println(runExperiment(200_000));
    }
}

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Immutable object không có method nào đổi state sau khi constructor chạy xong, nên không
 * tồn tại "khoảng thời gian" mà một thread đang sửa còn thread khác đang đọc field đó — mọi
 * thread luôn thấy đúng một giá trị duy nhất kể từ lúc object được publish an toàn (ví dụ
 * qua final field). Vì vậy không cần synchronized/lock khi nhiều thread cùng đọc chung một
 * SafeTeam hay Playlist; race condition (đọc thấy state nửa-cập-nhật) đơn giản là không thể
 * xảy ra vì chẳng có "cập nhật" nào để nửa chừng.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Nhược điểm: mỗi lần "sửa" phải tạo object mới, tốn cấp phát bộ nhớ và công GC dọn object
 * cũ — với vòng lặp chạy nhiều lần (nối chuỗi bằng +=, hay tạo lại Point liên tục), chi phí
 * này cộng dồn rõ rệt so với sửa tại chỗ một buffer/object mutable (StringBuilder,
 * MutablePoint). Nên chọn mutable khi: vòng lặp nóng (hot loop) sửa rất nhiều lần trong một
 * hàm, dữ liệu không cần chia sẻ ra ngoài, và không có yêu cầu thread-safety cho đối tượng
 * tạm đó — ví dụ dùng StringBuilder trong một method rồi chỉ trả về String (immutable) ở
 * bước cuối, thay vì nối chuỗi bằng += trong vòng lặp.
 * SOLUTION-END
 */

/* OBSERVATION Q6:
 * SOLUTION-BEGIN
 * Chạy main() với n = 200 000 ba lần trên máy tác giả (kết quả tham khảo, có thể khác trên
 * máy khác — số đo thô bằng nanoTime, chỉ có tính minh hoạ chứ không phải benchmark chuẩn):
 *   +=            : ~1512-1514 ms — cực chậm, vì mỗi vòng String cũ (dài dần) bị copy toàn bộ
 *                    sang String mới dài hơn 1 ký tự (độ phức tạp toàn vòng lặp gần O(n^2)).
 *   StringBuilder : ~3-4 ms — buffer nội bộ chỉ cấp phát lại (grow) theo cấp số nhân, tổng
 *                    chi phí gần O(n); nhanh hơn += khoảng 400-500 lần ở n = 200 000.
 *   Point.withX   : ~1.7-1.9 ms — tạo 200 000 record Point mới, nhưng object rất nhỏ (2 int)
 *                    nên chi phí cấp phát/GC vẫn thấp, chỉ chậm hơn MutablePoint ~1.5 lần.
 *   MutablePoint  : ~1.1-1.2 ms — chỉ set một field int tại chỗ, không cấp phát gì thêm.
 * Kết luận: chênh lệch khủng khiếp nằm ở nối chuỗi (String tăng kích thước mỗi vòng khiến
 * copy toàn bộ nội dung cũ lặp lại O(n) lần); với object nhỏ và cố định kích thước như
 * Point, chi phí tạo mới liên tục vẫn rất nhỏ ở quy mô này (không giống String).
 * SOLUTION-END
 */
