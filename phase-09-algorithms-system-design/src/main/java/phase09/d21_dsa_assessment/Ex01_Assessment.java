package phase09.d21_dsa_assessment;

import java.util.ArrayDeque;

/**
 * DSA assessment — bài thực hành tổng hợp, không phải curriculum topic.
 * <p>
 * Nguồn prompt: 09-algorithms-system-design.md, mục “DSA — Bài kiểm tra tổng hợp”.
 * Chọn arrays/strings, graph hoặc DP; trình bày input/output, biên, invariant, complexity
 * và brute-force oracle trước khi code. Test malformed/empty/duplicate/out-of-range theo contract.
 * <p>
 * Chọn bài toán chưa luyện thuộc khu vực arrays/strings, graph hoặc DP. Trình bày trước khi code:
 * <ul>
 *   <li>Input constraints, output contract và trường hợp biên.</li>
 *   <li>Cấu trúc dữ liệu/thuật toán, invariant và lý do chọn.</li>
 *   <li>Complexity worst-case, amortized hoặc expected phù hợp.</li>
 *   <li>Một cách brute force cho input nhỏ làm oracle đối chiếu.</li>
 * </ul>
 * Sau khi code, test empty, duplicate, malformed hoặc out-of-range theo contract, cycle/deep recursion
 * nếu liên quan. Đánh giá theo tính đúng, lập luận và test chất lượng; không lấy số lượng bài trên
 * nền tảng làm tiêu chí hoàn thành.
 * <p>
 * Source prompt verbatim (09-algorithms-system-design.md):
 * <pre>
 * Chọn bài toán chưa luyện thuộc khu vực arrays/strings, graph hoặc DP. Trình bày trước khi code:
 *
 * - Input constraints, output contract và trường hợp biên.
 * - Cấu trúc dữ liệu/thuật toán, invariant và lý do chọn.
 * - Complexity worst-case, amortized hoặc expected phù hợp.
 * - Một cách brute force cho input nhỏ làm oracle đối chiếu.
 *
 * Sau khi code, test empty, duplicate, malformed hoặc out-of-range theo contract, cycle/deep recursion nếu liên quan. Đánh giá theo tính đúng, lập luận và test chất lượng; không lấy số lượng bài trên nền tảng làm tiêu chí hoàn thành.
 * </pre>
 * <p>
 * B1 [CODE]: tìm đường đi ngắn nhất trên grid nhị phân; 1 đi được, 0 bị chặn, trả số bước
 * giữa hai tọa độ hoặc -1 nếu không tới được. Null/ragged/empty grid, tọa độ ngoài biên,
 * cell khác 0/1 bị từ chối.
 * <p>
 * Complexity: BFS O(rows × columns) time and space; each cell enters queue at most once.
 */
class Ex01_Assessment {
    static int shortestPath(int[][] grid, int startRow, int startColumn, int goalRow, int goalColumn) {
        throw new UnsupportedOperationException("TODO B1");
    }
}
