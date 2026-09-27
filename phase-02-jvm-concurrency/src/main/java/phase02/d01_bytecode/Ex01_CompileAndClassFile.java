package phase02.d01_bytecode;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Bytecode — Bài 1: Biên dịch và file class
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 1 (Compilation &amp; Bytecode), câu 1, 2, 6.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_CompileAndClassFileTest bằng nút ▶
 * (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 * <p>
 * Q1 [DỰ ĐOÁN] Java được compile hay interpreted?
 *   Bắt đầu   : đọc enum Pipeline, điền hằng Q1_JAVA_PIPELINE (thay null).
 *   Kiểm chứng: chạy q01_prediction. Nếu sai, Ctrl+N mở lớp này, Ctrl+F12 tìm Pipeline,
 *               đối chiếu từng hằng với pipeline javac rồi JVM.
 *   Hoàn thành khi: q01_prediction xanh và khối ANSWER Q1 giải thích pipeline bạn đã chọn.
 * <p>
 * Q2 [DỰ ĐOÁN + CODE] {@code .class} chứa gì?
 *   Bắt đầu   : cài đặt classMagic(Path) để đọc đúng bốn byte đầu; điền Q2_CLASS_MAGIC
 *               bằng chuỗi hex hoa, không dấu cách (thay null).
 *   Kiểm chứng: chạy q02_classMagic. Debug test, F7 vào classMagic, Alt+F8 đánh giá
 *               HexFormat.of().withUpperCase().formatHex trên mảng vừa đọc, sau khi đã điền dự đoán.
 *   Code      : static byte[] classMagic(Path classFile).
 *   Hoàn thành khi: q02_classMagic xanh; ANSWER Q2 nói file class chứa gì ngoài bốn byte đầu.
 * <p>
 * Q6 [DỰ ĐOÁN] JVM có chạy trực tiếp source code không?
 *   Bắt đầu   : điền hằng Q6_JVM_RUNS_SOURCE (thay null).
 *   Kiểm chứng: chạy q06_prediction. Ctrl+N mở lớp này; test chỉ ghi byte nhị phân ra file tạm,
 *               không đưa text source cho JVM.
 *   Hoàn thành khi: q06_prediction xanh và khối ANSWER Q6 trả lời câu hỏi này.
 */
public class Ex01_CompileAndClassFile {

    enum Pipeline {
        SOURCE_TO_BYTECODE_THEN_JVM,
        SOURCE_DIRECTLY_ON_CPU,
        INTERPRETER_ONLY
    }

    // Q1 — chọn một hằng của Pipeline cho chương trình Java thông thường.
    static final Pipeline Q1_JAVA_PIPELINE = Pipeline.SOURCE_TO_BYTECODE_THEN_JVM; // SOLUTION-VALUE

    // Q2 — chuỗi hex hoa, không dấu cách, của đúng bốn byte đầu.
    static final String Q2_CLASS_MAGIC = "CAFEBABE"; // SOLUTION-VALUE

    /**
     * Đọc đúng bốn byte đầu của {@code classFile}.
     *
     * @throws IllegalArgumentException nếu file ngắn hơn bốn byte
     * @throws UncheckedIOException nếu không đọc được file
     */
    static byte[] classMagic(Path classFile) {
        // SOLUTION-BEGIN throw Q2
        try (InputStream in = Files.newInputStream(classFile)) {
            byte[] magic = in.readNBytes(4);
            if (magic.length != 4) {
                throw new IllegalArgumentException("File class không đủ 4 byte đầu: " + classFile);
            }
            return magic;
        } catch (IOException e) {
            throw new UncheckedIOException("Không đọc được file class: " + classFile, e);
        }
        // SOLUTION-END
    }

    // Q6 — JVM có thực thi trực tiếp file nguồn .java không?
    static final Boolean Q6_JVM_RUNS_SOURCE = false; // SOLUTION-VALUE
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Java được biên dịch bằng javac thành bytecode, rồi JVM thực thi bytecode đó.
 * Pipeline đúng là source sang bytecode rồi tới JVM: có interpreter lúc đầu và JIT khi đoạn code nóng.
 * Source không chạy thẳng trên CPU, và chương trình cũng không chỉ có interpreter suốt đời.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * File .class chứa bytecode của các method cùng metadata: phiên bản, constant pool, field, method, attribute.
 * Bốn byte đầu là magic number CA FE BA BE.
 * HexFormat chữ hoa trên đúng bốn byte đó cho chuỗi CAFEBABE.
 * JVM đọc bytecode này, không đọc source Java từ file class.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * JVM không chạy file .java. Nó nạp bytecode đã nằm trong file .class.
 * Muốn chạy source phải qua javac hoặc một trình biên dịch tương đương trước.
 * Kể cả jshell cũng biên dịch đoạn nhập thành bytecode rồi mới thực thi.
 * SOLUTION-END
 */
