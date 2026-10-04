# Đặc tả bài tập Giai đoạn 9 — Algorithms & System Design

Ngày: 2026-10-04  
Trạng thái: chờ rà soát; nội dung Q phải khớp nguyên văn `09-algorithms-system-design.md`

## Mục tiêu và phạm vi

Tạo module Java 21/JUnit 5 với bài tập IDE cho đúng 20 heading và 100 câu hỏi trong `09-algorithms-system-design.md`. Giữ nguyên phân nhóm câu Q1–Q5 cục bộ trong từng topic; không tạo số toàn cục, không đổi/sáng tác câu hỏi. DSA assessment và system-design capstone là bài B (bài thực hành), không thay thế Q. Topic 02 giữ chung array + string; topic 11 Backtracking và 12 Dynamic Programming giữ riêng.

Module: `phase-09-algorithms-system-design`, không phụ thuộc Spring/DB cho DSA. DSA tasks dùng code, tests, brute-force oracle; system-design tasks dùng đáp án giải thích được người đọc review. Không chấm prose bằng assertions.

## Heading/topic register và câu hỏi nguyên văn

Nguồn: `09-algorithms-system-design.md`. Q numbers reset 1–5 trong mỗi topic. Nhãn [Q] là registry duy nhất; implementation Javadoc phải chép đúng các chuỗi sau, kể cả dấu câu/backticks.

### Track A — DSA

#### 01. Độ phức tạp và cách kiểm chứng
- Q1: Vì sao hai vòng lặp lồng nhau không phải lúc nào cũng O(n²)?
- Q2: Amortized complexity khác expected complexity thế nào?
- Q3: HashMap có bảo đảm lookup worst-case O(1) không?
- Q4: O(n log n) tăng thế nào so với O(n²) khi n lớn?
- Q5: Khi nào benchmark không đủ để chứng minh thuật toán phù hợp?

#### 02. Array và String
- Q1: Truy cập array theo index và chèn vào đầu có chi phí thế nào?
- Q2: Vì sao `String.length()` không phải lúc nào cũng là số ký tự người dùng nhìn thấy?
- Q3: Khi nào dùng code point thay vì `char`?
- Q4: Cộng hai index hoặc tính `n * n` có thể sai thế nào khi vượt giới hạn `int`?
- Q5: Chuỗi rỗng và null có cùng ý nghĩa không?

#### 03. Hashing và Hash Table
- Q1: Hai object `equals()` nhau phải thỏa điều kiện gì về `hashCode()`?
- Q2: Điều gì xảy ra nếu key thay đổi sau khi đưa vào `HashMap`?
- Q3: Collision có làm sai kết quả hay chủ yếu ảnh hưởng hiệu năng?
- Q4: Khi nào chọn `TreeMap` thay vì `HashMap`?
- Q5: Vì sao hash không chứng minh được hai input giống nhau?

#### 04. Two Pointers và Sliding Window
- Q1: Điều kiện nào giúp sliding window duy trì được kết quả khi co/mở cửa sổ?
- Q2: Vì sao tổng đoạn con với số âm có thể không dùng được cách co cửa sổ thông thường?
- Q3: Trong two pointers trên array đã sort, vì sao có thể di chuyển một pointer mà không bỏ sót nghiệm?
- Q4: Invariant của cửa sổ là gì?
- Q5: Cần kiểm tra trường hợp input rỗng nào?

#### 05. Binary Search
- Q1: Dữ liệu hoặc predicate cần tính chất gì để binary search đúng?
- Q2: Khác biệt giữa tìm một occurrence và lower bound là gì?
- Q3: Vì sao cập nhật biên sai một đơn vị có thể gây infinite loop?
- Q4: Khi nào binary search trên đáp án không hợp lệ?
- Q5: Cần trả về gì nếu không có giá trị thỏa predicate?

#### 06. Stack, Queue và Deque
- Q1: Bài toán nào tự nhiên dùng LIFO, bài toán nào dùng FIFO?
- Q2: Vì sao `ArrayDeque` thường phù hợp hơn `Stack`?
- Q3: Monotonic stack duy trì invariant gì?
- Q4: BFS cần queue để bảo đảm đặc tính nào?
- Q5: Hành vi khi deque rỗng cần được thiết kế thế nào?

#### 07. Linked List
- Q1: Chèn O(1) trong linked list có bao gồm chi phí tìm vị trí không?
- Q2: Fast/slow pointer phát hiện cycle dựa trên lý do gì?
- Q3: Dummy node giảm trường hợp biên nào?
- Q4: Vì sao linked list thường kém cache locality hơn array?
- Q5: Khi nào dùng collection chuẩn thay vì tự cài linked list?

#### 08. Tree và Binary Search Tree
- Q1: Inorder traversal của BST có đặc tính gì?
- Q2: Tại sao BST thường không đảm bảo O(log n)?
- Q3: Cây lệch có chiều cao và chi phí tìm kiếm thế nào?
- Q4: Khi nào traversal iterative an toàn hơn recursive?
- Q5: Heap có thỏa tính chất BST không?

#### 09. Heap và Top-K
- Q1: Root của `PriorityQueue` mặc định là phần tử nào?
- Q2: Vì sao heap không thể trả lời mọi truy vấn thứ tự như sorted array?
- Q3: Khi nào heap kích thước k tốt hơn sort toàn bộ?
- Q4: Comparator dùng phép trừ có nguy cơ gì?
- Q5: Cần quy định thế nào khi nhiều phần tử đồng hạng ở top-K?

#### 10. Graph: BFS, DFS và đường đi
- Q1: Vì sao BFS cho shortest path trên graph không trọng số?
- Q2: BFS có giải đúng shortest path với trọng số khác nhau không?
- Q3: Làm sao phát hiện cycle trong directed graph khác với undirected graph?
- Q4: Vì sao cần khởi động traversal từ mọi node chưa thăm?
- Q5: Dijkstra cần điều kiện gì về trọng số?

#### 11. Backtracking
- Q1: State của backtracking cần chứa thông tin gì?
- Q2: Pruning bảo toàn tính đúng ra sao?
- Q3: Vì sao phải hoàn tác state sau mỗi nhánh?
- Q4: Duplicate đầu vào có thể tạo output trùng thế nào?
- Q5: Liệt kê mọi nghiệm có thể tránh chi phí phụ thuộc output không?

#### 12. Dynamic Programming
- Q1: State biểu diễn điều gì và làm sao biết state đủ thông tin?
- Q2: Base case sai thường tạo lỗi kiểu nào?
- Q3: Khi memoization tốt hơn tabulation, hoặc ngược lại?
- Q4: Điều kiện nào cho phép giảm bộ nhớ từ O(n²) xuống O(n)?
- Q5: Làm sao tránh overflow khi cộng chi phí hoặc điểm số?

### Track B — System Design

#### 13. Requirements, scope và SLO
- Q1: Functional requirement khác non-functional requirement thế nào?
- Q2: SLI, SLO và SLA khác nhau thế nào?
- Q3: Vì sao cần giới hạn scope trước khi chọn công nghệ?
- Q4: Khi nào modular monolith là lựa chọn hợp lý?
- Q5: Requirement nào ảnh hưởng trực tiếp đến consistency hoặc durability?

#### 14. Ước lượng tải và dung lượng
- Q1: Vì sao peak RPS quan trọng hơn trung bình trong sizing?
- Q2: Cần đưa yếu tố nào vào ước lượng storage theo thời gian?
- Q3: Làm sao phân biệt giả định với số đo?
- Q4: Read/write ratio ảnh hưởng lựa chọn nào?
- Q5: Ước lượng nào nên xác minh bằng load test hoặc production metrics?

#### 15. API, data ownership và invariant
- Q1: Idempotency cần thiết ở endpoint nào và vì sao?
- Q2: Ai chịu trách nhiệm giữ invariant của order và inventory?
- Q3: Khi nào API DTO nên độc lập persistence entity?
- Q4: Authorization nên kiểm tra quyền truy cập record thế nào?
- Q5: Error response nào cần ổn định cho client?

#### 16. Horizontal scaling và load balancing
- Q1: Vì sao session trong memory gây vấn đề khi có nhiều instance?
- Q2: Sticky session giải quyết và không giải quyết điều gì?
- Q3: Vì sao scale app có thể làm database tệ hơn?
- Q4: Những dependency nào cần capacity budget chung?
- Q5: Khi nào cần đo trước khi thêm instance?

#### 17. Caching
- Q1: TTL dài giảm tải nhưng tăng loại rủi ro nào?
- Q2: Cache-aside có race condition nào khi cập nhật dữ liệu?
- Q3: Cache miss đồng thời nhiều request có thể gây gì?
- Q4: Khi nào stale data không chấp nhận được?
- Q5: Cache outage nên làm request fail-open hay fail-closed dựa trên điều gì?

#### 18. Database scaling và lựa chọn consistency
- Q1: Index giúp query nào và gây cost gì cho write/storage?
- Q2: Read replica có thể trả dữ liệu cũ trong tình huống nào?
- Q3: Shard key kém có thể tạo hot partition ra sao?
- Q4: CAP áp dụng trade-off trong điều kiện nào?
- Q5: Availability và durability khác nhau thế nào?

#### 19. Messaging, retry và failure handling
- Q1: Vì sao consumer phải chấp nhận duplicate message?
- Q2: Outbox giải quyết dual-write nào và không giải quyết điều gì?
- Q3: Retry vô hạn có thể gây hậu quả gì?
- Q4: Exactly-once delivery có đồng nghĩa exactly-once business effect không?
- Q5: Vì sao local Java lock không bảo vệ inventory giữa nhiều instance?

#### 20. Reliability, observability và security
- Q1: Vì sao p99 latency có thể quan trọng hơn average?
- Q2: Timeout và circuit breaker giải quyết vấn đề khác nhau nào?
- Q3: Cardinality cao trong metric có thể gây cost gì?
- Q4: Correlation ID giúp điều tra luồng qua service thế nào?
- Q5: Dữ liệu nào không nên ghi vào application log?

## Bài thực hành — không sửa nội dung Q

Mỗi topic giữ B1/B2… riêng cho các bài trong mục `### Bài thực hành` của nguồn. Code bài tập là phần mở rộng mang nhãn `[CODE]`/`[THÍ NGHIỆM]`, nhưng chỉ Q được chép nguyên văn; không thay Q bằng lời nhắc code mới.

- Topics 01–12: chuyển yêu cầu thực hành nguồn thành method/test rõ ràng; các câu Q lý thuyết vẫn là `[TỰ TRẢ LỜI]` hoặc `[DỰ ĐOÁN]`, không đổi câu chữ thành bài code. Brute-force oracle chỉ bổ sung cho bài thực hành input nhỏ.
- Topic 01 B1: so sánh hai cách giải cùng bài toán, input nhỏ/lớn, thời gian/bộ nhớ/complexity và giới hạn áp dụng.
- Topic 02 B1: xử lý chuỗi với contract rõ; test empty, Unicode ngoài BMP, lặp, null và biên độ dài. Code point/grapheme phải phân biệt.
- Topic 03 B1: tần suất bằng collection chuẩn; key bất biến/equality, duplicate, null theo lựa chọn và adversarial input.
- Topic 04 B1: bài pair sorted và sliding window nonnegative; brute force đối chiếu input nhỏ.
- Topic 05 B1: lower bound và answer search đơn điệu; empty, ngoài biên, duplicate, không có đáp án.
- Topic 06 B1: ngoặc cân bằng + BFS grid bằng `ArrayDeque`; empty, sai ngoặc, grid boundary, visited.
- Topic 07 B1: reverse list/Floyd cycle; empty, singleton, cycle đầu/giữa, list dài.
- Topic 08 B1: traversal/validate BST theo duplicate contract đã chọn; empty, singleton, lệch, duplicate, sâu.
- Topic 09 B1: top-K bằng `PriorityQueue`; đối chiếu sort; k=0, k>n, duplicate và int extremes.
- Topic 10 B1: component count + shortest path trên unweighted graph; empty, disconnected, cycle, self-loop, isolate.
- Topic 11 B1: sinh tổ hợp có constraint; empty, duplicate, impossible/multiple solutions; brute force oracle nhỏ.
- Topic 12 B1: brute force trước rồi recurrence/DP; base, impossible, boundary, overflow.
- Topic 13 B1: brief order flow một trang, requirements/SLO/workload/data retention/unknowns.
- Topic 14 B1: dùng chính bộ giả định nguyên văn bài thực hành nguồn; ghi phép tính có đơn vị, read/write, peak, logical/overhead/replica, assumption vs measurement.
- Topic 15 B1: request flow order API, validation/auth/transaction/data owner/duplicate request.
- Topic 16 B1: sơ đồ hai app instance + DB; session, pool/quota, app loss, bottleneck.
- Topic 17 B1: read endpoint cache key/TTL/invalidation/freshness/outage/metrics.
- Topic 18 B1: chọn một query/page size hoặc connection limit của phase04/05 capstone; local disposable dataset/concurrency/time cố định, đổi một yếu tố; đo throughput/p95/error/pool acquisition evidence và giới hạn.
- Topic 19 B1: order + event flow; commit, duplicate, timeout/retry/idempotency/backlog.
- Topic 20 B1: failure-mode table cho dependency chậm, DB unavailable, instance loss; signal, limits, user behavior, recovery.
- DSA assessment giữ nguyên prompt nguồn trong `DSA — Bài kiểm tra tổng hợp`; có thể thêm test harness mới nhưng không sửa câu hỏi/topic register.
- System-design capstone giữ nguyên prompt/requirements nguồn trong `System Design — Capstone`; không thay bằng thiết kế service mới.

## Hợp đồng measurement đã chốt

Topic 18 lab/capstone chỉ dùng phase05 capstone có sẵn. HTTP request bounded GET `/api/products?page=0&size=20`, local JWT scope `products.read`. Learner export metric protected Actuator `/actuator/metrics/hikaricp.connections.acquire` bằng PowerShell; scope `metrics.read`. Phase09 không poll Actuator hoặc thêm JSON dependency.

CSV UTF-8 header chính xác: `run,snapshot,capturedAtUtc,metricName,pool,count,totalTimeSeconds`. Mỗi run (`baseline`, `changed`) có hai snapshots (`start`, `end`), tổng cộng bốn rows. `count` và `totalTimeSeconds` là cumulative snapshots; metric và pool không đổi; pool same giữa hai runs. Timestamp ISO-8601 UTC phải bracket request window; tối đa 5s margin, 300s request window. Harness measured duration default 5s, max 30s, warmup separate. Validate header/row keys/timestamps/metric/pool/count/time monotonicity, finite nonnegative time, integer nonnegative count, positive deltaCount. Mean = deltaTotalTimeSeconds / deltaCount. Hikari acquire timer tính từ lúc gọi `getConnection()` tới lúc connection handle được trả về: acquisition duration gồm pool wait + acquisition overhead; không phải pure queue wait, không gồm lease/usage time. Thiếu/sai/stale/out-of-window telemetry hoặc delta=0 là lỗi bắt buộc: diagnostic + nonzero exit, không skip/pass. HTTP latency và pool metric báo cáo riêng.

Chỉ loopback host (`localhost`, `127.0.0.1`, `::1`); reject non-HTTP, URL user-info, DNS/IP không loopback và redirect ra ngoài loopback. Không log token/request body/response payload. Bounded request count/concurrency/duration/timeout. Chỉ đổi một setting mỗi so sánh; data disposable.

## Module constraints và nghiệm thu

- JDK 21, JUnit 5 theo parent; no Spring/DB dependency for DSA.
- Source bài giải có `SOLUTION-BEGIN throw Qn/Bn`/`SOLUTION-VALUE`; answer dùng `ANSWER Qn`/`OBSERVATION Qn`, strip Java only.
- Mỗi câu nguyên văn xuất hiện đúng một lần trong Javadoc bài tương ứng. Coverage audit scoped theo topic: năm câu Q1–Q5, không global Q numbering. Mỗi B task tách riêng khỏi Q.
- DSA tests deterministic, seeded small oracle, không timing assertions; invalid input contracts gọi method ngoài `assertThrows`.
- System design prose review thủ công. Topic 14 lab dùng đúng assumptions nguồn; không sửa câu Q hay invent input.
- DSA assessment và design capstone độc lập. Full module tests không cần Docker/service. Required manual measurement missing/malformed data phải fail rõ.

## Frozen implementation map

Mỗi curriculum topic có đúng một `Ex01_<Topic>.java` và một matching `Ex01_<Topic>Test.java`, trong package `phase09.dNN_<topic>`; method contracts và executable TDD snippet nằm trong implementation plan. Topic 18 uses `d18_database_scaling`. Assessment deliverables riêng `d21_dsa_assessment/Ex01_Assessment.java` và `d22_design_capstone/Ex01_DesignCapstone.java`, không phải curriculum topics.

Question category freeze: source questions remain exact text. Qs are `[TỰ TRẢ LỜI]` by default; only deterministic behavior-check question can carry `[DỰ ĐOÁN]`, never rewrite as `[CODE]`. Code exercise contracts belong to B1 labs, separate from Q. Design answers are never graded by string assertions.
