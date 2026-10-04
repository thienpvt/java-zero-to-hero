# Phase 04 Exercises and Solutions Design

## Goal and scope

Deliver full learner exercises and solved examples for all 17 topics in `04-database-persistence.md`: exactly five original questions per topic (85 total), plus all 17 hands-on labs. Preserve original `Qn` wording verbatim in Java Javadoc; map labs independently as `B1` through `B17`. Package names, exercise numbering, shared domain, test conventions, PostgreSQL requirement, and stripping boundaries below are frozen.

The module teaches PostgreSQL, SQL, JDBC, Jakarta Persistence/Hibernate without Spring, REST, authentication, or distributed messaging. Every topic has a runnable test or a clearly marked human investigation; prose-only labs do not count as coverage. Theory/prediction questions use prediction constants or written `ANSWER` blocks; actual database experiments use PostgreSQL and record observations.

## Source of truth and authoring contract

Primary curriculum: `04-database-persistence.md` at repository root, current `main`. Question labels and wording below are authoritative exact copies. Each `ExNN_*.java` contains its topic's five Q labels, in source order, and exactly one independent lab label `Bnn`. Matching `ExNN_*Test.java` has `qNN_*` and `bNN_*` methods with Vietnamese `@DisplayName`s. Mark answer regions using existing Java-only `StripSolutions` markers; do not modify stripping grammar unless a concrete regression proves it necessary.

Follow `phase-03-clean-code-design-patterns/src/main/java/phase03/d01_srp/Ex01_OrderServiceSplit.java` and matching test style: concise Vietnamese Javadoc says prerequisites, concrete start action, test/investigation, and completion condition. Prediction constants carry `// SOLUTION-VALUE`; learner implementations and marked answers use exact `SOLUTION-BEGIN` / `SOLUTION-END` forms already recognized by the stripper. Answer block removal leaves valid Java. Human written answer blocks are explanatory, not automatically graded. Hints explain the question without exposing result values or SQL answers.

Java-only stripping is a hard boundary. Learner SQL, DDL, query, and migration answers belong in marked Java method bodies or Java string suppliers. Do not place target learner-answer SQL in `src/test/resources`, README, test constants, or fixture initialization. Fixed schema/seed SQL for non-DDL topic scaffolding is allowed when independent of and not a leak of that topic's learner answer. Audit stripped sources, test fixtures, messages, and hints for answer leakage. Keep tests actionable: invoke learner TODO outside broad `assertThrows`; do not let an unimplemented method pass as expected rejection.

## Frozen module layout

Module: `phase-04-database-persistence/`, Java 21, JUnit 5, ordinary Maven module under root reactor. Phase04 owns exact dependency versions in its module POM; foundation work owns and records compatible pinned versions for PostgreSQL JDBC, Jakarta Persistence, Hibernate ORM, pool, Flyway, and PostgreSQL 18 Testcontainers. Use plain JDBC/JPA; no Spring, H2, `latest`, or new shared framework. Consume the root Maven/test-support contract from foundation plan; do not edit root POM or shared tools as exercise author.

Main sources and paired tests use these exact packages and files:

| Topic | Package | Main source | Test source | Coverage per topic |
|---|---|---|---|---|
| 01 Relational schema | `phase04.d01_schema` | `Ex01_RelationalSchema.java` | `Ex01_RelationalSchemaTest.java` | Q1–Q5, B1 |
| 02 Types and constraints | `phase04.d02_constraints` | `Ex01_DataConstraints.java` | `Ex01_DataConstraintsTest.java` | Q1–Q5, B2 |
| 03 Multi-table SQL | `phase04.d03_sql_joins` | `Ex01_OrderHistoryQuery.java` | `Ex01_OrderHistoryQueryTest.java` | Q1–Q5, B3 |
| 04 Indexes | `phase04.d04_indexes` | `Ex01_HistoryIndex.java` | `Ex01_HistoryIndexTest.java` | Q1–Q5, B4 |
| 05 Execution plans | `phase04.d05_explain` | `Ex01_QueryPlan.java` | `Ex01_QueryPlanTest.java` | Q1–Q5, B5 |
| 06 Pagination | `phase04.d06_pagination` | `Ex01_OrderPage.java` | `Ex01_OrderPageTest.java` | Q1–Q5, B6 |
| 07 ACID and transaction boundary | `phase04.d07_transactions` | `Ex01_OrderTransaction.java` | `Ex01_OrderTransactionTest.java` | Q1–Q5, B7 |
| 08 Isolation | `phase04.d08_isolation` | `Ex01_IsolationAnomaly.java` | `Ex01_IsolationAnomalyTest.java` | Q1–Q5, B8 |
| 09 JDBC resources/pool | `phase04.d09_jdbc` | `Ex01_JdbcResources.java` | `Ex01_JdbcResourcesTest.java` | Q1–Q5, B9 |
| 10 JDBC transaction/batch | `phase04.d10_jdbc_transactions` | `Ex01_JdbcBatch.java` | `Ex01_JdbcBatchTest.java` | Q1–Q5, B10 |
| 11 JPA lifecycle | `phase04.d11_entity_lifecycle` | `Ex01_EntityLifecycle.java` | `Ex01_EntityLifecycleTest.java` | Q1–Q5, B11 |
| 12 Mapping/cascade | `phase04.d12_relationships` | `Ex01_OrderMapping.java` | `Ex01_OrderMappingTest.java` | Q1–Q5, B12 |
| 13 N+1/projection | `phase04.d13_fetching` | `Ex01_OrderHistoryProjection.java` | `Ex01_OrderHistoryProjectionTest.java` | Q1–Q5, B13 |
| 14 Stock locking | `phase04.d14_locking` | `Ex01_StockLocking.java` | `Ex01_StockLockingTest.java` | Q1–Q5, B14 |
| 15 Deadlock/retry | `phase04.d15_deadlocks` | `Ex01_DeadlockRetry.java` | `Ex01_DeadlockRetryTest.java` | Q1–Q5, B15 |
| 16 Migration/real DB | `phase04.d16_migrations` | `Ex01_MigrationCompatibility.java` | `Ex01_MigrationCompatibilityTest.java` | Q1–Q5, B16 |
| 17 Capstone | `phase04.d17_capstone` | `Ex01_OrderPersistence.java` | `Ex01_OrderPersistenceTest.java` | Q1–Q5, B17 |

Additional owned files limited to module `pom.xml`, `README.md`, migrations/fixture resources with fixed scaffold only, and the topic-specific tests listed above. Main module executable starts only when explicitly invoked; no framework bootstrapping.

## Shared DB contract and fixture interface

All topics that use shared domain data use exactly four tables: `customers`, `products`, `orders`, `order_items`; stock resides at `products.stock`. IDs are `BIGINT`; prices are `NUMERIC(19,2)`, `BigDecimal`, one documented fixed lab currency USD; timestamps represent UTC instants (`Instant` / PostgreSQL `TIMESTAMP WITH TIME ZONE`). Order status constrained to documented values. Item quantity is 1–1000. Snapshot unit price lives in `order_items`; never trust a submitted total or client price. Foreign keys and non-null/check constraints protect invariants in DB.

Exact shared schema contract: `customers(id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, name TEXT NOT NULL UNIQUE)`; `products(id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, name TEXT NOT NULL UNIQUE, price NUMERIC(19,2) NOT NULL CHECK (price >= 0), stock INTEGER NOT NULL CHECK (stock >= 0))`; `orders(id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, customer_id BIGINT NOT NULL REFERENCES customers(id), status TEXT NOT NULL CHECK (status IN ('NEW','CANCELLED')), created_at TIMESTAMP WITH TIME ZONE NOT NULL)`; `order_items(id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE, product_id BIGINT NOT NULL REFERENCES products(id), quantity INTEGER NOT NULL CHECK (quantity BETWEEN 1 AND 1000), unit_price NUMERIC(19,2) NOT NULL CHECK (unit_price >= 0), UNIQUE (order_id,product_id))`. The generated `orders.id` is the capstone returned ID. Schema tests may apply learner DDL. For non-DDL topics, fixed fixture schema/seed SQL is valid scaffolding, provided it is independent of and does not leak that exercise's learner-answer SQL. Migration topic owns Flyway schema lifecycle. Common fixture IDs/rows are deterministic and documented in module README. Bounded pages accept 1–100 and sort by `(created_at,id)`; offset `page` is zero-based (`page >= 0`). Customer-scoped reads include customer ID in SQL predicate. Duplicate product IDs are rejected. Orders are nonempty.

Foundation owns test support. Frozen consumer contract: `phase04.support.PostgresFixture implements AutoCloseable`, `static PostgresFixture start()`, `DataSource dataSource()`, `String jdbcUrl()`, `String username()`, `String password()`, `void reset(String schemaSql,String seedSql) throws SQLException`, `close()`. It starts one disposable PostgreSQL 18 Testcontainers instance per test class with random-schema/container isolation; reset runs class-local schema and seed SQL between tests only, and tests in a resetting class do not run in parallel. The fixed bootstrap contains only declared provided scaffolding, never learner SQL answers. Foundation readiness waits for Docker ≤300 seconds outside JUnit; container startup also has a separate bounded timeout. Topic 09 pool lab builds its own pool from URL/credentials without logging credentials. Topic 16 alone owns Flyway lifecycle/migration tests. Mandatory DB tests fail when Docker or PostgreSQL is unavailable; no silent skip or H2 substitution.

Use actual JDBC connections per concurrency worker, independent transaction/EntityManager per worker, timeout-bounded gates created before contested lock acquisition, and bounded `Statement.setQueryTimeout` / test futures. Do not use `sleep` to force ordering. Do not put a barrier after one worker holds a lock where peer needs that same lock to reach it. Assert final committed state from a fresh connection/transaction.

## Frozen learner contracts

The topic exercises are small, single-purpose Java shells with TODOs and marked solution methods. Public surfaces below are test targets; support data types can be package-visible where tests are package siblings. Add no generic ports/repositories/factories unless needed by one concrete exercise.

- `d01_schema.Ex01_RelationalSchema`: `static List<String> migrations()` returns executable SQL statement strings, learner-authored and stripped from main source; tests apply statements to disposable schema and assert keys, FK, nullability, uniqueness, and order-item price snapshot.
- `d02_constraints.Ex01_DataConstraints`: `static BigDecimal priceFor(long productId, DataSource ds)`; direct DB invalid-write test proves checks, including NULL interaction and quantity/stock domains.
- `d03_sql_joins.Ex01_OrderHistoryQuery`: `static List<OrderHistory> forCustomer(DataSource ds,long customerId)`; `record OrderHistory(long orderId,Instant createdAt,String status,long itemCount,BigDecimal total)`; one LEFT JOIN variant also includes customers without orders via `static List<CustomerOrderSummary> allCustomers(DataSource ds)`; `record CustomerOrderSummary(long customerId,String customerName,long orderCount)`.
- `d04_indexes.Ex01_HistoryIndex`: learner supplies index DDL in marked Java SQL string; test checks catalog presence and writes bounded before/after evidence, not planner/timing golden output.
- `d05_explain.Ex01_QueryPlan`: `static String explain(DataSource ds,long customerId)` returns actual `EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)` output on disposable seeded DB; test checks meaningful plan tokens and stores dataset/plan, never asserts exact timing or chosen node.
- `d06_pagination.Ex01_OrderPage`: `static List<OrderRow> offsetPage(DataSource,long customerId,int page,int size)` (zero-based page; page >=0) and `static List<OrderRow> after(DataSource,long customerId,Instant createdAt,long id,int size)`; `record OrderRow(long id,Instant createdAt,String status)`; offset page is zero-based and rejects page<0 or size outside1–100, stable `(created_at,id)`, allowlisted sort only if a sort option is explicitly exercised.
- `d07_transactions.Ex01_OrderTransaction`: `static void create(DataSource,long orderId,long customerId,List<Line> items)`; `record Line(long productId,int quantity)`; demonstrate local atomic rollback without external I/O.
- `d08_isolation.Ex01_IsolationAnomaly`: `static Observation run(DataSource,IsolationLevel level)` and `record Observation(List<String> events,boolean invariantHeld)`; two actual connections and timed synchronization; lab records PostgreSQL-specific observed behavior without assuming generic DB equivalence.
- `d09_jdbc.Ex01_JdbcResources`: `static Optional<OrderRow> findOrder(DataSource,long id)` for order-by-ID; optionally `static List<Product> findProductByName(DataSource,String name)` for SQL-injection-shaped bound-value experiment; `static int reduceStock(DataSource,long id,int quantity)`; `record OrderRow(long id,long customerId,Instant createdAt,String status)` and `record Product(long id,String name,int stock,BigDecimal price)`; binding, resource closure, SQL exception preservation, and pool-return evidence. Do not claim injection coverage for numeric ID input.
- `d10_jdbc_transactions.Ex01_JdbcBatch`: `static int[] insertItems(DataSource,long orderId,List<Line> items)`; `record Line(long productId,int quantity)`; price is selected from `products` inside DB transaction, never accepted from caller; verify every batch count and whole transaction rollback for invalid member; classify SQLSTATE.
- `d11_entity_lifecycle.Ex01_EntityLifecycle`: small JPA test entity; `static LifecycleObservation observe(EntityManagerFactory emf,long id)`; `record LifecycleObservation(boolean managedBeforeFlush,boolean rowVisibleBeforeCommit,boolean mergeReturnedManaged)`; tests demonstrate transient/managed/detached/removed, flush vs commit and returned merge object.
- `d12_relationships.Ex01_OrderMapping`: `Order.addItem(OrderItem)` and `Order.removeItem(OrderItem)` maintain owning side; `OrderItem` owns FK; test actual flush, cascade policy, orphan/remove behavior and direct DB FK.
- `d13_fetching.Ex01_OrderHistoryProjection`: `static List<OrderSummary> page(EntityManager em,long customerId,int page,int size)`; `record OrderSummary(long orderId,Instant createdAt,String customerName,List<ItemSummary> items)`; `record ItemSummary(String productName,int quantity,BigDecimal unitPrice)`; instrumented SQL count demonstrates no per-row N+1 and correct pagination.
- `d14_locking.Ex01_StockLocking`: `static boolean reserve(Connection c,long productId,int quantity)` uses conditional stock update and affected-row count; comparison exercise includes optimistic version and `SELECT ... FOR UPDATE`; test two independently connected contenders and stock conservation.
- `d15_deadlocks.Ex01_DeadlockRetry`: `static <T> T retryTransaction(DataSource ds,int maxAttempts,SqlWork<T> work)`; `@FunctionalInterface interface SqlWork<T>{ T run(Connection connection) throws SQLException; }`; bounded retry whole transaction only for SQLSTATE `40001` and PostgreSQL `40P01`, with diagnostics; tests induce each SQLSTATE or exercise deterministic classifier hook while DB-backed scenario remains mandatory.
- `d16_migrations.Ex01_MigrationCompatibility`: `static void migrate(DataSource ds)`; migration code/DDL inside stripped Java source; Flyway records version; migration tests from empty and pre-existing compatible rows, idempotent re-run, no destructive rolling-deploy assumption.
- `d17_capstone.Ex01_OrderPersistence`: `static long createOrder(DataSource ds,long customerId,List<Line> items)`; `record Line(long productId,int quantity)`; `static List<OrderHistory> ordersForCustomer(DataSource,long customerId,int page,int size)`; transaction includes server-side product prices, order and items, conditional stock decrement; customer-scoped read and stable pagination. Reject null/empty, quantity outside1–1000, duplicate product IDs, missing product/customer, and insufficient stock. Any failure rolls back all items and stock changes.

These method signatures are frozen to make test and implementation work independent. Tests may invoke TODOs directly; skeleton intentionally does not meet solved behavior. Avoid adding interfaces beyond `SqlWork` above and whichever foundation test fixture contract is frozen.

## Question and lab coverage matrix

`[CODE]`, `[DỰ ĐOÁN]`, `[THÍ NGHIỆM]`, `[TỰ TRẢ LỜI]` are exact source categories. The category sequence here maps Q1 through Q5 in order; source labels retain exact original question wording below. Each `Q1–Q5` needs a corresponding `qNN_*` test or written `ANSWER` coverage, and every `Bnn` needs test or explicit human evidence. No question gets dropped or silently converted to another topic.

| Topic | Q1 | Q2 | Q3 | Q4 | Q5 | Lab id / evidence |
|---|---|---|---|---|---|---|
| 01 | TỰ TRẢ LỜI | TỰ TRẢ LỜI | THÍ NGHIỆM | THÍ NGHIỆM | TỰ TRẢ LỜI | B1 schema apply + valid/invalid rows |
| 02 | TỰ TRẢ LỜI | CODE | TỰ TRẢ LỜI | TỰ TRẢ LỜI | THÍ NGHIỆM | B2 direct SQL constraint violations and SQLSTATE |
| 03 | TỰ TRẢ LỜI | TỰ TRẢ LỜI | TỰ TRẢ LỜI | THÍ NGHIỆM | TỰ TRẢ LỜI | B3 history query + customer without orders |
| 04 | TỰ TRẢ LỜI | TỰ TRẢ LỜI | TỰ TRẢ LỜI | TỰ TRẢ LỜI | THÍ NGHIỆM | B4 index catalog + bounded plan/write observation |
| 05 | TỰ TRẢ LỜI | TỰ TRẢ LỜI | TỰ TRẢ LỜI | TỰ TRẢ LỜI | TỰ TRẢ LỜI | B5 saved EXPLAIN ANALYZE plan/data notes |
| 06 | TỰ TRẢ LỜI | TỰ TRẢ LỜI | TỰ TRẢ LỜI | THÍ NGHIỆM | TỰ TRẢ LỜI | B6 offset/keyset, tie and inserted-row experiment |
| 07 | CODE | TỰ TRẢ LỜI | TỰ TRẢ LỜI | TỰ TRẢ LỜI | TỰ TRẢ LỜI | B7 forced failure + fresh-connection rollback proof |
| 08 | TỰ TRẢ LỜI | TỰ TRẢ LỜI | TỰ TRẢ LỜI | TỰ TRẢ LỜI | CODE | B8 two connections, timed gates, observed invariant |
| 09 | CODE | TỰ TRẢ LỜI | TỰ TRẢ LỜI | THÍ NGHIỆM | TỰ TRẢ LỜI | B9 query/binding/error/pool-return evidence |
| 10 | TỰ TRẢ LỜI | CODE | TỰ TRẢ LỜI | CODE | TỰ TRẢ LỜI | B10 batch row counts, rollback, SQLSTATE |
| 11 | TỰ TRẢ LỜI | CODE | CODE | TỰ TRẢ LỜI | TỰ TRẢ LỜI | B11 flush, commit, detach, merge observations |
| 12 | TỰ TRẢ LỜI | THÍ NGHIỆM | TỰ TRẢ LỜI | CODE | TỰ TRẢ LỜI | B12 owning FK and actual cascade behavior |
| 13 | CODE | TỰ TRẢ LỜI | TỰ TRẢ LỜI | TỰ TRẢ LỜI | THÍ NGHIỆM | B13 SQL query count, rows and pagination |
| 14 | TỰ TRẢ LỜI | CODE | TỰ TRẢ LỜI | TỰ TRẢ LỜI | CODE | B14 simultaneous contenders, stock conservation |
| 15 | TỰ TRẢ LỜI | CODE | TỰ TRẢ LỜI | CODE | TỰ TRẢ LỜI | B15 bounded real deadlock and retry result |
| 16 | TỰ TRẢ LỜI | TỰ TRẢ LỜI | TỰ TRẢ LỜI | CODE | CODE | B16 empty DB, old rows, repeat migration and PG tests |
| 17 | TỰ TRẢ LỜI | CODE | CODE | CODE | THÍ NGHIỆM | B17 acceptance evidence below |

### Exact original question wording (85)

#### 01 — Relational schema
1. Primary key khác unique constraint ở điểm nào?
2. Khi nào một cột nên cho phép `NULL`, và `NULL` khác giá trị rỗng thế nào?
3. Vì sao đơn hàng cần giữ đơn giá tại thời điểm mua?
4. Foreign key bảo vệ được lỗi nào mà validation ở Java không đủ bảo vệ?
5. Khi nào denormalization đáng cân nhắc và chi phí nào phát sinh?

#### 02 — Types and constraints
1. Vì sao `double` không phù hợp để biểu diễn tiền?
2. Constraint nào đảm bảo quantity của order item lớn hơn 0?
3. Vì sao invariant quan trọng nên được bảo vệ cả ở DB?
4. Khi nào dùng `TIMESTAMP WITH TIME ZONE` thay cho `DATE`?
5. Default có tự áp dụng khi ứng dụng ghi explicit `NULL` không?

#### 03 — Multi-table SQL
1. `LEFT JOIN` tạo kết quả gì khi không có dòng khớp?
2. Vì sao `column = NULL` không dùng để kiểm tra null?
3. `WHERE` khác `HAVING` ở đâu?
4. Join orders với order_items có thể làm tổng đơn hàng bị nhân đôi thế nào?
5. Khi nào window function phù hợp hơn aggregate làm mất chi tiết từng dòng?

#### 04 — Index and query cost
1. Độ chọn lọc của cột ảnh hưởng quyết định dùng index thế nào?
2. Composite index `(a, b)` thường hữu ích cho điều kiện nào?
3. Vì sao index làm chậm một số thao tác ghi?
4. Tại sao sequential scan đôi khi nhanh hơn index scan?
5. Bằng chứng nào đủ để đề xuất index mới?

#### 05 — Execution plan
1. `EXPLAIN` khác `EXPLAIN ANALYZE` ở điểm nào?
2. Vì sao actual rows lệch xa estimated rows có thể làm plan kém?
3. `BUFFERS` cung cấp loại bằng chứng nào?
4. Vì sao không được chạy `EXPLAIN ANALYZE` câu lệnh sửa dữ liệu tùy tiện?
5. Một plan có cost thấp hơn có luôn đồng nghĩa latency production thấp hơn không?

#### 06 — Pagination
1. Vì sao `ORDER BY created_at` chưa chắc tạo thứ tự ổn định?
2. Keyset pagination cần giữ thông tin gì từ trang trước?
3. Offset lớn có thể tốn kém vì sao?
4. Dữ liệu thay đổi giữa hai request ảnh hưởng trang offset thế nào?
5. Rủi ro của phân trang kết hợp fetch collection là gì?

#### 07 — ACID and transaction boundary
1. Transaction boundary của tạo order và giảm stock nên bao gồm thao tác nào?
2. MVCC giúp readers và writers tương tác ra sao?
3. Local DB transaction không đảm bảo điều gì với payment API?
4. Vì sao transaction dài có thể gây vấn đề cho hệ thống?
5. Khi DB commit thành công nhưng email gửi lỗi, hệ thống cần xem đây là loại failure nào?

#### 08 — Isolation
1. Dirty read khác non-repeatable read thế nào?
2. `READ COMMITTED` cho snapshot ở phạm vi nào trong PostgreSQL?
3. Vì sao PostgreSQL `REPEATABLE READ` vẫn có thể có serialization anomaly?
4. Vì sao test race dựa trên `sleep` không đáng tin?
5. Test cần assert invariant nào khi hai request mua cùng sản phẩm?

#### 09 — JDBC resources and pool
1. `PreparedStatement` bảo vệ dữ liệu đầu vào theo cách nào?
2. Vì sao không thể bind tên cột bằng parameter placeholder thông thường?
3. `Connection.close()` có ý nghĩa gì khi connection do pool cấp?
4. Điều gì xảy ra nếu không đóng `ResultSet`/statement/connection?
5. Vì sao bắt SQLException rồi tiếp tục trả response thành công là nguy hiểm?

#### 10 — JDBC transaction and batch
1. Auto-commit làm mỗi câu lệnh có ranh giới transaction thế nào?
2. Khi nào cần rollback trong `catch` và lỗi rollback cần được xử lý ra sao?
3. Batch cải thiện chi phí nào, và không đảm bảo điều gì?
4. Vì sao batch result cần được kiểm tra?
5. Có nên retry mọi SQLException không? Vì sao?

#### 11 — JPA lifecycle
1. `flush` khác commit thế nào?
2. Dirty checking xảy ra khi nào?
3. Vì sao giá trị trả về từ `merge` cần được dùng đúng?
4. Entity detached thay đổi có tự persist không?
5. Generated ID làm equals/hashCode khó ở điểm nào?

#### 12 — Relationships and cascade
1. Owning side quyết định điều gì khi lưu quan hệ?
2. Vì sao chỉ đổi inverse side có thể không ghi FK như mong đợi?
3. `CascadeType.REMOVE` và `orphanRemoval` có thể gây mất dữ liệu nào?
4. Khi nào child entity có lifecycle phụ thuộc parent?
5. Vì sao mapping hai chiều làm tăng độ phức tạp?

#### 13 — Fetching and N+1
1. N+1 thường hình thành qua chuỗi truy vấn nào?
2. Vì sao đổi mọi quan hệ sang EAGER không phải cách sửa tổng quát?
3. Fetch join nhiều collection có thể gây vấn đề gì?
4. Khi nào projection hiệu quả hơn tải entity đầy đủ?
5. Bằng chứng nào chứng minh N+1 đã được loại bỏ?

#### 14 — Stock locking
1. Vì sao read-then-write không điều kiện có race condition?
2. Số dòng update có ý nghĩa gì với conditional stock update?
3. Optimistic và pessimistic locking đánh đổi điều gì?
4. Vì sao Java lock không đủ cho nhiều instance ứng dụng?
5. Làm sao đảm bảo invariant `stock >= 0` cả khi có nhiều writer?

#### 15 — Deadlock and retry
1. Deadlock khác lock wait timeout như thế nào?
2. Vì sao chỉ retry câu lệnh bị lỗi có thể sai?
3. SQLSTATE `40001` báo loại lỗi nào trong PostgreSQL?
4. Retry không giới hạn có thể gây vấn đề gì?
5. Vì sao gọi payment API trong transaction khiến retry nguy hiểm?

#### 16 — Migration and real DB testing
1. Migration đem lại lợi ích nào so với sửa schema thủ công?
2. Vì sao migration rename/drop cột có thể phá deploy rolling?
3. H2 khác PostgreSQL ở những hành vi nào cần quan tâm?
4. Test concurrency cần chạy trên loại database nào để có giá trị?
5. Làm sao tránh test phụ thuộc dữ liệu từ lần chạy trước?

#### 17 — Capstone
1. Giá order item lấy từ đâu và vì sao?
2. Thành phần nào phải rollback cùng nhau khi một item thiếu stock?
3. Làm sao chứng minh hai request đồng thời không oversell?
4. Làm sao chứng minh query list không bị N+1 và pagination đúng?
5. Bằng chứng nào cần lưu để giải thích lựa chọn index và transaction?

### Exact lab steps (17)

**B1 — Topic 01:** Thiết kế schema cho bốn bảng domain; viết migration SQL, chèn dữ liệu mẫu và chứng minh FK, unique, nullability hoạt động bằng các thao tác hợp lệ lẫn không hợp lệ.

**B2 — Topic 02:** Bổ sung constraints cho giá, số lượng, trạng thái đơn hàng và tồn kho. Thử ghi dữ liệu vi phạm trực tiếp bằng SQL, không qua Java, rồi ghi lại lỗi DB trả về.

**B3 — Topic 03:** Viết truy vấn lịch sử đơn hàng theo khách hàng gồm ngày, tổng tiền, số item và trạng thái; thêm biến thể trả về cả khách chưa đặt đơn, kiểm thử khách không có dữ liệu.

**B4 — Topic 04:** Tạo dữ liệu đủ lớn cho truy vấn lịch sử đơn hàng theo customer và thời gian. So sánh plan trước/sau composite index, ghi thời gian, số hàng ước lượng/thực tế và tác động insert.

**B5 — Topic 05:** Lưu plan trước/sau thay đổi cho một truy vấn domain. Giải thích node tốn kém nhất, lý do chọn index hoặc viết lại SQL, và điều kiện khiến kết luận có thể thay đổi.

**B6 — Topic 06:** Cài hai truy vấn phân trang lịch sử order: offset và keyset theo `(created_at, id)`. Kiểm tra trang rỗng, tie-break, dữ liệu mới xen giữa các request và giới hạn kích thước trang.

**B7 — Topic 07:** Thực hiện tạo order gồm insert order, insert items và cập nhật stock trong một transaction. Cố ý lỗi giữa chừng, kiểm tra mọi thay đổi DB đều rollback; không gắn lời gọi mạng vào transaction này.

**B8 — Topic 08:** Dùng hai worker và connection riêng, điều phối để cả hai đọc cùng snapshot trước khi ghi; barrier phải có timeout và không chờ worker đang bị lock. Ghi lại anomaly ở isolation phù hợp, sau đó xác minh invariant sau khi sửa bằng transaction/locking.

**B9 — Topic 09:** Viết DAO JDBC cho truy vấn order theo ID và cập nhật stock. Dùng bind parameters, đóng tài nguyên tự động, kiểm thử đường lỗi SQL và xác nhận connection được trả về pool.

**B10 — Topic 10:** Tạo nhiều order items bằng JDBC batch trong transaction; chèn một item vi phạm constraint và xác minh cả batch nghiệp vụ rollback. Ghi nhận SQLSTATE và trạng thái connection.

**B11 — Topic 11:** Tạo order bằng JPA, quan sát thời điểm insert trước/sau flush và commit. Tách entity khỏi persistence context, cập nhật state, rồi kiểm tra hành vi khi merge và dùng instance được trả về.

**B12 — Topic 12:** Map `Order` và `OrderItem`; viết thêm/xóa item qua API domain nhất quán. Xác minh FK sau flush, rồi thử xóa parent trong database disposable và ghi lại chính xác cascade behavior.

**B13 — Topic 13:** Truy vấn trang lịch sử order gồm customer, items và product name. Đếm SQL cho trang nhiều bản ghi, loại bỏ N+1 bằng query phù hợp, rồi kiểm tra số dòng và pagination vẫn đúng.

**B14 — Topic 14:** Tạo stock nhỏ và chạy hai transaction được điều phối để đồng thời mua hết số lượng. Cài conditional update hoặc locking, assert tổng bán không vượt stock và stock cuối không âm.

**B15 — Topic 15:** Dùng hai connection cập nhật hai sản phẩm theo thứ tự đối nghịch để tái hiện deadlock có timeout. Sau đó thống nhất thứ tự lock và triển khai retry giới hạn cho lỗi phù hợp, log số lần thử và kết quả.

**B16 — Topic 16:** Viết migration tạo domain schema và migration kế tiếp thêm một constraint có xử lý dữ liệu cũ. Chạy từ database rỗng, chạy lại trên bản có dữ liệu, rồi kiểm thử persistence và locking với PostgreSQL lab.

**B17 — Topic 17:** Hoàn thành flow đặt hàng cho domain xuyên suốt. Viết test cho success, reject order rỗng, quantity không dương/vượt giới hạn và duplicate product IDs theo policy đã chọn; kiểm tra stock conservation (tổng lượng bán không vượt stock ban đầu, không có quantity âm). Test rollback toàn order khi lỗi giữa chừng hoặc một trong nhiều items thiếu stock. Kiểm tra hai đơn cạnh tranh stock, customer scope và pagination ổn định. Ghi SQL thực tế, số query và plan của truy vấn lịch sử đơn hàng. Mỗi concurrency worker dùng connection/EntityManager riêng; barrier có timeout, không chờ sau lock khiến peer không thể tới barrier. Không dùng `sleep` để áp đặt thứ tự.

## Test and evidence policy

Use real PostgreSQL 18 for all DB behavior including SQL syntax, migration, transaction, isolation, lock, SQLSTATE, query plans, JDBC pool, Hibernate flush and query count. No mocks for DB correctness, no H2, and no Docker-optional silent skip. Database unavailable means full verification blocked, never pass. Test Docker readiness bounded to 300 seconds as foundation contract states.

Deterministic assertions cover returned values, constraints, row counts, transaction-visible state, SQLSTATE classes, sorted page contents, and stock conservation. Performance labs keep plan text and measured observations as local/test output; assert no exact elapsed-time golden value or exact planner choice. Dataset size and seed are fixed and documented. Concurrency starts gates before contested operations; all waits/timeouts are bounded. Connection pool evidence needs actual acquisition/return observation or explicit instrumentation; do not assert guessed metrics. SQL query counting must prove history page complexity does not grow one query per row.

Lab evidence is a machine assertion, saved plan/output, or a written human experiment in `ANSWER`/`OBSERVATION` block. Experiments record PostgreSQL minor version, dataset shape, query, plan, estimated/actual rows, elapsed observations and caveats. Do not convert hardware-dependent timing into pass/fail threshold. Written theory answers need not be graded.

## Review focus — five high-risk cases and owning tests

1. Null, empty order, duplicate product ID, quantity below 1 or above 1000: reject before stock updates and prove no accidental stock increase. Owned by `Ex01_OrderPersistenceTest` B17.
2. One item missing/insufficient stock after earlier item succeeds: entire order/items/stock rollback, verified from fresh connection. Owned by `Ex01_OrderPersistenceTest` B17 and `Ex01_OrderTransactionTest` B7.
3. Two independent simultaneous reservations: no negative stock and total accepted quantity never exceeds initial stock; all gates and futures time out. Owned by `Ex01_StockLockingTest` B14.
4. Foreign customer reads and page boundary/timestamp ties: return no foreign records and deterministic `(created_at,id)` pages, page size bounded. Owned by `Ex01_OrderPersistenceTest` B17 and `Ex01_OrderPageTest` B6.
5. SQL injection-looking values and broken statements: values remain bound data, failure is surfaced, resources return to pool; no success-shaped fallback. Owned by `Ex01_JdbcResourcesTest` B9 and `Ex01_OrderHistoryQueryTest` B3.

## Completion criteria

- Exactly 17 topic files, 85 preserved Q labels/wordings, and B1–B17 coverage.
- All 17 packages and source/test names match frozen table; public test targets match frozen signatures.
- Real PostgreSQL tests demonstrate schema constraints, migration, joins, stable pagination, SQL plans, transaction rollback, batch correctness, JPA lifecycle/relationship semantics, query counts, isolation, lock invariants, deadlock/serialization classification and bounded retry.
- Capstone meets validation, server-side pricing, atomic rollback, customer scoping, stable pagination and stock conservation.
- Strip-solution verification proves Java compiles after answer removal, learner tests fail for intended missing behavior (not infrastructure or unrelated exceptions), and no SQL answers leak through resources/hints/tests.
- README records JDK/PostgreSQL minor versions, commands, fixture contract and evidence to save; no false claim of remote publication.
