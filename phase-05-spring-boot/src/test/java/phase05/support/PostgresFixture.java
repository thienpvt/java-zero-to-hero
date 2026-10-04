package phase05.support;

import java.sql.SQLException;
import java.time.Duration;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public final class PostgresFixture implements AutoCloseable {
    private final PostgreSQLContainer pg;
    private final PGSimpleDataSource ds;

    private PostgresFixture(PostgreSQLContainer pg, PGSimpleDataSource ds) {
        this.pg = pg;
        this.ds = ds;
    }

    public static PostgresFixture start() {
        PostgreSQLContainer pg = new PostgreSQLContainer("postgres:18.0")
                .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(120)));
        try {
            pg.start();
            PGSimpleDataSource ds = new PGSimpleDataSource();
            ds.setURL(pg.getJdbcUrl());
            ds.setUser(pg.getUsername());
            ds.setPassword(pg.getPassword());
            return new PostgresFixture(pg, ds);
        } catch (RuntimeException | Error failure) {
            try {
                pg.stop();
            } catch (RuntimeException | Error cleanupFailure) {
                failure.addSuppressed(cleanupFailure);
            }
            throw failure;
        }
    }

    public DataSource dataSource() { return ds; }
    public String jdbcUrl() { return pg.getJdbcUrl(); }
    public String username() { return pg.getUsername(); }
    public String password() { return pg.getPassword(); }

    public void reset(String schemaSql, String seedSql) throws SQLException {
        try (var connection = ds.getConnection(); var statement = connection.createStatement()) {
            statement.execute("DROP SCHEMA public CASCADE");
            statement.execute("CREATE SCHEMA public");
            statement.execute(schemaSql);
            if (!seedSql.isBlank()) {
                statement.execute(seedSql);
            }
        }
    }

    @Override
    public void close() {
        pg.stop();
    }
}
