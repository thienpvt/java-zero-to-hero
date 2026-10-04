# Giai đoạn 4 — Database & Persistence

Module Maven `phase-04-database-persistence` chứa bài thực hành cho [17 mục trong giáo trình](../04-database-persistence.md). Mỗi mục là một package `phase04.dNN_*` với một file `Ex01_*.java` (đúng 5 nhãn `Q1`–`Q5` và một nhãn bài thực hành `Bn`) và lớp test JUnit 5 `Ex01_*Test.java` cùng package. Tất cả hành vi DB chạy trên PostgreSQL thật trong container dùng một lần; không có H2, không có mock DB và không có skip Docker.

Đây là nhánh lời giải của người biên soạn. Lời giải được đánh dấu `SOLUTION-BEGIN`/`SOLUTION-END` trong `src/main/java`; nhánh khung cho người học sinh ra bằng `tools/verify-skeleton.ps1`. Không có nhánh lời giải hay tài liệu nào được publish/đẩy ra remote trong khuôn khổ module này.

## Bốn loại câu hỏi

| Nhãn | Ý nghĩa |
|------|---------|
| `[CODE]` | Cài/hoàn thiện method được đánh dấu TODO để test kiểm tra hành vi DB. |
| `[THÍ NGHIỆM]` | Chạy thí nghiệm có kiểm soát trên PostgreSQL và ghi quan sát trong khối `OBSERVATION`; không dùng ngưỡng thời gian làm điều kiện pass. |
| `[TỰ TRẢ LỜI]` | Viết giải thích trong khối `ANSWER`; nội dung chữ được con người đối chiếu, không tự chấm. |

Nhãn là bản sao nguyên văn thứ tự câu hỏi gốc trong `04-database-persistence.md`. Câu trả lời/solution SQL chỉ nằm trong method Java được đánh dấu; không đặt SQL lời giải trong `src/test/resources`, README hay hằng số của test.

## Chuẩn bị

- **JDK 21** (đã kiểm chứng Temurin `21.0.12.1+1-LTS`, release `21.0.12.1`).
- **Maven Wrapper** đi kèm repo: Apache Maven `3.9.9`.
- **Docker** đang chạy, bắt buộc. Fixture kéo image PostgreSQL `18.0`; máy kiểm chứng dùng Docker Server `29.4.3`.
- Version dependency cố định trong `phase-04-database-persistence/pom.xml` (đọc từ POM): PostgreSQL JDBC `42.7.13`, Hibernate ORM `6.6.58.Final`, Jakarta Persistence API `3.1.0`, HikariCP `5.1.0`, Flyway `11.20.3` (`flyway-core` + `flyway-database-postgresql`), Testcontainers `testcontainers-postgresql:2.0.3` (scope test). Không dùng nhãn `latest`.

Đặt `JAVA_HOME` trong phiên process (không hard-code credential, không đổi ExecutionPolicy):

```powershell
$env:JAVA_HOME = "C:\Users\thien\.jdks\jdk-21.0.12.1+1"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

Fixtures dùng `PGSimpleDataSource` với URL/user/password do Testcontainers sinh ngẫu nhiên theo container dùng một lần. Không in URL, password hay token ra output; lab pool tự dựng `HikariDataSource` từ các giá trị đó và tuyệt đối không log chúng.

## Chạy test

Từ thư mục gốc repository:

```powershell
# Toàn module (đầy đủ, yêu cầu Docker + PostgreSQL 18)
.\mvnw.cmd -f phase-04-database-persistence/pom.xml test

# Một topic
.\mvnw.cmd -f phase-04-database-persistence/pom.xml "-Dtest=phase04/d17_capstone/Ex01_OrderPersistenceTest" test

# Riêng fixture hạ tầng
.\mvnw.cmd -f phase-04-database-persistence/pom.xml "-Dtest=phase04.support.PostgresFixtureTest" test
```

Docker/PostgreSQL không sẵn sàng là lỗi chặn (blocking), không phải skip. Không có annotation Docker-optional, không có `assumeTrue`, không có nhóm test bị loại trong gate đầy đủ.

## Cấu trúc module và bản đồ nhãn

Mỗi package có đúng `Q1`–`Q5` (nguyên văn) và `Bn`:

| # | Package | Source / Test | Lab |
|:-:|---------|---------------|-----|
| 01 | `d01_schema` | `Ex01_RelationalSchema` | B1 schema 4 bảng, FK/unique/nullable |
| 02 | `d02_constraints` | `Ex01_DataConstraints` | B2 constraint qua SQL trực tiếp + SQLSTATE |
| 03 | `d03_sql_joins` | `Ex01_OrderHistoryQuery` | B3 lịch sử đơn + khách chưa có đơn |
| 04 | `d04_indexes` | `Ex01_HistoryIndex` | B4 catalog index + quan sát plan/ghi |
| 05 | `d05_explain` | `Ex01_QueryPlan` | B5 EXPLAIN ANALYZE BUFFERS trước/sau |
| 06 | `d06_pagination` | `Ex01_OrderPage` | B6 offset/keyset, tie, insert xen giữa |
| 07 | `d07_transactions` | `Ex01_OrderTransaction` | B7 rollback toàn order từ connection mới |
| 08 | `d08_isolation` | `Ex01_IsolationAnomaly` | B8 hai connection, write skew, Serializable |
| 09 | `d09_jdbc` | `Ex01_JdbcResources` | B9 bind/đóng tài nguyên/trả lease pool |
| 10 | `d10_jdbc_transactions` | `Ex01_JdbcBatch` | B10 batch rollback + SQLSTATE |
| 11 | `d11_entity_lifecycle` | `Ex01_EntityLifecycle` | B11 flush/commit/detach/merge |
| 12 | `d12_relationships` | `Ex01_OrderMapping` | B12 owning FK, orphan, cascade |
| 13 | `d13_fetching` | `Ex01_OrderHistoryProjection` | B13 đếm SQL, projection, phân trang |
| 14 | `d14_locking` | `Ex01_StockLocking` | B14 conditional/optimistic/pessimistic, conservation |
| 15 | `d15_deadlocks` | `Ex01_DeadlockRetry` | B15 deadlock/serialization thật + retry giới hạn |
| 16 | `d16_migrations` | `Ex01_MigrationCompatibility` | B16 Flyway rỗng/dữ liệu cũ/chạy lại |
| 17 | `d17_capstone` | `Ex01_OrderPersistence` | B17 order + inventory xuyên suốt |

## Fixture PostgreSQL

`phase04.support.PostgresFixture` là API công khai (test-only, `final`, `AutoCloseable`):

- `static PostgresFixture start()` — khởi động một container PostgreSQL `18.0` dùng một lần cho mỗi lớp test.
- `DataSource dataSource()`, `String jdbcUrl()`, `String username()`, `String password()`.
- `void reset(String schemaSql, String seedSql) throws SQLException` — xóa schema `public`, tạo lại, chạy schema rồi seed trong **cùng một lớp test**. Reset chỉ chạy giữa các test trong lớp cục bộ; lớp có reset không chạy song song.
- `close()`.

Chuỗi SQL được truyền nguyên vẹn cho `Statement.execute` (không tách theo dấu `;` vì literal/hàm PostgreSQL có thể chứa `;`). Lỗi SQL phải propagate, không bị nuốt. Fixture là nguồn cấu hình dùng chung; mỗi worker concurrency và mỗi truy vấn kiểm chứng trạng thái đã commit mở `Connection` **riêng** (connection/EntityManager độc lập cho mỗi worker, không dùng chung persistence context).

## Contract domain dùng chung

Schema bốn bảng `customers`, `products`, `orders`, `order_items`; khóa `BIGINT`; giá `NUMERIC(19,2)`/`BigDecimal`, một đơn vị tiền lab cố định USD; thời gian là `Instant`/`TIMESTAMP WITH TIME ZONE`; trạng thái đơn `NEW`/`CANCELLED`; quantity 1–1000. Server là nguồn tin cậy cho giá: client chỉ gửi `productId`/`quantity`, không gửi giá/tổng. Truy vấn theo khách áp dụng customer predicate trong SQL; phân trang dùng thứ tự ổn định `(created_at, id)`, page zero-based, size 1–100.

`customerId` trong capstone là **caller đã tin cậy** — customer scope trong SQL không thay thế authorization HTTP (module này không có tầng REST/auth).

## Bằng chứng SQL và EXPLAIN

Lab B4/B5 in ra plan thật từ PostgreSQL trong output của test (không lưu file); capstone B17 vừa in vừa ghi `EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)` trước/sau index vào:

```text
phase-04-database-persistence/target/task6-evidence/history-plan.txt
```

Tạo lại bằng test thí nghiệm của capstone (chạy trên DB dùng một lần, dataset cố định):

```powershell
.\mvnw.cmd -f phase-04-database-persistence/pom.xml "-Dtest=phase04/d17_capstone/Ex01_OrderPersistenceTest#q05_historyPlanEvidence_experimentRuns" test
```

Bằng chứng ghi: version PostgreSQL, version JDK, hình dạng dataset (3 khách/3 sản phẩm, khoảng 2001 order/item), SQL truy vấn lịch sử thực thi, bound params, số query cho một trang (một projection query/trang, không tăng theo từng hàng — không N+1), và plan trước/sau một index thử nghiệm trên cột customer và cột sắp xếp. Đây là **quan sát trên cache nóng và dataset nhỏ**, không phải ngưỡng thời gian golden hay cam kết hiệu năng production; kết luận có thể thay đổi theo workload thực tế.

## Migration V1 → V2 (tương thích và sửa dữ liệu)

Topic 16 sở hữu duy nhất vòng đời Flyway. V1 tạo bốn bảng (quantity `NOT NULL`). V2 thêm `CHECK (quantity BETWEEN 1 AND 1000)` theo hai bước `NOT VALID` rồi `VALIDATE` trong transaction Flyway:

- Dòng cũ **hợp lệ** giữ nguyên id/quantity/price qua nâng cấp; chạy lại migration không tạo thêm history entry.
- Dòng cũ **không hợp lệ** làm upgrade fail và rollback DDL — không clamp, không xóa dữ liệu để che lỗi. Operator phải sửa dữ liệu tường minh rồi migrate lại.
- `NOT VALID`/`VALIDATE` vẫn có lock và scan; lab **không** chứng minh zero-downtime, và module không giả định rename/drop cột an toàn cho rolling deploy.

## Concurrency, rollback và lock

- Giá lấy từ `products` dưới row lock và lưu snapshot vào `order_items.unit_price`; mọi thay đổi stock dùng conditional `UPDATE ... WHERE stock >= ?` và kiểm tra số dòng ảnh hưởng.
- Order/items/stock nằm trong một transaction local: mọi lỗi `SQLException`/`RuntimeException`/`Error` rollback toàn bộ, lỗi rollback được gắn `addSuppressed` lên lỗi gốc; transaction không bao giờ chứa payment API/email/mạng ngoài.
- Worker concurrency dùng connection/backend riêng, gate có timeout đặt **trước** lock tranh chấp (không đặt barrier sau lock khiến peer không thể tới barrier), không dùng `sleep` để áp đặt thứ tự. Tất cả wait/future/statement có deadline hữu hạn.
- Retry chỉ cho SQLSTATE `40001`/`40P01`, có giới hạn (`maxAttempts` 1–10 trong lab), chạy lại toàn transaction trên connection mới.
- `synchronized`/lock Java không đủ cho nhiều instance: invariant `stock >= 0` được bảo vệ ở tầng database (conditional update + `CHECK`), không bằng khóa trong một JVM.

## Kiểm tra skeleton

```powershell
.\tools\verify-skeleton.ps1 -Module phase-04-database-persistence
.\tools\verify-skeleton.ps1 -Module phase-04-database-persistence -Package d17_capstone -ExpectedQuestions 5
```

Gate đầy đủ báo `OK` khi mọi source khung biên dịch được và mọi test người học đỏ đúng lý do dự kiến (`TODO Qn`/`TODO Bn`, prediction chưa điền), không có test skip và không có xanh ngoài allowlist. `PARTIAL` (khi loại trừ group) không phải gate đầy đủ.

## Dành cho người biên soạn

Viết bản lời giải trên nhánh tích hợp; đánh dấu chỗ người học phải tự viết bằng `SOLUTION-BEGIN`/`SOLUTION-END` chỉ trong `src/main/java`. Test giữ nguyên giữa nhánh lời giải và nhánh khung. Không đặt SQL lời giải trong `src/test/resources`, README hay hằng test. Câu hỏi và bài thực hành phải giữ nguyên văn bản gốc trong `04-database-persistence.md`.
