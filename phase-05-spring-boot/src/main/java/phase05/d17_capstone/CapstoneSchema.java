package phase05.d17_capstone;

import java.sql.SQLException;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.migration.Context;
import org.flywaydb.core.api.migration.JavaMigration;
import org.springframework.stereotype.Component;

/** B1: Java-owned Flyway migration. No hidden resource DDL. */
@Component
public final class CapstoneSchema implements JavaMigration {
    // Provided Flyway adapter; learner owns sql().
    @Override public MigrationVersion getVersion() { return MigrationVersion.fromVersion("1"); }
    @Override public String getDescription() { return "capstone schema"; }
    @Override public Integer getChecksum() { return sql().hashCode(); }
    @Override public boolean canExecuteInTransaction() { return true; }
    @Override public void migrate(Context context) throws SQLException {
        try (var statement = context.getConnection().createStatement()) {
            statement.execute(sql());
        }
    }

    public static String sql() {
        throw new UnsupportedOperationException("TODO B1");
    }
}
