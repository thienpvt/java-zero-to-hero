package phase04.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.Test;

class PostgresFixtureTest {
    @Test
    void startsPostgres18AndExposesConnectionSettings() throws SQLException {
        try (var fixture = PostgresFixture.start(); var connection = fixture.dataSource().getConnection(); var statement = connection.createStatement()) {
            try (var result = statement.executeQuery("SELECT 1")) {
                result.next();
                assertEquals(1, result.getInt(1));
            }
            try (var result = statement.executeQuery("SHOW server_version")) {
                result.next();
                assertTrue(result.getString(1).startsWith("18.0 "));
            }
            assertFalse(fixture.jdbcUrl().isBlank());
            assertFalse(fixture.username().isBlank());
            assertFalse(fixture.password().isBlank());
        }
    }

    @Test
    void resetClearsPublicSchemaAndExecutesWholeSqlStrings() throws SQLException {
        try (var fixture = PostgresFixture.start(); var connection = fixture.dataSource().getConnection(); var statement = connection.createStatement()) {
            fixture.reset("CREATE TABLE fixture_probe(value text)", "INSERT INTO fixture_probe VALUES ('semi;colon')");
            assertEquals(1, query(statement, "SELECT count(*) FROM fixture_probe"));
            assertEquals("semi;colon", text(statement, "SELECT value FROM fixture_probe"));

            fixture.reset("CREATE TABLE replacement_probe(id int)", "INSERT INTO replacement_probe VALUES (1)");
            assertThrows(SQLException.class, () -> statement.executeQuery("SELECT * FROM fixture_probe"));
            assertEquals(1, query(statement, "SELECT count(*) FROM replacement_probe"));
        }
    }

    @Test
    void resetPropagatesInvalidSchemaSql() throws SQLException {
        try (var fixture = PostgresFixture.start()) {
            assertThrows(SQLException.class, () -> fixture.reset("CREATE TABL broken", ""));
        }
    }

    @Test
    void separateFixturesDoNotSharePublicSchema() throws SQLException {
        try (var first = PostgresFixture.start(); var second = PostgresFixture.start(); var firstConnection = first.dataSource().getConnection(); var secondConnection = second.dataSource().getConnection(); var firstStatement = firstConnection.createStatement(); var secondStatement = secondConnection.createStatement()) {
            first.reset("CREATE TABLE private_marker(id int)", "INSERT INTO private_marker VALUES (1)");
            assertThrows(SQLException.class, () -> secondStatement.executeQuery("SELECT * FROM private_marker"));
        }
    }

    private static int query(Statement statement, String sql) throws SQLException {
        try (var result = statement.executeQuery(sql)) {
            result.next();
            return result.getInt(1);
        }
    }

    private static String text(Statement statement, String sql) throws SQLException {
        try (var result = statement.executeQuery(sql)) {
            result.next();
            return result.getString(1);
        }
    }
}
