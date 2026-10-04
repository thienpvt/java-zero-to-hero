package phase05.d09_flyway;

import jakarta.persistence.*;
import java.math.BigDecimal;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.migration.JavaMigration;
import org.flywaydb.core.api.migration.Context;

/**
 * <h2>Chủ đề 9 — Flyway và quyền sở hữu schema</h2>
 * <p>Nguồn: {@code 05-spring-boot.md}, §9, Q1–Q5, B09. Tiên quyết: DDL, JPA, PostgreSQL.</p>
 * <p>B09: mở {@code migrationSql} trong IntelliJ; viết migration Java-owned, chạy
 * {@code Ex01_SchemaOwnershipTest} từ DB rỗng. Debugger xem Flyway info và Hibernate validate.
 * Không đặt SQL vào resources; configuration chỉ đăng ký migration của chủ đề này.</p>
 * Q1 [DỰ ĐOÁN] Vì sao migration đã chạy không nên sửa trực tiếp?
 * <p>Dự đoán history sau lần migrate thứ hai; đọc Flyway migration/checksum docs.
 * Hoàn thành: history vẫn một version; giải thích thêm version mới trong ANSWER Q1.</p>
 * Q2 [CODE] {@code ddl-auto=validate} kiểm tra gì và không làm gì?
 * <p>Viết DDL đúng mapping Product; đọc Hibernate schema validation. Breakpoint sau migrate.
 * Hoàn thành: mapping validate thành công; bỏ stock gây lỗi mà Hibernate không sửa schema.</p>
 * Q3 [TỰ TRẢ LỜI] Tại sao migration là owner của schema khi đã chọn Flyway?
 * <p>Mở ANSWER Q3; đối chiếu ddl-auto update và validate trong docs.
 * Hoàn thành: nêu một nguồn sự thật và thứ tự migrate trước validate.</p>
 * Q4 [THÍ NGHIỆM] Khi nào cần index/constraint trong migration thay vì chỉ kiểm tra ở Java?
 * <p>Chạy smoke Q4, xem pg_indexes/pg_constraint bằng debugger; đọc PostgreSQL constraints.
 * Hoàn thành: ghi bằng chứng stock constraint và owner paging index vào ANSWER Q4.</p>
 * Q5 [CODE] Flyway PostgreSQL có thể cần module hỗ trợ database riêng nào ở Boot 4?
 * <p>Mở {@code flyway}, đối chiếu POM có sẵn và Flyway PostgreSQL docs.
 * Hoàn thành: migrate PostgreSQL 18 thật với module đúng; không thay POM hoặc dùng H2.</p>
 * ANSWER Q1:
 * SOLUTION-BEGIN
 * DB đã áp dụng version không chạy lại chỉ vì file thay đổi; sửa lịch sử tạo drift/checksum mismatch.
 * Thêm migration version mới để mọi môi trường nhận cùng thay đổi theo thứ tự.
 * SOLUTION-END
 * ANSWER Q2:
 * SOLUTION-BEGIN
 * validate kiểm tra table/column/type của mapping, không tạo table hoặc sửa column bị thiếu.
 * Không coi validate là kiểm chứng đầy đủ index, check constraint hay dữ liệu nghiệp vụ.
 * SOLUTION-END
 * ANSWER Q3:
 * SOLUTION-BEGIN
 * Flyway lưu lịch sử DDL có version; Hibernate chỉ kiểm tra mapping tương thích sau migrate.
 * Hai cơ chế cùng create/update schema làm nguồn sự thật mâu thuẫn và deployment khó tái hiện.
 * SOLUTION-END
 * ANSWER Q4:
 * SOLUTION-BEGIN
 * Constraint bảo vệ mọi writer và race, kể cả writer không qua Java validation.
 * Index phục vụ access path tại DB; B09 có orders_owner_page cho customer/created_at/id.
 * SOLUTION-END
 * ANSWER Q5:
 * SOLUTION-BEGIN
 * flyway-database-postgresql cung cấp hỗ trợ PostgreSQL cho Flyway modular; JDBC driver riêng.
 * Boot 4.1.1 quản lý version; starter-flyway không thay thế việc kiểm tra database module.
 * SOLUTION-END
 */
public final class Ex01_SchemaOwnership {
    private Ex01_SchemaOwnership() {}
    public static final Integer HISTORY_AFTER_RERUN = 1; // SOLUTION-VALUE

    @Entity(name = "MigrationProduct") @Table(name = "products")
    public static class Product {
        @Id public Long id;
        @Column(nullable = false, length = 200) public String name;
        @Column(nullable = false, precision = 19, scale = 2) public BigDecimal price;
        @Column(nullable = false) public int stock;
        public Product() {}
    }
    @Entity(name = "MigrationCustomer") @Table(name = "customers")
    public static class Customer {
        @Id public Long id;
        @Column(nullable = false, length = 100) public String subject;
        public Customer() {}
    }
    @Entity(name = "MigrationOrder") @Table(name = "orders")
    public static class Order {
        @Id public Long id;
        @Column(name = "customer_id", nullable = false) public long customerId;
        @Column(nullable = false, length = 20) public String status;
        @Column(nullable = false, length = 3) public String currency;
        @Column(nullable = false, precision = 19, scale = 2) public BigDecimal total;
        @Column(name = "created_at", nullable = false) public java.time.Instant createdAt;
        public Order() {}
    }
    @Entity(name = "MigrationItem") @Table(name = "order_items") @IdClass(ItemId.class)
    public static class Item {
        @Id @Column(name = "order_id") public long orderId;
        @Id @Column(name = "product_id") public long productId;
        @Column(nullable = false) public int quantity;
        @Column(name = "unit_price", nullable = false, precision = 19, scale = 2) public BigDecimal unitPrice;
        public Item() {}
    }
    public record ItemId(long orderId, long productId) implements java.io.Serializable {}
    public static Flyway flyway(DataSource dataSource) {
        // SOLUTION-BEGIN throw B09
        return Flyway.configure().dataSource(dataSource).locations("classpath:phase05/d09_flyway")
                .javaMigrations(new TopicMigration()).load();
        // SOLUTION-END
    }
    public static String migrationSql() {
        // SOLUTION-BEGIN throw B09
        return """
                create table customers(id bigint primary key, subject varchar(100) not null unique);
                create table products(id bigint primary key, name varchar(200) not null,
                    price numeric(19,2) not null check(price>0), stock integer not null check(stock>=0));
                create table orders(id bigint primary key, customer_id bigint not null references customers(id),
                    status varchar(20) not null check(status in ('NEW','CANCELLED')),
                    currency varchar(3) not null check(currency='USD'), total numeric(19,2) not null check(total>=0),
                    created_at timestamp with time zone not null);
                create table order_items(order_id bigint not null references orders(id),
                    product_id bigint not null references products(id), quantity integer not null check(quantity between 1 and 1000),
                    unit_price numeric(19,2) not null check(unit_price>0), primary key(order_id,product_id));
                create index orders_owner_page on orders(customer_id,created_at,id);
                """;
        // SOLUTION-END
    }
    // Provided adapter: learner SQL supplied at migrate time, no broad/default migration discovery.
    public static class TopicMigration implements JavaMigration {
        @Override public boolean canExecuteInTransaction() { return true; }
        @Override public MigrationVersion getVersion() { return MigrationVersion.fromVersion("1"); }
        @Override public String getDescription() { return "topic nine schema"; }
        @Override public Integer getChecksum() { return migrationSql().hashCode(); }
        @Override public void migrate(Context context) throws Exception {
            try (var statement = context.getConnection().createStatement()) { statement.execute(migrationSql()); }
        }
    }
}
