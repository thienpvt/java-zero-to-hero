# Giai đoạn 1 — Bài thực hành Java Core nâng cao

Module Maven `phase-01-core-advanced` chứa bài tập theo từng chủ đề trong [01-java-core-advanced.md](../01-java-core-advanced.md). Mỗi **domain** là một package `phase01.dNN_<tên>` với một hoặc nhiều file `ExNN_<ChủĐềCon>.java` (bài tích hợp `d17_capstone` dùng `Order`, `CsvOrderParser`, `CustomerReportService`, `ReportApp`). Mỗi file bài tập có đúng một lớp test `ExNN_*Test.java` cùng package.

Câu hỏi trong tài liệu `.md` được ánh xạ vào Javadoc đầu file dưới dạng `Qn [NHÃN] …` (domain thường) hoặc `Bn [NHÃN] …` (bài tích hợp **B1–B5**). Làm lần lượt từng câu/bước; test của câu hiện tại xanh rồi mới sang câu sau.

## Bốn loại câu hỏi

| Nhãn | Ý nghĩa |
|------|---------|
| `[CODE]` | Viết hoặc hoàn thiện code (method, class, refactor) để test kiểm tra hành vi. |
| `[DỰ ĐOÁN]` | Điền hằng số dự đoán hoặc trả lời “có biên dịch được không” trước khi chạy; test so khớp với kết quả thực tế. |
| `[THÍ NGHIỆM]` | Chạy `runExperiment` / `main()` để quan sát hiệu năng hoặc hành vi không tất định; ghi nhận trong khối `OBSERVATION`. |
| `[TỰ TRẢ LỜI]` | Giải thích bằng lời trong khối `ANSWER` ở cuối file; không có assert riêng ngoài việc bạn tự kiểm tra nội dung. |

Có thể gặp nhãn ghép, ví dụ `[DỰ ĐOÁN + CODE]`.

## Chuẩn bị

- **JDK 21** (khuyến nghị Temurin 21). Trong IntelliJ: *File → Project Structure → SDKs → + → Download JDK → version 21*.
- Mở **thư mục gốc repo** (có `pom.xml` và `mvnw.cmd`); IntelliJ nhận Maven multi-module tự động.
- Terminal (PowerShell): đặt `JAVA_HOME` trỏ tới JDK 21 nếu chưa có trên PATH.

## Quy trình học (5 bước)

1. Chuyển sang nhánh bài tập (khung TODO): `git switch phase01-exercises`, rồi tạo nhánh riêng: `git switch -c my-work`.
2. Mở file bài tập của domain (ví dụ `Ex01_…java` trong package tương ứng), đọc Javadoc từng câu/bước.
3. Làm theo dòng **Bắt đầu**, triển khai phần **Code** (nếu có).
4. Chạy test của câu đó bằng nút ▶ cạnh tên test hoặc `Ctrl+Shift+F10`; câu trước xanh mới sang câu sau.
5. Khi cần xem lời giải mẫu: `git diff my-work solutions -- phase-01-core-advanced/src/main/java/phase01/d04_hashmap/Ex01_LookupAndCollision.java` (thay đường dẫn file), hoặc IntelliJ *Git → Compare with Branch…* chọn `solutions`.

Đánh dấu tiến độ trong bảng checklist bên dưới.

## Chạy test

- **Một domain** (ví dụ HashMap):

  ```powershell
  .\mvnw.cmd -q -pl phase-01-core-advanced test "-Dtest=phase01/d04_hashmap/*Test"
  ```

- **Toàn module**:

  ```powershell
  .\mvnw.cmd -q -pl phase-01-core-advanced test
  ```

- **Toàn repo** (mọi module):

  ```powershell
  .\mvnw.cmd -q test
  ```

Trên nhánh **`phase01-exercises`** (bản khung), hầu hết test **đỏ** là trạng thái mong đợi (`UnsupportedOperationException("TODO Qn")`, hằng dự đoán `null`, v.v.). Chỉ test thí nghiệm (tên chứa `experimentRuns`) được phép xanh ngay trên khung. Nhánh **`solutions`** (và nhánh làm việc của biên soạn như `phase01-work`) phải xanh toàn bộ.

## Checklist theo domain

| Xong | Package | Chủ đề (.md) | File bài tập |
|:----:|---------|--------------|--------------|
| [ ] | [d01_generics](src/main/java/phase01/d01_generics/) | Generics | 3 bài (`Ex01`–`Ex03`) |
| [ ] | [d02_arraylist](src/main/java/phase01/d02_arraylist/) | ArrayList | 3 bài |
| [ ] | [d03_linkedlist](src/main/java/phase01/d03_linkedlist/) | LinkedList | 3 bài |
| [ ] | [d04_hashmap](src/main/java/phase01/d04_hashmap/) | HashMap | 5 bài |
| [ ] | [d05_equals_hashcode](src/main/java/phase01/d05_equals_hashcode/) | equals() và hashCode() | 3 bài |
| [ ] | [d06_hashset](src/main/java/phase01/d06_hashset/) | HashSet | 2 bài |
| [ ] | [d07_comparable_comparator](src/main/java/phase01/d07_comparable_comparator/) | Comparable & Comparator | 3 bài |
| [ ] | [d08_immutability](src/main/java/phase01/d08_immutability/) | Immutability | 3 bài |
| [ ] | [d09_lambda](src/main/java/phase01/d09_lambda/) | Lambda & Functional Interface | 2 bài |
| [ ] | [d10_stream](src/main/java/phase01/d10_stream/) | Stream API | 4 bài |
| [ ] | [d11_optional](src/main/java/phase01/d11_optional/) | Optional | 2 bài |
| [ ] | [d12_datetime](src/main/java/phase01/d12_datetime/) | Date & Time API | 3 bài |
| [ ] | [d13_reflection](src/main/java/phase01/d13_reflection/) | Reflection | 2 bài |
| [ ] | [d14_annotation](src/main/java/phase01/d14_annotation/) | Annotation | 2 bài |
| [ ] | [d15_modern_java](src/main/java/phase01/d15_modern_java/) | Record, sealed, pattern matching | 2 bài |
| [ ] | [d16_exceptions_resources](src/main/java/phase01/d16_exceptions_resources/) | Exception & quản lý tài nguyên | 2 bài |
| [ ] | [d17_capstone](src/main/java/phase01/d17_capstone/) | Bài thực hành tích hợp | 4 file (`Order`, `CsvOrderParser`, `CustomerReportService`, `ReportApp`) |

## Exit Criteria

Ứng viên hoàn thành giai đoạn khi có thể:

- Giải thích HashMap từ hashCode tới bucket lookup.
- Giải thích chính xác equals/hashCode contract.
- Sử dụng Generics và giải thích PECS.
- Chọn đúng Collection dựa trên workload.
- Sử dụng Stream mà không tạo side effect khó kiểm soát.
- Hiểu Optional thay vì dùng như cách né null.
- Phân biệt Instant, LocalDateTime, ZonedDateTime.
- Giải thích Reflection/Annotation ở mức framework sử dụng.
- Dùng record/sealed/pattern matching đúng phiên bản Java, xử lý file/exception an toàn và hoàn thành bài tích hợp có test.

**Tài liệu chuẩn:** [Java Language Changes by Release](https://docs.oracle.com/en/java/javase/25/language/java-language-changes-release.html), [Java Collections Framework](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/doc-files/coll-overview.html).

---

## Dành cho người biên soạn

Biên soạn luôn viết **bản lời giải** trên nhánh tích hợp (ví dụ `phase01-work`), đánh dấu chỗ ứng viên phải tự viết bằng `SOLUTION-*` trong `src/main/java` (không đánh dấu trong test):

1. **Thân method / constructor** — `// SOLUTION-BEGIN throw Qn` … `// SOLUTION-END` (hoặc `throw Bn` cho capstone): công cụ thay bằng `throw new UnsupportedOperationException("TODO Qn");` giữ thụt lề dòng BEGIN.
2. **Đoạn không phải thân method** — `// SOLUTION-BEGIN todo Qn` … `// SOLUTION-END` → skeleton: `// TODO Qn: viết code của bạn ở đây`.
3. **Hằng dự đoán** — dòng kết thúc `// SOLUTION-VALUE` → skeleton gán `null`.
4. **Đáp án trong comment** — khối `ANSWER Qn` / `OBSERVATION Qn` bọc `SOLUTION-BEGIN` / `SOLUTION-END` (không có `throw`/`todo` trên dòng BEGIN) → skeleton để dòng ` *` trống.

Sinh bản khung toàn module:

```powershell
java tools/src/main/java/javaroadmap/tools/StripSolutions.java phase-01-core-advanced/src/main/java
```

Kiểm tra skeleton (biên dịch, không còn marker, test đỏ đúng lý do, đủ câu `Q1..QN`):

```powershell
.\tools\verify-skeleton.ps1 -Module phase-01-core-advanced
.\tools\verify-skeleton.ps1 -Module phase-01-core-advanced -Package d04_hashmap -ExpectedQuestions 15
```

**Nhánh git (sau khi gộp đủ 17 domain):**

- Tạo `phase01-exercises` từ snapshot lời giải, chạy `StripSolutions` trên `phase-01-core-advanced/src/main/java`, commit bản khung.
- Tạo `solutions` từ `phase01-exercises`, `git revert --no-edit HEAD` để khôi phục lời giải; diff giữa hai nhánh chỉ thay đổi phần đã đánh dấu trong `src/main/java`.

Ứng viên làm trên `phase01-exercises`; lời giải tham khảo nằm trên `solutions`.
