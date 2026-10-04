package phase09.d10_graph_bfs_dfs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Graph: BFS, DFS và đường đi.
 * <p>
 * Nguồn: 09-algorithms-system-design.md, mục 10 (Graph: BFS, DFS và đường đi), câu 1–5.
 * Cần làm trước: mục 6 (Stack, Queue và Deque), mục 9 (Heap và Top-K).
 * Cách làm: trả lời từng câu, chạy test trong Ex01_GraphBfsDfsTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] Vì sao BFS cho shortest path trên graph không trọng số?
 *   Bắt đầu   : viết ANSWER Q1; xét thứ tự duyệt theo từng lớp.
 *   Tra cứu   : BFS và khoảng cách theo số cạnh.
 *   Hoàn thành khi: ANSWER Q1 giải thích lần đầu gặp node là khoảng cách ngắn nhất.
 * <p>
 * Q2 [TỰ TRẢ LỜI] BFS có giải đúng shortest path với trọng số khác nhau không?
 *   Bắt đầu   : viết ANSWER Q2; phân biệt số cạnh với tổng trọng số.
 *   Tra cứu   : Dijkstra và graph có trọng số.
 *   Hoàn thành khi: ANSWER Q2 nêu giới hạn của BFS.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Làm sao phát hiện cycle trong directed graph khác với undirected graph?
 *   Bắt đầu   : viết ANSWER Q3; xét node đang ở stack đệ quy.
 *   Tra cứu   : trạng thái visited và parent.
 *   Hoàn thành khi: ANSWER Q3 phân biệt cạnh ngược với cạnh tới parent.
 * <p>
 * Q4 [TỰ TRẢ LỜI] Vì sao cần khởi động traversal từ mọi node chưa thăm?
 *   Bắt đầu   : viết ANSWER Q4; xét graph disconnected.
 *   Tra cứu   : connected components.
 *   Hoàn thành khi: ANSWER Q4 giải thích node không thể tới từ component đã duyệt.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Dijkstra cần điều kiện gì về trọng số?
 *   Bắt đầu   : viết ANSWER Q5; kiểm tra trọng số âm.
 *   Tra cứu   : greedy relaxation.
 *   Hoàn thành khi: ANSWER Q5 nêu trọng số không âm.
 * <p>
 * B1: Đếm thành phần liên thông và tìm shortest path trong graph không trọng số.
 */
class Ex01_GraphBfsDfs {
    record Graph(Set<Integer> vertices, Map<Integer, List<Integer>> edges, boolean directed) {
        Graph {
            throw new UnsupportedOperationException("TODO B1");
        }
    }

    static int componentCount(Graph graph) {
        throw new UnsupportedOperationException("TODO B1");
    }

    static int shortestPath(Graph graph, int start, int end) {
        throw new UnsupportedOperationException("TODO B1");
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

/* ANSWER Q5:
 *
 */
