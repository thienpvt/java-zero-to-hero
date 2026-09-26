package phase01.d17_capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;
import phase01.d17_capstone.CsvOrderParser.LineError;
import phase01.d17_capstone.CsvOrderParser.ParseResult;

@TestMethodOrder(MethodOrderer.MethodName.class)
class CsvOrderParserTest {

    private Path sampleFile() throws Exception {
        return Path.of(getClass().getResource("/phase01/d17_capstone/orders-sample.csv").toURI());
    }

    @Test
    @DisplayName("B2 parse: file mẫu trả về đúng 5 order hợp lệ theo thứ tự trong file")
    void b02_parseSampleFileReturnsValidOrdersInFileOrder() throws Exception {
        ParseResult result = new CsvOrderParser().parse(sampleFile());

        assertEquals(List.of("o1", "o2", "o3", "o4", "o6"),
                result.orders().stream().map(Order::id).toList(),
                "5 order hợp lệ trong file mẫu là o1, o2, o3, o4, o6 (o5 lỗi ngày, o1 dòng 8 trùng id).");
    }

    @Test
    @DisplayName("B2 parse: file mẫu trả về đúng 2 lỗi ở dòng 6 (ngày sai) và dòng 8 (trùng id)")
    void b02_parseSampleFileReturnsErrorsAtCorrectLineNumbers() throws Exception {
        ParseResult result = new CsvOrderParser().parse(sampleFile());

        assertEquals(2, result.errors().size(), "File mẫu phải có đúng 2 lỗi.");
        LineError first = result.errors().get(0);
        LineError second = result.errors().get(1);
        assertEquals(6, first.lineNumber(), "Lỗi đầu tiên (ngày sai của o5) phải ở dòng 6.");
        assertEquals(8, second.lineNumber(), "Lỗi thứ hai (o1 trùng id) phải ở dòng 8.");
        assertTrue(second.message().contains("o1"), "Thông báo lỗi trùng id phải nêu rõ id 'o1' bị trùng.");
    }

    @Test
    @DisplayName("B2 parse: customerId tiếng Việt có dấu được giữ nguyên khi đọc UTF-8")
    void b02_parsePreservesVietnameseCustomerId() throws Exception {
        ParseResult result = new CsvOrderParser().parse(sampleFile());

        Order firstOrder = result.orders().get(0);
        assertEquals("khách-Đạt", firstOrder.customerId(),
                "customerId 'khách-Đạt' phải giữ nguyên dấu tiếng Việt sau khi đọc UTF-8.");
    }

    @Test
    @DisplayName("B2 parse: file 0 byte trả về rỗng, không lỗi")
    void b02_parseEmptyFileReturnsEmptyResultNoErrors(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("empty.csv");
        Files.writeString(file, "", StandardCharsets.UTF_8);

        ParseResult result = new CsvOrderParser().parse(file);

        assertTrue(result.orders().isEmpty(), "File 0 byte không có order nào.");
        assertTrue(result.errors().isEmpty(), "File 0 byte không phải là lỗi.");
    }

    @Test
    @DisplayName("B2 parse: file chỉ có header trả về rỗng, không lỗi")
    void b02_parseHeaderOnlyFileReturnsEmptyResult(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("header-only.csv");
        Files.writeString(file, CsvOrderParser.HEADER + "\n", StandardCharsets.UTF_8);

        ParseResult result = new CsvOrderParser().parse(file);

        assertTrue(result.orders().isEmpty(), "Chỉ có header thì không có order nào.");
        assertTrue(result.errors().isEmpty(), "Header đúng thì không có lỗi.");
    }

    @Test
    @DisplayName("B2 parse: header sai trả về 1 lỗi ở dòng 1 và không có order nào")
    void b02_parseWrongHeaderReturnsOneErrorAndNoOrders(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("bad-header.csv");
        Files.writeString(file,
                "id,customerId,createdAt,amount\n" + "o1,an,2026-01-01T00:00:00Z,1.00,PAID\n",
                StandardCharsets.UTF_8);

        ParseResult result = new CsvOrderParser().parse(file);

        assertTrue(result.orders().isEmpty(), "Header sai thì phải dừng ngay, không order nào được parse.");
        assertEquals(1, result.errors().size(), "Header sai chỉ tạo đúng 1 lỗi.");
        assertEquals(1, result.errors().get(0).lineNumber(), "Lỗi header sai phải ở dòng 1.");
    }

    @Test
    @DisplayName("B2 parse: dòng thiếu trường báo lỗi đúng số dòng, các dòng khác vẫn được đọc tiếp")
    void b03_parseLineWithMissingFieldReportsCorrectLineNumber(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("missing-field.csv");
        String content = CsvOrderParser.HEADER + "\n"
                + "o1,an,2026-01-01T00:00:00Z,1.00\n" // dòng 2: thiếu trường status
                + "o2,an,2026-01-02T00:00:00Z,2.00,PAID\n"; // dòng 3: hợp lệ
        Files.writeString(file, content, StandardCharsets.UTF_8);

        ParseResult result = new CsvOrderParser().parse(file);

        assertEquals(1, result.errors().size(), "Chỉ dòng 2 thiếu trường mới bị lỗi.");
        assertEquals(2, result.errors().get(0).lineNumber(), "Dòng thiếu trường là dòng 2.");
        assertEquals(List.of("o2"), result.orders().stream().map(Order::id).toList(),
                "Dòng 3 hợp lệ vẫn phải được đọc tiếp sau khi dòng 2 lỗi.");
    }

    @Test
    @DisplayName("B2 parse: amount âm báo lỗi tại đúng dòng đó")
    void b03_parseNegativeAmountReportsErrorAtLine(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("negative-amount.csv");
        String content = CsvOrderParser.HEADER + "\n" + "o1,an,2026-01-01T00:00:00Z,-5.00,PAID\n";
        Files.writeString(file, content, StandardCharsets.UTF_8);

        ParseResult result = new CsvOrderParser().parse(file);

        assertTrue(result.orders().isEmpty(), "amount âm thì order không được tạo.");
        assertEquals(1, result.errors().size(), "amount âm phải tạo đúng 1 lỗi.");
        assertEquals(2, result.errors().get(0).lineNumber(), "Lỗi amount âm phải ở dòng 2.");
    }

    @Test
    @DisplayName("B2 parse: file không tồn tại thì ném NoSuchFileException, không bị nuốt")
    void b03_parseMissingFileThrowsNoSuchFileException(@TempDir Path tempDir) {
        Path file = tempDir.resolve("khong-ton-tai-" + UUID.randomUUID() + ".csv");
        CsvOrderParser parser = new CsvOrderParser();

        assertThrows(NoSuchFileException.class, () -> parser.parse(file),
                "File không tồn tại phải ném NoSuchFileException ra ngoài, không được bắt/nuốt.");
    }
}
