package phase05.d12_aop;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.springframework.aop.support.AopUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import phase05.support.PostgresFixture;

class Ex01_ProxyBoundaryTest {
    static PostgresFixture db;
    JdbcTemplate jdbc;
    @BeforeAll static void start() { db = PostgresFixture.start(); }
    @AfterAll static void stop() { if (db != null) db.close(); }
    @BeforeEach void reset() throws Exception {
        db.reset("create table audit_events(label varchar(100) not null)", "");
        jdbc = new JdbcTemplate(db.dataSource());
    }

    @Test void q01_inspectsRealProxyAndSelfInvocation() {
        assertNotNull(Ex01_ProxyBoundary.SELF_HAS_TRANSACTION, "thay null: dự đoán Q1");
        try (var context = Ex01_ProxyBoundary.context(db.dataSource())) {
            var worker = context.getBean(Ex01_ProxyBoundary.Worker.class);
            var caller = context.getBean(Ex01_ProxyBoundary.Caller.class);
            assertTrue(AopUtils.isAopProxy(worker));
            assertTrue(caller.externalActive());
            assertEquals(Ex01_ProxyBoundary.SELF_HAS_TRANSACTION, worker.selfActive());
            assertFalse(worker.selfActive());
        }
    }

    @Test void q02_existingTransactionAdviceIsCrossCuttingBoundary() {
        try (var context = Ex01_ProxyBoundary.context(db.dataSource())) {
            var caller = context.getBean(Ex01_ProxyBoundary.Caller.class);
            caller.externalSuccess();
            assertEquals(1, rows());
            assertThrows(IllegalStateException.class, caller::externalFailure);
            assertEquals(1, rows());
        }
    }

    @Test void b12_selfInvocationLeavesAutocommitWriteUnlikeExternalCall() {
        try (var context = Ex01_ProxyBoundary.context(db.dataSource())) {
            var worker = context.getBean(Ex01_ProxyBoundary.Worker.class);
            assertTrue(worker.transactionActive());
            worker.success();
            assertEquals(1, rows());
            assertThrows(IllegalStateException.class, worker::selfFailure);
            assertEquals(2, rows());
        }
    }

    @Test void q05_testActualAdviceAndSafeLogOutput() {
        try (var context = Ex01_ProxyBoundary.context(db.dataSource())) {
            assertTrue(context.getBean(Ex01_ProxyBoundary.Caller.class).externalActive());
        }
        String secret = "SENTINEL_PASSWORD_42";
        String token = "SENTINEL_TOKEN_99";
        String email = "private@example.test";
        String log = Ex01_ProxyBoundary.safeLog(new Ex01_ProxyBoundary.RequestBody("login", secret, token, email));
        assertEquals("operation=login", log);
        assertFalse(log.contains(secret)); assertFalse(log.contains(token)); assertFalse(log.contains(email));
        assertEquals("operation=unknown", Ex01_ProxyBoundary.safeLog(
                new Ex01_ProxyBoundary.RequestBody(secret + "\n" + token, secret, token, email)));
    }

    @Test void q04_experimentRuns() {
        System.out.println(Ex01_ProxyBoundary.safeLog(new Ex01_ProxyBoundary.RequestBody("login", "not-logged", "not-logged", "not-logged")));
    }
    int rows() { return jdbc.queryForObject("select count(*) from audit_events", Integer.class); }
}
