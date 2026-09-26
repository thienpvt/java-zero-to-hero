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
 * Q10 [DỰ ĐOÁN + CODE] Điều gì xảy ra nếu field tham gia `hashCode()` bị thay đổi
 *     sau khi object đã được put?
 *   Bắt đầu   : đọc class MutableKey bên dưới, điền 4 hằng số Q10_* (thay null).
 *   Kiểm chứng: chạy q10_prediction. Nếu sai, đặt breakpoint trong HashMap.getNode
 *               (Ctrl+N → HashMap → Ctrl+F12 → getNode), Debug test, dùng F7, sau khi đã
 *               điền dự đoán, tự xem get theo từng key tìm thấy value hay không và size()
 *               còn bao nhiêu.
 *   Code      : cài đặt changeIdSafely() để đổi id mà map vẫn tìm được value.
 *   Hoàn thành khi: các test q10_* xanh; giải thích được size() và get() sau khi đổi field
 *               tham gia hashCode() của key đang nằm trong map.
 *
 * Q9 [TỰ TRẢ LỜI] Mutable object có nên được dùng làm HashMap key không?
 *   Bắt đầu   : làm Q10 trước, dùng chính kết quả quan sát được để trả lời.
 *   Tra cứu   : Javadoc java.util.Map, đoạn "Great care must be exercised if mutable
 *               objects are used as map keys" (đặt con trỏ lên chữ Map, Ctrl+Q).
 *   Hoàn thành khi: viết xong khối ANSWER Q9, có nêu ít nhất một cách phòng tránh.
 */
public class Ex04_MutableKey {

    static final class MutableKey {
        int id;

        MutableKey(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof MutableKey k && k.id == id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "MutableKey[id=" + id + "]";
        }
    }

    // Q10 — kịch bản: map.put(new MutableKey(1), "A"); rồi key.id = 2
    static final Boolean Q10_GET_BY_SAME_REFERENCE_FINDS_VALUE = null;
    static final Boolean Q10_GET_BY_NEW_KEY_1_FINDS_VALUE = null;
    static final Boolean Q10_GET_BY_NEW_KEY_2_FINDS_VALUE = null;
    static final Integer Q10_SIZE_AFTER_MUTATION = null;

    /**
     * Đổi id của {@code key} sao cho sau đó {@code map.get(key)} vẫn trả về value cũ.
     *
     * @throws IllegalArgumentException nếu {@code key} hiện không tìm thấy trong map (map giữ nguyên)
     */
    static <V> void changeIdSafely(Map<MutableKey, V> map, MutableKey key, int newId) {
        throw new UnsupportedOperationException("TODO Q10");
    }
}

/* ANSWER Q9:
 *
 */
