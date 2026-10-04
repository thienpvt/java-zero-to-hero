# Giai đoạn 4 — Database & Persistence

## Mục tiêu

Xây dựng hiểu biết đủ sâu để lưu và truy vấn dữ liệu đúng, nhất quán, an toàn và có thể đo được. Có thể theo dõi một thao tác từ Java qua JDBC/JPA xuống SQL, đánh giá execution plan, nhận ra lỗi concurrency và giới hạn của transaction.

Giai đoạn này tập trung vào PostgreSQL, SQL, JDBC và JPA/Hibernate thuần. Spring Boot, REST, authentication và distributed messaging thuộc các giai đoạn sau.

**Đầu ra:** triển khai persistence cho customers, products, orders và order_items; chứng minh bằng test và bằng chứng SQL rằng dữ liệu hợp lệ, tồn kho không âm khi có request đồng thời, transaction rollback đúng và truy vấn chính có kế hoạch hợp lý.

## Điều kiện tiên quyết và môi trường

- Dùng JDK 21 theo baseline của project hiện tại; ghi version cụ thể trong project.
- Nắm Java collections, equals/hashCode, exceptions, try-with-resources, concurrency cơ bản, Maven hoặc Gradle và Git.
- Cài PostgreSQL 18 cho lab; ghi lại đúng minor version đã cài. Không giả định mọi hành vi tương đương giữa PostgreSQL và H2.
- Dùng database riêng có thể xóa/tạo lại. Không chạy thử nghiệm phá hủy trên dữ liệu cần giữ; credentials lấy từ environment, không commit secrets.
- Khi bắt đầu triển khai JDBC/JPA, pin version driver, Jakarta Persistence API và Hibernate ORM rõ ràng; xác nhận compatibility giữa chúng. Không dùng nhãn `latest` làm version dự án.
- Ghi lại SQL, thời gian, số hàng và kế hoạch thực thi. Không kết luận hiệu năng chỉ từ cảm giác hoặc từ một lần chạy.

### Domain dùng xuyên suốt

- `customers`: khách hàng; đơn hàng phải thuộc một khách hàng.
- `products`: sản phẩm có giá và tồn kho.
- `orders`: đơn hàng có trạng thái và thời điểm tạo.
- `order_items`: sản phẩm, số lượng và đơn giá đã chốt tại thời điểm đặt hàng.
- Server là nguồn tin cậy cho giá. Không nhận tổng tiền hoặc giá từ client làm giá trị chuẩn.
- Dùng `NUMERIC` trong DB và `BigDecimal` trong Java cho tiền; quy định rõ scale, rounding và currency.
- Truy vấn khách hàng phải áp dụng customer scope phù hợp; phân trang phải có thứ tự ổn định.

---

## 1. Mô hình quan hệ và thiết kế schema

### Kiến thức cần nắm

- Chuyển yêu cầu nghiệp vụ thành bảng, primary key, foreign key và quan hệ một-nhiều/nhiều-nhiều.
- Phân biệt natural key và surrogate key; chọn kiểu ID và chiến lược sinh ID có lý do.
- Chuẩn hóa để tránh dữ liệu trùng và anomaly khi insert/update/delete; nhận biết khi nào denormalization có chủ đích.
- Xác định nullability, default, precision/scale, timezone và quy tắc xóa dữ liệu.
- Thiết kế `order_items` lưu giá đã chốt để lịch sử đơn hàng không đổi khi giá sản phẩm thay đổi.

### Câu hỏi kiểm tra

1. Primary key khác unique constraint ở điểm nào?
2. Khi nào một cột nên cho phép `NULL`, và `NULL` khác giá trị rỗng thế nào?
3. Vì sao đơn hàng cần giữ đơn giá tại thời điểm mua?
4. Foreign key bảo vệ được lỗi nào mà validation ở Java không đủ bảo vệ?
5. Khi nào denormalization đáng cân nhắc và chi phí nào phát sinh?

### Bài thực hành

Thiết kế schema cho bốn bảng domain; viết migration SQL, chèn dữ liệu mẫu và chứng minh FK, unique, nullability hoạt động bằng các thao tác hợp lệ lẫn không hợp lệ.

---

## 2. Kiểu dữ liệu và constraint ở database

### Kiến thức cần nắm

- Chọn `INTEGER`/`BIGINT`, `NUMERIC`, `DATE`, `TIMESTAMP WITH TIME ZONE`, `TEXT` và boolean theo miền giá trị.
- Dùng `NOT NULL`, `CHECK`, `UNIQUE`, primary key và foreign key để giữ invariant tại nơi dữ liệu được ghi.
- Dùng `NUMERIC(precision, scale)` cho tiền; ánh xạ tương ứng sang `BigDecimal` và làm rõ rounding.
- `TIMESTAMP WITH TIME ZONE` lưu instant, không giữ tên timezone gốc; chuyển sang timezone hiển thị ở tầng ứng dụng.
- `CHECK` cho kết quả `NULL` không loại dòng đó, nên constraint cần kết hợp `NOT NULL` nếu giá trị bắt buộc; default không thay thế kiểm tra hợp lệ.

### Câu hỏi kiểm tra

1. Vì sao `double` không phù hợp để biểu diễn tiền?
2. Constraint nào đảm bảo quantity của order item lớn hơn 0?
3. Vì sao invariant quan trọng nên được bảo vệ cả ở DB?
4. Khi nào dùng `TIMESTAMP WITH TIME ZONE` thay cho `DATE`?
5. Default có tự áp dụng khi ứng dụng ghi explicit `NULL` không?

### Bài thực hành

Bổ sung constraints cho giá, số lượng, trạng thái đơn hàng và tồn kho. Thử ghi dữ liệu vi phạm trực tiếp bằng SQL, không qua Java, rồi ghi lại lỗi DB trả về.

---

## 3. SQL truy vấn nhiều bảng

### Kiến thức cần nắm

- Viết `INNER JOIN`, `LEFT JOIN`, điều kiện nối theo khóa và alias rõ ràng.
- Hiểu `NULL`, `IS NULL`, `COALESCE` và cách `NULL` ảnh hưởng logic ba giá trị.
- Dùng aggregate, `GROUP BY`, `HAVING` đúng thứ tự logic; tránh đếm sai khi join quan hệ một-nhiều.
- Dùng subquery, CTE và window functions khi giúp truy vấn dễ đọc hoặc giải quyết bài toán theo nhóm/thứ tự.
- Chỉ chọn cột cần thiết; tránh `SELECT *` trong truy vấn ứng dụng lâu dài.

### Câu hỏi kiểm tra

1. `LEFT JOIN` tạo kết quả gì khi không có dòng khớp?
2. Vì sao `column = NULL` không dùng để kiểm tra null?
3. `WHERE` khác `HAVING` ở đâu?
4. Join orders với order_items có thể làm tổng đơn hàng bị nhân đôi thế nào?
5. Khi nào window function phù hợp hơn aggregate làm mất chi tiết từng dòng?

### Bài thực hành

Viết truy vấn lịch sử đơn hàng theo khách hàng gồm ngày, tổng tiền, số item và trạng thái; thêm biến thể trả về cả khách chưa đặt đơn, kiểm thử khách không có dữ liệu.

---

## 4. Index và chi phí truy vấn

### Kiến thức cần nắm

- Index giúp tìm kiếm/sắp xếp một số truy vấn nhưng tiêu tốn dung lượng và tăng chi phí ghi.
- Chọn index theo workload thực tế: predicate, join key, thứ tự sort và độ chọn lọc.
- Hiểu thứ tự cột trong composite index; index `(customer_id, created_at)` không tương đương mọi hoán vị.
- Nhận biết sequential scan không đồng nghĩa truy vấn lỗi; planner cân nhắc kích thước bảng và chi phí truy cập.
- Không tạo index trùng lặp hoặc index mọi cột theo thói quen.

### Câu hỏi kiểm tra

1. Độ chọn lọc của cột ảnh hưởng quyết định dùng index thế nào?
2. Composite index `(a, b)` thường hữu ích cho điều kiện nào?
3. Vì sao index làm chậm một số thao tác ghi?
4. Tại sao sequential scan đôi khi nhanh hơn index scan?
5. Bằng chứng nào đủ để đề xuất index mới?

### Bài thực hành

Tạo dữ liệu đủ lớn cho truy vấn lịch sử đơn hàng theo customer và thời gian. So sánh plan trước/sau composite index, ghi thời gian, số hàng ước lượng/thực tế và tác động insert.

---

## 5. Đọc execution plan và tối ưu có đo lường

### Kiến thức cần nắm

- Đọc `EXPLAIN` để xem planner dự kiến node, join, scan và chi phí.
- Dùng `EXPLAIN (ANALYZE, BUFFERS)` trên dataset lab để so estimate với actual rows, thời gian và buffer reads/hits.
- Tìm sequential/index scan, nested loop/hash join, sort, filter và chênh lệch cardinality.
- Đảm bảo statistics phù hợp; cân nhắc `ANALYZE` khi dữ liệu thử nghiệm thay đổi mạnh.
- `EXPLAIN ANALYZE` thực thi câu lệnh. Với `UPDATE`/`DELETE`, chỉ thử trên database dùng một lần; rollback không hoàn tác sequence increment hoặc tác động ngoài database.

### Câu hỏi kiểm tra

1. `EXPLAIN` khác `EXPLAIN ANALYZE` ở điểm nào?
2. Vì sao actual rows lệch xa estimated rows có thể làm plan kém?
3. `BUFFERS` cung cấp loại bằng chứng nào?
4. Vì sao không được chạy `EXPLAIN ANALYZE` câu lệnh sửa dữ liệu tùy tiện?
5. Một plan có cost thấp hơn có luôn đồng nghĩa latency production thấp hơn không?

### Bài thực hành

Lưu plan trước/sau thay đổi cho một truy vấn domain. Giải thích node tốn kém nhất, lý do chọn index hoặc viết lại SQL, và điều kiện khiến kết luận có thể thay đổi.

---

## 6. Phân trang, thứ tự ổn định và keyset

### Kiến thức cần nắm

- Phân trang phải dùng thứ tự xác định, thường bổ sung unique key làm tie-breaker.
- `OFFSET/LIMIT` đơn giản nhưng chi phí offset lớn có thể tăng và dữ liệu chèn/xóa giữa các trang gây lệch kết quả.
- Keyset pagination dùng giá trị cuối trang trước làm cursor; cần sort key phù hợp và predicate nhất quán.
- Trả về kích thước trang hợp lý; không để client yêu cầu số lượng không giới hạn.
- Pagination trên collection fetch join trong ORM có thể phát sinh kết quả sai/không hiệu quả; dùng projection hoặc truy vấn hai bước khi phù hợp.

### Câu hỏi kiểm tra

1. Vì sao `ORDER BY created_at` chưa chắc tạo thứ tự ổn định?
2. Keyset pagination cần giữ thông tin gì từ trang trước?
3. Offset lớn có thể tốn kém vì sao?
4. Dữ liệu thay đổi giữa hai request ảnh hưởng trang offset thế nào?
5. Rủi ro của phân trang kết hợp fetch collection là gì?

### Bài thực hành

Cài hai truy vấn phân trang lịch sử đơn hàng: offset và keyset theo `(created_at, id)`. Kiểm tra trang rỗng, tie-break, dữ liệu mới xen giữa các request và giới hạn kích thước trang.

---

## 7. ACID, MVCC và transaction boundary

### Kiến thức cần nắm

- Atomicity, consistency, isolation, durability mô tả các thuộc tính transaction với điều kiện cấu hình và storage phù hợp.
- Transaction phải bao trọn thao tác nghiệp vụ cần thành công/thất bại cùng nhau, không kéo dài qua I/O không cần thiết.
- PostgreSQL dùng MVCC; snapshot và lock ảnh hưởng khả năng đọc/ghi đồng thời.
- Phân biệt transaction DB với lời gọi HTTP, thanh toán, email hoặc message broker; local transaction không làm chúng atomic cùng nhau.
- Với thao tác nhiều hệ thống, cần thiết kế idempotency/compensation/outbox ở giai đoạn thích hợp, không giả định rollback DB hoàn tác side effect ngoài.

### Câu hỏi kiểm tra

1. Transaction boundary của tạo order và giảm stock nên bao gồm thao tác nào?
2. MVCC giúp readers và writers tương tác ra sao?
3. Local DB transaction không đảm bảo điều gì với payment API?
4. Vì sao transaction dài có thể gây vấn đề cho hệ thống?
5. Khi DB commit thành công nhưng email gửi lỗi, hệ thống cần xem đây là loại failure nào?

### Bài thực hành

Thực hiện tạo order gồm insert order, insert items và cập nhật stock trong một transaction. Cố ý lỗi giữa chừng, kiểm tra mọi thay đổi DB đều rollback; không gắn lời gọi mạng vào transaction này.

---

## 8. Isolation level và tái hiện race condition

### Kiến thức cần nắm

- Phân biệt dirty read, non-repeatable read, phantom và serialization anomaly.
- Trong PostgreSQL, `READ UNCOMMITTED` hoạt động như `READ COMMITTED`.
- `REPEATABLE READ` của PostgreSQL ngăn phantom reads nhưng vẫn có thể xảy ra serialization anomaly; không suy rộng nhãn isolation thành bảo đảm giống nhau giữa mọi DB.
- `SERIALIZABLE` có thể hủy một transaction khi phát hiện xung đột; ứng dụng phải xử lý/retry transaction phù hợp.
- Tái hiện concurrency bằng hai worker, mỗi worker dùng connection riêng (và EntityManager riêng nếu dùng JPA); dùng gate/barrier có timeout. Không đặt barrier sau khi một worker giữ lock nếu worker còn lại cần lấy chính lock đó mới tới barrier.

### Câu hỏi kiểm tra

1. Dirty read khác non-repeatable read thế nào?
2. `READ COMMITTED` cho snapshot ở phạm vi nào trong PostgreSQL?
3. Vì sao PostgreSQL `REPEATABLE READ` vẫn có thể có serialization anomaly?
4. Vì sao test race dựa trên `sleep` không đáng tin?
5. Test cần assert invariant nào khi hai request mua cùng sản phẩm?

### Bài thực hành

Dùng hai worker và connection riêng, điều phối để cả hai đọc cùng snapshot trước khi ghi; barrier phải có timeout và không chờ worker đang bị lock. Ghi lại anomaly ở isolation phù hợp, sau đó xác minh invariant sau khi sửa bằng transaction/locking.

---

## 9. JDBC, tài nguyên và connection pool

### Kiến thức cần nắm

- Dùng `DataSource`, `Connection`, `PreparedStatement` và `ResultSet`; đóng tài nguyên bằng try-with-resources.
- Bind giá trị bằng placeholder; parameterized statement ngăn dữ liệu đầu vào trở thành SQL syntax.
- SQL identifier như tên bảng/cột thường không bind được như value; dynamic identifier phải đến từ allowlist cố định.
- Phân biệt connection vật lý với connection mượn từ pool; trả connection về pool qua close, không giữ quá lâu.
- Xử lý SQLException và transaction failure mà không nuốt lỗi hoặc báo thành công khi commit thất bại.

### Câu hỏi kiểm tra

1. `PreparedStatement` bảo vệ dữ liệu đầu vào theo cách nào?
2. Vì sao không thể bind tên cột bằng parameter placeholder thông thường?
3. `Connection.close()` có ý nghĩa gì khi connection do pool cấp?
4. Điều gì xảy ra nếu không đóng `ResultSet`/statement/connection?
5. Vì sao bắt SQLException rồi tiếp tục trả response thành công là nguy hiểm?

### Bài thực hành

Viết DAO JDBC cho truy vấn order theo ID và cập nhật stock. Dùng bind parameters, đóng tài nguyên tự động, kiểm thử đường lỗi SQL và xác nhận connection được trả về pool.

---

## 10. JDBC transaction, batch và lỗi ghi

### Kiến thức cần nắm

- JDBC mặc định thường bật auto-commit; tắt auto-commit để nhóm thao tác liên quan, rồi commit hoặc rollback tường minh.
- Nếu nhiều thao tác cần nguyên tử, lỗi ở bất kỳ bước nào phải không để lại dữ liệu nửa chừng.
- Batch insert/update có thể giảm round-trip; kiểm tra batch result và xử lý lỗi đúng.
- Sau lỗi trong transaction, không tiếp tục giả định connection ở trạng thái sử dụng bình thường; rollback hoặc kết thúc transaction theo đúng API/driver behavior.
- Phân biệt constraint violation, deadlock, serialization failure và lỗi kết nối để quyết định hành động tiếp theo.

### Câu hỏi kiểm tra

1. Auto-commit làm mỗi câu lệnh có ranh giới transaction thế nào?
2. Khi nào cần rollback trong `catch` và lỗi rollback cần được xử lý ra sao?
3. Batch cải thiện chi phí nào, và không đảm bảo điều gì?
4. Vì sao batch result cần được kiểm tra?
5. Có nên retry mọi SQLException không? Vì sao?

### Bài thực hành

Tạo nhiều order items bằng JDBC batch trong transaction; chèn một item vi phạm constraint và xác minh cả batch nghiệp vụ rollback. Ghi nhận SQLSTATE và trạng thái connection.

---

## 11. JPA/Hibernate: persistence context và entity lifecycle

### Kiến thức cần nắm

- Jakarta Persistence là specification; Hibernate là provider triển khai nó. Phân biệt transient, managed, detached và removed entity.
- Entity cơ bản cần constructor không tham số phù hợp và mapping access nhất quán qua field hoặc property theo quy tắc JPA.
- Persistence context theo dõi entity managed; dirty checking có thể tạo SQL khi flush.
- `flush` đồng bộ thay đổi pending xuống DB nhưng không đồng nghĩa transaction đã commit.
- `merge` copy state vào managed instance và trả về instance managed; đối tượng truyền vào không nhất thiết trở thành managed.
- Generated ID ảnh hưởng equals/hashCode; không dùng ID chưa sinh theo cách làm hỏng contract hoặc hash-based collection.

### Câu hỏi kiểm tra

1. `flush` khác commit thế nào?
2. Dirty checking xảy ra khi nào?
3. Vì sao giá trị trả về từ `merge` cần được dùng đúng?
4. Entity detached thay đổi có tự persist không?
5. Generated ID làm equals/hashCode khó ở điểm nào?

### Bài thực hành

Tạo order bằng JPA, quan sát thời điểm insert trước/sau flush và commit. Tách entity khỏi persistence context, cập nhật state, rồi kiểm tra hành vi khi merge và dùng instance được trả về.

---

## 12. Mapping quan hệ, cascade và ownership

### Kiến thức cần nắm

- Chọn association cần thiết; hiểu phía owning side quyết định ghi khóa ngoại trong mapping hai chiều.
- Giữ hai phía của quan hệ hai chiều đồng bộ qua helper method nếu thực sự cần cả hai chiều.
- Chỉ dùng cascade theo lifecycle nghiệp vụ; cascade remove và `orphanRemoval` có thể xóa dữ liệu.
- Không mặc định mọi quan hệ đều cần cascade `ALL` hoặc mapping hai chiều.
- Mapping phải phù hợp với FK và hành vi delete mong muốn trong schema.

### Câu hỏi kiểm tra

1. Owning side quyết định điều gì khi lưu quan hệ?
2. Vì sao chỉ đổi inverse side có thể không ghi FK như mong đợi?
3. `CascadeType.REMOVE` và `orphanRemoval` có thể gây mất dữ liệu nào?
4. Khi nào child entity có lifecycle phụ thuộc parent?
5. Vì sao mapping hai chiều làm tăng độ phức tạp?

### Bài thực hành

Map `Order` và `OrderItem`; viết thêm/xóa item qua API domain nhất quán. Xác minh FK sau flush, rồi thử xóa parent trong database disposable và ghi lại chính xác cascade behavior.

---

## 13. Fetching, N+1 và projection

### Kiến thức cần nắm

- `LAZY`/`EAGER` ảnh hưởng lúc tải association nhưng không thay thế thiết kế query theo use case.
- N+1 phát sinh khi một truy vấn danh sách kéo theo truy vấn lặp cho từng entity/association.
- Dùng SQL logging, query counter hoặc datasource instrumentation để đếm câu lệnh, không đoán từ object graph.
- Entity graph, fetch join có giới hạn; join nhiều collection có thể nhân số dòng và pagination collection fetch là tình huống cần đặc biệt thận trọng.
- Projection/DTO query phù hợp cho read model khi không cần toàn bộ entity lifecycle.

### Câu hỏi kiểm tra

1. N+1 thường hình thành qua chuỗi truy vấn nào?
2. Vì sao đổi mọi quan hệ sang EAGER không phải cách sửa tổng quát?
3. Fetch join nhiều collection có thể gây vấn đề gì?
4. Khi nào projection hiệu quả hơn tải entity đầy đủ?
5. Bằng chứng nào chứng minh N+1 đã được loại bỏ?

### Bài thực hành

Truy vấn trang lịch sử order gồm customer, items và product name. Đếm SQL cho trang nhiều bản ghi, loại bỏ N+1 bằng query phù hợp, rồi kiểm tra số dòng và pagination vẫn đúng.

---

## 14. Bảo vệ invariant tồn kho bằng locking

### Kiến thức cần nắm

- Kiểm tra stock rồi giảm bằng hai câu lệnh riêng mà không lock có thể cho phép oversell.
- Conditional atomic update dạng `UPDATE ... SET stock = stock - ? WHERE id = ? AND stock >= ?`; kiểm tra số dòng cập nhật để biết có đủ stock.
- Optimistic locking dùng version column để phát hiện cập nhật cạnh tranh; caller phải xử lý conflict.
- Pessimistic row lock như `SELECT ... FOR UPDATE` giữ row lock đến khi transaction kết thúc; cần giới hạn thời gian và phạm vi lock.
- Java `synchronized`/lock trong một process không bảo vệ database trước JVM khác hoặc ứng dụng khác.

### Câu hỏi kiểm tra

1. Vì sao read-then-write không điều kiện có race condition?
2. Số dòng update có ý nghĩa gì với conditional stock update?
3. Optimistic và pessimistic locking đánh đổi điều gì?
4. Vì sao Java lock không đủ cho nhiều instance ứng dụng?
5. Làm sao đảm bảo invariant `stock >= 0` cả khi có nhiều writer?

### Bài thực hành

Tạo stock nhỏ và chạy hai transaction được điều phối để đồng thời mua hết số lượng. Cài conditional update hoặc locking, assert tổng bán không vượt stock và stock cuối không âm.

---

## 15. Deadlock, retry và giới hạn transaction

### Kiến thức cần nắm

- Deadlock có thể xảy ra khi transaction giữ lock theo thứ tự khác nhau; DB thường hủy một transaction để phục hồi.
- Giữ lock ngắn, cập nhật nhiều row theo thứ tự nhất quán và tránh chờ I/O bên ngoài trong transaction.
- PostgreSQL SQLSTATE `40001` là serialization failure; deadlock có SQLSTATE riêng. Phân loại lỗi qua SQLSTATE/driver exception thay vì dò chuỗi message.
- Retry phải có giới hạn, chỉ áp dụng cho lỗi retryable, và chạy lại toàn bộ transaction với state mới.
- Không retry một phần transaction đã có side effect bên ngoài; đảm bảo thao tác idempotent hoặc dùng boundary/outbox phù hợp.

### Câu hỏi kiểm tra

1. Deadlock khác lock wait timeout như thế nào?
2. Vì sao chỉ retry câu lệnh bị lỗi có thể sai?
3. SQLSTATE `40001` báo loại lỗi nào trong PostgreSQL?
4. Retry không giới hạn có thể gây vấn đề gì?
5. Vì sao gọi payment API trong transaction khiến retry nguy hiểm?

### Bài thực hành

Dùng hai connection cập nhật hai sản phẩm theo thứ tự đối nghịch để tái hiện deadlock có timeout. Sau đó thống nhất thứ tự lock và triển khai retry giới hạn cho lỗi phù hợp, log số lần thử và kết quả.

---

## 16. Migration, tương thích schema và kiểm thử DB thật

### Kiến thức cần nắm

- Migration có version được áp dụng một lần và ghi vào migration history; chạy lại migration tool không thực thi lại SQL đã áp dụng.
- Phân biệt migration schema với dữ liệu seed dành cho test/dev.
- Thay đổi schema phải cân nhắc ứng dụng phiên bản cũ/mới cùng tồn tại trong lúc deploy; ưu tiên thay đổi tương thích theo bước khi cần.
- Test persistence quan trọng trên PostgreSQL thật hoặc môi trường tương thích đáng tin cậy; H2 không tái tạo đầy đủ SQL, isolation, locking và planner PostgreSQL.
- Test phải cô lập dữ liệu, cleanup an toàn và không phụ thuộc thứ tự chạy.

### Câu hỏi kiểm tra

1. Migration đem lại lợi ích nào so với sửa schema thủ công?
2. Vì sao migration rename/drop cột có thể phá deploy rolling?
3. H2 khác PostgreSQL ở những hành vi nào cần quan tâm?
4. Test concurrency cần chạy trên loại database nào để có giá trị?
5. Làm sao tránh test phụ thuộc dữ liệu từ lần chạy trước?

### Bài thực hành

Viết migration tạo domain schema và migration kế tiếp thêm một constraint có xử lý dữ liệu cũ. Chạy từ database rỗng, chạy lại trên bản có dữ liệu, rồi kiểm thử persistence và locking với PostgreSQL lab.

---

## 17. Capstone — Order và inventory persistence

### Kiến thức cần nắm

- Tạo order phải đọc giá đáng tin cậy từ DB/server, ghi giá snapshot vào order item và cập nhật stock nguyên tử.
- Validate quantity là số nguyên dương có giới hạn trên; order không rỗng. Reject product ID trùng hoặc aggregate nhất quán trước conditional stock update; quantity âm không bao giờ được phép làm stock tăng.
- Insert order, items và stock update thuộc cùng transaction local; mọi lỗi DB phải rollback toàn bộ thay đổi đó.
- Customer scope phải áp dụng cho read/update order; không tin customer ID hoặc total do client gửi mà chưa authorization/validation.
- Kiểm thử concurrency bằng nhiều connection, không chỉ nhiều thread dùng chung persistence context.
- Thu thập SQL/query-count, execution plan và bằng chứng invariant thay vì chỉ demo happy path.

### Câu hỏi kiểm tra

1. Giá order item lấy từ đâu và vì sao?
2. Thành phần nào phải rollback cùng nhau khi một item thiếu stock?
3. Làm sao chứng minh hai request đồng thời không oversell?
4. Làm sao chứng minh query list không bị N+1 và pagination đúng?
5. Bằng chứng nào cần lưu để giải thích lựa chọn index và transaction?

### Bài thực hành

Hoàn thành flow đặt hàng cho domain xuyên suốt. Viết test cho success, reject order rỗng, quantity không dương/vượt giới hạn và duplicate product IDs theo policy đã chọn; kiểm tra stock conservation (tổng lượng bán không vượt stock ban đầu, không có quantity âm). Test rollback toàn order khi lỗi giữa chừng hoặc một trong nhiều items thiếu stock. Kiểm tra hai đơn cạnh tranh stock, customer scope và pagination ổn định. Ghi SQL thực tế, số query và plan của truy vấn lịch sử đơn hàng. Mỗi concurrency worker dùng connection/EntityManager riêng; barrier có timeout, không chờ sau lock khiến peer không thể tới barrier. Không dùng `sleep` để áp đặt thứ tự.

---

## Bằng chứng cần lưu

- Migration/schema SQL và version PostgreSQL/JDK thực tế.
- Test kết quả rollback, constraint và customer scope.
- Log SQL/query count cho use case history; bằng chứng N+1 đã được xử lý.
- `EXPLAIN (ANALYZE, BUFFERS)` trước/sau tối ưu trên database disposable; kèm dataset/điều kiện đo.
- Kết quả concurrency test chứng minh `stock >= 0`, không oversell và xử lý serialization/deadlock có giới hạn.
- Ghi chú transaction boundary, locking strategy và trade-off index.

Đây là bằng chứng cần tạo trong lúc học, không phải test đã chạy sẵn trong repository. Không ghi lệnh test/module cụ thể nếu project chưa có cấu hình tương ứng.

## Exit Criteria

Hoàn thành giai đoạn khi có thể:

- Thiết kế schema quan hệ và dùng constraint để bảo vệ invariant.
- Viết SQL nhiều bảng, aggregate và pagination có thứ tự xác định.
- Giải thích index theo query workload, đọc execution plan và đo thay đổi.
- Mô tả ACID/MVCC và isolation PostgreSQL, kể cả serialization anomaly.
- Dùng JDBC parameter binding, try-with-resources, transaction và batch an toàn.
- Giải thích JPA lifecycle, flush/commit/merge, owning side, cascade và rủi ro generated ID trong equality.
- Phát hiện N+1 bằng bằng chứng SQL, chọn fetch/projection đúng use case.
- Bảo vệ stock bằng thao tác nguyên tử/locking ở database; không nhầm Java lock với distributed coordination.
- Retry có giới hạn đúng phạm vi; nhận biết local DB transaction không bao phủ HTTP/payment/broker.
- Chạy migration và integration/concurrency test với PostgreSQL thật, không mặc định H2 tương đương.

## Tài liệu chuẩn

- [PostgreSQL 18 Documentation](https://www.postgresql.org/docs/18/index.html)
- [PostgreSQL: Transaction Isolation](https://www.postgresql.org/docs/18/transaction-iso.html)
- [PostgreSQL: EXPLAIN](https://www.postgresql.org/docs/18/using-explain.html)
- [Java 21: `java.sql` API](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/package-summary.html)
- [Jakarta Persistence 3.2 Specification](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2)
- [Hibernate ORM Documentation](https://hibernate.org/orm/documentation/)
