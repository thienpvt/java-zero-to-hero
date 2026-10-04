package phase04.d16_migrations;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

/**
 * Migration PostgreSQL 18; USD cố định, không thêm cột currency.
 * <p>Nguồn: 04-database-persistence.md, mục 16. B16: tạo schema, nâng cấp dữ liệu cũ,
 * chạy lại history; không giả định rename/drop an toàn cho rolling deploy.
 * Q1 [TỰ TRẢ LỜI] Migration đem lại lợi ích nào so với sửa schema thủ công?
 * Cần trước: schema bốn bảng và transaction DDL. Bắt đầu: viết ANSWER Q1.
 * Tài liệu: https://documentation.red-gate.com/flyway/flyway-concepts/migrations
 * Debug: đối chiếu flyway_schema_history, không sửa migration đã áp dụng.
 * Hoàn thành: giải thích version, thứ tự, history và khả năng tái tạo DB.
 * Q2 [TỰ TRẢ LỜI] Vì sao migration rename/drop cột có thể phá deploy rolling?
 * Cần trước: app cũ/mới cùng tồn tại. Bắt đầu: viết trình tự expand/contract.
 * Tài liệu: https://www.postgresql.org/docs/18/sql-altertable.html
 * Debug: tìm SQL app cũ vẫn tham chiếu cột. Hoàn thành: nêu giới hạn rollout lab.
 * Q3 [TỰ TRẢ LỜI] H2 khác PostgreSQL ở những hành vi nào cần quan tâm?
 * Cần trước: SQLSTATE, MVCC, row lock. Bắt đầu: liệt kê khác biệt trong ANSWER Q3.
 * Tài liệu: https://www.postgresql.org/docs/18/transaction-iso.html
 * Debug: lưu server_version và plan thật. Hoàn thành: không dùng H2 thay test DB.
 * Q4 [CODE] Test concurrency cần chạy trên loại database nào để có giá trị?
 * Cần trước: connection riêng, conditional update. Bắt đầu: cài reserve, chạy q04.
 * Tài liệu: https://www.postgresql.org/docs/18/explicit-locking.html
 * Debug: gate trước lock, query/future có timeout. Hoàn thành: stock conservation PG18.
 * Q5 [CODE] Làm sao tránh test phụ thuộc dữ liệu từ lần chạy trước?
 * Cần trước: fixture disposable và unique name. Bắt đầu: cài insertCustomer, chạy q05.
 * Tài liệu: https://java.testcontainers.org/modules/databases/postgres/
 * Debug: reset schema riêng giữa test, không dùng DB dùng chung hay thứ tự test.
 * Hoàn thành: cùng seed unique chạy được sau hai lần reset; không che lỗi bằng ON CONFLICT.
 */
public class Ex01_MigrationCompatibility {
    /** Provided bootstrap: topic 16 alone registers and executes real Flyway migrations. */
    public static void migrate(DataSource ds) { migrate(ds, "2"); }

    public static void migrate(DataSource ds, String target) {
        if (ds == null || !("1".equals(target) || "2".equals(target)))
            throw new IllegalArgumentException("DataSource required; target 1 or 2");
        Flyway.configure().dataSource(ds).loggers("slf4j").locations(new String[0])
                .schemas("public").defaultSchema("public").cleanDisabled(true)
                .baselineOnMigrate(false)
                .javaMigrations(new V1__DomainSchema(), new V2__QuantityConstraint())
                .target(target).load().migrate();
    }

    public static class V1__DomainSchema extends BaseJavaMigration {
        @Override public void migrate(Context context) throws SQLException {
            try (var statement = context.getConnection().createStatement()) {
                statement.setQueryTimeout(10);
                for (String sql : schemaSql()) statement.execute(sql);
            }
        }
    }

    public static class V2__QuantityConstraint extends BaseJavaMigration {
        @Override public void migrate(Context context) throws SQLException {
            try (var statement = context.getConnection().createStatement()) {
                statement.setQueryTimeout(10);
                for (String sql : quantityConstraintSql()) statement.execute(sql);
            }
        }
    }

    static List<String> schemaSql() {
        throw new UnsupportedOperationException("TODO B16");
    }

    static List<String> quantityConstraintSql() {
        throw new UnsupportedOperationException("TODO B16");
    }

    static boolean reserve(Connection c, long productId, int quantity) throws SQLException {
        throw new UnsupportedOperationException("TODO Q4");
    }

    static long insertCustomer(DataSource ds, String name) throws SQLException {
        throw new UnsupportedOperationException("TODO Q5");
    }
}

/* ANSWER Q1:
 *
 */
/* ANSWER Q2:
 *
 */
/* ANSWER Q3:
 *
 */
/* OBSERVATION B16 / ANSWER Q4,Q5:
 *
 */
