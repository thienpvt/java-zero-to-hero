# Java Zero to Hero

Lộ trình học Java theo **domain/chủ đề**, từ kiến thức cơ bản đến Java Core nâng cao và các mảng backend tiếp theo. `d01`, `d02`… là mã thứ tự chủ đề/bài tập, **không phải lịch học theo ngày**.

Nhánh `main` chứa tài liệu định hướng; code thực hành nằm ở các nhánh bài tập. Mỗi domain có đề trong source, test JUnit để kiểm tra và lời giải để đối chiếu sau khi tự làm.

Bản docs-only này đưa vào commit **cục bộ** các guide/spec đã duyệt cho Phase 04/05/09 và số đo local lịch sử; không cập nhật nhánh `main`, không push hay công bố từ xa. Code, cấu hình và test chỉ có ở nhánh `solutions`/bài tập (`*-exercises`): chuyển sang nhánh tương ứng trước khi dùng các đường dẫn source/lệnh trong README module. Số đo chỉ là một cặp chạy ngắn mixed-mode, không phải SLO hay kết luận hiệu năng production.

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

### Guide/spec Phase 04/05/09 trong commit cục bộ

- Foundation: [spec](docs/superpowers/specs/2026-10-04-phase04-05-09-foundation-design.md) · [plan](docs/superpowers/plans/2026-10-04-phase04-05-09-foundation.md)
- Phase 04 — Database & Persistence: [guide](phase-04-database-persistence/README.md) · [spec](docs/superpowers/specs/2026-10-04-phase04-exercises-design.md) · [plan](docs/superpowers/plans/2026-10-04-phase04-exercises.md)
- Phase 05 — Spring Boot: [guide](phase-05-spring-boot/README.md) · [spec](docs/superpowers/specs/2026-10-04-phase05-exercises-design.md) · [plan](docs/superpowers/plans/2026-10-04-phase05-exercises.md)
- Phase 09 — Algorithms & System Design: [guide](phase-09-algorithms-system-design/README.md) · [spec](docs/superpowers/specs/2026-10-04-phase09-exercises-design.md) · [plan](docs/superpowers/plans/2026-10-04-phase09-exercises.md)
- Số đo local lịch sử 2026-10-05: [thiết lập và giới hạn](phase-09-algorithms-system-design/evidence/2026-10-05-pool-comparison/measurement.txt) · [compare](phase-09-algorithms-system-design/evidence/2026-10-05-pool-comparison/comparison.txt) · [baseline](phase-09-algorithms-system-design/evidence/2026-10-05-pool-comparison/baseline.properties) · [changed](phase-09-algorithms-system-design/evidence/2026-10-05-pool-comparison/changed.properties) · [telemetry](phase-09-algorithms-system-design/evidence/2026-10-05-pool-comparison/telemetry.csv)

Guide/spec được giữ nguyên từ ref đã duyệt `4af3c13`; nhãn trạng thái review/draft và mô tả nhánh lời giải trong tài liệu là ngữ cảnh lịch sử, không phải trạng thái checkout docs-only này. Các đường dẫn source, POM, wrapper và tools trong module guide chỉ dùng sau khi chuyển sang `solutions`/nhánh bài tập; không có code hay công cụ build ở đây. Tài liệu curriculum sẵn có không thay đổi. Các liên kết remote trong bảng nhánh giữ nguyên cho Phase 00/01 đã xuất bản; phần Phase 04/05/09 này chỉ là commit cục bộ, không tuyên bố đã publish nhánh mới.
