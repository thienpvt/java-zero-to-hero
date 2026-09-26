# Giai đoạn 0 — Ôn tập và kiểm tra Java cơ bản

## Mục tiêu

Xác định ứng viên đã đủ nền tảng để bắt đầu `01-java-core-advanced.md` hay cần ôn lại phần nào. Đây là checklist chẩn đoán: mỗi chủ đề gồm kiến thức cần nhớ, câu hỏi giải thích và một việc phải làm bằng code. Không cần thuộc định nghĩa; hãy nói rõ code chạy ra sao và vì sao.

**Môi trường:** chọn JDK 21 hoặc 25 LTS và ghi version đã dùng; có thể dùng IDE bất kỳ, Git, Maven/Gradle và JUnit. Chạy thử code trước khi xem đáp án hoặc tra tài liệu. Nếu ứng viên chưa biết build tool, có thể làm các phần đầu bằng `javac`/`java`, rồi học build tool ở mục 12.

**Cách kiểm tra:** làm lần lượt từng mục, ghi điểm 0–2 theo bảng cuối tài liệu. Phân biệt “tôi đã nghe tới” với “tôi có thể viết code và giải thích kết quả”.

---

## 1. JDK, JVM và cấu trúc chương trình

### Kiến thức cần nắm

- Phân biệt JDK, JVM, `javac`, `java`, source `.java` và bytecode `.class`.
- Class, `main`, package, `import`; tên file và tên public class.
- Biên dịch/chạy project nhỏ, đọc lỗi compiler và stack trace thay vì đoán.

### Câu hỏi kiểm tra

1. JDK khác JVM ở đâu? Lệnh `javac` và `java` làm gì?
2. Khi nào cần `import`? Class cùng package có cần import không?
3. Lỗi compile khác exception lúc chạy như thế nào?

### Bài code

Tạo hai class trong cùng package: một class chứa hàm tính tổng, một class có `main` gọi hàm đó. Chạy từ terminal; cố ý đổi kiểu tham số sai để đọc lỗi biên dịch rồi sửa.

---

## 2. Biến, kiểu dữ liệu và phép toán

### Kiến thức cần nắm

- Primitive và reference; `int`, `long`, `double`, `boolean`, `char` và các wrapper.
- Giá trị mặc định của field khác với local variable chưa gán; scope và vòng đời biến.
- Chia số nguyên, ép kiểu, overflow; autoboxing/unboxing và nguy cơ `null`.
- Dùng `BigDecimal` tạo từ chuỗi cho bài toán tiền tệ cơ bản, tránh kỳ vọng phép toán dấu phẩy động luôn chính xác tuyệt đối.

### Câu hỏi kiểm tra

1. `5 / 2` và `5 / 2.0` cho kết quả khác nhau thế nào?
2. Điều gì xảy ra nếu `Integer x = null; int y = x;`?
3. Khi cộng vượt quá giới hạn của `int`, Java có tự chuyển sang `long` không?
4. `final` trên biến tham chiếu có làm object được trỏ tới bất biến không?

### Bài code

Viết hàm tính tổng tiền của nhiều mặt hàng bằng `BigDecimal`; thử giá, số lượng bằng 0, đầu vào âm và phép cộng có phần thập phân. Nêu cách ứng dụng xử lý dữ liệu không hợp lệ.

---

## 3. Điều khiển luồng và phương thức

### Kiến thức cần nắm

- `if/else`, `switch`, `for`, `while`, `break`, `continue`.
- Tham số, kiểu trả về, `return`; overload và tên phương thức rõ ý nghĩa.
- Java truyền **giá trị**: với object, giá trị được truyền là một bản sao của tham chiếu.

### Câu hỏi kiểm tra

1. Khi nào dùng `switch` thay cho chuỗi `if/else`?
2. `break` và `continue` ảnh hưởng vòng lặp ra sao?
3. Một phương thức gán lại tham số object có thay tham chiếu ở phía caller không? Thay đổi state của object đó thì sao?
4. Overload khác override ở điểm nào?

### Bài code

Viết hàm phân loại điểm số, kiểm tra ranh giới (0, 1, 49, 50, 99, 100) và đầu vào ngoài khoảng hợp lệ. Viết một bản dùng `if`, một bản dùng `switch` nếu điều kiện cho phép; giải thích bản nào dễ hiểu hơn.

---

## 4. Array, String và enum

### Kiến thức cần nắm

- Array có độ dài cố định, truy cập bằng index bắt đầu từ 0 và lỗi vượt chỉ số.
- `String` bất biến; phân biệt `==` và `equals()` khi so sánh nội dung.
- `StringBuilder` cho thao tác nối chuỗi lặp nhiều lần; `trim`, `isBlank`, `split` và giới hạn của cách parse đơn giản.
- `enum` biểu diễn tập trạng thái cố định.

### Câu hỏi kiểm tra

1. `new int[3]` có các index hợp lệ nào?
2. Vì sao không dùng `==` để kiểm tra hai chuỗi có cùng nội dung?
3. `String` bất biến nghĩa là gì với lời gọi `text.toUpperCase()`?
4. Khi nào `enum` tốt hơn những chuỗi trạng thái rải rác trong code?

### Bài code

Đọc một dòng gồm các trạng thái `NEW,DONE,NEW`, tách và đếm từng trạng thái hợp lệ. Xử lý `null`, chuỗi rỗng và trạng thái lạ bằng cách đã nêu rõ.

---

## 5. Class, object và đóng gói

### Kiến thức cần nắm

- Field, constructor, method, `this`; object được tạo bằng `new`.
- `private`, package-private, `protected`, `public`; chọn quyền truy cập nhỏ đủ dùng.
- Instance member khác static member; static field dùng chung và có thể tạo state toàn cục khó kiểm soát.
- Bảo vệ bất biến của object: validate ở constructor/method, không tạo setter cho mọi field một cách máy móc.

### Câu hỏi kiểm tra

1. Constructor có kiểu trả về không? Nếu không khai báo constructor thì điều gì được tạo?
2. `static` method có truy cập trực tiếp instance field không? Vì sao?
3. Vì sao `balance` của tài khoản nên là `private`?
4. Một getter trả về trực tiếp `List` nội bộ có thể gây lỗi gì?

### Bài code

Viết class `BankAccount` có mã tài khoản và số dư; chỉ cho phép nạp/rút số dương, không rút quá số dư. Viết cách sử dụng chứng minh caller không thể sửa số dư tùy ý.

---

## 6. Kế thừa, đa hình, interface và abstract class

### Kiến thức cần nắm

- Quan hệ is-a; `extends`, `implements`, `super`, `@Override`.
- Phân biệt override với overload; phương thức instance được dispatch theo kiểu object thực khi override.
- Interface định nghĩa hợp đồng; abstract class có thể chứa state/logic chung.
- Ưu tiên một mô hình đơn giản có ý nghĩa; dùng composition khi chỉ cần tái sử dụng hành vi.

### Câu hỏi kiểm tra

1. Biến kiểu interface có thể trỏ đến những implementation nào?
2. Khi gọi một method đã override qua tham chiếu parent, bản nào chạy?
3. Java cho phép class `extends` nhiều class cùng lúc không?
4. Khi nào interface phù hợp hơn abstract class?

### Bài code

Tạo interface `ShippingFeePolicy` với hai implementation: phí cố định và phí theo khối lượng. Viết hàm nhận interface để tính phí mà không kiểm tra tên class cụ thể.

---

## 7. Object, so sánh và biểu diễn dữ liệu

### Kiến thức cần nắm

- `==` trên reference kiểm tra cùng object; `equals()` thường dùng cho bằng nhau theo giá trị nếu class định nghĩa như vậy.
- Vai trò của `toString()`; nguyên tắc cơ bản: khi hai object `equals()` là true, `hashCode()` phải giống nhau.
- Không suy ra hai object bằng nhau chỉ vì cùng `hashCode()`; học chi tiết ở giai đoạn 1.

### Câu hỏi kiểm tra

1. Hai object `new Customer("A")` khác nhau có mặc định `equals()` là true không?
2. Vì sao override `equals()` mà quên `hashCode()` có thể làm `HashSet` hoạt động sai kỳ vọng?
3. `toString()` nên chứa thông tin gì và tránh lộ thông tin gì?

### Bài code

Viết value object `ProductCode` bất biến (có thể dùng `record` nếu JDK đã học). Thêm hai mã cùng giá trị vào `HashSet` và giải thích số phần tử. So sánh với một class thường chưa override `equals()`.

---

## 8. Exception và quản lý tài nguyên

### Kiến thức cần nắm

- Checked/unchecked exception ở mức sử dụng; `throw`, `throws`, `try/catch/finally`.
- Đọc stack trace từ nguyên nhân và dòng lỗi; không nuốt exception.
- `try-with-resources` đóng tài nguyên có `AutoCloseable`, kể cả khi có lỗi.

### Câu hỏi kiểm tra

1. `NumberFormatException` có thể xảy ra khi nào? Nên bắt ở đâu trong bài nhập liệu?
2. `throws` và `throw` khác nhau ra sao?
3. `finally` và `try-with-resources` giải quyết những vấn đề gì?
4. Vì sao `catch (Exception e) { return null; }` thường làm khó tìm lỗi?

### Bài code

Viết hàm parse số lượng từ chuỗi; xử lý chuỗi trống, không phải số và số âm bằng lỗi có ý nghĩa. Viết test chứng minh đường thành công và các đường lỗi.

---

## 9. Collections ở mức sử dụng

### Kiến thức cần nắm

- `List` giữ thứ tự và cho phép phần tử trùng; `Set` loại phần tử trùng theo quy tắc bằng nhau; `Map` ánh xạ key → value.
- Tạo, thêm, lấy, duyệt, xóa; `getOrDefault` và kiểm tra key không tồn tại.
- Phân biệt `ArrayList`, `HashSet`, `HashMap` theo mục đích; chưa cần thuộc chi tiết bucket/resize.
- Tránh sửa collection trực tiếp trong vòng `for-each` khi không có cơ chế xóa phù hợp.

### Câu hỏi kiểm tra

1. Vì sao dùng `Set` để phát hiện mã đơn hàng trùng?
2. `Map.get(key)` trả `null` có luôn chứng minh key không tồn tại không?
3. Muốn giữ thứ tự thêm phần tử, `HashSet` có bảo đảm không?
4. Khi cần đếm số lần xuất hiện của một từ, bạn chọn cấu trúc nào?

### Bài code

Nhận một `List<String>` mã sản phẩm; trả về số lần mỗi mã xuất hiện, danh sách mã trùng và danh sách mã duy nhất theo thứ tự gặp đầu tiên. Viết test cho danh sách rỗng và các mã trùng.

---

## 10. Generics ở mức cơ bản

### Kiến thức cần nắm

- Viết `List<String>`, `Map<String, Integer>`; compiler kiểm tra kiểu phần tử.
- Generic method/class đơn giản; không dùng raw type để né lỗi biên dịch.
- Wildcard/PECS/type erasure học sâu trong `01-java-core-advanced.md`.

### Câu hỏi kiểm tra

1. `List<String>` giúp tránh lỗi nào so với raw `List`?
2. Có thể thêm `Integer` vào `List<String>` bình thường không?
3. Khi nào một phương thức tiện ích nên là `<T> T first(List<T> items)`?

### Bài code

Viết generic method trả phần tử đầu tiên của một `List<T>`; định nghĩa rõ hành vi khi list rỗng và kiểm tra với `String` cùng `Integer`.

---

## 11. File I/O và thời gian cơ bản

### Kiến thức cần nắm

- `Path`, `Files`, UTF-8; phân biệt lỗi không có file, lỗi đọc và dữ liệu sai.
- Chọn đọc cả file hay đọc từng dòng tùy kích thước; đóng reader/writer đúng cách.
- `LocalDate` cho ngày lịch và `Instant` cho thời điểm tuyệt đối; không xem `LocalDateTime` là timestamp có múi giờ.

### Câu hỏi kiểm tra

1. Vì sao nên chỉ rõ charset khi đọc/ghi file văn bản?
2. File rất lớn có nên luôn dùng `Files.readAllLines()` không?
3. `LocalDate` và `Instant` biểu diễn những thông tin khác nhau nào?

### Bài code

Đọc file văn bản nhỏ gồm `productCode,quantity` (quy ước dữ liệu không chứa dấu phẩy bên trong mã). Tính tổng số lượng và ghi một báo cáo UTF-8; xử lý file không tồn tại, dòng lỗi và đóng tài nguyên.

---

## 12. Test, debug và build cơ bản

### Kiến thức cần nắm

- Chạy project bằng Maven hoặc Gradle; đọc kết quả build/test; dùng Git giữ lịch sử thay đổi.
- Test JUnit có tên mô tả hành vi, phần chuẩn bị–thực thi–kiểm tra rõ ràng.
- Test trường hợp bình thường, biên và lỗi; dùng debugger/breakpoint hoặc log phù hợp để tìm nguyên nhân.
- Không coi một lần chạy thành công là bằng chứng đủ cho mọi đầu vào.

### Câu hỏi kiểm tra

1. Unit test khác việc chạy `main` và nhìn output ở đâu?
2. Vì sao test cần kiểm tra kết quả cụ thể thay vì chỉ gọi method không ném lỗi?
3. Khi test fail, bạn kiểm tra expected/actual và stack trace theo thứ tự nào?

### Bài code

Đưa ít nhất một bài ở trên vào project Maven/Gradle. Viết test cho một trường hợp bình thường, một trường hợp biên và một lỗi; chạy test từ terminal, cố ý làm một assertion fail rồi sửa.

---

## Bài tổng hợp — Quản lý đơn hàng ở bộ nhớ

Viết ứng dụng console nhỏ để ôn lại 12 mục, không dùng Spring hoặc database.

1. `Order` có ID, mã khách hàng, ngày tạo, tổng tiền (`BigDecimal`) và `Status` dạng enum. Không cho phép tổng tiền âm.
2. Lưu đơn trong `Map<String, Order>`; thêm mới, tìm theo ID, đổi trạng thái theo quy tắc bạn nêu rõ, và liệt kê đơn theo khách hàng.
3. Nhập một file đơn giản do bạn quy định định dạng, charset UTF-8 và cách báo lỗi theo số dòng. Không cần parser CSV đầy đủ; ghi rõ giới hạn định dạng.
4. Tách xử lý nhập liệu, quy tắc đơn hàng và in kết quả thành các phương thức/class vừa đủ hiểu; không cần áp dụng design pattern nâng cao.
5. Viết test cho ID trùng, đơn không tồn tại, trạng thái chuyển sai, số tiền âm, file rỗng và một đường thành công.
6. Thêm README ngắn gồm JDK đang dùng, lệnh build/test/chạy, quy ước file và một quyết định thiết kế.

**Bằng chứng đạt:** project chạy từ terminal; test xanh; kết quả đúng với các ca trên; ứng viên giải thích được vì sao chọn `Map`, `enum`, `BigDecimal` và nơi xử lý exception.

---

## Chấm điểm và quyết định ôn tập

Chấm **mỗi mục 1–12** từ 0 đến 2 điểm:

| Điểm | Bằng chứng |
| --- | --- |
| 0 | Không giải thích được hoặc code không chạy; chưa phát hiện lỗi chính. |
| 1 | Giải thích được một phần hoặc code chạy với gợi ý nhưng chưa xử lý biên/lỗi. |
| 2 | Tự giải thích đúng, code chạy và kiểm tra được trường hợp biên/lỗi quan trọng. |

Tổng tối đa **24 điểm**. Từ **18/24**, không có mục 5, 6, 8, 9 ở mức 0 và hoàn thành bài tổng hợp: có thể bắt đầu `01-java-core-advanced.md`. Nếu chưa đạt, ôn các mục điểm 0 trước, làm lại bài code liên quan rồi tự chấm lại; điểm là công cụ tìm lỗ hổng, không phải đánh giá năng lực tổng thể của ứng viên.

**Tài liệu chuẩn:** [Learn Java](https://dev.java/learn/), [Java Language Basics](https://dev.java/learn/language/constructs/), [Classes and Objects](https://dev.java/learn/language/oop/classes-objects/).
