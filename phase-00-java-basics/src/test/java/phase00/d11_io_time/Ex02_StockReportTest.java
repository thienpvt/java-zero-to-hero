package phase00.d11_io_time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_StockReportTest {

    @TempDir
    Path dir;

    @Test
    @DisplayName("Q1 code: cộng số lượng UTF-8, bỏ dòng trống, cộng dồn mã trùng")
    void q01_sumsVietnameseCodes() throws IOException {
        Path input = dir.resolve("stock.txt");
        Path report = dir.resolve("report.txt");
        Files.writeString(input, "Đồng-hồ,2\n\nÁo,3\nĐồng-hồ,1\n", StandardCharsets.UTF_8);

        int total = Ex02_StockReport.summarize(input, report);

        assertEquals(6, total);
        String text = Files.readString(report, StandardCharsets.UTF_8);
        assertEquals("Đồng-hồ=3\nÁo=3\ntotal=6\n", text);
    }

    @Test
    @DisplayName("Q1 code: file rỗng vẫn ghi total=0")
    void q01_emptyFileWritesZero() throws IOException {
        Path input = dir.resolve("empty.txt");
        Path report = dir.resolve("report.txt");
        Files.writeString(input, "", StandardCharsets.UTF_8);

        int total = Ex02_StockReport.summarize(input, report);

        assertEquals(0, total);
        assertEquals("total=0\n", Files.readString(report, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Q1 code: file không tồn tại và dòng sai có số dòng")
    void q01_missingFileAndBadLine() throws IOException {
        Path missing = dir.resolve("khong-co.txt");
        Path report = dir.resolve("report.txt");
        assertThrows(NoSuchFileException.class, () -> Ex02_StockReport.summarize(missing, report));

        Path input = dir.resolve("bad.txt");
        Files.writeString(input, "Áo,1\nABC\n", StandardCharsets.UTF_8);
        IllegalArgumentException bad = assertThrows(IllegalArgumentException.class,
                () -> Ex02_StockReport.summarize(input, report));
        assertTrue(bad.getMessage().contains("dòng 2"));
    }
}
