package phase01.d04_hashmap;

import java.util.Objects;

/**
 * HashMap — Bài 1: Lookup và collision
 *
 * Nguồn: 01-java-core-advanced.md, mục 4 (HashMap), câu 1–5.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_LookupAndCollisionTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q1 [CODE + TỰ TRẢ LỜI] `HashMap.get(key)` hoạt động như thế nào?
 *   Bắt đầu   : đọc class Entry và mảng table (16 bucket cố định, không resize) bên dưới;
 *               cài đặt put(K, V) và get(Object) của MiniHashMap.
 *   Kiểm chứng: chạy các test q01_*; đặt breakpoint tại HashMap.getNode
 *               (Ctrl+N → HashMap → Ctrl+F12 → getNode), Debug một test bất kỳ, dùng F7
 *               (Step Into) để thấy thứ tự hash() → indexFor() → duyệt chuỗi → equals().
 *   Code      : put nối entry mới vào cuối chuỗi của bucket; nếu key đã có (so hash rồi
 *               Objects.equals) thì thay value và trả value cũ; hỗ trợ key null.
 *               get duyệt chuỗi bucket, đếm số entry đã duyệt vào lastProbeCount.
 *   Hoàn thành khi: mọi test q01_* xanh + viết xong khối ANSWER Q1 mô tả luồng
 *               key → hashCode() → hash → bucket → so hash → equals() → value.
 *
 * Q2 [DỰ ĐOÁN] Vì sao `hashCode()` thôi chưa đủ?
 *   Bắt đầu   : đọc class HashCodeOnlyKey bên dưới (có hashCode() nhưng KHÔNG override
 *               equals()); điền hằng số Q2_HASHCODE_ONLY_KEY_FOUND_BY_EQUAL_INSTANCE (thay null).
 *   Kiểm chứng: chạy q02_hashCodeOnlyKey_prediction; nếu sai, đặt con trỏ lên equals() của
 *               Object rồi Ctrl+Q để xem hợp đồng mặc định (so sánh theo địa chỉ tham chiếu).
 *   Hoàn thành khi: test q02_* xanh.
 *
 * Q3 [TỰ TRẢ LỜI] Collision là gì?
 *   Bắt đầu   : làm Q1 trước; chạy q01_get_hundredConstantHashKeys_singleBucketWithProbeCount,
 *               dùng Evaluate Expression (Alt+F8) xem giá trị lastProbeCount().
 *   Kiểm chứng: so sánh lastProbeCount() khi 100 key rơi vào một bucket (ConstantHashKey)
 *               với khi 100 key rải đều 16 bucket (GoodKey, xem bucketSizes()).
 *   Hoàn thành khi: viết xong khối ANSWER Q3 định nghĩa collision và nêu ảnh hưởng của nó
 *               lên lastProbeCount/hiệu năng lookup.
 *
 * Q4 [DỰ ĐOÁN] Hai object có cùng `hashCode()` nhưng `equals()` false thì chuyện gì xảy ra?
 *   Bắt đầu   : đọc class ConstantHashKey bên dưới (hashCode() luôn trả 42, equals() theo
 *               id); điền hằng số Q4_SIZE_WITH_TWO_COLLIDING_UNEQUAL_KEYS.
 *   Kiểm chứng: chạy q04_constantHashKey_twoUnequalKeys_prediction; đặt breakpoint tại
 *               HashMap.putVal, Step Into (F7) để thấy cả hai entry cùng nằm trong một
 *               bucket dạng chuỗi (linked) vì hash bằng nhau nhưng equals() false.
 *   Hoàn thành khi: test q04_* xanh.
 *
 * Q5 [DỰ ĐOÁN] Nếu `equals()` true nhưng hashCode khác nhau thì có vấn đề gì?
 *   Bắt đầu   : đọc class EqualsOnlyKey bên dưới (equals() theo id, hashCode() cộng thêm
 *               salt tăng dần mỗi lần tạo — mô phỏng tất định việc quên override hashCode);
 *               điền hằng số Q5_EQUALS_ONLY_KEY_FOUND_BY_EQUAL_INSTANCE.
 *   Kiểm chứng: chạy q05_equalsOnlyKey_prediction; dùng Evaluate Expression so sánh
 *               hashCode() của hai instance cùng id để thấy chúng khác nhau.
 *   Hoàn thành khi: test q05_* xanh.
 */
public class Ex01_LookupAndCollision {

    /** Bảng băm tối giản: 16 bucket cố định, không resize, dùng để quan sát lookup/collision. */
    static final class MiniHashMap<K, V> {

        static final class Entry<K, V> {
            final K key;
            final int hash;
            V value;
            Entry<K, V> next;

            Entry(K key, int hash, V value) {
                this.key = key;
                this.hash = hash;
                this.value = value;
            }
        }

        private static final int TABLE_SIZE = 16;

        @SuppressWarnings("unchecked")
        private final Entry<K, V>[] table = (Entry<K, V>[]) new Entry<?, ?>[TABLE_SIZE];
        private int size;
        private int lastProbeCount;

        /** Trộn hash gốc với phần cao 16 bit của nó, giống HashMap.hash(Object) của JDK. */
        static int hash(Object key) {
            int h;
            return key == null ? 0 : (h = key.hashCode()) ^ (h >>> 16);
        }

        /** Chọn bucket bằng phép AND với (length - 1), giống HashMap.indexFor cũ. */
        static int indexFor(int hash, int length) {
            return hash & (length - 1);
        }

        int size() {
            return size;
        }

        /** Số entry đã phải duyệt qua ở lần get() gần nhất. */
        int lastProbeCount() {
            return lastProbeCount;
        }

        /** Số entry trong từng bucket, theo đúng thứ tự chỉ số 0..15. */
        int[] bucketSizes() {
            int[] sizes = new int[table.length];
            for (int i = 0; i < table.length; i++) {
                int count = 0;
                for (Entry<K, V> e = table[i]; e != null; e = e.next) {
                    count++;
                }
                sizes[i] = count;
            }
            return sizes;
        }

        V put(K key, V value) {
            throw new UnsupportedOperationException("TODO Q1");
        }

        V get(Object key) {
            throw new UnsupportedOperationException("TODO Q1");
        }
    }

    /** Override hashCode() trả về id → phân bố bucket tất định, dùng để dựng dữ liệu mẫu. */
    record GoodKey(int id) {
        @Override
        public int hashCode() {
            return id;
        }
    }

    /** hashCode() luôn là 42 → mọi instance collide vào cùng một bucket dù equals() false. */
    static final class ConstantHashKey {
        final int id;

        ConstantHashKey(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ConstantHashKey k && k.id == id;
        }

        @Override
        public int hashCode() {
            return 42;
        }
    }

    /** Có hashCode() theo id nhưng KHÔNG override equals() — equals() rơi về Object (theo địa chỉ). */
    static final class HashCodeOnlyKey {
        final int id;

        HashCodeOnlyKey(int id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

    /**
     * equals() theo id nhưng hashCode() còn cộng thêm salt tăng dần mỗi lần tạo instance —
     * mô phỏng tất định việc "quên override hashCode()" (hashCode khác nhau giữa các instance
     * dù equals() coi là bằng nhau), không cần dựa vào Object.hashCode() không tất định.
     */
    static final class EqualsOnlyKey {
        private static int saltCounter;

        final int id;
        final int salt;

        EqualsOnlyKey(int id) {
            this.id = id;
            this.salt = saltCounter++;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof EqualsOnlyKey k && k.id == id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, salt);
        }
    }

    // Q2 — kịch bản: put(new HashCodeOnlyKey(1), ...); get(new HashCodeOnlyKey(1) khác instance)
    static final Boolean Q2_HASHCODE_ONLY_KEY_FOUND_BY_EQUAL_INSTANCE = null;

    // Q4 — kịch bản: put hai ConstantHashKey với id khác nhau (cùng hashCode 42) vào java.util.HashMap
    static final Integer Q4_SIZE_WITH_TWO_COLLIDING_UNEQUAL_KEYS = null;

    // Q5 — kịch bản: put(new EqualsOnlyKey(1), ...); get(new EqualsOnlyKey(1) khác instance)
    static final Boolean Q5_EQUALS_ONLY_KEY_FOUND_BY_EQUAL_INSTANCE = null;
}

/* ANSWER Q1:
 *
 */

/* ANSWER Q3:
 *
 */
