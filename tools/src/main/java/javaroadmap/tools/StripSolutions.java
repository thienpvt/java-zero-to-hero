package javaroadmap.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Sinh bản khung bài tập từ bản lời giải có đánh dấu SOLUTION-BEGIN/END và SOLUTION-VALUE.
 *
 * <p>Chạy không cần build: {@code java tools/src/main/java/javaroadmap/tools/StripSolutions.java <thư mục>...}
 * — ghi đè tại chỗ mọi file .java trong các thư mục được truyền vào.
 */
public final class StripSolutions {

    private static final Pattern BEGIN =
            Pattern.compile("^(\\s*)(//|\\*)?\\s*SOLUTION-BEGIN(?:\\s+(throw|todo)\\s+([A-Z]+\\d+))?\\s*$");
    private static final Pattern END = Pattern.compile("^\\s*(?://|\\*)?\\s*SOLUTION-END\\s*$");
    private static final Pattern VALUE = Pattern.compile("^(.*?=)\\s*.+;\\s*// SOLUTION-VALUE\\s*$");

    private StripSolutions() {
    }

    public static String strip(String source) {
        String newline = source.contains("\r\n") ? "\r\n" : "\n";
        String[] lines = source.split("\\R", -1);
        List<String> out = new ArrayList<>();
        int openedAt = -1;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            Matcher begin = BEGIN.matcher(line);
            boolean isEnd = END.matcher(line).matches();
            if (openedAt >= 0) {
                if (begin.matches()) {
                    throw new IllegalArgumentException("SOLUTION-BEGIN lồng nhau ở dòng " + (i + 1));
                }
                if (isEnd) {
                    openedAt = -1;
                }
                continue;
            }
            if (begin.matches()) {
                openedAt = i;
                out.add(replacement(begin));
            } else if (isEnd) {
                throw new IllegalArgumentException("SOLUTION-END không có BEGIN ở dòng " + (i + 1));
            } else {
                Matcher value = VALUE.matcher(line);
                out.add(value.matches() ? value.group(1) + " null;" : line);
            }
        }
        if (openedAt >= 0) {
            throw new IllegalArgumentException("SOLUTION-BEGIN chưa đóng ở dòng " + (openedAt + 1));
        }
        for (int i = 0; i < out.size(); i++) {
            if (out.get(i).contains("SOLUTION-")) {
                throw new IllegalArgumentException(
                        "Còn marker SOLUTION- sau khi strip, dòng " + (i + 1) + ": " + out.get(i).trim());
            }
        }
        return String.join(newline, out);
    }

    private static String replacement(Matcher begin) {
        String indent = begin.group(1);
        String commentMarker = begin.group(2);
        String kind = begin.group(3);
        String question = begin.group(4);
        if ("throw".equals(kind)) {
            return indent + "throw new UnsupportedOperationException(\"TODO " + question + "\");";
        }
        if ("todo".equals(kind)) {
            return indent + "// TODO " + question + ": viết code của bạn ở đây";
        }
        return indent + (commentMarker == null ? "//" : commentMarker);
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Cách dùng: java StripSolutions.java <thư mục chứa .java>...");
            System.exit(2);
        }
        int changed = 0;
        for (String arg : args) {
            try (Stream<Path> paths = Files.walk(Path.of(arg))) {
                for (Path file : paths.filter(p -> p.toString().endsWith(".java")).toList()) {
                    String original = Files.readString(file, StandardCharsets.UTF_8);
                    String stripped = strip(original);
                    if (!stripped.equals(original)) {
                        Files.writeString(file, stripped, StandardCharsets.UTF_8);
                        changed++;
                    }
                }
            }
        }
        System.out.println("Stripped " + changed + " file(s).");
    }
}
