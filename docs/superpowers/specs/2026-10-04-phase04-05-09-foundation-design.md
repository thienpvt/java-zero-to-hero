# Foundation design: phases 04, 05, 09

Date: 2026-10-04
Status: review

## Goal and constraints

Prepare root build, three phase modules, skeleton verification, and phase-local PostgreSQL test fixtures so separately owned exercise work can build and test without coupling phase domains. This foundation does not author product exercises, answers, JWT utilities, or shared domain code. Existing Java 21 / JUnit 5.11.4 modules remain unchanged in dependency behavior. `main` stays documentation-only; work targets the `solutions` implementation baseline and must not publish or merge branches.

Acceptance: reactor includes phases 04/05/09; phase05 can build independently with its own Spring Boot parent; phases 04 and 09 inherit root Java/JUnit pins; existing phases retain behavior; verifier handles a selected module and required full checks fail on skipped/missing reports or incorrect red/green results; each database module has its own manually-started, disposable-PostgreSQL fixture with the exact shared API below; all API and build checks work with JDK 21.

## Architecture

Root reactor adds `phase-04-database-persistence`, `phase-05-spring-boot`, and `phase-09-algorithms-system-design`. Phase04 is plain JDBC/Hibernate/Jakarta Persistence and Flyway; phase05 is independently parented by Spring Boot and must declare explicit group/artifact/version plus `<relativePath/>`; phase09 is Java 21/JUnit only. Phase05 cannot depend on phase04 at runtime. No shared domain/library module.

Foundation owns root POM, the three module POMs, the existing skeleton verifier/stripper only where required, local DB fixture implementations and narrow regression tests, and module setup READMEs. Exercise-domain source files, phase05 JWT/seed fixtures, coverage matrices, and curriculum edits belong to other assigned owners.

Each DB module owns its own fixture: `phase04.support.PostgresFixture` and `phase05.support.PostgresFixture`. Each is test-only, `final`, `AutoCloseable`; it offers `static start()`, `DataSource dataSource()`, `void reset(String schemaSql, String seedSql) throws SQLException`, `String jdbcUrl()`, `String username()`, `String password()`, and `close()`. Use manual Testcontainers lifecycle, one container per fixture/class or a demonstrably isolated random schema; do not rely on fixed host port 5432. `reset` executes supplied schema and seed SQL explicitly, never invokes Flyway implicitly. Pass each SQL string whole to JDBC `Statement.execute`, schema first then seed; never split on semicolons because PostgreSQL literals/functions may contain them. Fixture uses `PGSimpleDataSource`; HikariCP is a production-scope phase04 dependency solely for pooling lab, not fixture. Do not add Testcontainers JUnit extension. No cross-module fixture dependency. The phase05 security/capstone owner alone adds JWT and seed helpers.

## Version and compatibility pins

- Root remains Java 21 and JUnit Jupiter BOM `5.11.4`; do not upgrade existing modules.
- Phase05 uses parent `org.springframework.boot:spring-boot-starter-parent:4.1.1`, Java release 21, explicit project coordinates, and empty `<relativePath/>`. Spring Boot manages its Spring, Jackson, Hibernate, JUnit, and plugin versions; module must not inherit root dependency/plugin management accidentally. Confirm effective POM and independent `-f` build before merging.
- Testcontainers Java `2.0.3`, with separate `org.testcontainers:testcontainers-postgresql` test dependency and API package `org.testcontainers.postgresql`. Add Testcontainers core only if required by resolved transitive graph; no JUnit integration module. Verify exact API against resolved `testcontainers-postgresql:2.0.3` artifact before compiling helper.
- PostgreSQL image is exactly `postgres:18.0` (not `latest`); coordinator has assigned Docker readiness and disposable PostgreSQL verification elsewhere. This plan consumes its actual readiness/version evidence; do not start/pull Docker here.
- Phase04 exact pins: `org.postgresql:postgresql:42.7.13`, `org.hibernate.orm:hibernate-core:6.6.58.Final`, `jakarta.persistence:jakarta.persistence-api:3.1.0`, `com.zaxxer:HikariCP:5.1.0`, and `org.flywaydb:flyway-core:11.20.3` plus `org.flywaydb:flyway-database-postgresql:11.20.3`. Hikari is production-scope only because phase04 includes a connection-pooling lab; fixture itself remains `PGSimpleDataSource`. Hibernate6.6/JPA3.1 is deliberate and compatible with all phase04 curriculum features; curriculum's JPA3.2 mention is reference context, not a required target API. Do not claim JPA3.2 API coverage from these pins. Testcontainers `2.0.3` PostgreSQL module.

Documentation basis checked 2026-10-04: Spring Boot official docs describe parent Maven dependency/plugin management and Java17 minimum (21 supported); Context7 lists Boot4.1.0, not 4.1.1. Testcontainers docs show 2.x PostgreSQL module coordinate/API package and separate DB driver requirement; pgJDBC docs show Java8+ and `PGSimpleDataSource`; Flyway docs require separate PostgreSQL database-support module. Maven Central metadata confirms selected pgJDBC42.7.13, Hibernate6.6.58.Final, Jakarta Persistence3.1.0, Flyway11.20.3 and HikariCP5.1.0 are published releases. Hibernate6.6 is the JPA3.1 line; Hikari5.1.0 supports Java11+. Verify resolved phase04 dependency tree in Task1.

## Build and safety behavior

- Java exercise answers stay in Java source within existing `SOLUTION-BEGIN`/`SOLUTION-VALUE` markers so existing stripper remains Java-only. SQL answers live in marked Java methods/string suppliers; no generic resource parser and no student answers in resources.
- Extend `tools/verify-skeleton.ps1` backward-compatibly for standalone module POMs (`-f <module>/pom.xml`) and optional tag exclusions for partial interim checks. Default remains full suite. Exclude `.claude` from its temporary repository copy in addition to existing exclusions.
- Verification rejects no/missing Surefire reports, skipped mandatory tests, skeleton tests red for reasons other than TODO/unfinished prediction, unexpected green tests except existing explicit narrow allowlist, named experiments, and exact `phase04.support.PostgresFixtureTest` / `phase05.support.PostgresFixtureTest` classes. The DB fixture classes are intentionally green on skeleton and must be run separately as mandatory infrastructure tests; never allow all `support` tests green. The empty phase09 foundation is compile-only until exercise tests exist; it must not execute a phase09 test gate or fail for having no report. For each selected module/package, derive expected test classes from source files under `src/test/java` whose filename ends `Test.java` and whose class contains at least one `@Test`; derive FQCN from package declaration plus class filename, then apply the same selected package/class filter as Maven. Compare this expected set with Surefire XML `testsuite/@name` (or each `testcase/@classname`) before evaluating outcomes; fail if any expected class has no report/cases, any unexpected class appears, or any expected test is skipped. Foundation’s only expected DB fixture test classes are exactly `phase04.support.PostgresFixtureTest` and `phase05.support.PostgresFixtureTest`; run them separately. Tag-filtered/interim runs must derive inventory after the same filters and say partial; never silently skip DB tests or claim full pass without required PostgreSQL/JWT evidence.
- Keep tests deterministic. No timing threshold for experiments, no sleep-based race coordination, and no broad exception assertion that can pass before TODO code runs. Database correctness tests use real PostgreSQL, explicit transactions and independent reads.
- Docker readiness was verified by coordinator: Docker Desktop4.74.0 / Engine29.4.3; image `postgres:18.0` digest `sha256:41fc5342eefba6cc2ccda736aaf034bbbb7c3df0fdb81516eba1ba33f360162c`; disposable server returned `18.0 Debian 18.0-1.pgdg13+3` and `SELECT 1` passed. Container was removed. Reuse this evidence; do not repeat readiness startup. DB tests still must run once exercise code exists.

## Files and ownership

- Modify root `pom.xml`: reactor modules only; preserve existing Java/JUnit/plugin configuration.
- Create each module `pom.xml`; phase05 remains isolated from root management.
- Create each module-local `src/test/java/phase0N/support/PostgresFixture.java` with identical API and implementation-local dependencies; tests for start/connect/reset/close and isolation.
- Modify `tools/verify-skeleton.ps1`; extend `StripSolutionsTest.java` only for demonstrated Java text-block regression.
- Add focused verifier regression checks using existing project conventions (no framework/dependency); cover `.claude` exclusion, standalone POM invocation, selected-test filtering, wrong-red/missing/skipped result rejection, and preservation of phase03 allowlist behavior. Include a runnable minimal JUnit/XML case: `@Test void fixtureSupportMayPassOnSkeleton(){}` produces `<testcase classname="phase04.support.PostgresFixtureTest" name="fixtureSupportMayPassOnSkeleton"/>`; same green XML under `phase04.d01_schema.Ex01Test` must fail skeleton verifier, and `<skipped/>` must fail full fixture verification.
- Put a minimal actual POM example in the plan: `<dependency><groupId>org.testcontainers</groupId><artifactId>testcontainers-postgresql</artifactId><version>2.0.3</version><scope>test</scope></dependency>`; testImplementation also includes `org.postgresql:postgresql:42.7.13`; fixture contains `PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:18.0"); pg.start(); PGSimpleDataSource ds = new PGSimpleDataSource(); ds.setURL(pg.getJdbcUrl()); ds.setUser(pg.getUsername()); ds.setPassword(pg.getPassword()); try (var c = ds.getConnection(); var s = c.createStatement()) { s.execute(schemaSql); s.execute(seedSql); }`. This is contract excerpt, not full implementation.
- Add module READMEs with only setup/run instructions and accurate partial/full verification labels.

## Acceptance checks

1. Root existing module test command remains green without upgrading existing dependencies.
2. `help:effective-pom` for phase05 confirms Boot-managed versions and no inherited root JUnit5/plugin override; direct module Maven invocation works.
3. Both phase-local fixture implementations compile against Testcontainers 2 API; fixture tests prove lifecycle, explicit SQL reset, and database/schema isolation, without JUnit extension or fixed host-port assumption.
4. Test SQL reset with separate schema/seed statements, connection validity, and cleanup; failed SQL must propagate rather than be swallowed.
5. Stripper regression confirms Java text-block marked answer removal and leaves no answer marker; do not introduce resource stripping.
6. Verifier regression preserves old phase03 allowed-green cases; fails missing/skipped reports, unmarked green or wrong red; `.claude` does not enter copy; filtering passes correct standalone module build invocation.
7. Full commands only claimed after Docker proof and all required tests run; selected tag runs are partial.
