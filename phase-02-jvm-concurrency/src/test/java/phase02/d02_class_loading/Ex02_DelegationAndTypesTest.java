package phase02.d02_class_loading;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static phase02.support.Predictions.assertPrediction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_DelegationAndTypesTest {

    private static final String HINT_Q5 =
            "Parent không có class nằm ngoài classpath nên mỗi URLClassLoader tự định nghĩa một Class. "
                    + "Hai đối tượng Class cùng tên vẫn khác nhau khi so bằng ==. Ctrl+Q trên ClassLoader.loadClass.";

    private static final String HINT_Q6_FORNAME =
            "Class.forName ném checked exception khi không tìm thấy tên lớp. Ctrl+Q trên Class.forName, "
                    + "xem simple name của exception vừa bắt.";

    private static final String HINT_Q6_LINK =
            "Đây là Error lúc liên kết class đã compile được, không phải exception của lần gọi forName "
                    + "với tên không tồn tại. Ctrl+N tới lớp Error trong câu hỏi rồi Ctrl+Q.";

    @Test
    @DisplayName("Q5: hai URLClassLoader load cùng tên từ thư mục ngoài classpath ra hai Class")
    void q05_sameTypeAcrossLoaders_differentClassObjects() throws IOException {
        Path dir = Files.createTempDirectory("d02-q05-");
        try {
            boolean actual = Ex02_DelegationAndTypes.sameTypeAcrossLoaders(dir);

            assertFalse(actual,
                    "Hai URLClassLoader phải cho hai đối tượng Class khác nhau khi class không thuộc parent.");
            assertPrediction("Q5_SAME_TYPE", actual, Ex02_DelegationAndTypes.Q5_SAME_TYPE, HINT_Q5);
        } finally {
            deleteRecursively(dir);
        }
    }

    @Test
    @DisplayName("Q6: forName tên không tồn tại, và tên lỗi lúc liên kết class đã compile")
    void q06_missingClassForName_andLinkError() {
        String forNameFailure;
        try {
            Class.forName("phase02.missing.NoSuch");
            forNameFailure = "không ném";
        } catch (Throwable thrown) {
            forNameFailure = thrown.getClass().getSimpleName();
        }

        assertPrediction("Q6_MISSING_CLASS_FORNAME", forNameFailure,
                Ex02_DelegationAndTypes.Q6_MISSING_CLASS_FORNAME, HINT_Q6_FORNAME);
        assertPrediction("Q6_MISSING_LINK_ERROR", "NoClassDefFoundError",
                Ex02_DelegationAndTypes.Q6_MISSING_LINK_ERROR, HINT_Q6_LINK);
    }

    private static void deleteRecursively(Path root) throws IOException {
        if (Files.notExists(root)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(root)) {
            walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // Thư mục tạm có thể còn khóa sau khi đóng URLClassLoader.
                }
            });
        }
    }
}
