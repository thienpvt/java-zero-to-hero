# Bài thực hành IDE Giai đoạn 1 — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Tạo module Maven `phase-01-core-advanced` chứa bài thực hành (khung TODO + JUnit test) cho toàn bộ 16 domain và bài tích hợp của `01-java-core-advanced.md`, cùng nhánh lời giải.

**Architecture:** Maven multi-module (parent + `tools` + `phase-01-core-advanced`). Mỗi domain là một package `phase01.dNN_<tên>`. Implementer viết **bản lời giải** có đánh dấu `SOLUTION-*`; công cụ `StripSolutions` sinh bản khung (skeleton) bằng cách thay các khối đánh dấu bằng `throw new UnsupportedOperationException("TODO Qn")` / `null` / dòng trống. Nhánh `solutions` giữ bản lời giải, nhánh `phase01-exercises` giữ bản khung.

**Tech Stack:** JDK 21 (Temurin 21.0.12 tại `C:\Users\thien\.jdks\jdk-21.0.12.1+1`), Maven 3.9.9 qua Maven Wrapper (only-script), JUnit Jupiter 5.11.4, PowerShell 7 cho script kiểm tra.

**Spec:** `docs/superpowers/specs/2026-09-26-phase01-practice-exercises-design.md`

## Global Constraints

- JDK 21: `<maven.compiler.release>21</maven.compiler.release>`; không dùng preview feature.
- Chỉ JUnit Jupiter 5.11.4 (qua `junit-bom`); không AssertJ, Mockito, JMH hay thư viện ngoài nào khác.
- Encoding UTF-8 cho source; toàn bộ Javadoc, thông điệp assertion, `@DisplayName` viết tiếng Việt có dấu.
- Package gốc `phase01`; package domain `dNN_<tên>` đúng như bảng trong từng task; file `ExNN_<ChủĐềCon>.java`, test `ExNN_<ChủĐềCon>Test.java` cùng package trong `src/test/java`.
- Không đọc field/method private của JDK bằng reflection (không `--add-opens`). Ngoại lệ duy nhất: câu d13 Q4 cố ý *thử* `setAccessible` trên `String.value` để quan sát `InaccessibleObjectException`.
- Không test nào phụ thuộc thời gian đo, race giữa các thread, múi giờ/locale mặc định, hay thứ tự chạy giữa các class. Demo race/hiệu năng chỉ nằm trong `main()`/`runExperiment` của bài `[THÍ NGHIỆM]`.
- Luôn truyền `ZoneId` tường minh; dùng `Locale.ROOT` cho `toUpperCase/toLowerCase`. Surefire đặt `-Duser.timezone=Pacific/Chatham` để lộ lỗi phụ thuộc múi giờ mặc định.
- Mỗi câu hỏi của `.md` xuất hiện **đúng một lần** dưới dạng dòng ` * Qn [NHÃN] <câu hỏi>` trong Javadoc đầu file của đúng domain; số `Qn` giữ đúng số trong `.md`.
- Nhánh làm việc không bao giờ là `main`. Không push (repo không có remote), không merge vào `main`.

## Review Focus

- **Test xanh giả trên skeleton:** `assertThrows(UnsupportedOperationException.class, () -> hàmTODO())` sẽ xanh ngay trên khung vì TODO cũng ném UOE → luôn gọi hàm của ứng viên *ngoài* lambda rồi mới `assertThrows` trên thao tác sửa kết quả. `verify-skeleton.ps1` bắt lỗi này (mọi test xanh trên skeleton mà tên không chứa `experimentRuns` là vi phạm).
- **Test chập chờn:** race, thời gian, identity hashCode. Chỉ assert điều tất định; phần không tất định đưa vào `main()`.
- **Phụ thuộc môi trường:** múi giờ/locale mặc định, `\r\n` vs `\n`, encoding console. Test đọc/ghi file luôn chỉ định `StandardCharsets.UTF_8`; d16 có test nội dung tiếng Việt.
- **Sai kiến thức Java trong dự đoán:** giá trị đúng của hằng số dự đoán phải được test **tính từ code thật** khi có thể; chỉ hard-code khi là hằng số trong source JDK (ghi rõ nguồn trong gợi ý) hoặc câu "có biên dịch được không".
- **Thiếu/trùng câu hỏi:** `verify-skeleton.ps1 -ExpectedQuestions N` kiểm tra tập `Qn` của package bằng đúng `1..N`.

---

## Môi trường cho mọi task (PowerShell 7)

Mỗi phiên shell mới, chạy trước:

```powershell
$env:JAVA_HOME = "C:\Users\thien\.jdks\jdk-21.0.12.1+1"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

Chạy test một domain (trong thư mục gốc của worktree):

```powershell
.\mvnw.cmd -q -pl phase-01-core-advanced test "-Dtest=phase01/d04_hashmap/*Test"
```

Kiểm tra skeleton của một domain:

```powershell
.\tools\verify-skeleton.ps1 -Module phase-01-core-advanced -Package d04_hashmap -ExpectedQuestions 15
```

---

## Hướng dẫn biên soạn bài (bắt buộc cho Task 2–18)

### A. Đánh dấu lời giải (StripSolutions)

Bạn viết **bản đã giải**. Công cụ sinh bản khung theo 3 loại đánh dấu:

1. **Thân code cần ứng viên viết** — thay bằng câu `throw`:
   ```java
   static int capacityFor(int expectedSize) {
       // SOLUTION-BEGIN throw Q7
       ...code lời giải...
       // SOLUTION-END
   }
   ```
   → skeleton: `throw new UnsupportedOperationException("TODO Q7");` (giữ thụt lề của dòng BEGIN).
   Dùng được trong method, constructor, compact constructor của record, default method của interface. Mọi method có khối này phải biên dịch được khi thân chỉ còn `throw` (được, vì `throw` kết thúc bất thường).
2. **Code không phải thân method** (hiếm; ví dụ vài dòng trong `main`) — `// SOLUTION-BEGIN todo Qn` … `// SOLUTION-END` → skeleton: `// TODO Qn: viết code của bạn ở đây`. Code còn lại phải vẫn biên dịch.
3. **Hằng số dự đoán** — một dòng, kiểu tham chiếu (`Boolean`, `Integer`, `String`, `Compiles`, `Complexity`, hoặc enum khai báo trong file):
   ```java
   static final Integer Q10_SIZE_AFTER_MUTATION = 1; // SOLUTION-VALUE
   ```
   → skeleton: `static final Integer Q10_SIZE_AFTER_MUTATION = null;`
4. **Đáp án trong khối comment** `ANSWER Qn` / `OBSERVATION Qn` ở cuối file — dùng đánh dấu trần:
   ```java
   /* ANSWER Q9:
    * SOLUTION-BEGIN
    * ...đáp án ngắn 2–6 dòng...
    * SOLUTION-END
    */
   ```
   → skeleton: dòng ` *` trống.

Quy tắc: không lồng khối; mọi `SOLUTION-BEGIN` có `SOLUTION-END`; chỉ đánh dấu trong `src/main/java` (file test **không bao giờ** chứa đánh dấu). Test chỉ được tham chiếu khai báo nằm **ngoài** khối đánh dấu.

### B. Javadoc đầu file (theo spec mục 5.2)

```text
/**
 * <Domain> — Bài N: <chủ đề con>
 *
 * Nguồn: 01-java-core-advanced.md, mục <số> (<tên>), câu <danh sách>.
 * Cần làm trước: <file/khái niệm, hoặc "không">.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong <Tên>Test bằng nút ▶
 * cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Qn [NHÃN] <câu hỏi nguyên văn từ .md>
 *   Bắt đầu   : <bước đầu tiên cụ thể trong IDE>
 *   Kiểm chứng: <breakpoint/phím tắt/thao tác để tự thấy đáp án>   (hoặc "Tra cứu")
 *   Code      : <phần phải cài đặt, nếu có>
 *   Hoàn thành khi: <test nào xanh> + <điều phải giải thích được bằng lời>.
 *
 * Qm [NHÃN] ...
 */
```

- Nhãn: `[CODE]`, `[DỰ ĐOÁN]`, `[THÍ NGHIỆM]`, `[TỰ TRẢ LỜI]`, có thể ghép `[DỰ ĐOÁN + CODE]`.
- Dòng câu hỏi phải khớp regex `^\s*\*\s*Q(\d+)\s+\[` và chỉ xuất hiện trong Javadoc đầu file (Javadoc của method không bắt đầu dòng bằng `Qn [`).
- Gợi ý phải **hành động được trong IntelliJ**: `Ctrl+N` mở class, `Ctrl+F12` danh sách method, `Ctrl+B`/`Ctrl+Click` đi tới source JDK, `Ctrl+Q` xem Javadoc, `F7` Step Into, `Alt+F8` Evaluate Expression, `Ctrl+Shift+F10` chạy test, bỏ comment đoạn code mẫu để xem lỗi đỏ, chạy `javap -c -p` trên `target/classes` trong Terminal (`Alt+F12`). Nêu **tên method JDK cụ thể** khi bảo đặt breakpoint.
- Câu `[DỰ ĐOÁN]` dạng "có biên dịch được không": để đoạn code mẫu trong comment ngay cạnh hằng số, hướng dẫn "bỏ comment, xem lỗi đỏ, comment lại".
- Thứ tự câu trong file có thể khác `.md` khi câu sau là nền cho câu trước.

### C. Thân file

- Class mẫu cần quan sát: nested `static` class/record trong file bài tập.
- Hằng số dự đoán: `static final <Kiểu> Qn_<MÔ_TẢ> = <đáp án>; // SOLUTION-VALUE`, đặt gần câu tương ứng, kèm comment 1 dòng mô tả kịch bản.
- Method của ứng viên: package-private, `static` khi không cần state; Javadoc method mô tả hợp đồng (đầu vào, đầu ra, ngoại lệ).
- `[THÍ NGHIỆM]`: code thí nghiệm **cho sẵn** (không đánh dấu) dạng `static String runExperiment(int n)` trả về báo cáo văn bản + `public static void main(String[] args)` in `runExperiment(<n lớn>)`. Đo thô bằng `System.nanoTime()` có vòng warm-up; ghi chú rằng Giai đoạn 2 học đo đúng cách (JMH).
- Cuối file: một khối comment cho mỗi câu `[TỰ TRẢ LỜI]` (`ANSWER Qn`) và `[THÍ NGHIỆM]` (`OBSERVATION Qn`), nội dung lời giải bọc đánh dấu trần. Đáp án ngắn, đúng, có nêu trade-off.

### D. Test

- Mỗi file bài tập có đúng một test class, `@TestMethodOrder(MethodOrderer.MethodName.class)`, package-private.
- Tên method `qNN_<mô tả>` (hai chữ số: `q01_…`, `q10_…`), mỗi test có `@DisplayName("Qn …")` tiếng Việt.
- Dự đoán: dùng `phase01.support.Predictions.assertPrediction(tênHằng, giáTrịThựcTế, Ex.Qn_…, gợiÝ)`; `giáTrịThựcTế` **tính bằng cách chạy code** trong test khi có thể.
- Test `[THÍ NGHIỆM]` chỉ là smoke test: tên kết thúc bằng `experimentRuns` (ví dụ `q05_experimentRuns`), gọi `runExperiment(<n nhỏ>)` và assert kết quả không rỗng. Đây là loại test duy nhất được phép xanh trên skeleton.
- Mọi test khác phải chạm vào phần của ứng viên (hàm TODO hoặc hằng số dự đoán) để **đỏ trên skeleton** với lý do `TODO Qn` hoặc "Chưa điền dự đoán".
- Kiểm tra ngoại lệ trên kết quả: gọi hàm ứng viên trước, ngoài lambda:
  ```java
  List<String> result = Ex02_DefensiveCopy.copyOf(input);
  assertThrows(UnsupportedOperationException.class, () -> result.add("x"));
  ```
- File I/O dùng `@TempDir`; luôn `StandardCharsets.UTF_8`.
- Không `Thread.sleep` trong test; test đa luồng chỉ khi kết quả tất định (ví dụ collection đồng bộ phải có đúng N phần tử).

### E. Cổng hoàn thành của mỗi task domain

1. `.\mvnw.cmd -q -pl phase-01-core-advanced test "-Dtest=phase01/<package>/*Test"` → BUILD SUCCESS, 0 failure.
2. `.\tools\verify-skeleton.ps1 -Module phase-01-core-advanced -Package <package> -ExpectedQuestions <N>` → in `OK` và exit code 0.
3. Commit (chỉ file của package mình): `git add phase-01-core-advanced/src/main/java/phase01/<package> phase-01-core-advanced/src/test/java/phase01/<package>` rồi `git commit -m "feat(phase01): add <package> exercises"`.

---

### Task 1: Khung project, công cụ StripSolutions/verify-skeleton và bài mẫu d04 Ex04

**Files:**
- Create: `.gitignore`
- Create: `pom.xml`
- Create: `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties` (sinh bằng wrapper plugin)
- Create: `tools/pom.xml`
- Create: `tools/src/main/java/javaroadmap/tools/StripSolutions.java`
- Create: `tools/src/test/java/javaroadmap/tools/StripSolutionsTest.java`
- Create: `tools/verify-skeleton.ps1`
- Create: `phase-01-core-advanced/pom.xml`
- Create: `phase-01-core-advanced/src/main/java/phase01/support/Compiles.java`
- Create: `phase-01-core-advanced/src/main/java/phase01/support/Complexity.java`
- Create: `phase-01-core-advanced/src/test/java/phase01/support/Predictions.java`
- Create: `phase-01-core-advanced/src/main/java/phase01/d04_hashmap/Ex04_MutableKey.java`
- Create: `phase-01-core-advanced/src/test/java/phase01/d04_hashmap/Ex04_MutableKeyTest.java`

**Interfaces:**
- Produces: `phase01.support.Compiles { YES, NO }`, `phase01.support.Complexity { O_1, O_LOG_N, O_N, O_N_LOG_N }`, `phase01.support.Predictions.assertPrediction(String constantName, T actual, T predicted, String hint)` (test scope), `javaroadmap.tools.StripSolutions.strip(String) : String`, script `tools/verify-skeleton.ps1 -Module <m> [-Package <p>] [-ExpectedQuestions <n>]`, lệnh `.\mvnw.cmd`.

- [ ] **Step 1: `.gitignore`**

```gitignore
target/
.idea/
*.iml
out/
.superpowers/
```

- [ ] **Step 2: Parent `pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>javaroadmap</groupId>
  <artifactId>java-roadmap-practice</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>pom</packaging>
  <name>Java Roadmap Practice</name>

  <modules>
    <module>tools</module>
    <module>phase-01-core-advanced</module>
  </modules>

  <properties>
    <maven.compiler.release>21</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <junit.version>5.11.4</junit.version>
  </properties>

  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.junit</groupId>
        <artifactId>junit-bom</artifactId>
        <version>${junit.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <dependencies>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <pluginManagement>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-clean-plugin</artifactId>
          <version>3.4.0</version>
        </plugin>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-resources-plugin</artifactId>
          <version>3.3.1</version>
        </plugin>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-compiler-plugin</artifactId>
          <version>3.13.0</version>
        </plugin>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-surefire-plugin</artifactId>
          <version>3.5.2</version>
          <configuration>
            <argLine>-Duser.timezone=Pacific/Chatham -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8</argLine>
            <trimStackTrace>false</trimStackTrace>
          </configuration>
        </plugin>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-jar-plugin</artifactId>
          <version>3.4.2</version>
        </plugin>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-install-plugin</artifactId>
          <version>3.1.3</version>
        </plugin>
      </plugins>
    </pluginManagement>
  </build>
</project>
```

- [ ] **Step 3: Module poms**

`tools/pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>javaroadmap</groupId>
    <artifactId>java-roadmap-practice</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>
  <artifactId>tools</artifactId>
  <name>Tools (dành cho người biên soạn bài)</name>
</project>
```

`phase-01-core-advanced/pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>javaroadmap</groupId>
    <artifactId>java-roadmap-practice</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>
  <artifactId>phase-01-core-advanced</artifactId>
  <name>Giai đoạn 1 — Java Core nâng cao</name>
</project>
```

- [ ] **Step 4: Sinh Maven Wrapper (Maven 3.9.9, kiểu only-script)**

Maven chưa cài trên máy; tải tạm vào `%TEMP%`, sinh wrapper, rồi xóa:

```powershell
$env:JAVA_HOME = "C:\Users\thien\.jdks\jdk-21.0.12.1+1"; $env:Path = "$env:JAVA_HOME\bin;$env:Path"
$zip = "$env:TEMP\apache-maven-3.9.9-bin.zip"
Invoke-WebRequest "https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip" -OutFile $zip
Expand-Archive $zip -DestinationPath $env:TEMP -Force
& "$env:TEMP\apache-maven-3.9.9\bin\mvn.cmd" -N -q org.apache.maven.plugins:maven-wrapper-plugin:3.3.2:wrapper "-Dtype=only-script" "-Dmaven=3.9.9"
Remove-Item $zip; Remove-Item -Recurse "$env:TEMP\apache-maven-3.9.9"
```

Expected: có `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties` ở gốc repo.

- [ ] **Step 5: Test cho StripSolutions (viết trước)**

`tools/src/test/java/javaroadmap/tools/StripSolutionsTest.java`:

```java
package javaroadmap.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class StripSolutionsTest {

    @Test
    void replacesThrowBlockKeepingIndentation() {
        String source = String.join("\n",
                "    int f() {",
                "        // SOLUTION-BEGIN throw Q7",
                "        return 42;",
                "        // SOLUTION-END",
                "    }");
        String expected = String.join("\n",
                "    int f() {",
                "        throw new UnsupportedOperationException(\"TODO Q7\");",
                "    }");
        assertEquals(expected, StripSolutions.strip(source));
    }

    @Test
    void acceptsStepLabelsForCapstone() {
        String source = String.join("\n",
                "        // SOLUTION-BEGIN throw B2",
                "        return parse(file);",
                "        // SOLUTION-END");
        assertEquals("        throw new UnsupportedOperationException(\"TODO B2\");", StripSolutions.strip(source));
    }

    @Test
    void replacesTodoBlockWithComment() {
        String source = String.join("\n",
                "        // SOLUTION-BEGIN todo Q3",
                "        list.add(1);",
                "        // SOLUTION-END");
        assertEquals("        // TODO Q3: viết code của bạn ở đây", StripSolutions.strip(source));
    }

    @Test
    void replacesBareBlockInsideCommentWithEmptyCommentLine() {
        String source = String.join("\n",
                "/* ANSWER Q9:",
                " * SOLUTION-BEGIN",
                " * Không nên.",
                " * SOLUTION-END",
                " */");
        assertEquals(String.join("\n", "/* ANSWER Q9:", " *", " */"), StripSolutions.strip(source));
    }

    @Test
    void replacesPredictionValueWithNull() {
        String source = "    static final Integer Q10_SIZE = 1; // SOLUTION-VALUE";
        assertEquals("    static final Integer Q10_SIZE = null;", StripSolutions.strip(source));
    }

    @Test
    void predictionValueMayContainEqualsSigns() {
        String source = "    static final Boolean Q1_X = 1 == 1; // SOLUTION-VALUE";
        assertEquals("    static final Boolean Q1_X = null;", StripSolutions.strip(source));
    }

    @Test
    void keepsCrlfLineEndingsAndTrailingNewline() {
        String source = "a\r\n// SOLUTION-BEGIN throw Q1\r\nb\r\n// SOLUTION-END\r\nc\r\n";
        assertEquals("a\r\nthrow new UnsupportedOperationException(\"TODO Q1\");\r\nc\r\n",
                StripSolutions.strip(source));
    }

    @Test
    void leavesUnmarkedSourceUntouched() {
        String source = "class A {\n    int x = 1;\n}\n";
        assertEquals(source, StripSolutions.strip(source));
    }

    @Test
    void rejectsNestedBegin() {
        String source = "// SOLUTION-BEGIN throw Q1\n// SOLUTION-BEGIN throw Q2\n// SOLUTION-END\n";
        assertThrows(IllegalArgumentException.class, () -> StripSolutions.strip(source));
    }

    @Test
    void rejectsUnclosedBegin() {
        assertThrows(IllegalArgumentException.class,
                () -> StripSolutions.strip("// SOLUTION-BEGIN throw Q1\nreturn 1;\n"));
    }

    @Test
    void rejectsEndWithoutBegin() {
        assertThrows(IllegalArgumentException.class, () -> StripSolutions.strip("x\n// SOLUTION-END\n"));
    }
}
```

- [ ] **Step 6: Chạy test, xác nhận đỏ**

Run: `.\mvnw.cmd -q -pl tools test`
Expected: FAIL — biên dịch lỗi vì chưa có `StripSolutions`.

- [ ] **Step 7: Cài đặt StripSolutions**

`tools/src/main/java/javaroadmap/tools/StripSolutions.java`:

```java
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
```

- [ ] **Step 8: Chạy test, xác nhận xanh**

Run: `.\mvnw.cmd -q -pl tools test`
Expected: BUILD SUCCESS, 11 tests pass.

- [ ] **Step 9: Support classes**

`phase-01-core-advanced/src/main/java/phase01/support/Compiles.java`:

```java
package phase01.support;

/** Đáp án cho câu dự đoán dạng "đoạn code này có biên dịch được không?". */
public enum Compiles {
    YES,
    NO
}
```

`phase-01-core-advanced/src/main/java/phase01/support/Complexity.java`:

```java
package phase01.support;

/** Đáp án cho câu dự đoán về độ phức tạp thời gian. */
public enum Complexity {
    O_1,
    O_LOG_N,
    O_N,
    O_N_LOG_N
}
```

`phase-01-core-advanced/src/test/java/phase01/support/Predictions.java`:

```java
package phase01.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/** So sánh dự đoán của ứng viên với giá trị thực tế, kèm gợi ý khi sai. */
public final class Predictions {

    private Predictions() {
    }

    public static <T> void assertPrediction(String constantName, T actual, T predicted, String hint) {
        assertNotNull(predicted, () -> "Chưa điền dự đoán " + constantName
                + ": thay null bằng giá trị bạn nghĩ là đúng.");
        assertEquals(actual, predicted, () -> constantName + ": bạn dự đoán " + predicted
                + " nhưng thực tế là " + actual + ". Gợi ý: " + hint);
    }
}
```

- [ ] **Step 10: Bài mẫu d04 Ex04 (bản lời giải có đánh dấu)**

`phase-01-core-advanced/src/main/java/phase01/d04_hashmap/Ex04_MutableKey.java`:

```java
package phase01.d04_hashmap;

import java.util.Map;
import java.util.Objects;

/**
 * HashMap — Bài 4: Key mutable
 *
 * Nguồn: 01-java-core-advanced.md, mục 4 (HashMap), câu 9–10.
 * Cần làm trước: Ex01_LookupAndCollision (luồng hashCode → bucket → equals).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex04_MutableKeyTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q10 [DỰ ĐOÁN + CODE] Điều gì xảy ra nếu field tham gia hashCode() bị thay đổi
 *     sau khi object đã được put?
 *   Bắt đầu   : đọc class MutableKey bên dưới, điền 4 hằng số Q10_* (thay null).
 *   Kiểm chứng: chạy q10_prediction. Nếu sai, đặt breakpoint trong HashMap.getNode
 *               (Ctrl+N → HashMap → Ctrl+F12 → getNode), Debug test, dùng F7 để thấy
 *               hash mới trỏ vào bucket nào và vì sao entry cũ không được tìm thấy.
 *   Code      : cài đặt changeIdSafely() để đổi id mà map vẫn tìm được value.
 *   Hoàn thành khi: các test q10_* xanh; giải thích được vì sao size() vẫn là 1
 *               nhưng get() trả null.
 *
 * Q9 [TỰ TRẢ LỜI] Mutable object có nên được dùng làm HashMap key không?
 *   Bắt đầu   : làm Q10 trước, dùng chính kết quả quan sát được để trả lời.
 *   Tra cứu   : Javadoc java.util.Map, đoạn "Great care must be exercised if mutable
 *               objects are used as map keys" (đặt con trỏ lên chữ Map, Ctrl+Q).
 *   Hoàn thành khi: viết xong khối ANSWER Q9, có nêu ít nhất một cách phòng tránh.
 */
public class Ex04_MutableKey {

    static final class MutableKey {
        int id;

        MutableKey(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof MutableKey k && k.id == id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "MutableKey[id=" + id + "]";
        }
    }

    // Q10 — kịch bản: map.put(new MutableKey(1), "A"); rồi key.id = 2
    static final Boolean Q10_GET_BY_SAME_REFERENCE_FINDS_VALUE = false; // SOLUTION-VALUE
    static final Boolean Q10_GET_BY_NEW_KEY_1_FINDS_VALUE = false; // SOLUTION-VALUE
    static final Boolean Q10_GET_BY_NEW_KEY_2_FINDS_VALUE = false; // SOLUTION-VALUE
    static final Integer Q10_SIZE_AFTER_MUTATION = 1; // SOLUTION-VALUE

    /**
     * Đổi id của {@code key} sao cho sau đó {@code map.get(key)} vẫn trả về value cũ.
     *
     * @throws IllegalArgumentException nếu {@code key} hiện không tìm thấy trong map (map giữ nguyên)
     */
    static <V> void changeIdSafely(Map<MutableKey, V> map, MutableKey key, int newId) {
        // SOLUTION-BEGIN throw Q10
        if (!map.containsKey(key)) {
            throw new IllegalArgumentException("Key không có trong map: " + key);
        }
        V value = map.remove(key);
        key.id = newId;
        map.put(key, value);
        // SOLUTION-END
    }
}

/* ANSWER Q9:
 * SOLUTION-BEGIN
 * Không nên, trừ khi các field tham gia equals/hashCode không bao giờ đổi sau khi put.
 * Khi field đổi, hash mới trỏ sang bucket khác nên get/containsKey/remove không tìm thấy
 * entry cũ, trong khi entry cũ vẫn nằm trong map (size không giảm) — dữ liệu "ma", rò rỉ bộ nhớ.
 * Phòng tránh: dùng key bất biến (record, String, id final); nếu buộc phải đổi thì
 * remove → đổi → put lại như changeIdSafely().
 * SOLUTION-END
 */
```

`phase-01-core-advanced/src/test/java/phase01/d04_hashmap/Ex04_MutableKeyTest.java`:

```java
package phase01.d04_hashmap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase01.support.Predictions.assertPrediction;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d04_hashmap.Ex04_MutableKey.MutableKey;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex04_MutableKeyTest {

    private static final String HINT_Q10 =
            "hashCode() mới trỏ sang bucket khác; đặt breakpoint trong HashMap.getNode để xem bucket được tìm.";

    @Test
    @DisplayName("Q10 dự đoán: key bị đổi id sau khi put")
    void q10_prediction() {
        Map<MutableKey, String> map = new HashMap<>();
        MutableKey key = new MutableKey(1);
        map.put(key, "A");
        key.id = 2;

        assertPrediction("Q10_GET_BY_SAME_REFERENCE_FINDS_VALUE",
                map.get(key) != null, Ex04_MutableKey.Q10_GET_BY_SAME_REFERENCE_FINDS_VALUE, HINT_Q10);
        assertPrediction("Q10_GET_BY_NEW_KEY_1_FINDS_VALUE",
                map.get(new MutableKey(1)) != null, Ex04_MutableKey.Q10_GET_BY_NEW_KEY_1_FINDS_VALUE, HINT_Q10);
        assertPrediction("Q10_GET_BY_NEW_KEY_2_FINDS_VALUE",
                map.get(new MutableKey(2)) != null, Ex04_MutableKey.Q10_GET_BY_NEW_KEY_2_FINDS_VALUE, HINT_Q10);
        assertPrediction("Q10_SIZE_AFTER_MUTATION",
                map.size(), Ex04_MutableKey.Q10_SIZE_AFTER_MUTATION,
                "Entry cũ không bị xóa khi field của key đổi.");
    }

    @Test
    @DisplayName("Q10 changeIdSafely: sau khi đổi id vẫn tìm được value")
    void q10_changeIdSafely_keepsValueReachable() {
        Map<MutableKey, String> map = new HashMap<>();
        MutableKey key = new MutableKey(1);
        map.put(key, "A");

        Ex04_MutableKey.changeIdSafely(map, key, 2);

        assertEquals(2, key.id, "Id của key phải được đổi thành 2.");
        assertEquals("A", map.get(key), "Tìm bằng chính key sau khi đổi phải ra value cũ.");
        assertEquals("A", map.get(new MutableKey(2)), "Tìm bằng key mới id=2 phải ra value cũ.");
        assertEquals(1, map.size(), "Map chỉ được có đúng 1 entry.");
    }

    @Test
    @DisplayName("Q10 changeIdSafely: key không có trong map thì báo lỗi và không đổi map")
    void q10_changeIdSafely_rejectsMissingKey() {
        Map<MutableKey, String> map = new HashMap<>();
        map.put(new MutableKey(1), "A");
        MutableKey missing = new MutableKey(99);

        assertThrows(IllegalArgumentException.class, () -> Ex04_MutableKey.changeIdSafely(map, missing, 5));
        assertEquals(99, missing.id, "Không được đổi id khi key không có trong map.");
        assertEquals(Map.of(new MutableKey(1), "A"), map, "Map phải giữ nguyên.");
    }
}
```

- [ ] **Step 11: Script `tools/verify-skeleton.ps1`** (chỉ dùng ký tự ASCII trong script)

```powershell
<#
.SYNOPSIS
  Sinh ban khung (skeleton) tu ban loi giai vao thu muc tam va kiem tra quy tac skeleton.
.DESCRIPTION
  1. Sao chep repo (tru .git, target, .idea, .superpowers) vao thu muc tam.
  2. Chay StripSolutions tren <Module>/src/main/java cua ban sao.
  3. Kiem tra khong con chu "SOLUTION-" trong src/main/java.
  4. Chay test (tuy chon loc theo package). Skeleton phai bien dich duoc.
  5. Moi test phai DO voi ly do "TODO Qn"/"TODO Bn" hoac "thay null" (chua dien du doan),
     tru test co ten chua "experimentRuns" (duoc phep xanh).
  6. Neu co -ExpectedQuestions: tap so Qn trong Javadoc cua package phai dung bang 1..N.
.EXAMPLE
  ./tools/verify-skeleton.ps1 -Module phase-01-core-advanced -Package d04_hashmap -ExpectedQuestions 15
#>
param(
    [Parameter(Mandatory = $true)] [string] $Module,
    [string] $Package = '',
    [int] $ExpectedQuestions = 0
)

$ErrorActionPreference = 'Stop'
$repo = Split-Path -Parent $PSScriptRoot
if (-not $env:JAVA_HOME) { throw 'JAVA_HOME is not set.' }
$java = Join-Path $env:JAVA_HOME 'bin\java.exe'
$mainJava = Join-Path $repo "$Module\src\main\java"
$basePackage = (Get-ChildItem $mainJava -Directory | Select-Object -First 1).Name
$violations = New-Object System.Collections.Generic.List[string]

if ($Package -and $ExpectedQuestions -gt 0) {
    $dir = Join-Path $mainJava "$basePackage\$Package"
    $nums = @(Get-ChildItem $dir -Filter *.java |
            Select-String -Pattern '^\s*\*\s*Q(\d+)\s+\[' |
            ForEach-Object { [int]$_.Matches[0].Groups[1].Value })
    foreach ($dup in ($nums | Group-Object | Where-Object Count -gt 1)) {
        $violations.Add("Question Q$($dup.Name) appears $($dup.Count) times in $Package")
    }
    foreach ($n in 1..$ExpectedQuestions) {
        if ($nums -notcontains $n) { $violations.Add("Question Q$n missing in $Package") }
    }
    foreach ($n in ($nums | Where-Object { $_ -lt 1 -or $_ -gt $ExpectedQuestions })) {
        $violations.Add("Question Q$n out of range 1..$ExpectedQuestions in $Package")
    }
}

$tmp = Join-Path $env:TEMP ('skeleton-' + [guid]::NewGuid().ToString('N').Substring(0, 8))
New-Item -ItemType Directory $tmp | Out-Null
try {
    robocopy $repo $tmp /E /XD .git target .idea .superpowers /XF .git *.iml /NFL /NDL /NJH /NJS /NP | Out-Null
    if ($LASTEXITCODE -ge 8) { throw "robocopy failed with exit code $LASTEXITCODE" }

    & $java (Join-Path $tmp 'tools\src\main\java\javaroadmap\tools\StripSolutions.java') (Join-Path $tmp "$Module\src\main\java")
    if ($LASTEXITCODE -ne 0) { throw 'StripSolutions failed.' }

    $left = Get-ChildItem (Join-Path $tmp "$Module\src\main\java") -Recurse -Filter *.java |
            Select-String -Pattern 'SOLUTION-' -SimpleMatch
    foreach ($hit in $left) { $violations.Add("Marker left after strip: $($hit.Path):$($hit.LineNumber)") }

    $mvnArgs = @('-q', '-pl', $Module, 'test', '-Dmaven.test.failure.ignore=true', '-Dsurefire.failIfNoSpecifiedTests=false')
    if ($Package) { $mvnArgs += "-Dtest=$basePackage/$Package/*Test" }
    Push-Location $tmp
    try {
        & .\mvnw.cmd @mvnArgs | Out-Host
        $mvnExit = $LASTEXITCODE
    } finally {
        Pop-Location
    }
    if ($mvnExit -ne 0) { throw "Maven failed (exit $mvnExit): skeleton does not compile or build error." }

    $reports = @(Get-ChildItem (Join-Path $tmp "$Module\target\surefire-reports") -Filter 'TEST-*.xml' -ErrorAction SilentlyContinue)
    if ($reports.Count -eq 0) { throw 'No surefire reports: no test was run.' }
    $total = 0
    foreach ($report in $reports) {
        [xml]$xml = Get-Content $report.FullName -Raw -Encoding UTF8
        foreach ($case in $xml.testsuite.testcase) {
            $total++
            $name = "$($case.classname)#$($case.name)"
            $problems = @($case.failure) + @($case.error) | Where-Object { $_ }
            if (-not $problems) {
                if ($case.name -notmatch 'experimentRuns') { $violations.Add("GREEN on skeleton (must be red): $name") }
                continue
            }
            $text = ($problems | ForEach-Object { "$($_.message) $($_.InnerText)" }) -join ' '
            if ($text -notmatch 'TODO [A-Z]+\d+|thay null') {
                $violations.Add("RED for wrong reason: $name -> $($problems[0].message)")
            }
        }
    }
} finally {
    Remove-Item -Recurse -Force $tmp -ErrorAction SilentlyContinue
}

if ($violations.Count -gt 0) {
    $violations | ForEach-Object { Write-Host "VIOLATION: $_" }
    exit 1
}
Write-Host "OK: $total test(s) checked, skeleton rules satisfied."
exit 0
```

- [ ] **Step 12: Chạy toàn bộ lời giải — xanh**

Run: `.\mvnw.cmd -q test`
Expected: BUILD SUCCESS; `tools` 11 tests, `phase-01-core-advanced` 3 tests pass.

- [ ] **Step 13: Chạy verify-skeleton trên bài mẫu**

Run: `.\tools\verify-skeleton.ps1 -Module phase-01-core-advanced -Package d04_hashmap`
Expected: `OK: 3 test(s) checked, skeleton rules satisfied.` (không truyền `-ExpectedQuestions` vì d04 mới có câu 9–10).

Kiểm tra thêm bằng mắt: tạo bản strip tạm và xem `Ex04_MutableKey.java` sau strip có `= null;` ở 4 hằng số, thân `changeIdSafely` chỉ còn `throw new UnsupportedOperationException("TODO Q10");`, khối `ANSWER Q9` chỉ còn ` *`.

- [ ] **Step 14: Commit**

```powershell
git add .gitignore pom.xml mvnw mvnw.cmd .mvn tools phase-01-core-advanced
git commit -m "build: scaffold maven project, solution stripper and reference exercise"
```

---

## Quy ước chung cho Task 2–18

Mỗi task domain:
- **Files:** tạo các file `src/main/java/phase01/<package>/ExNN_*.java` và test tương ứng `src/test/java/phase01/<package>/ExNN_*Test.java` như liệt kê. Không sửa file ngoài package của mình.
- **Interfaces:** Consumes `phase01.support.Compiles`, `phase01.support.Complexity`, `phase01.support.Predictions.assertPrediction` (Task 1). Không phụ thuộc package domain khác. Produces: không có gì cho task khác.
- **Các bước (giống nhau cho mọi task domain):**
  - [ ] Step 1: Viết test class cho từng file theo mục "Test" (đọc Hướng dẫn biên soạn mục D).
  - [ ] Step 2: Viết file bài tập bản lời giải, có Javadoc đầu file (mục B), đánh dấu (mục A), khối ANSWER/OBSERVATION (mục C).
  - [ ] Step 3: Chạy test của package → xanh (cổng E.1).
  - [ ] Step 4: Chạy `verify-skeleton.ps1` với `-ExpectedQuestions` của task → `OK` (cổng E.2).
  - [ ] Step 5: Commit (cổng E.3).

Trong đặc tả dưới đây: "dự đoán" = hằng số `SOLUTION-VALUE` (tên, kiểu, đáp án); "thực tế" = cách test tính giá trị để so; "code" = method có thân `SOLUTION-BEGIN throw Qn`; "cho sẵn" = code không đánh dấu. "Gợi ý chính" = điểm bắt buộc có trong Javadoc của câu; bạn viết thêm cho đủ 4 phần.

---

### Task 2: d01_generics (11 câu)

**Package:** `d01_generics` — `-ExpectedQuestions 11`

**File `Ex01_InvarianceAndWildcards`** (câu 1, 9, 2, 3, 4 — theo thứ tự này)
- Q1 [DỰ ĐOÁN] `List<String>` có phải subtype của `List<Object>`?
  - Dự đoán: `Compiles Q1_LIST_STRING_TO_LIST_OBJECT_COMPILES = Compiles.NO` với mẫu comment `// List<Object> objects = new ArrayList<String>();`; `Compiles Q1_STRING_ARRAY_TO_OBJECT_ARRAY_COMPILES = Compiles.YES` với mẫu `// Object[] objects = new String[1];`; `String Q1_ARRAY_STORE_EXCEPTION = "ArrayStoreException"`.
  - Thực tế Q1_ARRAY_STORE_EXCEPTION: test chạy `Object[] arr = new String[1]; arr[0] = 1;` bắt `RuntimeException`, lấy `getClass().getSimpleName()`. Hai hằng `Compiles` so với đáp án cố định.
  - Gợi ý chính: bỏ comment từng dòng mẫu để xem lỗi đỏ; so sánh mảng covariant (lỗi lúc chạy) với generic invariant (lỗi lúc biên dịch).
- Q9 [TỰ TRẢ LỜI] Generic invariance là gì? — ANSWER Q9 dùng kết quả Q1.
- Q2 [CODE] Khi nào dùng `? extends T`? — `static double sum(Collection<? extends Number> numbers)` trả tổng `doubleValue()`; rỗng → `0.0`.
  - Test: `sum(List.of(1, 2, 3)) == 6.0`; `sum(List.of(1.5, 2.5)) == 4.0`; `sum(List.of()) == 0.0`.
- Q3 [CODE] Khi nào dùng `? super T`? — `static void addNumbers(List<? super Integer> target, int count)` thêm `0..count-1`; `count < 0` → `IllegalArgumentException`.
  - Test: với `List<Number>` và `List<Object>` đều nhận `[0, 1, 2]` khi `count = 3`; `count = -1` → IAE.
- Q4 [CODE] PECS bằng ví dụ thực tế — `static <T> void copy(List<? super T> dst, List<? extends T> src)` nối toàn bộ `src` vào cuối `dst`; `static <T> T max(Collection<? extends T> items, Comparator<? super T> comparator)` trả phần tử lớn nhất; rỗng → `NoSuchElementException`.
  - Test: copy `List<Integer> [1,2]` vào `List<Number> [0.5]` → `[0.5, 1, 2]`; `max(List.of(3, 7, 5), Comparator<Number> theo doubleValue) == 7`; `max(List.of(), ...)` → NoSuchElementException.
  - Gợi ý chính: `Ctrl+N` → `Collections` → `Ctrl+F12` → `copy`, đọc chữ ký của JDK.

**File `Ex02_TypeErasure`** (câu 5, 6, 10, 7, 11)
- Q5 [DỰ ĐOÁN + CODE] Tại sao không viết được `T value = new T();`?
  - Dự đoán: `Compiles Q5_NEW_T_COMPILES = Compiles.NO` (mẫu comment trong một generic method).
  - Code: `static <T> List<T> createN(Supplier<? extends T> factory, int n)` gọi factory `n` lần (`n < 0` → IAE); `static <T> T newInstance(Class<T> type)` gọi constructor không tham số qua `type.getDeclaredConstructor().newInstance()`, bọc `ReflectiveOperationException` thành `IllegalArgumentException` giữ nguyên cause.
  - Test: `createN(StringBuilder::new, 3)` có 3 phần tử, đôi một khác instance (`assertNotSame`); `newInstance(ArrayList.class)` là `ArrayList` rỗng; `newInstance(Integer.class)` → IAE có `getCause()` là `NoSuchMethodException`.
- Q6 [DỰ ĐOÁN] Type Erasure là gì? — `Boolean Q6_SAME_RUNTIME_CLASS = true`; thực tế: `new ArrayList<String>().getClass() == new ArrayList<Integer>().getClass()`.
  - Gợi ý chính: build, mở Terminal chạy `javap -c -p target/classes/phase01/d01_generics/Ex02_TypeErasure.class`, tìm `checkcast` sau lời gọi `get`.
- Q10 [DỰ ĐOÁN + TỰ TRẢ LỜI] Tại sao generic giúp type safety nhưng không hoàn toàn tồn tại ở runtime?
  - Khai báo trong file: `enum FailurePoint { ADD, GET }`; dự đoán `FailurePoint Q10_HEAP_POLLUTION_FAILS_AT = FailurePoint.GET`.
  - Thực tế: test thực hiện `List<String> strings = new ArrayList<>(List.of("a")); List raw = strings; raw.add(42);` (không lỗi) rồi `String s = strings.get(1);` (ném `ClassCastException`) — test ghi nhận bước ném lỗi đầu tiên (ADD hoặc GET). Dùng `@SuppressWarnings({"rawtypes", "unchecked"})` ở method test.
  - ANSWER Q10.
- Q7 [DỰ ĐOÁN + CODE] Tại sao Java không cho phép `if (obj instanceof List<String>)`?
  - Dự đoán: `Compiles Q7_INSTANCEOF_LIST_STRING_COMPILES = Compiles.NO` (mẫu: `Object obj = ...; // if (obj instanceof List<String>) {}`).
  - Code: `static boolean isListOfStrings(Object obj)` → `true` nếu `obj` là `List<?>` và mọi phần tử là `String` (list rỗng → `true`; phần tử `null` → `false`).
  - Test: `List.of("a","b")` → true; `List.of("a", 1)` → false; `List.of()` → true; `"abc"` → false; `Arrays.asList("a", null)` → false.
- Q11 [TỰ TRẢ LỜI] Vì sao `List<String>` và `List<Integer>` không phân biệt được bằng `instanceof` tại runtime? — ANSWER Q11 liên hệ Q6.

**File `Ex03_GenericMethodVsWildcard`** (câu 8)
- Q8 [DỰ ĐOÁN + CODE] So sánh `<T> void process(List<T> list)` và `void process(List<?> list)`.
  - Dự đoán: `Compiles Q8_ADD_STRING_TO_WILDCARD_LIST_COMPILES = Compiles.NO` (mẫu `// list.add("x");` với `List<?> list`); `Compiles Q8_ADD_NULL_TO_WILDCARD_LIST_COMPILES = Compiles.YES` (mẫu `// list.add(null);`).
  - Code: `static void swapFirstLast(List<?> list)` đổi chỗ phần tử đầu và cuối; bắt buộc gọi helper private `private static <T> void swapHelper(List<T> list)` (wildcard capture). List có < 2 phần tử giữ nguyên.
  - Test: `[1,2,3]` → `[3,2,1]`; `["a"]` giữ nguyên; `[]` giữ nguyên (dùng `ArrayList` có thể sửa).
  - ANSWER Q8: khi nào chọn type parameter (cần liên hệ giữa các tham số/giá trị trả về) và khi nào wildcard (chỉ đọc, không cần tên kiểu).

---

### Task 3: d02_arraylist (10 câu)

**Package:** `d02_arraylist` — `-ExpectedQuestions 10`

**File `Ex01_CapacityAndResize`** (câu 1, 4, 3, 2, 6)
- Cho sẵn khung `static final class MiniArrayList<E>` với field `Object[] elementData`, `int size`, `int resizeCount`, constructor `MiniArrayList()` (capacity 10) và `MiniArrayList(int initialCapacity)` (`< 0` → IAE), method cho sẵn `size()`, `capacity()` (= `elementData.length`), `resizeCount()`.
- Code (mỗi method một khối `throw`, gắn số câu ghi bên cạnh):
  - `E get(int index)` — Q1; ngoài `[0, size)` → `IndexOutOfBoundsException`.
  - `void add(E element)` — Q2; khi đầy gọi `grow()`.
  - `private void grow()` — Q3; capacity mới = `max(old + (old >> 1), old + 1)`; tăng `resizeCount`; dùng `Arrays.copyOf`.
  - `void add(int index, E element)` — Q2; chèn và dời phần tử (`System.arraycopy`); `index` ngoài `[0, size]` → IOOBE.
  - `E remove(int index)` — Q2; dời phần tử về trái, gán `null` ô cuối, trả phần tử bị xóa.
- Dự đoán: `Integer Q3_RESIZES_FOR_100_ADDS = 6` (10→15→22→33→49→73→109); `Integer Q6_RESIZES_WITH_INITIAL_CAPACITY_100 = 0`.
  - Thực tế: tạo `MiniArrayList` của ứng viên, add 100 phần tử, đọc `resizeCount()`; đồng thời test code riêng kiểm tra chuỗi capacity.
- Q4 [DỰ ĐOÁN] size vs capacity: `Integer Q4_SIZE_AFTER_11_ADDS = 11`, `Integer Q4_CAPACITY_AFTER_11_ADDS = 15` (thực tế đọc từ MiniArrayList).
- Q1, Q2, Q3, Q6 có ANSWER ngắn. Gợi ý chính: `Ctrl+N` → `ArrayList` → `Ctrl+F12` → `grow` và `get`, đặt breakpoint trong `ArrayList.grow` rồi Debug test Q3 để so công thức.
- Test code: sau 10 lần add capacity 10, `resizeCount` 0; lần 11 → capacity 15, `resizeCount` 1; `get(-1)` và `get(size)` → IOOBE; `add(0, x)` dời phần tử; `remove(1)` trả đúng phần tử, size giảm; `new MiniArrayList<>(0)` add 1 phần tử → capacity 1.

**File `Ex02_InsertRemoveCost`** (câu 5, 10)
- Q5 [THÍ NGHIỆM] Tại sao insert ở đầu `ArrayList` là O(n)?; Q10 [THÍ NGHIỆM] Khi nào ArrayList tốt hơn LinkedList dù phải insert/remove?
- Cho sẵn `static String runExperiment(int n)`: đo thời gian insert `n` phần tử ở đầu/giữa/cuối cho `ArrayList` và `LinkedList` (giữa của `LinkedList` dùng `add(size/2, x)` để thấy chi phí tìm node), và `ArrayDeque.addFirst`; trả bảng văn bản. `main` gọi `runExperiment(50_000)`.
- Test: `q05_experimentRuns` gọi `runExperiment(1_000)`, assert chứa `"ArrayList"` và `"LinkedList"`.
- OBSERVATION Q5, OBSERVATION Q10 (lời giải: số liệu mẫu tương đối + giải thích dời mảng vs tìm node, cache locality).

**File `Ex03_Pitfalls`** (câu 9, 8, 7)
- Q9 [DỰ ĐOÁN + CODE] Iterate rồi remove sai cách:
  - Dự đoán: `String Q9_FOREACH_REMOVE_FIRST_EXCEPTION = "ConcurrentModificationException"` — thực tế: for-each trên `new ArrayList<>(List.of("a","b","c"))`, remove `"a"` khi gặp; bắt exception, lấy simple name (không ném → `"none"`).
  - `Boolean Q9_FOREACH_REMOVE_SECOND_LAST_THROWS = false` — thực tế: như trên nhưng remove `"b"`; ghi nhận có ném hay không.
  - Code: `static void removeBlank(List<String> list)` xóa tại chỗ phần tử `null` hoặc `isBlank()` (Iterator.remove hoặc removeIf).
  - Test code: `["a", "", " ", null, "b"]` → `["a", "b"]` (dùng `new ArrayList<>(Arrays.asList(...))`).
  - Gợi ý chính: đặt breakpoint trong `ArrayList.Itr.checkForComodification` và `hasNext`, giải thích vì sao remove "b" không ném.
- Q8 [DỰ ĐOÁN] ArrayList có chứa null không? — `Boolean Q8_CAN_ADD_NULL = true`, `Integer Q8_SIZE_AFTER_TWO_NULLS = 2` (thực tế chạy code).
- Q7 [DỰ ĐOÁN + CODE + THÍ NGHIỆM] ArrayList có thread-safe không?
  - Dự đoán: `Boolean Q7_ARRAYLIST_IS_THREAD_SAFE = false` (đáp án cố định; gợi ý: chạy `main`).
  - Cho sẵn: `static int concurrentAdds(List<Integer> list, int threads, int perThread)` chạy `threads` thread (dùng `ExecutorService` + `CountDownLatch` làm cổng xuất phát), mỗi thread add `perThread` lần, chờ xong, trả `list.size()`; exception trong thread được gom lại và ném lại. `static String runExperiment(int perThread)` so `ArrayList` thường (bắt `ArrayIndexOutOfBoundsException` nếu có) với list của `threadSafeList()`. `main` gọi `runExperiment(100_000)`.
  - Code: `static <T> List<T> threadSafeList()` → `Collections.synchronizedList(new ArrayList<>())`.
  - Test: `concurrentAdds(threadSafeList(), 4, 10_000) == 40_000`; `q07_experimentRuns` gọi `runExperiment(1_000)`.
  - OBSERVATION Q7.

---

### Task 4: d03_linkedlist (6 câu)

**Package:** `d03_linkedlist` — `-ExpectedQuestions 6`

**File `Ex01_NodeTraversal`** (câu 1, 2)
- Cho sẵn `static final class MiniLinkedList<E>` với nested `static final class Node<E> { E item; Node<E> prev; Node<E> next; }`, field `first`, `last`, `size`, `lastTraversalSteps`; method cho sẵn `size()`, `lastTraversalSteps()`, `first()` (trả node đầu), `toList()` duyệt xuôi, `toListBackward()` duyệt ngược.
- Code:
  - `void addLast(E e)` và `void addFirst(E e)` — Q2.
  - `Node<E> nodeAt(int index)` — Q1; nếu `index < (size >> 1)` duyệt từ `first` (bước = `index`), ngược lại từ `last` (bước = `size - 1 - index`); ghi `lastTraversalSteps`; ngoài khoảng → IOOBE.
  - `E get(int index)` — Q1; dùng `nodeAt`.
  - `Node<E> insertAfter(Node<E> node, E e)` — Q2; O(1), nối đủ `prev/next` cả hai chiều, cập nhật `last` khi chèn sau node cuối; trả node mới.
- Dự đoán: `Integer Q1_STEPS_GET_5000_OF_10000 = 4999`, `Integer Q1_STEPS_GET_10_OF_10000 = 10` (thực tế: đọc `lastTraversalSteps()` sau `get` trên MiniLinkedList 10 000 phần tử); `Boolean Q2_INSERT_MIDDLE_O1_INCLUDING_SEARCH = false` (cố định).
- Test code: add 5 phần tử, `toList()` và `toListBackward()` khớp; `insertAfter(nodeAt(1), x)` → thứ tự đúng cả hai chiều; `insertAfter(last)` cập nhật đuôi; `get(9990)` trên list 10 000 → bước 9; `get(10_000)` → IOOBE.
- Gợi ý chính: `Ctrl+N` → `LinkedList` → `Ctrl+F12` → `node(int)`; so sánh với `nodeAt` của bạn.

**File `Ex02_MemoryAndLocality`** (câu 3, 5, 6)
- Q3 [THÍ NGHIỆM + TỰ TRẢ LỜI] LinkedList dùng nhiều memory hơn vì sao?; Q5 [THÍ NGHIỆM] Vì sao trên workload thực tế ArrayList thường nhanh hơn?; Q6 [TỰ TRẢ LỜI] CPU cache locality ảnh hưởng thế nào?
- Cho sẵn `static String runExperiment(int n)`: (a) ước lượng byte/phần tử cho `ArrayList<Integer>` và `LinkedList<Integer>` bằng chênh lệch `Runtime.totalMemory() - freeMemory()` sau `System.gc()` (ghi chú: số thô, chỉ để so sánh tương đối); (b) thời gian duyệt tổng bằng for-each trên `int[]`, `ArrayList`, `LinkedList`.
- Test: `q03_experimentRuns` với `runExperiment(10_000)`, assert không rỗng.
- OBSERVATION Q3, OBSERVATION Q5, ANSWER Q3, ANSWER Q6 (đáp án Q3: mỗi Node là một object có header + 3 tham chiếu, cộng object Integer).

**File `Ex03_WhenLinkedListWins`** (câu 4)
- Q4 [CODE + TỰ TRẢ LỜI] Khi nào LinkedList thực sự có lợi?
- Code: `static void insertAfterEach(List<String> list, Predicate<String> match, String toInsert)` — một lượt duyệt bằng `ListIterator`, chèn `toInsert` ngay sau mỗi phần tử khớp (không xét lại phần tử vừa chèn).
- Test: với cả `LinkedList` và `ArrayList`: `[a, x, b, x]`, match `"x"::equals`, chèn `"y"` → `[a, x, y, b, x, y]`; list rỗng giữ nguyên; `toInsert` khớp predicate (chèn `"x"` sau `"x"`) không lặp vô hạn → `[x]` thành `[x, x]`.
- ANSWER Q4: chèn/xóa khi iterator đã đứng tại vị trí, dùng như deque (nhưng `ArrayDeque` thường tốt hơn), list rất dài chèn nhiều ở giữa khi đã có node.

---

### Task 5: d04_hashmap (15 câu; Ex04 đã có từ Task 1)

**Package:** `d04_hashmap` — `-ExpectedQuestions 15`. Không sửa `Ex04_MutableKey*`.

**File `Ex01_LookupAndCollision`** (câu 1, 2, 3, 4, 5)
- Cho sẵn `static final class MiniHashMap<K, V>` với `static final class Entry<K, V> { final K key; final int hash; V value; Entry<K, V> next; }`, mảng `Entry<K, V>[] table` kích thước cố định 16 (không resize), `int size`, `int lastProbeCount`; method cho sẵn `static int hash(Object key)` (`key == null ? 0 : (h = key.hashCode()) ^ (h >>> 16)`), `static int indexFor(int hash, int length)` (`hash & (length - 1)`), `size()`, `lastProbeCount()`, `int[] bucketSizes()`.
- Code:
  - `V put(K key, V value)` — Q1; nối entry mới vào **cuối** chuỗi bucket; key đã có (so `hash` rồi `Objects.equals`) → thay value, trả value cũ; hỗ trợ `null` key.
  - `V get(Object key)` — Q1; đếm số entry đã duyệt vào `lastProbeCount`.
- Cho sẵn các key mẫu: `record GoodKey(int id)` có override `hashCode()` trả về `id` (để phân bố bucket tất định); `static final class ConstantHashKey { final int id; hashCode() = 42; equals theo id }` (Q3); `static final class HashCodeOnlyKey { final int id; hashCode theo id; KHÔNG override equals }` (Q2); `static final class EqualsOnlyKey { final int id; final int salt; equals theo id; hashCode() = Objects.hash(id, salt) }` với `salt` lấy từ bộ đếm static tăng dần mỗi lần tạo — mô phỏng tất định việc "quên override hashCode" (Q5).
- Dự đoán (thực tế đều chạy trên `java.util.HashMap` trong test):
  - `Boolean Q2_HASHCODE_ONLY_KEY_FOUND_BY_EQUAL_INSTANCE = false` — put `new HashCodeOnlyKey(1)`, get bằng instance mới cùng id.
  - `Integer Q4_SIZE_WITH_TWO_COLLIDING_UNEQUAL_KEYS = 2` — put hai `ConstantHashKey` id khác nhau.
  - `Boolean Q5_EQUALS_ONLY_KEY_FOUND_BY_EQUAL_INSTANCE = false` — put `new EqualsOnlyKey(1)`, get bằng instance mới.
- Test code (trên MiniHashMap của ứng viên): put/get cơ bản; put trùng key trả value cũ và size không đổi; `null` key; 100 `ConstantHashKey` → `bucketSizes()` có đúng một bucket 100 phần tử, get key thứ 100 (instance mới) trả đúng value và `lastProbeCount() == 100`; 100 `GoodKey` → không bucket nào vượt 10 phần tử (tất định với `hash` cho sẵn).
- Q1 [CODE + TỰ TRẢ LỜI], Q3 [TỰ TRẢ LỜI] (dựa vào `lastProbeCount`). Gợi ý chính: đặt breakpoint ở `HashMap.getNode`, dùng Evaluate Expression (`Alt+F8`) xem `tab.length` và `(n - 1) & hash`.

**File `Ex02_EqualsWithoutHashCode`** (câu 6)
- Q6 [DỰ ĐOÁN + CODE] Vì sao phải override `hashCode()` khi override `equals()`?
- Cho sẵn `static final class EqualsOnlyPoint` (equals theo x, y; hashCode = `Objects.hash(x, y, salt)` với salt tăng dần như Ex01, kèm comment giải thích mô phỏng identity hashCode tất định).
- Dự đoán: `Integer Q6_HASHSET_SIZE_TWO_EQUAL_POINTS = 2` (thực tế: add hai `EqualsOnlyPoint(1,2)` vào `HashSet`).
- Code: `static final class Point { final int x; final int y; }` — `equals(Object)` và `hashCode()` đều là khối `throw Q6`.
- Test: `new Point(1,2).equals(new Point(1,2))` true và cùng hashCode; khác x hoặc y → không equal; `equals(null)` false; `equals("x")` false; `HashSet` hai điểm bằng nhau → size 1; `HashMap` get bằng instance mới trả value.

**File `Ex03_ResizeAndTreeify`** (câu 7, 8, 11, 12)
- Q7 [DỰ ĐOÁN + CODE] Load factor dùng để làm gì?
  - Dự đoán: `Integer Q7_DEFAULT_THRESHOLD = 12` (cố định; gợi ý đọc `DEFAULT_INITIAL_CAPACITY`, `DEFAULT_LOAD_FACTOR` trong source HashMap).
  - Code: `static int tableSizeFor(int capacity)` → lũy thừa 2 nhỏ nhất ≥ capacity; `capacity <= 1` → 1; `> 1 << 30` → `1 << 30`. `static int capacityFor(int expectedSize)` → `tableSizeFor((int) Math.ceil(expectedSize / 0.75))`; `expectedSize < 0` → IAE. `static int resizesNeeded(int inserts, int initialTableSize)` → mô phỏng: threshold = `(int) (tableSize * 0.75)`; mỗi lần `size > threshold` sau khi thêm thì gấp đôi bảng; trả số lần gấp đôi (`initialTableSize` phải là lũy thừa 2 > 0, ngược lại IAE).
  - Test: `tableSizeFor`: 0→1, 1→1, 16→16, 17→32, `(1 << 30) + 1` → `1 << 30`; `capacityFor`: 0→1, 12→16, 13→32, 100→256; `capacityFor(-1)` → IAE; `resizesNeeded(12, 16)` = 0, `(13, 16)` = 1, `(100, 16)` = 4, `(100, 256)` = 0; `resizesNeeded(10, 12)` → IAE.
- Q8 [THÍ NGHIỆM] Resize ảnh hưởng performance thế nào? — cho sẵn `static String runExperiment(int n)`: so thời gian put `n` entry vào `new HashMap<>()` vs `HashMap.newHashMap(n)`; và lookup 5 000 key va chạm cùng hash khi key `Comparable` vs không `Comparable` (Q12). Test `q08_experimentRuns` với `runExperiment(2_000)`.
- Q11 [DỰ ĐOÁN] Worst-case lookup: `Complexity Q11_WORST_CASE_LINKED_BUCKET = Complexity.O_N`, `Complexity Q11_WORST_CASE_TREE_BIN_COMPARABLE_KEYS = Complexity.O_LOG_N` (cố định).
- Q12 [DỰ ĐOÁN + THÍ NGHIỆM] Java hiện đại xử lý bucket có quá nhiều collision thế nào? — `Integer Q12_TREEIFY_THRESHOLD = 8`, `Integer Q12_MIN_TREEIFY_CAPACITY = 64` (cố định; gợi ý: `Ctrl+N` → HashMap, `Ctrl+F12` → tìm hằng số `TREEIFY_THRESHOLD`, `MIN_TREEIFY_CAPACITY`). OBSERVATION Q8, OBSERVATION Q12.

**File `Ex05_MapVariants`** (câu 13, 14, 15)
- Q13 [DỰ ĐOÁN + THÍ NGHIỆM] HashMap có thread-safe không? — `Boolean Q13_HASHMAP_IS_THREAD_SAFE = false` (cố định). Cho sẵn `static String runExperiment(int perThread)`: 4 thread cùng `merge(key, 1, Integer::sum)` trên 100 key vào `HashMap` và `ConcurrentHashMap`, in tổng đếm được. Code: `static Map<String, Integer> concurrentCounter()` → `new ConcurrentHashMap<>()`; test: 4 thread × 10 000 lần `merge("k" + (i % 100), 1, Integer::sum)` trên `concurrentCounter()` → tổng value = 40 000 (tất định). `q13_experimentRuns` với `runExperiment(1_000)`.
- Q14 [DỰ ĐOÁN] HashMap cho phép bao nhiêu null key? — `Integer Q14_SIZE_AFTER_TWO_NULL_KEYS = 1` (thực tế: `put(null,"a"); put(null,"b")`); `String Q14_VALUE_FOR_NULL_KEY = "b"`; `String Q14_TREEMAP_NULL_KEY_EXCEPTION = "NullPointerException"` (thực tế: `new TreeMap<String,String>().put(null, "x")`, bắt exception lấy simple name).
- Q15 [DỰ ĐOÁN + CODE] So sánh HashMap, LinkedHashMap, TreeMap.
  - Dự đoán: `Complexity Q15_TREEMAP_GET = Complexity.O_LOG_N` (cố định); `Boolean Q15_HASHMAP_GUARANTEES_ORDER = false` (cố định).
  - Code: `static Map<String, Integer> wordCountsInFirstSeenOrder(List<String> words)` (LinkedHashMap); `static SortedMap<String, Integer> wordCountsSorted(List<String> words)` (TreeMap); `static <K, V> Map<K, V> lruCache(int maxEntries)` (LinkedHashMap `accessOrder = true` + `removeEldestEntry`; `maxEntries < 1` → IAE).
  - Test: `["b","a","b","c"]` → thứ tự khóa first-seen `[b, a, c]`, sorted `[a, b, c]`, đếm `b=2`; LRU max 2: put a, b, get a, put c → `keySet()` là `[a, c]`; `lruCache(0)` → IAE.

---

### Task 6: d05_equals_hashcode (7 câu)

**Package:** `d05_equals_hashcode` — `-ExpectedQuestions 7`

**File `Ex01_EqualsContract`** (câu 1, 5)
- Q1 [DỰ ĐOÁN + CODE] Contract của `equals()` gồm những gì?
  - Cho sẵn `static final class CaseInsensitiveName { final String value; equals chấp nhận cả CaseInsensitiveName và String, so equalsIgnoreCase; hashCode theo value.toLowerCase(Locale.ROOT) }` — ví dụ phá tính đối xứng.
  - Dự đoán: `Boolean Q1_NAME_EQUALS_STRING = true` (thực tế `new CaseInsensitiveName("Alice").equals("alice")`); `Boolean Q1_STRING_EQUALS_NAME = false` (thực tế `"alice".equals(new CaseInsensitiveName("Alice"))`).
  - Code: `static final class Money { final long amountMinor; final String currency; }` (constructor cho sẵn, `Objects.requireNonNull(currency)`); `equals` và `hashCode` là khối `throw Q1`.
  - Test: reflexive; symmetric; transitive (3 instance bằng nhau); consistent (gọi lặp 10 lần); `equals(null)` false; khác kiểu false; khác currency false; khác amount false; bằng nhau → cùng hashCode.
- Q5 [DỰ ĐOÁN] `==` và `equals()` khác nhau thế nào? — thực tế chạy code:
  - `Boolean Q5_NEW_STRINGS_DOUBLE_EQUALS = false` (`new String("hi") == new String("hi")`);
  - `Boolean Q5_STRING_LITERALS_DOUBLE_EQUALS = true` (`"hi" == "hi"` qua hai biến);
  - `Boolean Q5_INTEGER_127_DOUBLE_EQUALS = true` (`Integer a = 127, b = 127; a == b`);
  - `Boolean Q5_INTEGER_128_DOUBLE_EQUALS = false`.
  - Gợi ý chính: `Ctrl+N` → `Integer` → tìm `IntegerCache`, đọc `Integer.valueOf(int)`.

**File `Ex02_HashCodeRules`** (câu 2, 3, 4)
- Q2 [DỰ ĐOÁN] Hai object cùng hashCode có bắt buộc equals? — `Boolean Q2_SAME_HASH_IMPLIES_EQUALS = false`; thực tế: `"Aa".hashCode() == "BB".hashCode()` và `"Aa".equals("BB")` → giá trị thực tế = `false` khi hash trùng mà không equal (test assert trước rằng hai hash bằng nhau).
- Q3 [DỰ ĐOÁN] Hai object equals có bắt buộc cùng hashCode? — `Boolean Q3_EQUALS_REQUIRES_SAME_HASH = true` (cố định; gợi ý Javadoc `Object.hashCode`).
- Q4 [DỰ ĐOÁN + CODE] Chỉ override equals mà không override hashCode thì bug ở đâu?
  - Cho sẵn `static final class EqualsOnlyUser { final String email; equals theo email; không override hashCode }` (dùng identity hashCode thật; comment: xác suất hai identity hashCode trùng nhau không đáng kể).
  - Dự đoán: `Integer Q4_HASHSET_SIZE_TWO_EQUAL_USERS = 2`, `Boolean Q4_HASHSET_CONTAINS_EQUAL_USER = false` (thực tế chạy trên `HashSet`).
  - Code: `static final class User { final String email; final String displayName; }` — constructor cho sẵn chuẩn hóa `email.trim().toLowerCase(Locale.ROOT)`; `equals`/`hashCode` theo email là khối `throw Q4`.
  - Test: hai User cùng email khác displayName → equal, cùng hash, `HashSet` size 1; `" A@X.com "` và `"a@x.com"` equal; `HashMap` get bằng instance mới.

**File `Ex03_RecordsAndMutableEntities`** (câu 6, 7)
- Q6 [DỰ ĐOÁN + CODE] Với record, equals/hashCode được xử lý thế nào?
  - Cho sẵn `record Point(int x, int y)`, `record Blob(byte[] data)`.
  - Dự đoán: `Boolean Q6_RECORD_EQUAL_BY_VALUE = true`, `Boolean Q6_RECORD_SAME_HASH = true`, `Boolean Q6_ARRAY_COMPONENT_EQUAL_BY_CONTENT = false` (thực tế: hai `Blob` cùng nội dung mảng khác instance).
  - Code: `record SafeBlob(byte[] data)` — compact constructor (sao chép mảng; `null` → NPE qua `Objects.requireNonNull`), accessor `data()` trả bản sao, `equals` bằng `Arrays.equals`, `hashCode` bằng `Arrays.hashCode`, `toString` trả `"SafeBlob" + Arrays.toString(data)`: mỗi phần thân là khối `throw Q6`.
  - Test: hai SafeBlob cùng nội dung equal và cùng hash; sửa mảng gốc sau khi tạo không đổi blob; sửa mảng trả về từ `data()` không đổi blob; `new SafeBlob(null)` → NPE.
- Q7 [DỰ ĐOÁN + CODE] Mutable entity trong HashSet:
  - Cho sẵn `static final class Customer { final String id; String email; equals/hashCode theo cả id và email }`.
  - Dự đoán (thực tế chạy: add, đổi email, rồi thao tác): `Boolean Q7_CONTAINS_AFTER_MUTATION = false`, `Boolean Q7_REMOVE_AFTER_MUTATION_SUCCEEDS = false`, `Integer Q7_SIZE_AFTER_READD = 2`.
  - Code: `static final class StableCustomer { final String id; String email; }` — equals/hashCode chỉ theo id là khối `throw Q7`.
  - Test: add StableCustomer, đổi email → `contains` vẫn true, `remove` thành công.

---

### Task 7: d06_hashset (6 câu)

**Package:** `d06_hashset` — `-ExpectedQuestions 6`

**File `Ex01_Uniqueness`** (câu 1, 2, 4)
- Q1 [DỰ ĐOÁN] HashSet phát hiện duplicate như thế nào? — `Boolean Q1_ADD_DUPLICATE_RETURNS = false` (thực tế: `add("a")` lần hai); `String Q1_BACKING_MAP_CLASS = "HashMap"` (cố định; gợi ý: `Ctrl+N` → HashSet, `Ctrl+B` vào `add`, thấy `map.put(e, PRESENT)`).
- Q2 [CODE + THÍ NGHIỆM] HashSet khác ArrayList ở lookup thế nào?
  - Code: `static <T> List<T> duplicates(List<T> items)` — mỗi phần tử trùng xuất hiện một lần, theo thứ tự lần xuất hiện thứ hai; `static <T> boolean hasDuplicates(Collection<T> items)`.
  - Test: `[a, b, a, c, b, a]` → `[a, b]`; `[]` → `[]`; `hasDuplicates([1,2,3])` false, `([1,2,1])` true.
  - Cho sẵn `static String runExperiment(int n)`: thời gian `contains` n lần trên `ArrayList` vs `HashSet` cỡ n. Test `q02_experimentRuns` với `runExperiment(2_000)`. OBSERVATION Q2.
- Q4 [DỰ ĐOÁN + TỰ TRẢ LỜI] Vì sao mutable object trong HashSet nguy hiểm? — cho sẵn `static final class Tag { String name; equals/hashCode theo name }`; `Boolean Q4_REMOVE_AFTER_MUTATION_WORKS = false` (thực tế: add, đổi name, remove). ANSWER Q4 (nhắc bài d05 Ex03 Q7 để so sánh).

**File `Ex02_SetVariants`** (câu 3, 5, 6)
- Q3 [DỰ ĐOÁN] HashSet có đảm bảo iteration order không? — `Boolean Q3_HASHSET_GUARANTEES_ORDER = false` (cố định; gợi ý: thêm `"banana","apple","cherry","date"` rồi in ra trong `main` cho sẵn).
- Q5 [CODE] LinkedHashSet khác HashSet ra sao? — `static <T> List<T> distinctInFirstSeenOrder(List<T> items)`.
  - Test: `[c, a, c, b, a]` → `[c, a, b]`.
- Q6 [DỰ ĐOÁN + CODE] TreeSet cần Comparable/Comparator vì sao?
  - Dự đoán: `String Q6_TREESET_NON_COMPARABLE_EXCEPTION = "ClassCastException"` (thực tế: `new TreeSet<Object>().add(new Object())`, lấy simple name).
  - Code: `record Person(String name, int age)`; `static <T extends Comparable<? super T>> List<T> distinctSorted(Collection<T> items)`; `static NavigableSet<Person> byAgeThenName(Collection<Person> people)` (TreeSet với `Comparator.comparingInt(Person::age).thenComparing(Person::name)`).
  - Test: `distinctSorted([3,1,3,2])` → `[1,2,3]`; `byAgeThenName` với An 30, Bình 25, Chi 30 → thứ tự Bình, An, Chi và size 3.

---

### Task 8: d07_comparable_comparator (6 câu)

**Package:** `d07_comparable_comparator` — `-ExpectedQuestions 6`

**File `Ex01_NaturalVsCustomOrder`** (câu 1, 3, 2)
- Q1 [CODE] Comparable và Comparator khác nhau thế nào? / Q3 [DỰ ĐOÁN + CODE] Natural ordering nghĩa là gì?
  - Cho sẵn `static final class Version implements Comparable<Version> { final int major, minor, patch; static Version parse(String text) }` (parse `"1.10.0"`, sai định dạng → IAE), `equals/hashCode/toString` cho sẵn.
  - Code: `compareTo(Version other)` — so major, rồi minor, rồi patch (khối `throw Q1`).
  - Dự đoán: `Boolean Q3_STRING_ORDER_PUTS_1_10_BEFORE_1_2 = true` (thực tế `"1.10.0".compareTo("1.2.0") < 0`).
  - Test: `1.2.0 < 1.10.0`; `2.0.0 > 1.99.99`; bằng nhau → 0; sort `["1.10.0","1.2.0","1.2.10","1.2.9"]` → `1.2.0, 1.2.9, 1.2.10, 1.10.0`.
- Q2 [CODE] Khi nào dùng Comparator thay Comparable? — `record Product(String name, BigDecimal price)`; code `static Comparator<Product> byPrice()` và `static Comparator<Product> byNameIgnoreCase()` (dùng `String.CASE_INSENSITIVE_ORDER`).
  - Test: sort theo giá tăng; sort `["banana","Apple","cherry"]` theo tên không phân biệt hoa thường → `Apple, banana, cherry`.

**File `Ex02_ComparatorComposition`** (câu 5, 6)
- Cho sẵn `record Employee(String department, String name, Integer age)`.
- Q5 [CODE] Sort theo nhiều field — `static Comparator<Employee> byDepartmentThenAgeDescThenName()`.
  - Test: `(IT, Bình, 30), (HR, An, 25), (IT, An, 30), (IT, Chi, 40)` → HR/An, IT/Chi(40), IT/An(30), IT/Bình(30).
- Q6 [DỰ ĐOÁN + CODE] Xử lý null khi sort —
  - Dự đoán: `String Q6_SORT_NULL_AGE_EXCEPTION = "NullPointerException"` (thực tế: sort danh sách có `age == null` bằng `Comparator.comparing(Employee::age)`).
  - Code: `static Comparator<Employee> byAgeNullsLast()`; `static Comparator<Employee> byDepartmentNullsFirstThenName()`.
  - Test: tuổi `[30, null, 20]` → `20, 30, null`; phòng ban `[IT, null, HR]` → `null, HR, IT`.

**File `Ex03_InconsistentWithEquals`** (câu 4)
- Q4 [DỰ ĐOÁN + CODE] Comparator không consistent với equals thì TreeSet ra sao?
  - Cho sẵn `record Person(String name, int age)`.
  - Dự đoán (thực tế chạy TreeSet với `Comparator.comparingInt(Person::age)` thêm An 30, Bình 30, Chi 25): `Integer Q4_TREESET_SIZE = 2`, `Integer Q4_HASHSET_SIZE = 3`, `Boolean Q4_TREESET_CONTAINS_BINH = true`.
  - Code: `static Comparator<Person> consistentByAge()` — tuổi rồi tên.
  - Test: TreeSet với `consistentByAge()` giữ đủ 3 người, thứ tự Chi, An, Bình.
  - Gợi ý chính: đọc Javadoc `TreeSet` (đoạn "consistent with equals"), đặt breakpoint `TreeMap.put`.

---

### Task 9: d08_immutability (6 câu)

**Package:** `d08_immutability` — `-ExpectedQuestions 6`

**File `Ex01_FinalIsNotImmutable`** (câu 1, 2)
- Q1 [DỰ ĐOÁN] `final class` có đồng nghĩa immutable? — cho sẵn `static final class Counter { private int value; void increment(); int value(); }`; `Boolean Q1_FINAL_CLASS_STATE_CHANGED = true` (thực tế: gọi `increment()`, so value).
- Q2 [DỰ ĐOÁN] `final List<String>` có immutable? — `Boolean Q2_CAN_ADD_TO_FINAL_LIST = true` (thực tế); `Compiles Q2_REASSIGN_FINAL_LIST_COMPILES = Compiles.NO` (mẫu comment); `String Q2_LIST_OF_ADD_EXCEPTION = "UnsupportedOperationException"` (thực tế `List.of("a").add("b")`); `Boolean Q2_UNMODIFIABLE_VIEW_SEES_BACKING_CHANGE = true` (thực tế: `Collections.unmodifiableList(backing)`, rồi `backing.add`, view thấy phần tử mới).

**File `Ex02_DefensiveCopy`** (câu 3, 5)
- Q3 [CODE] Defensive copy là gì?
  - Cho sẵn `static final class LeakyTeam { private final List<String> members; constructor gán thẳng; members() trả thẳng }` để so sánh.
  - Code: `static final class SafeTeam { private final String name; private final List<String> members; }` — constructor (khối `throw Q3`: `Objects.requireNonNull(name)`, `members = List.copyOf(members)`), `members()` trả list bất biến.
  - Test: sửa list nguồn sau khi tạo không ảnh hưởng; `members()` gọi ngoài lambda rồi `assertThrows(UnsupportedOperationException, () -> m.add("x"))`; member `null` → NPE (`List.copyOf`).
- Q5 [DỰ ĐOÁN + CODE] Record có tự đảm bảo deep immutability?
  - Cho sẵn `record Bag(List<String> items)` không sao chép.
  - Dự đoán: `Boolean Q5_RECORD_IS_DEEPLY_IMMUTABLE = false` (thực tế: sửa list nguồn, `bag.items()` thay đổi).
  - Code: `record Playlist(String name, List<String> songs)` — compact constructor khối `throw Q5`: `Objects.requireNonNull(name)`, `songs = List.copyOf(songs)`.
  - Test: tương tự SafeTeam.

**File `Ex03_WhyImmutable`** (câu 4, 6)
- Q4 [TỰ TRẢ LỜI] Vì sao immutable object dễ thread-safe?; Q6 [THÍ NGHIỆM + TỰ TRẢ LỜI] Khi nào immutable object có nhược điểm?
- Cho sẵn `record Point(int x, int y) { Point withX(int x) }` và `static String runExperiment(int n)`: nối chuỗi bằng `+=` trong vòng lặp vs `StringBuilder`; tạo `n` Point qua `withX` liên tiếp vs sửa một object mutable.
- Test `q06_experimentRuns` với `runExperiment(2_000)`. ANSWER Q4, ANSWER Q6, OBSERVATION Q6.

---

### Task 10: d09_lambda (6 câu)

**Package:** `d09_lambda` — `-ExpectedQuestions 6`

**File `Ex01_FunctionalInterfaces`** (câu 1, 2, 3)
- Q1 [DỰ ĐOÁN + CODE] Functional Interface là gì?
  - Dự đoán: `Compiles Q1_TWO_ABSTRACT_METHODS_WITH_ANNOTATION_COMPILES = Compiles.NO` (mẫu comment interface `@FunctionalInterface` có hai abstract method); `Boolean Q1_DEFAULT_METHODS_ALLOWED = true` (cố định).
  - Cho sẵn `@FunctionalInterface interface Validator<T> { boolean validate(T value); default Validator<T> and(Validator<? super T> other) {...} }`.
  - Code: thân `and` (khối `throw Q1`): kết quả true khi cả hai true, `other == null` → NPE, short-circuit (không gọi `other` khi vế đầu false). `static Validator<String> notBlank()`, `static Validator<String> maxLength(int max)` (`max < 0` → IAE; `null` → false).
  - Test: `notBlank().and(maxLength(3))` với `"abc"` true, `"abcd"` false, `" "` false; short-circuit: `other` là lambda gọi `fail(...)` và vế đầu false → không fail.
- Q2 [DỰ ĐOÁN + CODE] `Predicate` và `Function` khác nhau thế nào? — dự đoán `String Q2_PREDICATE_TEST_RETURN_TYPE = "boolean"` (thực tế: `Predicate.class.getMethod("test", Object.class).getReturnType().getName()`).
- Q3 [DỰ ĐOÁN + CODE] `Consumer` có return value không? — dự đoán `String Q3_CONSUMER_ACCEPT_RETURN_TYPE = "void"` (thực tế tương tự với `Consumer.accept`).
  - Code chung Q2/Q3: `static List<String> process(List<String> input, Predicate<String> keep, Function<String, String> transform, Consumer<String> audit)` — lọc, biến đổi, gọi `audit` với từng kết quả theo thứ tự, trả list mới.
  - Test: input `["a", "", "bc"]`, keep `not blank`, transform `toUpperCase(Locale.ROOT)`, audit thêm vào list → kết quả `["A", "BC"]`, audit ghi `["A", "BC"]`.

**File `Ex02_CaptureAndMethodRef`** (câu 4, 5, 6)
- Q4 [DỰ ĐOÁN + CODE] Vì sao local variable được lambda capture phải effectively final?
  - Dự đoán: `Compiles Q4_MODIFY_CAPTURED_LOCAL_COMPILES = Compiles.NO`, `Compiles Q4_MODIFY_FIELD_IN_LAMBDA_COMPILES = Compiles.YES`, `Compiles Q4_MODIFY_ARRAY_ELEMENT_COMPILES = Compiles.YES` (mỗi cái có mẫu comment).
  - Code: `static Supplier<Integer> counter(int start)` — mỗi lần `get()` trả `start`, `start+1`, … (dùng `AtomicInteger`).
  - Test: `counter(5)` → 5, 6, 7; hai counter độc lập nhau.
- Q5 [CODE + TỰ TRẢ LỜI] Method reference có khác lambda về bản chất không?
  - Code: `static Function<String, Integer> length()` (`String::length`), `static BiPredicate<String, String> equalsIgnoreCase()` (`String::equalsIgnoreCase`), `static Supplier<List<String>> listFactory()` (`ArrayList::new`).
  - Test: `length().apply("abc") == 3`; `equalsIgnoreCase().test("A", "a")`; hai lần `listFactory().get()` trả hai list khác instance, có thể add.
  - Gợi ý chính: build rồi chạy `javap -c -p target/classes/phase01/d09_lambda/Ex02_CaptureAndMethodRef.class` trong Terminal, tìm `invokedynamic` và method `lambda$...`.
- Q6 [TỰ TRẢ LỜI] Lambda có phù hợp với logic dài và nhiều side effect không? — ANSWER Q6.

---

### Task 11: d10_stream (11 câu)

**Package:** `d10_stream` — `-ExpectedQuestions 11`

**File `Ex01_MapVsFlatMap`** (câu 1)
- Q1 [DỰ ĐOÁN + CODE] `map()` và `flatMap()` khác nhau thế nào?
  - Dự đoán: `Integer Q1_MAP_COUNT = 2` (thực tế `Stream.of(List.of(1, 2), List.of(3)).map(l -> l).count()`), `Integer Q1_FLATMAP_COUNT = 3` (thực tế `.flatMap(List::stream).count()`).
  - Code: `record Order(String id, List<String> items)`; `static List<Integer> itemCounts(List<Order> orders)`; `static List<String> allItemsDistinctSorted(List<Order> orders)`.
  - Test: 2 order `[pen, ink]`, `[ink, pad]` → counts `[2, 2]`; items `[ink, pad, pen]`.

**File `Ex02_LazinessAndPipeline`** (câu 2, 4, 3, 11 + ví dụ users)
- Q2 [DỰ ĐOÁN] Vì sao intermediate operation là lazy? — `Integer Q2_LOG_SIZE_WITHOUT_TERMINAL = 0` (thực tế: pipeline `filter` + `map` có ghi log, không gọi terminal op).
- Q4 [DỰ ĐOÁN] `filter()` chạy lúc nào? — `String Q4_LOG_WITH_FIND_FIRST = "filter a, filter bb, map bb"` (thực tế: `Stream.of("a", "bb", "ccc").filter(len > 1, log "filter " + s).map(log "map " + s).findFirst()`, log nối bằng `", "`).
- Q3 [DỰ ĐOÁN] Stream có reusable không? — `String Q3_REUSE_EXCEPTION = "IllegalStateException"` (thực tế: gọi `count()` hai lần trên cùng stream).
- Q11 [DỰ ĐOÁN + CODE] Thứ tự operation ảnh hưởng performance thế nào? — `Integer Q11_MAP_CALLS_WHEN_FILTER_FIRST = 2` (thực tế: 10 số `1..10`, `filter(n % 5 == 0)` rồi `map` có đếm, `toList()`); `Integer Q11_MAP_CALLS_WHEN_MAP_FIRST = 10`.
- Ví dụ [CODE] `users.stream()` (không đánh số; dòng tiêu đề dạng ` * Ví dụ [CODE] users.stream()…` để không khớp regex câu hỏi): `record User(String email, boolean active)`; code `static List<String> activeUniqueEmails(List<User> users)` theo đúng pipeline trong `.md`. Test: có user inactive và email trùng → chỉ email của user active, không trùng, giữ thứ tự gặp đầu. ANSWER cho ví dụ: giải thích từng bước và thời điểm chạy (xử lý dọc từng phần tử; `distinct` là stateful).

**File `Ex03_ReduceVsCollect`** (câu 5, 6, 7)
- Q5 [CODE] `reduce()` dùng để làm gì? — `static int sumWithReduce(List<Integer> numbers)`; `static Optional<Integer> maxWithReduce(List<Integer> numbers)`. Test: `[1,2,3]` → 6; `[]` → 0 và `Optional.empty()`.
- Q6 [DỰ ĐOÁN + CODE] `collect()` và `reduce()` khác nhau thế nào?
  - Dự đoán: `Boolean Q6_STREAM_TOLIST_IS_MODIFIABLE = false` (thực tế: thử `add` vào `Stream.of(1).toList()`); `Boolean Q6_COLLECTORS_TOLIST_ADD_WORKS_ON_THIS_JDK = true` (thực tế tương tự với `Collectors.toList()`; gợi ý nhấn mạnh Javadoc **không bảo đảm** điều này).
  - Code: `static Map<Character, Long> countByFirstLetter(List<String> words)` (bỏ chuỗi rỗng; so khóa bằng chữ thường `Character.toLowerCase`); `static String joinWithCollect(List<String> parts, String separator)`.
  - Test: `["apple","Avocado","banana",""]` → `{a=2, b=1}`; join `["a","b"]` với `"-"` → `"a-b"`.
- Q7 [DỰ ĐOÁN + CODE + THÍ NGHIỆM] Side effect trong Stream có vấn đề gì?
  - Cho sẵn `static List<Integer> squaresBuggy(List<Integer> numbers)` dùng `parallelStream().forEach(result::add)` vào `ArrayList`; `static String runExperiment(int n)` chạy bản buggy 20 lần, đếm số lần sai (size khác, thứ tự khác, hoặc exception). `main` gọi `runExperiment(100_000)`.
  - Dự đoán: `Boolean Q7_BUGGY_VERSION_ALWAYS_CORRECT = false` (cố định).
  - Code: `static List<Integer> squares(List<Integer> numbers)` — không side effect, giữ thứ tự (kể cả khi dùng `parallelStream`).
  - Test: `squares(1..10_000)` bằng danh sách bình phương đúng thứ tự; `q07_experimentRuns` với `runExperiment(1_000)`.

**File `Ex04_ParallelStreams`** (câu 8, 9, 10)
- Q8 [DỰ ĐOÁN + THÍ NGHIỆM] `parallelStream()` có luôn nhanh hơn không? — `Boolean Q8_PARALLEL_ALWAYS_FASTER = false` (cố định).
- Q9 [DỰ ĐOÁN] Parallel stream sử dụng thread pool nào? — `String Q9_COMMON_POOL_THREAD_NAME_PREFIX = "ForkJoinPool.commonPool-worker-"` (cố định; `main` cho sẵn in tên thread của một parallel stream để ứng viên tự thấy).
- Q10 [THÍ NGHIỆM + TỰ TRẢ LỜI] Tại sao blocking I/O thường không phải use case tốt cho parallel stream?
- Cho sẵn `static String runExperiment(int n)`: (a) tổng `LongStream.rangeClosed(1, n)` tuần tự vs song song với n nhỏ và lớn; (b) tổng `List<Integer>` boxed song song; (c) 32 tác vụ "I/O giả" (`Thread.sleep(20)`) chạy bằng parallel stream vs `Executors.newFixedThreadPool(32)`; in `ForkJoinPool.commonPool().getParallelism()`.
- Test `q08_experimentRuns` với `runExperiment(10_000)`. OBSERVATION Q8, OBSERVATION Q10, ANSWER Q10.

---

### Task 12: d11_optional (6 câu)

**Package:** `d11_optional` — `-ExpectedQuestions 6`

**File `Ex01_CreateAndUnwrap`** (câu 1, 2, 3)
- Q1 [DỰ ĐOÁN] `Optional.of()` và `ofNullable()` khác nhau thế nào? — `String Q1_OF_NULL_EXCEPTION = "NullPointerException"` (thực tế); `Boolean Q1_OF_NULLABLE_NULL_IS_PRESENT = false` (thực tế).
- Q2 [DỰ ĐOÁN] `orElse()` và `orElseGet()` khác gì? / Q3 [DỰ ĐOÁN + CODE] Vì sao ảnh hưởng performance?
  - Cho sẵn `static final AtomicInteger DEFAULT_CALLS` và `static String expensiveDefault()` (tăng bộ đếm, trả `"default"`).
  - Dự đoán (thực tế: reset bộ đếm, chạy trên `Optional.of("x")`): `Integer Q2_ORELSE_CALLS_WHEN_PRESENT = 1`, `Integer Q2_ORELSEGET_CALLS_WHEN_PRESENT = 0`.
  - Code: `static String displayName(Optional<String> nickname, Supplier<String> fullNameLookup)` — trả nickname nếu có và không blank; ngược lại gọi `fullNameLookup` (chỉ gọi khi cần).
  - Test: nickname `"Bee"` với supplier gọi `fail(...)` → `"Bee"`; `Optional.of(" ")` → giá trị supplier; `Optional.empty()` → giá trị supplier.

**File `Ex02_MapFlatMapAndUsage`** (câu 6, 5, 4)
- Cho sẵn `record Address(String city, String zip)` (các field có thể null), `record Customer(String id, Address address)` (address có thể null), `static Optional<Address> addressOpt(Customer c)` và `static String legacyCityUpper(Customer c)` (chuỗi if-null lồng nhau, để đối chiếu).
- Q6 [DỰ ĐOÁN + CODE] `map()` và `flatMap()` trên Optional khác nhau thế nào?
  - Dự đoán: `Boolean Q6_MAP_WITH_OPTIONAL_FUNCTION_NESTS = true` (thực tế: `Optional.of(c).map(Ex02_MapFlatMapAndUsage::addressOpt).get() instanceof Optional`).
  - Code: `static Optional<String> cityUpper(Customer customer)` (customer có thể null; city blank → empty; viết hoa `Locale.ROOT`); `static Optional<String> zipOf(Optional<Customer> customer)` dùng `flatMap(…::addressOpt)`.
  - Test: city `"hà nội"` → `"HÀ NỘI"`; address null → empty; city `" "` → empty; customer null → empty; `zipOf(Optional.of(customer có zip "700000"))` → `"700000"`; zip null → empty.
- Q5 [DỰ ĐOÁN] Optional có thực sự loại bỏ NPE không? — `String Q5_GET_ON_EMPTY_EXCEPTION = "NoSuchElementException"` (thực tế); `Compiles Q5_ASSIGN_NULL_TO_OPTIONAL_COMPILES = Compiles.YES` (mẫu comment `Optional<String> o = null;`).
- Q4 [TỰ TRẢ LỜI] Optional có nên dùng làm method argument không? — gợi ý Javadoc `Optional` ("API Note: Optional is primarily intended for use as a method return type").

---

### Task 13: d12_datetime (7 câu)

**Package:** `d12_datetime` — `-ExpectedQuestions 7`. Mọi test dùng `ZoneId` tường minh: `Asia/Ho_Chi_Minh`, `Europe/London`, `America/New_York`, `UTC`.

**File `Ex01_TimeTypes`** (câu 1, 3, 2)
- Q1 [DỰ ĐOÁN] `LocalDateTime` có timezone không? — `Boolean Q1_SAME_LOCAL_DATETIME_SAME_INSTANT_IN_TWO_ZONES = false` (thực tế: `LocalDateTime.of(2026, 1, 1, 9, 0)` gắn `Asia/Ho_Chi_Minh` và `Europe/London`, so `toInstant()`).
- Q3 [DỰ ĐOÁN] `Instant` và `ZonedDateTime` khác nhau thế nào? — với `Instant.parse("2026-01-01T02:00:00Z")` gắn HCM và UTC: `Boolean Q3_ZONED_EQUALS = false` (thực tế `equals`), `Boolean Q3_ZONED_IS_EQUAL = true` (thực tế `isEqual`).
- Code (Q1): `static Instant toInstant(LocalDateTime local, ZoneId zone)`; `static LocalDate localDateOf(Instant timestamp, ZoneId zone)`.
  - Test: `localDateOf(2026-01-01T17:30:00Z, HCM)` → `2026-01-02`, với UTC → `2026-01-01`; `toInstant(2026-01-01T09:00, HCM)` → `2026-01-01T02:00:00Z`.
- Q2 [TỰ TRẢ LỜI] Khi lưu timestamp vào database nên cân nhắc `Instant` vì sao?

**File `Ex02_DurationPeriodDst`** (câu 4, 6, 5)
- Q4 [DỰ ĐOÁN] `Duration` và `Period` khác nhau thế nào? — bắt đầu `2026-03-07T12:00` `America/New_York`: `Integer Q4_PLUS_PERIOD_1_DAY_LOCAL_HOUR = 12` (thực tế `plus(Period.ofDays(1)).getHour()`), `Integer Q4_PLUS_DURATION_24H_LOCAL_HOUR = 13` (thực tế `plus(Duration.ofHours(24)).getHour()`).
- Q6 [DỰ ĐOÁN + CODE] DST có thể gây những bug nào? —
  - Dự đoán (thực tế chạy): `Integer Q6_GAP_TIME_RESOLVED_HOUR = 3` (`LocalDateTime.of(2026, 3, 8, 2, 30).atZone(NY).getHour()`); `String Q6_OVERLAP_CHOSEN_OFFSET = "-04:00"` (`LocalDateTime.of(2026, 11, 1, 1, 30).atZone(NY).getOffset().getId()`); `Integer Q6_HOURS_IN_DST_START_DAY = 23`.
  - Code: `static int hoursInLocalDay(LocalDate date, ZoneId zone)` (giờ giữa hai nửa đêm liên tiếp theo `atStartOfDay(zone)`); `static long hoursBetween(ZonedDateTime from, ZonedDateTime to)`.
  - Test: NY `2026-03-08` → 23, `2026-11-01` → 25, `2026-06-01` → 24; HCM `2026-03-08` → 24; `hoursBetween(2026-03-07T12:00 NY, 2026-03-08T12:00 NY)` → 23.
- Q5 [TỰ TRẢ LỜI] Vì sao cộng/trừ offset thủ công dễ lỗi? — liên hệ Q6.

**File `Ex03_AppointmentModeling`** (câu 7)
- Q7 [CODE + TỰ TRẢ LỜI] Cuộc hẹn 09:00 theo múi giờ địa phương nên lưu những thông tin gì?
- Code: `record Appointment(LocalDateTime localStart, ZoneId zone)` — compact constructor cho sẵn (`requireNonNull`); method `Instant startInstant()` (khối `throw Q7`); `static Appointment parse(String isoLocalDateTime, String zoneId)` (khối `throw Q7`).
- Test: `parse("2026-07-01T09:00", "America/New_York").startInstant()` → `2026-07-01T13:00:00Z`; tháng 12 → `2026-12-01T14:00:00Z`; zone sai → `DateTimeException`.
- ANSWER Q7: lưu giờ địa phương + zone ID (không chỉ offset/instant); tính instant khi cần vì luật múi giờ có thể đổi.

---

### Task 14: d13_reflection (6 câu)

**Package:** `d13_reflection` — `-ExpectedQuestions 6`

**File `Ex01_InspectAndInvoke`** (câu 1, 4, 5)
- Cho sẵn `static final class SecretBox { private String secret = "s3cr3t"; public int size = 3; private String reveal(String prefix) { return prefix + secret; } private void explode() { throw new IllegalStateException("boom"); } }`.
- Q1 [CODE] Reflection giải quyết vấn đề gì? — `static List<String> declaredFieldNames(Class<?> type)` (sắp xếp tăng dần, bỏ field synthetic). Test: `SecretBox` → `[secret, size]`.
- Q4 [DỰ ĐOÁN + CODE] Có thể gọi private method bằng reflection không?
  - Code: `static Object invokePrivate(Object target, String methodName, Class<?>[] parameterTypes, Object... args)` — `getDeclaredMethod`, `setAccessible(true)`, `invoke`; `InvocationTargetException` có cause là `RuntimeException` → ném lại cause; `ReflectiveOperationException` khác → `IllegalStateException` giữ cause.
  - Test: `reveal("x-")` → `"x-s3cr3t"`; `explode` → `IllegalStateException("boom")`; tên sai → `IllegalStateException` có cause `NoSuchMethodException`.
  - Dự đoán: `String Q4_SETACCESSIBLE_ON_STRING_VALUE_EXCEPTION = "InaccessibleObjectException"` (thực tế: `String.class.getDeclaredField("value").setAccessible(true)`, bắt exception lấy simple name). Gợi ý: module `java.base` không `opens java.lang` cho code của bạn.
- Q5 [DỰ ĐOÁN] Reflection ảnh hưởng compile-time checking ra sao? — `Compiles Q5_TYPO_METHOD_NAME_COMPILES = Compiles.YES` (mẫu `getDeclaredMethod("revael", String.class)`), `String Q5_TYPO_FAILS_AT_RUNTIME_WITH = "NoSuchMethodException"` (thực tế).

**File `Ex02_MiniDiContainer`** (câu 2, 3, 6)
- Q2 [CODE + TỰ TRẢ LỜI] Spring có thể sử dụng reflection ở đâu?
  - Cho sẵn các class mẫu: `static final class Repo { public Repo() {} }`, `static final class Service { public Service(Repo repo) }`, `static final class Controller { public Controller(Service service) }`, vòng lặp `static final class CycleA { public CycleA(CycleB b) }` / `static final class CycleB { public CycleB(CycleA a) }`, `static final class TwoConstructors { public TwoConstructors() {} public TwoConstructors(Repo r) {} }`. Mỗi class có getter tới dependency.
  - Code: `static final class MiniContainer { <T> T get(Class<T> type) }` — singleton lười (cache); chọn constructor public duy nhất (nhiều hơn một → `IllegalArgumentException`); giải tham số đệ quy; phát hiện vòng lặp → `IllegalStateException` có message chứa tên cả hai class; interface/abstract → `IllegalArgumentException`.
  - Test: `get(Controller.class)` dựng đủ chuỗi; gọi hai lần trả cùng instance; service bên trong controller là cùng instance với `get(Service.class)`; vòng lặp → ISE message chứa `"CycleA"` và `"CycleB"`; `TwoConstructors` → IAE; `get(Runnable.class)` → IAE.
- Q3 [DỰ ĐOÁN] Reflection có nhược điểm gì? — `Boolean Q3_COMPILER_CHECKS_REFLECTIVE_CALL_NAMES = false` (cố định).
- Q6 [THÍ NGHIỆM + TỰ TRẢ LỜI] Vì sao business code không nên lạm dụng reflection? — cho sẵn `static String runExperiment(int n)`: gọi trực tiếp vs `Method.invoke` vs `MethodHandle.invokeExact`. Test `q06_experimentRuns` với `runExperiment(10_000)`.
- ANSWER Q2, ANSWER Q6, OBSERVATION Q6.

---

### Task 15: d14_annotation (6 câu)

**Package:** `d14_annotation` — `-ExpectedQuestions 6`. Không dùng lại container của d13 (ruling: giữ các domain độc lập).

**File `Ex01_RetentionAndTarget`** (câu 1, 2, 3, 4)
- Cho sẵn trong file: `@Retention(SOURCE) @interface SourceNote {}`, `@Retention(CLASS) @interface ClassNote {}`, `@Retention(RUNTIME) @interface RuntimeNote {}`, `@Retention(RUNTIME) @Target(METHOD) @interface LogCalls {}`, `@Target(FIELD) @interface FieldOnly {}`; class `Annotated` gắn cả ba note và method `@LogCalls String greet(String name)` (chỉ trả `"Hi " + name`).
- Q1 [DỰ ĐOÁN] Annotation có tự thực thi logic không? — `Boolean Q1_ANNOTATION_ALONE_PRODUCES_LOG = false` (thực tế: test gọi `greet` và kiểm tra một log dùng chung trong class cho sẵn vẫn rỗng).
- Q2 [DỰ ĐOÁN] `RUNTIME` nghĩa là gì? — `Boolean Q2_RUNTIME_NOTE_VISIBLE = true` (thực tế `Annotated.class.isAnnotationPresent(RuntimeNote.class)`).
- Q3 [DỰ ĐOÁN] SOURCE và RUNTIME khác nhau thế nào? — `Boolean Q3_SOURCE_NOTE_VISIBLE = false`, `Boolean Q3_CLASS_NOTE_VISIBLE = false` (thực tế). Gợi ý: `javap -v` trên `Ex01_RetentionAndTarget$Annotated.class`, tìm `RuntimeVisibleAnnotations` và `RuntimeInvisibleAnnotations`.
- Q4 [DỰ ĐOÁN + CODE] `@Target` dùng để làm gì? — `Compiles Q4_FIELD_ONLY_ON_METHOD_COMPILES = Compiles.NO` (mẫu comment). Code: `static Set<ElementType> targetsOf(Class<? extends Annotation> annotationType)` (đọc `@Target`; không có → `EnumSet` rỗng). Test: `LogCalls` → `{METHOD}`, `FieldOnly` → `{FIELD}`, `RuntimeNote` → rỗng.

**File `Ex02_CustomAnnotationProcessing`** (câu 5, 6)
- Cho sẵn: `@Retention(RUNTIME) @Target(FIELD) @interface NotBlank { String message() default "không được để trống"; }`, `@Retention(RUNTIME) @Target(FIELD) @interface MaxLength { int value(); }`, `@Retention(RUNTIME) @Target(TYPE) @interface Component {}`; class mẫu `SignupForm` (field private `username` có `@NotBlank @MaxLength(10)`, `email` có `@NotBlank`, `nickname` không annotation); `@Component` `static final class Mailer { public Mailer() {} }`, `@Component` `static final class Clock2 { public Clock2() {} }`, `static final class NotAComponent { public NotAComponent() {} }`.
- Q6 [CODE + TỰ TRẢ LỜI] Khi nào nên tạo custom annotation? — `static List<String> validate(Object bean)` → danh sách `"<field>: <message>"`, sắp theo tên field; `@NotBlank` vi phạm khi `null` hoặc blank; `@MaxLength` vi phạm khi dài hơn value → `"<field>: dài tối đa <N> ký tự"`; đọc cả field private.
  - Test: form hợp lệ → `[]`; username `" "`, email `null` → `["email: không được để trống", "username: không được để trống"]`; username 11 ký tự → `["username: dài tối đa 10 ký tự"]`.
- Q5 [CODE + TỰ TRẢ LỜI] Spring xử lý `@Service` bằng cơ chế nào? — `static Map<Class<?>, Object> instantiateComponents(List<Class<?>> candidates)` → chỉ class có `@Component`, tạo bằng constructor không tham số; giữ thứ tự (`LinkedHashMap`).
  - Test: `[Mailer, NotAComponent, Clock2]` → khóa `[Mailer, Clock2]`, value đúng kiểu.
- ANSWER Q5 (quét classpath + reflection + BeanPostProcessor/proxy), ANSWER Q6.

---

### Task 16: d15_modern_java (5 câu)

**Package:** `d15_modern_java` — `-ExpectedQuestions 5`

**File `Ex01_Records`** (câu 1, 2)
- Q1 [DỰ ĐOÁN + CODE] `record` khác POJO ở điểm nào?
  - Dự đoán: `Compiles Q1_RECORD_EXTENDS_CLASS_COMPILES = Compiles.NO`, `Compiles Q1_RECORD_IMPLEMENTS_INTERFACE_COMPILES = Compiles.YES` (mẫu comment); `String Q1_RECORD_TO_STRING = "Point[x=1, y=2]"` (thực tế `new Point(1, 2).toString()` với `record Point(int x, int y)` cho sẵn).
  - Code: `record Email(String value)` — compact constructor (khối `throw Q1`): `null` → NPE; trim + `toLowerCase(Locale.ROOT)`; không có đúng một `@` hoặc phần trước/sau `@` rỗng → IAE.
  - Test: `new Email(" An@Mail.COM ").value()` → `"an@mail.com"`; `"abc"`, `"a@"`, `"@b"`, `"a@b@c"` → IAE; `null` → NPE.
- Q2 [DỰ ĐOÁN + CODE] Component `List` trong record có tự immutable không?
  - Dự đoán: `Boolean Q2_LIST_COMPONENT_CHANGES_WITHOUT_COPY = true` (thực tế với `record RawOrder(String id, List<String> lines)` cho sẵn).
  - Code: `record Order(String id, List<String> lines)` — compact constructor (khối `throw Q2`): `requireNonNull(id)`, `lines = List.copyOf(lines)`.
  - Test: sửa list nguồn không ảnh hưởng; `lines()` gọi ngoài lambda rồi `assertThrows(UnsupportedOperationException)` khi add.

**File `Ex02_SealedAndPatternMatching`** (câu 3, 4, 5)
- Cho sẵn `sealed interface Shape permits Circle, Rectangle, Square {}`, `record Circle(double radius) implements Shape`, `record Rectangle(double width, double height) implements Shape`, `record Square(double side) implements Shape`.
- Q3 [CODE] `sealed` mang lại lợi ích gì cho `switch`? — `static double area(Shape shape)` dùng `switch` pattern matching **không có** `default`.
  - Test: `Circle(1)` → `Math.PI` (delta 1e-9); `Rectangle(2,3)` → 6; `Square(4)` → 16.
- Q4 [TỰ TRẢ LỜI] Khi thêm subtype mới, compiler giúp tìm những chỗ nào? — Bắt đầu: thêm `record Triangle(double base, double height) implements Shape` vào `permits`, build (`Ctrl+F9`), ghi lại lỗi, rồi hoàn tác.
- Q5 [DỰ ĐOÁN + CODE] `switch` expression khác statement thế nào?
  - Dự đoán: `Compiles Q5_SWITCH_EXPRESSION_MISSING_CASE_COMPILES = Compiles.NO` (mẫu comment thiếu `Square`); `String Q5_SWITCH_ON_NULL_WITHOUT_CASE_NULL = "NullPointerException"` (thực tế: gọi hàm dùng switch pattern với `null`, lấy simple name).
  - Code: `static String describe(Object value)` với switch pattern + guard: `null` → `"null"`; `Integer i when i < 0` → `"số âm"`; `Integer` → `"số nguyên"`; `String s when s.isEmpty()` → `"chuỗi rỗng"`; `String` → `"chuỗi"`; `Shape` → `"hình"`; còn lại → `"khác"`.
  - Test đủ 7 nhánh.

---

### Task 17: d16_exceptions_resources (5 câu)

**Package:** `d16_exceptions_resources` — `-ExpectedQuestions 5`

**File `Ex01_TryWithResources`** (câu 1, 2)
- Cho sẵn `static final class TrackedResource implements AutoCloseable { TrackedResource(String name, List<String> log, boolean failOnClose); close() thêm name vào log rồi ném IllegalStateException("close " + name) nếu failOnClose }`.
- Q1 [DỰ ĐOÁN + CODE] try-with-resources đóng theo thứ tự nào? — `String Q1_CLOSE_ORDER = "B,A"` (thực tế: `try (A; B) {}` rồi nối log bằng `","`).
- Q2 [DỰ ĐOÁN] Thao tác chính và `close()` cùng ném exception thì xem lỗi ở đâu? — thân ném `IllegalStateException("body failed")`, resource A `failOnClose`: `String Q2_PRIMARY_MESSAGE = "body failed"`, `Integer Q2_SUPPRESSED_COUNT = 1` (thực tế từ exception bắt được và `getSuppressed()`).
- Code (Q1): `static void closeAll(List<? extends AutoCloseable> resources) throws Exception` — đóng theo thứ tự ngược, đóng tất cả kể cả khi có lỗi; lỗi đầu tiên gặp phải được ném, các lỗi sau `addSuppressed` vào nó.
  - Test: `[A, B, C]` không lỗi → log `C,B,A`; `[A(fail), B, C(fail)]` → cả 3 được đóng, exception chính message `"close C"`, 1 suppressed message `"close A"`; list rỗng không ném.

**File `Ex02_FileIo`** (câu 3, 4, 5)
- Cho sẵn `static final class ConfigException extends RuntimeException { ConfigException(String message); ConfigException(String message, Throwable cause) }` và `static String swallowingLoad(Path file)` (bản xấu: `catch (Exception e) { return null; }`).
- Q3 [DỰ ĐOÁN + CODE] Vì sao `Files.readString()` không phù hợp với file nhiều GB? — `Boolean Q3_READSTRING_LOADS_WHOLE_FILE = true` (cố định; gợi ý Javadoc `Files.readString`). Code: `static long countNonBlankLines(Path file) throws IOException` — đọc từng dòng UTF-8 (`Files.newBufferedReader` hoặc `Files.lines` trong try-with-resources).
  - Test (`@TempDir`): file `"a\n\n  \nb\n"` → 2; file nội dung tiếng Việt `"xin chào\nViệt Nam\n"` ghi UTF-8 → 2.
- Q4 [CODE] Khi nào chuyển `IOException` thành lỗi tầng ứng dụng? — `static String readConfigValue(Path file, String key)` — dòng dạng `key=value` (trim hai phía, bỏ dòng trống và dòng bắt đầu `#`); file không tồn tại/lỗi đọc → `ConfigException("Không đọc được cấu hình: " + file, cause)`; không có key → `ConfigException("Thiếu khóa: " + key)`.
  - Test: `"# c\ntên = Thiện\nport=8080\n"` → `readConfigValue(f, "tên")` = `"Thiện"`, `"port"` = `"8080"`; thiếu key → ConfigException không có cause; file không tồn tại → ConfigException có cause `NoSuchFileException`.
- Q5 [TỰ TRẢ LỜI] Vì sao `catch (Exception e) {}` khiến vận hành khó khăn? — so với `swallowingLoad`.

---

### Task 18: d17_capstone (bài tích hợp)

**Package:** `d17_capstone` — không truyền `-ExpectedQuestions` (không có Qn). Nhãn đánh dấu dùng `B1`–`B5` (Bước 1–5 trong `.md`): `// SOLUTION-BEGIN throw B2`.

**Files:**
- Create: `src/main/java/phase01/d17_capstone/Order.java`, `CsvOrderParser.java`, `CustomerReportService.java`, `ReportApp.java`
- Create: `src/test/java/phase01/d17_capstone/CsvOrderParserTest.java`, `CustomerReportServiceTest.java`, `ReportAppTest.java`
- Create: `src/test/resources/phase01/d17_capstone/orders-sample.csv`

Javadoc đầu mỗi file: tiêu đề `Bài tích hợp — <vai trò file>`, Nguồn: mục "Bài thực hành tích hợp", liệt kê các Bước liên quan dạng ` * B<n> [CODE] <mô tả bước từ .md>` với 4 phần Bắt đầu/Kiểm chứng/Code/Hoàn thành khi. `ReportApp` ghi thêm lệnh chạy từ dòng lệnh.

- **`Order.java`** (B1, cho sẵn toàn bộ): `public record Order(String id, String customerId, Instant createdAt, BigDecimal amount, Status status)` với `enum Status { NEW, PAID, CANCELLED }` lồng trong record; compact constructor `requireNonNull` mọi field, `amount.signum() < 0` → IAE. Javadoc giải thích vì sao `Instant` (thời điểm tuyệt đối) chứ không phải `LocalDateTime`. Khối `ANSWER B1` ngắn.
- **`CsvOrderParser.java`** (B2, B3):
  - Cho sẵn: `public static final String HEADER = "id,customerId,createdAt,amount,status";`, `public record LineError(int lineNumber, String message)`, `public record ParseResult(List<Order> orders, List<LineError> errors)` (compact constructor `List.copyOf`).
  - Code `public ParseResult parse(Path file) throws IOException` (khối `throw B2`): đọc UTF-8 bằng `Files.newBufferedReader` trong try-with-resources; file 0 byte → kết quả rỗng, không lỗi; dòng 1 khác `HEADER` → một `LineError(1, "Header không hợp lệ")` và dừng, không order nào; bỏ qua dòng trống; mỗi dòng dữ liệu `split(",", -1)` phải có đúng 5 trường, parse `Instant.parse`, `new BigDecimal`, `Order.Status.valueOf`; lỗi → `LineError(số dòng, mô tả tiếng Việt)` rồi đọc tiếp; id trùng → `LineError(số dòng, "Trùng id: " + id)`, giữ bản đầu (dùng `Set<String>` — B3). `IOException` (kể cả `NoSuchFileException`) được ném ra, không nuốt.
  - ANSWER B3: vì sao `HashSet<String>`/`LinkedHashMap` và equals/hashCode của `String` phù hợp.
- **`CustomerReportService.java`** (B3, B4):
  - Cho sẵn: constructor `CustomerReportService(ZoneId reportZone)`; `public record CustomerSummary(String customerId, int orderCount, BigDecimal totalPaid, LocalDate firstOrderDate)`.
  - Code `public List<CustomerSummary> summarizeWithLoop(List<Order> orders)` (khối `throw B4`) và `public List<CustomerSummary> summarizeWithStream(List<Order> orders)` (khối `throw B4`): bỏ order `CANCELLED`; `orderCount` = số order còn lại; `totalPaid` = tổng `amount` của order `PAID` (`BigDecimal.ZERO` nếu không có); `firstOrderDate` = ngày của `createdAt` sớm nhất theo `reportZone`; khách chỉ có order CANCELLED không xuất hiện; sắp theo `customerId` tăng dần; trả list không sửa được. Bản Stream không dùng state mutable dùng chung.
  - ANSWER B4: so sánh hai bản.
- **`ReportApp.java`** (B5):
  - Code `static String render(List<CustomerSummary> rows, List<LineError> errors)` (khối `throw B5`): mỗi dòng `customerId | orderCount | totalPaid (toPlainString) | firstOrderDate`, phân tách bằng `"\n"`, có dòng tiêu đề `"customerId | orders | totalPaid | firstOrderDate"`; nếu có lỗi thêm một dòng trống rồi mỗi lỗi `"Dòng <n>: <message>"`.
  - Cho sẵn `public static void main(String[] args)`: cần 2 tham số `<file.csv> <zoneId>` (thiếu → in hướng dẫn ra stderr, `System.exit(2)`); gọi parser + service + `render`; `IOException` → in lỗi ra stderr, `System.exit(1)`. Javadoc ghi lệnh chạy: `.\mvnw.cmd -q -pl phase-01-core-advanced compile` rồi `java -cp phase-01-core-advanced/target/classes phase01.d17_capstone.ReportApp <file> Asia/Ho_Chi_Minh`.
- **`orders-sample.csv`** (UTF-8):
  ```csv
  id,customerId,createdAt,amount,status
  o1,khách-Đạt,2026-01-01T17:30:00Z,100.00,PAID
  o2,khách-Đạt,2026-01-03T01:00:00Z,50.50,PAID
  o3,an,2026-01-02T08:00:00Z,20.00,NEW
  o4,an,2026-01-01T00:00:00Z,999.00,CANCELLED
  o5,binh,khong-phai-ngay,10.00,PAID

  o1,an,2026-01-04T00:00:00Z,5.00,PAID
  o6,chi,2026-01-05T00:00:00Z,7.00,CANCELLED
  ```
- **Test:**
  - `CsvOrderParserTest`: file mẫu (copy từ resource vào `@TempDir` hoặc đọc qua `Path.of(getClass().getResource(...).toURI())`) → 5 order hợp lệ (o1, o2, o3, o4, o6), 2 lỗi ở dòng 6 (ngày sai) và dòng 8 (trùng id `o1`); file 0 byte → rỗng; chỉ header → rỗng; header sai → 1 lỗi dòng 1, 0 order; dòng thiếu trường → lỗi đúng số dòng; amount âm → lỗi; `customerId` tiếng Việt giữ nguyên `"khách-Đạt"`; file không tồn tại → `assertThrows(NoSuchFileException.class, ...)`.
  - `CustomerReportServiceTest` (`@ParameterizedTest` với `@ValueSource(strings = {"loop", "stream"})` chọn bản cài đặt): từ 5 order hợp lệ, zone `Asia/Ho_Chi_Minh` → `an`: 1 order, totalPaid 0, first 2026-01-02; `khách-Đạt`: 2 order, totalPaid 150.50 (so bằng `compareTo`), first `2026-01-02`; `chi` không xuất hiện; thứ tự `[an, khách-Đạt]`; zone `UTC` → first của khách-Đạt là `2026-01-01`; kết quả không sửa được (gọi hàm ngoài lambda); list rỗng → rỗng.
  - `ReportAppTest`: `render` với 1 dòng và 1 lỗi → chuỗi chính xác theo định dạng trên.
- **Cổng:** test package xanh; `.\tools\verify-skeleton.ps1 -Module phase-01-core-advanced -Package d17_capstone` → `OK`; chạy thử `ReportApp` với file mẫu bằng lệnh trong Javadoc, dán output vào report.

---

### Task 19: README giai đoạn, liên kết từ `.md`, kiểm chứng toàn module

**Files:**
- Create: `phase-01-core-advanced/README.md`
- Modify: `01-java-core-advanced.md` (chỉ thêm dòng liên kết)

- [ ] **Step 1: `phase-01-core-advanced/README.md`** — nội dung (tiếng Việt), theo spec mục 7:
  - Mục đích và cách bài tập được tổ chức (mỗi domain một package, 4 loại câu hỏi với bảng nhãn).
  - Chuẩn bị: JDK 21 (IntelliJ: *File → Project Structure → SDKs → + → Download JDK → version 21*), mở thư mục repo, IntelliJ tự nhận Maven.
  - Quy trình 5 bước (tạo nhánh `my-work`, mở file, đọc Javadoc, chạy test ▶, xem lời giải bằng `git diff my-work solutions -- <file>` hoặc *Git → Compare with Branch…*).
  - Lệnh chạy: một domain `.\mvnw.cmd -q -pl phase-01-core-advanced test "-Dtest=phase01/d04_hashmap/*Test"`; toàn module; ghi rõ "trên nhánh bài tập, test đỏ là trạng thái mong đợi".
  - Bảng checklist 17 dòng: `| [ ] | d01_generics | Generics | 3 bài |` … `| [ ] | d17_capstone | Bài tích hợp | 4 file |` (số bài lấy theo thực tế trong package).
  - Exit Criteria sao chép từ `01-java-core-advanced.md`.
  - Mục "Dành cho người biên soạn": đánh dấu `SOLUTION-*`, lệnh `verify-skeleton.ps1`, cách sinh nhánh bài tập (Task 20).
- [ ] **Step 2: Liên kết trong `01-java-core-advanced.md`** — ngay dưới mỗi tiêu đề `## 1. Generics` … `## 16. Exception handling và quản lý tài nguyên`, thêm một dòng trống rồi `> Thực hành: [d01_generics](phase-01-core-advanced/src/main/java/phase01/d01_generics/)` (đúng package của mục); dưới `## Bài thực hành tích hợp` thêm `> Thực hành: [d17_capstone](phase-01-core-advanced/src/main/java/phase01/d17_capstone/)`. Không sửa dòng nào khác (`git diff` chỉ có dòng thêm).
- [ ] **Step 3: Kiểm chứng toàn bộ**
  - `.\mvnw.cmd -q test` → BUILD SUCCESS (bản lời giải xanh toàn bộ).
  - `.\tools\verify-skeleton.ps1 -Module phase-01-core-advanced` → `OK`.
  - Với từng package d01…d16 chạy `verify-skeleton.ps1 -Package <p> -ExpectedQuestions <N>` với N: d01 11, d02 10, d03 6, d04 15, d05 7, d06 6, d07 6, d08 6, d09 6, d10 11, d11 6, d12 7, d13 6, d14 6, d15 5, d16 5 → tất cả `OK`. (Tổng 119 câu.)
- [ ] **Step 4: Commit** — `git add phase-01-core-advanced/README.md 01-java-core-advanced.md` rồi `git commit -m "docs(phase01): add practice README and links from the plan"`.

---

### Task 20: Tạo nhánh bài tập và nhánh lời giải

Chạy trên nhánh tích hợp `phase01-work` (đã chứa Task 1–19, bản lời giải).

- [ ] **Step 1:** `git switch -c phase01-exercises` (từ đầu `phase01-work`).
- [ ] **Step 2:** `java tools/src/main/java/javaroadmap/tools/StripSolutions.java phase-01-core-advanced/src/main/java` → in `Stripped N file(s).`
- [ ] **Step 3:** `Select-String -Path phase-01-core-advanced/src/main/java -Pattern 'SOLUTION-' -SimpleMatch` (đệ quy qua `Get-ChildItem -Recurse -Filter *.java`) → không có kết quả; `.\mvnw.cmd -q -pl phase-01-core-advanced test-compile` → BUILD SUCCESS.
- [ ] **Step 4:** `git commit -am "chore(phase01): generate exercise skeleton from solutions"`.
- [ ] **Step 5:** `git switch -c solutions` rồi `git revert --no-edit HEAD` (khôi phục lời giải trên nền nhánh bài tập, để `git diff phase01-exercises solutions` chỉ chứa lời giải và sau này merge `phase01-exercises → solutions` được).
- [ ] **Step 6:** Trên `solutions`: `.\mvnw.cmd -q test` → BUILD SUCCESS. `git diff --stat phase01-exercises solutions` chỉ liệt kê file trong `phase-01-core-advanced/src/main/java`.
- [ ] **Step 7:** Không merge vào `main`, không xóa `phase01-work`. Báo lại tên ba nhánh và commit đầu mỗi nhánh.

---

## Ghi chú thực thi (cho controller)

- **Không gian làm việc:** worktree tích hợp `C:\Users\thien\IdeaProjects\jav-wt\work` (nhánh `phase01-work` tách từ `main`). Task 1 chạy ở đó. Task 2–18 chạy **song song theo đợt 4–5 task**, mỗi task một worktree riêng `C:\Users\thien\IdeaProjects\jav-wt\<package>` (nhánh `p1/<package>` tách từ `phase01-work` sau Task 1); các package tách biệt nên không xung đột file. Sau khi review sạch, controller `git cherry-pick` commit của task vào `phase01-work`. Task 19–20 chạy trên `phase01-work`.
- **Model:** Task 1, 19, 20 (có sẵn code/lệnh đầy đủ, ít suy luận) → `composer-2.5-fast`. Task 2–18 (biên soạn nội dung, viết test từ đặc tả, cần kiến thức Java chính xác) → `claude-sonnet-5-thinking-high`. Task reviewer → `claude-sonnet-5-thinking-high`; re-review diff sửa nhỏ → `composer-2.5-fast`. Fix round 4–5 và final review toàn nhánh → `claude-opus-5-5-high`.
- **Sổ theo dõi:** `C:\Users\thien\IdeaProjects\jav-wt\.sdd\progress.md` (ngoài repo, không bị commit).

