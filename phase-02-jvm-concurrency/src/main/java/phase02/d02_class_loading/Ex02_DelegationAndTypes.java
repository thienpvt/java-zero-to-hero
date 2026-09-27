package phase02.d02_class_loading;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * Class Loading — Bài 2: Parent delegation và type
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 2 (Class Loading), câu 4–6.
 * Cần làm trước: Ex01_LoadVsInit.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex02_DelegationAndTypesTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q4 [TỰ TRẢ LỜI] Parent delegation giải quyết vấn đề gì?
 *   Bắt đầu   : viết ANSWER Q4. Đọc Q5 sau khi đã chạy sameTypeAcrossLoaders để thấy loader con
 *               chỉ tự định nghĩa class khi cha không có class đó.
 *   Kiểm chứng: Ctrl+N → ClassLoader → Ctrl+F12 → loadClass, Ctrl+Q đọc đoạn parent delegation.
 *   Hoàn thành khi: viết xong khối ANSWER Q4.
 * <p>
 * Q5 [DỰ ĐOÁN + CODE] Hai class cùng tên do hai ClassLoader khác nhau load có phải cùng type không?
 *   Bắt đầu   : cài sameTypeAcrossLoaders(Path); điền Q5_SAME_TYPE (thay null).
 *   Kiểm chứng: chạy q05_*. Hai URLClassLoader cùng parent, load cùng tên từ thư mục ngoài classpath
 *               của ứng dụng. So hai đối tượng Class bằng ==. Ctrl+Q trên URLClassLoader.
 *   Code      : ghi một class tối thiểu vào dir bằng JavaCompiler (class này không nằm trên classpath
 *               ứng dụng), mở hai URLClassLoader riêng, loadClass cùng tên, trả về kết quả so sánh ==.
 *   Hoàn thành khi: test q05_* xanh.
 * <p>
 * Q6 [DỰ ĐOÁN] {@code ClassNotFoundException} và {@code NoClassDefFoundError} khác nhau thế nào?
 *   Bắt đầu   : điền Q6_MISSING_CLASS_FORNAME sau khi gọi
 *               {@code Class.forName("phase02.missing.NoSuch")} và xem simple name của exception
 *               (Alt+F8 hoặc một main nhỏ). Điền Q6_MISSING_LINK_ERROR (thay null). Viết ANSWER Q6.
 *               Không cần dựng class hỏng.
 *   Kiểm chứng: chạy q06_*. Ctrl+N tới từng lớp exception trong câu hỏi, Ctrl+Q đọc khi nào lớp đó bị ném.
 *   Hoàn thành khi: test q06_* xanh và ANSWER Q6 phân biệt lúc gọi forName với lúc liên kết class đã compile.
 */
public class Ex02_DelegationAndTypes {

    static final Boolean Q5_SAME_TYPE = null;

    static final String Q6_MISSING_CLASS_FORNAME = null;

    static final String Q6_MISSING_LINK_ERROR = null;

    /**
     * {@code true} khi hai loader cho ra cùng một đối tượng Class.
     */
    static boolean sameTypeAcrossLoaders(Path dir) throws IOException {
        throw new UnsupportedOperationException("TODO Q5");
    }
}

/* ANSWER Q4:
 *
 */

/* ANSWER Q6:
 *
 */
