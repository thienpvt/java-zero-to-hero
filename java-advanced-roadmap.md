# Java Advanced Roadmap

Mục tiêu: từ Java cơ bản lên Java Backend Developer có nền tảng vững về Java, JVM, database, Spring và distributed systems.

**Mốc học:** dùng một JDK LTS thống nhất (21 hoặc 25; ưu tiên 25 cho project mới), ghi rõ version trong project. Dùng Git, Maven hoặc Gradle và JUnit ngay từ giai đoạn 1; giai đoạn 8 tập trung vào vận hành thay vì đợi tới đó mới học công cụ. Mỗi giai đoạn kết thúc bằng code chạy được, test và một ghi chú giải thích các lựa chọn.

## Giai đoạn 1 — Java Core nâng cao
- Generics
- Collections internals
- equals/hashCode
- Comparable/Comparator
- Immutability
- Lambda & Functional Interface
- Stream API
- Optional
- Date/Time API
- Reflection & Annotation
- Records, sealed types và pattern matching
- Exception handling, try-with-resources và I/O cơ bản

**Đầu ra:** viết thư viện nhỏ chọn đúng collection/API, xử lý tài nguyên an toàn và giải thích được trade-off.

---

## Giai đoạn 2 — JVM & Concurrency

### JVM
- Bytecode
- Class Loading
- Stack / Heap / Metaspace
- Object lifecycle
- Garbage Collection
- JIT Compiler
- Memory leak
- JVM diagnostics cơ bản
- Thread dump, GC logs, JFR ở mức sử dụng

### Concurrency
- Thread / Runnable / Callable
- synchronized / volatile
- Java Memory Model
- Locks
- Atomic classes
- ExecutorService
- CompletableFuture
- Concurrent Collections
- Race condition / Deadlock
- Virtual threads cho tác vụ I/O, cancellation và giới hạn tài nguyên

**Đầu ra:** sửa một race condition, giới hạn tải cho executor và tìm nguyên nhân một vấn đề JVM bằng bằng chứng.

---

## Giai đoạn 3 — Clean Code & Design Patterns
- SOLID
- Dependency Injection
- Composition vs Inheritance
- Separation of Concerns
- Layered Architecture
- Strategy
- Factory
- Builder
- Adapter
- Decorator
- Observer
- Template Method
- Proxy
- Singleton
- Refactor có test giữ hành vi; xác định transaction và integration boundary

**Đầu ra:** refactor một service thực tế với test trước/sau và giải thích những abstraction đã chọn.

---

## Giai đoạn 4 — Database & Persistence
- SQL nâng cao
- Index
- Transaction & Isolation Level
- JDBC
- JPA / Hibernate
- Entity lifecycle
- Fetching
- N+1
- Locking
- Query optimization

**Đầu ra:** truy vết SQL thực tế của một API, kiểm chứng index/plan và xử lý transaction đúng phạm vi.

---

## Giai đoạn 5 — Spring Boot
- Spring Core / IoC / DI
- Spring MVC
- REST API
- Spring Data JPA
- Validation
- Exception Handling
- Transaction
- Spring Security
- JWT / OAuth2

**Đầu ra:** REST API có validation, phân quyền, migration DB và đường xử lý lỗi rõ ràng.

---

## Giai đoạn 6 — Testing
- JUnit
- Mockito
- Unit Test
- Integration Test
- Testcontainers
- Testing Pyramid

**Đầu ra:** test có giá trị ở unit/integration/API, chạy tự động và không phụ thuộc thứ tự.

---

## Giai đoạn 7 — Distributed Systems
- Redis
- Kafka / RabbitMQ
- Cache
- Messaging
- Idempotency
- Event-driven Architecture
- Saga
- Outbox Pattern
- Circuit Breaker
- Timeout, retry/backoff và giới hạn tải

**Đầu ra:** chứng minh được idempotency, retry/backoff, failure handling và giới hạn nhất quán qua một luồng thực tế.

---

## Giai đoạn 8 — Production & DevOps Fundamentals
- Maven / Gradle
- Git
- Linux
- Docker
- Docker Compose
- CI/CD
- Logging
- Metrics
- Monitoring
- Cấu hình, secret, health checks và quy trình phát hành

**Đầu ra:** build và chạy ứng dụng lặp lại được, có log/metric/trace tối thiểu và quy trình CI.

---

## Giai đoạn 9 — Algorithms & System Design
Học song song với các giai đoạn trên theo mục tiêu công việc/phỏng vấn; không chờ hoàn thành hết DSA mới làm backend.

### DSA
- Array / String
- Hash Table
- Stack / Queue
- Tree / Heap
- Graph
- DFS / BFS
- Backtracking
- Dynamic Programming

### System Design
- Scalability
- Load balancing
- Caching
- Database scaling
- Messaging
- Consistency
- Availability
- Distributed systems fundamentals

**Đầu ra:** trình bày trade-off, ước lượng tải và vẽ luồng dữ liệu của một dịch vụ đã xây.

**Tài liệu chuẩn:** [Java SE Support Roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html), [Java Language Changes by Release](https://docs.oracle.com/en/java/javase/25/language/java-language-changes-release.html).
