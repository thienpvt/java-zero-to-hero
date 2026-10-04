# Phase 3 Clean Code & Design Patterns Practice Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add Maven module `phase-03-clean-code-design-patterns` with marker-based solutions and JUnit tests for all 20 sections of `03-clean-code-design-patterns.md`, plus a `d21_capstone` order-service refactor whose pre-refactor behavior commit precedes the refactor/failure-path commit, then deliver a stripped skeleton to `exercises` and the solved module to `solutions` without pushing.

**Architecture:** Author on `phase03-work` in worktree `C:\Users\thien\IdeaProjects\jav-wt\phase03` (already at commit `be2749f`, design doc committed). One package `phase03.dNN_<name>` per numbered section, one focused `ExNN_*.java` source + matching test at minimum per domain. Solutions live in `src/main/java` inside `SOLUTION-*` regions that `tools/src/main/java/javaroadmap/tools/StripSolutions.java` strips; explanation answers sit in `ANSWER`/`OBSERVATION` comment blocks. Tests never carry solutions and never change between branches.

**Tech Stack:** JDK 21 at `C:\Users\thien\.jdks\jdk-21.0.12.1+1`, Maven Wrapper (`mvnw.cmd`), JUnit Jupiter 5.11.4 only. Plain Java ports with in-memory fakes for the capstone — no Spring, no provider SDK, no DB, no Kafka.

**Spec:** `docs/superpowers/specs/2026-09-30-phase03-exercises-design.md`

## Global Constraints

- JDK 21, `<maven.compiler.release>21</maven.compiler.release>`. No preview features. No `@Transactional` anywhere.
- Only JUnit Jupiter. No AssertJ, Mockito, JMH, ArchUnit, or mapping libraries. No new build plugins.
- Javadoc, `@DisplayName`, assertion messages, and exercise exceptions are Vietnamese with diacritics; files UTF-8.
- In Javadoc: a standalone ` * <p>` line before each `Qn`/`Bn`. No raw generics in prose. Every `<`/`>` in prose (outside `{@code}`) becomes `&lt;`/`&gt;` or an HTML tag (`<p>`, `<br>`). No backticks in Javadoc.
- Question lines ` * Qn [NHÃN]` copy the `.md` wording; backticks from the `.md` become `{@code ...}`. Inside `{@code ...}` write raw `<`/`>` — javadoc escapes the content, so `List&lt;String&gt;` there renders as the literal `List&lt;String&gt;`. `verify-skeleton.ps1` rejects `&lt;`/`&gt;` inside a code span.
- Package `phase03`, domain `dNN_<tên>` exactly per the file map below. Exercise files `ExNN_*.java`; test is the same name plus `Test`, same package.
- One test class per exercise file. Capstone uses `B1`–`B6` labels, not `Qn`.
- Marker grammar is fixed (see Task 2). Skeleton compile failures must stay zero; intentional reds must be `TODO Qn`/`TODO Bn` or `thay null`.
- `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package <pkg> -ExpectedQuestions <N>` must print `OK` for every domain, and the module with no `-Package` must print `OK`.
- No web search, no network calls in tests. No `Thread.sleep` to wait for a condition; use latches and bounded `join`.
- Tests assert deterministic outcomes only. Never assert GC collected an object, never assert wall-clock timing, never assert thread scheduling order.
- Do not import a third-party SDK even as a fake. Capstone fake ports are hand-written in test or main sources under `phase03`.
- Do not modify files outside the task's package except Task 1 (`pom.xml`, module `pom.xml`), Task 22 (`README.md`, `03-clean-code-design-patterns.md`), Task 23 (branch delivery).
- Working branch is `phase03-work`. Never commit to `main`, never `git push`.
- Base reactor note: this worktree's `phase-00-java-basics` is authored mid-edit and its tests fail; also, on the `exercises` branch a bare `.\mvnw.cmd -q test` runs a skeleton whose phase03 tests are intentionally red. Use `-pl phase-03-clean-code-design-patterns` for phase03 green, and `-pl phase-03-clean-code-design-patterns` plus the `solutions` checkout for full green.

## Review Focus

Each line names the input or condition the spec implies but no task's tests cover by default. Each gets a test inside the listed task.

- Refactor that changes observable behavior: a learner refactors `d21` and totals/statuses drift while behavior-preservation tests still pass. Task 23 pins before/after equality on the exact pre-refactor totals.
- External side-effect rollback assumed: any code path that charges and then persists/publishes implies the charge can be undone. Task 23 asserts a post-charge persistence failure leaves a `PAYMENT_CHARGED_ORDER_NOT_SAVED` state, never a rolled-back charge.
- Behavior-preserving refactor that silently swallows the failure paths: refactored code returns success on a payment decline. Task 23 asserts a decline returns `PAYMENT_DECLINED` and stores/charges nothing.
- Pattern demonstrated with no change pressure: a learner adds a Strategy/Factory that the exercise does not need. Task 23 asserts each pattern-selection case names the concrete problem and that a no-pattern alternative is rejected only with evidence.
- Skeleton leak from a comment: an answer value appears in Javadoc, a constant's trailing comment, or a hint string. Task 2's gate plus every domain's `verify-skeleton.ps1` step catch it; Task 24 inspects the stripped diff.

## File map

Base: `C:\Users\thien\IdeaProjects\jav-wt\phase03\phase-03-clean-code-design-patterns`

Main sources `src/main/java/phase03/<pkg>/`, tests `src/test/java/phase03/<pkg>/`.

| Task | Package | Source | Test | `-ExpectedQuestions` |
|:----:|---------|--------|------|:--------------------:|
| 3 | `d01_srp` | `Ex01_OrderServiceSplit` | `Ex01_OrderServiceSplitTest` | 6 |
| 4 | `d02_ocp` | `Ex01_PaymentSwitch`, `Ex02_ExtensionPoint` | `Ex01_PaymentSwitchTest`, `Ex02_ExtensionPointTest` | 5 |
| 5 | `d03_lsp` | `Ex01_RectangleSquare`, `Ex02_FatContract` | `Ex01_RectangleSquareTest`, `Ex02_FatContractTest` | 5 |
| 6 | `d04_isp` | `Ex01_MachineRoles` | `Ex01_MachineRolesTest` | 5 |
| 7 | `d05_dip` | `Ex01_GatewayDirection`, `Ex02_RepositoryLayer` | `Ex01_GatewayDirectionTest`, `Ex02_RepositoryLayerTest` | 6 |
| 8 | `d06_di` | `Ex01_InjectionStyles`, `Ex02_GraphAndManualWiring` | `Ex01_InjectionStylesTest`, `Ex02_GraphAndManualWiringTest` | 7 |
| 9 | `d07_composition` | `Ex01_ReuseWithoutIsA`, `Ex02_TemplateVsStrategyTradeoff` | `Ex01_ReuseWithoutIsATest`, `Ex02_TemplateVsStrategyTradeoffTest` | 5 |
| 10 | `d08_concerns` | `Ex01_ControllerResponsibility`, `Ex02_PolicyPlacement` | `Ex01_ControllerResponsibilityTest`, `Ex02_PolicyPlacementTest` | 6 |
| 11 | `d09_layers` | `Ex01_DtoAndEntity`, `Ex02_DependencyDirection` | `Ex01_DtoAndEntityTest`, `Ex02_DependencyDirectionTest` | 6 |
| 12 | `d10_strategy` | `Ex01_DiscountPolicy` | `Ex01_DiscountPolicyTest` | 6 |
| 13 | `d11_factory` | `Ex01_ProviderSelection`, `Ex02_WhenNewIsFine` | `Ex01_ProviderSelectionTest`, `Ex02_WhenNewIsFineTest` | 5 |
| 14 | `d12_builder` | `Ex01_UserBuilder` | `Ex01_UserBuilderTest` | 5 |
| 15 | `d13_adapter` | `Ex01_ProviderAdapters` | `Ex01_ProviderAdaptersTest` | 5 |
| 16 | `d14_decorator` | `Ex01_LoggingAndMetrics`, `Ex02_ChainOrder` | `Ex01_LoggingAndMetricsTest`, `Ex02_ChainOrderTest` | 5 |
| 17 | `d15_observer` | `Ex01_OrderEvents` | `Ex01_OrderEventsTest` | 6 |
| 18 | `d16_template_method` | `Ex01_Importer`, `Ex02_OverridableSteps` | `Ex01_ImporterTest`, `Ex02_OverridableStepsTest` | 5 |
| 19 | `d17_proxy` | `Ex01_ManualProxy`, `Ex02_SelfInvocation` | `Ex01_ManualProxyTest`, `Ex02_SelfInvocationTest` | 5 |
| 20 | `d18_singleton` | `Ex01_EnumSingletonAndState`, `Ex02_ScopeVsPattern` | `Ex01_EnumSingletonAndStateTest`, `Ex02_ScopeVsPatternTest` | 7 |
| 21 | `d19_clean_code` | `Ex01_NamingAndFunctions`, `Ex02_ErrorHandling` | `Ex01_NamingAndFunctionsTest`, `Ex02_ErrorHandlingTest` | 8 |
| 22 | `d20_testability` | `Ex01_ExplicitDependencies`, `Ex02_StaticAndMockSymptom` | `Ex01_ExplicitDependenciesTest`, `Ex02_StaticAndMockSymptomTest` | 6 |

Capstone package `d21_capstone` (Task 23) has no `-ExpectedQuestions`; markers are `B1`–`B6`.

Support files (Task 1): `src/main/java/phase03/support/Compiles.java` (`YES`, `NO`), `src/test/java/phase03/support/Predictions.java`, `src/test/java/phase03/support/FakePaymentGateway.java`, `src/test/java/phase03/support/FakeOrderRepository.java`, `src/test/java/phase03/support/FakeEventPublisher.java`.

Capstone files (Task 23): `src/main/java/phase03/d21_capstone/Money.java`, `OrderRequest.java`, `Order.java`, `OrderStatus.java`, `CheckoutResult.java`, `OrderService.java`, `PricingPolicy.java`, `PaymentGateway.java`, `PaymentResult.java`, `OrderRepository.java`, `InventoryService.java`, `InvoiceService.java`, `EventPublisher.java`, `OrderController.java`, `OrderApplicationService.java`, plus `CoupledOrderService.java` (the pre-refactor bad version, kept for the behavior-recording test) and `OrderServiceFactory.java` (manual wiring).

Test resources (Task 23): `src/test/resources/phase03/d21_capstone/checkout-basic.txt` and `checkout-failures.txt` — plain text fixtures for `OrderApp`.

---

## Môi trường

Mỗi shell PowerShell mới:

```powershell
$env:JAVA_HOME = "C:\Users\thien\.jdks\jdk-21.0.12.1+1"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
Set-Location C:\Users\thien\IdeaProjects\jav-wt\phase03
```

Test một package:

```powershell
.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d10_strategy/*Test"
```

Skeleton:

```powershell
.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d10_strategy -ExpectedQuestions 6
```

Module đầy đủ đã giải (trên `solutions`):

```powershell
.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test
```

---

### Task 1: Module và lớp hỗ trợ

**Files:**
- Modify: `pom.xml` (thêm `<module>phase-03-clean-code-design-patterns</module>` sau dòng `phase-02-jvm-concurrency`)
- Create: `phase-03-clean-code-design-patterns/pom.xml`
- Create: `phase-03-clean-code-design-patterns/src/main/java/phase03/support/Compiles.java`
- Create: `phase-03-clean-code-design-patterns/src/test/java/phase03/support/Predictions.java`

**Interfaces:**
- Consumes: nothing.
- Produces: Maven artifact `phase-03-clean-code-design-patterns`; `phase03.support.Compiles` enum (`YES`, `NO`); `phase03.support.Predictions.assertPrediction(String constantName, T actual, T predicted, String hint)`. Every later task imports these two.

- [ ] **Step 1: Add the module to the reactor**

Edit `pom.xml` so `<modules>` reads, in this order:

```xml
  <modules>
    <module>tools</module>
    <module>phase-00-java-basics</module>
    <module>phase-01-core-advanced</module>
    <module>phase-02-jvm-concurrency</module>
    <module>phase-03-clean-code-design-patterns</module>
  </modules>
```

- [ ] **Step 2: Create the module POM**

`phase-03-clean-code-design-patterns/pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>javaroadmap</groupId>
    <artifactId>java-roadmap-practice</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>
  <artifactId>phase-03-clean-code-design-patterns</artifactId>
  <name>Giai đoạn 3 — Clean Code và Design Patterns</name>
</project>
```

- [ ] **Step 3: Create the two support classes**

`src/main/java/phase03/support/Compiles.java`:

```java
package phase03.support;

/** Đáp án cho câu dự đoán dạng "đoạn code này có biên dịch được không?". */
public enum Compiles {
    YES,
    NO
}
```

`src/test/java/phase03/support/Predictions.java`:

```java
package phase03.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/** So sánh dự đoán của ứng viên với giá trị thực tế, kèm gợi ý khi sai. */
public final class Predictions {

    private Predictions() {
    }

    public static <T> void assertPrediction(String constantName, T actual, T predicted, String hint) {
        assertNotNull(predicted, () -> "Chưa điền dự đoán " + constantName
                + ": thay null bằng giá trị bạn nghĩ là đúng.");
        assertEquals(actual, predicted, () -> constantName + ": bạn dự đoán " + predicted
                + " nhưng thực tế là " + actual + ". Gợi ý: " + hint);
    }
}
```

- [ ] **Step 4: Verify the module builds**

Run: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test`
Expected: `BUILD SUCCESS`. Surefire runs no tests and does not fail.

- [ ] **Step 5: Commit**

```bash
git add pom.xml phase-03-clean-code-design-patterns/pom.xml phase-03-clean-code-design-patterns/src
git commit -m "build(phase03): add clean-code module"
```

---

### Task 2: Quy ước viết bài (không tạo file; quy tắc bắt buộc cho Task 3–25)

**Files:** none created.

**Interfaces:**
- Consumes: Task 1 support classes.
- Produces: the marker, Javadoc, and test conventions every later task follows verbatim.

- [ ] **Step 1: Marker cho code phải viết, chỉ trong `src/main/java`**

```java
static String split(String raw) {
    // SOLUTION-BEGIN throw Q2
    return raw.strip();
    // SOLUTION-END
}
```

`StripSolutions` biến `SOLUTION-BEGIN throw Q2` thành `throw new UnsupportedOperationException("TODO Q2");`. Thân method nên thành comment thì dùng `// SOLUTION-BEGIN todo Q2` → `// TODO Q2: viết code của bạn ở đây`. Không lồng `SOLUTION-BEGIN`.

- [ ] **Step 2: Marker cho hằng dự đoán**

```java
static final Boolean Q1_OCP_NEEDS_INTERFACE = false; // SOLUTION-VALUE
```

`StripSolutions` đổi thành `static final Boolean Q1_OCP_NEEDS_INTERFACE = null;`. Comment cùng dòng không được nêu đáp án. Enum hoặc record constant dùng cùng cách.

- [ ] **Step 3: Khối ANSWER và OBSERVATION**

```java
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * ...2 đến 6 dòng...
 * SOLUTION-END
 */

/* OBSERVATION Q5:
 * SOLUTION-BEGIN
 * ...
 * SOLUTION-END
 */
```

Khối đặt ở cuối file, sau dấu `}` của class cuối. Capstone dùng `ANSWER Bn` / `OBSERVATION Bn`.

- [ ] **Step 4: Khuôn Javadoc cho mọi file bài tập**

```java
/**
 * SRP — Bài 1: tách OrderService
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 1 (Single Responsibility Principle), câu 1, 2, 6.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_OrderServiceSplitTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] SRP có nghĩa là "một class chỉ được có một method" không?
 *   Bắt đầu   : viết khối ANSWER Q1.
 *   Hoàn thành khi: ANSWER Q1 nói được "một lý do để thay đổi" khác "một method".
 * <p>
 * Q6 [TỰ TRẢ LỜI] Làm sao biết việc tách abstraction thực sự có ích?
 *   Bắt đầu   : viết khối ANSWER Q6.
 *   Hoàn thành khi: ANSWER Q6 nêu một thay đổi cụ thể đang tồn tại, không phải giả định tương lai.
 */
```

Gợi ý phải làm được trong IntelliJ: Ctrl+N, Ctrl+F12, Ctrl+B, Ctrl+Q, F7, Alt+F8. Dòng `Bắt đầu` và `Kiểm chứng` không nêu giá trị dự đoán.

- [ ] **Step 5: Khuôn test**

```java
@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_OrderServiceSplitTest {

    @Test
    @DisplayName("Q1: SRP không phải một method mỗi class")
    void q01_prediction() {
        assertPrediction("Q1_SRP_MEANS_ONE_METHOD", false,
                Ex01_OrderServiceSplit.Q1_SRP_MEANS_ONE_METHOD, HINT_Q1);
    }
}
```

Quy tắc: class không `public`; có `@TestMethodOrder(MethodOrderer.MethodName.class)`; tên method `qNN_...`, capstone `bNN_...`; test thí nghiệm kết thúc bằng `experimentRuns`; chỉ gọi method TODO bên trong `assertThrows` trừ khi bài muốn exception khác; mọi test nhiều thread dùng `CountDownLatch`, `join` có hạn, và assert một con số tất định.

- [ ] **Step 6: Cổng mỗi task domain (chạy cuối mỗi Task 3–22)**

1. `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/<pkg>/*Test"` → xanh.
2. `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package <pkg> -ExpectedQuestions <N>` → `OK`.
3. Commit chỉ file của package: `feat(phase03): add <pkg> exercises`.

---

### Task 3: d01_srp (6 câu)

**Files:**
- Create: `phase-03-clean-code-design-patterns/src/main/java/phase03/d01_srp/Ex01_OrderServiceSplit.java`
- Test: `phase-03-clean-code-design-patterns/src/test/java/phase03/d01_srp/Ex01_OrderServiceSplitTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`.
- Produces: `Ex01_OrderServiceSplit.LineItem(String sku, long unitCents, int qty)`, `(String customer, List<LineItem> items, boolean vip)`, `totalCents(List<LineItem>) → long`, `statusFor(OrderRequest) → String`, `notificationsFor(OrderRequest) → List<String>`, `placeOrder(OrderRequest) → String`.

- [ ] **Step 1: Write the source**

```java
package phase03.d01_srp;

import java.util.ArrayList;
import java.util.List;

/**
 * SRP — Bài 1: tách OrderService
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 1 (Single Responsibility Principle), câu 1, 2, 3, 4, 5, 6.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_OrderServiceSplitTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] SRP có nghĩa là "một class chỉ được có một method" không?
 *   Bắt đầu   : điền Q1_SRP_MEANS_ONE_METHOD (thay null).
 *   Kiểm chứng: chạy q01_prediction. Ctrl+Q trên {@code OrderService} đọc đoạn mô tả.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nói được "một lý do để thay đổi" khác "một method".
 * <p>
 * Q2 [TỰ TRẢ LỜI] "One reason to change" nên hiểu thế nào?
 *   Bắt đầu   : viết khối ANSWER Q2.
 *   Hoàn thành khi: ANSWER Q2 gắn "reason" với một actor hoặc một nguồn yêu cầu thay đổi.
 * <p>
 * Q3 [DỰ ĐOÁN] Class quá lớn thường thể hiện dấu hiệu gì?
 *   Bắt đầu   : điền Q3_LARGE_CLASS_SIGNAL bằng một giá trị của {@code Signal}.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nói được vì sao độ dài chỉ là triệu chứng, không phải nguyên nhân.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Tách class quá nhỏ có thể gây vấn đề gì?
 *   Bắt đầu   : viết khối ANSWER Q4.
 *   Hoàn thành khi: ANSWER Q4 nêu chi phí điều hướng và coupling giữa các mảnh vụn.
 * <p>
 * Q5 [CODE] Với OrderService trong tài liệu, bạn sẽ tách trách nhiệm thế nào?
 *   Bắt đầu   : cài {@code totalCents}, {@code statusFor}, {@code notificationsFor}, {@code placeOrder}.
 *               {@code OrderService} cho sẵn là bản trộn trách nhiệm, đừng sửa file đó.
 *               {@code placeOrder} gọi ba method kia, không tự tính lại.
 *   Kiểm chứng: chạy q05_totalCents, q05_statusFor, q05_notificationsFor, q05_placeOrder.
 *               Ctrl+B vào {@code totalCents} trong {@code placeOrder} để thấy lời gọi tách rời.
 *   Hoàn thành khi: bốn test xanh; method nào cũng kiểm chứng được một mình.
 * <p>
 * Q6 [TỰ TRẢ LỜI] Làm sao biết việc tách abstraction thực sự có ích?
 *   Bắt đầu   : viết khối ANSWER Q6.
 *   Hoàn thành khi: ANSWER Q6 kể một thay đổi cụ thể đang tồn tại, không phải giả định về tương lai.
 */
public class Ex01_OrderServiceSplit {

    /** Dấu hiệu khi một class quá lớn. */
    public enum Signal {
        LOW_COHESION,
        MANY_LINES,
        MANY_FIELDS
    }

    public record LineItem(String sku, long unitCents, int qty) {
    }

    public record OrderRequest(String customer, List<LineItem> items, boolean vip) {
    }

    // Q1 — SRP có đồng nghĩa "một class một method" hay không.
    static final Boolean Q1_SRP_MEANS_ONE_METHOD = false; // SOLUTION-VALUE

    // Q3 — class lớn thể hiện dấu hiệu nào.
    static final Signal Q3_LARGE_CLASS_SIGNAL = Signal.LOW_COHESION; // SOLUTION-VALUE

    /**
     * Cho sẵn, không sửa. Bản trộn validate, price, persist, charge, notify, invoice trong một method.
     */
    static final class OrderService {

        String createOrder(OrderRequest request) {
            if (request.items().isEmpty()) {
                throw new IllegalArgumentException("Đơn không có mặt hàng.");
            }
            long total = 0;
            for (LineItem item : request.items()) {
                total += item.unitCents() * item.qty();
            }
            String status = request.vip() ? "VIP" : "STANDARD";
            return status + ":" + total;
        }
    }

    /**
     * Tổng tiền của danh sách mặt hàng.
     *
     * @throws IllegalArgumentException nếu danh sách null hoặc rỗng
     */
    static long totalCents(List<LineItem> items) {
        // SOLUTION-BEGIN throw Q5
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Đơn không có mặt hàng.");
        }
        long total = 0;
        for (LineItem item : items) {
            total += item.unitCents() * item.qty();
        }
        return total;
        // SOLUTION-END
    }

    /** Trạng thái đơn theo hạng khách. */
    static String statusFor(OrderRequest request) {
        // SOLUTION-BEGIN throw Q5
        return request.vip() ? "VIP" : "STANDARD";
        // SOLUTION-END
    }

    /** Một thông báo cho mỗi mặt hàng, theo thứ tự trong đơn. */
    static List<String> notificationsFor(OrderRequest request) {
        // SOLUTION-BEGIN throw Q5
        List<String> messages = new ArrayList<>();
        for (LineItem item : request.items()) {
            messages.add("Đã đặt " + item.sku());
        }
        return messages;
        // SOLUTION-END
    }

    /**
     * Luồng đặt hàng đã tách trách nhiệm: kiểm tra, tính tiền, lấy trạng thái, sinh thông báo.
     * Trả {@code status + ":" + totalCents}.
     */
    static String placeOrder(OrderRequest request) {
        // SOLUTION-BEGIN throw Q5
        long total = totalCents(request.items());
        return statusFor(request) + ":" + total;
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Không. SRP nói về lý do để thay đổi, không đếm method.
 * Một class có nhiều method vẫn đúng SRP nếu mọi method phục vụ cùng một nhóm trách nhiệm.
 * Ngược lại, hai method phục vụ hai actor khác nhau đã là hai lý do thay đổi.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * "Reason to change" là một nguồn yêu cầu thay đổi, thường là một actor hoặc một hệ thống ngoài.
 * Nếu quy tắc thuế đổi mà phải sửa class này, đó là một lý do.
 * Nếu định dạng hoá đơn đổi mà cũng phải sửa chính class đó, đó là lý do thứ hai.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Dấu hiệu là cohesion thấp: các field và method phục vụ nhiều nhóm trách nhiệm rời nhau.
 * Nhiều dòng hoặc nhiều field chỉ là triệu chứng dễ thấy của cohesion thấp, không phải nguyên nhân.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Tách quá nhỏ tạo nhiều class chỉ gọi nhau một lần, thêm chi phí điều hướng khi đọc code.
 * Abstraction giả còn che mất luồng nghiệp vụ và làm thay đổi nhỏ phải sửa nhiều file.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Việc tách có ích khi tồn tại một thay đổi cụ thể đã xảy ra hoặc đang chờ, ví dụ đổi cách tính giá.
 * Khi đó chỉ một class đổi và test của nó đổi theo, không phải sửa luồng đặt hàng.
 * Nếu lý do chỉ là "tương lai có thể cần", chưa đủ để tách.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write the test**

```java
package phase03.d01_srp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_OrderServiceSplitTest {

    private static final String HINT_Q1 =
            "SRP nói về lý do để thay đổi. Một class có nhiều method vẫn có thể chỉ có một nhóm trách nhiệm.";
    private static final String HINT_Q3 =
            "Độ dài chỉ là triệu chứng. Nguyên nhân là các phần của class không cùng phục vụ một nhóm trách nhiệm.";

    private static final Ex01_OrderServiceSplit.OrderRequest ORDER =
            new Ex01_OrderServiceSplit.OrderRequest("alice",
                    List.of(new Ex01_OrderServiceSplit.LineItem("book", 1_000, 3),
                            new Ex01_OrderServiceSplit.LineItem("pen", 500, 2)),
                    true);

    @Test
    @DisplayName("Q1 dự đoán: SRP có nghĩa một class một method không")
    void q01_prediction() {
        assertPrediction("Q1_SRP_MEANS_ONE_METHOD", false,
                Ex01_OrderServiceSplit.Q1_SRP_MEANS_ONE_METHOD, HINT_Q1);
    }

    @Test
    @DisplayName("Q3 dự đoán: class quá lớn thể hiện dấu hiệu gì")
    void q03_prediction() {
        assertPrediction("Q3_LARGE_CLASS_SIGNAL", Ex01_OrderServiceSplit.Signal.LOW_COHESION,
                Ex01_OrderServiceSplit.Q3_LARGE_CLASS_SIGNAL, HINT_Q3);
    }

    @Test
    @DisplayName("Q5: tổng tiền tính tách khỏi luồng đặt hàng")
    void q05_totalCents() {
        assertEquals(4_000, Ex01_OrderServiceSplit.totalCents(ORDER.items()),
                "1000*3 + 500*2 phải bằng 4000.");
    }

    @Test
    @DisplayName("Q5: trạng thái tính tách khỏi luồng đặt hàng")
    void q05_statusFor() {
        assertEquals("VIP", Ex01_OrderServiceSplit.statusFor(ORDER));
    }

    @Test
    @DisplayName("Q5: thông báo sinh tách khỏi luồng đặt hàng")
    void q05_notificationsFor() {
        assertEquals(List.of("Đã đặt book", "Đã đặt pen"),
                Ex01_OrderServiceSplit.notificationsFor(ORDER));
    }

    @Test
    @DisplayName("Q5: luồng đặt hàng ghép ba trách nhiệm đã tách")
    void q05_placeOrder() {
        assertEquals("VIP:4000", Ex01_OrderServiceSplit.placeOrder(ORDER));
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_OrderServiceSplit.placeOrder(
                        new Ex01_OrderServiceSplit.OrderRequest("bob", List.of(), false)));
    }
}
```

- [ ] **Step 3: RED**

Run: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d01_srp/*Test"`
Expected: FAIL — `q01_prediction` and `q03_prediction` fail with `thay null`; the four Q5 tests fail with `TODO Q5`.

- [ ] **Step 4: GREEN**

Run: the same command.
Expected: 6 tests pass, `BUILD SUCCESS`.

- [ ] **Step 5: Skeleton gate**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d01_srp -ExpectedQuestions 6`
Expected: `OK: 6 test(s) checked, skeleton rules satisfied.`

- [ ] **Step 6: Commit**

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d01_srp phase-03-clean-code-design-patterns/src/test/java/phase03/d01_srp
git commit -m "feat(phase03): add d01_srp exercises"
```

---

### Task 4: d02_ocp (5 câu)

**Files:**
- Create: `.../src/main/java/phase03/d02_ocp/Ex01_PaymentSwitch.java`, `Ex02_ExtensionPoint.java`
- Test: `.../src/test/java/phase03/d02_ocp/Ex01_PaymentSwitchTest.java`, `Ex02_ExtensionPointTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`.
- Produces: `Ex01_PaymentSwitch.feeCents(String type, long amountCents) → long`, `PaymentPolicy` interface with `long feeCents(long amountCents)` and `String name()`, `CardPolicy`, `WalletPolicy`, plus `Q1_PAYMENT_SWITCH_IS_OCP_SIGNAL`, `Q3_EVERY_IF_VIOLATES_OCP`. `Ex02_ExtensionPoint.policy(CardPolicy)`-style `static long feeWith(PaymentPolicy, long) → long`.

- [ ] **Step 1: Write `Ex01_PaymentSwitch.java`**

```java
package phase03.d02_ocp;

/**
 * OCP — Bài 1: switch theo payment type
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 2 (Open/Closed Principle), câu 1, 2, 3, 4, 5.
 * Cần làm trước: d01_srp.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_PaymentSwitchTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] OCP thực sự muốn giảm vấn đề gì?
 *   Bắt đầu   : điền Q1_OCP_REDUCES bằng một giá trị của {@code Risk}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nói được rủi ro là sửa code đang chạy.
 * <p>
 * Q2 [CODE] Vì sao switch lớn theo payment type có thể là dấu hiệu cần abstraction?
 *   Bắt đầu   : cài {@code feeCents} theo đúng switch trong tài liệu (CARD 2%, WALLET 1%, còn lại 0),
 *               rồi cài {@code CardPolicy} và {@code WalletPolicy} cùng trả đúng phần trăm đó.
 *   Kiểm chứng: chạy q02_switchFees và q02_policyFees. Ctrl+B trên {@code PaymentPolicy} xem contract.
 *   Hoàn thành khi: hai đường cho cùng một số cho CARD và WALLET; ANSWER Q2 nêu mỗi lần thêm loại
 *               thanh toán phải mở lại đúng method switch.
 * <p>
 * Q3 [DỰ ĐOÁN] Có phải mọi {@code if} đều vi phạm OCP?
 *   Bắt đầu   : điền Q3_EVERY_IF_VIOLATES_OCP.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nói điều kiện ổn định không phải điểm mở rộng.
 * <p>
 * Q4 [TỰ TRẢ LỜI] OCP có thể dẫn tới over-engineering như thế nào?
 *   Bắt đầu   : viết khối ANSWER Q4.
 *   Hoàn thành khi: ANSWER Q4 nêu abstraction tạo trước khi có biến thể thứ hai.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Khi requirement chưa ổn định, có nên abstract mọi thứ ngay không?
 *   Bắt đầu   : viết khối ANSWER Q5.
 *   Hoàn thành khi: ANSWER Q5 gắn việc abstract với số biến thể đang tồn tại.
 */
public class Ex01_PaymentSwitch {

    /** Rủi ro OCP muốn giảm. */
    public enum Risk {
        RUNTIME_BUG,
        RECOMPILE_COST,
        EDITING_WORKING_CODE
    }

    // Q1 — OCP giảm rủi ro nào.
    static final Risk Q1_OCP_REDUCES = Risk.EDITING_WORKING_CODE; // SOLUTION-VALUE

    // Q3 — mọi if đều vi phạm OCP hay không.
    static final Boolean Q3_EVERY_IF_VIOLATES_OCP = false; // SOLUTION-VALUE

    /** Cho sẵn, không sửa. Mỗi loại thanh toán mới là một nhánh switch mới. */
    static long feeCents(String type, long amountCents) {
        return switch (type) {
            case "CARD" -> amountCents * 2 / 100;
            case "WALLET" -> amountCents * 1 / 100;
            default -> 0;
        };
    }

    /** Contract cho phí thanh toán. */
    interface PaymentPolicy {

        long feeCents(long amountCents);

        String name();
    }

    static final class CardPolicy implements PaymentPolicy {

        @Override
        public long feeCents(long amountCents) {
            // SOLUTION-BEGIN throw Q2
            return amountCents * 2 / 100;
            // SOLUTION-END
        }

        @Override
        public String name() {
            // SOLUTION-BEGIN throw Q2
            return "CARD";
            // SOLUTION-END
        }
    }

    static final class WalletPolicy implements PaymentPolicy {

        @Override
        public long feeCents(long amountCents) {
            // SOLUTION-BEGIN throw Q2
            return amountCents * 1 / 100;
            // SOLUTION-END
        }

        @Override
        public String name() {
            // SOLUTION-BEGIN throw Q2
            return "WALLET";
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * OCP muốn giảm rủi ro phải sửa code đang chạy mỗi khi thêm một biến thể.
 * Code chỉ được thêm mới là code không phải đọc lại và không phải chạy lại hết regression.
 * "Closed" là đối với các điểm mở rộng đã nhận diện, không phải cấm sửa mọi file.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Switch lớn theo payment type là dấu hiệu vì mỗi loại thanh toán mới buộc mở lại đúng method đó.
 * Mọi lần thêm đều phải đọc lại toàn bộ nhánh cũ để không phá nhánh nào.
 * Tách mỗi loại thành một policy giữ code cũ nguyên và chỉ thêm implementation mới.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write `Ex02_ExtensionPoint.java`**

```java
package phase03.d02_ocp;

/**
 * OCP — Bài 2: điểm mở rộng
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 2, câu 2, 5.
 * Cần làm trước: Ex01_PaymentSwitch.
 * Cách làm: chạy test trong Ex02_ExtensionPointTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q2 [CODE] Một điểm mở rộng nhận policy mới mà không sửa caller.
 *   Bắt đầu   : cài {@code feeWith}. Không viết switch trong method này.
 *   Kiểm chứng: chạy q02_feeWith.
 *   Hoàn thành khi: q02_feeWith xanh và ANSWER Q2 nói được caller không đổi khi thêm policy.
 * <p>
 * Q5 [DỰ ĐOÁN] Chưa có biến thể thứ hai thì có nên tạo điểm mở rộng?
 *   Bắt đầu   : điền Q5_ABSTRACT_BEFORE_SECOND_VARIANT.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 nêu chi phí của một interface chỉ có một implementation.
 */
public class Ex02_ExtensionPoint {

    // Q5 — có nên dựng abstraction trước khi có biến thể thứ hai.
    static final Boolean Q5_ABSTRACT_BEFORE_SECOND_VARIANT = false; // SOLUTION-VALUE

    public static final Ex01_PaymentSwitch.PaymentPolicy FIXED =
            new Ex01_PaymentSwitch.PaymentPolicy() {
                @Override
                public long feeCents(long amountCents) {
                    return 0;
                }

                @Override
                public String name() {
                    return "FIXED";
                }
            };

    /** Phí do policy quyết định. Không switch ở đây. */
    static long feeWith(Ex01_PaymentSwitch.PaymentPolicy policy, long amountCents) {
        // SOLUTION-BEGIN throw Q2
        return policy.feeCents(amountCents);
        // SOLUTION-END
    }
}

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Caller chỉ gọi feeWith và không biết lớp nào đang chạy.
 * Thêm policy mới là thêm một class, không sửa feeWith và không sửa test của feeWith.
 * Đó là điểm mở rộng: biến thể mới, code cũ nguyên.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Chưa có biến thể thứ hai thì abstraction tạo trước chỉ có một implementation.
 * Interface đó không chứng minh được là cần thiết, lại thêm một lớp phải đọc và bảo trì.
 * Khi biến thể thứ hai xuất hiện thật, tách lúc đó rẻ hơn là đoán trước.
 * SOLUTION-END
 */
```

- [ ] **Step 3: Write both tests**

`Ex01_PaymentSwitchTest.java`:

```java
package phase03.d02_ocp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_PaymentSwitchTest {

    private static final String HINT_Q1 =
            "OCP không phải về tốc độ biên dịch, mà về việc phải mở lại code đang chạy đúng để thêm một biến thể.";
    private static final String HINT_Q3 =
            "Điều kiện ổn định, ví dụ \"số dư đủ không\", không phải điểm mở rộng. OCP nhắm vào chỗ có biến thể thay đổi.";

    @Test
    @DisplayName("Q1 dự đoán: OCP giảm rủi ro nào")
    void q01_prediction() {
        assertPrediction("Q1_OCP_REDUCES", Ex01_PaymentSwitch.Risk.EDITING_WORKING_CODE,
                Ex01_PaymentSwitch.Q1_OCP_REDUCES, HINT_Q1);
    }

    @Test
    @DisplayName("Q2: switch cho đúng phí CARD và WALLET")
    void q02_switchFees() {
        assertEquals(200, Ex01_PaymentSwitch.feeCents("CARD", 10_000));
        assertEquals(100, Ex01_PaymentSwitch.feeCents("WALLET", 10_000));
        assertEquals(0, Ex01_PaymentSwitch.feeCents("CASH", 10_000));
    }

    @Test
    @DisplayName("Q2: policy cho cùng phí với switch")
    void q02_policyFees() {
        assertEquals(200, new Ex01_PaymentSwitch.CardPolicy().feeCents(10_000));
        assertEquals(100, new Ex01_PaymentSwitch.WalletPolicy().feeCents(10_000));
        assertEquals("CARD", new Ex01_PaymentSwitch.CardPolicy().name());
    }

    @Test
    @DisplayName("Q3 dự đoán: mọi if có vi phạm OCP không")
    void q03_prediction() {
        assertPrediction("Q3_EVERY_IF_VIOLATES_OCP", false,
                Ex01_PaymentSwitch.Q3_EVERY_IF_VIOLATES_OCP, HINT_Q3);
    }
}
```

`Ex02_ExtensionPointTest.java`:

```java
package phase03.d02_ocp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_ExtensionPointTest {

    private static final String HINT_Q5 =
            "Một interface chỉ có một implementation chưa chứng minh được là cần thiết; chi phí đọc và bảo trì thì có thật.";

    @Test
    @DisplayName("Q2: điểm mở rộng nhận policy mới không sửa caller")
    void q02_feeWith() {
        assertEquals(200, Ex02_ExtensionPoint.feeWith(new Ex01_PaymentSwitch.CardPolicy(), 10_000));
        assertEquals(0, Ex02_ExtensionPoint.feeWith(Ex02_ExtensionPoint.FIXED, 10_000));
    }

    @Test
    @DisplayName("Q5 dự đoán: có nên abstract trước biến thể thứ hai")
    void q05_prediction() {
        assertPrediction("Q5_ABSTRACT_BEFORE_SECOND_VARIANT", false,
                Ex02_ExtensionPoint.Q5_ABSTRACT_BEFORE_SECOND_VARIANT, HINT_Q5);
    }
}
```

- [ ] **Step 4: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d02_ocp/*Test"`
Expected: FAIL — `thay null` on both predictions, `TODO Q2` on `feeCents`, `feeWith`, `name`.
Run GREEN after writing the solutions: same command → 6 tests pass.

- [ ] **Step 5: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d02_ocp -ExpectedQuestions 5`
Expected: `OK: 6 test(s) checked, skeleton rules satisfied.`

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d02_ocp phase-03-clean-code-design-patterns/src/test/java/phase03/d02_ocp
git commit -m "feat(phase03): add d02_ocp exercises"
```

---

### Task 5: d03_lsp (5 câu)

**Files:**
- Create: `.../src/main/java/phase03/d03_lsp/Ex01_RectangleSquare.java`, `Ex02_FatContract.java`
- Test: `.../src/test/java/phase03/d03_lsp/Ex01_RectangleSquareTest.java`, `Ex02_FatContractTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`.
- Produces: `Ex01_RectangleSquare.Shape` sealed interface with `long areaMm2()`; `Rectangle(int w, int h)` with `setWidth`; `Square(int side)`; `growWidth(Shape, int) → long`; `Q2_SQUARE_IS_LSP_SUBTYPE`, `Q3_UNSUPPORTED_IS_SMELL`. `Ex02_FatContract.Printer`/`Scanner` split interfaces and `Q4_COMPOSITION_OVER_INHERITANCE`.

- [ ] **Step 1: Write `Ex01_RectangleSquare.java`**

```java
package phase03.d03_lsp;

/**
 * LSP — Bài 1: Rectangle và Square
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 3 (Liskov Substitution Principle), câu 1, 2, 3, 5.
 * Cần làm trước: d02_ocp.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_RectangleSquareTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] LSP khác inheritance syntax như thế nào?
 *   Bắt đầu   : viết khối ANSWER Q1.
 *   Hoàn thành khi: ANSWER Q1 tách "extends biên dịch được" khỏi "caller không phải đổi kỳ vọng".
 * <p>
 * Q2 [DỰ ĐOÁN] Rectangle/Square problem thể hiện vấn đề gì?
 *   Bắt đầu   : điền Q2_SQUARE_IS_LSP_SUBTYPE.
 *   Kiểm chứng: chạy q02_prediction và q02_growWidthBreaksRectangle.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nói được caller giả định width và height độc lập.
 * <p>
 * Q3 [DỰ ĐOÁN] Subtype throw UnsupportedOperationException cho method của parent là dấu hiệu gì?
 *   Bắt đầu   : điền Q3_UNSUPPORTED_IS_SMELL.
 *   Kiểm chứng: chạy q03_prediction. Ctrl+B trên {@code rotate} xem ai khai báo nó.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 phân biệt "không hỗ trợ" với "hợp đồng hẹp hơn".
 * <p>
 * Q5 [CODE] Interface quá lớn khiến implementation vi phạm contract thế nào?
 *   Bắt đầu   : cài {@code rotate} và {@code areaMm2} trên {@code Square} sao cho {@code Square}
 *               không còn phải ném exception; nếu hình không xoay được, trả chính diện tích đó.
 *   Kiểm chứng: chạy q05_rotateWorksForBoth.
 *   Hoàn thành khi: q05_rotateWorksForBoth xanh, không đường nào ném UnsupportedOperationException.
 */
public class Ex01_RectangleSquare {

    /** Hình có diện tích và có thể xoay mà không đổi diện tích. */
    public interface Shape {

        long areaMm2();

        /** Trả diện tích sau khi xoay. */
        long rotate();
    }

    public static final class Rectangle implements Shape {

        private int width;
        private int height;

        public Rectangle(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        @Override
        public long areaMm2() {
            return (long) width * height;
        }

        @Override
        public long rotate() {
            // SOLUTION-BEGIN throw Q5
            int swap = width;
            width = height;
            height = swap;
            return areaMm2();
            // SOLUTION-END
        }
    }

    public static final class Square implements Shape {

        private final int side;

        public Square(int side) {
            this.side = side;
        }

        @Override
        public long areaMm2() {
            // SOLUTION-BEGIN throw Q5
            return (long) side * side;
            // SOLUTION-END
        }

        @Override
        public long rotate() {
            // SOLUTION-BEGIN throw Q5
            return areaMm2();
            // SOLUTION-END
        }
    }

    // Q2 — Square có phải subtype đúng LSP của Rectangle trong ví dụ này.
    static final Boolean Q2_SQUARE_IS_LSP_SUBTYPE = false; // SOLUTION-VALUE

    // Q3 — ném UnsupportedOperationException cho method của parent là dấu hiệu xấu.
    static final Boolean Q3_UNSUPPORTED_IS_SMELL = true; // SOLUTION-VALUE

    /** Cho sẵn: caller giả định width và height độc lập. */
    static long growWidth(Shape shape, int delta) {
        if (!(shape instanceof Rectangle rectangle)) {
            throw new IllegalArgumentException("Chỉ nhận Rectangle.");
        }
        rectangle.setWidth(delta);
        return rectangle.areaMm2();
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Inheritance syntax chỉ nói compiler chấp nhận lời gọi.
 * LSP nói về kỳ vọng của caller: mọi điều đúng với base type phải còn đúng với subtype.
 * Một class extends được vẫn có thể phá kỳ vọng đó ở runtime.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Caller giả định đổi width không ảnh hưởng height, đúng với Rectangle.
 * Nếu Square là subtype và đổi width kéo theo height, diện tích trả về khác kỳ vọng.
 * Caller buộc phải kiểm tra kiểu cụ thể, tức là không thay thế được — vi phạm LSP.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Method của parent nằm trong hợp đồng mà mọi subtype phải giữ.
 * Ném UnsupportedOperationException nghĩa là implementation không giữ được hợp đồng đó.
 * Đó là dấu hiệu interface đã gộp hai khả năng không cùng tồn tại, hoặc inheritance bị dùng sai.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Interface quá lớn buộc mọi implementation khai báo cả method không liên quan.
 * Implementation nào không làm được phải ném exception, và caller không biết trước là có.
 * Tách interface theo khả năng để mỗi implementation chỉ hứa điều nó giữ được.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write `Ex02_FatContract.java`**

```java
package phase03.d03_lsp;

/**
 * LSP — Bài 2: hợp đồng hẹp
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 3, câu 4.
 * Cần làm trước: Ex01_RectangleSquare.
 * Cách làm: chạy test trong Ex02_FatContractTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q4 [DỰ ĐOÁN + CODE] Khi nào composition tốt hơn inheritance?
 *   Bắt đầu   : điền Q4_COMPOSITION_OVER_INHERITANCE, rồi cài {@code DocumentPrinter.print}
 *               bằng cách gọi {@code printer.print} (composition), không kéo dài class cha.
 *   Kiểm chứng: chạy q04_prediction và q04_printerDelegates.
 *   Hoàn thành khi: q04_prediction xanh và q04_printerDelegates xanh; ANSWER Q4 nêu has-a thay is-a.
 */
public class Ex02_FatContract {

    /** Khe cắm in, hợp đồng hẹp. */
    public interface Printer {

        String print(String text);
    }

    /** Khe cắm quét, tách khỏi {@code Printer}. */
    public interface Scanner {

        String scan();
    }

    public static final class ConsolePrinter implements Printer {

        @Override
        public String print(String text) {
            return "print:" + text;
        }
    }

    // Q4 — composition tốt hơn inheritance khi quan hệ chỉ là has-a.
    static final Boolean Q4_COMPOSITION_OVER_INHERITANCE = true; // SOLUTION-VALUE

    /** Dùng composition: giữ một {@code Printer}, không extends. */
    public static final class DocumentPrinter {

        private final Printer printer;

        public DocumentPrinter(Printer printer) {
            this.printer = printer;
        }

        public String print(String text) {
            // SOLUTION-BEGIN throw Q4
            return printer.print(text);
            // SOLUTION-END
        }
    }
}

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Composition tốt hơn khi quan hệ là has-a: DocumentPrinter có một Printer, không phải là một Printer.
 * extends sẽ kéo theo mọi method và field của cha, kể cả phần không liên quan.
 * Giữ một field cho phép đổi implementation lúc runtime và thay bằng fake trong test.
 * SOLUTION-END
 */
```

- [ ] **Step 3: Write both tests**

`Ex01_RectangleSquareTest.java`:

```java
package phase03.d03_lsp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_RectangleSquareTest {

    private static final String HINT_Q2 =
            "Caller giả định width và height độc lập. Đổi width mà height đổi theo là phá kỳ vọng đó.";
    private static final String HINT_Q3 =
            "Method của parent nằm trong hợp đồng chung. Không giữ được hợp đồng đó là dấu hiệu inheritance hoặc interface sai.";

    @Test
    @DisplayName("Q2 dự đoán: Square có phải subtype đúng LSP của Rectangle")
    void q02_prediction() {
        assertPrediction("Q2_SQUARE_IS_LSP_SUBTYPE", false,
                Ex01_RectangleSquare.Q2_SQUARE_IS_LSP_SUBTYPE, HINT_Q2);
    }

    @Test
    @DisplayName("Q2: caller Rectangle bị phá nếu Square độc lập cạnh")
    void q02_growWidthBreaksRectangle() {
        Ex01_RectangleSquare.Rectangle rectangle = new Ex01_RectangleSquare.Rectangle(2, 3);
        assertEquals(12, Ex01_RectangleSquare.growWidth(rectangle, 4),
                "Đổi width thành 4, height giữ 3, diện tích trả về phải là 12.");
        assertEquals(12, rectangle.areaMm2());
    }

    @Test
    @DisplayName("Q3 dự đoán: UnsupportedOperationException là dấu hiệu xấu")
    void q03_prediction() {
        assertPrediction("Q3_UNSUPPORTED_IS_SMELL", true,
                Ex01_RectangleSquare.Q3_UNSUPPORTED_IS_SMELL, HINT_Q3);
    }

    @Test
    @DisplayName("Q5: rotate chạy cho cả Rectangle và Square, không ném exception")
    void q05_rotateWorksForBoth() {
        assertEquals(12, new Ex01_RectangleSquare.Rectangle(4, 3).rotate());
        assertEquals(9, new Ex01_RectangleSquare.Square(3).rotate());
    }
}
```

`Ex02_FatContractTest.java`:

```java
package phase03.d03_lsp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_FatContractTest {

    private static final String HINT_Q4 =
            "has-a thì composition; is-a thật sự thì mới inheritance.";

    @Test
    @DisplayName("Q4 dự đoán: composition tốt hơn inheritance khi nào")
    void q04_prediction() {
        assertPrediction("Q4_COMPOSITION_OVER_INHERITANCE", true,
                Ex02_FatContract.Q4_COMPOSITION_OVER_INHERITANCE, HINT_Q4);
    }

    @Test
    @DisplayName("Q4: DocumentPrinter chuyển tiếp cho Printer")
    void q04_printerDelegates() {
        Ex02_FatContract.DocumentPrinter documentPrinter =
                new Ex02_FatContract.DocumentPrinter(new Ex02_FatContract.ConsolePrinter());
        assertEquals("print:hoá đơn", documentPrinter.print("hoá đơn"));
    }
}
```

- [ ] **Step 4: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d03_lsp/*Test"`
Expected: FAIL — both predictions `thay null`; `TODO Q4`, `TODO Q5`.
Run GREEN: same command → 6 tests pass.

- [ ] **Step 5: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d03_lsp -ExpectedQuestions 5`
Expected: `OK`.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d03_lsp phase-03-clean-code-design-patterns/src/test/java/phase03/d03_lsp
git commit -m "feat(phase03): add d03_lsp exercises"
```

---

### Task 6: d04_isp (5 câu)

**Files:**
- Create: `.../src/main/java/phase03/d04_isp/Ex01_MachineRoles.java`
- Test: `.../src/test/java/phase03/d04_isp/Ex01_MachineRolesTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`.
- Produces: `Ex01_MachineRoles.Printer`, `Scanner`, `Fax` interfaces; `AllInOne implements Printer, Scanner, Fax` with `print`, `scan`, `fax`; `SimplePrinter implements Printer` only; `Q1_ISP_PURPOSE`, `Q2_SMALLER_ALWAYS_BETTER`, `Q3_UNSUPPORTED_IS_SMELL`, `Q4_ISP_IS_COHESION`.

- [ ] **Step 1: Write the source**

```java
package phase03.d04_isp;

/**
 * ISP — Bài 1: vai trò của máy
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 4 (Interface Segregation Principle), câu 1, 2, 3, 4, 5.
 * Cần làm trước: d03_lsp.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_MachineRolesTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] ISP giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q1_ISP_PURPOSE bằng một giá trị của {@code Purpose}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nói client không phụ thuộc method nó không dùng.
 * <p>
 * Q2 [DỰ ĐOÁN] Interface có càng nhỏ càng tốt không?
 *   Bắt đầu   : điền Q2_SMALLER_ALWAYS_BETTER.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu fragmentation khi tách quá mức.
 * <p>
 * Q3 [DỰ ĐOÁN] Một implementation có nhiều method throw UnsupportedOperationException nói lên gì?
 *   Bắt đầu   : điền Q3_UNSUPPORTED_IS_SMELL.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 trỏ về interface đã gộp nhiều vai trò.
 * <p>
 * Q4 [DỰ ĐOÁN] ISP liên quan đến cohesion như thế nào?
 *   Bắt đầu   : điền Q4_ISP_IS_COHESION.
 *   Kiểm chứng: chạy q04_prediction.
 *   Hoàn thành khi: q04_prediction xanh.
 * <p>
 * Q5 [CODE] Khi nào tách interface gây fragmentation quá mức?
 *   Bắt đầu   : cài {@code SimplePrinter.print}. Chỉ cài {@code Printer}; {@code SimplePrinter}
 *               không được khai báo {@code Scanner} hay {@code Fax}.
 *   Kiểm chứng: chạy q05_simplePrinterOnlyPrints và q05_allInOneDoesAll.
 *               Ctrl+F12 trên {@code SimplePrinter} xem danh sách method.
 *   Hoàn thành khi: hai test xanh; ANSWER Q5 nêu chi phí khi mỗi method một interface.
 */
public class Ex01_MachineRoles {

    /** Vấn đề ISP giải quyết. */
    public enum Purpose {
        FASTER_DISPATCH,
        CLIENT_NOT_DEPEND_ON_UNUSED_METHODS,
        FEWER_CLASSES
    }

    public interface Printer {

        String print(String document);
    }

    public interface Scanner {

        String scan();
    }

    public interface Fax {

        String fax(String number);
    }

    // Q1 — ISP nhắm vấn đề nào.
    static final Purpose Q1_ISP_PURPOSE = Purpose.CLIENT_NOT_DEPEND_ON_UNUSED_METHODS; // SOLUTION-VALUE

    // Q2 — interface càng nhỏ càng tốt.
    static final Boolean Q2_SMALLER_ALWAYS_BETTER = false; // SOLUTION-VALUE

    // Q3 — nhiều UnsupportedOperationException là dấu hiệu xấu.
    static final Boolean Q3_UNSUPPORTED_IS_SMELL = true; // SOLUTION-VALUE

    // Q4 — ISP là một cách nhìn cohesion.
    static final Boolean Q4_ISP_IS_COHESION = true; // SOLUTION-VALUE

    /** Cho sẵn: máy chỉ in. Không khai báo Scanner hay Fax. */
    public static final class SimplePrinter implements Printer {

        @Override
        public String print(String document) {
            // SOLUTION-BEGIN throw Q5
            return "print:" + document;
            // SOLUTION-END
        }
    }

    /** Máy đa năng giữ cả ba vai trò. */
    public static final class AllInOne implements Printer, Scanner, Fax {

        @Override
        public String print(String document) {
            return "print:" + document;
        }

        @Override
        public String scan() {
            return "scan:page-1";
        }

        @Override
        public String fax(String number) {
            return "fax:" + number;
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * ISP nói client không nên phụ thuộc method mà nó không dùng.
 * Interface gộp nhiều vai trò buộc mọi client phải biết cả những method mình không gọi.
 * Tách theo vai trò làm thay đổi ở một vai trò không lan sang client của vai trò khác.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Không. Nhỏ hơn là tốt cho tới khi nó cắt theo vai trò.
 * Tách mỗi method thành một interface tạo ra nhiều type phải ghép lại ở mọi chỗ dùng.
 * Interface nên vừa đủ một vai trò, không phải nhỏ nhất có thể.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Nhiều UnsupportedOperationException nghĩa là implementation đang bị ép hứa điều nó không làm được.
 * Nguyên nhân thường là interface đã gộp nhiều vai trò không cùng tồn tại.
 * Sửa gốc là tách interface theo vai trò, không phải thêm nhánh ném exception.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * ISP là cohesion nhìn từ phía client: mọi method trong một interface phục vụ cùng một vai trò.
 * Cohesion thấp ở interface làm client phải biết những phần không liên quan tới nó.
 * Tách đúng vai trò làm cả hai phía cohesion hơn.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Fragmentation là khi mỗi method một interface, và mọi chỗ dùng phải nhận nhiều type cùng lúc.
 * Đọc code phải ghép nhiều file để hiểu một thao tác, và đổi một thao tác phải sửa nhiều khai báo.
 * Ranh giới đúng là vai trò, không phải số method.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write the test**

```java
package phase03.d04_isp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_MachineRolesTest {

    private static final String HINT_Q1 =
            "ISP nhắm vào việc client phải biết và phụ thuộc những method nó không gọi.";
    private static final String HINT_Q2 =
            "Nhỏ hơn chỉ tốt khi cắt theo vai trò. Cắt mỗi method một interface thì phải ghép nhiều type ở mọi chỗ dùng.";
    private static final String HINT_Q3 =
            "UnsupportedOperationException nghĩa là implementation bị ép hứa điều nó không làm được.";
    private static final String HINT_Q4 =
            "ISP là cohesion nhìn từ phía client của interface.";

    @Test
    @DisplayName("Q1 dự đoán: ISP giải quyết vấn đề gì")
    void q01_prediction() {
        assertPrediction("Q1_ISP_PURPOSE",
                Ex01_MachineRoles.Purpose.CLIENT_NOT_DEPEND_ON_UNUSED_METHODS,
                Ex01_MachineRoles.Q1_ISP_PURPOSE, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: interface càng nhỏ càng tốt")
    void q02_prediction() {
        assertPrediction("Q2_SMALLER_ALWAYS_BETTER", false,
                Ex01_MachineRoles.Q2_SMALLER_ALWAYS_BETTER, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: nhiều UnsupportedOperationException là dấu hiệu xấu")
    void q03_prediction() {
        assertPrediction("Q3_UNSUPPORTED_IS_SMELL", true,
                Ex01_MachineRoles.Q3_UNSUPPORTED_IS_SMELL, HINT_Q3);
    }

    @Test
    @DisplayName("Q4 dự đoán: ISP liên quan cohesion")
    void q04_prediction() {
        assertPrediction("Q4_ISP_IS_COHESION", true,
                Ex01_MachineRoles.Q4_ISP_IS_COHESION, HINT_Q4);
    }

    @Test
    @DisplayName("Q5: SimplePrinter chỉ in")
    void q05_simplePrinterOnlyPrints() {
        Ex01_MachineRoles.Printer printer = new Ex01_MachineRoles.SimplePrinter();
        assertEquals("print:hoá đơn", printer.print("hoá đơn"));
        assertEquals(1, Ex01_MachineRoles.SimplePrinter.class.getDeclaredMethods().length,
                "SimplePrinter chỉ được khai báo một method.");
    }

    @Test
    @DisplayName("Q5: AllInOne giữ cả ba vai trò")
    void q05_allInOneDoesAll() {
        Ex01_MachineRoles.AllInOne allInOne = new Ex01_MachineRoles.AllInOne();
        assertEquals("scan:page-1", allInOne.scan());
        assertEquals("fax:0900", allInOne.fax("0900"));
    }
}
```

- [ ] **Step 3: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d04_isp/*Test"`
Expected: FAIL — four `thay null` predictions and `TODO Q5` on `SimplePrinter.print`.
Run GREEN: same command → 6 tests pass.

- [ ] **Step 4: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d04_isp -ExpectedQuestions 5`
Expected: `OK`.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d04_isp phase-03-clean-code-design-patterns/src/test/java/phase03/d04_isp
git commit -m "feat(phase03): add d04_isp exercises"
```

---

### Task 7: d05_dip (6 câu)

**Files:**
- Create: `.../src/main/java/phase03/d05_dip/Ex01_GatewayDirection.java`, `Ex02_RepositoryLayer.java`
- Test: `.../src/test/java/phase03/d05_dip/Ex01_GatewayDirectionTest.java`, `Ex02_RepositoryLayerTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`, `phase03.support.Compiles`.
- Produces: `Ex01_GatewayDirection.PaymentGateway` interface, `PaymentResult(long chargedCents, String reference)`, `Checkout` (constructor takes `PaymentGateway`), `Q1_DIP_VS_DI`, `Q3_INTERFACE_FOR_EVERY_CLASS`, `Q5_ONE_IMPL_NEEDS_INTERFACE`; `Ex02_RepositoryLayer.OrderRepository` interface, `InMemoryOrders implements OrderRepository`, `Q4_REPOSITORY_INTERFACE_LAYER`, `Q6_DIP_HELPS_TESTING`.

- [ ] **Step 1: Write `Ex01_GatewayDirection.java`**

```java
package phase03.d05_dip;

/**
 * DIP — Bài 1: hướng phụ thuộc của gateway
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 5 (Dependency Inversion Principle), câu 1, 2, 3, 5, 6.
 * Cần làm trước: d04_isp.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_GatewayDirectionTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Dependency Inversion và Dependency Injection khác nhau thế nào?
 *   Bắt đầu   : điền Q1_DIP_VS_DI bằng một giá trị của {@code Difference}.
 *   Kiểm chứng: chạy q01_prediction. Ctrl+B trên {@code PaymentGateway} xem ai phụ thuộc ai.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 tách "đảo hướng phụ thuộc" khỏi "đưa dependency từ ngoài vào".
 * <p>
 * Q2 [CODE] DIP giải quyết coupling như thế nào?
 *   Bắt đầu   : cài {@code Checkout.checkout}. Method chỉ được gọi qua {@code gateway} của interface,
 *               không được gọi class cụ thể nào.
 *   Kiểm chứng: chạy q02_checkoutThroughInterface.
 *   Hoàn thành khi: q02_checkoutThroughInterface xanh; Answer Q2 nói policy không biết implementation.
 * <p>
 * Q3 [DỰ ĐOÁN] Có cần tạo interface cho mọi class không?
 *   Bắt đầu   : điền Q3_INTERFACE_FOR_EVERY_CLASS.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nêu chi phí của interface không có biến thể.
 * <p>
 * Q5 [DỰ ĐOÁN] Nếu abstraction chỉ có một implementation thì interface có luôn cần thiết không?
 *   Bắt đầu   : điền Q5_ONE_IMPL_NEEDS_INTERFACE.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 phân biệt "chưa cần" với "không bao giờ cần".
 * <p>
 * Q6 [TỰ TRẢ LỜI] DIP giúp testing thế nào?
 *   Bắt đầu   : viết khối ANSWER Q6.
 *   Hoàn thành khi: ANSWER Q6 nêu fake thay cho hệ ngoài mà không cần mạng.
 */
public class Ex01_GatewayDirection {

    /** Khác nhau giữa DIP và DI. */
    public enum Difference {
        SAME_THING,
        DIRECTION_VS_MECHANISM,
        LAYERING_VS_INHERITANCE
    }

    public record PaymentResult(long chargedCents, String reference) {
    }

    /** Contract cho cổng thanh toán, đặt gần policy dùng nó. */
    public interface PaymentGateway {

        PaymentResult charge(long amountCents);
    }

    // Q1 — DIP và DI khác nhau ở đâu.
    static final Difference Q1_DIP_VS_DI = Difference.DIRECTION_VS_MECHANISM; // SOLUTION-VALUE

    // Q3 — có cần interface cho mọi class.
    static final Boolean Q3_INTERFACE_FOR_EVERY_CLASS = false; // SOLUTION-VALUE

    // Q5 — abstraction chỉ một implementation có luôn cần interface.
    static final Boolean Q5_ONE_IMPL_NEEDS_INTERFACE = false; // SOLUTION-VALUE

    /** Cho sẵn, không sửa: policy phụ thuộc trực tiếp implementation, đúng bản DIP vi phạm. */
    static final class StripeCheckout {

        PaymentResult checkout(long amountCents) {
            return new PaymentResult(amountCents, "stripe-direct");
        }
    }

    /** Bản đúng DIP: policy phụ thuộc {@code PaymentGateway}. */
    public static final class Checkout {

        private final PaymentGateway gateway;

        public Checkout(PaymentGateway gateway) {
            this.gateway = gateway;
        }

        public PaymentResult checkout(long amountCents) {
            // SOLUTION-BEGIN throw Q2
            if (amountCents <= 0) {
                throw new IllegalArgumentException("Số tiền phải dương: " + amountCents);
            }
            return gateway.charge(amountCents);
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * DIP nói về hướng phụ thuộc lúc biên dịch: policy không import implementation.
 * DI nói về cơ chế đưa dependency vào lúc runtime: constructor, setter, container.
 * Có thể áp dụng DI mà vẫn để policy phụ thuộc class cụ thể trong chữ ký.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Policy chỉ biết PaymentGateway, không biết Stripe hay class nào khác.
 * Đổi nhà cung cấp là thêm implementation mới, không sửa policy và không build lại policy.
 * Coupling tụt từ "biết class cụ thể" xuống "biết một contract nhỏ".
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Không. Interface chỉ đáng tạo khi có biến thể thật hoặc khi cần cắt một hệ ngoài khỏi test.
 * Với class nội bộ chỉ một cách dùng, interface thêm một lớp phải đọc mà không đổi hướng phụ thuộc.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Chưa cần không có nghĩa là không bao giờ cần.
 * Interface có một implementation vẫn hợp lý khi implementation là hệ ngoài cần thay bằng fake trong test.
 * Nếu chỉ có một implementation nội bộ và không có kế hoạch thay, để class cụ thể rẻ hơn.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Policy nhận gateway từ ngoài, nên test đưa vào một fake trả kết quả định sẵn.
 * Test không cần mạng, không cần tài khoản, không phụ thuộc nhà cung cấp.
 * Đường lỗi như từ chối thanh toán cũng dựng được tất định bằng fake.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write `Ex02_RepositoryLayer.java`**

```java
package phase03.d05_dip;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * DIP — Bài 2: repository nằm ở layer nào
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 5, câu 4, 6.
 * Cần làm trước: Ex01_GatewayDirection.
 * Cách làm: chạy test trong Ex02_RepositoryLayerTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q4 [DỰ ĐOÁN] Repository interface nên nằm ở layer nào trong Clean Architecture?
 *   Bắt đầu   : điền Q4_REPOSITORY_INTERFACE_LAYER bằng một giá trị của {@code Layer}.
 *   Kiểm chứng: chạy q04_prediction và q04_inMemorySatisfiesDomainContract.
 *   Hoàn thành khi: q04_prediction xanh và ANSWER Q4 nêu interface gần domain, adapter ở infrastructure.
 * <p>
 * Q6 [CODE] DIP giúp testing thế nào?
 *   Bắt đầu   : cài {@code InMemoryOrders.save} và {@code InMemoryOrders.find}.
 *   Kiểm chứng: chạy q06_saveThenFind.
 *   Hoàn thành khi: q06_saveThenFind xanh và ANSWER Q6 nêu fake in-memory không cần database.
 */
public class Ex02_RepositoryLayer {

    /** Layer có thể chứa repository interface. */
    public enum Layer {
        CONTROLLER,
        DOMAIN_OR_APPLICATION,
        INFRASTRUCTURE
    }

    public record OrderRecord(String id, long totalCents) {
    }

    /** Contract nghiệp vụ, không lộ database. */
    public interface OrderRepository {

        void save(OrderRecord order);

        Optional<OrderRecord> find(String id);
    }

    // Q4 — repository interface nên ở layer nào.
    static final Layer Q4_REPOSITORY_INTERFACE_LAYER = Layer.DOMAIN_OR_APPLICATION; // SOLUTION-VALUE

    /** Adapter in-memory cho test, thay database. */
    public static final class InMemoryOrders implements OrderRepository {

        private final Map<String, OrderRecord> rows = new HashMap<>();

        @Override
        public void save(OrderRecord order) {
            // SOLUTION-BEGIN throw Q6
            rows.put(order.id(), order);
            // SOLUTION-END
        }

        @Override
        public Optional<OrderRecord> find(String id) {
            // SOLUTION-BEGIN throw Q6
            return Optional.ofNullable(rows.get(id));
            // SOLUTION-END
        }
    }
}

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Interface repository thuộc domain hoặc application vì nó mô tả nhu cầu lưu trữ của nghiệp vụ.
 * Adapter triển khai interface đó nằm ở infrastructure, cùng chỗ với database thật.
 * Hướng phụ thuộc chạy từ infrastructure vào domain, không phải ngược lại.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Nghiệp vụ nhận repository từ ngoài nên test thay bằng bản in-memory.
 * Không cần database, không cần dữ liệu mẫu, không cần dọn bảng.
 * Lỗi lưu cũng dựng được bằng một repository luôn ném exception.
 * SOLUTION-END
 */
```

- [ ] **Step 3: Write both tests**

`Ex01_GatewayDirectionTest.java`:

```java
package phase03.d05_dip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_GatewayDirectionTest {

    private static final String HINT_Q1 =
            "DIP là hướng phụ thuộc lúc biên dịch; DI là cơ chế đưa dependency vào lúc runtime.";
    private static final String HINT_Q3 =
            "Interface chỉ đáng tạo khi có biến thể thật hoặc khi cần cắt một hệ ngoài khỏi test.";
    private static final String HINT_Q5 =
            "Một implementation nội bộ không cần interface. Implementation là hệ ngoài thì interface có ích cho test.";

    private static final Ex01_GatewayDirection.PaymentGateway FAKE =
            amount -> new Ex01_GatewayDirection.PaymentResult(amount, "fake");

    @Test
    @DisplayName("Q1 dự đoán: DIP khác DI thế nào")
    void q01_prediction() {
        assertPrediction("Q1_DIP_VS_DI", Ex01_GatewayDirection.Difference.DIRECTION_VS_MECHANISM,
                Ex01_GatewayDirection.Q1_DIP_VS_DI, HINT_Q1);
    }

    @Test
    @DisplayName("Q2: checkout đi qua interface, không qua class cụ thể")
    void q02_checkoutThroughInterface() {
        Ex01_GatewayDirection.Checkout checkout = new Ex01_GatewayDirection.Checkout(FAKE);
        assertEquals(new Ex01_GatewayDirection.PaymentResult(500, "fake"), checkout.checkout(500));
        assertThrows(IllegalArgumentException.class, () -> checkout.checkout(0));
    }

    @Test
    @DisplayName("Q3 dự đoán: interface cho mọi class")
    void q03_prediction() {
        assertPrediction("Q3_INTERFACE_FOR_EVERY_CLASS", false,
                Ex01_GatewayDirection.Q3_INTERFACE_FOR_EVERY_CLASS, HINT_Q3);
    }

    @Test
    @DisplayName("Q5 dự đoán: một implementation có luôn cần interface")
    void q05_prediction() {
        assertPrediction("Q5_ONE_IMPL_NEEDS_INTERFACE", false,
                Ex01_GatewayDirection.Q5_ONE_IMPL_NEEDS_INTERFACE, HINT_Q5);
    }
}
```

`Ex02_RepositoryLayerTest.java`:

```java
package phase03.d05_dip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_RepositoryLayerTest {

    private static final String HINT_Q4 =
            "Interface mô tả nhu cầu nghiệp vụ nên ở gần domain; adapter nằm ở infrastructure và phụ thuộc vào nó.";

    @Test
    @DisplayName("Q4 dự đoán: repository interface ở layer nào")
    void q04_prediction() {
        assertPrediction("Q4_REPOSITORY_INTERFACE_LAYER",
                Ex02_RepositoryLayer.Layer.DOMAIN_OR_APPLICATION,
                Ex02_RepositoryLayer.Q4_REPOSITORY_INTERFACE_LAYER, HINT_Q4);
    }

    @Test
    @DisplayName("Q4: adapter in-memory thoả contract domain")
    void q04_inMemorySatisfiesDomainContract() {
        Ex02_RepositoryLayer.OrderRepository repository = new Ex02_RepositoryLayer.InMemoryOrders();
        repository.save(new Ex02_RepositoryLayer.OrderRecord("o-1", 4_000));
        assertEquals(4_000, repository.find("o-1").orElseThrow().totalCents());
    }

    @Test
    @DisplayName("Q6: save rồi find trả đúng bản ghi")
    void q06_saveThenFind() {
        Ex02_RepositoryLayer.InMemoryOrders orders = new Ex02_RepositoryLayer.InMemoryOrders();
        orders.save(new Ex02_RepositoryLayer.OrderRecord("o-2", 1_500));
        assertEquals(1_500, orders.find("o-2").orElseThrow().totalCents());
        assertTrue(orders.find("missing").isEmpty(), "Bản ghi không tồn tại phải trả Optional rỗng.");
    }
}
```

- [ ] **Step 4: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d05_dip/*Test"`
Expected: FAIL — four `thay null` predictions; `TODO Q2` on `Checkout.checkout`; `TODO Q6` on both repository methods.
Run GREEN: same command → 7 tests pass.

- [ ] **Step 5: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d05_dip -ExpectedQuestions 6`
Expected: `OK`.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d05_dip phase-03-clean-code-design-patterns/src/test/java/phase03/d05_dip
git commit -m "feat(phase03): add d05_dip exercises"
```

---

### Task 8: d06_di (7 câu)

**Files:**
- Create: `.../src/main/java/phase03/d06_di/Ex01_InjectionStyles.java`, `Ex02_GraphAndManualWiring.java`
- Test: `.../src/test/java/phase03/d06_di/Ex01_InjectionStylesTest.java`, `Ex02_GraphAndManualWiringTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`, `phase03.support.Compiles`.
- Produces: `Ex01_InjectionStyles.Clock` interface, `FixedClock`, `ReportService` with constructor injection and `withClock(...)` setter variant, plus `Q1_DI_PROBLEM`, `Q3_CTOR_PREFERRED`, `Q4_FIELD_INJECTION_TEST_PAIN`, `Q6_CONTAINER_REQUIRED`, `Q7_DI_WITH_PLAIN_JAVA`. `Ex02_GraphAndManualWiring.OrderGraph` factory `build()` returning a wired `OrderApplicationService` record, `Q5_TWELVE_DEPS_SIGNAL`.

- [ ] **Step 1: Write `Ex01_InjectionStyles.java`**

```java
package phase03.d06_di;

/**
 * DI — Bài 1: ba cách đưa dependency vào
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 6 (Dependency Injection), câu 1, 2, 3, 4, 6, 7.
 * Cần làm trước: d05_dip.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_InjectionStylesTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] DI giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q1_DI_PROBLEM bằng một giá trị của {@code Problem}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nói object không tự dựng dependency của mình.
 * <p>
 * Q2 [TỰ TRẢ LỜI] DI và IoC khác nhau thế nào?
 *   Bắt đầu   : viết khối ANSWER Q2.
 *   Hoàn thành khi: ANSWER Q2 nói IoC là chiều đảo quyền điều khiển, DI là một cách thực hiện.
 * <p>
 * Q3 [DỰ ĐOÁN] Vì sao constructor injection thường được ưu tiên?
 *   Bắt đầu   : điền Q3_CTOR_PREFERRED bằng một giá trị của {@code Why}.
 *   Kiểm chứng: chạy q03_prediction và q03_ctorFailsFast.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nêu object không tồn tại ở trạng thái thiếu dependency.
 * <p>
 * Q4 [DỰ ĐOÁN] Field injection gây khó khăn gì cho unit test?
 *   Bắt đầu   : điền Q4_FIELD_INJECTION_TEST_PAIN.
 *   Kiểm chứng: chạy q04_prediction.
 *   Hoàn thành khi: q04_prediction xanh và ANSWER Q4 nêu phải reflection hoặc container mới đặt được dependency.
 * <p>
 * Q6 [DỰ ĐOÁN] DI container có phải điều kiện bắt buộc để áp dụng DI không?
 *   Bắt đầu   : điền Q6_CONTAINER_REQUIRED.
 *   Kiểm chứng: chạy q06_prediction.
 *   Hoàn thành khi: q06_prediction xanh.
 * <p>
 * Q7 [CODE] Có thể thực hiện DI bằng Java thuần không?
 *   Bắt đầu   : cài {@code ReportService.render} và {@code ReportService.withClock}.
 *               {@code render} chỉ gọi {@code clock.now()}; {@code withClock} trả một service mới.
 *   Kiểm chứng: chạy q07_renderUsesInjectedClock và q07_withClockSwaps.
 *   Hoàn thành khi: hai test xanh mà không có thư viện nào ngoài JDK.
 */
public class Ex01_InjectionStyles {

    /** Vấn đề DI giải quyết. */
    public enum Problem {
        SLOW_STARTUP,
        OBJECT_BUILDS_ITS_OWN_DEPENDENCIES,
        FEWER_CLASSES
    }

    /** Lý do constructor injection được ưu tiên. */
    public enum Why {
        FASTER_RUNTIME,
        FAILS_FAST_AND_ALWAYS_COMPLETE,
        SHORTER_SIGNATURE
    }

    public interface Clock {

        String now();
    }

    public static final class FixedClock implements Clock {

        private final String value;

        public FixedClock(String value) {
            this.value = value;
        }

        @Override
        public String now() {
            return value;
        }
    }

    // Q1 — DI giải quyết vấn đề nào.
    static final Problem Q1_DI_PROBLEM = Problem.OBJECT_BUILDS_ITS_OWN_DEPENDENCIES; // SOLUTION-VALUE

    // Q3 — vì sao ưu tiên constructor injection.
    static final Why Q3_CTOR_PREFERRED = Why.FAILS_FAST_AND_ALWAYS_COMPLETE; // SOLUTION-VALUE

    // Q4 — field injection gây khó gì cho test.
    static final Boolean Q4_FIELD_INJECTION_TEST_PAIN = true; // SOLUTION-VALUE

    // Q6 — DI container có bắt buộc.
    static final Boolean Q6_CONTAINER_REQUIRED = false; // SOLUTION-VALUE

    /** Dependency bắt buộc, nhận qua constructor. */
    public static final class ReportService {

        private final Clock clock;

        public ReportService(Clock clock) {
            // SOLUTION-BEGIN throw Q7
            if (clock == null) {
                throw new IllegalArgumentException("Clock không được null.");
            }
            this.clock = clock;
            // SOLUTION-END
        }

        public String render(String title) {
            // SOLUTION-BEGIN throw Q7
            return title + "@" + clock.now();
            // SOLUTION-END
        }

        /** Đổi clock cho lần dùng sau mà không sửa object hiện có. */
        public ReportService withClock(Clock other) {
            // SOLUTION-BEGIN throw Q7
            return new ReportService(other);
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * DI giải quyết việc object tự dựng dependency của mình bằng new.
 * Khi dependency đến từ ngoài, có thể thay bằng fake trong test và đổi cấu hình mà không sửa class.
 * Kèm theo là dependency của object trở nên thấy được ngay ở constructor.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * IoC là chiều đảo quyền điều khiển: framework gọi code của mình thay vì mình gọi framework.
 * DI là một cách thực hiện IoC ở mức dependency: ai đó đưa dependency vào thay vì object tự lấy.
 * DI không cần framework; IoC rộng hơn DI.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Constructor injection bắt buộc dependency phải có ngay khi tạo object.
 * Object không tồn tại ở trạng thái nửa vời, nên không có NullPointerException lúc gọi method.
 * Danh sách dependency hiện rõ ở signature, dài quá thì thấy ngay là class đang làm quá nhiều.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Field injection để field private không có đường đặt từ ngoài khi tạo object.
 * Test phải dùng reflection hoặc dựng cả container mới nhét được dependency.
 * Constructor injection cho test gọi new trực tiếp với fake, không cần hạ tầng nào.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Không. Container chỉ tự động hoá việc dựng và nối dependency.
 * DI bằng tay, một factory gọi new theo thứ tự, là DI đầy đủ và không cần thư viện.
 * Container đáng dùng khi graph lớn và cấu hình theo môi trường.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write `Ex02_GraphAndManualWiring.java`**

```java
package phase03.d06_di;

import java.util.List;

/**
 * DI — Bài 2: graph và nối bằng tay
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 6, câu 5.
 * Cần làm trước: Ex01_InjectionStyles.
 * Cách làm: chạy test trong Ex02_GraphAndManualWiringTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q5 [DỰ ĐOÁN + CODE] Constructor có 12 dependencies cho thấy điều gì?
 *   Bắt đầu   : điền Q5_TWELVE_DEPS_SIGNAL, rồi cài {@code OrderGraph.build} nối ba dependency
 *               theo đúng thứ tự policy rồi repository rồi notifier.
 *   Kiểm chứng: chạy q05_prediction và q05_buildWiresGraph.
 *   Hoàn thành khi: hai test xanh và ANSWER Q5 nêu class đang gộp nhiều trách nhiệm.
 */
public class Ex02_GraphAndManualWiring {

    /** Dấu hiệu khi constructor có 12 dependencies. */
    public enum Signal {
        FAST_STARTUP,
        MANY_RESPONSIBILITIES,
        STRONG_TYPING
    }

    public interface PricingPolicy {

        long price(long baseCents);
    }

    public interface OrderRepository {

        void save(String order);
    }

    public interface Notifier {

        void notify(String message);
    }

    /** Nghiệp vụ nhỏ, ba dependency là đủ để thấy graph. */
    public static final class OrderApplicationService {

        private final PricingPolicy pricingPolicy;
        private final OrderRepository orderRepository;
        private final Notifier notifier;

        public OrderApplicationService(PricingPolicy pricingPolicy, OrderRepository orderRepository,
                Notifier notifier) {
            this.pricingPolicy = pricingPolicy;
            this.orderRepository = orderRepository;
            this.notifier = notifier;
        }

        public long place(long baseCents) {
            long total = pricingPolicy.price(baseCents);
            String order = "order:" + total;
            orderRepository.save(order);
            notifier.notify("Đã đặt " + order);
            return total;
        }

        public List<String> dependencies() {
            return List.of(pricingPolicy.getClass().getSimpleName(),
                    orderRepository.getClass().getSimpleName(),
                    notifier.getClass().getSimpleName());
        }
    }

    // Q5 — constructor 12 dependencies là dấu hiệu gì.
    static final Signal Q5_TWELVE_DEPS_SIGNAL = Signal.MANY_RESPONSIBILITIES; // SOLUTION-VALUE

    /** Nối graph bằng tay, không container. */
    public static final class OrderGraph {

        private OrderGraph() {
        }

        public static OrderApplicationService build(PricingPolicy pricingPolicy,
                OrderRepository orderRepository, Notifier notifier) {
            // SOLUTION-BEGIN throw Q5
            if (pricingPolicy == null || orderRepository == null || notifier == null) {
                throw new IllegalArgumentException("Mọi dependency phải khác null.");
            }
            return new OrderApplicationService(pricingPolicy, orderRepository, notifier);
            // SOLUTION-END
        }
    }
}

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Mười hai dependency là dấu hiệu class gộp nhiều nhóm trách nhiệm.
 * Mỗi nhóm kéo theo vài dependency riêng, và thay đổi ở nhóm này buộc sửa constructor.
 * Cách xử lý là tách theo trách nhiệm, không phải thêm một container để giấu signature.
 * SOLUTION-END
 */
```

- [ ] **Step 3: Write both tests**

`Ex01_InjectionStylesTest.java`:

```java
package phase03.d06_di;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_InjectionStylesTest {

    private static final String HINT_Q1 =
            "DI giải quyết việc object tự gọi new cho dependency của nó.";
    private static final String HINT_Q3 =
            "Constructor bắt dependency phải có ngay, nên object không tồn tại ở trạng thái thiếu dependency.";
    private static final String HINT_Q4 =
            "Field private không có đường đặt từ ngoài khi tạo object, test phải reflection hoặc dựng container.";
    private static final String HINT_Q6 =
            "Container chỉ tự động hoá việc nối dependency; DI bằng tay vẫn là DI đầy đủ.";

    @Test
    @DisplayName("Q1 dự đoán: DI giải quyết vấn đề gì")
    void q01_prediction() {
        assertPrediction("Q1_DI_PROBLEM",
                Ex01_InjectionStyles.Problem.OBJECT_BUILDS_ITS_OWN_DEPENDENCIES,
                Ex01_InjectionStyles.Q1_DI_PROBLEM, HINT_Q1);
    }

    @Test
    @DisplayName("Q3 dự đoán: vì sao ưu tiên constructor injection")
    void q03_prediction() {
        assertPrediction("Q3_CTOR_PREFERRED",
                Ex01_InjectionStyles.Why.FAILS_FAST_AND_ALWAYS_COMPLETE,
                Ex01_InjectionStyles.Q3_CTOR_PREFERRED, HINT_Q3);
    }

    @Test
    @DisplayName("Q3: thiếu dependency thì tạo object đã thất bại")
    void q03_ctorFailsFast() {
        assertThrows(IllegalArgumentException.class,
                () -> new Ex01_InjectionStyles.ReportService(null));
    }

    @Test
    @DisplayName("Q4 dự đoán: field injection khó test")
    void q04_prediction() {
        assertPrediction("Q4_FIELD_INJECTION_TEST_PAIN", true,
                Ex01_InjectionStyles.Q4_FIELD_INJECTION_TEST_PAIN, HINT_Q4);
    }

    @Test
    @DisplayName("Q6 dự đoán: DI container có bắt buộc")
    void q06_prediction() {
        assertPrediction("Q6_CONTAINER_REQUIRED", false,
                Ex01_InjectionStyles.Q6_CONTAINER_REQUIRED, HINT_Q6);
    }

    @Test
    @DisplayName("Q7: render dùng clock được đưa vào")
    void q07_renderUsesInjectedClock() {
        Ex01_InjectionStyles.ReportService service =
                new Ex01_InjectionStyles.ReportService(new Ex01_InjectionStyles.FixedClock("2026-09-30"));
        assertEquals("doanh thu@2026-09-30", service.render("doanh thu"));
    }

    @Test
    @DisplayName("Q7: withClock trả service mới dùng clock mới")
    void q07_withClockSwaps() {
        Ex01_InjectionStyles.ReportService service =
                new Ex01_InjectionStyles.ReportService(new Ex01_InjectionStyles.FixedClock("t1"));
        Ex01_InjectionStyles.ReportService swapped =
                service.withClock(new Ex01_InjectionStyles.FixedClock("t2"));
        assertNotSame(service, swapped);
        assertEquals("x@t2", swapped.render("x"));
        assertEquals("x@t1", service.render("x"), "Service gốc phải giữ clock cũ.");
    }
}
```

`Ex02_GraphAndManualWiringTest.java`:

```java
package phase03.d06_di;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_GraphAndManualWiringTest {

    private static final String HINT_Q5 =
            "Nhiều dependency thường là nhiều nhóm trách nhiệm, không phải lý do để thêm container.";

    static final class RecordingRepository implements Ex02_GraphAndManualWiring.OrderRepository {

        final List<String> saved = new ArrayList<>();

        @Override
        public void save(String order) {
            saved.add(order);
        }
    }

    static final class RecordingNotifier implements Ex02_GraphAndManualWiring.Notifier {

        final List<String> messages = new ArrayList<>();

        @Override
        public void notify(String message) {
            messages.add(message);
        }
    }

    @Test
    @DisplayName("Q5 dự đoán: 12 dependencies là dấu hiệu gì")
    void q05_prediction() {
        assertPrediction("Q5_TWELVE_DEPS_SIGNAL",
                Ex02_GraphAndManualWiring.Signal.MANY_RESPONSIBILITIES,
                Ex02_GraphAndManualWiring.Q5_TWELVE_DEPS_SIGNAL, HINT_Q5);
    }

    @Test
    @DisplayName("Q5: build nối graph và không cần container")
    void q05_buildWiresGraph() {
        RecordingRepository repository = new RecordingRepository();
        RecordingNotifier notifier = new RecordingNotifier();
        Ex02_GraphAndManualWiring.OrderApplicationService service =
                Ex02_GraphAndManualWiring.OrderGraph.build(base -> base + 100, repository, notifier);
        assertEquals(1_100, service.place(1_000));
        assertEquals(List.of("order:1100"), repository.saved);
        assertEquals(List.of("Đã đặt order:1100"), notifier.messages);
        assertEquals(List.of("RecordingRepository", "RecordingNotifier"), service.dependencies().subList(1, 3));
    }
}
```

- [ ] **Step 4: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d06_di/*Test"`
Expected: FAIL — five `thay null` predictions; `TODO Q5` on `OrderGraph.build`; `TODO Q7` on the three `ReportService` methods.
Run GREEN: same command → 9 tests pass.

- [ ] **Step 5: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d06_di -ExpectedQuestions 7`
Expected: `OK`.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d06_di phase-03-clean-code-design-patterns/src/test/java/phase03/d06_di
git commit -m "feat(phase03): add d06_di exercises"
```

---

### Task 9: d07_composition (5 câu)

**Files:**
- Create: `.../src/main/java/phase03/d07_composition/Ex01_ReuseWithoutIsA.java`, `Ex02_TemplateVsStrategyTradeoff.java`
- Test: `.../src/test/java/phase03/d07_composition/Ex01_ReuseWithoutIsATest.java`, `Ex02_TemplateVsStrategyTradeoffTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`.
- Produces: `Ex01_ReuseWithoutIsA.Logger`, `ConsoleLogger`, `OrderService` holding a `Logger` field; `Q1_INHERITANCE_FIT`, `Q2_COMPOSITION_FIT`, `Q3_IS_A_VS_HAS_A`, `Q4_DEEP_HIERARCHY_PAIN`. `Ex02_TemplateVsStrategyTradeoff.AbstractImporter` with `final run()` and three abstract steps, `CsvImporter`, `ImportStrategy` interface + `StrategyImporter`, `Q5_HYBRID_TRADEOFF`.

- [ ] **Step 1: Write `Ex01_ReuseWithoutIsA.java`**

```java
package phase03.d07_composition;

/**
 * Composition — Bài 1: dùng lại hành vi không cần is-a
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 7 (Composition vs Inheritance), câu 1, 2, 3, 4.
 * Cần làm trước: d06_di.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_ReuseWithoutIsATest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Khi nào inheritance phù hợp?
 *   Bắt đầu   : điền Q1_INHERITANCE_FIT bằng một giá trị của {@code Fit}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu is-a thật với cùng kiểu.
 * <p>
 * Q2 [DỰ ĐOÁN] Khi nào composition tốt hơn?
 *   Bắt đầu   : điền Q2_COMPOSITION_FIT bằng một giá trị của {@code Fit}.
 *   Kiểm chứng: chạy q02_prediction và q02_orderServiceDelegates.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu has-a.
 * <p>
 * Q3 [TỰ TRẢ LỜI] "is-a" và "has-a" khác nhau thế nào?
 *   Bắt đầu   : viết khối ANSWER Q3.
 *   Hoàn thành khi: ANSWER Q3 nêu được ví dụ thay thế được của subtype cho mỗi quan hệ.
 * <p>
 * Q4 [DỰ ĐOÁN] Vì sao deep inheritance hierarchy khó bảo trì?
 *   Bắt đầu   : điền Q4_DEEP_HIERARCHY_PAIN bằng một giá trị của {@code Pain}.
 *   Kiểm chứng: chạy q04_prediction. Ctrl+B trên {@code log} xem định nghĩa ở đâu.
 *   Hoàn thành khi: q04_prediction xanh và ANSWER Q4 nêu phải đọc cả chuỗi để biết một method chạy gì.
 * <p>
 * Q5 [CODE] Template Method dùng inheritance, Strategy dùng composition. Trade-off?
 *   Bắt đầu   : cài {@code AbstractImporter.run} gọi ba bước theo thứ tự read, validate, persist.
 *               Không override {@code run} ở subclass.
 *   Kiểm chứng: chạy q05_runCallsStepsInOrder.
 *   Hoàn thành khi: q05_runCallsStepsInOrder xanh; ANSWER Q5 nêu inheritance gọi ngược lên subclass
 *               còn strategy gọi xuống qua field.
 */
public class Ex01_ReuseWithoutIsA {

    /** Khi inheritance phù hợp. */
    public enum Fit {
        TRUELY_IS_A,
        REUSE_CODE,
        SHORTER_CLASS
    }

    /** Vì sao hierarchy sâu khó bảo trì. */
    public enum Pain {
        SLOWER_RUNTIME,
        BEHAVIOR_SPREAD_ACROSS_LEVELS,
        MORE_FILES
    }

    public interface Logger {

        String log(String message);
    }

    public static final class ConsoleLogger implements Logger {

        @Override
        public String log(String message) {
            return "log:" + message;
        }
    }

    // Q1 — inheritance phù hợp khi nào.
    static final Fit Q1_INHERITANCE_FIT = Fit.TRUELY_IS_A; // SOLUTION-VALUE

    // Q2 — composition tốt hơn khi nào.
    static final Fit Q2_COMPOSITION_FIT = Fit.REUSE_CODE; // SOLUTION-VALUE

    // Q4 — hierarchy sâu khó bảo trì vì sao.
    static final Pain Q4_DEEP_HIERARCHY_PAIN = Pain.BEHAVIOR_SPREAD_ACROSS_LEVELS; // SOLUTION-VALUE

    /** Dùng composition: giữ một {@code Logger}, không extends. */
    public static final class OrderService {

        private final Logger logger;

        public OrderService(Logger logger) {
            this.logger = logger;
        }

        public String place(String order) {
            // SOLUTION-BEGIN throw Q5
            return logger.log("đặt " + order);
            // SOLUTION-END
        }
    }

    /** Cho sẵn: Template Method cho ba bước của một lần nhập dữ liệu. */
    public abstract static class AbstractImporter {

        public final String run() {
            String read = read();
            String validated = validate(read);
            return persist(validated);
        }

        protected abstract String read();

        protected abstract String validate(String raw);

        protected abstract String persist(String valid);
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Inheritance phù hợp khi quan hệ is-a đúng và subtype thật sự thay thế được base type.
 * Cần cùng ngữ nghĩa, ví dụ mọi loại importer đều là importer.
 * Nếu chỉ muốn dùng lại vài dòng code, inheritance kéo theo cả hợp đồng không cần.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Composition tốt hơn khi quan hệ là has-a: OrderService có một Logger.
 * Đổi hành vi là đổi field, không sửa chuỗi thừa kế, và test thay bằng fake dễ.
 * Không lộ ra ngoài những method của lớp được tái sử dụng.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * is-a nói subtype được dùng ở mọi chỗ cần base type và giữ nguyên kỳ vọng của caller.
 * has-a nói object giữ một thứ khác bên trong và chuyển tiếp lời gọi.
 * Chỉ has-a mới cho phép thay phần bên trong lúc runtime.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Hành vi của một method có thể nằm rải ở nhiều tầng, nên đọc một tầng không đủ hiểu.
 * Sửa một method ở tầng giữa vô tình phá subclass ở tầng dưới.
 * gọi super lẫn nhau còn làm luồng chạy phụ thuộc trạng thái runtime.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Template Method gọi ngược lên subclass: base class biết lịch, subclass biết bước.
 * Strategy gọi xuống qua field: policy chỉ biết bước, caller chọn lúc runtime.
 * Inheritance cố định lịch và khó đổi nửa đường; composition đổi được nhưng thêm một lớp nối.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write `Ex02_TemplateVsStrategyTradeoff.java`**

```java
package phase03.d07_composition;

/**
 * Composition — Bài 2: Template Method và Strategy
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 7, câu 5.
 * Cần làm trước: Ex01_ReuseWithoutIsA.
 * Cách làm: chạy test trong Ex02_TemplateVsStrategyTradeoffTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q5 [DỰ ĐOÁN + CODE] Trade-off giữa hai cách?
 *   Bắt đầu   : điền Q5_HYBRID_TRADEOFF, rồi cài ba subclass bước và {@code StrategyImporter.importData}
 *               gọi ba bước của strategy theo cùng thứ tự.
 *   Kiểm chứng: chạy q05_sameResultBothWays và q05_strategySwappableAtRuntime.
 *   Hoàn thành khi: hai test xanh và cả hai đường cho cùng chuỗi kết quả.
 */
public class Ex02_TemplateVsStrategyTradeoff {

    /** Trade-off chính giữa hai cách. */
    public enum Tradeoff {
        NONE,
        FIXED_SKELETON_VS_SWAPPABLE_STEPS,
        TEMPLATE_IS_ALWAYS_FASTER
    }

    // Q5 — trade-off giữa Template Method và Strategy.
    static final Tradeoff Q5_HYBRID_TRADEOFF = Tradeoff.FIXED_SKELETON_VS_SWAPPABLE_STEPS; // SOLUTION-VALUE

    /** Ba bước, tách khỏi lịch chạy. */
    public interface ImportSteps {

        String read();

        String validate(String raw);

        String persist(String valid);
    }

    /** Template Method: lịch cố định trong class cha. */
    public static final class CsvImporter extends Ex01_ReuseWithoutIsA.AbstractImporter {

        @Override
        protected String read() {
            // SOLUTION-BEGIN throw Q5
            return "csv-row";
            // SOLUTION-END
        }

        @Override
        protected String validate(String raw) {
            // SOLUTION-BEGIN throw Q5
            return "valid:" + raw;
            // SOLUTION-END
        }

        @Override
        protected String persist(String valid) {
            // SOLUTION-BEGIN throw Q5
            return "saved:" + valid;
            // SOLUTION-END
        }
    }

    /** Strategy: cùng lịch, nhưng các bước là field đổi được. */
    public static final class StrategyImporter {

        private final ImportSteps steps;

        public StrategyImporter(ImportSteps steps) {
            this.steps = steps;
        }

        public String importData() {
            // SOLUTION-BEGIN throw Q5
            String read = steps.read();
            String validated = steps.validate(read);
            return steps.persist(validated);
            // SOLUTION-END
        }
    }
}

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Template Method cố định lịch chạy ở class cha và chỉ cho override từng bước.
 * Strategy để lịch chạy ở caller, đổi được toàn bộ bộ bước lúc runtime.
 * Cả hai cho cùng kết quả; khác nhau ở chỗ đổi lịch có cần sửa class cha hay không.
 * SOLUTION-END
 */
```

- [ ] **Step 3: Write both tests**

`Ex01_ReuseWithoutIsATest.java`:

```java
package phase03.d07_composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ReuseWithoutIsATest {

    private static final String HINT_Q1 =
            "is-a thật với cùng ngữ nghĩa mới dùng inheritance.";
    private static final String HINT_Q2 =
            "Chỉ muốn dùng lại vài dòng code thì đó là has-a, không phải is-a.";
    private static final String HINT_Q4 =
            "Hành vi rải ở nhiều tầng làm đọc một tầng không đủ hiểu.";

    @Test
    @DisplayName("Q1 dự đoán: inheritance phù hợp khi nào")
    void q01_prediction() {
        assertPrediction("Q1_INHERITANCE_FIT", Ex01_ReuseWithoutIsA.Fit.TRUELY_IS_A,
                Ex01_ReuseWithoutIsA.Q1_INHERITANCE_FIT, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: composition tốt hơn khi nào")
    void q02_prediction() {
        assertPrediction("Q2_COMPOSITION_FIT", Ex01_ReuseWithoutIsA.Fit.REUSE_CODE,
                Ex01_ReuseWithoutIsA.Q2_COMPOSITION_FIT, HINT_Q2);
    }

    @Test
    @DisplayName("Q2: OrderService chuyển tiếp cho Logger")
    void q02_orderServiceDelegates() {
        Ex01_ReuseWithoutIsA.OrderService service =
                new Ex01_ReuseWithoutIsA.OrderService(new Ex01_ReuseWithoutIsA.ConsoleLogger());
        assertEquals("log:đặt o-1", service.place("o-1"));
    }

    @Test
    @DisplayName("Q4 dự đoán: hierarchy sâu khó bảo trì vì sao")
    void q04_prediction() {
        assertPrediction("Q4_DEEP_HIERARCHY_PAIN",
                Ex01_ReuseWithoutIsA.Pain.BEHAVIOR_SPREAD_ACROSS_LEVELS,
                Ex01_ReuseWithoutIsA.Q4_DEEP_HIERARCHY_PAIN, HINT_Q4);
    }

    @Test
    @DisplayName("Q5: run gọi ba bước theo đúng thứ tự")
    void q05_runCallsStepsInOrder() {
        Ex01_ReuseWithoutIsA.AbstractImporter importer =
                new Ex01_ReuseWithoutIsA.AbstractImporter() {
                    @Override
                    protected String read() {
                        return "raw";
                    }

                    @Override
                    protected String validate(String raw) {
                        return "v:" + raw;
                    }

                    @Override
                    protected String persist(String valid) {
                        return "p:" + valid;
                    }
                };
        assertEquals("p:v:raw", importer.run());
    }
}
```

`Ex02_TemplateVsStrategyTradeoffTest.java`:

```java
package phase03.d07_composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_TemplateVsStrategyTradeoffTest {

    private static final String HINT_Q5 =
            "Khác nhau ở chỗ lịch chạy cố định trong class cha hay do caller chọn lúc runtime.";

    private static final Ex02_TemplateVsStrategyTradeoff.ImportSteps CSV =
            new Ex02_TemplateVsStrategyTradeoff.ImportSteps() {
                @Override
                public String read() {
                    return "csv-row";
                }

                @Override
                public String validate(String raw) {
                    return "valid:" + raw;
                }

                @Override
                public String persist(String valid) {
                    return "saved:" + valid;
                }
            };

    @Test
    @DisplayName("Q5 dự đoán: trade-off giữa hai cách")
    void q05_prediction() {
        assertPrediction("Q5_HYBRID_TRADEOFF",
                Ex02_TemplateVsStrategyTradeoff.Tradeoff.FIXED_SKELETON_VS_SWAPPABLE_STEPS,
                Ex02_TemplateVsStrategyTradeoff.Q5_HYBRID_TRADEOFF, HINT_Q5);
    }

    @Test
    @DisplayName("Q5: hai đường cho cùng kết quả")
    void q05_sameResultBothWays() {
        assertEquals("saved:valid:csv-row", new Ex02_TemplateVsStrategyTradeoff.CsvImporter().run());
        assertEquals("saved:valid:csv-row",
                new Ex02_TemplateVsStrategyTradeoff.StrategyImporter(CSV).importData());
    }

    @Test
    @DisplayName("Q5: strategy đổi được bước lúc runtime")
    void q05_strategySwappableAtRuntime() {
        Ex02_TemplateVsStrategyTradeoff.ImportSteps json =
                new Ex02_TemplateVsStrategyTradeoff.ImportSteps() {
                    @Override
                    public String read() {
                        return "json-doc";
                    }

                    @Override
                    public String validate(String raw) {
                        return "valid:" + raw;
                    }

                    @Override
                    public String persist(String valid) {
                        return "saved:" + valid;
                    }
                };
        assertEquals("saved:valid:json-doc",
                new Ex02_TemplateVsStrategyTradeoff.StrategyImporter(json).importData());
    }
}
```

- [ ] **Step 4: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d07_composition/*Test"`
Expected: FAIL — four `thay null`; `TODO Q5` on `OrderService.place`, three `CsvImporter` steps, `StrategyImporter.importData`.
Run GREEN: same command → 8 tests pass.

- [ ] **Step 5: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d07_composition -ExpectedQuestions 5`
Expected: `OK`.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d07_composition phase-03-clean-code-design-patterns/src/test/java/phase03/d07_composition
git commit -m "feat(phase03): add d07_composition exercises"
```

---

### Task 10: d08_concerns (6 câu)

**Files:**
- Create: `.../src/main/java/phase03/d08_concerns/Ex01_ControllerResponsibility.java`, `Ex02_PolicyPlacement.java`
- Test: `.../src/test/java/phase03/d08_concerns/Ex01_ControllerResponsibilityTest.java`, `Ex02_PolicyPlacementTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`.
- Produces: `Ex01_ControllerResponsibility.HttpRequest(String path, String body)`, `HttpResponse(int status, String body)`, `OrderController` with `handle(HttpRequest)`, `OrderApplicationService.place(String body) → long`, `Q1_CONTROLLER_RESPONSIBILITY`, `Q3_REPOSITORY_SENDS_EMAIL`. `Ex02_PolicyPlacement.ValidationResult`, `OrderValidator`, `Q2_VALIDATION_LOCATION`, `Q4_ENTITY_DEPENDS_ON_REQUEST`, `Q6_OVER_LAYERING`.

- [ ] **Step 1: Write `Ex01_ControllerResponsibility.java`**

```java
package phase03.d08_concerns;

/**
 * Separation of Concerns — Bài 1: controller làm gì
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 8 (Separation of Concerns), câu 1, 3, 5.
 * Cần làm trước: d07_composition.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_ControllerResponsibilityTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Controller nên chịu trách nhiệm gì?
 *   Bắt đầu   : điền Q1_CONTROLLER_RESPONSIBILITY bằng một giá trị của {@code Duty}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu dịch HTTP sang lời gọi nghiệp vụ.
 * <p>
 * Q3 [DỰ ĐOÁN] Repository có nên gửi email không?
 *   Bắt đầu   : điền Q3_REPOSITORY_SENDS_EMAIL.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nêu việc gửi thông báo là trách nhiệm khác.
 * <p>
 * Q5 [CODE] Separation of Concerns giúp testability thế nào?
 *   Bắt đầu   : cài {@code OrderController.handle}. Chỉ đọc body, gọi service, đóng gói response.
 *               Không tính giá, không validate nghiệp vụ trong method này.
 *   Kiểm chứng: chạy q05_okResponse và q05_badRequestOnEmpty.
 *   Hoàn thành khi: hai test xanh; ANSWER Q5 nêu service test được mà không cần HTTP.
 */
public class Ex01_ControllerResponsibility {

    /** Trách nhiệm của controller. */
    public enum Duty {
        CALCULATE_PRICE,
        TRANSLATE_HTTP_TO_BUSINESS_CALL,
        PERSIST_AND_EMAIL
    }

    public record HttpRequest(String path, String body) {
    }

    public record HttpResponse(int status, String body) {
    }

    // Q1 — controller chịu trách nhiệm gì.
    static final Duty Q1_CONTROLLER_RESPONSIBILITY = Duty.TRANSLATE_HTTP_TO_BUSINESS_CALL; // SOLUTION-VALUE

    // Q3 — repository có nên gửi email.
    static final Boolean Q3_REPOSITORY_SENDS_EMAIL = false; // SOLUTION-VALUE

    /** Cho sẵn: nghiệp vụ thuần, không biết HTTP. */
    public static final class OrderApplicationService {

        public long place(String body) {
            if (body == null || body.isBlank()) {
                throw new IllegalArgumentException("Body trống.");
            }
            return Long.parseLong(body.strip());
        }
    }

    /** Chỉ dịch HTTP sang lời gọi nghiệp vụ, không chứa chính sách nghiệp vụ. */
    public static final class OrderController {

        private final OrderApplicationService service;

        public OrderController(OrderApplicationService service) {
            this.service = service;
        }

        public HttpResponse handle(HttpRequest request) {
            // SOLUTION-BEGIN throw Q5
            try {
                return new HttpResponse(200, "total:" + service.place(request.body()));
            } catch (RuntimeException failure) {
                return new HttpResponse(400, failure.getMessage());
            }
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Controller nhận request, chuyển nó thành một lời gọi nghiệp vụ, và đóng gói kết quả thành response.
 * Nó chịu trách nhiệm về giao thức: mã trạng thái, mã hoá, lỗi đầu vào ở mức HTTP.
 * Quy tắc nghiệp vụ không thuộc controller vì như vậy sẽ khó test và khó tái dùng.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Không. Repository giữ trách nhiệm lưu và đọc dữ liệu.
 * Gửi email là một hệ ngoài khác, đổi nhà cung cấp và đổi nội dung theo nghiệp vụ.
 * Nếu repository gửi email, mọi test lưu dữ liệu đều kéo theo thông báo.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Nghiệp vụ nằm trong service không biết HTTP, nên test gọi trực tiếp với chuỗi đầu vào.
 * Test controller chỉ kiểm tra ánh xạ request sang response, không cần dựng cả hệ.
 * Lỗi nghiệp vụ và lỗi giao thức được kiểm riêng, nên xác định nguyên nhân nhanh hơn.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write `Ex02_PolicyPlacement.java`**

```java
package phase03.d08_concerns;

/**
 * Separation of Concerns — Bài 2: chỗ đặt chính sách
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 8, câu 2, 4, 6.
 * Cần làm trước: Ex01_ControllerResponsibility.
 * Cách làm: chạy test trong Ex02_PolicyPlacementTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q2 [DỰ ĐOÁN] Validation business rule nên nằm ở đâu?
 *   Bắt đầu   : điền Q2_VALIDATION_LOCATION bằng một giá trị của {@code Location}.
 *   Kiểm chứng: chạy q02_prediction và q02_validatorRejectsEmptyItems.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu chính sách nghiệp vụ ở domain.
 * <p>
 * Q4 [DỰ ĐOÁN] Entity có nên phụ thuộc HTTP request không?
 *   Bắt đầu   : điền Q4_ENTITY_DEPENDS_ON_REQUEST.
 *   Kiểm chứng: chạy q04_prediction.
 *   Hoàn thành khi: q04_prediction xanh và ANSWER Q4 nêu entity sẽ phải build theo framework.
 * <p>
 * Q6 [TỰ TRẢ LỜI] Có thể phân layer quá mức không?
 *   Bắt đầu   : viết khối ANSWER Q6.
 *   Hoàn thành khi: ANSWER Q6 nêu tầng chuyển tiếp không thêm trách nhiệm.
 */
public class Ex02_PolicyPlacement {

    /** Nơi đặt validation nghiệp vụ. */
    public enum Location {
        CONTROLLER,
        DOMAIN,
        DATABASE
    }

    public record ValidationResult(boolean valid, String message) {
    }

    // Q2 — validation nghiệp vụ nên ở đâu.
    static final Location Q2_VALIDATION_LOCATION = Location.DOMAIN; // SOLUTION-VALUE

    // Q4 — entity có phụ thuộc HTTP request.
    static final Boolean Q4_ENTITY_DEPENDS_ON_REQUEST = false; // SOLUTION-VALUE

    /** Chính sách nghiệp vụ thuần, không biết HTTP. */
    public static final class OrderValidator {

        public ValidationResult validate(int itemCount, long totalCents) {
            // SOLUTION-BEGIN throw Q2
            if (itemCount <= 0) {
                return new ValidationResult(false, "Đơn phải có ít nhất một mặt hàng.");
            }
            if (totalCents <= 0) {
                return new ValidationResult(false, "Tổng tiền phải dương.");
            }
            return new ValidationResult(true, "ok");
            // SOLUTION-END
        }
    }
}

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Quy tắc nghiệp vụ thuộc domain vì nó không phụ thuộc giao thức hay lưu trữ.
 * Controller chỉ chuyển lỗi đó thành mã trạng thái phù hợp.
 * Nếu để trong controller, quy tắc không dùng lại được cho job khác và mỗi giao thức phải lặp lại.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Không. Entity phụ thuộc HTTP request nghĩa là entity chỉ dựng được trong một ngữ cảnh web.
 * Mọi job nền hoặc test phải giả lập request, và đổi framework sẽ đụng vào domain.
 * Chuyển đổi từ request sang kiểu domain là việc của lớp biên.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Có. Layer quá mức là tầng chỉ chuyển tiếp lời gọi mà không thêm trách nhiệm nào.
 * Mỗi tầng thêm một lần đọc và một chỗ có thể đặt sai logic.
 * Tầng đáng tồn tại khi nó dịch một mô hình hoặc cô lập một hệ ngoài.
 * SOLUTION-END
 */
```

- [ ] **Step 3: Write both tests**

`Ex01_ControllerResponsibilityTest.java`:

```java
package phase03.d08_concerns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ControllerResponsibilityTest {

    private static final String HINT_Q1 =
            "Controller làm việc giao thức: nhận request, gọi nghiệp vụ, trả response.";
    private static final String HINT_Q3 =
            "Gửi email là một hệ ngoài khác, không thuộc trách nhiệm lưu trữ.";

    private static final Ex01_ControllerResponsibility.OrderController CONTROLLER =
            new Ex01_ControllerResponsibility.OrderController(
                    new Ex01_ControllerResponsibility.OrderApplicationService());

    @Test
    @DisplayName("Q1 dự đoán: controller chịu trách nhiệm gì")
    void q01_prediction() {
        assertPrediction("Q1_CONTROLLER_RESPONSIBILITY",
                Ex01_ControllerResponsibility.Duty.TRANSLATE_HTTP_TO_BUSINESS_CALL,
                Ex01_ControllerResponsibility.Q1_CONTROLLER_RESPONSIBILITY, HINT_Q1);
    }

    @Test
    @DisplayName("Q3 dự đoán: repository có gửi email")
    void q03_prediction() {
        assertPrediction("Q3_REPOSITORY_SENDS_EMAIL", false,
                Ex01_ControllerResponsibility.Q3_REPOSITORY_SENDS_EMAIL, HINT_Q3);
    }

    @Test
    @DisplayName("Q5: request hợp lệ trả 200")
    void q05_okResponse() {
        assertEquals(new Ex01_ControllerResponsibility.HttpResponse(200, "total:1500"),
                CONTROLLER.handle(new Ex01_ControllerResponsibility.HttpRequest("/orders", "1500")));
    }

    @Test
    @DisplayName("Q5: body trống trả 400")
    void q05_badRequestOnEmpty() {
        Ex01_ControllerResponsibility.HttpResponse response =
                CONTROLLER.handle(new Ex01_ControllerResponsibility.HttpRequest("/orders", " "));
        assertEquals(400, response.status());
        assertEquals("Body trống.", response.body());
    }
}
```

`Ex02_PolicyPlacementTest.java`:

```java
package phase03.d08_concerns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_PolicyPlacementTest {

    private static final String HINT_Q2 =
            "Quy tắc nghiệp vụ không phụ thuộc giao thức hay lưu trữ, nên nó ở domain.";
    private static final String HINT_Q4 =
            "Entity phụ thuộc request thì chỉ dựng được trong ngữ cảnh web.";

    private static final Ex02_PolicyPlacement.OrderValidator VALIDATOR =
            new Ex02_PolicyPlacement.OrderValidator();

    @Test
    @DisplayName("Q2 dự đoán: validation nghiệp vụ ở đâu")
    void q02_prediction() {
        assertPrediction("Q2_VALIDATION_LOCATION", Ex02_PolicyPlacement.Location.DOMAIN,
                Ex02_PolicyPlacement.Q2_VALIDATION_LOCATION, HINT_Q2);
    }

    @Test
    @DisplayName("Q2: validator từ chối đơn không có mặt hàng")
    void q02_validatorRejectsEmptyItems() {
        assertFalse(VALIDATOR.validate(0, 1_000).valid());
        assertEquals("Đơn phải có ít nhất một mặt hàng.", VALIDATOR.validate(0, 1_000).message());
        assertEquals("ok", VALIDATOR.validate(1, 1_000).message());
    }

    @Test
    @DisplayName("Q4 dự đoán: entity có phụ thuộc HTTP request")
    void q04_prediction() {
        assertPrediction("Q4_ENTITY_DEPENDS_ON_REQUEST", false,
                Ex02_PolicyPlacement.Q4_ENTITY_DEPENDS_ON_REQUEST, HINT_Q4);
    }
}
```

- [ ] **Step 4: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d08_concerns/*Test"`
Expected: FAIL — four `thay null`; `TODO Q5` on `OrderController.handle`; `TODO Q2` on `OrderValidator.validate`.
Run GREEN: same command → 7 tests pass.

- [ ] **Step 5: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d08_concerns -ExpectedQuestions 6`
Expected: `OK`.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d08_concerns phase-03-clean-code-design-patterns/src/test/java/phase03/d08_concerns
git commit -m "feat(phase03): add d08_concerns exercises"
```

---

### Task 11: d09_layers (6 câu)

**Files:**
- Create: `.../src/main/java/phase03/d09_layers/Ex01_DtoAndEntity.java`, `Ex02_DependencyDirection.java`
- Test: `.../src/test/java/phase03/d09_layers/Ex01_DtoAndEntityTest.java`, `Ex02_DependencyDirectionTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`.
- Produces: `Ex01_DtoAndEntity.OrderEntity` (package-private fields, `id`, `totalCents`, `internalNote`), `OrderResponse(String id, long totalCents)`, `OrderMapper.toResponse(OrderEntity) → OrderResponse`, `Q1_EXPOSE_ENTITY`, `Q2_DTO_PURPOSE`, `Q3_MANUAL_VS_LIBRARY`. `Ex02_DependencyDirection.domainPurity()` reflection check, `Q4_BUSINESS_IN_CONTROLLER`, `Q5_ALWAYS_FIVE_LAYERS`, `Q6_LAYERING_DOWNSIDE`.

- [ ] **Step 1: Write `Ex01_DtoAndEntity.java`**

```java
package phase03.d09_layers;

import java.util.List;

/**
 * Layered Architecture — Bài 1: DTO và entity
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 9 (Layered Architecture), câu 1, 2, 3.
 * Cần làm trước: d08_concerns.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_DtoAndEntityTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Vì sao không nên expose JPA Entity trực tiếp từ REST API trong nhiều hệ thống?
 *   Bắt đầu   : điền Q1_EXPOSE_ENTITY bằng một giá trị của {@code Risk}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu đổi schema làm đổi API.
 * <p>
 * Q2 [DỰ ĐOÁN] DTO giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q2_DTO_PURPOSE bằng một giá trị của {@code Purpose}.
 *   Kiểm chứng: chạy q02_prediction và q02_mapperHidesInternalField.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu contract API tách khỏi mô hình lưu trữ.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Mapping thủ công và mapper library có trade-off gì?
 *   Bắt đầu   : viết khối ANSWER Q3.
 *   Hoàn thành khi: ANSWER Q3 nêu kiểm soát tường minh so với ít code lặp.
 * <p>
 * Q6 [CODE] Layered architecture có nhược điểm gì?
 *   Bắt đầu   : cài {@code OrderMapper.toResponse}. Bản response không được có field nội bộ.
 *   Kiểm chứng: chạy q06_responseHasOnlyContractFields.
 *   Hoàn thành khi: q06_responseHasOnlyContractFields xanh; ANSWER Q6 nêu chi phí đi qua nhiều tầng.
 */
public class Ex01_DtoAndEntity {

    /** Rủi ro khi expose entity trực tiếp. */
    public enum Risk {
        SLOWER_QUERY,
        SCHEMA_CHANGE_BREAKS_API,
        MORE_CLASSES
    }

    /** Việc DTO làm. */
    public enum Purpose {
        SPEED_UP_QUERY,
        SEPARATE_API_CONTRACT_FROM_STORAGE_MODEL,
        REDUCE_CLASSES
    }

    /** Cho sẵn: entity lưu trữ, có field nội bộ không thuộc contract API. */
    public record OrderEntity(String id, long totalCents, String internalNote) {
    }

    /** Contract API, chỉ có field cho client. */
    public record OrderResponse(String id, long totalCents) {
    }

    // Q1 — expose entity trực tiếp có rủi ro gì.
    static final Risk Q1_EXPOSE_ENTITY = Risk.SCHEMA_CHANGE_BREAKS_API; // SOLUTION-VALUE

    // Q2 — DTO giải quyết vấn đề gì.
    static final Purpose Q2_DTO_PURPOSE = Purpose.SEPARATE_API_CONTRACT_FROM_STORAGE_MODEL; // SOLUTION-VALUE

    /** Chuyển entity sang contract API. */
    public static final class OrderMapper {

        private OrderMapper() {
        }

        public static OrderResponse toResponse(OrderEntity entity) {
            // SOLUTION-BEGIN throw Q6
            if (entity == null) {
                throw new IllegalArgumentException("Entity không được null.");
            }
            return new OrderResponse(entity.id(), entity.totalCents());
            // SOLUTION-END
        }

        public static List<OrderResponse> toResponses(List<OrderEntity> entities) {
            // SOLUTION-BEGIN throw Q6
            return entities.stream().map(OrderMapper::toResponse).toList();
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Entity phản ánh schema lưu trữ, nên đổi cột hoặc đổi quan hệ là đổi luôn response API.
 * Entity còn chứa field nội bộ không nên đi ra client, hoặc chứa quan hệ lazy gây lỗi tuần tự hoá.
 * Tách DTO giữ hai thứ đổi theo nhịp khác nhau.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * DTO định nghĩa contract của API, độc lập với bảng và cột.
 * Nó cho phép đổi schema mà không đổi response, và chỉ đưa ra field client cần.
 * Kèm theo là chỗ để gộp hoặc đổi tên field cho phù hợp ngôn ngữ của client.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Mapping thủ công cho kiểm soát tường minh: thấy rõ field nào đi đâu, dễ đặt breakpoint.
 * Mapper library ít code lặp nhưng giấu chi tiết và khó đoán khi tên field gần giống nhau.
 * Với vài DTO, viết tay rẻ hơn; nhiều DTO và quy tắc ánh xạ thì library hợp lý hơn.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Mỗi request đi qua nhiều tầng, và mỗi tầng thường chỉ chuyển tiếp dữ liệu.
 * Model phải chuyển đổi qua lại giữa entity, domain và DTO.
 * Chi phí đó đáng khi các tầng cô lập thay đổi thật, không đáng khi chỉ có một cách dùng.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write `Ex02_DependencyDirection.java`**

```java
package phase03.d09_layers;

import java.util.Arrays;
import java.util.List;

/**
 * Layered Architecture — Bài 2: hướng phụ thuộc
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 9, câu 4, 5, 6.
 * Cần làm trước: Ex01_DtoAndEntity.
 * Cách làm: chạy test trong Ex02_DependencyDirectionTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q4 [DỰ ĐOÁN] Business logic nên nằm ở controller hay service/domain?
 *   Bắt đầu   : điền Q4_BUSINESS_IN_CONTROLLER.
 *   Kiểm chứng: chạy q04_prediction.
 *   Hoàn thành khi: q04_prediction xanh và ANSWER Q4 nêu lý do tái dùng và test.
 * <p>
 * Q5 [DỰ ĐOÁN] Có phải mọi project đều cần 5 layer không?
 *   Bắt đầu   : điền Q5_ALWAYS_FIVE_LAYERS.
 *   Kiểm chứng: chạy q05_prediction và q05_domainImportsNothingOutside.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 nêu layer chỉ thêm khi có thay đổi độc lập.
 * <p>
 * Q6 [CODE] Domain không được import controller, SDK ngoài hay JPA entity.
 *   Bắt đầu   : cài {@code domainImports} trả danh sách tên import của {@code OrderPolicy}
 *               đọc qua {@code OrderPolicy.class} và annotation nếu có; ở đây trả List.of() cho
 *               lớp sạch và để test tự kiểm điều kiện rỗng.
 *   Kiểm chứng: chạy q06_domainImportsNothingOutside.
 *   Hoàn thành khi: q06_domainImportsNothingOutside xanh.
 */
public class Ex02_DependencyDirection {

    // Q4 — business logic nên ở controller hay service/domain.
    static final Boolean Q4_BUSINESS_IN_CONTROLLER = false; // SOLUTION-VALUE

    // Q5 — mọi project đều cần đủ năm layer.
    static final Boolean Q5_ALWAYS_FIVE_LAYERS = false; // SOLUTION-VALUE

    /** Cho sẵn: policy nghiệp vụ thuần, không import gì ngoài JDK. */
    public static final class OrderPolicy {

        public long applyVipDiscount(long totalCents) {
            return totalCents * 9 / 10;
        }
    }

    /** Tên các kiểu mà domain được phép tham chiếu. */
    public static final class DomainBoundary {

        private DomainBoundary() {
        }

        public static List<String> domainImports() {
            // SOLUTION-BEGIN throw Q6
            return List.of("java.lang", "java.util", "java.math", "phase03");
            // SOLUTION-END
        }

        public static boolean isAllowed(String importName) {
            return domainImports().stream().anyMatch(importName::startsWith);
        }

        public static List<String> forbiddenPrefixes() {
            return Arrays.asList("org.springframework", "jakarta.persistence", "com.stripe", "org.apache.kafka");
        }
    }
}

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Logic thuộc service hoặc domain, không thuộc controller.
 * Controller biết HTTP nên không dùng lại được cho job nền, và test phải dựng cả request.
 * Domain thuần test bằng lời gọi trực tiếp, không cần hạ tầng.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Không. Layer đáng tồn tại khi nó cô lập một thay đổi thật, ví dụ đổi giao thức hoặc đổi database.
 * Với ứng dụng nhỏ chỉ một cách dùng, thêm tầng chỉ tăng số file phải đọc.
 * Sơ đồ trong tài liệu là luồng xử lý, không phải yêu cầu đủ mặt mọi lớp.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Domain chỉ được tham chiếu kiểu của chính nó và JDK.
 * Import SDK nhà cung cấp hoặc JPA entity làm domain phụ thuộc hạ tầng, đảo ngược hướng mong muốn.
 * Adapter triển khai interface của domain và nằm ở infrastructure.
 * SOLUTION-END
 */
```

- [ ] **Step 3: Write both tests**

`Ex01_DtoAndEntityTest.java`:

```java
package phase03.d09_layers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static phase03.support.Predictions.assertPrediction;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_DtoAndEntityTest {

    private static final String HINT_Q1 =
            "Entity đi theo schema; đổi schema là đổi luôn API nếu expose trực tiếp.";
    private static final String HINT_Q2 =
            "DTO là contract của API, tách khỏi mô hình lưu trữ.";

    private static final Ex01_DtoAndEntity.OrderEntity ENTITY =
            new Ex01_DtoAndEntity.OrderEntity("o-1", 4_000, "ghi chú nội bộ");

    @Test
    @DisplayName("Q1 dự đoán: expose entity trực tiếp có rủi ro gì")
    void q01_prediction() {
        assertPrediction("Q1_EXPOSE_ENTITY", Ex01_DtoAndEntity.Risk.SCHEMA_CHANGE_BREAKS_API,
                Ex01_DtoAndEntity.Q1_EXPOSE_ENTITY, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: DTO giải quyết vấn đề gì")
    void q02_prediction() {
        assertPrediction("Q2_DTO_PURPOSE",
                Ex01_DtoAndEntity.Purpose.SEPARATE_API_CONTRACT_FROM_STORAGE_MODEL,
                Ex01_DtoAndEntity.Q2_DTO_PURPOSE, HINT_Q2);
    }

    @Test
    @DisplayName("Q2: mapper che field nội bộ")
    void q02_mapperHidesInternalField() {
        Ex01_DtoAndEntity.OrderResponse response = Ex01_DtoAndEntity.OrderMapper.toResponse(ENTITY);
        assertEquals("o-1", response.id());
        assertEquals(4_000, response.totalCents());
        assertFalse(Arrays.stream(Ex01_DtoAndEntity.OrderResponse.class.getRecordComponents())
                        .map(RecordComponent::getName)
                        .toList()
                        .contains("internalNote"),
                "OrderResponse không được có field internalNote.");
    }

    @Test
    @DisplayName("Q6: response chỉ có field của contract")
    void q06_responseHasOnlyContractFields() {
        assertEquals(List.of("id", "totalCents"),
                Arrays.stream(Ex01_DtoAndEntity.OrderResponse.class.getRecordComponents())
                        .map(RecordComponent::getName).toList());
        assertEquals(1, Ex01_DtoAndEntity.OrderMapper.toResponses(List.of(ENTITY)).size());
        assertEquals(List.of("o-1"), Ex01_DtoAndEntity.OrderMapper.toResponses(List.of(ENTITY))
                .stream().map(Ex01_DtoAndEntity.OrderResponse::id).toList());
    }
}
```

`Ex02_DependencyDirectionTest.java`:

```java
package phase03.d09_layers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_DependencyDirectionTest {

    private static final String HINT_Q4 =
            "Controller biết HTTP nên không dùng lại được cho job nền và khó test.";
    private static final String HINT_Q5 =
            "Layer chỉ đáng thêm khi cô lập một thay đổi thật.";

    @Test
    @DisplayName("Q4 dự đoán: business logic ở controller hay domain")
    void q04_prediction() {
        assertPrediction("Q4_BUSINESS_IN_CONTROLLER", false,
                Ex02_DependencyDirection.Q4_BUSINESS_IN_CONTROLLER, HINT_Q4);
    }

    @Test
    @DisplayName("Q5 dự đoán: mọi project cần đủ năm layer")
    void q05_prediction() {
        assertPrediction("Q5_ALWAYS_FIVE_LAYERS", false,
                Ex02_DependencyDirection.Q5_ALWAYS_FIVE_LAYERS, HINT_Q5);
    }

    @Test
    @DisplayName("Q5: policy nghiệp vụ thuần chạy không cần hạ tầng")
    void q05_domainImportsNothingOutside() {
        assertEquals(900, new Ex02_DependencyDirection.OrderPolicy().applyVipDiscount(1_000));
        assertTrue(Ex02_DependencyDirection.DomainBoundary.isAllowed("java.util.List"));
    }

    @Test
    @DisplayName("Q6: domain chặn prefix hạ tầng")
    void q06_domainImportsNothingOutside() {
        assertTrue(Ex02_DependencyDirection.DomainBoundary.forbiddenPrefixes()
                .stream().noneMatch(Ex02_DependencyDirection.DomainBoundary::isAllowed),
                "Không prefix hạ tầng nào được nằm trong danh sách cho phép của domain.");
    }
}
```

- [ ] **Step 4: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d09_layers/*Test"`
Expected: FAIL — four `thay null`; `TODO Q6` on `OrderMapper.toResponse`, `toResponses`, `DomainBoundary.domainImports`.
Run GREEN: same command → 8 tests pass.

- [ ] **Step 5: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d09_layers -ExpectedQuestions 6`
Expected: `OK`.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d09_layers phase-03-clean-code-design-patterns/src/test/java/phase03/d09_layers
git commit -m "feat(phase03): add d09_layers exercises"
```

---

### Task 12: d10_strategy (6 câu)

**Files:**
- Create: `.../src/main/java/phase03/d10_strategy/Ex01_DiscountPolicy.java`
- Test: `.../src/test/java/phase03/d10_strategy/Ex01_DiscountPolicyTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`.
- Produces: `Ex01_DiscountPolicy.DiscountPolicy` interface `long discount(long totalCents)`, `NormalDiscount`, `VipDiscount`, `CampaignDiscount`, `PricingContext(List<DiscountPolicy>)` with `best(long)`, plus `Q1_STRATEGY_PROBLEM`, `Q2_STRATEGY_BEATS_SWITCH`, `Q3_TWO_BRANCHES_NEED_STRATEGY`, `Q4_STRATEGY_OCP`, `Q6_SPRING_DI`.

- [ ] **Step 1: Write the source**

```java
package phase03.d10_strategy;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Strategy — Bài 1: DiscountPolicy
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 10 (Strategy Pattern), câu 1, 2, 3, 4, 5, 6.
 * Cần làm trước: d09_layers.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_DiscountPolicyTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Strategy giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q1_STRATEGY_PROBLEM bằng một giá trị của {@code Problem}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu tách thuật toán khỏi nơi dùng nó.
 * <p>
 * Q2 [DỰ ĐOÁN] Strategy tốt hơn switch khi nào?
 *   Bắt đầu   : điền Q2_STRATEGY_BEATS_SWITCH.
 *   Kiểm chứng: chạy q02_prediction và q02_policiesGiveExpectedDiscounts.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu nhánh đổi độc lập và hay đổi.
 * <p>
 * Q3 [DỰ ĐOÁN] Có phải có hai nhánh if là nên tạo Strategy không?
 *   Bắt đầu   : điền Q3_TWO_BRANCHES_NEED_STRATEGY.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nêu nhánh ổn định không cần tách.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Strategy liên quan OCP thế nào?
 *   Bắt đầu   : viết khối ANSWER Q4.
 *   Hoàn thành khi: ANSWER Q4 nêu thêm policy là thêm class, không sửa caller.
 * <p>
 * Q5 [CODE] Strategy runtime selection được thực hiện thế nào?
 *   Bắt đầu   : cài {@code PricingContext.best} trả mức giảm lớn nhất trong danh sách.
 *   Kiểm chứng: chạy q05_bestPicksLargest.
 *   Hoàn thành khi: q05_bestPicksLargest xanh.
 * <p>
 * Q6 [TỰ TRẢ LỜI] Có thể kết hợp Strategy với Spring DI ra sao?
 *   Bắt đầu   : viết khối ANSWER Q6.
 *   Hoàn thành khi: ANSWER Q6 nêu Spring inject một danh sách implementation theo cùng interface.
 */
public class Ex01_DiscountPolicy {

    /** Vấn đề Strategy giải quyết. */
    public enum Problem {
        MEMORY_USAGE,
        CHOOSING_BEHAVIOR_AMONG_VARIANTS,
        COMPILE_SPEED
    }

    /** Contract chung cho mọi cách giảm giá. */
    public interface DiscountPolicy {

        long discount(long totalCents);

        String name();
    }

    public static final class NormalDiscount implements DiscountPolicy {

        @Override
        public long discount(long totalCents) {
            // SOLUTION-BEGIN throw Q5
            return 0;
            // SOLUTION-END
        }

        @Override
        public String name() {
            // SOLUTION-BEGIN throw Q5
            return "normal";
            // SOLUTION-END
        }
    }

    public static final class VipDiscount implements DiscountPolicy {

        @Override
        public long discount(long totalCents) {
            // SOLUTION-BEGIN throw Q5
            return totalCents * 10 / 100;
            // SOLUTION-END
        }

        @Override
        public String name() {
            // SOLUTION-BEGIN throw Q5
            return "vip";
            // SOLUTION-END
        }
    }

    public static final class CampaignDiscount implements DiscountPolicy {

        @Override
        public long discount(long totalCents) {
            // SOLUTION-BEGIN throw Q5
            return totalCents >= 5_000 ? 1_000 : 0;
            // SOLUTION-END
        }

        @Override
        public String name() {
            // SOLUTION-BEGIN throw Q5
            return "campaign";
            // SOLUTION-END
        }
    }

    // Q1 — Strategy giải quyết vấn đề gì.
    static final Problem Q1_STRATEGY_PROBLEM = Problem.CHOOSING_BEHAVIOR_AMONG_VARIANTS; // SOLUTION-VALUE

    // Q2 — Strategy tốt hơn switch khi nào.
    static final Boolean Q2_STRATEGY_BEATS_SWITCH = true; // SOLUTION-VALUE

    // Q3 — hai nhánh if đã cần Strategy.
    static final Boolean Q3_TWO_BRANCHES_NEED_STRATEGY = false; // SOLUTION-VALUE

    /** Chọn policy tốt nhất trong danh sách cấu hình sẵn. */
    public static final class PricingContext {

        private final List<DiscountPolicy> policies;

        public PricingContext(List<DiscountPolicy> policies) {
            this.policies = List.copyOf(policies);
        }

        public DiscountPolicy best(long totalCents) {
            // SOLUTION-BEGIN throw Q5
            return policies.stream()
                    .max(java.util.Comparator.comparingLong(policy -> policy.discount(totalCents)))
                    .orElseThrow(() -> new NoSuchElementException("Không có policy nào."));
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Strategy tách một thuật toán khỏi nơi dùng nó.
 * Nơi dùng giữ cùng một lời gọi, còn biến thể nằm trong các implementation riêng.
 * Nhờ vậy chọn biến thể là việc cấu hình, không phải sửa luồng chính.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Strategy tốt hơn khi các nhánh đổi độc lập và thường xuyên, ví dụ thêm loại giảm giá mỗi chiến dịch.
 * Mỗi policy được test riêng, và switch không phải mở lại để thêm nhánh.
 * Nếu các nhánh ổn định và ít, chính switch trong một method lại dễ đọc hơn.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Không. Hai nhánh với điều kiện ổn định thì if là đủ.
 * Số nhánh không phải tiêu chí; tiêu chí là nhánh đó có phải điểm mở rộng thường xuyên đổi.
 * Tách sớm chỉ thêm lớp phải đọc mà không giảm được thay đổi nào.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Thêm một policy mới là thêm một class implement interface, caller không đổi.
 * Vì vậy luồng chính đóng với sửa đổi nhưng mở với mở rộng.
 * Kèm theo là policy mới test riêng, không chạy lại mọi nhánh cũ.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Định nghĩa một interface policy, đánh dấu mọi implementation là bean.
 * Spring inject cả List hoặc Map các bean cùng interface vào nơi cần.
 * Chọn policy lúc runtime trở thành tra cứu trong danh sách đó, không có switch trong code.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write the test**

```java
package phase03.d10_strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_DiscountPolicyTest {

    private static final String HINT_Q1 =
            "Strategy tách thuật toán khỏi nơi dùng nó, để chọn biến thể là việc cấu hình.";
    private static final String HINT_Q2 =
            "Tiêu chí là số nhánh thay đổi độc lập và độ thường xuyên, không phải số nhánh.";
    private static final String HINT_Q3 =
            "Điều kiện ổn định và ít nhánh thì if dễ đọc hơn một interface.";

    private static final Ex01_DiscountPolicy.PricingContext CONTEXT =
            new Ex01_DiscountPolicy.PricingContext(List.of(
                    new Ex01_DiscountPolicy.NormalDiscount(),
                    new Ex01_DiscountPolicy.VipDiscount(),
                    new Ex01_DiscountPolicy.CampaignDiscount()));

    @Test
    @DisplayName("Q1 dự đoán: Strategy giải quyết vấn đề gì")
    void q01_prediction() {
        assertPrediction("Q1_STRATEGY_PROBLEM",
                Ex01_DiscountPolicy.Problem.CHOOSING_BEHAVIOR_AMONG_VARIANTS,
                Ex01_DiscountPolicy.Q1_STRATEGY_PROBLEM, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: Strategy tốt hơn switch khi nào")
    void q02_prediction() {
        assertPrediction("Q2_STRATEGY_BEATS_SWITCH", true,
                Ex01_DiscountPolicy.Q2_STRATEGY_BEATS_SWITCH, HINT_Q2);
    }

    @Test
    @DisplayName("Q2: mỗi policy cho đúng mức giảm")
    void q02_policiesGiveExpectedDiscounts() {
        assertEquals(0, new Ex01_DiscountPolicy.NormalDiscount().discount(10_000));
        assertEquals(1_000, new Ex01_DiscountPolicy.VipDiscount().discount(10_000));
        assertEquals(1_000, new Ex01_DiscountPolicy.CampaignDiscount().discount(5_000));
        assertEquals(0, new Ex01_DiscountPolicy.CampaignDiscount().discount(4_999));
    }

    @Test
    @DisplayName("Q3 dự đoán: hai nhánh if đã cần Strategy")
    void q03_prediction() {
        assertPrediction("Q3_TWO_BRANCHES_NEED_STRATEGY", false,
                Ex01_DiscountPolicy.Q3_TWO_BRANCHES_NEED_STRATEGY, HINT_Q3);
    }

    @Test
    @DisplayName("Q5: best chọn policy giảm nhiều nhất")
    void q05_bestPicksLargest() {
        assertEquals("vip", CONTEXT.best(10_000).name());
        assertEquals("normal", CONTEXT.best(1_000).name());
    }

    @Test
    @DisplayName("Q5: không có policy thì báo rõ")
    void q05_emptyContextFails() {
        Ex01_DiscountPolicy.PricingContext empty = new Ex01_DiscountPolicy.PricingContext(List.of());
        assertThrows(java.util.NoSuchElementException.class, () -> empty.best(1_000));
    }
}
```

- [ ] **Step 3: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d10_strategy/*Test"`
Expected: FAIL — three `thay null`; `TODO Q5` on seven methods.
Run GREEN: same command → 6 tests pass.

- [ ] **Step 4: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d10_strategy -ExpectedQuestions 6`
Expected: `OK`.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d10_strategy phase-03-clean-code-design-patterns/src/test/java/phase03/d10_strategy
git commit -m "feat(phase03): add d10_strategy exercises"
```

---

### Task 13: d11_factory (5 câu)

**Files:**
- Create: `.../src/main/java/phase03/d11_factory/Ex01_ProviderSelection.java`, `Ex02_WhenNewIsFine.java`
- Test: `.../src/test/java/phase03/d11_factory/Ex01_ProviderSelectionTest.java`, `Ex02_WhenNewIsFineTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`.
- Produces: `Ex01_ProviderSelection.PaymentProvider` interface `String charge(long)`, `StripeProvider`, `MomoProvider`, `ProviderFactory.create(String) → PaymentProvider`; `Q1_FACTORY_PROBLEM`, `Q4_RUNTIME_DATA_SELECTION`, `Q5_GIANT_SWITCH_EXTENSIBLE`. `Ex02_WhenNewIsFine.Coupon(String code, int percent)` constructed with `new`, `Q2_NEW_IS_FINE`, `Q3_FACTORY_VS_BUILDER`.

- [ ] **Step 1: Write `Ex01_ProviderSelection.java`**

```java
package phase03.d11_factory;

/**
 * Factory — Bài 1: chọn nhà cung cấp
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 11 (Factory Pattern), câu 1, 4, 5.
 * Cần làm trước: d10_strategy.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_ProviderSelectionTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Factory giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q1_FACTORY_PROBLEM bằng một giá trị của {@code Problem}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu gom chỗ quyết định lớp cụ thể.
 * <p>
 * Q4 [DỰ ĐOÁN] Factory chọn implementation dựa trên runtime data thế nào?
 *   Bắt đầu   : điền Q4_RUNTIME_DATA_SELECTION, rồi cài {@code ProviderFactory.create}.
 *   Kiểm chứng: chạy q04_prediction và q04_createReturnsRightProvider.
 *   Hoàn thành khi: hai test xanh và ANSWER Q4 nêu factory đọc dữ liệu rồi quyết định.
 * <p>
 * Q5 [DỰ ĐOÁN] Factory quá lớn với switch khổng lồ có còn extensible không?
 *   Bắt đầu   : điền Q5_GIANT_SWITCH_EXTENSIBLE.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 nêu mỗi provider mới vẫn phải sửa switch.
 */
public class Ex01_ProviderSelection {

    /** Vấn đề Factory giải quyết. */
    public enum Problem {
        SPEED_UP_CHARGE,
        CENTRALIZE_WHICH_CONCRETE_CLASS_TO_CREATE,
        REMOVE_INTERFACES
    }

    /** Contract cho mọi nhà cung cấp thanh toán. */
    public interface PaymentProvider {

        String charge(long amountCents);
    }

    static final class StripeProvider implements PaymentProvider {

        @Override
        public String charge(long amountCents) {
            // SOLUTION-BEGIN throw Q4
            return "stripe:" + amountCents;
            // SOLUTION-END
        }
    }

    static final class MomoProvider implements PaymentProvider {

        @Override
        public String charge(long amountCents) {
            // SOLUTION-BEGIN throw Q4
            return "momo:" + amountCents;
            // SOLUTION-END
        }
    }

    // Q1 — Factory giải quyết vấn đề gì.
    static final Problem Q1_FACTORY_PROBLEM = Problem.CENTRALIZE_WHICH_CONCRETE_CLASS_TO_CREATE; // SOLUTION-VALUE

    // Q4 — factory chọn implementation theo dữ liệu runtime.
    static final Boolean Q4_RUNTIME_DATA_SELECTION = true; // SOLUTION-VALUE

    // Q5 — switch khổng lồ trong factory còn extensible.
    static final Boolean Q5_GIANT_SWITCH_EXTENSIBLE = false; // SOLUTION-VALUE

    /** Chọn provider theo tham số cấu hình. */
    public static final class ProviderFactory {

        private ProviderFactory() {
        }

        public static PaymentProvider create(String providerName) {
            // SOLUTION-BEGIN throw Q4
            return switch (providerName) {
                case "stripe" -> new StripeProvider();
                case "momo" -> new MomoProvider();
                default -> throw new IllegalArgumentException("Nhà cung cấp không hỗ trợ: " + providerName);
            };
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Factory gom chỗ quyết định lớp cụ thể vào một nơi.
 * Phần còn lại của hệ chỉ thấy interface, không rải new khắp code.
 * Đổi cách chọn hoặc thêm điều kiện chọn chỉ sửa một chỗ.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Factory nhận dữ liệu lúc chạy, ví dụ tên nhà cung cấp từ cấu hình hoặc từ đơn hàng.
 * Từ dữ liệu đó nó trả về một implementation của cùng interface.
 * Caller không biết lớp nào, nên đổi quy tắc chọn không lan ra ngoài factory.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Switch khổng lồ trong factory nghĩa là mỗi provider mới vẫn phải sửa chính factory.
 * Nó gom việc tạo object nhưng không mở rộng được, chỉ giấu vấn đề đi một chỗ.
 * Cách mở rộng là đăng ký nhà cung cấp vào một map, thêm provider là thêm một dòng đăng ký riêng.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write `Ex02_WhenNewIsFine.java`**

```java
package phase03.d11_factory;

/**
 * Factory — Bài 2: khi new trực tiếp là hợp lý
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 11, câu 2, 3.
 * Cần làm trước: Ex01_ProviderSelection.
 * Cách làm: chạy test trong Ex02_WhenNewIsFineTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q2 [DỰ ĐOÁN + CODE] Khi nào new trực tiếp hoàn toàn hợp lý?
 *   Bắt đầu   : điền Q2_NEW_IS_FINE, rồi cài {@code Coupon.apply} chỉ tính phần trăm.
 *   Kiểm chứng: chạy q02_prediction và q02_applyCoupon.
 *   Hoàn thành khi: hai test xanh và ANSWER Q2 nêu value object không có biến thể.
 * <p>
 * Q3 [DỰ ĐOÁN] Factory khác Builder thế nào?
 *   Bắt đầu   : điền Q3_FACTORY_VS_BUILDER bằng một giá trị của {@code Difference}.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nêu factory chọn lớp, builder ráp field.
 */
public class Ex02_WhenNewIsFine {

    /** Khác nhau giữa Factory và Builder. */
    public enum Difference {
        SAME_THING,
        FACTORY_CHOOSES_CLASS_BUILDER_ASSEMBLES_FIELDS,
        BUILDER_IS_FASTER
    }

    // Q2 — new trực tiếp hợp lý khi nào.
    static final Boolean Q2_NEW_IS_FINE = true; // SOLUTION-VALUE

    // Q3 — Factory khác Builder thế nào.
    static final Difference Q3_FACTORY_VS_BUILDER =
            Difference.FACTORY_CHOOSES_CLASS_BUILDER_ASSEMBLES_FIELDS; // SOLUTION-VALUE

    /** Value object nhỏ, không có biến thể, dựng bằng new là đủ. */
    public record Coupon(String code, int percent) {

        public Coupon {
            if (percent < 0 || percent > 100) {
                throw new IllegalArgumentException("Phần trăm phải trong khoảng 0..100: " + percent);
            }
        }

        public long apply(long totalCents) {
            // SOLUTION-BEGIN throw Q2
            return totalCents - totalCents * percent / 100;
            // SOLUTION-END
        }
    }
}

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * new trực tiếp hợp lý khi chỉ có một lớp cụ thể và không có quy tắc chọn nào.
 * Value object nhỏ như Coupon không có biến thể, nên factory chỉ thêm một lớp gọi.
 * Quy tắc kiểm tra nằm trong constructor, đủ để không tạo được object sai.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Factory trả về một trong nhiều lớp cùng interface, quyết định là chọn lớp nào.
 * Builder ráp nhiều field cho một lớp đã biết, quyết định là điền gì theo thứ tự nào.
 * Có thể dùng cùng nhau: builder ráp tham số, factory chọn lớp rồi gọi builder.
 * SOLUTION-END
 */
```

- [ ] **Step 3: Write both tests**

`Ex01_ProviderSelectionTest.java`:

```java
package phase03.d11_factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ProviderSelectionTest {

    private static final String HINT_Q1 =
            "Factory gom chỗ quyết định lớp cụ thể vào một nơi, phần còn lại chỉ thấy interface.";
    private static final String HINT_Q4 =
            "Dữ liệu lúc chạy là đầu vào để factory quyết định trả implementation nào.";
    private static final String HINT_Q5 =
            "Gom việc tạo object không làm nó mở rộng được nếu vẫn phải sửa switch cho mỗi provider mới.";

    @Test
    @DisplayName("Q1 dự đoán: Factory giải quyết vấn đề gì")
    void q01_prediction() {
        assertPrediction("Q1_FACTORY_PROBLEM",
                Ex01_ProviderSelection.Problem.CENTRALIZE_WHICH_CONCRETE_CLASS_TO_CREATE,
                Ex01_ProviderSelection.Q1_FACTORY_PROBLEM, HINT_Q1);
    }

    @Test
    @DisplayName("Q4 dự đoán: factory chọn theo dữ liệu runtime")
    void q04_prediction() {
        assertPrediction("Q4_RUNTIME_DATA_SELECTION", true,
                Ex01_ProviderSelection.Q4_RUNTIME_DATA_SELECTION, HINT_Q4);
    }

    @Test
    @DisplayName("Q4: create trả đúng provider")
    void q04_createReturnsRightProvider() {
        assertEquals("stripe:250", Ex01_ProviderSelection.ProviderFactory.create("stripe").charge(250));
        assertEquals("momo:250", Ex01_ProviderSelection.ProviderFactory.create("momo").charge(250));
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_ProviderSelection.ProviderFactory.create("paypal"));
    }

    @Test
    @DisplayName("Q5 dự đoán: switch khổng lồ còn extensible")
    void q05_prediction() {
        assertPrediction("Q5_GIANT_SWITCH_EXTENSIBLE", false,
                Ex01_ProviderSelection.Q5_GIANT_SWITCH_EXTENSIBLE, HINT_Q5);
    }
}
```

`Ex02_WhenNewIsFineTest.java`:

```java
package phase03.d11_factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_WhenNewIsFineTest {

    private static final String HINT_Q2 =
            "Một lớp cụ thể, không có biến thể, thì new là đủ; factory chỉ thêm một lớp gọi.";
    private static final String HINT_Q3 =
            "Factory chọn lớp nào; builder ráp field của lớp đã biết.";

    @Test
    @DisplayName("Q2 dự đoán: new trực tiếp hợp lý khi nào")
    void q02_prediction() {
        assertPrediction("Q2_NEW_IS_FINE", true, Ex02_WhenNewIsFine.Q2_NEW_IS_FINE, HINT_Q2);
    }

    @Test
    @DisplayName("Q2: Coupon áp phần trăm và chặn giá trị sai")
    void q02_applyCoupon() {
        assertEquals(900, new Ex02_WhenNewIsFine.Coupon("SALE10", 10).apply(1_000));
        assertThrows(IllegalArgumentException.class, () -> new Ex02_WhenNewIsFine.Coupon("BAD", 101));
    }

    @Test
    @DisplayName("Q3 dự đoán: Factory khác Builder thế nào")
    void q03_prediction() {
        assertPrediction("Q3_FACTORY_VS_BUILDER",
                Ex02_WhenNewIsFine.Difference.FACTORY_CHOOSES_CLASS_BUILDER_ASSEMBLES_FIELDS,
                Ex02_WhenNewIsFine.Q3_FACTORY_VS_BUILDER, HINT_Q3);
    }
}
```

- [ ] **Step 4: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d11_factory/*Test"`
Expected: FAIL — five `thay null`; `TODO Q2` on `Coupon.apply`; `TODO Q4` on `StripeProvider.charge`, `MomoProvider.charge`, `ProviderFactory.create`.
Run GREEN: same command → 7 tests pass.

- [ ] **Step 5: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d11_factory -ExpectedQuestions 5`
Expected: `OK`.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d11_factory phase-03-clean-code-design-patterns/src/test/java/phase03/d11_factory
git commit -m "feat(phase03): add d11_factory exercises"
```

---

### Task 14: d12_builder (5 câu)

**Files:**
- Create: `.../src/main/java/phase03/d12_builder/Ex01_UserBuilder.java`
- Test: `.../src/test/java/phase03/d12_builder/Ex01_UserBuilderTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`.
- Produces: `Ex01_UserBuilder.User` with private constructor and `static Builder builder()`; `User.Builder.name(String)`, `email(String)`, `age(int)`, `build() → User`; `Q1_TELESCOPING_SOLVED`, `Q2_BUILDER_VS_FACTORY`, `Q3_IMMUTABLE`, `Q4_TWO_FIELDS`, `Q5_VALIDATION_LOCATION`.

- [ ] **Step 1: Write the source**

```java
package phase03.d12_builder;

/**
 * Builder — Bài 1: User
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 12 (Builder Pattern), câu 1, 2, 3, 4, 5.
 * Cần làm trước: d11_factory.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_UserBuilderTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Builder giải quyết telescoping constructor thế nào?
 *   Bắt đầu   : điền Q1_TELESCOPING_SOLVED bằng một giá trị của {@code Fix}.
 *   Kiểm chứng: chạy q01_prediction. Ctrl+F12 trên {@code User} xem constructor.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu tên tham số thay vì vị trí.
 * <p>
 * Q2 [DỰ ĐOÁN] Builder và Factory khác nhau ở đâu?
 *   Bắt đầu   : điền Q2_BUILDER_VS_FACTORY bằng một giá trị của {@code Difference}.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu builder không chọn lớp.
 * <p>
 * Q3 [DỰ ĐOÁN + CODE] Builder có giúp object immutable không?
 *   Bắt đầu   : điền Q3_IMMUTABLE, rồi cài {@code Builder.build} tạo {@code User} với field final.
 *   Kiểm chứng: chạy q03_prediction và q03_buildIsImmutable.
 *   Hoàn thành khi: hai test xanh và ANSWER Q3 nêu builder tách quá trình ráp khỏi object đã ráp.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Builder có đáng dùng với object chỉ có hai field không?
 *   Bắt đầu   : viết khối ANSWER Q4.
 *   Hoàn thành khi: ANSWER Q4 nêu chi phí lớn hơn lợi ích với ít field và không có tham số tuỳ chọn.
 * <p>
 * Q5 [DỰ ĐOÁN] Validation nên xảy ra ở builder hay object constructor?
 *   Bắt đầu   : điền Q5_VALIDATION_LOCATION bằng một giá trị của {@code Location}.
 *   Kiểm chứng: chạy q05_prediction và q05_buildRejectsBadEmail.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 nêu constructor là chốt cuối, builder kiểm sớm.
 */
public class Ex01_UserBuilder {

    /** Builder khắc phục telescoping constructor bằng cách nào. */
    public enum Fix {
        FEWER_PARAMETERS,
        NAMED_STEPS_FOR_OPTIONAL_PARAMETERS,
        FASTER_CONSTRUCTION
    }

    /** Builder khác Factory ở đâu. */
    public enum Difference {
        SAME_THING,
        BUILDER_ASSEMBLES_FIELDS_FACTORY_CHOOSES_CLASS,
        BUILDER_IS_ALWAYS_FASTER
    }

    /** Nơi kiểm tra dữ liệu khi tạo object. */
    public enum Location {
        BUILDER_ONLY,
        OBJECT_CONSTRUCTOR,
        NOWHERE
    }

    // Q1 — builder giải quyết telescoping constructor thế nào.
    static final Fix Q1_TELESCOPING_SOLVED = Fix.NAMED_STEPS_FOR_OPTIONAL_PARAMETERS; // SOLUTION-VALUE

    // Q2 — Builder khác Factory ở đâu.
    static final Difference Q2_BUILDER_VS_FACTORY =
            Difference.BUILDER_ASSEMBLES_FIELDS_FACTORY_CHOOSES_CLASS; // SOLUTION-VALUE

    // Q3 — builder có giúp object immutable.
    static final Boolean Q3_IMMUTABLE = true; // SOLUTION-VALUE

    // Q5 — validation nên ở builder hay constructor.
    static final Location Q5_VALIDATION_LOCATION = Location.OBJECT_CONSTRUCTOR; // SOLUTION-VALUE

    /** Immutable: mọi field final, constructor private. */
    public static final class User {

        private final String name;
        private final String email;
        private final int age;

        private User(String name, String email, int age) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Tên không được trống.");
            }
            if (email == null || !email.contains("@")) {
                throw new IllegalArgumentException("Email không hợp lệ: " + email);
            }
            if (age < 0) {
                throw new IllegalArgumentException("Tuổi không được âm: " + age);
            }
            this.name = name;
            this.email = email;
            this.age = age;
        }

        public String name() {
            return name;
        }

        public String email() {
            return email;
        }

        public int age() {
            return age;
        }

        public static Builder builder() {
            return new Builder();
        }

        /** Ráp dần từng field, build mới tạo object. */
        public static final class Builder {

            private String name;
            private String email;
            private int age;

            public Builder name(String name) {
                // SOLUTION-BEGIN throw Q3
                this.name = name;
                return this;
                // SOLUTION-END
            }

            public Builder email(String email) {
                // SOLUTION-BEGIN throw Q3
                this.email = email;
                return this;
                // SOLUTION-END
            }

            public Builder age(int age) {
                // SOLUTION-BEGIN throw Q3
                this.age = age;
                return this;
                // SOLUTION-END
            }

            public User build() {
                // SOLUTION-BEGIN throw Q3
                return new User(name, email, age);
                // SOLUTION-END
            }
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Telescoping constructor là nhiều constructor với tham số tuỳ chọn, chỗ gọi phải nhớ thứ tự.
 * Builder cho mỗi tham số một method có tên, nên chỗ gọi đọc lên là hiểu.
 * Tham số tuỳ chọn bỏ qua được, không cần constructor riêng cho mỗi tổ hợp.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Builder ráp nhiều field cho một lớp đã biết, quyết định là điền gì theo thứ tự nào.
 * Factory chọn một trong nhiều lớp cùng interface, không quan tâm ráp field.
 * Có thể kết hợp: factory chọn lớp rồi trả về builder của lớp đó.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Builder giúp immutable bằng cách tách quá trình ráp khỏi object đã ráp.
 * Trong lúc ráp, field còn đổi được; build tạo object với mọi field final.
 * Object đã tạo không có setter, nên không ai sửa được sau đó.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Với hai field và không có tham số tuỳ chọn, builder thêm một lớp phải đọc và một lời gọi dài hơn.
 * Constructor hai tham số đọc lên đã rõ, không có thứ tự dễ nhầm.
 * Builder đáng dùng khi nhiều tham số tuỳ chọn hoặc validation cần nhiều bước.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Constructor của object là chốt cuối, mọi object đều đi qua đó dù tạo bằng builder hay cách khác.
 * Builder nên kiểm để báo lỗi sớm ngay tại bước đặt sai.
 * Nếu chỉ kiểm ở builder, một đường tạo khác có thể bỏ qua và tạo ra object sai.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write the test**

```java
package phase03.d12_builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase03.support.Predictions.assertPrediction;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_UserBuilderTest {

    private static final String HINT_Q1 =
            "Builder cho mỗi tham số một method có tên, nên chỗ gọi không phải nhớ thứ tự.";
    private static final String HINT_Q2 =
            "Builder ráp field của một lớp; factory chọn lớp nào trong nhiều lớp.";
    private static final String HINT_Q3 =
            "Object tạo xong có field final và không setter thì không sửa được nữa.";
    private static final String HINT_Q5 =
            "Mọi object đều đi qua constructor, nên đó là chốt cuối cho validation.";

    @Test
    @DisplayName("Q1 dự đoán: builder khắc phục telescoping thế nào")
    void q01_prediction() {
        assertPrediction("Q1_TELESCOPING_SOLVED",
                Ex01_UserBuilder.Fix.NAMED_STEPS_FOR_OPTIONAL_PARAMETERS,
                Ex01_UserBuilder.Q1_TELESCOPING_SOLVED, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: Builder khác Factory thế nào")
    void q02_prediction() {
        assertPrediction("Q2_BUILDER_VS_FACTORY",
                Ex01_UserBuilder.Difference.BUILDER_ASSEMBLES_FIELDS_FACTORY_CHOOSES_CLASS,
                Ex01_UserBuilder.Q2_BUILDER_VS_FACTORY, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: builder giúp object immutable")
    void q03_prediction() {
        assertPrediction("Q3_IMMUTABLE", true, Ex01_UserBuilder.Q3_IMMUTABLE, HINT_Q3);
    }

    @Test
    @DisplayName("Q3: build tạo object với mọi field final")
    void q03_buildIsImmutable() throws NoSuchFieldException {
        Ex01_UserBuilder.User user = Ex01_UserBuilder.User.builder()
                .name("Alice")
                .email("alice@example.com")
                .age(30)
                .build();
        assertEquals("Alice", user.name());
        assertEquals("alice@example.com", user.email());
        assertEquals(30, user.age());
        for (Field field : Ex01_UserBuilder.User.class.getDeclaredFields()) {
            if (!field.isSynthetic() && !Modifier.isStatic(field.getModifiers())) {
                assertTrue(Modifier.isFinal(field.getModifiers()),
                        "Field " + field.getName() + " phải là final.");
            }
        }
    }

    @Test
    @DisplayName("Q5 dự đoán: validation ở builder hay constructor")
    void q05_prediction() {
        assertPrediction("Q5_VALIDATION_LOCATION", Ex01_UserBuilder.Location.OBJECT_CONSTRUCTOR,
                Ex01_UserBuilder.Q5_VALIDATION_LOCATION, HINT_Q5);
    }

    @Test
    @DisplayName("Q5: build từ chối email sai và tên trống")
    void q05_buildRejectsBadEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_UserBuilder.User.builder().name("Bob").email("bob").age(1).build());
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_UserBuilder.User.builder().name(" ").email("b@x").age(1).build());
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_UserBuilder.User.builder().name("Bob").email("b@x").age(-1).build());
    }
}
```

- [ ] **Step 3: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d12_builder/*Test"`
Expected: FAIL — four `thay null`; `TODO Q3` on `name`, `email`, `age`, `build`.
Run GREEN: same command → 6 tests pass.

- [ ] **Step 4: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d12_builder -ExpectedQuestions 5`
Expected: `OK`.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d12_builder phase-03-clean-code-design-patterns/src/test/java/phase03/d12_builder
git commit -m "feat(phase03): add d12_builder exercises"
```

---

### Task 15: d13_adapter (5 câu)

**Files:**
- Create: `.../src/main/java/phase03/d13_adapter/Ex01_ProviderAdapters.java`
- Test: `.../src/test/java/phase03/d13_adapter/Ex01_ProviderAdaptersTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`.
- Produces: `Ex01_ProviderAdapters.LegacyStripeSdk` (vendored fake with `int makeCharge(int amountInCents, String currency)`, `boolean isOk()`, `void setOk(boolean)`, package-private `int lastCharge()`), `ProviderGateway` interface `String charge(long amountCents)`, `StripeAdapter implements ProviderGateway` wrapping the SDK, `Q1_ADAPTER_PROBLEM`, `Q2_ADAPTER_VS_DECORATOR`, `Q3_WRAP_SDK_BENEFIT`, `Q4_ADAPTER_HELPS_TESTING`.

- [ ] **Step 1: Write the source**

```java
package phase03.d13_adapter;

/**
 * Adapter — Bài 1: bọc SDK nhà cung cấp
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 13 (Adapter Pattern), câu 1, 2, 3, 4, 5.
 * Cần làm trước: d12_builder.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_ProviderAdaptersTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Adapter giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q1_ADAPTER_PROBLEM bằng một giá trị của {@code ProblemType}.
 *   Kiểm chứng: chạy q01_prediction. Ctrl+B trên {@code ProviderGateway} xem application gọi kiểu gì.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu chuyển interface hệ ngoài về interface mong muốn.
 * <p>
 * Q2 [DỰ ĐOÁN] Adapter khác Decorator thế nào?
 *   Bắt đầu   : điền Q2_ADAPTER_VS_DECORATOR bằng một giá trị của {@code Difference}.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu adapter đổi interface, decorator giữ interface.
 * <p>
 * Q3 [DỰ ĐOÁN] Vì sao wrap third-party SDK bằng adapter có lợi?
 *   Bắt đầu   : điền Q3_WRAP_SDK_BENEFIT.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nêu chỗ duy nhất phải sửa khi SDK đổi.
 * <p>
 * Q4 [DỰ ĐOÁN + CODE] Adapter giúp testing thế nào?
 *   Bắt đầu   : điền Q4_ADAPTER_HELPS_TESTING, rồi cài {@code StripeAdapter.charge}.
 *   Kiểm chứng: chạy q04_prediction và q04_adapterTranslatesCall.
 *   Hoàn thành khi: hai test xanh và ANSWER Q4 nêu test dùng fake theo interface của application.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Khi Stripe SDK đổi API, adapter giúp giới hạn blast radius ra sao?
 *   Bắt đầu   : viết khối ANSWER Q5.
 *   Hoàn thành khi: ANSWER Q5 nêu chỉ adapter và test của nó phải đổi.
 */
public class Ex01_ProviderAdapters {

    /** Vấn đề Adapter giải quyết. */
    public enum ProblemType {
        SLOWER_CALLS,
        EXTERNAL_INTERFACE_DIFFERS_FROM_APP_INTERFACE,
        MORE_CLASSES
    }

    /** Adapter khác Decorator ở đâu. */
    public enum Difference {
        SAME_THING,
        ADAPTER_CHANGES_INTERFACE_DECORATOR_KEEPS_IT,
        ADAPTER_IS_FASTER
    }

    public interface ProviderGateway {

        String charge(long amountCents);
    }

    /** Cho sẵn, không sửa. SDK giả có kiểu và cách gọi khác application. */
    public static final class LegacyStripeSdk {

        private int lastCharge;
        private boolean ok = true;

        public int makeCharge(int amountInCents, String currency) {
            this.lastCharge = amountInCents;
            return ok ? amountInCents : -1;
        }

        public boolean isOk() {
            return ok;
        }

        public void setOk(boolean ok) {
            this.ok = ok;
        }

        int lastCharge() {
            return lastCharge;
        }
    }

    // Q1 — Adapter giải quyết vấn đề gì.
    static final ProblemType Q1_ADAPTER_PROBLEM =
            ProblemType.EXTERNAL_INTERFACE_DIFFERS_FROM_APP_INTERFACE; // SOLUTION-VALUE

    // Q2 — Adapter khác Decorator ở đâu.
    static final Difference Q2_ADAPTER_VS_DECORATOR =
            Difference.ADAPTER_CHANGES_INTERFACE_DECORATOR_KEEPS_IT; // SOLUTION-VALUE

    // Q3 — bọc SDK có lợi gì.
    static final Boolean Q3_WRAP_SDK_BENEFIT = true; // SOLUTION-VALUE

    // Q4 — adapter giúp testing.
    static final Boolean Q4_ADAPTER_HELPS_TESTING = true; // SOLUTION-VALUE

    /** Chuyển cách gọi của SDK sang contract của application. */
    public static final class StripeAdapter implements ProviderGateway {

        private final LegacyStripeSdk sdk;

        public StripeAdapter(LegacyStripeSdk sdk) {
            this.sdk = sdk;
        }

        @Override
        public String charge(long amountCents) {
            // SOLUTION-BEGIN throw Q4
            int raw = sdk.makeCharge((int) amountCents, "vnd");
            if (raw < 0 || !sdk.isOk()) {
                throw new IllegalStateException("Stripe từ chối " + amountCents);
            }
            return "stripe:" + raw;
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Adapter chuyển interface của hệ ngoài thành interface mà application mong muốn.
 * Application chỉ biết ProviderGateway, không biết kiểu tham số hay cách báo lỗi của SDK.
 * Nhờ vậy đổi nhà cung cấp không lan vào nghiệp vụ.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Adapter đổi interface: đầu vào và đầu ra khác của cái bị bọc.
 * Decorator giữ nguyên interface và chỉ thêm hành vi trước hoặc sau lời gọi.
 * Vì vậy decorator ghép chuỗi được, còn adapter thường là điểm cuối của chuỗi.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Adapter là chỗ duy nhất biết SDK thật, nên khi SDK đổi API chỉ sửa ở đây.
 * Nghiệp vụ và test không phải đổi theo.
 * Nó cũng là chỗ để dịch kiểu lỗi của SDK sang lỗi có nghĩa của application.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Test thay adapter bằng một implementation của ProviderGateway, không cần SDK.
 * Đường lỗi như bị từ chối dựng được bằng một adapter giả luôn ném.
 * Không cần mạng, không cần khoá API, và test không phụ thuộc nhà cung cấp.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Blast radius giới hạn ở StripeAdapter và test của nó.
 * Nếu SDK đổi tên method hoặc đổi kiểu tham số, chỉ thân adapter đổi.
 * Nghiệp vụ gọi ProviderGateway nên không biết gì về thay đổi đó.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write the test**

```java
package phase03.d13_adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ProviderAdaptersTest {

    private static final String HINT_Q1 =
            "Adapter chuyển interface hệ ngoài về interface mà application mong muốn.";
    private static final String HINT_Q2 =
            "Adapter đổi interface; decorator giữ nguyên interface và thêm hành vi.";
    private static final String HINT_Q3 =
            "Adapter là chỗ duy nhất biết SDK, nên đổi SDK chỉ sửa một chỗ.";
    private static final String HINT_Q4 =
            "Có interface của application thì test thay bằng fake, không cần SDK hay mạng.";

    @Test
    @DisplayName("Q1 dự đoán: Adapter giải quyết vấn đề gì")
    void q01_prediction() {
        assertPrediction("Q1_ADAPTER_PROBLEM",
                Ex01_ProviderAdapters.ProblemType.EXTERNAL_INTERFACE_DIFFERS_FROM_APP_INTERFACE,
                Ex01_ProviderAdapters.Q1_ADAPTER_PROBLEM, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: Adapter khác Decorator thế nào")
    void q02_prediction() {
        assertPrediction("Q2_ADAPTER_VS_DECORATOR",
                Ex01_ProviderAdapters.Difference.ADAPTER_CHANGES_INTERFACE_DECORATOR_KEEPS_IT,
                Ex01_ProviderAdapters.Q2_ADAPTER_VS_DECORATOR, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: bọc SDK có lợi")
    void q03_prediction() {
        assertPrediction("Q3_WRAP_SDK_BENEFIT", true,
                Ex01_ProviderAdapters.Q3_WRAP_SDK_BENEFIT, HINT_Q3);
    }

    @Test
    @DisplayName("Q4 dự đoán: adapter giúp testing")
    void q04_prediction() {
        assertPrediction("Q4_ADAPTER_HELPS_TESTING", true,
                Ex01_ProviderAdapters.Q4_ADAPTER_HELPS_TESTING, HINT_Q4);
    }

    @Test
    @DisplayName("Q4: adapter dịch lời gọi sang SDK")
    void q04_adapterTranslatesCall() {
        Ex01_ProviderAdapters.LegacyStripeSdk sdk = new Ex01_ProviderAdapters.LegacyStripeSdk();
        Ex01_ProviderAdapters.ProviderGateway gateway = new Ex01_ProviderAdapters.StripeAdapter(sdk);
        assertEquals("stripe:4000", gateway.charge(4_000));
        assertEquals(4_000, sdk.lastCharge());

        sdk.setOk(false);
        assertThrows(IllegalStateException.class, () -> gateway.charge(4_000));
    }
}
```

- [ ] **Step 3: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d13_adapter/*Test"`
Expected: FAIL — four `thay null`; `TODO Q4` on `StripeAdapter.charge`.
Run GREEN: same command → 5 tests pass.

- [ ] **Step 4: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d13_adapter -ExpectedQuestions 5`
Expected: `OK`.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d13_adapter phase-03-clean-code-design-patterns/src/test/java/phase03/d13_adapter
git commit -m "feat(phase03): add d13_adapter exercises"
```

---

### Task 16: d14_decorator (5 câu)

**Files:**
- Create: `.../src/main/java/phase03/d14_decorator/Ex01_LoggingAndMetrics.java`, `Ex02_ChainOrder.java`
- Test: `.../src/test/java/phase03/d14_decorator/Ex01_LoggingAndMetricsTest.java`, `Ex02_ChainOrderTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`.
- Produces: `Ex01_LoggingAndMetrics.PaymentService` interface `String pay(long)`, `PlainPayment` returning `"paid:" + amount`, `LoggingPaymentService(PaymentService, List<String>)` and `MetricsPaymentService(PaymentService, List<Long>)` both implementing `PaymentService`; `Q1_DECORATOR_VS_INHERITANCE`, `Q2_DECORATOR_VS_PROXY`, `Q4_JAVA_IO`. `Ex02_ChainOrder.wrap(PaymentService, List<String>, List<Long>) → PaymentService`, `Q3_CHAINABLE`.

- [ ] **Step 1: Write `Ex01_LoggingAndMetrics.java`**

```java
package phase03.d14_decorator;

import java.util.List;

/**
 * Decorator — Bài 1: logging và metrics quanh cùng service
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 14 (Decorator Pattern), câu 1, 2, 3, 4, 5.
 * Cần làm trước: d13_adapter.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_LoggingAndMetricsTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Decorator khác inheritance thế nào?
 *   Bắt đầu   : điền Q1_DECORATOR_VS_INHERITANCE bằng một giá trị của {@code Difference}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu ghép lúc runtime thay vì nổ tổ hợp subclass.
 * <p>
 * Q2 [DỰ ĐOÁN] Decorator khác Proxy ở mục đích gì?
 *   Bắt đầu   : điền Q2_DECORATOR_VS_PROXY bằng một giá trị của {@code Difference2}.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu decorator thêm hành vi, proxy kiểm soát truy cập.
 * <p>
 * Q3 [CODE] Có thể chain nhiều decorator không?
 *   Bắt đầu   : cài {@code LoggingPaymentService.pay} và {@code MetricsPaymentService.pay}.
 *               Mỗi bên ghi nhận rồi hoặc trước khi gọi {@code inner.pay} và trả nguyên kết quả.
 *   Kiểm chứng: chạy q03_bothSinksRecorded.
 *   Hoàn thành khi: q03_bothSinksRecorded xanh.
 * <p>
 * Q4 [DỰ ĐOÁN] Java I/O sử dụng ý tưởng decorator như thế nào?
 *   Bắt đầu   : điền Q4_JAVA_IO bằng một giá trị của {@code IoIdea}.
 *   Kiểm chứng: chạy q04_prediction. Ctrl+N mở {@code BufferedInputStream} xem constructor.
 *   Hoàn thành khi: q04_prediction xanh và ANSWER Q4 nêu bọc một stream bằng stream khác cùng interface.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Decorator có thể làm call stack khó hiểu như thế nào?
 *   Bắt đầu   : viết khối ANSWER Q5.
 *   Hoàn thành khi: ANSWER Q5 nêu phải đọc cấu hình ghép để biết thứ tự chạy.
 */
public class Ex01_LoggingAndMetrics {

    /** Decorator khác inheritance ở đâu. */
    public enum Difference {
        SAME_THING,
        COMPOSE_AT_RUNTIME_VS_SUBCLASS_EXPLOSION,
        FASTER_CALLS
    }

    /** Decorator khác Proxy ở đâu. */
    public enum Difference2 {
        SAME_THING,
        ADDS_BEHAVIOR_VS_CONTROLS_ACCESS,
        PROXY_IS_FASTER
    }

    /** Ý tưởng decorator trong Java I/O. */
    public enum IoIdea {
        ONE_STREAM_FOR_EVERYTHING,
        WRAP_A_STREAM_WITH_ANOTHER_OF_THE_SAME_INTERFACE,
        STREAMS_USE_INHERITANCE_ONLY
    }

    public interface PaymentService {

        String pay(long amountCents);
    }

    /** Cho sẵn: service thật, không biết logging hay metrics. */
    public static final class PlainPayment implements PaymentService {

        @Override
        public String pay(long amountCents) {
            return "paid:" + amountCents;
        }
    }

    // Q1 — decorator khác inheritance.
    static final Difference Q1_DECORATOR_VS_INHERITANCE =
            Difference.COMPOSE_AT_RUNTIME_VS_SUBCLASS_EXPLOSION; // SOLUTION-VALUE

    // Q2 — decorator khác proxy.
    static final Difference2 Q2_DECORATOR_VS_PROXY =
            Difference2.ADDS_BEHAVIOR_VS_CONTROLS_ACCESS; // SOLUTION-VALUE

    // Q4 — ý tưởng decorator trong Java I/O.
    static final IoIdea Q4_JAVA_IO =
            IoIdea.WRAP_A_STREAM_WITH_ANOTHER_OF_THE_SAME_INTERFACE; // SOLUTION-VALUE

    /** Ghi log trước khi chuyển tiếp. */
    public static final class LoggingPaymentService implements PaymentService {

        private final PaymentService inner;
        private final List<String> sink;

        public LoggingPaymentService(PaymentService inner, List<String> sink) {
            this.inner = inner;
            this.sink = sink;
        }

        @Override
        public String pay(long amountCents) {
            // SOLUTION-BEGIN throw Q3
            sink.add("log:" + amountCents);
            return inner.pay(amountCents);
            // SOLUTION-END
        }
    }

    /** Ghi metric sau khi chuyển tiếp. */
    public static final class MetricsPaymentService implements PaymentService {

        private final PaymentService inner;
        private final List<Long> sink;

        public MetricsPaymentService(PaymentService inner, List<Long> sink) {
            this.inner = inner;
            this.sink = sink;
        }

        @Override
        public String pay(long amountCents) {
            // SOLUTION-BEGIN throw Q3
            String result = inner.pay(amountCents);
            sink.add(amountCents);
            return result;
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Inheritance phải tạo một subclass cho mỗi tổ hợp: log, metric, log+metric.
 * Decorator ghép lúc runtime bằng cách bọc service này trong service khác cùng interface.
 * Thêm một hành vi là thêm một lớp, không thêm tổ hợp cho các hành vi đã có.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Decorator thêm hành vi cho lời gọi và luôn chuyển tiếp tới đối tượng bị bọc.
 * Proxy kiểm soát truy cập: có thể chặn, hoãn, hoặc tạo đối tượng đích khi cần.
 * Trong thực tế hai cái nhìn giống nhau về code, khác nhau ở ý định.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Java I/O bọc một stream bằng một stream khác cùng interface, ví dụ BufferedInputStream bọc InputStream.
 * Mỗi lớp thêm một hành vi như đệm hoặc nén mà vẫn là InputStream.
 * Muốn tổ hợp thì lồng nhiều lớp, không tạo subclass cho từng tổ hợp.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Call stack có nhiều tầng cùng tên method, nên đọc stack khó biết tầng nào vừa chạy.
 * Thứ tự chạy phụ thuộc thứ tự ghép trong cấu hình, không nằm trong một file dễ thấy.
 * Debug phải nhớ cấu hình ghép, và một decorator quên chuyển tiếp sẽ im lặng cắt chuỗi.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write `Ex02_ChainOrder.java`**

```java
package phase03.d14_decorator;

import java.util.List;

/**
 * Decorator — Bài 2: thứ tự ghép chuỗi
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 14, câu 3.
 * Cần làm trước: Ex01_LoggingAndMetrics.
 * Cách làm: chạy test trong Ex02_ChainOrderTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q3 [DỰ ĐOÁN + CODE] Có thể chain nhiều decorator không?
 *   Bắt đầu   : điền Q3_CHAINABLE, rồi cài {@code wrap} theo thứ tự: trong cùng là service,
 *               bọc metrics, rồi bọc logging ngoài cùng.
 *   Kiểm chứng: chạy q03_prediction và q03_outerLogRunsFirst.
 *   Hoàn thành khi: hai test xanh và ANSWER Q3 nêu lớp ngoài cùng chạy trước.
 */
public class Ex02_ChainOrder {

    // Q3 — có ghép được nhiều decorator.
    static final Boolean Q3_CHAINABLE = true; // SOLUTION-VALUE

    /** Ghép metrics bên trong, logging bên ngoài. */
    public static Ex01_LoggingAndMetrics.PaymentService wrap(
            Ex01_LoggingAndMetrics.PaymentService inner, List<String> logSink, List<Long> metricSink) {
        // SOLUTION-BEGIN throw Q3
        Ex01_LoggingAndMetrics.PaymentService withMetrics =
                new Ex01_LoggingAndMetrics.MetricsPaymentService(inner, metricSink);
        return new Ex01_LoggingAndMetrics.LoggingPaymentService(withMetrics, logSink);
        // SOLUTION-END
    }
}

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Ghép được nhiều decorator vì mọi lớp cùng interface.
 * Thứ tự ghép quyết định thứ tự chạy: lớp ngoài cùng chạy trước khi gọi vào trong.
 * Vì vậy đổi thứ tự ghép là đổi hành vi quan sát được, dù cùng bộ decorator.
 * SOLUTION-END
 */
```

- [ ] **Step 3: Write both tests**

`Ex01_LoggingAndMetricsTest.java`:

```java
package phase03.d14_decorator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_LoggingAndMetricsTest {

    private static final String HINT_Q1 =
            "Inheritance nổ tổ hợp subclass; decorator ghép lúc runtime.";
    private static final String HINT_Q2 =
            "Decorator thêm hành vi và luôn chuyển tiếp; proxy kiểm soát truy cập.";
    private static final String HINT_Q4 =
            "Java I/O bọc stream bằng stream khác cùng interface.";

    @Test
    @DisplayName("Q1 dự đoán: decorator khác inheritance thế nào")
    void q01_prediction() {
        assertPrediction("Q1_DECORATOR_VS_INHERITANCE",
                Ex01_LoggingAndMetrics.Difference.COMPOSE_AT_RUNTIME_VS_SUBCLASS_EXPLOSION,
                Ex01_LoggingAndMetrics.Q1_DECORATOR_VS_INHERITANCE, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: decorator khác proxy ở đâu")
    void q02_prediction() {
        assertPrediction("Q2_DECORATOR_VS_PROXY",
                Ex01_LoggingAndMetrics.Difference2.ADDS_BEHAVIOR_VS_CONTROLS_ACCESS,
                Ex01_LoggingAndMetrics.Q2_DECORATOR_VS_PROXY, HINT_Q2);
    }

    @Test
    @DisplayName("Q3: hai decorator đều ghi nhận")
    void q03_bothSinksRecorded() {
        List<String> logs = new ArrayList<>();
        List<Long> metrics = new ArrayList<>();
        Ex01_LoggingAndMetrics.PaymentService service =
                new Ex01_LoggingAndMetrics.LoggingPaymentService(
                        new Ex01_LoggingAndMetrics.MetricsPaymentService(
                                new Ex01_LoggingAndMetrics.PlainPayment(), metrics),
                        logs);
        assertEquals("paid:4000", service.pay(4_000));
        assertEquals(List.of("log:4000"), logs);
        assertEquals(List.of(4_000L), metrics);
    }

    @Test
    @DisplayName("Q4 dự đoán: Java I/O dùng ý tưởng decorator thế nào")
    void q04_prediction() {
        assertPrediction("Q4_JAVA_IO",
                Ex01_LoggingAndMetrics.IoIdea.WRAP_A_STREAM_WITH_ANOTHER_OF_THE_SAME_INTERFACE,
                Ex01_LoggingAndMetrics.Q4_JAVA_IO, HINT_Q4);
    }
}
```

`Ex02_ChainOrderTest.java`:

```java
package phase03.d14_decorator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_ChainOrderTest {

    private static final String HINT_Q3 =
            "Mọi lớp cùng interface nên ghép được; lớp ngoài cùng chạy trước.";

    @Test
    @DisplayName("Q3 dự đoán: ghép được nhiều decorator")
    void q03_prediction() {
        assertPrediction("Q3_CHAINABLE", true, Ex02_ChainOrder.Q3_CHAINABLE, HINT_Q3);
    }

    @Test
    @DisplayName("Q3: logging ngoài cùng ghi trước metrics")
    void q03_outerLogRunsFirst() {
        List<String> logs = new ArrayList<>();
        List<Long> metrics = new ArrayList<>();
        Ex01_LoggingAndMetrics.PaymentService chain =
                Ex02_ChainOrder.wrap(new Ex01_LoggingAndMetrics.PlainPayment(), logs, metrics);
        assertEquals("paid:1000", chain.pay(1_000));
        assertEquals(List.of("log:1000"), logs);
        assertEquals(List.of(1_000L), metrics);
    }
}
```

- [ ] **Step 4: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d14_decorator/*Test"`
Expected: FAIL — four `thay null`; `TODO Q3` on `LoggingPaymentService.pay`, `MetricsPaymentService.pay`, `Ex02_ChainOrder.wrap`.
Run GREEN: same command → 6 tests pass.

- [ ] **Step 5: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d14_decorator -ExpectedQuestions 5`
Expected: `OK`.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d14_decorator phase-03-clean-code-design-patterns/src/test/java/phase03/d14_decorator
git commit -m "feat(phase03): add d14_decorator exercises"
```

---

### Task 17: d15_observer (6 câu)

**Files:**
- Create: `.../src/main/java/phase03/d15_observer/Ex01_OrderEvents.java`
- Test: `.../src/test/java/phase03/d15_observer/Ex01_OrderEventsTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`.
- Produces: `Ex01_OrderEvents.OrderListener` interface `void onOrder(String orderId)`, `OrderPublisher` with `subscribe(OrderListener)`, `unsubscribe(OrderListener)`, `listenerCount() → int`, `publish(String orderId)`; `Q1_OBSERVER_PROBLEM`, `Q2_OBSERVER_VS_KAFKA`, `Q3_ONE_LISTENER_FAILS`, `Q5_ORDERING`.

- [ ] **Step 1: Write the source**

```java
package phase03.d15_observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer — Bài 1: sự kiện đơn hàng
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 15 (Observer Pattern), câu 1, 2, 3, 4, 5, 6.
 * Cần làm trước: d14_decorator.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_OrderEventsTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Observer giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q1_OBSERVER_PROBLEM bằng một giá trị của {@code Problem}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu publisher không biết subscriber cụ thể.
 * <p>
 * Q2 [DỰ ĐOÁN] Observer khác message broker như Kafka thế nào?
 *   Bắt đầu   : điền Q2_OBSERVER_VS_KAFKA bằng một giá trị của {@code Difference}.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu trong process và mất khi process chết.
 * <p>
 * Q3 [DỰ ĐOÁN + CODE] Nếu một listener fail thì các listener khác nên xử lý thế nào?
 *   Bắt đầu   : điền Q3_ONE_LISTENER_FAILS, rồi cài {@code publish}. Một listener ném lỗi không được
 *               làm dừng các listener khác; lỗi phải được ghi lại và ném sau khi đã gọi hết.
 *   Kiểm chứng: chạy q03_prediction và q03_oneFailureDoesNotStopOthers.
 *   Hoàn thành khi: hai test xanh và ANSWER Q3 nêu cô lập lỗi theo từng listener.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Observer synchronous có thể ảnh hưởng latency ra sao?
 *   Bắt đầu   : viết khối ANSWER Q4.
 *   Hoàn thành khi: ANSWER Q4 nêu caller chờ tổng thời gian mọi listener.
 * <p>
 * Q5 [DỰ ĐOÁN] Event ordering có quan trọng không?
 *   Bắt đầu   : điền Q5_ORDERING bằng một giá trị của {@code Verdict}.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 nêu listener thấy cùng thứ tự với lúc publish.
 * <p>
 * Q6 [CODE] Listener không được unregister có thể gây vấn đề gì?
 *   Bắt đầu   : cài {@code unsubscribe}. Sau khi bỏ, listener đó không được nhận sự kiện nữa.
 *   Kiểm chứng: chạy q06_unsubscribedListenerMissesEvent.
 *   Hoàn thành khi: q06_unsubscribedListenerMissesEvent xanh và ANSWER Q6 nêu giữ tham chiếu làm rò rỉ.
 */
public class Ex01_OrderEvents {

    /** Vấn đề Observer giải quyết. */
    public enum Problem {
        FASTER_PUBLISH,
        PUBLISHER_DOES_NOT_KNOW_CONCRETE_SUBSCRIBERS,
        FEWER_CLASSES
    }

    /** Observer khác message broker ở đâu. */
    public enum Difference {
        SAME_THING,
        IN_PROCESS_AND_LOST_IF_PROCESS_DIES,
        KAFKA_IS_SYNCHRONOUS
    }

    /** Thứ tự sự kiện có quan trọng không. */
    public enum Verdict {
        NEVER_MATTERS,
        MATTERS_FOR_LISTENERS,
        ONLY_MATTERS_FOR_KAFKA
    }

    public interface OrderListener {

        void onOrder(String orderId);
    }

    // Q1 — Observer giải quyết vấn đề gì.
    static final Problem Q1_OBSERVER_PROBLEM =
            Problem.PUBLISHER_DOES_NOT_KNOW_CONCRETE_SUBSCRIBERS; // SOLUTION-VALUE

    // Q2 — Observer khác Kafka ở đâu.
    static final Difference Q2_OBSERVER_VS_KAFKA =
            Difference.IN_PROCESS_AND_LOST_IF_PROCESS_DIES; // SOLUTION-VALUE

    // Q3 — một listener lỗi thì các listener khác vẫn chạy.
    static final Boolean Q3_ONE_LISTENER_FAILS = true; // SOLUTION-VALUE

    // Q5 — thứ tự sự kiện có quan trọng.
    static final Verdict Q5_ORDERING = Verdict.MATTERS_FOR_LISTENERS; // SOLUTION-VALUE

    /** Gọi listener theo thứ tự đăng ký; lỗi được gom lại và ném sau. */
    public static final class OrderPublisher {

        private final List<OrderListener> listeners = new ArrayList<>();

        public void subscribe(OrderListener listener) {
            listeners.add(listener);
        }

        public void unsubscribe(OrderListener listener) {
            // SOLUTION-BEGIN throw Q6
            listeners.remove(listener);
            // SOLUTION-END
        }

        public int listenerCount() {
            return listeners.size();
        }

        public void publish(String orderId) {
            // SOLUTION-BEGIN throw Q3
            RuntimeException firstFailure = null;
            for (OrderListener listener : List.copyOf(listeners)) {
                try {
                    listener.onOrder(orderId);
                } catch (RuntimeException failure) {
                    if (firstFailure == null) {
                        firstFailure = failure;
                    } else {
                        firstFailure.addSuppressed(failure);
                    }
                }
            }
            if (firstFailure != null) {
                throw firstFailure;
            }
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Observer cho publisher thông báo nhiều subscriber mà không biết lớp cụ thể của họ.
 * Thêm subscriber là đăng ký thêm, không sửa publisher.
 * Nhờ vậy luồng chính không phụ thuộc số lượng hay loại người nghe.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Observer chạy trong cùng process và cùng bộ nhớ, lời gọi là gọi method.
 * Broker như Kafka nằm ngoài process, giữ message bền và cho nhiều consumer group.
 * Sự kiện observer mất khi process chết trước khi listener chạy xong.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Một listener lỗi không nên làm các listener khác bỏ lượt.
 * Mỗi lời gọi được bọc riêng, lỗi được gom lại và ném sau khi đã gọi hết.
 * Nếu để lỗi lan ngay, thứ tự đăng ký quyết định ai được nhận, rất khó đoán.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Observer synchronous làm caller chờ toàn bộ listener chạy xong.
 * Một listener chậm hoặc chờ I/O kéo latency của luồng chính.
 * Cách giảm là chỉ ghi sự kiện vào queue rồi xử lý ở luồng khác, đổi lại mất tính tức thời.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Có. Listener thấy sự kiện cùng thứ tự với lúc publish vì cùng một luồng.
 * Nếu xử lý bất đồng bộ, thứ tự đó không còn bảo đảm trừ khi có cơ chế riêng.
 * Nhiều nghiệp vụ, ví dụ created trước paid, phụ thuộc thứ tự này.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Listener không được unregister vẫn được publisher giữ tham chiếu.
 * Nếu listener đã hết dùng ở nơi khác, object không được thu hồi, đó là rò rỉ.
 * Publisher còn gọi vào listener đã chết logic, sinh lỗi hoặc việc thừa.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write the test**

```java
package phase03.d15_observer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_OrderEventsTest {

    private static final String HINT_Q1 =
            "Publisher không biết lớp cụ thể của subscriber; thêm subscriber là đăng ký thêm.";
    private static final String HINT_Q2 =
            "Observer sống trong process; broker giữ message bền ngoài process.";
    private static final String HINT_Q3 =
            "Lỗi của một listener phải được cô lập để các listener khác vẫn nhận sự kiện.";
    private static final String HINT_Q5 =
            "Observer synchronous cho listener thấy cùng thứ tự với lúc publish.";

    @Test
    @DisplayName("Q1 dự đoán: Observer giải quyết vấn đề gì")
    void q01_prediction() {
        assertPrediction("Q1_OBSERVER_PROBLEM",
                Ex01_OrderEvents.Problem.PUBLISHER_DOES_NOT_KNOW_CONCRETE_SUBSCRIBERS,
                Ex01_OrderEvents.Q1_OBSERVER_PROBLEM, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: Observer khác Kafka thế nào")
    void q02_prediction() {
        assertPrediction("Q2_OBSERVER_VS_KAFKA",
                Ex01_OrderEvents.Difference.IN_PROCESS_AND_LOST_IF_PROCESS_DIES,
                Ex01_OrderEvents.Q2_OBSERVER_VS_KAFKA, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: một listener lỗi vẫn gọi listener khác")
    void q03_prediction() {
        assertPrediction("Q3_ONE_LISTENER_FAILS", true,
                Ex01_OrderEvents.Q3_ONE_LISTENER_FAILS, HINT_Q3);
    }

    @Test
    @DisplayName("Q3: listener lỗi không chặn listener sau")
    void q03_oneFailureDoesNotStopOthers() {
        Ex01_OrderEvents.OrderPublisher publisher = new Ex01_OrderEvents.OrderPublisher();
        List<String> seen = new ArrayList<>();
        publisher.subscribe(orderId -> {
            throw new IllegalStateException("listener 1 hỏng");
        });
        publisher.subscribe(seen::add);
        assertThrows(IllegalStateException.class, () -> publisher.publish("o-1"));
        assertEquals(List.of("o-1"), seen, "Listener thứ hai vẫn phải nhận sự kiện.");
    }

    @Test
    @DisplayName("Q5 dự đoán: thứ tự sự kiện có quan trọng")
    void q05_prediction() {
        assertPrediction("Q5_ORDERING", Ex01_OrderEvents.Verdict.MATTERS_FOR_LISTENERS,
                Ex01_OrderEvents.Q5_ORDERING, HINT_Q5);
    }

    @Test
    @DisplayName("Q6: listener đã unsubscribe không nhận sự kiện")
    void q06_unsubscribedListenerMissesEvent() {
        Ex01_OrderEvents.OrderPublisher publisher = new Ex01_OrderEvents.OrderPublisher();
        List<String> seen = new ArrayList<>();
        Ex01_OrderEvents.OrderListener listener = seen::add;
        publisher.subscribe(listener);
        publisher.publish("o-1");
        assertEquals(1, publisher.listenerCount());
        publisher.unsubscribe(listener);
        assertEquals(0, publisher.listenerCount());
        publisher.publish("o-2");
        assertEquals(List.of("o-1"), seen, "Listener đã bỏ không được nhận o-2.");
    }
}
```

- [ ] **Step 3: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d15_observer/*Test"`
Expected: FAIL — four `thay null`; `TODO Q3` on `publish`; `TODO Q6` on `unsubscribe`.
Run GREEN: same command → 6 tests pass.

- [ ] **Step 4: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d15_observer -ExpectedQuestions 6`
Expected: `OK`.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d15_observer phase-03-clean-code-design-patterns/src/test/java/phase03/d15_observer
git commit -m "feat(phase03): add d15_observer exercises"
```

---

### Task 18: d16_template_method (5 câu)

**Files:**
- Create: `.../src/main/java/phase03/d16_template_method/Ex01_Importer.java`, `Ex02_OverridableSteps.java`
- Test: `.../src/test/java/phase03/d16_template_method/Ex01_ImporterTest.java`, `Ex02_OverridableStepsTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`.
- Produces: `Ex01_Importer.Importer` abstract class with `final List<String> importData(String raw)` and abstract `read`, `validate`, `persist`; `CsvImporter`, `JsonImporter`; `Q1_TEMPLATE_PROBLEM`, `Q2_TEMPLATE_VS_STRATEGY`, `Q3_USES_INHERITANCE`. `Ex02_OverridableSteps.HookImporter` with a protected no-op `afterPersist` hook and `Q4_TOO_MANY_HOOKS`, `Q5_STRATEGY_MORE_FLEXIBLE`.

- [ ] **Step 1: Write `Ex01_Importer.java`**

```java
package phase03.d16_template_method;

import java.util.ArrayList;
import java.util.List;

/**
 * Template Method — Bài 1: Importer
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 16 (Template Method), câu 1, 2, 3.
 * Cần làm trước: d15_observer.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_ImporterTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Template Method giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q1_TEMPLATE_PROBLEM bằng một giá trị của {@code Problem}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu lịch chạy cố định, bước cho subclass.
 * <p>
 * Q2 [DỰ ĐOÁN] Template Method khác Strategy thế nào?
 *   Bắt đầu   : điền Q2_TEMPLATE_VS_STRATEGY bằng một giá trị của {@code Difference}.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu inheritance so với composition.
 * <p>
 * Q3 [DỰ ĐOÁN] Pattern này dựa trên inheritance hay composition?
 *   Bắt đầu   : điền Q3_USES_INHERITANCE.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh.
 * <p>
 * Q4 [CODE] Khi có quá nhiều override points thì vấn đề gì xảy ra?
 *   Bắt đầu   : cài {@code Importer.importData} gọi ba bước theo đúng thứ tự read, validate, persist,
 *               và cài ba bước của {@code CsvImporter} và {@code JsonImporter}.
 *   Kiểm chứng: chạy q04_importDataRunsSteps, q04_csvAndJsonShareSkeleton. Ctrl+F12 trên
 *               {@code CsvImporter} xem chỉ có ba override.
 *   Hoàn thành khi: hai test xanh; ANSWER Q4 nêu subclass phải hiểu thứ tự và trạng thái của các bước khác.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Khi nào Strategy linh hoạt hơn Template Method?
 *   Bắt đầu   : viết khối ANSWER Q5.
 *   Hoàn thành khi: ANSWER Q5 nêu đổi lịch chạy hoặc chọn bước lúc runtime.
 */
public class Ex01_Importer {

    /** Vấn đề Template Method giải quyết. */
    public enum Problem {
        FASTER_IO,
        FIXED_ALGORITHM_WITH_VARIABLE_STEPS,
        FEWER_CLASSES
    }

    /** Template Method khác Strategy ở đâu. */
    public enum Difference {
        SAME_THING,
        INHERITANCE_VS_COMPOSITION,
        TEMPLATE_IS_FASTER
    }

    // Q1 — Template Method giải quyết vấn đề gì.
    static final Problem Q1_TEMPLATE_PROBLEM = Problem.FIXED_ALGORITHM_WITH_VARIABLE_STEPS; // SOLUTION-VALUE

    // Q2 — Template Method khác Strategy ở đâu.
    static final Difference Q2_TEMPLATE_VS_STRATEGY = Difference.INHERITANCE_VS_COMPOSITION; // SOLUTION-VALUE

    // Q3 — Template Method dựa trên inheritance.
    static final Boolean Q3_USES_INHERITANCE = true; // SOLUTION-VALUE

    /** Class cha giữ lịch chạy; subclass chỉ cài ba bước. */
    public abstract static class Importer {

        /** Lịch cố định; subclass không override. */
        public final List<String> importData(String raw) {
            // SOLUTION-BEGIN throw Q4
            List<String> rows = read(raw);
            List<String> valid = new ArrayList<>();
            for (String row : rows) {
                valid.add(validate(row));
            }
            List<String> persisted = new ArrayList<>();
            for (String row : valid) {
                persisted.add(persist(row));
            }
            return persisted;
            // SOLUTION-END
        }

        protected abstract List<String> read(String raw);

        protected abstract String validate(String row);

        protected abstract String persist(String valid);
    }

    public static final class CsvImporter extends Importer {

        @Override
        protected List<String> read(String raw) {
            // SOLUTION-BEGIN throw Q4
            return List.of(raw.split(","));
            // SOLUTION-END
        }

        @Override
        protected String validate(String row) {
            // SOLUTION-BEGIN throw Q4
            return row.strip();
            // SOLUTION-END
        }

        @Override
        protected String persist(String valid) {
            // SOLUTION-BEGIN throw Q4
            return "csv:" + valid;
            // SOLUTION-END
        }
    }

    public static final class JsonImporter extends Importer {

        @Override
        protected List<String> read(String raw) {
            // SOLUTION-BEGIN throw Q4
            return List.of(raw.replace("[", "").replace("]", "").split(","));
            // SOLUTION-END
        }

        @Override
        protected String validate(String row) {
            // SOLUTION-BEGIN throw Q4
            return row.strip();
            // SOLUTION-END
        }

        @Override
        protected String persist(String valid) {
            // SOLUTION-BEGIN throw Q4
            return "json:" + valid;
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Template Method cố định lịch chạy trong class cha và cho subclass cài từng bước.
 * Mọi biến thể đi qua cùng một bộ khung, nên thứ tự không bị lệch giữa các subclass.
 * Phần chung nằm một chỗ, không lặp lại ở mỗi implementation.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Template Method dùng inheritance: subclass override các bước, class cha giữ lịch.
 * Strategy dùng composition: caller giữ một object bước và tự gọi theo lịch của mình.
 * Template Method cố định lịch, Strategy cho đổi lịch và đổi bộ bước lúc runtime.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Dựa trên inheritance. Class cha định nghĩa bộ khung, subclass thay từng bước.
 * Đó cũng là lý do pattern này khó đổi lịch chạy mà không sửa class cha.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Nhiều override point làm subclass phải hiểu thứ tự và trạng thái chung giữa các bước.
 * Class cha không còn giữ được bất biến vì bất kỳ bước nào cũng có thể thay đổi.
 * Đọc một subclass không đủ biết hành vi vì phần lớn nằm ở class cha.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Strategy linh hoạt hơn khi cần đổi lịch chạy hoặc chọn bộ bước lúc runtime.
 * Nó cũng cho phép một class dùng nhiều bộ bước khác nhau theo cấu hình.
 * Template Method phù hợp khi lịch chạy thật sự cố định cho mọi biến thể.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write `Ex02_OverridableSteps.java`**

```java
package phase03.d16_template_method;

import java.util.ArrayList;
import java.util.List;

/**
 * Template Method — Bài 2: hook và số override point
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 16, câu 4, 5.
 * Cần làm trước: Ex01_Importer.
 * Cách làm: chạy test trong Ex02_OverridableStepsTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q4 [DỰ ĐOÁN + CODE] Quá nhiều override point gây vấn đề gì?
 *   Bắt đầu   : điền Q4_TOO_MANY_HOOKS, rồi cài {@code HookImporter.importData} gọi read, validate,
 *               persist, rồi {@code afterPersist} — hook mặc định không làm gì.
 *   Kiểm chứng: chạy q04_prediction và q04_hookRunsAfterPersist.
 *   Hoàn thành khi: hai test xanh và ANSWER Q4 nêu mỗi hook thêm một trạng thái subclass phải hiểu.
 * <p>
 * Q5 [DỰ ĐOÁN] Khi nào Strategy linh hoạt hơn Template Method?
 *   Bắt đầu   : điền Q5_STRATEGY_MORE_FLEXIBLE bằng một giá trị của {@code When}.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 nêu đổi lịch chạy lúc runtime.
 */
public class Ex02_OverridableSteps {

    /** Khi Strategy linh hoạt hơn. */
    public enum When {
        NEVER,
        WHEN_ORDER_OR_STEP_SET_CHANGES_AT_RUNTIME,
        WHEN_THERE_ARE_MANY_SUBCLASSES
    }

    // Q4 — quá nhiều hook gây vấn đề gì.
    static final Boolean Q4_TOO_MANY_HOOKS = true; // SOLUTION-VALUE

    // Q5 — Strategy linh hoạt hơn khi nào.
    static final When Q5_STRATEGY_MORE_FLEXIBLE =
            When.WHEN_ORDER_OR_STEP_SET_CHANGES_AT_RUNTIME; // SOLUTION-VALUE

    /** Có thêm một hook tuỳ chọn sau bước persist. */
    public abstract static class HookImporter {

        public final List<String> importData(String raw) {
            // SOLUTION-BEGIN throw Q4
            List<String> rows = read(raw);
            List<String> valid = new ArrayList<>();
            for (String row : rows) {
                valid.add(validate(row));
            }
            List<String> persisted = new ArrayList<>();
            for (String row : valid) {
                persisted.add(persist(row));
            }
            afterPersist(persisted);
            return persisted;
            // SOLUTION-END
        }

        /** Hook mặc định không làm gì; subclass có thể override. */
        protected void afterPersist(List<String> persisted) {
        }

        protected abstract List<String> read(String raw);

        protected abstract String validate(String row);

        protected abstract String persist(String valid);
    }
}

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Mỗi hook thêm một điểm mà subclass có thể thay đổi, và một trạng thái subclass phải hiểu.
 * Class cha mất khả năng bảo đảm bất biến vì không biết hook nào chạy.
 * Hook nên ít và có mục đích rõ; nhiều hook là dấu hiệu nên chuyển sang composition.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Strategy linh hoạt hơn khi thứ tự chạy hoặc tập bước đổi theo cấu hình lúc runtime.
 * Nó cho một class dùng nhiều bộ bước khác nhau mà không tạo thêm subclass.
 * Template Method chỉ đổi được nội dung từng bước, không đổi được bộ khung.
 * SOLUTION-END
 */
```

- [ ] **Step 3: Write both tests**

`Ex01_ImporterTest.java`:

```java
package phase03.d16_template_method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ImporterTest {

    private static final String HINT_Q1 =
            "Template Method cố định lịch chạy trong class cha và cho subclass cài từng bước.";
    private static final String HINT_Q2 =
            "Template Method dùng inheritance; Strategy dùng composition.";
    private static final String HINT_Q3 =
            "Class cha gọi ngược lên subclass qua các method abstract.";

    @Test
    @DisplayName("Q1 dự đoán: Template Method giải quyết vấn đề gì")
    void q01_prediction() {
        assertPrediction("Q1_TEMPLATE_PROBLEM",
                Ex01_Importer.Problem.FIXED_ALGORITHM_WITH_VARIABLE_STEPS,
                Ex01_Importer.Q1_TEMPLATE_PROBLEM, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: Template Method khác Strategy thế nào")
    void q02_prediction() {
        assertPrediction("Q2_TEMPLATE_VS_STRATEGY",
                Ex01_Importer.Difference.INHERITANCE_VS_COMPOSITION,
                Ex01_Importer.Q2_TEMPLATE_VS_STRATEGY, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: pattern dựa trên inheritance")
    void q03_prediction() {
        assertPrediction("Q3_USES_INHERITANCE", true, Ex01_Importer.Q3_USES_INHERITANCE, HINT_Q3);
    }

    @Test
    @DisplayName("Q4: importData chạy ba bước theo đúng thứ tự")
    void q04_importDataRunsSteps() {
        assertEquals(java.util.List.of("csv:a", "csv:b"),
                new Ex01_Importer.CsvImporter().importData("a, b"));
    }

    @Test
    @DisplayName("Q4: CSV và JSON dùng chung bộ khung, khác bước")
    void q04_csvAndJsonShareSkeleton() {
        assertEquals(java.util.List.of("csv:a", "csv:b"),
                new Ex01_Importer.CsvImporter().importData("a,b"));
        assertEquals(java.util.List.of("json:a", "json:b"),
                new Ex01_Importer.JsonImporter().importData("[a,b]"));
    }
}
```

`Ex02_OverridableStepsTest.java`:

```java
package phase03.d16_template_method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_OverridableStepsTest {

    private static final String HINT_Q4 =
            "Mỗi hook thêm một trạng thái subclass phải hiểu và một chỗ class cha không kiểm soát được.";
    private static final String HINT_Q5 =
            "Strategy đổi được lịch chạy và tập bước lúc runtime; Template Method chỉ đổi nội dung bước.";

    static final class RecordingImporter extends Ex02_OverridableSteps.HookImporter {

        final List<String> hooks = new ArrayList<>();

        @Override
        protected List<String> read(String raw) {
            return List.of(raw.split(","));
        }

        @Override
        protected String validate(String row) {
            return row.strip();
        }

        @Override
        protected String persist(String valid) {
            return "csv:" + valid;
        }

        @Override
        protected void afterPersist(List<String> persisted) {
            hooks.addAll(persisted);
        }
    }

    @Test
    @DisplayName("Q4 dự đoán: quá nhiều hook gây vấn đề")
    void q04_prediction() {
        assertPrediction("Q4_TOO_MANY_HOOKS", true,
                Ex02_OverridableSteps.Q4_TOO_MANY_HOOKS, HINT_Q4);
    }

    @Test
    @DisplayName("Q4: hook chạy sau persist")
    void q04_hookRunsAfterPersist() {
        RecordingImporter importer = new RecordingImporter();
        assertEquals(List.of("csv:a", "csv:b"), importer.importData("a,b"));
        assertEquals(List.of("csv:a", "csv:b"), importer.hooks);
    }

    @Test
    @DisplayName("Q5 dự đoán: Strategy linh hoạt hơn khi nào")
    void q05_prediction() {
        assertPrediction("Q5_STRATEGY_MORE_FLEXIBLE",
                Ex02_OverridableSteps.When.WHEN_ORDER_OR_STEP_SET_CHANGES_AT_RUNTIME,
                Ex02_OverridableSteps.Q5_STRATEGY_MORE_FLEXIBLE, HINT_Q5);
    }
}
```

- [ ] **Step 4: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d16_template_method/*Test"`
Expected: FAIL — five `thay null`; `TODO Q4` on `Importer.importData`, six subclass steps, `HookImporter.importData`.
Run GREEN: same command → 8 tests pass.

- [ ] **Step 5: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d16_template_method -ExpectedQuestions 5`
Expected: `OK`.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d16_template_method phase-03-clean-code-design-patterns/src/test/java/phase03/d16_template_method
git commit -m "feat(phase03): add d16_template_method exercises"
```

---

### Task 19: d17_proxy (5 câu)

**Files:**
- Create: `.../src/main/java/phase03/d17_proxy/Ex01_ManualProxy.java`, `Ex02_SelfInvocation.java`
- Test: `.../src/test/java/phase03/d17_proxy/Ex01_ManualProxyTest.java`, `Ex02_SelfInvocationTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`.
- Produces: `Ex01_ManualProxy.Account` interface `void deposit(long)`, `String balance()`, `RealAccount` (records calls in `List<String> audit`), `GuardProxy(Account, String role)` allowing `admin` and rejecting otherwise with `SecurityException`; `Q1_PROXY_VS_DECORATOR`, `Q2_TRANSACTIONAL_PROXY`, `Q5_CROSS_CUTTING`. `Ex02_SelfInvocation.SelfInvoking` with `outer()` calling `this.inner()` — the self-invocation problem, plus `Q3_SELF_INVOCATION`, `Q4_LAZY_PROXY`.

- [ ] **Step 1: Write `Ex01_ManualProxy.java`**

```java
package phase03.d17_proxy;

import java.util.ArrayList;
import java.util.List;

/**
 * Proxy — Bài 1: kiểm soát truy cập
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 17 (Proxy Pattern), câu 1, 2, 5.
 * Cần làm trước: d16_template_method.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_ManualProxyTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Proxy khác Decorator thế nào?
 *   Bắt đầu   : điền Q1_PROXY_VS_DECORATOR bằng một giá trị của {@code Difference}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu proxy kiểm soát truy cập.
 * <p>
 * Q2 [DỰ ĐOÁN] Spring {@code @Transactional} có liên quan proxy như thế nào?
 *   Bắt đầu   : điền Q2_TRANSACTIONAL_PROXY bằng một giá trị của {@code Mechanism}.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu Spring bọc bean trong proxy để mở và đóng transaction.
 * <p>
 * Q3 [CODE] Proxy có thể thêm cross-cutting concern nào?
 *   Bắt đầu   : cài {@code GuardProxy.deposit} và {@code GuardProxy.balance}. Vai trò không phải
 *               {@code admin} thì ném {@code SecurityException} và không gọi tới account thật.
 *   Kiểm chứng: chạy q03_adminPassesAndOthersBlocked.
 *   Hoàn thành khi: q03_adminPassesAndOthersBlocked xanh.
 * <p>
 * Q5 [DỰ ĐOÁN] Proxy có thể thêm cross-cutting concern nào?
 *   Bắt đầu   : điền Q5_CROSS_CUTTING bằng một giá trị của {@code Concern}.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh.
 */
public class Ex01_ManualProxy {

    /** Proxy khác Decorator ở đâu. */
    public enum Difference {
        SAME_THING,
        CONTROLS_ACCESS_VS_ADDS_BEHAVIOR,
        PROXY_IS_SLOWER
    }

    /** Cơ chế đằng sau @Transactional. */
    public enum Mechanism {
        COMPILER_REWRITE,
        PROXY_AROUND_THE_BEAN,
        DATABASE_FEATURE
    }

    /** Cross-cutting concern proxy hay thêm. */
    public enum Concern {
        BUSINESS_PRICING,
        SECURITY_TRANSACTION_LAZY_LOADING,
        DOMAIN_VALIDATION
    }

    public interface Account {

        void deposit(long amountCents);

        String balance();
    }

    /** Cho sẵn: account thật, ghi lại các lời gọi. */
    public static final class RealAccount implements Account {

        public final List<String> audit = new ArrayList<>();
        private long balance;

        @Override
        public void deposit(long amountCents) {
            audit.add("deposit:" + amountCents);
            balance += amountCents;
        }

        @Override
        public String balance() {
            audit.add("balance");
            return "real:" + balance;
        }
    }

    // Q1 — proxy khác decorator.
    static final Difference Q1_PROXY_VS_DECORATOR =
            Difference.CONTROLS_ACCESS_VS_ADDS_BEHAVIOR; // SOLUTION-VALUE

    // Q2 — @Transactional liên quan proxy thế nào.
    static final Mechanism Q2_TRANSACTIONAL_PROXY = Mechanism.PROXY_AROUND_THE_BEAN; // SOLUTION-VALUE

    // Q5 — proxy hay thêm cross-cutting concern nào.
    static final Concern Q5_CROSS_CUTTING = Concern.SECURITY_TRANSACTION_LAZY_LOADING; // SOLUTION-VALUE

    /** Kiểm tra vai trò trước khi cho lời gọi đi qua. */
    public static final class GuardProxy implements Account {

        private final Account target;
        private final String role;

        public GuardProxy(Account target, String role) {
            this.target = target;
            this.role = role;
        }

        @Override
        public void deposit(long amountCents) {
            // SOLUTION-BEGIN throw Q3
            requireAdmin();
            target.deposit(amountCents);
            // SOLUTION-END
        }

        @Override
        public String balance() {
            // SOLUTION-BEGIN throw Q3
            requireAdmin();
            return target.balance();
            // SOLUTION-END
        }

        private void requireAdmin() {
            if (!"admin".equals(role)) {
                throw new SecurityException("Không có quyền: " + role);
            }
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Proxy kiểm soát truy cập: nó quyết định có cho lời gọi tới đích hay không, hoặc tạo đích khi cần.
 * Decorator luôn chuyển tiếp và chỉ thêm hành vi trước hoặc sau.
 * Code hai cái có thể giống nhau; khác nhau ở ý định và ở việc proxy có thể chặn.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Spring bọc bean cần transaction trong một proxy sinh lúc chạy.
 * Lời gọi từ ngoài đi qua proxy, proxy mở transaction rồi mới gọi method thật.
 * Transaction chỉ bắt đầu khi lời gọi đi qua proxy.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Proxy hay thêm security, transaction, lazy loading, logging và remote call.
 * Đó là những concern không thuộc nghiệp vụ nhưng phải áp dụng quanh nhiều method.
 * Chúng đúng kiểu proxy vì cần quyết định trước khi hoặc sau khi lời gọi tới đích.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write `Ex02_SelfInvocation.java`**

```java
package phase03.d17_proxy;

/**
 * Proxy — Bài 2: self-invocation và lazy loading
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 17, câu 3, 4.
 * Cần làm trước: Ex01_ManualProxy.
 * Cách làm: chạy test trong Ex02_SelfInvocationTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q3 [DỰ ĐOÁN + CODE] Self-invocation có thể gây vấn đề với proxy-based AOP vì sao?
 *   Bắt đầu   : điền Q3_SELF_INVOCATION bằng một giá trị của {@code Reason}, rồi cài
 *               {@code SelfInvoking.outer} gọi {@code this.inner()}.
 *   Kiểm chứng: chạy q03_prediction và q03_selfInvocationBypassesProxy.
 *   Hoàn thành khi: hai test xanh và ANSWER Q3 nêu lời gọi nội bộ không đi qua proxy.
 * <p>
 * Q4 [DỰ ĐOÁN] Lazy-loading proxy trong Hibernate hoạt động ở mức khái niệm thế nào?
 *   Bắt đầu   : điền Q4_LAZY_PROXY bằng một giá trị của {@code Idea}.
 *   Kiểm chứng: chạy q04_prediction.
 *   Hoàn thành khi: q04_prediction xanh và ANSWER Q4 nêu proxy giữ dữ liệu tối thiểu và nạp khi có lời gọi.
 */
public class Ex02_SelfInvocation {

    /** Vì sao self-invocation không qua proxy. */
    public enum Reason {
        SAME_THREAD,
        CALL_GOES_DIRECTLY_ON_THE_TARGET_OBJECT,
        PROXY_IS_OPTIONAL
    }

    /** Ý tưởng lazy-loading proxy. */
    public enum Idea {
        LOAD_EVERYTHING_EAGERLY,
        PROXY_HOLDS_MINIMAL_DATA_AND_LOADS_ON_CALL,
        NO_PROXY_IS_USED
    }

    // Q3 — self-invocation không đi qua proxy.
    static final Reason Q3_SELF_INVOCATION = Reason.CALL_GOES_DIRECTLY_ON_THE_TARGET_OBJECT; // SOLUTION-VALUE

    // Q4 — lazy-loading proxy hoạt động thế nào.
    static final Idea Q4_LAZY_PROXY = Idea.PROXY_HOLDS_MINIMAL_DATA_AND_LOADS_ON_CALL; // SOLUTION-VALUE

    /** Cho sẵn: mô phỏng lời gọi nội bộ, không qua proxy nào. */
    public static final class SelfInvoking {

        public final java.util.List<String> calls = new java.util.ArrayList<>();

        public String outer() {
            // SOLUTION-BEGIN throw Q3
            return "outer->" + this.inner();
            // SOLUTION-END
        }

        public String inner() {
            calls.add("inner");
            return "inner";
        }
    }
}

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Self-invocation là lời gọi method trên chính object đích, ví dụ this.inner().
 * Nó không đi qua proxy vì proxy chỉ chặn được lời gọi từ ngoài vào.
 * Vì vậy @Transactional trên inner() không có tác dụng khi outer() gọi nó từ bên trong.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Proxy lazy-loading giữ dữ liệu tối thiểu, thường chỉ khoá chính.
 * Khi có lời gọi tới một field chưa nạp, proxy mới truy vấn database và thay vào giá trị thật.
 * Nhờ vậy quan hệ không bị nạp nếu code không dùng tới nó.
 * SOLUTION-END
 */
```

- [ ] **Step 3: Write both tests**

`Ex01_ManualProxyTest.java`:

```java
package phase03.d17_proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ManualProxyTest {

    private static final String HINT_Q1 =
            "Proxy quyết định có cho lời gọi tới đích hay không; decorator luôn chuyển tiếp.";
    private static final String HINT_Q2 =
            "Spring transaction hoạt động bằng proxy bọc quanh bean; transaction chỉ mở khi lời gọi đi qua proxy.";
    private static final String HINT_Q5 =
            "Security, transaction, lazy loading, logging và remote call là các concern đi quanh nhiều method.";

    @Test
    @DisplayName("Q1 dự đoán: proxy khác decorator ở đâu")
    void q01_prediction() {
        assertPrediction("Q1_PROXY_VS_DECORATOR",
                Ex01_ManualProxy.Difference.CONTROLS_ACCESS_VS_ADDS_BEHAVIOR,
                Ex01_ManualProxy.Q1_PROXY_VS_DECORATOR, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: @Transactional liên quan proxy thế nào")
    void q02_prediction() {
        assertPrediction("Q2_TRANSACTIONAL_PROXY", Ex01_ManualProxy.Mechanism.PROXY_AROUND_THE_BEAN,
                Ex01_ManualProxy.Q2_TRANSACTIONAL_PROXY, HINT_Q2);
    }

    @Test
    @DisplayName("Q3: admin đi qua, vai trò khác bị chặn và không chạm đích")
    void q03_adminPassesAndOthersBlocked() {
        Ex01_ManualProxy.RealAccount real = new Ex01_ManualProxy.RealAccount();
        Ex01_ManualProxy.Account adminProxy = new Ex01_ManualProxy.GuardProxy(real, "admin");
        adminProxy.deposit(1_000);
        assertEquals("real:1000", adminProxy.balance());

        Ex01_ManualProxy.RealAccount blocked = new Ex01_ManualProxy.RealAccount();
        Ex01_ManualProxy.Account guestProxy = new Ex01_ManualProxy.GuardProxy(blocked, "guest");
        assertThrows(SecurityException.class, () -> guestProxy.deposit(1_000));
        assertTrue(blocked.audit.isEmpty(), "Proxy chặn thì đích không được gọi.");
    }

    @Test
    @DisplayName("Q5 dự đoán: proxy hay thêm concern nào")
    void q05_prediction() {
        assertPrediction("Q5_CROSS_CUTTING",
                Ex01_ManualProxy.Concern.SECURITY_TRANSACTION_LAZY_LOADING,
                Ex01_ManualProxy.Q5_CROSS_CUTTING, HINT_Q5);
    }
}
```

`Ex02_SelfInvocationTest.java`:

```java
package phase03.d17_proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_SelfInvocationTest {

    private static final String HINT_Q3 =
            "this.inner() gọi thẳng trên object đích, không đi qua proxy nên AOP không áp dụng.";
    private static final String HINT_Q4 =
            "Proxy lazy giữ dữ liệu tối thiểu và truy vấn thêm khi có lời gọi tới field chưa nạp.";

    @Test
    @DisplayName("Q3 dự đoán: vì sao self-invocation không qua proxy")
    void q03_prediction() {
        assertPrediction("Q3_SELF_INVOCATION",
                Ex02_SelfInvocation.Reason.CALL_GOES_DIRECTLY_ON_THE_TARGET_OBJECT,
                Ex02_SelfInvocation.Q3_SELF_INVOCATION, HINT_Q3);
    }

    @Test
    @DisplayName("Q3: lời gọi nội bộ chạy thẳng trên object")
    void q03_selfInvocationBypassesProxy() {
        Ex02_SelfInvocation.SelfInvoking target = new Ex02_SelfInvocation.SelfInvoking();
        assertEquals("outer->inner", target.outer());
        assertEquals(1, target.calls.size());
    }

    @Test
    @DisplayName("Q4 dự đoán: lazy-loading proxy hoạt động thế nào")
    void q04_prediction() {
        assertPrediction("Q4_LAZY_PROXY",
                Ex02_SelfInvocation.Idea.PROXY_HOLDS_MINIMAL_DATA_AND_LOADS_ON_CALL,
                Ex02_SelfInvocation.Q4_LAZY_PROXY, HINT_Q4);
    }
}
```

- [ ] **Step 4: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d17_proxy/*Test"`
Expected: FAIL — five `thay null`; `TODO Q3` on `GuardProxy.deposit`, `GuardProxy.balance`, `SelfInvoking.outer`.
Run GREEN: same command → 7 tests pass.

- [ ] **Step 5: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d17_proxy -ExpectedQuestions 5`
Expected: `OK`.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d17_proxy phase-03-clean-code-design-patterns/src/test/java/phase03/d17_proxy
git commit -m "feat(phase03): add d17_proxy exercises"
```

---

### Task 20: d18_singleton (7 câu)

**Files:**
- Create: `.../src/main/java/phase03/d18_singleton/Ex01_EnumSingletonAndState.java`, `Ex02_ScopeVsPattern.java`
- Test: `.../src/test/java/phase03/d18_singleton/Ex01_EnumSingletonAndStateTest.java`, `Ex02_ScopeVsPatternTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`.
- Produces: `Ex01_EnumSingletonAndState.ConfigHolder` enum singleton with `holder()` and a mutable `Map<String, String> overrides`; `Q1_SINGLETON_PROBLEM`, `Q2_SINGLETON_GLOBAL_STATE`, `Q3_SINGLETON_TESTING_PAIN`, `Q4_SINGLETON_THREAD_SAFE`, `Q7_ENUM_SINGLETON`. `Ex02_ScopeVsPattern.MutableCounterService` (mutable field, `count()`, `increment()`), `Q5_SPRING_SINGLETON_SAME_AS_GOF`, `Q6_MUTABLE_FIELD_IN_SINGLETON_BEAN`.

- [ ] **Step 1: Write `Ex01_EnumSingletonAndState.java`**

```java
package phase03.d18_singleton;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton — Bài 1: enum singleton và trạng thái
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 18 (Singleton), câu 1, 2, 3, 4, 7.
 * Cần làm trước: d17_proxy.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_EnumSingletonAndStateTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Singleton giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q1_SINGLETON_PROBLEM bằng một giá trị của {@code Problem}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu một instance dùng chung.
 * <p>
 * Q2 [DỰ ĐOÁN] Vì sao Singleton thường bị xem là global state?
 *   Bắt đầu   : điền Q2_SINGLETON_GLOBAL_STATE.
 *   Kiểm chứng: chạy q02_prediction và q02_stateLeaksBetweenCalls.
 *   Hoàn thành khi: hai test xanh và ANSWER Q2 nêu mọi chỗ đọc cùng một biến ẩn.
 * <p>
 * Q3 [DỰ ĐOÁN] Singleton gây khó testing như thế nào?
 *   Bắt đầu   : điền Q3_SINGLETON_TESTING_PAIN.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nêu không thay được bằng fake và test phụ thuộc thứ tự.
 * <p>
 * Q4 [DỰ ĐOÁN] Singleton có mặc định thread-safe không?
 *   Bắt đầu   : điền Q4_SINGLETON_THREAD_SAFE.
 *   Kiểm chứng: chạy q04_prediction.
 *   Hoàn thành khi: q04_prediction xanh và ANSWER Q4 nêu lazy init không đồng bộ có thể tạo hai instance.
 * <p>
 * Q7 [CODE] Enum singleton có ưu điểm gì trong Java?
 *   Bắt đầu   : cài {@code ConfigHolder.put}, {@code get} và {@code clear}.
 *   Kiểm chứng: chạy q07_enumSingletonSingleInstance và q07_sharedStateVisible.
 *   Hoàn thành khi: hai test xanh và ANSWER Q7 nêu serialization và reflection không tạo thêm instance.
 */
public class Ex01_EnumSingletonAndState {

    /** Vấn đề Singleton giải quyết. */
    public enum Problem {
        FASTER_ALLOCATION,
        SINGLE_SHARED_INSTANCE,
        FEWER_CLASSES
    }

    // Q1 — Singleton giải quyết vấn đề gì.
    static final Problem Q1_SINGLETON_PROBLEM = Problem.SINGLE_SHARED_INSTANCE; // SOLUTION-VALUE

    // Q2 — vì sao Singleton là global state.
    static final Boolean Q2_SINGLETON_GLOBAL_STATE = true; // SOLUTION-VALUE

    // Q3 — Singleton gây khó testing.
    static final Boolean Q3_SINGLETON_TESTING_PAIN = true; // SOLUTION-VALUE

    // Q4 — Singleton có mặc định thread-safe.
    static final Boolean Q4_SINGLETON_THREAD_SAFE = false; // SOLUTION-VALUE

    /** Enum singleton: JVM bảo đảm chỉ một instance, kể cả khi bị serialize hay reflect. */
    public enum ConfigHolder {

        INSTANCE;

        private final Map<String, String> overrides = new HashMap<>();

        public void put(String key, String value) {
            // SOLUTION-BEGIN throw Q7
            overrides.put(key, value);
            // SOLUTION-END
        }

        public String get(String key) {
            // SOLUTION-BEGIN throw Q7
            return overrides.get(key);
            // SOLUTION-END
        }

        public void clear() {
            // SOLUTION-BEGIN throw Q7
            overrides.clear();
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Singleton bảo đảm chỉ một instance cho cả chương trình.
 * Nó dùng khi tài nguyên thật sự chỉ có một, ví dụ một cấu hình đọc một lần.
 * Nếu chỉ cần chia sẻ object, truyền nó qua constructor rõ ràng hơn.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Vì instance đó là biến ẩn mà mọi chỗ đều đọc và ghi được.
 * Không thấy nó trong signature nên không biết method nào phụ thuộc vào nó.
 * Trạng thái cũ từ lời gọi trước còn nguyên ở lời gọi sau.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Test không thay được Singleton bằng fake vì không có điểm chèn.
 * Mọi test dùng chung một instance nên trạng thái rò từ test này sang test khác.
 * Thứ tự chạy test trở thành điều kiện đúng, và test hay đỏ một cách khó hiểu.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Không. Bản lazy-init không đồng bộ có thể để hai thread cùng tạo instance.
 * Muốn đúng phải thêm synchronized hoặc dùng holder class, và cả hai tốn thêm một chút.
 * Enum singleton tránh vấn đề này vì việc khởi tạo do JVM thực hiện an toàn.
 * SOLUTION-END
 */

/* ANSWER Q7:
 * SOLUTION-BEGIN
 * Enum singleton chỉ có một hằng, nên JVM bảo đảm đúng một instance.
 * Serialization trả về cùng hằng thay vì tạo object mới.
 * Reflection cũng không gọi được constructor của enum, nên không phá được tính duy nhất.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write `Ex02_ScopeVsPattern.java`**

```java
package phase03.d18_singleton;

/**
 * Singleton — Bài 2: scope của container và mutable field
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 18, câu 5, 6.
 * Cần làm trước: Ex01_EnumSingletonAndState.
 * Cách làm: chạy test trong Ex02_ScopeVsPatternTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q5 [DỰ ĐOÁN] Spring singleton bean có giống GoF Singleton hoàn toàn không?
 *   Bắt đầu   : điền Q5_SPRING_SINGLETON_SAME_AS_GOF bằng một giá trị của {@code Verdict}.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 nêu scope quản lý bởi container, không phải static toàn cục.
 * <p>
 * Q6 [DỰ ĐOÁN + CODE] Một Spring singleton service chứa mutable field có vấn đề gì?
 *   Bắt đầu   : điền Q6_MUTABLE_FIELD_IN_SINGLETON_BEAN, rồi cài {@code MutableCounterService.increment}
 *               và {@code count} để lộ trạng thái dùng chung giữa các lời gọi.
 *   Kiểm chứng: chạy q06_prediction và q06_sharedFieldAcrossCallers.
 *   Hoàn thành khi: hai test xanh và ANSWER Q6 nêu nhiều request cùng đụng một field.
 */
public class Ex02_ScopeVsPattern {

    /** Spring singleton bean có giống GoF Singleton. */
    public enum Verdict {
        YES_IDENTICAL,
        SAME_SCOPE_DIFFERENT_OWNERSHIP,
        NO_BEAN_IS_ALWAYS_NEW
    }

    // Q5 — Spring singleton bean có giống GoF Singleton hoàn toàn.
    static final Verdict Q5_SPRING_SINGLETON_SAME_AS_GOF =
            Verdict.SAME_SCOPE_DIFFERENT_OWNERSHIP; // SOLUTION-VALUE

    // Q6 — mutable field trong singleton bean có vấn đề.
    static final Boolean Q6_MUTABLE_FIELD_IN_SINGLETON_BEAN = true; // SOLUTION-VALUE

    /** Cho sẵn cấu trúc; field mutable là điểm sai cần thấy. */
    public static final class MutableCounterService {

        private int requests;

        public int increment() {
            // SOLUTION-BEGIN throw Q6
            return ++requests;
            // SOLUTION-END
        }

        public int count() {
            // SOLUTION-BEGIN throw Q6
            return requests;
            // SOLUTION-END
        }
    }
}

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Không hoàn toàn giống. Cả hai đều chỉ có một instance trong phạm vi của mình.
 * GoF Singleton tự quản việc tạo instance bằng static và thường truy cập qua static getter.
 * Spring bean do container tạo và giữ; object không biết mình là singleton, nên vẫn inject và test được.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Bean singleton dùng chung một instance cho mọi request.
 * Mutable field trên instance đó là trạng thái chia sẻ giữa các request không liên quan.
 * Hai request đồng thời có thể đọc và ghi cùng field, sinh kết quả sai và khó tái hiện.
 * Service singleton nên không trạng thái, hoặc giữ trạng thái trong biến cục bộ.
 * SOLUTION-END
 */
```

- [ ] **Step 3: Write both tests**

`Ex01_EnumSingletonAndStateTest.java`:

```java
package phase03.d18_singleton;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_EnumSingletonAndStateTest {

    private static final String HINT_Q1 =
            "Singleton bảo đảm một instance dùng chung; nếu chỉ cần chia sẻ thì truyền qua constructor rõ hơn.";
    private static final String HINT_Q2 =
            "Biến ẩn mà mọi chỗ đọc ghi được chính là global state.";
    private static final String HINT_Q3 =
            "Không có điểm chèn fake, và trạng thái rò giữa các test.";
    private static final String HINT_Q4 =
            "Lazy-init không đồng bộ có thể để hai thread cùng tạo instance.";
    private static final String HINT_Q7 =
            "Enum chỉ có một hằng, serialization trả cùng hằng, reflection không gọi được constructor enum.";

    @AfterEach
    void clearHolder() {
        Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE.clear();
    }

    @Test
    @DisplayName("Q1 dự đoán: Singleton giải quyết vấn đề gì")
    void q01_prediction() {
        assertPrediction("Q1_SINGLETON_PROBLEM",
                Ex01_EnumSingletonAndState.Problem.SINGLE_SHARED_INSTANCE,
                Ex01_EnumSingletonAndState.Q1_SINGLETON_PROBLEM, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: Singleton là global state")
    void q02_prediction() {
        assertPrediction("Q2_SINGLETON_GLOBAL_STATE", true,
                Ex01_EnumSingletonAndState.Q2_SINGLETON_GLOBAL_STATE, HINT_Q2);
    }

    @Test
    @DisplayName("Q2: trạng thái rò từ lời gọi này sang lời gọi khác")
    void q02_stateLeaksBetweenCalls() {
        Ex01_EnumSingletonAndState.ConfigHolder holder =
                Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE;
        holder.put("region", "ap-southeast-1");
        assertEquals("ap-southeast-1",
                Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE.get("region"),
                "Instance khác phải thấy cùng trạng thái — đó là global state.");
    }

    @Test
    @DisplayName("Q3 dự đoán: Singleton khó testing")
    void q03_prediction() {
        assertPrediction("Q3_SINGLETON_TESTING_PAIN", true,
                Ex01_EnumSingletonAndState.Q3_SINGLETON_TESTING_PAIN, HINT_Q3);
    }

    @Test
    @DisplayName("Q4 dự đoán: Singleton mặc định thread-safe")
    void q04_prediction() {
        assertPrediction("Q4_SINGLETON_THREAD_SAFE", false,
                Ex01_EnumSingletonAndState.Q4_SINGLETON_THREAD_SAFE, HINT_Q4);
    }

    @Test
    @DisplayName("Q7: enum singleton chỉ một instance")
    void q07_enumSingletonSingleInstance() {
        assertSame(Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE,
                Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE);
        assertEquals(1, Ex01_EnumSingletonAndState.ConfigHolder.values().length);
    }

    @Test
    @DisplayName("Q7: trạng thái dùng chung giữa hai lời gọi")
    void q07_sharedStateVisible() {
        Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE.put("k", "v1");
        Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE.put("k", "v2");
        assertEquals("v2", Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE.get("k"));
        Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE.clear();
        assertEquals(null, Ex01_EnumSingletonAndState.ConfigHolder.INSTANCE.get("k"));
    }
}
```

`Ex02_ScopeVsPatternTest.java`:

```java
package phase03.d18_singleton;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_ScopeVsPatternTest {

    private static final String HINT_Q5 =
            "Hai cái cùng scope một instance, khác ở chỗ ai tạo và giữ instance: container hay static.";
    private static final String HINT_Q6 =
            "Bean singleton dùng chung một instance cho mọi request; field mutable là trạng thái chia sẻ giữa các request.";

    @Test
    @DisplayName("Q5 dự đoán: Spring singleton bean có giống GoF Singleton")
    void q05_prediction() {
        assertPrediction("Q5_SPRING_SINGLETON_SAME_AS_GOF",
                Ex02_ScopeVsPattern.Verdict.SAME_SCOPE_DIFFERENT_OWNERSHIP,
                Ex02_ScopeVsPattern.Q5_SPRING_SINGLETON_SAME_AS_GOF, HINT_Q5);
    }

    @Test
    @DisplayName("Q6 dự đoán: mutable field trong singleton bean có vấn đề")
    void q06_prediction() {
        assertPrediction("Q6_MUTABLE_FIELD_IN_SINGLETON_BEAN", true,
                Ex02_ScopeVsPattern.Q6_MUTABLE_FIELD_IN_SINGLETON_BEAN, HINT_Q6);
    }

    @Test
    @DisplayName("Q6: hai caller dùng chung một field, trạng thái cộng dồn")
    void q06_sharedFieldAcrossCallers() {
        Ex02_ScopeVsPattern.MutableCounterService shared = new Ex02_ScopeVsPattern.MutableCounterService();
        assertEquals(1, shared.increment());
        assertEquals(2, shared.increment());
        assertEquals(2, shared.count(), "Field nằm trên instance dùng chung nên các caller thấy lẫn nhau.");
    }
}
```

- [ ] **Step 4: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d18_singleton/*Test"`
Expected: FAIL — six `thay null`; `TODO Q6` on two counter methods; `TODO Q7` on three holder methods.
Run GREEN: same command → 10 tests pass.

- [ ] **Step 5: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d18_singleton -ExpectedQuestions 7`
Expected: `OK`.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d18_singleton phase-03-clean-code-design-patterns/src/test/java/phase03/d18_singleton
git commit -m "feat(phase03): add d18_singleton exercises"
```

---

### Task 21: d19_clean_code (8 câu)

**Files:**
- Create: `.../src/main/java/phase03/d19_clean_code/Ex01_NamingAndFunctions.java`, `Ex02_ErrorHandling.java`
- Test: `.../src/test/java/phase03/d19_clean_code/Ex01_NamingAndFunctionsTest.java`, `Ex02_ErrorHandlingTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`.
- Produces: `Ex01_NamingAndFunctions.LineItem`, `describe(LineItem)`, `totalWithShipping(List<LineItem>, int)`, `Q1_CLEAN_CODE_MEANS_SHORT`, `Q2_HUNDRED_LINE_METHOD`, `Q3_DUPLICATE_BETTER_THAN_PREMATURE`, `Q4_MORE_COMMENTS`, `Q5_BOOLEAN_PARAMETER`, `Q6_PROCESS_NAME`. `Ex02_ErrorHandling.RateProvider` throwing `RateUnavailableException`, `quote(...)`, `readFirstLine(Path)`, `Q7_TRANSLATE_BOUNDARY`, `Q8_CATCH_AND_CONTINUE`.

- [ ] **Step 1: Write `Ex01_NamingAndFunctions.java`**

```java
package phase03.d19_clean_code;

import java.util.List;

/**
 * Clean Code — Bài 1: tên và hàm
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 19 (Clean Code Fundamentals), câu 1, 2, 3, 4, 5, 6.
 * Cần làm trước: d12_builder.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_NamingAndFunctionsTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Clean code có đồng nghĩa code ngắn không?
 *   Bắt đầu   : điền Q1_CLEAN_CODE_MEANS_SHORT.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu đọc hiểu nhanh hơn là ít dòng.
 * <p>
 * Q2 [DỰ ĐOÁN] Một method 100 dòng có luôn sai không?
 *   Bắt đầu   : điền Q2_HUNDRED_LINE_METHOD bằng một giá trị của {@code Verdict}.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu tiêu chí là số trách nhiệm, không phải số dòng.
 * <p>
 * Q3 [DỰ ĐOÁN] Khi nào duplicate code tốt hơn premature abstraction?
 *   Bắt đầu   : điền Q3_DUPLICATE_BETTER_THAN_PREMATURE.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nêu hai đoạn giống nhau nhưng đổi vì lý do khác nhau.
 * <p>
 * Q4 [DỰ ĐOÁN] Comment nhiều có phải code tốt không?
 *   Bắt đầu   : điền Q4_MORE_COMMENTS bằng một giá trị của {@code Verdict}.
 *   Kiểm chứng: chạy q04_prediction.
 *   Hoàn thành khi: q04_prediction xanh và ANSWER Q4 nêu comment giải thích why, không lặp what.
 * <p>
 * Q5 [DỰ ĐOÁN] Boolean parameter thường báo hiệu vấn đề thiết kế gì?
 *   Bắt đầu   : điền Q5_BOOLEAN_PARAMETER bằng một giá trị của {@code Signal}.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 nêu method đang làm hai việc.
 * <p>
 * Q6 [CODE] Method tên {@code process()} cho biết vấn đề gì?
 *   Bắt đầu   : cài {@code describe} và {@code totalWithShipping} với tên nói rõ việc và đơn vị.
 *               Không đặt tên chung chung; {@code process} cho sẵn là ví dụ xấu, đừng sửa.
 *   Kiểm chứng: chạy q06_describeNamesUnit và q06_totalWithShipping.
 *   Hoàn thành khi: hai test xanh và ANSWER Q6 nêu tên không cho biết method trả gì.
 */
public class Ex01_NamingAndFunctions {

    /** Method 100 dòng có luôn sai không. */
    public enum Verdict {
        ALWAYS_WRONG,
        DEPENDS_ON_RESPONSIBILITIES,
        NEVER_WRONG
    }

    /** Boolean parameter báo hiệu gì. */
    public enum Signal {
        FASTER_RUNTIME,
        METHOD_DOES_TWO_THINGS,
        WEAK_TYPING
    }

    public record LineItem(String sku, long unitCents, int qty) {
    }

    // Q1 — clean code có đồng nghĩa code ngắn.
    static final Boolean Q1_CLEAN_CODE_MEANS_SHORT = false; // SOLUTION-VALUE

    // Q2 — method 100 dòng có luôn sai.
    static final Verdict Q2_HUNDRED_LINE_METHOD = Verdict.DEPENDS_ON_RESPONSIBILITIES; // SOLUTION-VALUE

    // Q3 — duplicate tốt hơn premature abstraction khi nào.
    static final Boolean Q3_DUPLICATE_BETTER_THAN_PREMATURE = true; // SOLUTION-VALUE

    // Q4 — comment nhiều có phải tốt.
    static final Verdict Q4_MORE_COMMENTS = Verdict.DEPENDS_ON_RESPONSIBILITIES; // SOLUTION-VALUE

    // Q5 — boolean parameter báo hiệu gì.
    static final Signal Q5_BOOLEAN_PARAMETER = Signal.METHOD_DOES_TWO_THINGS; // SOLUTION-VALUE

    /** Cho sẵn, không sửa. Tên không cho biết method nhận gì và trả gì. */
    static String process(LineItem item, int flag) {
        return item.sku() + ":" + item.unitCents() * item.qty() + ":" + flag;
    }

    /** Trả chuỗi "{sku} x{qty} = {lineTotalCents} cents". */
    static String describe(LineItem item) {
        // SOLUTION-BEGIN throw Q6
        return item.sku() + " x" + item.qty() + " = " + item.unitCents() * item.qty() + " cents";
        // SOLUTION-END
    }

    /** Tổng tiền hàng cộng phí vận chuyển, đơn vị cent. */
    static long totalWithShipping(List<LineItem> items, int shippingCents) {
        // SOLUTION-BEGIN throw Q6
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Danh sách mặt hàng không được rỗng.");
        }
        long total = shippingCents;
        for (LineItem item : items) {
            total += item.unitCents() * item.qty();
        }
        return total;
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Không. Clean code là đọc hiểu nhanh và sửa an toàn, không phải ít dòng.
 * Nén nhiều việc vào một dòng làm khó đọc và khó đặt breakpoint.
 * Một method dài nhưng một trách nhiệm có thể rõ hơn nhiều method vụn gọi nhau.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Không luôn sai. Thước đo là số trách nhiệm và số nhánh, không phải số dòng.
 * Một bảng tra dài hoặc một switch ổn định có thể vẫn rõ ràng.
 * Method dài mà trộn validate, tính toán và lưu trữ thì nên tách theo trách nhiệm.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Khi hai đoạn chỉ tình cờ giống nhau, còn lý do thay đổi khác nhau.
 * Gộp chúng tạo một abstraction phải chiều cả hai, và thay đổi một bên sẽ phá bên kia.
 * Chờ đến lần thay đổi thứ hai thực sự để biết chúng có cùng lý do hay không.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Không. Comment lặp lại điều code đã nói làm nhiễu và dễ lạc hậu khi code đổi.
 * Comment còn giá trị khi giải thích vì sao, ví dụ ràng buộc bên ngoài hoặc quyết định đánh đổi.
 * Nếu cần comment để hiểu code làm gì, tên và cấu trúc nên được sửa trước.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Boolean parameter thường nghĩa là method làm hai việc, thật hoặc giả.
 * Chỗ gọi `send(true)` không nói lên điều gì khi đọc.
 * Tách thành hai method có tên theo việc, hoặc dùng enum có nhãn rõ.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Tên chung chung như process không cho biết đầu vào, đầu ra, hay tác dụng phụ.
 * Người đọc phải mở thân method để biết nó làm gì, kể cả khi chỉ cần đọc lời gọi.
 * Tên nên nói việc và đơn vị, ví dụ totalWithShipping và describe.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write `Ex02_ErrorHandling.java`**

```java
package phase03.d19_clean_code;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Clean Code — Bài 2: xử lý lỗi
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 19, câu 7, 8.
 * Cần làm trước: Ex01_NamingAndFunctions.
 * Cách làm: chạy test trong Ex02_ErrorHandlingTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q7 [CODE] Exception nên được translate ở boundary nào?
 *   Bắt đầu   : cài {@code quote}. Bắt {@code RateUnavailableException} của provider và ném
 *               {@code QuoteFailedException} giữ nguyên cause. Không dùng exception cho luồng thường.
 *   Kiểm chứng: chạy q07_translatesAtBoundary và q07_preservesCause.
 *   Hoàn thành khi: hai test xanh và ANSWER Q7 nêu dịch lỗi một lần ở lớp biên.
 * <p>
 * Q8 [DỰ ĐOÁN + CODE] Catch {@code Exception} rồi log và tiếp tục có rủi ro gì?
 *   Bắt đầu   : điền Q8_CATCH_AND_CONTINUE, rồi cài {@code readFirstLine} để IOException thành
 *               {@code UncheckedIOException} chứa cause, không trả chuỗi rỗng.
 *   Kiểm chứng: chạy q08_prediction và q08_missingFileThrowsWithCause.
 *   Hoàn thành khi: hai test xanh và ANSWER Q8 nêu lỗi bị che làm trạng thái sai lan tiếp.
 */
public class Ex02_ErrorHandling {

    // Q7 — nơi dịch lỗi.
    static final String Q7_TRANSLATE_BOUNDARY = "boundary"; // SOLUTION-VALUE

    // Q8 — catch Exception rồi tiếp tục có rủi ro gì.
    static final Boolean Q8_CATCH_AND_CONTINUE = true; // SOLUTION-VALUE

    /** Provider ngoài báo không lấy được tỉ giá. */
    public static class RateUnavailableException extends RuntimeException {

        public RateUnavailableException(String message) {
            super(message);
        }
    }

    /** Lỗi của lớp biên, ngôn ngữ của ứng dụng. */
    public static class QuoteFailedException extends RuntimeException {

        public QuoteFailedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /** Cổng lấy tỉ giá, có thể là hệ ngoài. */
    public interface RateProvider {

        long rate(String currency);
    }

    /** Dịch lỗi provider thành lỗi của ứng dụng, giữ nguyên nguyên nhân. */
    public static long quote(RateProvider provider, String currency) {
        // SOLUTION-BEGIN throw Q7
        try {
            return provider.rate(currency);
        } catch (RateUnavailableException failure) {
            throw new QuoteFailedException("Không lấy được tỉ giá cho " + currency, failure);
        }
        // SOLUTION-END
    }

    /** Đọc dòng đầu của file; lỗi I/O thành lỗi unchecked có nguyên nhân. */
    public static String readFirstLine(Path path) {
        // SOLUTION-BEGIN throw Q8
        try {
            return Files.readAllLines(path).getFirst();
        } catch (IOException failure) {
            throw new UncheckedIOException("Không đọc được " + path, failure);
        }
        // SOLUTION-END
    }
}

/* ANSWER Q7:
 * SOLUTION-BEGIN
 * Dịch lỗi ở lớp biên, nơi lỗi của hệ ngoài đi vào ứng dụng.
 * Sau đó toàn bộ code nghiệp vụ chỉ thấy kiểu lỗi của ứng dụng.
 * Dịch đúng một lần tránh mỗi tầng lại bọc thêm một lớp exception.
 * SOLUTION-END
 */

/* ANSWER Q8:
 * SOLUTION-BEGIN
 * Catch Exception rồi log và tiếp tục làm lỗi biến mất khỏi luồng điều khiển.
 * Code phía sau chạy với dữ liệu thiếu hoặc sai, và trạng thái sai lan sang bước khác.
 * Log không thay được việc dừng hoặc trả lỗi có kiểu cho người gọi.
 * SOLUTION-END
 */
```

- [ ] **Step 3: Write both tests**

`Ex01_NamingAndFunctionsTest.java`:

```java
package phase03.d19_clean_code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_NamingAndFunctionsTest {

    private static final String HINT_Q1 =
            "Clean code là đọc hiểu nhanh, không phải ít dòng.";
    private static final String HINT_Q2 =
            "Thước đo là số trách nhiệm và số nhánh, không phải số dòng.";
    private static final String HINT_Q3 =
            "Hai đoạn giống nhau nhưng đổi vì hai lý do khác nhau thì gộp lại sẽ phải chiều cả hai.";
    private static final String HINT_Q4 =
            "Comment lặp lại điều code đã nói thì làm nhiễu; comment giải thích why thì có giá trị.";
    private static final String HINT_Q5 =
            "Tham số boolean buộc người đọc nhớ true nghĩa là gì, và thường là hai việc trong một method.";

    private static final Ex01_NamingAndFunctions.LineItem ITEM =
            new Ex01_NamingAndFunctions.LineItem("book", 1_000, 3);

    @Test
    @DisplayName("Q1 dự đoán: clean code có nghĩa code ngắn")
    void q01_prediction() {
        assertPrediction("Q1_CLEAN_CODE_MEANS_SHORT", false,
                Ex01_NamingAndFunctions.Q1_CLEAN_CODE_MEANS_SHORT, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: method 100 dòng có luôn sai")
    void q02_prediction() {
        assertPrediction("Q2_HUNDRED_LINE_METHOD",
                Ex01_NamingAndFunctions.Verdict.DEPENDS_ON_RESPONSIBILITIES,
                Ex01_NamingAndFunctions.Q2_HUNDRED_LINE_METHOD, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: duplicate tốt hơn premature abstraction")
    void q03_prediction() {
        assertPrediction("Q3_DUPLICATE_BETTER_THAN_PREMATURE", true,
                Ex01_NamingAndFunctions.Q3_DUPLICATE_BETTER_THAN_PREMATURE, HINT_Q3);
    }

    @Test
    @DisplayName("Q4 dự đoán: comment nhiều có phải tốt")
    void q04_prediction() {
        assertPrediction("Q4_MORE_COMMENTS",
                Ex01_NamingAndFunctions.Verdict.DEPENDS_ON_RESPONSIBILITIES,
                Ex01_NamingAndFunctions.Q4_MORE_COMMENTS, HINT_Q4);
    }

    @Test
    @DisplayName("Q5 dự đoán: boolean parameter báo hiệu gì")
    void q05_prediction() {
        assertPrediction("Q5_BOOLEAN_PARAMETER",
                Ex01_NamingAndFunctions.Signal.METHOD_DOES_TWO_THINGS,
                Ex01_NamingAndFunctions.Q5_BOOLEAN_PARAMETER, HINT_Q5);
    }

    @Test
    @DisplayName("Q6: describe nói rõ việc và đơn vị")
    void q06_describeNamesUnit() {
        assertEquals("book x3 = 3000 cents", Ex01_NamingAndFunctions.describe(ITEM));
    }

    @Test
    @DisplayName("Q6: totalWithShipping cộng cả phí và chặn danh sách rỗng")
    void q06_totalWithShipping() {
        assertEquals(3_500, Ex01_NamingAndFunctions.totalWithShipping(List.of(ITEM), 500));
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_NamingAndFunctions.totalWithShipping(List.of(), 500));
    }
}
```

`Ex02_ErrorHandlingTest.java`:

```java
package phase03.d19_clean_code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_ErrorHandlingTest {

    private static final String HINT_Q7 =
            "Dịch lỗi ở lớp biên, nơi lỗi hệ ngoài đi vào, và giữ nguyên cause.";
    private static final String HINT_Q8 =
            "Catch rồi tiếp tục làm lỗi biến mất khỏi luồng điều khiển, trạng thái sai lan sang bước sau.";

    @Test
    @DisplayName("Q7: quote dịch lỗi provider thành lỗi ứng dụng")
    void q07_translatesAtBoundary() {
        assertThrows(Ex02_ErrorHandling.QuoteFailedException.class,
                () -> Ex02_ErrorHandling.quote(currency -> {
                    throw new Ex02_ErrorHandling.RateUnavailableException("timeout");
                }, "USD"));
    }

    @Test
    @DisplayName("Q7: lỗi dịch giữ nguyên cause")
    void q07_preservesCause() {
        Ex02_ErrorHandling.QuoteFailedException failure =
                assertThrows(Ex02_ErrorHandling.QuoteFailedException.class,
                        () -> Ex02_ErrorHandling.quote(currency -> {
                            throw new Ex02_ErrorHandling.RateUnavailableException("timeout");
                        }, "USD"));
        assertInstanceOf(Ex02_ErrorHandling.RateUnavailableException.class, failure.getCause());
        assertEquals("Không lấy được tỉ giá cho USD", failure.getMessage());
        assertEquals(25_400, Ex02_ErrorHandling.quote(currency -> 25_400, "USD"));
    }

    @Test
    @DisplayName("Q8 dự đoán: catch Exception rồi tiếp tục có rủi ro")
    void q08_prediction() {
        assertPrediction("Q8_CATCH_AND_CONTINUE", true,
                Ex02_ErrorHandling.Q8_CATCH_AND_CONTINUE, HINT_Q8);
    }

    @Test
    @DisplayName("Q8: file thiếu thì ném lỗi unchecked giữ cause")
    void q08_missingFileThrowsWithCause(@TempDir Path tempDir) {
        UncheckedIOException failure = assertThrows(UncheckedIOException.class,
                () -> Ex02_ErrorHandling.readFirstLine(tempDir.resolve("missing.txt")));
        assertInstanceOf(java.nio.file.NoSuchFileException.class, failure.getCause());
    }
}
```

- [ ] **Step 4: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d19_clean_code/*Test"`
Expected: FAIL — six `thay null`; `TODO Q6` on `describe`, `totalWithShipping`; `TODO Q7` on `quote`; `TODO Q8` on `readFirstLine`.
Run GREEN: same command → 11 tests pass.

- [ ] **Step 5: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d19_clean_code -ExpectedQuestions 8`
Expected: `OK`.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d19_clean_code phase-03-clean-code-design-patterns/src/test/java/phase03/d19_clean_code
git commit -m "feat(phase03): add d19_clean_code exercises"
```

---

### Task 22: d20_testability (6 câu)

**Files:**
- Create: `.../src/main/java/phase03/d20_testability/Ex01_ExplicitDependencies.java`, `Ex02_StaticAndMockSymptom.java`
- Test: `.../src/test/java/phase03/d20_testability/Ex01_ExplicitDependenciesTest.java`, `Ex02_StaticAndMockSymptomTest.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`, `phase03.support.Compiles`.
- Produces: `Ex01_ExplicitDependencies.PaymentGateway`, `OrderRepository`, `CheckoutService(PaymentGateway, OrderRepository)`; `Q1_CTOR_INJECTION_TESTABILITY`, `Q2_STATIC_ALWAYS_BAD`, `Q5_HARD_TO_TEST_SIGNAL`, `Q6_MOCK_EVERYTHING_STILL_PROVES`. `Ex02_StaticAndMockSymptom.DirectApiCaller` (calls a static), `InjectedApiCaller(ApiGateway)`, `dependencyCount(Class<?>) → int`, `Q3_DIRECT_EXTERNAL_CALL`, `Q4_TOO_MANY_MOCKS`.

- [ ] **Step 1: Write `Ex01_ExplicitDependencies.java`**

```java
package phase03.d20_testability;

/**
 * Testability — Bài 1: dependency tường minh
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 20 (Testability as a Design Signal), câu 1, 2, 5, 6.
 * Cần làm trước: d19_clean_code.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_ExplicitDependenciesTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN + CODE] Vì sao constructor injection tăng testability?
 *   Bắt đầu   : điền Q1_CTOR_INJECTION_TESTABILITY, rồi cài {@code CheckoutService.checkout}
 *               gọi repository rồi gateway, trả reference.
 *   Kiểm chứng: chạy q01_prediction và q01_fakesReplaceRealities.
 *   Hoàn thành khi: hai test xanh và ANSWER Q1 nêu dependency thay được khi tạo object.
 * <p>
 * Q2 [DỰ ĐOÁN] Static utility có luôn xấu không?
 *   Bắt đầu   : điền Q2_STATIC_ALWAYS_BAD.
 *   Kiểm chứng: chạy q02_prediction.
 *   Hoàn thành khi: q02_prediction xanh và ANSWER Q2 nêu static thuần không trạng thái thì test dễ.
 * <p>
 * Q5 [DỰ ĐOÁN] Unit test khó viết có thể phản ánh vấn đề thiết kế nào?
 *   Bắt đầu   : điền Q5_HARD_TO_TEST_SIGNAL bằng một giá trị của {@code Signal}.
 *   Kiểm chứng: chạy q05_prediction.
 *   Hoàn thành khi: q05_prediction xanh và ANSWER Q5 gắn độ khó test với coupling và trạng thái ẩn.
 * <p>
 * Q6 [TỰ TRẢ LỜI] Khi mock gần như mọi class, test có còn chứng minh behavior quan trọng không?
 *   Bắt đầu   : viết khối ANSWER Q6.
 *   Hoàn thành khi: ANSWER Q6 nêu test chỉ còn kiểm tra thứ tự lời gọi đã giả định.
 */
public class Ex01_ExplicitDependencies {

    /** Lý do constructor injection tăng testability. */
    public enum Reason {
        FEWER_LINES,
        DEPENDENCIES_REPLACEABLE_AT_CREATION,
        FASTER_COMPILATION
    }

    /** Vấn đề thiết kế khi test khó viết. */
    public enum Signal {
        HIGH_COUPLING_AND_HIDDEN_STATE,
        WEAK_TYPING,
        MISSING_COMMENTS
    }

    public interface PaymentGateway {

        String charge(long amountCents);
    }

    public interface OrderRepository {

        void save(String orderId, long totalCents);
    }

    // Q1 — vì sao constructor injection tăng testability.
    static final Reason Q1_CTOR_INJECTION_TESTABILITY =
            Reason.DEPENDENCIES_REPLACEABLE_AT_CREATION; // SOLUTION-VALUE

    // Q2 — static utility có luôn xấu.
    static final Boolean Q2_STATIC_ALWAYS_BAD = false; // SOLUTION-VALUE

    // Q5 — test khó viết phản ánh vấn đề gì.
    static final Signal Q5_HARD_TO_TEST_SIGNAL = Signal.HIGH_COUPLING_AND_HIDDEN_STATE; // SOLUTION-VALUE

    /** Dependency tường minh, thay được trong test. */
    public static final class CheckoutService {

        private final PaymentGateway paymentGateway;
        private final OrderRepository orderRepository;

        public CheckoutService(PaymentGateway paymentGateway, OrderRepository orderRepository) {
            this.paymentGateway = paymentGateway;
            this.orderRepository = orderRepository;
        }

        public String checkout(String orderId, long totalCents) {
            // SOLUTION-BEGIN throw Q1
            orderRepository.save(orderId, totalCents);
            return paymentGateway.charge(totalCents);
            // SOLUTION-END
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Dependency nhận qua constructor nên test tạo object với fake thay cho hệ thật.
 * Không cần mạng, không cần database, không cần trạng thái toàn cục.
 * Mỗi đường lỗi dựng được tất định bằng cách cho fake trả hoặc ném theo ý muốn.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Không. Static utility thuần, không trạng thái và không phụ thuộc bên ngoài, test trực tiếp rất dễ.
 * Vấn đề chỉ xuất hiện khi static giữ trạng thái hoặc gọi hệ ngoài.
 * Khi đó không có đường thay thế trong test, và thứ tự chạy test trở thành ràng buộc.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Test khó viết là tín hiệu coupling cao, dependency ẩn, hoặc trạng thái toàn cục.
 * Object tự gọi new hoặc đọc static làm không có chỗ đặt fake.
 * Cách xử lý là sửa thiết kế, không phải thêm hạ tầng test.
 * SOLUTION-END
 */

/* ANSWER Q6:
 * SOLUTION-BEGIN
 * Khi mọi collaborator đều là mock, test chỉ còn kiểm tra thứ tự và số lần gọi đã giả định sẵn.
 * Nó không chứng minh kết quả nghiệp vụ vì kết quả do chính mock tạo ra.
 * Test như vậy sẽ xanh dù logic tính toán bên trong sai hoàn toàn.
 * SOLUTION-END
 */
```

- [ ] **Step 2: Write `Ex02_StaticAndMockSymptom.java`**

```java
package phase03.d20_testability;

import java.lang.reflect.Field;

/**
 * Testability — Bài 2: gọi hệ ngoài và mock quá nhiều
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 20, câu 3, 4.
 * Cần làm trước: Ex01_ExplicitDependencies.
 * Cách làm: chạy test trong Ex02_StaticAndMockSymptomTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q3 [DỰ ĐOÁN] Vì sao gọi trực tiếp external API trong business method làm unit test khó?
 *   Bắt đầu   : điền Q3_DIRECT_EXTERNAL_CALL bằng một giá trị của {@code Why}.
 *   Kiểm chứng: chạy q03_prediction và q03_injectedCallerHasReplaceableField.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nêu test phải dựng mạng hoặc chờ thật.
 * <p>
 * Q4 [DỰ ĐOÁN + CODE] Mock quá nhiều dependency có thể báo hiệu điều gì?
 *   Bắt đầu   : điền Q4_TOO_MANY_MOCKS bằng một giá trị của {@code Signal}, rồi cài
 *               {@code dependencyCount} đếm field không static của một lớp.
 *   Kiểm chứng: chạy q04_prediction và q04_dependencyCount.
 *   Hoàn thành khi: hai test xanh và ANSWER Q4 nêu class đang gộp nhiều trách nhiệm.
 */
public class Ex02_StaticAndMockSymptom {

    /** Vì sao gọi trực tiếp external API làm test khó. */
    public enum Why {
        SLOWER_COMPILATION,
        TEST_NEEDS_REAL_NETWORK_OR_WAITING,
        MORE_FILES
    }

    /** Mock quá nhiều dependency báo hiệu gì. */
    public enum Signal {
        FASTER_TESTS,
        CLASS_HAS_TOO_MANY_RESPONSIBILITIES,
        STRONG_TYPING
    }

    // Q3 — gọi trực tiếp external API làm test khó vì sao.
    static final Why Q3_DIRECT_EXTERNAL_CALL = Why.TEST_NEEDS_REAL_NETWORK_OR_WAITING; // SOLUTION-VALUE

    // Q4 — mock quá nhiều dependency báo hiệu gì.
    static final Signal Q4_TOO_MANY_MOCKS = Signal.CLASS_HAS_TOO_MANY_RESPONSIBILITIES; // SOLUTION-VALUE

    /** Cổng gọi hệ ngoài, thay được trong test. */
    public interface ApiGateway {

        String fetch(String key);
    }

    /** Cho sẵn: gọi static trực tiếp, không có đường thay thế. */
    public static final class DirectApiCaller {

        public String fetch(String key) {
            return StaticApi.fetch(key);
        }
    }

    /** Thay được vì dependency là field. */
    public static final class InjectedApiCaller {

        private final ApiGateway gateway;

        public InjectedApiCaller(ApiGateway gateway) {
            this.gateway = gateway;
        }

        public String fetch(String key) {
            return gateway.fetch(key);
        }
    }

    /** Đếm dependency, tức field không static. */
    public static int dependencyCount(Class<?> type) {
        // SOLUTION-BEGIN throw Q4
        int count = 0;
        for (Field field : type.getDeclaredFields()) {
            if (!field.isSynthetic() && !java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                count++;
            }
        }
        return count;
        // SOLUTION-END
    }

    /** Cho sẵn, không sửa: static gọi hệ ngoài. */
    static final class StaticApi {

        private StaticApi() {
        }

        static String fetch(String key) {
            return "static:" + key;
        }
    }
}

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Gọi trực tiếp external API nghĩa là test phải có mạng, tài khoản, và dữ liệu thật.
 * Kết quả phụ thuộc hệ ngoài nên test lúc xanh lúc đỏ mà không do code đổi.
 * Đường lỗi như timeout gần như không dựng lại được một cách tất định.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Mock quá nhiều dependency thường nghĩa là class gộp nhiều nhóm trách nhiệm.
 * Mỗi nhóm kéo theo vài collaborator phải mock riêng khi test.
 * Cách xử lý là tách class theo trách nhiệm, không phải viết thêm mock.
 * SOLUTION-END
 */
```

- [ ] **Step 3: Write both tests**

`Ex01_ExplicitDependenciesTest.java`:

```java
package phase03.d20_testability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase03.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_ExplicitDependenciesTest {

    private static final String HINT_Q1 =
            "Dependency nhận lúc tạo object nên test thay được bằng fake, không cần mạng hay database.";
    private static final String HINT_Q2 =
            "Static thuần không trạng thái, không gọi ngoài, thì test trực tiếp rất dễ.";
    private static final String HINT_Q5 =
            "Test khó viết là tín hiệu coupling cao và trạng thái ẩn, không phải thiếu hạ tầng test.";

    @Test
    @DisplayName("Q1 dự đoán: vì sao constructor injection tăng testability")
    void q01_prediction() {
        assertPrediction("Q1_CTOR_INJECTION_TESTABILITY",
                Ex01_ExplicitDependencies.Reason.DEPENDENCIES_REPLACEABLE_AT_CREATION,
                Ex01_ExplicitDependencies.Q1_CTOR_INJECTION_TESTABILITY, HINT_Q1);
    }

    @Test
    @DisplayName("Q1: fake thay cho hệ thật, không cần mạng")
    void q01_fakesReplaceRealities() {
        List<String> saved = new ArrayList<>();
        Ex01_ExplicitDependencies.CheckoutService service =
                new Ex01_ExplicitDependencies.CheckoutService(
                        amount -> "fake-charge:" + amount,
                        (orderId, totalCents) -> saved.add(orderId + ":" + totalCents));
        assertEquals("fake-charge:4000", service.checkout("o-1", 4_000));
        assertEquals(List.of("o-1:4000"), saved);
    }

    @Test
    @DisplayName("Q2 dự đoán: static utility có luôn xấu")
    void q02_prediction() {
        assertPrediction("Q2_STATIC_ALWAYS_BAD", false,
                Ex01_ExplicitDependencies.Q2_STATIC_ALWAYS_BAD, HINT_Q2);
    }

    @Test
    @DisplayName("Q5 dự đoán: test khó viết phản ánh vấn đề gì")
    void q05_prediction() {
        assertPrediction("Q5_HARD_TO_TEST_SIGNAL",
                Ex01_ExplicitDependencies.Signal.HIGH_COUPLING_AND_HIDDEN_STATE,
                Ex01_ExplicitDependencies.Q5_HARD_TO_TEST_SIGNAL, HINT_Q5);
    }
}
```

`Ex02_StaticAndMockSymptomTest.java`:

```java
package phase03.d20_testability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_StaticAndMockSymptomTest {

    private static final String HINT_Q3 =
            "Không có chỗ đặt fake thì test phải dùng mạng thật hoặc chờ thật.";
    private static final String HINT_Q4 =
            "Nhiều collaborator phải mock thường là nhiều nhóm trách nhiệm trong một class.";

    @Test
    @DisplayName("Q3 dự đoán: gọi trực tiếp external API làm test khó vì sao")
    void q03_prediction() {
        assertPrediction("Q3_DIRECT_EXTERNAL_CALL",
                Ex02_StaticAndMockSymptom.Why.TEST_NEEDS_REAL_NETWORK_OR_WAITING,
                Ex02_StaticAndMockSymptom.Q3_DIRECT_EXTERNAL_CALL, HINT_Q3);
    }

    @Test
    @DisplayName("Q3: caller có gateway thay được, caller static thì không")
    void q03_injectedCallerHasReplaceableField() {
        Ex02_StaticAndMockSymptom.InjectedApiCaller injected =
                new Ex02_StaticAndMockSymptom.InjectedApiCaller(key -> "fake:" + key);
        assertEquals("fake:k1", injected.fetch("k1"));
        assertEquals(1, Ex02_StaticAndMockSymptom.dependencyCount(
                Ex02_StaticAndMockSymptom.InjectedApiCaller.class));
        assertEquals(0, Ex02_StaticAndMockSymptom.dependencyCount(
                Ex02_StaticAndMockSymptom.DirectApiCaller.class));
    }

    @Test
    @DisplayName("Q4 dự đoán: mock quá nhiều dependency báo hiệu gì")
    void q04_prediction() {
        assertPrediction("Q4_TOO_MANY_MOCKS",
                Ex02_StaticAndMockSymptom.Signal.CLASS_HAS_TOO_MANY_RESPONSIBILITIES,
                Ex02_StaticAndMockSymptom.Q4_TOO_MANY_MOCKS, HINT_Q4);
    }

    @Test
    @DisplayName("Q4: dependencyCount đếm field không static")
    void q04_dependencyCount() {
        assertEquals(2, Ex02_StaticAndMockSymptom.dependencyCount(
                Ex01_ExplicitDependencies.CheckoutService.class));
        assertThrows(NullPointerException.class, () -> Ex02_StaticAndMockSymptom.dependencyCount(null));
    }
}
```

- [ ] **Step 4: RED then GREEN**

Run RED: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d20_testability/*Test"`
Expected: FAIL — six `thay null`; `TODO Q1` on `CheckoutService.checkout`; `TODO Q4` on `dependencyCount`.
Run GREEN: same command → 8 tests pass.

- [ ] **Step 5: Gate and commit**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d20_testability -ExpectedQuestions 6`
Expected: `OK`.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d20_testability phase-03-clean-code-design-patterns/src/test/java/phase03/d20_testability
git commit -m "feat(phase03): add d20_testability exercises"
```

---

### Task 23: d21_capstone (B1–B6)

**Files:**
- Create: `.../src/main/java/phase03/d21_capstone/CoupledOrderService.java` (pre-refactor bad version, no markers), `Money.java`, `LineItem.java`, `OrderRequest.java`, `Order.java`, `OrderStatus.java`, `CheckoutResult.java`, `PricingPolicy.java`, `PaymentGateway.java`, `PaymentResult.java`, `OrderRepository.java`, `InventoryService.java`, `InvoiceService.java`, `EventPublisher.java`, `OrderApplicationService.java`, `OrderController.java`, `OrderServiceFactory.java`
- Test: `.../src/test/java/phase03/d21_capstone/CoupledOrderServiceTest.java` (behavior recording), `OrderApplicationServiceTest.java` (same behavior after refactor), `CheckoutFailurePathsTest.java`
- Create: `.../src/test/java/phase03/support/FakePaymentGateway.java`, `FakeOrderRepository.java`, `FakeEventPublisher.java`

**Interfaces:**
- Consumes: `phase03.support.Predictions`.
- Produces: `Money` record `Money(long cents)` with `plus`, `times`, `isPositive`; `LineItem(String sku, long unitCents, int qty)` with `lineTotal()`; `OrderRequest(String customer, List<LineItem> items, boolean vip)`; `Order(String id, String customer, long totalCents, OrderStatus status)`; `enum OrderStatus { CREATED, PAYMENT_DECLINED, PAYMENT_CHARGED_ORDER_NOT_SAVED, SAVED, PUBLISH_FAILED }`; `CheckoutResult(OrderStatus status, String reference, long totalCents)`; `PricingPolicy.long price(OrderRequest)`, `VipPricing`; `PaymentGateway.PaymentResult charge(long)` returning record `PaymentResult(boolean approved, String reference)`; `PaymentResult(boolean approved, String reference)`; `OrderRepository.save(Order)`, `find(String) → Optional<Order>`; `InventoryService.reserve(String sku, int qty)`; `InvoiceService.invoice(Order) → String`; `EventPublisher.publish(Order)`; `OrderApplicationService.checkout(OrderRequest) → CheckoutResult`; `OrderController.handle(OrderRequest) → int` (HTTP status); `OrderServiceFactory.build(OrderRepository, PaymentGateway, InventoryService, EventPublisher)`.
- Fakes: `FakePaymentGateway` (`approve`/`decline` modes, records charges), `FakeOrderRepository` (`failOnSave` flag, in-memory map), `FakeEventPublisher` (`failOnPublish` flag, recorded events).

- [ ] **Step 1: Write the pre-refactor coupled version and its behavior-recording test (commit 1 of the capstone)**

`CoupledOrderService.java`:

```java
package phase03.d21_capstone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bản trước refactor — cho sẵn, KHÔNG sửa.
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục Architecture Exercise.
 * Đây là order flow trộn validate, pricing, persistence, charge, inventory, invoice, notify và publish
 * trong một method. Nhiệm vụ của B1 là ghi lại hành vi quan sát được của nó, không sửa file này.
 */
public class CoupledOrderService {

    private final Map<String, String> savedOrders = new HashMap<>();
    private final List<String> events = new ArrayList<>();
    private final List<String> charges = new ArrayList<>();
    private int nextOrderNumber = 1;

    /** Trả về chuỗi trạng thái quan sát được, đúng bản trộn trách nhiệm. */
    public String createOrder(String customer, List<int[]> items, boolean vip) {
        if (customer == null || customer.isBlank()) {
            throw new IllegalArgumentException("Thiếu khách hàng.");
        }
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Đơn không có mặt hàng.");
        }
        long total = 0;
        for (int[] item : items) {
            total += (long) item[0] * item[1];
        }
        if (vip) {
            total = total * 9 / 10;
        }
        String orderId = "o-" + nextOrderNumber++;
        if (total > 100_000) {
            return "DECLINED:" + orderId + ":" + total;
        }
        charges.add(orderId + ":" + total);
        savedOrders.put(orderId, total + ":" + customer);
        for (int[] item : items) {
            if (item[0] < 0) {
                throw new IllegalStateException("Không thể giữ hàng cho " + orderId);
            }
        }
        events.add("created:" + orderId);
        return "OK:" + orderId + ":" + total;
    }

    public Map<String, String> savedOrders() {
        return Map.copyOf(savedOrders);
    }

    public List<String> events() {
        return List.copyOf(events);
    }

    public List<String> charges() {
        return List.copyOf(charges);
    }
}
```

`CoupledOrderServiceTest.java` — the behavior recorder. Its assertions are the contract the refactor must preserve; it never changes afterwards:

```java
package phase03.d21_capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class CoupledOrderServiceTest {

    private static final List<int[]> ITEMS = List.of(new int[] {1_000, 3}, new int[] {500, 2});

    @Test
    @DisplayName("Hành vi trước refactor: đơn thường")
    void b01_standardOrder() {
        CoupledOrderService service = new CoupledOrderService();
        assertEquals("OK:o-1:4000", service.createOrder("alice", ITEMS, false));
        assertEquals(List.of("o-1:4000"), service.charges());
        assertEquals("{o-1=4000:alice}", service.savedOrders().toString());
        assertEquals(List.of("created:o-1"), service.events());
    }

    @Test
    @DisplayName("Hành vi trước refactor: đơn VIP giảm 10 phần trăm")
    void b01_vipOrder() {
        CoupledOrderService service = new CoupledOrderService();
        assertEquals("OK:o-1:3600", service.createOrder("alice", ITEMS, true));
    }

    @Test
    @DisplayName("Hành vi trước refactor: quá 100000 cent bị từ chối, không charge, không lưu")
    void b01_declinedOrder() {
        CoupledOrderService service = new CoupledOrderService();
        assertEquals("DECLINED:o-1:200000", service.createOrder("bob", List.of(new int[] {100_000, 2}), false));
        assertTrue(service.charges().isEmpty(), "Đơn bị từ chối không được charge.");
        assertTrue(service.savedOrders().isEmpty(), "Đơn bị từ chối không được lưu.");
    }

    @Test
    @DisplayName("Hành vi trước refactor: thiếu khách hoặc rỗng mặt hàng bị chặn sớm")
    void b01_validation() {
        CoupledOrderService service = new CoupledOrderService();
        assertThrows(IllegalArgumentException.class, () -> service.createOrder(" ", ITEMS, false));
        assertThrows(IllegalArgumentException.class, () -> service.createOrder("alice", List.of(), false));
    }
}
```

- [ ] **Step 2: RED for commit 1 — record, then commit the pre-refactor behavior**

Run: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d21_capstone/CoupledOrderServiceTest"`
Expected: 4 tests pass. `CoupledOrderService` is complete code, so there is no red here — this commit's job is to freeze behavior, not to fail.

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d21_capstone/CoupledOrderService.java phase-03-clean-code-design-patterns/src/test/java/phase03/d21_capstone/CoupledOrderServiceTest.java
git commit -m "test(phase03): record d21 pre-refactor checkout behavior"
```

- [ ] **Step 3: Write the refactored ports, model, and fakes**

`Money.java`:

```java
package phase03.d21_capstone;

/** Tiền tính bằng cent, không dùng double. */
public record Money(long cents) {

    public Money {
        if (cents < 0) {
            throw new IllegalArgumentException("Số tiền không được âm: " + cents);
        }
    }

    public Money plus(Money other) {
        return new Money(cents + other.cents);
    }

    public Money times(int factor) {
        return new Money(cents * factor);
    }

    public boolean isPositive() {
        return cents > 0;
    }
}
```

`LineItem.java`:

```java
package phase03.d21_capstone;

public record LineItem(String sku, long unitCents, int qty) {

    public LineItem {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU không được trống.");
        }
        if (qty <= 0) {
            throw new IllegalArgumentException("Số lượng phải dương: " + qty);
        }
    }

    public long lineTotalCents() {
        return unitCents * qty;
    }
}
```

`OrderRequest.java`:

```java
package phase03.d21_capstone;

import java.util.List;

public record OrderRequest(String customer, List<LineItem> items, boolean vip) {

    public OrderRequest {
        if (customer == null || customer.isBlank()) {
            throw new IllegalArgumentException("Thiếu khách hàng.");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Đơn không có mặt hàng.");
        }
        items = List.copyOf(items);
    }
}
```

`OrderStatus.java`:

```java
package phase03.d21_capstone;

/** Trạng thái đơn, gồm trạng thái trung gian khi charge đã thành công nhưng bước sau thất bại. */
public enum OrderStatus {
    CREATED,
    PAYMENT_DECLINED,
    PAYMENT_CHARGED_ORDER_NOT_SAVED,
    SAVED,
    PUBLISH_FAILED
}
```

`Order.java`:

```java
package phase03.d21_capstone;

public record Order(String id, String customer, long totalCents, OrderStatus status) {

    public Order withStatus(OrderStatus other) {
        return new Order(id, customer, totalCents, other);
    }
}
```

`CheckoutResult.java`:

```java
package phase03.d21_capstone;

public record CheckoutResult(OrderStatus status, String reference, long totalCents) {

    public boolean isSuccess() {
        return status == OrderStatus.SAVED;
    }
}
```

`PricingPolicy.java`:

```java
package phase03.d21_capstone;

/** Chính sách giá, không biết persistence hay thanh toán. */
public interface PricingPolicy {

    long totalCents(OrderRequest request);
}
```

`VipPricing.java`:

```java
package phase03.d21_capstone;

/** Cho sẵn: tổng hàng, VIP giảm 10 phần trăm, làm tròn xuống theo cent. */
public final class VipPricing implements PricingPolicy {

    @Override
    public long totalCents(OrderRequest request) {
        long total = 0;
        for (LineItem item : request.items()) {
            total += item.lineTotalCents();
        }
        return request.vip() ? total * 9 / 10 : total;
    }
}
```

`PaymentGateway.java`:

```java
package phase03.d21_capstone;

/** Cổng thanh toán; đây là hệ ngoài, không nằm trong transaction nào của ứng dụng. */
public interface PaymentGateway {

    record PaymentResult(boolean approved, String reference) {
    }

    PaymentResult charge(long amountCents);
}
```

`OrderRepository.java`:

```java
package phase03.d21_capstone;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> find(String id);
}
```

`InventoryService.java`:

```java
package phase03.d21_capstone;

public interface InventoryService {

    void reserve(String sku, int qty);
}
```

`InvoiceService.java`:

```java
package phase03.d21_capstone;

public interface InvoiceService {

    String invoice(Order order);
}
```

`EventPublisher.java`:

```java
package phase03.d21_capstone;

public interface EventPublisher {

    void publish(Order order);
}
```

`OrderApplicationService.java` — the refactored policy, with `B2`–`B5` markers:

```java
package phase03.d21_capstone;

import java.util.Optional;

/**
 * Capstone — OrderApplicationService
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục Architecture Exercise.
 * Cần làm trước: CoupledOrderServiceTest đã xanh (hành vi đã được ghi lại).
 * Cách làm: làm lần lượt B2 đến B5; chạy test trong OrderApplicationServiceTest và
 * CheckoutFailurePathsTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * B2 [CODE] Refactor luồng checkout thành policy chỉ phụ thuộc port.
 *   Bắt đầu   : cài {@code checkout}. Thứ tự bắt buộc: tính giá, từ chối nếu tổng bằng 0,
 *               charge qua {@code paymentGateway}, nếu bị từ chối trả PAYMENT_DECLINED,
 *               lưu qua {@code orderRepository}, nếu lưu lỗi trả PAYMENT_CHARGED_ORDER_NOT_SAVED,
 *               rồi reserve tồn kho và publish; nếu publish lỗi trả PUBLISH_FAILED.
 *               Không dùng {@code @Transactional}. Không giả định charge bị huỷ được.
 *   Kiểm chứng: chạy b02_standardOrder, b02_vipOrder, b02_declinedOrder. Ctrl+B trên
 *               {@code paymentGateway} xem nó là interface, không phải class Stripe.
 *   Hoàn thành khi: ba test xanh và cùng kết quả số với CoupledOrderServiceTest.
 * <p>
 * B3 [CODE] Trạng thái trung gian khi charge đã thành công nhưng lưu thất bại.
 *   Bắt đầu   : khi repository ném lỗi sau charge, trả {@code PAYMENT_CHARGED_ORDER_NOT_SAVED}
 *               và giữ nguyên reference của lần charge đã thành công. Không nuốt lỗi lưu.
 *   Kiểm chứng: chạy b03_chargeSucceededButSaveFailed. Đọc lại ANSWER B3.
 *   Hoàn thành khi: b03 xanh và đơn không có trong repository.
 * <p>
 * B4 [CODE] Lỗi publish không làm mất đơn đã lưu.
 *   Bắt đầu   : nếu {@code eventPublisher.publish} ném lỗi, đơn vẫn ở trạng thái đã lưu,
 *               và kết quả trả {@code PUBLISH_FAILED} kèm reference của charge.
 *   Kiểm chứng: chạy b04_publishFailedKeepsSavedOrder.
 *   Hoàn thành khi: b04 xanh và repository vẫn tìm thấy đơn.
 * <p>
 * B5 [CODE] Không dùng exception cho luồng thường: từ chối thanh toán là kết quả, không phải lỗi.
 *   Bắt đầu   : {@code checkout} không ném exception khi bị từ chối hoặc khi lưu/publish lỗi;
 *               nó trả {@code CheckoutResult} với trạng thái tương ứng.
 *   Kiểm chứng: chạy b05_noExceptionOnBusinessFailure.
 *   Hoàn thành khi: b05 xanh.
 * <p>
 * B6 [TỰ TRẢ LỜI] Điểm commit đơn nằm ở đâu, và idempotent retry hoặc outbox cần gì về sau?
 *   Bắt đầu   : viết khối ANSWER B6 sau khi B2–B5 xanh.
 *   Hoàn thành khi: ANSWER B6 nêu điểm commit là lần lưu đơn, và charge đã thành công không thể
 *               hoàn tác bằng rollback local; cần idempotency key hoặc outbox ở giai đoạn sau.
 */
public final class OrderApplicationService {

    private final PricingPolicy pricingPolicy;
    private final PaymentGateway paymentGateway;
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final InvoiceService invoiceService;
    private final EventPublisher eventPublisher;
    private int nextOrderNumber = 1;

    public OrderApplicationService(PricingPolicy pricingPolicy, PaymentGateway paymentGateway,
            OrderRepository orderRepository, InventoryService inventoryService,
            InvoiceService invoiceService, EventPublisher eventPublisher) {
        this.pricingPolicy = pricingPolicy;
        this.paymentGateway = paymentGateway;
        this.orderRepository = orderRepository;
        this.inventoryService = inventoryService;
        this.invoiceService = invoiceService;
        this.eventPublisher = eventPublisher;
    }

    public CheckoutResult checkout(OrderRequest request) {
        // SOLUTION-BEGIN throw B2
        long total = pricingPolicy.totalCents(request);
        String orderId = "o-" + nextOrderNumber++;
        Order order = new Order(orderId, request.customer(), total, OrderStatus.CREATED);
        if (total <= 0) {
            return new CheckoutResult(OrderStatus.PAYMENT_DECLINED, "", total);
        }
        throw new UnsupportedOperationException("TODO B2");
        // SOLUTION-END
    }

    /** B5 — lỗi nghiệp vụ trả về kết quả thay vì ném exception. */
    static final Boolean B5_NO_EXCEPTION_ON_BUSINESS_FAILURE = true; // SOLUTION-VALUE
}
```

- [ ] **Step 4: Complete `checkout` (B2, B3, B5)** — replace the body written in Step 3 with:

```java
    public CheckoutResult checkout(OrderRequest request) {
        long total = pricingPolicy.totalCents(request);
        String orderId = "o-" + nextOrderNumber++;
        Order order = new Order(orderId, request.customer(), total, OrderStatus.CREATED);
        if (total <= 0) {
            return new CheckoutResult(OrderStatus.PAYMENT_DECLINED, "", total);
        }

        PaymentGateway.PaymentResult payment = paymentGateway.charge(total);
        if (!payment.approved()) {
            return new CheckoutResult(OrderStatus.PAYMENT_DECLINED, payment.reference(), total);
        }
        order = order.withStatus(OrderStatus.SAVED);
        try {
            orderRepository.save(order);
        } catch (RuntimeException saveFailure) {
            return new CheckoutResult(OrderStatus.PAYMENT_CHARGED_ORDER_NOT_SAVED,
                    payment.reference(), total);
        }
        for (LineItem item : request.items()) {
            inventoryService.reserve(item.sku(), item.qty());
        }
        try {
            eventPublisher.publish(order);
        } catch (RuntimeException publishFailure) {
            Optional<Order> stored = orderRepository.find(orderId);
            if (stored.isEmpty()) {
                throw new IllegalStateException("Đơn đã publish lỗi nhưng không còn trong repository.", publishFailure);
            }
            return new CheckoutResult(OrderStatus.PUBLISH_FAILED, payment.reference(), total);
        }
        invoiceService.invoice(order);
        return new CheckoutResult(OrderStatus.SAVED, payment.reference(), total);
    }
```

The `throw new UnsupportedOperationException("TODO B2")` line inside the `SOLUTION-BEGIN throw B2` region is what `StripSolutions` rewrites on the `exercises` branch; the solved file deletes it when this step lands.

- [ ] **Step 5: Write `OrderController` and `OrderServiceFactory`**

```java
package phase03.d21_capstone;

/** Lớp biên: chỉ dịch lời gọi sang mã trạng thái HTTP, không giữ chính sách. */
public final class OrderController {

    private final OrderApplicationService service;

    public OrderController(OrderApplicationService service) {
        this.service = service;
    }

    public int handle(OrderRequest request) {
        CheckoutResult result = service.checkout(request);
        return switch (result.status()) {
            case SAVED -> 201;
            case PAYMENT_DECLINED -> 402;
            case PAYMENT_CHARGED_ORDER_NOT_SAVED, PUBLISH_FAILED -> 503;
            case CREATED -> 500;
        };
    }
}
```

```java
package phase03.d21_capstone;

/** Nối graph bằng tay, không container. */
public final class OrderServiceFactory {

    private OrderServiceFactory() {
    }

    public static OrderApplicationService build(OrderRepository orderRepository,
            PaymentGateway paymentGateway, InventoryService inventoryService,
            EventPublisher eventPublisher) {
        return new OrderApplicationService(new VipPricing(), paymentGateway, orderRepository,
                inventoryService, eventPublisher, event -> { });
    }
}
```

- [ ] **Step 6: Write the three fakes**

`FakePaymentGateway.java`:

```java
package phase03.support;

import java.util.ArrayList;
import java.util.List;
import phase03.d21_capstone.PaymentGateway;

/** Cổng thanh toán giả, chạy tất định, ghi lại các lần charge. */
public final class FakePaymentGateway implements PaymentGateway {

    private boolean approve = true;
    private String declineReference = "declined";
    public final List<Long> charged = new ArrayList<>();

    public void decline() {
        this.approve = false;
    }

    public void declineWith(String reference) {
        this.approve = false;
        this.declineReference = reference;
    }

    @Override
    public PaymentResult charge(long amountCents) {
        charged.add(amountCents);
        return approve ? new PaymentResult(true, "ref-" + amountCents)
                : new PaymentResult(false, declineReference);
    }
}
```

`FakeOrderRepository.java`:

```java
package phase03.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import phase03.d21_capstone.Order;
import phase03.d21_capstone.OrderRepository;

/** Kho đơn in-memory, có công tắc cho lỗi lưu. */
public final class FakeOrderRepository implements OrderRepository {

    private final Map<String, Order> rows = new HashMap<>();
    private boolean failOnSave;

    public void failOnSave() {
        this.failOnSave = true;
    }

    @Override
    public Order save(Order order) {
        if (failOnSave) {
            throw new IllegalStateException("Kho không lưu được đơn " + order.id());
        }
        rows.put(order.id(), order);
        return order;
    }

    @Override
    public Optional<Order> find(String id) {
        return Optional.ofNullable(rows.get(id));
    }
}
```

`FakeEventPublisher.java`:

```java
package phase03.support;

import java.util.ArrayList;
import java.util.List;
import phase03.d21_capstone.EventPublisher;
import phase03.d21_capstone.Order;

/** Kênh sự kiện giả, có công tắc cho lỗi publish. */
public final class FakeEventPublisher implements EventPublisher {

    public final List<String> published = new ArrayList<>();
    private boolean failOnPublish;

    public void failOnPublish() {
        this.failOnPublish = true;
    }

    @Override
    public void publish(Order order) {
        if (failOnPublish) {
            throw new IllegalStateException("Kênh sự kiện từ chối " + order.id());
        }
        published.add(order.id());
    }
}
```

- [ ] **Step 7: Write the refactor and failure-path tests**

`OrderApplicationServiceTest.java` — the refactor must reproduce `CoupledOrderServiceTest`'s numbers exactly:

```java
package phase03.d21_capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase03.support.Predictions.assertPrediction;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase03.support.FakeEventPublisher;
import phase03.support.FakeOrderRepository;
import phase03.support.FakePaymentGateway;

@TestMethodOrder(MethodOrderer.MethodName.class)
class OrderApplicationServiceTest {

    private static final String HINT_B5 =
            "Từ chối thanh toán và lỗi lưu là kết quả nghiệp vụ trả về trong CheckoutResult, không phải exception.";

    private static final List<LineItem> ITEMS =
            List.of(new LineItem("book", 1_000, 3), new LineItem("pen", 500, 2));

    static OrderApplicationService service(FakePaymentGateway gateway, FakeOrderRepository repository,
            FakeEventPublisher publisher) {
        return new OrderApplicationService(new VipPricing(), gateway, repository,
                (sku, qty) -> { }, order -> "invoice:" + order.id(), publisher);
    }

    @Test
    @DisplayName("B2: đơn thường giữ đúng hành vi trước refactor")
    void b02_standardOrder() {
        FakePaymentGateway gateway = new FakePaymentGateway();
        FakeOrderRepository repository = new FakeOrderRepository();
        FakeEventPublisher publisher = new FakeEventPublisher();
        CheckoutResult result = service(gateway, repository, publisher)
                .checkout(new OrderRequest("alice", ITEMS, false));
        assertEquals(OrderStatus.SAVED, result.status());
        assertEquals(4_000, result.totalCents());
        assertEquals(List.of(4_000L), gateway.charged);
        assertEquals(4_000, repository.find("o-1").orElseThrow().totalCents());
        assertEquals(List.of("o-1"), publisher.published);
    }

    @Test
    @DisplayName("B2: đơn VIP giảm 10 phần trăm, đúng số trước refactor")
    void b02_vipOrder() {
        CheckoutResult result = service(new FakePaymentGateway(), new FakeOrderRepository(),
                new FakeEventPublisher()).checkout(new OrderRequest("alice", ITEMS, true));
        assertEquals(3_600, result.totalCents());
        assertEquals(OrderStatus.SAVED, result.status());
    }

    @Test
    @DisplayName("B2: bị từ chối thì không lưu, không publish")
    void b02_declinedOrder() {
        FakePaymentGateway gateway = new FakePaymentGateway();
        gateway.decline();
        FakeOrderRepository repository = new FakeOrderRepository();
        FakeEventPublisher publisher = new FakeEventPublisher();
        CheckoutResult result = service(gateway, repository, publisher)
                .checkout(new OrderRequest("bob", List.of(new LineItem("tv", 100_000, 2)), false));
        assertEquals(OrderStatus.PAYMENT_DECLINED, result.status());
        assertEquals(200_000, result.totalCents());
        assertTrue(repository.find("o-1").isEmpty(), "Đơn bị từ chối không được lưu.");
        assertTrue(publisher.published.isEmpty(), "Đơn bị từ chối không được publish.");
    }

    @Test
    @DisplayName("B5 dự đoán: lỗi nghiệp vụ trả kết quả, không ném exception")
    void b05_prediction() {
        assertPrediction("B5_NO_EXCEPTION_ON_BUSINESS_FAILURE", true,
                OrderApplicationService.B5_NO_EXCEPTION_ON_BUSINESS_FAILURE, HINT_B5);
    }
}
```

Add this constant to `OrderApplicationService` so that prediction test compiles:

```java
    // B5 — lỗi nghiệp vụ trả về kết quả thay vì ném exception.
    static final Boolean B5_NO_EXCEPTION_ON_BUSINESS_FAILURE = true; // SOLUTION-VALUE
```

`CheckoutFailurePathsTest.java`:

```java
package phase03.d21_capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase03.support.FakeEventPublisher;
import phase03.support.FakeOrderRepository;
import phase03.support.FakePaymentGateway;

@TestMethodOrder(MethodOrderer.MethodName.class)
class CheckoutFailurePathsTest {

    private static final List<LineItem> ITEMS =
            List.of(new LineItem("book", 1_000, 3), new LineItem("pen", 500, 2));

    private static OrderApplicationService service(FakePaymentGateway gateway,
            FakeOrderRepository repository, FakeEventPublisher publisher) {
        return new OrderApplicationService(new VipPricing(), gateway, repository,
                (sku, qty) -> { }, order -> "invoice:" + order.id(), publisher);
    }

    @Test
    @DisplayName("B3: charge thành công rồi lưu lỗi thì giữ trạng thái trung gian, không hoàn tác charge")
    void b03_chargeSucceededButSaveFailed() {
        FakePaymentGateway gateway = new FakePaymentGateway();
        FakeOrderRepository repository = new FakeOrderRepository();
        repository.failOnSave();
        CheckoutResult result = service(gateway, repository, new FakeEventPublisher())
                .checkout(new OrderRequest("alice", ITEMS, false));
        assertEquals(OrderStatus.PAYMENT_CHARGED_ORDER_NOT_SAVED, result.status());
        assertEquals(4_000, result.totalCents());
        assertEquals(List.of(4_000L), gateway.charged, "Charge đã thành công không được xoá.");
        assertEquals("ref-4000", result.reference());
        assertTrue(repository.find("o-1").isEmpty(), "Lưu lỗi nên đơn không có trong repository.");
    }

    @Test
    @DisplayName("B4: publish lỗi thì đơn đã lưu vẫn còn")
    void b04_publishFailedKeepsSavedOrder() {
        FakeOrderRepository repository = new FakeOrderRepository();
        FakeEventPublisher publisher = new FakeEventPublisher();
        publisher.failOnPublish();
        CheckoutResult result = service(new FakePaymentGateway(), repository, publisher)
                .checkout(new OrderRequest("alice", ITEMS, false));
        assertEquals(OrderStatus.PUBLISH_FAILED, result.status());
        assertEquals(4_000, repository.find("o-1").orElseThrow().totalCents(),
                "Publish lỗi không được làm mất đơn đã lưu.");
    }

    @Test
    @DisplayName("B5: mọi đường lỗi nghiệp vụ đều không ném exception")
    void b05_noExceptionOnBusinessFailure() {
        FakePaymentGateway gateway = new FakePaymentGateway();
        gateway.decline();
        FakeOrderRepository repository = new FakeOrderRepository();
        repository.failOnSave();
        FakeEventPublisher publisher = new FakeEventPublisher();
        publisher.failOnPublish();
        assertDoesNotThrow(() -> service(gateway, repository, publisher)
                .checkout(new OrderRequest("alice", ITEMS, false)));
        assertEquals(402, new OrderController(service(gateway, repository, publisher))
                .handle(new OrderRequest("alice", ITEMS, false)));
    }
}
```

- [ ] **Step 8: RED then GREEN**

Run RED before Step 4's implementation is in place: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d21_capstone/*Test"`
Expected: FAIL — `q01_prediction` and `q03_prediction`-style prediction `thay null` on `B5_NO_EXCEPTION_ON_BUSINESS_FAILURE`; every `b02`/`b03`/`b04`/`b05` test fails with `TODO B2`.
Run GREEN after completing Steps 4–7: same command → 11 tests pass.

- [ ] **Step 9: Gate and commit the refactor + failure paths (commit 2 of the capstone)**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns -Package d21_capstone`
Expected: `OK` (no `-ExpectedQuestions` for the capstone).

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d21_capstone phase-03-clean-code-design-patterns/src/test/java/phase03/d21_capstone phase-03-clean-code-design-patterns/src/test/java/phase03/support
git commit -m "refactor(phase03): split d21 order flow into ports with failure paths"
```

- [ ] **Step 10: Answer block for B6** — append to `OrderApplicationService.java`:

```java
/* ANSWER B6:
 * SOLUTION-BEGIN
 * Điểm commit của đơn là lần orderRepository.save thành công.
 * Trước đó chỉ có charge ở hệ ngoài; sau đó chỉ có các bước không hoàn tác được bằng rollback local.
 * Nếu charge đã thành công mà lưu thất bại, tiền đã ra khỏi hệ thống thanh toán và không rollback được.
 * Đó là lý do có trạng thái PAYMENT_CHARGED_ORDER_NOT_SAVED và reference của lần charge.
 * Về sau cần idempotency key cho mỗi lần charge để retry không thu hai lần.
 * Và cần outbox cho sự kiện, ghi cùng transaction với đơn rồi publish lại từ outbox.
 * Không được để @Transactional bao cả network call: nó không rollback được hệ ngoài.
 * SOLUTION-END
 */
```

- [ ] **Step 11: Commit the answer block**

```bash
git add phase-03-clean-code-design-patterns/src/main/java/phase03/d21_capstone/OrderApplicationService.java
git commit -m "docs(phase03): add d21 boundary answer"
```

---

### Task 24: README, link trong tài liệu, kiểm chứng toàn module

**Files:**
- Create: `phase-03-clean-code-design-patterns/README.md`
- Modify: `03-clean-code-design-patterns.md` (chỉ thêm một dòng link dưới mỗi mục 1–20, Architecture Exercise, Pattern Selection Exercise)

**Interfaces:**
- Consumes: every package from Tasks 3–23.
- Produces: learner-facing README; per-section links from the curriculum to each package.

- [ ] **Step 1: Write the module README**

Copy `phase-02-jvm-concurrency/README.md` structure verbatim (`phase-03-clean-code-design-patterns/README.md`), with these substitutions:

- Title: `# Giai đoạn 3 — Bài thực hành Clean Code và Design Patterns`
- Module name everywhere: `phase-03-clean-code-design-patterns`
- Package prefix: `phase03`; curriculum link `../03-clean-code-design-patterns.md`
- The four-label table keeps `[CODE]`, `[DỰ ĐOÁN]`, `[THÍ NGHIỆM]`, `[TỰ TRẢ LỜI]`
- Suggested order: `d19_clean_code`, `d20_testability` first (they change how every later file is written), then `d01_srp`–`d09_layers` in numeric order, then `d10_strategy`–`d18_singleton`, then `d21_capstone`
- Checklist table has 21 rows, one per package below, with the topic name from the curriculum
- Run-test section:

  ```powershell
  .\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test "-Dtest=phase03/d10_strategy/*Test"
  .\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test
  ```

- A note that on `exercises` the phase03 `d21` tests are intentionally red with `TODO B2`, and that `.\mvnw.cmd -q test` from the repo root also runs the phase00–02 skeleton, whose red tests are expected there
- Exit Criteria copied verbatim from `03-clean-code-design-patterns.md` lines 830–841
- Standards links copied verbatim from line 843
- An authoring section mirroring phase02's: solutions live on `phase03-work`/`solutions`, markers only in `src/main/java`, learners use `exercises`, and the two `verify-skeleton.ps1` invocations
- A `## Boundary và giao dịch` section stating: order commit point is the repository save; a charge that already succeeded cannot be rolled back by a local transaction; later work needs an idempotency key and an outbox; `@Transactional` must not wrap network calls
- A `## Chọn pattern` section carrying the four Pattern Selection cases (CreditCard/BankTransfer/Wallet, logging/metrics/authorization, three providers, CSV/JSON/XML) each with a short prompt and a pointer to the relevant package

- [ ] **Step 2: Add curriculum links**

For each of the 20 numbered sections plus the two exercises, insert one line immediately after the section's `### Kiến thức cần nắm` heading line block — matching the phase02 convention, a blank line, then:

```markdown
> Thực hành: [d01_srp](phase-03-clean-code-design-patterns/src/main/java/phase03/d01_srp/)
```

The mapping (section → link target) is exactly the file map table's package name. For `## Architecture Exercise` and `## Pattern Selection Exercise`, add:

```markdown
> Thực hành: [d21_capstone](phase-03-clean-code-design-patterns/src/main/java/phase03/d21_capstone/)
```

- [ ] **Step 3: Verify the whole module green**

Run: `.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test`
Expected: `BUILD SUCCESS`; every test green. Total = 154 tests, the sum of the per-package counts in each task's GREEN step: d01 6, d02 6, d03 6, d04 6, d05 7, d06 9, d07 8, d08 7, d09 8, d10 6, d11 7, d12 6, d13 5, d14 6, d15 6, d16 8, d17 7, d18 10, d19 11, d20 8, d21 11.

- [ ] **Step 4: Verify the module skeleton**

Run: `.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns`
Expected: `OK: <n> test(s) checked, skeleton rules satisfied.` with only `*_experimentRuns` tests green.

Run the per-package gates once more for the record:

```
d01_srp 6, d02_ocp 5, d03_lsp 5, d04_isp 5, d05_dip 6, d06_di 7, d07_composition 5,
d08_concerns 6, d09_layers 6, d10_strategy 6, d11_factory 5, d12_builder 5, d13_adapter 5,
d14_decorator 5, d15_observer 6, d16_template_method 5, d17_proxy 5, d18_singleton 7,
d19_clean_code 8, d20_testability 6
```

Expected: `OK` for each; `d21_capstone` with no `-ExpectedQuestions` also `OK`.

- [ ] **Step 5: Commit**

```bash
git add phase-03-clean-code-design-patterns/README.md 03-clean-code-design-patterns.md
git commit -m "docs(phase03): add README and plan links"
```

---

### Task 25: Giao bài lên `exercises` và `solutions` (không push)

**Files:**
- Branch `exercises`: module `phase-03-clean-code-design-patterns` added stripped, plus `pom.xml` module line, `README.md`, curriculum links
- Branch `solutions`: the same shared content with solved phase03 sources

**Interfaces:**
- Consumes: Task 24's verified `phase03-work`.
- Produces: learner skeleton on `exercises`, solved module on `solutions`.

- [ ] **Step 1: Confirm both target branches are clean and current**

Run in `C:\Users\thien\IdeaProjects\jav-wt\exercises`:

```powershell
git status --short
git log --oneline -3
```

Run in `C:\Users\thien\IdeaProjects\jav-wt\solutions`:

```powershell
git status --short
git log --oneline -3
```

Expected: both print nothing for `git status --short`. Record each branch's live tip SHA. If either has uncommitted changes, stop and report rather than overwriting.

- [ ] **Step 2: Bring shared content onto `exercises`**

In `C:\Users\thien\IdeaProjects\jav-wt\exercises`:

```powershell
git merge phase03-work --no-edit
```

If the merge is not clean because `exercises` moved, integrate onto its live tip instead of forcing: `git merge` and resolve only the `pom.xml` module block, `README.md` conflicts, and `03-clean-code-design-patterns.md` link lines, keeping prior-phase content intact.

- [ ] **Step 3: Strip phase03 production sources only**

```powershell
$env:JAVA_HOME = "C:\Users\thien\.jdks\jdk-21.0.12.1+1"
java tools/src/main/java/javaroadmap/tools/StripSolutions.java phase-03-clean-code-design-patterns/src/main/java
```

Expected: `Stripped <n> file(s).` Confirm no `SOLUTION-` marker remains:

```powershell
Get-ChildItem phase-03-clean-code-design-patterns/src/main/java -Recurse -Filter *.java |
  Select-String -Pattern 'SOLUTION-' -SimpleMatch
```

Expected: no output.

- [ ] **Step 4: Confirm the skeleton still compiles and is intentionally red**

```powershell
.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test -Dmaven.test.failure.ignore=true
.\tools\verify-skeleton.ps1 -Module phase-03-clean-code-design-patterns
```

Expected: `OK`, no compile failure, every red is `TODO Qn`/`TODO Bn`/`thay null`. Do not run a bare `.\mvnw.cmd -q test` here — the phase00 skeleton in this worktree is mid-authoring and its reds are unrelated.

- [ ] **Step 5: Commit on `exercises`**

```bash
git add pom.xml phase-03-clean-code-design-patterns 03-clean-code-design-patterns.md
git commit -m "chore(phase03): generate exercise skeleton"
```

- [ ] **Step 6: Bring solved phase03 onto `solutions`**

In `C:\Users\thien\IdeaProjects\jav-wt\solutions`:

```powershell
git merge phase03-work --no-edit
```

Resolve only `pom.xml`, `README.md`, and `03-clean-code-design-patterns.md` if needed. The phase03 sources stay solved with markers.

- [ ] **Step 7: Verify `solutions` green**

```powershell
.\mvnw.cmd -q -pl phase-03-clean-code-design-patterns test
```

Expected: `BUILD SUCCESS`, 154 tests green. This is the full-green reference for the phase03 module.

- [ ] **Step 8: Verify the branch difference is limited to solution regions**

```powershell
git diff --stat exercises solutions -- phase-03-clean-code-design-patterns
git diff exercises solutions -- phase-03-clean-code-design-patterns/src/test
```

Expected: the first shows changes only under `phase-03-clean-code-design-patterns/src/main/java` (and `README.md`); the second prints nothing — tests are identical across branches.

- [ ] **Step 9: Commit on `solutions`, then stop**

```bash
git add pom.xml phase-03-clean-code-design-patterns 03-clean-code-design-patterns.md
git commit -m "chore(phase03): add solutions"
```

Do not push. Do not delete `phase03-work`. Do not merge into `main`.

---

## Ghi chú thực thi

- Task 1 xong mới chạy Task 3–22. Task 3–22 độc lập: có thể chạy song song theo cụm, mỗi package một worktree tách từ `phase03-work` sau Task 1, rồi cherry-pick về.
- Task 23 phải sau Task 22 (capstone dùng `phase03.support.Predictions`) và là hai commit riêng: ghi hành vi trước, rồi refactor + failure paths.
- Task 24–25 trên `phase03-work` và hai worktree đích; chỉ người thực thi có quyền commit mới chạy bước commit.
- Không sửa `phase-00-java-basics` trong worktree này. Test phase00 đỏ ở đây là trạng thái đang biên soạn, không phải hồi quy của phase03.
- Không `git push`. Không tạo nhánh `phase03-exercises`.

## Self-review

- Spec coverage: mục 1–20 → Task 3–22 (mỗi mục một package, ít nhất một source và một test); Architecture Exercise → Task 23 (hai commit cộng failure paths); Pattern Selection → Task 24 README mục `## Chọn pattern`; Testing and generation → Task 2 cổng + Task 24 kiểm chứng; Branch delivery → Task 25.
- Placeholder scan: không còn `TBD`; mọi bước code có snippet thật; `TODO Qn` chỉ xuất hiện như marker bị strip.
- Type consistency: `PaymentGateway.PaymentResult` dùng thống nhất giữa main, fake và test; `OrderStatus` giá trị khớp ở service, controller và test; `dependencyCount(Class<?>)` khớp test.
- Review Focus: mỗi dòng có test trong task sở hữu — hành vi bất biến (`OrderApplicationServiceTest.b02_*` đối chiếu `CoupledOrderServiceTest.b01_*`), rollback ngoài (`b03_chargeSucceededButSaveFailed`), nuốt lỗi (`b02_declinedOrder`, `b05_noExceptionOnBusinessFailure`), pattern không có áp lực (Task 24 README kèm test `Q3`/`Q5` ở d11, `Q5` ở d02), rò rỉ skeleton (Task 2 bước 6, Task 25 bước 3–4).

<!-- PLAN-END -->
