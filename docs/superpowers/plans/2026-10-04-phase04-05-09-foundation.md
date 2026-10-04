# Phases 04, 05, 09 Foundation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:subagent-driven-development` for independent tasks. Steps use checkbox (`- [ ]`) syntax.

**Goal:** Add isolated build/test foundations for phase04 persistence, phase05 Spring Boot, and phase09 algorithms without authoring domain exercises.

**Architecture:** Root reactor aggregates phase04/05/09. Phase04 and phase09 inherit root Java21/JUnit5.11.4; phase05 declares standalone Spring Boot parent and coordinates. Phase04/05 each own a manual Testcontainers PostgreSQL fixture with same API, no shared module.

**Tech Stack:** Java 21, Maven Wrapper 3.9.9, JUnit5.11.4 (existing modules), Spring Boot4.1.1 (phase05), Testcontainers2.0.3, PostgreSQL `postgres:18.0`, JDBC/pgJDBC, Hibernate/Jakarta Persistence, Flyway.

**Spec:** `docs/superpowers/specs/2026-10-04-phase04-05-09-foundation-design.md`

## Global Constraints

- Preserve root Java21/JUnit5.11.4 and existing module behavior.
- Phase05 parent: `org.springframework.boot:spring-boot-starter-parent:4.1.1`; explicit coordinates and `<relativePath/>`.
- Testcontainers `2.0.3`; PostgreSQL image `postgres:18.0`; no JUnit extension, H2 fallback, or fixed host port.
- Manual fixture API identical in each module: `static start()`, `DataSource dataSource()`, `void reset(String schemaSql, String seedSql) throws SQLException`, `String jdbcUrl()/username()/password()`, `close()`; class `final implements AutoCloseable`.
- No cross-module fixture dependency. Phase05 JWT/seed helper belongs to security/capstone owner.
- Coordinator readiness evidence: Docker Desktop4.74.0 / Engine29.4.3; `postgres:18.0` digest `sha256:41fc5342eefba6cc2ccda736aaf034bbbb7c3df0fdb81516eba1ba33f360162c`; actual server `18.0 Debian 18.0-1.pgdg13+3`, `SELECT 1` passed; disposable container removed. Do not repeat startup/pull. DB tests can run once code exists.
- SQL answers stay in marked Java; no resource parser or generic exercise framework.
- No full-pass claim when DB/JWT required tests did not run; no silent test skips.

## Review Focus

- Standalone phase05 must not inherit root JUnit/plugin overrides; assert effective POM and `-f` invocation.
- SQL reset must execute explicit schema then seed SQL, propagate failure, and never silently run Flyway; test both success and failure.
- Parallel fixtures must not collide; test isolated containers/schema and ensure lifecycle closes resources.
- Verifier must distinguish expected TODO red from wrong failures, missing/skipped tests and unauthorized green tests; preserve phase03 allowlist.
- Temporary skeleton copy must exclude nested `.claude` worktree content; assert it is absent.

---

### Task 1: Reactor modules and dependency isolation

**Files:** Modify `pom.xml`; create `phase-04-database-persistence/pom.xml`, `phase-05-spring-boot/pom.xml`, `phase-09-algorithms-system-design/pom.xml`.

**Interfaces:** Root aggregates three modules. 04/09 inherit root. 05 parent is Boot4.1.1, has explicit group/artifact/version and `<relativePath/>`.

- [ ] Add reactor entries and module POMs. Keep 04/09 on JUnit5.11.4; phase04 declares pinned PostgreSQL driver, Hibernate/Jakarta Persistence, Flyway core/PostgreSQL extension, HikariCP5.1.0 production scope for its pool lab, and Testcontainers2.0.3 PostgreSQL test module. Hibernate6.6/JPA3.1 supports the full curriculum target; JPA3.2 is reference context only. Phase05 parent owns versions; its dependency set is web MVC, Data JPA, validation, OAuth2 resource server, Flyway, actuator, and Testcontainers PostgreSQL/JUnit test dependencies. Phase09 has no framework dependencies. Example POM dependencies:

```xml
<!-- Phase05: versions managed by Spring Boot4.1.1 parent -->
<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-webmvc</artifactId></dependency>
<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-data-jpa</artifactId></dependency>
<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-validation</artifactId></dependency>
<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-security-oauth2-resource-server</artifactId></dependency>
<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-flyway</artifactId></dependency>
<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-actuator</artifactId></dependency>
<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-test</artifactId><scope>test</scope></dependency>
<dependency><groupId>org.testcontainers</groupId><artifactId>testcontainers-postgresql</artifactId><version>2.0.3</version><scope>test</scope></dependency>
```

Verify every concrete starter artifact against Boot4.1.1 effective dependency resolution during this task; where Boot supplies fewer/more modular artifacts, include the direct managed modules required for MVC/JPA/security and document the exact resolved set, never add arbitrary versions.

Example Testcontainers POM dependency:

```xml
<dependency>
  <groupId>org.testcontainers</groupId>
  <artifactId>testcontainers-postgresql</artifactId>
  <version>2.0.3</version>
  <scope>test</scope>
</dependency>
```

The phase04 pgJDBC dependency is also test scope for fixture support if main JDBC modules declare it explicitly; production application JDBC access requires compile scope. Fixture helper APIs remain test-only.
- [ ] Pin `org.postgresql:postgresql:42.7.13`, `org.hibernate.orm:hibernate-core:6.6.58.Final`, `jakarta.persistence:jakarta.persistence-api:3.1.0`, `com.zaxxer:HikariCP:5.1.0` (production scope; phase04 pooling lab), `org.flywaydb:flyway-core:11.20.3`, and `org.flywaydb:flyway-database-postgresql:11.20.3`. Maven Central metadata confirms release availability; Hibernate6.6 uses JPA3.1 and Hikari5.1.0 supports Java11+. Confirm effective resolved dependency tree on Java21 before commit. This contract targets JPA3.1; do not claim JPA3.2 API coverage.
- [ ] Run `./mvnw.cmd -pl tools,phase-00-java-basics,phase-01-core-advanced,phase-02-jvm-concurrency,phase-03-clean-code-design-patterns test` with process-local Java21 environment; expect existing modules pass unchanged.
- [ ] Add a minimal build-ownership JUnit/XML self-check to existing foundation test conventions. Check phase05's effective POM contains resolved web MVC, Data JPA, validation, OAuth2 resource server, Flyway, actuator and test modules, and phase04 remains JUnit5.11.4; fail on unresolved dependency or root override. Minimal report fixture:

```xml
<testsuite tests="1" failures="0" errors="0" skipped="0">
  <testcase classname="foundation.BuildIsolationTest" name="phase05UsesBootManagedDependencies"/>
</testsuite>
```

The test must actually execute `help:effective-pom` or inspect Maven's resolved model; do not hardcode this XML as passing evidence.
- [ ] Run `./mvnw.cmd -f phase-05-spring-boot/pom.xml help:effective-pom`; inspect effective dependency/plugin versions for Boot ownership, no root JUnit5 override. Run each new module's test via root reactor and phase05 standalone `-f` invocation. Expect all POMs resolve without unexpected inherited dependencies.

### Task 2: Independent PostgreSQL fixture in each DB module

**Files:** Create `phase-04-database-persistence/src/test/java/phase04/support/PostgresFixture.java`, matching test; create `phase-05-spring-boot/src/test/java/phase05/support/PostgresFixture.java`, matching test.

**Interfaces:** In both modules provide identical `final class PostgresFixture implements AutoCloseable`; `static PostgresFixture start()`; `DataSource dataSource()`; `void reset(String schemaSql, String seedSql) throws SQLException`; `String jdbcUrl()`, `username()`, `password()`; `close()`.

- [ ] Write `PostgresFixtureTest` with JUnit checks `assertEquals(1, query("SELECT 1"))`, `fixture.reset("CREATE TABLE fixture_probe(id int)", "INSERT INTO fixture_probe VALUES (1)")`, then `assertEquals(1, query("SELECT count(*) FROM fixture_probe"))`; verify `jdbcUrl()/username()/password()` are nonblank and invalid schema SQL propagates `SQLException`. Run two independently started fixtures; write unique marker in first and assert second sees no table. Use `try (var fixture = PostgresFixture.start())` in each test so close runs on assertion failure. Class stays exact `phase04.support.PostgresFixtureTest` / `phase05.support.PostgresFixtureTest`, the only support-green skeleton allowlist.
- [ ] Run each fixture test against assigned Docker readiness and `postgres:18.0`; test must fail on unavailable Docker rather than skip. Record actual PostgreSQL version from query as evidence.
- [ ] Implement each local fixture independently with Testcontainers 2 `org.testcontainers.postgresql.PostgreSQLContainer`, fixed image tag `postgres:18.0`, dynamic mapped port and `PGSimpleDataSource`. Constructor ownership shape:

```java
final class PostgresFixture implements AutoCloseable {
    private final PostgreSQLContainer<?> pg;
    private final PGSimpleDataSource ds;
    static PostgresFixture start() { /* create, start, configure, return */ }
    public DataSource dataSource() { return ds; }
    public String jdbcUrl() { return pg.getJdbcUrl(); }
    public String username() { return pg.getUsername(); }
    public String password() { return pg.getPassword(); }
    public void reset(String schemaSql, String seedSql) throws SQLException { /* excerpt below */ }
    @Override public void close() { pg.stop(); }
}
```


```java
PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:18.0");
pg.start();
PGSimpleDataSource ds = new PGSimpleDataSource();
ds.setURL(pg.getJdbcUrl());
ds.setUser(pg.getUsername());
ds.setPassword(pg.getPassword());
try (var c = ds.getConnection(); var s = c.createStatement()) {
    s.execute(schemaSql);
    s.execute(seedSql);
}
```

`reset` passes each SQL string whole to `Statement.execute`, schema first then seed; no semicolon splitting (strings/functions can contain semicolons), no auto-migration/hidden Flyway. `close` stops container; `PGSimpleDataSource` has no pool to close.
- [ ] If using one container per fixture/class, prove independent fixtures do not share database state. If choosing shared process container, use unique schema per fixture and ensure reset qualifies/isolate all names; prefer simpler per-class container.
- [ ] Run both fixture tests and module compile; report container/JDBC evidence with PostgreSQL `server_version`.

### Task 3: Skeleton verifier compatibility and regressions

**Files:** Modify `tools/verify-skeleton.ps1`; modify `tools/src/test/java/javaroadmap/tools/StripSolutionsTest.java` only if necessary; add smallest existing-convention verifier checks.

**Interfaces:** Keep current arguments/backward behavior; add optional tag exclusion for partial run if supported by JUnit tag filters. Standalone module must invoke `mvnw -f <module>/pom.xml ...` when it cannot resolve from reactor.

- [ ] Add focused checks proving current Java marked-answer stripping handles SQL text blocks and leaves no marked solution content. Run existing stripper tests first to identify real gap; do not change parser absent a failing test.
- [ ] Update temporary copy exclusion to include `.claude`; verify copied tree excludes it.
- [ ] Update verifier module build selection to support standalone Boot parent. Add optional explicit tag exclusion only for interim checks; full checks remain default.
- [ ] Add regression coverage for incomplete report inventory, skipped mandatory tests, wrong-red failure, unexpected-green test and existing explicit phase03 allowlist. Retain experiment exception. Expected red must include `TODO Qn`, `TODO Bn` or existing unfinished prediction marker; broad exception catch must not count. Permit only exact `phase04.support.PostgresFixtureTest` and `phase05.support.PostgresFixtureTest` green on skeleton; never blanket `support` packages. Fixture tests are separately mandatory and must be green with zero skipped.
- [ ] Define report inventory without a JUnit discovery framework: enumerate selected `src/test/java/**/*.java` ending `Test.java`; select class files containing `@Test`; derive FQCN from `package` declaration plus filename; apply same requested package/class selection as Maven. Compare this set with Surefire XML suite names or testcase `classname`s. Fail for any expected class without report/testcase, any unexpected class, or skipped expected case. Phase04/05 foundation fixture inventory is the exact one-class set per module above. Phase09 empty foundation has no test run; use `test-compile`, not an empty inventory/report gate.
- [ ] Add runnable minimal verifier XML self-check cases. Green fixture `TEST-phase04.support.PostgresFixtureTest.xml`:

```xml
<testsuite tests="1" failures="0" errors="0" skipped="0">
  <testcase classname="phase04.support.PostgresFixtureTest" name="fixtureSupportMayPassOnSkeleton"/>
</testsuite>
```

These synthetic XML rows are classifier inputs only, not executable JUnit tests. A green `phase04.d01_schema.Ex01Test#q01` row must be rejected as unexpected green; a fixture `<testcase ...><skipped/></testcase>` row must be rejected by required fixture verification. Actual green exception evidence comes only from the DB fixture tests in Task2 (`SELECT 1`, reset, isolation); add no no-op JUnit test.

- [ ] Run verifier tests and old phase03 skeleton command. Run each new module selected check. Expected reports list all executed tests and zero skipped mandatory tests; any tag-filtered run labels output partial. Phase09 foundation currently has no exercise tests: run `test-compile` only, do not require Surefire XML until phase09 domain tasks add tests.

### Task 4: Module setup notes and final foundation gate

**Files:** Create initial module `README.md` files only for phases04/05/09; no curriculum edits. Foundation owns initial setup/build notes. Domain integration owner updates exercise mappings, coverage matrices, curriculum links and exercise README sections after filenames stabilize; avoid concurrent edits to root/curriculum files.

**Interfaces:** Each README gives module setup/build/test instructions. 04/05 state Docker-backed PostgreSQL requirement; 09 states no Spring runtime.

- [ ] Add concise Vietnamese setup instructions: Java21, module purpose, exact build/test command, full-vs-partial validation and Docker PostgreSQL requirement for 04/05. For phase05 state standalone Boot parent. For phase09 state JDK-only DSA, no Spring dependency.
- [ ] Run this README contract check after writing:

```powershell
$paths = @('phase-04-database-persistence/README.md','phase-05-spring-boot/README.md','phase-09-algorithms-system-design/README.md')
$paths | ForEach-Object { if (-not (Test-Path $_)) { throw "Missing $_" } }
if ((Get-Content $paths[2] -Raw) -match 'Docker|PostgreSQL') { throw 'Phase09 README must not claim DB runtime requirement.' }
if ((Get-Content $paths[1] -Raw) -notmatch 'Boot|Spring') { throw 'Phase05 README must identify Boot parent.' }
```

- [ ] Run `./mvnw.cmd -pl tools,phase-00-java-basics,phase-01-core-advanced,phase-02-jvm-concurrency,phase-03-clean-code-design-patterns test`.
- [ ] Run `./mvnw.cmd -pl phase-04-database-persistence,phase-05-spring-boot test`; run phase05 standalone effective POM/build; run skeleton verifier for implemented phase04/05 domain packages and fixture checks separately. Require expected-class inventory to match all XML reports, Docker-backed fixture tests green, zero skipped mandatory tests, and matching actual PG version before claiming complete. For empty phase09 foundation, run `./mvnw.cmd -pl phase-09-algorithms-system-design test-compile`; do not execute test/report gate or product-domain skeleton gate until phase09 exercises exist.
- [ ] Run `git diff --check`; inspect changed paths to ensure no exercise answers, JWT/seed helpers, curriculum edits, or unrelated learner work entered foundation scope.
