# Giai đoạn 3 — Clean Code và Design Patterns

Module `phase-03-clean-code-design-patterns` chứa bài thực hành cho [20 mục trong giáo trình](../03-clean-code-design-patterns.md) và một bài kiến trúc tích hợp. Mỗi package `phase03.dNN_*` có source `ExNN_*.java` và test JUnit cùng tên; bài tích hợp dùng `d21_capstone`.

## Cách học

1. Mở nhánh `exercises` và tạo nhánh làm việc riêng; đọc Javadoc `Qn` hoặc `Bn` trong source.
2. Giữ thứ tự câu hỏi. `[DỰ ĐOÁN]`: điền hằng `null` trước khi chạy. `[CODE]`: hoàn thiện method được đánh dấu TODO. `[THÍ NGHIỆM]`: chạy và ghi quan sát. `[TỰ TRẢ LỜI]`: viết câu trả lời trong khối `ANSWER`.
3. Chạy test gần nhất bằng IntelliJ hoặc Maven. Trên `exercises`, test đỏ với `TODO Qn`/`TODO Bn` hoặc `thay null` là điểm bắt đầu, không phải lỗi build.
4. Khi cần đối chiếu lời giải: `git diff exercises solutions -- phase-03-clean-code-design-patterns/src/main/java/phase03/d10_strategy/Ex01_DiscountPolicy.java`.

Cần JDK 21. Tại gốc repo:

```powershell
.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d10_strategy/*Test"
.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test
```

Trên `solutions`, module xanh toàn bộ. Trên `exercises`, test bài tập cố ý đỏ. Các phase trước trên `exercises` cũng có TODO nên test toàn reactor ở nhánh đó không phải kiểm chứng bản lời giải.

## Checklist

| Xong | Package | Chủ đề |
|:----:|---------|--------|
| [ ] | [d01_srp](src/main/java/phase03/d01_srp/) | Single Responsibility |
| [ ] | [d02_ocp](src/main/java/phase03/d02_ocp/) | Open/Closed |
| [ ] | [d03_lsp](src/main/java/phase03/d03_lsp/) | Liskov Substitution |
| [ ] | [d04_isp](src/main/java/phase03/d04_isp/) | Interface Segregation |
| [ ] | [d05_dip](src/main/java/phase03/d05_dip/) | Dependency Inversion |
| [ ] | [d06_di](src/main/java/phase03/d06_di/) | Dependency Injection |
| [ ] | [d07_composition](src/main/java/phase03/d07_composition/) | Composition vs Inheritance |
| [ ] | [d08_concerns](src/main/java/phase03/d08_concerns/) | Separation of Concerns |
| [ ] | [d09_layers](src/main/java/phase03/d09_layers/) | Layered Architecture |
| [ ] | [d10_strategy](src/main/java/phase03/d10_strategy/) | Strategy |
| [ ] | [d11_factory](src/main/java/phase03/d11_factory/) | Factory |
| [ ] | [d12_builder](src/main/java/phase03/d12_builder/) | Builder |
| [ ] | [d13_adapter](src/main/java/phase03/d13_adapter/) | Adapter |
| [ ] | [d14_decorator](src/main/java/phase03/d14_decorator/) | Decorator |
| [ ] | [d15_observer](src/main/java/phase03/d15_observer/) | Observer |
| [ ] | [d16_template_method](src/main/java/phase03/d16_template_method/) | Template Method |
| [ ] | [d17_proxy](src/main/java/phase03/d17_proxy/) | Proxy |
| [ ] | [d18_singleton](src/main/java/phase03/d18_singleton/) | Singleton |
| [ ] | [d19_clean_code](src/main/java/phase03/d19_clean_code/) | Clean Code Fundamentals |
| [ ] | [d20_testability](src/main/java/phase03/d20_testability/) | Testability |
| [ ] | [d21_capstone](src/main/java/phase03/d21_capstone/) | Order-service architecture |

Gợi ý: học `d19_clean_code` và `d20_testability` trước, rồi `d01`–`d09`, `d10`–`d18` và cuối cùng `d21_capstone`.

## Boundary và giao dịch

```text
Caller → OrderApplicationService → PricingPolicy (logic thuần)
                                 → PaymentGateway (hệ thanh toán ngoài)
                                 → OrderRepository (điểm commit đơn cục bộ)
                                 → EventPublisher (hệ sự kiện ngoài)
```

`CoupledOrderServiceTest` ghi lại tổng tiền, charge, lưu đơn và event trước refactor; `CheckoutTest` kiểm cùng hành vi và lỗi sau refactor. Charge thành công nhưng `save` báo lỗi trả `SAVE_OUTCOME_UNKNOWN` cùng payment reference: repository có thể đã commit trước khi response mất. Publish báo lỗi giữ đơn đã lưu và trả `PUBLISH_OUTCOME_UNKNOWN`: event có thể đã đến người nhận. Không retry mù toàn bộ checkout. Retry charge về sau cần idempotency key; publish tin cậy cần outbox ghi event cùng transaction với đơn rồi phát lại có xử lý trùng. Không bọc network call bằng `@Transactional` và coi đó là giao dịch phân tán.

Chỉ các hệ ngoài có port để thay bằng fake; tính giá thuần test trực tiếp. Bài mẫu chưa triển khai refund, inventory, invoice PDF, HTTP hay broker thật: thêm boundary tương ứng khi yêu cầu và failure policy cụ thể xuất hiện.

## Chọn pattern: bốn tình huống

1. **CreditCard, BankTransfer, Wallet** — `Strategy` ở [d10_strategy](src/main/java/phase03/d10_strategy/) khi nhiều quy tắc thanh toán đổi độc lập; `Factory` ở [d11_factory](src/main/java/phase03/d11_factory/) chỉ khi dữ liệu runtime quyết định loại được tạo. Hai nhánh ổn định thì `if` dễ đọc hơn.
2. **Logging, metrics, authorization** — `Decorator` ở [d14_decorator](src/main/java/phase03/d14_decorator/) thêm hành vi cùng interface; `Proxy` ở [d17_proxy](src/main/java/phase03/d17_proxy/) kiểm soát quyền truy cập. AOP chỉ đáng dùng khi chính sách xuyên nhiều service; nhớ self-invocation bỏ qua proxy.
3. **Ba payment providers** — `Adapter` ở [d13_adapter](src/main/java/phase03/d13_adapter/) giữ SDK ngoài khỏi nghiệp vụ; mỗi provider chuyển dữ liệu sang một hợp đồng ứng dụng. Không tạo adapter nếu chỉ gọi một API ổn định và không có boundary cần test.
4. **CSV, JSON, XML cùng read → validate → transform → persist** — `Template Method` ở [d16_template_method](src/main/java/phase03/d16_template_method/) khi lịch bước cố định; `Strategy` khi cần hoán đổi bước lúc chạy. Nhiều override points làm hierarchy khó hiểu.

Mỗi lựa chọn cần trả lời: bỏ pattern đi thì vấn đề cụ thể nào xuất hiện ngay bây giờ? Nếu chưa có, giữ code đơn giản.

## Dành cho người biên soạn

Viết bản lời giải trên nhánh biên soạn với `SOLUTION-*` chỉ trong `src/main/java`. `StripSolutions.java` sinh khung trên `exercises`; test giữ nguyên giữa hai nhánh. Kiểm tra:

```powershell
.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns
.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d10_strategy -ExpectedQuestions 6
```
