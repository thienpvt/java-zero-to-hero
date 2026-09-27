# Giai đoạn 2 — Bài thực hành JVM và Concurrency

Module Maven `phase-02-jvm-concurrency` chứa bài tập theo từng mục trong [02-jvm-concurrency.md](../02-jvm-concurrency.md). Mỗi **domain** là một package `phase02.dNN_<tên>` với một hoặc nhiều file `ExNN_*.java` (bài tích hợp `d19_capstone` dùng `Account`, `RaceDemo`, `SafeAccount`). Mỗi file bài tập có đúng một lớp test `ExNN_*Test.java` cùng package (capstone: test cho `SafeAccount` và `RaceDemo`).

Câu hỏi trong tài liệu `.md` được ánh xạ vào Javadoc đầu file dưới dạng `Qn [NHÃN] …` (domain thường) hoặc `Bn [NHÃN] …` (bài tích hợp). Làm lần lượt từng câu/bước; test của câu hiện tại xanh rồi mới sang câu sau.

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
- Terminal (PowerShell): đặt `JAVA_HOME` trỏ tới JDK 21 nếu chưa có trên PATH:

  ```powershell
  $env:JAVA_HOME = "C:\Users\thien\.jdks\jdk-21.0.12.1+1"
  $env:Path = "$env:JAVA_HOME\bin;$env:Path"
  ```

## Quy trình học (5 bước)

1. Chuyển sang nhánh bài tập (khung TODO, chung cho mọi giai đoạn): `git switch exercises`, rồi tạo nhánh riêng: `git switch -c my-work`.
2. Mở file bài tập của domain (ví dụ `Ex01_…java` trong package tương ứng), đọc Javadoc từng câu/bước.
3. Làm theo dòng **Bắt đầu**, triển khai phần **Code** (nếu có).
4. Chạy test của câu đó bằng nút ▶ cạnh tên test hoặc `Ctrl+Shift+F10`; câu trước xanh mới sang câu sau.
5. Khi cần xem lời giải mẫu: `git diff my-work solutions -- phase-02-jvm-concurrency/src/main/java/phase02/d07_race/Ex01_Counter.java` (thay đường dẫn file), hoặc IntelliJ *Git → Compare with Branch…* chọn `solutions`.

Đánh dấu tiến độ trong bảng checklist bên dưới.

## Thứ tự học gợi ý

Javadoc mỗi file có dòng **Cần làm trước**. Thứ tự gợi ý (nhóm JVM không chặn nhóm concurrency):

1. `d06_threads`, rồi `d07_race`.
2. `d08_synchronized`, `d09_jmm`, `d10_atomic`, `d11_locks`, `d12_deadlock`.
3. `d13_executor`, `d14_completable_future`, `d15_concurrent_collections`, `d16_thread_safety`, `d17_virtual_threads`.
4. `d01_bytecode` đến `d05_jit`, rồi `d18_diagnostics`.
5. `d19_capstone` sau `d07_race` và `d08_synchronized`.

Phụ thuộc tối thiểu: `d02` sau `d01`, `d09` sau `d07` và `d08`, `d17` sau `d13`.

## Chạy test

- **Một domain** (ví dụ race condition):

  ```powershell
  .\mvnw.cmd -q -pl phase-02-jvm-concurrency test "-Dtest=phase02/d07_race/*Test"
  ```

- **Toàn module**:

  ```powershell
  .\mvnw.cmd -q -pl phase-02-jvm-concurrency test
  ```

- **Toàn repo** (mọi module):

  ```powershell
  .\mvnw.cmd -q test
  ```

Trên nhánh **`exercises`** (bản khung, gồm mọi giai đoạn), hầu hết test **đỏ** là trạng thái mong đợi (`UnsupportedOperationException("TODO Qn")`, hằng dự đoán `null`, v.v.). Chỉ test thí nghiệm (tên chứa `experimentRuns`) được phép xanh ngay trên khung. Nhánh **`solutions`** (và nhánh làm việc của biên soạn như `phase02-work`) phải xanh toàn bộ.

## Checklist theo domain

| Xong | Package | Chủ đề (.md) | File bài tập |
|:----:|---------|--------------|--------------|
| [ ] | [d01_bytecode](src/main/java/phase02/d01_bytecode/) | Compilation & Bytecode | 2 bài |
| [ ] | [d02_class_loading](src/main/java/phase02/d02_class_loading/) | Class Loading | 2 bài |
| [ ] | [d03_jvm_memory](src/main/java/phase02/d03_jvm_memory/) | JVM Memory | 2 bài |
| [ ] | [d04_gc](src/main/java/phase02/d04_gc/) | Object Lifecycle & GC | 3 bài |
| [ ] | [d05_jit](src/main/java/phase02/d05_jit/) | JIT Compiler | 1 bài |
| [ ] | [d06_threads](src/main/java/phase02/d06_threads/) | Thread Fundamentals | 2 bài |
| [ ] | [d07_race](src/main/java/phase02/d07_race/) | Race Condition | 1 bài |
| [ ] | [d08_synchronized](src/main/java/phase02/d08_synchronized/) | synchronized | 2 bài |
| [ ] | [d09_jmm](src/main/java/phase02/d09_jmm/) | volatile & JMM | 2 bài |
| [ ] | [d10_atomic](src/main/java/phase02/d10_atomic/) | Atomic Classes | 2 bài |
| [ ] | [d11_locks](src/main/java/phase02/d11_locks/) | Lock API | 2 bài |
| [ ] | [d12_deadlock](src/main/java/phase02/d12_deadlock/) | Deadlock | 2 bài |
| [ ] | [d13_executor](src/main/java/phase02/d13_executor/) | ExecutorService | 2 bài |
| [ ] | [d14_completable_future](src/main/java/phase02/d14_completable_future/) | CompletableFuture | 2 bài |
| [ ] | [d15_concurrent_collections](src/main/java/phase02/d15_concurrent_collections/) | Concurrent Collections | 2 bài |
| [ ] | [d16_thread_safety](src/main/java/phase02/d16_thread_safety/) | Thread Safety Design | 2 bài |
| [ ] | [d17_virtual_threads](src/main/java/phase02/d17_virtual_threads/) | Virtual threads (Java 21+) | 2 bài |
| [ ] | [d18_diagnostics](src/main/java/phase02/d18_diagnostics/) | Chẩn đoán JVM | 1 bài |
| [ ] | [d19_capstone](src/main/java/phase02/d19_capstone/) | Practical Exercise — rút tiền | `Account`, `RaceDemo`, `SafeAccount` |

## Exit Criteria

Ứng viên hoàn thành giai đoạn khi có thể:

- Giải thích stack, heap và metaspace.
- Giải thích vì sao Java vẫn có memory leak.
- Hiểu JIT và warm-up.
- Phân biệt atomicity, visibility và ordering.
- Giải thích synchronized, volatile, atomic và Lock.
- Phát hiện race condition/deadlock.
- Thiết kế thread pool hợp lý.
- Sử dụng CompletableFuture mà không block tùy tiện.
- Chọn đúng concurrent collection cho workload.
- Thu thread dump/JFR hoặc GC log để chẩn đoán một hiện tượng thực tế, và giải thích khi nào dùng virtual threads.

**Tài liệu chuẩn:** [JEP 444 — Virtual Threads](https://openjdk.org/jeps/444), [JEP 491 — Synchronize Virtual Threads without Pinning](https://openjdk.org/jeps/491), [jcmd](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jcmd.html).

---

## Dành cho người biên soạn

Biên soạn luôn viết **bản lời giải** trên nhánh tích hợp (`phase02-work`), đánh dấu chỗ ứng viên phải tự viết bằng `SOLUTION-*` trong `src/main/java` (không đánh dấu trong test).

Người học dùng nhánh **`exercises`**. Lời giải nằm trên **`solutions`**. Không tạo nhánh `phase02-exercises`.

Kiểm tra skeleton:

```powershell
.\tools\verify-skeleton.ps1 -Module phase-02-jvm-concurrency
.\tools\verify-skeleton.ps1 -Module phase-02-jvm-concurrency -Package d07_race -ExpectedQuestions 6
```
