package phase09.d03_hash_table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Hashing và Hash Table.
 * Q1: Hai object `equals()` nhau phải thỏa điều kiện gì về `hashCode()`? [TỰ TRẢ LỜI]
 * Q2: Điều gì xảy ra nếu key thay đổi sau khi đưa vào `HashMap`? [TỰ TRẢ LỜI]
 * Q3: Collision có làm sai kết quả hay chủ yếu ảnh hưởng hiệu năng? [TỰ TRẢ LỜI]
 * Q4: Khi nào chọn `TreeMap` thay vì `HashMap`? [TỰ TRẢ LỜI]
 * Q5: Vì sao hash không chứng minh được hai input giống nhau? [TỰ TRẢ LỜI]
 * B1: Đếm tần suất phần tử bằng collection chuẩn. Tạo key bất biến có equality đúng;
 * kiểm tra phần tử trùng, null theo lựa chọn collection và dữ liệu adversarial phù hợp.
 */
class Ex01_HashTable {
    record Key(String value) {
        Key {
            Objects.requireNonNull(value, "value");
        }
    }

    static Map<String, Integer> frequencies(List<String> values) {
        // SOLUTION-BEGIN throw B1
        Objects.requireNonNull(values, "values");
        Map<String, Integer> counts = new HashMap<>();
        for (String value : values) counts.merge(Objects.requireNonNull(value, "element"), 1, Integer::sum);
        return Map.copyOf(counts);
        // SOLUTION-END
    }
}
