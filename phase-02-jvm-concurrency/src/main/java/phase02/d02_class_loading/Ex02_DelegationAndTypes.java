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

    static final Boolean Q5_SAME_TYPE = false; // SOLUTION-VALUE

    static final String Q6_MISSING_CLASS_FORNAME = "ClassNotFoundException"; // SOLUTION-VALUE

    static final String Q6_MISSING_LINK_ERROR = "NoClassDefFoundError"; // SOLUTION-VALUE

    /**
     * {@code true} khi hai loader cho ra cùng một đối tượng Class.
     */
    static boolean sameTypeAcrossLoaders(Path dir) throws IOException {
        // SOLUTION-BEGIN throw Q5
        String binaryName = "isolated.Twin";
        Path sourceDir = dir.resolve("isolated");
        Files.createDirectories(sourceDir);
        Path source = sourceDir.resolve("Twin.java");
        Files.writeString(source, "package isolated;\npublic class Twin {}\n", StandardCharsets.UTF_8);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Cần JDK (javac) để biên dịch class thí nghiệm.");
        }
        int compiled = compiler.run(null, null, null, "-d", dir.toAbsolutePath().toString(),
                source.toAbsolutePath().toString());
        if (compiled != 0) {
            throw new IllegalStateException("Không biên dịch được class thí nghiệm, mã " + compiled);
        }

        String external = dir.toAbsolutePath().normalize().toUri().toURL().toExternalForm();
        if (!external.endsWith("/")) {
            external = external + "/";
        }
        URL url = URI.create(external).toURL();
        ClassLoader parent = Ex02_DelegationAndTypes.class.getClassLoader();
        try (URLClassLoader first = new URLClassLoader(new URL[] {url}, parent);
                URLClassLoader second = new URLClassLoader(new URL[] {url}, parent)) {
            Class<?> left = first.loadClass(binaryName);
            Class<?> right = second.loadClass(binaryName);
            return left == right;
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Không load được " + binaryName + " từ " + dir, e);
        }
        // SOLUTION-END
    }
}

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Parent delegation bắt loader con hỏi cha trước khi tự định nghĩa class.
 * Nhờ đó class lõi (java.lang.String và API nền) chỉ có một bản từ bootstrap hoặc platform,
 * code ứng dụng không thay thế được chúng, và mọi loader con dùng chung định nghĩa mà cha đã load.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * ClassNotFoundException là checked exception khi code chủ động xin load theo tên
 * (Class.forName, ClassLoader.loadClass) mà loader không tìm thấy bytecode.
 * NoClassDefFoundError là Error lúc liên kết hoặc initialize: class đã compile được vì
 * dependency có mặt lúc biên dịch, nhưng lúc chạy JVM không còn tìm thấy định nghĩa đó
 * (jar thiếu, file class bị xóa sau khi compile).
 * SOLUTION-END
 */
