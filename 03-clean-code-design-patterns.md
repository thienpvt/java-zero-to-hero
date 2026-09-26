# Giai đoạn 3 — Clean Code & Design Patterns

## Mục tiêu

Ứng viên phải chuyển từ:

```text
Code chạy được
```

sang:

```text
Code chạy đúng
+ dễ hiểu
+ dễ test
+ dễ thay đổi
+ có boundary rõ ràng
```

Không đánh giá ứng viên dựa trên khả năng đọc thuộc định nghĩa pattern.

**Cách học:** dùng cùng một project Java (JDK 21 hoặc 25 LTS) để refactor từng bước. Trước mỗi lần tách module, viết test ghi lại hành vi hiện có; sau đó kiểm chứng hành vi và giải thích độ phức tạp mới có xứng đáng không.

Quan trọng hơn là:
- Nhận ra vấn đề thiết kế.
- Chọn abstraction phù hợp.
- Giải thích trade-off.
- Biết khi nào KHÔNG nên dùng pattern.

---

## 1. Single Responsibility Principle

### Kiến thức cần nắm

Một module/class nên có một nhóm trách nhiệm cohesive và một lý do chính để thay đổi.

Ví dụ xấu:

```java
class OrderService {

    void createOrder() {
        // validate request
        // calculate price
        // save database
        // charge payment
        // send email
        // generate PDF
    }
}
```

Cần biết tách boundary hợp lý thay vì tạo class cho từng dòng code.

---

### Câu hỏi kiểm tra

1. SRP có nghĩa là "một class chỉ được có một method" không?
2. "One reason to change" nên hiểu thế nào?
3. Class quá lớn thường thể hiện dấu hiệu gì?
4. Tách class quá nhỏ có thể gây vấn đề gì?
5. Với OrderService trên, bạn sẽ tách trách nhiệm thế nào?
6. Làm sao biết việc tách abstraction thực sự có ích?

---

## 2. Open/Closed Principle

### Kiến thức cần nắm

Software entity nên:
- Open for extension.
- Closed for modification.

Không có nghĩa là không bao giờ sửa code cũ.

Hiểu cách polymorphism/strategy giúp mở rộng behavior.

---

### Câu hỏi kiểm tra

1. OCP thực sự muốn giảm vấn đề gì?
2. Vì sao switch khổng lồ theo payment type có thể là dấu hiệu cần abstraction?
3. Có phải mọi `if` đều vi phạm OCP?
4. OCP có thể dẫn tới over-engineering như thế nào?
5. Khi requirement chưa ổn định, có nên abstract mọi thứ ngay không?

---

## 3. Liskov Substitution Principle

### Kiến thức cần nắm

Subtype phải có thể thay thế base type mà không phá vỡ expectation của caller.

Không chỉ là compile được.

Hiểu:
- Behavioral contract.
- Preconditions.
- Postconditions.
- Invariants ở mức khái niệm.

---

### Câu hỏi kiểm tra

1. LSP khác inheritance syntax như thế nào?
2. Rectangle/Square problem thể hiện vấn đề gì?
3. Một subtype throw UnsupportedOperationException cho method của parent có thể là dấu hiệu gì?
4. Khi nào composition tốt hơn inheritance?
5. Interface quá lớn có thể khiến implementation vi phạm contract như thế nào?

---

## 4. Interface Segregation Principle

### Kiến thức cần nắm

Client không nên phụ thuộc vào method mà nó không cần.

Tránh fat interface.

Ví dụ:

```java
interface Machine {
    void print();
    void scan();
    void fax();
}
```

Không phải machine nào cũng hỗ trợ mọi operation.

---

### Câu hỏi kiểm tra

1. ISP giải quyết vấn đề gì?
2. Interface có càng nhỏ càng tốt không?
3. Một implementation có nhiều method throw UnsupportedOperationException nói lên điều gì?
4. ISP liên quan đến cohesion như thế nào?
5. Khi nào tách interface gây fragmentation quá mức?

---

## 5. Dependency Inversion Principle

### Kiến thức cần nắm

High-level policy không nên phụ thuộc trực tiếp low-level implementation.

Cả hai nên phụ thuộc abstraction.

Ví dụ:

```java
interface PaymentGateway {
    PaymentResult charge(Money amount);
}
```

Service phụ thuộc:

```java
PaymentGateway
```

thay vì:

```java
StripePaymentGateway
```

---

### Câu hỏi kiểm tra

1. Dependency Inversion và Dependency Injection khác nhau thế nào?
2. DIP giải quyết coupling như thế nào?
3. Có cần tạo interface cho mọi class không?
4. Repository interface nên nằm ở layer nào trong Clean Architecture?
5. Nếu abstraction chỉ có một implementation thì interface có luôn cần thiết không?
6. DIP giúp testing thế nào?

---

## 6. Dependency Injection

### Kiến thức cần nắm

Hiểu:
- Constructor injection.
- Setter injection.
- Field injection.
- Dependency graph.
- IoC.

Ưu tiên constructor injection khi dependency bắt buộc.

---

### Câu hỏi kiểm tra

1. DI giải quyết vấn đề gì?
2. DI và IoC khác nhau thế nào?
3. Vì sao constructor injection thường được ưu tiên?
4. Field injection gây khó khăn gì cho unit test?
5. Constructor có 12 dependencies cho thấy điều gì?
6. DI container có phải điều kiện bắt buộc để áp dụng DI không?
7. Có thể thực hiện DI bằng Java thuần không?

---

## 7. Composition vs Inheritance

### Kiến thức cần nắm

Ưu tiên composition khi muốn reuse behavior mà không có quan hệ "is-a" thật sự.

Inheritance:
- Strong coupling.
- Expose parent behavior.
- Fragile hierarchy nếu dùng sai.

Composition:

```java
class OrderService {

    private final PricingPolicy pricingPolicy;
}
```

---

### Câu hỏi kiểm tra

1. Khi nào inheritance phù hợp?
2. Khi nào composition tốt hơn?
3. "is-a" và "has-a" khác nhau thế nào?
4. Vì sao deep inheritance hierarchy khó bảo trì?
5. Template Method sử dụng inheritance; Strategy thường sử dụng composition. Trade-off là gì?

---

## 8. Separation of Concerns

### Kiến thức cần nắm

Phân tách:
- HTTP concerns.
- Business logic.
- Persistence.
- External integrations.
- Serialization.
- Security.

Controller không nên chứa toàn bộ business logic.

Repository không nên chứa business policy.

---

### Câu hỏi kiểm tra

1. Controller nên chịu trách nhiệm gì?
2. Validation business rule nên nằm ở đâu?
3. Repository có nên gửi email không?
4. Entity có nên phụ thuộc HTTP request không?
5. Separation of Concerns giúp testability thế nào?
6. Có thể phân layer quá mức không?

---

## 9. Layered Architecture

### Kiến thức cần nắm

Một luồng gọi phổ biến (sơ đồ luồng xử lý, không quy định hướng dependency lúc biên dịch):

```text
Controller
    ↓
Application/Service
    ↓
Domain
    ↓
Repository
    ↓
Database
```

Hiểu:
- Layer responsibilities.
- Dependency direction.
- DTO.
- Entity.
- Mapping.

Với thiết kế tách domain, domain không nên import controller, SDK bên ngoài hay JPA entity; interface/port phục vụ nghiệp vụ có thể đặt gần domain/application, còn adapter triển khai ở infrastructure. Không bắt buộc mọi project có đủ các lớp trong sơ đồ.

Không đồng nhất database entity với API contract trong mọi hệ thống.

---

### Câu hỏi kiểm tra

1. Vì sao không nên expose JPA Entity trực tiếp từ REST API trong nhiều hệ thống?
2. DTO giải quyết vấn đề gì?
3. Mapping thủ công và mapper library có trade-off gì?
4. Business logic nên nằm ở controller hay service/domain?
5. Có phải mọi project đều cần 5 layer không?
6. Layered architecture có nhược điểm gì?

---

## 10. Strategy Pattern

### Kiến thức cần nắm

Dùng khi nhiều algorithm/behavior có cùng contract và muốn chọn behavior linh hoạt.

Ví dụ:

```java
public interface DiscountPolicy {
    BigDecimal discount(Order order);
}
```

Implementations:

```text
NormalDiscount
VipDiscount
CampaignDiscount
```

---

### Câu hỏi kiểm tra

1. Strategy giải quyết vấn đề gì?
2. Strategy tốt hơn switch khi nào?
3. Có phải có hai nhánh if là nên tạo Strategy không?
4. Strategy liên quan OCP thế nào?
5. Strategy runtime selection được thực hiện thế nào?
6. Có thể kết hợp Strategy với Spring DI ra sao?

---

## 11. Factory Pattern

### Kiến thức cần nắm

Factory encapsulate object creation.

Phân biệt ở mức thực dụng:
- Simple factory.
- Factory Method.
- Abstract Factory.

Không cần ép mọi `new` vào factory.

---

### Câu hỏi kiểm tra

1. Factory giải quyết vấn đề gì?
2. Khi nào `new` trực tiếp hoàn toàn hợp lý?
3. Factory khác Builder thế nào?
4. Factory có thể chọn implementation dựa trên runtime data như thế nào?
5. Factory quá lớn với switch khổng lồ có còn extensible không?

---

## 12. Builder Pattern

### Kiến thức cần nắm

Hữu ích với object:
- Nhiều parameters.
- Optional parameters.
- Cần readable construction.
- Có validation trong creation process.

Ví dụ:

```java
User user = User.builder()
    .name("Alice")
    .email("alice@example.com")
    .age(30)
    .build();
```

---

### Câu hỏi kiểm tra

1. Builder giải quyết telescoping constructor thế nào?
2. Builder và Factory khác nhau ở đâu?
3. Builder có giúp object immutable không?
4. Builder có đáng dùng với object chỉ có hai field không?
5. Validation nên xảy ra ở builder hay object constructor?

---

## 13. Adapter Pattern

### Kiến thức cần nắm

Adapter chuyển interface của hệ thống ngoài thành interface mà application mong muốn.

Ví dụ:

```text
Application
    ↓
PaymentGateway
    ↓
StripeAdapter
    ↓
Stripe SDK
```

---

### Câu hỏi kiểm tra

1. Adapter giải quyết vấn đề gì?
2. Adapter khác Decorator thế nào?
3. Vì sao wrap third-party SDK bằng adapter có lợi?
4. Adapter giúp testing thế nào?
5. Khi Stripe SDK đổi API, adapter giúp giới hạn blast radius ra sao?

---

## 14. Decorator Pattern

### Kiến thức cần nắm

Decorator bổ sung behavior mà giữ cùng interface.

Ví dụ:

```text
PaymentService
    ↑
LoggingPaymentService
    ↑
MetricsPaymentService
```

Hiểu composition thay vì subclass explosion.

---

### Câu hỏi kiểm tra

1. Decorator khác inheritance thế nào?
2. Decorator khác Proxy ở mục đích gì?
3. Có thể chain nhiều decorator không?
4. Java I/O sử dụng ý tưởng decorator như thế nào?
5. Decorator có thể làm call stack khó hiểu như thế nào?

---

## 15. Observer Pattern

### Kiến thức cần nắm

Một publisher thông báo nhiều subscribers khi event xảy ra.

Ứng dụng:
- Domain events.
- GUI events.
- Event-driven systems ở mức local.

Hiểu:
- Coupling.
- Ordering.
- Error handling.
- Synchronous vs asynchronous observer.

---

### Câu hỏi kiểm tra

1. Observer giải quyết vấn đề gì?
2. Observer khác message broker như Kafka thế nào?
3. Nếu một listener fail thì các listener khác nên xử lý thế nào?
4. Observer synchronous có thể ảnh hưởng latency ra sao?
5. Event ordering có quan trọng không?
6. Listener không được unregister có thể gây vấn đề gì?

---

## 16. Template Method

### Kiến thức cần nắm

Base class định nghĩa algorithm skeleton.

Subclass override một số steps.

Ví dụ:

```java
abstract class Importer {

    public final void importData() {
        read();
        validate();
        persist();
    }

    protected abstract void read();

    protected abstract void validate();

    protected abstract void persist();
}
```

---

### Câu hỏi kiểm tra

1. Template Method giải quyết vấn đề gì?
2. Template Method khác Strategy thế nào?
3. Pattern này dựa trên inheritance hay composition?
4. Khi có quá nhiều override points thì vấn đề gì xảy ra?
5. Khi nào Strategy linh hoạt hơn Template Method?

---

## 17. Proxy Pattern

### Kiến thức cần nắm

Proxy kiểm soát access tới object khác.

Ứng dụng:
- Security.
- Transaction.
- Lazy loading.
- Logging.
- Remote call.

Spring AOP là ví dụ quan trọng để nghiên cứu.

---

### Câu hỏi kiểm tra

1. Proxy khác Decorator thế nào?
2. Spring `@Transactional` có liên quan proxy như thế nào?
3. Self-invocation có thể gây vấn đề với proxy-based AOP vì sao?
4. Lazy-loading proxy trong Hibernate hoạt động ở mức khái niệm thế nào?
5. Proxy có thể thêm cross-cutting concern nào?

---

## 18. Singleton

### Kiến thức cần nắm

Hiểu pattern chứ không mặc định sử dụng.

Các vấn đề:
- Global state.
- Hidden dependency.
- Testing.
- Concurrency.
- Lifecycle.

Phân biệt Singleton pattern và singleton scope của DI container.

---

### Câu hỏi kiểm tra

1. Singleton giải quyết vấn đề gì?
2. Vì sao Singleton thường bị xem là global state?
3. Singleton gây khó testing như thế nào?
4. Singleton có mặc định thread-safe không?
5. Spring singleton bean có giống GoF Singleton hoàn toàn không?
6. Một Spring singleton service chứa mutable field có vấn đề gì?
7. Enum singleton có ưu điểm gì trong Java?

---

## 19. Clean Code Fundamentals

### Kiến thức cần nắm

#### Naming
Tên phải thể hiện intent.

#### Functions
- Cohesive.
- Limited responsibility.
- Ít side effects bất ngờ.

#### Error handling
- Exceptions có ý nghĩa.
- Không swallow exception.
- Không dùng exception cho normal control flow.
- Giữ nguyên nguyên nhân khi dịch lỗi ở boundary; đóng resource đúng vòng đời.

#### Comments
Comment giải thích "why" tốt hơn việc lặp lại "what".

#### Duplication
Loại bỏ duplication có ý nghĩa, nhưng tránh abstraction giả.

---

### Câu hỏi kiểm tra

1. Clean code có đồng nghĩa code ngắn không?
2. Một method 100 dòng có luôn sai không?
3. Khi nào duplicate code tốt hơn premature abstraction?
4. Comment nhiều có phải code tốt không?
5. Boolean parameter thường có thể báo hiệu vấn đề thiết kế gì?
6. Method tên `process()` cho biết vấn đề gì?
7. Exception nên được translate ở boundary nào?
8. Catch `Exception` rồi log và tiếp tục có rủi ro gì?

---

## 20. Testability as a Design Signal

### Kiến thức cần nắm

Code khó test thường có thể đang:
- Coupling cao.
- Hidden dependencies.
- Global state.
- Static dependencies.
- Nhiều responsibility.

Ví dụ tốt:

```java
class OrderService {

    private final PaymentGateway paymentGateway;
    private final OrderRepository repository;

    OrderService(
            PaymentGateway paymentGateway,
            OrderRepository repository) {

        this.paymentGateway = paymentGateway;
        this.repository = repository;
    }
}
```

Dependencies explicit và thay thế được trong test.

---

### Câu hỏi kiểm tra

1. Vì sao constructor injection tăng testability?
2. Static utility có luôn xấu không?
3. Vì sao gọi trực tiếp external API bên trong business method làm unit test khó?
4. Mock quá nhiều dependency có thể báo hiệu điều gì?
5. Unit test khó viết có thể phản ánh vấn đề thiết kế nào?
6. Khi mock gần như mọi class, test có còn chứng minh behavior quan trọng không?

---

## Architecture Exercise

Cho code:

```java
class OrderService {

    public void createOrder(OrderRequest request) {

        // validate

        // query product database

        // calculate discount

        // save order

        // call Stripe

        // decrease inventory

        // generate invoice PDF

        // send email

        // send Kafka event
    }
}
```

Ứng viên phải thiết kế lại thành một kiến trúc hợp lý.

Ví dụ các abstraction có thể xuất hiện:

```text
OrderController

OrderApplicationService
    |
    +-- OrderRepository
    +-- PricingPolicy
    +-- PaymentGateway
    +-- InventoryService
    +-- InvoiceService
    +-- EventPublisher
```

Không yêu cầu ứng viên tạo abstraction cho mọi dòng code.

Phải giải thích được:
- Boundary nằm ở đâu.
- Dependency direction.
- Transaction boundary.
- Failure handling.
- External system abstraction.
- Tại sao chọn từng abstraction.

**Ràng buộc của bài:** database, thanh toán và Kafka không nằm trong một transaction ACID cục bộ. Xác định điểm commit đơn hàng, hậu quả nếu charge thành công nhưng lưu/thông báo thất bại, và chiến lược xử lý rõ ràng (trạng thái trung gian, retry có idempotency hoặc outbox khi học giai đoạn 7). Không cho `@Transactional` bao toàn bộ network call rồi giả định rollback được hệ thống ngoài.

**Cách nộp:** một commit trước refactor có test ghi lại behavior; một commit sau refactor với cùng test và test cho lỗi thanh toán, lỗi lưu, lỗi publish. Ghi sơ đồ dependency, phạm vi transaction và lý do chọn/không chọn các pattern. Chỉ mock boundary ngoài; dùng test thật cho logic thuần.

---

## Pattern Selection Exercise

Đưa ra các tình huống sau và yêu cầu ứng viên chọn giải pháp kèm lý do.

### Case 1

Hệ thống có:

```text
CreditCardPayment
BankTransferPayment
WalletPayment
```

Hỏi:
- Strategy có phù hợp không?
- Khi nào Factory cần xuất hiện?

### Case 2

Cần thêm:

```text
logging
metrics
authorization
```

xung quanh cùng một service.

Hỏi:
- Decorator hay Proxy?
- Có nên sử dụng AOP?

### Case 3

Application phải gọi ba payment providers.

Hỏi:
- Có nên để business layer import SDK từng provider trực tiếp?
- Adapter giúp gì?

### Case 4

Import CSV, JSON và XML đều có flow:

```text
read
validate
transform
persist
```

Hỏi:
- Template Method hay Strategy?
- Trade-off của mỗi cách?

---

## Dấu hiệu ứng viên chỉ học thuộc

Cần đào sâu nếu ứng viên:

- Chỉ đọc định nghĩa SOLID.
- Nói "luôn dùng interface".
- Nói "inheritance luôn xấu".
- Nói "pattern càng nhiều càng tốt".
- Không giải thích được trade-off.
- Không đưa được ví dụ thực tế.
- Không nhận ra over-engineering.
- Không thể refactor một đoạn code cụ thể.

Một câu hỏi đào sâu hiệu quả là:

> Nếu bỏ pattern này đi và viết implementation đơn giản hơn thì cụ thể vấn đề gì sẽ xuất hiện?

Sau đó hỏi:

> Vấn đề đó đang tồn tại ngay bây giờ hay chỉ là giả định về tương lai?

---

## Exit Criteria

Ứng viên hoàn thành giai đoạn khi có thể:

- Giải thích SOLID qua code thay vì thuộc định nghĩa.
- Nhận biết coupling và cohesion.
- Chọn composition/inheritance hợp lý.
- Thiết kế dependency rõ ràng.
- Phân biệt DI và IoC.
- Sử dụng Strategy, Factory, Builder, Adapter, Decorator, Observer, Template Method và Proxy đúng ngữ cảnh.
- Biết tại sao không nên áp dụng pattern một cách máy móc.
- Refactor một service lớn thành các boundary hợp lý.
- Giải thích trade-off của kiến trúc mình lựa chọn.
- Giữ nguyên hành vi bằng test khi refactor, và không nhầm ranh giới transaction trong process với giao dịch xuyên các dịch vụ ngoài.

**Tài liệu chuẩn:** [Java Records](https://docs.oracle.com/en/java/javase/25/language/records.html), [Spring Framework: declarative transaction management](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html).
