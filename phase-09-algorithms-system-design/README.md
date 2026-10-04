# Phase 09 — Algorithms and System Design

Mô-đun Java 21 cho cấu trúc dữ liệu, thuật toán và system design. Đây là module Java thuần theo parent root, không cần Spring runtime hay database.

Chạy test module từ thư mục gốc repository:

```powershell
./mvnw.cmd -pl phase-09-algorithms-system-design test
```

Kiểm tra skeleton sau khi chạy test:

```powershell
./tools/verify-skeleton.ps1 -Module phase-09-algorithms-system-design
```

Verifier đối chiếu report với test inventory, yêu cầu bài tập chưa giải còn đỏ đúng lý do; `PARTIAL` không phải gate đầy đủ. Nếu module chưa có test inventory, verifier chỉ báo `COMPILE-ONLY`, không phải test suite xanh. Hiện Phase 09 có các bài DSA từ d01 đến d08; đây là curriculum một phần, các chủ đề còn lại vẫn cần bổ sung. DSA không yêu cầu database; PostgreSQL chỉ là prerequisite riêng nếu sau này chạy phép đo telemetry.
