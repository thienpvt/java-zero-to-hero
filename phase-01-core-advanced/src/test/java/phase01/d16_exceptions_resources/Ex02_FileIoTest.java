package phase01.d16_exceptions_resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase01.support.Predictions.assertPrediction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;
import phase01.d16_exceptions_resources.Ex02_FileIo.ConfigException;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_FileIoTest {

    private static final String HINT_Q3 =
            "Xem Javadoc Files.readString (Ctrl+Q trên tên method): đọc toàn bộ nội dung vào 1 String"
                    + " trong bộ nhớ; JDK ghi rõ \"@throws OutOfMemoryError if the file is extremely"
                    + " large, for example larger than 2GB\".";

    @Test
    @DisplayName("Q3 dự đoán: Files.readString() có tải toàn bộ file vào bộ nhớ không")
    void q03_readStringLoadsWholeFilePrediction() {
        assertPrediction("Q3_READSTRING_LOADS_WHOLE_FILE", true, Ex02_FileIo.Q3_READSTRING_LOADS_WHOLE_FILE, HINT_Q3);
    }

    @Test
    @DisplayName("Q3 code: countNonBlankLines đếm đúng số dòng không trống")
    void q03_countNonBlankLinesCountsCorrectly(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("data.txt");
        Files.writeString(file, "a\n\n  \nb\n", StandardCharsets.UTF_8);

        long count = Ex02_FileIo.countNonBlankLines(file);

        assertEquals(2, count, "Chỉ 2 dòng 'a' và 'b' là không trống; dòng rỗng và dòng chỉ có khoảng trắng bị bỏ.");
    }

    @Test
    @DisplayName("Q3 code: countNonBlankLines đọc đúng nội dung tiếng Việt UTF-8")
    void q03_countNonBlankLinesHandlesVietnameseUtf8(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("vi.txt");
        Files.writeString(file, "xin chào\nViệt Nam\n", StandardCharsets.UTF_8);

        long count = Ex02_FileIo.countNonBlankLines(file);

        assertEquals(2, count, "Cả 2 dòng tiếng Việt đều không trống, phải đọc đúng dù charset mặc định của máy khác UTF-8.");
    }

    @Test
    @DisplayName("Q4 code: readConfigValue đọc đúng giá trị, bỏ qua comment và dòng trống")
    void q04_readConfigValueReadsExistingKeys(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("app.conf");
        Files.writeString(file, "# c\ntên = Thiện\nport=8080\n", StandardCharsets.UTF_8);

        assertEquals("Thiện", Ex02_FileIo.readConfigValue(file, "tên"), "Giá trị của khóa 'tên' phải được trim hai phía.");
        assertEquals("8080", Ex02_FileIo.readConfigValue(file, "port"), "Giá trị của khóa 'port' phải đọc đúng.");
    }

    @Test
    @DisplayName("Q4 code: readConfigValue báo lỗi khi thiếu khóa, không có cause")
    void q04_readConfigValueMissingKeyThrowsWithoutCause(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("app.conf");
        Files.writeString(file, "# c\ntên = Thiện\nport=8080\n", StandardCharsets.UTF_8);

        ConfigException thrown = assertThrows(ConfigException.class,
                () -> Ex02_FileIo.readConfigValue(file, "khongCoKhoaNay"));

        assertEquals("Thiếu khóa: khongCoKhoaNay", thrown.getMessage());
        assertNull(thrown.getCause(), "Thiếu khóa không phải lỗi đọc file nên không có cause.");
    }

    @Test
    @DisplayName("Q4 code: readConfigValue báo lỗi có cause NoSuchFileException khi file không tồn tại")
    void q04_readConfigValueMissingFileThrowsWithCause(@TempDir Path dir) {
        Path file = dir.resolve("missing.conf");

        ConfigException thrown = assertThrows(ConfigException.class,
                () -> Ex02_FileIo.readConfigValue(file, "port"));

        assertEquals("Không đọc được cấu hình: " + file, thrown.getMessage());
        assertEquals(NoSuchFileException.class, thrown.getCause().getClass(),
                "Cause phải là NoSuchFileException do Files.readAllLines ném ra khi file không tồn tại.");
    }
}
