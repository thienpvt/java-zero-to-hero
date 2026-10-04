package phase05.d08_jpa_queries;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.junit.jupiter.api.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import phase05.support.PostgresFixture;

class Ex01_QueryBehaviorTest {
    static PostgresFixture db;
    final List<String> sql = new ArrayList<>();
    final StatementInspector inspector = statement -> { sql.add(statement); return statement; };

    @BeforeAll static void start() { db = PostgresFixture.start(); }
    @AfterAll static void stop() { if (db != null) db.close(); }
    @AfterEach void clearPrincipal() { SecurityContextHolder.clearContext(); }
    @BeforeEach void reset() throws Exception {
        db.reset("""
                create table customers(id bigint primary key, subject varchar(100) unique not null);
                create table orders(id bigint primary key, customer_id bigint not null references customers,
                    status varchar(20) not null, currency varchar(3) not null,
                    total numeric(19,2) not null, created_at timestamp with time zone not null);
                create table order_items(id bigint primary key, order_id bigint not null references orders,
                    product_id bigint not null, quantity integer not null, unit_price numeric(19,2) not null);
                """, """
                insert into customers values (1,'alice'),(2,'bob');
                insert into orders values
                    (1,2,'NEW','USD',1,'2026-01-01T00:00:00Z'),
                    (2,1,'NEW','USD',2,'2026-01-02T00:00:00Z'),
                    (3,1,'NEW','USD',3,'2026-01-02T00:00:00Z'),
                    (4,1,'NEW','USD',4,'2026-01-03T00:00:00Z'),
                    (5,2,'NEW','USD',5,'2026-01-04T00:00:00Z');
                insert into order_items values (1,2,10,1,2),(2,3,11,1,3),(3,4,12,1,4);
                """);
    }

    @Test void q01_saveIsNotCommit() {
        assertNotNull(Ex01_QueryBehavior.VISIBLE_BEFORE_COMMIT, "thay null: dự đoán Q1");
        try (var sessions = sessions();
             var session = sessions.openSession()) {
            var tx = session.beginTransaction();
            session.persist(new Ex01_QueryBehavior.OrderEntity(90, 1));
            session.flush();
            var jdbc = new org.springframework.jdbc.core.JdbcTemplate(db.dataSource());
            assertEquals(Ex01_QueryBehavior.VISIBLE_BEFORE_COMMIT.intValue(),
                    jdbc.queryForObject("select count(*) from orders where id=90", Integer.class));
            assertEquals(0, jdbc.queryForObject("select count(*) from orders where id=90", Integer.class));
            tx.commit();
            assertEquals(1, jdbc.queryForObject("select count(*) from orders where id=90", Integer.class));
        }
    }

    @Test void q02_countsExecutedSelectsNotRepositoryCalls() {
        try (var sessions = sessions()) {
            var query = new Ex01_QueryBehavior.OrderQueries(sessions);
            sql.clear();
            assertEquals(3, query.naiveOrderPage("alice").size());
            long naive = selects();
            sql.clear();
            assertEquals(3, query.orderPage("alice", 0, 20).items().size());
            assertEquals(3, selects());
            assertEquals(4, naive);
        }
    }

    @Test void b08_ownerPredicatePagingTiesAndScopeAtHttpBoundary() throws Exception {
        try (var sessions = sessions()) {
            var query = new Ex01_QueryBehavior.OrderQueries(sessions);
            var controller = new Ex01_QueryBehavior.OrderController(query);
            var mvc = MockMvcBuilders.standaloneSetup(controller).build();
            principal("alice", "SCOPE_orders.read");
            sql.clear();
            mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/orders")
                    .param("page", "0").param("size", "20"))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.totalItems").value(3))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.items[0].id").value(2))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.items[2].id").value(4));
            assertEquals(3, selects());
            assertTrue(sql.stream().anyMatch(s -> s.toLowerCase().contains("limit") && s.toLowerCase().contains("offset")));
            assertEquals(List.of(2L), ids(query.orderPage("alice", 0, 1)));
            assertEquals(List.of(3L), ids(query.orderPage("alice", 1, 1)));
            assertEquals(List.of(4L), ids(query.orderPage("alice", 2, 1)));
            assertEquals(List.of(1L, 5L), ids(query.orderPage("bob", 0, 20)));
            principal("alice", "SCOPE_products.read");
            assertThrows(AccessDeniedException.class, () -> controller.page(0, 20));
        }
    }

    @Test void q05_projectionSurvivesClosedContextAndBoundsInput() {
        try (var sessions = sessions()) {
            var query = new Ex01_QueryBehavior.OrderQueries(sessions);
            var page = query.orderPage("alice", 0, 2);
            assertEquals(2, page.items().size()); // valid learner path before negative cases
            assertEquals(10L, page.items().getFirst().items().getFirst().productId());
            assertEquals(2, page.totalPages());
            assertEquals(3, page.totalItems());
            assertThrows(IllegalArgumentException.class, () -> query.orderPage("alice", -1, 20));
            assertThrows(IllegalArgumentException.class, () -> query.orderPage("alice", 0, 0));
            assertThrows(IllegalArgumentException.class, () -> query.orderPage("alice", 0, 101));
            assertThrows(IllegalArgumentException.class, () -> query.orderPage(null, 0, 20));
            assertTrue(query.orderPage("unknown", 0, 100).items().isEmpty());
            assertTrue(query.orderPage("alice", Integer.MAX_VALUE, 100).items().isEmpty());
        }
    }

    @Test void q04_experimentRuns() {
        try (var sessions = sessions()) {
            var page = new Ex01_QueryBehavior.OrderQueries(sessions).orderPage("alice", 0, 20);
            System.out.println("B08 DTO ngoài session: " + page.items().size());
        }
    }

    org.hibernate.SessionFactory sessions() {
        return new org.hibernate.cfg.Configuration()
                .addAnnotatedClass(Ex01_QueryBehavior.OrderEntity.class)
                .addAnnotatedClass(Ex01_QueryBehavior.ItemEntity.class)
                .setProperty("hibernate.hbm2ddl.auto", "validate")
                .setProperty("hibernate.connection.url", db.jdbcUrl())
                .setProperty("hibernate.connection.username", db.username())
                .setProperty("hibernate.connection.password", db.password())
                .setStatementInspector(inspector).buildSessionFactory();
    }
    long selects() { return sql.stream().filter(s -> s.stripLeading().toLowerCase().startsWith("select")).count(); }
    static List<Long> ids(Ex01_QueryBehavior.PageResponse<Ex01_QueryBehavior.OrderResponse> page) {
        return page.items().stream().map(Ex01_QueryBehavior.OrderResponse::id).toList();
    }
    static void principal(String subject, String scope) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                subject, "unused", List.of(new SimpleGrantedAuthority(scope))));
    }
}
