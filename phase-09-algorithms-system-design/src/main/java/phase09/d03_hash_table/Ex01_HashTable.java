package phase09.d03_hash_table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Hashing và Hash Table.
 * Nguồn: 09-algorithms-system-design.md, mục 3, câu 1–5.
 * Cần làm trước: ôn equals/hashCode, Map và tính bất biến của key.
 * Cách làm: trả lời từng câu trong ANSWER tương ứng; chạy test B1 cho dữ liệu hợp lệ và null.
 *
 * Q1 [TỰ TRẢ LỜI] Hai object `equals()` nhau phải thỏa điều kiện gì về `hashCode()`?
 *   Bắt đầu: viết chiều bắt buộc của hợp đồng equals/hashCode.
 *   Tra cứu: Java 21 Object.hashCode contract và mục 3 tài liệu nguồn.
 *   Hoàn thành khi: nêu được equals true bắt buộc hashCode bằng nhau, chiều ngược lại không bắt buộc.
 * Q2 [TỰ TRẢ LỜI] Điều gì xảy ra nếu key thay đổi sau khi đưa vào `HashMap`?
 *   Bắt đầu: theo dõi bucket trước/sau khi đổi field tham gia equals/hashCode.
 *   Tra cứu: Java 21 Map/HashMap API và mục 3 tài liệu nguồn.
 *   Hoàn thành khi: giải thích được lookup có thể không tìm thấy entry do bucket mới khác bucket lưu.
 * Q3 [TỰ TRẢ LỜI] Collision có làm sai kết quả hay chủ yếu ảnh hưởng hiệu năng?
 *   Bắt đầu: phân biệt hash dùng tìm bucket với equals dùng xác nhận key.
 *   Tra cứu: Java 21 HashMap API và mục 3 tài liệu nguồn.
 *   Hoàn thành khi: giải thích collision hợp lệ không đổi tính đúng nếu equals/hashCode đúng, nhưng có thể ảnh hưởng hiệu năng.
 * Q4 [TỰ TRẢ LỜI] Khi nào chọn `TreeMap` thay vì `HashMap`?
 *   Bắt đầu: liệt kê yêu cầu thứ tự, range query và chi phí lookup.
 *   Tra cứu: Java 21 TreeMap và HashMap API.
 *   Hoàn thành khi: chọn TreeMap khi cần thứ tự/sorted navigation; chọn HashMap khi không cần thứ tự.
 * Q5 [TỰ TRẢ LỜI] Vì sao hash không chứng minh được hai input giống nhau?
 *   Bắt đầu: xét số lượng input có thể lớn hơn miền hash.
 *   Tra cứu: mục 3 tài liệu nguồn và nguyên lý pigeonhole.
 *   Hoàn thành khi: nêu được collision khiến hai input khác nhau có thể cùng hash.
 * B1 [CODE]: cài frequencies và Key; chạy b01CountsDuplicatesAndReturnsImmutableMap, b01RejectsNullBoundaries.
 */

/* ANSWER Q1:
 *
 */
/* ANSWER Q2:
 *
 */
/* ANSWER Q3:
 *
 */
/* ANSWER Q4:
 *
 */
/* ANSWER Q5:
 *
 */
class Ex01_HashTable {
    record Key(String value) {
        Key {
            Objects.requireNonNull(value, "value");
        }
    }

    static Map<String, Integer> frequencies(List<String> values) {
        throw new UnsupportedOperationException("TODO B1");
    }
}
