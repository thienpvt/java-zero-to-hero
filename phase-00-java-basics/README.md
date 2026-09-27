# Giai đoạn 0 — Bài thực hành ôn tập Java cơ bản

Module Maven `phase-00-java-basics` chứa bài tập theo từng mục trong [00-java-basics-review.md](../00-java-basics-review.md). Mỗi mục là một package `phase00.dNN_<tên>`. Bài tổng hợp nằm ở `d13_capstone`.

Câu hỏi trong tài liệu `.md` được ánh xạ vào Javadoc đầu file dưới dạng `Qn [NHÃN] …`. Bài tổng hợp dùng `B1`–`B6`. Làm lần lượt từng câu; test của câu hiện tại xanh rồi mới sang câu sau.

## Bốn loại câu hỏi

| Nhãn | Ý nghĩa |
|------|---------|
| `[CODE]` | Viết method đang `throw new UnsupportedOperationException("TODO …")`. |
| `[DỰ ĐOÁN]` | Thay `null` của hằng dự đoán. Test chạy code thật rồi so với hằng. |
| `[TỰ TRẢ LỜI]` | Viết vào khối `ANSWER` cuối file. |
| `[THÍ NGHIỆM]` | Không dùng ở giai đoạn này. |

Có thể gặp nhãn ghép, ví dụ `[DỰ ĐOÁN + CODE]`.

## Chuẩn bị

- **JDK 21**. Trong IntelliJ: *File → Project Structure → SDKs → + → Download JDK → version 21*.
- Mở **thư mục gốc repo** (có `pom.xml` và `mvnw.cmd`).
- Terminal (PowerShell), nếu `java` chưa có trên PATH:

```powershell
$env:JAVA_HOME = "$env:USERPROFILE\.jdks\<tên-thư-mục-jdk-21>"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

## Quy trình học

1. Chuyển sang nhánh bài tập chung mọi giai đoạn: `git switch exercises`, rồi tạo nhánh riêng: `git switch -c my-work`.
2. Mở file bài của mục, đọc Javadoc từng câu.
3. Làm theo dòng **Bắt đầu**.
4. Chạy test của câu bằng nút ▶ hoặc `Ctrl+Shift+F10`.
5. Khi cần lời giải mẫu: `git diff my-work solutions -- <đường dẫn file>`.

## Chạy test

```powershell
.\mvnw.cmd -q -pl phase-00-java-basics test "-Dtest=phase00/d05_encapsulation/*Test"
.\mvnw.cmd -q -pl phase-00-java-basics test
```

Trên nhánh **`exercises`** (bản khung, gồm mọi giai đoạn), test đỏ vì `TODO Qn` hoặc vì hằng dự đoán còn `null`. Nhánh **`solutions`** phải xanh toàn bộ.

## Chạy OrderApp

```powershell
chcp 65001
.\mvnw.cmd -q -pl phase-00-java-basics compile
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp phase-00-java-basics/target/classes phase00.d13_capstone.OrderApp phase-00-java-basics/src/test/resources/phase00/d13_capstone/orders-sample.txt
```

Định dạng file và quy tắc trạng thái nằm trong Javadoc `OrderFile` và `Order`. Quyết định thiết kế (`Map`, `enum`, `BigDecimal`, chỗ xử lý exception) viết ở khối `ANSWER B6`.

## Checklist

| Xong | Package | Mục (.md) | File |
|:----:|---------|-----------|------|
| [ ] | [d01_jdk_program](src/main/java/phase00/d01_jdk_program/) | JDK, JVM, chương trình | 2 bài |
| [ ] | [d02_types](src/main/java/phase00/d02_types/) | Biến, kiểu, phép toán | 2 bài |
| [ ] | [d03_flow](src/main/java/phase00/d03_flow/) | Luồng và phương thức | 2 bài |
| [ ] | [d04_text](src/main/java/phase00/d04_text/) | Array, String, enum | 2 bài |
| [ ] | [d05_encapsulation](src/main/java/phase00/d05_encapsulation/) | Class và đóng gói | 2 bài |
| [ ] | [d06_polymorphism](src/main/java/phase00/d06_polymorphism/) | Kế thừa, đa hình | 2 bài |
| [ ] | [d07_equality](src/main/java/phase00/d07_equality/) | equals, hashCode, toString | 2 bài |
| [ ] | [d08_exceptions](src/main/java/phase00/d08_exceptions/) | Exception và tài nguyên | 2 bài |
| [ ] | [d09_collections](src/main/java/phase00/d09_collections/) | Collections mức sử dụng | 2 bài |
| [ ] | [d10_generics](src/main/java/phase00/d10_generics/) | Generics cơ bản | 1 bài |
| [ ] | [d11_io_time](src/main/java/phase00/d11_io_time/) | File và thời gian | 2 bài |
| [ ] | [d12_testing](src/main/java/phase00/d12_testing/) | Test, debug, build | 1 bài |
| [ ] | [d13_capstone](src/main/java/phase00/d13_capstone/) | Quản lý đơn hàng | 4 file |

## Exit criteria

Chấm mỗi mục 1–12 từ 0 đến 2 (tối đa 24). Từ **18/24**, không có mục 5, 6, 8, 9 ở mức 0, và bài tổng hợp chạy được với test xanh: có thể bắt đầu `01-java-core-advanced.md`.

**Tài liệu chuẩn:** [Learn Java](https://dev.java/learn/), [Java Language Basics](https://dev.java/learn/language/constructs/), [Classes and Objects](https://dev.java/learn/language/oop/classes-objects/).
