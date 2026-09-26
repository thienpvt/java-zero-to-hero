# Giai đoạn 1 — Java Core nâng cao

## Mục tiêu

Ứng viên không chỉ biết gọi Java API mà phải giải thích được:
- API hoạt động như thế nào.
- Khi nào nên sử dụng.
- Trade-off của từng lựa chọn.
- Những bug có thể xuất hiện khi sử dụng sai.

**Chuẩn bị:** JDK 21 hoặc 25 LTS, một project Maven/Gradle và JUnit. Viết và chạy code cho mỗi nhóm kiến thức; câu hỏi bên dưới dùng để kiểm tra sau khi thử nghiệm, không thay thế bài thực hành. Các ví dụ cú pháp ngắn trong tài liệu chỉ minh họa ý tưởng, không phải file Java độc lập.

---

## 1. Generics

> Thực hành: [d01_generics](phase-01-core-advanced/src/main/java/phase01/d01_generics/)

### Kiến thức cần nắm

#### Cơ bản
- Generic class
- Generic method
- Generic interface
- Type parameter

```java
class Box<T> {
    private T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
```

#### Bounds

```java
<T extends Number>
```

#### Wildcard

```java
List<?>
List<? extends Number>
List<? super Integer>
```

#### PECS

Producer Extends, Consumer Super.

#### Type Erasure

Hiểu rằng generic chủ yếu được compiler xử lý và type parameter không tồn tại đầy đủ tại runtime. Phân biệt giới hạn do erasure, raw type và unchecked warning; đừng tắt warning để che lỗi kiểu dữ liệu.

#### Cần phân biệt

```java
List<Object>
List<?>
List<? extends Object>
```

---

### Câu hỏi kiểm tra

1. `List<String>` có phải subtype của `List<Object>` không? Tại sao?
2. Khi nào dùng `? extends T`?
3. Khi nào dùng `? super T`?
4. Giải thích PECS bằng ví dụ thực tế.
5. Tại sao không thể viết:

```java
T value = new T();
```

6. Type Erasure là gì?
7. Tại sao Java không cho phép:

```java
if (obj instanceof List<String>)
```

8. So sánh:

```java
<T> void process(List<T> list)
```

và

```java
void process(List<?> list)
```

9. Generic invariance là gì?
10. Tại sao generic giúp type safety nhưng không hoàn toàn tồn tại ở runtime?
11. Vì sao `List<String>` và `List<Integer>` không phân biệt được bằng `instanceof` tại runtime?

---

## 2. ArrayList

> Thực hành: [d02_arraylist](phase-01-core-advanced/src/main/java/phase01/d02_arraylist/)

### Kiến thức cần nắm

Hiểu:
- Backing array.
- Capacity vs size.
- Array resize.
- Random access.
- Insert/delete giữa array.
- Time complexity.

Thông thường:

| Operation | Complexity |
|---|---|
| get | O(1) |
| set | O(1) |
| append | amortized O(1) |
| insert middle | O(n) |
| contains | O(n) |
| remove by index | O(n) |

Hiểu khái niệm **amortized complexity**.

---

### Câu hỏi kiểm tra

1. Tại sao `ArrayList.get()` là O(1)?
2. Tại sao `add()` thường được gọi là amortized O(1)?
3. Điều gì xảy ra khi backing array hết capacity?
4. `size` và `capacity` khác nhau thế nào?
5. Tại sao insert ở đầu `ArrayList` là O(n)?
6. Khi nào nên truyền `initialCapacity`?
7. `ArrayList` có thread-safe không?
8. `ArrayList` có thể chứa `null` không?
9. Khi iterate rồi remove sai cách có thể xảy ra chuyện gì?
10. Khi nào ArrayList tốt hơn LinkedList dù phải insert/remove?

---

## 3. LinkedList

> Thực hành: [d03_linkedlist](phase-01-core-advanced/src/main/java/phase01/d03_linkedlist/)

### Kiến thức cần nắm

Hiểu:
- Doubly linked list.
- Node.
- Previous / next pointer.
- Traversal.
- Random access không hiệu quả.

Không được chỉ kết luận:

> insert LinkedList luôn O(1).

Phải hiểu tìm được node cần insert có thể mất O(n).

---

### Câu hỏi kiểm tra

1. `LinkedList.get(5000)` hoạt động thế nào?
2. Insert giữa LinkedList có thực sự O(1) không?
3. LinkedList sử dụng nhiều memory hơn ArrayList vì sao?
4. Khi nào LinkedList thực sự có lợi?
5. Vì sao trên workload thực tế ArrayList thường nhanh hơn LinkedList?
6. CPU cache locality ảnh hưởng thế nào?

---

## 4. HashMap

> Thực hành: [d04_hashmap](phase-01-core-advanced/src/main/java/phase01/d04_hashmap/)

### Kiến thức cần nắm

Đây là phần bắt buộc phải hiểu sâu.

Hiểu:
- Hash function.
- Bucket.
- `hashCode()`.
- `equals()`.
- Collision.
- Capacity.
- Load factor.
- Resize.
- Treeification.
- Average O(1) lookup.
- Mutable key problem.
- Chi phí key có `hashCode()` kém; resize và thứ tự duyệt không được bảo đảm.

Luồng lookup về mặt khái niệm:

```text
key
 ↓
hashCode()
 ↓
hash
 ↓
bucket
 ↓
so sánh hash
 ↓
equals()
 ↓
value
```

---

### Câu hỏi kiểm tra

1. `HashMap.get(key)` hoạt động như thế nào?
2. Vì sao `hashCode()` thôi chưa đủ?
3. Collision là gì?
4. Hai object có cùng `hashCode()` nhưng `equals()` false thì chuyện gì xảy ra?
5. Nếu `equals()` true nhưng hashCode khác nhau thì có vấn đề gì?
6. Vì sao phải override `hashCode()` khi override `equals()`?
7. Load factor dùng để làm gì?
8. Resize ảnh hưởng performance thế nào?
9. Mutable object có nên được dùng làm HashMap key không?
10. Điều gì xảy ra nếu field tham gia `hashCode()` bị thay đổi sau khi object đã được put?
11. Worst-case lookup của HashMap là gì?
12. Java hiện đại xử lý bucket có quá nhiều collision như thế nào?
13. `HashMap` có thread-safe không?
14. `HashMap` cho phép bao nhiêu null key?
15. So sánh `HashMap`, `LinkedHashMap`, `TreeMap`.

---

## 5. equals() và hashCode()

> Thực hành: [d05_equals_hashcode](phase-01-core-advanced/src/main/java/phase01/d05_equals_hashcode/)

### Kiến thức cần nắm

Contract của equals:
- Reflexive
- Symmetric
- Transitive
- Consistent
- null-safe

HashCode contract:

Nếu:

```java
a.equals(b)
```

thì bắt buộc:

```java
a.hashCode() == b.hashCode()
```

Chiều ngược lại không bắt buộc.

---

### Câu hỏi kiểm tra

1. Contract của `equals()` gồm những gì?
2. Hai object cùng hashCode có bắt buộc equals không?
3. Hai object equals có bắt buộc cùng hashCode không?
4. Nếu chỉ override equals mà không override hashCode thì bug xuất hiện ở đâu?
5. `==` và `equals()` khác nhau thế nào?
6. Với record, equals/hashCode được xử lý thế nào?
7. Vì sao mutable entity có thể gây vấn đề khi nằm trong HashSet?

---

## 6. HashSet

> Thực hành: [d06_hashset](phase-01-core-advanced/src/main/java/phase01/d06_hashset/)

### Kiến thức cần nắm

Hiểu rằng `HashSet` dựa trên HashMap.

Nắm:
- Uniqueness.
- `equals/hashCode`.
- Lookup.
- Mutable element problem.

---

### Câu hỏi kiểm tra

1. HashSet phát hiện duplicate như thế nào?
2. HashSet khác ArrayList ở lookup thế nào?
3. HashSet có đảm bảo iteration order không?
4. Vì sao mutable object trong HashSet nguy hiểm?
5. `LinkedHashSet` khác HashSet ra sao?
6. `TreeSet` cần Comparable/Comparator vì sao?

---

## 7. Comparable & Comparator

> Thực hành: [d07_comparable_comparator](phase-01-core-advanced/src/main/java/phase01/d07_comparable_comparator/)

### Kiến thức cần nắm

#### Comparable

Natural ordering.

```java
class User implements Comparable<User>
```

#### Comparator

External/custom ordering.

```java
Comparator<User> byAge =
    Comparator.comparing(User::getAge);
```

Hiểu comparator composition:

```java
Comparator
    .comparing(User::getAge)
    .thenComparing(User::getName);
```

---

### Câu hỏi kiểm tra

1. Comparable và Comparator khác nhau thế nào?
2. Khi nào nên dùng Comparator thay vì Comparable?
3. Natural ordering nghĩa là gì?
4. Nếu Comparator không consistent với equals thì điều gì có thể xảy ra với TreeSet?
5. Làm thế nào sort theo nhiều field?
6. Làm thế nào xử lý null khi sort?

---

## 8. Immutability

> Thực hành: [d08_immutability](phase-01-core-advanced/src/main/java/phase01/d08_immutability/)

### Kiến thức cần nắm

Immutable object:
- State không thay đổi sau construction.
- Không expose mutable internal state.
- Defensive copy khi cần.

Lợi ích:
- Thread safety.
- Predictability.
- Dễ reasoning.
- An toàn khi dùng làm map key.

Nắm:
- final field.
- defensive copy.
- immutable collections.
- record.

Lưu ý `List.of(...)` và `Stream.toList()` tạo danh sách không cho sửa nội dung; `record` chỉ bảo đảm các component field là `final`, không tự sao chép object mutable lồng bên trong.

---

### Câu hỏi kiểm tra

1. `final class` có đồng nghĩa immutable không?
2. `final List<String>` có immutable không?
3. Defensive copy là gì?
4. Vì sao immutable object dễ thread-safe?
5. Record có tự động đảm bảo deep immutability không?
6. Khi nào immutable object có nhược điểm?

---

## 9. Lambda & Functional Interface

> Thực hành: [d09_lambda](phase-01-core-advanced/src/main/java/phase01/d09_lambda/)

### Kiến thức cần nắm

Functional Interface có đúng một abstract method.

Các interface phổ biến:
- `Predicate<T>`
- `Function<T,R>`
- `Consumer<T>`
- `Supplier<T>`
- `UnaryOperator<T>`
- `BinaryOperator<T>`

Hiểu:
- Lambda.
- Method reference.
- Captured variables.
- Effectively final.

---

### Câu hỏi kiểm tra

1. Functional Interface là gì?
2. `Predicate` và `Function` khác nhau thế nào?
3. `Consumer` có return value không?
4. Vì sao local variable được lambda capture phải effectively final?
5. Method reference có khác lambda về bản chất không?
6. Lambda có phù hợp với logic dài và nhiều side effect không?

---

## 10. Stream API

> Thực hành: [d10_stream](phase-01-core-advanced/src/main/java/phase01/d10_stream/)

### Kiến thức cần nắm

Phân biệt:
- Source.
- Intermediate operation.
- Terminal operation.

Intermediate:
- map
- filter
- flatMap
- distinct
- sorted
- limit

Terminal:
- collect
- reduce
- count
- findFirst
- anyMatch

Hiểu:
- Lazy evaluation.
- Stream pipeline.
- Stateless vs stateful operations.
- Side effects.
- Parallel stream trade-offs.
- Phân biệt `Stream.toList()` với `Collectors.toList()` về hợp đồng mutability; đừng mặc định kết quả của collector luôn sửa được.

---

### Câu hỏi kiểm tra

1. `map()` và `flatMap()` khác nhau thế nào?
2. Vì sao Stream intermediate operation là lazy?
3. Stream có reusable không?
4. `filter()` chạy lúc nào?
5. `reduce()` dùng để làm gì?
6. `collect()` và `reduce()` khác nhau thế nào?
7. Side effect trong Stream có vấn đề gì?
8. `parallelStream()` có luôn nhanh hơn không?
9. Parallel stream sử dụng thread pool nào?
10. Tại sao blocking I/O thường không phải use case tốt cho parallel stream?
11. Thứ tự operation có thể ảnh hưởng performance thế nào?

Ví dụ:

```java
users.stream()
    .filter(User::isActive)
    .map(User::getEmail)
    .distinct()
    .toList();
```

Ứng viên phải giải thích được từng bước và thời điểm chúng thực thi.

---

## 11. Optional

> Thực hành: [d11_optional](phase-01-core-advanced/src/main/java/phase01/d11_optional/)

### Kiến thức cần nắm

Optional biểu diễn giá trị có thể không tồn tại.

Nắm:
- `of`
- `ofNullable`
- `empty`
- `map`
- `flatMap`
- `filter`
- `orElse`
- `orElseGet`
- `orElseThrow`

Không nên dùng Optional bừa bãi cho:
- Entity fields.
- Method parameters.
- Mọi field trong DTO.

---

### Câu hỏi kiểm tra

1. `Optional.of()` và `ofNullable()` khác nhau thế nào?
2. `orElse()` và `orElseGet()` khác gì?
3. Tại sao khác biệt trên có thể ảnh hưởng performance?
4. Optional có nên dùng làm method argument không?
5. Optional có thực sự loại bỏ NullPointerException không?
6. `map()` và `flatMap()` trên Optional khác nhau thế nào?

---

## 12. Date & Time API

> Thực hành: [d12_datetime](phase-01-core-advanced/src/main/java/phase01/d12_datetime/)

### Kiến thức cần nắm

Nắm:
- `LocalDate`
- `LocalTime`
- `LocalDateTime`
- `Instant`
- `ZonedDateTime`
- `ZoneId`
- `Duration`
- `Period`
- `OffsetDateTime` khi cần giữ offset trong một giá trị thời gian.

Phân biệt:
- Thời gian không timezone.
- Absolute timestamp.
- Timezone-aware time.

---

### Câu hỏi kiểm tra

1. `LocalDateTime` có timezone không?
2. Khi lưu timestamp vào database nên cân nhắc `Instant` vì sao?
3. `Instant` và `ZonedDateTime` khác nhau thế nào?
4. `Duration` và `Period` khác nhau thế nào?
5. Vì sao xử lý timezone bằng cộng/trừ offset thủ công dễ lỗi?
6. DST có thể gây những bug nào?
7. Một cuộc hẹn lúc 09:00 theo múi giờ địa phương nên lưu những thông tin gì để không lệch sau thay đổi DST?

---

## 13. Reflection

> Thực hành: [d13_reflection](phase-01-core-advanced/src/main/java/phase01/d13_reflection/)

### Kiến thức cần nắm

Reflection cho phép inspect/manipulate runtime metadata:
- Class.
- Constructor.
- Field.
- Method.
- Annotation.

Hiểu Reflection được framework như Spring, Hibernate, Jackson sử dụng.

Nắm trade-off:
- Type safety.
- Performance.
- Encapsulation.
- Maintainability.

---

### Câu hỏi kiểm tra

1. Reflection giải quyết vấn đề gì?
2. Spring có thể sử dụng reflection ở đâu?
3. Reflection có nhược điểm gì?
4. Có thể gọi private method bằng reflection không?
5. Reflection ảnh hưởng compile-time checking ra sao?
6. Vì sao business code thông thường không nên lạm dụng reflection?

---

## 14. Annotation

> Thực hành: [d14_annotation](phase-01-core-advanced/src/main/java/phase01/d14_annotation/)

### Kiến thức cần nắm

Hiểu:
- Built-in annotation.
- Custom annotation.
- Retention.
- Target.
- Annotation processing/reflection.

Retention:
- SOURCE
- CLASS
- RUNTIME

---

### Câu hỏi kiểm tra

1. Annotation bản thân nó có tự thực thi logic không?
2. `RetentionPolicy.RUNTIME` có ý nghĩa gì?
3. SOURCE và RUNTIME khác nhau thế nào?
4. `@Target` dùng để làm gì?
5. Spring xử lý những annotation như `@Service` bằng cơ chế nào?
6. Khi nào nên tạo custom annotation?

---

## 15. Java hiện đại: record, sealed type, pattern matching

> Thực hành: [d15_modern_java](phase-01-core-advanced/src/main/java/phase01/d15_modern_java/)

### Kiến thức cần nắm

- Dùng `record` cho dữ liệu mang giá trị, hiểu constructor, `equals`/`hashCode` tự sinh và giới hạn về shallow immutability.
- Dùng `sealed interface/class` để giới hạn tập subtype; biết khi nào mô hình phân cấp đóng là phù hợp.
- Dùng pattern matching với `instanceof` và `switch` để xử lý subtype; giải thích exhaustiveness, `null` và khả năng thay đổi tập subtype.
- Nhận biết `switch` expression và text block; chỉ dùng cú pháp đã ổn định trong JDK mục tiêu của project.

### Câu hỏi kiểm tra

1. `record` khác POJO thông thường ở điểm nào và khi nào vẫn cần class thường?
2. Một component `List` trong record có tự trở nên immutable không? Bạn sẽ bảo vệ nó thế nào?
3. `sealed` mang lại lợi ích gì cho `switch` trên các subtype?
4. Khi thêm subtype mới, compiler có thể giúp tìm những chỗ nào cần sửa?
5. `switch` expression khác statement ở giá trị trả về và tính đầy đủ của nhánh thế nào?

---

## 16. Exception handling và quản lý tài nguyên

> Thực hành: [d16_exceptions_resources](phase-01-core-advanced/src/main/java/phase01/d16_exceptions_resources/)

### Kiến thức cần nắm

- Checked/unchecked exception, nguyên nhân gốc và ranh giới chuyển lỗi giữa các layer.
- `try-with-resources` và `AutoCloseable`: đóng file, stream, connection đúng cả khi có exception.
- `IOException`, `Path`, `Files`, charset UTF-8; đọc file vừa đủ cho dữ liệu nhỏ, dùng streaming cho file lớn.
- Không nuốt lỗi, không dùng exception cho nhánh nghiệp vụ thông thường; giữ nguyên cause khi bọc exception.

### Câu hỏi kiểm tra

1. `try-with-resources` đóng tài nguyên theo thứ tự nào khi khai báo nhiều tài nguyên?
2. Nếu thao tác chính và `close()` cùng ném exception thì kiểm tra thông tin lỗi ở đâu?
3. Vì sao `Files.readString()` không phù hợp với file nhiều GB?
4. Khi nào nên chuyển một `IOException` thành lỗi ở tầng ứng dụng?
5. Vì sao `catch (Exception e) {}` khiến việc vận hành khó khăn?

---

## Bài thực hành tích hợp

> Thực hành: [d17_capstone](phase-01-core-advanced/src/main/java/phase01/d17_capstone/)

Viết công cụ đọc file CSV UTF-8 chứa đơn hàng (`id`, `customerId`, `createdAt`, `amount`, `status`), tạo báo cáo theo khách hàng.

1. Dùng `record` cho dữ liệu đầu vào; phân biệt timestamp tuyệt đối và ngày theo múi giờ của báo cáo.
2. Parse bằng `Files.newBufferedReader()` và `try-with-resources`; ghi nhận dòng lỗi có số dòng mà không nuốt exception I/O. Dùng CSV đơn giản không có dấu phẩy nằm trong chuỗi; nếu cần CSV đầy đủ, chọn thư viện parser riêng.
3. Chọn `Map`/`Set` phù hợp để phát hiện ID trùng, tổng hợp và giữ thứ tự báo cáo; giải thích `equals`/`hashCode` của key.
4. Viết hai phiên bản tổng hợp bằng vòng lặp và Stream; tránh state mutable dùng chung trong Stream.
5. Viết test cho file rỗng, dòng sai, ID trùng, múi giờ khác và kết quả không cho sửa ngoài ý muốn.

**Đạt khi:** chạy từ dòng lệnh, test xanh, không rò rỉ tài nguyên và giải thích được lý do chọn cấu trúc dữ liệu/API.

---

## Exit Criteria

Ứng viên hoàn thành giai đoạn khi có thể:

- Giải thích HashMap từ hashCode tới bucket lookup.
- Giải thích chính xác equals/hashCode contract.
- Sử dụng Generics và giải thích PECS.
- Chọn đúng Collection dựa trên workload.
- Sử dụng Stream mà không tạo side effect khó kiểm soát.
- Hiểu Optional thay vì dùng như cách né null.
- Phân biệt Instant, LocalDateTime, ZonedDateTime.
- Giải thích Reflection/Annotation ở mức framework sử dụng.
- Dùng record/sealed/pattern matching đúng phiên bản Java, xử lý file/exception an toàn và hoàn thành bài tích hợp có test.

**Tài liệu chuẩn:** [Java Language Changes by Release](https://docs.oracle.com/en/java/javase/25/language/java-language-changes-release.html), [Java Collections Framework](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/doc-files/coll-overview.html).
