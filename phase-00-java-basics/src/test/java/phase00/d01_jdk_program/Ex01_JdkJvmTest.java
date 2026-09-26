package phase00.d01_jdk_program;

import static phase00.support.Predictions.assertPrediction;

import javax.tools.ToolProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_JdkJvmTest {

    private static final String HINT_Q1 =
            "Ctrl+Q trên ToolProvider.getSystemJavaCompiler; trong Terminal so javac -help với java -help.";

    @Test
    @DisplayName("Q1 dự đoán: JDK có compiler, và lệnh java có biên dịch source không?")
    void q01_prediction() {
        boolean compilerPresent = ToolProvider.getSystemJavaCompiler() != null;
        assertPrediction("Q1_JDK_PROVIDES_COMPILER",
                compilerPresent, Ex01_JdkJvm.Q1_JDK_PROVIDES_COMPILER, HINT_Q1);
        assertPrediction("Q1_JAVA_COMMAND_COMPILES_SOURCE",
                false, Ex01_JdkJvm.Q1_JAVA_COMMAND_COMPILES_SOURCE, HINT_Q1);
    }
}
