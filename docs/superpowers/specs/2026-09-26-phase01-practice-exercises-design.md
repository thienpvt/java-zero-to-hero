# Thiết kế: Bài thực hành trong IDE cho Giai đoạn 1 — Java Core nâng cao

Ngày: 2026-09-26
Trạng thái: chờ duyệt spec

## 1. Mục tiêu

Biến các câu hỏi kiểm tra trong `01-java-core-advanced.md` thành bài thực hành chạy được trong IDE, để ứng viên:

- Biết **bắt đầu từ đâu** với mỗi câu (bước đầu tiên cụ thể trong IntelliJ).
- Biết **cách tự tìm ra câu trả lời** (debugger, source JDK, Javadoc, thí nghiệm).
- **Tự kiểm tra** kết quả bằng JUnit thay vì chỉ đọc và đoán.

Tiêu chí thành công: mỗi câu hỏi trong file `.md` (16 domain) được ánh xạ vào đúng một file Java; ứng viên có thể làm từng câu một, chạy test của câu đó và biết mình đúng/sai kèm hướng tìm hiểu.

## 2. Phạm vi

**Trong phạm vi:** Giai đoạn 1 (`01-java-core-advanced.md`), toàn bộ 16 domain và bài thực hành tích hợp. Đây là bản mẫu; sau khi duyệt sẽ nhân rộng cùng khuôn cho Giai đoạn 0, 2, 3 (mỗi giai đoạn một spec/plan riêng).

**Ngoài phạm vi:**
- Giai đoạn 0, 2, 3 và các giai đoạn chưa có plan chi tiết.
- Sửa nội dung kiến thức trong file `.md` (chỉ thêm link).
- Thư viện test ngoài JUnit 5 (AssertJ, Mockito, JMH).
- Đọc internals JDK bằng reflection (`--add-opens`).

## 3. Quyết định đã chốt

| Chủ đề | Quyết định |
|---|---|
| Dạng bài | Khung code có `TODO` + JUnit 5 test tự kiểm tra |
| Vị trí hướng dẫn | Chỉ trong Javadoc của file Java; `.md` giữ nguyên, thêm link |
| Build | Maven multi-module + Maven Wrapper, JDK 21 LTS |
| Bố cục | Mỗi giai đoạn là một module ở gốc repo; mỗi domain là một package |
| Chia file | 2–5 file mỗi domain, gom theo chủ đề con; mỗi file một test class |
| Lời giải | Nhánh git `solutions`; `main` chỉ có khung bài và test đỏ |
| Câu lý thuyết | Ưu tiên "dự đoán rồi kiểm chứng"; không kiểm chứng được bằng code thì `[TỰ TRẢ LỜI]` |

## 4. Cấu trúc project

```text
jav/
├── pom.xml                          # parent: JDK 21, JUnit 5 BOM, surefire
├── mvnw, mvnw.cmd, .mvn/wrapper/
├── .gitignore                       # target/, .idea/, *.iml
├── 00-...md, 01-java-core-advanced.md, 02-...md, 03-...md, java-advanced-roadmap.md
└── phase-01-core-advanced/
    ├── pom.xml
    ├── README.md
    └── src/
        ├── main/java/phase01/<domain>/ExNN_<Topic>.java
        └── test/java/phase01/<domain>/ExNN_<Topic>Test.java
            (d17_capstone có thêm src/test/resources/phase01/d17_capstone/*.csv)
```

- Package gốc: `phase01`. Package domain: `dNN_<tên>` (ví dụ `d04_hashmap`).
- File bài tập: `ExNN_<ChủĐềCon>.java`, test tương ứng: `ExNN_<ChủĐềCon>Test.java`.
- Parent POM chỉ khai báo module `phase-01-core-advanced` ở bản này.

## 5. Cấu trúc một file bài tập

### 5.1 Loại câu hỏi

| Nhãn | Ứng viên làm gì | Kiểm tra |
|---|---|---|
| `[CODE]` | Cài đặt method đang `throw new UnsupportedOperationException("TODO Qn")` | Test JUnit |
| `[DỰ ĐOÁN]` | Thay `null` trong hằng số dự đoán (`Boolean`, `Integer`, `String` hoặc enum lựa chọn khai báo trong file) | Test chạy code thật, so với dự đoán; sai thì thông báo kết quả thực tế và gợi ý tìm hiểu |
| `[THÍ NGHIỆM]` | Chạy `main()` của file, ghi quan sát vào khối `OBSERVATION Qn` | Không assert (kết quả đo không ổn định); test chỉ kiểm tra `main()` chạy không lỗi |
| `[TỰ TRẢ LỜI]` | Viết vào khối `ANSWER Qn` cuối file | So với nhánh `solutions` |

Một câu có thể mang nhiều nhãn (ví dụ `[DỰ ĐOÁN + CODE]`). Câu dạng "có biên dịch được không" dùng enum `Compiles { YES, NO }` làm dự đoán; ứng viên kiểm chứng bằng cách bỏ comment đoạn code mẫu trong IDE và xem lỗi đỏ, test chỉ so dự đoán với đáp án.

### 5.2 Javadoc đầu file

Thứ tự bắt buộc:

1. Tiêu đề: `<Domain> — Bài N: <chủ đề con>`.
2. `Nguồn:` file `.md`, số mục, danh sách câu.
3. `Cần làm trước:` file/khái niệm tiên quyết (nếu có).
4. `Cách làm:` chạy test theo câu, xanh mới sang câu sau.
5. Mỗi câu một khối, gồm 4 phần cố định:
   - `Qn [NHÃN] <câu hỏi nguyên văn từ .md>`
   - `Bắt đầu:` bước đầu tiên cụ thể trong IDE.
   - `Kiểm chứng:` / `Tra cứu:` breakpoint ở đâu, phím tắt IntelliJ mở source JDK (Ctrl+N, Ctrl+F12, Ctrl+Q, F7), đoạn Javadoc/tài liệu cần đọc.
   - `Code:` phần phải cài đặt (nếu có).
   - `Hoàn thành khi:` test nào xanh + điều phải giải thích được bằng lời.

Thứ tự câu trong file có thể khác `.md` khi câu sau là nền tảng để trả lời câu trước; số hiệu `Qn` luôn giữ đúng số trong `.md`.

### 5.3 Thân file

- Code mẫu cần quan sát (class nhỏ, lồng `static` trong file bài tập).
- Hằng số dự đoán, đặt tên `Qn_<MÔ_TẢ>`, giá trị `null` trên `main`.
- Method `TODO`, package-private, `static` khi không cần state.
- Cuối file: khối comment `ANSWER Qn` / `OBSERVATION Qn` để trống trên `main`.

### 5.4 Quy ước test

- `@TestMethodOrder(MethodOrderer.MethodName.class)`; tên method `qNN_<mô tả>` (ví dụ `q10_changeIdSafely`), `@DisplayName("Q10 …")`.
- Test dự đoán: `assertNotNull` với thông điệp "Chưa điền dự đoán Qn_…", rồi `assertEquals(thựcTế, dựĐoán, <gợi ý tìm hiểu>)`.
- Thông điệp lỗi bằng tiếng Việt, mang tính hướng dẫn.
- Dùng `@TempDir` cho bài file I/O; không phụ thuộc thời gian thực hoặc múi giờ máy (luôn truyền `ZoneId`/`Clock` tường minh).
- Không có test phụ thuộc thứ tự chạy giữa các class.

### 5.5 Ví dụ tham chiếu

`d04_hashmap/Ex04_MutableKey.java` (câu 9, 10):

```java
package phase01.d04_hashmap;

import java.util.Map;
import java.util.Objects;

/**
 * HashMap — Bài 4: Key mutable
 *
 * Nguồn: 01-java-core-advanced.md, mục 4 (HashMap), câu 9–10.
 * Cần làm trước: Ex01_LookupAndCollision (luồng hashCode → bucket → equals).
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex04_MutableKeyTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q10 [DỰ ĐOÁN + CODE] Điều gì xảy ra nếu field tham gia hashCode() bị
 *     thay đổi sau khi object đã được put?
 *   Bắt đầu   : đọc class MutableKey bên dưới, điền 3 hằng số Q10_* (thay null).
 *   Kiểm chứng: chạy q10_prediction. Nếu sai, đặt breakpoint trong HashMap.getNode
 *               (Ctrl+N → HashMap → Ctrl+F12 → getNode), Debug test, F7 để thấy
 *               hash mới trỏ vào bucket nào và vì sao entry cũ không được tìm thấy.
 *   Code      : cài đặt changeIdSafely() để đổi id mà map vẫn tìm được value.
 *   Hoàn thành khi: q10_* xanh; giải thích được vì sao size() vẫn là 1 nhưng get() trả null.
 *
 * Q9 [TỰ TRẢ LỜI] Mutable object có nên được dùng làm HashMap key không?
 *   Bắt đầu   : làm Q10 trước, dùng chính kết quả quan sát được để trả lời.
 *   Tra cứu   : Javadoc java.util.Map, đoạn "Great care must be exercised if
 *               mutable objects are used as map keys" (Ctrl+Q trên chữ Map).
 *   Hoàn thành khi: viết xong khối ANSWER Q9, có nêu ít nhất một cách phòng tránh.
 */
public class Ex04_MutableKey {

    static final class MutableKey {
        int id;
        MutableKey(int id) { this.id = id; }
        @Override public boolean equals(Object o) {
            return o instanceof MutableKey k && k.id == id;
        }
        @Override public int hashCode() { return Objects.hash(id); }
    }

    // Q10 — dự đoán: put(key(1), "A"), rồi key.id = 2
    static final Boolean Q10_GET_BY_SAME_REFERENCE_FINDS_VALUE = null;
    static final Integer Q10_SIZE_AFTER_MUTATION = null;
    static final Boolean Q10_GET_BY_NEW_KEY_2_FINDS_VALUE = null;

    // Q10 — code
    static <V> void changeIdSafely(Map<MutableKey, V> map, MutableKey key, int newId) {
        throw new UnsupportedOperationException("TODO Q10");
    }
}

/* ANSWER Q9:
 *
 */
```

## 6. Ánh xạ câu hỏi → file (Giai đoạn 1)

Số trong ngoặc là số câu trong `01-java-core-advanced.md`. Mọi câu phải xuất hiện đúng một lần.

| Domain (mục .md) | File | Câu | Trọng tâm thực hành |
|---|---|---|---|
| `d01_generics` (1) | `Ex01_InvarianceAndWildcards` | 1, 2, 3, 4, 9 | `copy(dst, src)`, `max(coll, cmp)` theo PECS |
| | `Ex02_TypeErasure` | 5, 6, 7, 10, 11 | Dự đoán biên dịch/`getClass()`; tạo `T` qua `Supplier<T>`/`Class<T>` |
| | `Ex03_GenericMethodVsWildcard` | 8 | `swap` với wildcard capture |
| `d02_arraylist` (2) | `Ex01_CapacityAndResize` | 1, 2, 3, 4, 6 | `MiniArrayList` với `grow()`, đếm số lần resize |
| | `Ex02_InsertRemoveCost` | 5, 10 | Thí nghiệm insert đầu/giữa/cuối |
| | `Ex03_Pitfalls` | 7, 8, 9 | Dự đoán `ConcurrentModificationException`; sửa bằng `Iterator.remove`/`removeIf` |
| `d03_linkedlist` (3) | `Ex01_NodeTraversal` | 1, 2 | `MiniLinkedList.get(i)` duyệt từ đầu gần hơn |
| | `Ex02_MemoryAndLocality` | 3, 5, 6 | Thí nghiệm + tự trả lời |
| | `Ex03_WhenLinkedListWins` | 4 | Chèn khi duyệt bằng `ListIterator` |
| `d04_hashmap` (4) | `Ex01_LookupAndCollision` | 1, 2, 3, 4, 5 | `MiniHashMap` bucket chain; key hashCode tệ |
| | `Ex02_EqualsWithoutHashCode` | 6 | Dự đoán lookup thất bại |
| | `Ex03_ResizeAndTreeify` | 7, 8, 11, 12 | `capacityFor(expectedSize)` |
| | `Ex04_MutableKey` | 9, 10 | Như mục 5.5 |
| | `Ex05_MapVariants` | 13, 14, 15 | Thứ tự duyệt, null key, lost update đa luồng |
| `d05_equals_hashcode` (5) | `Ex01_EqualsContract` | 1, 5 | `Money.equals`; test từng tính chất contract; Integer cache 127/128 |
| | `Ex02_HashCodeRules` | 2, 3, 4 | Dự đoán kích thước `HashSet` |
| | `Ex03_RecordsAndMutableEntities` | 6, 7 | record equals; entity mutable trong `HashSet` |
| `d06_hashset` (6) | `Ex01_Uniqueness` | 1, 2, 4 | Phát hiện duplicate; so lookup với `ArrayList` |
| | `Ex02_SetVariants` | 3, 5, 6 | Thứ tự duyệt; `TreeSet` ném `ClassCastException` |
| `d07_comparable_comparator` (7) | `Ex01_NaturalVsCustomOrder` | 1, 2, 3 | `User implements Comparable` + `Comparator` |
| | `Ex02_ComparatorComposition` | 5, 6 | Nhiều field, `nullsLast` |
| | `Ex03_InconsistentWithEquals` | 4 | `TreeSet` "nuốt" phần tử |
| `d08_immutability` (8) | `Ex01_FinalIsNotImmutable` | 1, 2 | Dự đoán |
| | `Ex02_DefensiveCopy` | 3, 5 | Sửa class lộ state; record + `List.copyOf` |
| | `Ex03_WhyImmutable` | 4, 6 | Tự trả lời + thí nghiệm |
| `d09_lambda` (9) | `Ex01_FunctionalInterfaces` | 1, 2, 3 | Compose `Predicate`/`Function`/`Consumer` |
| | `Ex02_CaptureAndMethodRef` | 4, 5, 6 | Dự đoán biên dịch effectively final; lambda ↔ method ref |
| `d10_stream` (10) | `Ex01_MapVsFlatMap` | 1 | Code |
| | `Ex02_LazinessAndPipeline` | 2, 3, 4, 11 + ví dụ `users.stream()` | Dự đoán thứ tự thực thi qua `peek` |
| | `Ex03_ReduceVsCollect` | 5, 6, 7 | `reduce`/`collect`; sửa bug side effect |
| | `Ex04_ParallelStreams` | 8, 9, 10 | Thí nghiệm; tên thread common pool |
| `d11_optional` (11) | `Ex01_CreateAndUnwrap` | 1, 2, 3 | Counter chứng minh `orElse` luôn được tính |
| | `Ex02_MapFlatMapAndUsage` | 4, 5, 6 | Refactor null-check chain |
| `d12_datetime` (12) | `Ex01_TimeTypes` | 1, 2, 3 | Dự đoán |
| | `Ex02_DurationPeriodDst` | 4, 5, 6 | 1 ngày vs 24 giờ qua DST `America/New_York` |
| | `Ex03_AppointmentModeling` | 7 | Mô hình lưu cuộc hẹn + tự trả lời |
| `d13_reflection` (13) | `Ex01_InspectAndInvoke` | 1, 4, 5 | Liệt kê field, gọi private method |
| | `Ex02_MiniDiContainer` | 2, 3, 6 | Container DI tí hon; thí nghiệm gọi trực tiếp vs reflection |
| `d14_annotation` (14) | `Ex01_RetentionAndTarget` | 1, 2, 3, 4 | Dự đoán SOURCE/RUNTIME lúc chạy; `@Target` sai |
| | `Ex02_CustomAnnotationProcessing` | 5, 6 | Quét `@Component` (dùng lại container d13); validator `@NotBlank` |
| `d15_modern_java` (15) | `Ex01_Records` | 1, 2 | record vs POJO; bảo vệ component `List` |
| | `Ex02_SealedAndPatternMatching` | 3, 4, 5 | `Shape` sealed + `switch`; thêm subtype xem compiler báo |
| `d16_exceptions_resources` (16) | `Ex01_TryWithResources` | 1, 2 | Thứ tự `close()`, suppressed exception |
| | `Ex02_FileIo` | 3, 4, 5 | Đọc theo dòng với `@TempDir`; bọc `IOException` giữ cause |
| `d17_capstone` (Bài tích hợp) | `Order`, `CsvOrderParser`, `CustomerReportService` | Bước 1–5 | CSV mẫu trong test resources; test file rỗng, dòng lỗi, ID trùng, múi giờ, kết quả không cho sửa |

Tổng: 44 file bài tập + 3 file khung capstone, mỗi file có test tương ứng.

Nguyên tắc: cơ chế bên trong (bucket, resize, node) học qua bản `Mini*` tự viết và debugger bước vào source JDK; không dùng reflection đọc field private của JDK.

## 7. Quy trình học (nội dung `phase-01-core-advanced/README.md`)

1. Clone repo, tạo nhánh riêng: `git switch -c my-work`.
2. Mở project trong IntelliJ; nếu chưa có JDK: *Project Structure → SDK → Download JDK* (21).
3. Mở file bài tập đầu tiên của domain, đọc Javadoc, làm theo **Bắt đầu**, chạy test của câu (▶), xanh mới sang câu sau.
4. Khi bí: `git diff my-work solutions -- <đường dẫn file>` hoặc IntelliJ *Compare with Branch*.
5. Đánh dấu bảng checklist theo domain trong README; cuối README là Exit Criteria của giai đoạn.

Chạy test:
- Một test/file: nút ▶ trong IntelliJ.
- Một domain: `mvnw -pl phase-01-core-advanced test -Dtest="phase01.d04_hashmap.*"`.
- Trên `main`, `mvnw test` **đỏ là trạng thái mong đợi**; README ghi rõ.

## 8. Nhánh git

- `main`: khung bài, Javadoc, test, hằng số dự đoán `null`, khối `ANSWER`/`OBSERVATION` trống.
- `solutions`: tách từ `main`; điền code, dự đoán đúng, đáp án ngắn, quan sát mẫu cho thí nghiệm.
- Sửa đề trên `main` trước rồi merge sang `solutions`.

## 9. Thay đổi file `.md`

Chỉ thêm một dòng ngay dưới tiêu đề mỗi domain trong `01-java-core-advanced.md`:

```markdown
> Thực hành: [d04_hashmap](phase-01-core-advanced/src/main/java/phase01/d04_hashmap/)
```

Và một dòng tương tự dưới "Bài thực hành tích hợp" trỏ tới `d17_capstone`. Không sửa nội dung khác.

## 10. Kiểm chứng trước khi báo hoàn thành

- **Nhánh `solutions`:** `mvnw test` xanh toàn bộ.
- **Nhánh `main`:** `mvnw test-compile` thành công; `mvnw test` chỉ đỏ do `UnsupportedOperationException("TODO …")` hoặc "Chưa điền dự đoán …", không có lỗi khác. Test của `[THÍ NGHIỆM]` chỉ kiểm tra `main()` chạy được nên có thể xanh ngay.
- **Độ phủ câu hỏi:** đối chiếu số câu theo từng domain giữa `.md` và các khối `Qn` trong Javadoc; mỗi câu đúng một lần.

## 11. Điều kiện tiên quyết

Máy hiện chưa có JDK (không có `java` trên PATH, không có `JAVA_HOME`, không có `~/.jdks`). Trước bước build/kiểm chứng cần cài JDK 21: qua IntelliJ, hoặc `winget install EclipseAdoptium.Temurin.21.JDK` khi người dùng cho phép. Maven Wrapper loại bỏ nhu cầu cài Maven riêng.
