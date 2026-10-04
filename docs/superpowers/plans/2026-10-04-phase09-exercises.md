# Giai đoạn 9 — Implementation Plan

> **For agentic workers:** thực hiện task theo thứ tự. Exact ownership, tests and acceptance gates below; no unrelated edits.

**Goal:** Implement phase09 exercises for all 20 source topics and source-verbatim Q1–Q5 within each topic. Add B labs, separate DSA assessment and design capstone.

**Architecture:** module `phase-09-algorithms-system-design`; Java 21, JUnit 5, no Spring/DB dependency for DSA. Solution sources contain `SOLUTION-*` markers; strip to learner skeleton. Written design answers reviewed by human. Topic 18 measurement consumes existing phase05 service and exported telemetry CSV; no new backend or dependency.

**Spec:** `docs/superpowers/specs/2026-10-04-phase09-exercises-design.md`

## Tech Stack

- JDK 21, parent Maven/JUnit 5 configuration; no new dependencies.
- `java.util` collections and `java.net.http.HttpClient`; standard library CSV/time/math.
- Existing `tools/StripSolutions.java` and `tools/verify-skeleton.ps1`; foundation owner owns tooling/POM changes.

## Global Constraints

- Source of truth `09-algorithms-system-design.md`; spec question register. Copy Q text verbatim. Numbering resets Q1–Q5 in each source topic; never globalize.
- Exactly 20 topic headings/packages: d01_complexity, d02_array_string, d03_hash_table, d04_two_pointers, d05_binary_search, d06_stack_queue_deque, d07_linked_list, d08_tree_bst, d09_heap_topk, d10_graph_bfs_dfs, d11_backtracking, d12_dynamic_programming, d13_requirements_slo, d14_capacity_estimation, d15_api_ownership, d16_horizontal_scaling, d17_caching, d18_database_scaling, d19_messaging_retry, d20_reliability_observability_security.
- Exactly one named source/test pair per topic, frozen mapping: d01 `d01_complexity/Ex01_Complexity.java` + `Ex01_ComplexityTest.java`; d02 `d02_array_string/Ex01_ArrayString.java` + `Ex01_ArrayStringTest.java`; d03 `d03_hash_table/Ex01_HashTable.java` + `Ex01_HashTableTest.java`; d04 `d04_two_pointers/Ex01_TwoPointers.java` + `Ex01_TwoPointersTest.java`; d05 `d05_binary_search/Ex01_BinarySearch.java` + `Ex01_BinarySearchTest.java`; d06 `d06_stack_queue_deque/Ex01_StackQueueDeque.java` + `Ex01_StackQueueDequeTest.java`; d07 `d07_linked_list/Ex01_LinkedList.java` + `Ex01_LinkedListTest.java`; d08 `d08_tree_bst/Ex01_TreeBst.java` + `Ex01_TreeBstTest.java`; d09 `d09_heap_topk/Ex01_HeapTopK.java` + `Ex01_HeapTopKTest.java`; d10 `d10_graph_bfs_dfs/Ex01_GraphBfsDfs.java` + `Ex01_GraphBfsDfsTest.java`; d11 `d11_backtracking/Ex01_Backtracking.java` + `Ex01_BacktrackingTest.java`; d12 `d12_dynamic_programming/Ex01_DynamicProgramming.java` + `Ex01_DynamicProgrammingTest.java`; d13 `d13_requirements_slo/Ex01_RequirementsSlo.java` + `Ex01_RequirementsSloTest.java`; d14 `d14_capacity_estimation/Ex01_CapacityEstimation.java` + `Ex01_CapacityEstimationTest.java`; d15 `d15_api_ownership/Ex01_ApiOwnership.java` + `Ex01_ApiOwnershipTest.java`; d16 `d16_horizontal_scaling/Ex01_HorizontalScaling.java` + `Ex01_HorizontalScalingTest.java`; d17 `d17_caching/Ex01_Caching.java` + `Ex01_CachingTest.java`; d18 `d18_database_scaling/Ex01_DatabaseScaling.java` + `Ex01_DatabaseScalingTest.java`; d19 `d19_messaging_retry/Ex01_MessagingRetry.java` + `Ex01_MessagingRetryTest.java`; d20 `d20_reliability_observability_security/Ex01_ReliabilityObservabilitySecurity.java` + `Ex01_ReliabilityObservabilitySecurityTest.java`. Full paths are under `phase-09-algorithms-system-design/src/main/java/phase09/` and matching `src/test/java/phase09/`. No more files per topic.
- Javadoc Vietnamese: heading, source path/section/Qs, prerequisites, first IDE action, investigation hint, CODE contract and completion criterion. Keep original Q string distinct from B1 exercise instructions.
- Category mapping freeze: all Q1–Q5 are `[TỰ TRẢ LỜI]` except Q prompts that can be predicted from a deterministic JDK/code behavior, marked `[DỰ ĐOÁN]` without changing wording. CODE belongs to B labs unless source question itself explicitly requests code (none do).
- Method contracts state null, range, empty, ordering, duplicate, overflow and graph direction. Avoid undefined symbols. Tests invoke TODO outside `assertThrows`.
- Test feedback and resources may show expected outcomes, never solved implementation. No timing thresholds. No edits to root POM, phase04/05, tool scripts, curriculum docs, `.idea` or user work. Workers may create atomic commits for owned product batches per coordinator; never push.

## Common Verification Flow

From repo root, PowerShell:

```powershell
$env:JAVA_HOME = 'C:\Users\thien\.jdks\jdk-21.0.12.1+1'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd -pl phase-09-algorithms-system-design -Dtest='phase09.d01_complexity.Ex01_ComplexityTest' test
.\mvnw.cmd -pl phase-09-algorithms-system-design test
.\tools\verify-skeleton.ps1 -Module phase-09-algorithms-system-design
```

For each task: (1) write one failing JUnit test for named method/contract; (2) run targeted command and observe `TODO B1`; (3) implement minimum marked method; (4) rerun targeted test green; (5) run full module; (6) strip copy and confirm learner failure is TODO only; (7) `git diff --check`, inspect paths. Report test counts. DSA must run without Docker/DB/phase05.

Seed oracle: `new Random(0x09_2026L)`, bounded n≤12 (less for combinatorial), assertions print seed/input. Oracle uses straightforward brute force, not production algorithm. No test uses timing as pass/fail.

## Task 1 — Module conventions and d01–d04

**Files (exact):**
`phase-09-algorithms-system-design/src/main/java/phase09/d01_complexity/Ex01_Complexity.java` + matching `src/test/java/.../Ex01_ComplexityTest.java`; same Ex01/Test convention under `d02_array_string`, `d03_hash_table`, `d04_two_pointers`.

**Methods/contracts:**
- d01 B1: `static long countPairsQuadratic(int[] values, int target)` and `static long countPairsLinear(int[] values, int target)`. Count index pairs i<j; duplicates count by positions; sum in long. null throws NPE. Q prompts remain theory. `static String experimentReport(int n)` reports operation counts, not timing verdict.
- d02 B1: `static String reverseCodePoints(String value)` reverses Unicode code points, not grapheme clusters; null NPE. `static int checkedArea(int width,int height)` rejects negative dimensions and throws `ArithmeticException` on overflow. Tests empty, surrogate pair, max integer overflow.
- d03 B1: `static Map<String,Integer> frequencies(List<String> values)` rejects null list/elements and returns immutable map; `record Key(String value)` is immutable. Tests duplicates, empty and null boundary.
- d04 B1: `static int[] pairSumSorted(int[] values,int target)` requires ascending input, returns first pair indices or `{-1,-1}`; `static int minWindowLength(int[] values,long target)` requires nonnegative values and target>0, returns 0 if absent. Tests duplicates, extremes, empty, negative rejection; compare window result with quadratic oracle.

**Representative test-first example:**
```java
@Test void b1_pairUsesDistinctIndicesForDuplicates() {
    assertEquals(3, Ex01_Complexity.countPairsQuadratic(new int[]{2,2,2}, 4));
    assertEquals(3, Ex01_Complexity.countPairsLinear(new int[]{2,2,2}, 4));
}
```
Expected initial red: `UnsupportedOperationException: TODO B1`; after solution green. Each file Javadoc additionally maps its exact source Q1–Q5 verbatim, labelled `[TỰ TRẢ LỜI]` unless prediction can execute deterministically.

## Task 2 — d05–d08 search and structures

**Files:** exact source/test pairs: `d05_binary_search/Ex01_BinarySearch.java` + `Ex01_BinarySearchTest.java`; `d06_stack_queue_deque/Ex01_StackQueueDeque.java` + matching test; `d07_linked_list/Ex01_LinkedList.java` + matching test; `d08_tree_bst/Ex01_TreeBst.java` + matching test. All under module `src/main/java/phase09/` and `src/test/java/phase09/`.

- d05 B1: `static int lowerBound(int[] values,int target)` returns insertion point `[0,n]`; `static int firstTrue(int low,int high,IntPredicate predicate)` requires `predicate(low)==false`, `predicate(high)==true`, `low<high`, and safe midpoint. Invalid preconditions IAE. Tests empty/duplicates/ends and firstTrue boundary.
- d06 B1: `static boolean balancedBrackets(String input)` accepts only `()[]{}`; null NPE; other chars false. `static int shortestGridPath(char[][] grid)` 4-neighbor BFS, `S`/`E` unique, `#` blocked, `.` open; rectangular nonempty grid only, malformed grid IAE, no path -1. Use `ArrayDeque`, mark visited on enqueue.
- d07 B1: nested mutable `Node(int value,Node next)`; `static Node reverse(Node head)`; `static Node cycleEntry(Node head)` Floyd; null head returns null. Identity, not value equality.
- d08 B1: nested `Node`; iterative `static List<Integer> inorder(Node root)` and `static boolean isBst(Node root)`; duplicates forbidden; iterative stack/long bounds; null tree valid. Tests skewed tree depth 20,000, duplicate and integer extremes.

One runnable JUnit per task must first fail on TODO then pass. Add boundary tests stated above and B1 source-lab completion note. Do not recast source Qs as these method signatures.

## Task 3 — d09–d12 algorithm families

**Files:** exact source/test pairs: `d09_heap_topk/Ex01_HeapTopK.java` + matching test; `d10_graph_bfs_dfs/Ex01_GraphBfsDfs.java` + matching test; `d11_backtracking/Ex01_Backtracking.java` + matching test; `d12_dynamic_programming/Ex01_DynamicProgramming.java` + matching test. All under module `src/main/java/phase09/` and `src/test/java/phase09/`.

- d09 B1 `static List<Integer> topK(int[] values,int k)`: descending output; ties preserve first occurrence; require `0<=k<=n`, null NPE; safe comparator. Include `Integer.MIN_VALUE/MAX_VALUE`.
- d10 B1 type `record Graph(Set<Integer> vertices,Map<Integer,List<Integer>> edges,boolean directed)` with compact constructor defensively copying vertices, map, and every nested adjacency list to immutable collections before validating endpoints; reject null/malformed edges and endpoints absent from copied vertex set. `static int componentCount(Graph g)` treats edges as undirected for component count; `static int shortestPath(Graph g,int start,int end)` requires undirected unweighted graph, returns edge distance or -1. Reject undeclared start/end. Test mutation of constructor inputs cannot alter graph and undeclared endpoint fails at construction.
- d11 B1 `static List<List<Integer>> combinations(int n,int k)` lexicographic ascending, values 1..n, invalid n/k IAE; state undo after branch. Duplicate Q discussion uses deduplicated input in a separate written example only.
- d12 B1 `static Optional<List<Integer>> minCoins(int[] denominations,int amount)` requires non-null denominations, positive unique values, `0 <= amount <= 10_000`, and at most 32 denominations. Return fewest coins or empty; bottom-up DP; tie-break lexicographically smallest sorted coin list. Validate `amount` and length before allocating DP; denominations greater than amount, including `Integer.MAX_VALUE`, are valid but ignored. Reject invalid contract inputs with IAE; compare with brute force for seeded small amounts; assert 10,001 fails before allocation and `Integer.MAX_VALUE` denomination is safely ignored for small amount.

Tests: top-K vs sort oracle; disconnected graph/self-loop/isolated/no path; combinations empty/invalid; DP zero/impossible/ties and seeded oracle. Each file retains exact source Q1–Q5 separately.

## Task 4 — d13–d17 design prompts and capacity calculation

**Files:** exact source/test pairs: `d13_requirements_slo/Ex01_RequirementsSlo.java` + matching test; `d14_capacity_estimation/Ex01_CapacityEstimation.java` + `Ex01_CapacityEstimationTest.java`; `d15_api_ownership/Ex01_ApiOwnership.java` + matching test; `d16_horizontal_scaling/Ex01_HorizontalScaling.java` + matching test; `d17_caching/Ex01_Caching.java` + matching test. All under module `src/main/java/phase09/` and `src/test/java/phase09/`.

- d13 `static String requirementsChecklist()` returns blank learner template only; B1 written order brief captures actors, FR/NFR, scope, assumptions, SLO/SLI, consistency/durability and unknowns. Review manually.
- d14 math helper: `static BigDecimal averageRps(long requestsPerDay)`; `static BigDecimal storageBytes(long orders,long bytesPerOrder,int days)`; `static BigDecimal physicalBytes(BigDecimal logical,int overhead,int replicas)`. Validate positive values; exact decimal division with caller-visible scale 6 HALF_UP; checked integer multiplications. B1 uses precisely source assumptions (1m orders/day, ten reads + one write per order, peak factor 10 over total API RPS, 2KiB/order, 365 days, overhead×2, copies×3); report read and write separately and formulas.
- d15 `static String orderFlowTemplate()` blank outline. B1 manual API/data owner diagram includes idempotency, authorization, transaction/invariant, DTO/entity, stable error contract.
- d16 `static String twoInstanceFailureTemplate()` blank outline. B1 review session affinity, DB pool/quota, lost instance, dependency budget, need for measurement.
- d17 `static String cacheDecisionTemplate()` blank outline. B1 specify cache key, TTL, invalidation, stale contract, stampede, outage, eviction/memory and metrics.

Tests assert math only and template markers exist; no phrase/key-word grading. A representative executable math test:
```java
@Test void b1_convertsDailyRequestsToRps() {
    assertEquals(new BigDecimal("11.574074"), Ex01_CapacityEstimation.averageRps(1_000_000));
}
```

## Task 5 — d18 measurement, d19–d20 design

**Files:** `phase-09-algorithms-system-design/src/main/java/phase09/d18_database_scaling/Ex01_DatabaseScaling.java` + `src/test/java/phase09/d18_database_scaling/Ex01_DatabaseScalingTest.java`; exact pairs `d19_messaging_retry/Ex01_MessagingRetry.java` + `Ex01_MessagingRetryTest.java`, `d20_reliability_observability_security/Ex01_ReliabilityObservabilitySecurity.java` + matching test. No endpoint/backend creation.

**d18 interfaces:**
- `record MetricSnapshot(String run,String snapshot,Instant capturedAtUtc,String metricName,String pool,long count,double totalTimeSeconds)`.
- `record HttpRun(String run,String settingLabel,Instant start,Instant end,int samples,long successes,long errors,long elapsedNanos,long p50Nanos,long p95Nanos,double throughputPerSecond)`; validate nonblank run/setting, `start < end`, samples>0, successes/errors nonnegative, successes+errors=samples, elapsedNanos>0, percentile nanos nonnegative and <=elapsedNanos, finite nonnegative throughput.
- `record AcquisitionResult(String run,long deltaCount,double meanAcquisitionSeconds)`.
- `record ComparisonReport(HttpRun baseline,HttpRun changed,AcquisitionResult baselineAcquisition,AcquisitionResult changedAcquisition)`.
- `static void writeRun(HttpRun run,Path output)` and `static HttpRun readRun(Path input)` serialize via JDK `Properties` with exact keys `run`, `setting`, `start`, `end`, `samples`, `successes`, `errors`, `elapsedNanos`, `p50Nanos`, `p95Nanos`, `throughputPerSecond`. ISO-8601 instants and numeric base-10 values; validate all fields on read and write; reject missing/unknown keys, malformed numbers/instants, non-finite values and inconsistent counts/times. Files contain no token or response body.
- `static List<MetricSnapshot> parseCsv(Reader input)`; `static AcquisitionResult validateRun(List<MetricSnapshot> rows,String run,Instant requestStart,Instant requestEnd)`.
- `static HttpRun measureRun(String run,URI baseUri,String bearerToken,int concurrency,int requests,Duration timeout,Duration duration,String settingLabel)` runs one pass only; it never mutates app config. `static ComparisonReport compare(HttpRun baseline,HttpRun changed,Path telemetryCsv)` validates four snapshots and returns HTTP+pool results.

CLI: `run baseline <baseUri> <token> <concurrency> <requests> <durationSeconds> <settingLabel> --output baseline.properties`; repeat `run changed ... --output changed.properties` after operator changes exactly one app setting; then `compare baseline.properties changed.properties telemetry.csv`. CLI compare calls `readRun` for both artifacts, then `compare`. `run` writes local Properties summary only, never token/response body. Operator exports Actuator snapshots before/after each run and assembles exact telemetry CSV with `run=baseline|changed`, `snapshot=start|end`.

Defaults/limits: concurrency default 4/max16; requests max1000; timeout default2s; measured duration default5s/max30s; warmup fixed and excluded. Telemetry window max300s, timestamps bracket requests within 5s. GET `/api/products?page=0&size=20`, products.read bearer; loopback only; no actuator polling. Metrics export via PowerShell, metrics.read bearer.

Validate CSV header, unique run×snapshot pairs, UTC timestamps, consistent metric/pool, integer nonnegative count, finite nonnegative cumulative seconds, monotonic count/time, positive delta. Hikari timer acquisition covers `getConnection()` call to connection handle return: report acquisition duration (pool wait + acquisition overhead), not pure queue wait or lease/usage time. Missing/malformed/stale/out-of-window snapshots, wrong run pairs, zero delta, failed HTTP request run, or non-loopback URL => diagnostic and nonzero exit; mandatory measurement fails, never skips/passes. No credential logging or response-body retention.

Properties artifact tests: `writeRun`/`readRun` round-trip; missing required key; malformed number/Instant; non-finite throughput; negative/inconsistent samples, success/error counts, elapsed/percentile values; wrong run IDs rejected by `compare`. CSV parser tests: valid four-row fixture; malformed header; missing/duplicate snapshot; run/metric/pool mismatch; invalid UTC; negative/noninteger/nonfinite values; decreasing count/time; delta zero; timestamp outside bracket. Request harness tests must not contact non-loopback or production.

- d19 B1 `static String eventFlowTemplate()` blank outline; manual write-up outbox commit, duplicates, bounded retries/backoff/jitter, backlog and lock-vs-multi-instance.
- d20 B1 `static String failureMatrixTemplate()` blank outline; manual scenarios slow dependency, DB unavailable, instance loss; observability, timeout/concurrency limits, auth/secrets/log privacy and recovery.

**Measurement acceptance:** manual run requires actual disposable phase05 app and valid telemetry; report HTTP p50/p95, successes/errors, throughput/sample count separately from acquisition mean and intervals. No timing assertion.

## Task 6 — DSA assessment, design capstone, docs integration

**Files:** `phase09/d21_dsa_assessment/Ex01_Assessment.java` + test; `phase09/d22_design_capstone/Ex01_DesignCapstone.java` + test only if Javadoc/template tests needed. These are assessment packages, not extra curriculum topics; no duplicate Q. Preserve source assessment prompt and capstone requirements verbatim in docs.

- DSA assessment combines array/string, graph or DP; bounded input, explicit malformed/empty/duplicate behavior, seeded brute-force oracle and complexity rationale. New B1 task, no Q numbering.
- Design capstone uses source `System Design — Capstone` submission checklist and Topic 18 measurement; diagram/notes reviewed manually, no prose grader or new backend.
- Update phase09 README only after all filenames/commands stable. Document local-only token, CSV export, exact 20-topic Q mapping, labs, branches/solutions and manual-failure behavior.

## Final gates

1. Audit all 100 question strings byte-for-byte against source, grouped into exactly 20 topics × local Q1–Q5; no invented/omitted question.
2. Run targeted JUnit and full module tests; DSA is service-independent.
3. Strip copy compiles, skeleton failures are expected TODO/prediction only; zero unexpected green/skips/missing reports.
4. Review all markers/resources for answer leaks; run `git diff --check` and inspect changed paths.
5. Required measurement manual command succeeds only with local app and valid telemetry; otherwise fails explicitly. Never claim measurement pass without evidence.
6. Review plan/spec path match, phase05 endpoint/CSV/scopes, test totals and topic ownership before integration.
