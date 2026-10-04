# Phase 05 Spring Boot Exercises Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build full Phase 05 learning exercises for 80 questions, 16 labs, and one secured PostgreSQL order API capstone.

**Architecture:** Add an independent Spring Boot module aggregated by root, with isolated per-topic exercise configurations and a capstone-only application. Keep answers in Java solution markers, use real PostgreSQL for persistence/security integration, and keep tests at unit, MVC, decoder, and database boundaries.

**Tech Stack:** Java 21, Spring Boot 4.1.1 independent parent, JUnit version managed by Boot, Spring MVC/Validation/Data JPA/Security Resource Server/Actuator, Flyway, PostgreSQL 18, Docker-backed `phase05.support.PostgresFixture`.

**Review status:** Draft for artifact review; umbrella architecture approved; no product implementation before design/plan review.

**Spec:** `docs/superpowers/specs/2026-10-04-phase05-exercises-design.md`

## Global Constraints

- Module: `phase-05-spring-boot`; package root `phase05`; Java 21; Boot parent `4.1.1`; use explicit coordinates and `<relativePath/>`.
- Four tables only: `customers`, `products`, `orders`, `order_items`; stock is `products.stock`; IDs BIGINT; USD price `BigDecimal`/`NUMERIC(19,2)`; order item stores price snapshot; order status starts `NEW`, transitions to `CANCELLED`.
- Order lines nonempty; quantity 1–1000; duplicate product IDs rejected; no client-owned customer/price/currency trust; page zero-based, size 1–100; product sorting by allowlist `id`,`name`,`price` plus ID tie-breaker; order list `(created_at,id)`. Cancel uses guarded state transition/CAS with atomic restock; concurrent cancel can restock once only.
- Capstone stripper marker IDs are B1–B8 (not dotted); topic labs remain B01–B16. Q category assignment per topic: Q1 `[DỰ ĐOÁN]`, Q2 `[CODE]`, Q3 `[TỰ TRẢ LỜI]`, Q4 `[THÍ NGHIỆM]`, Q5 `[CODE]`; source wording stays unchanged.
- Order/items/stock share one transaction; conditional stock updates check affected-row count; no H2 or silent Docker skip.
- `GET /api/orders?page=0&size=20` requires `SCOPE_orders.read`; implement `orderPage(String subject,int page,int size)` with owner SQL predicate before pagination and stable `(created_at,id)` order. `GET /api/products` remains Phase09 measurement endpoint.
- Flyway owns schema; Hibernate uses `ddl-auto=validate`; `spring.jpa.open-in-view=false`; SQL/DDL answer code stays in stripper-visible Java markers.
- Capstone is sole executable app scan; topic tests/configurations explicitly scoped, with no sibling package scan.
- Bearer-only `Authorization` header; no cookie auth; CSRF/CORS decisions follow spec; JWT validates algorithm, signature, exp, nbf, issuer, audience, scopes with locally signed test tokens.
- Owner derives from validated principal; foreign order returns 404; cancellation is one-time and atomic with restock; repeat cancel returns 409.
- Preserve auth status and `WWW-Authenticate`; no token/secret/PII leaks. Default Actuator exposes health only. `local-lab` exposes standard `/actuator/metrics/hikaricp.connections.acquire` on localhost management bind or test-only access, protected by bearer `SCOPE_metrics.read`; products list requires `SCOPE_products.read`. No custom metrics endpoint. Meter must be acquisition timer (`COUNT`, `TOTAL_TIME` seconds), not checkout lease usage; verify selected Boot/Hikari meter name/BaseUnit via documentation or smoke before implementation.
- Phase09 reads `GET /api/products?page=0&size=20` and standard Actuator JSON snapshots, exporting CSV with PowerShell stdlib (no custom parser/dependency). Header: `run,snapshot,capturedAtUtc,metricName,pool,count,totalTimeSeconds`; rows baseline/changed × start/end. Use actual cumulative COUNT/TOTAL_TIME seconds from same Hikari pool, UTC-bracket measured interval, monotonic `deltaCount>0`; never infer acquisition wait from HTTP latency or fabricate zero.
- Foundation owns root aggregation, POMs and basic fixture only: `phase05.support.PostgresFixture` with `start()`, `dataSource()`, `reset(schemaSql, seedSql)`, `jdbcUrl()`, `username()`, `password()`, `close()`. Phase05 security owns JWT fixture; capstone test-fixture task may add `seedCustomer(long id,String subject)` and `seedProduct(long id,String name,BigDecimal usdPrice,int stock)` after schema freeze.

## Review Focus

1. Empty/duplicate/invalid quantity and client-owned price/customer inputs: B2/B4 tests assert rejection/ignore policy.
2. Concurrent last-stock purchase, simultaneous cancellation, and mid-transaction failure: B11/B7 assert no oversell, one restock, and whole-transaction state.
3. Foreign-owner ID guessing: B13/B7 assert 404 for get/cancel.
4. Malformed JSON/media type: B04 asserts service not invoked and correct 4xx.
5. Valid signature with wrong issuer/audience/time/algorithm or missing scope: B14/B5 assert decoder/filter rejection and auth headers (scope absence is 403 authorization, not decoder rejection).

---

## File structure

- `phase-05-spring-boot/src/main/java/phase05/d01_di` … `d16_actuator`: topic Java exercises; one or more `ExNN_*.java` by coherent concept.
- Matching tests in `src/test/java/phase05/dNN_*/ExNN_*Test.java`; topic-local `@Configuration`/test application classes only where needed.
- `src/main/java/phase05/d17_capstone`: capstone application, API DTOs, domain/service, repository, security/configuration, and Java-owned migration content.
- `src/test/java/phase05/d17_capstone`: PostgreSQL integration, MVC, decoder, ownership, transaction/concurrency tests; JWT/key fixture owned by security task; seed helpers can be added after schema freeze.
- `src/test/resources`: only non-answer fixtures such as signed token public test JWK material if necessary, never answer-bearing YAML/SQL/config.
- `phase-05-spring-boot/README.md`: exact Docker/build/run/test and local-token guidance.
- `docs/superpowers/specs/2026-10-04-phase05-exercises-design.md`: only update if a confirmed interface contract requires exact clarification; no curriculum or unrelated docs edits.

All Q numbers and preserved source wording are in the design spec's coverage table/register. Each task owns listed Qs and corresponding B-lab; no task duplicates question text ownership.

### Task 1: Topic groups 1–4 — DI, lifecycle, configuration, MVC

**Files:**
- Create: `phase-05-spring-boot/src/main/java/phase05/d01_di/Ex01_ConstructorInjection.java` and matching `src/test/java/phase05/d01_di/Ex01_ConstructorInjectionTest.java`
- Create: `phase-05-spring-boot/src/main/java/phase05/d02_beans_lifecycle/Ex01_ScopeAndState.java` and matching test
- Create: `phase-05-spring-boot/src/main/java/phase05/d03_configuration/Ex01_AutoConfigurationAndSecrets.java` and matching test
- Create: `phase-05-spring-boot/src/main/java/phase05/d04_mvc/Ex01_RequestPipeline.java` and matching test/configuration

**Interfaces:** Consumes independent module POM from foundation and exercise marker conventions from spec. Produces pure Java `OrderRepository`/fake service demonstration, isolated MVC request fixture and B01–B04 evidence. Own Q1–Q5 in each topic; B01–B04.

- [ ] **Step 1: Write focused tests first.** Unit-test service with a hand-written fake and no Spring context. For lifecycle/config use safe explicit example values, not real secrets. MVC test posts valid DTO and malformed JSON; use a mock service to prove malformed request never calls it.

```java
@Test
void malformedJsonFailsBeforeServiceCall() throws Exception {
    mvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{"))
        .andExpect(status().isBadRequest());
    verify(service, never()).place(any());
}
```

- [ ] **Step 2: Verify red for intended reason.** Run `./mvnw -f phase-05-spring-boot/pom.xml -Dtest='phase05.d01_di.*Test,phase05.d02_beans_lifecycle.*Test,phase05.d03_configuration.*Test,phase05.d04_mvc.*Test' test`; confirm only exercise TODO/prediction failures, not Spring context/package-scan errors.
- [ ] **Step 3: Add minimal exercises.** Add Vietnamese Javadoc for preserved source Q wording, runnable examples, answer blocks and marked B01–B04 code. No broad component scanning; topic test config imports only the exercise under test.
- [ ] **Step 4: Run tests and learner checks.** Run targeted tests on solutions; strip Java markers and compile learner module, verifying only intended unanswered markers fail.

### Task 2: Topic groups 5–7 — REST, DTO validation, Problem Details

**Files:**
- Create: `src/main/java/phase05/d05_rest_pagination/Ex01_HttpAndPaging.java` and matching test
- Create: `src/main/java/phase05/d06_dto_validation/Ex01_BoundaryValidation.java` and matching test
- Create: `src/main/java/phase05/d07_problem_details/Ex01_ErrorContract.java` and matching test

**Interfaces:** Consumes DTO/request-pipeline concepts from Task 1. Produces bounded page/sort examples, DTO validation and RFC 9457 MVC/filter error demonstrations. Own Q1–Q5 and B05–B07.

- [ ] **Step 1: Write behavior tests.** Cover page size upper bound, rejected sort key, stable ID tie-breaker, `201` plus `Location`, field validation, entity-free response, and Problem Details for validation/not-found/conflict/security. Assert malformed input does not reach service (Review Focus 4).

```java
@Test
void createReturnsLocationAndProblemType() throws Exception {
    mvc.perform(post("/api/products").contentType(APPLICATION_JSON)
            .content("{\"name\":\"Tea\",\"price\":\"2.00\"}"))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"));
}
```

- [ ] **Step 2: Verify red, then author exercises.** Run targeted Maven test filter for `phase05.d05_rest_pagination.*Test`, `d06_dto_validation`, `d07_problem_details`; preserve original Q text, include answer markers and B05–B07 checks.
- [ ] **Step 3: Verify responses and skeleton.** Confirm exact status/content type/body fields and no entity relation/internal property; run module test and stripper learner compile.

### Task 3: Topic groups 8–12 — JPA, migration, transaction, concurrency, AOP

**Files:**
- Create: `src/main/java/phase05/d08_jpa_queries/Ex01_QueryBehavior.java` and matching test
- Create: `src/main/java/phase05/d09_flyway/Ex01_SchemaOwnership.java` and matching test
- Create: `src/main/java/phase05/d10_transactions/Ex01_ProxyAndRollback.java` and matching test
- Create: `src/main/java/phase05/d11_concurrency/Ex01_StockRace.java` and matching test
- Create: `src/main/java/phase05/d12_aop/Ex01_ProxyBoundary.java` and matching test

**Interfaces:** Consumes `PostgresFixture` API and schema/query constraints from approved design. Produces explicit transaction semantics and PostgreSQL-backed evidence; own Q1–Q5 and B08–B12. Use separate fixture/schema lifecycle per test class; no parallel destructive resets. No seed helper until capstone schema is frozen.

- [ ] **Step 1: Write PostgreSQL tests.** Verify Flyway schema creation/validation, real query count via executed SQL instrumentation (Hibernate statistics or `StatementInspector`), database pagination, rollback after injected failure, self-invocation limitation and concurrent stock conservation. Use independent connections/transactions and bounded latches, not test-managed transaction or sleeps.

```java
@Test
void failureAfterStockUpdateRollsBackOrderAndStock() {
    assertThrows(InjectedFailure.class, () -> service.placeOrder(subject, request)); // test-only injected failure seam configured for this test
    assertEquals(5, jdbc.queryForObject("select stock from products where id=?", Integer.class, productId));
    assertEquals(0, jdbc.queryForObject("select count(*) from orders", Integer.class));
}
```

- [ ] **Step 2: Run against required PostgreSQL.** Use Docker-backed fixture; fail/report blocker if daemon/image unavailable. Confirm test proves committed state via separate query/transaction and is not accidentally wrapped in test transaction.
- [ ] **Step 3: Author labs and verify.** Java-mark SQL/migration answer content; no answer resources. Targeted tests, full module tests, then solution stripping and skeleton compile.

### Task 4: Topic groups 13–16 — Security, JWT, test boundaries, Actuator

**Files:**
- Create: `src/main/java/phase05/d13_security/Ex01_AuthenticationAuthorization.java` and matching test
- Create: `src/main/java/phase05/d14_oauth_jwt/Ex01_TokenBoundary.java` and matching test
- Create: `src/main/java/phase05/d15_testing/Ex01_TestBoundaries.java` and matching test
- Create: `src/main/java/phase05/d16_actuator/Ex01_HealthAndLogging.java` and matching test
- Create: module-local test-only JWT key/token fixture under `src/test/java/phase05/support/` (security task owner)

**Interfaces:** Consumes Q/B contracts and capstone token policy. Produces test-only signed-token builder/JWK and tested decoder validators, distinct mock-principal examples, safe health/logging config. Own Q1–Q5, B13–B16. Do not use unsigned token shortcuts for decoder tests. Local-lab standard Actuator metrics route and scope are fixed by Global Constraints; no custom endpoint.

- [ ] **Step 1: Write real decoder and filter-chain tests.** `TestJwt.signed(overrides)` generates real local token; test accepted token and reject wrong signature, algorithm, `exp`, `nbf`, issuer and audience. Missing scope is an authorization filter 403, not decoder failure; assert 401/403 retain correct `WWW-Authenticate`. MVC mock claims remain a separate boundary.

```java
@Test
void wrongAudienceIsRejectedByRealDecoder() {
    String token = TestJwt.signed(Map.of("aud", "other", "scope", "orders.read"));
    assertThrows(JwtException.class, () -> TestJwt.decoder().decode(token));
}
```

- [ ] **Step 2: Add minimal Java configuration.** Expose only health by default; in `local-lab`, enable standard Actuator `metrics` exposure on localhost management bind (or test-only access) and protect with `SCOPE_metrics.read`. Require `SCOPE_products.read` for `GET /api/products`; `orders.read/write` scopes for order routes. No custom metrics endpoint, no cookie auth.
- [ ] **Step 3: Verify Hikari meter metadata before wiring measurement.** Selected Boot/Hikari docs or smoke must confirm exact `hikaricp.connections.acquire` meter and BaseUnit is acquisition timer (`COUNT`, `TOTAL_TIME` seconds), not checkout/lease usage. If not confirmed, keep measurement blocked; never substitute values.
- [ ] **Step 4: Test local measurement/auth boundary.** Default profile exposes health only; metrics path unavailable. `local-lab` metrics request without `SCOPE_metrics.read` is denied; with scope, standard meter response reports actual same-pool cumulative count/total seconds. Test delta count grows after acquisition. Product endpoint rejects absent `SCOPE_products.read`.
- [ ] **Step 5: Test logging and errors.** Assert no token/secret sentinel in logs or error body and auth error preserves `WWW-Authenticate`; run focused tests and stripper leak audit.

### Task 5: Capstone schema and domain/API interfaces

`PostgresFixture.reset(String schemaSql,String seedSql) throws SQLException`; JUnit setup methods calling it declare `throws SQLException`.

**Files:**
- Create: `src/main/java/phase05/d17_capstone/OrderApiApplication.java`
- Create: `src/main/java/phase05/d17_capstone/Customer.java`, `Product.java`, `PurchaseOrder.java`, `OrderItem.java`, request/response DTOs and enums as needed
- Create: `src/main/java/phase05/d17_capstone/CapstoneSchema.java` (Java Flyway migration or Java supplier invoking marked SQL)
- Create: `src/test/java/phase05/d17_capstone/CapstoneSchemaTest.java`

**Interfaces:** Consumes `phase05.support.PostgresFixture`: `start()`, `dataSource()`, `reset(String schemaSql,String seedSql) throws SQLException`, `jdbcUrl()`, `username()`, `password()`, `close()`. Produces four-table schema (USD `NUMERIC(19,2)`, statuses `NEW`/`CANCELLED`), canonical seed helpers, and these exact interfaces consumed by Tasks 6–7:

```java
record LineRequest(long productId, int quantity) {}
record CreateOrderRequest(List<LineRequest> items) {}
record OrderItemResponse(long productId, int quantity, BigDecimal unitPrice) {}
record OrderResponse(long id, String status, String currency, BigDecimal total,
                     List<OrderItemResponse> items, Instant createdAt) {}
record ProductResponse(long id, String name, BigDecimal price, int stock) {}
record PageResponse<T>(List<T> items, int page, int size, long totalItems, int totalPages) {}
OrderResponse placeOrder(String subject, CreateOrderRequest request);
OrderResponse getOrder(String subject, long orderId);
OrderResponse cancelOrder(String subject, long orderId);
PageResponse<ProductResponse> productPage(int page, int size, String sort);
PageResponse<OrderResponse> orderPage(String subject, int page, int size);
```

Use exactly `placeOrder`, not a second `place`/`placeThenFail` alias; inject a package-private test failure hook only in a test-specific service configuration, not public API. Add `seedCustomer(long id,String subject)` and `seedProduct(long id,String name,BigDecimal usdPrice,int stock)` after schema freezes. Subject mapping is internal; never body-supplied.

- [ ] **Step 1: Write schema/DTO test first.** Fresh `PostgresFixture` starts PostgreSQL, runs Flyway migration and verifies exactly four named tables, foreign keys/checks/unique constraints, stock column and Hibernate validation. Test only record/domain-local validation: `new LineRequest(11,0)` is rejected and `CreateOrderRequest` rejects empty items if constructor validation is chosen. Do not call `OrderService` here; Task 6 creates it and owns service duplicate/quantity-write assertions.
- [ ] **Step 2: Run PostgreSQL schema test.** Verify schema absent before Flyway migration and present after; query committed state outside test transaction.
- [ ] **Step 3: Implement marked migration and exact API.** Keep marked SQL in `CapstoneSchema.sql()` per spec example; declare request/response records and `OrderService.placeOrder/getOrder/cancelOrder/productPage/orderPage` exact signatures. Freeze table shape and seed helpers only after test passes.
- [ ] **Step 4: Seed fixture and re-run.** Add `seedCustomer(long id,String subject)` and `seedProduct(long id,String name,BigDecimal usdPrice,int stock)` backed by JDBC; re-run migration/schema and audit stripped sources.
- [ ] **Step 5: Re-run learner stripper audit.** No unmarked SQL/YAML answer leaks.

### Task 6: Capstone persistence, order transaction, and product/order paging

**Files:**
- Create: `src/main/java/phase05/d17_capstone/OrderRepository.java`, `ProductRepository.java`, `OrderService.java`, `OrderController.java`, `ProductController.java`, and focused mapping/config classes only as needed
- Create: `src/test/java/phase05/d17_capstone/OrderServicePostgresTest.java`, `StockConcurrencyTest.java`, `OrderApiMvcTest.java` (B3/B7 capstone behavior; B08 remains Task 3 topic 8)

**Interfaces:** Consumes Task 5 records/signatures. Produces `POST /api/orders`, owner get/cancel and `GET /api/products?page=0&size=20`. Authorization scopes: create/cancel `SCOPE_orders.write`; order get/list `SCOPE_orders.read`; products `SCOPE_products.read`. `productPage(int page,int size,String sort)` clamps/rejects page size outside 1–100; product sort keys `id`,`name`,`price` with ID tie-breaker; `orderPage(String subject,int page,int size)` filters owner in SQL before paging and sorts `(created_at,id)`. Conditional stock and cancellation state updates inspect affected-row count. Own capstone B3/B7 persistence behavior; B08 remains Task 3’s topic-8 lab, not repeated here.

- [ ] **Step 1: Write failing service/MVC tests with frozen types.** Assert no client customer/price trust, reject empty/duplicate/quantity invalid lines, snapshot server product price, cap page size 100, product stable-ID/order created-ID sort, 201 plus `Location`, and whole transaction rollback on injected failure.

```java
@Test
void duplicateLinesDoNotWriteOrderOrStock() {
    seedProduct(11, "Tea", new BigDecimal("4.25"), 5);
    var request = new CreateOrderRequest(List.of(new LineRequest(11, 1), new LineRequest(11, 2)));
    assertThrows(InvalidOrder.class, () -> service.placeOrder("alice", request));
    assertEquals(5, stock(11));
    assertEquals(0, orderCount());
}
```

- [ ] **Step 2: Implement transactional behavior.** `placeOrder(String,CreateOrderRequest)` writes order/items and conditional stock update in one transaction; do not put network/payment effects in it. Resolve customer from trusted subject. `getOrder` and `cancelOrder` enforce owner in query/service; foreign gets 404. Implement `orderPage(String,int,int)` with customer predicate in SQL before pagination, `created_at,id` sort; implement `productPage(int,int,String)` with `id`,`name`,`price` allowlist and ID tie-breaker.
- [ ] **Step 3: Implement and verify capstone owner-paged list (B3/B7).** `orderPage("alice",0,2)` filters Alice in SQL, sorts `(created_at,id)`, and never returns Bob; assert response ownership and pagination behavior. Topic lab B08 remains in Task 3 and separately pins bounded SQL query count for paginated order-list retrieval.

- [ ] **Step 3: Test concurrency and rollback.** Two independent connections attempt purchase against last stock with bounded coordination. Assert at most one success, stock never negative, one committed order/items, and independent final-state read. Two simultaneous cancels use guarded `UPDATE orders SET status='CANCELLED' WHERE id=? AND status='NEW'`; only the winning transaction restocks, exactly once; loser gets 409. Repeat cancel returns 409.
- [ ] **Step 4: Verify list SQL and pagination.** Assert executed SQL count/statement instrumentation and DB LIMIT/OFFSET or equivalent; never infer N+1 from repository method calls.

### Task 7: Capstone JWT, security, Problem Details, health and measurement timer

**Files:**
- Create: `src/main/java/phase05/d17_capstone/CapstoneSecurity.java`, principal/customer resolver and Problem Details/filter handlers
- Create: `src/test/java/phase05/d17_capstone/SecurityIntegrationTest.java`, `OrderOwnershipTest.java`, `AuthErrorContractTest.java`
- Create/modify: `src/main/java/phase05/d17_capstone/` local profile security/Actuator configuration and tests; no custom metrics endpoint; Task 7 owns B5 security config and B7 security acceptance tests

**Interfaces:** Consumes Task 4 `phase05.support.TestJwt`: `signed(Map<String,Object> overrides)`, `publicJwkSet()`, `decoder()`, `signedWithWrongKey(claims)`, `signedWithAlgorithm(algorithm,claims)`; Task 6 exact endpoints/services. Produces deny-by-default bearer-only SecurityFilterChain, issuer/audience/scope validation, subject mapping, 404 ownership policy, protected minimal Actuator. Security errors retain `WWW-Authenticate`. Local-lab exposes standard Hikari connection acquisition meter only; no custom timer/endpoint. Scopes: `SCOPE_orders.write` for POST/cancel, `SCOPE_orders.read` for GET/list, `SCOPE_products.read` for product list, `SCOPE_metrics.read` for local-lab meter.

- [ ] **Step 1: Write failing filter/ownership tests.** Missing/invalid token → 401; authenticated no scope → 403; foreign order read/cancel → 404; owner succeeds; cancel once succeeds and repeat gives 409. Verify client-supplied owner ignored/unavailable.
- [ ] **Step 2: Wire standard resource-server decoder.** Configure allowlisted algorithm, signature, exp/nbf, issuer, audience and scope; no handwritten JWT filter. No cookie authentication; no CORS-as-auth.
- [ ] **Step 3: Verify built-in Hikari meter contract.** From selected Boot/Hikari docs or a smoke test, confirm exact meter name `hikaricp.connections.acquire` and BaseUnit; unresolved name/unit blocks measurement instead of substituting values. Exercise standard `/actuator/metrics/hikaricp.connections.acquire` only under `local-lab`, localhost management bind or test-only access, bearer `SCOPE_metrics.read`; test that it is unavailable by default and rejects missing scope. No custom endpoint or application timer.
- [ ] **Step 4: Validate Phase09 sampling boundary.** Test products endpoint requires `SCOPE_products.read`. Document CSV export consumer contract only: `run,snapshot,capturedAtUtc,metricName,pool,count,totalTimeSeconds`; four baseline/changed × start/end rows; same pool; actual cumulative count/time seconds; UTC bracket; `deltaCount>0`. No app CSV writer or HTTP-latency inference.
- [ ] **Step 5: Verify filter-level errors and minimal Actuator.** Assert body/status/content type/`WWW-Authenticate`, no token in response/log, only health default exposure, and metrics exposure limited to local-lab.

### Task 8: Coverage reconciliation, README, final module verification

**Files:**
- Modify: `phase-05-spring-boot/README.md`
- Modify: spec coverage matrix only for final stable filenames/contracts
- All Phase05 source/test files from Tasks 1–7

**Interfaces:** Consumes completed groups and foundation verification tools; produces 80-Q one-to-one coverage, 16 lab mappings plus capstone, learner/solution test evidence and runnable instructions.

- [ ] **Step 1: Reconcile Q/B IDs mechanically.** Count Q1–Q5 for each 16 topic once, plus exactly 16 topic lab IDs and capstone B1–B8; compare exact wording with `05-spring-boot.md`. Search markers/resources for answer leaks.
- [ ] **Step 2: Write README commands.** Include Docker PostgreSQL requirement/version, build command, full tests (no skip), local app startup, locally generated test token process, no production credential, API samples and module isolation. No live IdP instructions.
- [ ] **Step 3: Run solution suite.** Run module `mvn test`, including mandatory PostgreSQL tests; inspect skips/reports and prove migration, rollback, concurrency, JWT, ownership, actual SQL count, pagination, Actuator and timer evidence.
- [ ] **Step 4: Run learner-strip verification.** Strip solution markers and compile/test with explicit expected TODO/prediction failure reasons; fail on skipped tests or unrelated failures. Verify root legacy modules unaffected and `git diff --check` clean.

## Self-review

Coverage: every Q grouped once in spec mapping; Tasks 1–4 own topics 1–16 and B01–B16; Tasks 5–7 own capstone; Task 8 reconciles. Interfaces flow from frozen schema/DTOs to service/security. Four-table and schema constraints live in Task 5; rollback/concurrency in Task 6; token/ownership in Task 7. Five review-focus input classes each have explicit test ownership. No placeholder tasks, no third-party API signature assumed beyond explicit fixture contract. Required PostgreSQL tests do not auto-skip.
