# Phase 04 — Database Persistence

Mô-đun Java 21 về JDBC, JPA/Hibernate, connection pool và migration với Flyway. Test fixture tích hợp cần Docker đang chạy và image PostgreSQL `18.0`; fixture tạo database dùng một lần và `reset` xóa schema `public`, chỉ dùng với container thử nghiệm.

Chạy từ thư mục gốc repository:

```powershell
./mvnw.cmd -pl phase-04-database-persistence test
```

Chạy riêng kiểm tra fixture PostgreSQL:

```powershell
./mvnw.cmd -pl phase-04-database-persistence "-Dtest=phase04.support.PostgresFixtureTest" test
```

Hiện đây là nền tảng fixture, chưa phải bộ bài tập persistence hoàn chỉnh. Test fixture xanh xác nhận hạ tầng; không đồng nghĩa coverage bài tập đã đầy đủ. Chạy gate skeleton đầy đủ bằng `./tools/verify-skeleton.ps1 -Module phase-04-database-persistence`; `OK` xác nhận inventory/report hiện có, còn `PARTIAL` (khi loại trừ group) không phải gate đầy đủ. Build/test module chỉ xác nhận trạng thái hiện có, không thay cho gate skeleton và kiểm tra fixture PostgreSQL của đợt tích hợp.
