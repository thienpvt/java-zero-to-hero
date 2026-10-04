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

Chạy gate skeleton đầy đủ bằng `./tools/verify-skeleton.ps1 -Module phase-05-spring-boot`; `OK` xác nhận inventory/report hiện có, còn `PARTIAL` (khi loại trừ group) không phải gate đầy đủ. Build/test chỉ xác nhận trạng thái hiện có, không thay cho gate skeleton và kiểm tra fixture PostgreSQL của đợt tích hợp.

## Phạm vi bài tập và coverage

- 16 chủ đề `d01_di` … `d16_actuator` (tương ứng §1–§16 của `05-spring-boot.md`), mỗi chủ đề một `Ex01_*.java` kèm `Ex01_*Test.java` trong cùng package `phase05.dNN_*`; mỗi chủ đề giữ nguyên câu hỏi Q1–Q5 gốc (80 câu, mỗi câu đúng một lần) và một lab B01–B16.
- Bảng `docs/superpowers/specs/2026-10-04-phase05-exercises-design.md` ghi category chuẩn mỗi chủ đề: Q1 `[DỰ ĐOÁN]`, Q2 `[CODE]`, Q3 `[TỰ TRẢ LỜI]`, Q4 `[THÍ NGHIỆM]`, Q5 `[CODE]`; câu hỏi `TỰ TRẢ LỜI` không được chấm tự động.
- Capstone `d17_capstone` là ứng dụng REST hoàn chỉnh (`phase05.d17_capstone.OrderApiApplication`); công việc mã nguồn ghi qua marker B1–B7 trong code, còn B8 chính là README/lệnh chạy lại và bằng chứng kiểm chứng này.
- Audit chính xác nhất thời điểm hiện tại: 80/80 câu hỏi có model `ANSWER/OBSERVATION` trong khối `SOLUTION-BEGIN/END`, 155 khối solution trên 16 file chủ đề; capstone không chứa prompt Q, không có câu hỏi Q thứ 81.

## Yêu cầu môi trường

- JDK 21 (đã kiểm chứng với Eclipse Temurin `21.0.12.1+1`); đặt `JAVA_HOME` tới thư mục JDK này vì wrapper không tự dò JDK.
- Maven wrapper 3.9.9 đi kèm repository (`./mvnw.cmd`), không cần cài Maven riêng.
- Docker daemon chạy được: test tích hợp buộc PostgreSQL `18.0` qua Testcontainers; Docker không khả dụng nghĩa là verification bị chặn, module không tự skip để xanh.

## Chạy app capstone (OrderApiApplication)

Ứng dụng `phase05.d17_capstone.OrderApiApplication` chạy bằng native `spring-boot:run`:

```powershell
$env:JAVA_HOME = 'C:\Users\thien\.jdks\jdk-21.0.12.1+1'   # điều chỉnh theo máy
$env:SPRING_PROFILES_ACTIVE = 'local-lab'                  # bật metrics/loopback của lab
$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5432/capstone'
$env:SPRING_DATASOURCE_USERNAME = 'capstone'
$env:SPRING_DATASOURCE_PASSWORD = $env:CAPSTONE_DB_PASSWORD   # không gõ mật khẩu vào lệnh
./mvnw.cmd -f phase-05-spring-boot/pom.xml spring-boot:run
```

Chạy không kèm profile `local-lab` thì app chỉ có health tối thiểu và không expose metrics; dưới `local-lab`, `server.address` mặc định về `127.0.0.1` (có thể ghi đè `SERVER_ADDRESS` nhưng giá trị khác `127.0.0.1` bị từ chối).

Cấu hình runtime bắt buộc (thiếu hoặc sai định dạng thì app fail ngay khi khởi động, không có giá trị mặc định nào được sinh ra):

- `CAPSTONE_JWT_ISSUER` → `capstone.jwt.issuer`
- `CAPSTONE_JWT_AUDIENCE` → `capstone.jwt.audience`
- `CAPSTONE_JWT_PUBLIC_KEY` → `capstone.jwt.public-key`: khóa công khai RSA tối thiểu 2048 bit, định dạng X.509 SubjectPublicKeyInfo — PEM `-----BEGIN PUBLIC KEY-----` hoặc Base64 DER không đầu/cuối PEM.

Cấu hình mặc định do app tự đặt: `spring.jpa.open-in-view=false`, Hibernate `ddl-auto=validate` (Flyway là owner của schema qua Java migration `CapstoneSchema`), Actuator mặc định chỉ expose `health`, health không show details/components, pool Hikari tên `capstone`. Profile `local-lab` chỉ cho phép bind `127.0.0.1` và mở thêm metrics; endpoint health vẫn ở cùng main server port — hiện tại mô-đun không cấu hình `management.server.port` riêng, và đó là biên giới an toàn khuyến nghị: mọi thứ phục vụ trên một cổng loopback duy nhất, đừng thêm management port tách biệt cho tới khi policy bind riêng được hoàn thiện.

Quan trọng: token để gọi app chạy thật phải do bạn tự sinh với clock hiện tại (xem mục dưới); decoder dùng `System clock` của runtime với lệch cho phép 60 giây, không dùng clock cố định của test fixture.

## Sinh token test cục bộ

Fixture `phase05.support.TestJwt` đã review chỉ dùng cho test (JUnit classpath `src/test`), không nằm trên classpath chạy thật. Điểm cần chú ý: `TestJwt` cố định thời gian mặc định `2026-10-04T12:00:00Z` cho `iat/nbf/exp`; token sinh theo mặc định đó sẽ bị decoder runtime từ chối vì hết hạn. Khi cần gọi app đang chạy, sinh token mới ghi đè `iat`/`nbf`/`exp` theo thời điểm hiện tại (ví dụ `exp = now + 300s`) và cấu hình `CAPSTONE_JWT_PUBLIC_KEY` là public key khớp với cặp khóa dùng ký.

Quy trình an toàn với JDK/Nimbus stdlib, chỉ dành cho môi trường test/local, không thêm helper vào source chạy thật:

1. Sinh cặp khóa RSA 2048 bit tạm (ví dụ `jshell` hoặc `keytool -genkeypair -keyalg RSA -keysize 2048`); private key chỉ tồn tại phiên làm việc, không đưa vào repo, không echo ra terminal, không đưa vào argument dòng lệnh.
2. Trích public key dạng X.509 SPKI (PEM hoặc Base64 DER) và gán qua biến môi trường `CAPSTONE_JWT_PUBLIC_KEY`.
3. Ký token RS256 với issuer/audience khớp `CAPSTONE_JWT_ISSUER`/`CAPSTONE_JWT_AUDIENCE`, claim `iat`/`nbf` quanh hiện tại và `exp` trong tương lai gần, `sub` là subject đã có trong bảng `customers`, `scope` theo endpoint cần gọi.
4. Gửi qua header `Authorization: Bearer <token>`; không đặt token vào URL hay log. Khuyến nghị dựng các lệnh này trong một script PowerShell riêng đặt ngoài repo, đọc giá trị nhạy cảm từ biến môi trường.

Không có live IdP trong phạm vi giai đoạn; đây chỉ là quy trình test token nội bộ.

## Endpoint và contract chính

| Route | Method | Scope yêu cầu | Ghi chú |
|---|---|---|---|
| `/api/orders` | POST | `orders.write` | 201 + `Location: /api/orders/{id}` |
| `/api/orders/{id}/cancel` | POST | `orders.write` | hủy lần đầu; lặp lại trả 409 |
| `/api/orders/{id}` | GET | `orders.read` | order người khác trả 404 theo policy |
| `/api/orders?page=&size=` | GET | `orders.read` | lọc theo owner trong SQL trước khi phân trang |
| `/api/products?page=&size=&sort=` | GET | `products.read` | sort allowlist `id`, `name`, `price` |
| `/actuator/health` | GET | (công khai, tối thiểu) | chỉ status/probe groups |
| `/actuator/metrics/hikaricp.connections.acquire` | GET | `metrics.read` (chỉ `local-lab`) | meter chuẩn, tag `pool:capstone` |

Nội quy đã đóng băng trong mã nguồn: thiếu token 401, token hợp lệ nhưng thiếu scope 403 (kèm `WWW-Authenticate`); `quantity` 1–1000, mỗi order ≥1 dòng, trùng `productId` bị từ chối; giá/currency lấy từ server, `BigDecimal` USD, item lưu snapshot giá; trừ tồn kho và ghi order/items cùng một transaction với conditional update có kiểm tra affected rows — không gọi mạng trong transaction; cancel là transition NEW→CANCELLED có guard ở DB, chỉ transaction thắng restock đúng một lần, cancel lặp lại 409; phân trang page 0-based, size 1–100, sort order-list cố định `(created_at,id)`. Lỗi body theo Problem Details, không lộ SQL/token/class nội bộ.

## Đo lường (chưa chạy — chờ Phase05 Task16)

Meter `hikaricp.connections.acquire` (BaseUnit seconds, thống kê `COUNT`/`TOTAL_TIME`) đã được chứng minh tăng thật trong test (mỗi GET products thật làm số connection acquire tăng). Chạy đo lường chính là việc của Task16 với harness phase09 hiện có; module này không có writer CSV riêng. Contract CSV consumer chờ Phase09 ghi rõ header: `run,snapshot,capturedAtUtc,metricName,pool,count,totalTimeSeconds` — bốn dòng baseline/changed × start/end cho cùng pool, giá trị là cumulative thật, đo trong khoảng UTC, `deltaCount > 0`, một pool-size change giữa baseline và changed. Chưa có giá trị đo nào được ghi nhận ở thời điểm viết README này.
