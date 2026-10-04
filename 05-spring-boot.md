# Giai đoạn 5 — Spring Boot

## Mục tiêu

Xây một REST API bảo trì được trên nền Spring, không biến framework thành một tập annotation học thuộc. Sau giai đoạn này, bạn phải biết request đi qua những boundary nào, dữ liệu được kiểm tra và ghi ra sao, ai được phép truy cập tài nguyên nào, và test nào chứng minh từng hành vi.

**Đầu ra:** API quản lý sản phẩm và đơn hàng có validation, phân quyền, migration DB, transaction có phạm vi rõ, lỗi theo một contract ổn định và test tự động.

## Cách học và phạm vi

Dùng cùng project domain nhỏ xuyên suốt: khách hàng, sản phẩm, đơn hàng, dòng hàng (`order_items`) và tồn kho. Bắt đầu bằng modular monolith có package/module rõ ràng; chưa thêm microservice, message broker, cache hay distributed transaction. Mỗi chủ đề gồm kiến thức, câu hỏi tự kiểm tra và một bài thực hành ngắn. Giữ test hành vi trước mỗi refactor; ưu tiên ít abstraction, giải thích được trade-off và failure mode.

## Tiên quyết và phiên bản

- Hoàn thành giai đoạn 4: SQL, JDBC/JPA, entity lifecycle, fetching/N+1, locking, index và transaction/isolation. Có thể đọc SQL và kiểm tra plan cơ bản.
- Nắm giai đoạn 3: DI, layered architecture, separation of concerns, composition, boundary và refactor có test.
- Dùng JDK 21 như baseline của lộ trình. Chọn một bản Spring Boot 4.x ổn định được hỗ trợ tại thời điểm bắt đầu, chốt version trong project và dùng documentation cùng version đó; không lấy nhãn “latest” làm policy.
- Maven BOM của Spring Boot quản lý version tương thích cho dependency Spring và thư viện liên quan. BOM không tự quản lý version Maven plugin; khi tạo module, dùng Spring Boot parent hoặc khai báo plugin version tường minh. Không tự pin từng dependency nếu chưa có lý do tương thích cụ thể.
- Spring Boot 4 có thay đổi về module starter/test và một số import so với tutorial Boot 3. Chỉ dùng starter, package `jakarta.*` và test API đúng version; không trộn ví dụ Boot 3 vào project Boot 4 theo kiểu copy-paste.
- Repo roadmap hiện dùng Java/JUnit 5. Khi tạo module Spring, để một nơi duy nhất quản lý version JUnit; không kế thừa một override JUnit cũ ở parent nếu nó xung đột với BOM Boot.
- Lab dùng container PostgreSQL cần Docker daemon hoạt động; PostgreSQL cài local không cần Docker. Ghi rõ cách dựng database disposable trong project; không đổi sang H2 rồi coi là kiểm chứng tương đương.

---

## 1. Spring Core, IoC và Dependency Injection

### Kiến thức cần nắm

IoC nghĩa là framework quản lý vòng đời object và wiring thay vì ứng dụng tự khởi tạo toàn bộ dependency. DI là cách đưa dependency vào component; constructor injection giúp dependency bắt buộc hiển thị, dễ test và tránh object nửa khởi tạo. Phân biệt Spring container với tự động hóa của Spring Boot; Spring vẫn có thể dùng bằng cấu hình tường minh.

Không gắn `@Component` vào mọi class. Domain logic thuần Java có thể không cần Spring. Chọn boundary theo vai trò: web adapter, application/service, persistence adapter và cấu hình.

### Câu hỏi kiểm tra

1. IoC khác DI ở điểm nào?
2. Vì sao constructor injection thường rõ hơn field injection?
3. Một class thuần Java có cần annotation Spring để test không?
4. Khi nào wiring thủ công dễ hiểu hơn component scan?
5. DI có tự làm thiết kế tốt nếu dependency graph vẫn rối không?

### Bài thực hành

Tạo service nhận repository qua constructor. Unit test service bằng fake implementation viết tay; test không khởi tạo Spring context. Giải thích đâu là policy, đâu là adapter.

---

## 2. Beans, cấu hình và lifecycle

### Kiến thức cần nắm

Bean singleton trong Spring thường là một instance cho mỗi application context, không phải singleton toàn JVM hay bảo đảm thread-safe. Bean web có thể được gọi đồng thời bởi nhiều request; giữ state request/user trong field singleton gây race condition và rò dữ liệu. Ưu tiên bean stateless, immutable sau khởi tạo. Hiểu scope `singleton`, `prototype`, request scope và lifecycle callback ở mức cần dùng.

`@Configuration` và `@Bean` thích hợp cho wiring thư viện hoặc cấu hình có chủ ý; component stereotype phù hợp cho component ứng dụng. Tránh phụ thuộc vào thứ tự khởi tạo ngẫu nhiên; khai báo dependency qua constructor.

### Câu hỏi kiểm tra

1. Spring singleton scope bảo đảm điều gì và không bảo đảm điều gì?
2. Vì sao field mutable trong controller/service có thể hỏng dưới tải đồng thời?
3. Scope `prototype` có nghĩa là Spring quản lý toàn bộ lifecycle sau khi inject không?
4. Khi nào chọn `@Bean` thay vì `@Component`?
5. Có thể giải quyết mọi state bằng cách đổi bean sang request scope không?

### Bài thực hành

Thêm một field lưu `currentUser` vào service minh họa lỗi thiết kế bằng test/giải thích; sau đó xóa field và truyền user context qua tham số hoặc principal theo boundary phù hợp.

---

## 3. Auto-configuration, profiles và cấu hình an toàn

### Kiến thức cần nắm

Spring Boot auto-configuration dựa trên classpath, property và điều kiện; nó giảm wiring nhưng không phải phép màu. Học cách đọc condition evaluation/debug output và override đúng bean/property trước khi thêm cấu hình đối kháng. Dùng profiles cho khác biệt môi trường có chủ ý; profile không phải cách giấu secret.

Đưa endpoint, credential, signing key và password ra khỏi source control. Secret lấy từ environment/secret manager theo môi trường; không in secret vào log hoặc exception. Cấu hình production phải fail fast khi thiếu giá trị bắt buộc. Không bật debug/config dump nhạy cảm ở môi trường chia sẻ.

### Câu hỏi kiểm tra

1. Điều kiện nào thường khiến auto-configuration tạo hoặc bỏ qua bean?
2. Khi cấu hình không như mong đợi, tìm bằng chứng ở đâu trước khi thêm bean mới?
3. Profile khác secret management thế nào?
4. Vì sao giá trị mặc định an toàn khác với hard-code credential?
5. Nên phản ứng thế nào khi thiếu signing key ở môi trường production?

### Bài thực hành

Tạo cấu hình database theo profile local/test. Đặt password bằng biến môi trường trong môi trường chạy; kiểm tra log và repository không chứa giá trị thật. Cho app fail rõ ràng khi thiếu property bắt buộc.

---

## 4. Spring MVC và vòng đời HTTP request

### Kiến thức cần nắm

Theo dõi luồng từ servlet/filter chain qua `DispatcherServlet`, handler mapping, controller, binding/conversion, validation, service, serialization và response. Jackson deserialization chuyển JSON thành object đầu vào; serialization chuyển object trả về thành JSON. Hai hướng này khác nhau và đều là boundary cần kiểm soát. Controller điều phối HTTP, không nên chứa query nghiệp vụ, transaction dài hay domain policy.

Content type, encoding, conversion failure và malformed JSON là lỗi đầu vào khác nhau. HTTP response không chỉ là status: headers, body, `Location`, cache policy và content type đều là contract.

### Câu hỏi kiểm tra

1. Filter và Spring MVC interceptor khác vị trí trong request pipeline ra sao?
2. Deserialization khác serialization thế nào?
3. Vì sao controller không nên gọi `new` để khởi tạo dependency?
4. Binding lỗi có xảy ra trước khi service được gọi không?
5. API nên trả gì khi client gửi JSON sai cú pháp hoặc media type không hỗ trợ?

### Bài thực hành

Tạo endpoint `POST /api/orders` nhận JSON request DTO, gọi service và trả response DTO. Test request sai JSON, content type sai và response content type bằng MockMvc.

---

## 5. REST semantics, status code và pagination

### Kiến thức cần nắm

Chọn HTTP method/status theo semantics, không theo cách dễ code nhất. Dùng `201 Created` khi tạo resource và trả `Location` nếu resource có URL; `400` cho request không hợp lệ, `401` chưa xác thực, `403` bị từ chối, `404` không tồn tại hoặc không được lộ theo policy, `409` xung đột trạng thái, `415` media type không hỗ trợ. Dùng `GET` cho đọc, không gây side effect nghiệp vụ có chủ ý. Thiết kế idempotency cho operation theo requirement, không mặc định mọi POST idempotent.

Collection endpoint phải giới hạn page size, có thứ tự ổn định (thêm tie-breaker như ID khi sort field trùng), và kiểm soát field sort được phép. Không tin page size/sort tùy ý từ client; response pagination không nên tải toàn bộ bảng.

### Câu hỏi kiểm tra

1. `401` và `403` khác nhau thế nào?
2. Khi nào trả `201` thay vì `200`?
3. Vì sao pagination cần sort ổn định?
4. Rủi ro của page size không giới hạn là gì?
5. `409` phù hợp với xung đột nào trong đặt hàng?

### Bài thực hành

Tạo `GET /api/products` có page size tối đa và allowlist sort field. Test giới hạn, sort ổn định, `404` và `201` cùng `Location` cho thao tác tạo.

---

## 6. DTO, entity và validation

### Kiến thức cần nắm

DTO là contract của API; JPA entity là mô hình persistence. Không serialize entity trực tiếp: quan hệ lazy, proxy, vòng tham chiếu, cột nội bộ và thay đổi schema có thể lọt ra ngoài. Tạo request/response DTO riêng, map tường minh ở boundary cần thiết. Dùng `jakarta.validation` cho ràng buộc đầu vào có thể diễn đạt như bắt buộc, độ dài, định dạng và giá trị dương. Kiểm tra lại invariant như quantity dương trong service boundary; không chỉ dựa vào validation ở DTO.

Phân biệt validation tại boundary, invariant nghiệp vụ và constraint DB. Ví dụ quantity phải dương là boundary; tổng đơn và trạng thái cho phép là business rule; foreign key/unique/not-null là DB invariant. Client validation không thay server validation. DB constraint vẫn cần vì nhiều request/process có thể ghi đồng thời.

### Câu hỏi kiểm tra

1. Vì sao không trả JPA entity từ controller?
2. `@Valid` kiểm tra loại quy tắc nào, và không thể thay thế quy tắc nào?
3. Validation DTO có thể ngăn race condition giữa hai transaction không?
4. Vì sao cần cả check nghiệp vụ lẫn constraint DB?
5. Khi nào response DTO nên khác request DTO?

### Bài thực hành

Tạo DTO tạo sản phẩm với validation, thêm constraint DB phù hợp và trả DTO không có entity relation hoặc field nội bộ. Test lỗi field và lỗi constraint với response contract thống nhất.

---

## 7. Error handling và Problem Details

### Kiến thức cần nắm

Dùng `@RestControllerAdvice`/`@ExceptionHandler` để ánh xạ lỗi application/domain sang status và body ổn định; RFC 9457 `ProblemDetail` là format chuẩn có thể dùng. Không trả stack trace, SQL, token, class name nội bộ hoặc dữ liệu nhạy cảm cho client. Gắn correlation/trace identifier nếu hệ thống có cơ chế phù hợp; log đủ ngữ cảnh an toàn ở server.

Lỗi từ Spring Security filter chain có thể xảy ra trước controller, nên không đi qua `ControllerAdvice`. Cấu hình entry point/access denied handler riêng để lỗi xác thực/phân quyền có cùng contract khi cần. Không bắt `Exception` rồi biến mọi lỗi thành `400`; giữ lỗi 5xx là lỗi server.

### Câu hỏi kiểm tra

1. Vì sao lỗi validation thường là 4xx nhưng lỗi DB bất ngờ thường là 5xx?
2. `ProblemDetail` giải quyết phần nào của error contract?
3. Vì sao Security exception không nhất thiết tới `@RestControllerAdvice`?
4. Thông tin nào không được trả trong error body?
5. Có nên trả nguyên thông điệp exception từ persistence layer không?

### Bài thực hành

Định nghĩa body lỗi cho validation, not found và conflict; viết MVC tests cho status/content type/field cần thiết. Cấu hình và test response lỗi ở security filter boundary riêng.

---

## 8. Spring Data JPA và hành vi query

### Kiến thức cần nắm

Repository abstraction không xóa hành vi JPA/Hibernate. Hiểu `Optional` cho kết quả đơn có thể vắng mặt, `save`, flush, dirty checking và thời điểm SQL thực sự chạy. `save` không đồng nghĩa dữ liệu đã commit; lỗi constraint có thể chỉ xuất hiện lúc flush/commit. Chọn query tường minh khi derived query khó đọc.

Đọc SQL sinh ra, query count và execution plan. Tránh N+1; chọn fetch theo use case bằng projection/entity graph/query phù hợp. Collection fetch join kết hợp pagination có thể tạo kết quả sai hoặc phân trang trong memory; không dùng mù quáng. Mặc định Open EntityManager in View có thể giữ persistence context qua web request; tắt rõ `spring.jpa.open-in-view=false`, tải dữ liệu cần thiết trong transaction rồi map DTO.

### Câu hỏi kiểm tra

1. `save` khác commit như thế nào?
2. Vì sao test chỉ đếm repository call chưa đủ để phát hiện N+1?
3. Collection fetch join ảnh hưởng pagination ra sao?
4. Vì sao map entity sang DTO sau khi persistence context đóng cần query/fetch plan rõ?
5. Khi nào projection hiệu quả hơn load entity graph đầy đủ?

### Bài thực hành

Viết endpoint danh sách đơn hàng có pagination và response DTO. Bật SQL logging trong môi trường local, đo số query, xử lý N+1 và xác nhận DB thực hiện pagination thay vì load toàn bảng.

---

## 9. Flyway và quyền sở hữu schema

### Kiến thức cần nắm

Flyway migration là nguồn sự thật cho thay đổi schema. Mỗi migration đã áp dụng được xem như immutable; thêm migration mới để sửa schema, không sửa migration đã dùng ở môi trường khác. Với Flyway, đặt Hibernate `ddl-auto=validate` để kiểm tra mapping thay vì tự tạo/sửa schema. Không dùng `create`/`update` như cơ chế triển khai schema.

Với Boot 4, kiểm tra starter Flyway và module hỗ trợ database đúng version, gồm `spring-boot-starter-flyway` và `flyway-database-postgresql` khi target PostgreSQL. PostgreSQL JDBC driver là dependency riêng; kiểm tra tài liệu đúng release vì starter/module khác Boot 3. DB schema mới cần constraint, index và quan hệ được migration hóa. Migration có thể lock hoặc thất bại giữa deployment; hiểu thứ tự rollout và backup/restore, nhưng không học zero-downtime migration đầy đủ trong giai đoạn này.

### Câu hỏi kiểm tra

1. Vì sao migration đã chạy không nên sửa trực tiếp?
2. `ddl-auto=validate` kiểm tra gì và không làm gì?
3. Tại sao migration là owner của schema khi đã chọn Flyway?
4. Khi nào cần index/constraint trong migration thay vì chỉ kiểm tra ở Java?
5. Flyway PostgreSQL có thể cần module hỗ trợ database riêng nào ở Boot 4?

### Bài thực hành

Khởi tạo schema customers/products/orders/order_items bằng migration mới; lưu số lượng tồn trong `products.stock`, không tạo bảng inventory riêng. Xóa DB local, tạo lại từ đầu trên PostgreSQL, chạy app với `validate`; kiểm tra schema và migration history.

---

## 10. Transaction và proxy boundary

### Kiến thức cần nắm

`@Transactional` được thực thi qua Spring AOP proxy trong cấu hình thông thường. Gọi method transactional khác qua `this` (self-invocation) thường không đi qua proxy, vì vậy propagation/isolation/transaction setting trên method đó có thể không chạy như kỳ vọng. Đặt transaction boundary ở application service/use case, không rải annotation tùy tiện trên controller.

Mặc định Spring rollback khi có `RuntimeException` hoặc `Error`, không mặc định rollback với checked exception; có thể cấu hình rollback rule rõ ràng. `readOnly=true` là hint/optimization, không phải cơ chế bảo đảm cấm ghi. `REQUIRED` dùng chung transaction hiện có; exception bị bắt nuốt ở boundary có thể khiến transaction rollback-only và outer commit ném `UnexpectedRollbackException`. Không mở transaction DB rồi gọi payment/HTTP từ xa trong đó: remote call không thuộc ACID transaction, kéo dài lock và có thể tạo trạng thái không xác định.

### Câu hỏi kiểm tra

1. Vì sao self-invocation bỏ qua transaction proxy?
2. Mặc định checked exception có rollback không?
3. `readOnly=true` có bảo đảm DB từ chối write không?
4. `UnexpectedRollbackException` có thể xuất hiện khi nào?
5. Vì sao không gọi dịch vụ payment từ xa bên trong DB transaction dài?

### Bài thực hành

Dùng test với PostgreSQL để tạo đơn và trừ tồn kho trong một service transaction. Thử lỗi giữa chừng, xác nhận rollback cả hai ghi; thêm tình huống exception bị bắt và giải thích rollback-only.

---

## 11. Tồn kho đồng thời và locking

### Kiến thức cần nắm

Hai request có thể cùng đọc stock rồi cùng ghi nếu chỉ kiểm tra `quantity > 0` trong Java. Lock của một JVM không bảo vệ nhiều instance ứng dụng, nên không dùng `synchronized` làm consistency boundary cho DB. Xem hai hướng: optimistic locking với `@Version` và xử lý conflict/retry có giới hạn; hoặc conditional atomic update như giảm stock khi `stock >= requested`, kiểm tra số row update. Chọn dựa trên contention và semantics, không dùng cả hai máy móc.

Test trên PostgreSQL thật cho concurrency behavior; H2 không chứng minh lock/isolation giống PostgreSQL. Không giữ Java lock qua I/O hoặc qua nhiều service instance. Xác định behavior khi hết hàng và khi có conflict.

### Câu hỏi kiểm tra

1. Vì sao check rồi update trong Java có race condition?
2. `@Version` phát hiện điều gì?
3. Conditional update giải quyết phần nào của race?
4. Vì sao `synchronized` không thể thay row lock/version trong ứng dụng nhiều instance?
5. Khi conflict, API có thể trả status nào và vì sao?

### Bài thực hành

Dùng nhiều connection và transaction riêng, đặt barrier ngay trước thao tác cạnh tranh, timeout hữu hạn. Gửi đồng thời request mua vượt tồn kho; chứng minh stock không âm và không có đơn hàng vượt hàng khả dụng; so sánh optimistic version với conditional update. Test commit/rollback qua transaction do test quản lý riêng, tránh transaction test tự rollback che mất kết quả commit.

---

## 12. AOP và cross-cutting concern

### Kiến thức cần nắm

AOP/proxy có ích cho transaction, method security và một số cross-cutting concern; không nên dùng để giấu logic nghiệp vụ hoặc tạo call flow khó tìm. Proxy thường áp dụng ở boundary Spring bean; self-invocation và kiểu proxy có thể ảnh hưởng hành vi. Ưu tiên logging/metrics instrumentation có API rõ ràng; không viết aspect tổng quát nếu interceptor/filter hoặc thư viện sẵn đã đáp ứng.

Không log request/response body tùy tiện: có thể chứa password, token và PII. Nếu logging cần thiết, redact allowlist và giới hạn kích thước.

### Câu hỏi kiểm tra

1. Vì sao proxy không tự intercept lời gọi nội bộ cùng object?
2. Khi nào AOP phù hợp hơn lời gọi method tường minh?
3. Dấu hiệu nào cho thấy aspect đang che giấu business flow?
4. Vì sao log toàn bộ request body nguy hiểm?
5. Nên kiểm chứng transaction/security aspect ở test nào?

### Bài thực hành

Kiểm tra proxy của service transactional và tìm self-invocation không hoạt động như dự kiến. Không viết aspect mới; thêm test chứng minh boundary và ghi lại nơi gọi từ bean khác.

---

## 13. Spring Security: xác thực và phân quyền

### Kiến thức cần nắm

Cấu hình `SecurityFilterChain` tường minh: authentication xác định caller là ai, authorization quyết định caller được làm gì. Bắt đầu bằng deny-by-default và chỉ mở endpoint cần thiết. Phân quyền theo role/scope ở endpoint và method boundary; kiểm tra object-level ownership ở service/use case bằng authenticated principal, không nhận `customerId` từ body rồi tin theo. Trả 401/403 theo semantics và tránh lộ việc resource tồn tại khi policy yêu cầu.

Nếu có local account, password encoder chuẩn để lưu hash; không lưu plaintext, không tự thiết kế hashing hoặc JWT format. Không viết JWT filter thủ công khi Spring Security resource server hỗ trợ decoder/validation.

### Câu hỏi kiểm tra

1. Authentication khác authorization thế nào?
2. Vì sao role ở endpoint chưa đủ để bảo vệ order của khách hàng khác?
3. Ai là nguồn sự thật của current user ID?
4. `401` và `403` nên được phát ra ở điều kiện nào?
5. Vì sao nên ưu tiên security filter/config chuẩn thay vì JWT filter tự viết?

### Bài thực hành

Bảo vệ API theo scope/role và kiểm tra ownership ở service. Tạo hai principal: người A không thể đọc, sửa hoặc hủy order của người B dù đoán được ID.

---

## 14. OAuth2, OIDC, JWT và ranh giới token

### Kiến thức cần nắm

OAuth 2.0 là authorization framework; OpenID Connect thêm identity layer. Access token thường phục vụ authorization API, ID token phục vụ client hiểu authentication event; không dùng hai loại thay nhau. Với resource server JWT, kiểm tra chữ ký qua trusted key/JWK, algorithm allowlist, `exp`, `nbf`, issuer và audience theo contract. Cấu hình issuer không tự bảo đảm mọi ứng dụng đã kiểm tra audience; khai báo validator audience cụ thể. Có kế hoạch key rotation và fail closed khi chữ ký/issuer invalid.

Phân biệt authority dạng `ROLE_...` với OAuth scope dạng `SCOPE_...`; cấu hình mapping và rule rõ ràng. Authorization Code + PKCE phù hợp browser/native client; không dùng password grant (RFC 9700 MUST NOT) và tránh implicit grant. Stateless session không tự động có nghĩa tắt CSRF: cookie/basic credential tự gửi bởi browser vẫn có thể cần CSRF protection. CORS là policy browser cho cross-origin, không phải authorization. Xác định nơi lưu token và trust boundary; tránh local storage nếu threat model yêu cầu bảo vệ khỏi XSS, không đưa token vào URL/log.

### Câu hỏi kiểm tra

1. Access token và ID token có cùng mục đích không?
2. Vì sao xác minh issuer/signature nhưng bỏ audience vẫn có thể nhận token không dành cho API?
3. `ROLE_` và `SCOPE_` khác semantics thế nào?
4. Stateless API có luôn an toàn khi tắt CSRF không?
5. CORS có thể thay thế kiểm tra authorization không?

### Bài thực hành

Dùng test decoder/configuration để từ chối token sai chữ ký, hết hạn, sai issuer và sai audience. Test scope allow/deny; tách test decoder thật khỏi test controller dùng mock authentication claims.

---

## 15. Test theo boundary

### Kiến thức cần nắm

Unit test logic domain/service không cần Spring context; dùng fake/mock có chủ đích. MVC slice (`@WebMvcTest`) kiểm tra mapping, validation, status và serialization; Boot 4 thay đổi test module/import, cần theo đúng docs version đã chốt. Khi thay mock bean trong Spring test, dùng API hiện hành như `@MockitoBean` của Spring Framework thay vì bê nguyên annotation cũ từ tutorial.

`@DataJpaTest` kiểm tra mapping/repository, nhưng test trên embedded DB không chứng minh PostgreSQL production behavior; transaction test slice có thể rollback sau test. Dùng PostgreSQL thật disposable/container cho migration, lock, constraint, dialect và concurrent behavior quan trọng. `@SpringBootTest` giữ cho một số integration/HTTP boundary cần thiết; tránh khởi động full app trong mọi unit test. Test security qua request thật ở filter chain, và có cả token invalid thật ở decoder boundary.

Giai đoạn 6 đào sâu testing pyramid, Mockito và Testcontainers; ở đây vẫn phải có baseline test cho behavior cốt lõi, không hoãn kiểm chứng đến sau.

### Câu hỏi kiểm tra

1. Test nào chứng minh validation/binding của MVC?
2. Vì sao unit test repository mock không chứng minh SQL đúng?
3. Vì sao H2 không đủ cho PostgreSQL lock/migration behavior?
4. Khi nào `@SpringBootTest` đáng chi phí?
5. Vì sao test token decoder thật khác test controller với mock claims?

### Bài thực hành

Viết một unit test service, một MVC test request sai, một persistence test PostgreSQL cho migration/constraint và một security integration test cho ownership. Giữ mỗi test ở boundary phù hợp.

---

## 16. Actuator, health và logging tối thiểu

### Kiến thức cần nắm

Expose đúng actuator endpoint cần cho local/health/readiness; không mở toàn bộ management endpoint công khai. Liveness hỏi process có cần restart không; readiness hỏi instance có thể nhận traffic không. Không để dependency chập chờn làm liveness thất bại liên tục nếu điều đó gây restart storm. Cấu hình auth/network boundary riêng cho management endpoint.

Logging có context hữu ích, không ghi password, token, signing key hay payload nhạy cảm. Metrics dùng để trả lời câu hỏi cơ bản như request/error/latency và connection pool; tránh label có cardinality cao như user ID. Full observability, tracing và deployment nằm ở giai đoạn 8.

### Câu hỏi kiểm tra

1. Liveness khác readiness thế nào?
2. Vì sao không expose mọi actuator endpoint ra internet?
3. Vì sao dependency failure không phải lúc nào cũng nên làm liveness fail?
4. Label metric theo user ID gây vấn đề gì?
5. Dữ liệu nào không được ghi vào log?

### Bài thực hành

Bật health endpoint tối thiểu cho local profile, kiểm tra readiness/liveness semantics và xác nhận endpoint management không public ngoài ý muốn. Tạo log lỗi request không chứa credential/token.

---

## Capstone — API đơn hàng an toàn

Xây API trong modular monolith cho products và orders; tồn kho nằm trong `products.stock`. Dùng DTO riêng, Flyway migrations, PostgreSQL, transaction service boundary, `SecurityFilterChain` và resource server JWT configuration. Request order phải có ít nhất một dòng, quantity dương và có giới hạn; kiểm tra quantity tại service boundary. Quy định duplicate product ID trong một order (gộp quantity hoặc từ chối) và test quy tắc đó. Giá/currency lấy từ server-side product price, dùng `BigDecimal`, lưu snapshot tại order item; không tin giá do client gửi. Owner chỉ lấy từ principal đã xác thực. Response không serialize JPA entity. Error contract ổn định; list có pagination giới hạn và SQL không phát sinh N+1 không kiểm soát.

### Tiêu chí hoàn thành

- DB mới dựng được từ migration; Hibernate chỉ validate schema.
- Invalid request trả validation problem; resource không có trả 404; conflict stock trả kết quả đã định nghĩa.
- Hai người dùng không thể truy cập order của nhau bằng cách sửa ID.
- Concurrent purchase không làm stock âm hoặc tạo order vượt stock.
- Lỗi giữa ghi order và stock update không để lại partial write.
- Token sai signature/issuer/audience/expiry bị từ chối; scope/role mapping có test.
- Có unit, MVC, persistence/PostgreSQL và security integration test tối thiểu; có lệnh chạy lại được và ghi chú lựa chọn/trade-off.
- Test/build và cách khởi chạy DB phải được ghi trong README của project Spring khi project được khởi tạo. File này mô tả learning plan, không giả định module hoặc script hiện có.

## Exit Criteria

- [ ] Có thể giải thích bean wiring, singleton/thread-safety, auto-configuration và cách tách cấu hình khỏi secret.
- [ ] Request đi qua đúng MVC boundary; API dùng request/response DTO, validation và RFC 9457 error contract.
- [ ] Status code, `Location`, pagination limit, sort allowlist và contract lỗi có MVC test.
- [ ] Flyway dựng được schema PostgreSQL từ DB rỗng; Hibernate chỉ validate; `products.stock` giữ tồn kho.
- [ ] Transaction boundary tạo order và cập nhật stock nguyên tử; hiểu rollback rule, proxy/self-invocation và lỗi rollback-only.
- [ ] SQL/query count và pagination được kiểm chứng; không có N+1 không kiểm soát trên endpoint capstone.
- [ ] Security deny-by-default; owner lấy từ principal; ownership, scope/role và JWT signature/issuer/audience/expiry có test.
- [ ] PostgreSQL test kiểm chứng migration/constraint và concurrency bằng connection/transaction riêng; health endpoint tối thiểu không lộ dữ liệu nhạy cảm.

## Ngoài phạm vi giai đoạn này

Không yêu cầu hoàn tất Testcontainers/testing strategy nâng cao (giai đoạn 6), Redis/Kafka/Saga/outbox/distributed transaction (giai đoạn 7), hoặc CI/CD, Docker orchestration, metrics/tracing production và quy trình release đầy đủ (giai đoạn 8). Không hoãn hoặc bỏ baseline validation, security, migration và test cần để chứng minh API an toàn.

## Tài liệu chuẩn

- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/reference/)
- [Spring Framework — Declarative Transaction Management](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/reference/)
- [Spring Security — OAuth 2.0 Resource Server JWT](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)
- [RFC 9457 — Problem Details for HTTP APIs](https://www.rfc-editor.org/rfc/rfc9457)
- [RFC 9700 — OAuth 2.0 Security Best Current Practice](https://www.rfc-editor.org/rfc/rfc9700)
