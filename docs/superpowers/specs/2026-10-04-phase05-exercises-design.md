# Phase 05 — Spring Boot Exercises Design

**Status:** draft for artifact review; conforms to approved umbrella architecture. Product work remains blocked pending review.
**Source curriculum:** `05-spring-boot.md` (16 topics, 80 numbered questions, 16 labs, one capstone).

## Goal and scope

Turn every existing Phase 05 question into one IDE exercise mapping, with runnable tests for code/prediction questions, safe experiments, written-answer blocks, and one secured PostgreSQL-backed order API capstone. Preserve original Q numbers and question wording. Deliver solved exercises on `solutions`; learner skeletons/tests on `exercises`; keep `main` docs-only. This document specifies learner material, not production implementation or integration of branches.

In scope: topics 1–16; labs B01–B16; capstone steps B1–B8 scoped to `d17_capstone` (valid stripper marker IDs); module-local Spring Boot build/test/docs. Out of scope: modifying curriculum prose, live identity provider/payment, phase04/09 product code, deployable production platform, microservices, broker/cache, full observability, and push/PR.

## Architecture

Module `phase-05-spring-boot`, package root `phase05`, topics `d01_di` through `d16_actuator`, plus `d17_capstone`. Topic exercises are independent, explicitly configured and tested at narrow boundaries; only capstone is executable app with package-scoped scan. Each Q appears once in a coverage matrix and one Javadoc exercise. Student answers stay in Java marked regions so Java-only solution stripping does not expose SQL/config answers.

The capstone uses one modular monolith and real PostgreSQL. Flyway owns schema; Hibernate validates; service owns transaction/stock/ownership policy; MVC/security provide HTTP/auth boundaries. A local signed JWT test token exercises the real resource-server decoder; separate mock-auth MVC tests test controller behavior. No external IdP needed.

## Tech stack and dependency ownership

- Java 21; independent Spring Boot parent `4.1.1`, explicit coordinates and `<relativePath/>`; root only aggregates module and does not override Boot-managed JUnit/Spring/Jackson/Hibernate/plugins.
- Boot 4-compatible Web MVC, Validation, Data JPA, Security OAuth2 Resource Server, Actuator, PostgreSQL driver, Flyway starter plus PostgreSQL Flyway module, and Boot test support; versions owned by Boot BOM/parent unless artifact compatibility forces explicit documented pin.
- PostgreSQL 18 pinned image for required Docker-backed integration tests; no H2, no Docker auto-skip. Container unavailable means full verification blocked, never green.
- Use Boot 4/Spring Security current API; no Boot 3 imports or legacy test-mock annotations. Keep dependencies to the above and what capstone requires.

## Global behavior contract

- Schema tables: `customers`, `products`, `orders`, `order_items`; stock is `products.stock`, no inventory table. IDs BIGINT; monetary values `BigDecimal`, `NUMERIC(19,2)`, currency USD; `order_items` snapshots server-owned USD price. Orders start `NEW`, transition once to `CANCELLED`.
- Order has at least one line; quantity 1–1000; duplicate product IDs rejected; unknown product/customer follows documented 404 policy. Validate DTO at HTTP boundary and re-check invariant in service. No client-trusted customer ID, price or currency. Capstone state machine is `NEW` → `CANCELLED` only; no reactivation.
- Capstone creates `POST /api/orders`; owner comes from validated JWT subject resolved internally to customer. Owner-scoped get/cancel only; foreign-owned order returns 404. Cancel once only; transition/restock is atomic using guarded state transition/CAS, repeat cancel returns 409. Page numbers are zero-based; page size 1–100. Product order has stable ID tie-breaker; order list stable `(created_at,id)`; accepted product sort keys are only `id`, `name`, `price`, each with ID tie-breaker.
- Order, item rows and conditional stock updates share one DB transaction. No order for insufficient stock; affected-row count determines conflict. No process-local lock as cross-instance correctness. `GET /api/orders?page=0&size=20` requires `SCOPE_orders.read`, SQL-filters by authenticated customer before paging, and sorts `(created_at,id)`; never page all owners then filter in Java.
- API accepts Bearer token from `Authorization` header only; no cookie auth. CSRF may be disabled only for this bearer-only API; document cookie/browser caveat. CORS is not authorization.
- JWT uses locally generated test key/JWK and actual decoder. Validate allowed signature algorithm, signature, expiry, not-before, issuer, audience and scopes. Keep decoder-token validation tests distinct from mock-principal MVC tests. No live keys/credentials.
- Security filter errors retain 401/403 semantics, `WWW-Authenticate`, and stable Problem Details contract; MVC errors use RFC 9457. Do not leak stack traces, SQL, token, secrets or PII. Default Actuator exposure is health only. Under `local-lab` only, expose `/actuator/metrics/hikaricp.connections.acquire` bound to localhost management interface or test-only access and protected by bearer `SCOPE_metrics.read`; `GET /api/products` requires `SCOPE_products.read`. No custom metrics endpoint. Phase09 exports standard JSON snapshots to CSV using PowerShell stdlib; header is `run,snapshot,capturedAtUtc,metricName,pool,count,totalTimeSeconds`, with baseline/changed × start/end rows and UTC-bracketed interval. Values are actual cumulative COUNT and TOTAL_TIME seconds for same Hikari pool; measured interval must have monotonic deltaCount > 0. Verify exact meter name and BaseUnit from selected Boot/Hikari via docs or smoke before implementation; unresolved meter blocks measurement, never fake values.
- Flyway migration is executable Java-supplied marked SQL (or Java migration) so stripper covers student DDL. No answer-bearing SQL/YAML/properties resources. Student-owned security config/beans/migration SQL reside in marked Java bodies; fixed test fixtures contain no solution.
- `spring.jpa.open-in-view=false`; Hibernate `ddl-auto=validate`; explicit test configuration prevents sibling exercises being component/entity scanned. Test writes/commit/rollback with transaction semantics visible; do not let test-managed rollback fake a commit assertion.

## Exercise conventions

Follow `phase-03-clean-code-design-patterns` exercise pattern: Vietnamese Javadoc, source topic and Q numbers, prerequisites, concrete IntelliJ first action, debugger/docs hint, code target, completion check. Categories `[CODE]`, `[DỰ ĐOÁN]`, `[THÍ NGHIỆM]`, `[TỰ TRẢ LỜI]`; use `ANSWER Qn`, `OBSERVATION Qn`, `SOLUTION-BEGIN throw Qn/Bn`, `SOLUTION-VALUE`. Keep exact source question as Q prompt. Prediction constants null on learner branch; experiments have smoke-only tests and no timing assertions; written answers remain ungraded. Every TODO is reached outside broad `assertThrows` so TODO cannot pass as intended rejection. Stripper audit covers source, test data, resource/config, expected values, comments and hints.

## File map and exercise coverage

One or more `ExNN_<Subject>.java` per topic, matching `ExNN_<Subject>Test.java` in same `phase05.dNN_*` package. Group related questions with a coherent test boundary; don't force one file/question. `Qn` ownership below is exhaustive, one occurrence each. Topic source question wording is retained verbatim in corresponding Javadoc.

| Topic / package | Exercise group(s) and question mapping | Lab |
|---|---|---|
| Q category rule | Per topic Q1–Q5: Q1 `[DỰ ĐOÁN]`, Q2 `[CODE]`, Q3 `[TỰ TRẢ LỜI]`, Q4 `[THÍ NGHIỆM]`, Q5 `[CODE]`; preserve wording verbatim and use prediction only when observable behavior can be asserted | — |
| 1 `d01_di` | `Ex01_ConstructorInjection`: Q1–5 | B01 |
| 2 `d02_beans_lifecycle` | `Ex01_ScopeAndState`: Q1–5 | B02 |
| 3 `d03_configuration` | `Ex01_AutoConfigurationAndSecrets`: Q1–5 | B03 |
| 4 `d04_mvc` | `Ex01_RequestPipeline`: Q1–5 | B04 |
| 5 `d05_rest_pagination` | `Ex01_HttpAndPaging`: Q1–5 | B05 |
| 6 `d06_dto_validation` | `Ex01_BoundaryValidation`: Q1–5 | B06 |
| 7 `d07_problem_details` | `Ex01_ErrorContract`: Q1–5 | B07 |
| 8 `d08_jpa_queries` | `Ex01_QueryBehavior`: Q1–5 | B08 |
| 9 `d09_flyway` | `Ex01_SchemaOwnership`: Q1–5 | B09 |
| 10 `d10_transactions` | `Ex01_ProxyAndRollback`: Q1–5 | B10 |
| 11 `d11_concurrency` | `Ex01_StockRace`: Q1–5 | B11 |
| 12 `d12_aop` | `Ex01_ProxyBoundary`: Q1–5 | B12 |
| 13 `d13_security` | `Ex01_AuthenticationAuthorization`: Q1–5 | B13 |
| 14 `d14_oauth_jwt` | `Ex01_TokenBoundary`: Q1–5 | B14 |
| 15 `d15_testing` | `Ex01_TestBoundaries`: Q1–5 | B15 |
| 16 `d16_actuator` | `Ex01_HealthAndLogging`: Q1–5 | B16 |
| Capstone `d17_capstone` | `OrderApiApplication`, focused DTO/domain/service/repository/controller/security/config/migration classes; B1–B8 | Integrated |

**Question wording register** (copy exact question text from `05-spring-boot.md`; question numbers are scoped by topic):

1. IoC khác DI ở điểm nào?; Vì sao constructor injection thường rõ hơn field injection?; Một class thuần Java có cần annotation Spring để test không?; Khi nào wiring thủ công dễ hiểu hơn component scan?; DI có tự làm thiết kế tốt nếu dependency graph vẫn rối không?
2. Spring singleton scope bảo đảm điều gì và không bảo đảm điều gì?; Vì sao field mutable trong controller/service có thể hỏng dưới tải đồng thời?; Scope `prototype` có nghĩa là Spring quản lý toàn bộ lifecycle sau khi inject không?; Khi nào chọn `@Bean` thay vì `@Component`?; Có thể giải quyết mọi state bằng cách đổi bean sang request scope không?
3. Điều kiện nào thường khiến auto-configuration tạo hoặc bỏ qua bean?; Khi cấu hình không như mong đợi, tìm bằng chứng ở đâu trước khi thêm bean mới?; Profile khác secret management thế nào?; Vì sao giá trị mặc định an toàn khác với hard-code credential?; Nên phản ứng thế nào khi thiếu signing key ở môi trường production?
4. Filter và Spring MVC interceptor khác vị trí trong request pipeline ra sao?; Deserialization khác serialization thế nào?; Vì sao controller không nên gọi `new` để khởi tạo dependency?; Binding lỗi có xảy ra trước khi service được gọi không?; API nên trả gì khi client gửi JSON sai cú pháp hoặc media type không hỗ trợ?
5. `401` và `403` khác nhau thế nào?; Khi nào trả `201` thay vì `200`?; Vì sao pagination cần sort ổn định?; Rủi ro của page size không giới hạn là gì?; `409` phù hợp với xung đột nào trong đặt hàng?
6. Vì sao không trả JPA entity từ controller?; `@Valid` kiểm tra loại quy tắc nào, và không thể thay thế quy tắc nào?; Validation DTO có thể ngăn race condition giữa hai transaction không?; Vì sao cần cả check nghiệp vụ lẫn constraint DB?; Khi nào response DTO nên khác request DTO?
7. Vì sao lỗi validation thường là 4xx nhưng lỗi DB bất ngờ thường là 5xx?; `ProblemDetail` giải quyết phần nào của error contract?; Vì sao Security exception không nhất thiết tới `@RestControllerAdvice`?; Thông tin nào không được trả trong error body?; Có nên trả nguyên thông điệp exception từ persistence layer không?
8. `save` khác commit như thế nào?; Vì sao test chỉ đếm repository call chưa đủ để phát hiện N+1?; Collection fetch join ảnh hưởng pagination ra sao?; Vì sao map entity sang DTO sau khi persistence context đóng cần query/fetch plan rõ?; Khi nào projection hiệu quả hơn load entity graph đầy đủ?
9. Vì sao migration đã chạy không nên sửa trực tiếp?; `ddl-auto=validate` kiểm tra gì và không làm gì?; Tại sao migration là owner của schema khi đã chọn Flyway?; Khi nào cần index/constraint trong migration thay vì chỉ kiểm tra ở Java?; Flyway PostgreSQL có thể cần module hỗ trợ database riêng nào ở Boot 4?
10. Vì sao self-invocation bỏ qua transaction proxy?; Mặc định checked exception có rollback không?; `readOnly=true` có bảo đảm DB từ chối write không?; `UnexpectedRollbackException` có thể xuất hiện khi nào?; Vì sao không gọi dịch vụ payment từ xa bên trong DB transaction dài?
11. Vì sao check rồi update trong Java có race condition?; `@Version` phát hiện điều gì?; Conditional update giải quyết phần nào của race?; Vì sao `synchronized` không thể thay row lock/version trong ứng dụng nhiều instance?; Khi conflict, API có thể trả status nào và vì sao?
12. Vì sao proxy không tự intercept lời gọi nội bộ cùng object?; Khi nào AOP phù hợp hơn lời gọi method tường minh?; Dấu hiệu nào cho thấy aspect đang che giấu business flow?; Vì sao log toàn bộ request body nguy hiểm?; Nên kiểm chứng transaction/security aspect ở test nào?
13. Authentication khác authorization thế nào?; Vì sao role ở endpoint chưa đủ để bảo vệ order của khách hàng khác?; Ai là nguồn sự thật của current user ID?; `401` và `403` nên được phát ra ở điều kiện nào?; Vì sao nên ưu tiên security filter/config chuẩn thay vì JWT filter tự viết?
14. Access token và ID token có cùng mục đích không?; Vì sao xác minh issuer/signature nhưng bỏ audience vẫn có thể nhận token không dành cho API?; `ROLE_` và `SCOPE_` khác semantics thế nào?; Stateless API có luôn an toàn khi tắt CSRF không?; CORS có thể thay thế kiểm tra authorization không?
15. Test nào chứng minh validation/binding của MVC?; Vì sao unit test repository mock không chứng minh SQL đúng?; Vì sao H2 không đủ cho PostgreSQL lock/migration behavior?; Khi nào `@SpringBootTest` đáng chi phí?; Vì sao test token decoder thật khác test controller với mock claims?
16. Liveness khác readiness thế nào?; Vì sao không expose mọi actuator endpoint ra internet?; Vì sao dependency failure không phải lúc nào cũng nên làm liveness fail?; Label metric theo user ID gây vấn đề gì?; Dữ liệu nào không được ghi vào log?

## Lab contracts

Labs keep topic B IDs independent of Q IDs and include learner step, test/evidence, and answer marker:

- B01: pure service constructor-injects repository; hand-written fake test without Spring context.
- B02: demonstrate unsafe mutable `currentUser` on singleton bean conceptually/testably, remove it, pass principal/context as method input.
- B03: local/test DB config; required production secret fails fast; no committed credential or secret log.
- B04: MockMvc POST DTO; malformed JSON, unsupported media type, response content type; prove service not invoked on binding failure.
- B05: product-list bounded page and sort allowlist/stable tie-breaker; test 201 plus `Location` where resource created.
- B06: create-product request DTO, bean validation plus DB constraint, response omits entity relation/internal field.
- B07: Problem Details for validation/not-found/conflict and filter-boundary auth error.
- B08: PostgreSQL-backed `GET /api/orders?page=0&size=20`; require `SCOPE_orders.read`, apply owner predicate in SQL before paging, stable `(created_at,id)` order; assert bounded executed SQL count, no foreign-owner rows, and stable tie ordering, not repository call count.
- B09: marked Java-owned Flyway migration; fresh PostgreSQL database migrates; Hibernate validates schema.
- B10: PostgreSQL integration test proves whole transaction rollback after injected mid-flow runtime failure; separate checked-exception/rollback-only demonstration if included.
- B11: independent DB connections/transactions and bounded coordination compete for stock; assert stock conservation and no oversell.
- B12: inspect Spring proxy and test self-invocation limitation; no new generic aspect.
- B13: authenticated owner/foreign owner boundary and scope/role decision; principal is source of customer identity.
- B14: locally signed real JWT decoder tests for signature, `exp`, `nbf`, issuer, audience and scopes; separate MVC tests with mock claims.
- B15: unit, MVC, PostgreSQL migration/constraint and security-filter tests at correct boundaries.
- B16: minimal health exposure by default; `local-lab` exposes standard Actuator Hikari acquire meter only at localhost/test-only boundary, requiring `SCOPE_metrics.read`; products list requires `SCOPE_products.read`. Verify meter name/BaseUnit with selected Boot/Hikari docs or smoke; unresolved meter blocks measurement. Safe log assertion contains no token/password; no high-cardinality user metric. Phase09 records actual cumulative COUNT/TOTAL_TIME seconds in CSV (`run,snapshot,capturedAtUtc,metricName,pool,count,totalTimeSeconds`), baseline/changed × start/end, same pool, UTC-bracketed measurement, deltaCount > 0; PowerShell stdlib parses Actuator JSON, no app custom endpoint or custom JSON parser/dep.

## Capstone interfaces and sequence

Exact learner-facing service/API signatures (records may add validation, no aliases):

```java
public record LineRequest(long productId, int quantity) {}
public record CreateOrderRequest(List<LineRequest> items) {}
public record OrderItemResponse(long productId, int quantity, BigDecimal unitPrice) {}
public record OrderResponse(long id, String status, String currency,
                            BigDecimal total, List<OrderItemResponse> items,
                            Instant createdAt) {}
public record ProductResponse(long id, String name, BigDecimal price, int stock) {}
public record PageResponse<T>(List<T> items, int page, int size, long totalItems, int totalPages) {}

public interface OrderService {
    OrderResponse placeOrder(String subject, CreateOrderRequest request);
    OrderResponse getOrder(String subject, long orderId);
    OrderResponse cancelOrder(String subject, long orderId);
    PageResponse<ProductResponse> productPage(int page, int size, String sort);
    PageResponse<OrderResponse> orderPage(String subject, int page, int size);
}
```

`placeOrder` receives trusted JWT subject only from authenticated principal mapping. Product/order list authorization: `SCOPE_products.read` for products and `SCOPE_orders.read` for order reads; create/cancel require `SCOPE_orders.write`. `OrderResponse` item price is snapshot USD. `productPage(int page,int size,String sort)` clamps/rejects page outside zero-based valid range or size outside 1–100 and applies product sort allowlist `id`, `name`, `price` plus ID tie-breaker. `orderPage(String subject,int page,int size)` applies SQL owner predicate before paging, returns only that subject's orders, and orders by `(created_at,id)`; page indexes are zero-based and size is 1–100.

Test support API is fixed: `phase05.support.PostgresFixture.start()`, `.dataSource()`, `.reset(String schemaSql, String seedSql) throws SQLException`, `.jdbcUrl()`, `.username()`, `.password()`, `.close()`. Security task owns `phase05.support.TestJwt`: `signed(Map<String,Object> overrides)` returns compact locally signed JWT using fixed issuer/time defaults that overrides replace; `publicJwkSet()` returns JSON JWK Set; `decoder()` builds real decoder configured for test issuer/audience and allowed algorithm. Overrides may replace `iss`, `aud`, `exp`, `nbf`, `scope`; signature/algorithm negative tests use dedicated fixture methods `signedWithWrongKey(claims)` and `signedWithAlgorithm(algorithm, claims)`. MVC mock-auth tests do not use this decoder fixture.

Capstone work IDs are B1–B8 only (stripper supports B-number markers, not dotted identifiers): B1 marked Java migration creates schema; B2 DTO/domain validation; B3 repositories/query paging; B4 transactional order/stock; B5 JWT/security/subject mapping; B6 controllers/error contract; B7 DB/security/concurrency/rollback acceptance; B8 README and rerunnable evidence. PostgreSQL fixture helper `seedCustomer(long id,String subject)` and `seedProduct(long id,String name,BigDecimal usdPrice,int stock)` may be added after schema freeze.

Representative capstone test/config/migration pattern (executed migration SQL remains inside the marked Java supplier):

```java
@SpringBootTest(classes = OrderApiApplication.class)
@ActiveProfiles("test")
class OrderServicePostgresTest {
    static final PostgresFixture DB = PostgresFixture.start();
    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", DB::jdbcUrl);
        properties.add("spring.datasource.username", DB::username);
        properties.add("spring.datasource.password", DB::password);
    }
    @Autowired OrderService orders;
    JdbcTemplate jdbc = new JdbcTemplate(DB.dataSource());
    @BeforeEach void seedAfterFlywayMigration() throws SQLException {
        jdbc.update("delete from order_items");
        jdbc.update("delete from orders");
        jdbc.update("delete from products");
        jdbc.update("delete from customers");
        jdbc.update("insert into customers(id,subject) values(?,?)", 7L, "alice");
        jdbc.update("insert into products(id,name,price,stock) values(?,?,?,?)", 11L, "Tea", new BigDecimal("4.25"), 3);
    }
    @AfterAll static void closeDatabase() { DB.close(); }
    @Test void placesValidOrderThenRejectsDuplicateLines() {
        var valid = orders.placeOrder("alice", new CreateOrderRequest(List.of(new LineRequest(11, 1))));
        assertEquals(new BigDecimal("4.25"), valid.items().getFirst().unitPrice());
        var duplicate = new CreateOrderRequest(List.of(new LineRequest(11, 1), new LineRequest(11, 2)));
        assertThrows(InvalidOrder.class, () -> orders.placeOrder("alice", duplicate));
        assertEquals(2, jdbc.queryForObject("select stock from products where id=11", Integer.class));
        assertEquals(1, jdbc.queryForObject("select count(*) from orders", Integer.class));
    }
}

static String sql() {
    // SOLUTION-BEGIN throw B1
    return "create table customers (id bigint primary key, subject text unique not null); "
         + "create table products (id bigint primary key, name text not null, price numeric(19,2) not null, stock integer not null check(stock>=0)); "
         + "create table orders (id bigint generated by default as identity primary key, customer_id bigint not null references customers(id), status text not null check(status in ('NEW','CANCELLED')), currency char(3) not null check(currency='USD'), created_at timestamptz not null); "
         + "create table order_items (order_id bigint not null references orders(id), product_id bigint not null references products(id), quantity integer not null check(quantity between 1 and 1000), unit_price numeric(19,2) not null, primary key(order_id,product_id))";
    // SOLUTION-END
}
```

Use fixture helper lifecycle configured once per class and isolated schema/container when running parallel; never concurrently reset a shared schema. Test migration from fresh DB through Flyway, not by treating `reset` SQL as production migration. CapstoneTestConfig is minimal example; production app uses `@SpringBootApplication(scanBasePackages="phase05.d17_capstone")` and explicit JPA entity/repository scan rooted in `d17_capstone`.

## Capstone sequence and acceptance

B1 marked Java migration creates four tables and constraints; PostgreSQL 18 starts without silent skip. B2 domain/request/response types encode quantity, duplicate and server-price rules. B3 persistence repositories and SQL-backed product/order pagination; order page applies owner predicate in SQL and stable `(created_at,id)` order. B4 service transaction creates order/items and atomically decrements stock. B5 security decoder and principal-to-customer mapping; no owner in request. B6 controller DTOs for product list, order create/get/cancel/list and RFC 9457 error contract. B7 independent-transaction tests prove insufficient/concurrent stock and injected failure do not leave partial writes; owner A cannot read/cancel B order; two simultaneous cancels cause one state transition/restock only; repeat cancellation conflict. B8 README run/build/test steps and evidence.

Representative capstone B3/B7 order-page test against the fixed service contract:

```java
@Test
void orderPageFiltersOwnerBeforePagingAndBreaksTimestampTiesById() {
    seedOrders("alice", List.of(order(4, sameTime), order(9, sameTime), order(10, olderTime)));
    seedOrders("bob", List.of(order(1, sameTime)));
    var page = service.orderPage("alice", 0, 2);
    assertEquals(List.of(10L, 4L), page.items().stream().map(OrderResponse::id).toList());
    assertEquals(3, page.totalItems());
    assertThatSqlQueryCountIsAtMost(2); // count + page SELECT, measured statements only
}
```

Capstone B3/B7 sends `GET /api/orders?page=0&size=20` with `SCOPE_orders.read`, asserts no Bob order leaks to Alice and stable `(created_at,id)` tie order. Independent topic B08 remains owned by `d08_jpa_queries/Ex01_QueryBehavior`; it tests PostgreSQL-backed paginated order-list SQL and bounded actual statement count.

Acceptance: clean DB migrates; schema validates; POST uses server price and returns 201/Location; malformed/invalid order rejected; stock cannot go below zero or oversell; rollback preserves all-or-nothing state; user B cannot access A order and vice versa; foreign order is 404; JWT invalid signature/algorithm/time/issuer/audience rejected; required scope enforced; auth error preserves `WWW-Authenticate`; paging capped/stable; actual executed SQL query evidence shows bounded query count and DB paging; actuator exposure limited; no secret/token/PII leak; learner skeleton compiles and fails only at deliberate exercise markers, with narrow documented allowed-green experiment tests.

## Review focus

1. Order with empty lines, quantity 0/1001, duplicate IDs, or client price/customer ID must fail or be ignored by documented policy; pin in B2/B4 tests.
2. Two concurrent purchases for last stock must not oversell or partially persist; pin in B11/B7 with independent transactions and final DB state.
3. Foreign-owner guessed ID must return 404 for read/cancel; pin in B13/B7.
4. Malformed JSON / unsupported content type must fail before service invocation with correct 4xx contract; pin in B04.
5. JWT with valid signature but wrong issuer/audience, expired/not-yet-valid token, wrong algorithm or missing scope must be rejected while retaining auth headers; pin in B14/B5.

## Verification boundary

Module build/test, full PostgreSQL-backed integration run, source question count (80 exactly once), lab count (16 plus capstone), stripping audit, learner compile/red reason audit, README commands and `git diff --check`. Docker-dependent full tests are mandatory; report blocked if unavailable. Do not implement or claim tests here.
