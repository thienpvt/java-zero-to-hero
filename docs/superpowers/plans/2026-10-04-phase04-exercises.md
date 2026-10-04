# Phase 04 Exercises Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver 85 exact-wording questions, 17 runnable database labs, solved examples, learner skeletons and PostgreSQL-backed tests for Phase 04.

**Architecture:** Add one Java 21/JUnit 5 module with 17 disjoint packages, each pairing one `ExNN_*.java` with one `ExNN_*Test.java`. Use the foundation-owned PostgreSQL fixture for real DB behavior; keep learner answers inside Java stripping markers and keep SQL resources limited to fixed scaffolding.

**Tech Stack:** Java 21, Maven, JUnit 5, PostgreSQL 18, PostgreSQL JDBC, Jakarta Persistence, Hibernate ORM, HikariCP, Flyway, Testcontainers PostgreSQL artifact pinned by foundation plan.

**Spec:** `docs/superpowers/specs/2026-10-04-phase04-exercises-design.md`

## Global Constraints

- Preserve exactly 85 original question labels and wording, five per topic; deliver all 17 labs as `B1`–`B17`.
- Keep packages `phase04.d01_schema` through `phase04.d17_capstone` and exact source/test filenames from spec.
- Use exact four-table columns and constraints in spec; identity BIGINT IDs; status `NEW` or `CANCELLED`; stock in `products.stock`; price `NUMERIC(19,2)` / `BigDecimal`, fixed lab currency USD; UTC `Instant`; quantities 1–1000.
- Reject empty/duplicate-product orders; page size 1–100, stable `(created_at,id)` ordering; server-owned prices; customer-scoped reads.
- Use real PostgreSQL 18; no H2, mock-only DB correctness, or silent Docker skip. Foundation Docker readiness limit 300 seconds; Testcontainers startup separately bounded.
- Use `phase04.support.PostgresFixture`: `start()`, `dataSource()`, `jdbcUrl()`, `username()`, `password()`, `reset(String schemaSql,String seedSql) throws SQLException`, `close()`. Reset only between tests in class-local nonparallel classes. Fixed bootstrap never contains target learner-answer SQL; unrelated fixed schema/seed scaffolding for non-DDL labs is allowed.
- Topic 09 pool lab creates its own Hikari pool from fixture URL/credentials; never log credentials. Topic 16 alone owns Flyway lifecycle tests.
- All learner SQL/DDL/query/migration answers stay in marked Java methods/string suppliers. Resources may contain only fixed scaffolding independent from target learner answers.
- Concurrency uses separate connections/EntityManagers, gates before contested locks, bounded waits, no `sleep`; inspect committed state from a new connection.
- No exact timing golden values, planner-node assertions, unsignaled skipping, or false claims that DB rollback undoes external effects.

## Review Focus

1. Invalid order inputs (null/empty, quantity <1 or >1000, duplicate IDs) must fail without increasing stock — Task 6, B17.
2. One insufficient item after earlier valid item must rollback all order/items/stock — Tasks 3 and 6, B7/B17.
3. Two concurrent reservations must conserve stock and never oversell — Task 5, B14/B17.
4. Foreign customer reads must be empty; timestamp ties must paginate deterministically and enforce size 1–100 — Tasks 2 and 6, B6/B17.
5. SQL-looking values remain data and SQL errors propagate while pooled connections return — Task 3, B9.

---

## File Structure

- `phase-04-database-persistence/pom.xml`: module dependency/version pins from foundation contract; do not edit root POM.
- `phase-04-database-persistence/README.md`: setup, PostgreSQL/JDK versions, module commands, fixture, lab evidence.
- `phase-04-database-persistence/src/main/java/phase04/d01_schema/Ex01_RelationalSchema.java` through `.../d17_capstone/Ex01_OrderPersistence.java`: one topic's exact five Q labels, one B label, learner TODOs and stripped solution.
- Matching `src/test/java/phase04/d01_schema/Ex01_RelationalSchemaTest.java` through `.../d17_capstone/Ex01_OrderPersistenceTest.java`: paired Q/B coverage and real PostgreSQL tests.
- `src/test/java/phase04/support/PostgresFixture.java`: foundation-owned, consumed only; do not create or edit here. If foundation API differs, pause and reconcile spec before coding.
- `src/test/resources/`: only fixed safe bootstrap/schema seeds required by foundation; no learner solution SQL. Topic 16's Flyway migrations are Java-owned marked SQL, not answer resources.

## Execution Rules

Every task follows TDD and uses exact module command below. Confirm expected red reason is learner TODO/assertion, not Docker, dependency resolution, or fixture mismatch. Foundation contract must be available before database tasks; user selected Docker startup, so full run requires working Docker and PostgreSQL 18. Plan task batches are disjoint by package except Task 0 (foundation dependency contract) and Task 8 (integration README). Keep topic files compact and avoid shared abstractions beyond frozen spec signatures.

Common module commands (from repository root):

```powershell
./mvnw.cmd -f phase-04-database-persistence/pom.xml -Dtest=Ex01_RelationalSchemaTest test
./mvnw.cmd -f phase-04-database-persistence/pom.xml test
```

Full command must fail if Docker/PostgreSQL unavailable. Never add a Docker-optional annotation or H2 fallback.

### Task 0: Consume Frozen Foundation Contract

**Files:**
- Read: foundation plan `docs/superpowers/plans/2026-10-04-phase04-05-09-foundation.md` (owned by foundation planner)
- Read: `phase-04-database-persistence/pom.xml` and `src/test/java/phase04/support/PostgresFixture.java` (foundation-owned)
- Modify: this plan/spec only if the frozen contract changed; request coordinator update rather than changing foundation files.

**Interfaces:**
- Consumes: module reactor/POM and exact versions; `phase04.support.PostgresFixture.start()`, `dataSource()`, `jdbcUrl()`, `username()`, `password()`, `reset(String,String)`, `close()`.
- Produces: verified usable module command, foundation fixture lifecycle, exact pinned dependency set for later topics.

- [ ] **Step 1: Verify fixture contract and versions**

Check exact signatures, PostgreSQL image tag, startup/readiness timeouts and module Maven command against foundation deliverable. Confirm test reset uses class-local schema/seed strings and never ships learner SQL.

- [ ] **Step 2: Run a fixture-only smoke test**

Run: `./mvnw.cmd -f phase-04-database-persistence/pom.xml -Dtest=PostgresFixtureTest test`
Expected: PASS against PostgreSQL 18; unavailable Docker is a blocking error, not a skip.

- [ ] **Step 3: Gate dependent tasks**

Do not begin Tasks 1–7 until method names and imports are fixed. Do not implement a local duplicate fixture.

### Task 1: Schema, Constraints, and SQL Read Model (Topics 1–3)

**Files:**
- Create: `src/main/java/phase04/d01_schema/Ex01_RelationalSchema.java`
- Create: `src/test/java/phase04/d01_schema/Ex01_RelationalSchemaTest.java`
- Create: `src/main/java/phase04/d02_constraints/Ex01_DataConstraints.java`
- Create: `src/test/java/phase04/d02_constraints/Ex01_DataConstraintsTest.java`
- Create: `src/main/java/phase04/d03_sql_joins/Ex01_OrderHistoryQuery.java`
- Create: `src/test/java/phase04/d03_sql_joins/Ex01_OrderHistoryQueryTest.java`

**Interfaces:**
- Consumes: foundation `PostgresFixture`; spec domain schema, IDs, price policy and exact original Q1–Q5 wording for topics 01–03.
- Produces: `Ex01_RelationalSchema.migrations(): List<String>`; `Ex01_DataConstraints.priceFor(long,DataSource): BigDecimal`; `Ex01_OrderHistoryQuery.forCustomer(DataSource,long): List<OrderHistory>`; `allCustomers(DataSource): List<CustomerOrderSummary>`; records exactly as spec.

- [ ] **Step 1: Add failing tests and skeletons**

Each main class has five original Q labels and declares its B label; copy exact wording from `04-database-persistence.md` on main and do not rewrite it. Each test has `q01_...` through `q05_...` coverage using prediction constants or explicit written answers, plus `b01_...`/`b02_...`/`b03_...` DB test. Put target learner DDL and queries only in marked Java methods/string suppliers. For non-DDL topics, use independent fixed fixture schema/seed SQL. B1 applies learner DDL supplied by `migrations()`.

Runnable example for B1:

```java
@Test void b01_schemaEnforcesForeignKey() throws SQLException {
    fixture.reset(String.join(";", Ex01_RelationalSchema.migrations()),
            "INSERT INTO customers(name) VALUES ('Mai');");
    assertThrows(SQLException.class, () -> insertOrderWithCustomer(fixture.dataSource(), 999L));
}
```

Expected red: learner migration TODO or missing FK causes assertion failure after fixture starts. Green: same test passes on PostgreSQL 18.

- [ ] **Step 2: Run intended red tests**

Run: `./mvnw.cmd -f phase-04-database-persistence/pom.xml -Dtest=Ex01_RelationalSchemaTest,Ex01_DataConstraintsTest,Ex01_OrderHistoryQueryTest test`
Expected: FAIL at unresolved TODO/assertions after PostgreSQL fixture starts; FAIL before assertion means diagnose fixture/build, not accepted red.

- [ ] **Step 3: Implement minimum solved behavior**

Implement migrations with four tables, FK, unique, `NOT NULL`, checks, stock and unit-price snapshot. Implement direct invalid-write evidence including explicit NULL/default behavior and SQLSTATE. Implement bound history query with `LEFT JOIN`, grouped totals/counts, no duplicate multiplication, customer-with-no-orders result. Each SQL answer stays between stripper markers.

- [ ] **Step 4: Run topic tests and stripping checks**

Run: `./mvnw.cmd -f phase-04-database-persistence/pom.xml -Dtest=Ex01_RelationalSchemaTest,Ex01_DataConstraintsTest,Ex01_OrderHistoryQueryTest test`
Expected: PASS on PostgreSQL. Run skeleton verifier for packages `d01_schema`, `d02_constraints`, `d03_sql_joins`; expected stripped Java compiles and learner-only assertions fail for intended unanswered code.

### Task 2: Index, EXPLAIN, and Pagination (Topics 4–6)

**Files:**
- Create: `src/main/java/phase04/d04_indexes/Ex01_HistoryIndex.java`
- Create: `src/test/java/phase04/d04_indexes/Ex01_HistoryIndexTest.java`
- Create: `src/main/java/phase04/d05_explain/Ex01_QueryPlan.java`
- Create: `src/test/java/phase04/d05_explain/Ex01_QueryPlanTest.java`
- Create: `src/main/java/phase04/d06_pagination/Ex01_OrderPage.java`
- Create: `src/test/java/phase04/d06_pagination/Ex01_OrderPageTest.java`

**Interfaces:**
- Consumes: Task 1 schema/read query and fixture; spec signatures and stable `(created_at,id)` contract.
- Produces: `Ex01_QueryPlan.explain(DataSource,long): String`; `Ex01_OrderPage.offsetPage(...)`, `after(...)`, and `OrderRow` as frozen.

- [ ] **Step 1: Write runnable JUnit tests**

Add `@Test void b06_usesStableZeroBasedPages()` with two rows sharing timestamp and distinct IDs; assert `offsetPage(ds,1,0,1)` returns lower ID, `offsetPage(ds,1,1,1)` returns next row, `after(ds,1,createdAt,id,1)` agrees, and page `-1`/size `101` throw `IllegalArgumentException`. B4 test applies learner index DDL then checks `pg_indexes`, records insert-batch row counts and elapsed observations on the same fixed dataset before/after index, without a threshold/golden. B5 test invokes explain and asserts output contains `Buffers` or `Buffers:` and nonempty rows, never a timing or chosen-node value. For B6, fixed seed has page size 2 and rows `(id,created_at)=(1,t10),(2,t20),(3,t30),(4,t40)`; first offset page is IDs 1,2 and cursor page is IDs 1,2. Insert `(5,t15)` between requests: next offset page repeats ID 2 due shifted offset; keyset after `(t20,2)` returns IDs 3,4. Assert both exactly; explain keyset avoids rows inserted before cursor, not a global snapshot.

- [ ] **Step 2: Run topic tests red**

Run: `./mvnw.cmd -f phase-04-database-persistence/pom.xml -Dtest=Ex01_HistoryIndexTest,Ex01_QueryPlanTest,Ex01_OrderPageTest test`
Expected: learner-method assertions fail; DB starts; no timeout-based green criterion.

- [ ] **Step 3: Implement index evidence, EXPLAIN and paging**

Keep learner index DDL and explain SQL in marked Java. Bind values; no bindable dynamic identifier. Order pages by `created_at,id`; enforce size 1–100 and zero-based page >=0. Record plan text, dataset and observations, not exact elapsed output. Representative offset clause:

```sql
SELECT id, created_at, status FROM orders
WHERE customer_id = ? ORDER BY created_at, id LIMIT ? OFFSET ?
```

Bind offset as `Math.multiplyExact(page,size)` after validating both inputs.

- [ ] **Step 4: Run topic and skeleton checks**

Run exact command from Step 2; expected PASS. Verify `d04_indexes`, `d05_explain`, `d06_pagination` stripped sources compile and tests fail only on intentionally absent learner solution.

### Task 3: ACID and JDBC Transaction/Batch (Topics 7, 9–10)

**Files:**
- Create: `src/main/java/phase04/d07_transactions/Ex01_OrderTransaction.java`
- Create: `src/test/java/phase04/d07_transactions/Ex01_OrderTransactionTest.java`
- Create: `src/main/java/phase04/d09_jdbc/Ex01_JdbcResources.java`
- Create: `src/test/java/phase04/d09_jdbc/Ex01_JdbcResourcesTest.java`
- Create: `src/main/java/phase04/d10_jdbc_transactions/Ex01_JdbcBatch.java`
- Create: `src/test/java/phase04/d10_jdbc_transactions/Ex01_JdbcBatchTest.java`

**Interfaces:**
- Consumes: Task 1 schema; `PostgresFixture`; spec record/method signatures for topics 07,09,10.
- Produces: `OrderTransaction.create(DataSource,long,long,List<Line>)`; `JdbcResources.findOrder/findProductByName/reduceStock`; `JdbcBatch.insertItems`; rollback and SQL exception behavior.

- [ ] **Step 1: Write failing JUnit tests**

Add `@Test void b07_rollsBackAllWrites()` which invokes `OrderTransaction.create(...)` with a valid first line and insufficient second line, then queries a fresh connection and asserts no order row and unchanged stock. Add `@Test void b09_bindsValuesAndReturnsPoolLease()` which supplies `name = "x' OR '1'='1"` via `findProductByName(DataSource,String)`, asserts exact literal-only result (order lookup separately uses numeric `findOrder(DataSource,long)`), acquires/closes/reacquires a Hikari connection under a 2-second timeout, and checks output contains no URL/password. Add `@Test void b10_batchFailureRollsBack()` with one valid and one invalid line, assert no items and inspect `SQLException.getSQLState()`.

Representative assertions:

```java
assertThrows(InsufficientStockException.class,
        () -> Ex01_OrderTransaction.create(fixture.dataSource(), 901L, 1L,
                List.of(new Ex01_OrderTransaction.Line(1L, 1),
                        new Ex01_OrderTransaction.Line(2L, 1))));
assertEquals(0, countOrders(fixture.dataSource(), 901L));
assertEquals(2, stockOf(fixture.dataSource(), 1L));
```

Use one fixed schema/seed fixture for these non-DDL topics; it must not contain their target answer SQL.

- [ ] **Step 2: Confirm meaningful red**

Run: `./mvnw.cmd -f phase-04-database-persistence/pom.xml -Dtest=Ex01_OrderTransactionTest,Ex01_JdbcResourcesTest,Ex01_JdbcBatchTest test`
Expected: assertion/TODO failures after PostgreSQL starts; no successful broad `assertThrows` masking TODO.

- [ ] **Step 3: Implement JDBC transaction and resource correctness**

Use try-with-resources for JDBC resources; parameterize values. Explicitly rollback on transaction failure, preserve primary exception and attach rollback exception if needed. Verify batch counts; no external I/O inside transaction. Representative atomic write shape:

```java
try (Connection c = ds.getConnection()) {
    c.setAutoCommit(false);
    try {
        // bind and insert order/items; conditional stock update must affect one row
        c.commit();
    } catch (SQLException | RuntimeException e) {
        try { c.rollback(); } catch (SQLException rollback) { e.addSuppressed(rollback); }
        throw e;
    }
}
```

Topic 09 B9 creates a local Hikari `HikariDataSource` using fixture `jdbcUrl()`, `username()`, `password()`; use try-with-resources and do not print credentials.

- [ ] **Step 4: Run DB tests and stripped learner tests**

Run command from Step 2; expected PASS. Verify all three topic packages stripped and correct intentional learner-red behavior.

### Task 4: JPA Lifecycle, Relationship, and Query Count (Topics 11–13)

**Files:**
- Create: `src/main/java/phase04/d11_entity_lifecycle/Ex01_EntityLifecycle.java`
- Create: `src/test/java/phase04/d11_entity_lifecycle/Ex01_EntityLifecycleTest.java`
- Create: `src/main/java/phase04/d12_relationships/Ex01_OrderMapping.java`
- Create: `src/test/java/phase04/d12_relationships/Ex01_OrderMappingTest.java`
- Create: `src/main/java/phase04/d13_fetching/Ex01_OrderHistoryProjection.java`
- Create: `src/test/java/phase04/d13_fetching/Ex01_OrderHistoryProjectionTest.java`

**Interfaces:**
- Consumes: Task 1 DB schema; pinned JPA/Hibernate and fixture; frozen `LifecycleObservation`, owning-side order mapping helpers, projection record/method.
- Produces: test-local `EntityManagerFactory` config with schema validation and disposable DB; actual entity lifecycle/mapping/query-count examples.

- [ ] **Step 1: Add JPA observations and runnable tests**

Add tests asserting lifecycle state transitions and owning FK using an independent PostgreSQL connection. For N+1, seed 3 and 12 orders, reset query counter before each call, then assert `queries(12) <= queries(3) + 1` and same page result contract; never compare timing. Example lifecycle assertion:

```java
var seen = Ex01_EntityLifecycle.observe(emf, fixtureProductId);
assertTrue(seen.managedBeforeFlush());
assertTrue(seen.mergeReturnedManaged());
```

Check separate connection cannot see uncommitted row before commit, then can after commit.

- [ ] **Step 2: Run topic tests red**

Run: `./mvnw.cmd -f phase-04-database-persistence/pom.xml -Dtest=Ex01_EntityLifecycleTest,Ex01_OrderMappingTest,Ex01_OrderHistoryProjectionTest test`
Expected: failed learner observation/SQL-count assertions, not Hibernate bootstrap config error.

- [ ] **Step 3: Implement focused mappings and projections**

Use field access, explicit `EntityManager`/transaction lifetime, minimum associations, child owns FK. Keep entities scoped to exercise; do not add global entity scanning. Use projection/read query for history; do not fix N+1 by blanket EAGER.

- [ ] **Step 4: Run real PostgreSQL JPA tests**

Run command from Step 2; expected PASS. Inspect generated SQL/query counter confirms no one-query-per-row growth. Run stripping verifier for all three packages.

### Task 5: Isolation, Locking, and Deadlock/Retry (Topics 8, 14–15)

**Files:**
- Create: `src/main/java/phase04/d08_isolation/Ex01_IsolationAnomaly.java`
- Create: `src/test/java/phase04/d08_isolation/Ex01_IsolationAnomalyTest.java`
- Create: `src/main/java/phase04/d14_locking/Ex01_StockLocking.java`
- Create: `src/test/java/phase04/d14_locking/Ex01_StockLockingTest.java`
- Create: `src/main/java/phase04/d15_deadlocks/Ex01_DeadlockRetry.java`
- Create: `src/test/java/phase04/d15_deadlocks/Ex01_DeadlockRetryTest.java`

**Interfaces:**
- Consumes: Tasks 1 and 3; `PostgresFixture`; exact `Observation`, `reserve(Connection,long,int)`, `SqlWork<T>`, and retry contract in spec.
- Produces: bounded actual PostgreSQL isolation, stock safety, deadlock/serialization evidence using independent workers.

- [ ] **Step 1: Write gated concurrency tests**

Use two real connections, fixed thread pool, `CountDownLatch`/`CyclicBarrier` gates with timeout before contested operations, test future timeout and `Statement.setQueryTimeout`. Start stock at known small value; assert successes' total quantity <= initial stock and final stock >=0. Deadlock workers each update a distinct first product row, then rendezvous at a timeout-bounded barrier after both first updates have acquired row locks and before either attempts its second row. This guarantees crossed locks; neither worker needs the other's first lock to reach the barrier. Then each updates the other's row. Assert one PostgreSQL SQLSTATE `40P01` within statement/future timeout; exercise `40001` classification and bounded whole-transaction retries. Other concurrency gates remain before contested operations where no lock is already held. Inspect state from a third connection.

- [ ] **Step 2: Run isolated concurrency red tests**

Run: `./mvnw.cmd -f phase-04-database-persistence/pom.xml -Dtest=Ex01_IsolationAnomalyTest,Ex01_StockLockingTest,Ex01_DeadlockRetryTest test`
Expected: TODO/invariant failure within bounded timeout; not deadlock hang or random sleep-dependent pass.

- [ ] **Step 3: Implement DB-level safety**

Use conditional `UPDATE products SET stock=stock-? WHERE id=? AND stock>=?`, classify affected row count; keep comparison paths for optimistic/pessimistic behavior. Retry complete transaction only for SQLSTATE `40001` / `40P01`, max attempts fixed by caller; no external side effects. Representative reservation code:

```java
try (PreparedStatement ps = c.prepareStatement(
        "UPDATE products SET stock=stock-? WHERE id=? AND stock>=?")) {
    ps.setInt(1, quantity);
    ps.setLong(2, productId);
    ps.setInt(3, quantity);
    return ps.executeUpdate() == 1;
}
```

Tests must prove two connections contend for actual rows and assert total accepted stock never exceeds starting stock.

- [ ] **Step 4: Run repeatable concurrency checks**

Run command from Step 2 multiple times; expected PASS each run within documented timeout. Verify no `Thread.sleep`, no shared Connection, no indefinite gate, and no retry for unrelated SQLSTATE.

### Task 6: Migration Compatibility and Order Persistence Capstone (Topics 16–17)

**Files:**
- Create: `src/main/java/phase04/d16_migrations/Ex01_MigrationCompatibility.java`
- Create: `src/test/java/phase04/d16_migrations/Ex01_MigrationCompatibilityTest.java`
- Create: `src/main/java/phase04/d17_capstone/Ex01_OrderPersistence.java`
- Create: `src/test/java/phase04/d17_capstone/Ex01_OrderPersistenceTest.java`

**Interfaces:**
- Consumes: Tasks 1–6; foundation fixture; topic 16 sole Flyway lifecycle; capstone public API `createOrder(DataSource,long,List<Line>)`, `ordersForCustomer(DataSource,long,int,int)`, records and constraints in spec.
- Produces: migration compatibility evidence and fully transactional persistence capstone with server price, owner scope, stable page and stock conservation.

- [ ] **Step 1: Write runnable migration and capstone tests**

Migration tests start empty, apply versioned migration, preserve pre-existing valid row when adding a constraint, and assert second migrate call creates no new history entry. Capstone `@Test void b17_rejectsDuplicateAndRollsBackInsufficientStock()` asserts duplicate input throws before writes, then a valid-first/insufficient-second order throws and fresh connection shows zero order/items plus unchanged first product stock. Additional tests prove server price snapshot, foreign customer empty result, same-timestamp stable paging, size bounds and two-connection stock conservation.

```java
assertThrows(InsufficientStockException.class,
        () -> Ex01_OrderPersistence.createOrder(ds, 1L,
                List.of(new Line(productA, 1), new Line(productB, 2))));
assertEquals(initialStockA, stockOf(ds, productA));
assertEquals(0, countOrdersForCustomer(ds, 1L));
```

Run scenario under a class-local disposable schema; do not assume test order.

- [ ] **Step 2: Confirm failing behavior**

Run: `./mvnw.cmd -f phase-04-database-persistence/pom.xml -Dtest=Ex01_MigrationCompatibilityTest,Ex01_OrderPersistenceTest test`
Expected: behavioral TODO failures on actual PG; Testcontainers/Docker unavailable is an infrastructure blocker, not expected red.

- [ ] **Step 3: Implement migration and capstone transaction**

Put learner DDL/migration answers in marked Java source. Flyway migration creation/application remains only topic 16. Capstone validates all inputs before writes; fetches product price from DB; starts one transaction; conditionally decrements each stock; checks affected rows; inserts order and snapshots; commits once. Any exception rolls back all. Scope order query with customer ID and stable order tuple.

- [ ] **Step 4: Run both topic suites and skeleton checks**

Run command from Step 2; expected PASS. Verify stripped capstone remains compilable and learner tests exercise intended omissions. No HTTP/auth layer.

### Task 7: Documentation, Full Verification, and Coverage Audit

**Files:**
- Create: `phase-04-database-persistence/README.md`
- Modify: only if needed, the 17 exercise/test files from Tasks 1–7; do not edit foundation-owned POM/tools.

**Interfaces:**
- Consumes: all module tasks, exact spec matrix, foundation module command and dependency pins.
- Produces: runnable learner setup, 85/17 audit, complete full test and stripped-skeleton verification evidence.

- [ ] **Step 1: Write module README**

Document JDK 21, actual pinned PostgreSQL/Testcontainers version, Docker requirement/readiness command, module test command, fixture limitations, class-local reset policy, topic list, SQL evidence location/output, and no-H2/no-skip requirement. State local test data only; no secrets.

- [ ] **Step 2: Count question/lab source coverage**

Check each package has exactly five `Q1`–`Q5` labels preserving exact source wording and one B label matching corresponding original lab. Check category labels, `ANSWER`/`OBSERVATION`, test names, `@DisplayName`s, and no duplicate/missing Q/B IDs. Compare against spec's exact question and lab sections.

- [ ] **Step 3: Run complete module tests and skeleton verifier**

Run: `./mvnw.cmd -f phase-04-database-persistence/pom.xml test`
Expected: all solved tests pass on real PostgreSQL 18. Then run verifier for all 17 packages with module option per foundation contract; expected all stripped source compiles and unanswered learner tests fail only for intended behavior. Any Docker/test skips are failures requiring diagnosis.

- [ ] **Step 4: Audit leaks, evidence, and diff**

Inspect learner skeleton/test/resource output for solution leaks. Confirm plan/EXPLAIN output has no golden timings, all concurrency waits bounded, actual SQLException handling, customer-scope tests, no secret output. Run `git diff --check`; expected clean whitespace and only assigned Phase04 files.

## Review Findings Resolved

- Topic 09 separates numeric `findOrder(DataSource,long)` from injection-tested `findProductByName(DataSource,String)`; result record fields frozen in spec.
- Deadlock barrier occurs after distinct first-row locks and before second-row attempts; PostgreSQL `40P01` assertion remains bounded.
- B4 records fixed-dataset insert observations before/after index without timing threshold.
- B6 covers empty pages and interleaved-insert observation for offset versus keyset.
