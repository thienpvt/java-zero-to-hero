package phase02.d01_bytecode;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static phase02.support.Predictions.assertPrediction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HexFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;
import phase02.d01_bytecode.Ex01_CompileAndClassFile.Pipeline;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_CompileAndClassFileTest {

    private static final String HINT_Q1 =
            "javac dịch source thành bytecode; JVM thực thi bytecode đó. Hằng đúng là SOURCE_TO_BYTECODE_THEN_JVM.";

    private static final String HINT_Q2 =
            "Bốn byte đầu file class là CA FE BA BE. HexFormat.of().withUpperCase().formatHex trên đúng bốn byte đó ra CAFEBABE.";

    private static final String HINT_Q6 =
            "JVM không chạy file .java. Nó chạy bytecode trong file .class, nên hằng này là false.";

    private static final byte[] CLASS_MAGIC_BYTES = {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Q1 dự đoán: Java được compile hay interpreted")
    void q01_prediction() {
        assertPrediction("Q1_JAVA_PIPELINE",
                Pipeline.SOURCE_TO_BYTECODE_THEN_JVM,
                Ex01_CompileAndClassFile.Q1_JAVA_PIPELINE,
                HINT_Q1);
    }

    @Test
    @DisplayName("Q2: bốn byte đầu của file class và chuỗi hex dự đoán")
    void q02_classMagic() throws IOException {
        Path classFile = tempDir.resolve("Sample.class");
        byte[] contents = new byte[CLASS_MAGIC_BYTES.length + 2];
        System.arraycopy(CLASS_MAGIC_BYTES, 0, contents, 0, CLASS_MAGIC_BYTES.length);
        Files.write(classFile, contents);

        byte[] magic = Ex01_CompileAndClassFile.classMagic(classFile);

        assertNotNull(magic, "classMagic phải trả về bốn byte đầu, không trả về null.");
        assertArrayEquals(CLASS_MAGIC_BYTES, magic, "classMagic phải trả về đúng bốn byte đầu của file.");
        String actual = HexFormat.of().withUpperCase().formatHex(magic);
        assertPrediction("Q2_CLASS_MAGIC", actual, Ex01_CompileAndClassFile.Q2_CLASS_MAGIC, HINT_Q2);
    }

    @Test
    @DisplayName("Q6 dự đoán: JVM có chạy trực tiếp source code không")
    void q06_prediction() {
        assertPrediction("Q6_JVM_RUNS_SOURCE",
                false,
                Ex01_CompileAndClassFile.Q6_JVM_RUNS_SOURCE,
                HINT_Q6);
    }
}
