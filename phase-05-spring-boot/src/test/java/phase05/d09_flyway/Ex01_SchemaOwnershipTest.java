package phase05.d09_flyway;

import static org.junit.jupiter.api.Assertions.*;

import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import phase05.support.PostgresFixture;

class Ex01_SchemaOwnershipTest {
    static PostgresFixture db;
    @BeforeAll static void start() { db = PostgresFixture.start(); }
    @AfterAll static void stop() { if (db != null) db.close(); }
    @BeforeEach void empty() throws Exception { db.reset("select 1", ""); }

    @Test void q01_migrationIsAppliedOnceAndValidated() {
        assertNotNull(Ex01_SchemaOwnership.HISTORY_AFTER_RERUN, "thay null: dự đoán Q1");
        var flyway = Ex01_SchemaOwnership.flyway(db.dataSource());
        assertEquals(1, flyway.migrate().migrationsExecuted);
        assertEquals(0, flyway.migrate().migrationsExecuted);
        flyway.validate();
        var jdbc = new JdbcTemplate(db.dataSource());
        int history = jdbc.queryForObject("select count(*) from flyway_schema_history where success", Integer.class);
        assertEquals(Ex01_SchemaOwnership.HISTORY_AFTER_RERUN.intValue(), history);
        assertEquals(1, history);
        assertEquals("1", jdbc.queryForObject("select version from flyway_schema_history where success", String.class));
        jdbc.update("update flyway_schema_history set checksum=checksum+1 where version='1'");
        assertThrows(org.flywaydb.core.api.exception.FlywayValidateException.class, flyway::validate);
    }

    @Test void q02_validateChecksMappingButDoesNotCreateOrRepairSchema() {
        var flyway = Ex01_SchemaOwnership.flyway(db.dataSource());
        assertEquals(1, flyway.migrate().migrationsExecuted); // valid learner path before negative check
        try (var emf = mapping().buildSessionFactory()) { assertTrue(emf.isOpen()); }
        var jdbc = new JdbcTemplate(db.dataSource());
        jdbc.execute("alter table products drop column stock");
        assertThrows(org.hibernate.tool.schema.spi.SchemaManagementException.class,
                () -> mapping().buildSessionFactory());
        assertEquals(0, jdbc.queryForObject("select count(*) from information_schema.columns "
                + "where table_name='products' and column_name='stock'", Integer.class));
    }

    @Test void b09_freshDatabaseHasFourTablesConstraintsAndIndexes() {
        var flyway = Ex01_SchemaOwnership.flyway(db.dataSource());
        assertEquals(0, flyway.info().applied().length);
        flyway.migrate();
        var jdbc = new JdbcTemplate(db.dataSource());
        assertEquals(4, jdbc.queryForObject("select count(*) from information_schema.tables "
                + "where table_schema='public' and table_name in ('customers','products','orders','order_items')", Integer.class));
        assertEquals(0, jdbc.queryForObject("select count(*) from information_schema.tables "
                + "where table_name='inventory'", Integer.class));
        jdbc.update("insert into products(id,name,price,stock) values (1,'book',12.34,5)");
        try (var emf = mapping().buildSessionFactory()) {
            try (var session = emf.openSession()) {
                assertEquals(5, session.find(Ex01_SchemaOwnership.Product.class, 1L).stock);
            }
        }
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class,
                () -> jdbc.update("insert into products(id,name,price,stock) values (2,'bad',1,-1)"));
        assertTrue(jdbc.queryForObject("select count(*) from pg_indexes where indexname='orders_owner_page'", Integer.class) > 0);
    }

    @Test void q05_usesRealPostgresDatabaseModule() {
        var flyway = Ex01_SchemaOwnership.flyway(db.dataSource());
        flyway.migrate();
        assertEquals(1, flyway.info().applied().length);
        assertTrue(new JdbcTemplate(db.dataSource()).queryForObject("select version()", String.class).startsWith("PostgreSQL 18"));
    }

    @Test void q04_experimentRuns() {
        var flyway = Ex01_SchemaOwnership.flyway(db.dataSource());
        flyway.migrate();
        System.out.println("B09 migration history: " + flyway.info().applied().length);
    }

    Configuration mapping() {
        return new Configuration().addAnnotatedClass(Ex01_SchemaOwnership.Product.class)
                .addAnnotatedClass(Ex01_SchemaOwnership.Customer.class)
                .addAnnotatedClass(Ex01_SchemaOwnership.Order.class)
                .addAnnotatedClass(Ex01_SchemaOwnership.Item.class)
                .setProperty("hibernate.hbm2ddl.auto", "validate")
                .setProperty("hibernate.connection.url", db.jdbcUrl())
                .setProperty("hibernate.connection.username", db.username())
                .setProperty("hibernate.connection.password", db.password());
    }
}
