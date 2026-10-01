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
    static final Problem Q1_SINGLETON_PROBLEM = null;

    // Q2 — vì sao Singleton là global state.
    static final Boolean Q2_SINGLETON_GLOBAL_STATE = null;

    // Q3 — Singleton gây khó testing.
    static final Boolean Q3_SINGLETON_TESTING_PAIN = null;

    // Q4 — Singleton có mặc định thread-safe.
    static final Boolean Q4_SINGLETON_THREAD_SAFE = null;

    /** Enum singleton: JVM bảo đảm chỉ một instance, kể cả khi bị serialize hay reflect. */
    public enum ConfigHolder {

        INSTANCE;

        private final Map<String, String> overrides = new HashMap<>();

        public void put(String key, String value) {
            throw new UnsupportedOperationException("TODO Q7");
        }

        public String get(String key) {
            throw new UnsupportedOperationException("TODO Q7");
        }

        public void clear() {
            overrides.clear();
        }
    }
}

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

/* ANSWER Q7:
 *
 */
