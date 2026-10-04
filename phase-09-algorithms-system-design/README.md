# Phase 09 — Algorithms and System Design

Java 21/JUnit 5 module for 20 curriculum topics: DSA plus system design. Module uses Java standard library; DSA and full tests need no Spring, service, or database.

Run from repository root:

```powershell
$env:JAVA_HOME='C:\Users\thien\.jdks\jdk-21.0.12.1+1'
./mvnw.cmd -pl phase-09-algorithms-system-design test
./tools/verify-skeleton.ps1 -Module phase-09-algorithms-system-design
```

Run assessment only:

```powershell
./mvnw.cmd -pl phase-09-algorithms-system-design -Dtest=Ex01_AssessmentTest test
```

## Topic/Q map

Q numbers reset locally in each topic; B1 assessments/capstones are not extra topics or Qs.

| Topic | Package | Questions |
|---|---|---|
| 01. Độ phức tạp và cách kiểm chứng | `d01_complexity` | Q1–Q5 |
| 02. Array và String | `d02_array_string` | Q1–Q5 |
| 03. Hashing và Hash Table | `d03_hash_table` | Q1–Q5 |
| 04. Two Pointers và Sliding Window | `d04_two_pointers` | Q1–Q5 |
| 05. Binary Search | `d05_binary_search` | Q1–Q5 |
| 06. Stack, Queue và Deque | `d06_stack_queue_deque` | Q1–Q5 |
| 07. Linked List | `d07_linked_list` | Q1–Q5 |
| 08. Tree và Binary Search Tree | `d08_tree_bst` | Q1–Q5 |
| 09. Heap và Top-K | `d09_heap_topk` | Q1–Q5 |
| 10. Graph: BFS, DFS và đường đi | `d10_graph_bfs_dfs` | Q1–Q5 |
| 11. Backtracking | `d11_backtracking` | Q1–Q5 |
| 12. Dynamic Programming | `d12_dynamic_programming` | Q1–Q5 |
| 13. Requirements, scope và SLO | `d13_requirements_slo` | Q1–Q5 |
| 14. Ước lượng tải và dung lượng | `d14_capacity_estimation` | Q1–Q5 |
| 15. API, data ownership và invariant | `d15_api_ownership` | Q1–Q5 |
| 16. Horizontal scaling và load balancing | `d16_horizontal_scaling` | Q1–Q5 |
| 17. Caching | `d17_caching` | Q1–Q5 |
| 18. Database scaling và lựa chọn consistency | `d18_database_scaling` | Q1–Q5 |
| 19. Messaging, retry và failure handling | `d19_messaging_retry` | Q1–Q5 |
| 20. Reliability, observability và security | `d20_reliability_observability_security` | Q1–Q5 |

There are exactly 100 source questions: five per topic. Keep source wording, punctuation, code ticks and local numbering intact. Assessment deliverables `d21_dsa_assessment` and `d22_design_capstone` are B tasks only, not curriculum topics; prose answers and design diagrams receive human review, never keyword/string grading.

## Solutions and skeleton

Source exercises are unsolved by default. Put executable answers only in their declared `SOLUTION-BEGIN` block, with `throw Qn/B1` matching that exercise, and written model answers only in comment `SOLUTION` blocks. Run learner tests and skeleton verifier before recording solutions; skeleton failures are expected TODO/prediction behavior only. Do not use excluded groups or ignored failures to claim a pass. `PARTIAL` and `COMPILE-ONLY` are not full gates.

## Topic 18 bounded local measurement

Reuse `phase09.d18_database_scaling.Ex01_DatabaseScaling`; do not add a second backend. Local-only HTTP GET targets `/api/products?page=0&size=20`; set `PHASE09_BEARER_TOKEN` in the environment (scope `products.read`), never on command line or logs. Run baseline and changed with same disposable dataset/concurrency/duration and one justified setting change; export protected Actuator `hikaricp.connections.acquire` snapshots to UTF-8 CSV (scope `metrics.read`) for `compare`. Actual commands from repository root:

```powershell
$env:PHASE09_BEARER_TOKEN='local-token'
./mvnw.cmd -pl phase-09-algorithms-system-design exec:java -Dexec.mainClass=phase09.d18_database_scaling.Ex01_DatabaseScaling -Dexec.args="run baseline http://127.0.0.1:8080 4 1000 5 pool=4 --output baseline.properties"
# After exporting baseline/start+end and changed/start+end metric snapshots to telemetry.csv,
# repeat for changed, then compare:
./mvnw.cmd -pl phase-09-algorithms-system-design exec:java -Dexec.mainClass=phase09.d18_database_scaling.Ex01_DatabaseScaling -Dexec.args="compare baseline.properties changed.properties telemetry.csv"
```

CSV header must be exactly `run,snapshot,capturedAtUtc,metricName,pool,count,totalTimeSeconds`, four rows total. Missing token/app, malformed/stale/out-of-window telemetry, missing snapshots or zero counter delta must fail explicitly with nonzero exit; they are not skip/pass and must never be fabricated as zero pool wait. Report HTTP latency/throughput separately from mean Hikari acquisition (pool wait plus acquisition overhead, not pure queue wait). Assessment/test runs do not constitute application measurement.

A historical bounded local measurement ran on 2026-10-05 (UTC 2026-10-04) against the real secured native Phase05 capstone app and disposable PostgreSQL 18.0; see [setup, results, exits and caveats](evidence/2026-10-05-pool-comparison/measurement.txt) and [existing d18 comparison output](evidence/2026-10-05-pool-comparison/comparison.txt). With the same dataset and concurrency 4, changing only pool size 2 to 4 yielded 1000 successes and zero errors in each run: throughput 757.651580 to 776.860073 successes/s, but HTTP p95 worsened from 7.1945 to 7.3307 ms. Mean native Hikari acquisition fell from 0.000275811876 to 0.000007066766 seconds, including two warmup acquisitions per run. Both reached the 1000-request cap before the 5-second limit (1.3198679 / 1.2872331 seconds). This single sequential pair is not steady state or causal improvement evidence, a production SLO, or a pool-size recommendation; JIT/cache/cold-pool/warmup noise remains. Static examples are illustrative, not additional measurements.

`tools/verify-skeleton.ps1` is intended for a temporary skeleton copy when this checkout has solved sources; never strip live sources. Do not publish/claim remote test status from a local branch.

## Manual review

`Ex01_Assessment` tests bounded grid BFS against a deterministic small brute-force oracle, plus empty/malformed/boundary behavior. Its expected complexity rationale is O(rows × columns) time and space. Review written assessment/design reasoning manually; no prose grader.

`d22_design_capstone.Ex01_DesignCapstone` preserves source capstone checklist and links measurement to the existing d18 harness. Submit diagram/notes, local setup, dataset, concurrency, one change, throughput/p95/error-rate/acquisition evidence and limits; separate assumptions from observations.
