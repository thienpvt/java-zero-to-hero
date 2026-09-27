# Giai đoạn 2 — JVM & Concurrency

## Mục tiêu

Ứng viên phải hiểu (mô hình đơn giản; JVM có thể thông dịch hoặc JIT biên dịch bytecode):

```text
Java source
   ↓
Compiler
   ↓
Bytecode
   ↓
JVM
   ↓
Machine Code
```

Đồng thời có khả năng phân tích:
- Memory.
- Thread safety.
- Race conditions.
- Visibility.
- Synchronization.
- Thread pools.
- Async execution.

**Chuẩn bị:** JDK 21 hoặc 25 LTS; chạy bài tập trên một phiên bản đã ghi rõ. Sử dụng project nhỏ có JUnit và lệnh chạy lặp lại. Phân biệt quan sát thực nghiệm với phát biểu được Java Memory Model bảo đảm.

---

## 1. Compilation & Bytecode

> Thực hành: [d01_bytecode](phase-02-jvm-concurrency/src/main/java/phase02/d01_bytecode/)

### Kiến thức cần nắm

Nắm pipeline:

```text
.java
 ↓ javac
.class
 ↓ JVM
bytecode execution
```

Hiểu:
- Bytecode.
- Platform independence.
- Interpreter.
- JIT compiler.

---

### Câu hỏi kiểm tra

1. Java được compile hay interpreted?
2. `.class` chứa gì?
3. Vì sao Java được gọi là platform-independent?
4. Bytecode khác machine code thế nào?
5. JIT giải quyết vấn đề gì?
6. JVM có chạy trực tiếp source code không?

---

## 2. Class Loading

> Thực hành: [d02_class_loading](phase-02-jvm-concurrency/src/main/java/phase02/d02_class_loading/)

### Kiến thức cần nắm

Các bước:
- Loading
- Linking
  - Verification
  - Preparation
  - Resolution
- Initialization

Class Loaders:
- Bootstrap
- Platform
- Application

Hiểu parent delegation ở mức khái niệm.

---

### Câu hỏi kiểm tra

1. Class được JVM load khi nào?
2. Loading và initialization khác nhau thế nào?
3. Static field được initialize khi nào?
4. Parent delegation giải quyết vấn đề gì?
5. Hai class cùng tên do hai ClassLoader khác nhau load có phải cùng type không?
6. `ClassNotFoundException` và `NoClassDefFoundError` khác nhau thế nào?

---

## 3. JVM Memory

> Thực hành: [d03_jvm_memory](phase-02-jvm-concurrency/src/main/java/phase02/d03_jvm_memory/)

### Kiến thức cần nắm

Phân biệt:

#### Stack
- Method frames.
- Local variables.
- References.
- Per-thread.

#### Heap
- Objects.
- Shared giữa threads.
- Garbage collected.

#### Metaspace
- Class metadata.

Nắm thêm:
- PC register.
- Native method stack ở mức khái niệm.
- Phân biệt heap với off-heap/direct buffer và native memory; không quy mọi `OutOfMemoryError` về một nguyên nhân.

---

### Câu hỏi kiểm tra

1. Object Java thường được lưu ở đâu?
2. Local primitive nằm ở đâu?
3. Reference và object có nhất thiết nằm cùng nơi không?
4. Stack có shared giữa threads không?
5. Heap có shared không?
6. StackOverflowError xảy ra khi nào?
7. OutOfMemoryError có những nguyên nhân nào?
8. Metaspace chứa gì?
9. Memory leak có thể xảy ra trong Java dù có GC không?

---

## 4. Object Lifecycle & Garbage Collection

> Thực hành: [d04_gc](phase-02-jvm-concurrency/src/main/java/phase02/d04_gc/)

### Kiến thức cần nắm

Hiểu:
- Object allocation.
- Reachability.
- Garbage collection roots.
- Young/old generation ở mức khái niệm.
- Minor/major/full collection ở mức khái niệm.
- Stop-the-world.
- GC pause.
- Throughput vs latency.

Reference types:
- Strong
- Weak
- Soft
- Phantom

Không cần học thuộc implementation mọi collector trước khi hiểu fundamentals.

---

### Câu hỏi kiểm tra

1. Khi nào object đủ điều kiện để GC?
2. GC có chạy ngay khi object không còn reference không?
3. GC Root là gì?
4. Circular references có gây memory leak giống reference counting không?
5. Strong và WeakReference khác nhau thế nào?
6. WeakHashMap hữu ích trong trường hợp nào?
7. Stop-the-world nghĩa là gì?
8. GC tối ưu điều gì: latency hay throughput?
9. Vì sao cache không giới hạn có thể gây memory leak?
10. Có thể ép JVM thực hiện GC bằng `System.gc()` không?

`System.gc()` chỉ là yêu cầu/đề nghị; đừng dựa vào nó để đảm bảo thu hồi một object cụ thể.

---

## 5. JIT Compiler

> Thực hành: [d05_jit](phase-02-jvm-concurrency/src/main/java/phase02/d05_jit/)

### Kiến thức cần nắm

Hiểu:
- JVM theo dõi runtime execution.
- Hot methods/hot paths.
- Compile bytecode thành optimized machine code.
- Runtime optimization.

Không cần đi sâu compiler internals ở giai đoạn này.

---

### Câu hỏi kiểm tra

1. JIT khác `javac` như thế nào?
2. Vì sao Java application có thể nhanh hơn sau warm-up?
3. JVM có thể tối ưu dựa trên runtime information như thế nào?
4. Benchmark Java tại sao cần warm-up?
5. Vì sao benchmark bằng `System.nanoTime()` loop đơn giản dễ sai?

---

## 6. Thread Fundamentals

> Thực hành: [d06_threads](phase-02-jvm-concurrency/src/main/java/phase02/d06_threads/)

### Kiến thức cần nắm

Nắm:
- Process vs Thread.
- Thread lifecycle.
- Runnable.
- Callable.
- start vs run.
- join.
- interrupt.
- daemon thread.
- Hủy tác vụ hợp tác qua `interrupt` và timeout; khôi phục cờ interrupt khi không thể xử lý tại chỗ.

---

### Câu hỏi kiểm tra

1. Process và Thread khác nhau thế nào?
2. `thread.start()` khác `thread.run()` thế nào?
3. Runnable và Callable khác nhau thế nào?
4. `join()` dùng để làm gì?
5. Interrupt có buộc thread dừng ngay lập tức không?
6. InterruptedException nên được xử lý thế nào?
7. Daemon thread là gì?
8. Tạo một thread mới cho mỗi request có vấn đề gì?

---

## 7. Race Condition

> Thực hành: [d07_race](phase-02-jvm-concurrency/src/main/java/phase02/d07_race/)

### Kiến thức cần nắm

Ví dụ:

```java
class Counter {
    private int count;

    void increment() {
        count++;
    }
}
```

Phải hiểu:

```text
count++
```

không phải một atomic operation đơn giản.

Về logic:

```text
read
modify
write
```

---

### Câu hỏi kiểm tra

1. Code trên có thread-safe không?
2. Vì sao `count++` không atomic?
3. Race condition là gì?
4. Race condition có luôn reproduce được không?
5. Tại sao bug concurrency thường khó debug?
6. Có thể sửa Counter bằng những cách nào?

---

## 8. synchronized

> Thực hành: [d08_synchronized](phase-02-jvm-concurrency/src/main/java/phase02/d08_synchronized/)

### Kiến thức cần nắm

Hiểu:
- Intrinsic lock / monitor.
- synchronized method.
- synchronized block.
- Mutual exclusion.
- Happens-before liên quan tới monitor.

```java
synchronized (lock) {
    // critical section
}
```

Biết giảm scope của critical section khi phù hợp.

---

### Câu hỏi kiểm tra

1. `synchronized` lock object nào?
2. synchronized instance method lock gì?
3. synchronized static method lock gì?
4. Hai synchronized methods của cùng object có chạy song song được không?
5. Nếu synchronize trên hai object khác nhau thì sao?
6. synchronized chỉ giải quyết atomicity hay còn visibility?
7. Vì sao synchronized block đôi khi tốt hơn synchronized method?
8. Java intrinsic locks có reentrant không?

---

## 9. volatile & Java Memory Model

> Thực hành: [d09_jmm](phase-02-jvm-concurrency/src/main/java/phase02/d09_jmm/)

### Kiến thức cần nắm

Hiểu:
- Visibility.
- Ordering.
- Happens-before.
- volatile.
- Atomicity khác visibility.

Ví dụ:

```java
private volatile boolean running = true;
```

Phải biết volatile không biến:

```java
count++;
```

thành atomic.

---

### Câu hỏi kiểm tra

1. `volatile` giải quyết vấn đề gì?
2. volatile có đảm bảo atomicity không?
3. `volatile int count; count++` có thread-safe không?
4. Visibility nghĩa là gì?
5. Happens-before nghĩa là gì?
6. Khi nào volatile phù hợp?
7. volatile và synchronized khác nhau như thế nào?
8. Double-checked locking cần volatile vì sao?

---

## 10. Atomic Classes

> Thực hành: [d10_atomic](phase-02-jvm-concurrency/src/main/java/phase02/d10_atomic/)

### Kiến thức cần nắm

Nắm:
- AtomicInteger
- AtomicLong
- AtomicReference
- Compare-And-Set / CAS

Ví dụ:

```java
AtomicInteger counter = new AtomicInteger();

counter.incrementAndGet();
```

Hiểu atomic classes không thay thế mọi loại locking.

---

### Câu hỏi kiểm tra

1. AtomicInteger khác volatile int thế nào?
2. CAS là gì?
3. Vì sao CAS có thể tránh lock trong một số trường hợp?
4. CAS có nhược điểm gì khi contention cao?
5. AtomicInteger có phù hợp để bảo vệ nhiều biến phải update cùng transaction không?
6. ABA problem là gì ở mức khái niệm?

---

## 11. Lock API

> Thực hành: [d11_locks](phase-02-jvm-concurrency/src/main/java/phase02/d11_locks/)

### Kiến thức cần nắm

Nắm:
- Lock.
- ReentrantLock.
- tryLock.
- lockInterruptibly.
- Condition.
- ReadWriteLock ở mức khái niệm.

Phải luôn cân nhắc unlock trong finally.

```java
lock.lock();

try {
    // critical section
} finally {
    lock.unlock();
}
```

---

### Câu hỏi kiểm tra

1. ReentrantLock cung cấp gì hơn synchronized?
2. Vì sao unlock phải nằm trong finally?
3. `tryLock()` hữu ích khi nào?
4. Reentrant nghĩa là gì?
5. Khi nào synchronized đơn giản và tốt hơn ReentrantLock?
6. ReadWriteLock phù hợp với workload nào?

---

## 12. Deadlock

> Thực hành: [d12_deadlock](phase-02-jvm-concurrency/src/main/java/phase02/d12_deadlock/)

### Kiến thức cần nắm

Ví dụ:

```text
Thread A:
lock X
wait Y

Thread B:
lock Y
wait X
```

Hiểu các điều kiện deadlock ở mức khái niệm:
- Mutual exclusion
- Hold and wait
- No preemption
- Circular wait

Biết chiến lược:
- Consistent lock ordering.
- Minimize nested locks.
- tryLock.
- Timeout khi phù hợp.

---

### Câu hỏi kiểm tra

1. Deadlock là gì?
2. Hai threads phải có ít nhất bao nhiêu lock để tạo ví dụ deadlock điển hình?
3. Lock ordering giúp tránh deadlock thế nào?
4. Deadlock khác livelock thế nào?
5. Starvation là gì?
6. Làm sao diagnose deadlock trong JVM?
7. Thread dump có thể giúp gì?

---

## 13. ExecutorService

> Thực hành: [d13_executor](phase-02-jvm-concurrency/src/main/java/phase02/d13_executor/)

### Kiến thức cần nắm

Không quản lý thread thủ công nếu thread pool phù hợp.

Nắm:
- Executor.
- ExecutorService.
- Fixed thread pool.
- Cached thread pool.
- Scheduled executor.
- submit.
- shutdown.
- Future.

Hiểu thread pool:
- Worker threads.
- Work queue.
- Pool sizing.
- Rejection.
- Bounded queue, backpressure và chính sách khi hết chỗ; đóng executor sau sử dụng.

---

### Câu hỏi kiểm tra

1. Tại sao dùng thread pool thay vì tạo Thread liên tục?
2. `execute()` và `submit()` khác nhau thế nào?
3. Future dùng để làm gì?
4. `shutdown()` và `shutdownNow()` khác nhau thế nào?
5. Thread pool quá lớn gây vấn đề gì?
6. Thread pool quá nhỏ gây vấn đề gì?
7. CPU-bound và I/O-bound workload có cùng chiến lược pool size không?
8. Unbounded queue có rủi ro gì?
9. Rejection policy được sử dụng khi nào?

---

## 14. CompletableFuture

> Thực hành: [d14_completable_future](phase-02-jvm-concurrency/src/main/java/phase02/d14_completable_future/)

### Kiến thức cần nắm

Nắm:
- supplyAsync
- runAsync
- thenApply
- thenCompose
- thenCombine
- exceptionally
- handle
- allOf

Phân biệt:

```text
thenApply   ~ map
thenCompose ~ flatMap
```

Hiểu executor mặc định và custom executor; exception và cancellation trong chuỗi các stage. Đặt timeout cho lời gọi I/O theo ranh giới hệ thống.

---

### Câu hỏi kiểm tra

1. Future có điểm yếu gì mà CompletableFuture giải quyết?
2. `thenApply()` và `thenCompose()` khác nhau thế nào?
3. `thenCombine()` dùng để làm gì?
4. Exception được propagate như thế nào?
5. `join()` và `get()` khác nhau thế nào?
6. `supplyAsync()` mặc định chạy ở đâu?
7. Vì sao blocking task trong common pool có thể gây vấn đề?
8. Khi nào nên truyền custom Executor?

---

## 15. Concurrent Collections

> Thực hành: [d15_concurrent_collections](phase-02-jvm-concurrency/src/main/java/phase02/d15_concurrent_collections/)

### Kiến thức cần nắm

Nắm:
- ConcurrentHashMap
- CopyOnWriteArrayList
- BlockingQueue

Hiểu workload phù hợp.

#### CopyOnWriteArrayList

Phù hợp:
- Read rất nhiều.
- Write rất ít.

#### BlockingQueue

Quan trọng trong producer-consumer.

---

### Câu hỏi kiểm tra

1. Tại sao không đơn giản synchronize HashMap trong mọi trường hợp?
2. ConcurrentHashMap hỗ trợ concurrency như thế nào ở mức khái niệm?
3. ConcurrentHashMap có cho null key/value không?
4. CopyOnWriteArrayList tốt cho workload nào?
5. Vì sao write của CopyOnWriteArrayList đắt?
6. BlockingQueue giải quyết producer-consumer như thế nào?
7. `put()` và `offer()` khác nhau thế nào?

---

## 16. Thread Safety Design

> Thực hành: [d16_thread_safety](phase-02-jvm-concurrency/src/main/java/phase02/d16_thread_safety/)

### Kiến thức cần nắm

Các chiến lược:
- Immutability.
- Thread confinement.
- Stateless design.
- Synchronization.
- Atomic operations.
- Concurrent collections.

Quan trọng nhất: tránh shared mutable state khi có thể.

---

### Câu hỏi kiểm tra

1. Stateless service thường thread-safe vì sao?
2. Immutable object giúp concurrency thế nào?
3. Local variable có cần synchronize không?
4. Spring singleton service có thể gặp concurrency bug khi nào?
5. Shared mutable state là gì?
6. ThreadLocal giải quyết vấn đề gì?
7. ThreadLocal có thể gây memory/resource leak như thế nào khi dùng với thread pool?

---

## 17. Virtual threads (Java 21+)

> Thực hành: [d17_virtual_threads](phase-02-jvm-concurrency/src/main/java/phase02/d17_virtual_threads/)

### Kiến thức cần nắm

- Virtual thread là `Thread` nhẹ do JDK quản lý, phù hợp nhiều tác vụ chủ yếu chờ I/O; không làm một phép tính CPU nhanh hơn.
- Thử `Executors.newVirtualThreadPerTaskExecutor()`; mỗi task có thể có một virtual thread, không tạo pool virtual thread để giới hạn số lượng.
- Đặt giới hạn theo tài nguyên hữu hạn: DB connection pool, API bên ngoài, semaphore, rate limit và timeout.
- Phân biệt virtual threads, platform threads, `CompletableFuture` và thread pool truyền thống bằng một workload cụ thể.
- Nếu học vấn đề pinning, ghi rõ phiên bản JDK: hành vi liên quan `synchronized` đã thay đổi từ JDK 24; không áp kết luận của JDK 21 cho JDK 25.
- Structured concurrency là chủ đề theo dõi tùy chọn; kiểm tra trạng thái preview của đúng JDK trước khi dùng trong project.

### Câu hỏi kiểm tra

1. Virtual threads tăng khả năng phục vụ nhiều request chờ I/O theo cơ chế nào?
2. Vì sao 100.000 virtual threads vẫn có thể làm cạn DB connection pool?
3. Khi nào virtual threads không giúp tăng throughput?
4. Timeout và hủy request nên được truyền tới tác vụ con như thế nào?
5. Vì sao không nên pool virtual threads chỉ để giới hạn concurrency?

---

## 18. Chẩn đoán JVM bằng bằng chứng

> Thực hành: [d18_diagnostics](phase-02-jvm-concurrency/src/main/java/phase02/d18_diagnostics/)

### Kiến thức cần nắm

- Lấy thread dump để thấy thread đang chạy, chờ lock hoặc deadlock; đối chiếu nhiều lần chụp khi cần.
- Dùng `jcmd` xem thông tin VM/heap, bắt đầu JFR và đọc sự kiện; đọc GC logs để nhận biết pause và áp lực bộ nhớ.
- Phân biệt retained heap do reference còn sống, tốc độ allocation cao, contention và backlog của queue.
- Tránh kết luận từ một con số CPU/heap đơn lẻ; ghi workload, cấu hình JDK, thời điểm đo và dấu hiệu trước/sau.

### Câu hỏi kiểm tra

1. Thread dump cho thấy deadlock và thread chờ I/O khác nhau ra sao?
2. Tại sao heap sử dụng tăng chưa chứng minh có memory leak?
3. JFR hữu ích gì khi tìm contention hoặc allocation tăng?
4. Khi queue tăng dần nhưng CPU thấp, bạn sẽ kiểm tra gì trước?
5. Làm thế nào so sánh một thay đổi GC hoặc pool size mà không nhầm do workload khác nhau?

---

## Practical Exercise

> Thực hành: [d19_capstone](phase-02-jvm-concurrency/src/main/java/phase02/d19_capstone/)

Cho class:

```java
public class Account {

    private BigDecimal balance;

    public void withdraw(BigDecimal amount) {
        if (balance.compareTo(amount) >= 0) {
            balance = balance.subtract(amount);
        }
    }
}
```

Ứng viên phải:

1. Chỉ ra race condition.
2. Mô tả interleaving gây sai balance.
3. Đề xuất ít nhất hai cách sửa.
4. Giải thích trade-off.
5. Phân tích nếu Account nằm trong distributed system thì Java lock có đủ không.

**Phần mở rộng có thể chạy:** tạo nhiều task cùng rút tiền bằng một `ExecutorService`; dùng start gate để tăng khả năng va chạm. Kiểm tra bất biến tổng số tiền đã rút và số dư cuối, nhưng không coi một lần chạy không lỗi là chứng minh thread-safe. Sửa bằng lock bao trùm cặp kiểm tra/cập nhật hoặc mô hình bất biến với CAS thích hợp; benchmark chỉ sau khi đúng. Với nhiều JVM, cần cơ chế nhất quán tại nơi lưu trạng thái chung (ví dụ transaction/locking ở DB), không dựa vào Java lock cục bộ.

**Bài đo bổ sung:** so sánh platform-thread pool và virtual threads cho I/O giả lập có giới hạn 20 kết nối; ghi throughput, latency, queue/connection usage; giải thích vì sao tăng số thread không vượt qua giới hạn tài nguyên.

---

## Exit Criteria

Ứng viên hoàn thành giai đoạn khi có thể:

- Giải thích stack, heap và metaspace.
- Giải thích vì sao Java vẫn có memory leak.
- Hiểu JIT và warm-up.
- Phân biệt atomicity, visibility và ordering.
- Giải thích synchronized, volatile, atomic và Lock.
- Phát hiện race condition/deadlock.
- Thiết kế thread pool hợp lý.
- Sử dụng CompletableFuture mà không block tùy tiện.
- Chọn đúng concurrent collection cho workload.
- Thu thread dump/JFR hoặc GC log để chẩn đoán một hiện tượng thực tế, và giải thích khi nào dùng virtual threads.

**Tài liệu chuẩn:** [JEP 444 — Virtual Threads](https://openjdk.org/jeps/444), [JEP 491 — Synchronize Virtual Threads without Pinning](https://openjdk.org/jeps/491), [jcmd](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jcmd.html).
