# Java Zero to Hero

Lộ trình học Java theo **domain/chủ đề**, từ kiến thức cơ bản đến Java Core nâng cao và các mảng backend tiếp theo. `d01`, `d02`… là mã thứ tự chủ đề/bài tập, **không phải lịch học theo ngày**.

Nhánh `main` chứa tài liệu định hướng; code thực hành nằm ở các nhánh bài tập. Mỗi domain có đề trong source, test JUnit để kiểm tra và lời giải để đối chiếu sau khi tự làm.

## Chọn nhánh

| Nhánh | Nội dung | Khi nào dùng |
| --- | --- | --- |
| [`phase00-exercises`](https://github.com/thienpvt/java-zero-to-hero/tree/phase00-exercises) | Khung bài Java cơ bản (`phase-00-java-basics`); đồng thời có khung Phase 01. | Bắt đầu hoặc ôn lại cú pháp, OOP, Collections, Generics, I/O và test. |
| [`phase01-exercises`](https://github.com/thienpvt/java-zero-to-hero/tree/phase01-exercises) | Khung bài Java Core nâng cao (`phase-01-core-advanced`). | Sau khi đã vững kiến thức cơ bản; luyện Generics, Collections internals, Streams, Reflection, Java hiện đại… |
| [`solutions`](https://github.com/thienpvt/java-zero-to-hero/tree/solutions) | Lời giải cho cả Phase 00 và Phase 01. | Đối chiếu **sau** khi tự làm và chạy test. |

## Bắt đầu

Cần JDK **21+**, Git và Maven Wrapper đi kèm repo; không cần cài Maven riêng. Ví dụ trên Windows/PowerShell:

```powershell
git clone https://github.com/thienpvt/java-zero-to-hero.git
cd java-zero-to-hero
git switch phase00-exercises
git switch -c my-phase00
.\mvnw.cmd -pl phase-00-java-basics test
```

Đọc đề và các câu `Qn` trong `phase-00-java-basics/src/main/java/phase00/`; sửa phần còn thiếu rồi chạy lại test. **Test thất bại trước khi giải là bình thường.** Khi hoàn tất Phase 00, chuyển sang `phase01-exercises`, tạo nhánh làm bài riêng và chạy:

```powershell
.\mvnw.cmd -pl phase-01-core-advanced test
```

Trên macOS/Linux, dùng `./mvnw` thay cho `.\mvnw.cmd`. Muốn xem khác biệt với đáp án của một file, dùng `git diff solutions -- <đường-dẫn-file>` trên nhánh đang làm; tránh chuyển sang nhánh `solutions` trước khi tự giải.

## Tài liệu

- [Lộ trình tổng thể](java-advanced-roadmap.md)
- [Ôn tập Java cơ bản](00-java-basics-review.md)
- [Java Core nâng cao](01-java-core-advanced.md)
- [JVM & Concurrency](02-jvm-concurrency.md)
- [Clean Code & Design Patterns](03-clean-code-design-patterns.md)
- [Bốn kho bài luyện thêm sau các domain](java-practice-next.md)

Các tài liệu trên `main` định hướng cả những giai đoạn chưa có nhánh bài tập; hiện các nhánh bài tập đã xuất bản chỉ bao gồm Phase 00 và Phase 01.
