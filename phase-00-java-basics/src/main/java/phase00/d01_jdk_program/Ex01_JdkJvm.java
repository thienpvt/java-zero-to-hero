package phase00.d01_jdk_program;

/**
 * JDK và JVM — Bài 1: Công cụ biên dịch và lệnh chạy
 *
 * Nguồn: 00-java-basics-review.md, mục 1 (JDK, JVM và cấu trúc chương trình), câu 1.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_JdkJvmTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q1 [DỰ ĐOÁN + TỰ TRẢ LỜI] JDK khác JVM ở đâu? Lệnh {@code javac} và {@code java} làm gì?
 *   Bắt đầu   : điền hai hằng Q1_* (thay null), rồi viết khối ANSWER Q1 ở cuối file.
 *   Kiểm chứng: chạy q01_prediction. Muốn tự thấy compiler: Terminal (Alt+F12) gõ
 *               {@code javac -help} và {@code java -help}, đối chiếu lệnh nào nói tới
 *               source {@code .java}. Trong IDE, Ctrl+N gõ {@code ToolProvider}, Ctrl+F12
 *               tìm {@code getSystemJavaCompiler}, Ctrl+Q đọc Javadoc.
 *   Hoàn thành khi: q01_prediction xanh; ANSWER Q1 nói được JDK gồm những gì so với JVM
 *               và mỗi lệnh tạo ra hoặc tiêu thụ file gì.
 */
public class Ex01_JdkJvm {

    // Q1 — kịch bản: quá trình đang chạy có xin được Java compiler từ JDK hay không.
    static final Boolean Q1_JDK_PROVIDES_COMPILER = true; // SOLUTION-VALUE

    // Q1 — kịch bản: lệnh `java` có phải công cụ biên dịch file .java thành .class không?
    static final Boolean Q1_JAVA_COMMAND_COMPILES_SOURCE = false; // SOLUTION-VALUE

    /* ANSWER Q1:
     * SOLUTION-BEGIN
     * JDK là bộ công cụ phát triển: gồm JVM, thư viện chuẩn và các lệnh như javac.
     * JVM là máy ảo thực thi bytecode, không kèm trình biên dịch.
     * javac đọc source .java và ghi bytecode .class. Lệnh java khởi động JVM để nạp
     * và chạy .class (hoặc một lớp trên classpath), không biên dịch source.
     * SOLUTION-END
     */
}
