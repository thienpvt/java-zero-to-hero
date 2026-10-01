package phase03.d18_singleton;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton — Bài 1: enum singleton và trạng thái
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 18 (Singleton), câu 1, 2, 3, 4, 7.
 * Cần làm trước: d17_proxy.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_EnumSingletonAndStateTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [DỰ ĐOÁN] Singleton giải quyết vấn đề gì?
 *   Bắt đầu   : điền Q1_SINGLETON_PROBLEM bằng một giá trị của {@code Problem}.
 *   Kiểm chứng: chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh và ANSWER Q1 nêu một instance dùng chung.
 * <p>
 * Q2 [DỰ ĐOÁN] Vì sao Singleton thường bị xem là global state?
 *   Bắt đầu   : điền Q2_SINGLETON_GLOBAL_STATE.
 *   Kiểm chứng: chạy q02_prediction và q02_stateLeaksBetweenCalls.
 *   Hoàn thành khi: hai test xanh và ANSWER Q2 nêu mọi chỗ đọc cùng một biến ẩn.
 * <p>
 * Q3 [DỰ ĐOÁN] Singleton gây khó testing như thế nào?
 *   Bắt đầu   : điền Q3_SINGLETON_TESTING_PAIN.
 *   Kiểm chứng: chạy q03_prediction.
 *   Hoàn thành khi: q03_prediction xanh và ANSWER Q3 nêu không thay được bằng fake và test phụ thuộc thứ tự.
 * <p>
 * Q4 [DỰ ĐOÁN] Singleton có mặc định thread-safe không?
 *   Bắt đầu   : điền Q4_SINGLETON_THREAD_SAFE.
 *   Kiểm chứng: chạy q04_prediction.
 *   Hoàn thành khi: q04_prediction xanh và ANSWER Q4 nêu lazy init không đồng bộ có thể tạo hai instance.
 * <p>
 * Q7 [CODE] Enum singleton có ưu điểm gì trong Java?
 *   Bắt đầu   : cài {@code ConfigHolder.put}, {@code get} và {@code clear}.
 *   Kiểm chứng: chạy q07_enumSingletonSingleInstance và q07_sharedStateVisible.
 *   Hoàn thành khi: hai test xanh và ANSWER Q7 nêu serialization và reflection không tạo thêm instance.
 */
public class Ex01_EnumSingletonAndState {

    /** Vấn đề Singleton giải quyết. */
    public enum Problem {
        FASTER_ALLOCATION,
        SINGLE_SHARED_INSTANCE,
        FEWER_CLASSES
    }

    // Q1 — Singleton giải quyết vấn đề gì.
    static final Problem Q1_SINGLETON_PROBLEM = Problem.SINGLE_SHARED_INSTANCE; // SOLUTION-VALUE

    // Q2 — vì sao Singleton là global state.
    static final Boolean Q2_SINGLETON_GLOBAL_STATE = true; // SOLUTION-VALUE

    // Q3 — Singleton gây khó testing.
    static final Boolean Q3_SINGLETON_TESTING_PAIN = true; // SOLUTION-VALUE

    // Q4 — Singleton có mặc định thread-safe.
    static final Boolean Q4_SINGLETON_THREAD_SAFE = false; // SOLUTION-VALUE

    /** Enum singleton: JVM bảo đảm chỉ một instance, kể cả khi bị serialize hay reflect. */
    public enum ConfigHolder {

        INSTANCE;

        private final Map<String, String> overrides = new HashMap<>();

        public void put(String key, String value) {
            // SOLUTION-BEGIN throw Q7
            overrides.put(key, value);
            // SOLUTION-END
        }

        public String get(String key) {
            // SOLUTION-BEGIN throw Q7
            return overrides.get(key);
            // SOLUTION-END
        }

        public void clear() {
            overrides.clear();
        }
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Singleton bảo đảm chỉ một instance cho cả chương trình.
 * Nó dùng khi tài nguyên thật sự chỉ có một, ví dụ một cấu hình đọc một lần.
 * Nếu chỉ cần chia sẻ object, truyền nó qua constructor rõ ràng hơn.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Vì instance đó là biến ẩn mà mọi chỗ đều đọc và ghi được.
 * Không thấy nó trong signature nên không biết method nào phụ thuộc vào nó.
 * Trạng thái cũ từ lời gọi trước còn nguyên ở lời gọi sau.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Test không thay được Singleton bằng fake vì không có điểm chèn.
 * Mọi test dùng chung một instance nên trạng thái rò từ test này sang test khác.
 * Thứ tự chạy test trở thành điều kiện đúng, và test hay đỏ một cách khó hiểu.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Không. Bản lazy-init không đồng bộ có thể để hai thread cùng tạo instance.
 * Muốn đúng phải thêm synchronized hoặc dùng holder class, và cả hai tốn thêm một chút.
 * Enum singleton tránh vấn đề này vì việc khởi tạo do JVM thực hiện an toàn.
 * SOLUTION-END
 */

/* ANSWER Q7:
 * SOLUTION-BEGIN
 * Enum singleton chỉ có một hằng, nên JVM bảo đảm đúng một instance.
 * Serialization trả về cùng hằng thay vì tạo object mới.
 * Reflection cũng không gọi được constructor của enum, nên không phá được tính duy nhất.
 * SOLUTION-END
 */
