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
            // SOLUTION-BEGIN throw B1
            if (vertices == null || edges == null) throw new NullPointerException();
            vertices = Set.copyOf(vertices);
            Map<Integer, List<Integer>> copied = new HashMap<>();
            for (var entry : edges.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    throw new IllegalArgumentException("edge source and adjacency list must be non-null");
                }
                copied.put(entry.getKey(), List.copyOf(entry.getValue()));
            }
            edges = Map.copyOf(copied);
            for (var entry : edges.entrySet()) {
                if (!vertices.contains(entry.getKey()) || !vertices.containsAll(entry.getValue())) {
                    throw new IllegalArgumentException("edge endpoints must be declared vertices");
                }
            }
            // SOLUTION-END
        }
    }

    static int componentCount(Graph graph) {
        // SOLUTION-BEGIN throw B1
        if (graph == null) throw new NullPointerException("graph");
        Map<Integer, List<Integer>> undirected = new HashMap<>();
        graph.vertices().forEach(vertex -> undirected.put(vertex, new ArrayList<>()));
        graph.edges().forEach((source, neighbors) -> neighbors.forEach(destination -> {
            undirected.get(source).add(destination);
            undirected.get(destination).add(source);
        }));
        Set<Integer> visited = new HashSet<>();
        int count = 0;
        for (int start : graph.vertices()) {
            if (!visited.add(start)) continue;
            count++;
            ArrayDeque<Integer> queue = new ArrayDeque<>();
            queue.add(start);
            while (!queue.isEmpty()) {
                for (int neighbor : undirected.get(queue.remove())) {
                    if (visited.add(neighbor)) queue.add(neighbor);
                }
            }
        }
        return count;
        // SOLUTION-END
    }

    static int shortestPath(Graph graph, int start, int end) {
        // SOLUTION-BEGIN throw B1
        if (graph == null) throw new NullPointerException("graph");
        if (graph.directed()) throw new IllegalArgumentException("shortestPath requires an undirected graph");
        if (!graph.vertices().contains(start) || !graph.vertices().contains(end)) {
            throw new IllegalArgumentException("start and end must be declared vertices");
        }
        Map<Integer, List<Integer>> undirected = new HashMap<>();
        graph.vertices().forEach(vertex -> undirected.put(vertex, new ArrayList<>()));
        graph.edges().forEach((source, neighbors) -> neighbors.forEach(destination -> {
            undirected.get(source).add(destination);
            undirected.get(destination).add(source);
        }));
        Map<Integer, Integer> distances = new HashMap<>();
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        distances.put(start, 0);
        queue.add(start);
        while (!queue.isEmpty()) {
            int current = queue.remove();
            if (current == end) return distances.get(current);
            for (int neighbor : undirected.get(current)) {
                if (distances.putIfAbsent(neighbor, distances.get(current) + 1) == null) queue.add(neighbor);
            }
        }
        return -1;
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * BFS duyệt các node theo lớp khoảng cách tính bằng số cạnh từ nguồn.
 * Vì mọi cạnh có cùng trọng số đơn vị, lần đầu thăm node đã cho đường đi ít cạnh nhất.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Không. BFS tối thiểu số cạnh, nhưng đường ít cạnh nhất có thể có tổng trọng số lớn hơn.
 * Với trọng số không âm khác nhau, dùng Dijkstra thay vì BFS.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Directed DFS phát hiện cycle khi gặp node đang ở trạng thái active trên recursion stack.
 * Undirected DFS bỏ qua cạnh về parent; một cạnh tới node đã thăm khác parent chỉ ra cycle.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Một traversal chỉ tới được các node trong component của node bắt đầu.
 * Lặp qua mọi node chưa thăm bảo đảm xử lý cả component disconnected và đếm đủ chúng.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Dijkstra cần trọng số cạnh không âm để node có khoảng cách nhỏ nhất được chốt an toàn.
 * Cạnh âm có thể làm giảm đường đi sau khi node đã bị chốt nên vi phạm greedy invariant.
 * SOLUTION-END
 */
