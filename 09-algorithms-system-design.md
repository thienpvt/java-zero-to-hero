# Giai đoạn 9 — Algorithms & System Design

## Mục tiêu

Học song song hai năng lực có liên quan nhưng không phụ thuộc hoàn toàn vào nhau:

- DSA: chọn cấu trúc dữ liệu và thuật toán phù hợp, chứng minh tính đúng và phân tích chi phí.
- System Design: biến yêu cầu thành kiến trúc có ranh giới, ước lượng tải và giải thích trade-off.

Không đợi học xong DSA mới tiếp tục làm backend. Có thể học DSA theo lịch luyện tập. Bắt đầu System Design cơ bản sau khi có nền tảng database và Spring ở giai đoạn 4/5; học sâu messaging/failure handling sau distributed systems ở giai đoạn 7 trong [roadmap](java-advanced-roadmap.md).

**Môi trường:** JDK 21; dùng Maven/Gradle và JUnit theo cấu hình project hiện có. Ưu tiên collection và API chuẩn của Java trong ứng dụng. Chỉ tự cài đặt cấu trúc dữ liệu khi mục tiêu là hiểu thuật toán.

**Cách học:** với mỗi thuật toán, nêu invariant, điều kiện áp dụng, trường hợp biên, độ phức tạp worst-case và test bằng ví dụ nhỏ. Với mỗi thiết kế, ghi requirement, giả định, số liệu có đơn vị, luồng dữ liệu, failure mode và lý do chọn trade-off.

---

## Track A — DSA

## 1. Độ phức tạp và cách kiểm chứng

### Kiến thức cần nắm

- Big O mô tả tăng trưởng theo kích thước input; phân tích thời gian và bộ nhớ riêng.
- Phân biệt worst-case, amortized và expected complexity; trung bình không đồng nghĩa với worst-case.
- Đếm thao tác chi phối, không đoán từ tên thuật toán.
- Hash table thường có lookup expected O(1), nhưng worst-case có thể O(n).
- Kiểm tra giới hạn dữ liệu trước khi chọn thuật toán; benchmark bổ sung phân tích, không thay thế chứng minh. Benchmark Java cần warmup để JIT ổn định; không dùng ngưỡng thời gian tuyệt đối làm tiêu chí đúng/sai.

### Câu hỏi kiểm tra

1. Vì sao hai vòng lặp lồng nhau không phải lúc nào cũng O(n²)?
2. Amortized complexity khác expected complexity thế nào?
3. HashMap có bảo đảm lookup worst-case O(1) không?
4. O(n log n) tăng thế nào so với O(n²) khi n lớn?
5. Khi nào benchmark không đủ để chứng minh thuật toán phù hợp?

### Bài thực hành

So sánh hai cách giải cùng bài toán với input nhỏ và lớn. Ghi thời gian, bộ nhớ, phân tích complexity và giới hạn mà mỗi cách còn dùng được.

---

## 2. Array và String

### Kiến thức cần nắm

- Array truy cập theo index O(1); chèn/xóa giữa thường cần dịch phần tử O(n).
- Phân biệt độ dài, capacity và kích thước input; chú ý phép tính có thể overflow `int`.
- Java `String` là chuỗi UTF-16: `char` là code unit, không luôn tương ứng một Unicode code point. Chọn xử lý `char`, code point hay grapheme theo yêu cầu bài toán.
- String immutable; nối chuỗi lặp trong vòng lặp có thể tạo nhiều object trung gian. Dùng `StringBuilder` khi cần dựng chuỗi nhiều lần.
- Xác định rõ null, chuỗi rỗng, ký tự ngoài ASCII và quy tắc chuẩn hóa nếu so sánh văn bản.

### Câu hỏi kiểm tra

1. Truy cập array theo index và chèn vào đầu có chi phí thế nào?
2. Vì sao `String.length()` không phải lúc nào cũng là số ký tự người dùng nhìn thấy?
3. Khi nào dùng code point thay vì `char`?
4. Cộng hai index hoặc tính `n * n` có thể sai thế nào khi vượt giới hạn `int`?
5. Chuỗi rỗng và null có cùng ý nghĩa không?

### Bài thực hành

Viết hàm xử lý chuỗi theo một quy tắc rõ ràng. Test chuỗi rỗng, Unicode ngoài BMP, ký tự lặp, null theo contract và độ dài sát giới hạn đầu vào.

---

## 3. Hashing và Hash Table

### Kiến thức cần nắm

- Hash table ánh xạ key qua hash để tìm bucket; collision vẫn có thể xảy ra.
- Trong Java, `equals` và `hashCode` phải nhất quán khi dùng key trong `HashMap`/`HashSet`.
- Key mutable có thể làm lookup hỏng sau khi thay đổi field ảnh hưởng equality/hash.
- Chọn `HashMap`, `HashSet` hoặc cấu trúc có thứ tự theo nhu cầu; không dựa vào thứ tự iteration không được bảo đảm.
- Hash không phải encryption; không dùng hash thường để bảo vệ bí mật hoặc chứng minh hai object chắc chắn bằng nhau.

### Câu hỏi kiểm tra

1. Hai object `equals()` nhau phải thỏa điều kiện gì về `hashCode()`?
2. Điều gì xảy ra nếu key thay đổi sau khi đưa vào `HashMap`?
3. Collision có làm sai kết quả hay chủ yếu ảnh hưởng hiệu năng?
4. Khi nào chọn `TreeMap` thay vì `HashMap`?
5. Vì sao hash không chứng minh được hai input giống nhau?

### Bài thực hành

Đếm tần suất phần tử bằng collection chuẩn. Tạo key bất biến có equality đúng; kiểm tra phần tử trùng, null theo lựa chọn collection và dữ liệu adversarial phù hợp.

---

## 4. Two Pointers và Sliding Window

### Kiến thức cần nắm

- Two pointers hữu ích khi có invariant cho hai vị trí, thường trên dữ liệu đã sắp xếp hoặc chuỗi/array có cấu trúc phù hợp.
- Sliding window áp dụng khi cửa sổ có thể mở rộng/thu hẹp theo điều kiện có tính đơn điệu thích hợp.
- Không áp dụng máy móc sliding window cho mọi bài toán tổng đoạn con; số âm có thể phá điều kiện co cửa sổ tham lam.
- Nêu invariant sau mỗi lần di chuyển pointer và chứng minh mỗi phần tử được xử lý hữu hạn lần.
- Xử lý cửa sổ rỗng, biên đầu/cuối và phép tính tổng không overflow.

### Câu hỏi kiểm tra

1. Điều kiện nào giúp sliding window duy trì được kết quả khi co/mở cửa sổ?
2. Vì sao tổng đoạn con với số âm có thể không dùng được cách co cửa sổ thông thường?
3. Trong two pointers trên array đã sort, vì sao có thể di chuyển một pointer mà không bỏ sót nghiệm?
4. Invariant của cửa sổ là gì?
5. Cần kiểm tra trường hợp input rỗng nào?

### Bài thực hành

Giải một bài tìm cặp trên array đã sort và một bài cửa sổ con với dữ liệu không âm. Viết thêm brute force cho input nhỏ để đối chiếu.

---

## 5. Binary Search

### Kiến thức cần nắm

- Binary search cần miền tìm kiếm có thứ tự hoặc predicate đơn điệu; xác định precondition trước khi code.
- Phân biệt tìm phần tử chính xác, lower bound/upper bound và tìm đáp án nhỏ nhất/lớn nhất thỏa điều kiện.
- Viết rõ invariant của `low`/`high`, quy ước inclusive/exclusive và điều kiện dừng để tránh lặp vô hạn.
- Tính midpoint tránh overflow; kiểm tra boundary khi không có kết quả hoặc có nhiều phần tử trùng.
- Binary search trên đáp án chỉ hợp lệ khi predicate thật sự đơn điệu.

### Câu hỏi kiểm tra

1. Dữ liệu hoặc predicate cần tính chất gì để binary search đúng?
2. Khác biệt giữa tìm một occurrence và lower bound là gì?
3. Vì sao cập nhật biên sai một đơn vị có thể gây infinite loop?
4. Khi nào binary search trên đáp án không hợp lệ?
5. Cần trả về gì nếu không có giá trị thỏa predicate?

### Bài thực hành

Cài lower bound và tìm đáp án theo predicate đơn điệu. Test array rỗng, giá trị ngoài hai đầu, duplicate và không có đáp án.

---

## 6. Stack, Queue và Deque

### Kiến thức cần nắm

- Stack là LIFO; queue là FIFO; deque hỗ trợ thao tác hai đầu.
- Ưu tiên `ArrayDeque` cho stack/queue trong Java khi phù hợp; tránh lớp `Stack` cũ cho code mới.
- Dùng stack cho ngoặc cân bằng, monotonic stack hoặc mô phỏng call stack; queue cho BFS và xử lý theo thứ tự đến.
- Queue/deque có thể từ chối null; chọn rõ behavior khi rỗng, capacity đầy hoặc input không hợp lệ.
- Độ phức tạp phụ thuộc cấu trúc; không giả định mọi thao tác giữa collection đều O(1).

### Câu hỏi kiểm tra

1. Bài toán nào tự nhiên dùng LIFO, bài toán nào dùng FIFO?
2. Vì sao `ArrayDeque` thường phù hợp hơn `Stack`?
3. Monotonic stack duy trì invariant gì?
4. BFS cần queue để bảo đảm đặc tính nào?
5. Hành vi khi deque rỗng cần được thiết kế thế nào?

### Bài thực hành

Dùng `ArrayDeque` giải ngoặc cân bằng và BFS trên lưới. Test input rỗng, ngoặc sai, biên lưới và phần tử đã thăm.

---

## 7. Linked List

### Kiến thức cần nắm

- Singly linked list cần traversal O(n); truy cập index không phải O(1).
- Chèn/xóa tại node đã có reference có thể O(1); với singly linked list, xóa node thường cần reference tới predecessor, node đơn lẻ không luôn đủ. Tìm node/predecessor vẫn O(n).
- Nắm dummy/sentinel node, đảo danh sách, fast/slow pointers và phát hiện cycle.
- Theo dõi tham chiếu `next` khi chỉnh sửa để tránh mất phần còn lại hoặc tạo cycle ngoài ý muốn.
- Trong ứng dụng Java, thường ưu tiên collection chuẩn hoặc array-backed structure; tự cài linked list chủ yếu để học hoặc khi có yêu cầu cụ thể.

### Câu hỏi kiểm tra

1. Chèn O(1) trong linked list có bao gồm chi phí tìm vị trí không?
2. Fast/slow pointer phát hiện cycle dựa trên lý do gì?
3. Dummy node giảm trường hợp biên nào?
4. Vì sao linked list thường kém cache locality hơn array?
5. Khi nào dùng collection chuẩn thay vì tự cài linked list?

### Bài thực hành

Đảo singly linked list và phát hiện cycle. Test list rỗng, một node, cycle ở đầu/giữa và list dài.

---

## 8. Tree và Binary Search Tree

### Kiến thức cần nắm

- Phân biệt binary tree, binary search tree (BST), balanced tree và heap; chúng không đồng nghĩa.
- BST chỉ có hiệu năng O(log n) nếu chiều cao được kiểm soát; cây lệch có thể thành O(n).
- Hiểu preorder, inorder, postorder và level-order; xác định traversal phù hợp với kết quả cần tạo.
- Đệ quy đơn giản nhưng chiều sâu lớn có thể gây `StackOverflowError`; iterative traversal dùng explicit stack/queue.
- Dùng `TreeMap`/`TreeSet` khi cần thứ tự có sẵn; không tự cài cây cân bằng nếu mục tiêu chỉ là ứng dụng.

### Câu hỏi kiểm tra

1. Inorder traversal của BST có đặc tính gì?
2. Tại sao BST thường không đảm bảo O(log n)?
3. Cây lệch có chiều cao và chi phí tìm kiếm thế nào?
4. Khi nào traversal iterative an toàn hơn recursive?
5. Heap có thỏa tính chất BST không?

### Bài thực hành

Viết traversal và kiểm tra BST theo contract đã chọn cho duplicate. Test cây rỗng, một node, cây lệch, duplicate và cây sâu.

---

## 9. Heap và Top-K

### Kiến thức cần nắm

- Heap giữ min hoặc max ở root; không giữ toàn bộ phần tử theo thứ tự.
- `PriorityQueue` của Java là min-heap mặc định; dùng `Comparator` để định nghĩa ưu tiên rõ ràng.
- Top-K có thể giải bằng sort O(n log n) hoặc heap O(n log k); chọn theo kích thước k và nhu cầu.
- Comparator phải nhất quán, tránh `a - b` vì phép trừ có thể overflow; dùng so sánh an toàn.
- Khi phần tử bằng nhau, xác định tie-break nếu output phải deterministic.

### Câu hỏi kiểm tra

1. Root của `PriorityQueue` mặc định là phần tử nào?
2. Vì sao heap không thể trả lời mọi truy vấn thứ tự như sorted array?
3. Khi nào heap kích thước k tốt hơn sort toàn bộ?
4. Comparator dùng phép trừ có nguy cơ gì?
5. Cần quy định thế nào khi nhiều phần tử đồng hạng ở top-K?

### Bài thực hành

Tìm top-K phần tử bằng `PriorityQueue`. Đối chiếu sort với input nhỏ; test k=0, k lớn hơn n, duplicate và số nguyên cực trị.

---

## 10. Graph: BFS, DFS và đường đi

### Kiến thức cần nắm

- Chọn adjacency list hoặc matrix theo mật độ graph và thao tác cần thiết.
- BFS tìm đường đi ngắn nhất theo số cạnh trong graph không trọng số; DFS không bảo đảm đường đi ngắn nhất.
- Duyệt đủ mọi component khi graph không liên thông; đánh dấu visited đúng thời điểm để không enqueue lặp vô hạn.
- Phân biệt directed/undirected, cycle, self-loop và parallel edge; thuật toán phải khớp loại graph.
- Đường đi ngắn nhất có trọng số cần thuật toán phù hợp như Dijkstra với trọng số không âm; BFS đơn thuần không đủ.
- Tránh recursion quá sâu cho graph lớn; stack/queue tường minh kiểm soát bộ nhớ tốt hơn.

### Câu hỏi kiểm tra

1. Vì sao BFS cho shortest path trên graph không trọng số?
2. BFS có giải đúng shortest path với trọng số khác nhau không?
3. Làm sao phát hiện cycle trong directed graph khác với undirected graph?
4. Vì sao cần khởi động traversal từ mọi node chưa thăm?
5. Dijkstra cần điều kiện gì về trọng số?

### Bài thực hành

Tìm số component và đường đi ngắn nhất trên graph không trọng số. Test graph rỗng, disconnected, cycle, self-loop và node không có cạnh.

---

## 11. Backtracking

### Kiến thức cần nắm

- Backtracking xây candidate từng bước, kiểm tra constraint, rồi hoàn tác trạng thái.
- Mô tả state, lựa chọn, điều kiện dừng và nhánh có thể prune.
- Worst-case thường tăng theo số lựa chọn và độ sâu; pruning cải thiện thực tế nhưng không mặc định đổi worst-case.
- Chú ý duplicate đầu vào để tránh sinh kết quả trùng; mutation state phải được hoàn tác cả khi nhánh kết thúc.
- Tách rõ tìm một nghiệm, đếm nghiệm và liệt kê mọi nghiệm vì chi phí output khác nhau.

### Câu hỏi kiểm tra

1. State của backtracking cần chứa thông tin gì?
2. Pruning bảo toàn tính đúng ra sao?
3. Vì sao phải hoàn tác state sau mỗi nhánh?
4. Duplicate đầu vào có thể tạo output trùng thế nào?
5. Liệt kê mọi nghiệm có thể tránh chi phí phụ thuộc output không?

### Bài thực hành

Giải một bài sinh tổ hợp có constraint. Test input rỗng, duplicate, không có nghiệm và nhiều nghiệm; so kết quả với brute force ở kích thước nhỏ.

---

## 12. Dynamic Programming

### Kiến thức cần nắm

- DP phù hợp khi bài toán có overlapping subproblems và optimal substructure theo mô hình đã chứng minh.
- Định nghĩa state bằng ngôn ngữ bài toán; viết transition, base case, thứ tự tính và kết quả cần trả về.
- Memoization và tabulation có trade-off về call stack, bộ nhớ và khả năng tối ưu không gian.
- Tối ưu bộ nhớ chỉ khi transition cho phép; ghi rõ thứ tự iteration trước khi bỏ chiều state.
- Kiểm tra overflow, unreachable state và sentinel; không cộng vào giá trị sentinel một cách mù quáng.

### Câu hỏi kiểm tra

1. State biểu diễn điều gì và làm sao biết state đủ thông tin?
2. Base case sai thường tạo lỗi kiểu nào?
3. Khi memoization tốt hơn tabulation, hoặc ngược lại?
4. Điều kiện nào cho phép giảm bộ nhớ từ O(n²) xuống O(n)?
5. Làm sao tránh overflow khi cộng chi phí hoặc điểm số?

### Bài thực hành

Giải bài tối ưu hóa nhỏ bằng brute force trước, sau đó xây recurrence và DP. So sánh kết quả trên input nhỏ và test base case, impossible state, boundary, overflow.

---

## DSA — Bài kiểm tra tổng hợp

Chọn bài toán chưa luyện thuộc khu vực arrays/strings, graph hoặc DP. Trình bày trước khi code:

- Input constraints, output contract và trường hợp biên.
- Cấu trúc dữ liệu/thuật toán, invariant và lý do chọn.
- Complexity worst-case, amortized hoặc expected phù hợp.
- Một cách brute force cho input nhỏ làm oracle đối chiếu.

Sau khi code, test empty, duplicate, malformed hoặc out-of-range theo contract, cycle/deep recursion nếu liên quan. Đánh giá theo tính đúng, lập luận và test chất lượng; không lấy số lượng bài trên nền tảng làm tiêu chí hoàn thành.

---

## Track B — System Design

## 13. Requirements, scope và SLO

### Kiến thức cần nắm

- Tách functional requirements khỏi non-functional requirements; xác định actor, use case và boundary.
- Chốt scope, traffic assumptions, latency, availability, durability, consistency, privacy và retention trước khi vẽ kiến trúc.
- SLO mô tả mục tiêu dịch vụ đo được; phân biệt SLI, SLO và SLA.
- Ưu tiên modular monolith làm baseline cho ứng dụng chưa chứng minh nhu cầu tách service.
- Quy mô không phải mục tiêu tự thân; tránh dựng kiến trúc “internet scale” khi workload chưa yêu cầu.

### Câu hỏi kiểm tra

1. Functional requirement khác non-functional requirement thế nào?
2. SLI, SLO và SLA khác nhau thế nào?
3. Vì sao cần giới hạn scope trước khi chọn công nghệ?
4. Khi nào modular monolith là lựa chọn hợp lý?
5. Requirement nào ảnh hưởng trực tiếp đến consistency hoặc durability?

### Bài thực hành

Viết brief một trang cho luồng order: use case, tải dự kiến, latency/availability mục tiêu, dữ liệu cần giữ và ba điều chưa biết cần xác nhận.

---

## 14. Ước lượng tải và dung lượng

### Kiến thức cần nắm

- Ước lượng request/second trung bình và peak; ghi rõ ngày/giây, tỷ lệ đọc/ghi và giả định.
- Tính dung lượng từ số record, bytes/record, retention và overhead; quy đổi đơn vị nhất quán.
- Ước lượng network throughput và fan-out; request trung bình có thể che mất burst.
- Ghi ra phép tính và khoảng sai số; không trình bày ước lượng như số đo production.
- Dùng số liệu để xác định bottleneck giả định và câu hỏi cần đo tiếp, không để con số một mình quyết định kiến trúc.

### Câu hỏi kiểm tra

1. Vì sao peak RPS quan trọng hơn trung bình trong sizing?
2. Cần đưa yếu tố nào vào ước lượng storage theo thời gian?
3. Làm sao phân biệt giả định với số đo?
4. Read/write ratio ảnh hưởng lựa chọn nào?
5. Ước lượng nào nên xác minh bằng load test hoặc production metrics?

### Bài thực hành

Dùng bộ giả định cho bài tập, không phải số đo production: 1,000,000 orders/day; mỗi order tạo 10 read requests; tính thêm 1 write request/order; peak factor 10 áp dụng lên tổng API requests trung bình; mỗi order cùng items chiếm 2 KiB logical; retention 365 ngày; overhead multiplier 2; replication copies 3. Ghi công thức RPS trung bình/peak (đếm rõ read và write), bytes logical/năm và bytes sau overhead/replication. Nêu giả định nào cần xác minh và không trình bày kết quả như benchmark hay số liệu quan sát.

---

## 15. API, data ownership và invariant

### Kiến thức cần nắm

- Thiết kế API theo use case, validation, authorization, idempotency và error contract.
- Xác định owner của dữ liệu và invariant cần giữ trong transaction nào.
- Mô hình nền cho order có thể gồm customers, products, orders và order_items; điều chỉnh theo requirement, không giả định schema cụ thể sẵn có.
- Tách API contract khỏi persistence model khi thay đổi schema không nên làm vỡ client.
- Phân biệt business invariant với kiểm tra hình thức; không dựa riêng vào UI để bảo vệ dữ liệu.

### Câu hỏi kiểm tra

1. Idempotency cần thiết ở endpoint nào và vì sao?
2. Ai chịu trách nhiệm giữ invariant của order và inventory?
3. Khi nào API DTO nên độc lập persistence entity?
4. Authorization nên kiểm tra quyền truy cập record thế nào?
5. Error response nào cần ổn định cho client?

### Bài thực hành

Vẽ request flow tạo order từ API tới database. Đánh dấu validation, quyền truy cập, transaction boundary, owner của mỗi dữ liệu và phản hồi khi duplicate request.

---

## 16. Horizontal scaling và load balancing

### Kiến thức cần nắm

- Load balancer phân phối request; horizontal scaling thêm instance nhưng không tự làm ứng dụng stateless.
- Instance stateless giữ state bền vững ở nơi dùng chung phù hợp; session affinity che giấu stateful coupling chứ không loại bỏ nó.
- Xác định thành phần có thể trở thành bottleneck: DB, connection pool, downstream, hot key hoặc bandwidth.
- Capacity planning phải tính giới hạn mỗi instance và tổng quota downstream; thêm instance có thể làm DB quá tải.
- Phân biệt khả năng scale ứng dụng với khả năng scale từng dependency.

### Câu hỏi kiểm tra

1. Vì sao session trong memory gây vấn đề khi có nhiều instance?
2. Sticky session giải quyết và không giải quyết điều gì?
3. Vì sao scale app có thể làm database tệ hơn?
4. Những dependency nào cần capacity budget chung?
5. Khi nào cần đo trước khi thêm instance?

### Bài thực hành

Vẽ service chạy hai instance và một database. Theo dõi session, connection pool và quota khi một instance mất; nêu bottleneck đầu tiên cần đo.

---

## 17. Caching

### Kiến thức cần nắm

- Cache giảm latency/tải nhưng thêm stale data, invalidation, memory cost và failure mode.
- Chọn cache key, TTL và consistency kỳ vọng; nêu dữ liệu nào chấp nhận stale.
- Cache-aside là một mẫu phổ biến, không bảo đảm không có race giữa cập nhật DB và invalidation.
- Tránh cache stampede, hot key và unbounded cardinality; đặt giới hạn bộ nhớ và cách xử lý cache outage.
- Đo hit rate, latency và backend load; cache không hiệu quả nếu lookup không phải bottleneck.

### Câu hỏi kiểm tra

1. TTL dài giảm tải nhưng tăng loại rủi ro nào?
2. Cache-aside có race condition nào khi cập nhật dữ liệu?
3. Cache miss đồng thời nhiều request có thể gây gì?
4. Khi nào stale data không chấp nhận được?
5. Cache outage nên làm request fail-open hay fail-closed dựa trên điều gì?

### Bài thực hành

Chọn một read endpoint có thể cache. Ghi key, TTL, invalidation, freshness contract, outage behavior và metric để xác minh cache có ích.

---

## 18. Database scaling và lựa chọn consistency

### Kiến thức cần nắm

- Tối ưu query/index và transaction trước khi thêm tầng phân tán; kiểm tra query plan và workload thực tế.
- Read replica có thể tăng read capacity nhưng có replication lag; không mặc định read-after-write từ replica là nhất quán.
- Sharding phân vùng dữ liệu, tăng độ phức tạp query, migration và hot partition; chọn shard key theo access pattern.
- Replication, consistency, availability và durability là thuộc tính khác nhau. Synchronous replication thường chờ replica xác nhận, tăng commit latency nhưng giảm nguy cơ mất write đã xác nhận khi failover; asynchronous replication giảm latency nhưng có thể mất các write chưa replicate khi primary hỏng.
- CAP nói về trade-off khi xảy ra network partition: với operation cần linearizability, hệ thống phải từ chối/chặn một phía hoặc trả lỗi để giữ consistency, thay vì luôn phục vụ mọi request như thể dữ liệu mới nhất có sẵn. Không phải quy tắc “chọn hai trong ba” mọi lúc.
- Quyết định consistency theo invariant nghiệp vụ: inventory/order không giống feed/cache.

### Câu hỏi kiểm tra

1. Index giúp query nào và gây cost gì cho write/storage?
2. Read replica có thể trả dữ liệu cũ trong tình huống nào?
3. Shard key kém có thể tạo hot partition ra sao?
4. CAP áp dụng trade-off trong điều kiện nào?
5. Availability và durability khác nhau thế nào?

### Bài thực hành

Chọn query/page size hoặc connection limit trên service hiện có từ giai đoạn 4/5, rồi chạy một thí nghiệm local bounded: cùng dataset cố định, cùng concurrency và cùng thời gian đo ngắn; thay đổi đúng một yếu tố có lý do và so sánh before/after. Ghi throughput, p95 latency, error rate và connection-pool wait; ghi rõ cấu hình, môi trường, độ lệch và giới hạn phép đo. Dùng database/dữ liệu local disposable, không cần distributed infrastructure. Không coi thay đổi có lợi nếu error rate hoặc pool wait xấu đi mà không giải thích được.

---

## 19. Messaging, retry và failure handling

### Kiến thức cần nắm

- Queue/broker tách producer/consumer theo thời gian nhưng thêm delivery semantics, ordering, backlog và vận hành.
- Transactional outbox nối commit DB với phát sự kiện mà tránh dual-write đơn giản; consumer vẫn phải xử lý duplicate.
- Idempotency cần key, phạm vi lưu và thời hạn phù hợp; không giả định exactly-once side effect xuyên DB, broker và dịch vụ ngoài.
- Timeout phải nằm trong budget; retry chỉ lỗi retryable, có giới hạn, backoff và jitter để tránh khuếch đại tải.
- Đặt bounded queue, rate limit và backpressure; theo dõi backlog, tuổi message và dead-letter flow.
- Payment provider và local DB không nằm trong một transaction ACID chung; mô hình trạng thái trung gian/compensation thay vì hứa rollback toàn hệ thống.

### Câu hỏi kiểm tra

1. Vì sao consumer phải chấp nhận duplicate message?
2. Outbox giải quyết dual-write nào và không giải quyết điều gì?
3. Retry vô hạn có thể gây hậu quả gì?
4. Exactly-once delivery có đồng nghĩa exactly-once business effect không?
5. Vì sao local Java lock không bảo vệ inventory giữa nhiều instance?

### Bài thực hành

Vẽ luồng tạo order và phát event. Đánh dấu commit point, duplicate, timeout, retry policy, idempotency boundary và cách phát hiện message backlog.

---

## 20. Reliability, observability và security

### Kiến thức cần nắm

- Đặt timeout và concurrency limit cho dependency; failure cần bị cô lập thay vì giữ cạn thread/connection pool.
- Retry có budget và jitter; circuit breaker không thay thế timeout, giới hạn tải hoặc xử lý lỗi nghiệp vụ.
- Log, metric và trace trả lời câu hỏi khác nhau; dùng correlation/trace ID, tránh log secret hoặc dữ liệu cá nhân không cần thiết.
- Theo dõi latency percentile, error rate, saturation và throughput; tổng hợp metric theo cardinality có giới hạn.
- Kiểm tra authn/authz, input validation, least privilege, secret handling, rate limit và audit requirement.
- Failure test kiểm tra mất instance, chậm dependency, DB unavailable và backlog; phân biệt mô hình giả định với hành vi đã đo trong triển khai.

### Câu hỏi kiểm tra

1. Vì sao p99 latency có thể quan trọng hơn average?
2. Timeout và circuit breaker giải quyết vấn đề khác nhau nào?
3. Cardinality cao trong metric có thể gây cost gì?
4. Correlation ID giúp điều tra luồng qua service thế nào?
5. Dữ liệu nào không nên ghi vào application log?

### Bài thực hành

Lập bảng failure mode gồm dependency chậm, DB unavailable và mất một instance. Với mỗi tình huống, nêu signal quan sát được, giới hạn tải, hành vi người dùng và recovery.

---

## System Design — Capstone

Phát triển thiết kế cho service đã xây ở các giai đoạn trước; không cần triển khai lại distributed system chỉ để chứng minh sơ đồ. Capstone bao gồm một thí nghiệm local bounded như bài thực hành mục 18: dataset và concurrency cố định, một thay đổi có lý do, đo before/after throughput, p95, error rate và pool wait; mọi dữ liệu/database dùng phải disposable. Tách rõ giả định thiết kế khỏi số đo thí nghiệm.

Nộp sơ đồ request/data flow và ghi chú ngắn gồm:

- Requirement, SLO và giả định tải có đơn vị; phân biệt ước lượng với số đo.
- API, data ownership, transaction boundary và invariant order/inventory.
- Lý do chọn modular monolith hay thành phần bổ sung; nêu rõ non-goals.
- Cách giữ đúng tồn kho khi có nhiều request/instance; không coi Java lock trong process là distributed lock.
- Ít nhất ba failure scenario, timeout/retry/idempotency behavior và tín hiệu quan sát.
- Security boundary, dữ liệu nhạy cảm, retention và access control.
- Trade-off được chọn, lựa chọn bị loại và bằng chứng cần thu thập trước khi đổi kiến trúc.
- Kết quả thí nghiệm local: setup, dataset, concurrency, thay đổi duy nhất, throughput/p95/error rate/pool wait trước và sau; ghi rõ đây là phép đo cục bộ, không phải SLO production.

## Exit Criteria

Có thể hoàn thành giai đoạn khi:

- Chọn và giải thích array/string, hashing, stack/queue, tree/heap, graph, backtracking và DP theo constraint.
- Nêu precondition, invariant, trường hợp biên và complexity đúng loại; kiểm chứng thuật toán bằng test và brute-force oracle nhỏ.
- Đọc được dấu hiệu Unicode, overflow, duplicate, cycle và deep recursion có thể làm sai giải pháp.
- Chuyển requirement thành API/data flow có boundary, SLO, giả định và ước lượng tải ghi rõ đơn vị.
- Giải thích cache, replica, shard, messaging, consistency và retry qua failure mode cụ thể, không khẳng định exactly-once magic.
- Bảo vệ invariant nghiệp vụ qua transaction boundary và nhiều instance; phân biệt thiết kế đề xuất với điều đã đo.
- Trình bày trade-off và non-goals; không xem số lượng bài tập hoặc sơ đồ kiến trúc phức tạp là bằng chứng năng lực.

## Tài liệu chuẩn

- [Java 21 Collections API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/package-summary.html)
- [Princeton Algorithms](https://algs4.cs.princeton.edu/home/)
- [Google SRE Book — Service Level Objectives](https://sre.google/sre-book/service-level-objectives/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/current/)
- [RFC 9110 — HTTP Semantics](https://www.rfc-editor.org/rfc/rfc9110.html)
