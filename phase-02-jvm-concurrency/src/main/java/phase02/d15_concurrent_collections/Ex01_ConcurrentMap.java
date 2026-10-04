package phase02.d15_concurrent_collections;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Concurrent collections — Bài 1: ConcurrentHashMap
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục 15 (Concurrent Collections), câu 1, 2, 3.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test trong Ex01_ConcurrentMapTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] Tại sao không đơn giản synchronize HashMap trong mọi trường hợp?
 *   Bắt đầu   : viết khối ANSWER Q1. Ctrl+N mở ConcurrentHashMap, Ctrl+Q đọc đoạn đầu, rồi Ctrl+N
 *               mở Collections và Ctrl+F12 tìm synchronizedMap để đối chiếu một khóa cho cả map.
 *   Hoàn thành khi: viết xong khối ANSWER Q1.
 * <p>
 * Q2 [CODE] ConcurrentHashMap hỗ trợ concurrency như thế nào ở mức khái niệm?
 *   Bắt đầu   : cài đặt {@code counts()} để trả một {@code Map<String, Integer>} mới, kiểu
 *               ConcurrentHashMap. Viết khối ANSWER Q2 về mức khái niệm, không chỉ tên lớp.
 *   Kiểm chứng: chạy q02_fourThreadsMergeSum40000. Bốn thread, mỗi thread {@code merge} 10000 lần
 *               trên 100 key, cổng {@code CountDownLatch}, {@code join} tối đa 10 giây.
 *               Không dùng {@code Thread.sleep}. Ctrl+B vào {@code ConcurrentHashMap.merge} nếu muốn đọc chữ ký.
 *   Hoàn thành khi: q02 xanh, tổng value bằng 40000, và ANSWER Q2 nói các key khác nhau đi song song thế nào.
 * <p>
 * Q3 [DỰ ĐOÁN] ConcurrentHashMap có cho null key/value không?
 *   Bắt đầu   : điền Q3_ALLOWS_NULL_KEY (thay null). Test tự gọi {@code put} với key null rồi ghi nhận
 *               key null có được nhận hay không. Viết khối ANSWER Q3, nói cả key và value.
 *   Kiểm chứng: chạy q03_nullKeyFromPut. Ctrl+N mở ConcurrentHashMap, Ctrl+F12 tìm put, Ctrl+Q.
 *   Hoàn thành khi: q03 xanh và ANSWER Q3 nói rõ null key cùng null value.
 */
public class Ex01_ConcurrentMap {

    // Q3 — ConcurrentHashMap có nhận null key hay không.
    static final Boolean Q3_ALLOWS_NULL_KEY = null;

    /**
     * Trả một {@code Map<String, Integer>} mới để nhiều thread cùng {@code merge}.
     */
    static Map<String, Integer> counts() {
        throw new UnsupportedOperationException("TODO Q2");
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
