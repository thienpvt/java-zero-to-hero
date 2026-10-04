# Phase 05 — Spring Boot

Mô-đun Java 21 cho ứng dụng Spring Boot. Mô-đun dùng parent Spring Boot `4.1.1` độc lập, không kế thừa parent Maven ở repository root; vì vậy dependency test do Boot quản lý (JUnit 6), khác JUnit 5 của các module dùng parent root. Test fixture tích hợp cần Docker đang chạy và image PostgreSQL `18.0`; fixture tạo database dùng một lần và `reset` xóa schema `public`, chỉ dùng với container thử nghiệm.

Chạy từ thư mục gốc repository:

```powershell
./mvnw.cmd -pl phase-05-spring-boot test
```

Chạy riêng kiểm tra fixture PostgreSQL:

```powershell
./mvnw.cmd -pl phase-05-spring-boot "-Dtest=phase05.support.PostgresFixtureTest" test
```

Xem POM hiệu lực và build riêng module (không cần reactor parent root):

```powershell
./mvnw.cmd -f phase-05-spring-boot/pom.xml help:effective-pom
./mvnw.cmd -f phase-05-spring-boot/pom.xml test
```

Hiện module mới có nền tảng fixture, chưa có ứng dụng/capstone hoàn chỉnh. Test fixture xanh xác nhận hạ tầng; không chứng minh coverage bài tập hay ứng dụng hoàn chỉnh. Chạy gate skeleton đầy đủ bằng `./tools/verify-skeleton.ps1 -Module phase-05-spring-boot`; `OK` xác nhận inventory/report hiện có, còn `PARTIAL` (khi loại trừ group) không phải gate đầy đủ. Build/test chỉ xác nhận trạng thái hiện có, không thay cho gate skeleton và kiểm tra fixture PostgreSQL của đợt tích hợp.
