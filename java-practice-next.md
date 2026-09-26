# Luyện thêm Java sau các domain trong repo

Hoàn thành các bài và test của từng domain trong repo này trước. Sau đó chọn bài bên ngoài theo kỹ năng muốn củng cố; không cần làm hết bốn kho theo thứ tự. Các tài nguyên dưới đây bổ sung bài thực hành, không thay thế phần dự đoán, thí nghiệm và giải thích trade-off trong repo này.

| Kho bài | Nên luyện thêm gì | Cách làm và đối chiếu |
| --- | --- | --- |
| [Bobocode — Java Fundamentals Exercises](https://github.com/bobocode-projects/java-fundamentals-exercises) | Bài toán ứng dụng theo chủ đề: Java basics, cấu trúc dữ liệu, OOP, Generics, Streams và Optional. Phù hợp nhất để chuyển kiến thức Java Core thành code giải quyết yêu cầu cụ thể. | Đọc README của bài, cài đặt phần còn thiếu rồi chạy test; xem nhánh [`completed`](https://github.com/bobocode-projects/java-fundamentals-exercises/tree/completed) **sau** khi tự giải. Một số bài có tài liệu hoặc video tham khảo. |
| [Exercism — Java Track](https://github.com/exercism/java) | Nhiều bài ngắn về cú pháp, OOP, Collections, thuật toán và xử lý dữ liệu; chọn bài thuộc domain cần luyện lại thay vì làm trọn kho. | Làm bài qua [Java Track trên Exercism](https://exercism.org/tracks/java): tải bài, chạy test, gửi lời giải để nhận phản hồi. Repo GitHub là mã nguồn bài tập và test, **không có một nhánh đáp án chung** như repo này. |
| [Java8 Code Kata](https://github.com/konohiroaki/java8-code-kata) | Luyện tập trung Collection APIs, Stream API và Date/Time API, đặc biệt khi cần quen với các phương thức Java 8. | Sửa phần cần hoàn thành ngay trong test, chạy test tương ứng; đối chiếu nhánh [`solution`](https://github.com/konohiroaki/java8-code-kata/tree/solution). Phạm vi Java 8, không dùng thay phần Records hoặc sealed types của Java 21. |
| [Java Stream Kata](https://github.com/HubertWo/java-stream-kata) | Luyện sâu Stream API qua các bài nhỏ, khi đã học Lambda và Streams mà vẫn cần thêm ví dụ. | Chạy từng JUnit test, tự làm trước; xem phần đáp án có thể mở trong bài hoặc nhánh [`answers`](https://github.com/HubertWo/java-stream-kata/tree/answers). Chỉ tập trung Streams, không phải lộ trình Java Core đầy đủ. |

**Cách chọn nhanh:** muốn bài ứng dụng nhiều domain → Bobocode; muốn thêm nhiều bài độc lập → Exercism; muốn ôn API Collections/Streams/Date-Time → Java8 Code Kata; chỉ muốn luyện Streams → Java Stream Kata.
